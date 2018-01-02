package com.itrax.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.itrax.R;
import com.itrax.aynctask.IAsyncCaller;
import com.itrax.aynctask.ServerJSONAsyncTask;
import com.itrax.aynctaskold.ServerIntractorAsync;
import com.itrax.db.CreateSalesDataSource;
import com.itrax.db.DatabaseHandler;
import com.itrax.models.CreateSalesModel;
import com.itrax.models.LoginModel;
import com.itrax.models.Model;
import com.itrax.models.PostNoteModel;
import com.itrax.models.SendOtpModel;
import com.itrax.parser.LoginParser;
import com.itrax.parser.PostNoteParser;
import com.itrax.parser.SendOtpParser;
import com.itrax.utils.APIConstants;
import com.itrax.utils.Constants;
import com.itrax.utils.FetchAddressIntentService;
import com.itrax.utils.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Shankar on 5/2/2017.
 */

public class DashBoardActivity extends BaseActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, IAsyncCaller {

    protected GoogleApiClient mGoogleApiClient;
    protected Location mCurrentLocation;
    protected String mLastUpdateTime;
    protected Boolean mRequestingLocationUpdates;
    protected LocationSettingsRequest mLocationSettingsRequest;
    protected LocationRequest mLocationRequest;
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;

    @BindView(R.id.edt_note)
    EditText edtNote;

    @BindView(R.id.tv_exit)
    TextView tv_exit;

    static EditText edt_delivery_date;

    @BindView(R.id.edt_check)
    TextView edt_check;

    @BindView(R.id.edt_customer_name)
    EditText edt_customer_name;

    @BindView(R.id.edt_mobile_number)
    EditText edt_mobile_number;

    @BindView(R.id.ll_dynamic_data)
    LinearLayout ll_dynamic_data;

    @BindView(R.id.tv_off_line_count)
    TextView tv_off_line_count;
    @BindView(R.id.rl_count)
    RelativeLayout rl_count;

    private AddressResultReceiver mResultReceiver;
    private String location;
    private boolean isVerified = false;
    private LoginModel mLoginModel;

    private ArrayList<EditText> views = new ArrayList<>();
    private CreateSalesDataSource createSalesDatasource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_new);
        ButterKnife.bind(this);
        DatabaseHandler.getInstance(this);
        createSalesDatasource = new CreateSalesDataSource(this);
        initUI();
        buildGoogleApiClient();
        createLocationRequest();
        buildLocationSettingsRequest();
        mRequestingLocationUpdates = false;
        if (!mRequestingLocationUpdates) {
            mRequestingLocationUpdates = true;
            startLocationUpdates();
        }
        mResultReceiver = new AddressResultReceiver(new Handler());
    }

    private void initUI() {
        edt_check.setTypeface(Utility.getMaterialIconsRegular(this));
        edt_delivery_date = (EditText) findViewById(R.id.edt_delivery_date);
        isVerified = false;

        setDynamicData();

        if (createSalesDatasource.getDataCount() > 0) {
            rl_count.setVisibility(View.VISIBLE);
            tv_off_line_count.setText("" + createSalesDatasource.getDataCount());
        } else
            rl_count.setVisibility(View.GONE);

    }

    /**
     * This method is used for sync
     */
    @OnClick(R.id.rl_count)
    void sync() {
        postLocationData();
    }

    /**
     * This method is used to set the dynamic data
     */
    private void setDynamicData() {
        ll_dynamic_data.removeAllViews();
        String json = Utility.getSharedPrefStringData(this, Constants.LOGIN_RESPONSE);
        if (!Utility.isValueNullOrEmpty(json)) {
            LoginParser loginParser = new LoginParser();
            mLoginModel = (LoginModel) loginParser.parse(json, this);
        }
        isVerified = !mLoginModel.isOTPRequired();

        if (mLoginModel != null && mLoginModel.getDynamicFieldsModels() != null
                && mLoginModel.getDynamicFieldsModels().size() > 0) {
            for (int i = 0; i < mLoginModel.getDynamicFieldsModels().size(); i++) {
                if (mLoginModel.getDynamicFieldsModels().get(i).getType().equalsIgnoreCase("Text")) {
                    LinearLayout linearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.edit_text_dynamic, null);
                    EditText editText = (EditText) linearLayout.findViewById(R.id.edt_id);
                    editText.setHint(mLoginModel.getDynamicFieldsModels().get(i).getLabel());
                    views.add(editText);
                    ll_dynamic_data.addView(linearLayout);
                } else if (mLoginModel.getDynamicFieldsModels().get(i).getType().equalsIgnoreCase("Date")) {
                    LinearLayout linearLayout2 = (LinearLayout) getLayoutInflater().inflate(R.layout.edit_text_date_dynamic, null);
                    final EditText editTextDate = (EditText) linearLayout2.findViewById(R.id.edt_date);
                    editTextDate.setHint(mLoginModel.getDynamicFieldsModels().get(i).getLabel());
                    editTextDate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            SelectDateFragment newFragment = new SelectDateFragment(editTextDate);
                            newFragment.show(getSupportFragmentManager(), "DatePicker");
                        }
                    });
                    views.add(editTextDate);
                    ll_dynamic_data.addView(linearLayout2);
                } else if (mLoginModel.getDynamicFieldsModels().get(i).getType().equalsIgnoreCase("Dropdown")) {
                    LinearLayout linearLayout3 = (LinearLayout) getLayoutInflater().inflate(R.layout.edit_text_spinner_dynamic, null);
                    final EditText editTextSpinner = (EditText) linearLayout3.findViewById(R.id.edt_spinner);
                    editTextSpinner.setHint(mLoginModel.getDynamicFieldsModels().get(i).getLabel());
                    final String mName = mLoginModel.getDynamicFieldsModels().get(i).getLabel();

                    String mListNames = mLoginModel.getDynamicFieldsModels().get(i).getList();
                    String[] array_list = mListNames.split(", ");
                    final List<String> stringList = new ArrayList<>();
                    for (int k = 0; k < array_list.length; k++) {
                        stringList.add(array_list[k]);
                    }
                    editTextSpinner.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Utility.showSpinnerDialog(DashBoardActivity.this, mName, stringList, editTextSpinner);
                        }
                    });
                    views.add(editTextSpinner);
                    ll_dynamic_data.addView(linearLayout3);
                }
            }
        }
    }

    /**
     * Receiver for data sent from FetchAddressIntentService.
     */
    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            // Display the address string or an error message sent from the intent service.
            location = resultData.getString(Constants.RESULT_DATA_KEY);
            Utility.showLog("Location Address", "location" + location);
        }
    }

    private void getCityAndCountryData() {
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, mResultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, mCurrentLocation);
        startService(intent);
    }

    /**
     * Submit click and api call
     */
    @OnClick(R.id.btn_send)
    void onBtnLoginClick() {
        if (isVerified) {
            if (isValidFields()) {
                if (Utility.isNetworkAvailable(DashBoardActivity.this)) {
                    saveInLocalDb();
                    postLocationData();
                } else {
                    saveInLocalDb();
                    edtNote.setText("");
                    showAlertForLocationOff();
                }
            }
        } else if (Utility.isValueNullOrEmpty(edt_mobile_number.getText().toString())) {
            if (isValidFields()) {
                if (Utility.isNetworkAvailable(DashBoardActivity.this)) {
                    saveInLocalDb();
                    postLocationData();
                } else {
                    saveInLocalDb();
                    edtNote.setText("");
                    showAlertForLocationOff();
                }
            }
        } else if (edt_mobile_number.getText().toString().length() == 10) {
            callSendOtp();
        } else {
            Utility.showOKOnlyDialog(DashBoardActivity.this, "Enter 10 digits mobile number",
                    Utility.getResourcesString(this, R.string.app_name));
        }

    }

    /**
     * This method is used to save data in the local db
     */
    private void saveInLocalDb() {
        CreateSalesModel createSalesModel = new CreateSalesModel();
        JSONArray jsonArray = new JSONArray();
        try {
            jsonArray.put(mCurrentLocation.getLongitude());
            jsonArray.put(mCurrentLocation.getLatitude());

            createSalesModel.setCoordinates(jsonArray.toString());
            createSalesModel.setArea(location);
            createSalesModel.setTime(Utility.getTime());
            createSalesModel.setNote(edtNote.getText().toString());
            createSalesModel.setCustomer_name(edt_customer_name.getText().toString());
            if (Utility.isValueNullOrEmpty(edt_mobile_number.getText().toString())) {
                createSalesModel.setCustomer_mobile("");
            } else {
                createSalesModel.setCustomer_mobile(edt_customer_name.getText().toString());
            }
            createSalesModel.setDue_date(edt_delivery_date.getText().toString());
            if (isVerified)
                createSalesModel.setIsotpverified("true");
            else
                createSalesModel.setIsotpverified("false");

            createSalesModel.setInitiatedDate(Utility.getDate());
            createSalesModel.setInitiatedTime(Utility.getTime());

            JSONObject jsonObject = new JSONObject();
            if (mLoginModel != null && mLoginModel.getDynamicFieldsModels() != null
                    && mLoginModel.getDynamicFieldsModels().size() > 0) {
                for (int i = 0; i < mLoginModel.getDynamicFieldsModels().size(); i++) {
                    jsonObject.put(mLoginModel.getDynamicFieldsModels().get(i).getLabel(), views.get(i).getText().toString());
                }
                createSalesModel.setAdditional_info(jsonObject.toString());
            } else {
                createSalesModel.setAdditional_info("");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        createSalesDatasource.insertData(createSalesModel);
    }

    private boolean isValidFields() {
        if (Utility.isValueNullOrEmpty(this.edt_customer_name.getText().toString())) {
            Utility.setSnackBar(this.edt_customer_name, "Please enter customer name");
            return false;
        } /*else if (!isVerified) {
            Utility.setSnackBar(this.edtNote, "Please verify your number");
            return false;
        }  else if (Utility.isValueNullOrEmpty(edt_delivery_date.getText().toString())) {
            Utility.setSnackBar(edt_delivery_date, "Please select measurement date");
            return false;
        } */ else if (Utility.isValueNullOrEmpty(this.edtNote.getText().toString())) {
            Utility.setSnackBar(this.edtNote, "Please enter note");
            return false;
        } else if (this.mCurrentLocation != null) {
            return true;
        } else {
            Utility.setSnackBar(this.edtNote, "Something problem with location getting. Try after some time");
            return false;
        }
    }

    private void postLocationData() {
        final ArrayList<CreateSalesModel> createSalesModels = createSalesDatasource.selectAll();
        JSONArray jsonSalesRecordsArray = new JSONArray();
        if (createSalesModels != null && createSalesModels.size() > 0) {
            for (int i = 0; i < createSalesModels.size(); i++) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("Coordinates", createSalesModels.get(i).getCoordinates());
                    jsonObject.put("Area", createSalesModels.get(i).getArea());
                    jsonObject.put("Country", "India");
                    jsonObject.put("Time", createSalesModels.get(i).getTime());
                    jsonObject.put("Note", createSalesModels.get(i).getNote());
                    jsonObject.put("CustomerName", createSalesModels.get(i).getCustomer_name());
                    jsonObject.put("CustomerMobile", createSalesModels.get(i).getCustomer_mobile());
                    jsonObject.put("DueDate", createSalesModels.get(i).getDue_date());
                    jsonObject.put("IsOtpVerified", createSalesModels.get(i).getIsotpverified());
                    jsonObject.put("Source", "android");
                    jsonObject.put("InitiatedDate", createSalesModels.get(i).getInitiatedDate());
                    jsonObject.put("InitiatedTime", createSalesModels.get(i).getInitiatedTime());
                    jsonObject.put("AdditionalInfo", createSalesModels.get(i).getAdditional_info());
                    jsonSalesRecordsArray.put(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }

        try {
            LinkedHashMap linkedHashMap = new LinkedHashMap();
            linkedHashMap.put("SalesRecords", jsonSalesRecordsArray);
            PostNoteParser mPostNoteParser = new PostNoteParser();
            ServerIntractorAsync serverJSONAsyncTask = new ServerIntractorAsync(
                    this, Utility.getResourcesString(this, R.string.please_wait), true,
                    APIConstants.CREATE_SALES_RECORD, linkedHashMap,
                    APIConstants.REQUEST_TYPE.POST, this, mPostNoteParser);
            Utility.execute(serverJSONAsyncTask);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
            startLocationUpdates();
        }
        updateLocationUI();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Stop location updates to save battery, but don't disconnect the GoogleApiClient object.
        if (mGoogleApiClient.isConnected()) {

        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (mCurrentLocation == null) {
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
            getCityAndCountryData();
        }
        /*if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }*/
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i("", "Connection suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i("", "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
    }

    /**
     * Requests location updates from the FusedLocationApi.
     */
    protected void startLocationUpdates() {
        LocationServices.SettingsApi.checkLocationSettings(
                mGoogleApiClient,
                mLocationSettingsRequest
        ).setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult locationSettingsResult) {
                final Status status = locationSettingsResult.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.i("startLocationUpdates", "All location settings are satisfied.");
                        LocationServices.FusedLocationApi.requestLocationUpdates(
                                mGoogleApiClient, mLocationRequest, DashBoardActivity.this);
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.i("startLocationUpdates", "Location settings are not satisfied. Attempting to upgrade " +
                                "location settings ");
                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the
                            // result in onActivityResult().
                            status.startResolutionForResult(DashBoardActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            Log.i("startLocationUpdates", "PendingIntent unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        String errorMessage = "Location settings are inadequate, and cannot be " +
                                "fixed here. Fix in Settings.";
                        Log.e("startLocationUpdates", errorMessage);
                        Toast.makeText(DashBoardActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                        mRequestingLocationUpdates = false;
                }
                updateLocationUI();
            }
        });
    }

    private void updateLocationUI() {
        if (mCurrentLocation != null) {
            Utility.showLog("lat", "mCurrentLocation" + mCurrentLocation.getLatitude());
            Utility.showLog("getLongitude", "mCurrentLocation" + mCurrentLocation.getLongitude());
            getCityAndCountryData();
        }
    }

    protected void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.i("", "User agreed to make required location settings changes.");
                        // Nothing to do. startLocationupdates() gets called in onResume again.
                        getCityAndCountryData();
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.i("", "User chose not to make required location settings changes.");
                        mRequestingLocationUpdates = false;
                        DashBoardActivity.this.finish();
                        System.exit(0);
                        break;
                }
                break;
        }
    }

    @Override
    public void onComplete(Model model) {
        if (model != null) {
            if (model instanceof PostNoteModel) {
                PostNoteModel mPostNoteModel = (PostNoteModel) model;
                edtNote.setText("");
                createSalesDatasource.deleteAll();
                showAlertForLocationOff();
            } else if (model instanceof SendOtpModel) {
                SendOtpModel mSendOtpModel = (SendOtpModel) model;
                if (mSendOtpModel != null && mSendOtpModel.isSuccess())
                    showOtpDialog(mSendOtpModel.getOtp());
                else
                    Utility.showOKOnlyDialog(DashBoardActivity.this, mSendOtpModel.getMessage(),
                            Utility.getResourcesString(DashBoardActivity.this, R.string.app_name));
            }
        }
    }

    private void showAlertForLocationOff() {
        /*new AlertDialog.Builder(DashBoardActivity.this)
                .setTitle(Utility.getResourcesString(DashBoardActivity.this, R.string.app_name))
                .setMessage("Data sent successfully,\n<b>Switch off your location to save battery</b>")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent intent = new Intent(
                                android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        DashBoardActivity.this.startActivity(intent);
                        DashBoardActivity.this.finish();
                        System.exit(0);
                    }
                })
                .show();*/

        final Dialog mDialog = new Dialog(this);
        mDialog.requestWindowFeature(1);
        mDialog.setContentView(R.layout.data_save_dialog);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setCancelable(true);
        ((TextView) mDialog.findViewById(R.id.tv_ok)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mDialog.dismiss();
                Intent intent = new Intent(
                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                DashBoardActivity.this.startActivity(intent);
                DashBoardActivity.this.finish();
                System.exit(0);
            }
        });
        mDialog.show();
    }

    @OnClick(R.id.tv_exit)
    void logout() {
        Utility.setSharedPrefStringData(DashBoardActivity.this, Constants.LOGIN_SESSION_ID, "");
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        DashBoardActivity.this.finish();
    }


    public static class SelectDateFragment extends android.support.v4.app.DialogFragment implements DatePickerDialog.OnDateSetListener {

        EditText editText;

        public SelectDateFragment() {
        }

        @SuppressLint("ValidFragment")
        public SelectDateFragment(EditText mEditText) {
            this.editText = mEditText;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar calendar = Calendar.getInstance();
            int yy = calendar.get(Calendar.YEAR);
            int mm = calendar.get(Calendar.MONTH);
            int dd = calendar.get(Calendar.DAY_OF_MONTH);
            return new DatePickerDialog(getActivity(), this, yy, mm, dd);
        }

        public void onDateSet(DatePicker view, int yy, int mm, int dd) {
            populateSetDate(yy, mm + 1, dd);
        }

        public void populateSetDate(int year, int month, int day) {
            editText.setText(month + "/" + day + "/" + year);
        }

    }

    private boolean isMobileNumberEntered() {
        boolean isValidated = false;
        if (Utility.isValueNullOrEmpty(edt_mobile_number.getText().toString().trim())) {
            Utility.setSnackBar(edt_mobile_number, "Please enter mobile number");
            edt_mobile_number.requestFocus();
        } else {
            isValidated = true;
        }
        return isValidated;
    }

    private boolean isOtp(EditText opt, String otpMessage) {
        boolean isValidated = false;
        if (Utility.isValueNullOrEmpty(opt.getText().toString().trim())) {
            Utility.setSnackBar(opt, "Please enter otp");
            opt.requestFocus();
        } else if (!otpMessage.equalsIgnoreCase(opt.getText().toString())) {
            Utility.setSnackBar(opt, "Please enter valid otp");
            opt.requestFocus();
        } else {
            isValidated = true;
        }
        return isValidated;
    }

    private void showOtpDialog(final String otpMessage) {
        final Dialog mDialog = new Dialog(this);
        mDialog.requestWindowFeature(1);
        mDialog.setContentView(R.layout.otp_dialog);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setCancelable(true);
        final EditText edt_otp = (EditText) mDialog.findViewById(R.id.edt_otp);
        Button btn_ok = (Button) mDialog.findViewById(R.id.btn_ok);
        ((Button) mDialog.findViewById(R.id.btn_cancel)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        btn_ok.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (isOtp(edt_otp, otpMessage)) {
                    mDialog.dismiss();
                    Utility.showToastMessage(DashBoardActivity.this, "Success, Mobile number verified");
                    DashBoardActivity.this.isVerified = true;
                }
            }
        });
        mDialog.show();
    }

    @OnClick({R.id.edt_check})
    void onEdtCheck() {
        if (isMobileNumberEntered()) {
            //showOtpDialog();
            callSendOtp();
        }
    }

    /*This call is used to send the OTP*/
    private void callSendOtp() {
        try {
            LinkedHashMap linkedHashMap = new LinkedHashMap();
            linkedHashMap.put("MobileNumber", edt_mobile_number.getText().toString());
            SendOtpParser mSendOtpParser = new SendOtpParser();
            ServerJSONAsyncTask serverJSONAsyncTask = new ServerJSONAsyncTask(
                    this, Utility.getResourcesString(this, R.string.please_wait), true,
                    APIConstants.GET_SALES_OTP, linkedHashMap,
                    APIConstants.REQUEST_TYPE.POST, this, mSendOtpParser);
            Utility.execute(serverJSONAsyncTask);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick({R.id.edt_delivery_date})
    void onDeliveryDate() {
        SelectDateFragment newFragment = new SelectDateFragment(edt_delivery_date);
        newFragment.show(getSupportFragmentManager(), "DatePicker");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            logout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}

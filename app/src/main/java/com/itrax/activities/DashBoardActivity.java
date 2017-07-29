package com.itrax.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
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
import com.itrax.models.Model;
import com.itrax.models.PostNoteModel;
import com.itrax.models.SendOtpModel;
import com.itrax.parser.PostNoteParser;
import com.itrax.parser.SendOtpParser;
import com.itrax.utils.APIConstants;
import com.itrax.utils.Constants;
import com.itrax.utils.FetchAddressIntentService;
import com.itrax.utils.Utility;

import org.json.JSONArray;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;

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

    private AddressResultReceiver mResultReceiver;
    private String location;
    private boolean isVerified = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_new);
        ButterKnife.bind(this);
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
                postLocationData();
            }
        } else if (Utility.isValueNullOrEmpty(edt_mobile_number.getText().toString())) {
            if (isValidFields()) {
                postLocationData();
            }
        } else if (edt_mobile_number.getText().toString().length() == 10) {
            callSendOtp();
        } else {
            Utility.showOKOnlyDialog(DashBoardActivity.this, "Enter 10 digits mobile number",
                    Utility.getResourcesString(this, R.string.app_name));
        }

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
        try {
            LinkedHashMap linkedHashMap = new LinkedHashMap();
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(mCurrentLocation.getLatitude());
            jsonArray.put(mCurrentLocation.getLongitude());
            linkedHashMap.put("Coordinates", jsonArray.toString());
            linkedHashMap.put("Area", location);
            linkedHashMap.put("Country", "India");
            linkedHashMap.put("Time", Utility.getTime());
            linkedHashMap.put("Note", edtNote.getText().toString());
            linkedHashMap.put("CustomerName", edt_customer_name.getText().toString());
            if (Utility.isValueNullOrEmpty(edt_mobile_number.getText().toString())) {
                linkedHashMap.put("CustomerMobile", "");
            } else {
                linkedHashMap.put("CustomerMobile", edt_mobile_number.getText().toString());
            }
            linkedHashMap.put("DueDate", edt_delivery_date.getText().toString());
            if (isVerified)
                linkedHashMap.put("IsOtpVerified", "true");
            else
                linkedHashMap.put("IsOtpVerified", "false");
            linkedHashMap.put("Source", "android");
            linkedHashMap.put("InitiatedDate", Utility.getDate());
            linkedHashMap.put("InitiatedTime", Utility.getTime());

            PostNoteParser mPostNoteParser = new PostNoteParser();
            ServerJSONAsyncTask serverJSONAsyncTask = new ServerJSONAsyncTask(
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
    }


    public static class SelectDateFragment extends android.support.v4.app.DialogFragment implements DatePickerDialog.OnDateSetListener {

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
            DashBoardActivity.edt_delivery_date.setText(month + "/" + day + "/" + year);
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
        new SelectDateFragment().show(getSupportFragmentManager(), "DatePicker");
    }

}

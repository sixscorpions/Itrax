package com.itrax.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
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
import com.itrax.adapters.SummeryListAdapter;
import com.itrax.aynctask.IAsyncCaller;
import com.itrax.aynctaskold.ServerIntractorAsync;
import com.itrax.db.CreateSalesDataSource;
import com.itrax.db.DatabaseHandler;
import com.itrax.fragments.HomeFragment;
import com.itrax.models.CreateSalesModel;
import com.itrax.models.LoginModel;
import com.itrax.models.Model;
import com.itrax.models.PostNoteModel;
import com.itrax.parser.LoginParser;
import com.itrax.parser.PostNoteParser;
import com.itrax.utils.APIConstants;
import com.itrax.utils.Constants;
import com.itrax.utils.FetchAddressIntentService;
import com.itrax.utils.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SummaryActivity extends BaseActivity implements GoogleApiClient.ConnectionCallbacks,
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


    private AddressResultReceiver mResultReceiver;
    private String location;
    private LoginModel mLoginModel;

    private CreateSalesDataSource createSalesDatasource;
    /**
     * AllEvents List set up
     */
    @BindView(R.id.listView)
    SwipeMenuListView listView;

    @BindView(R.id.btn_submit)
    Button btn_submit;

    @BindView(R.id.img_back)
    ImageView img_back;
    public static SummeryListAdapter summeryListAdapter;
    private int mDeletePosition = -1;
    private int mEditPosition = -1;

    public static ArrayList<Integer> count;
    public static ArrayList<String> stringList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);
        ButterKnife.bind(this);

        DatabaseHandler.getInstance(this);
        createSalesDatasource = new CreateSalesDataSource(this);
        intiUI();
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
                        SummaryActivity.this.finish();
                        System.exit(0);
                        break;
                }
                break;
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
                                mGoogleApiClient, mLocationRequest, SummaryActivity.this);
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.i("startLocationUpdates", "Location settings are not satisfied. Attempting to upgrade " +
                                "location settings ");
                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the
                            // result in onActivityResult().
                            status.startResolutionForResult(SummaryActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            Log.i("startLocationUpdates", "PendingIntent unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        String errorMessage = "Location settings are inadequate, and cannot be " +
                                "fixed here. Fix in Settings.";
                        Log.e("startLocationUpdates", errorMessage);
                        Toast.makeText(SummaryActivity.this, errorMessage, Toast.LENGTH_LONG).show();
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

    /**
     * This method is used to initiate
     */
    private void intiUI() {
        String json = Utility.getSharedPrefStringData(this, Constants.LOGIN_RESPONSE);
        if (!Utility.isValueNullOrEmpty(json)) {
            LoginParser loginParser = new LoginParser();
            mLoginModel = (LoginModel) loginParser.parse(json, this);
        }
        stringList = getIntent().getStringArrayListExtra(Constants.SELECTED_LIST);
        count = getIntent().getIntegerArrayListExtra(Constants.SELECTED_COUNT);

        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Utility.isNetworkAvailable(SummaryActivity.this)) {
                    saveInLocalDb();
                    postLocationData();
                } else {
                    saveInLocalDb();
                    showAlertForLocationOff();
                }
            }
        });

        setGridViewData();
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
                    SummaryActivity.this, Utility.getResourcesString(SummaryActivity.this, R.string.please_wait), true,
                    APIConstants.CREATE_SALES_RECORD, linkedHashMap,
                    APIConstants.REQUEST_TYPE.POST, this, mPostNoteParser);
            Utility.execute(serverJSONAsyncTask);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * This method is used to save data in the local db
     */
    private void saveInLocalDb() {
        CreateSalesModel createSalesModel = new CreateSalesModel();
        JSONArray jsonArray = new JSONArray();
        try {

            jsonArray.put(mCurrentLocation.getLatitude());
            jsonArray.put(mCurrentLocation.getLongitude());

            createSalesModel.setCoordinates(jsonArray.toString());
            createSalesModel.setArea(location);
            createSalesModel.setTime(Utility.getTime());
            createSalesModel.setNote(HomeFragment.edtNote.getText().toString());
            createSalesModel.setCustomer_name(HomeFragment.edt_doctor_name.getText().toString());
            if (Utility.isValueNullOrEmpty(HomeFragment.edt_mobile_number.getText().toString())) {
                createSalesModel.setCustomer_mobile("");
            } else {
                createSalesModel.setCustomer_mobile(HomeFragment.edt_mobile_number.getText().toString());
            }
            createSalesModel.setDue_date(HomeFragment.edt_delivery_date.getText().toString());

            createSalesModel.setInitiatedDate(Utility.getDate());
            createSalesModel.setInitiatedTime(Utility.getTime());

            JSONObject jsonObject = new JSONObject();
            if (mLoginModel != null && mLoginModel.getDynamicFieldsModels() != null
                    && mLoginModel.getDynamicFieldsModels().size() > 0) {
                for (int i = 0; i < mLoginModel.getDynamicFieldsModels().size(); i++) {
                    if (mLoginModel.getDynamicFieldsModels().get(i).getType().equalsIgnoreCase("DropdownPopup")) {
                        JSONArray jsonArray1 = new JSONArray();
                        for (int j = 0; j < stringList.size(); j++) {
                            JSONObject jsonObject1 = new JSONObject();
                            jsonObject1.put(stringList.get(j), "" + SummaryActivity.count.get(j));
                            jsonArray1.put(jsonObject1);
                        }
                        jsonObject.put(mLoginModel.getDynamicFieldsModels().get(i).getLabel(), jsonArray1.toString());
                    } else {
                        jsonObject.put(mLoginModel.getDynamicFieldsModels().get(i).getLabel(), HomeFragment.views.get(i).getText().toString());
                    }
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

    /*This method is used to set the lsit view data*/
    private void setGridViewData() {
        summeryListAdapter = new SummeryListAdapter(this, 200, stringList);
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {

                // create "open" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xEF, 0x53,
                        0x53)));
                // set item width
                deleteItem.setWidth(dp2px(70));
                // set item title
                deleteItem.setTitle("Delete");
                // set item title fontsize
                deleteItem.setTitleSize(15);
                deleteItem.setIcon(R.drawable.delete_icon);
                // set item title font color
                deleteItem.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(deleteItem);

                SwipeMenuItem openItem = new SwipeMenuItem(
                        getApplicationContext());
                openItem.setBackground(new ColorDrawable(Color.rgb(0xB2, 0xE7,
                        0xFA)));
                openItem.setWidth(dp2px(70));
                // set item title
                openItem.setTitle("Edit");
                // set item title fontsize
                openItem.setTitleSize(15);
                openItem.setIcon(R.drawable.edit_icon);
                // set item title font color
                openItem.setTitleColor(Utility.getColor(SummaryActivity.this, R.color.back_ground));
                // add to menu
                menu.addMenuItem(openItem);


            }
        };
        // set creator
        listView.setMenuCreator(creator);
        listView.setAdapter(summeryListAdapter);
        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        mDeletePosition = position;
                        stringList.remove(mDeletePosition);
                        setGridViewData();
                        break;
                    case 1:
                        mEditPosition = position;
                        Utility.showAskCountDialog(mEditPosition, SummaryActivity.this, stringList.get(mEditPosition), "Summary");
                        break;
                }
                return false;
            }
        });
    }


    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

    @Override
    public void onComplete(Model model) {
        if (model != null) {
            if (model instanceof PostNoteModel) {
                PostNoteModel mPostNoteModel = (PostNoteModel) model;
                if (mPostNoteModel.isStatus()) {
                    createSalesDatasource.deleteAll();
                    showAlertForLocationOff();
                } else {
                    Utility.showToastMessage(SummaryActivity.this, mPostNoteModel.getMessage());
                    showAlertForLocationOffCustom();
                }
            }
        }
    }

    private void showAlertForLocationOff() {
        final Dialog mDialog = new Dialog(this);
        mDialog.requestWindowFeature(1);
        mDialog.setContentView(R.layout.data_save_dialog);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setCancelable(true);

        TextView tv_heading = (TextView) mDialog.findViewById(R.id.tv_heading);
        if (Utility.isNetworkAvailable(SummaryActivity.this)) {
            tv_heading.setText(Utility.getResourcesString(SummaryActivity.this, R.string.data_sent_successfully));
        } else {
            tv_heading.setText(Utility.getResourcesString(SummaryActivity.this, R.string.data_stored_successfully));
        }

        ((TextView) mDialog.findViewById(R.id.tv_ok)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mDialog.dismiss();
                /*Intent intent = new Intent(
                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                SummaryActivity.this.startActivity(intent);
                SummaryActivity.this.finish();
                System.exit(0);*/
            }
        });
        mDialog.show();
    }

    private void showAlertForLocationOffCustom() {

        final Dialog mDialog = new Dialog(this);
        mDialog.requestWindowFeature(1);
        mDialog.setContentView(R.layout.data_save_dialog);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setCancelable(true);

        TextView tv_heading = (TextView) mDialog.findViewById(R.id.tv_heading);
        tv_heading.setText(Utility.getResourcesString(SummaryActivity.this, R.string.data_stored_successfully));


        ((TextView) mDialog.findViewById(R.id.tv_ok)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mDialog.dismiss();
                Intent intent = new Intent(
                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                SummaryActivity.this.startActivity(intent);
                SummaryActivity.this.finish();
                System.exit(0);
            }
        });
        mDialog.show();
    }
}

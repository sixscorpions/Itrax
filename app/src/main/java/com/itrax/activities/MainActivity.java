package com.itrax.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
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
import com.itrax.desinges.MaterialDialog;
import com.itrax.models.LoginModel;
import com.itrax.models.Model;
import com.itrax.models.PostNoteModel;
import com.itrax.parser.LoginParser;
import com.itrax.parser.PostNoteParser;
import com.itrax.permisions.Permissions;
import com.itrax.utils.APIConstants;
import com.itrax.utils.Constants;
import com.itrax.utils.FetchAddressIntentService;
import com.itrax.utils.Utility;

import org.json.JSONArray;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedHashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends BaseActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, IAsyncCaller {

    /**
     * Provides the entry point to Google Play services.
     */
    protected GoogleApiClient mGoogleApiClient;
    protected LocationRequest mLocationRequest;
    private AddressResultReceiver mResultReceiver;
    /**
     * Represents a geographical location.
     */
    protected Location mCurrentLocation;
    protected LocationSettingsRequest mLocationSettingsRequest;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    @BindView(R.id.edt_note)
    EditText edtNote;

    @BindView(R.id.tv_exit)
    TextView tv_exit;

    private String country;
    private String location;
    private boolean isLocationGot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mResultReceiver = new AddressResultReceiver(new Handler());
        buildGoogleApiClient();
        buildLocationSettingsRequest();
        if (Utility.isMarshmallowOS()) {
            getLocationPermission();
        }
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void buildLocationSettingsRequest() {
        if (Utility.isMarshmallowOS()) {
            PackageManager pm = this.getPackageManager();
            int hasWritePerm = pm.checkPermission(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    this.getPackageName());
            if (hasWritePerm == PackageManager.PERMISSION_GRANTED) {
                Utility.showLog("Setting Location update", "Setting Location update");
                createLocationRequest();
                LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
                builder.addLocationRequest(mLocationRequest);
                mLocationSettingsRequest = builder.build();
                startLocationUpdates();
            }
        }
    }

    private void getLocationPermission() {
        Permissions.getInstance().setActivity(this);
        CheckForPermissions(this,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.INTERNET);


    }

    private void CheckForPermissions(final Context mContext, final String... mPermissions) {
        // A request for two permissions
        Permissions.getInstance().requestPermissions(new Permissions.IOnPermissionResult() {
            @Override
            public void onPermissionResult(Permissions.ResultSet resultSet) {

                if (!resultSet.isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    final MaterialDialog denyDialog = new MaterialDialog(mContext, Permissions.TITLE,
                            Permissions.MESSAGE);
                    //Positive
                    denyDialog.setAcceptButton("RE-TRY");
                    denyDialog.setOnAcceptButtonClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            CheckForPermissions(mContext, mPermissions);
                        }
                    });
                    denyDialog.show();
                }
            }

            @Override
            public void onRationaleRequested(Permissions.IOnRationaleProvided callback, String... permissions) {
                Permissions.getInstance().showRationaleInDialog(Permissions.TITLE,
                        Permissions.MESSAGE, "RE-TRY", callback);
            }
        }, mPermissions);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case Constants.MY_PERMISSIONS_REQUEST_FINE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    createLocationRequest();
                    LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
                    builder.addLocationRequest(mLocationRequest);
                    mLocationSettingsRequest = builder.build();
                    startLocationUpdates();
                    getCityAndCountryData();
                } else {
                    MainActivity.this.finish();
                    System.exit(0);
                }
                return;
            }
        }
    }

    private void startLocationUpdates() {
        LocationServices.SettingsApi.checkLocationSettings(
                mGoogleApiClient,
                mLocationSettingsRequest
        ).setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult locationSettingsResult) {
                final Status status = locationSettingsResult.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.i("", "All location settings are satisfied.");
                        LocationServices.FusedLocationApi.requestLocationUpdates(
                                mGoogleApiClient, mLocationRequest, MainActivity.this);
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.i("", "Location settings are not satisfied. Attempting to upgrade " +
                                "location settings ");
                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the
                            // result in onActivityResult().
                            status.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            Log.i("", "PendingIntent unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        String errorMessage = "Location settings are inadequate, and cannot be " +
                                "fixed here. Fix in Settings.";
                        Log.e("", errorMessage);
                        Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     * Builds a GoogleApiClient. Uses the addApi() method to request the LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (mCurrentLocation == null) {
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            getCityAndCountryData();
        }
        if (mCurrentLocation != null) {
            Utility.showLog("Lat", "" + mCurrentLocation.getLatitude());
            Utility.showLog("Lng", "" + mCurrentLocation.getLongitude());
        } else {
            //Toast.makeText(this, R.string.no_location_detected, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
    }

    /**
     * Submit click and api call
     */
    @OnClick(R.id.btn_send)
    void onBtnLoginClick() {
        if (isValidFields()) {
            postLocationData();
        }
    }

    private void postLocationData() {
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        try {
            linkedHashMap.put("CreatedDate", getDate());
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(mCurrentLocation.getLatitude());
            jsonArray.put(mCurrentLocation.getLongitude());
            linkedHashMap.put("Coordinates", jsonArray.toString());
            linkedHashMap.put("Area", location);
            linkedHashMap.put("Country", "India");
            linkedHashMap.put("Time", getTime());
            linkedHashMap.put("Note", edtNote.getText().toString());

            PostNoteParser mPostNoteParser = new PostNoteParser();
            ServerJSONAsyncTask serverJSONAsyncTask = new ServerJSONAsyncTask(
                    this, Utility.getResourcesString(this, R.string.please_wait), true,
                    APIConstants.PLOTPROPOINT_URL, linkedHashMap,
                    APIConstants.REQUEST_TYPE.POST, this, mPostNoteParser);
            Utility.execute(serverJSONAsyncTask);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getTime() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("HH:mm");
        String formattedDate = df.format(c.getTime());
        return formattedDate;
    }

    private String getDate() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        String formattedDate = df.format(c.getTime());
        return formattedDate;
    }

    private boolean isValidFields() {
        boolean isValid = true;
        if (Utility.isValueNullOrEmpty(edtNote.getText().toString())) {
            Utility.setSnackBar(edtNote, "Please enter note");
            isValid = false;
        } else if (mCurrentLocation == null) {
            Utility.setSnackBar(edtNote, "Something problem with location getting. Try after some time");
            isValid = false;
        }
        return isValid;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        startLocationUpdates();
                        break;
                    case Activity.RESULT_CANCELED:
                        MainActivity.this.finish();
                        System.exit(0);
                        break;
                }
                break;
        }
    }

    private void getCityAndCountryData() {
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, mResultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, mCurrentLocation);
        startService(intent);
    }

    /**
     * Receiver for data sent from FetchAddressIntentService.
     */
    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        /**
         * Receives data sent from FetchAddressIntentService and updates the UI in MainActivity.
         */
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            // Display the address string or an error message sent from the intent service.
            location = resultData.getString(Constants.RESULT_DATA_KEY);
            Utility.showLog("Location Address", "location" + location);
        }
    }

    @Override
    public void onComplete(Model model) {
        if (model != null) {
            if (model instanceof PostNoteModel) {
                PostNoteModel mPostNoteModel = (PostNoteModel) model;
                edtNote.setText("");
                showAlertForLocationOff();
            }
        }
    }

    private void showAlertForLocationOff() {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle(Utility.getResourcesString(MainActivity.this, R.string.app_name))
                .setMessage("Data sent successfully,\nSwitch off your location to save battery")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent intent = new Intent(
                                android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        MainActivity.this.startActivity(intent);
                        MainActivity.this.finish();
                        System.exit(0);
                    }
                })
                .show();
    }

    @OnClick(R.id.tv_exit)
    void logout() {
        Utility.setSharedPrefStringData(MainActivity.this, Constants.LOGIN_SESSION_ID, "");
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

}

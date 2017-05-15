package com.itrax.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v7.app.AlertDialog;
import android.util.Log;
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
import com.itrax.parser.PostNoteParser;
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

    private AddressResultReceiver mResultReceiver;
    private String location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
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
        if (isValidFields()) {
            postLocationData();
        }
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

    private void postLocationData() {
        try {
            LinkedHashMap linkedHashMap = new LinkedHashMap();
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
            }
        }
    }

    private void showAlertForLocationOff() {
        new AlertDialog.Builder(DashBoardActivity.this)
                .setTitle(Utility.getResourcesString(DashBoardActivity.this, R.string.app_name))
                .setMessage("Data sent successfully,\nSwitch off your location to save battery")
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
                .show();
    }

    @OnClick(R.id.tv_exit)
    void logout() {
        Utility.setSharedPrefStringData(DashBoardActivity.this, Constants.LOGIN_SESSION_ID, "");
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}

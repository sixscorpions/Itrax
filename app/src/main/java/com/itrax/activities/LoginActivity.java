package com.itrax.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.itrax.R;
import com.itrax.aynctask.IAsyncCaller;
import com.itrax.aynctask.ServerJSONAsyncTask;
import com.itrax.desinges.MaterialDialog;
import com.itrax.models.LoginModel;
import com.itrax.models.Model;
import com.itrax.parser.LoginParser;
import com.itrax.permisions.Permissions;
import com.itrax.utils.APIConstants;
import com.itrax.utils.Constants;
import com.itrax.utils.Utility;

import java.util.LinkedHashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends BaseActivity implements IAsyncCaller {

    @BindView(R.id.edt_username)
    EditText edtUsername;
    @BindView(R.id.edt_password)
    EditText edtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_time);
        ButterKnife.bind(this);
        if (Utility.isMarshmallowOS()) {
            getLocationPermission();
        }

        edtUsername.setText("9652232101");
        edtPassword.setText("1234");
        /*edtUsername.setText("7799920200");
        edtPassword.setText("1234");*/
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

                } else {
                    LoginActivity.this.finish();
                    System.exit(0);
                }
                return;
            }
        }
    }

    /**
     * Submit click and api call
     */
    @OnClick(R.id.btn_login)
    void onBtnLoginClick() {
        if (isValidFields()) {
            postLoginData();
        }
    }

    private void postLoginData() {
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        try {
            linkedHashMap.put("username", edtUsername.getText().toString());
            linkedHashMap.put("password", edtPassword.getText().toString());
            LoginParser mLoginParser = new LoginParser();
            ServerJSONAsyncTask serverJSONAsyncTask = new ServerJSONAsyncTask(
                    this, Utility.getResourcesString(this, R.string.please_wait), true,
                    APIConstants.LOGIN_URL, linkedHashMap,
                    APIConstants.REQUEST_TYPE.POST, this, mLoginParser);
            Utility.execute(serverJSONAsyncTask);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isValidFields() {
        boolean isValid = true;
        if (Utility.isValueNullOrEmpty(edtUsername.getText().toString())) {
            Utility.setSnackBar(edtUsername, "Please enter username");
            isValid = false;
        } else if (Utility.isValueNullOrEmpty(edtPassword.getText().toString())) {
            Utility.setSnackBar(edtPassword, "Please enter password");
            isValid = false;
        }
        return isValid;
    }

    @Override
    public void onComplete(Model model) {

        if (model != null) {
            if (model instanceof LoginModel) {
                LoginModel loginModel = (LoginModel) model;
                Utility.setSharedPrefStringData(LoginActivity.this, Constants.LOGIN_ID, "" + loginModel.getId());

                Utility.setSharedPrefStringData(LoginActivity.this, Constants.LOGIN_SESSION_ID, "" + loginModel.getToken());
                Utility.setSharedPrefStringData(LoginActivity.this, Constants.TYPE_OF_BUSINESS, "" + loginModel.getTypeOfBusiness());

                Intent intent;
                if (!Utility.isValueNullOrEmpty(loginModel.getTypeOfBusiness())
                        && loginModel.getTypeOfBusiness().equalsIgnoreCase("pharma")) {
                    intent = new Intent(this, WorkBenchActivity.class);
                } else {
                    intent = new Intent(this, DashBoardActivity.class);
                }
                startActivity(intent);

                Utility.setSharedPrefStringData(LoginActivity.this, Constants.USER_NAME, "");
                Utility.setSharedPrefStringData(LoginActivity.this, Constants.CONTACT_NUMBER, edtUsername.getText().toString());

                finish();
            }
        }
    }
}

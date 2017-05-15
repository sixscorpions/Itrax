package com.itrax.activities;

import android.content.Intent;
import android.os.Bundle;

import com.itrax.utils.Constants;
import com.itrax.utils.Utility;

public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent;
        if (Utility.isValueNullOrEmpty(Utility.getSharedPrefStringData(SplashActivity.this, Constants.LOGIN_SESSION_ID))) {
            intent = new Intent(this, LoginActivity.class);
        } else {
            intent = new Intent(this, DashBoardActivity.class);
        }
        startActivity(intent);
        finish();
    }
}
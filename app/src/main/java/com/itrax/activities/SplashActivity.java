package com.itrax.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.itrax.R;
import com.itrax.utils.Constants;
import com.itrax.utils.Utility;

public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme_NoActionBar);
        setContentView(R.layout.activity_splash);
        Handler mSplashHandler = new Handler();
        Runnable action = new Runnable() {
            @Override
            public void run() {
                Intent intent;
                if (Utility.isValueNullOrEmpty(Utility.getSharedPrefStringData(SplashActivity.this, Constants.LOGIN_SESSION_ID))) {
                    intent = new Intent(SplashActivity.this, LoginActivity.class);
                } else {
                    if (!Utility.isValueNullOrEmpty(Utility.getSharedPrefStringData(SplashActivity.this, Constants.TYPE_OF_BUSINESS)) &&
                            Utility.getSharedPrefStringData(SplashActivity.this, Constants.TYPE_OF_BUSINESS).equalsIgnoreCase("pharma")) {
                        intent = new Intent(SplashActivity.this, WorkBenchActivity.class);
                    } else {
                        intent = new Intent(SplashActivity.this, DashBoardActivity.class);
                    }
                }
                startActivity(intent);
                finish();
            }
        };
        mSplashHandler.postDelayed(action, Constants.SPLASH_TIME_OUT);
    }
}
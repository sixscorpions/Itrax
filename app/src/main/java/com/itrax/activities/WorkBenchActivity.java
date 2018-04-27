package com.itrax.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.itrax.R;
import com.itrax.fragments.HomeFragment;
import com.itrax.utils.Constants;
import com.itrax.utils.Utility;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WorkBenchActivity extends BaseActivity {

    private DrawerLayout drawer_layout;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.menu_icon)
    ImageView menu_icon;

    @BindView(R.id.iv_home)
    ImageView iv_home;

    @BindView(R.id.tv_home)
    TextView tv_home;

    @BindView(R.id.iv_logout)
    ImageView iv_logout;

    @BindView(R.id.tv_logout)
    TextView tv_logout;


    @BindView(R.id.ll_home)
    LinearLayout ll_home;
    @BindView(R.id.ll_logout)
    LinearLayout ll_logout;

    @BindView(R.id.tv_username)
    TextView tv_username;

    @BindView(R.id.tv_phone)
    TextView tv_phone;

    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_bench);
        ButterKnife.bind(this);
        initUI();
    }

    private void initUI() {

        drawer_layout = (DrawerLayout) findViewById(R.id.drawer_layout);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        navigationView = (NavigationView) findViewById(R.id.nav_view);
        ll_home.performClick();

        /*USER NAME */
        tv_username.setTypeface(Utility.setRobotoRegular(this));
        String UserName = Utility.getSharedPrefStringData(this, Constants.USER_NAME);
        tv_username.setText("nForce");

         /*USER NAME */
        tv_phone.setTypeface(Utility.setRobotoRegular(this));
        String phoneNumber = Utility.getSharedPrefStringData(this, Constants.CONTACT_NUMBER);
        tv_phone.setText(phoneNumber);
    }


    @OnClick(R.id.menu_icon)
    public void toggelMenu() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START);
        } else {
            drawer_layout.openDrawer(GravityCompat.START);
        }
    }


    @OnClick(R.id.ll_home)
    public void navigatingHome() {
        drawer_layout.closeDrawer(GravityCompat.START);
        Bundle bundle = new Bundle();
        Utility.navigateDashBoardFragment(new HomeFragment(), HomeFragment.TAG, bundle, this);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @OnClick(R.id.ll_logout)
    public void logout() {
        Utility.setSharedPrefStringData(this, Constants.LOGIN_SESSION_ID, "");
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        this.finish();
    }
}

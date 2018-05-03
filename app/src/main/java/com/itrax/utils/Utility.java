package com.itrax.utils;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.itrax.R;
import com.itrax.activities.BaseActivity;
import com.itrax.activities.DashBoardActivity;
import com.itrax.activities.LoginActivity;
import com.itrax.activities.SummaryActivity;
import com.itrax.adapters.SpinnerDialogAdapter;
import com.itrax.adapters.SpinnerDialogAdapterForMedicines;
import com.itrax.adapters.SpinnerDialogWorkBenchAdapter;
import com.itrax.fragments.HomeFragment;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by shankar on 4/30/2017.
 */

public class Utility {

    public static final int NO_INTERNET_CONNECTION = 1;
    private static final int NO_GPS_ACCESS = 2;
    private static final int CONNECTION_TIMEOUT = 25000;

    /**
     * Check the value is null or empty
     *
     * @param value Value of that string
     * @return Boolean returns the value true or false
     */
    public static boolean isValueNullOrEmpty(String value) {
        boolean isValue = false;
        if (value == null || value.equals("") || value.equals("0.0")
                || value.equals("null") || value.trim().length() == 0) {
            isValue = true;
        }
        return isValue;
    }

    /**
     * TO CHECK IS IT BELOW MARSHMALLOW OR NOT
     */
    public static boolean isMarshmallowOS() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    public static boolean isNetworkAvailable(Context context) {
        try {
            ConnectivityManager connMgr = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                    .getState() == NetworkInfo.State.CONNECTED
                    || connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                    .getState() == NetworkInfo.State.CONNECTING) {
                return true;
            } else return connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
                    .getState() == NetworkInfo.State.CONNECTED
                    || connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
                    .getState() == NetworkInfo.State.CONNECTING;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void showLog(String logMsg, String logVal) {
        try {
            if (Constants.logMessageOnOrOff) {
                if (!isValueNullOrEmpty(logMsg) && !isValueNullOrEmpty(logVal)) {
                    Log.e(logMsg, logVal);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Shows toast message
     *
     * @param context Context of the class
     * @param message What message you have to show
     */
    public static void showToastMessage(Context context, String message) {
        try {
            if (!isValueNullOrEmpty(message) && context != null) {
                final Toast toast = Toast.makeText(
                        context.getApplicationContext(), message,
                        Toast.LENGTH_SHORT);
                toast.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static AlertDialog showSettingDialog(final Context context,
                                                String msg, String title, final int id) {
        return new AlertDialog.Builder(context)
                // .setMobile_icon_code(android.R.attr.alertDialogIcon)
                .setMessage(msg)
                .setTitle(title)
                .setPositiveButton(R.string.alert_dialog_ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int whichButton) {
                            }
                        })
                .setNegativeButton(R.string.alert_dialog_setting,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int whichButton) {
                                switch (id) {
                                    case Utility.NO_INTERNET_CONNECTION:
                                        context.startActivity(new Intent(
                                                android.provider.Settings.ACTION_SETTINGS));
                                        break;
                                    case Utility.NO_GPS_ACCESS:
                                        context.startActivity(new Intent(
                                                android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                                        break;
                                    default:
                                        break;
                                }
                            }
                        }).create();
    }

    public static void setSnackBar(final View mView, final String message) {
        final Snackbar snackbar = Snackbar
                .make(mView, message, Snackbar.LENGTH_SHORT)
                .setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                    }
                });
        snackbar.show();
    }

    /**
     * GET Resources String
     *
     * @param context Context of the class
     * @param id      Id of the resource
     * @return String
     */
    public static String getResourcesString(Context context, int id) {
        String value = null;
        if (context != null && id != -1) {
            value = context.getResources().getString(id);
        }
        return value;
    }

    /*
     *
	 * @Sparity
	 *
	 * These methods are to make async tasks concurrent, and run on parallel on
	 * android 3+
	 */

    public static <P, T extends AsyncTask<P, ?, ?>> void execute(T task) {
        execute(task, (P[]) null);
    }

    @SafeVarargs
    @SuppressLint("NewApi")
    public static <P, T extends AsyncTask<P, ?, ?>> void execute(T task,
                                                                 P... params) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
        } else {
            task.execute(params);
        }
    }

    public static void setSharedPrefStringData(Context context, String key, String value) {
        try {
            if (context != null) {
                SharedPreferences appInstallInfoSharedPref = context.getSharedPreferences(Constants.APP_PREF,
                        Context.MODE_PRIVATE);
                SharedPreferences.Editor appInstallInfoEditor = appInstallInfoSharedPref.edit();
                appInstallInfoEditor.putString(key, value);
                appInstallInfoEditor.apply();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * GET SHARED PREFERENCES STRING DATA
     */
    public static String getSharedPrefStringData(Context context, String key) {

        try {
            SharedPreferences userAcountPreference = context
                    .getSharedPreferences(Constants.APP_PREF,
                            Context.MODE_PRIVATE);
            return userAcountPreference.getString(key, "");
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return "";

    }

    public static Typeface getMaterialIconsRegular(Context context) {
        return Typeface.createFromAsset(context.getAssets(), "fonts/matireal_icons_regular.ttf");
    }

    public static String getTime() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("HH:mm");
        String formattedDate = df.format(c.getTime());
        return formattedDate;
    }

    public static String getDate() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        String formattedDate = df.format(c.getTime());
        return formattedDate;
    }

    public static void showSpinnerDialog(BaseActivity parent, String title,
                                         List<String> mList, final EditText et) {

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(parent);

        /*CUSTOM TITLE*/
        LayoutInflater inflater = (LayoutInflater) parent.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.layout_include_dialog_header, null);
        TextView tv_title = (TextView) view.findViewById(R.id.tv_alert_dialog_title);
        builderSingle.setCustomTitle(view);
        tv_title.setText(title);

        final SpinnerDialogAdapter adapter = new SpinnerDialogAdapter(parent,
                100, mList);
        builderSingle.setAdapter(adapter,
                new DialogInterface.OnClickListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String mData = adapter.getItem(which);
                        et.setText("" + mData);
                    }
                });
        builderSingle.show();
    }

    public static void showSpinnerDialogWorkBench(BaseActivity parent, String title,
                                         List<String> mList, final EditText et) {

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(parent);

        /*CUSTOM TITLE*/
        LayoutInflater inflater = (LayoutInflater) parent.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.layout_include_dialog_header, null);
        TextView tv_title = (TextView) view.findViewById(R.id.tv_alert_dialog_title);
        builderSingle.setCustomTitle(view);
        tv_title.setText(title);

        final SpinnerDialogWorkBenchAdapter adapter = new SpinnerDialogWorkBenchAdapter(parent,
                100, mList);
        builderSingle.setAdapter(adapter,
                new DialogInterface.OnClickListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String mData = adapter.getItem(which);
                        et.setText("" + mData);
                    }
                });
        builderSingle.show();
    }

    public static void showSpinnerDialogForMedicines(final BaseActivity parent, String title,
                                                     List<String> mList, final EditText et) {

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(parent);

        /*CUSTOM TITLE*/
        LayoutInflater inflater = (LayoutInflater) parent.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.layout_include_dialog_header, null);
        TextView tv_title = (TextView) view.findViewById(R.id.tv_alert_dialog_title);
        builderSingle.setCustomTitle(view);
        tv_title.setText(title);

        final SpinnerDialogAdapterForMedicines adapter = new SpinnerDialogAdapterForMedicines(parent,
                100, mList);
        builderSingle.setAdapter(adapter,
                new DialogInterface.OnClickListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String mData = adapter.getItem(which);
                        et.setText("" + mData);
                        //showAskCountDialog(which, parent);
                    }
                });
        builderSingle.show();
    }

    public static void showAskCountDialog(final int which, final BaseActivity parent, String mName, final String tag) {
        final Dialog dialog = new Dialog(parent);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.count_dialog);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        final TextView tv_medicine_name = (TextView) dialog.findViewById(R.id.tv_medicine_name);
        tv_medicine_name.setText("" + mName);

        final EditText edt_count = (EditText) dialog.findViewById(R.id.edt_count);
        final Button btn_send = (Button) dialog.findViewById(R.id.btn_send);
        final ImageView img_close = (ImageView) dialog.findViewById(R.id.img_close);
        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        InputMethodManager imm = (InputMethodManager) parent.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Utility.isValueNullOrEmpty(edt_count.getText().toString())) {
                    if (tag.equalsIgnoreCase("Summary")){
                        SummaryActivity.count.set(which, Integer.parseInt(edt_count.getText().toString()));
                        dialog.dismiss();
                        parent.hideKeyboard(parent);
                        SummaryActivity.summeryListAdapter.notifyDataSetChanged();
                    } else {
                        HomeFragment.count.set(which, Integer.parseInt(edt_count.getText().toString()));
                        dialog.dismiss();
                        parent.hideKeyboard(parent);
                        HomeFragment.adapter.notifyDataSetChanged();
                    }
                } else {
                    Utility.showToastMessage(parent, "Enter count");
                }
            }
        });

        dialog.show();
    }


    public static String httpJsonRequest(String url, HashMap<String, String> mParams, Context context) {
        String websiteData = "error";
        HttpClient client = new DefaultHttpClient();
        HttpConnectionParams.setConnectionTimeout(client.getParams(),
                CONNECTION_TIMEOUT); // Timeout
        // Limit
        HttpResponse response;
        HttpPost post = new HttpPost(url);
        Utility.showLog("session id ", "Session" + Utility.getSharedPrefStringData(context, Constants.LOGIN_SESSION_ID));
        post.setHeader("Cookie", "connect.sid=" + Utility.getSharedPrefStringData(context, Constants.LOGIN_SESSION_ID));
        StringEntity se;
        try {
            se = new StringEntity(getJsonParams(mParams));
            se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE,
                    "application/json"));
            post.setEntity(se);
            response = client.execute(post);

            if (response != null && response.getStatusLine().getStatusCode() == 401 || response.getStatusLine().getStatusCode() == 502) {
                websiteData = null;
            } else if (response != null) {
                websiteData = EntityUtils.toString(response.getEntity());
            }
        } catch (Exception e) {
            e.printStackTrace();
            websiteData = "error";
            return websiteData;
        }
        return websiteData;
    }

    public static String getJsonParams(HashMap<String, String> paramMap) {
        if (paramMap == null) {
            return null;
        }
        JSONObject jsonObject = new JSONObject();
        for (Map.Entry<String, String> entry : paramMap.entrySet()) {
            try {
                if (entry.getKey().equalsIgnoreCase("contacts")) {
                    JSONArray jsonArray = new JSONArray(entry
                            .getValue());
                    jsonObject.accumulate(entry.getKey(), jsonArray);
                } else if (entry.getKey().equalsIgnoreCase("SalesRecords")) {
                    /*JSONArray jsonArray = new JSONArray();
                    jsonArray.put(entry.getValue());*/
                    jsonObject.accumulate(entry.getKey(), entry.getValue());
                } else if (entry.getKey().equalsIgnoreCase("login")) {
                    JSONObject jsonArrayLogin = new JSONObject(entry
                            .getValue());
                    jsonObject.accumulate(entry.getKey(), jsonArrayLogin);
                } else if (entry.getKey().equalsIgnoreCase("StudentId")
                        || entry.getKey().equalsIgnoreCase("RoleType")) {
                    int i = (int) Double.parseDouble(entry.getValue());
                    jsonObject.accumulate(entry.getKey(), i);
                } else {
                    jsonObject.accumulate(entry.getKey(), entry
                            .getValue());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Utility.showLog("jsonObject", "jsonObject" + jsonObject.toString());
        return jsonObject.toString();
    }

    public static void showOKOnlyDialogNormal(final DashBoardActivity context, String msg,
                                              String title) {

        final Dialog mDialog = new Dialog(context);
        mDialog.requestWindowFeature(1);
        mDialog.setContentView(R.layout.session_dialog);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setCancelable(true);

        TextView tv_heading = (TextView) mDialog.findViewById(R.id.tv_heading);
        tv_heading.setText(msg);
        ((TextView) mDialog.findViewById(R.id.tv_ok)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Utility.showLog("Clicked", "Clicked");
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }

    public static void showOKOnlyDialog(final DashBoardActivity context, String msg,
                                        String title) {

        final Dialog mDialog = new Dialog(context);
        mDialog.requestWindowFeature(1);
        mDialog.setContentView(R.layout.session_dialog);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setCancelable(true);

        TextView tv_heading = (TextView) mDialog.findViewById(R.id.tv_heading);
        tv_heading.setText(msg);
        ((TextView) mDialog.findViewById(R.id.tv_ok)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Utility.showLog("Clicked", "Clicked");
                Utility.setSharedPrefStringData(context, Constants.LOGIN_SESSION_ID, "");
                Intent intent = new Intent(context, LoginActivity.class);
                context.startActivity(intent);
                context.finish();
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }

    public static void showOKOnlyDialog(final LoginActivity context, String msg,
                                        String title) {

        final Dialog mDialog = new Dialog(context);
        mDialog.requestWindowFeature(1);
        mDialog.setContentView(R.layout.session_dialog);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setCancelable(true);

        TextView tv_heading = (TextView) mDialog.findViewById(R.id.tv_heading);
        tv_heading.setText(msg);
        ((TextView) mDialog.findViewById(R.id.tv_ok)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Utility.showLog("Clicked", "Clicked");
                Utility.setSharedPrefStringData(context, Constants.LOGIN_SESSION_ID, "");
                Intent intent = new Intent(context, LoginActivity.class);
                context.startActivity(intent);
                context.finish();
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }

    /*Get Font Awesome Web font Type face*/
    public static Typeface setRobotoRegular(Context context) {
        return Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Regular.ttf");
    }

    /*Get Font Awesome Web font Type face*/
    public static Typeface setRobotoBold(Context context) {
        return Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Bold.ttf");
    }

    public static Typeface setLucidaSansItalic(Context context) {
        return Typeface.createFromAsset(context.getAssets(), "fonts/Lucida Sans Italic.ttf");
    }

    public static void navigateDashBoardFragment(Fragment fragment,
                                                 String tag, Bundle bundle, FragmentActivity fragmentActivity) {
        FragmentManager fragmentManager = fragmentActivity
                .getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager
                .beginTransaction();
        /*fragmentTransaction.setCustomAnimations(R.anim.slide_left_right,
                R.anim.fade_out);*/
        if (bundle != null) {
            fragment.setArguments(bundle);
        }
        fragmentTransaction.replace(R.id.content_frame, fragment, tag);
        fragmentTransaction.addToBackStack(tag);
        fragmentTransaction.commit();
    }

    /**
     * ASSIGN THE COLOR
     **/
    public static int getColor(Context context, int id) {
        final int version = Build.VERSION.SDK_INT;
        if (version >= 23)
            return ContextCompat.getColor(context, id);
        else
            return context.getResources().getColor(id);
    }
}

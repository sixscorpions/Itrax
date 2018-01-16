package com.itrax.aynctask;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;

import com.itrax.activities.LoginActivity;
import com.itrax.customviews.CustomProgressDialog;
import com.itrax.parser.Parser;
import com.itrax.utils.APIConstants;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;


/**
 * Created by Shankar on 3/7/2017.
 */
public abstract class BaseAsyncTask extends AsyncTask<Void, Void, Integer> {
    protected AnimationDrawable Anim;
    protected CustomProgressDialog mCustomProgressDialog = null;
    protected LoginActivity mContext;
    protected String mDialogMessage, mApiMessage;
    protected boolean mShowDialog;

    protected LinkedHashMap<String, String> mParams;
    protected APIConstants.REQUEST_TYPE mRequestType;
    protected IAsyncCaller caller;
    protected String mUrl;
    @SuppressWarnings("rawtypes")
    protected Parser parser;
    protected LinkedHashMap<String, File> mFileMap;
    protected LayoutInflater mLayoutInflater;
    protected File file;
    protected String tag;
    protected ArrayList<File> mFiles;

    /**
     * @param context       ,Context to show progress dialog
     * @param dialogMessage , Dialog message for progress dialog
     * @param showDialog    , boolean varialble to show progress dialog or not
     * @param url           , Url of the web service
     * @param mParams       , HashMap of keys
     * @param requestType   , Type of request(GET/POST)
     * @param caller        , Caller activity which will recieve response
     * @param parser        , JSON parser for the response
     */
    public BaseAsyncTask(LoginActivity context, String dialogMessage,
                         boolean showDialog, String url, LinkedHashMap<String, String> mParams,
                         APIConstants.REQUEST_TYPE requestType, IAsyncCaller caller, Parser parser) {

        this.mContext = context;
        this.mDialogMessage = dialogMessage;
        mShowDialog = showDialog;
        this.mParams = mParams;
        mRequestType = requestType;
        this.caller = caller;
        this.mUrl = url;
        this.parser = parser;
        mCustomProgressDialog = new CustomProgressDialog(mContext);
    }

    public BaseAsyncTask(LoginActivity context, String dialogMessage,
                         boolean showDialog, String url, LinkedHashMap<String, String> mParams,
                         APIConstants.REQUEST_TYPE requestType, IAsyncCaller caller, Parser parser, String tag, File file, ArrayList<File> mFiles) {

        this.mContext = context;
        this.mDialogMessage = dialogMessage;
        mShowDialog = showDialog;
        this.mParams = mParams;
        mRequestType = requestType;
        this.caller = caller;
        this.mUrl = url;
        this.parser = parser;
        this.file = file;
        this.mFiles = mFiles;
        this.tag = tag;
        mCustomProgressDialog = new CustomProgressDialog(mContext);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (mShowDialog) {
            mCustomProgressDialog.showProgress("");
        }
    }

    /**
     * Abstract method for interaction with Web Service.
     */
    public abstract Integer doInBackground(Void... params);

    @Override
    protected void onPostExecute(Integer result) {
        super.onPostExecute(result);
        if (mCustomProgressDialog != null) {
            mCustomProgressDialog.dismissProgress();
        }
    }
}

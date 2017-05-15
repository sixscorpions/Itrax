package com.itrax.customviews;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.itrax.R;
import com.itrax.utils.Utility;

/**
 * Created by Shankar on 3/7/2017.
 */

public class CustomProgressDialog {

    private Dialog mDialog;
    private TextView mTxtMessage;

    public CustomProgressDialog(Context context) {
        mDialog = new Dialog(context, R.style.style_new_dialog);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater mInflater = LayoutInflater.from(context);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        View layout = mInflater.inflate(R.layout.custom_progress_dialog, null);
        //for progress bar color
        ProgressBar mProgress = (ProgressBar) layout.findViewById(R.id.progress);
        mProgress.getIndeterminateDrawable().setColorFilter(context.getResources().getColor(R.color.colorAccent),
                android.graphics.PorterDuff.Mode.MULTIPLY);
        mDialog.setContentView(layout);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setCancelable(false);


        mTxtMessage = (TextView) mDialog.findViewById(R.id.tvProgressMessage);
    }

    public void showProgress(String message) {
        if (Utility.isValueNullOrEmpty(message)) {
            mTxtMessage.setVisibility(View.GONE);
        } else {
            mTxtMessage.setText("" + message);
        }

        if (mDialog != null) {
            mDialog.show();
        }
    }

    public void dismissProgress() {
        if (mDialog != null || mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }
}

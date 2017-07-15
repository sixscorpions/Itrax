package com.itrax.parser;

import com.itrax.models.LoginModel;
import com.itrax.models.Model;
import com.itrax.models.SendOtpModel;

import org.json.JSONObject;

/**
 * Created by shankar on 4/30/2017.
 */

public class SendOtpParser implements Parser<Model> {
    @Override
    public Model parse(String s) {
        SendOtpModel mSendOtpModel = new SendOtpModel();
        try {
            JSONObject jsonObject = new JSONObject(s);
            if (jsonObject.has("otp"))
                mSendOtpModel.setOtp(jsonObject.optString("otp"));
            if (jsonObject.has("message"))
                mSendOtpModel.setMessage(jsonObject.optString("message"));
            if (jsonObject.has("success"))
                mSendOtpModel.setSuccess(jsonObject.optBoolean("success"));
            if (jsonObject.has("err"))
                mSendOtpModel.setErr(jsonObject.optBoolean("err"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mSendOtpModel;
    }
}

package com.itrax.parser;


import android.content.Context;


import com.itrax.models.GetAppUpdateInfoModel;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Admin on 14/06/2018.
 */

public class GetAppUpdateInfoParser implements Parser<GetAppUpdateInfoModel> {
    GetAppUpdateInfoModel model = new GetAppUpdateInfoModel();


    @Override
    public GetAppUpdateInfoModel parse(String s, Context context)  {
        try {
            JSONObject jsonObject = new JSONObject(s);
            if(jsonObject.has("updateApp"))
                model.setUpdate(jsonObject.optBoolean("updateApp"));

            if(jsonObject.has("forceUpdateApp"))
                model.setForceToUpdate(jsonObject.optBoolean("forceUpdateApp"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return model;
    }
}

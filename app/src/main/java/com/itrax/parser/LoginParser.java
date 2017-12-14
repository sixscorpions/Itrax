package com.itrax.parser;

import android.content.Context;

import com.itrax.models.DynamicFieldsModel;
import com.itrax.models.LoginModel;
import com.itrax.models.Model;
import com.itrax.utils.Constants;
import com.itrax.utils.Utility;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by shankar on 4/30/2017.
 */

public class LoginParser implements Parser<Model> {
    @Override
    public Model parse(String s, Context context) {
        LoginModel loginModel = new LoginModel();
        try {
            JSONObject jsonObject = new JSONObject(s);
            if (jsonObject.has("Id"))
                loginModel.setId(jsonObject.optInt("Id"));
            if (jsonObject.has("OrganizationId"))
                loginModel.setOrganizationId(jsonObject.optInt("OrganizationId"));
            if (jsonObject.has("ZoneId"))
                loginModel.setZoneId(jsonObject.optInt("ZoneId"));
            if (jsonObject.has("BranchId"))
                loginModel.setBranchId(jsonObject.optInt("BranchId"));
            if (jsonObject.has("FirstName"))
                loginModel.setFirstName(jsonObject.optString("FirstName"));
            if (jsonObject.has("MiddleName"))
                loginModel.setMiddleName(jsonObject.optString("MiddleName"));
            if (jsonObject.has("LastName"))
                loginModel.setLastName(jsonObject.optString("LastName"));
            if (jsonObject.has("DateOfBirth"))
                loginModel.setDateOfBirth(jsonObject.optString("DateOfBirth"));
            if (jsonObject.has("Email"))
                loginModel.setEmail(jsonObject.optString("Email"));
            if (jsonObject.has("LicenceNumber"))
                loginModel.setLicenceNumber(jsonObject.optString("LicenceNumber"));
            if (jsonObject.has("LicenceIssuedDate"))
                loginModel.setLicenceIssuedDate(jsonObject.optString("LicenceIssuedDate"));
            if (jsonObject.has("LicenceExpiryDate"))
                loginModel.setLicenceExpiryDate(jsonObject.optString("LicenceExpiryDate"));
            if (jsonObject.has("Mobile"))
                loginModel.setMobile(jsonObject.optString("Mobile"));
            if (jsonObject.has("City"))
                loginModel.setCity(jsonObject.optString("City"));
            if (jsonObject.has("State"))
                loginModel.setState(jsonObject.optString("State"));
            if (jsonObject.has("Country"))
                loginModel.setCountry(jsonObject.optString("Country"));
            if (jsonObject.has("Pincode"))
                loginModel.setPincode(jsonObject.optString("Pincode"));
            if (jsonObject.has("IsActive"))
                loginModel.setIsActive(jsonObject.optBoolean("IsActive"));
            if (jsonObject.has("VehicleNumber"))
                loginModel.setVehicleNumber(jsonObject.optString("VehicleNumber"));

            if (jsonObject.has("OTPRequired"))
                loginModel.setOTPRequired(jsonObject.optBoolean("OTPRequired"));

            ArrayList<DynamicFieldsModel> dynamicFieldsModels = new ArrayList<>();
            if (jsonObject.has("ProFeatures")) {
                JSONArray jsonArray = jsonObject.optJSONArray("ProFeatures");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject2 = jsonArray.optJSONObject(i);
                    DynamicFieldsModel dynamicFieldsModel = new DynamicFieldsModel();
                    dynamicFieldsModel.setId(jsonObject2.optInt("id"));
                    dynamicFieldsModel.setLabel(jsonObject2.optString("Label"));
                    dynamicFieldsModel.setType(jsonObject2.optString("Type"));
                    dynamicFieldsModel.setList(jsonObject2.optString("List"));
                    dynamicFieldsModels.add(dynamicFieldsModel);
                }
                loginModel.setDynamicFieldsModels(dynamicFieldsModels);
            }

            Utility.setSharedPrefStringData(context, Constants.LOGIN_RESPONSE, s);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return loginModel;
    }
}

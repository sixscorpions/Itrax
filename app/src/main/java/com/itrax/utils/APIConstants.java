package com.itrax.utils;

/**
 * Created by shankar on 4/30/2017.
 */

public class APIConstants {
    public enum REQUEST_TYPE {
        GET, POST, MULTIPART_GET, MULTIPART_POST, DELETE, PUT, PATCH
    }

    private static final String STATUS = "status";
    public final static String SERVER_NOT_RESPONDING = "We are unable to connect the internet. " +
            "Please check your connection and try again.";

    public static String ERROR_MESSAGE = "We could not process your request at this time. Please try again later.";

    public static String BASE_URL = "http://itraxpro.com/api/v1.0/";
    //public static String BASE_URL = "http://icuepro.com/api/v1.0/";
    public static String LOGIN_URL = BASE_URL + "driverLogin";
    public static String PLOTPROPOINT_URL = BASE_URL + "plotProPoint";
    public static String GET_SALES_OTP = BASE_URL + "getSalesOtp";
    public static String CREATE_SALES_RECORD = BASE_URL + "createSalesRecord";


}

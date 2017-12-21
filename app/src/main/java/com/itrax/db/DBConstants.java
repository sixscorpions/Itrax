package com.itrax.db;

/**
 * Created by Shankar on 12/21/2017.
 */

public class DBConstants {

    /* Workouts table details */
    public static final String TABLE_CREATE_SALES_RECORD = "createSalesRecord";

    public static final String KEY_CREATE_SALES_ID = "_id";
    public static final String KEY_CREATE_SALES_COORDINATES = "coordinates";
    public static final String KEY_WEARABLE_AREA = "area";
    public static final String KEY_WEARABLE_TIME = "time";
    public static final String KEY_WEARABLE_NOTE = "note";
    public static final String KEY_WEARABLE_CUSTOMER_NAME = "customername";
    public static final String KEY_WEARABLE_CUSTOMER_MOBILE = "customermobile";
    public static final String KEY_WEARABLE_DUE_DATE = "duedate";
    public static final String KEY_WEARABLE_IS_OTP_VERIFIED = "IsOtpVerified";
    public static final String KEY_WEARABLE_INITIATED_DATE = "InitiatedDate";
    public static final String KEY_WEARABLE_INITIATED_TIME = "InitiatedTime";

    public static final String TABLE_CREATE_SALES = "CREATE TABLE IF NOT EXISTS "
            + TABLE_CREATE_SALES_RECORD
            + "("
            + KEY_CREATE_SALES_ID
            + " INTEGER PRIMARY KEY, "
            + KEY_CREATE_SALES_COORDINATES
            + " TEXT, "
            + KEY_WEARABLE_AREA
            + " TEXT, "
            + KEY_WEARABLE_TIME
            + " TEXT, "
            + KEY_WEARABLE_NOTE
            + " TEXT, "
            + KEY_WEARABLE_CUSTOMER_NAME
            + " TEXT, "
            + KEY_WEARABLE_CUSTOMER_MOBILE
            + " TEXT, "
            + KEY_WEARABLE_DUE_DATE
            + " TEXT, "
            + KEY_WEARABLE_IS_OTP_VERIFIED
            + " TEXT, "
            + KEY_WEARABLE_INITIATED_DATE
            + " TEXT, "
            + KEY_WEARABLE_INITIATED_TIME
            + " TEXT " + ");";

}

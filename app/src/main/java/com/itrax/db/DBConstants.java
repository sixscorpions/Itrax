package com.itrax.db;

/**
 * Created by Shankar on 12/24/2017.
 */

public class DBConstants {

    public static final String TABLE_CREATE_SALES_HISTORY = "create_sales";

    public static final String CREATE_SALES_ID = "_id";
    public static final String CREATE_SALES_COORDINATES = "coordinates";
    public static final String CREATE_SALES_AREA = "area";
    public static final String CREATE_SALES_TIME = "time";
    public static final String CREATE_SALES_NOTE = "note";
    public static final String CREATE_SALES_CUSTOMER_NAME = "customer_name";
    public static final String CREATE_SALES_CUSTOMER_MOBILE = "customer_mobile";
    public static final String CREATE_SALES_DUE_DATE = "due_date";
    public static final String CREATE_SALES_ISOTPVERIFIED = "isotpverified";
    public static final String CREATE_SALES_INITIATED_DATE = "InitiatedDate";
    public static final String CREATE_SALES_INITIATED_TIME = "InitiatedTime";
    public static final String CREATE_SALES_ADDITIONAL_INFO = "AdditionalInfo";

    public static final String CREATE_TABLE_CREATE_SALES = "CREATE TABLE IF NOT EXISTS " +
            TABLE_CREATE_SALES_HISTORY
            + "(" + CREATE_SALES_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + CREATE_SALES_COORDINATES + "  TEXT , "
            + CREATE_SALES_AREA + "  TEXT , "
            + CREATE_SALES_TIME + "  TEXT , "
            + CREATE_SALES_NOTE + "  TEXT , "
            + CREATE_SALES_CUSTOMER_NAME + "  TEXT , "
            + CREATE_SALES_CUSTOMER_MOBILE + "  TEXT , "
            + CREATE_SALES_DUE_DATE + "  TEXT , "
            + CREATE_SALES_ISOTPVERIFIED + "  TEXT ,"
            + CREATE_SALES_INITIATED_DATE + "  TEXT ,"
            + CREATE_SALES_INITIATED_TIME + "  TEXT ,"
            + CREATE_SALES_ADDITIONAL_INFO + "  TEXT "
            + ")";
}

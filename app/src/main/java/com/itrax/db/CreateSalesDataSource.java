package com.itrax.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.itrax.models.CreateSalesDataModel;

import java.util.ArrayList;

/**
 * Created by Shankar on 12/21/2017.
 */

public class CreateSalesDataSource {
    private SQLiteDatabase mDatabase;
    private DatabaseHandler mHandler;
    private Context mContext;
    private String mColumns[] = {DBConstants.KEY_CREATE_SALES_ID,
            DBConstants.KEY_CREATE_SALES_COORDINATES, DBConstants.KEY_WEARABLE_AREA,
            DBConstants.KEY_WEARABLE_TIME, DBConstants.KEY_WEARABLE_NOTE,
            DBConstants.KEY_WEARABLE_CUSTOMER_NAME, DBConstants.KEY_WEARABLE_CUSTOMER_MOBILE,
            DBConstants.KEY_WEARABLE_DUE_DATE, DBConstants.KEY_WEARABLE_IS_OTP_VERIFIED,
            DBConstants.KEY_WEARABLE_INITIATED_DATE, DBConstants.KEY_WEARABLE_INITIATED_TIME
    };

    public CreateSalesDataSource(Context context) {
        if (context != null) {
            mContext = context;
            mHandler = new DatabaseHandler(mContext);
        }
    }

    private void open() {
        if (mHandler != null) {
            mDatabase = mHandler.getWritableDatabase();
        }
    }

    private void close() {
        if (mDatabase != null) {
            mDatabase.close();
        }
    }

    public long insertData(CreateSalesDataModel model) {
        long insertValue = -1;

        ContentValues values = new ContentValues();
        values.put(DBConstants.KEY_CREATE_SALES_COORDINATES, model.getCoordinates());
        values.put(DBConstants.KEY_WEARABLE_AREA, model.getArea());
        values.put(DBConstants.KEY_WEARABLE_TIME, model.getTime());
        values.put(DBConstants.KEY_WEARABLE_NOTE, model.getNote());
        values.put(DBConstants.KEY_WEARABLE_CUSTOMER_NAME, model.getCustomername());
        values.put(DBConstants.KEY_WEARABLE_CUSTOMER_MOBILE, model.getCustomermobile());
        values.put(DBConstants.KEY_WEARABLE_DUE_DATE, model.getDuedate());
        values.put(DBConstants.KEY_WEARABLE_IS_OTP_VERIFIED, model.getIsOtpVerified());
        values.put(DBConstants.KEY_WEARABLE_INITIATED_DATE, model.getInitiatedDate());
        values.put(DBConstants.KEY_WEARABLE_INITIATED_TIME, model.getInitiatedTime());
        open();
        insertValue = mDatabase.insert(DBConstants.TABLE_CREATE_SALES_RECORD, null,
                values);
        close();
        return insertValue;
    }


    /* Get all workouts models */
    public ArrayList<CreateSalesDataModel> selectAll() {
        ArrayList<CreateSalesDataModel> createSalesDataModels = null;
        open();
        Cursor cursor = mDatabase.query(DBConstants.TABLE_CREATE_SALES_RECORD, mColumns,
                null, null, null, null, null);
        if (cursor.getCount() > 0) {
            createSalesDataModels = new ArrayList<>();
            while (cursor.moveToNext()) {
                CreateSalesDataModel model = new CreateSalesDataModel();
                model.setCoordinates(cursor.getString(cursor
                        .getColumnIndex(DBConstants.KEY_CREATE_SALES_COORDINATES)));
                model.setArea(cursor.getString(cursor
                        .getColumnIndex(DBConstants.KEY_WEARABLE_AREA)));
                model.setTime(cursor.getString(cursor
                        .getColumnIndex(DBConstants.KEY_WEARABLE_TIME)));
                model.setNote(cursor.getString(cursor
                        .getColumnIndex(DBConstants.KEY_WEARABLE_NOTE)));
                model.setCustomername(cursor.getString(cursor
                        .getColumnIndex(DBConstants.KEY_WEARABLE_CUSTOMER_NAME)));
                model.setCustomermobile(cursor.getString(cursor
                        .getColumnIndex(DBConstants.KEY_WEARABLE_CUSTOMER_MOBILE)));
                model.setDuedate(cursor.getString(cursor
                        .getColumnIndex(DBConstants.KEY_WEARABLE_DUE_DATE)));
                model.setIsOtpVerified(cursor.getString(cursor
                        .getColumnIndex(DBConstants.KEY_WEARABLE_IS_OTP_VERIFIED)));
                model.setInitiatedDate(cursor.getString(cursor
                        .getColumnIndex(DBConstants.KEY_WEARABLE_INITIATED_DATE)));
                model.setInitiatedTime(cursor.getString(cursor
                        .getColumnIndex(DBConstants.KEY_WEARABLE_INITIATED_TIME)));
                createSalesDataModels.add(model);
            }
        }
        cursor.close();
        close();
        return createSalesDataModels;
    }


    /* Get Workouts count */
    public int getCreateSalesDataCount() {
        int brandCount = -1;
        open();
        Cursor cursor = mDatabase.query(DBConstants.TABLE_CREATE_SALES_RECORD, new String[]{DBConstants.KEY_CREATE_SALES_COORDINATES},
                null, null, null, null, null);
        if (cursor.getCount() > 0) {
            brandCount = cursor.getCount();
        }
        cursor.close();
        close();
        return brandCount;
    }

    /* Delete depends on date */
    public int deleteWearableBySelection(String position) {
        int deleteValue = -1;
        open();
        deleteValue = mDatabase.delete(DBConstants.TABLE_CREATE_SALES_RECORD, DBConstants.KEY_CREATE_SALES_COORDINATES
                + " = ?", new String[]{position});
        close();
        return deleteValue;
    }

    /* Delete depends on date */
    public int deleteWearableBySelectionByName(String address) {
        int deleteValue = -1;
        open();
        deleteValue = mDatabase.delete(DBConstants.TABLE_CREATE_SALES_RECORD, DBConstants.KEY_CREATE_SALES_COORDINATES
                + " = ?", new String[]{address});
        close();
        return deleteValue;
    }

    public int deleteAll() {
        int deleteValue = -1;
        open();
        deleteValue = mDatabase.delete(DBConstants.TABLE_CREATE_SALES_RECORD, null, null);
        close();

        return deleteValue;
    }

    public boolean isExisting(String name) {
        boolean isRead;
        open();
        Cursor cursor = mDatabase.query(DBConstants.TABLE_CREATE_SALES_RECORD, mColumns,
                mColumns[1] + " = ?", new String[]{"" + name}, null, null, null);
        if (cursor.getCount() > 0) {
            isRead = true;
        } else {
            isRead = false;
        }
        cursor.close();
        close();
        return isRead;
    }

    public boolean isExistingAddress(String address) {
        boolean isRead;
        open();
        Cursor cursor = mDatabase.query(DBConstants.TABLE_CREATE_SALES_RECORD, mColumns,
                mColumns[2] + " = ?", new String[]{"" + address}, null, null, null);
        if (cursor.getCount() > 0) {
            isRead = true;
        } else {
            isRead = false;
        }
        cursor.close();
        close();
        return isRead;
    }
}
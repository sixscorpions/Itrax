package com.itrax.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.itrax.models.CreateSalesModel;

import java.util.ArrayList;

/**
 * Created by Shankar on 12/24/2017.
 */

public class CreateSalesDataSource {

    private SQLiteDatabase mDatabase;
    private DatabaseHandler mHandler;
    private Context mContext;
    private String mColumns[] = {DBConstants.CREATE_SALES_ID, DBConstants.CREATE_SALES_COORDINATES,
            DBConstants.CREATE_SALES_AREA, DBConstants.CREATE_SALES_TIME,
            DBConstants.CREATE_SALES_NOTE, DBConstants.CREATE_SALES_CUSTOMER_NAME,
            DBConstants.CREATE_SALES_CUSTOMER_MOBILE, DBConstants.CREATE_SALES_DUE_DATE,
            DBConstants.CREATE_SALES_ISOTPVERIFIED, DBConstants.CREATE_SALES_INITIATED_DATE,
            DBConstants.CREATE_SALES_INITIATED_TIME, DBConstants.CREATE_SALES_ADDITIONAL_INFO};

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

    public long insertData(CreateSalesModel model) {
        long insertValue = -1;

        ContentValues values = new ContentValues();
        values.put(DBConstants.CREATE_SALES_COORDINATES, model.getCoordinates());
        values.put(DBConstants.CREATE_SALES_AREA, model.getArea());
        values.put(DBConstants.CREATE_SALES_TIME, model.getTime());
        values.put(DBConstants.CREATE_SALES_NOTE, model.getNote());
        values.put(DBConstants.CREATE_SALES_CUSTOMER_NAME, model.getCustomer_name());
        values.put(DBConstants.CREATE_SALES_CUSTOMER_MOBILE, model.getCustomer_mobile());
        values.put(DBConstants.CREATE_SALES_DUE_DATE, model.getDue_date());
        values.put(DBConstants.CREATE_SALES_ISOTPVERIFIED, model.getIsotpverified());
        values.put(DBConstants.CREATE_SALES_INITIATED_DATE, model.getInitiatedDate());
        values.put(DBConstants.CREATE_SALES_INITIATED_TIME, model.getInitiatedTime());
        values.put(DBConstants.CREATE_SALES_ADDITIONAL_INFO, model.getAdditional_info());
        open();
        insertValue = mDatabase.insert(DBConstants.TABLE_CREATE_SALES_HISTORY, null,
                values);
        close();
        return insertValue;
    }


    /* Get all workouts models */
    public ArrayList<CreateSalesModel> selectAll() {
        ArrayList<CreateSalesModel> createSalesModels = null;
        open();
        Cursor cursor = mDatabase.query(DBConstants.TABLE_CREATE_SALES_HISTORY, mColumns,
                null, null, null, null, null);
        if (cursor.getCount() > 0) {
            createSalesModels = new ArrayList<CreateSalesModel>();
            while (cursor.moveToNext()) {
                CreateSalesModel model = new CreateSalesModel();
                model.setCoordinates(cursor.getString(cursor
                        .getColumnIndex(DBConstants.CREATE_SALES_COORDINATES)));
                model.setArea(cursor.getString(cursor
                        .getColumnIndex(DBConstants.CREATE_SALES_AREA)));
                model.setTime(cursor.getString(cursor
                        .getColumnIndex(DBConstants.CREATE_SALES_TIME)));
                model.setNote(cursor.getString(cursor
                        .getColumnIndex(DBConstants.CREATE_SALES_NOTE)));
                model.setCustomer_name(cursor.getString(cursor
                        .getColumnIndex(DBConstants.CREATE_SALES_CUSTOMER_NAME)));
                model.setCustomer_mobile(cursor.getString(cursor
                        .getColumnIndex(DBConstants.CREATE_SALES_CUSTOMER_MOBILE)));
                model.setDue_date(cursor.getString(cursor
                        .getColumnIndex(DBConstants.CREATE_SALES_DUE_DATE)));
                model.setIsotpverified(cursor.getString(cursor
                        .getColumnIndex(DBConstants.CREATE_SALES_ISOTPVERIFIED)));
                model.setInitiatedDate(cursor.getString(cursor
                        .getColumnIndex(DBConstants.CREATE_SALES_INITIATED_DATE)));
                model.setInitiatedTime(cursor.getString(cursor
                        .getColumnIndex(DBConstants.CREATE_SALES_INITIATED_TIME)));
                model.setAdditional_info(cursor.getString(cursor
                        .getColumnIndex(DBConstants.CREATE_SALES_ADDITIONAL_INFO)));
                createSalesModels.add(model);
            }
        }
        cursor.close();
        close();
        return createSalesModels;
    }


    public int deleteAll() {
        int deleteValue = -1;
        open();
        deleteValue = mDatabase.delete(DBConstants.TABLE_CREATE_SALES_HISTORY, null, null);
        close();

        return deleteValue;
    }

    /* Get count */
    public int getDataCount() {
        int brandCount = -1;
        open();
        Cursor cursor = mDatabase.query(DBConstants.TABLE_CREATE_SALES_HISTORY, new String[]{DBConstants.CREATE_SALES_ID},
                null, null, null, null, null);
        if (cursor.getCount() > 0) {
            brandCount = cursor.getCount();
        }
        cursor.close();
        close();
        return brandCount;
    }
}
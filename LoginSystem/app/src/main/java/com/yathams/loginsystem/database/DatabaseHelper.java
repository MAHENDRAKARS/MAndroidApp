package com.yathams.loginsystem.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.yathams.loginsystem.pojo.ProductItem;
import com.yathams.loginsystem.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "applicationdata";

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_CREATE_TABLE_PRODUCT_NAMES = "create table "
            + DBAdapter.DATABASE_TABLE_PRODUCT_NAMES + "(_id integer primary key autoincrement, "
            + DBAdapter.KEY_PRODUCT_NAME + " text not null);";
    private Context mContext = null;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(DATABASE_CREATE_TABLE_PRODUCT_NAMES);
            String json = Utils.readTextFileFromAssets(mContext, "product_names.txt");
            ProductNamesResp productNamesResp = null;
            if (json.length() > 0) {
                productNamesResp = new Gson().fromJson(json, ProductNamesResp.class);
                initilizeProductNamesTable(db, productNamesResp.productItems);
            }
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void initilizeProductNamesTable(SQLiteDatabase db, List<ProductItem> productItems) {

        for (ProductItem countryCode : productItems) {
            try {
                ContentValues values = new ContentValues();
                values.put(DBAdapter.KEY_PRODUCT_NAME, countryCode.name);
                long rowId = db.insert(DBAdapter.DATABASE_TABLE_PRODUCT_NAMES, null, values);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//		Log.w(DatabaseHelper.class.getName(),
//				"Upgrading database from version " + oldVersion + " to "
//						+ newVersion + ", which will destroy all old data");
//		db.execSQL("DROP TABLE IF EXISTS " + DBAdapter.DATABASE_TABLE_STATIONS);
        onCreate(db);

    }

    public class ProductNamesResp {
        public List<ProductItem> productItems = new ArrayList<>();
    }

}

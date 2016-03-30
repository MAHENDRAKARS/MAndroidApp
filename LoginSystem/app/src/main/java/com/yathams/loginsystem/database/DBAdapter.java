package com.yathams.loginsystem.database;


import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.yathams.loginsystem.pojo.ProductItem;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Venka Reddy
 * 
 */
public class DBAdapter {
	// Database fields

	public static final String DATABASE_TABLE_PRODUCT_NAMES = "productNames";
	public static final String KEY_PRODUCT_NAME = "productName";

	private Context context;
	private SQLiteDatabase database;
	private DatabaseHelper dbHelper;

	public DBAdapter(Context context) {
		this.context = context;
	}

	public DBAdapter open() throws SQLException {
		dbHelper = new DatabaseHelper(context);
		database = dbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		dbHelper.close();
	}

	/**
	 *
	 * @return
     */
	public List<ProductItem> getProductNames() {
		List<ProductItem> productItems = new ArrayList<>();
		Cursor cursor = null;
		cursor = database.query(DATABASE_TABLE_PRODUCT_NAMES, new String[] {
				KEY_PRODUCT_NAME },	null, null, null, null, null);
		if(cursor!=null){
			if(cursor.getCount() > 0){
				while (cursor.moveToNext()) {
					ProductItem productItem = new ProductItem(cursor.getString(0));
					productItems.add(productItem);
				}
			}
			cursor.close();
		}
		return productItems;
	}

	/**
	 *
	 * @return
	 */
	public List<String> getProductNameStrings() {
		List<String> productItemStrings = new ArrayList<>();
		Cursor cursor = null;
		cursor = database.query(DATABASE_TABLE_PRODUCT_NAMES, new String[] {
				KEY_PRODUCT_NAME },	null, null, null, null, null);
		if(cursor!=null){
			if(cursor.getCount() > 0){
				while (cursor.moveToNext()) {
					productItemStrings.add(cursor.getString(0));
				}
			}
			cursor.close();
		}
		return productItemStrings;
	}
}

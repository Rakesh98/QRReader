package com.rakesh.mobile.qrreader.data_base;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by rakesh.jnanagari on 07/05/17.
 */

public class ScanDataBaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "scan_database";
    public static final String SCAN_TABLE_NAME = "scanned_data";
    public static final String SCAN_COLUMN_ID = "_id";
    public static final String SCAN_COLUMN_RESULT = "result";
    public static final String SCAN_COLUMN_DATE = "date";
    public static final String SCAN_COLUMN_TYPE = "type";

    public ScanDataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + SCAN_TABLE_NAME + " (" +
                SCAN_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                SCAN_COLUMN_RESULT + " TEXT, " +
                SCAN_COLUMN_DATE + " TEXT, " +
                SCAN_COLUMN_TYPE + " TEXT" + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + SCAN_TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
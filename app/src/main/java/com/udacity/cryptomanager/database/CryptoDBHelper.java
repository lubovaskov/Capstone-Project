package com.udacity.cryptomanager.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.udacity.cryptomanager.database.CryptoDBContract.ListingsEntry;
import com.udacity.cryptomanager.database.CryptoDBContract.GlobalDataEntry;

public class CryptoDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "cryptomanager.db";

    private static final int DATABASE_VERSION = 1;

    public CryptoDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_LISTINGS_TABLE =
                "CREATE TABLE " + ListingsEntry.TABLE_NAME + " (" +
                        ListingsEntry._ID                 + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        ListingsEntry.COLUMN_LISTING_ID     + " INTEGER NOT NULL, " +
                        ListingsEntry.COLUMN_NAME        + " TEXT NOT NULL, " +
                        ListingsEntry.COLUMN_SYMBOL        + " TEXT NOT NULL, " +
                        ListingsEntry.COLUMN_RANK + " INTEGER, " +
                        ListingsEntry.COLUMN_CIRCULATING_SUPPLY  + " REAL, " +
                        ListingsEntry.COLUMN_TOTAL_SUPPLY + " REAL, " +
                        ListingsEntry.COLUMN_MAX_SUPPLY + " REAL, " +
                        ListingsEntry.COLUMN_PRICE + " REAL, " +
                        ListingsEntry.COLUMN_VOLUME_24H + " REAL, " +
                        ListingsEntry.COLUMN_MARKET_CAP + " REAL, " +
                        ListingsEntry.COLUMN_CHANGE_1H + " REAL, " +
                        ListingsEntry.COLUMN_CHANGE_24H + " REAL, " +
                        ListingsEntry.COLUMN_CHANGE_7d + " REAL, " +
                        ListingsEntry.COLUMN_LAST_UPDATED + " INTEGER, " +
                        " UNIQUE (" + ListingsEntry.COLUMN_LISTING_ID + ") ON CONFLICT REPLACE);";

        final String SQL_CREATE_GLOBAL_DATA_TABLE =
                "CREATE TABLE " + GlobalDataEntry.TABLE_NAME + " (" +
                        GlobalDataEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        GlobalDataEntry.COLUMN_GLOBAL_DATA_ID + " INTEGER NOT NULL, " +
                        GlobalDataEntry.COLUMN_GLOBAL_DATA_ACTIVE_CRYPTOCURRENCIES + " INTEGER, " +
                        GlobalDataEntry.COLUMN_GLOBAL_DATA_ACTIVE_MARKETS + " INTEGER, " +
                        GlobalDataEntry.COLUMN_GLOBAL_DATA_BTC_MARKET_CAP + " REAL, " +
                        GlobalDataEntry.COLUMN_GLOBAL_DATA_TOTAL_MARKET_CAP + " REAL, " +
                        GlobalDataEntry.COLUMN_GLOBAL_DATA_TOTAL_VOLUME + " REAL, " +
                        GlobalDataEntry.COLUMN_GLOBAL_DATA_LAST_UPDATED + " INTEGER, " +
                        " UNIQUE (" + GlobalDataEntry.COLUMN_GLOBAL_DATA_ID + ") ON CONFLICT REPLACE);";

        sqLiteDatabase.execSQL(SQL_CREATE_GLOBAL_DATA_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_LISTINGS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + GlobalDataEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ListingsEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}

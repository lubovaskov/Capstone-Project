package com.udacity.cryptomanager.database;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import com.udacity.cryptomanager.valueobjects.GlobalData;
import com.udacity.cryptomanager.valueobjects.GlobalQuote;
import com.udacity.cryptomanager.valueobjects.GlobalUSD;
import com.udacity.cryptomanager.valueobjects.Listing;
import com.udacity.cryptomanager.valueobjects.Ticker;
import com.udacity.cryptomanager.valueobjects.TickerQuote;
import com.udacity.cryptomanager.valueobjects.TickerUSD;

import java.util.ArrayList;
import java.util.List;

public class CryptoDBResolver {

    private static final String[] LISTINGS_ID_PROJECTION = {
            CryptoDBContract.ListingsEntry.COLUMN_LISTING_ID
    };

    private static final String[] TICKERS_PROJECTION = {
            CryptoDBContract.ListingsEntry.COLUMN_LISTING_ID,
            CryptoDBContract.ListingsEntry.COLUMN_NAME,
            CryptoDBContract.ListingsEntry.COLUMN_SYMBOL,
            CryptoDBContract.ListingsEntry.COLUMN_RANK,
            CryptoDBContract.ListingsEntry.COLUMN_MARKET_CAP,
            CryptoDBContract.ListingsEntry.COLUMN_CIRCULATING_SUPPLY,
            CryptoDBContract.ListingsEntry.COLUMN_MAX_SUPPLY,
            CryptoDBContract.ListingsEntry.COLUMN_TOTAL_SUPPLY,
            CryptoDBContract.ListingsEntry.COLUMN_PRICE,
            CryptoDBContract.ListingsEntry.COLUMN_VOLUME_24H,
            CryptoDBContract.ListingsEntry.COLUMN_CHANGE_1H,
            CryptoDBContract.ListingsEntry.COLUMN_CHANGE_24H,
            CryptoDBContract.ListingsEntry.COLUMN_CHANGE_7d,
            CryptoDBContract.ListingsEntry.COLUMN_LAST_UPDATED
    };

    private static final String[] GLOBAL_DATA_PROJECTION = {
            CryptoDBContract.GlobalDataEntry.COLUMN_GLOBAL_DATA_ID,
            CryptoDBContract.GlobalDataEntry.COLUMN_GLOBAL_DATA_ACTIVE_CRYPTOCURRENCIES,
            CryptoDBContract.GlobalDataEntry.COLUMN_GLOBAL_DATA_ACTIVE_MARKETS,
            CryptoDBContract.GlobalDataEntry.COLUMN_GLOBAL_DATA_BTC_MARKET_CAP,
            CryptoDBContract.GlobalDataEntry.COLUMN_GLOBAL_DATA_TOTAL_MARKET_CAP,
            CryptoDBContract.GlobalDataEntry.COLUMN_GLOBAL_DATA_TOTAL_VOLUME,
            CryptoDBContract.GlobalDataEntry.COLUMN_GLOBAL_DATA_LAST_UPDATED
    };

    @NonNull
    public static Loader<Cursor> getTickersLoader(@NonNull Context context) {
        Uri uri = CryptoDBContract.ListingsEntry.CONTENT_URI;
        String sortOrder = CryptoDBContract.ListingsEntry.COLUMN_RANK + " ASC";
        return new CursorLoader(context, uri, TICKERS_PROJECTION, null, null, sortOrder);
    }

    @NonNull
    public static Loader<Cursor> getGlobalDataLoader(@NonNull Context context) {
        Uri uri = CryptoDBContract.GlobalDataEntry.CONTENT_URI;
        return new CursorLoader(context, uri, GLOBAL_DATA_PROJECTION, null, null, null);
    }

    //load all tickers from a cursor into a list
    public static List<Ticker> loadTickersFromCursor(@NonNull Cursor cursor) {
        List<Ticker> tickers = new ArrayList<>();

        if (cursor.getCount() != 0) {
            int columnIndexListingId =
                    cursor.getColumnIndex(CryptoDBContract.ListingsEntry.COLUMN_LISTING_ID);
            int columnIndexName =
                    cursor.getColumnIndex(CryptoDBContract.ListingsEntry.COLUMN_NAME);
            int columnIndexSymbol =
                    cursor.getColumnIndex(CryptoDBContract.ListingsEntry.COLUMN_SYMBOL);
            int columnIndexRank =
                    cursor.getColumnIndex(CryptoDBContract.ListingsEntry.COLUMN_RANK);
            int columnIndexCirculatingSupply =
                    cursor.getColumnIndex(CryptoDBContract.ListingsEntry.COLUMN_CIRCULATING_SUPPLY);
            int columnIndexTotalSupply =
                    cursor.getColumnIndex(CryptoDBContract.ListingsEntry.COLUMN_TOTAL_SUPPLY);
            int columnIndexMaxSupply =
                    cursor.getColumnIndex(CryptoDBContract.ListingsEntry.COLUMN_MAX_SUPPLY);
            int columnIndexPrice =
                    cursor.getColumnIndex(CryptoDBContract.ListingsEntry.COLUMN_PRICE);
            int columnIndexVolume24h =
                    cursor.getColumnIndex(CryptoDBContract.ListingsEntry.COLUMN_VOLUME_24H);
            int columnIndexMarketCap =
                    cursor.getColumnIndex(CryptoDBContract.ListingsEntry.COLUMN_MARKET_CAP);
            int columnIndexChange1h =
                    cursor.getColumnIndex(CryptoDBContract.ListingsEntry.COLUMN_CHANGE_1H);
            int columnIndexChange24h =
                    cursor.getColumnIndex(CryptoDBContract.ListingsEntry.COLUMN_CHANGE_24H);
            int columnIndexChange7d =
                    cursor.getColumnIndex(CryptoDBContract.ListingsEntry.COLUMN_CHANGE_7d);
            int columnIndexLastUpdated =
                    cursor.getColumnIndex(CryptoDBContract.ListingsEntry.COLUMN_LAST_UPDATED);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                tickers.add(
                        new Ticker(
                                cursor.getInt(columnIndexListingId),
                                cursor.getString(columnIndexName),
                                cursor.getString(columnIndexSymbol),
                                null,
                                cursor.getInt(columnIndexRank),
                                cursor.getDouble(columnIndexCirculatingSupply),
                                cursor.getDouble(columnIndexTotalSupply),
                                cursor.getDouble(columnIndexMaxSupply),
                                new TickerQuote(
                                        new TickerUSD(
                                                cursor.getDouble(columnIndexPrice),
                                                cursor.getDouble(columnIndexVolume24h),
                                                cursor.getDouble(columnIndexMarketCap),
                                                cursor.getDouble(columnIndexChange1h),
                                                cursor.getDouble(columnIndexChange24h),
                                                cursor.getDouble(columnIndexChange7d)
                                        )
                                ),
                                cursor.getLong(columnIndexLastUpdated)
                        )
                );
                cursor.moveToNext();
            }
        }

        return tickers;
    }

    //load all listings from the database into a list
    public static List<Ticker> loadTickersFromDB(@NonNull Context context, int limit) {
        List<Ticker> tickers = null;

        ContentResolver cr = context.getContentResolver();

        Uri uri = CryptoDBContract.ListingsEntry.CONTENT_URI;

        //load first <limit> tickers from the database, sorted by rank
        String sortOrder = CryptoDBContract.ListingsEntry.COLUMN_RANK + " ASC";
        if (limit > 0) {
            sortOrder = sortOrder + " LIMIT " + limit;
        }
        Cursor cursor = cr.query(uri, TICKERS_PROJECTION, null, null, sortOrder);

        if (cursor != null) {
            try {
                tickers = loadTickersFromCursor(cursor);
            } finally {
                cursor.close();
            }
        }

        return tickers;
    }

    //load global data from a cursor into a GlobalData object
    public static GlobalData loadGlobalDataFromCursor(@NonNull Cursor cursor) {
        GlobalData globalData = null;
        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            globalData = new GlobalData(
                    cursor.getInt(cursor.getColumnIndex(
                            CryptoDBContract.GlobalDataEntry.COLUMN_GLOBAL_DATA_ACTIVE_CRYPTOCURRENCIES)),
                    cursor.getInt(cursor.getColumnIndex(
                            CryptoDBContract.GlobalDataEntry.COLUMN_GLOBAL_DATA_ACTIVE_MARKETS)),
                    cursor.getDouble(cursor.getColumnIndex(
                            CryptoDBContract.GlobalDataEntry.COLUMN_GLOBAL_DATA_BTC_MARKET_CAP)),
                    new GlobalQuote(
                            new GlobalUSD(
                                    cursor.getDouble(cursor.getColumnIndex(
                                            CryptoDBContract.GlobalDataEntry.COLUMN_GLOBAL_DATA_TOTAL_MARKET_CAP)),
                                    cursor.getDouble(cursor.getColumnIndex(
                                            CryptoDBContract.GlobalDataEntry.COLUMN_GLOBAL_DATA_TOTAL_VOLUME))
                            )
                    ),
                    cursor.getLong(cursor.getColumnIndex(
                            CryptoDBContract.GlobalDataEntry.COLUMN_GLOBAL_DATA_LAST_UPDATED))
            );
        }
        return globalData;
    }

    //select all listing IDs from the database into a list
    public static void loadListingIDsFromDB(@NonNull Context context, @NonNull List<Integer> idList) {
        idList.clear();

        ContentResolver cr = context.getContentResolver();

        Uri uri = CryptoDBContract.ListingsEntry.CONTENT_URI;

        //load IDs of all listings in the database, sorted by ID
        String sortOrder = CryptoDBContract.ListingsEntry.COLUMN_LISTING_ID + " ASC";
        Cursor cursor = cr.query(uri, LISTINGS_ID_PROJECTION, null, null, sortOrder);

        if (cursor != null) {
            try {
                if (cursor.getCount() != 0) {
                    int columnIndexListingId =
                            cursor.getColumnIndex(CryptoDBContract.ListingsEntry.COLUMN_LISTING_ID);
                    cursor.moveToFirst();
                    while (!cursor.isAfterLast()) {
                        idList.add(cursor.getInt(columnIndexListingId));
                        cursor.moveToNext();
                    }
                }
            } finally {
                cursor.close();
            }
        }
    }

    //insert or update a given list of listings into the database
    public static void saveListingsToDB(@NonNull Context context, @NonNull List<Listing> listings) {
        ContentResolver cr = context.getContentResolver();

        Uri uri = CryptoDBContract.ListingsEntry.CONTENT_URI;

        ContentValues[] valuesArray = new ContentValues[listings.size()];

        for (int i = 0; i < listings.size(); i++) {
            Listing listing = listings.get(i);
            ContentValues values = new ContentValues(3);
            values.put(CryptoDBContract.ListingsEntry.COLUMN_LISTING_ID, listing.id);
            values.put(CryptoDBContract.ListingsEntry.COLUMN_NAME, listing.name);
            values.put(CryptoDBContract.ListingsEntry.COLUMN_SYMBOL, listing.symbol);
            valuesArray[i] = values;
        }

        cr.bulkInsert(uri, valuesArray);
    }

    //delete all tickers from the database
    public static void clearTickers(@NonNull Context context) {
        ContentResolver cr = context.getContentResolver();

        Uri uri = CryptoDBContract.ListingsEntry.CONTENT_URI;
        cr.delete(uri, null, null);
    }

    public static void deleteTicker(@NonNull Context context, int tickerId) {
        ContentResolver cr = context.getContentResolver();

        Uri uri = CryptoDBContract.ListingsEntry.CONTENT_URI;
        cr.delete(ContentUris.withAppendedId(uri, tickerId), null, null);
    }

    //update all tickers in the database using the data in a given list of tickers
    public static void updateTickersInDB(@NonNull Context context, @NonNull List<Ticker> tickers) {
        ContentResolver cr = context.getContentResolver();

        Uri uri = CryptoDBContract.ListingsEntry.CONTENT_URI;

        //prepare a list of bulk updates
        ArrayList<ContentProviderOperation> operations = new ArrayList<>();
        //create an operation for updating the database with the data from the tickers list
        for (Ticker ticker : tickers) {
            ContentValues values = new ContentValues();
            values.put(CryptoDBContract.ListingsEntry.COLUMN_LISTING_ID,
                    ticker.id);
            values.put(CryptoDBContract.ListingsEntry.COLUMN_NAME,
                    ticker.name);
            values.put(CryptoDBContract.ListingsEntry.COLUMN_SYMBOL,
                    ticker.symbol);
            values.put(CryptoDBContract.ListingsEntry.COLUMN_RANK,
                    ticker.rank);
            values.put(CryptoDBContract.ListingsEntry.COLUMN_CIRCULATING_SUPPLY,
                    ticker.circulatingSupply);
            values.put(CryptoDBContract.ListingsEntry.COLUMN_MAX_SUPPLY,
                    ticker.maxSupply);
            values.put(CryptoDBContract.ListingsEntry.COLUMN_TOTAL_SUPPLY,
                    ticker.totalSupply);
            if ((ticker.quotes != null) && (ticker.quotes.tickerUsd != null)) {
                values.put(CryptoDBContract.ListingsEntry.COLUMN_MARKET_CAP,
                        ticker.quotes.tickerUsd.marketCap);
                values.put(CryptoDBContract.ListingsEntry.COLUMN_PRICE,
                        ticker.quotes.tickerUsd.price);
                values.put(CryptoDBContract.ListingsEntry.COLUMN_VOLUME_24H,
                        ticker.quotes.tickerUsd.volume24h);
                values.put(CryptoDBContract.ListingsEntry.COLUMN_CHANGE_1H,
                        ticker.quotes.tickerUsd.percentChange1h);
                values.put(CryptoDBContract.ListingsEntry.COLUMN_CHANGE_24H,
                        ticker.quotes.tickerUsd.percentChange24h);
                values.put(CryptoDBContract.ListingsEntry.COLUMN_CHANGE_7d,
                        ticker.quotes.tickerUsd.percentChange7d);
            } else {
                values.putNull(CryptoDBContract.ListingsEntry.COLUMN_MARKET_CAP);
                values.putNull(CryptoDBContract.ListingsEntry.COLUMN_PRICE);
                values.putNull(CryptoDBContract.ListingsEntry.COLUMN_VOLUME_24H);
                values.putNull(CryptoDBContract.ListingsEntry.COLUMN_CHANGE_1H);
                values.putNull(CryptoDBContract.ListingsEntry.COLUMN_CHANGE_24H);
                values.putNull(CryptoDBContract.ListingsEntry.COLUMN_CHANGE_7d);
            }
            values.put(CryptoDBContract.ListingsEntry.COLUMN_LAST_UPDATED, ticker.lastUpdated);

            operations.add(ContentProviderOperation.newUpdate(ContentUris.withAppendedId(uri, ticker.id))
                    .withValues(values)
                    .withYieldAllowed(true)
                    .build());
        }
        //execute all operations as a batch
        try {
            cr.applyBatch(CryptoDBContract.CONTENT_AUTHORITY, operations);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //updates global data in the database using the data in a given GlobalData object
    public static void updateGlobalDataInDB(@NonNull Context context, @NonNull GlobalData globalData) {
        ContentResolver cr = context.getContentResolver();

        Uri uri = CryptoDBContract.GlobalDataEntry.CONTENT_URI;

        ContentValues values = new ContentValues();
        values.put(CryptoDBContract.GlobalDataEntry.COLUMN_GLOBAL_DATA_ID, 1);
        values.put(CryptoDBContract.GlobalDataEntry.COLUMN_GLOBAL_DATA_ACTIVE_CRYPTOCURRENCIES,
                globalData.activeCryptocurrencies);
        values.put(CryptoDBContract.GlobalDataEntry.COLUMN_GLOBAL_DATA_ACTIVE_MARKETS,
                globalData.activeMarkets);
        values.put(CryptoDBContract.GlobalDataEntry.COLUMN_GLOBAL_DATA_BTC_MARKET_CAP,
                globalData.bitcoinPercentageOfMarketCap);
        if ((globalData.quotes != null) && (globalData.quotes.globalUsd != null)) {
            values.put(CryptoDBContract.GlobalDataEntry.COLUMN_GLOBAL_DATA_TOTAL_MARKET_CAP,
                    globalData.quotes.globalUsd.totalMarketCap);
            values.put(CryptoDBContract.GlobalDataEntry.COLUMN_GLOBAL_DATA_TOTAL_VOLUME,
                    globalData.quotes.globalUsd.totalVolume24h);
        } else {
            values.putNull(CryptoDBContract.GlobalDataEntry.COLUMN_GLOBAL_DATA_TOTAL_MARKET_CAP);
            values.putNull(CryptoDBContract.GlobalDataEntry.COLUMN_GLOBAL_DATA_TOTAL_VOLUME);
        }
        values.put(CryptoDBContract.GlobalDataEntry.COLUMN_GLOBAL_DATA_LAST_UPDATED,
                globalData.lastUpdated);

        cr.insert(uri, values);
    }
}

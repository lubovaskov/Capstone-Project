package com.udacity.cryptomanager.database;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.udacity.cryptomanager.R;

import java.util.ArrayList;
import java.util.List;

public class CryptoDBProvider extends ContentProvider {

    public static final int CODE_LISTINGS = 10;
    public static final int CODE_LISTING = 11;
    public static final int CODE_GLOBAL_DATA = 12;


    private static final UriMatcher uriMatcher = buildUriMatcher();
    private CryptoDBHelper helper;

    private final ThreadLocal<Boolean> isInBatchMode = new ThreadLocal<>();
    private List<Uri> delayedNotifications;

    public static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = CryptoDBContract.CONTENT_AUTHORITY;

        // content://com.udacity.cryptomanager.free.cryptodbprovider/listings
        matcher.addURI(authority, CryptoDBContract.CRYPTODB_URL_SEGMENT_LISTINGS, CODE_LISTINGS);
        // content://com.udacity.cryptomanager.free.cryptodbprovider/listings/{listing_id}
        matcher.addURI(authority, CryptoDBContract.CRYPTODB_URL_SEGMENT_LISTINGS + "/#", CODE_LISTING);
        // content://com.udacity.cryptomanager.free.cryptodbprovider/global_data
        matcher.addURI(authority, CryptoDBContract.CRYPTODB_URL_SEGMENT_GLOBAL_DATA, CODE_GLOBAL_DATA);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        helper = new CryptoDBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase db;

        switch (uriMatcher.match(uri)) {
            case CODE_LISTING:
                db = helper.getWritableDatabase();
                long listingID = db.insertWithOnConflict(CryptoDBContract.ListingsEntry.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                return getUriForId(listingID, uri);
            case CODE_GLOBAL_DATA:
                db = helper.getWritableDatabase();
                long globalDataID = db.insertWithOnConflict(CryptoDBContract.GlobalDataEntry.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                return getUriForId(globalDataID, uri);
            default:
                throw new UnsupportedOperationException(getContext().getString(R.string.unknown_uri_error_text) + uri);
        }
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        final SQLiteDatabase db = helper.getWritableDatabase();

        switch (uriMatcher.match(uri)) {
            case CODE_LISTINGS:
                db.beginTransaction();
                int rowsInserted = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(CryptoDBContract.ListingsEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            rowsInserted++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                if (rowsInserted > 0) {
                    sendNotification(uri);
                }

                return rowsInserted;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Cursor cursor;

        switch (uriMatcher.match(uri)) {
            case CODE_LISTING:
                String listingId = uri.getLastPathSegment();
                String[] selectionArguments = new String[]{listingId};

                cursor = helper.getReadableDatabase().query(
                        CryptoDBContract.ListingsEntry.TABLE_NAME,
                        projection,
                        CryptoDBContract.ListingsEntry.COLUMN_LISTING_ID + " = ? ",
                        selectionArguments,
                        null,
                        null,
                        sortOrder);
                break;
            case CODE_LISTINGS:
                cursor = helper.getReadableDatabase().query(
                        CryptoDBContract.ListingsEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case CODE_GLOBAL_DATA:
                cursor = helper.getReadableDatabase().query(
                        CryptoDBContract.GlobalDataEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        null);
                break;
            default:
                throw new UnsupportedOperationException(getContext().getString(R.string.unknown_uri_error_text) + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        int rowsDeleted;

        switch (uriMatcher.match(uri)) {
            case CODE_LISTING:
                rowsDeleted = helper.getWritableDatabase().delete(
                        CryptoDBContract.ListingsEntry.TABLE_NAME,
                        CryptoDBContract.ListingsEntry.COLUMN_LISTING_ID + " = " +
                                uri.getLastPathSegment(),
                        selectionArgs);
                break;
            case CODE_LISTINGS:
                if (null == selection) selection = "1";
                rowsDeleted = helper.getWritableDatabase().delete(
                        CryptoDBContract.ListingsEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            case CODE_GLOBAL_DATA:
                if (null == selection) selection = "1";
                rowsDeleted = helper.getWritableDatabase().delete(
                        CryptoDBContract.GlobalDataEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException(getContext().getString(R.string.unknown_uri_error_text) + uri);
        }

        if (rowsDeleted != 0) {
            sendNotification(uri);
        }

        return rowsDeleted;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        throw new RuntimeException(getContext().getString(R.string.gettype_not_implemented_error_text));
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int rowsUpdated = 0;

        switch (uriMatcher.match(uri)) {
            case CODE_LISTING:
                rowsUpdated = helper.getWritableDatabase().update(
                        CryptoDBContract.ListingsEntry.TABLE_NAME,
                        values,
                        CryptoDBContract.ListingsEntry.COLUMN_LISTING_ID + " = " +
                                uri.getLastPathSegment(),
                        selectionArgs);
                break;
            case CODE_LISTINGS:
                if (null == selection) selection = "1";
                rowsUpdated = helper.getWritableDatabase().update(
                        CryptoDBContract.ListingsEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            case CODE_GLOBAL_DATA:
                if (null == selection) selection = "1";
                rowsUpdated = helper.getWritableDatabase().update(
                        CryptoDBContract.GlobalDataEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException(getContext().getString(R.string.unknown_uri_error_text) + uri);
        }

        if (rowsUpdated != 0) {
            sendNotification(uri);
        }

        return rowsUpdated;
    }

    @NonNull
    @Override
    public ContentProviderResult[] applyBatch(@NonNull ArrayList<ContentProviderOperation> operations)
            throws OperationApplicationException {
        final SQLiteDatabase db = helper.getWritableDatabase();
        db.beginTransaction();
        isInBatchMode.set(true);
        try {
            final int numOperations = operations.size();
            final ContentProviderResult[] results = new ContentProviderResult[numOperations];
            for (int i = 0; i < numOperations; i++) {
                results[i] = operations.get(i).apply(this, results, i);
            }
            db.setTransactionSuccessful();

            isInBatchMode.remove();

            synchronized (delayedNotifications) {
                for (Uri uri : delayedNotifications) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
            }
            return results;
        } finally {
            isInBatchMode.remove();
            db.endTransaction();
        }
    }

    protected void sendNotification(Uri uri) {
        if (batchMode()) {
            if (delayedNotifications == null) {
                delayedNotifications = new ArrayList<>();
            }
            synchronized (delayedNotifications) {
                if (!delayedNotifications.contains(uri)) {
                    delayedNotifications.add(uri);
                }
            }
        } else {
            getContext().getContentResolver().notifyChange(uri, null);
        }
    }

    private Uri getUriForId(long id, Uri uri) {
        if (id > 0) {
            Uri itemUri = ContentUris.withAppendedId(uri, id);
            sendNotification(itemUri);
            return itemUri;
        }
        throw new SQLException(getContext().getString(R.string.insert_error_text) + uri);
    }

    private boolean batchMode() {
        return isInBatchMode.get() != null && isInBatchMode.get();
    }

    @Override
    public void shutdown() {
        helper.close();
        super.shutdown();
    }
}

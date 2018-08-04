package com.udacity.cryptomanager.database;

import android.net.Uri;
import android.provider.BaseColumns;

import com.udacity.cryptomanager.BuildConfig;

public class CryptoDBContract {

    public static final String CONTENT_AUTHORITY = BuildConfig.APPLICATION_ID + ".cryptodbprovider";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String CRYPTODB_URL_SEGMENT_LISTINGS = "listings";
    public static final String CRYPTODB_URL_SEGMENT_GLOBAL_DATA = "global_data";

    public static final class ListingsEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(CRYPTODB_URL_SEGMENT_LISTINGS)
                .build();

        public static final String TABLE_NAME = "listings";

        public static final String COLUMN_LISTING_ID = "id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_SYMBOL = "symbol";
        public static final String COLUMN_RANK = "rank";
        public static final String COLUMN_CIRCULATING_SUPPLY = "circulating_supply";
        public static final String COLUMN_TOTAL_SUPPLY = "total_supply";
        public static final String COLUMN_MAX_SUPPLY = "max_supply";
        public static final String COLUMN_LAST_UPDATED = "last_updated";
        public static final String COLUMN_PRICE = "price";
        public static final String COLUMN_VOLUME_24H = "volume_24h";
        public static final String COLUMN_MARKET_CAP = "market_cap";
        public static final String COLUMN_CHANGE_1H = "change_1h";
        public static final String COLUMN_CHANGE_24H = "change_24h";
        public static final String COLUMN_CHANGE_7d = "change_7d";
    }

    public static final class GlobalDataEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(CRYPTODB_URL_SEGMENT_GLOBAL_DATA)
                .build();

        public static final String TABLE_NAME = "global_data";

        public static final String COLUMN_GLOBAL_DATA_ID = "id";
        public static final String COLUMN_GLOBAL_DATA_ACTIVE_CRYPTOCURRENCIES = "active_cryptocurrencies";
        public static final String COLUMN_GLOBAL_DATA_ACTIVE_MARKETS = "active_markets";
        public static final String COLUMN_GLOBAL_DATA_BTC_MARKET_CAP = "btc_market_cap";
        public static final String COLUMN_GLOBAL_DATA_TOTAL_MARKET_CAP = "total_market_cap";
        public static final String COLUMN_GLOBAL_DATA_TOTAL_VOLUME = "total_volume";
        public static final String COLUMN_GLOBAL_DATA_LAST_UPDATED = "last_updated";
    }
}

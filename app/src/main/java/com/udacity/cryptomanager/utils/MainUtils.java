package com.udacity.cryptomanager.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.udacity.cryptomanager.R;
import com.udacity.cryptomanager.database.CryptoDBResolver;
import com.udacity.cryptomanager.valueobjects.GlobalData;
import com.udacity.cryptomanager.valueobjects.Ticker;
import com.udacity.cryptomanager.webapi.CoinMCClient;
import com.udacity.cryptomanager.webapi.GlobalWrapper;
import com.udacity.cryptomanager.webapi.ListWrapper;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import retrofit2.Response;

public class MainUtils {

    private static final String LOG_TAG_TICKERS = "RefreshTickers";
    private static final String LOG_TAG_GLOBAL_DATA = "RefreshGlobalData";

    public static void refreshGlobalData(@NonNull Context context) {
        try {
            Response<GlobalWrapper<GlobalData>> response = CoinMCClient.getInstance().getGlobalData().execute();
            if (response.isSuccessful()) {
                GlobalWrapper globalWrapper = response.body();
                if (globalWrapper != null) {
                    if (globalWrapper.data != null) {
                        CryptoDBResolver.updateGlobalDataInDB(context, (GlobalData) globalWrapper.data);
                    } else {
                        Log.e(LOG_TAG_GLOBAL_DATA, context.getResources().getString(R.string.empty_response_text));
                    }
                } else {
                    Log.e(LOG_TAG_GLOBAL_DATA, context.getResources().getString(R.string.empty_response_text));
                }
            } else {
                Log.e(LOG_TAG_GLOBAL_DATA, context.getResources().getString(R.string.network_error_text));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void refreshTickers(@NonNull Context context) {
        try {
            ArrayList<Ticker> tickers = new ArrayList<>();
            ArrayList<Integer> ids = new ArrayList<>();
            //get the IDs of all listings in the database
            CryptoDBResolver.loadListingIDsFromDB(context, ids);
            //request the ticker data for every listing ID and save the tickers into a list
            for (int i = 0; i < ids.size(); i++) {
                Response<ListWrapper<Ticker>> response =
                        CoinMCClient.getInstance().getTicker(ids.get(i)).execute();
                if (response.isSuccessful()) {
                    ListWrapper<Ticker> body = response.body();
                    if ((body != null) && (body.data != null)) {
                        Ticker ticker = body.data.get(0);
                        if (ticker != null) {
                            tickers.add(ticker);
                        } else {
                            Log.e(LOG_TAG_TICKERS, context.getResources().getString(R.string.empty_response_text));
                        }
                    } else {
                        Log.e(LOG_TAG_TICKERS, context.getResources().getString(R.string.empty_response_text));
                    }
                } else {
                    Log.e(LOG_TAG_TICKERS, context.getResources().getString(R.string.network_error_text));
                }
            }

            if (tickers.size() > 0) {
                //update tickers in the database with the data from the tickers list
                CryptoDBResolver.updateTickersInDB(context, tickers);
                //update widget data using the top 5 tickers
                int maxIndex = Math.min(5, tickers.size());
                WidgetUtils.updateCryptoWidget(context, tickers.subList(0, maxIndex));
            } else {
                //clear widget data
                WidgetUtils.updateCryptoWidget(context, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String shortenNumber(double doubleNumber) {
        double absNumber = Math.abs(doubleNumber);

        if (absNumber >= 1000000000000000L) {
            return String.format(Locale.getDefault(), "%1$d", (long) (doubleNumber / 1000000000000L)) + "T";
        } else if (absNumber >= 100000000000000L) {
            return String.format(Locale.getDefault(), "%1$.1f", doubleNumber / 1000000000000L) + "T";
        } else if (absNumber >= 10000000000000L) {
            return String.format(Locale.getDefault(), "%1$.2f", doubleNumber / 1000000000000L) + "T";
        } else if (absNumber >= 1000000000000L) {
            return String.format(Locale.getDefault(), "%1$.3f", doubleNumber / 1000000000000L) + "T";
        } else if (absNumber >= 100000000000L) {
            return String.format(Locale.getDefault(), "%1$.1f", doubleNumber / 1000000000) + "B";
        } else if (absNumber >= 10000000000L) {
            return String.format(Locale.getDefault(), "%1$.2f", doubleNumber / 1000000000) + "B";
        } else if (absNumber >= 1000000000) {
            return String.format(Locale.getDefault(), "%1$.3f", doubleNumber / 1000000000) + "B";
        } else if (absNumber >= 100000000) {
            return String.format(Locale.getDefault(), "%1$.1f", doubleNumber / 1000000) + "M";
        } else if (absNumber >= 10000000) {
            return String.format(Locale.getDefault(), "%1$.2f", doubleNumber / 1000000) + "M";
        } else if (absNumber >= 1000000) {
            return String.format(Locale.getDefault(), "%1$.3f", doubleNumber / 1000000) + "M";
        } else if (absNumber >= 100000) {
            return String.format(Locale.getDefault(), "%1$.1f", doubleNumber / 1000) + "K";
        } else if (absNumber >= 10000) {
            return String.format(Locale.getDefault(), "%1$.2f", doubleNumber / 1000) + "K";
        } else if (absNumber >= 1000) {
            return String.format(Locale.getDefault(), "%1$.3f", doubleNumber / 1000) + "K";
        } else if (absNumber >= 100) {
            return String.format(Locale.getDefault(), "%1$.1f", doubleNumber);
        } else if (absNumber >= 10) {
            return String.format(Locale.getDefault(), "%1$.2f", doubleNumber);
        } else if (absNumber >= 1) {
            return String.format(Locale.getDefault(), "%1$.3f", doubleNumber);
        } else if (absNumber >= 0.1) {
            return String.format(Locale.getDefault(), "%1$.4f", doubleNumber);
        } else if (absNumber >= 0.01) {
            return String.format(Locale.getDefault(), "%1$.5f", doubleNumber);
        } else {
            return String.format(Locale.getDefault(), "%1$.6f", doubleNumber);
        }
    }

    public static String shortenPrice(double price) {
        if (price >= 10000) {
            return String.format(Locale.getDefault(), "%1$.1d", (long) price);
        } else if (price >= 100) {
            return String.format(Locale.getDefault(), "%1$.2f", price);
        } else if (price >= 1) {
            return String.format(Locale.getDefault(), "%1$.4f", price);
        } else {
            return String.format(Locale.getDefault(), "%1$.6f", price);
        }
    }

    public static String formatUnixTime(long unixTime) {
        return DateFormat.getDateTimeInstance().format(new Date(unixTime*1000));
    }
}

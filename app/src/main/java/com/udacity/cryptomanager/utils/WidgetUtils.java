package com.udacity.cryptomanager.utils;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.support.annotation.NonNull;

import com.udacity.cryptomanager.CryptoWidget;
import com.udacity.cryptomanager.database.CryptoDBResolver;
import com.udacity.cryptomanager.valueobjects.Ticker;

import java.util.ArrayList;
import java.util.List;

public class WidgetUtils {
    private static final int WIDGET_TICKERS_COUNT = 5;

    //load a list of the first 5 tickers by rank from the database to be displayed into a widget
    public static List<Ticker> getWidgetTickers(Context context) {
        return CryptoDBResolver.loadTickersFromDB(context, WIDGET_TICKERS_COUNT);
    }

    //update the content of all widgets
    public static void updateCryptoWidget(@NonNull Context context, List<Ticker> tickers) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName widgetCompName = new ComponentName(context, CryptoWidget.class);
        for (int widgetId : appWidgetManager.getAppWidgetIds(widgetCompName)) {
            CryptoWidget.updateAppWidget(context, appWidgetManager, widgetId, tickers);
        }
    }
}

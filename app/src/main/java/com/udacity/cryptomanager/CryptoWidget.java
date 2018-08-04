package com.udacity.cryptomanager;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.graphics.Color;
import android.widget.RemoteViews;

import com.udacity.cryptomanager.utils.MainUtils;
import com.udacity.cryptomanager.utils.WidgetUtils;
import com.udacity.cryptomanager.valueobjects.Ticker;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

public class CryptoWidget extends AppWidgetProvider {

    public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                       int appWidgetId, List<Ticker> tickers) {
        //construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_crypto);
        views.removeAllViews(R.id.widget_tickers_container);

        if ((tickers != null) && (tickers.size() > 0)) {
            for (Ticker ticker : tickers) {
                RemoteViews tickerView = new RemoteViews(context.getPackageName(),
                        R.layout.widget_crypto_item);

                tickerView.setTextViewText(R.id.textview_last_updated,
                        MainUtils.formatUnixTime(ticker.lastUpdated));
                tickerView.setTextViewText(R.id.textview_widget_symbol, ticker.symbol);
                tickerView.setTextViewText(R.id.textview_widget_price, String.format(
                        context.getResources().getString(R.string.price_format),
                        MainUtils.shortenPrice(ticker.quotes.tickerUsd.price)));
                tickerView.setTextViewText(R.id.textview_widget_change_24h, String.format(
                        context.getResources().getString(R.string.change_format),
                        ticker.quotes.tickerUsd.percentChange24h));
                if (ticker.quotes.tickerUsd.percentChange24h > 0) {
                    tickerView.setTextColor(R.id.textview_widget_change_24h,
                            context.getResources().getColor(R.color.material_color_green_primary_dark)
                    );
                } else if (ticker.quotes.tickerUsd.percentChange24h < 0) {
                    tickerView.setTextColor(R.id.textview_widget_change_24h,
                            context.getResources().getColor(R.color.material_color_red_primary_dark)
                    );
                } else {
                    tickerView.setTextColor(R.id.textview_widget_change_24h,
                            context.getResources().getColor(R.color.material_color_white));
                }
                views.addView(R.id.widget_tickers_container, tickerView);
            }
        } else {
            //if there are no tickers in the database - show "no data" text
            RemoteViews emptyView = new RemoteViews(context.getPackageName(),
                    R.layout.widget_empty_item);
            emptyView.setTextViewText(R.id.widget_empty, context.getString(R.string.no_widget_data));
            views.addView(R.id.widget_tickers_container, emptyView);
        }

        //instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        //there may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId,
                    WidgetUtils.getWidgetTickers(context));
        }
    }

    @Override
    public void onEnabled(Context context) {
    }

    @Override
    public void onDisabled(Context context) {
    }
}


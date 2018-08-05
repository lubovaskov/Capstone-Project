package com.udacity.cryptomanager;

import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.udacity.cryptomanager.utils.MainUtils;
import com.udacity.cryptomanager.valueobjects.Ticker;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class TickerDetailsActivity extends AppCompatActivity {

    private Unbinder unbinder;

    @Nullable
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.textview_ticker_id)
    TextView tvId;
    @BindView(R.id.textview_ticker_symbol)
    TextView tvSymbol;
    @BindView(R.id.textview_ticker_rank)
    TextView tvRank;
    @BindView(R.id.textview_ticker_price)
    TextView tvPrice;
    @BindView(R.id.textview_ticker_volume)
    TextView tvVolume;
    @BindView(R.id.textview_ticker_market_cap)
    TextView tvMarketCap;
    @BindView(R.id.textview_ticker_change_1h)
    TextView tvChange1h;
    @BindView(R.id.textview_ticker_change_24h)
    TextView tvChange24h;
    @BindView(R.id.textview_ticker_change_7d)
    TextView tvChange7d;
    @BindView(R.id.textview_ticker_circulating_supply)
    TextView tvCirculatingSupply;
    @BindView(R.id.textview_ticker_max_supply)
    TextView tvMaxSupply;
    @BindView(R.id.textview_ticker_total_supply)
    TextView tvTotalSupply;

    private Ticker ticker;

    //analytics tracker
    Tracker tracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticker_details);

        //initialize butterknife bindings
        unbinder = ButterKnife.bind(this);

        //get shared tracker for analytics
        CryptoManagerApplication application = (CryptoManagerApplication) getApplication();
        tracker = application.getDefaultTracker();

        setSupportActionBar(toolbar);

        final ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }

        ticker = getIntent().getParcelableExtra(Ticker.TICKER_PARCELABLE_NAME);
        updateViews();
        updateTitle();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //send screen name to analytics
        tracker.setScreenName(getLocalClassName());
        tracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //release butterknife bindings
        unbinder.unbind();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(Ticker.TICKER_PARCELABLE_NAME, ticker);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        ticker = savedInstanceState.getParcelable(Ticker.TICKER_PARCELABLE_NAME);
    }

    //display ticker name in the title
    private void updateTitle() {
        if (ticker != null) {
            setTitle(ticker.name);
        }
    }

    private void updateViews() {
        if (ticker != null) {
            tvId.setText(String.format(
                    getResources().getString(R.string.id_format),
                    ticker.id));
            tvSymbol.setText(String.format(
                    getResources().getString(R.string.symbol_format),
                    ticker.symbol));
            tvRank.setText(String.format(
                    getResources().getString(R.string.rank_format),
                    ticker.rank));
            tvPrice.setText(String.format(
                    getResources().getString(R.string.detail_price_format),
                    MainUtils.shortenPrice(ticker.quotes.tickerUsd.price)));
            tvVolume.setText(String.format(
                    getResources().getString(R.string.volume_format),
                    MainUtils.shortenNumber(ticker.quotes.tickerUsd.volume24h)));
            tvMarketCap.setText(String.format(
                    getResources().getString(R.string.market_cap_format),
                    MainUtils.shortenNumber(ticker.quotes.tickerUsd.marketCap)));
            tvChange1h.setText(String.format(
                    getResources().getString(R.string.change_1h_format),
                    ticker.quotes.tickerUsd.percentChange1h));
            tvChange24h.setText(String.format(
                    getResources().getString(R.string.change_24h_format),
                    ticker.quotes.tickerUsd.percentChange24h));
            tvChange7d.setText(String.format(
                    getResources().getString(R.string.change_7d_format),
                    ticker.quotes.tickerUsd.percentChange7d));
            tvCirculatingSupply.setText(String.format(
                    getResources().getString(R.string.circulating_supply_format),
                    MainUtils.shortenNumber(ticker.circulatingSupply)));
            tvMaxSupply.setText(String.format(
                    getResources().getString(R.string.max_supply_format),
                    MainUtils.shortenNumber(ticker.maxSupply)));
            tvTotalSupply.setText(String.format(
                    getResources().getString(R.string.total_supply_format),
                    MainUtils.shortenNumber(ticker.totalSupply)));

            tvChange1h.setTextColor(MainUtils.getTextColorByValue(
                    this,
                    ticker.quotes.tickerUsd.percentChange1h,
                    R.color.material_typography_primary_text_color_dark));
            tvChange7d.setTextColor(MainUtils.getTextColorByValue(
                    this,
                    ticker.quotes.tickerUsd.percentChange7d,
                    R.color.material_typography_primary_text_color_dark));
            tvChange24h.setTextColor(MainUtils.getTextColorByValue(
                    this,
                    ticker.quotes.tickerUsd.percentChange24h,
                    R.color.material_typography_primary_text_color_dark));
        }
    }
}


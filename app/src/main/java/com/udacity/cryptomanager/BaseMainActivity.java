package com.udacity.cryptomanager;

import android.app.ActivityOptions;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import com.udacity.cryptomanager.adapters.TickersAdapter;
import com.udacity.cryptomanager.database.CryptoDBResolver;
import com.udacity.cryptomanager.sync.TickerFirebaseJobService;
import com.udacity.cryptomanager.tasks.RefreshTickersTask;
import com.udacity.cryptomanager.utils.MainUtils;
import com.udacity.cryptomanager.utils.NetworkUtils;
import com.udacity.cryptomanager.utils.WidgetUtils;
import com.udacity.cryptomanager.valueobjects.GlobalData;
import com.udacity.cryptomanager.valueobjects.Listing;
import com.udacity.cryptomanager.valueobjects.Ticker;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseMainActivity extends AppCompatActivity implements
        //load tickers from ContentProvider
        LoaderManager.LoaderCallbacks<Cursor>,
        //add listings from dialog
        AddListingsDialogFragment.AddListingsDialogResultListener,
        //tickers recycler view adapter interface
        TickersAdapter.TickersAdapterOnClickHandler {

    private static final int TICKERS_LOADER_ID = 20;
    private static final int GLOBAL_DATA_LOADER_ID = 21;
    private static final String ADD_LISTINGS_DIALOG_TAG = "add_listings_dialog";

    private Unbinder unbinder;

    @Nullable
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.textview_active_cryptocurrencies)
    TextView tvActiveCryptoCurrencies;
    @BindView(R.id.textview_active_markets)
    TextView tvActiveMarkets;
    @BindView(R.id.textview_total_market_cap)
    TextView tvTotalMarketCap;
    @BindView(R.id.textview_total_volume)
    TextView tvTotalVolume;
    @BindView(R.id.textview_btc_market_cap)
    TextView tvBTCMarketCap;
    @BindView(R.id.textview_last_updated)
    TextView tvLastUpdated;
    @BindView(R.id.recyclerview_tickers)
    RecyclerView rvTickers;

    private TickersAdapter tickersAdapter;
    private List<Ticker> tickers;

    //analytics tracker
    Tracker tracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        unbinder = ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        final ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setTitle(R.string.app_name);
        }

        //get tracker for analytics
        CryptoManagerApplication application = (CryptoManagerApplication) getApplication();
        tracker = application.getDefaultTracker();

        initRecyclerViewTickers();

        //initialize global data and tickers loaders
        initLoaders();

        if (savedInstanceState == null) {
            //reload data from coinmarketcap.com into the database
            refreshTickers();
        }

        //create Firebase job dispatcher to regularly get ticker data from coinmarketcap.com and
        //save it in the database and display it the widget
        TickerFirebaseJobService.createTickerJob(getApplicationContext());
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
        unbinder.unbind();
    }

    private void initLoaders() {
        //initialize loader for global market data
        getSupportLoaderManager().initLoader(GLOBAL_DATA_LOADER_ID, null, this);
        //initialize loader for tickers data
        getSupportLoaderManager().initLoader(TICKERS_LOADER_ID, null, this);
    }

    private void initRecyclerViewTickers() {
        tickersAdapter = new TickersAdapter(this, this);
        rvTickers.setAdapter(tickersAdapter);
        Drawable divDrawable = getDrawable(R.drawable.horizontal_divider);
        if (divDrawable != null) {
            DividerItemDecoration divider = new DividerItemDecoration(this,
                    DividerItemDecoration.VERTICAL);
            divider.setDrawable(divDrawable);
            rvTickers.addItemDecoration(divider);
        }
    }

    private void displayGlobalData(GlobalData globalData) {
        if (globalData != null) {
            tvActiveCryptoCurrencies.setText(String.format(Locale.getDefault(),
                    getResources().getString(R.string.active_cryptocurrencies_text),
                    globalData.activeCryptocurrencies));
            tvActiveMarkets.setText(String.format(
                    getResources().getString(R.string.active_markets_text),
                    globalData.activeMarkets));
            tvTotalMarketCap.setText(String.format(
                    getResources().getString(R.string.total_market_cap_text),
                    MainUtils.shortenNumber(globalData.quotes.globalUsd.totalMarketCap)));
            tvTotalVolume.setText(String.format(
                    getResources().getString(R.string.total_volume_text),
                    MainUtils.shortenNumber(globalData.quotes.globalUsd.totalVolume24h)));
            tvBTCMarketCap.setText(String.format(
                    getResources().getString(R.string.btc_market_cap_text),
                    globalData.bitcoinPercentageOfMarketCap));
            tvLastUpdated.setText(String.format(
                    getResources().getString(R.string.last_updated_text),
                    MainUtils.formatUnixTime(globalData.lastUpdated)));
        } else {
            tvActiveCryptoCurrencies.setText(
                    getResources().getString(R.string.active_cryptocurrencies_empty_text));
            tvActiveMarkets.setText(
                    getResources().getString(R.string.active_cryptocurrencies_empty_text));
            tvTotalMarketCap.setText(
                    getResources().getString(R.string.active_cryptocurrencies_empty_text));
            tvTotalVolume.setText(
                    getResources().getString(R.string.active_cryptocurrencies_empty_text));
            tvBTCMarketCap.setText(
                    getResources().getString(R.string.active_cryptocurrencies_empty_text));
            tvLastUpdated.setText(
                    getResources().getString(R.string.last_updated_empty_text));
        }
    }

    private void displayTickers(List<Ticker> newTickers) {
        tickers = newTickers;
        if (tickers == null) {
            tickers = new ArrayList<>();
        }
        tickersAdapter.reloadData(tickers);
    }

    private void refreshTickers() {
        if (!NetworkUtils.isOnline(this)) {
            onError(getResources().getString(R.string.not_online_error_text));
        } else {
            //download all data for the tickers and save it in the database
            RefreshTickersTask.runTask(this);
        }
    }

    private void clearTickers() {
        //delete all tickers from the database
        CryptoDBResolver.clearTickers(this);
        //clear all data from the widget
        WidgetUtils.updateCryptoWidget(this, null);
    }

    public void onOK(List<Listing> addedListings) {
        //add selected listings to the database
        CryptoDBResolver.saveListingsToDB(this, addedListings);
        //refresh all tickers data
        refreshTickers();
    }

    public void onCancel() {
        //do nothing
    }

    public void onError(String errorText) {
        Toast.makeText(this, errorText, Toast.LENGTH_SHORT).show();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle bundle) {
        switch (loaderId) {
            case TICKERS_LOADER_ID:
                return CryptoDBResolver.getTickersLoader(this);
            case GLOBAL_DATA_LOADER_ID:
                return CryptoDBResolver.getGlobalDataLoader(this);
            default:
                throw new RuntimeException(getResources()
                        .getString(R.string.loader_not_implemented_error_text) + loaderId);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case TICKERS_LOADER_ID:
                displayTickers(CryptoDBResolver.loadTickersFromCursor(data));
                break;
            case GLOBAL_DATA_LOADER_ID:
                displayGlobalData(CryptoDBResolver.loadGlobalDataFromCursor(data));
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()) {
            case TICKERS_LOADER_ID:
                displayTickers(null);
                break;
            case GLOBAL_DATA_LOADER_ID:
                displayGlobalData(null);
                break;
        }
    }

    @Override
    public void onTickerClick(int position) {
        //show activity with detail info about the clicked ticker
        Intent intent = new Intent(this, TickerDetailsActivity.class);
        if (tickers != null) {
            intent.putExtra(Ticker.TICKER_PARCELABLE_NAME, tickers.get(position));
        }
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this);
        startActivity(intent, options.toBundle());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            //reload all listings data
            case R.id.action_refresh_tickers:
                refreshTickers();
                break;
            //add new listings
            case R.id.action_add_listings:
                AddListingsDialogFragment.newInstance().show(getSupportFragmentManager(),
                        ADD_LISTINGS_DIALOG_TAG);
                break;
            //delete all tickers from the database
            case R.id.action_clear_tickers:
                clearTickers();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}

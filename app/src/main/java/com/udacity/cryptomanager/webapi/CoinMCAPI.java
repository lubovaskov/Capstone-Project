package com.udacity.cryptomanager.webapi;

import com.udacity.cryptomanager.valueobjects.GlobalData;
import com.udacity.cryptomanager.valueobjects.Listing;
import com.udacity.cryptomanager.valueobjects.Ticker;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface CoinMCAPI {

    String BASE_URL = "https://api.coinmarketcap.com/v2/";

    String COINMC_TICKER_PARAM_LIMIT = "limit";

    @GET("listings/")
    Call<ListWrapper<Listing>> getListings();
    @GET("ticker/?structure=array")
    Call<ListWrapper<Ticker>> getTickerAll(@Query(COINMC_TICKER_PARAM_LIMIT) int limit);
    @GET("ticker/{id}?structure=array")
    Call<ListWrapper<Ticker>> getTicker(@Path("id") int id);
    @GET("global/")
    Call<GlobalWrapper<GlobalData>> getGlobalData();
}
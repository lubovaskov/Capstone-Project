package com.udacity.cryptomanager.webapi;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CoinMCClient {

    private static CoinMCAPI mInstance;

    private CoinMCClient() {}

    public static CoinMCAPI getInstance() {
        if (mInstance == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(CoinMCAPI.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            mInstance = retrofit.create(CoinMCAPI.class);
        }

        return mInstance;
    }
}

package com.udacity.cryptomanager.valueobjects;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class GlobalData implements Parcelable {

    public static final String GLOBAL_DATA_PARCELABLE_NAME = "GlobalData";

    @SerializedName("active_cryptocurrencies")
    public int activeCryptocurrencies;
    @SerializedName("active_markets")
    public int activeMarkets;
    @SerializedName("bitcoin_percentage_of_market_cap")
    public double bitcoinPercentageOfMarketCap;
    @SerializedName("quotes")
    public GlobalQuote quotes;
    @SerializedName("last_updated")
    public long lastUpdated;

    public GlobalData(int activeCryptocurrencies, int activeMarkets,
                      double bitcoinPercentageOfMarketCap, GlobalQuote quotes, long lastUpdated) {
        this.activeCryptocurrencies = activeCryptocurrencies;
        this.activeMarkets = activeMarkets;
        this.bitcoinPercentageOfMarketCap = bitcoinPercentageOfMarketCap;
        this.quotes = quotes;
        this.lastUpdated = lastUpdated;
    }

    private GlobalData(Parcel parcel) {
        activeCryptocurrencies = parcel.readInt();
        activeMarkets = parcel.readInt();
        bitcoinPercentageOfMarketCap = parcel.readDouble();
        quotes = parcel.readParcelable(GlobalQuote.class.getClassLoader());
        lastUpdated = parcel.readLong();
    }

    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(activeCryptocurrencies);
        dest.writeInt(activeMarkets);
        dest.writeDouble(bitcoinPercentageOfMarketCap);
        dest.writeParcelable(quotes, 0);
        dest.writeLong(lastUpdated);
    }

    public final static Parcelable.Creator<GlobalData> CREATOR = new Parcelable.Creator<GlobalData>() {

        @Override
        public GlobalData createFromParcel(Parcel parcel) {
            return new GlobalData(parcel);
        }

        @Override
        public GlobalData[] newArray(int size) {
            return (new GlobalData[size]);
        }
    };
}

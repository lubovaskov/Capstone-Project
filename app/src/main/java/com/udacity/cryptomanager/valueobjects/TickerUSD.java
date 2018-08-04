package com.udacity.cryptomanager.valueobjects;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class TickerUSD implements Parcelable {

    public static final String TICKER_USD_PARCELABLE_NAME = "TickerUSD";

    @SerializedName("price")
    public double price;
    @SerializedName("volume_24h")
    public double volume24h;
    @SerializedName("market_cap")
    public double marketCap;
    @SerializedName("percent_change_1h")
    public double percentChange1h;
    @SerializedName("percent_change_24h")
    public double percentChange24h;
    @SerializedName("percent_change_7d")
    public double percentChange7d;

    public TickerUSD(double price, double volume24h, double marketCap, double percentChange1h,
                     double percentChange24h, double percentChange7d) {
        this.price = price;
        this.volume24h = volume24h;
        this.marketCap = marketCap;
        this.percentChange1h = percentChange1h;
        this.percentChange24h = percentChange24h;
        this.percentChange7d = percentChange7d;
    }

    protected TickerUSD(Parcel parcel) {
        price = parcel.readDouble();
        volume24h = parcel.readDouble();
        marketCap = parcel.readDouble();
        percentChange1h = parcel.readDouble();
        percentChange24h = parcel.readDouble();
        percentChange7d = parcel.readDouble();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeDouble(price);
        parcel.writeDouble(volume24h);
        parcel.writeDouble(marketCap);
        parcel.writeDouble(percentChange1h);
        parcel.writeDouble(percentChange24h);
        parcel.writeDouble(percentChange7d);
    }

    public final static Parcelable.Creator<TickerUSD> CREATOR = new Parcelable.Creator<TickerUSD>() {

        @Override
        public TickerUSD createFromParcel(Parcel parcel) {
            return new TickerUSD(parcel);
        }

        @Override
        public TickerUSD[] newArray(int size) {
            return (new TickerUSD[size]);
        }
    };
}

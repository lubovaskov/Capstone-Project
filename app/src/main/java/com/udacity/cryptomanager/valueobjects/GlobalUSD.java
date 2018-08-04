package com.udacity.cryptomanager.valueobjects;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class GlobalUSD implements Parcelable {

    public static final String GLOBAL_USD_PARCELABLE_NAME = "GlobalUSD";

    @SerializedName("total_market_cap")
    public double totalMarketCap;
    @SerializedName("total_volume_24h")
    public double totalVolume24h;

    public GlobalUSD(double totalMarketCap, double totalVolume24h) {
        this.totalMarketCap = totalMarketCap;
        this.totalVolume24h = totalVolume24h;
    }

    protected GlobalUSD(Parcel parcel) {
        totalMarketCap = parcel.readDouble();
        totalVolume24h = parcel.readDouble();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeDouble(totalMarketCap);
        parcel.writeDouble(totalVolume24h);
    }

    public final static Parcelable.Creator<GlobalUSD> CREATOR = new Parcelable.Creator<GlobalUSD>() {

        @Override
        public GlobalUSD createFromParcel(Parcel parcel) {
            return new GlobalUSD(parcel);
        }

        @Override
        public GlobalUSD[] newArray(int size) {
            return (new GlobalUSD[size]);
        }
    };
}

package com.udacity.cryptomanager.valueobjects;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class TickerQuote implements Parcelable {

    public static final String TICKER_QUOTE_PARCELABLE_NAME = "TickerQuote";

    @SerializedName("USD")
    public TickerUSD tickerUsd;

    public TickerQuote(TickerUSD tickerUsd) {
        this.tickerUsd = tickerUsd;
    }

    private TickerQuote(Parcel parcel) {
        tickerUsd = parcel.readParcelable(TickerUSD.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeParcelable(tickerUsd, 0);
    }

    public static final Parcelable.Creator<TickerQuote> CREATOR = new Parcelable.Creator<TickerQuote>() {

        @Override
        public TickerQuote createFromParcel(Parcel parcel) {
            return new TickerQuote(parcel);
        }

        @Override
        public TickerQuote[] newArray(int size) {
            return (new TickerQuote[size]);
        }

    };
}

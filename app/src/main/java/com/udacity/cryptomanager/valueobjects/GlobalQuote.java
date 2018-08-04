package com.udacity.cryptomanager.valueobjects;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class GlobalQuote implements Parcelable {

    public static final String GLOBAL_QUOTE_PARCELABLE_NAME = "GlobalQuote";

    @SerializedName("USD")
    public GlobalUSD globalUsd;

    public GlobalQuote(GlobalUSD globalUsd) {
        this.globalUsd = globalUsd;
    }

    private GlobalQuote(Parcel parcel) {
        globalUsd = parcel.readParcelable(GlobalUSD.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeParcelable(globalUsd, 0);
    }

    public static final Parcelable.Creator<GlobalQuote> CREATOR = new Parcelable.Creator<GlobalQuote>() {

        @Override
        public GlobalQuote createFromParcel(Parcel parcel) {
            return new GlobalQuote(parcel);
        }

        @Override
        public GlobalQuote[] newArray(int size) {
            return (new GlobalQuote[size]);
        }

    };
}

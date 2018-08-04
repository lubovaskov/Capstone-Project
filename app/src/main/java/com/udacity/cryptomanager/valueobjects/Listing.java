package com.udacity.cryptomanager.valueobjects;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Listing implements Parcelable {

    public static final String LISTING_PARCELABLE_NAME = "listing";

    @SerializedName("id")
    public int id;
    @SerializedName("name")
    public String name;
    @SerializedName("symbol")
    public String symbol;
    @SerializedName("website_slug")
    public String websiteSlug;

    public Listing(int id, String name, String symbol, String websiteSlug) {
        this.id = id;
        this.name = name;
        this.symbol = symbol;
        this.websiteSlug = websiteSlug;
    }

    private Listing(Parcel parcel) {
        id = parcel.readInt();
        name = parcel.readString();
        symbol = parcel.readString();
        websiteSlug = parcel.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(name);
        parcel.writeString(symbol);
        parcel.writeString(websiteSlug);
    }

    public static final Parcelable.Creator<Listing> CREATOR = new Parcelable.Creator<Listing>() {

        @Override
        public Listing createFromParcel(Parcel parcel) {
            return new Listing(parcel);
        }

        @Override
        public Listing[] newArray(int i) {
            return new Listing[i];
        }
    };
}

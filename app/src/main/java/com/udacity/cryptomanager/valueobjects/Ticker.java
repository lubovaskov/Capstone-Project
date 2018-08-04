package com.udacity.cryptomanager.valueobjects;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Ticker implements Parcelable {

    public static final String TICKER_PARCELABLE_NAME = "Ticker";

    @SerializedName("id")
    public int id;
    @SerializedName("name")
    public String name;
    @SerializedName("symbol")
    public String symbol;
    @SerializedName("website_slug")
    public String websiteSlug;
    @SerializedName("rank")
    public int rank;
    @SerializedName("circulating_supply")
    public double circulatingSupply;
    @SerializedName("total_supply")
    public double totalSupply;
    @SerializedName("max_supply")
    public double maxSupply;
    @SerializedName("quotes")
    public TickerQuote quotes;
    @SerializedName("last_updated")
    public long lastUpdated;

    public Ticker(int id, String name, String symbol, String websiteSlug, int rank,
                  double circulatingSupply, double totalSupply, double maxSupply, TickerQuote quotes,
                  long lastUpdated) {
        this.id = id;
        this.name = name;
        this.symbol = symbol;
        this.websiteSlug = websiteSlug;
        this.rank = rank;
        this.circulatingSupply = circulatingSupply;
        this.totalSupply = totalSupply;
        this.maxSupply = maxSupply;
        this.quotes = quotes;
        this.lastUpdated = lastUpdated;
    }

    private Ticker(Parcel parcel) {
        id = parcel.readInt();
        name = parcel.readString();
        symbol = parcel.readString();
        websiteSlug = parcel.readString();
        rank = parcel.readInt();
        circulatingSupply = parcel.readDouble();
        totalSupply = parcel.readDouble();
        maxSupply = parcel.readDouble();
        quotes = parcel.readParcelable(TickerQuote.class.getClassLoader());
        lastUpdated = parcel.readLong();
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
        parcel.writeInt(rank);
        parcel.writeDouble(circulatingSupply);
        parcel.writeDouble(totalSupply);
        parcel.writeDouble(maxSupply);
        parcel.writeParcelable(quotes, 0);
        parcel.writeLong(lastUpdated);
    }

    public static final Parcelable.Creator<Ticker> CREATOR = new Parcelable.Creator<Ticker>() {

        @Override
        public Ticker createFromParcel(Parcel parcel) {
            return new Ticker(parcel);
        }

        @Override
        public Ticker[] newArray(int i) {
            return new Ticker[i];
        }
    };
}

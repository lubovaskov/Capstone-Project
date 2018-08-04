package com.udacity.cryptomanager.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.udacity.cryptomanager.R;
import com.udacity.cryptomanager.valueobjects.Listing;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddListingsAdapter
        extends RecyclerView.Adapter<AddListingsAdapter.AddListingsAdapterViewHolder>
        implements Filterable {

    private final Context mContext;
    private List<Listing> listings;
    private List<Listing> listingsFiltered;
    private List<Listing> listingsSelected;
    private int mNextFocusUpId = 0;
    private int mNextFocusRightId = 0;
    private int mNextFocusLeftId = 0;

    public AddListingsAdapter(@NonNull Context context, List<Listing> selectedListings) {
        mContext = context;
        listingsSelected = selectedListings;
    }

    @Override
    public AddListingsAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_add_listings, viewGroup,
                false);

        return new AddListingsAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AddListingsAdapterViewHolder listingsAdapterViewHolder, int position) {
        if (position < listingsFiltered.size()) {
            Listing listing = listingsFiltered.get(position);
            listingsAdapterViewHolder.updateListing(listing);
        } else {
            listingsAdapterViewHolder.updateListing(null);
        }

        if ((mNextFocusUpId != 0) && (position == 0)) {
            listingsAdapterViewHolder.itemView.setNextFocusUpId(mNextFocusUpId);
        } else {
            listingsAdapterViewHolder.itemView.setNextFocusUpId(View.NO_ID);
        }
        if (mNextFocusLeftId != 0) {
            listingsAdapterViewHolder.itemView.setNextFocusLeftId(mNextFocusLeftId);
        }
        if (mNextFocusRightId != 0) {
            listingsAdapterViewHolder.itemView.setNextFocusRightId(mNextFocusRightId);
        }
    }

    @Override
    public int getItemCount() {
        if (listingsFiltered == null) return 0;
        return listingsFiltered.size();
    }

    /*@Override
    public int getItemViewType(int position) {
        return VIEW_TYPE_REVIEWS;
    }*/

    public void setFocusIds(int nextFocusUpId, int nextFocusLeftId, int nextFocusRightId) {
        mNextFocusUpId = nextFocusUpId;
        mNextFocusLeftId = nextFocusLeftId;
        mNextFocusRightId = nextFocusRightId;
    }

    public void reloadData(List<Listing> newListings, List<Listing> newSelectedListings) {
        listings = newListings;
        listingsFiltered = newListings;
        listingsSelected = newSelectedListings;
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {

        return new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    listingsFiltered = listings;
                } else {
                    List<Listing> filteredList = new ArrayList<>();
                    for (Listing row : listings) {
                        if (row.name.toLowerCase().contains(charString.toLowerCase()) ||
                                row.symbol.toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }
                    listingsFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = listingsFiltered;
                return filterResults;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                listingsFiltered = (ArrayList<Listing>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    class AddListingsAdapterViewHolder extends RecyclerView.ViewHolder
            implements
            View.OnClickListener,
            View.OnFocusChangeListener {

        @BindView(R.id.cardview_listing)
        CardView cardView;
        @BindView(R.id.textview_listing_name)
        TextView tvListingName;

        AddListingsAdapterViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
            view.setOnFocusChangeListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            selectListing(listingsFiltered.get(adapterPosition));
        }

        @Override
        public void onFocusChange(View view, boolean focused) {
            if (focused) {
                tvListingName.setTextSize(
                        mContext.getResources().getDimension(
                                R.dimen.material_list_normal_primary_text_size) + 4);
            } else {
                tvListingName.setTextSize(
                        mContext.getResources().getDimension(
                                R.dimen.material_list_normal_primary_text_size));
            }
        }

        void selectListing(Listing listing) {
            if (listingsSelected.contains(listing)) {
                listingsSelected.remove(listing);
                cardView.setCardBackgroundColor(
                        mContext.getResources().getColor(R.color.colorPrimaryLight));
            } else {
                listingsSelected.add(listing);
                cardView.setCardBackgroundColor(
                        mContext.getResources().getColor(R.color.selectedCard));
            }
        }

        void updateListing(final Listing listing) {
            if (listing != null) {
                tvListingName.setText(String.format(
                        mContext.getResources().getString(R.string.listing_text),
                        listing.name, listing.symbol));
                if (listingsSelected.contains(listing)) {
                    cardView.setCardBackgroundColor(
                            mContext.getResources().getColor(R.color.selectedCard));
                } else {
                    cardView.setCardBackgroundColor(
                            mContext.getResources().getColor(R.color.colorPrimaryLight));
                }
            } else {
                tvListingName.setText("");
                cardView.setCardBackgroundColor(
                        mContext.getResources().getColor(R.color.colorPrimaryLight));
            }

        }
    }
}

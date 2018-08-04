package com.udacity.cryptomanager.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.udacity.cryptomanager.R;
import com.udacity.cryptomanager.database.CryptoDBResolver;
import com.udacity.cryptomanager.utils.MainUtils;
import com.udacity.cryptomanager.valueobjects.Ticker;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TickersAdapter extends RecyclerView.Adapter<TickersAdapter.TickersAdapterViewHolder>
        implements Filterable {

    private final Context mContext;
    private boolean multiSelect = false;
    private List<Ticker> tickersSelected = new ArrayList<>();
    final private TickersAdapterOnClickHandler mClickHandler;
    private List<Ticker> tickers;
    private List<Ticker> tickersFiltered;

    public interface TickersAdapterOnClickHandler {
        void onTickerClick(int position);
    }

    public TickersAdapter(@NonNull Context context, TickersAdapterOnClickHandler clickHandler) {
        mContext = context;
        mClickHandler = clickHandler;
    }

    @Override
    public TickersAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_tickers, viewGroup,
                false);

        return new TickersAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TickersAdapterViewHolder tickersAdapterViewHolder, int position) {
        if (position < tickersFiltered.size()) {
            Ticker ticker = tickersFiltered.get(position);
            tickersAdapterViewHolder.updateTicker(ticker);
        } else {
            tickersAdapterViewHolder.updateTicker(null);
        }
    }

    @Override
    public int getItemCount() {
        if (tickersFiltered == null) return 0;
        return tickersFiltered.size();
    }

    /*@Override
    public int getItemViewType(int position) {
        return VIEW_TYPE_TICKERS;
    }*/

    public void reloadData(List<Ticker> newData) {
        tickers = newData;
        tickersFiltered = newData;
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {

        return new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    tickersFiltered = tickers;
                } else {
                    List<Ticker> filteredList = new ArrayList<>();
                    for (Ticker row : tickers) {
                        if (row.name.toLowerCase().contains(charString.toLowerCase()) ||
                                row.symbol.toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }
                    tickersFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = tickersFiltered;
                return filterResults;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                tickersFiltered = (ArrayList<Ticker>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    private ActionMode.Callback actionModeCallbacks = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            multiSelect = true;
            menu.add(mContext.getResources().getString(R.string.action_delete_tickers));
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            for (Ticker ticker : tickersSelected) {
                CryptoDBResolver.deleteTicker(mContext, ticker.id);
                tickersFiltered.remove(ticker);
            }
            mode.finish();
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            multiSelect = false;
            tickersSelected.clear();
            notifyDataSetChanged();
        }
    };

    class TickersAdapterViewHolder extends RecyclerView.ViewHolder
            implements
            View.OnClickListener,
            View.OnLongClickListener,
            View.OnFocusChangeListener {

        @BindView(R.id.cardview_ticker)
        CardView cardView;
        @BindView(R.id.textview_ticker_symbol)
        TextView tvSymbol;
        @BindView(R.id.textview_ticker_rank)
        TextView tvRank;
        @BindView(R.id.textview_ticker_market_cap)
        TextView tvMarketCap;
        @BindView(R.id.textview_ticker_volume)
        TextView tvVolume;
        @BindView(R.id.textview_ticker_price)
        TextView tvPrice;
        @BindView(R.id.textview_ticker_change_24h)
        TextView tvChange24h;

        TickersAdapterViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
            view.setOnFocusChangeListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            if (multiSelect) {
                selectTicker(tickersFiltered.get(adapterPosition));
            } else {
                mClickHandler.onTickerClick(adapterPosition);
            }
        }

        @Override
        public boolean onLongClick(View view) {
            ((AppCompatActivity) view.getContext()).startSupportActionMode(actionModeCallbacks);
            int adapterPosition = getAdapterPosition();
            selectTicker(tickersFiltered.get(adapterPosition));
            return true;
        }

        @Override
        public void onFocusChange(View view, boolean focused) {
            TextView v = view.findViewById(R.id.textview_ticker_symbol);
            if (focused) {
                v.setTextSize(
                        mContext.getResources().getDimension(
                                R.dimen.material_list_normal_primary_text_size) + 4);
            } else {
                v.setTextSize(
                        mContext.getResources().getDimension(
                                R.dimen.material_list_normal_primary_text_size));
            }
        }

        void selectTicker(Ticker ticker) {
            if (multiSelect) {
                if (tickersSelected.contains(ticker)) {
                    tickersSelected.remove(ticker);
                    cardView.setCardBackgroundColor(
                            mContext.getResources().getColor(R.color.colorPrimaryLight));
                } else {
                    tickersSelected.add(ticker);
                    cardView.setCardBackgroundColor(
                            mContext.getResources().getColor(R.color.selectedCard));
                }
            }
        }

        void updateTicker(final Ticker ticker) {
            if (ticker != null) {
                tvSymbol.setText(ticker.symbol);
                tvRank.setText(String.format(
                        mContext.getResources().getString(R.string.rank_format),
                        ticker.rank));
                tvMarketCap.setText(String.format(
                        mContext.getResources().getString(R.string.market_cap_format),
                        MainUtils.shortenNumber(ticker.quotes.tickerUsd.marketCap)));
                tvVolume.setText(String.format(
                        mContext.getResources().getString(R.string.volume_format),
                        MainUtils.shortenNumber(ticker.quotes.tickerUsd.volume24h)));
                tvPrice.setText(String.format(
                        mContext.getResources().getString(R.string.price_format),
                        MainUtils.shortenPrice(ticker.quotes.tickerUsd.price)));
                tvChange24h.setText(String.format(
                        mContext.getResources().getString(R.string.change_format),
                        ticker.quotes.tickerUsd.percentChange24h));
                if (ticker.quotes.tickerUsd.percentChange24h > 0) {
                    tvChange24h.setTextColor(
                            mContext.getResources().getColor(R.color.material_color_green_primary_dark)
                    );
                } else if (ticker.quotes.tickerUsd.percentChange24h < 0) {
                    tvChange24h.setTextColor(
                            mContext.getResources().getColor(R.color.material_color_red_primary_dark)
                    );
                } else {
                    tvChange24h.setTextColor(
                            mContext.getResources().getColor(R.color.material_typography_primary_text_color_dark));
                }
                //change card background color of selected tickers
                if (tickersSelected.contains(ticker)) {
                    cardView.setCardBackgroundColor(
                            mContext.getResources().getColor(R.color.selectedCard));
                } else {
                    cardView.setCardBackgroundColor(
                            mContext.getResources().getColor(R.color.colorPrimaryLight));
                }
            } else {
                tvSymbol.setText("");
                tvRank.setText("");
                tvMarketCap.setText("");
                tvVolume.setText("");
                tvPrice.setText("");
                tvChange24h.setText("");
                cardView.setCardBackgroundColor(
                        mContext.getResources().getColor(R.color.colorPrimaryLight));
            }
        }
    }
}

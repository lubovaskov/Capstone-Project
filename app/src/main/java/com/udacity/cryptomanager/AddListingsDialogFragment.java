package com.udacity.cryptomanager;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.View;

import android.widget.Button;
import android.widget.ProgressBar;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.udacity.cryptomanager.adapters.AddListingsAdapter;
import com.udacity.cryptomanager.utils.NetworkUtils;
import com.udacity.cryptomanager.valueobjects.Listing;
import com.udacity.cryptomanager.webapi.CoinMCClient;
import com.udacity.cryptomanager.webapi.ListWrapper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddListingsDialogFragment extends DialogFragment implements
        //Retrofit result interface
        Callback<ListWrapper<Listing>>,
        DialogInterface.OnShowListener {

    private static final String ADD_LISTINGS_PARCELABLE_NAME = "add_listings";
    private static final String SELECTED_LISTINGS_PARCELABLE_NAME = "selected_listings";

    Unbinder unbinder;

    @BindView(R.id.recyclerview_add_listings)
    RecyclerView rvAddListings;
    @BindView(R.id.searchview_add_listings)
    SearchView svAddListings;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    private AddListingsAdapter addListingsAdapter;
    private List<Listing> addListings = new ArrayList<>();
    private List<Listing> selectedListings = new ArrayList<>();

    //analytics tracker
    Tracker tracker;

    //interface for a receiver of the dialog result
    public interface AddListingsDialogResultListener {
        void onOK(List<Listing> addedListings);
        void onCancel();
        void onError(String errorText);
    }

    public static AddListingsDialogFragment newInstance() {
        final AddListingsDialogFragment fragment = new AddListingsDialogFragment();
        //final Bundle args = new Bundle();
        //args.putInt(ARG_ITEM_COUNT, itemCount);
        //fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = View.inflate(getContext(), R.layout.fragment_add_listings, null);

        unbinder = ButterKnife.bind(this, view);

        //get tracker for analytics
        CryptoManagerApplication application = (CryptoManagerApplication) getActivity().getApplication();
        tracker = application.getDefaultTracker();

        Dialog dialog = new AlertDialog.Builder(getActivity(), android.R.style.Theme_Material_Light_Dialog_NoActionBar_MinWidth)
                .setIcon(R.drawable.bitcoin)
                .setTitle(R.string.title_dialog_add_listings)
                .setView(view)
                .setPositiveButton(R.string.btn_ok_add_listings,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                ((AddListingsDialogResultListener) getActivity()).onOK(selectedListings);
                            }
                        }
                )
                .setNegativeButton(R.string.btn_cancel_add_listings,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                ((AddListingsDialogResultListener) getActivity()).onCancel();
                            }
                        }
                )
                .create();

        dialog.setOnShowListener(this);

        initViews();

        if (savedInstanceState == null) {
            //load listings data if there is no saved state
            getListings();
        } else {
            //restore listings data if there is a saved state
            restoreInstanceState(savedInstanceState);
        }

        return dialog;
    }

    private void initViews() {
        progressBar.setVisibility(View.GONE);

        addListingsAdapter = new AddListingsAdapter(getContext(), selectedListings);
        rvAddListings.setAdapter(addListingsAdapter);

        svAddListings.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus && (!svAddListings.isInTouchMode())) {
                    svAddListings.setIconifiedByDefault(false);
                }
            }
        });
        svAddListings.setQueryHint(getString(R.string.search_query_hint));
        svAddListings.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String txt) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String txt) {
                addListingsAdapter.getFilter().filter(txt);
                return false;
            }
        });
    }

    /*@Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_listings, container, false);
        return rootView;
    }*/

    @Override
    public void onShow(DialogInterface dialog) {
        final Button positiveButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
        final Button negativeButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);

        addListingsAdapter.setFocusIds(svAddListings.getId(), negativeButton.getId(), positiveButton.getId());
        svAddListings.requestFocus();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        unbinder.unbind();
        super.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();
        //send screen name to analytics
        tracker.setScreenName(getClass().getSimpleName());
        tracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(ADD_LISTINGS_PARCELABLE_NAME, new ArrayList<>(addListings));
        outState.putParcelableArrayList(SELECTED_LISTINGS_PARCELABLE_NAME, new ArrayList<>(selectedListings));
    }

    private void restoreInstanceState(Bundle savedInstanceState) {
        addListings = savedInstanceState.getParcelableArrayList(ADD_LISTINGS_PARCELABLE_NAME);
        selectedListings = savedInstanceState.getParcelableArrayList(SELECTED_LISTINGS_PARCELABLE_NAME);
        if (selectedListings == null) {
            selectedListings = new ArrayList<>();
        }
        if (addListings != null) {
            addListingsAdapter.reloadData(addListings, selectedListings);
        }
    }

    private void getListings() {
        //send request to receive listings data if there is network connection
        if (checkOnline()) {
            progressBar.setVisibility(View.VISIBLE);
            //enqueue all listings data loading
            CoinMCClient.getInstance().getListings().enqueue(this);
        }
    }

    @NonNull
    private Boolean checkOnline() {
        //check if network connection is available
        if (!NetworkUtils.isOnline(getActivity())) {
            doError(R.string.not_online_error_text);
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onResponse(@NonNull Call<ListWrapper<Listing>> call,
                           @NonNull Response<ListWrapper<Listing>> response) {
        progressBar.setVisibility(View.GONE);
        //load listings data from the JSON response
        if (response.isSuccessful()) {
            ListWrapper<Listing> body = response.body();
            if (body != null) {
                addListings = body.data;
                if (addListings != null) {
                    addListingsAdapter.reloadData(addListings, selectedListings);
                } else {
                    doError(R.string.empty_response_text);
                }
            } else {
                doError(R.string.empty_response_text);
            }
        } else {
            doError(R.string.network_error_text);
        }
    }

    @Override
    public void onFailure(@NonNull Call<ListWrapper<Listing>> call, @NonNull Throwable t) {
        progressBar.setVisibility(View.GONE);
        doError(R.string.network_error_text);
    }

    private void doError(@StringRes int id) {
        ((AddListingsDialogResultListener) getActivity()).onError(getResources().getString(id));
    }
}

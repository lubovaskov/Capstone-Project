package com.udacity.cryptomanager.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.udacity.cryptomanager.utils.MainUtils;
import com.udacity.cryptomanager.utils.NetworkUtils;

import java.lang.ref.WeakReference;

public class RefreshTickersTask extends AsyncTask<Void, Void, Void> {

    private WeakReference<Context> contextRef;

    public RefreshTickersTask(@NonNull Context context) {
        contextRef = new WeakReference<>(context);
    }

    @Override
    protected Void doInBackground(Void... params) {
        Context context = contextRef.get();
        if (NetworkUtils.isOnline(context)) {
            MainUtils.refreshGlobalData(context);
            MainUtils.refreshTickers(context);
        }
        return null;
    }

    public static void runTask(@NonNull Context context) {
        final RefreshTickersTask task = new RefreshTickersTask(context);
        task.execute();
    }
}

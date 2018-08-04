package com.udacity.cryptomanager.sync;

import android.content.Context;
import android.support.annotation.NonNull;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.SimpleJobService;
import com.firebase.jobdispatcher.Trigger;
import com.udacity.cryptomanager.utils.MainUtils;
import com.udacity.cryptomanager.utils.NetworkUtils;

public class TickerFirebaseJobService extends SimpleJobService {

    private static final String TICKER_JOB_TAG = "tickers_job";
    private static final int TICKER_JOB_WINDOW_START = 60;
    private static final int TICKER_JOB_WINDOW_END = 70;

    public static FirebaseJobDispatcher createTickerJob(@NonNull Context context) {
        FirebaseJobDispatcher tickerDispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));

        //Bundle myExtrasBundle = new Bundle();
        //myExtrasBundle.putString("some_key", "some_value");
        Job tickerJob = tickerDispatcher.newJobBuilder()
                .setService(TickerFirebaseJobService.class)
                .setTag(TICKER_JOB_TAG)
                .setRecurring(true)
                .setLifetime(Lifetime.FOREVER)
                .setTrigger(Trigger.executionWindow(TICKER_JOB_WINDOW_START, TICKER_JOB_WINDOW_END))
                .setReplaceCurrent(true)
                .setRetryStrategy(RetryStrategy.DEFAULT_LINEAR)
                .setConstraints(Constraint.ON_UNMETERED_NETWORK)
                //.setExtras(myExtrasBundle)
                .build();
        tickerDispatcher.mustSchedule(tickerJob);
        return tickerDispatcher;
    }

    @Override
    public int onRunJob(JobParameters job) {
        if (NetworkUtils.isOnline(this)) {
            MainUtils.refreshGlobalData(this);
            MainUtils.refreshTickers(this);
            return JobService.RESULT_SUCCESS;
        } else {
            return JobService.RESULT_FAIL_RETRY;
        }
    }
}

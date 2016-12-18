package com.forasterisk.ilkeok.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;

/**
 * Created by yearnning on 15. 2. 9..
 */
public class LoadingViewManager {

    private static final String TAG = "LoadingViewManager";

    /**
     *
     */
    private boolean mFirstLoading = true;

    private View mLoadingView = null;
    private View mContentView = null;
    private SwipeRefreshLayout mSwipRefreshLayout = null;

    private int shortAnimTime = 0;


    public LoadingViewManager(Context context, View loadingView, View contentView, SwipeRefreshLayout swipeRefreshLayout) {
        this.mLoadingView = loadingView;
        this.mContentView = contentView;
        this.mSwipRefreshLayout = swipeRefreshLayout;

        this.shortAnimTime = context.getResources().getInteger(android.R.integer.config_shortAnimTime);
    }

    public LoadingViewManager(Context context, View loadingView, View contentView) {
        this(context, loadingView, contentView, null);
    }

    /**
     * @param show
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void show(final boolean show) {
        Log.d(TAG, "show -> " + show);

        /**
         *
         */
        if (show && mContentView.getVisibility() == View.GONE && mLoadingView.getVisibility() == View.VISIBLE) {
            return;
        }

        if (!show && mContentView.getVisibility() == View.VISIBLE && mLoadingView.getVisibility() == View.GONE) {
            return;
        }


        /**
         *
         */
        if (mFirstLoading || mSwipRefreshLayout == null) {

            // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
            // for very easy animations. If available, use these APIs to fade-in
            // the progress spinner.


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {

                if (mLoadingView != null) {

                    mLoadingView.setVisibility(View.VISIBLE);
                    mLoadingView.animate()
                            .setDuration(shortAnimTime)
                            .alpha(show ? 1 : 0)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    mLoadingView.setVisibility(show ? View.VISIBLE : View.GONE);
                                }
                            });
                }

                if (mContentView != null) {

                    if (mSwipRefreshLayout != null && show) {
                        mContentView.setVisibility(View.GONE);

                    } else {
                        mContentView.setVisibility(View.VISIBLE);
                        mContentView.animate()
                                .setDuration(shortAnimTime)
                                .alpha(show ? 0 : 1)
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        mContentView.setVisibility(show ? View.GONE : View.VISIBLE);
                                    }
                                });
                    }
                }

            } else {
                // The ViewPropertyAnimator APIs are not available, so simply show
                // and hide the relevant UI components.
                if (mLoadingView != null) {
                    mLoadingView.setVisibility(show ? View.VISIBLE : View.GONE);
                }

                if (mContentView != null) {
                    mContentView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            }

            if (!show)
                mFirstLoading = false;

        } else {

            mSwipRefreshLayout.setRefreshing(show);
        }
    }

    public void init() {
        mFirstLoading = true;
    }
}

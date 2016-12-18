package com.forasterisk.ilkeok.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.CookieSyncManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.forasterisk.ilkeok.R;
import com.forasterisk.ilkeok.service.ScheduleBroadcastReceiver;
import com.forasterisk.ilkeok.utils.Application;
import com.forasterisk.ilkeok.utils.PreferenceManager;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    /**
     *
     */
    private WebView mWebView;
    private View mProgressBar;

    /**
     * @param activity
     */
    public static void startActivity(Activity activity) {
        Intent intent = new Intent(activity, LoginActivity.class);
        activity.startActivity(intent);
        activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

    }

    /**
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        /**
         *
         */
        Tracker t = ((Application) getApplication()).getTracker(Application.TrackerName.APP_TRACKER);
        t.setScreenName("LoginActivity");
        t.send(new HitBuilders.AppViewBuilder().build());

        /**
         *
         */
        mWebView = (WebView) findViewById(R.id.web_view);
        mProgressBar = findViewById(R.id.progress_bar);

        /**
         *
         */
        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

                mProgressBar.setVisibility(View.VISIBLE);

                if (url.contains("http://m.comic.naver.com/index.nhn")) {
                    mWebView.setVisibility(View.GONE);

                    /**
                     *
                     */
                    ScheduleBroadcastReceiver mScheduleManager = new ScheduleBroadcastReceiver();
                    mScheduleManager.cancelAlarm(LoginActivity.this);
                    mScheduleManager.setAlarm(LoginActivity.this);

                    /**
                     *
                     */
                    PreferenceManager.clear(LoginActivity.this);
                    WebtoonListActivity.startActivity(LoginActivity.this);
                    finish();
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                if (url.contains("/m.naver.com/")) {
                    if (view.canGoBack()) {
                        view.goBack();
                        Tracker t = ((Application) getApplication()).getTracker(Application.TrackerName.APP_TRACKER);
                        t.send(new HitBuilders.EventBuilder()
                                .setCategory("LoginActivity")
                                .setAction("goBack()")
                                .setLabel("")
                                .build());

                    }
                }

                Log.d(TAG, "url -> " + url);

                mProgressBar.setVisibility(View.GONE);
            }
        });

        /**
         *
         */
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        /**
         *
         */
        mWebView.loadUrl("https://nid.naver.com/nidlogin.login?svctype=262144&url=http%3A%2F%2Fm.comic.naver.com%2Findex.nhn%3F");
    }

    @Override
    protected void onResume() {
        super.onResume();
        //android.webkit.CookieManager.getInstance().setAcceptCookie(true);
        if (Build.VERSION.SDK_INT < 21) {
            CookieSyncManager.getInstance().startSync();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (Build.VERSION.SDK_INT < 21) {
            CookieSyncManager.getInstance().stopSync();
        }
    }


    /**
     *
     */
    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}

package com.forasterisk.ilkeok.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.ConsoleMessage;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.forasterisk.ilkeok.R;
import com.forasterisk.ilkeok.api.EpisodeListApi;
import com.forasterisk.ilkeok.model.Episode;
import com.forasterisk.ilkeok.model.Webtoon;
import com.forasterisk.ilkeok.utils.Application;
import com.forasterisk.ilkeok.utils.Argument;
import com.forasterisk.ilkeok.utils.IlkeokResultManager;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;

import rx.Subscriber;
import rx.Subscription;

public class IlkeokAllActivity extends AppCompatActivity {
    private static final String TAG = "IlkeokActivity";

    /**
     *
     */
    private ArrayList<Episode> mEpisodes = new ArrayList<>();

    /**
     *
     */
    private MaterialDialog mDialog;
    private TextView mContentTv;
    private View mProgressBar;
    private WebView mWebView;

    /**
     *
     */
    private String current_url = null;

    /**
     *
     */
    private Handler handler1;
    private Handler handler2;

    /**
     *
     */
    private int page = 1;
    private Subscription episodeListApiSubscription = null;

    /**
     * @param activity
     */
    public static void startActivity(Activity activity) {
        Intent intent = new Intent(activity, IlkeokAllActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ilkeok_activity);

        /**
         *
         */
        Tracker t = ((Application) getApplication()).getTracker(Application.TrackerName.APP_TRACKER);
        t.setScreenName("IlkeokAllActivity");
        t.send(new HitBuilders.AppViewBuilder().build());

        /**
         *
         */
        Bundle bundle = getIntent().getExtras();
        String url = "";

        mWebView = (WebView) findViewById(R.id.web_view);

        mProgressBar = findViewById(R.id.progress_bar);

        /**
         *
         */
        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                Log.d(TAG, "started url -> " + url);
                current_url = url;
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                Log.d(TAG, "finished url -> " + url);
                super.onPageFinished(view, url);

                check_ilkeoked();
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
        addJavascriptInterface();
        setWebViewClient();

        /**
         *
         */
        handler1 = new Handler();
        handler2 = new Handler();

        /**
         *
         */
        mWebView.loadUrl(url);

        /**
         *
         */
        MaterialDialog.Builder builder = new MaterialDialog.Builder(IlkeokAllActivity.this)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        super.onPositive(dialog);

                        Tracker t = ((Application) getApplication()).getTracker(Application.TrackerName.APP_TRACKER);
                        t.send(new HitBuilders.EventBuilder()
                                .setCategory("IlkeokAllActivity")
                                .setAction("positiveButton")
                                .setLabel("")
                                .setValue(mEpisodes.size())
                                .build());

                        requestEpisodeListApi();
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        super.onNegative(dialog);

                        Tracker t = ((Application) getApplication()).getTracker(Application.TrackerName.APP_TRACKER);
                        t.send(new HitBuilders.EventBuilder()
                                .setCategory("IlkeokAllActivity")
                                .setAction("negativeButton")
                                .setLabel("")
                                .setValue(mEpisodes.size())
                                .build());

                        dialog.dismiss();

                    }
                })
                .autoDismiss(false)
                .cancelable(false)
                .dismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        beforeOnDestroy();
                        finish();
                    }
                });


        builder.title("모든 화에 일격 날리기")
                .customView(createContentView(), false)
                .positiveText("날리겠어요!")
                .negativeText("안날릴래요");
        mDialog = builder.show();


    }

    private boolean is_end(ArrayList<Episode> episodes) {
        for (Episode episode : episodes) {
            if (episode.getDate().contains("12.03.07") || episode.getName().equals("1화")) {
                return true;
            }
        }
        return false;
    }

    private void requestEpisodeListApi() {
        Webtoon webtoon = Webtoon.newInstance(3);

        episodeListApiSubscription = EpisodeListApi.create(webtoon.naver_id, page)
                .subscribe(new Subscriber<ArrayList<Episode>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(ArrayList<Episode> episodes) {
                        mEpisodes.addAll(episodes);
                        page++;

                        if (is_end(episodes)) {
                            ilkeokAll();
                        } else {
                            requestEpisodeListApi();
                        }
                    }
                });


    }

    /**
     * @return
     */
    private View createContentView() {
        View view = getLayoutInflater().inflate(R.layout.ilkeok_activity_dialog, null);
        mContentTv = (TextView) view.findViewById(R.id.content_tv);
        mProgressBar = view.findViewById(R.id.progress_bar);
        return view;
    }

    /**
     *
     */
    @SuppressWarnings("JavascriptInterface")
    private void addJavascriptInterface() {
        mWebView.addJavascriptInterface(new JavascriptIlkeokInterface(), "Ilkeok");
    }

    /**
     *
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
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
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void ilkeok() {

        /**
         *
         */
        String script = "javascript:starAct.star_clk('1');" + ""
                + "document.getElementById('bottomStarScoreSubmitButton').click();";
        if (Build.VERSION.SDK_INT >= 19) {
            mWebView.evaluateJavascript(script, null);
        } else {
            mWebView.loadUrl(script);
        }
    }


    private void setWebViewClient() {
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(final WebView view,
                                     final String url, final String message,
                                     JsResult result) {
                Log.d(TAG, "onJsAlert(!" + view + ", " + url + ", "
                        + message + ", " + result + ")");
                //Toast.makeText(DetailActivity.this, message, Toast.LENGTH_LONG).show();
                Log.d(TAG, "ilkeok() success!");
                check_ilkeoked();
                result.confirm();
                return true; // I handled it
            }

            public void onConsoleMessage(String message, int lineNumber, String sourceID) {
                Log.d(TAG, message + " -- From line "
                        + lineNumber + " of "
                        + sourceID);
            }

            public boolean onConsoleMessage(ConsoleMessage cm) {
                Log.d(TAG, cm.message() + " -- From line "
                        + cm.lineNumber() + " of "
                        + cm.sourceId());
                return true;
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void check_ilkeoked() {
        String script = "javascript:"
                + "try {"
                + "var voteComplete = document.getElementById('voteComplete');"
                + "window.Ilkeok.onIlkeoked(voteComplete.getAttribute('style'));"
                + "} catch (err) { "
                + "window.Ilkeok.onIlkeoked('')"
                + "}";
        if (Build.VERSION.SDK_INT >= 19) {
            mWebView.evaluateJavascript(script, null);
        } else {
            mWebView.loadUrl(script);
        }
    }

    private class JavascriptIlkeokInterface {

        @JavascriptInterface // 반드시 이걸 써야 작동한다고 한다
        public void onIlkeoked(String style) {
            Log.d(TAG, "style -> " + style);

            if (style != null && style.contains("display:none")) {
                Log.d(TAG, "check -> unilkeoked");
                handler1.post(new Runnable() {
                    @Override
                    public void run() {
                        ilkeok();
                    }
                });
            } else {
                Log.d(TAG, "check -> ilkeoked");

                int star = 1;
                if (IlkeokResultManager.isTen(current_url)) {
                    star = 10;
                }
                IlkeokResultManager.setResult(IlkeokAllActivity.this, current_url, star);

                handler2.post(new Runnable() {
                    @Override
                    public void run() {

                        // update content tv
                        Log.d(TAG, "update content tv");

                        if (mEpisodes.size() > 0) {
                            Episode episode = mEpisodes.get(0);
                            String name = episode.getName();

                            if (IlkeokResultManager.isTen(episode.getUrl())) {
                                mContentTv.setText(name + "에 십격을 날렸습니다");
                            } else {
                                mContentTv.setText(name + "에 일격을 날렸습니다");
                            }

                            //
                            Log.d(TAG, "remove current and start new Ilkeok");
                            mEpisodes.remove(0);
                            ilkeokAll();

                            //
                            Log.d(TAG, "finished");
                        }
                    }
                });
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        beforeOnDestroy();
    }

    /**
     *
     */
    private void beforeOnDestroy() {
        handler1 = null;
        handler2 = null;

        if (!episodeListApiSubscription.isUnsubscribed()) {
            episodeListApiSubscription.unsubscribe();
        }
    }


    private void ilkeokAll() {

        Tracker t = ((Application) getApplication()).getTracker(Application.TrackerName.APP_TRACKER);
        t.send(new HitBuilders.EventBuilder()
                .setCategory("IlkeokAllActivity")
                .setAction("ilkeokAll")
                .setLabel("")
                .setValue(mEpisodes.size())
                .build());

        if (mEpisodes.size() > 0) {
            Log.d(TAG, "left webtoons");
            Log.d(TAG, "left url -> " + mEpisodes.get(0).getUrl());

            int star = IlkeokResultManager.getResult(IlkeokAllActivity.this, mEpisodes.get(0).getUrl());
            if (star != 0) {
                Log.d(TAG, "skipped -> " + mEpisodes.get(0).getName());
                mContentTv.setText(mEpisodes.get(0).getName() + "에는 이미 일격을 날렸습니다.");
                mEpisodes.remove(0);
                ilkeokAll();

            } else {
                mWebView.loadUrl(mEpisodes.get(0).getUrl());
            }

        } else {
            Log.d(TAG, "no left webtoons");
            mProgressBar.setVisibility(View.GONE);
            mDialog.setActionButton(DialogAction.NEGATIVE, "닫기");
            mContentTv.setText("모든 화에 성공적으로 일격을 날렸습니다");
        }
    }

}

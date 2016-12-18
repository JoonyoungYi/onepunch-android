package com.forasterisk.ilkeok.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.forasterisk.ilkeok.R;
import com.forasterisk.ilkeok.utils.Application;
import com.forasterisk.ilkeok.utils.IlkeokResultManager;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class EpisodeDetailActivity extends AppCompatActivity {

    private static final String TAG = "DetailActivity";

    /**
     *
     */
    private static final String ARG_URL = "arg_url";

    /**
     *
     */
    private enum IlkeokStatus {
        UNKNOWN, ILKEOKED, UNILKEOKED
    }

    /**
     *
     */
    private String mTitle;
    private IlkeokStatus mIlkeokStatus = IlkeokStatus.UNKNOWN;

    /**
     *
     */
    private String current_url = "";
    private String last_cartoon_url = "";

    /**
     *
     */
    private Handler handler;

    /**
     *
     */
    private ActionBar mActionBar;
    private View mNavigationView;
    private View mReturnView;
    private View mProgressBar;
    private WebView mWebView;
    private View mIlkeokView;
    private View mPrevBtn;
    private View mNextBtn;

    /**
     * @param activity
     * @param url
     */
    public static void startActivity(Activity activity, String url) {
        Intent intent = new Intent(activity, EpisodeDetailActivity.class);
        intent.putExtra(ARG_URL, url);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.episode_detail_activity);

        /**
         *
         */
        Tracker t = ((Application) getApplication()).getTracker(Application.TrackerName.APP_TRACKER);
        t.setScreenName("DetailActivity");
        t.send(new HitBuilders.AppViewBuilder().build());

        /**
         *
         */
        Bundle bundle = getIntent().getExtras();
        String url = bundle.getString(ARG_URL);

        /**
         *
         */
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /**
         *
         */
        mActionBar = getSupportActionBar();
        if (mActionBar != null) {
            mActionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP
                    | ActionBar.DISPLAY_SHOW_HOME
                    | ActionBar.DISPLAY_SHOW_TITLE);
        }

        /**
         *
         */
        mNavigationView = findViewById(R.id.navigation_view);
        mWebView = (WebView) findViewById(R.id.web_view);
        mIlkeokView = findViewById(R.id.ilkeok_view);
        mProgressBar = findViewById(R.id.progress_bar);
        mPrevBtn = findViewById(R.id.prev_btn);
        mNextBtn = findViewById(R.id.next_btn);
        mReturnView = findViewById(R.id.return_view);

        /**
         *
         */
        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                Log.d(TAG, "started url -> " + url);

                /**
                 *
                 */
                current_url = url;

                /**
                 *
                 */
                mWebView.setVisibility(View.GONE);
                mNavigationView.setVisibility(View.GONE);
                mIlkeokView.setVisibility(View.GONE);
                mProgressBar.setVisibility(View.VISIBLE);

                if (is_webtoon(url)) {
                    mReturnView.setVisibility(View.GONE);
                } else {
                    mReturnView.setVisibility(View.VISIBLE);
                }

                /**
                 *
                 */
                mIlkeokStatus = IlkeokStatus.UNKNOWN;
                supportInvalidateOptionsMenu();
                mActionBar.setTitle("");

                /**
                 *
                 */
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                Log.d(TAG, "finished url -> " + url);

                /**
                 *
                 */
                super.onPageFinished(view, url);

                /**
                 *
                 */
                if (is_webtoon(url)) {
                    mReturnView.setVisibility(View.GONE);
                    init();

                } else {
                    mReturnView.setVisibility(View.VISIBLE);
                    mWebView.setVisibility(View.VISIBLE);
                    mNavigationView.setVisibility(View.VISIBLE);
                    mIlkeokView.setVisibility(View.GONE);
                    mProgressBar.setVisibility(View.GONE);
                }
            }

            /**
             *
             * @param url
             * @return
             */
            private boolean is_webtoon(String url) {

                //
                if (url == null) {
                    return false;
                }

                //
                if (url.contains("comic.naver.com") && url.contains("/webtoon/detail.nhn")
                        && url.contains("titleId=449854") && url.contains("no=")) {
                    last_cartoon_url = url;
                    return true;
                }

                //
                return false;
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
        findViewById(R.id.ilkeok_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ilkeok();
                mIlkeokView.setVisibility(View.GONE);

                Tracker t = ((Application) getApplication()).getTracker(Application.TrackerName.APP_TRACKER);
                t.send(new HitBuilders.EventBuilder()
                        .setCategory("DetailActivity")
                        .setAction("IlkeokBtn")
                        .build());

            }
        });

        findViewById(R.id.later_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mIlkeokView.setVisibility(View.GONE);

                Tracker t = ((Application) getApplication()).getTracker(Application.TrackerName.APP_TRACKER);
                t.send(new HitBuilders.EventBuilder()
                        .setCategory("DetailActivity")
                        .setAction("LaterBtn")
                        .build());
            }
        });

        findViewById(R.id.comment_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCommentView();

                Tracker t = ((Application) getApplication()).getTracker(Application.TrackerName.APP_TRACKER);
                t.send(new HitBuilders.EventBuilder()
                        .setCategory("DetailActivity")
                        .setAction("CommentBtn")
                        .build());
            }
        });

        findViewById(R.id.return_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mWebView.loadUrl(last_cartoon_url);

                Tracker t = ((Application) getApplication()).getTracker(Application.TrackerName.APP_TRACKER);
                t.send(new HitBuilders.EventBuilder()
                        .setCategory("DetailActivity")
                        .setAction("ReturnBtn")
                        .build());
            }
        });

        mPrevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Tracker t = ((Application) getApplication()).getTracker(Application.TrackerName.APP_TRACKER);
                t.send(new HitBuilders.EventBuilder()
                        .setCategory("DetailActivity")
                        .setAction("PrevBtn")
                        .build());

                mProgressBar.setVisibility(View.VISIBLE);
                Object url_object = view.getTag(R.string.tag_url);
                if (url_object != null) {
                    String url = (String) url_object;
                    if (url.trim().length() > 0) {
                        mWebView.loadUrl(url);
                    } else {
                        Toast.makeText(EpisodeDetailActivity.this, "이전 화가 없습니다", Toast.LENGTH_SHORT).show();
                        mProgressBar.setVisibility(View.GONE);
                    }
                } else {
                    mProgressBar.setVisibility(View.GONE);
                }
            }
        });

        mNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Tracker t = ((Application) getApplication()).getTracker(Application.TrackerName.APP_TRACKER);
                t.send(new HitBuilders.EventBuilder()
                        .setCategory("DetailActivity")
                        .setAction("NextBtn")
                        .build());

                mProgressBar.setVisibility(View.VISIBLE);

                Object url_object = view.getTag(R.string.tag_url);
                if (url_object != null) {
                    String url = (String) url_object;
                    if (url.trim().length() > 0) {
                        mWebView.loadUrl(url);
                    } else {
                        Toast.makeText(EpisodeDetailActivity.this, "다음 화가 없습니다", Toast.LENGTH_SHORT).show();
                        mProgressBar.setVisibility(View.GONE);
                    }
                } else {
                    mProgressBar.setVisibility(View.GONE);
                }
            }
        });

        /**
         *
         */
        handler = new Handler();

        /**
         *
         */
        mWebView.loadUrl(url);
    }

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
        int star = 1;
        if (IlkeokResultManager.isTen(current_url)) {
            star = 1;
        }
        String script = "javascript:starAct.star_clk('" + star + "');" + ""
                + "document.getElementById('bottomStarScoreSubmitButton').click();";
        if (Build.VERSION.SDK_INT >= 19) {
            mWebView.evaluateJavascript(script, null);
        } else {
            mWebView.loadUrl(script);
        }

        Toast.makeText(EpisodeDetailActivity.this, "일격을 날렸습니다", Toast.LENGTH_LONG).show();
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
                Log.d(TAG, "" + result);
                check_ilkeoked();
                //return true; // I handled it
                return false;
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

    /**
     * @return
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void init() {

        Log.d(TAG, "init() started!");

        /**
         *
         */
        mProgressBar.setVisibility(View.VISIBLE);

        /**
         *
         */
        String script = "javascript:"
                + "try {"
                + "var voteStatus = document.getElementById('voteComplete').getAttribute('style');"
                + "var pv_url = '';"
                + "var nx_url = '';"
                + "var formForm = document.getElementById('form');"
                + "var bt7s = formForm.getElementsByClassName('bt7');"
                + "var as = bt7s[0].getElementsByTagName('a');"
                + "for (i = 0; i < as.length ; i ++ ) { "
                + "    if (as[i].innerHTML.search('pv') != -1 ){"
                + "        pv_url = as[i]['href'];"
                + "    } else if (as[i].innerHTML.search('nx') != -1 ){"
                + "        nx_url = as[i]['href'];"
                + "    }"
                + "}"
                + "window.Ilkeok.init(voteStatus, pv_url, nx_url);"
                + "} catch (err) { "
                + "window.Ilkeok.init('', '', '');"
                + "}";
        if (Build.VERSION.SDK_INT >= 19) {
            mWebView.evaluateJavascript(script, null);
        } else {
            mWebView.loadUrl(script);
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void check_ilkeoked() {
        String script = "javascript:window.Ilkeok.onIlkeoked(document.getElementById('voteComplete').getAttribute('style'));";
        if (Build.VERSION.SDK_INT >= 19) {
            mWebView.evaluateJavascript(script, null);
        } else {
            mWebView.loadUrl(script);
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void showCommentView() {
        /**
         *
         */
        String script = "javascript:document.getElementById('upload_txt').scrollIntoView();";
        if (Build.VERSION.SDK_INT >= 19) {
            mWebView.evaluateJavascript(script, null);
        } else {
            mWebView.loadUrl(script);
        }
    }


    private class JavascriptIlkeokInterface {

        @JavascriptInterface // 반드시 이걸 써야 작동한다고 한다
        public void init(String voteStatus, final String prev_url, final String next_url) {
            Log.d(TAG, "voteStatus -> " + voteStatus);
            Log.d(TAG, "prev_url -> " + prev_url);
            Log.d(TAG, "next_url -> " + next_url);

            if (voteStatus != null && voteStatus.contains("display:none")) {
                mIlkeokStatus = IlkeokStatus.UNILKEOKED;
            } else {
                IlkeokResultManager.setResult(EpisodeDetailActivity.this, current_url, 1);
                mIlkeokStatus = IlkeokStatus.ILKEOKED;
            }
            supportInvalidateOptionsMenu();

            handler.post(new Runnable() {
                @Override
                public void run() {

                    /**
                     *
                     */
                    mActionBar.setTitle("");

                    /**
                     *
                     */
                    if (mIlkeokStatus == IlkeokStatus.UNILKEOKED) {
                        mIlkeokView.setVisibility(View.VISIBLE);
                    } else {
                        mIlkeokView.setVisibility(View.GONE);
                    }
                    mWebView.setVisibility(View.VISIBLE);
                    mNavigationView.setVisibility(View.VISIBLE);
                    mProgressBar.setVisibility(View.GONE);

                    /**
                     *
                     */
                    setPrevNextUrl(prev_url, next_url);
                }
            });
        }


        @JavascriptInterface // 반드시 이걸 써야 작동한다고 한다
        public void onIlkeoked(String style) {
            Log.d(TAG, "style -> " + style);

            if (style != null && style.contains("display:none")) {
                mIlkeokStatus = IlkeokStatus.UNILKEOKED;
            } else {
                IlkeokResultManager.setResult(EpisodeDetailActivity.this, current_url, 1);
                mIlkeokStatus = IlkeokStatus.ILKEOKED;
            }
            supportInvalidateOptionsMenu();

        }
    }

    /**
     * @param prev_url
     * @param next_url
     */
    private void setPrevNextUrl(String prev_url, String next_url) {
        mPrevBtn.setTag(R.string.tag_url, prev_url);
        mNextBtn.setTag(R.string.tag_url, next_url);
    }

    /**
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail, menu);
        menu.findItem(R.id.action_ilkeok).setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        if (mIlkeokStatus == IlkeokStatus.UNILKEOKED) {
            MenuItem menuItem = menu.findItem(R.id.action_ilkeok);
            menuItem.setVisible(true);
            menuItem.setIcon(R.drawable.ic_ilkeok);

        } else if (mIlkeokStatus == IlkeokStatus.ILKEOKED) {
            MenuItem menuItem = menu.findItem(R.id.action_ilkeok);
            menuItem.setVisible(true);
            menuItem.setIcon(R.drawable.ic_ilkeok_deactivated);
        }

        return true;
    }

    /**
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            onBackPressed();
            Tracker t = ((Application) getApplication()).getTracker(Application.TrackerName.APP_TRACKER);
            t.send(new HitBuilders.EventBuilder()
                    .setCategory("DetailActivity")
                    .setAction("homeOptions")
                    .build());

            return true;

        } else if (id == R.id.action_ilkeok) {

            if (mIlkeokStatus == IlkeokStatus.ILKEOKED) {
                Tracker t = ((Application) getApplication()).getTracker(Application.TrackerName.APP_TRACKER);
                t.send(new HitBuilders.EventBuilder()
                        .setCategory("DetailActivity")
                        .setAction("IlkeokOptions")
                        .setLabel("already")
                        .build());
                Toast.makeText(EpisodeDetailActivity.this, "이미 일격을 날리셨습니다", Toast.LENGTH_SHORT).show();
            } else if (mIlkeokStatus == IlkeokStatus.UNILKEOKED) {
                Tracker t = ((Application) getApplication()).getTracker(Application.TrackerName.APP_TRACKER);
                t.send(new HitBuilders.EventBuilder()
                        .setCategory("DetailActivity")
                        .setAction("IlkeokOptions")
                        .setLabel("do")
                        .build());
                ilkeok();
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        handler = null;
    }

}

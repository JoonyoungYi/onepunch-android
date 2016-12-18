package com.forasterisk.ilkeok.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class SplashActivity extends AppCompatActivity {

    private static final String TAG = "SplashActivity";

    /**
     *
     */
    private WebView mWebView;

    public static void startActivity(Activity activity) {
        Intent intent = new Intent(activity, SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(intent);
        activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    /**
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);

        /**
         *
         */
        Tracker t = ((Application) getApplication()).getTracker(Application.TrackerName.APP_TRACKER);
        t.setScreenName("SplashActivity");
        t.send(new HitBuilders.AppViewBuilder().build());

        /**
         *
         */
        CookieSyncManager.createInstance(this);


        /**
         *
         */
        mWebView = (WebView) findViewById(R.id.web_view);

        /**
         *
         */
        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                check_login();
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
        mWebView.loadUrl("http://m.comic.naver.com/index.nhn?");
    }

    private void setWebViewClient() {
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(final WebView view,
                                     final String url, final String message,
                                     JsResult result) {
                Log.d(TAG, "onJsAlert(!" + view + ", " + url + ", "
                        + message + ", " + result + ")");
                Toast.makeText(SplashActivity.this, message, Toast.LENGTH_LONG).show();
                Log.d(TAG, "" + result);
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


    @SuppressWarnings("JavascriptInterface")
    private void addJavascriptInterface() {
        mWebView.addJavascriptInterface(new JavascriptCheckIlkeoked(), "JavascriptCheckIlkeoked");
    }

    /**
     * @return
     */
    private void check_login() {

        String script = "javascript:"
                + "var u_ftlkw = document.getElementById('u_ftlkw');"
                + "if (u_ftlkw){"
                + "    window.JavascriptCheckIlkeoked.setStyle(u_ftlkw.innerHTML)"
                + "} else {"
                + "    window.JavascriptCheckIlkeoked.invalid()"
                + "}";
        if (Build.VERSION.SDK_INT >= 19) {
            mWebView.evaluateJavascript(script, null);
        } else {
            mWebView.loadUrl(script);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        CookieSyncManager.getInstance().startSync();
    }

    @Override
    protected void onPause() {
        super.onPause();
        CookieSyncManager.getInstance().stopSync();
    }

    /**
     *
     */

    //TODO  : 리더던시를 제거해야 한다!!!
    private class JavascriptCheckIlkeoked {

        @JavascriptInterface // 반드시 이걸 써야 작동한다고 한다
        public void invalid() {
            Toast.makeText(SplashActivity.this, "로그인 상태를 확인할 수 없습니다. 관리자에게 문의해주세요", Toast.LENGTH_LONG).show();
            finish();
        }

        @JavascriptInterface // 반드시 이걸 써야 작동한다고 한다
        public void setStyle(String style) {
            Log.d(TAG, "style -> " + style);

            if (style.contains("login()")) {
                LoginActivity.startActivity(SplashActivity.this);
                finish();

            } else if (style.contains("logout()")) {
                WebtoonListActivity.startActivity(SplashActivity.this);
                finish();

            } else {

                Tracker t = ((Application) getApplication()).getTracker(Application.TrackerName.APP_TRACKER);
                t.send(new HitBuilders.EventBuilder()
                        .setCategory("SplashActivity")
                        .setAction("UnidentifiedLoginStatus")
                        .setLabel("")
                        .build());

                Toast.makeText(SplashActivity.this, "로그인 상태를 확인할 수 없습니다. 관리자에게 문의해주세요", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }


}

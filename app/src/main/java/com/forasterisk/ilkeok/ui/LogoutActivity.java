package com.forasterisk.ilkeok.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.forasterisk.ilkeok.R;
import com.forasterisk.ilkeok.utils.Application;
import com.forasterisk.ilkeok.utils.PreferenceManager;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class LogoutActivity extends AppCompatActivity {
    private static final String TAG = "LogoutActivity";

    /**
     *
     */
    private WebView mWebView;

    /**
     * @param activity
     */
    public static void startActivity(Activity activity) {
        Intent intent = new Intent(activity, LogoutActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.logout_activity);

        /**
         *
         */
        Tracker t = ((Application) getApplication()).getTracker(Application.TrackerName.APP_TRACKER);
        t.setScreenName("LogoutActivity");
        t.send(new HitBuilders.AppViewBuilder().build());

        /**
         *
         */
        Bundle bundle = getIntent().getExtras();

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

                if (url.contains("/mypage/recentlyview.nhn")) {

                    /*)
                    MaterialDialog.Builder builder = new MaterialDialog.Builder(LogoutActivity.this);
                    builder.title("로그아웃")
                            .content("정말 로그아웃 하시겠어요?")
                            .positiveText("확인")
                            .negativeText("닫기")
                            .progress(true, -1)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(MaterialDialog dialog, DialogAction which) {
                                    dialog.getProgressBar().setVisibility(View.VISIBLE);
                                    dialog.setContent("로그아웃 하는 중입니다");
                                    dialog.setActionButton(DialogAction.POSITIVE, "");
                                    dialog.setActionButton(DialogAction.NEGATIVE, "");
                                    logout();
                                }
                            })
                            .onNegative(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(MaterialDialog dialog, DialogAction which) {
                                    dialog.dismiss();
                                }
                            })
                            .cancelable(false)
                            .autoDismiss(false)
                            .dismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    onBackPressed();
                                }
                            });

                    MaterialDialog dialog = builder.show();
                    dialog.getProgressBar().setVisibility(View.GONE); */
                    logout();

                } else if (url.contains("login?")) {

                    PreferenceManager.clear(LogoutActivity.this);
                    SplashActivity.startActivity(LogoutActivity.this);
                    Toast.makeText(LogoutActivity.this, "앱을 재시작합니다", Toast.LENGTH_LONG).show();

                } else {
                    SplashActivity.startActivity(LogoutActivity.this);
                    Toast.makeText(LogoutActivity.this, "오류가 발생했습니다", Toast.LENGTH_LONG).show();
                }
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
        mWebView.loadUrl("http://m.comic.naver.com/mypage/recentlyview.nhn");
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
        CookieSyncManager.getInstance().startSync();
    }

    @Override
    protected void onPause() {
        super.onPause();
        CookieSyncManager.getInstance().stopSync();
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
    }


    private void setWebViewClient() {
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(final WebView view,
                                     final String url, final String message,
                                     JsResult result) {
                Log.d(TAG, "onJsAlert(!" + view + ", " + url + ", "
                        + message + ", " + result + ")");
                Log.d(TAG, "logout() success!");
                result.confirm();
                return true; // I handled it
            }

            public void onConsoleMessage(String message, int lineNumber, String sourceID) {
                Log.d(TAG+ "!!!!", message + " -- From line "
                        + lineNumber + " of "
                        + sourceID);
            }
            @Override
            public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
                Log.d(TAG, "onJsPrompt(!" + view + ", " + url + ", "
                        + message + ", " + result + ")");
                result.confirm();
                return true;
            }

            public boolean onConsoleMessage(ConsoleMessage cm) {
                Log.d(TAG + "!!!", cm.message() + " -- From line "
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
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void logout() {

        String script = "javascript:logout();";
        if (Build.VERSION.SDK_INT >= 19) {
            mWebView.evaluateJavascript(script, null);
        } else {
            mWebView.loadUrl(script);
        }
    }

    /**
     * @return
     */
    private void check_login() {

        String script = "javascript:"
                + "var u_ftlkw = document.getElementById('u_ftlkw');"
                + "window.JavascriptCheckIlkeoked.setStyle(u_ftlkw.innerHTML)";
        if (Build.VERSION.SDK_INT >= 19) {
            mWebView.evaluateJavascript(script, null);
        } else {
            mWebView.loadUrl(script);
        }
    }

    /**
     *
     */
    private class JavascriptCheckIlkeoked {

        @JavascriptInterface // 반드시 이걸 써야 작동한다고 한다
        public void setStyle(String style) {
            Log.d(TAG, "style -> " + style);

            if (style.contains("login()")) {

                Toast.makeText(LogoutActivity.this, "로그인해야함!", Toast.LENGTH_SHORT).show();
                LoginActivity.startActivity(LogoutActivity.this);
                finish();

            } else if (style.contains("logout()")) {
                logout();

            } else {

                Tracker t = ((Application) getApplication()).getTracker(Application.TrackerName.APP_TRACKER);
                t.send(new HitBuilders.EventBuilder()
                        .setCategory("LogoutActivity")
                        .setAction("UnidentifiedLoginStatus")
                        .setLabel("")
                        .build());

                Toast.makeText(LogoutActivity.this, "로그인 상태를 확인할 수 없습니다. 관리자에게 문의해주세요", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

}

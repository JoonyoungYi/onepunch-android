package com.forasterisk.ilkeok.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.forasterisk.ilkeok.R;
import com.forasterisk.ilkeok.model.Webtoon;
import com.forasterisk.ilkeok.utils.Application;
import com.forasterisk.ilkeok.utils.list.WebtoonLvAdapter;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;

public class WebtoonListActivity extends AppCompatActivity {

    private static final String TAG = "WebtoonListAct";

    private WebtoonLvAdapter mLvAdapter;

    public static void startActivity(Activity activity) {
        Intent intent = new Intent(activity, WebtoonListActivity.class);
        activity.startActivity(intent);
        activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webtoon_list_activity);

        /**
         *
         */
        Tracker t = ((Application) getApplication()).getTracker(Application.TrackerName.APP_TRACKER);
        t.setScreenName("NewMainActivity");
        t.send(new HitBuilders.AppViewBuilder().build());

        /**
         *
         */
        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_USE_LOGO);
        mActionBar.setLogo(R.mipmap.logo_main_activity);

        /**
         *
         */
        ListView mLv = (ListView) findViewById(R.id.lv);

        /*
         * ListView Setting
		 */
        ArrayList<Webtoon> webtoons = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            webtoons.add(Webtoon.newInstance(i));
        }

        mLvAdapter = new WebtoonLvAdapter(this, R.layout.webtoon_list_activity_lv,
                webtoons);
        mLv.setAdapter(mLvAdapter);
    }
}

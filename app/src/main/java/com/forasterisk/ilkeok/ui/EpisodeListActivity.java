package com.forasterisk.ilkeok.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.forasterisk.ilkeok.R;
import com.forasterisk.ilkeok.api.EpisodeListApi;
import com.forasterisk.ilkeok.api.WebtoonDetailApi;
import com.forasterisk.ilkeok.model.Episode;
import com.forasterisk.ilkeok.model.Webtoon;
import com.forasterisk.ilkeok.utils.Application;
import com.forasterisk.ilkeok.utils.Argument;
import com.forasterisk.ilkeok.utils.LoadingViewManager;
import com.forasterisk.ilkeok.utils.list.EpisodeLvAdapter;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;

import rx.Subscriber;
import rx.Subscription;

public class EpisodeListActivity extends AppCompatActivity {

    private static final String ARG_WEBTOON_ID = "arg_webtoon_id";
    private static final String TAG = "EpisodeActivity";

    /**
     *
     */
    private ListView mLv;
    private TextView mLvHeaderStarTv;
    private View mLvFooterView;
    private View mLvFooterProgressBar;

    /**
     *
     */
    private Webtoon webtoon;
    private int page = 1;

    /**
     *
     */
    private EpisodeLvAdapter mLvAdapter;

    /**
     *
     */
    private LoadingViewManager mLoadingViewManager;

    /**
     *
     */
    private Subscription episodeListApiSubscription;
    private Subscription webtoonDetailApiSubscription;

    /**
     * @param activity
     */
    public static void startActivity(Activity activity, int webtoon_id) {
        Intent intent = new Intent(activity, EpisodeListActivity.class);
        intent.putExtra(ARG_WEBTOON_ID, webtoon_id);
        activity.startActivity(intent);
    }

    /**
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.episode_list_activity);

        final int webtoon_id = getIntent().getExtras().getInt(ARG_WEBTOON_ID);
        webtoon = Webtoon.newInstance(webtoon_id);

        /**
         *
         */
        Tracker t = ((Application) getApplication()).getTracker(Application.TrackerName.APP_TRACKER);
        t.setScreenName("EpisodeActivity");
        t.send(new HitBuilders.AppViewBuilder().build());

        /**
         *
         */
        LayoutInflater layoutInflater = getLayoutInflater();
        View mLvHeaderView = layoutInflater.inflate(R.layout.episode_list_activity_lv_header, null);
        mLvFooterView = layoutInflater.inflate(R.layout.episode_list_activity_lv_footer, null);

        /**
         *
         */
        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_USE_LOGO);
        mActionBar.setLogo(R.mipmap.logo_main_activity);

        /**
         *
         */
        mLv = (ListView) findViewById(R.id.lv);
        ImageView mLvHeaderCoverIv = (ImageView) mLvHeaderView.findViewById(R.id.cover_iv);
        TextView mNameTv = (TextView) mLvHeaderView.findViewById(R.id.name_tv);
        TextView mWriterTv = (TextView) mLvHeaderView.findViewById(R.id.writer_tv);
        View mProgressBar = findViewById(R.id.progress_bar);
        mLvFooterProgressBar = mLvFooterView.findViewById(R.id.progress_bar);
        mLvHeaderStarTv = (TextView) mLvHeaderView.findViewById(R.id.star_tv);

        /*

         */
        mLv.setItemsCanFocus(true);
        mLv.addHeaderView(mLvHeaderView);
        mLv.addFooterView(mLvFooterView);

        /*
         * ListView Setting
		 */
        ArrayList<Episode> episodes = new ArrayList<Episode>();
        mLvAdapter = new EpisodeLvAdapter(EpisodeListActivity.this,
                R.layout.episode_list_activity_lv,
                episodes);
        mLvAdapter.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestMoreEpisodes();
            }
        });
        mLv.setAdapter(mLvAdapter);

        /**
         *
         */
        mLoadingViewManager = new LoadingViewManager(EpisodeListActivity.this, mProgressBar, mLv);

        /**
         *
         */
        mNameTv.setText(webtoon.name);
        mWriterTv.setText(webtoon.writer);
        ((Application) getApplication()).getPicasso()
                .load(webtoon.img_url)
                .into(mLvHeaderCoverIv);

        /**
         *
         */
        webtoonDetailApiSubscription = WebtoonDetailApi.create(webtoon.name)
                .subscribe(new Subscriber<Webtoon>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Webtoon webtoon) {

                        if (webtoon != null) {
                            double star = webtoon.star;

                            Tracker t = ((Application) getApplication()).getTracker(Application.TrackerName.APP_TRACKER);
                            t.send(new HitBuilders.EventBuilder()
                                    .setCategory("MainActivity")
                                    .setAction("Get")
                                    .setLabel("AverageStar")
                                    .setValue((int) (star * 10000))
                                    .build());

                            mLvHeaderStarTv.setText(String.format("%1.4f", star));
                            requestMoreEpisodes();
                        }
                    }
                });
    }

    /**
     *
     */
    private void requestMoreEpisodes() {

        /**
         *
         */
        if (mLvAdapter.is_additional_finished()) {
            return;
        }

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
                        Tracker t = ((Application) getApplication()).getTracker(Application.TrackerName.APP_TRACKER);
                        t.send(new HitBuilders.EventBuilder()
                                .setCategory("MainActivity")
                                .setAction("ListApi")
                                .setLabel("page")
                                .setValue(page)
                                .build());


                        /**
                         * 반드시 이후 section보다 앞에 와야 합니다.
                         */
                        if (page <= 1) {
                            mLvAdapter.episodes.clear();
                        }

                        /**
                         *
                         */
                        if (episodes != null) {
                            page++;
                        }

                        /**
                         *
                         */
                        if (episodes == null) {
                            //mErrorViewManager.show(true, ErrorViewManager.Type.FAIL_LOADING_STORES);

                            if (mLvAdapter.getCount() == 0) {
                                mLvFooterView.setVisibility(View.GONE);
                            } else {
                                mLvFooterView.setVisibility(View.VISIBLE);
                            }

                        } else if (episodes.size() != 0) {

                            for (Episode episode : episodes) {
                                Log.d(TAG, "episode.getDate() -> Main -> " + episode.getDate());
                                if (episode.getDate().contains("12.03.07") || episode.getName().equals("1화")) {
                                    mLvAdapter.setIs_additional_finished(true);
                                    Log.d(TAG, "episode.getDate() -> Main -> " + episode.getDate());
                                    Log.d(TAG, "episode.getName() -> Main -> " + episode.getName());
                                    Log.d(TAG, "1화 found");
                                    break;
                                }
                            }

                /*
                mErrorViewManager.show(false, ErrorViewManager.Type.HIDE);
                 */
                            mLvAdapter.episodes.addAll(episodes);


                        } else if (mLvAdapter.episodes.size() == 0) {

                 /* mErrorViewManager.show(true, ErrorViewManager.Type.NO_STORES_REGISTERED);
                 */
                            mLvFooterView.setVisibility(View.GONE);
                        } else {
                            mLvFooterView.setVisibility(View.VISIBLE);
                        }

                        /**
                         *
                         */
                        if (episodes == null || episodes.size() == 0 || mLvAdapter.is_additional_finished()) {
                            mLvFooterProgressBar.setVisibility(View.GONE);
                            mLvFooterView.setVisibility(View.GONE);
                            mLvAdapter.setIs_additional_finished(true);
                            Log.d(TAG, "1화 not found");
                        } else {
                            mLvFooterView.setVisibility(View.VISIBLE);
                            mLvFooterProgressBar.setVisibility(View.VISIBLE);
                        }

                        /**
                         *
                         */
                        mLvAdapter.notifyDataSetChanged();

                        /**
                         *
                         */
//            mSwipeRefreshLayout.setRefreshing(false);

                        /**
                         *
                         */
                        mLoadingViewManager.show(false);
                    }
                });
    }

    /**
     *
     */
    @Override
    public void onDestroy() {
        super.onDestroy();

        beforeOnDestroy();
    }

    //TODO: integrating in ActivityBase
    private void beforeOnDestroy() {
        if (webtoonDetailApiSubscription != null && !webtoonDetailApiSubscription.isUnsubscribed()) {
            webtoonDetailApiSubscription.unsubscribe();
        }

        if (episodeListApiSubscription != null && !episodeListApiSubscription.isUnsubscribed()) {
            episodeListApiSubscription.unsubscribe();
        }

    }

    /**
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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
        if (id == R.id.action_ilkeok_all) {
            IlkeokAllActivity.startActivity(EpisodeListActivity.this);
            return true;

        } else if (id == R.id.action_logout) {
            LogoutActivity.startActivity(EpisodeListActivity.this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}

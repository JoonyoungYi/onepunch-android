package com.forasterisk.ilkeok.utils.list;

import android.app.Activity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.forasterisk.ilkeok.R;
import com.forasterisk.ilkeok.model.Episode;
import com.forasterisk.ilkeok.ui.EpisodeDetailActivity;
import com.forasterisk.ilkeok.utils.Application;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;

/**
 * Created by joonyoung.yi on 2016. 7. 25..
 */
public class EpisodeLvAdapter extends ArrayAdapter<Episode> {

    private SwipeRefreshLayout.OnRefreshListener onRefreshListener = null;

    public void setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener onRefreshListener) {
        this.onRefreshListener = onRefreshListener;
    }

    public boolean is_additional_finished() {
        return is_additional_finished;
    }

    private boolean is_additional_finished = false;

    public void setIs_additional_finished(boolean is_additional_finished) {
        this.is_additional_finished = is_additional_finished;
    }

    public ArrayList<Episode> episodes;

    private static final String TAG = "EpisodeLvAdapter";

    private ViewHolder viewHolder = null;
    private int textViewResourceId;
    private Activity mActivity = null;

    public EpisodeLvAdapter(Activity activity,
                            int textViewResourceId,
                            ArrayList<Episode> episodes) {
        super(activity, textViewResourceId, episodes);

        this.mActivity = activity;
        this.textViewResourceId = textViewResourceId;
        this.episodes = episodes;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public int getCount() {
        return episodes.size();
    }

    @Override
    public Episode getItem(int position) {
        return episodes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

			/*
             * UI Initiailizing : View Holder
			 */

        if (convertView == null) {
            convertView = mActivity.getLayoutInflater()
                    .inflate(textViewResourceId, null);


            viewHolder = new ViewHolder();
            viewHolder.mIv = (ImageView) convertView.findViewById(R.id.iv);
            viewHolder.mNameTv = (TextView) convertView.findViewById(R.id.name_tv);
            viewHolder.mDateTv = (TextView) convertView.findViewById(R.id.date_tv);
            viewHolder.mStarTv = (TextView) convertView.findViewById(R.id.star_tv);
            viewHolder.mDividerView = convertView.findViewById(R.id.divider_view);

            viewHolder.mItemBtn = convertView.findViewById(R.id.item_btn);

            viewHolder.mItemBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Object episode_obj = view.getTag(R.string.tag_episode);
                    if (episode_obj != null) {
                        Episode episode = (Episode) episode_obj;
                        Tracker t = ((Application) mActivity.getApplication()).getTracker(Application.TrackerName.APP_TRACKER);
                        t.send(new HitBuilders.EventBuilder()
                                .setCategory("MainActivity")
                                .setAction("mItemBtn")
                                .setLabel(episode.getName())
                                .build());
                        EpisodeDetailActivity.startActivity(mActivity, episode.getUrl());
                    }
                }
            });


            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        /**
         * Data cached
         */
        Episode episode = this.getItem(position);

			/*
             * Data Import and export
			 */

        viewHolder.mNameTv.setText(episode.getName());
        viewHolder.mDateTv.setText(episode.getDate());
        viewHolder.mStarTv.setText(String.format("%1.03f", episode.getStar()));

        ((Application) mActivity.getApplication()).getPicasso()
                .load(episode.getImg_url())
                .into(viewHolder.mIv);

        // Set TAG
        viewHolder.mItemBtn.setTag(R.string.tag_position, position);
        viewHolder.mItemBtn.setTag(R.string.tag_episode, episode);

        //
        if (position == getCount() - 1 && is_additional_finished) {
            viewHolder.mDividerView.setVisibility(View.GONE);
        } else {
            viewHolder.mDividerView.setVisibility(View.VISIBLE);
        }

        /**
         * load more data
         */
        if (position >= getCount() - 4) {
            if (onRefreshListener != null) {
                onRefreshListener.onRefresh();
            }
            //requestMoreEpisodes();
        }

        return convertView;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    private class ViewHolder {

        ImageView mIv;
        TextView mNameTv;
        TextView mDateTv;
        TextView mStarTv;
        View mDividerView;

        View mItemBtn;
    }

}

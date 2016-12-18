package com.forasterisk.ilkeok.utils.list;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.forasterisk.ilkeok.R;
import com.forasterisk.ilkeok.model.Episode;
import com.forasterisk.ilkeok.model.Webtoon;
import com.forasterisk.ilkeok.ui.EpisodeDetailActivity;
import com.forasterisk.ilkeok.ui.EpisodeListActivity;
import com.forasterisk.ilkeok.utils.Application;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;

/**
 * Created by joonyoung.yi on 2016. 7. 25..
 */
public class WebtoonLvAdapter extends ArrayAdapter<Webtoon> {

    public ArrayList<Webtoon> webtoons;

    private static final String TAG = "WebtoonLvAdapter";

    private ViewHolder viewHolder = null;
    private int textViewResourceId;
    private Activity mActivity = null;

    public WebtoonLvAdapter(Activity activity,
                            int textViewResourceId,
                            ArrayList<Webtoon> webtoons) {
        super(activity, textViewResourceId, webtoons);

        this.mActivity = activity;
        this.textViewResourceId = textViewResourceId;
        this.webtoons = webtoons;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public int getCount() {
        return webtoons.size();
    }

    @Override
    public Webtoon getItem(int position) {
        return webtoons.get(position);
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
                    
                    Object webtoon_obj = view.getTag(R.string.tag_webtoon);
                    if (webtoon_obj != null) {
                        Webtoon webtoon = (Webtoon) webtoon_obj;
                        EpisodeListActivity.startActivity(mActivity, webtoon.id);
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
        Webtoon webtoon = this.getItem(position);

        /*
         * Data Import and export
         */
        viewHolder.mNameTv.setText(webtoon.name);
        ((Application) mActivity.getApplication()).getPicasso()
                .load(webtoon.img_url)
                .into(viewHolder.mIv);

        // Set TAG
        viewHolder.mItemBtn.setTag(R.string.tag_position, position);
        viewHolder.mItemBtn.setTag(R.string.tag_webtoon, webtoon);


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

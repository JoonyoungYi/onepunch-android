package com.forasterisk.ilkeok.api;

import android.util.Log;

import com.forasterisk.ilkeok.model.Episode;
import com.forasterisk.ilkeok.model.Webtoon;
import com.forasterisk.ilkeok.utils.Argument;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by joonyoung.yi on 2016. 7. 25..
 */
public class EpisodeListApi {

    private static final String TAG = "EpisodeListApi";

    public static Observable<ArrayList<Episode>> create(String naver_id,
                                                        int page) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://m.comic.naver.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        ApiInterface apiInterface = retrofit.create(ApiInterface.class);

        Observable<ResponseBody> observable = apiInterface.EpisodeListApi(naver_id,
                page);

        Observable<ArrayList<Episode>> observable2 = observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<ResponseBody, ArrayList<Episode>>() {
                    @Override
                    public ArrayList<Episode> call(ResponseBody responseBody) {

                        try {
                            String response = responseBody.string();
                            ArrayList<Episode> episodes = new ArrayList<>();

                            Document doc = Jsoup.parse(response);
                            Element pageList = doc.getElementById("pageList");
                            Elements lis = pageList.getElementsByTag("li");
                            for (Element li : lis) {

                                /**
                                 *
                                 */
                                Elements as = li.getElementsByTag("a");
                                if (as.size() == 0) {
                                    continue;
                                }
                                String url = "http://m.comic.naver.com/" + as.get(0).attr("href");
                                Log.d(TAG, "href -> " + as.get(0).attr("href"));
                                Log.d(TAG, "url -> " + url);

                                /**
                                 *
                                 */
                                Elements im_inbrs = li.getElementsByClass("im_inbr");
                                if (im_inbrs.size() == 0) {
                                    continue;
                                }
                                String img_url = im_inbrs.get(0).getElementsByTag("img").get(0).attr("src");
                                Log.d(TAG, "img_url -> " + img_url);

                                /**
                                 *
                                 */
                                Elements toon_names = li.getElementsByClass("toon_name");
                                if (toon_names.size() == 0) {
                                    continue;
                                }
                                String name = toon_names.get(0).getElementsByTag("span").get(0).text();
                                Log.d(TAG, "name -> " + name);

                                /**
                                 *
                                 */
                                Elements sub_infos = li.getElementsByClass("sub_info");
                                if (sub_infos.size() == 0) {
                                    continue;
                                }
                                Elements if1s = sub_infos.get(0).getElementsByClass("if1");
                                String date = null;
                                String star = null;
                                for (Element if1 : if1s) {
                                    if (if1.classNames().contains("st_r")) {
                                        star = if1.text();
                                    } else {
                                        date = if1.text();
                                    }
                                }
                                Log.d(TAG, "date -> " + date);
                                Log.d(TAG, "star -> " + star);

                                if (star == null || date == null) {
                                    continue;
                                }

                                Episode episode = new Episode();
                                episode.setUrl(url);
                                episode.setImg_url(img_url);
                                episode.setStar(Double.parseDouble(star));
                                episode.setName(name.trim());
                                episode.setDate(date.trim());
                                episodes.add(episode);
                            }

                            return episodes;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        return null;
                    }

                });


        observable2.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        return observable2;
    }
}

package com.forasterisk.ilkeok.api;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by yearnning on 15. 10. 21..
 */
public interface ApiInterface {

    @GET("search/msearch.nhn")
    Observable<ResponseBody> WebtoonDetailApi(@Query("keyword") String keyword,
                                              @Query("searchType") String search_type,
                                              @Query("version") String version,
                                              @Query("page") String page);

    @GET("webtoon/list.nhn")
    Observable<ResponseBody> EpisodeListApi(@Query("titleId") String naver_id,
                                            @Query("page") int page);
}


package com.forasterisk.ilkeok.model;

/**
 * Created by joonyoung.yi on 2016. 7. 24..
 */
public class Webtoon {

    public static Webtoon newInstance(int id) {
        if (id == 1) {
            return newInstance(id,
                    "183559",
                    "신의 탑",
                    "SIU",
                    "http://thumb.comic.naver.net/webtoon/183559/thumbnail/title_thumbnail_20160516123017_t125x101.jpg");
        } else if (id == 2) {
            return newInstance(id,
                    "679519",
                    "대학일기",
                    "자까",
                    "http://thumb.comic.naver.net/webtoon/679519/thumbnail/title_thumbnail_20160601180804_t125x101.jpg");
        } else if (id == 3) {
            return newInstance(id,
                    "449854",
                    "돌아온 럭키짱",
                    "김성모",
                    "http://thumb.comic.naver.net/webtoon/449854/thumbnail/title_thumbnail_20120302192957_t125x101.jpg");
        }

        return null;
    }

    private static Webtoon newInstance(int id,
                                       String naver_id,
                                       String name,
                                       String writer,
                                       String img_url) {
        Webtoon webtoon = new Webtoon();
        webtoon.id = id;
        webtoon.naver_id = naver_id;
        webtoon.name = name;
        webtoon.writer = writer;
        webtoon.img_url = img_url;
        return webtoon;
    }

    public int id = -1;
    public String naver_id = "";
    public String name = "";
    public String writer = "";
    public String img_url = "";
    public double star = -1;


}

package com.forasterisk.ilkeok.model;

public class Episode {
    private static final String TAG = "Episode";

    /**
     *
     */
    private String url = "";
    private String img_url = "";
    private String name = "";
    private String date = "";
    private double star = 0;


    /**
     *
     */
    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Double getStar() {
        return star;
    }

    public void setStar(Double star) {
        this.star = star;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url.replace("//", "/");
    }

}

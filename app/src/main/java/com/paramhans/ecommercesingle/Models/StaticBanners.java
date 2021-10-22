package com.paramhans.ecommercesingle.Models;

public class StaticBanners
{
    private String id,banner_image;

    public StaticBanners(String id, String banner_image) {
        this.id = id;
        this.banner_image = banner_image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBanner_image() {
        return banner_image;
    }

    public void setBanner_image(String banner_image) {
        this.banner_image = banner_image;
    }
}

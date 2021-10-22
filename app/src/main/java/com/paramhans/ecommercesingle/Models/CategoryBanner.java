package com.paramhans.ecommercesingle.Models;

public class CategoryBanner
{
    private String cat_id,homepagebanner_id,image;

    public CategoryBanner(String cat_id, String homepagebanner_id, String image) {
        this.cat_id = cat_id;
        this.homepagebanner_id = homepagebanner_id;
        this.image = image;
    }

    public String getCat_id() {
        return cat_id;
    }

    public void setCat_id(String cat_id) {
        this.cat_id = cat_id;
    }

    public String getHomepagebanner_id() {
        return homepagebanner_id;
    }

    public void setHomepagebanner_id(String homepagebanner_id) {
        this.homepagebanner_id = homepagebanner_id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}

package com.paramhans.ecommercesingle.Models;

public class CategoriesBanner
{
    private String id,banner;

    public CategoriesBanner(String id, String banner) {
        this.id = id;
        this.banner = banner;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBanner() {
        return banner;
    }

    public void setBanner(String banner) {
        this.banner = banner;
    }
}

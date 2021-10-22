package com.paramhans.ecommercesingle.Models;

import java.util.ArrayList;

public class HomeCatSubCat
{
    private String cat_id,cat_name,cat_icon;
    private ArrayList<HomeSubCat> homeSubCats;

    public HomeCatSubCat(String cat_id, String cat_name, String cat_icon, ArrayList<HomeSubCat> homeSubCats) {
        this.cat_id = cat_id;
        this.cat_name = cat_name;
        this.cat_icon = cat_icon;
        this.homeSubCats = homeSubCats;
    }

    public String getCat_id() {
        return cat_id;
    }

    public void setCat_id(String cat_id) {
        this.cat_id = cat_id;
    }

    public String getCat_name() {
        return cat_name;
    }

    public void setCat_name(String cat_name) {
        this.cat_name = cat_name;
    }

    public String getCat_icon() {
        return cat_icon;
    }

    public void setCat_icon(String cat_icon) {
        this.cat_icon = cat_icon;
    }

    public ArrayList<HomeSubCat> getHomeSubCats() {
        return homeSubCats;
    }

    public void setHomeSubCats(ArrayList<HomeSubCat> homeSubCats) {
        this.homeSubCats = homeSubCats;
    }
}

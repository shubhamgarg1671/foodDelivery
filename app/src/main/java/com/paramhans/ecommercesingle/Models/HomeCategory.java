package com.paramhans.ecommercesingle.Models;

public class HomeCategory
{
    private   String cat_id,cat_name,cat_icon;

    public HomeCategory(String cat_id, String cat_name, String cat_icon) {
        this.cat_id = cat_id;
        this.cat_name = cat_name;
        this.cat_icon = cat_icon;
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
}

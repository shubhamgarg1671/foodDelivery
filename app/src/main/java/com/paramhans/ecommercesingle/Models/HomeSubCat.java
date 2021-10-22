package com.paramhans.ecommercesingle.Models;

public class HomeSubCat
{
    private String subcat_id,subcat_name,subcat_image,subcat_description;

    public HomeSubCat(String subcat_id, String subcat_name, String subcat_image, String subcat_description) {
        this.subcat_id = subcat_id;
        this.subcat_name = subcat_name;
        this.subcat_image = subcat_image;
        this.subcat_description = subcat_description;
    }

    public String getSubcat_id() {
        return subcat_id;
    }

    public void setSubcat_id(String subcat_id) {
        this.subcat_id = subcat_id;
    }

    public String getSubcat_name() {
        return subcat_name;
    }

    public void setSubcat_name(String subcat_name) {
        this.subcat_name = subcat_name;
    }

    public String getSubcat_image() {
        return subcat_image;
    }

    public void setSubcat_image(String subcat_image) {
        this.subcat_image = subcat_image;
    }

    public String getSubcat_description() {
        return subcat_description;
    }

    public void setSubcat_description(String subcat_description) {
        this.subcat_description = subcat_description;
    }
}

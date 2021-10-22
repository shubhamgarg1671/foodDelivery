package com.paramhans.ecommercesingle.Models;

public class PromoCode
{
    private String promocode_id,promocode_image,promocode_name,promocode_percenatge,max_amount,description;

    public PromoCode(String promocode_id, String promocode_image, String promocode_name, String promocode_percenatge, String max_amount, String description) {
        this.promocode_id = promocode_id;
        this.promocode_image = promocode_image;
        this.promocode_name = promocode_name;
        this.promocode_percenatge = promocode_percenatge;
        this.max_amount = max_amount;
        this.description = description;
    }

    public String getPromocode_id() {
        return promocode_id;
    }

    public void setPromocode_id(String promocode_id) {
        this.promocode_id = promocode_id;
    }

    public String getPromocode_image() {
        return promocode_image;
    }

    public void setPromocode_image(String promocode_image) {
        this.promocode_image = promocode_image;
    }

    public String getPromocode_name() {
        return promocode_name;
    }

    public void setPromocode_name(String promocode_name) {
        this.promocode_name = promocode_name;
    }

    public String getPromocode_percenatge() {
        return promocode_percenatge;
    }

    public void setPromocode_percenatge(String promocode_percenatge) {
        this.promocode_percenatge = promocode_percenatge;
    }

    public String getMax_amount() {
        return max_amount;
    }

    public void setMax_amount(String max_amount) {
        this.max_amount = max_amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

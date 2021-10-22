package com.paramhans.ecommercesingle.Models;

public class RateCard
{
    private String ratechat_name,ratechat_price;

    public RateCard(String ratechat_name, String ratechat_price) {
        this.ratechat_name = ratechat_name;
        this.ratechat_price = ratechat_price;
    }

    public String getRatechat_name() {
        return ratechat_name;
    }

    public void setRatechat_name(String ratechat_name) {
        this.ratechat_name = ratechat_name;
    }

    public String getRatechat_price() {
        return ratechat_price;
    }

    public void setRatechat_price(String ratechat_price) {
        this.ratechat_price = ratechat_price;
    }
}

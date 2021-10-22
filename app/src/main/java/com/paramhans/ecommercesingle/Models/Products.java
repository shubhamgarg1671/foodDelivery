package com.paramhans.ecommercesingle.Models;

public class Products
{
    public  String pid;
    public  String name;
    public  String description;
    public  String price;
    public  String discount_price;
    public  String image;

    public  Products(){}

    public Products(String pid, String name, String description, String price, String discount_price, String image) {
        this.pid = pid;
        this.name = name;
        this.description = description;
        this.price = price;
        this.discount_price = discount_price;
        this.image = image;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDiscount_price() {
        return discount_price;
    }

    public void setDiscount_price(String discount_price) {
        this.discount_price = discount_price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}

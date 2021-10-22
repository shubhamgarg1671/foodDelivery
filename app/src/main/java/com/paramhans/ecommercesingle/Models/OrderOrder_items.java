package com.paramhans.ecommercesingle.Models;

public class OrderOrder_items {
    private String id;
    private String qty;
    private String price;
    private String total_price;
    private String product_id;
    private String product_name;
    private String product_main_img;
    private String category_id;
    private String category_name;
    private String subcategory_id;
    private String subcategory_name;
    private String service_provider_id;
    private String service_provider_name;

    public OrderOrder_items(String id, String qty, String price, String total_price, String product_id, String product_name, String product_main_img, String category_id, String category_name, String subcategory_id, String subcategory_name, String service_provider_id, String service_provider_name) {
        this.id = id;
        this.qty = qty;
        this.price = price;
        this.total_price = total_price;
        this.product_id = product_id;
        this.product_name = product_name;
        this.product_main_img = product_main_img;
        this.category_id = category_id;
        this.category_name = category_name;
        this.subcategory_id = subcategory_id;
        this.subcategory_name = subcategory_name;
        this.service_provider_id = service_provider_id;
        this.service_provider_name = service_provider_name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getTotal_price() {
        return total_price;
    }

    public void setTotal_price(String total_price) {
        this.total_price = total_price;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getProduct_main_img() {
        return product_main_img;
    }

    public void setProduct_main_img(String product_main_img) {
        this.product_main_img = product_main_img;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public String getSubcategory_id() {
        return subcategory_id;
    }

    public void setSubcategory_id(String subcategory_id) {
        this.subcategory_id = subcategory_id;
    }

    public String getSubcategory_name() {
        return subcategory_name;
    }

    public void setSubcategory_name(String subcategory_name) {
        this.subcategory_name = subcategory_name;
    }

    public String getService_provider_id() {
        return service_provider_id;
    }

    public void setService_provider_id(String service_provider_id) {
        this.service_provider_id = service_provider_id;
    }

    public String getService_provider_name() {
        return service_provider_name;
    }

    public void setService_provider_name(String service_provider_name) {
        this.service_provider_name = service_provider_name;
    }
}

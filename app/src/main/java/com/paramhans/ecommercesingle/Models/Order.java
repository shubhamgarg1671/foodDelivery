package com.paramhans.ecommercesingle.Models;

import java.util.ArrayList;

public class Order {
    private String id;
    private String order_code;
    private String address_id;
    private Object order_amount;
    private String order_date;
    private String order_status;
    private ArrayList<OrderOrder_items> orderOrder_items = new ArrayList<>();

    public Order(String id, String order_code, String address_id, Object order_amount, String order_date, String order_status, ArrayList<OrderOrder_items> orderOrder_items) {
        this.id = id;
        this.order_code = order_code;
        this.address_id = address_id;
        this.order_amount = order_amount;
        this.order_date = order_date;
        this.order_status = order_status;
        this.orderOrder_items = orderOrder_items;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrder_code() {
        return order_code;
    }

    public void setOrder_code(String order_code) {
        this.order_code = order_code;
    }

    public String getAddress_id() {
        return address_id;
    }

    public void setAddress_id(String address_id) {
        this.address_id = address_id;
    }

    public Object getOrder_amount() {
        return order_amount;
    }

    public void setOrder_amount(Object order_amount) {
        this.order_amount = order_amount;
    }

    public String getOrder_date() {
        return order_date;
    }

    public void setOrder_date(String order_date) {
        this.order_date = order_date;
    }

    public String getOrder_status() {
        return order_status;
    }

    public void setOrder_status(String order_status) {
        this.order_status = order_status;
    }

    public ArrayList<OrderOrder_items> getOrderOrder_items() {
        return orderOrder_items;
    }

    public void setOrderOrder_items(ArrayList<OrderOrder_items> orderOrder_items) {
        this.orderOrder_items = orderOrder_items;
    }
}

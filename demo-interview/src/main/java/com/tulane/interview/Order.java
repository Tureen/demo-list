package com.tulane.interview;

import java.util.List;

public class Order implements Cloneable {

    private Long id;

    private String orderNo;

    private List<Item> itemList;

    @Override
    public Order clone() {
        try {
            return (Order)super.clone();
        } catch (CloneNotSupportedException ignore) {
            // 不会调到这里
        }
        return null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public List<Item> getItemList() {
        return itemList;
    }

    public void setItemList(List<Item> itemList) {
        this.itemList = itemList;
    }
}
package com.sb.guesthouse.model;

/**
 * Model class for Items which are being purchase.
 * @author Sharn25
 * @since 27-02-2021
 * @version 0.0
 */
public class Item {
    String itemName;
    String itemType;
    double itemAmount;
    String date;
    String user;

    public Item(String ItemName, String ItemType, double ItemAmount, String Date, String User){
        this.itemName = ItemName;
        this.itemType = ItemType;
        this.itemAmount = ItemAmount;
        this.date = Date;
        this.user = User;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public double getItemAmount() {
        return itemAmount;
    }

    public void setItemAmount(double itemAmount) {
        this.itemAmount = itemAmount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}

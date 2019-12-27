package com.tulane.filmrental.second;

import com.tulane.filmrental.second.price.ChildrensPrice;
import com.tulane.filmrental.second.price.NewReleasePrice;
import com.tulane.filmrental.second.price.Price;
import com.tulane.filmrental.second.price.RegularPrice;

/**
 * 影片类
 */
public class Movie {

    public static final int CHILDRENS = 2; // 儿童片
    public static final int REGULAR = 0; // 普通
    public static final int NEW_RELEASE = 1; // 新片

    private String _title;

    private Price _price;

    public Movie(String _title, int _priceCode) {
        this._title = _title;
        setPriceCode(_priceCode);
    }

    public void setPriceCode(int arg) {
        switch (arg){
            case REGULAR:
                _price = new RegularPrice();
                break;
            case CHILDRENS:
                _price = new ChildrensPrice();
                break;
            case NEW_RELEASE:
                _price = new NewReleasePrice();
                break;
            default:
                throw new IllegalArgumentException("Incorrect Price Code");
        }
    }

    public String getTitle() {
        return _title;
    }

    public double getCharge(int daysRented) {
        return _price.getCharge(daysRented);
    }

    public int getFrequentRenterPoints(int daysRented) {
        return _price.getFrequentRenterPoints(daysRented);
    }
}

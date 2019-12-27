package com.tulane.filmrental.first;

/**
 * 影片类
 */
public class Movie {

    public static final int CHILDRENS = 2; // 儿童片
    public static final int REGULAR = 0; // 普通
    public static final int NEW_RELEASE = 1; // 新片

    private String _title;
    private int _priceCode;

    public Movie(String _title, int _priceCode) {
        this._title = _title;
        this._priceCode = _priceCode;
    }

    public int getPriceCode(){
        return _priceCode;
    }

    public void setPriceCode(int arg) {
        this._priceCode = arg;
    }

    public String getTitle() {
        return _title;
    }
}

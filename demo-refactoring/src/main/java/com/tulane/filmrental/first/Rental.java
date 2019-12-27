package com.tulane.filmrental.first;

/**
 * 租赁类
 */
public class Rental {

    private Movie _movie;
    private int _daysRented; // 租赁天数

    public Rental(Movie _movie, int _daysRented) {
        this._movie = _movie;
        this._daysRented = _daysRented;
    }

    public int getDaysRented() {
        return _daysRented;
    }

    public Movie getMovie() {
        return _movie;
    }
}

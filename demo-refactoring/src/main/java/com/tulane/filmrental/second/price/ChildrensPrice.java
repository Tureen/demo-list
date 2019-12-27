package com.tulane.filmrental.second.price;

import com.tulane.filmrental.second.Movie;

public class ChildrensPrice extends Price {

    public int getPriceCode() {
        return Movie.CHILDRENS;
    }

    @Override
    public double getCharge(int daysRented) {
        return (daysRented - 3) * 1.5;
    }
}

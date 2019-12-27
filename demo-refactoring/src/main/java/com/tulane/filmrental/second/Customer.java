package com.tulane.filmrental.second;

import java.util.Vector;

/**
 * 顾客类
 */
public class Customer {

    private String _name;
    private Vector<Rental> _rentals = new Vector<Rental>(); // 租赁集合

    public Customer(String _name) {
        this._name = _name;
    }

    public void addRental(Rental arg) {
        _rentals.addElement(arg);
    }

    public String getName() {
        return _name;
    }

    /**
     * 生成详单
     *
     * @return
     */
    public String statement() {
        String result = "Rental Record for " + getName() + "\n";
        for (Rental rental : _rentals) {
            result += "\t" + rental.getMovie().getTitle() + "\t" + String.valueOf(rental.getCharge()) + "\n";
        }

        result += "Amount owed is " + String.valueOf(getTotalCharge()) + "\n";
        result += "You earned " + String.valueOf(getTotalFrequentRenterPoints()) + " frequent renter points";
        return result;
    }

    public String htmlStatement() {
        String result = "<H1>Rentals for <EM>" + getName() + "</EM></H1><P>\n";
        for (Rental rental : _rentals) {
            result += rental.getMovie().getTitle() + ": " + String.valueOf(rental.getCharge()) + "<BR>\n";
        }

        result += "<P>You owe <EM>" + String.valueOf(getTotalCharge()) + "</EM><P>\n";
        result += "On this rental you earned <EM>" + String.valueOf(getTotalFrequentRenterPoints()) + "</EM> frequent rebter points<P>";
        return result;
    }

    private double  getTotalCharge() {
        double result = 0;
        for (Rental rental : _rentals) {
            result += rental.getCharge();
        }
        return result;
    }

    private int getTotalFrequentRenterPoints() {
        int result = 0;
        for (Rental rental : _rentals) {
            result += rental.getFrequentRenterPoints();
        }
        return result;
    }

}

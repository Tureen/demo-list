package com.tulane.filmrental.second;

import com.google.common.collect.Lists;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class CustomerTest {

    private List<Customer> customers = Lists.newArrayList();

    @Before
    public void setUp() throws Exception {
        Movie inception = new Movie("盗梦空间", Movie.NEW_RELEASE);
        Movie lifeOfPi = new Movie("少年派的奇幻漂流", Movie.REGULAR);
        Movie zootopia = new Movie("疯狂动物城", Movie.CHILDRENS);

        Rental tualenFirstRental = new Rental(inception, 3);
        Rental tualenSecondRental = new Rental(lifeOfPi, 7);
        Rental tualenThirdRental = new Rental(zootopia, 2);

        Rental tonyFirstRental = new Rental(inception, 9);
        Rental tonySecondRental = new Rental(lifeOfPi, 5);
        Rental tonyThirdRental = new Rental(inception, 1);

        Customer tulane = new Customer("tulane");
        Customer tony = new Customer("tony");

        customers.add(tulane);
        customers.add(tony);

        tulane.addRental(tualenFirstRental);
        tulane.addRental(tualenSecondRental);
        tulane.addRental(tualenThirdRental);

        tony.addRental(tonyFirstRental);
        tony.addRental(tonySecondRental);
        tony.addRental(tonyThirdRental);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testStatement(){
        StringBuilder strb = new StringBuilder();
        for (Customer customer : customers) {
            strb.append(customer.statement());
        }

        String example = "Rental Record for tulane\n" +
                "\t盗梦空间\t9.0\n" +
                "\t少年派的奇幻漂流\t9.5\n" +
                "\t疯狂动物城\t-1.5\n" +
                "Amount owed is 17.0\n" +
                "You earned 4 frequent renter pointsRental Record for tony\n" +
                "\t盗梦空间\t27.0\n" +
                "\t少年派的奇幻漂流\t6.5\n" +
                "\t盗梦空间\t3.0\n" +
                "Amount owed is 36.5\n" +
                "You earned 4 frequent renter points";

        assert strb.toString().equals(example);
    }

    @Test
    public void testHtmlStatement() {
        StringBuilder strb = new StringBuilder();
        for (Customer customer : customers) {
            strb.append(customer.htmlStatement());
        }
        String example = "<H1>Rentals for <EM>tulane</EM></H1><P>\n" +
                "盗梦空间: 9.0<BR>\n" +
                "少年派的奇幻漂流: 9.5<BR>\n" +
                "疯狂动物城: -1.5<BR>\n" +
                "<P>You owe <EM>17.0</EM><P>\n" +
                "On this rental you earned <EM>4</EM> frequent rebter points<P><H1>Rentals for <EM>tony</EM></H1><P>\n" +
                "盗梦空间: 27.0<BR>\n" +
                "少年派的奇幻漂流: 6.5<BR>\n" +
                "盗梦空间: 3.0<BR>\n" +
                "<P>You owe <EM>36.5</EM><P>\n" +
                "On this rental you earned <EM>4</EM> frequent rebter points<P>";

        assert strb.toString().equals(example);
    }
}
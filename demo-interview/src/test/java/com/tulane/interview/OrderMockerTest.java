package com.tulane.interview;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class OrderMockerTest {

    @Test
    public void shallowClone() {
        Order order = OrderMocker.mock();
        Order cloneOrder = order.clone();

        assertFalse(order == cloneOrder);
        assertTrue(order.getItemList() == cloneOrder.getItemList());
    }
}
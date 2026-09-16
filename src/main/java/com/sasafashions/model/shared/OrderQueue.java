package com.sasafashions.model.shared;

import com.sasafashions.model.Order;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Queue;

/**
 * Manages active tailoring orders in first-in,
 * first-out order.
 *
 * Queue is the third major data structure used by
 * the Sasa Fashions application.
 *
 * @author HP
 * @version 1.0
 */
public class OrderQueue {

    private final Queue<Order> pendingOrders =
            new ArrayDeque<>();

    /**
     * Loads active orders into the queue.
     *
     * @param orders orders retrieved from the database
     */
    public void loadOrders(
            ArrayList<Order> orders
    ) {

        pendingOrders.clear();

        for (Order order : orders) {

            if (order.isActive()) {
                pendingOrders.offer(order);
            }
        }
    }

    /**
     * Adds an active order to the back of the queue.
     *
     * @param order order to add
     * @return true when added
     */
    public boolean addOrder(Order order) {

        if (order == null || !order.isActive()) {
            return false;
        }

        return pendingOrders.offer(order);
    }

    /**
     * Views the next order without removing it.
     *
     * @return next Order, or null when empty
     */
    public Order viewNextOrder() {
        return pendingOrders.peek();
    }

    /**
     * Removes and returns the next order.
     *
     * @return next Order, or null when empty
     */
    public Order removeNextOrder() {
        return pendingOrders.poll();
    }

    /**
     * Returns a copy of the current queue.
     *
     * @return queued orders as an ArrayList
     */
    public ArrayList<Order> getQueueSnapshot() {

        return new ArrayList<>(pendingOrders);
    }

    public int size() {
        return pendingOrders.size();
    }

    public boolean isEmpty() {
        return pendingOrders.isEmpty();
    }

    public void clear() {
        pendingOrders.clear();
    }
}
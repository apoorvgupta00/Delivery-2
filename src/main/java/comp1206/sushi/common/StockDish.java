package comp1206.sushi.common;

import comp1206.sushi.server.Server;

import java.util.*;

public class StockDish {
    /**
     * Member Variables for the class
     */
    private Map<Dish, Number> d;
    private Server server;
    private Number restockAmount;
    private Number restockThreshold;
    private Dish dishes;

    /**
     * Constructor for the class
     * @param server
     */

    public StockDish(Server server) {
        this.d = new HashMap<>();
        this.server=server;
    }

    /**
     * getDishes with stock
     * @return
     */
    public Map<Dish, Number> dishesStock() {
        return d;
    }

    /**
     * Insert the stock for a dish
     * @param di
     * @param r
     */
    public synchronized void insertIntoDs(Dish di, Integer r) {
        d.put(di, r);
    }

    /**
     * Set the restock amount for a dish
     * @param d
     * @param restockAmount
     */
    public void setRestockAmountDish(Dish d,Number restockAmount) {
        d.setRestockAmount(restockAmount);
    }

    /**
     * Return the restock amount for a dish
     * @param d
     * @return
     */
    public Number getRestockAmountDish(Dish d) {
        return d.getRestockAmount();
    }

    /**
     * Set the restock threshold for a dish
     * @param d
     * @param restockThreshold
     */
    public void setRestockThresholdDish(Dish d, Number restockThreshold) {
        d.setRestockThreshold(restockThreshold);
    }

    /**
     * Return the restock threshold for a dish
     * @param d
     * @return
     */
    public Number getRestockThresholdDish(Dish d) {
        return d.getRestockThreshold();
    }
}

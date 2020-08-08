package comp1206.sushi.common;

import comp1206.sushi.server.Server;

import java.util.*;

public class StockManagementForIng{
    /**
     * Member variables for class
     */
    private Map<Ingredient, Number> ing;
    private Server server;

    /**
     * Constructor for the class
     * @param server
     */
    public StockManagementForIng(Server server) {
        this.ing = new HashMap<>();
        this.server=server;
    }

    /**
     * returns the map
     * @return
     */
    public Map<Ingredient, Number> ingredientsStock() {
        return ing;
    }

    /**
     * Insert the stock for a dish
     * @param ingr
     * @param r
     */

    public synchronized void insertIntoIg(Ingredient ingr, Integer r) {
        ing.put(ingr, r);
    }

    /**
     * Set the restock amount for an ingredient
     * @param d
     * @param restockAmount
     */
    public void setRestockAmountIng(Ingredient d,Number restockAmount) {
        d.setRestockAmount(restockAmount);
    }

    /**
     * Return the restock amount for ingredient
     * @param d
     * @return
     */
    public Number getRestockAmountIng(Ingredient d) {
        return d.getRestockAmount();
    }

    /**
     * Set the Restock Threshold for Ingredient
     * @param d
     * @param restockThreshold
     */
    public void setRestockThresholdIng(Ingredient d, Number restockThreshold) {
        d.setRestockThreshold(restockThreshold);
    }

    /**
     * Return the Restock Threshold for Ingredient
     * @param d
     * @return
     */
    public Number getRestockThresholdIng(Ingredient d) {
        return d.getRestockThreshold();
    }

}

package comp1206.sushi.common;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Dish extends Model implements Serializable {

	//MEMBER VARIABLES DEFINED

	private String name;
	private String description;
	private Number price;
	private Map <Ingredient,Number> recipe;
	private Number restockThreshold;
	private Number restockAmount;
	private comp1206.sushi.common.Lock lock = new comp1206.sushi.common.Lock();

	/**
	 *Constructor for the Dish Class
	 * @param name
	 * @param description
	 * @param price
	 * @param restockThreshold
	 * @param restockAmount
	 */


	public Dish(String name, String description, Number price, Number restockThreshold, Number restockAmount) {
		this.setName(name);
		this.description = description;
		this.price = price;
		this.restockThreshold = restockThreshold;
		this.restockAmount = restockAmount;
		this.recipe = new HashMap<Ingredient,Number>();

	}

	/**
	 * Returns the name of dish
	 * @return
	 */

	public String getName() {
		return name;
	}

	/**
	 * Sets the name of the Dish
	 * @param name model name
	 */

	public void setName(String name) {
		notifyUpdate("name",this.name,name);
		this.name = name;
	}

	/**
	 * Returns the description of Dish
	 * @return
	 */

	public String getDescription() {
		return description;
	}

	/**
	 * Sets the Description of the Dish
	 * @param description
	 */

	public void setDescription(String description) {
		notifyUpdate("description",this.description,description);
		this.description = description;
	}

	/**
	 * Returns the price of the Dish
	 * @return
	 */

	public Number getPrice() {
		return price;
	}

	/**
	 * Sets the price of the Dish
	 * @param price
	 */

	public void setPrice(Number price) {
		notifyUpdate("price",this.price,price);
		this.price = price;
	}

	/**
	 * Returns the Recipe of the Dish
	 * @return
	 */

	public Map <Ingredient,Number> getRecipe() {
		return recipe;
	}

	/**
	 * Sets the Recipe of the Dish
	 * @param recipe
	 */
	public void setRecipe(Map <Ingredient,Number> recipe) {
		notifyUpdate("recipe",this.recipe,recipe);
		this.recipe = recipe;
	}

	/**
	 * Sets the Restock Threshold of the Dish
	 * @param restockThreshold
	 */

	public void setRestockThreshold(Number restockThreshold) {
		notifyUpdate("restockThreshold",this.restockThreshold,restockThreshold);
		this.restockThreshold = restockThreshold;
	}

	/**
	 * Sets the Restock Amount of the Dish
	 * @param restockAmount
	 */

	public void setRestockAmount(Number restockAmount) {
		notifyUpdate("restockAmount",this.restockAmount,restockAmount);
		this.restockAmount = restockAmount;
	}

	/**
	 * Returns the Restock Threshold of the Dish
	 * @return
	 */

	public Number getRestockThreshold() {
		return this.restockThreshold;
	}

	/**
	 * Returns the Restock Amount of Dish
	 * @return
	 */

	public Number getRestockAmount() {
		return this.restockAmount;
	}

	/**
	 * Returns the lock;
	 * @return
	 */

	public comp1206.sushi.common.Lock pLock() {
		return lock;
	}



}

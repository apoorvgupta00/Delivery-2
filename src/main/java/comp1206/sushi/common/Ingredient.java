package comp1206.sushi.common;

import comp1206.sushi.common.Ingredient;
import comp1206.sushi.common.Supplier;

import java.io.Serializable;
import java.util.concurrent.locks.ReentrantLock;
public class Ingredient extends Model implements Serializable {
	/**
	 * Member Variables for the class Ingredient
	 */
	private String name;
	private String unit;
	private Supplier supplier;
	protected Number restockThreshold;
	protected Number restockAmount;
	protected Number weight;
	//protected Number  stock;
	private comp1206.sushi.common.Lock lock = new comp1206.sushi.common.Lock();

	/**
	 * Constructor for the Class Ingredient
	 * @param name name of the Ingredient
	 * @param unit unit of the Ingredient
	 * @param supplier supplier of the Ingredient
	 * @param restockThreshold restockThreshold of the Ingredient
	 * @param restockAmount restockAmount of the Ingredient
	 * @param weight weight of the Ingredient
	 */
	public Ingredient(String name, String unit, Supplier supplier, Number restockThreshold,
					  Number restockAmount, Number weight) {
		this.setName(name);
		this.setUnit(unit);
		this.setSupplier(supplier);
		this.setRestockThreshold(restockThreshold);
		this.setRestockAmount(restockAmount);
		this.setWeight(weight);
		//this.stock=0;
	}

	/**
	 * Returns the name
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of Ingredient
	 * @param name model name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 *
	 * @return the Unit of Ingredient
	 */
	public String getUnit() {
		return unit;
	}

	/**
	 * Sets the unit of Ingredient
	 * @param unit Unit of Ingredient
	 */
	public void setUnit(String unit) {
		this.unit = unit;
	}

	/**
	 * Returns the supplier
	 * @return supplier
	 */
	public Supplier getSupplier() {
		return supplier;
	}

	/**
	 * Set the supplier for the Ingredient
	 * @param supplier supplier of the Ingredient
	 */
	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	/**
	 * Returns Restock Threshold
	 * @return restock threshold
	 */
	public Number getRestockThreshold() {
		return restockThreshold;
	}

	/**
	 * Sets the Restock Threshold
	 * @param restockThreshold
	 */
	public void setRestockThreshold(Number restockThreshold) {
		this.restockThreshold = restockThreshold;
	}

	/**
	 *
	 * @return Restock Amount of ingredient
	 */
	public Number getRestockAmount() {
		return restockAmount;
	}

	/**
	 *
	 * @param restockAmount Sets the Restock Amount
	 */
	public void setRestockAmount(Number restockAmount) {
		this.restockAmount = restockAmount;
	}

	/**
	 *
	 * @return Weight of the Ingredient
	 */
	public Number getWeight() {
		return weight;
	}

	public Lock pLock() {
		return lock;
	}

	/**
	 *
	 * @param weight Weight of the Ingredient
	 */
	public void setWeight(Number weight) {
		this.weight = weight;
	}


}

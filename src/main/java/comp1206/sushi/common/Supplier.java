package comp1206.sushi.common;

import comp1206.sushi.common.Supplier;

import java.io.Serializable;

public class Supplier extends Model implements Serializable {
	/**
	 * Member Variables for the class
	 */
	private String name;
	private Postcode postcode;
	private Number distance;

	/**
	 * Constructor for the class
	 * @param name
	 * @param postcode
	 */
	public Supplier(String name, Postcode postcode) {
		this.name = name;
		this.postcode = postcode;

	}

	/**
	 *
	 * @return name of the supplier
	 */
	public String getName() {
		return name;
	}

	/**
	 *
	 * @param name model name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 *
	 * @return postcode of the supplier
	 */
	public Postcode getPostcode() {
		return this.postcode;
	}

	/**
	 * Sets the postcode
	 * @param postcode
	 */
	public void setPostcode(Postcode postcode) {
		this.postcode = postcode;
	}

	/**
	 *
	 * @return the distance of the postcode
	 */
	public Number getDistance() {
		return postcode.getDistance();
	}

	/**
	 * Sets the distance of the supplier
	 * @param postcode
	 */
	public void setDistance(Postcode postcode){
		this.distance=postcode.getDistance();
	}

}

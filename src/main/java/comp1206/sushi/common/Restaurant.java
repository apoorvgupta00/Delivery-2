package comp1206.sushi.common;

public class Restaurant {
	/**
	 * Member Variables for the class
	 */
	private String name;
	private Postcode location;

	/**
	 * Constructor of the class
	 * @param name
	 * @param location
	 */
	public Restaurant(String name, Postcode location) {
		this.name = name;
		this.location = location;
	}

	/**
	 * Returns the name
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the name of restaurant
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns the location of Restaurant
	 * @return
	 */
	public Postcode getLocation() {
		return this.location;
	}

	/**
	 * Set the Location of the Restaurant
	 * @param location
	 */
	public void setLocation(Postcode location) {
		this.location = location;
	}

}

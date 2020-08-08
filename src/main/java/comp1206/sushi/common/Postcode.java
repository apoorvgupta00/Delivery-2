package comp1206.sushi.common;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Postcode extends Model implements Serializable {
	/**
	 * Member Variables of the class
	 */
	private String name;
	private Map<String,Double> latLong;
	private Number distance;

	/**
	 * Constructor for the class
	 * @param code code of the postcode
	 */
	public Postcode(String code) {
		this.name = code;
		calculateLatLong();
		this.distance = Integer.valueOf(100);
		//try{
	//	MyGETRequest();}catch (IOException e){}
	}

	/**
	 * Overloaded Constructor for the class
	 * @param code code of the postcode
	 * @param restaurant
	 */
	public Postcode(String code, Restaurant restaurant) {
		this.name = code;
		calculateLatLong();
		calculateDistance(restaurant);
	}

	/**
	 *
	 * @return name of postcode
	 */
	@Override
	public String getName() {
		return this.name;
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
	 * @return distance of the postcode
	 */
	public Number getDistance() {
		return this.distance;
	}

	/**
	 *
	 * @return longitude and latitude
	 */
	public Map<String,Double> getLatLong() {
		return this.latLong;
	}

	/**
	 *
	 * @param restaurant calculates the distance from the restaurant
	 */
	protected void calculateDistance(Restaurant restaurant) {
		//This function needs implementing
		Postcode destination = restaurant.getLocation();
		this.distance = Integer.valueOf(0);
	}

	protected void calculateLatLong() {
		//This function needs implementing
		this.latLong = new HashMap<String,Double>();
		latLong.put("lat", 0d);
		latLong.put("lon", 0d);
		this.distance = new Integer(0);
	}

	/**
	 *
	 * @return code
	 */
	public String getCode() {
		return name;
	}

}

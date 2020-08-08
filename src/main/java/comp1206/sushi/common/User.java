package comp1206.sushi.common;

import comp1206.sushi.common.Postcode;
import comp1206.sushi.common.User;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
public class User extends Model implements Serializable{
	/**
	 * Member Variables for class
	 */
	private String name;
	private String password;
	private String address;
	private Postcode postcode;
	private HashMap<Dish, Number> basket;

	/**
	 * Constructor for the class
	 * @param username
	 * @param password
	 * @param address
	 * @param postcode
	 */

	public User(String username, String password, String address, Postcode postcode) {
		this.name = username;
		this.password = password;
		this.address = address;
		this.postcode = postcode;
		this.basket = new HashMap<>();
	}

	/**
	 * clear everything from the basket
	 */
	public void clearBasket(){
		basket=new HashMap<>();
	}

	/**
	 * returns the name
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 *Sets the name
	 * @param name model name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 *
	 * @return distance of the user
	 */
	public Number getDistance() {
		return postcode.getDistance();
	}

	/**
	 *
	 * @return postcode of the user
	 */
	public Postcode getPostcode() {
		return this.postcode;
	}

	/**
	 *
	 * @return basket of the user
	 */
	public  HashMap<Dish, Number> getBasket() {
		return basket;
	}

	/**
	 *
	 * @param postcode of the user
	 */
	public void setPostcode(Postcode postcode) {
		this.postcode = postcode;
	}

	/**
	 * adds to the basket
	 * @param dish
	 * @param quantity
	 */
	public void basketForClient(Dish dish, Number quantity) {
		basket.put(dish, quantity);
	}

	/**
	 *
	 * @return password of the user
	 */
	public String getPassword() {
		return password;
	}

	/**
	 *
	 * @param basket of the user
	 */
	public void setBasket(HashMap<Dish, Number> basket) {
		notifyUpdate("Dishes",this.basket,basket);
		this.basket = basket;
	}

	/**
	 *
	 * @return address of the user
	 */
	public String getAddress(){
		return address;
	}
}

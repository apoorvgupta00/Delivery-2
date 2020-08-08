package comp1206.sushi.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import comp1206.sushi.common.Dish;
import comp1206.sushi.common.Order;
import comp1206.sushi.common.Postcode;
import comp1206.sushi.common.Restaurant;
import comp1206.sushi.common.UpdateListener;
import comp1206.sushi.common.*;
import java.io.*;

import java.util.HashMap;

public class Client extends Model implements ClientInterface,Serializable {


	private static final Logger logger = LogManager.getLogger("Client");
	/**
	 * Member Variables for the class
	 */
	public Restaurant restaurant;
	private UserConnect userGet;
	private ArrayList<Dish> dishArrayList;
	private ArrayList<Postcode> postcodeArrayList;
	private ArrayList<Order> orderArrayList;
	private ArrayList<UpdateListener> listeners = new ArrayList<UpdateListener>();
	/**
	 * Constructor for the class
	 */
	public Client() {
        logger.info("Starting up client...");

		userGet = new UserConnect();
		dishArrayList = new ArrayList<>();
		orderArrayList = new ArrayList<>();

	}

	/**
	 * Return the name
	 * @return
	 */
	public  String getName(){
		return null;
	}

	/**
	 * Return the Restaurant
	 * @return
	 */
	@Override
	public Restaurant getRestaurant() {
		return this.restaurant;
	}

	/**
	 * Return  the name of the Restaurant
	 * @return
	 */
	@Override
	public String getRestaurantName() {
		return restaurant.getName();
	}

	/**
	 * Return the postcode of the Restaurant
	 * @return
	 */
	@Override
	public Postcode getRestaurantPostcode() {
		return restaurant.getLocation();
	}

	/**
	 * Register new User
	 * @param username username
	 * @param password password
	 * @param address address
	 * @param postcode valid postcode
	 * @return
	 */
	@Override
	public User register(String username, String password, String address, Postcode postcode) {

		User newC = new User(username, password, address, postcode);

		CommandFor newClient = new CommandFor("NewClient", newC);
		userGet.sendMessage(newClient);

		CommandFor getting = userGet.receiveMessage();
		if (getting.getProp().equals("New"))
			return newC;
		else return null;


	}

	/**
	 * Allows client to Login
	 * @param username username
	 * @param password password
	 * @return
	 */
	@Override
	public User login(String username, String password) {

		User loginClient = null;

		Comms2 u1 = new Comms2(username, password);
		CommandFor send = new CommandFor("PreviousRegistered", u1);

		userGet.sendMessage(send);
		CommandFor getting = userGet.receiveMessage();

		if (getting.getProp().equals("Win")) {
			loginClient = (User) getting.geto1();
		}

		return loginClient;
	}

	/**
	 * Returns all the postcodes
	 * @return
	 */
	@Override
	public synchronized List<Postcode> getPostcodes() {

		CommandFor p = new CommandFor("PutPostcode", null);
		userGet.sendMessage(p);

		CommandFor getting = userGet.receiveMessage();

		postcodeArrayList = (ArrayList<Postcode>) getting.geto1();
		this.notifyUpdate();
		return (ArrayList<Postcode>) getting.geto1();

	}

	/**
	 * Returns all the dishes
	 * @return
	 */
	@Override
	public synchronized List<Dish> getDishes() {

		CommandFor d = new CommandFor("ServerDish", null);
		userGet.sendMessage(d);

		CommandFor getting = userGet.receiveMessage();

		dishArrayList = (ArrayList<Dish>) getting.geto1();
		this.notifyUpdate();
		return (ArrayList<Dish>) getting.geto1();

	}


	/**
	 * Returns the Description of the dish
	 * @param dish Dish to lookup
	 * @return
	 */
	@Override
	public synchronized String getDishDescription(Dish dish) {
		this.notifyUpdate();
		return dish.getDescription();
	}

	/**
	 * Returns the price of the dish
	 * @param dish Dish to lookup
	 * @return
	 */
	@Override
	public synchronized Number getDishPrice(Dish dish) {
		this.notifyUpdate();
		return dish.getPrice();

	}

	/**
	 * Gets the basket of the client
	 * @param user user to lookup
	 * @return
	 */
	@Override
	public Map<Dish, Number> getBasket(User user) {


		CommandFor getB = new CommandFor("ClientB", user);
		userGet.sendMessage(getB);

		CommandFor getting = userGet.receiveMessage();

		if (getting.getProp().equals("True")) {

			HashMap<Dish, Number> basket = (HashMap<Dish, Number>) getting.geto1();
			this.notifyUpdate();
			return basket;

		} else return null;


	}

	/**
	 * Returns the cost of the basket
	 * @param user user to lookup basket
	 * @return
	 */
	@Override
	public Number getBasketCost(User user) {
		CommandFor getBC = new CommandFor("ClientBC", user);
		userGet.sendMessage(getBC);

		CommandFor getting = userGet.receiveMessage();

		if (getting.getProp().equals("True")) {
			this.notifyUpdate();
			return (Integer) getting.geto1();

		} else return null;

	}

	/**
	 *  Adds the dishes to the basket
	 * @param user user of basket
	 * @param dish dish to change
	 * @param quantity quantity to set
	 */
	@Override
	public void addDishToBasket(User user, Dish dish, Number quantity) {

		String addB = user.getName() + "|" + dish.getName() + "|" + quantity;
		CommandFor set = new CommandFor("ClientAddDB", addB);
		userGet.sendMessage(set);

		CommandFor getting = userGet.receiveMessage();
		this.notifyUpdate();

	}


	/**
	 * Updates the dishes in the basket
	 * @param user user of basket
	 * @param dish dish to change
	 * @param quantity quantity to set. 0 should remove.
	 */
	@Override
	public void updateDishInBasket(User user, Dish dish, Number quantity) {

		CommandFor upDB = new CommandFor("ClientUB", user, dish, quantity);
		userGet.sendMessage(upDB);

		CommandFor getting = userGet.receiveMessage();
		this.notifyUpdate();



	}

	/**
	 * Allows to checkout
	 * @param user user of basket
	 * @return
	 */
	@Override
	public Order checkoutBasket(User user) {

		CommandFor ckB = new CommandFor("ClientCheckout", user);
		userGet.sendMessage(ckB);
		CommandFor getting = userGet.receiveMessage();
		if (getting.getProp().equals("True")) {
			this.notifyUpdate();
			return (Order) getting.geto1();
		}

		return null;
	}

	/**
	 * remove the Dishes from the Basket
	 * @param user user of basket
	 */
	@Override
	public void clearBasket(User user) {

		CommandFor clB = new CommandFor("ClearBasket", user);
		userGet.sendMessage(clB);
		CommandFor getting = userGet.receiveMessage();
		this.notifyUpdate();

	}

	/**
	 * Returns the Orders
	 * @param user user
	 * @return
	 */
	@Override
	public synchronized List<Order> getOrders(User user) {
		CommandFor getO = new CommandFor("BringOrd", user);
		userGet.sendMessage(getO);

		CommandFor getting = userGet.receiveMessage();
		orderArrayList = (ArrayList<Order>) getting.geto1();

		this.notifyUpdate();
		return orderArrayList;

	}

	/**
	 * check if the order is complete
	 * @param order order
	 * @return
	 */
	@Override
	public boolean isOrderComplete(Order order) {

		CommandFor isC = new CommandFor("ClientOrdComplete", order);
		userGet.sendMessage(isC);
		CommandFor gettinge = userGet.receiveMessage();
		this.notifyUpdate();
		return (Boolean) gettinge.geto1();
	}

	/**
	 *Returns the status of the order
	 * @param order order to lookup
	 * @return
	 */
	@Override
	public String getOrderStatus(Order order) {

		CommandFor getOS = new CommandFor("ClientOrdSt", order);
		userGet.sendMessage(getOS);
		CommandFor getting = userGet.receiveMessage();
		this.notifyUpdate();
		return (String) getting.geto1();
	}

	/**
	 * Returns the cost of the order
	 * @param order to lookup
	 * @return
	 */
	@Override
	public Number getOrderCost(Order order) {
		CommandFor getOC = new CommandFor("ClientOC", order);
		userGet.sendMessage(getOC);

		CommandFor getting = userGet.receiveMessage();
		this.notifyUpdate();
		return (Number) getting.geto1();

	}

	/**
	 * Allows to cancel the order
	 * @param order to cancel
	 */
	@Override
	public void cancelOrder(Order order) {
		CommandFor cO = new CommandFor("RemoveOrd", order);
		userGet.sendMessage(cO);

		CommandFor getting = userGet.receiveMessage();
		this.notifyUpdate();
	}

	/**
	 * Adds update listeners
	 * @param listener An update listener to be informed of all model changes.
	 */
	@Override
	public void addUpdateListener(UpdateListener listener) {

		this.listeners.add(listener);


	}

	@Override
	public void notifyUpdate() {
//		this.listeners.forEach(listener -> listener.updated(new UpdateEvent()));
	}


}

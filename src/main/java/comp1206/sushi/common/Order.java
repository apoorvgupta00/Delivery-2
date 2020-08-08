package comp1206.sushi.common;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.*;
import comp1206.sushi.common.Order;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class Order extends Model implements Serializable {
	/**
	 * Member Variables for the class
	 */
	private String name, status;
	private Number distance, cost;
	private HashMap<Dish, Number> basket;
	private String orderWrite;
	private Boolean orderComplete;
	private User user;
	private Integer serialise;
	private comp1206.sushi.common.Lock lock = new comp1206.sushi.common.Lock();
	/**
	 * Constructor for the order
	 * @param user
	 * @param basket
	 * @param orderWrite
	 * @param serialise
	 */

	public Order(Integer serialise,User user,String orderWrite,HashMap<Dish,Number> basket) {
		this.user = user;
		this.orderWrite = orderWrite;
		this.name=user.getName();
		this.setName(user.getName());
		this.basket = basket;
		status = "Waiting for Dishes";
		this.cost = costofTheB();
		this.distance=user.getPostcode().getDistance();
		this.serialise=serialise;
		orderComplete = false;
	}
	public Order() {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/YYYY HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();
		this.name = dtf.format(now);
	}

	/**
	 * Returns Serialise number
	 * @return
	 */
	public Integer serialiseNumber(){
		return serialise;
	}

	/**
	 * Returns the cost of the basket
	 * @return
	 */
	private Integer costofTheB() {

		int c = 0;
		for (Map.Entry<Dish, Number> entry : basket.entrySet()) {

			c += (int)entry.getKey().getPrice() * (Integer) entry.getValue();
		}

		return c;

	}

	/**
	 * Returns the order in writable format
	 * @return
	 */
	public String orderInWriteFormat(){
		return orderWrite;
	}

	/**
	 * Returns if the order is complete
	 * @return
	 */
	public synchronized Boolean getOrderComplete() {
		return orderComplete;
	}

	/**
	 * Returns the user
	 * @return
	 */
	public synchronized User getUser() {
		return user;
	}

	/**
	 * Set the user of the order
	 * @param user
	 */
	public synchronized void setUser(User user) {
		this.user = user;
	}

	/**
	 * Returns the name of the Order
	 * @return
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * Returns the distance to the order
	 * @return
	 */
	public Number getDistance() {
		return distance;
	}

	/**
	 * Returns the Status of the order
	 * @return
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * Returns the cost of the basket
	 * @return
	 */
	public Number getCost() {
		return cost;
	}

	/**
	 * set the basket of the order
	 * @param basket
	 */
	public void setBasket(HashMap<Dish,Number> basket){
		notifyUpdate("Order", this.basket, basket);
		this.basket=basket;
	}

	/**
	 * Sets the status of order complete
	 * @param orderComplete
	 */
	public synchronized void setOrderComplete(Boolean orderComplete) {
		this.orderComplete = orderComplete;
	}

	/**
	 * returns the basket
	 * @return
	 */
	public HashMap<Dish, Number> getBasket() {
		return basket;
	}

	/**
	 * Lock for Synchronization
	 * @return
	 */
	public comp1206.sushi.common.Lock pLock() {
		return lock;
	}

	/**
	 * Set the status of the order
	 * @param status
	 */
	public void setStatus(String status) {
		notifyUpdate("status", this.status, status);
		this.status = status;
	}

}

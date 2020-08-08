package comp1206.sushi.server;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import comp1206.sushi.common.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Server implements ServerInterface, Serializable {

	///////MEMBER VARIABLES////////////////////////////

	private static final Logger logger = LogManager.getLogger("Server");
	private Comms c;
	public Restaurant restaurant;
	public Supplier sup;
	private ArrayList<Dish> dishes = new ArrayList<Dish>();
	private ArrayList<Drone> drones = new ArrayList<Drone>();
	private ArrayList<Ingredient> ingredients = new ArrayList<Ingredient>();
	private ArrayList<Order> orders = new ArrayList<Order>();
	private ArrayList<Staff> staff = new ArrayList<Staff>();
	private ArrayList<Supplier> suppliers = new ArrayList<Supplier>();
	private ArrayList<User> users = new ArrayList<User>();
	private ArrayList<Postcode> postcodes = new ArrayList<Postcode>();
	private ArrayList<UpdateListener> listeners = new ArrayList<UpdateListener>();
	private ArrayList<Restaurant> res = new ArrayList<Restaurant>();
	private Configuration conf;
	private boolean rIngreAllow = true;
	private boolean rDishAllow = true;
	private HashMap<String, User> feasibleU= new HashMap<>();
	private HashMap<String, Integer> serialNumber;
	private StockManagementForIng stockManagementForIng;
	private StockDish stockDish;
	private String aname;
	DataPersistence dataPersistence;

	/**
	 * Constructor
	 */
	public Server() {

		logger.info("Starting up server...");

		serialNumber = new HashMap<>();
		stockManagementForIng= new StockManagementForIng(this);
		stockDish = new StockDish(this);

		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				dataPersistence.newConfig();
			}
		});

		Comms c = new Comms(this);
		new Thread(c).start();
	}
	/////////////////DISHES///////////////////////
	@Override
	public ArrayList<Dish> getDishes() {
		return this.dishes;
	}

	/**
	 * Adds Dish to the server
	 * @param name name of dish
	 * @param description description of dish
	 * @param price price of dish
	 * @param restockThreshold minimum threshold to reach before restocking
	 * @param restockAmount amount to restock by
	 * @return
	 */
	@Override
	public Dish addDish(String name, String description, Number price, Number restockThreshold, Number restockAmount) {
		Dish d=new Dish(name,description,price,restockThreshold,restockAmount);
		this.dishes.add(d);
		this.stockDish.dishesStock().put(d, 0);
		this.stockDish.insertIntoDs(d,0);
		this.notifyUpdate();
		return d;
	}

	/**
	 * Removes the dish
	 * @param dish dish to remove
	 */

	@Override
	public void removeDish(Dish dish) {
		this.dishes.remove(dish);
		this.notifyUpdate();
	}


	/**
	 * Returns Dish Stock Level
	 * @return
	 */

	@Override
	public Map<Dish, Number> getDishStockLevels() {
		return stockDish.dishesStock();
	}

	/**
	 *
	 * @param enabled set to true to enable restocking of dishes, or false to disable.
	 */


	@Override
	public void setRestockingDishesEnabled(boolean enabled) {
		rDishAllow = enabled;
	}

	/**
	 *Set The Stock of the dish
	 * @param dish dish to set the stock
	 * @param stock stock amount
	 */
	@Override
	public void setStock(Dish dish, Number stock) {
		stockDish.dishesStock().put(dish,stock);
		stockDish.insertIntoDs(dish,(int)stock);
		this.notifyUpdate();
	}

	/**
	 *Add the ingredient to the dish
	 * @param dish dish to edit the recipe of
	 * @param ingredient ingredient to add/update
	 * @param quantity quantity to set. Should update and replace, not add to.
	 */
	@Override
	public void addIngredientToDish(Dish dish, Ingredient ingredient, Number quantity) {
		if(quantity == Integer.valueOf(0)) {
			removeIngredientFromDish(dish,ingredient);
		} else {
			dish.getRecipe().put(ingredient,quantity);
		}
	}

	/**
	 * Remove the Ingredient from the Dish
	 * @param dish dish to edit the recipe of
	 * @param ingredient ingredient to completely remove
	 */
	@Override
	public void removeIngredientFromDish(Dish dish, Ingredient ingredient) {
		dish.getRecipe().remove(ingredient);
		this.notifyUpdate();
	}

	/**
	 * Return's the Recipe of the Dish
	 * @param dish dish to query the recipe of
	 * @return
	 */


	@Override
	public Map<Ingredient, Number> getRecipe(Dish dish) {
		return dish.getRecipe();
	}

	/**
	 *Set the recipe of Dish
	 * @param dish dish to modify the recipe of
	 * @param recipe map of ingredients and quantity numbers to update
	 */
	@Override
	public void setRecipe(Dish dish, Map<Ingredient, Number> recipe) {
		dish.setRecipe(recipe);
		notifyUpdate();
	}

	/**
	 *Set the Restock Level of Dish
	 * @param dish dish to modify the restocking levels of
	 * @param restockThreshold new amount at which to restock
	 * @param restockAmount new amount to restock by when threshold is reached
	 */

	@Override
	public void setRestockLevels(Dish dish, Number restockThreshold, Number restockAmount) {
		stockDish.setRestockAmountDish(dish,restockAmount);
		stockDish.setRestockThresholdDish(dish,restockThreshold);
		this.notifyUpdate();
	}

	/**
	 *Return the Restock Threshold of dish
	 * @param dish dish to query restock threshold of
	 * @return
	 */
	@Override
	public Number getRestockThreshold(Dish dish) {
		return stockDish.getRestockThresholdDish(dish);
	}

	/**
	 *Return the Restock Amount of the Dish
	 * @param dish dish to query restock amount of
	 * @return
	 */
	@Override
	public Number getRestockAmount(Dish dish) {
		return stockDish.getRestockAmountDish(dish);
	}



/////////////INGREDIENTS/////////////////////////////////////////////////////////////////

	/**
	 *
	 * @return
	 */
	@Override
	public ArrayList<Ingredient> getIngredients() {
		return this.ingredients;
	}

	/**
	 *Add the Ingredient
	 * @param name name
	 * @param unit unit
	 * @param supplier supplier
	 * @param restockThreshold when amount reaches restockThreshold restock
	 * @param restockAmount when threshold is reached, restock with this amount
	 * @param weight weight of the ingredient
	 * @return
	 */
	@Override
	public Ingredient addIngredient(String name, String unit, Supplier supplier,
									Number restockThreshold, Number restockAmount, Number weight) {
		Ingredient i=new Ingredient(name,unit,supplier,restockThreshold,restockAmount,weight);
		stockManagementForIng.ingredientsStock().put(i,0);
		//stk.StockManagementForIng(i,0);
		ingredients.add(i);
		//this.notifyUpdate();
		this.notifyUpdate();
		return i;

	}

	/**
	 *Remove the Ingredient
	 * @param ingredient ingredient to remove
	 */
	@Override
	public void removeIngredient(Ingredient ingredient) {
		int index = this.ingredients.indexOf(ingredient);
		this.ingredients.remove(index);
		this.notifyUpdate();
	}

	/**
	 *
	 * @param enabled set to true to enable restocking of ingredients, or false to disable.
	 */
	@Override
	public void setRestockingIngredientsEnabled(boolean enabled) {
		rIngreAllow=enabled;
	}

	/**
	 *Set the stock of the Ingredient
	 * @param ingredient ingredient to set the stock
	 * @param stock stock amount
	 */
	@Override
	public void setStock(Ingredient ingredient, Number stock) {
		//ingredients = stk.getIngredient();
		//ingredient.setStock(stock);
		stockManagementForIng.ingredientsStock().put(ingredient,stock);
		//stk.setStockIngredients(ingredient,stock);
		this.notifyUpdate();
	}

	/**
	 *Return the Stock Level of the Ingredient
	 * @return
	 */

	@Override
	public Map<Ingredient, Number> getIngredientStockLevels() {
		return stockManagementForIng.ingredientsStock();
	}

	/**
	 *Set the Restock level of the ingredient
	 * @param ingredient ingredient to modify the restocking levels of
	 * @param restockThreshold new amount at which to restock
	 * @param restockAmount new amount to restock by when threshold is reached
	 */
	@Override
	public void setRestockLevels(Ingredient ingredient, Number restockThreshold, Number restockAmount) {
		stockManagementForIng.setRestockAmountIng(ingredient,restockAmount);
		stockManagementForIng.setRestockThresholdIng(ingredient,restockThreshold);
		this.notifyUpdate();
	}

	/**
	 *Return the Restock Threshold of ingredient
	 * @param ingredient ingredient to query restock threshold of
	 * @return
	 */
	@Override
	public Number getRestockThreshold(Ingredient ingredient) {
		return stockManagementForIng.getRestockThresholdIng(ingredient);
	}

	/**
	 *Return The Restock Amount of the Ingredient
	 * @param ingredient ingredient to query restock amount of
	 * @return
	 */
	@Override
	public Number getRestockAmount(Ingredient ingredient) {
		return stockManagementForIng.getRestockAmountIng(ingredient);
	}

/////////////RESTAURANT//////////////////////////////////

	/**
	 *Return the Restaurant
	 * @return
	 */
	public List<Restaurant> getRestauran(){
		return this.res;
	}

	/**
	 *Add the Restaurant
	 * @param name
	 * @param location
	 * @return
	 */
	public Restaurant addRestauran(String name, Postcode location){
		Restaurant r = new Restaurant(name,location);
		this.res.add(r);
		this.notifyUpdate();
		return r;
	}
/////////////SUPPLIERS////////////////////////////////////////////////////

	/**
	 *Return the List of Suppliers
	 * @return
	 */
	@Override
	public List<Supplier> getSuppliers() {
		return this.suppliers;
	}

	/**
	 *Add the Supplier
	 * @param name name of supplier
	 * @param postcode
	 * @return
	 */
	@Override
	public Supplier addSupplier(String name, Postcode postcode) {
		Supplier mock = new Supplier(name,postcode);
		this.suppliers.add(mock);
		this.notifyUpdate();
		return mock;
	}

	/**
	 *Remove the Supplier
	 * @param supplier supplier to remove
	 */

	@Override
	public void removeSupplier(Supplier supplier) {
		int index = this.suppliers.indexOf(supplier);
		this.suppliers.remove(index);
		this.notifyUpdate();
	}

	/**
	 *Return the Distance of Supplier
	 * @param supplier supplier to query
	 * @return
	 */
	@Override
	public Number getSupplierDistance(Supplier supplier) {
		return supplier.getDistance();
	}

/////////////////DRONES///////////////////////////////////

	/**
	 *Return the list of Drones
	 * @return
	 */
	@Override
	public List<Drone> getDrones() {
		return this.drones;
	}

	/**
	 *Add the drone
	 * @param speed speed of drone
	 * @return
	 */
	@Override
	public Drone addDrone(Number speed) {
		Drone drone = new Drone(speed,stockManagementForIng,this );
		this.drones.add(drone);
		Thread task = new Thread(drone);
		task.start();
		drone.thread = task;
		notifyUpdate();
		return drone;
	}

	/**
	 *Remove the drone
	 * @param drone drone to remove
	 */
	@Override
	public void removeDrone(Drone drone) {
		int index = this.drones.indexOf(drone);
		this.drones.remove(index);
		this.notifyUpdate();
	}

	/**
	 *Return the speed of drone
	 * @param drone drone to query
	 * @return
	 */
	@Override
	public Number getDroneSpeed(Drone drone) {
		return drone.getSpeed();
	}

	/**
	 *Return the status of drone
	 * @param drone drone to query
	 * @return
	 */
	@Override
	public String getDroneStatus(Drone drone) {
		Random rand = new Random();
		if(rand.nextBoolean()) {
			return "Idle";
		} else {
			return "Flying";
		}
	}

	/**
	 *Return the Source of drone
	 * @param drone source postcode
	 * @return
	 */
	@Override
	public Postcode getDroneSource(Drone drone) {
		return drone.getSource();
	}

	/**
	 *Return the Destination of the drone
	 * @param drone destination postcode
	 * @return
	 */
	@Override
	public Postcode getDroneDestination(Drone drone) {
		return drone.getDestination();
	}

	/**
	 *Return the progress of the drone
	 * @param drone drone to check progress of
	 * @return
	 */
	@Override
	public Number getDroneProgress(Drone drone) {
		return drone.getProgress();
	}


/////////////STAFF//////////////////////////////////////

	/**
	 *Return the staff
	 * @return
	 */
	@Override
	public List<Staff> getStaff() {
		return this.staff;
	}

	/**
	 *Add the staff
	 * @param name name of staff member
	 * @return
	 */
	@Override
	public Staff addStaff(String name) {
		Staff dron = new Staff(name,stockDish,this );
		this.staff.add(dron);
		Thread task = new Thread(dron);
		task.start();
		dron.thre = task;
		notifyUpdate();
		return dron;
	}

	/**
	 *Remove the staff
	 * @param staff staff member to remove
	 */
	@Override
	public void removeStaff(Staff staff) {
		this.staff.remove(staff);
		this.notifyUpdate();
	}

	/**
	 *Return the status of staff
	 * @param staff member to query
	 * @return
	 */

	@Override
	public String getStaffStatus(Staff staff) {
		Random rand = new Random();
		if(rand.nextBoolean()) {
			return "Idle";
		} else {
			return "Working";
		}
		//return staff.getStatus();
	}
	////////////////////ORDERS//////////////////////////

	/**
	 *Return the orders
	 * @return
	 */
	@Override
	public ArrayList<Order> getOrders() {
		return this.orders;
	}
	/**
	 *Remove the order
	 * @param order order to remove
	 */
	@Override
	public void removeOrder(Order order) {
		int index = this.orders.indexOf(order);
		this.orders.remove(index);
		this.notifyUpdate();
	}

	/**
	 *Return the cost of the order
	 * @param order order to query
	 * @return
	 */
	@Override
	public Number getOrderCost(Order order) {
		Random random = new Random();
		return random.nextInt(100);
	}

	/**
	 *Return the distance of the order
	 * @param order order to query
	 * @return
	 */
	@Override
	public Number getOrderDistance(Order order) {
		Order mock = (Order)order;
		return mock.getDistance();
	}

	/***
	 *Return if the order is complete
	 * @param order order to query
	 * @return
	 */

	@Override
	public boolean isOrderComplete(Order order) {
		return true;
	}

	/**
	 *Return teh status of the order
	 * @param order order to query
	 * @return
	 */
	@Override
	public String getOrderStatus(Order order) {
		return order.getStatus();
	}
	public void addOrder(Order order) {
		orders.add(order);
		this.notifyUpdate();
	}
//////////////POSTCODES////////////////////////////////////////////////////////////

	/**
	 *Return the list of Postcode
	 * @return
	 */

	@Override
	public ArrayList<Postcode> getPostcodes() {
		return this.postcodes;
	}

	/**
	 *
	 *Add the postcode
	 * @param code postcode string representation
	 * @return
	 */
	@Override
	public Postcode addPostcode(String code) {
		Postcode mock = new Postcode(code);
		this.postcodes.add(mock);
		this.notifyUpdate();
		return mock;
	}

	/**
	 *remove the postcode
	 * @param postcode postcode to remove
	 * @throws UnableToDeleteException
	 */

	@Override
	public void removePostcode(Postcode postcode) throws UnableToDeleteException {
		this.postcodes.remove(postcode);
		this.notifyUpdate();
	}


/////////////////////////////////////////USERS//////////////////////////////////////////////////////////////////////////

	/**
	 *Add the user
	 * @param username
	 * @param password
	 * @param address
	 * @param postcode
	 * @return
	 */
	public User addUser(String username, String password, String address, Postcode postcode){
		User u = new User(username,password,address,postcode);
		this.users.add(u);
		this.notifyUpdate();
		return u;
}

	/**
	 *Return the userList
	 * @return
	 */
	@Override
	public ArrayList<User> getUsers() {
		return this.users;
	}
	/**
	 *Remove the user
	 * @param user to remove
	 */
	@Override
	public void removeUser(User user) {
		this.users.remove(user);
		this.notifyUpdate();
	}
	/////////////////////CONFIGURATION//////////////////////////////////////////////////////////////////////////////////

	/**
	 *Load the configuration
	 * @param filename configuration file to load
	 * @throws FileNotFoundException
	 */
	@Override
	public void loadConfiguration(String filename) throws FileNotFoundException {
		System.out.println("Loaded configuration: " + filename);
		this.aname=filename;
		conf = new Configuration(filename,this,feasibleU);
		ingredients=conf.getIngredient();
		dishes=conf.getDishes();
		orders=conf.getOrders();
		stockManagementForIng=conf.getStockIngredient();
		stockDish = conf.getStockDish();
		feasibleU=conf.getFeasibleU();
		serialNumber=conf.getSerialNumber();
		for(Staff s:conf.getStaff())
		{
			this.addStaff(s.getName());
		}
		for(Drone d:conf.getDrone())
		{
			this.addDrone(d.getSpeed());
		}
		dataPersistence = new DataPersistence(this,filename);

	}
	public HashMap<String,Integer> getSerialNumber(){
		return serialNumber;
	}
public HashMap<String,User> getFeasibleU(){
		return feasibleU;
}

	/**
	 *
	 * @param listener An update listener to be informed of all model changes.
	 */

	@Override
	public void addUpdateListener(UpdateListener listener) {

		this.listeners.add(listener);
	}

	/**
	 *
	 */
	@Override
	public void notifyUpdate() {
		this.listeners.forEach(listener -> listener.updated(new UpdateEvent()));
	}

	/**
	 *
	 * @return Restaurant name
	 */
	@Override
	public String getRestaurantName() {
		return restaurant.getName();
	}

	/**
	 *
	 * @return Restaurant postcode
	 */
	@Override
	public Postcode getRestaurantPostcode() {
		return restaurant.getLocation();
	}

	/**
	 *
	 * @return Restaurant
	 */
	@Override
	public Restaurant getRestaurant() {
		return this.restaurant;
	}

	/**
	 *
	 * Remove's the postcode from the drone
	 */
	public Postcode nullify(){
		return null;
	}

	/**
	 *
	 *
	 * @return The Progress of the drone
	 */
	public Number progressofDrone(float x){
		if(x<60){
		float  y = ((60-x)/60)*100;
		return y;}
		else if(x>60){
			float z = ((x-60)/60)*100;
			return z;
		}
		else
			return null;
	}

	/**
	 * Removes the Progress from the Drone
	 * @return null
	 */
	public Number settingToZero(){
		return null;
	}

}

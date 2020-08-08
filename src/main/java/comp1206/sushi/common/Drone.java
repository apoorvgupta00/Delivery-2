package comp1206.sushi.common;


import comp1206.sushi.server.Server;

import java.io.Serializable;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Drone class
 */
public class Drone extends Model implements Runnable, Serializable {

	/**
	 * Member Variables of the class
	 */

	private Number speed;
	private Number progress;
	private Server server ;
	private Number capacity;
	private Number battery;
	public Thread thread;
	private Postcode source;
	private Postcode destination;
	private int a =100 ;
	private int latst;
	private String status;
	private StockManagementForIng StockManagementForIng;

	/**
	 * Constructor for the Class Drone
	 * @param speed
	 * @param StockManagementForIng
	 * @param server
	 */
	public Drone(Number speed , StockManagementForIng StockManagementForIng,Server server){
		this.setSpeed(speed);
		this.status="Idle";
		this.setCapacity(1);
		this.setBattery(100);
		this.server=server;
		this.StockManagementForIng=StockManagementForIng;
	}

	/**
	 * Returns the status of the drone
	 * @return
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * Returns speed of Drone
	 * @return
	 */
	public Number getSpeed() {
		return speed;
	}

	/**
	 * sets the Status of the Drone
	 * @param status
	 */
	public synchronized void setStatus(String status) {

		notifyUpdate("Status", this.status, status);
		this.status = status;
	}

	/**
	 * Sets the speed of Speed
	 * @param speed
	 */

	public synchronized void setSpeed(Integer speed) {
		notifyUpdate("speed", this.speed, speed);
		this.speed = speed;
	}

	/**
	 * Returns the name of drone
	 * @return
	 */
	public String getName() {
		return "Drone (" + getSpeed() + " speed)";
	}

	/**
	 * returns the progress of the drone
	 * @return
	 */

	public Number getProgress() {
		return progress;
	}

	/**
	 * Sets the Progress of the drone
	 * @param progress
	 */

	public void setProgress(Number progress) {
		this.progress = progress;
	}

	/**
	 * Sets the speed of drone
	 * @param speed
	 */
	public void setSpeed(Number speed) {
		this.speed = speed;
	}

	/**
	 * Returns the source of Drone
	 * @return
	 */
	public Postcode getSource() {
		return source;
	}

	/**
	 * Sets the source of drone
	 * @param source
	 */
	public void setSource(Postcode source) {
		this.source = source;
	}

	/**
	 * Returns destination of drone
	 * @return
	 */
	public Postcode getDestination() {
		return destination;
	}

	/**
	 * Sets destination of drone
	 * @param destination
	 */
	public void setDestination(Postcode destination) {
		this.destination = destination;
	}

	/**
	 * Returns the Capacity of drone
	 * @return
	 */
	public Number getCapacity() {
		return capacity;
	}

	/**
	 * Sets the Capacity of the Drone
	 * @param capacity
	 */
	public void setCapacity(Number capacity) {
		this.capacity = capacity;
	}

	/**
	 * Returns the battery of the Drone
	 * @return
	 */
	public Number getBattery() {
		return battery;
	}

	/**
	 * Sets the battery of the Drone
	 * @param battery
	 */
	public void setBattery(Number battery) {
		this.battery = battery;
	}

	/**
	 * Method to restock the Ingredients
	 */
	public void restockingIngredientsFromSupplier(){
		try{
			for (Ingredient i : server.getIngredients()){
				Lock l = i.pLock();
				if(!l.tryLock()) { continue; }
				try{

					l.lock();
					Integer restockIng = (int) i.getRestockThreshold();
					Integer recentStock = (int) server.getIngredientStockLevels().get(i);

					//Checks for the condition
					//If true then continue
					if (restockIng > recentStock) {
						//Time for the drone
						int restockingIngre =  (int) i.getSupplier().getDistance()/ (int) speed;
						int o = 100;
						for (int t = restockingIngre; t >= 0; t--) {
							Thread.sleep(500);
							//Sets the status to restocking
							setStatus("Restocking");

							//Sets the source postcode
							for (Supplier ry : server.getSuppliers()) {
								if (ry == i.getSupplier()) {
									setSource(ry.getPostcode());
								}
							}

							//Sets the destination postcode
							for (Restaurant ru : server.getRestauran()) {
								setDestination(ru.getLocation());
							}
							progressOfDrone(server.progressofDrone(t));

						}

						//restocks the level of the particular Ingredient
						server.setStock(i,(int)server.getIngredientStockLevels().get(i)+(int)i.getRestockAmount());

						//Some calculations for the current battery level
						a = a - restockingIngre;
						int s = a - restockingIngre;
						//Checks if its exhausted
						if (s == 0 || s < 0) {
							setBattery(0);
							setStatus("Out of Battery, Recharging");
							setBattery(100);
						} else {
							//Sets the current battery level
							setBattery(s);
							latst=s;
						}
						nullifying(server.nullify(), server.nullify());
					}
					//Thread.sleep(500);
					progressOfDrone(server.settingToZero());
					this.setStatus("Idle");
					l.unlock();
				}catch (InterruptedException e){
					l.unlock();
					Thread.currentThread().interrupt();
				}catch (ConcurrentModificationException e) {
					l.unlock();
					break;
				}

			}

		}catch (ConcurrentModificationException e){

		}


	}
	@Override
	public void run() {

		while (true) {
			restockingIngredientsFromSupplier();

				//Delivering the order
			try{
				again:
				for(Order order : server.getOrders()){
					synchronized (this) {
						//Checks for the condition
						if(order.getStatus().equals("Waiting for Dishes")){
							try {
								for (Map.Entry<Dish, Number> dishNumberEntry : order.getBasket().entrySet()) {
									for (Dish dish : server.getDishes()) {
										if (dish.equals(dishNumberEntry.getKey())) {
											Integer m = (int) server.getDishStockLevels().get(dish);
											if ((int) m < (int) dishNumberEntry.getValue()) {
												Thread.sleep(1000);
												setStatus("Idle");
												continue again;
											}
										}
									}
								}
								for (Map.Entry<Dish, Number> dishNumberEntry :order.getBasket().entrySet()) {
									for (Dish dish : server.getDishes()) {
										if(dishNumberEntry.getKey().getName().equals(dish.getName())) {
											Integer orderentry = (Integer) dishNumberEntry.getValue();
											int  present = (int) server.getDishStockLevels().get(dish);
											if ((int) present < orderentry) {
												Thread.sleep(1000);
												setStatus("Idle");
												continue again;
											}
										}
									}
								}
								for (Map.Entry<Dish, Number> recipeItem :order.getBasket().entrySet()) {
									for (Dish dish : server.getDishes()) {
										if (recipeItem.getKey().getName().equals(dish.getName())) {
											Integer orderentry = (Integer) recipeItem.getValue();
											int present = (int) server.getDishStockLevels().get(dish);
											Integer latestStk = present - orderentry;
											notifyUpdate("Dishes", server.getDishStockLevels().get(dish), latestStk);
											server.setStock(dish, latestStk);

									}
									}
								}

								int deliverytime = (int) order.getDistance()/(int) speed;
								for (int n = deliverytime ; n > 0 ; n--) {
									//sets the status
									this.status = "Delivering the Order" + order.getUser().getName();
									order.setStatus("Delivering the Order");
									//Sets the postcode of source
									for (Restaurant ru : server.getRestauran()) {
										setSource(ru.getLocation());
									}

									//updates the progress of drone
									progressOfDrone(server.progressofDrone(deliverytime));

									//sets the destination postcode
									setDestination(order.getUser().getPostcode());
								}
								a = a - deliverytime;
								int s = a - deliverytime;
								//Checks if its exhausted
								if (s == 0 || s < 0) {
									setBattery(0);
									setStatus("Out of Battery, Recharging");
									setBattery(100);
								} else {
									//Sets the current battery level
									setBattery(s);
									latst=s;
								}
								//}
								Thread.sleep(1000);
								order.setStatus("Completed");
								order.setOrderComplete(true);
								nullifying(server.nullify(), server.nullify());
								progressOfDrone(server.settingToZero());
								this.setStatus("Idle");

							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}}

				}
			}catch (ConcurrentModificationException e){
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}


		}

	}

	/**
	 * removes the postcode from the server of drone
	 * @param destination
	 * @param source
	 */
	private void nullifying(Postcode destination,Postcode source){
		this.destination=destination;
		this.source=source;
		server.notifyUpdate();
	}

	/**
	 * updates the progress of the drone
	 * @param progress
	 */
	private void progressOfDrone(Number progress){
		this.progress=progress;
		server.notifyUpdate();
	}



}

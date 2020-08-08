package comp1206.sushi.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Map;

import comp1206.sushi.server.Server;
public class Staff extends Model implements Runnable, Serializable {
	/**
	 * Member Variables of the class
	 */
	private String name;
	private String status;
	private Number fatigue;
	private Server server;
	public Thread thre;
	private int a =0 ;
	private StockDish stockDish ;

	/**
	 * Constructor for the class
	 * @param name
	 * @param stockDish
	 * @param server
	 */
	public Staff (String name, StockDish stockDish,Server server){
		this.setFatigue(0);
		this.name=name;
		this.server=server;
		this.status="Idle";
		this.stockDish=stockDish;
	}

	public Staff(Server server){
		this.server=server;
	}
	/**
	 * Set the name of the staff
	 * @param name model name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * returns the fatigue
	 * @return
	 */
	public Number getFatigue() {
		return fatigue;
	}

	/**
	 * set the fatigue
	 * @param fatigue
	 */
	public void setFatigue(Number fatigue) {
		this.fatigue = fatigue;
	}

	/**
	 * set the status of the staff
	 * @param status
	 */
	//public String getStatus() {
	//	return status;
	//}

	public void setStatus(String status) {
		notifyUpdate("status",this.status,status);
		this.status = status;
	}

	/**
	 * restocking the dishes
	 */
	private void prepareNewDishesInStock() {
		try {
			again:
			for (Dish dish : server.getDishes()) {
				Lock lock = dish.pLock();
				if(!lock.tryLock()) { continue; }
				try{
					lock.lock();
					Integer restockThresholddish = (int) dish.getRestockThreshold();
					Integer dishlevel = (int) server.getDishStockLevels().get(dish);
					if (dishlevel < restockThresholddish){
						//checks for the ingredients of the dish
						for (HashMap.Entry<Ingredient, Number> ds : dish.getRecipe().entrySet()) {
							if ((int) server.getIngredientStockLevels().get(ds.getKey()) < (int) ds.getValue()) {
								Thread.sleep(1000);
								//If the ingredients are low then set the status Idle
								this.setStatus("Idle");
								continue again;

							}
						}
						//deletes the ingredients from the stock
						for(Map.Entry<Ingredient,Number> in : server.getRecipe(dish).entrySet()){
							for(Ingredient ing : server.getIngredients()){
								if(ing.getName().equals(in.getKey().getName())){
									server.setStock(ing,(int)server.getIngredientStockLevels().get(ing)-(int)in.getValue());
								}
							}
						}


						//calculating the restockingdishtime
						int restockDishTime = (int) (Math.random() * 40 + 20);
						for (int r = 20 ;r > 0; r--) {
							this.setStatus(dish.getName()+" is being made ");
							Thread.sleep(1000);
						}

						//Update the stock od dish
						server.setStock(dish,(int)dish.getRestockAmount()+(int)server.getDishStockLevels().get(dish));

						//Fatigue calculations
						a = a +restockDishTime;
						if(a==100 || a>100){setFatigue(0);
							setStatus("Recharging");
							setFatigue(0);}
						else{setFatigue(a);}
						}
					Thread.sleep(500);
					this.setStatus("Idle");
					lock.unlock();
				} catch (InterruptedException e){
					lock.unlock();
					Thread.currentThread().interrupt();
				}catch (ConcurrentModificationException e) {
					lock.unlock();
					break;
				}
			}
		} catch (ConcurrentModificationException e) {
		}
	}

	/**
	 * Return the name of the staff
	 * @return
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 *Return the status of the staff
	 * @return
	 */
	public String getStatus() {
		return status;
	}
	@Override
	public void run() {
		while (true) {
			prepareNewDishesInStock();
			try {
				Thread.sleep(180);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}


}

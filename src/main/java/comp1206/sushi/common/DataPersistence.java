package comp1206.sushi.common;
import comp1206.sushi.common.*;
import comp1206.sushi.server.Server;

//import materials.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DataPersistence {
    /**
     * Member Variables
     */
    private FileOutputStream writingFile;
    private PrintWriter pwr;
    private Server server;
    private String fname ;

    /**
     * Constructor for class
     * @param server
     * @param fname
     */
    public DataPersistence(Server server,String fname) {
        this.server=server;
        this.fname=fname;
    }


    /**
     * Integrates all the methods
     */
    public void newConfig() {
        try {
            writingFile = new FileOutputStream(fname);
            pwr = new PrintWriter(writingFile);
            writePostcodeToFile();
            writeSupplierToFile();
            writeIngredientToFile();
            writeDishToFile();
            writeUserToFile();
            writeOrderToFile();
            writeStockToFile();
            writeDroneToFile();
            writeStaffToFile();
            writeRestaurantToFile();


            writingFile.close();
            pwr.close();


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Save the configuration of Postcode to the file
     * @throws IOException
     */
    private void writePostcodeToFile() throws IOException {
        for (Postcode postcode : server.getPostcodes()) {

            pwr.write("POSTCODE:" + postcode.getName()  + "\r\n");
            pwr.flush();


        }
    }

    /**
     * Save the configuration of Restaurant to the file
     * @throws IOException
     */
    private void writeRestaurantToFile() throws IOException{
        for(Restaurant r : server.getRestauran()){
            pwr.write("RESTAURANT:"+r.getName()+":"+r.getLocation().getCode() + "\r\n");
            pwr.flush();
        }
    }

    /**
     * Save the configuration of Supplier to the file
     * @throws IOException
     */
    private void writeSupplierToFile() throws IOException {
            for (Supplier s : server.getSuppliers()) {
                pwr.write("SUPPLIER:"+s.getName()+":"+s.getPostcode() + "\r\n");
            }
    }

    /**
     * Save the configuration of Ingredient to the file
     * @throws IOException
     */
    private void writeIngredientToFile() throws IOException {
        for (Ingredient i : server.getIngredients()) {
            pwr.write("INGREDIENT:"+i.getName()+":"+i.getUnit()+":"+i.getSupplier().getName()+":"
                    +i.getRestockThreshold()+":"+i.getRestockAmount()+":"+i.getWeight()+ "\r\n");
            pwr.flush();
        }
    }

    /**
     * Save the configuration of Dish to the file
     * @throws IOException
     */
    private void writeDishToFile() throws IOException {
        for (Dish d : server.getDishes()) {
            pwr.write("DISH:"+d.getName()+":"+d.getDescription()+":"+d.getPrice()+":"+
                    d.getRestockThreshold()+":"+d.getRestockAmount()+":");
            int x=0;
                for (Map.Entry<Ingredient, Number> recipeItem : d.getRecipe().entrySet()) {
                    pwr.write(recipeItem.getValue()+" * "+recipeItem.getKey().getName());
                    x++;
                    if(x<d.getRecipe().size()) pwr.write(",");
                }

            pwr.write("\r\n");
            pwr.flush();
        }
    }

    /**
     * Save the configuration of User to the file
     * @throws IOException
     */
    private void writeUserToFile() throws IOException {
        for (User user : server.getUsers()) {

            pwr.write("USER:" + user.getName() + ":" + user.getPassword() + ":" + user.getAddress() + ":" + user.getPostcode() + "\r\n");
            pwr.flush();

        }
    }

    /**
     * Save the configuration of Order to the file
     * @throws IOException
     */
    private void writeOrderToFile() throws IOException {
        for(Order o : server.getOrders()){
        pwr.write("ORDER:"+o.getName()+":");
        int iter=0;
        for (Map.Entry<Dish, Number> basketItem : o.getBasket().entrySet()) {
            pwr.write(basketItem.getValue()+" * "+basketItem.getKey().getName());
            iter++;
            if(iter<o.getBasket().size()) pwr.write(",");
        }
        pwr.write("\r\n");}
        pwr.flush();
    }

    /**
     * Save the configuration of Drone to the file
     * @throws IOException
     */
    private void writeDroneToFile() throws IOException {
        for (Drone drone : server.getDrones()) {

            pwr.write("DRONE:" + drone.getSpeed() + "\r\n");
            pwr.flush();

        }
    }

    /**
     * Save the configuration of Staff to the file
     * @throws IOException
     */
    private void writeStaffToFile() throws IOException {
        for (Staff staffR : server.getStaff()) {

            pwr.write("STAFF:" + staffR.getName() + "\r\n");
            pwr.flush();

        }
    }

    /**
     * Save the configuration of Stock to the file
     * @throws IOException
     */
    private void writeStockToFile() throws IOException {
        for (Map.Entry<Dish, Number> dishes : server.getDishStockLevels().entrySet()) {

            pwr.write("STOCK:" + dishes.getKey().getName() + ":" + dishes.getValue() + "\r\n");
            pwr.flush();

        }

        for (Map.Entry<Ingredient, Number> ingredient : server.getIngredientStockLevels().entrySet()) {

            pwr.write("STOCK:" + ingredient.getKey().getName() + ":" + ingredient.getValue() + "\r\n");
            pwr.flush();

        }
    }


}

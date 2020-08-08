package comp1206.sushi.common;

import comp1206.sushi.common.*;
import comp1206.sushi.server.Server;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
//import common.*;


public class Configuration {
    //Member Variables Defined
    private Server server;
    private String present ;
    private List<Supplier> supplierArrayList= new ArrayList<>();
    private ArrayList<User> userArrayList= new ArrayList<>();
    private List<Postcode> postcodeArrayList= new ArrayList<>();
    private ArrayList<Ingredient> ingredientArrayList= new ArrayList<>();
    private ArrayList<Dish> dishArrayList= new ArrayList<>();
    private ArrayList<Order> orderArrayList= new ArrayList<>();
    private ArrayList<Drone> droneArrayList= new ArrayList<>();
    private ArrayList<Staff> staffArrayList= new ArrayList<>();
    private List<Restaurant>restaurantArrayList = new ArrayList<>();
    private HashMap<String, User> feasibleU;
    private Map<String, Dish> stringDishMap;
    private StockManagementForIng stockManagementForIng ;
    private StockDish stockDish ;


    private HashMap<String, Integer> serialNumber = new HashMap<>();
    public String fname;

    /**
     * Constructor for the class
     * @param Filename
     * @param server
     * @throws FileNotFoundException
     */
    public Configuration(String Filename, Server server,HashMap<String ,User> feasibleU) throws FileNotFoundException{
        this.server=server;
        this.feasibleU = feasibleU;
        stringDishMap = new HashMap<>();
        stockManagementForIng = new StockManagementForIng(server);
        stockDish = new StockDish(server);
        this.fname=Filename;
        File x= new File(fname);
        BufferedReader a= new BufferedReader(new FileReader(x));

        try {
            while ((present = a.readLine()) != null) {
                String[] separating = present.split(":");
                switch (separating[0]){
                    //Adds the order to the server
                    case "ORDER":
                        String u = separating[1];
                        String overWrite = separating[2];
                        Integer serialise = 0;
                        if (serialNumber.containsKey(u)) {
                            serialise = serialNumber.get(u);
                        }
                        serialise++;
                        Order orderToAdd = new Order(serialise,feasibleU.get(u),overWrite,writeOrder(overWrite) );
                        serialNumber.put(u, serialise);
                        server.addOrder(orderToAdd);
                        orderArrayList.add(orderToAdd);
                        break;
                        //Add the Dish to the server
                    case "DISH":
                        String ing = separating[6];
                        Map<Ingredient, Number> ingredientNumberMap = writeRecipe(ing);
                        Dish d = new Dish(separating[1], separating[2], Integer.valueOf(separating[3]), Integer.valueOf(separating[4]), Integer.valueOf(separating[5]));
                        dishArrayList.add(d);
                        server.addDish(separating[1], separating[2], Integer.valueOf(separating[3]), Integer.valueOf(separating[4]), Integer.valueOf(separating[5]));
                        stringDishMap.put(separating[1], d);
                        stockDish.dishesStock().put(d, 0);
                        stockDish.insertIntoDs(d,0);
                        server.setRecipe(d,ingredientNumberMap);
                        d.setRecipe(ingredientNumberMap);
                        stockDish.setRestockThresholdDish(d,Integer.valueOf(separating[4]));
                        stockDish.setRestockAmountDish(d,Integer.valueOf(separating[5]));
                        break;
                        //Add the Ingredient to the server
                    case "INGREDIENT":
                        Ingredient in;
                        for (Supplier s : server.getSuppliers()) {
                            if (s.getName().equals(separating[3])) {
                                in = new Ingredient(separating[1], separating[2], s, Integer.valueOf(separating[4]), Integer.valueOf(separating[5]), Integer.parseInt(separating[6]));
                                ingredientArrayList.add(in);
                                stockManagementForIng.insertIntoIg(in,0);
                                stockManagementForIng.setRestockAmountIng(in,Integer.valueOf(separating[5]));
                                stockManagementForIng.setRestockThresholdIng(in,Integer.valueOf(separating[4]));


                            }
                        }
                        break;
                        //Add the Supplier to the server
                    case "SUPPLIER":
                        for (Postcode pc : server.getPostcodes()) {
                            if(pc.getName().equals(separating[2])) {
                                server.addSupplier(separating[1], pc);
                            }}
                        break;
                        //Add the Staff to the server
                    case "STAFF":
                        Staff s = new Staff(separating[1],stockDish,server);
                        staffArrayList.add(s);
                        break;
                        //Add the drone to the server
                    case "DRONE":
                        Drone dr= new Drone(Integer.parseInt(separating[1]),stockManagementForIng,server);
                        droneArrayList.add(dr);
                        break;
                        //Add the user to server
                    case "USER":
                        for (Postcode postcode : server.getPostcodes()) {
                            if (postcode.getName().equals(separating[4])) {
                                server.addUser(separating[1],separating[2],separating[3],postcode);
                                User newUser = new User(separating[1],separating[2],separating[3],postcode);
                                feasibleU.put(separating[1], newUser);
                            }
                        }
                        break;
                        //Add the Postcode
                    case "POSTCODE":
                        server.addPostcode(separating[1]);
                        //Add the Stock
                    case "STOCK":
                        //Add the Stock for the Ingredient
                        for (Ingredient i : ingredientArrayList) {
                            if (separating[1].equals(i.getName()))
                                stockManagementForIng.insertIntoIg(i,Integer.parseInt(separating[2]));
                        }
                        //Add the stock for the Dish
                        for (Dish di : dishArrayList) {
                            if (separating[1].equals(di.getName())) {
                                stockDish.insertIntoDs(di,Integer.parseInt(separating[2]));
                                stockDish.dishesStock().put(di,Integer.parseInt(separating[2]));
                            }
                        }
                        break;
                        //Add the Restaurant
                    case "RESTAURANT":
                        for (Postcode pc : server.getPostcodes()) {
                            if(separating[2].equals(pc.getCode())) {
                                server.addRestauran(separating[1],pc);}

                        }
                        break;

                }


            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Return the serial number
     * @return
     */
    public HashMap<String,Integer> getSerialNumber(){
        return serialNumber;
    }

    /**
     *
     * @return
     */
    public HashMap<String,User> getFeasibleU(){
        return feasibleU;
}
    /**
     * Return the supplier
     * @return
     */
    public List<Supplier> getSupplier()
    {
        return supplierArrayList;
    }

    /**
     * Return the ingredient
     * @return
     */
    public ArrayList<Ingredient> getIngredient()
    {
        return ingredientArrayList;
    }

    /**
     * Return the Dishes
     * @return
     */
    public ArrayList<Dish> getDishes()
    {
        return dishArrayList;
    }

    /**
     * Return the Staff
     * @return
     */
    public List<Staff> getStaff()
    {
        return staffArrayList;
    }

    /**
     * Return the Drone
     * @return
     */
    public List<Drone> getDrone()
    {
        return droneArrayList;
    }

    /**
     * Return the user
     * @return
     */
    public ArrayList<User> getUsers()
    {
        return userArrayList;
    }

    /**
     * Return the Postcode
     * @return
     */
    public List<Postcode> getPost()
    {
        return postcodeArrayList;
    }

    /**
     * Return the StockManagementForIng
     * @return
     */
    public StockManagementForIng getStockIngredient(){return stockManagementForIng;}

    /**
     * Return the StockDish
     * @return
     */
    public StockDish getStockDish(){return stockDish;}

    /**
     * Return the list of Order
     * @return
     */
    public ArrayList<Order> getOrders()
    {
        return orderArrayList;
    }

    /**
     * Write the recipe
     * @param x
     * @return
     */

    public Map<Ingredient, Number> writeRecipe(String x) {

        Map<Ingredient, Number> ingredientNumberHashMap = new HashMap<>();

        String[] a = x.split(",");

        for (String currentIngredient : a) {

            String q = currentIngredient.split("\\*")[0].trim();
            String i = currentIngredient.split("\\*")[1].trim();
for(Ingredient in : ingredientArrayList) {
    if(i.equals(in.getName())){
        ingredientNumberHashMap.put(in, Integer.parseInt(q));
    }
}
        }
        return ingredientNumberHashMap;
    }

    /**
     * Breaks down the order in Writable format
     * @param x
     * @return
     */


    public HashMap<Dish, Number> writeOrder(String x) {


        HashMap<Dish, Number> dishNumberHashMap = new HashMap<>();
        String[] dishes = x.split(",");
        for (String d : dishes) {

            String dish1 = d.split("\\*")[1].trim();
            String q = d.split("\\*")[0].trim();
            Dish dish2 = stringDishMap.get(dish1);
            dishNumberHashMap.put(dish2, Integer.parseInt(q));
            }
    return dishNumberHashMap;


    }

}

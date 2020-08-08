
package comp1206.sushi.common;

import comp1206.sushi.server.Server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;



public class Comms implements Runnable, Serializable {

    /**
     * Member variables for the class
     */
    private ServerSocket serverSocket;

    private HashMap<String, User> uExist;
    private ArrayList<User> u;
    private ArrayList<Order> o;
    private ArrayList<Postcode> p;
    private ArrayList<Dish> d;

    private HashMap<String, Integer> serialNumber;
    private ArrayList<Interaction> commingClient;
    private static boolean inquire = true;
private Server server;
    /**
     * Constructor for the Comms Class
     */
    public Comms(Server server) {
        try {
            //Start listening on the port
            serverSocket = new ServerSocket(52325);
            this.server=server;
            this.u = server.getUsers();
            this.uExist = server.getFeasibleU();
            commingClient = new ArrayList<>();
            this.p = server.getPostcodes();
            this.d = server.getDishes();
            this.o = server.getOrders();
            this.serialNumber = server.getSerialNumber();

        } catch (IOException ioe) {

            System.out.println("The server could not be created on port 52325 as it used by someone else too, Please close the program on that port or change the port number in file");
            System.exit(-1);
        }
    }
    /**
     * connect to the client
     */
    class Interaction extends Thread implements Serializable {
        /**
         * member Variables
         */
        private Socket sForClient;
        private boolean checking = true;

        /**
         * Constructor for the class
         * @param s
         */
        public Interaction(Socket sForClient) {
            this.sForClient = sForClient;
        }

        /**
         * Run method
         */
        @Override
        public void run() {
            try {

                System.out.println("New user detected - " + sForClient.getInetAddress().getHostName());
                ObjectOutputStream obj1 = new ObjectOutputStream(sForClient.getOutputStream());
                ObjectInputStream obj2 = new ObjectInputStream(sForClient.getInputStream());


                while (checking) {

                    CommandFor command = (CommandFor) obj2.readObject();
                    sendMessage(command, obj1);

                    if (!inquire) {
                        checking = false;
                    }
                }

                obj2.close();
                obj1.close();
                sForClient.close();

            } catch (IOException e) {
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        /**
         * Sends responses to the client
         * @param prop
         * @param objectout
         * @throws IOException
         */
        public synchronized void sendMessage(CommandFor prop, ObjectOutputStream objectout) throws IOException {

            switch (prop.getProp()){
                case "NewClient":
                    newRegisteration(prop, objectout);
                    break;
                case "BringOrd":
                    bringOrd(prop, objectout);
                    break;
                case "PreviousRegistered":
                    previouslyRegistered(prop, objectout);
                    break;
                case "PutPostcode":
                    postcodesFromServer(prop, objectout);
                    break;
                case "ServerDish":
                    dishesFromServer(prop, objectout);
                    break;
                case "ClientB":
                    clientB(prop, objectout);
                    break;
                case "ClientBC":
                    clientBC(prop, objectout);
                    break;
                case "ClientAddDB":
                    clientAddDB(prop, objectout);
                    break;
                case "RemoveOrd":
                    cancelOrder(prop, objectout);
                    break;
                case "ClientCheckout":
                    clientChk(prop, objectout);
                    break;
                case "ClientUB":
                    clientUB(prop, objectout);
                    break;
                case  "ClearBasket":
                    clearBasket(prop, objectout);
                    break;
                case "ClientOrdSt":
                    clientOrdSt(prop, objectout);
                    break;
                case "ClientOC":
                    clientOC(prop, objectout);
                    break;
                case "ClientOrdComplete":
                    clientOrdComplete(prop, objectout);
                    break;
            }
        }

        /**
         * Register if there is new User
         * @param prop
         * @param object1
         */
        private void newRegisteration(CommandFor prop, ObjectOutputStream object1) {
            User registeringUser = (User) prop.geto1();
            if (uExist.containsKey(registeringUser.getName())) {
                try {
                    CommandFor contradict = new CommandFor("Already", null);
                    object1.writeObject(contradict);
                    object1.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    CommandFor validated = new CommandFor("New", null);
                    object1.writeObject(validated);
                    object1.flush();
                    uExist.put(registeringUser.getName(), registeringUser);
                    u.add(registeringUser);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


        }

        /**
         * Login into the client window
         *
         * @param prop
         * @param object1
         * @throws IOException
         */
        private synchronized void previouslyRegistered(CommandFor prop, ObjectOutputStream object1) throws IOException {

            Comms2 userE = (Comms2) prop.geto1();
            String name = userE.getName();
            String password = userE.getPassword();
            if (uExist.containsKey(name)) {
                User user1 = uExist.get(name);
                if (user1.getPassword().equals(password)) {
                    CommandFor validate = new CommandFor("Win", user1);
                    object1.writeObject(validate);
                    object1.flush();

                } else {

                    CommandFor contradict = new CommandFor("Wrong", null);
                    object1.writeObject(contradict);
                    object1.flush();
                }
            } else {
                CommandFor deny = new CommandFor("Wrong", null);
                object1.writeObject(deny);
                object1.flush();

            }
        }

        /**
         * Shows the Postcode in Client window
         * @param prop
         * @param object1
         * @throws IOException
         */
        private synchronized void postcodesFromServer(CommandFor prop, ObjectOutputStream object1) throws IOException {

            CommandFor show = new CommandFor("postcodes", server.getPostcodes());


            object1.writeObject(show);
            object1.flush();

        }

        /**
         * Show the Dishes in Client Window
         * @param prop
         * @param object1
         * @throws IOException
         */
        private synchronized void dishesFromServer(CommandFor prop, ObjectOutputStream object1) throws IOException {

            CommandFor show = new CommandFor("dishes", d);
            object1.writeObject(show);
            object1.flush();
        }

        /**
         * Returns the Basket of the User
         * @param prop
         * @param object1
         * @throws IOException
         */
        private synchronized void clientB(CommandFor prop, ObjectOutputStream object1) throws IOException {

            User u1 = (User) prop.geto1();

            if (uExist.containsKey(u1.getName())) {
                HashMap<Dish, Number> basketc = uExist.get(u1.getName()).getBasket();

                object1.reset();
                CommandFor sucess = new CommandFor("True", basketc);
                object1.writeObject(sucess);
                object1.flush();

            } else {
                CommandFor failed = new CommandFor("False", null);
                object1.writeObject(failed);
                object1.flush();
            }


        }


        /**
         * Calculates the cost of the basket of the client
         * @param prop
         * @param object1
         * @throws IOException
         */
        private void clientBC(CommandFor prop, ObjectOutputStream object1) throws IOException {

            User  client1= (User) prop.geto1();

            if (uExist.containsKey(client1.getName())) {

                HashMap<Dish, Number> clientB = uExist.get(client1.getName()).getBasket();
                int costForTheBasket = 0;

                for (Map.Entry<Dish, Number> basketcost : clientB.entrySet()) {
                    costForTheBasket = costForTheBasket + (int)basketcost.getKey().getPrice() * (int) basketcost.getValue();
                    server.notifyUpdate();
                    }


                CommandFor successBasketCost = new CommandFor("True", costForTheBasket);
                object1.writeObject(successBasketCost);
                object1.flush();

            } else {
                CommandFor failure = new CommandFor("False", null);
                object1.writeObject(failure);
                object1.flush();

            }
        }

        /**
         * The method adds the present dish to the client's basket
         * @param prop
         * @param object1
         * @throws IOException
         */
        private synchronized void clientAddDB(CommandFor prop, ObjectOutputStream object1) throws IOException {

            String command = prop.geto1().toString();
            String client = command.split("\\|")[0];

            if (uExist.containsKey(client)) {

                User client1 = uExist.get(client);
                String a = command.split("\\|")[1];
                Integer b = Integer.parseInt(command.split("\\|")[2]);
                Dish addNew = null;

                for (Dish currentDish : d) {
                    if (currentDish.getName().equals(a)) {
                        addNew = currentDish;
                        break;
                    }
                }

                client1.basketForClient(addNew, b);
                CommandFor added = new CommandFor("True", null);

                object1.writeObject(added);
                object1.flush();

                client1.getBasket().put(addNew, b);

            }

        }

        /**
         * Returns orders
         * @param prop
         * @param object1
         * @throws IOException
         */
        private synchronized void bringOrd(CommandFor prop, ObjectOutputStream object1) throws IOException {
            User client1 = (User) prop.geto1();
            ArrayList<Order> newO = new ArrayList<>();
             for (Order newToAdd : server.getOrders()) {

                if (newToAdd.getUser().equals(client1))
                    newO.add(newToAdd);
            }


            CommandFor q = new CommandFor("True", newO);

            object1.reset();
            object1.writeObject(q);
            object1.flush();

        }

        /**
         * Show the latest changes in basket
         * @param prop
         * @param object1
         * @throws IOException
         */
        private synchronized void clientUB(CommandFor prop, ObjectOutputStream object1) throws IOException {

            User client = (User) prop.geto1();
            User client1 = uExist.get(client.getName());
            Dish bringD = (Dish) prop.geto2();
            Dish clientDish = null;


            for (Dish newAdd : d) {
                if (newAdd.getName().equals(bringD.getName())) {
                    clientDish = newAdd;
                    break;
                }
            }

            Number amount = (Number) prop.geto3();
            client1.getBasket().put(clientDish,amount);
            object1.writeObject(new CommandFor("True", null));
            object1.flush();
        }

        /**
         * Removes the basket
         * @param command
         * @param object1
         * @throws IOException
         */
        private synchronized void clearBasket(CommandFor command, ObjectOutputStream object1) throws IOException {

            User client = (User) command.geto1();
            User client2 = uExist.get(client.getName());
            client2.clearBasket();

            object1.writeObject(new CommandFor("True", null));
            object1.flush();

        }

        /**
         * Allows client to checkout
         * @param prop
         * @param object1
         * @throws IOException
         */
        private void clientChk(CommandFor prop, ObjectOutputStream object1) throws IOException {
            User client1 = (User) prop.geto1();
            User client2 = uExist.get(client1.getName());
            String writeIt ;
                Integer x = 0;
                writeIt=client2.getBasket().toString();
                String a=writeIt.substring(1,writeIt.length()-1);
                    if (serialNumber.containsKey(client1.getName())) {
                        x = serialNumber.get(client1.getName());
                    }
                    x++;
                    serialNumber.put(client1.getName(), x);
                    x++;
                    Order newOrd = new Order(x,client2,a,basketTOAdd(a));
                    o.add(newOrd);
                    server.addOrder(newOrd);
                    server.notifyUpdate();
                    CommandFor sending = new CommandFor("True", newOrd);
                    object1.writeObject(sending);
                    object1.flush();


        }

        /**
         * Returns the Status of the order
         * @param prop
         * @param object1
         * @throws IOException
         */
        private synchronized void clientOrdSt(CommandFor prop, ObjectOutputStream object1) throws IOException {

            String orderSt = "";

            Order ord1 = (Order) prop.geto1();

            for (Order newOrd : o) {
                if (newOrd.getName().equals(ord1.getName())&& newOrd.serialiseNumber().equals(ord1.serialiseNumber())) {

                    orderSt = server.getOrderStatus(newOrd);
                    break;

                }
            }

            CommandFor orderS = new CommandFor("True", orderSt);

            object1.writeObject(orderS);
            object1.flush();
        }

        /**
         * Returns the cost of the basket of client
         * @param prop
         * @param object1
         * @throws IOException
         */
        private synchronized void clientOC(CommandFor prop, ObjectOutputStream object1) throws IOException {

            Order ord2 = (Order) prop.geto1();
            int priceOrd = 0;

            for (Order newOrd : o) {
                if (newOrd.getName().equals(ord2.getName())&& newOrd.serialiseNumber().equals(ord2.serialiseNumber()) ) {
                    priceOrd = (int)newOrd.getCost();
                    break;
                }
            }
            CommandFor a = new CommandFor("True", priceOrd);

            object1.writeObject(a);
            object1.flush();

        }

        /**
         * Removes the order from the panel
         * @param prop
         * @param object1
         * @throws IOException
         */
        private synchronized void cancelOrder(CommandFor prop, ObjectOutputStream object1) throws IOException {

            Order ord1 = (Order) prop.geto1();

            for (Order newOrd : o) {
                if (newOrd.getName().equals(ord1.getName())&& newOrd.serialiseNumber().equals(ord1.serialiseNumber()) ) {
                    o.remove(newOrd);
                    break;

                }
            }

            CommandFor a = new CommandFor("True", null);
            object1.writeObject(a);
            object1.flush();
        }


        /**
         * returns true if the order is complete
         * @param prop
         * @param object1
         * @throws IOException
         */
        private synchronized void clientOrdComplete(CommandFor prop, ObjectOutputStream object1) throws IOException {

            Order ord1 = (Order) prop.geto1();

            Boolean inquiry = false;

            for (Order newOrd : o) {
                if (newOrd.getName().equals(ord1.getName())&& newOrd.serialiseNumber().equals(ord1.serialiseNumber())) {

                    inquiry = newOrd.getOrderComplete();
                    break;

                }
            }

            CommandFor a = new CommandFor("True", inquiry);
            object1.writeObject(a);
            object1.flush();
        }

        /**
         * adds to the basket
         * @param a
         * @return
         */
        public HashMap<Dish, Number> basketTOAdd(String a) {


            HashMap<Dish, Number> newOrd = new HashMap<>();
            String[] splitter = a.split(",");
            for (String splittingInto : splitter) {

                String orderD = splittingInto.split("=")[0].trim();
                String orderQ = splittingInto.split("=")[1].trim();

                Dish newD = null;
                for(Dish dish1 : d){
                    if (orderD.equals(dish1.getName())){
                        newD = dish1;
                    newOrd.put(newD, Integer.parseInt(orderQ));}
                    }


            }
            return newOrd;


        }
    }
    @Override
    public void run() {

        while (true) {

            try {
                Socket s = serverSocket.accept();
                Interaction currentThread = new Interaction(s);
                currentThread.start();
                commingClient.add(currentThread);

            } catch (IOException ioe) {

                System.out.println("Fault");
                ioe.printStackTrace();

            }
        }

    }

}


















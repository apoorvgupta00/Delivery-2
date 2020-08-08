package comp1206.sushi.common;

import java.io.Serializable;


public class Comms2 implements Serializable{

    /**
     * Member variables
     */
    private String n;
    private String p;

    /**
     * Constructor for the class
     * @param n
     * @param p
     */
    public Comms2(String n, String p){
        this.n=n;
        this.p=p;
    }

    /**
     * Return the password
     * @return
     */
    public String getPassword() {
        return p;
    }

    /**
     * Return the name
     * @return
     */
    public String getName() {
        return n;
    }
}

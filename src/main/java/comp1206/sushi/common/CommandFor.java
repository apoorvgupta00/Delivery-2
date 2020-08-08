
package comp1206.sushi.common;
import java.io.Serializable;
public class CommandFor implements Serializable {


    /**
     * Member variables
     */
    private String prop;
    private Object o1;
    private Object o2;
    private Object o3;

    /**
     * Constructor for the class
     * @param x
     * @param o1
     */
    public CommandFor(String x, Object o1) {
        this.prop = x;
        this.o1 = o1;
    }

    /**
     * Overloaded Constructor for the class
     * @param x
     * @param o1
     * @param o2
     * @param o3
     */
    public CommandFor(String x,Object o1,Object o2,Object o3){
        this.prop=x;
        this.o1=o1;
        this.o2=o2;
        this.o3=o3;
    }

    /**
     * Returns o1
     * @return
     */
    public Object geto1(){
        return o1;
    }

    /**
     * Returns o2
     * @return
     */
    public Object geto2() {
        return o2;
    }

    /**
     * Returns o3
     * @return
     */
    public Object geto3() {
        return o3;
    }

    /**
     * Returns prop
     * @return
     */
    public String getProp() {
        return prop;
    }

    /**
     * Set the object
     * @param o1
     */
    public void setObject(Object o1) {
        this.o1 = o1;
    }

    /**
     * Set the prop
     * @param prop
     */
    public void setProp(String prop) {
        this.prop = prop;
    }
}

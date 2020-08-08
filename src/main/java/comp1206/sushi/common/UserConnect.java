package comp1206.sushi.common;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;

public class UserConnect implements Serializable {
    /**
     * Member Variables for the class
     */
    private Socket s = null;
    private ObjectOutputStream writeout;
    private ObjectInputStream takein;
    private CommandFor prop;

    /**
     * Constructor for the class
     */
    public UserConnect() {

        // Connects
        try {
            s = new Socket("127.0.0.1", 52325);
            writeout = new ObjectOutputStream(s.getOutputStream());
            takein = new ObjectInputStream(s.getInputStream());
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

    }


    /**
     * receives the message
     * @return
     */
    public synchronized CommandFor receiveMessage() {

        return prop;

    }

    /**
     * sends the message
     * @param message
     */
    public synchronized void sendMessage(CommandFor message) {
        try {
            writeout.writeObject(message);
            writeout.flush();
            prop = (CommandFor) takein.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }
}

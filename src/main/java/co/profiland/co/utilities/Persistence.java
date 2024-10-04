package co.profiland.co.utilities;

import java.io.*;

public class Persistence implements Serializable {
    
    private static Persistence instance;
    private static final long serialVersionUID = 1L;


    // Singleton Pattern
    private Persistence() {
    }

    public static Persistence getInstance() {
        if (instance == null) {
            instance = new Persistence();
        }
        return instance;
    }


    public void serializeObjectXML(String file, Object object) throws IOException {
        try (ObjectOutputStream exit = new ObjectOutputStream(new FileOutputStream(file))) {
            exit.writeObject(object);
        } catch (IOException e) {
            throw e;
        }
    }

    // Deserialize an object in XML format
    public Object deserializeObjectXML(String file) throws IOException, ClassNotFoundException {
        try (ObjectInputStream entrance = new ObjectInputStream(new FileInputStream(file))) {
            return entrance.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw e;
        }
    }

    // Serialize an object in DAT format
    public void serializeObject(String file, Object object) throws IOException {
        try (ObjectOutputStream exit = new ObjectOutputStream(new FileOutputStream(file))) {
            exit.writeObject(object);
        } catch (IOException e) {
            throw e;
        }
    }

    // Deserialize an object in DAT format
    public Object deserializeObject(String file) throws IOException, ClassNotFoundException {
       
        try (ObjectInputStream entrance = new ObjectInputStream(new FileInputStream(file))) {
            return entrance.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw e;
        }
    }
}

package co.profiland.co.utilities;

import java.io.*;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;


@Slf4j
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

    public void initializeXmlFile(String pathcito, Object obj) {
        File file = new File(pathcito);
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                serializeObjectXML(pathcito, obj);
            } catch (IOException e) {
                log.error("Failed to initialize XML file", e);
            }
        }
    }

    // Serialize an object to XML format
    public void serializeObjectXML(String file, Object object) throws IOException {
        try (ObjectOutputStream exit = new ObjectOutputStream(new FileOutputStream(file))) {
            exit.writeObject(object);
        } catch (IOException e) {
            throw e;
        }
    }

    // Deserialize an object from XML format
    public Object deserializeObjectXML(String file) throws IOException, ClassNotFoundException {
        try (ObjectInputStream entrance = new ObjectInputStream(new FileInputStream(file))) {
            return entrance.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw e;
        }
    }

    // Serialize an object to DAT format
    public void serializeObject(String file, Object object) throws IOException {
        try (ObjectOutputStream exit = new ObjectOutputStream(new FileOutputStream(file))) {
            exit.writeObject(object);
        } catch (IOException e) {
            throw e;
        }
    }

    // Deserialize an object from DAT format
    public Object deserializeObject(String file) throws IOException, ClassNotFoundException {
        try (ObjectInputStream entrance = new ObjectInputStream(new FileInputStream(file))) {
            return entrance.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw e;
        }
    }

    // Utility Method: Convert any object to JSON string
    public String convertToJson(Object object) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(object);
    }
}
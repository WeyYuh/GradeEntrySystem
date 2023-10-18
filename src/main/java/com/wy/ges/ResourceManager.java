package com.wy.ges;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * This class is used to save or load serialized object in the file
 */
public class ResourceManager {
    /**
     * This method is used to save the serialized object to the file
     * @param data - serialized object
     * @param fileName - name of the save file
     * @throws Exception
     */
    public static void save(Serializable data, String fileName) throws Exception {
        try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(Paths.get(fileName)))) {
            oos.writeObject(data); // write data to file
        }
    }

    /**
     * This method is used to load the serialized object from the file
     * @param fileName - name of the save file
     * @return serialized object read from the file
     * @throws Exception
     */
    public static Object load(String fileName) throws Exception {
        try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(Paths.get(fileName)))) {
            return ois.readObject(); // return the data read from file
        }
    }
}

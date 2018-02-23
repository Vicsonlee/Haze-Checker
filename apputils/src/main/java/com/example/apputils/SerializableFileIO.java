package com.example.apputils;


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Wrapper for ObjectInput/OutputStream, provides nicer function calls for Serializable-File operations
 */
public class SerializableFileIO {

    /**
     * Saves a Serializable to a FileOutputStream
     * @param serialObj  Serializable to be saved, not null safe
     * @param outputStream FileOutputStream of destination file, not null safe
     */
    public static void save(Serializable serialObj, FileOutputStream outputStream) throws IOException{
        ObjectOutputStream oos = new ObjectOutputStream(outputStream);
        oos.writeObject(serialObj);
        if(oos != null){
            oos.close();
        }
    }

    /**
     * Loads a Serializable from a FileInputStream
     * @param inputStream  FileInputStream of the file to load from, not null safe
     * @return Serializable object
     */
    public static Serializable load(FileInputStream inputStream)
            throws IOException,ClassNotFoundException{
        ObjectInputStream ois = new ObjectInputStream(inputStream);
        return (Serializable) ois.readObject();
    }
}

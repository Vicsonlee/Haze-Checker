package com.example.apputils;


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class SerializableFileIO {

    public static void save(Serializable serialObj, FileOutputStream outputStream) throws IOException{
        ObjectOutputStream oos = new ObjectOutputStream(outputStream);
        oos.writeObject(serialObj);
        if(oos != null){
            oos.close();
        }
    }

    public static Serializable load(FileInputStream inputStream)
            throws IOException,ClassNotFoundException{
        ObjectInputStream ois = new ObjectInputStream(inputStream);
        return (Serializable) ois.readObject();
    }
}

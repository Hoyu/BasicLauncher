package com.example.darkm_000.basiclauncher.serialize;

import android.content.Context;

import com.example.darkm_000.basiclauncher.MainActivity;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;

/**
 * Created by darkm_000 on 10/06/2015.
 */
public class Serialization {

    public static void serializeData(SerializableData serializableData){

        FileOutputStream fos;
        try{

            fos = MainActivity.getActivity().openFileOutput("data", Context.MODE_PRIVATE);
            ObjectOutputStream objectOutputStream=new ObjectOutputStream(fos);
            objectOutputStream.writeObject(serializableData);
            objectOutputStream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static SerializableData loadSerializableData(){

        ObjectInputStream objectInputStream=null;

        try{

            objectInputStream=new ObjectInputStream(MainActivity.getActivity().openFileInput("data"));
            Object obj=objectInputStream.readObject();

            if(obj instanceof SerializableData)
                return (SerializableData) obj;
            else
                return null;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }finally {
            if(objectInputStream!=null)
                try {
                    objectInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }

        //If everything fails
        return null;
    }
}

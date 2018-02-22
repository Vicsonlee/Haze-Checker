package com.assignment.hazechecker;

import android.content.Context;
import android.util.Log;

import com.example.apputils.AsyncHttpGetTask;
import com.example.apputils.ResponseListener;
import com.example.apputils.SerializableFileIO;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainController implements ResponseListener {

    private MainActivity main;
    private AsyncHttpGetTask queryTask;

    public MainController(MainActivity main){
        this.main = main;
    }

    public void startQuery(){
        queryTask = new AsyncHttpGetTask(this);
        queryTask.execute("https://api.data.gov.sg/v1/environment/psi");
    }

    public void cancelQuery(){
        if (queryTask != null) {
            queryTask.cancel(true);
            queryTask = null;
        }
    }

    public void taskComplete(Object response){
        Log.d("controller","Task completed");
        if (response != null) {
            Map<String, String> psi = getIndex((String) response, "psi_twenty_four_hourly");
            Map<String, String> pm25 = getIndex((String) response, "pm25_twenty_four_hourly");
            main.setPsiValues(psi);
            main.setPm25Values(pm25);

            DateFormat displayFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            DateFormat fileFormat = new SimpleDateFormat("dd-MM-yyyy_HH:mm:ss");
            Date curTime = new Date();
            main.setTimestamp(displayFormat.format(curTime));

            ArrayList<Map> indexPackage = new ArrayList<>();
            indexPackage.add(psi);
            indexPackage.add(pm25);
            clearData();
            saveData(fileFormat.format(curTime),indexPackage);
            main.refresh();
        }
        else{
            main.timeout();
        }
    }

    private Map<String,String> getIndex(String rawJSON, String key){
        Map<String,String> indexMap = new HashMap<String, String>();
        String indexName;

        try {
            JSONObject indexes = new JSONObject(rawJSON)
                                    .getJSONArray("items")
                                    .getJSONObject(0)
                                    .getJSONObject("readings")
                                    .getJSONObject(key);
            for(int i = 0; i<indexes.names().length(); i++){
                indexName = indexes.names().getString(i);
                indexMap.put(indexName,indexes.getString(indexName));
            }
            return indexMap;
        } catch (JSONException e){
            Log.d("controller","JSON Exception");
            Log.d("controller",Log.getStackTraceString(e));
            return null;
        }
    }

    public void saveData(String filename, Serializable data){
        try {
            FileOutputStream outputStream = main.openFileOutput(filename, Context.MODE_PRIVATE);
            SerializableFileIO.save(data, outputStream);
            Log.d("controller","File saved to " + filename);
        }
        catch (IOException e){
            Log.d("controller",Log.getStackTraceString(e));
        }
    }

    public void clearData(){
        String[] files = main.fileList();
        if (files.length < 1) {
            Log.d("controller","Clear - No files found.");
            return;
        }
        for(int i = 0; i< files.length; i++){
            File file = new File(main.getFilesDir(),files[i]);
            if (file.exists()){
                file.delete();
                Log.d("controller", "Deleted file " + files[i]);
            }
        }
    }

    public void loadData(){
        try {
            String[] files = main.fileList();
            int fileCount = files.length;
            if (fileCount < 1) {
                Log.d("controller","Load - No files found.");
                return;
            }
            // open the last file - usually the latest timestamp
            Log.d("controller","Reading file " + files[fileCount-1]);
            FileInputStream inputStream = main.openFileInput(files[fileCount-1]);
            ArrayList<Map> indexPackage = (ArrayList) SerializableFileIO.load(inputStream);

            if (indexPackage != null) {
                Map<String, String> psi = (Map<String, String>) indexPackage.get(0);
                Map<String, String> pm25 = (Map<String, String>) indexPackage.get(1);
                String timestamp = files[0].replace('_', ' ');

                main.setPsiValues(psi);
                main.setPm25Values(pm25);
                main.setTimestamp(timestamp);
            }
        }
        catch (IOException | ClassNotFoundException e){
            Log.d("controller",Log.getStackTraceString(e));
        }
    }
}

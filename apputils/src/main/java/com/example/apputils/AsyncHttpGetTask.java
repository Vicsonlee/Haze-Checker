package com.example.apputils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Map;
import android.os.AsyncTask;
import android.util.Log;


public class AsyncHttpGetTask extends AsyncTask<String, Void, String> {

    private String serverResponse;
    private ResponseListener listener;
    private Map<String,String> headers;
    private Map<String,String> queryParams;

    public AsyncHttpGetTask(ResponseListener listener) {
        this.listener = listener;
    }

    public void setHeaders(Map<String,String> headers){
        this.headers = headers;
    }

    public void setQueryParams(Map<String,String> params){
        this.queryParams = params;
    }

    @Override
    protected String doInBackground(String... params) {

        URL url;
        HttpURLConnection urlConnection = null;
        try {
            if (queryParams != null) {
                ArrayList<String> paramList = new ArrayList<String>();
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    paramList.add(entry.getKey());
                    paramList.add(entry.getValue());
                }
                url = applyParameters(params[0], paramList.toArray(new String[paramList.size()]));
            }
            else {
                url = new URL(params[0]);
            }
            Log.d("async debug",url.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(5000); // 5 sec
            urlConnection.setReadTimeout(10000); // 10 sec

            if (headers != null) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    urlConnection.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }

            for (String header : urlConnection.getRequestProperties().keySet()) {
                if (header != null) {
                    for (String value : urlConnection.getRequestProperties().get(header)) {
                        Log.d("async debug",header + ":" + value);
                    }
                }
            }

            InputStream in = urlConnection.getInputStream();
            serverResponse = readStream(in);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            serverResponse = null;
        }
        finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return serverResponse;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        listener.taskComplete(serverResponse);
    }

    private String readStream(InputStream in) {
        BufferedReader reader = null;
        StringBuffer response = new StringBuffer();
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return response.toString();
    }

    private URL applyParameters(String baseUrl, String[] urlParameters){
        StringBuilder query = new StringBuilder();
        boolean first = true;
        for (int i = 0; i < urlParameters.length; i += 2) {
            if (first) {
                query.append("?");
                first = false;
            } else {
                query.append("&");
            }
            try {
                query.append(urlParameters[i]).append("=")
                        .append(URLEncoder.encode(urlParameters[i + 1], "UTF-8"));
            } catch (UnsupportedEncodingException ex) {
                throw new RuntimeException(ex);
            }
        }
        try {
            return new URL(baseUrl + query.toString());
        } catch (MalformedURLException ex) {
            throw new RuntimeException(ex);
        }
    }
}

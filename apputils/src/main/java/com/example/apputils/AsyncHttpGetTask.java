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

/**
 * Class for making Async HTTP GET requests, must be passed a ResponseListener when constructed
 * start task using execute("<base_url>");
 * will return null if request fails
 */
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
                // add params
                ArrayList<String> paramList = new ArrayList<String>();
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    paramList.add(entry.getKey());
                    paramList.add(entry.getValue());
                }
                url = applyParameters(params[0], paramList.toArray(new String[paramList.size()]));
                // when execute("url") is called, the url is passed to param[0]
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

    /**
     * Helper function to get String from an InputStream
     * @param in InputStream object, not null safe
     * @return String from InputStream
     */
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

    /**
     * Applies key-value parameter pairs to a url string and returns a URL object
     * @param baseUrl  Base URL string, not null safe
     * @param urlParameters Array of parameters in the form of [key1, value1, key2, value2, ...]
     *                      Throws IndexOutOfBoundsException if number of elements is not even
     * @return URL object
     */
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

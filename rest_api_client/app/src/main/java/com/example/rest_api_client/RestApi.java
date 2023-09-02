package com.example.rest_api_client;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class RestApi {
    private static final String TAG = "RestApi";
    private static final int REQUEST_TIMEOUT = 10000; // milliseconds
    private final ExecutorService executor;
    private final Handler handler;

    public RestApi(Looper mainLooper) {
        executor = Executors.newSingleThreadExecutor();
        handler = new Handler(mainLooper);
    }

    public void get(URL url, Consumer<JSONArray> callbackOnSuccess, Consumer<Exception> callbackOnError, Runnable onFinally) {
        executor.execute(() -> { // run network request in thread
            try {
                JSONArray responseBody = get(url);
                runOnUIThread(() -> callbackOnSuccess.accept(responseBody)); // pass response to callback to update UI
            } catch (IOException | JSONException e) {
                runOnUIThread(() -> callbackOnError.accept(e)); // pass error to callback to update UI
            } finally {
                runOnUIThread(onFinally); // run callback to update UI
            }
        });
    }

    public void post(URL url, JSONObject body, Consumer<JSONObject> callbackOnSuccess, Consumer<Exception> callbackOnError, Runnable onFinally) { // run network request in thread
        executor.execute(() -> { // run network request in thread
            try {
                JSONObject responseBody = post(url, body);
                runOnUIThread(() -> callbackOnSuccess.accept(responseBody)); // pass response to callback to update UI
            } catch (IOException | JSONException e) {
                runOnUIThread(() -> callbackOnError.accept(e)); // pass error to callback to update UI
            } finally {
                runOnUIThread(onFinally); // run callback to update UI
            }
        });
    }

    private JSONArray get(URL url) throws IOException, JSONException {
        Log.d(TAG, "Getting request: " + url);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        connection.setConnectTimeout(REQUEST_TIMEOUT);

        int responseCode = connection.getResponseCode();
        Log.d(TAG, "Response code for get request " + responseCode);

        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();

            String inputLine;
            while ((inputLine = in.readLine()) != null) response.append(inputLine);

            in.close();
            return new JSONArray(response.toString());
        }

        throw new IOException("GET request failed with response code: " + responseCode);
    }

    private JSONObject post(URL url, JSONObject body) throws IOException, JSONException {
        Log.d(TAG, "Posting request:" + url + " with body: " + body);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json"); // tells the server that the request body is JSON
        connection.setRequestProperty("Accept", "application/json"); // tells us that the response body is JSON

        connection.setConnectTimeout(REQUEST_TIMEOUT);

        connection.setDoOutput(true); // allows us to send the JSON body to the server

        String jsonString = body.toString();
        // send the JSON body to the server
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonString.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        int responseCode = connection.getResponseCode();
        Log.d(TAG, "Response code for post request" + responseCode);

        if (responseCode == HttpURLConnection.HTTP_OK) {
            Log.d(TAG, "Parsing response for post request");
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();

            String inputLine;
            while ((inputLine = in.readLine()) != null) response.append(inputLine);

            in.close();
            Log.d(TAG, "Finished parsing response for post request");
            return new JSONObject(response.toString());
        }

        throw new IOException("POST request failed with response code: " + responseCode);

    }

    public void runOnUIThread(Runnable fn) {
        handler.post(fn);
    }

    public void close() {
        executor.shutdown();
    }
}

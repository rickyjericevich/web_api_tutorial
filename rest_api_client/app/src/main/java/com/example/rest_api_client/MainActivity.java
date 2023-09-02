package com.example.rest_api_client;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.rest_api_client.databinding.ActivityMainBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final String IP = "147.182.235.242";
    // private static final String IP = "localhost";
    private static final int PORT = 42069;
    private static final String URL = "http://" + IP + ":" + PORT + "/messages"; // the address of the api server
    public static final int DELAY = 2000;
    private URL serverUrl;
    private ActivityMainBinding binding;
    private RestApi restApi;
    private View tmpMessageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        restApi = new RestApi(getMainLooper());

        try {
            serverUrl = new URL(URL);
            restApi.get(serverUrl, this::onMessagesSuccessfullyReceived, this::onMessagesUnsuccessfullyReceived, this::onAfterMessagesReceivedSuccessfullyOrNot); // repeatedly get messages when the page loads
            binding.btnSend.setOnClickListener(this::onSendButtonPressed); // run the onSendButtonPressed function when the Send button is clicked
        } catch (MalformedURLException e) {
            Log.e(TAG, "onCreate: server URI could not be created", e);
            showUserAMessage("Server URL is invalid");
            binding.btnSend.setEnabled(false);
        }
    }

    private void onSendButtonPressed(View view) {
        if (isInputsValid()) {
            try {
                binding.btnSend.setEnabled(false); // prevent user from submitting another message while the previous one is being sent

                JSONObject tmpMessage = new JSONObject("{'username':'" + binding.txtUsername.getText().toString().trim() + "','message':'" + binding.txtMsg.getText().toString().trim() + "'}"); // build JSON body from form data
                Log.d(TAG, "Sending message: " + tmpMessage);

                tmpMessageView = generateMessageView(tmpMessage); // create view to display the message temporarily until the server responds

                binding.llMsgs.addView(tmpMessageView); // add the temporary message view to the bottom of the messages list
                binding.llMsgs.invalidate(); // refresh the view

                // the API server will create a new message if we send it a post request with JSON body of the form { username: "username", message: "message" }
                restApi.post(serverUrl, tmpMessage, this::onMessageSuccessfullySent, this::onMessageUnsuccessfullySent, this::onAfterMessageSentSuccessfullyOrNot); // send the POST request to the API server to create the message
            } catch (JSONException e) {
                Log.e(TAG, "onSendButtonPressed: Error creating tmpMessage JSONObject", e);
            }
        }
    }

    private boolean isInputsValid() {
        if (binding.txtUsername.getText().toString().trim().isEmpty()) {
            binding.txtUsername.setError("Please enter a username"); // alert the user that there was an error when sending the message
            return false;
        }

        if (binding.txtMsg.getText().toString().trim().isEmpty()) {
            binding.txtMsg.setError("Please enter a message");
            return false;
        }

        return true;
    }

    private void onMessagesSuccessfullyReceived(JSONArray messages) { // the server will respond with an array of message objects in JSON format
        Log.i(TAG, "Received messages: " + messages);
        // sort messages by timestamp in ascending order

        binding.llMsgs.removeAllViews();

        for (int i = 0; i < messages.length(); i++) {
            try {
                View messageView = generateMessageView(messages.getJSONObject(i)); // convert each message to a view
                binding.llMsgs.addView(messageView);// replace any currently-displayed message views with these new ones
            } catch (JSONException e) {
                Log.e(TAG, "Could not get JSON message at index " + i, e);
            }
        }

        binding.llMsgs.invalidate(); // refresh the view
    }

    private void onMessagesUnsuccessfullyReceived(Exception e) {
        Log.e(TAG, "Error while receiving messages", e); // if the server responds with an error, print the error to the console
    }

    private void onAfterMessagesReceivedSuccessfullyOrNot() { // this code will run whether the request was successful or not
        // get messages again after DELAY milliseconds regardless of whether the GET request was successful or not
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            restApi.get(serverUrl, this::onMessagesSuccessfullyReceived, this::onMessagesUnsuccessfullyReceived, this::onAfterMessagesReceivedSuccessfullyOrNot);
        }, DELAY);
    }

    private void onMessageSuccessfullySent(JSONObject message) { // the server will respond with a message object which means the message was successfully created
        Log.i(TAG, "Message successfully sent: " + message);

        binding.txtMsg.setText(""); // clear the message input's text so that the user can enter a new message

        // recreate the message view using the server's response
        View messageView = generateMessageView(message);

        // add the message view to the bottom of the messages list
        binding.llMsgs.addView(messageView);
        binding.llMsgs.invalidate(); // refresh the view
    }

    private void onMessageUnsuccessfullySent(Exception e) {  // if an error occurs, it means the message was not successfully created
        Log.e(TAG, "Error while sending message", e);
        showUserAMessage(e.toString()); // alert the user that there was an error when sending the message
        // do not clear the message input's text so that the user can try sending the message again
    }

    private void onAfterMessageSentSuccessfullyOrNot() { // this code will run whether the request was successful or not
        binding.llMsgs.removeView(tmpMessageView); // remove tmpMessageView from list of messages
        binding.llMsgs.invalidate(); // refresh the view
        binding.btnSend.setEnabled(true);  // allow the user to submit another message
    }

    private void showUserAMessage(String text) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }

    private View generateMessageView(JSONObject messageJson) {
        // messages received from the server have a timestamp field included
        // temporary messages (ie messages that are still being sent to the API server) created by the code above do not have a timestamp field
        // so we can use the presence of the timestamp field to determine whether the message is temporary or not
        // a temporary message must be displayed with grey text to show the user that the message has not yet been created in the API server
        // a message received from the API server must be displayed with black text to show the user that the message has been successfully created in the API server

        int color = messageJson.has("timestamp") ? new TextView(this).getCurrentTextColor() : Color.GRAY;

        // create UI element for the username
        TextView tvUsername = new TextView(this);
        tvUsername.setTextColor(color);
        tvUsername.setText(getStringValueFromJSONObject("username", messageJson));
        tvUsername.setTypeface(tvUsername.getTypeface(), Typeface.BOLD_ITALIC);

        // create UI element for the message
        TextView tvMessage = new TextView(this);
        tvMessage.setTextColor(color);
        tvMessage.setText(getStringValueFromJSONObject("message", messageJson));

        // create UI element for the timestamp
        TextView tvTimestamp = new TextView(this);
        tvTimestamp.setTextColor(color);
        Date timestamp = getDateValueFromJSONObject("timestamp", messageJson);
        tvTimestamp.setText(timestamp == null ? null : timestamp.toString());

        // create UI element for the parent element that will contain the username, message and timestamp
        LinearLayout parentView = new LinearLayout(this);
        parentView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        parentView.setOrientation(LinearLayout.VERTICAL);
        parentView.setPadding(10, 10, 10, 10);
        parentView.addView(tvUsername);
        parentView.addView(tvMessage);
        parentView.addView(tvTimestamp);

        return parentView;
    }

    public String getStringValueFromJSONObject(String name, JSONObject json) {
        try {
            return json.getString(name);
        } catch (JSONException e) {
            Log.e(TAG, "Couldn't get String value from jsonObject using key: " + name, e);
        }
        return "Error - could not parse '" + name + "'";
    }

    @SuppressLint("NewApi")
    public Date getDateValueFromJSONObject(String name, JSONObject json) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSz"); // ISO 8601 datetime string
        try {
            return format.parse(json.getString(name));
        } catch (JSONException | ParseException e) {
            Log.d(TAG, "Couldn't get Date value from jsonObject using key: " + name, e);
            return null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        restApi.close();
    }
}
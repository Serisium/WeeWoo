package com.ggreenwood.weewoo;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.esri.android.geotrigger.GeotriggerApiClient;
import com.esri.android.geotrigger.GeotriggerApiListener;
import com.esri.android.geotrigger.GeotriggerService;

import org.json.JSONException;
import org.json.JSONObject;

import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;


public class MainActivity extends ActionBarActivity {

    private final String TAG = "WEEEWOOO";

    WebView webView;
    private final WebSocketConnection mConnection = new WebSocketConnection();

    TextView textViewLog;
    Button buttonPrevious;
    Button buttonPlay;
    Button buttonNext;

    // Create a new application at https://developers.arcgis.com/en/applications
    private static final String AGO_CLIENT_ID = "XmJXiQ7hjU1ucS1R";

    // The project number from https://cloud.google.com/console
    private static final String GCM_SENDER_ID = "987118115893";

    // A list of initial tags to apply to the device.
    // Triggers created on the server for this application, with at least one of these same tags,
    // will be active for the device.
    private static final String[] TAGS = new String[] {"house", "some_tag", "another_tag"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewLog = (TextView) findViewById(R.id.textViewLog);
        buttonPrevious = (Button) findViewById(R.id.buttonPrevious);
        buttonPlay = (Button) findViewById(R.id.buttonPlay);
        buttonNext = (Button) findViewById(R.id.buttonNext);
        webView = (WebView) findViewById(R.id.webView);


        GeotriggerService.setLoggingLevel(android.util.Log.DEBUG);
        GeotriggerHelper.startGeotriggerService(this, AGO_CLIENT_ID, GCM_SENDER_ID, TAGS,
                GeotriggerService.TRACKING_PROFILE_FINE);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onConnect(View v) {
        final String wsuri = ((TextView) findViewById(R.id.editTextURL)).getText().toString();

        try {
            mConnection.connect("ws://" + wsuri + ":1234", new WebSocketHandler() {

                @Override
                public void onOpen() {
                    log("Status: Connected to " + wsuri);
                    mConnection.sendTextMessage("Hello, world!");
                    buttonPrevious.setEnabled(true);
                    buttonPlay.setEnabled(true);
                    buttonNext.setEnabled(true);
                }

                @Override
                public void onTextMessage(String payload) {
                    log("Got echo: " + payload);
                }

                @Override
                public void onClose(int code, String reason) {
                    log("Connection lost.");
                    buttonPrevious.setEnabled(false);
                    buttonPlay.setEnabled(false);
                    buttonNext.setEnabled(false);
                }
            });
        } catch (WebSocketException e) {

            log(e.toString());
        }

        webView.setWebChromeClient(new WebChromeClient());
        webView.loadUrl("http://" + wsuri + ":8000");

    }

    public void onPrevious(View v) {
        if(mConnection != null)
            mConnection.sendTextMessage("previous");
    }

    public void onNext(View v) {
        if(mConnection != null)
            mConnection.sendTextMessage("next");
    }

    public void onPlay(View v) {
        if(mConnection != null && mConnection.isConnected())
            mConnection.sendTextMessage("play");

    }

    public void log(String message) {
        Log.d(TAG, message);
        textViewLog.setText(message + "\n" + textViewLog.getText());
    }
}

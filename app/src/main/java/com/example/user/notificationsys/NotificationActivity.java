package com.example.user.notificationsys;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class NotificationActivity extends AppCompatActivity {
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "NotificationActivity";

    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private ProgressBar mRegistrationProgressBar;
    private TextView mInformationTextView;

    Button sendButton;

    Spinner categorySpinner;
    EditText messageText;

    String categoryString ;
    String messageString;

    TextView personText;

    Button logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(QuickstartPreferences.MY_SHARED_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("Category", QuickstartPreferences.getNullPerson().getCategory());
        editor.commit();
        categorySpinner = (Spinner) findViewById(R.id.category_spinner);
        messageText = (EditText) findViewById(R.id.message_text);
        sendButton = (Button) findViewById(R.id.send_button);
        mRegistrationProgressBar = (ProgressBar) findViewById(R.id.registrationProgressBar);
        personText = (TextView) findViewById(R.id.person_text);
        personText.setText(QuickstartPreferences.getNullPerson().getName()+"\n"+QuickstartPreferences.getNullPerson().getCategory());
        logoutButton = (Button) findViewById(R.id.logout_button);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mRegistrationProgressBar.setVisibility(ProgressBar.GONE);
                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences
                        .getBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false);
                if (sentToken) {
                    mInformationTextView.setText(getString(R.string.gcm_send_message));
                } else {
                    mInformationTextView.setText(getString(R.string.token_error_message));
                }
            }
        };
        mInformationTextView = (TextView) findViewById(R.id.informationTextView);

        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryString =String.valueOf(categorySpinner.getSelectedItem().toString());
                messageString = messageText.getText().toString();
                new MessageSentTask().execute(messageString, categoryString);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    private class MessageSentTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            publishProgress("Sending");
            String resp="?";
            try {
                // Prepare JSON containing the GCM message content. What to send and where to send.
                JSONObject jGcmData = new JSONObject();
                JSONObject jData = new JSONObject();
                try {
                    jData.put("message", params[0]);
                    jGcmData.put("to", "/topics/"+params[1]);
                    jGcmData.put("data", jData);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // Create connection to send GCM Message request.
                URL url = new URL("https://android.googleapis.com/gcm/send");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Authorization", "key=" + QuickstartPreferences.API_KEY);
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);

                // Send GCM message content.
                OutputStream outputStream = conn.getOutputStream();
                outputStream.write(jGcmData.toString().getBytes());

                // Read GCM response.
                InputStream inputStream = conn.getInputStream();
                resp = IOUtils.toString(inputStream);
                System.out.println(resp);
                System.out.println("Check your device/emulator for notification or logcat for " +
                        "confirmation of the receipt of the GCM message.");
            } catch (IOException e) {
                System.out.println("Unable to send GCM message.");
                System.out.println("Please ensure that API_KEY has been replaced by the server " +
                        "API key, and that the device's registration token is correct (if specified).");
                e.printStackTrace();
            }
            return resp;
        }

        @Override
        protected void onProgressUpdate(String... progress) {
            Toast.makeText(getApplicationContext(), progress[0], Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onPostExecute(String response) {
            Toast.makeText(getApplicationContext(),response,Toast.LENGTH_LONG).show();
        }
    }
}

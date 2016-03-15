package com.example.user.notificationsys;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class RegisterActivity extends AppCompatActivity {

    EditText email,name,pass;
    Button registerButton;
    Button loginButton;
    Spinner categorySpinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        email = (EditText) findViewById(R.id.email_text);
        name = (EditText) findViewById(R.id.name_text);
        pass = (EditText) findViewById(R.id.pass_text);
        loginButton = (Button) findViewById(R.id.login_button);
        registerButton = (Button) findViewById(R.id.register_button);
        categorySpinner = (Spinner) findViewById(R.id.category_spinner);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String emailString= email.getText().toString(), passString = pass.getText().toString(), nameString = name.getText().toString(), categoryString = String.valueOf(categorySpinner.getSelectedItem());
                String data = "";
                try {
                    data = URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(emailString, "UTF-8");
                    data += "&" + URLEncoder.encode("pass", "UTF-8") + "=" + URLEncoder.encode(passString, "UTF-8");
                    data += "&" + URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(nameString, "UTF-8");
                    data += "&" + URLEncoder.encode("category","UTF-8") + "="+ URLEncoder.encode(categoryString,"UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                RegistrationTask registrationTask = new RegistrationTask();
                registrationTask.execute(data);

/*
                Intent intent = new Intent(RegisterActivity.this, NotificationActivity.class);
                startActivity(intent);
*/
            }
        });
    }

    private class RegistrationTask extends AsyncTask<String,String,String> {

        @Override
        protected String doInBackground(String... params) {
            publishProgress("Connecting....");
            String response="WHAT?";
            BufferedReader bufferedReader= null;
            // Send data
            try
            {
                // Defined URL  where to send data
                URL url = new URL("http://192.168.1.57/notificationsysphp/register.php");
                // Send POST data request
                URLConnection conn = url.openConnection();
                conn.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(params[0]);
                wr.flush();
                // Get the server response
                bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line = null;
                // Read Server Response
                while((line = bufferedReader.readLine()) != null)
                {
                    // Append server response in string
                    sb.append(line + "\n");
                }
                response = sb.toString();
            }
            catch(Exception ex)
            {
                ex.printStackTrace();
            }
            finally
            {
                try
                {
                    bufferedReader.close();
                }
                catch(Exception ex) {}
            }
            return response;
        }
        @Override
        protected void onProgressUpdate(String... progress) {
            Toast.makeText(getApplicationContext(), progress[0], Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onPostExecute(String response) {
            try {
                JSONArray jsonArraydata = new JSONArray(response);
                List<JSONObject> jsonObjectList = new ArrayList<JSONObject>();
                for (int i=0; i<jsonArraydata.length();i++){
                    JSONObject jsonObject = jsonArraydata.getJSONObject(i);
                    jsonObjectList.add(jsonObject);
                }
                String string="";
                for (int i=0; i<jsonObjectList.size();i++){
                    string+=jsonObjectList.get(i);
                    string+="\n";
                }
                Toast.makeText(getApplicationContext(),string,Toast.LENGTH_LONG).show();
                Intent intent = new Intent(RegisterActivity.this, NotificationActivity.class);
                startActivity(intent);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            SharedPreferences preferences = getSharedPreferences(QuickstartPreferences.MY_SHARED_PREFERENCE, MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
//            editor.putString("Category","CategoryName");
            editor.commit();
        }
    }


}

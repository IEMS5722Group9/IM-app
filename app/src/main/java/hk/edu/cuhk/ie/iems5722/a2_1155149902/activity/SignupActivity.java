package hk.edu.cuhk.ie.iems5722.a2_1155149902.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import hk.edu.cuhk.ie.iems5722.a2_1155149902.R;
import hk.edu.cuhk.ie.iems5722.a2_1155149902.util.UrlUtil;

public class SignupActivity extends AppCompatActivity {
    private String baseUrl = UrlUtil.BaseUrl;
    private String registerUrl = baseUrl + "/api/a3/register_user";
    private boolean usernameExist = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button signupButton = findViewById(R.id.btn_signup);
        EditText user = findViewById(R.id.user_input);
        EditText password = findViewById(R.id.password_input);
        EditText password_confirm = findViewById(R.id.password_confirm);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(user.getText()) && !TextUtils.isEmpty(password.getText()) && !TextUtils.isEmpty(password_confirm.getText())) {
                    if(password.getText().toString().equals(password_confirm.getText().toString()))
                        new registerTask().execute(user.getText().toString(), password.getText().toString());
                    else {
                        Toast.makeText(SignupActivity.this, "Two passwords are different", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(SignupActivity.this, "Please input your information", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    class registerTask extends AsyncTask<String, Void, Integer> {
        String username;
        String password;

        @Override
        protected Integer doInBackground(String... params) {
            username = params[0];
            password = params[1];

            try {
                registerUser(username, password);

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Integer responseCode) {
            super.onPostExecute(responseCode);
            if(usernameExist){
                Toast.makeText(SignupActivity.this, "User already exists", Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(SignupActivity.this, "Registration complete", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        }
    }

    private Integer registerUser(String username, String password) throws IOException, JSONException {
        URL url = new URL(registerUrl);
        String urlParams = "username=" + username + "&password=" + password;
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setConnectTimeout(10000);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("Content-Length", urlParams.length() + "");
        conn.connect();

        OutputStream os = conn.getOutputStream();
        os.write(urlParams.getBytes());
        os.flush();

        InputStream is = conn.getInputStream();
        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            String results = readStream(is);
            JSONObject data;
            data = new JSONObject(String.valueOf(results));
            String status = data.getString("status");
            if (status.equals("OK")) {
                Log.d("sign", "success");
                usernameExist = false;
            }
            else if (status.equals("ERROR")) {
                usernameExist = true;
            }else {
                return null;
            }
        }
        return responseCode;
    }

    private String readStream(InputStream is) {
        InputStreamReader isr;
        String result = "";
        try {
            String line = "";
            isr = new InputStreamReader(is, "utf-8");
            BufferedReader br = new BufferedReader(isr);
            while ((line = br.readLine()) != null) {
                result += line;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
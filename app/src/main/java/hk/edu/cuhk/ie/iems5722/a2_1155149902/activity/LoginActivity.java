package hk.edu.cuhk.ie.iems5722.a2_1155149902.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
import hk.edu.cuhk.ie.iems5722.a2_1155149902.model.User;

public class LoginActivity extends AppCompatActivity {
    //    private String getUserUrl = "http://10.0.2.2:5000/api/a3/get_user";
    private String getUserUrl = "http://10.0.2.2:5000/api/a3/get_user";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TextView signupTextview = findViewById(R.id.textSignUp);
        signupTextview.setMovementMethod(LinkMovementMethod.getInstance());
        signupTextview.setText(getSignupStyle());

        EditText user = findViewById(R.id.user_input);
        EditText password = findViewById(R.id.password_input);
        Button loginButton = findViewById(R.id.btn_login);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(user.getText()) && !TextUtils.isEmpty(password.getText())) {
//                    new MyLogInTask().execute(user.getText().toString(), password.getText().toString());
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    Bundle data = new Bundle();
                    data.putString("userId", "root");
                    data.putString("username", "admin");
                    intent.putExtras(data);
                    startActivity(intent);
                } else {
                    Toast.makeText(LoginActivity.this, "Please input username and password", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    class MyLogInTask extends AsyncTask<String, Void, User> {
        String username;
        String password;

        @Override
        protected User doInBackground(String... params) {
            User me;
            username = params[0];
            password = params[1];
            try {
                me = getUser(username);
                return me;
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(User me) {
            if (me == null) {
                Toast.makeText(LoginActivity.this, "User does not exist", Toast.LENGTH_SHORT).show();
            } else {
                if (me.password.equals(password)) {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    Bundle data = new Bundle();
                    data.putString("userId", String.valueOf(me.id));
                    data.putString("username", String.valueOf(me.username));
                    intent.putExtras(data);
                    startActivity(intent);
                } else {
                    Toast.makeText(LoginActivity.this, "Wrong password", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private User getUser(String username) throws IOException, JSONException {
        URL url = new URL(getUserUrl);
        String urlParams = "username=" + username;
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
                JSONObject user = data.getJSONArray("data").getJSONObject(0);
                return new User(Integer.parseInt(user.getString("id")), user.getString("username"), user.getString("password"));
            } else {
                return null;
            }
        }
        return null;
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

    private SpannableStringBuilder getSignupStyle() {
        final SpannableStringBuilder style = new SpannableStringBuilder();
        style.append("Don't have an account? Sign Up");
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        };
        style.setSpan(clickableSpan, 23, 30, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(Color.parseColor("#0000FF"));
        style.setSpan(foregroundColorSpan, 23, 30, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return style;
    }
}
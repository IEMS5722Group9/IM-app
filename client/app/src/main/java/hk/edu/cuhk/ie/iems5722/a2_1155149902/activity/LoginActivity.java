package hk.edu.cuhk.ie.iems5722.a2_1155149902.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
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
import hk.edu.cuhk.ie.iems5722.a2_1155149902.util.HttpUtil;
import hk.edu.cuhk.ie.iems5722.a2_1155149902.util.MD5Util;
import hk.edu.cuhk.ie.iems5722.a2_1155149902.util.UrlUtil;

public class LoginActivity extends AppCompatActivity {
    private String baseUrl = UrlUtil.BaseUrl;
    //    private String getUserUrl = "http://10.0.2.2:5000/api/a3/get_user";
    private String getUserUrl = baseUrl + "/api/a3/get_user";

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
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
                    String psd = password.getText().toString();
                    new MyLogInTask().execute(getUserUrl, user.getText().toString(), psd);
//                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                    Bundle data = new Bundle();
//                    data.putString("userId", "root");
//                    data.putString("username", "admin");
//                    intent.putExtras(data);
//                    startActivity(intent);
                } else {
                    Toast.makeText(LoginActivity.this, "Please input username and password", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    class MyLogInTask extends AsyncTask<String, Void, User> {
        String password;

        @Override
        protected User doInBackground(String... params) {
            password = params[2];
            try {
                return HttpUtil.getUser(params);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(User me) {
            if (me == null) {
                Toast.makeText(LoginActivity.this, "User does not exist", Toast.LENGTH_SHORT).show();
            } else {
                String psd = MD5Util.encoder(password, me.saltKey);
                if (me.password.equals(psd)) {
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
        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(Color.parseColor("#1E90FF"));
        style.setSpan(foregroundColorSpan, 23, 30, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return style;
    }
}
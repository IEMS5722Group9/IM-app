package hk.edu.cuhk.ie.iems5722.a2_1155149902.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.json.JSONException;

import java.io.IOException;

import hk.edu.cuhk.ie.iems5722.a2_1155149902.R;
import hk.edu.cuhk.ie.iems5722.a2_1155149902.util.HttpUtil;
import hk.edu.cuhk.ie.iems5722.a2_1155149902.util.MD5Util;
import hk.edu.cuhk.ie.iems5722.a2_1155149902.util.UrlUtil;

public class SignupActivity extends AppCompatActivity {
    private String baseUrl = UrlUtil.BaseUrl;
    private String registerUrl = baseUrl + "/api/a3/register_user";
    private boolean usernameExist = false;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
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
                    String psd = password.getText().toString();
                    if(psd.length()<6 || psd.length()>12){
                        Toast.makeText(SignupActivity.this, "Password length should be 6-15 characters", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        if (psd.equals(password_confirm.getText().toString()))
                            new MyRegisterTask().execute(registerUrl, user.getText().toString(), psd, MD5Util.generateSalt(psd));
                        else {
                            Toast.makeText(SignupActivity.this, "Two passwords are different", Toast.LENGTH_SHORT).show();
                        }

                    }
                } else {
                    Toast.makeText(SignupActivity.this, "Please input your information", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    class MyRegisterTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {

            try {
                String status = HttpUtil.registerUser(params);
                usernameExist = !status.equals("OK");
                return status;
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String stauts) {
            super.onPostExecute(stauts);
            if (usernameExist) {
                Toast.makeText(SignupActivity.this, "User already exists", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(SignupActivity.this, "Registration complete", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        }
    }
}
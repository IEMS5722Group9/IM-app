package hk.edu.cuhk.ie.iems5722.a2_1155149902;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class SignupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button signupButton = findViewById(R.id.btn_signup);
//        EditText user = findViewById(R.id.user_input);
//        EditText password = findViewById(R.id.password_input);
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                startActivity(intent);
//                if (!TextUtils.isEmpty(user.getText()) && !TextUtils.isEmpty(password.getText())) {
//                    if (user.getText().toString().equals("root") && password.getText().toString().equals("admin")) {
////                        Bundle data = new Bundle();
////                        data.putString("id", );
//                    }
//                }
            }
        });
    }
}
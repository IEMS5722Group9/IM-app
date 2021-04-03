package hk.edu.cuhk.ie.iems5722.a2_1155149902;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

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

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TextView signupTextview = findViewById(R.id.textSignUp);
        signupTextview.setMovementMethod(LinkMovementMethod.getInstance());
        signupTextview.setText(getSignupStyle());

        Button loginButton = findViewById(R.id.btn_login);
        EditText user = findViewById(R.id.user_input);
        EditText password = findViewById(R.id.password_input);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(user.getText()) && !TextUtils.isEmpty(password.getText())) {
                    if (user.getText().toString().equals("root") && password.getText().toString().equals("admin")) {
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                        Bundle data = new Bundle();
//                        data.putString("id", );
                        startActivity(intent);
                    }
                }
            }
        });
    }

    private SpannableStringBuilder getSignupStyle() {
        final SpannableStringBuilder style = new SpannableStringBuilder();
        style.append("Don't have an account? Sign Up");
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
//                Toast.makeText(LoginActivity.this, "触发点击事件!", Toast.LENGTH_SHORT).show();
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
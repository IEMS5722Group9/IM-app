package hk.edu.cuhk.ie.iems5722.a2_1155149902.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;

import hk.edu.cuhk.ie.iems5722.a2_1155149902.R;
import hk.edu.cuhk.ie.iems5722.a2_1155149902.adapter.MessageAdapter;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener{
    private String userId;
    private String friend_id;
    private String friend_name;
    private boolean userExist=true;
    private boolean alreadyAdd=false;
    private EditText editText;
    private TextView text_name;
    private ImageButton btn_search;
    private Button btn_add;
    private LinearLayout linear;
    private static final String TAG = "SearchActivity";
    private String searchURL = "http://10.0.2.2:5000/api/a3/search_friend";
    private String addURL = "http://10.0.2.2:5000/api/a3/add_friend";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        //设置返回键可用
        getSupportActionBar().setHomeButtonEnabled(true);

        editText = findViewById(R.id.edit_text);
        btn_search = (ImageButton) findViewById(R.id.search_button);
        // tv = (TextView) findViewById(R.id.text_view);
        btn_search.setOnClickListener(this);
        linear=(LinearLayout) findViewById(R.id.linear_friend);
        linear.setVisibility(View.GONE);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        userId = bundle.getString("userId");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.search_button:
                //判断文本框是否为空
                if (!TextUtils.isEmpty(editText.getText())) {
                    String friend_id = editText.getText().toString();
                    if(userId.equals(friend_id)){
                        Toast.makeText(SearchActivity.this, "Cannot add yourself!", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        new MySearchTask().execute(friend_id);
                    }
                    //adapter.notifyDataSetChanged();
                    //清空文本框
                    editText.setText("");
                }
        }
    }

    class MySearchTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            BufferedReader bufferedReader = null;

            try {
                URL url;
                String urlParams = "&user_id=" + userId + "&friend_id=" + params[0];
                url = new URL(searchURL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setConnectTimeout(10000);
                connection.setRequestMethod("POST");

                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                connection.setRequestProperty("Content-Length", urlParams.length() + "");
                connection.connect();

                //设置输出流向服务器提交数据
                OutputStream os = connection.getOutputStream();
                os.write(urlParams.getBytes());
                os.flush();

                InputStream is = connection.getInputStream();
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    String results = readStream(is);
                    JSONObject jsonObject;
                    jsonObject = new JSONObject(String.valueOf(results));
                    String status = jsonObject.getString("status");

                    if (status.equals("OK")) {
                        userExist = true;
                        JSONArray data = jsonObject.getJSONArray("data");
                        jsonObject = data.getJSONObject(0);
                        friend_id = jsonObject.getString("id");
                        friend_name = jsonObject.getString("username");
                    }
                    else if (status.equals("ERROR")) {
                        userExist = false;
                    }else {
                        return null;
                    }
                }

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(!userExist){
                Toast.makeText(SearchActivity.this, "User does not exists", Toast.LENGTH_LONG).show();
            }
            else {
                linear.setVisibility(View.VISIBLE);
                btn_add = (Button) findViewById(R.id.add_button);
                text_name = (TextView) findViewById(R.id.text_name);
                text_name.setText(friend_name);

                btn_add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new MyAddTask().execute(friend_id,friend_name);
                    }
                });
            }
        }

    }

    class MyAddTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            BufferedReader bufferedReader = null;
            try {
                URL url;
                String urlParams = "user_id=" + userId + "&friend_id=" + params[0] + "&friend_name=" + params[1];
                url = new URL(addURL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setConnectTimeout(10000);
                connection.setRequestMethod("POST");

                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                connection.setRequestProperty("Content-Length", urlParams.length() + "");
                connection.connect();

                OutputStream os = connection.getOutputStream();
                os.write(urlParams.getBytes());
                os.flush();

                InputStream is = connection.getInputStream();
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    String results = readStream(is);
                    JSONObject data;
                    data = new JSONObject(String.valueOf(results));
                    String status = data.getString("status");
                    if (status.equals("OK")) {
                        alreadyAdd = false;
                    }
                    else if (status.equals("ERROR")) {
                        alreadyAdd = true;
                    }
                }

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        //        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(alreadyAdd){
                Toast.makeText(SearchActivity.this, "Already add this user", Toast.LENGTH_LONG).show();
            }
        }

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Toolbar的事件---返回
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}

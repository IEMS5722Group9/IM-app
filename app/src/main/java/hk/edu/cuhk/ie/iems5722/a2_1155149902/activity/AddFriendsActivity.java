package hk.edu.cuhk.ie.iems5722.a2_1155149902.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import com.example.qrcode.Constant;
import com.example.qrcode.ScannerActivity;

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

public class AddFriendsActivity extends AppCompatActivity implements View.OnClickListener {
    private String userId;
    private String friend_id;
    private String friend_name;
    private boolean userExist = true;
    private boolean alreadyAdd = false;
    private EditText editText;
    private TextView text_name;
    private ImageButton btn_search;
    private Button btn_add;
    private LinearLayout linear;
    private static final String TAG = "SearchActivity";
    private String searchURL = "http://10.0.2.2:5000/api/a3/search_friend";
    private String addURL = "http://10.0.2.2:5000/api/a3/add_friend";
    private final int REQUEST_PERMISION_CODE_CAMARE = 0;
    private final int RESULT_REQUEST_CODE = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

//        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        editText = findViewById(R.id.edit_text);
        btn_search = (ImageButton) findViewById(R.id.search_button);
        // tv = (TextView) findViewById(R.id.text_view);
        btn_search.setOnClickListener(this);
        linear = (LinearLayout) findViewById(R.id.linear_friend);
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
                    if (userId.equals(friend_id)) {
                        Toast.makeText(AddFriendsActivity.this, "Cannot add yourself!", Toast.LENGTH_SHORT).show();
                    } else {
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
                    } else if (status.equals("ERROR")) {
                        userExist = false;
                    } else {
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
            if (!userExist) {
                Toast.makeText(AddFriendsActivity.this, "User does not exists", Toast.LENGTH_LONG).show();
            } else {
                linear.setVisibility(View.VISIBLE);
                btn_add = (Button) findViewById(R.id.add_button);
                text_name = (TextView) findViewById(R.id.text_name);
                text_name.setText(friend_name);

                btn_add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new MyAddTask().execute(friend_id, friend_name);
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
                    } else if (status.equals("ERROR")) {
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
            if (alreadyAdd) {
                Toast.makeText(AddFriendsActivity.this, "Already add this user", Toast.LENGTH_LONG).show();
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

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_scan, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.toolbar_scan) {
//            Toast.makeText(this, "click scan", Toast.LENGTH_SHORT).show();
            requestPermission();
        } else if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void requestPermission() {
        if (ContextCompat.checkSelfPermission(AddFriendsActivity.this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(AddFriendsActivity.this,
                    Manifest.permission.CAMERA)) {
                Toast.makeText(AddFriendsActivity.this, "Permission needed.", Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(AddFriendsActivity.this,
                        new String[]{Manifest.permission.CAMERA},
                        0);
                Toast.makeText(AddFriendsActivity.this, "Permission ready.", Toast.LENGTH_SHORT).show();
            }
        } else {
            goScanner();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISION_CODE_CAMARE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    goScanner();
                }
                return;
            }
        }
    }


    private void goScanner() {
        Toast.makeText(AddFriendsActivity.this, "已打开.", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, ScannerActivity.class);
        //这里可以用intent传递一些参数，比如扫码聚焦框尺寸大小，支持的扫码类型。
//        //设置扫码框的宽
//        intent.putExtra(Constant.EXTRA_SCANNER_FRAME_WIDTH, 400);
//        //设置扫码框的高
//        intent.putExtra(Constant.EXTRA_SCANNER_FRAME_HEIGHT, 400);
//        //设置扫码框距顶部的位置
//        intent.putExtra(Constant.EXTRA_SCANNER_FRAME_TOP_PADDING, 100);
//        //设置是否启用从相册获取二维码。
        intent.putExtra(Constant.EXTRA_IS_ENABLE_SCAN_FROM_PIC, true);
//        Bundle bundle = new Bundle();
//        //设置支持的扫码类型
//        bundle.putSerializable(Constant.EXTRA_SCAN_CODE_TYPE, mHashMap);
//        intent.putExtras(bundle);
        startActivityForResult(intent, RESULT_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case RESULT_REQUEST_CODE:
                    if (data == null) return;
                    String type = data.getStringExtra(Constant.EXTRA_RESULT_CODE_TYPE);
                    String content = data.getStringExtra(Constant.EXTRA_RESULT_CONTENT);
                    Toast.makeText(this, "codeType:" + type
                            + "-----content:" + content, Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}

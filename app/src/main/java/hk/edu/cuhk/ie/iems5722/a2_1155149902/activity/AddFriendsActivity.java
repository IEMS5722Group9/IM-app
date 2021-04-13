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

import org.json.JSONException;

import java.io.IOException;

import hk.edu.cuhk.ie.iems5722.a2_1155149902.R;
import hk.edu.cuhk.ie.iems5722.a2_1155149902.model.User;
import hk.edu.cuhk.ie.iems5722.a2_1155149902.util.HttpUtil;
import hk.edu.cuhk.ie.iems5722.a2_1155149902.util.UrlUtil;

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
    private String baseUrl = UrlUtil.BaseUrl;
    private String searchURL = baseUrl + "/api/a3/search_friend";
    private String addURL = baseUrl + "/api/a3/add_friend";
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
                        new MySearchTask().execute(searchURL, userId, friend_id);
                    }
                    //清空文本框
                    editText.setText("");
                }
        }
    }

    class MySearchTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                User friend = HttpUtil.searchFriend(params);
                if (friend == null) {
                    userExist = false;
                } else {
                    friend_id = String.valueOf(friend.id);
                    friend_name = friend.username;
                    userExist = true;
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
                        new MyAddTask().execute(addURL, userId, friend_id, friend_name);
                    }
                });
            }
        }

    }

    class MyAddTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                String status = HttpUtil.addFriend(params);
                alreadyAdd = !status.equals("OK");
                return status;
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (alreadyAdd) {
                Toast.makeText(AddFriendsActivity.this, "Already add this user", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(AddFriendsActivity.this, "Add success", Toast.LENGTH_LONG).show();
                Intent intent = new Intent();
                intent.setAction("action.refreshFriend");
                sendBroadcast(intent);
            }
        }
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
//                    String type = data.getStringExtra(Constant.EXTRA_RESULT_CODE_TYPE);
                    String content = data.getStringExtra(Constant.EXTRA_RESULT_CONTENT);
                    Toast.makeText(this, "User ID:" + content, Toast.LENGTH_SHORT).show();
                    new MySearchTask().execute(searchURL, userId, content);
                    break;
                default:
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}

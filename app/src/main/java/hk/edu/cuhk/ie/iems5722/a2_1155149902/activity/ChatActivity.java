package hk.edu.cuhk.ie.iems5722.a2_1155149902.activity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import hk.edu.cuhk.ie.iems5722.a2_1155149902.model.Message;
import hk.edu.cuhk.ie.iems5722.a2_1155149902.model.MessageList;
import hk.edu.cuhk.ie.iems5722.a2_1155149902.R;
import hk.edu.cuhk.ie.iems5722.a2_1155149902.adapter.MessageAdapter;
import hk.edu.cuhk.ie.iems5722.a2_1155149902.util.UrlUtil;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import static androidx.core.app.NotificationCompat.VISIBILITY_PUBLIC;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener, AbsListView.OnScrollListener {
    private static final String TAG = "ChatActivity";
    private EditText editText;
    private ImageButton btn_send;
    private ListView mlistview;
    private ArrayList<Message> mlist = new ArrayList<Message>();
    public MessageAdapter adapter;

    private String URL;
    private String baseUrl = UrlUtil.BaseUrl;
    private String getURL = baseUrl + "/api/a3/get_messages?chatroom_id=";
    private String postURL = baseUrl + "/api/a3/send_message";
    private String roomId;
    private String roomName;
    private String userId;
    private String username;
    private int total;
    private int current;

    private int lastVisibleItemPosition = 0;// 标记上次滑动位置，初始化默认为0
    private boolean scrollFlag = false;// 标记是否滑动
    private boolean isFirstItem = false;
    private ImageButton btn_refresh;
    //创建通知管理器
    private NotificationManager notificationManager;
    private Socket mSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //左侧添加一个默认的返回图标
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        //设置返回键可用
        getSupportActionBar().setHomeButtonEnabled(true);
        //初始化视图
        initView();

        //获取bundle传递的值
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        roomId = bundle.getString("id");
        roomName = bundle.getString("roomName");
        userId = bundle.getString("userId");
        username = bundle.getString("username");

        //设置toolbar标题
        toolbar.setTitle(roomName);

        URL = getURL + roomId + "&page=1";
        new NewAsyncTask().execute(URL);

        try {
            mSocket = IO.socket("http://18.219.150.95:8001/");
            //mSocket = IO.socket("http://10.0.2.2:8001/");
            mSocket.on(Socket.EVENT_CONNECT, onConnectSuccess);
            mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect);

            mSocket.on("join", onJoin);
            mSocket.on("leave", onLeave);
            mSocket.on("message", onMessage);

            mSocket.connect();
            mSocket.emit("join", username, roomId);

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }

    public void initView() {
        editText = findViewById(R.id.edit_text);
        btn_send = (ImageButton) findViewById(R.id.send_button);
        btn_refresh = (ImageButton) findViewById(R.id.refresh);
        mlistview = (ListView) findViewById(R.id.listView);

        adapter = new MessageAdapter(ChatActivity.this, mlist, userId);
        //获取ListView对象，通过调用setAdapter方法为ListView设置Adapter设置适配器
        mlistview.setAdapter(adapter);
        mlistview.setOnScrollListener(this);

        btn_send.setOnClickListener(this);
        btn_refresh.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        URL = getURL + roomId + "&page=1";
        switch (view.getId()) {
            case R.id.refresh:
                //点击刷新按钮回到第一页
                new NewAsyncTask().execute(URL);
            case R.id.send_button:
                //判断文本框是否为空
                if (!TextUtils.isEmpty(editText.getText())) {
                    String message = editText.getText().toString();
                    //执行post操作发送消息
                    new MyPostTask().execute(message);
                    //发送消息后刷新回到最新一页
//                    new NewAsyncTask().execute(URL);
//                    //nofify放在mAdapter里面无效，要在主线程中执行notifyDataSetChanged操作
                    adapter.notifyDataSetChanged();
                    //清空文本框
                    editText.setText("");
                }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Toolbar的事件---返回
        if (item.getItemId() == android.R.id.home) {
            mSocket.emit("leave", username, roomId);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private ArrayList<Message> getJsonData(String url) {
        MessageList dataList = new MessageList();
        dataList.messages = new ArrayList<Message>();
        String jsonString = null;
        try {
            jsonString = readStream(new URL(url).openStream());
            JSONObject jsonObject;

            try {
                //解析JSON数据到List中
                jsonObject = new JSONObject(jsonString);
                JSONObject data = jsonObject.getJSONObject("data");
                JSONArray messages = data.getJSONArray("messages");
                dataList.current_page = data.getString("current_page");
                dataList.total_pages = data.getString("total_pages");
                current = Integer.valueOf(dataList.current_page);
                total = Integer.valueOf(dataList.total_pages);

                for (int i = 0; i < messages.length(); i++) {
                    JSONObject message = messages.getJSONObject(i);
                    dataList.messages.add(new Message(message.getString("id"), message.getString("chatroom_id"),
                            message.getString("user_id"), message.getString("name"),
                            message.getString("message"), message.getString("message_time")));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("Chat", jsonString);// 打印获取信息
        return dataList.messages;
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

    /*
     * 定义内部类：
     * <Params, Progress, Result>
     * */
    class NewAsyncTask extends AsyncTask<String, Void, ArrayList<Message>> {
        @Override
        protected ArrayList<Message> doInBackground(String... params) {
            return getJsonData(params[0]);
        }

        // 设置适配器
        @Override
        protected void onPostExecute(ArrayList<Message> messages) {
            super.onPostExecute(messages);
            //倒序展示，最新的一条在最底部
            Collections.reverse(messages);
            MessageAdapter adapter = new MessageAdapter(ChatActivity.this, messages, userId);
            mlistview.setAdapter(adapter);
        }
    }

    class MyPostTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            BufferedReader bufferedReader = null;
            try {
                URL url;
                String urlParams = "chatroom_id=" + roomId + "&user_id=" + userId + "&name=" + username + "&message=" + params[0];
                url = new URL(postURL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                Log.d(TAG, postURL + "?" + urlParams);

                connection.setConnectTimeout(10000);
                connection.setRequestMethod("POST");

                //注意添加的类型和长度，否则 invalid params
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                connection.setRequestProperty("Content-Length", urlParams.length() + "");
                connection.connect();

                //设置输出流向服务器提交数据
                OutputStream os = connection.getOutputStream();
                os.write(urlParams.getBytes());
                os.flush();

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    Log.d(TAG, "success");

                    InputStream inputStream = connection.getInputStream();
                    bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    String readLine = bufferedReader.readLine();
                    Log.d(TAG, "content    " + readLine);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        //        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }

    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int scrollState) {
        if (scrollState == SCROLL_STATE_TOUCH_SCROLL) { //当屏幕停止滚动时
            scrollFlag = true;
        } else if (scrollState == SCROLL_STATE_IDLE) {
            scrollFlag = false;
            // 滚动停止时，判断滚动到底部, position是从0开始算起的
            if (mlistview.getLastVisiblePosition() == (mlistview.getCount() - 1)) {
                int next = current - 1;
                if (next >= 1) {
                    URL = getURL + roomId + "&page=" + next;
                    new NewAsyncTask().execute(URL);
                }
                adapter.notifyDataSetChanged();

            }
            // 判断滚动到顶部
            else if (mlistview.getFirstVisiblePosition() == 0) {
                int before = current + 1;
                if (before > total) {
                    Toast.makeText(ChatActivity.this, "This is the last page", Toast.LENGTH_SHORT).show();
                } else {
                    URL = getURL + roomId + "&page=" + before;
                    new NewAsyncTask().execute(URL);
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }

    public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        lastVisibleItemPosition = firstVisibleItem + visibleItemCount;
    }

    private void sendNotification(String content, String title) {
        Intent intent = new Intent();
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        NotificationManager notifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new NotificationCompat.Builder(this)
                .setAutoCancel(true)
                // 设置该通知优先级
                .setPriority(Notification.PRIORITY_MAX)
                .setSmallIcon(R.drawable.cuhk)
                .setContentTitle(title)
                .setContentText(content)
                .setVisibility(VISIBILITY_PUBLIC)
                .setWhen(System.currentTimeMillis())
                // 向通知添加声音、闪灯和振动效果
                .setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_ALL | Notification.DEFAULT_SOUND)
                .setContentIntent(pendingIntent)
                .build();
        notifyManager.notify(1, notification);//id要保证唯一
    }

    private Emitter.Listener onConnectSuccess = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.e(TAG, "连接成功");
                }
            });
        }
    };

    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.e(TAG, "退出连接");
                }
            });
        }
    };

    private Emitter.Listener onJoin = new Emitter.Listener() {
        //发送
        @Override
        public void call(Object... args) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    Log.e(TAG, String.valueOf(data));
                    String str = data.optString("data");

                    Log.e(TAG, "加入房间");
                    Toast.makeText(ChatActivity.this, str, Toast.LENGTH_LONG).show();
                }
            });
        }
    };

    private Emitter.Listener onLeave = new Emitter.Listener() {
        //发送
        @Override
        public void call(Object... args) {
            JSONObject data = (JSONObject) args[0];
            Log.e(TAG, String.valueOf(data));
            String str = data.optString("data");
            Log.e(TAG, "离开房间");
            Toast.makeText(ChatActivity.this, str, Toast.LENGTH_LONG).show();
        }
    };

    private Emitter.Listener onMessage = new Emitter.Listener() {
        //发送
        @Override
        public void call(Object... args) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String msg = data.optString("message");
                    String room = data.optString("chatroom_id");
                    sendNotification(msg, room);

                    new NewAsyncTask().execute(URL);
                    adapter.notifyDataSetChanged();
                }
            });
        }
    };

    @Override
    protected void onDestroy() {
        if (mSocket != null) {
            mSocket.disconnect();
            mSocket.off();
        }
        super.onDestroy();

    }
}
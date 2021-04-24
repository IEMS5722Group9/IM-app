package hk.edu.cuhk.ie.iems5722.a2_1155149902.activity;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
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

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Objects;

import hk.edu.cuhk.ie.iems5722.a2_1155149902.model.Message;
import hk.edu.cuhk.ie.iems5722.a2_1155149902.model.MessageList;
import hk.edu.cuhk.ie.iems5722.a2_1155149902.R;
import hk.edu.cuhk.ie.iems5722.a2_1155149902.adapter.MessageAdapter;
import hk.edu.cuhk.ie.iems5722.a2_1155149902.util.HttpUtil;
import hk.edu.cuhk.ie.iems5722.a2_1155149902.util.UrlUtil;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "ChatActivity";
    private EditText editText;
    private ImageButton btn_send;
    private ListView mlistview;
    private ArrayList<Message> mlist = new ArrayList<>();

    private String baseUrl = UrlUtil.BaseUrl;
    private String getURL = baseUrl + "/api/a3/get_messages?chatroom_id=";
    private String postURL = baseUrl + "/api/a3/send_message";
    private String roomId;
    private String roomName;
    private String roomType;
    private String userId;
    private String username;

    //    public Boolean isFirstRow;
//    public Boolean isLoading = false;
//    public Boolean isLastPage = false;
    public int page;
    public int total_page;

    private ImageButton btn_refresh;
    //创建通知管理器
    private NotificationManager notificationManager;
    private Socket mSocket;
    private Context mContext;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_chat);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
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
        roomType = bundle.getString("roomType");
        userId = bundle.getString("userId");
        username = bundle.getString("username");

        //设置toolbar标题
        toolbar.setTitle(roomName);

        try {
            mSocket = IO.socket("http://18.219.150.95:8001/");
            //mSocket = IO.socket("http://10.0.2.2:8001/");
            mSocket.on(Socket.EVENT_CONNECT, onConnectSuccess);
            mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect);
            mSocket.on("message", onMessage);
            if (roomType.equals("group")) {
                mSocket.on("join", onJoin);
                mSocket.on("leave", onLeave);
            }
            mSocket.connect();
            mSocket.emit("join", username, roomId);

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        getURL = getURL + roomId + "&page=";
        new MyGetTask().execute(getURL + 1);

        mlistview.setOnScrollListener(new AbsListView.OnScrollListener() {
            private Boolean isFirstRow;
            private Boolean isLoading = false;
            private Boolean isLastPage = false;

            @Override
            public void onScroll(AbsListView view, int first, int visible, int total) {
                if (first == 0) {
                    isFirstRow = true;
                } else {
                    isLoading = false;
                }
                if (page != total_page) {
                    isLastPage = false;
                }
            }

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (isFirstRow && !isLoading && !isLastPage && scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    page = page + 1;
                    if (page <= total_page) {
                        new MyGetTask().execute(getURL + page);
                        isFirstRow = false;
                        isLoading = true;
                    } else {
                        Toast.makeText(ChatActivity.this, "No more messages", Toast.LENGTH_SHORT).show();
                        isLastPage = true;
                    }
                }
            }
        });

        btn_send.setOnClickListener(this);
        btn_refresh.setOnClickListener(this);
    }

    public void initView() {
        editText = findViewById(R.id.edit_text);
        btn_send = (ImageButton) findViewById(R.id.send_button);
        btn_refresh = (ImageButton) findViewById(R.id.refresh);
        mlistview = (ListView) findViewById(R.id.listView);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.refresh:
                //点击刷新按钮回到第一页
                mlist = new ArrayList<>();
                new MyGetTask().execute(getURL + 1);
                break;
            case R.id.send_button:
                //判断文本框是否为空
                if (!TextUtils.isEmpty(editText.getText())) {
                    String message = editText.getText().toString();
                    if (message.length() > 0 && message.length() < 200) {
                        //执行post操作发送消息
                        new MyPostTask().execute(postURL, roomId, userId, username, message);
                    } else if (message.length() > 200) {
                        Toast.makeText(this, "Message too long", Toast.LENGTH_SHORT).show();
                    }
                    //清空文本框
                    editText.setText("");
                } else {
                    Toast.makeText(this, "Input something", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Toolbar的事件---返回
        if (item.getItemId() == android.R.id.home) {
//            mSocket.emit("leave", username, roomId);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    /*
     * 定义内部类：
     * <Params, Progress, Result>
     * */
    class MyGetTask extends AsyncTask<String, Void, MessageList> {
        @Override
        protected MessageList doInBackground(String... params) {
            try {
                return HttpUtil.fetchMessage(mContext, params[0]);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            return null;
        }

        // 设置适配器
        @Override
        protected void onPostExecute(MessageList newList) {
            super.onPostExecute(newList);
            if (newList != null) {
                try {
                    page = Integer.parseInt(newList.current_page);
                    total_page = Integer.parseInt(newList.total_pages);
                } catch (Exception e) {
                    return;
                }
                mlist.addAll(0, newList.messages);
                MessageAdapter adapter = new MessageAdapter(mContext, mlist, userId, roomType);
                mlistview.setAdapter(adapter);
                mlistview.setSelection(newList.messages.size());
            }
        }
    }

    class MyPostTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                HttpUtil.postMessage(params);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            mlist = new ArrayList<>();
            new MyGetTask().execute(getURL + 1);
            Intent intent = new Intent();
            intent.setAction("action.refreshRoom");
            sendBroadcast(intent);
        }

    }


    private void sendNotification(String content, String title) {
        Intent intent = new Intent();
        Notification notification = null;
        PendingIntent pi = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        //版本兼容
        if (Build.VERSION.SDK_INT < 26) {
            notification = new Notification.Builder(this)
                    .setAutoCancel(true)
                    // 设置该通知优先级
                    .setPriority(Notification.PRIORITY_MAX)
                    .setSmallIcon(R.drawable.cuhk)
                    .setContentTitle(title)
                    .setContentText(content)
                    .setWhen(System.currentTimeMillis())
                    // 向通知添加声音、闪灯和振动效果
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setContentIntent(pi)
                    .build();
        } else {
            String CHANNEL_ID = "my_channel_01";
            CharSequence name = "my_channel";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            notificationManager.createNotificationChannel(mChannel);
            notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setAutoCancel(true)
                    .setSmallIcon(R.drawable.cuhk)
                    .setContentTitle(title)
                    .setWhen(System.currentTimeMillis())
                    .setContentText(content)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .build();
        }
        notificationManager.notify(1, notification);//id要保证唯一


//        Intent intent = new Intent();
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
//        NotificationManager notifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        Notification notification = new NotificationCompat.Builder(this)
//                .setAutoCancel(true)
//                // 设置该通知优先级
//                .setPriority(Notification.PRIORITY_MAX)
//                .setSmallIcon(R.drawable.cuhk)
//                .setContentTitle(title)
//                .setContentText(content)
//                .setVisibility(VISIBILITY_PUBLIC)
//                .setWhen(System.currentTimeMillis())
//                // 向通知添加声音、闪灯和振动效果
//                .setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_ALL | Notification.DEFAULT_SOUND)
//                .setContentIntent(pendingIntent)
//                .build();
//        notifyManager.notify(1, notification);//id要保证唯一
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
//                    Toast.makeText(, "Disconnected, Please check your internet connection", Toast.LENGTH_SHORT).show();
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
//                    Log.e(TAG, String.valueOf(data));
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
                    mlist = new ArrayList<>();
                    new MyGetTask().execute(getURL + 1);
                    Intent intent = new Intent();
                    intent.setAction("action.refreshRoom");
                    sendBroadcast(intent);
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
package hk.edu.cuhk.ie.iems5722.a2_1155149902;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.net.URL;

import io.socket.client.Socket;

public class MainActivity extends AppCompatActivity {

    private Button btn1;
    private ListView roomListView;
    private Socket mSocket;
    private String userId;
    private String username;
    private static String URL = "http://10.0.2.2:5000/api/a3/get_chatrooms";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title);
        setSupportActionBar(toolbar);

        //获取bundle传递的值
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        userId = bundle.getString("userId");
        username = bundle.getString("username");

        roomListView = (ListView) findViewById(R.id.chatroom_listView);
        new NewAsyncTask().execute(URL);

        roomListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView tv = (TextView) view.findViewById(R.id.room_name);
                Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("id", String.valueOf(position + 1));
                bundle.putString("roomName", tv.getText().toString());
                bundle.putString("userId", userId);
                bundle.putString("username", username);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

    }

    private ArrayList<Chatroom> getJsonData(String url) {
        ArrayList<Chatroom> roomList = new ArrayList<Chatroom>();
        // readStream此句功能与url.openConnection().getInputStream()相同
        // 可根据URL直接联网获取网络数据
        // 返回值类型为InputStream
        // 用URL必须前面引入：import java.net.URL;
        String jsonString = null;
        try {
            jsonString = readStream(new URL(url).openStream());
            JSONObject jsonObject;
            Chatroom chatroom;
            try {
                //解析JSON数据到List中
                jsonObject = new JSONObject(jsonString);
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                for (int i = 0; i < jsonArray.length(); i++) {
                    jsonObject = jsonArray.getJSONObject(i);
                    chatroom = new Chatroom();
                    chatroom.room_id = jsonObject.getString("id");
                    chatroom.room_name = jsonObject.getString("name");
                    roomList.add(chatroom);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d("Main", jsonString);// 打印获取信息

        return roomList;
    }

    /*
     * 通过InputStream去读取网络信息：
     * 传来的参数是一个InputStream的字节流is，
     * 通过InputStreamReader将字节流转化为字符流，
     * 再通过BufferedReader将字符流以Buffer的形式读取出来，
     * 最终拼接到result里面。
     * 这样就完成了整个数据的读取。
     * */
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
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }


    /*
     * 定义内部类：
     * <Params, Progress, Result>
     * */
    class NewAsyncTask extends AsyncTask<String, Void, ArrayList<Chatroom>> {

        @Override
        protected ArrayList<Chatroom> doInBackground(String... params) {
            // params[0]为请求网站，因为只传了一个网址，所以只取0即可
            return getJsonData(params[0]);
        }

        // 设置适配器
        @Override
        protected void onPostExecute(ArrayList<Chatroom> chatroom) {
            super.onPostExecute(chatroom);
            roomAdapter adapter = new roomAdapter(MainActivity.this, chatroom);
            roomListView.setAdapter(adapter);
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
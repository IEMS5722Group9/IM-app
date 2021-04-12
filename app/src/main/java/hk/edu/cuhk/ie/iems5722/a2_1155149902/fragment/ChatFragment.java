package hk.edu.cuhk.ie.iems5722.a2_1155149902.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import hk.edu.cuhk.ie.iems5722.a2_1155149902.activity.ChatActivity;
import hk.edu.cuhk.ie.iems5722.a2_1155149902.model.Chatroom;
//import hk.edu.cuhk.ie.iems5722.a2_1155149902.MainActivity;
import hk.edu.cuhk.ie.iems5722.a2_1155149902.activity.MainActivity;
import hk.edu.cuhk.ie.iems5722.a2_1155149902.R;
import hk.edu.cuhk.ie.iems5722.a2_1155149902.adapter.RoomAdapter;
import hk.edu.cuhk.ie.iems5722.a2_1155149902.util.UrlUtil;

public class ChatFragment extends Fragment {

    private ListView roomListView;
    private String userId;
    private String username;
    private String baseUrl = UrlUtil.BaseUrl;
    private String URL = baseUrl + "/api/a3/get_chatrooms";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_chat, container, false);

        roomListView = (ListView) root.findViewById(R.id.chatroom_listView);
        new NewAsyncTask().execute(URL);

        roomListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView tv = (TextView) view.findViewById(R.id.room_name);
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("id", String.valueOf(position + 1));
                bundle.putString("roomName", tv.getText().toString());
                bundle.putString("userId", userId);
                bundle.putString("username", username);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        return root;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Bundle bundle = ((MainActivity) context).toValue();
        userId = bundle.getString("userId");
        username = bundle.getString("username");
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
            RoomAdapter adapter = new RoomAdapter(getActivity(), chatroom);
            roomListView.setAdapter(adapter);
        }
    }
}
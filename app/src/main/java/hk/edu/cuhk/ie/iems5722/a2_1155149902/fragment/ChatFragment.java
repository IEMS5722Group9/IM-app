package hk.edu.cuhk.ie.iems5722.a2_1155149902.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import androidx.annotation.Nullable;
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
/* import hk.edu.cuhk.ie.iems5722.a2_1155149902.MainActivity; */
import hk.edu.cuhk.ie.iems5722.a2_1155149902.activity.MainActivity;
import hk.edu.cuhk.ie.iems5722.a2_1155149902.R;
import hk.edu.cuhk.ie.iems5722.a2_1155149902.adapter.RoomAdapter;
import hk.edu.cuhk.ie.iems5722.a2_1155149902.model.User;
import hk.edu.cuhk.ie.iems5722.a2_1155149902.util.HttpUtil;
import hk.edu.cuhk.ie.iems5722.a2_1155149902.util.UrlUtil;

public class ChatFragment extends Fragment {

    private ListView roomListView;
    private String roomId;
    private String userId;
    private String username;
    private String baseUrl = UrlUtil.BaseUrl;
    private String URL = baseUrl + "/api/a3/get_chatrooms";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("action.refreshRoom");
        requireActivity().registerReceiver(mRefreshBroadcastReceiver, intentFilter);
    }

    // broadcast receiver
    private BroadcastReceiver mRefreshBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("action.refreshRoom")) {
                new NewAsyncTask().execute(URL);
            }
        }
    };

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_chat, container, false);

        roomListView = (ListView) root.findViewById(R.id.chatroom_listView);
        new NewAsyncTask().execute(URL);

        roomListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Chatroom get = (Chatroom) parent.getItemAtPosition(position);
//                TextView tv = (TextView) view.findViewById(R.id.room_name);
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("id", get.room_id);
                bundle.putString("roomName", get.room_name);
                bundle.putString("roomType", get.room_type);
                bundle.putString("userId", userId);
                bundle.putString("username", username);
//                bundle.putString("exit", "yes");
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

    /*
     * 定义内部类：
     * <Params, Progress, Result>
     * */
    class NewAsyncTask extends AsyncTask<String, Void, ArrayList<Chatroom>> {

        @Override
        protected ArrayList<Chatroom> doInBackground(String... params) {
            // params[0]为请求网站，因为只传了一个网址，所以只取0即可
            try {
                return fetchChatRoom(params[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        // 设置适配器
        @Override
        protected void onPostExecute(ArrayList<Chatroom> chatroom) {
            super.onPostExecute(chatroom);
            if (getActivity() != null) {
                RoomAdapter adapter = new RoomAdapter(getActivity(), chatroom, username);
                roomListView.setAdapter(adapter);
            }
        }
    }

    public ArrayList<Chatroom> fetchChatRoom(String url) throws IOException {
        ArrayList<Chatroom> rList = new ArrayList<>();
        String results = HttpUtil.readStream(new URL(url).openStream());
        try {
            JSONArray data = new JSONObject(results).getJSONArray("data");
            for (int i = 0; i < data.length(); i++) {
                JSONObject chatroom = data.getJSONObject(i);
                //分割私聊聊天室名字
                roomId = chatroom.getString("room_id");
                String name = chatroom.getString("room_name");
                String type = chatroom.getString("room_type");
                String message_user = chatroom.getString("username");
                String message = chatroom.getString("message");
                String messag_time = chatroom.getString("message_time");
                if (type.equals("person")) {
                    String[] chatroom_name = name.split("&");
                    if (!chatroom_name[0].equals(username) && !chatroom_name[1].equals(username)) {
                        continue;
                    } else {
                        name = chatroom_name[0].equals(username) ? chatroom_name[1] : chatroom_name[0];
                    }
                }
                Chatroom room = new Chatroom(roomId, name);
                room.setType(type);
                if (messag_time != null) {
                    room.setMessage(message_user, message, messag_time);
                }
                rList.add(room);
                //rList.add(new Chatroom(chatroom.getString("id"), chatroom.getString("name")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("Chatrooms", results);// 打印获取信息
        return rList;
    }
}
package hk.edu.cuhk.ie.iems5722.a2_1155149902.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

import hk.edu.cuhk.ie.iems5722.a2_1155149902.R;
import hk.edu.cuhk.ie.iems5722.a2_1155149902.activity.AddFriendsActivity;
import hk.edu.cuhk.ie.iems5722.a2_1155149902.activity.ChatActivity;
import hk.edu.cuhk.ie.iems5722.a2_1155149902.activity.MainActivity;
import hk.edu.cuhk.ie.iems5722.a2_1155149902.adapter.FriendAdapter;
import hk.edu.cuhk.ie.iems5722.a2_1155149902.model.User;
import hk.edu.cuhk.ie.iems5722.a2_1155149902.util.HttpUtil;
import hk.edu.cuhk.ie.iems5722.a2_1155149902.util.UrlUtil;

public class FriendsFragment extends Fragment {
    private ListView friendListView;
    private ArrayList<User> friendsList;
    private String userId;
    private String username;
    private String baseUrl = UrlUtil.BaseUrl;
    private String URL = baseUrl + "/api/a3/get_friends?user_id=";
    private String addURL = baseUrl + "/api/a3/add_chatroom?";
    private String type = "person";
//    private String roomId = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("action.refreshFriend");
        requireActivity().registerReceiver(mRefreshBroadcastReceiver, intentFilter);
    }

    // broadcast receiver
    private BroadcastReceiver mRefreshBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("action.refreshFriend")) {
                new MyGetTask().execute(URL + userId);
            }
        }
    };

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_friends, container, false);
        friendListView = (ListView) root.findViewById(R.id.friends_list);
        new MyGetTask().execute(URL + userId);
        friendListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User get = (User) parent.getItemAtPosition(position);
                new AddRoomTask().execute(addURL, username, get.username, type);
            }
        });
        return root;
    }

    /*
     * 定义内部类：
     * <Params, Progress, Result>
     * */
    class MyGetTask extends AsyncTask<String, Void, ArrayList<User>> {

        @Override
        protected ArrayList<User> doInBackground(String... params) {
            // params[0]为请求网站，因为只传了一个网址，所以只取0即可
            try {
                return HttpUtil.fetchFriendList(params[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        // 设置适配器
        @Override
        protected void onPostExecute(ArrayList<User> friend) {
            super.onPostExecute(friend);
            if (getActivity() != null) {
                FriendAdapter adapter = new FriendAdapter(getActivity(), friend);
                friendListView.setAdapter(adapter);
            }
        }
    }

    class AddRoomTask extends AsyncTask<String, Void, String> {
        String room_name;

        @Override
        protected String doInBackground(String... params) {
            room_name = params[2];
            try {
                return HttpUtil.addRoom(params);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result == null||result.equals("ERROR")) {
//                roomId = null;
                Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
            } else {
//                roomId = result;
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("id", result);
                bundle.putString("roomName", room_name);
                bundle.putString("roomType", type);
                bundle.putString("userId", userId);
                bundle.putString("username", username);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Bundle bundle = ((MainActivity) context).toValue();
        userId = bundle.getString("userId");
        username = bundle.getString("username");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //设置另外的menu
        menu.clear();
        inflater.inflate(R.menu.menu_add_friends, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.toolbar_add) {
            Intent intent = new Intent(getActivity(), AddFriendsActivity.class);
            Bundle data = new Bundle();
            data.putString("userId", userId);
            intent.putExtras(data);
            startActivity(intent);
        }
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        requireActivity().unregisterReceiver(mRefreshBroadcastReceiver);

    }
}

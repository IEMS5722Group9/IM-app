package hk.edu.cuhk.ie.iems5722.a2_1155149902.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

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

import hk.edu.cuhk.ie.iems5722.a2_1155149902.R;
import hk.edu.cuhk.ie.iems5722.a2_1155149902.activity.MainActivity;
import hk.edu.cuhk.ie.iems5722.a2_1155149902.activity.SearchActivity;
import hk.edu.cuhk.ie.iems5722.a2_1155149902.adapter.FriendAdapter;
import hk.edu.cuhk.ie.iems5722.a2_1155149902.adapter.RoomAdapter;
import hk.edu.cuhk.ie.iems5722.a2_1155149902.model.Chatroom;
import hk.edu.cuhk.ie.iems5722.a2_1155149902.model.User;

public class FriendsFragment extends Fragment {
    private ListView friendListView;
    private String userId;
    private String username;
    private static String URL = "http://10.0.2.2:5000/api/a3/get_friends?user_id=";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Bundle bundle = ((MainActivity) context).toValue();
        userId = bundle.getString("userId");
        username = bundle.getString("username");
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_friends, container, false);
        friendListView = (ListView) root.findViewById(R.id.friends_list);
        new FriendsFragment.NewAsyncTask().execute(URL + userId);
//        friendsList = new ArrayList<>();
//        friendsList.add(new User(1, "root"));
//        friendsList.add(new User(2, "root"));
//        friendsList.add(new User(3, "root"));
//        friendsList.add(new User(4, "root"));
//        friendsList.add(new User(1, "root"));
//        friendsList.add(new User(6, "root"));
//        friendsList.add(new User(7, "root"));
//        friendsList.add(new User(8, "root"));
//        FriendAdapter adapter = new FriendAdapter(getActivity(), friendsList);
//        friendListView.setAdapter(adapter);
        return root;
    }

    private ArrayList<User> getJsonData(String url) {
        ArrayList<User> friendList = new ArrayList<User>();
        // readStream此句功能与url.openConnection().getInputStream()相同
        // 可根据URL直接联网获取网络数据
        // 返回值类型为InputStream
        // 用URL必须前面引入：import java.net.URL;
        String jsonString = null;
        try {
            jsonString = readStream(new URL(url).openStream());
            JSONObject jsonObject;
            User friend;
            try {
                //解析JSON数据到List中
                jsonObject = new JSONObject(jsonString);
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                for (int i = 0; i < jsonArray.length(); i++) {
                    jsonObject = jsonArray.getJSONObject(i);
                    friend = new User();
                    friend.id = Integer.parseInt(jsonObject.getString("friend_id"));
                    friend.username = jsonObject.getString("friend_name");
                    friendList.add(friend);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("Friends", jsonString);// 打印获取信息

        return friendList;
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
    class NewAsyncTask extends AsyncTask<String, Void, ArrayList<User>> {

        @Override
        protected ArrayList<User> doInBackground(String... params) {
            // params[0]为请求网站，因为只传了一个网址，所以只取0即可
            return getJsonData(params[0]);
        }

        // 设置适配器
        @Override
        protected void onPostExecute(ArrayList<User> friend) {
            super.onPostExecute(friend);
            FriendAdapter adapter = new FriendAdapter(getActivity(), friend);
            friendListView.setAdapter(adapter);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //设置另外的menu
        menu.clear();
        inflater.inflate(R.menu.menu_friends, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Bundle bundle = ((MainActivity) context).toValue();
        userId = bundle.getString("userId");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.toolbar_search:
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                Bundle data = new Bundle();
                data.putString("userId", userId);
                intent.putExtras(data);
                startActivity(intent);
                break;
            case R.id.toolbar_scan:
                break;
        }
        return true;
    }
}

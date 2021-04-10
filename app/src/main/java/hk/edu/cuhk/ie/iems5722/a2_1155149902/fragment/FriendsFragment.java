package hk.edu.cuhk.ie.iems5722.a2_1155149902.fragment;

import android.content.Intent;
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

import java.util.ArrayList;

import hk.edu.cuhk.ie.iems5722.a2_1155149902.R;
import hk.edu.cuhk.ie.iems5722.a2_1155149902.activity.SearchActivity;
import hk.edu.cuhk.ie.iems5722.a2_1155149902.adapter.FriendAdapter;
import hk.edu.cuhk.ie.iems5722.a2_1155149902.model.User;

public class FriendsFragment extends Fragment {
    private ListView friendListView;
    ArrayList<User> friendsList = new ArrayList<>();
    private static String URL = "http://10.0.2.2:5000/api/a3/get_friends";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_friends, container, false);
        friendListView = (ListView) root.findViewById(R.id.friends_list);
        friendsList = new ArrayList<>();
        friendsList.add(new User(1, "root"));
        friendsList.add(new User(2, "root"));
        friendsList.add(new User(3, "root"));
        friendsList.add(new User(4, "root"));
        friendsList.add(new User(1, "root"));
        friendsList.add(new User(6, "root"));
        friendsList.add(new User(7, "root"));
        friendsList.add(new User(8, "root"));
        FriendAdapter adapter = new FriendAdapter(getActivity(), friendsList);
        friendListView.setAdapter(adapter);
        return root;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //设置另外的menu
        menu.clear();
        inflater.inflate(R.menu.menu_friends, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.toolbar_search:
                Log.e("click", "click");
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);
                break;
            case R.id.toolbar_scan:

                break;
        }
        return true;
    }
}

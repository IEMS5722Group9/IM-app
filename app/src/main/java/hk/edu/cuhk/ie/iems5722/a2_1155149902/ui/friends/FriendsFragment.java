package hk.edu.cuhk.ie.iems5722.a2_1155149902.ui.friends;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import androidx.fragment.app.Fragment;
import hk.edu.cuhk.ie.iems5722.a2_1155149902.R;
import hk.edu.cuhk.ie.iems5722.a2_1155149902.activity.SearchActivity;

public class FriendsFragment extends Fragment{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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
        switch (item.getItemId()){
            case R.id.toolbar_search:
                Log.e("click","click");
                Intent intent= new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);
                break;
            case R.id.toolbar_scan:

                break;
        }
        return true;
    }
}

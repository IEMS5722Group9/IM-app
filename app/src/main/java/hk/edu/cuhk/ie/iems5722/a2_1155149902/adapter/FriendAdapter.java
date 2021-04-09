package hk.edu.cuhk.ie.iems5722.a2_1155149902.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import hk.edu.cuhk.ie.iems5722.a2_1155149902.R;
import hk.edu.cuhk.ie.iems5722.a2_1155149902.model.User;

public class FriendAdapter extends ArrayAdapter<User> {
    public ArrayList<User> friendsList;
    public Context mContext;
    public LayoutInflater layoutInflater;

    public FriendAdapter(Context context, ArrayList<User> list) {
        super(context, 0, list);
        this.mContext = context;
        this.friendsList = list;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        FriendAdapter.ViewHolder holder;
        User friend = getItem(i);

        if (convertView == null) {
            //layoutInflater.inflate
            //使用这个布局里面的控件
            holder = new FriendAdapter.ViewHolder();
            convertView = layoutInflater.inflate(R.layout.item_friend, viewGroup,false);
            holder.name = (TextView) convertView.findViewById(R.id.friend_name);
            convertView.setTag(holder);

        } else {
            holder = (FriendAdapter.ViewHolder) convertView.getTag();
        }

        holder.name.setText(friend.username);

        return convertView;
    }

    private static class ViewHolder {
        TextView name;

    }
}

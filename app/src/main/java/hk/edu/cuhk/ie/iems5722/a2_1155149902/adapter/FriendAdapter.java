package hk.edu.cuhk.ie.iems5722.a2_1155149902.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import hk.edu.cuhk.ie.iems5722.a2_1155149902.R;
import hk.edu.cuhk.ie.iems5722.a2_1155149902.model.User;
import hk.edu.cuhk.ie.iems5722.a2_1155149902.util.ViewUtil;

public class FriendAdapter extends ArrayAdapter<User> {
    public ArrayList<User> friendsList;
    public Context mContext;
    public LayoutInflater layoutInflater;

    public FriendAdapter(Context context, ArrayList<User> list) {
        super(context, 0, list);
        this.mContext = context;
        this.friendsList = list;
        this.friendsList.add(new User());
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public boolean isEnabled(int position) {
        return position != getCount() - 1;
    }

    @Override
    public User getItem(int position) {
        return this.friendsList.get(position);
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        FriendAdapter.ViewHolder holder;
        User friend = getItem(i);

        if (i + 1 != getCount()) {
            //layoutInflater.inflate
            //使用这个布局里面的控件
            holder = new FriendAdapter.ViewHolder();
            convertView = layoutInflater.inflate(R.layout.item_friend, viewGroup, false);
            holder.name = (TextView) convertView.findViewById(R.id.friend_name);
            holder.id = (TextView) convertView.findViewById(R.id.friend_id);
            holder.avatar = (ImageView) convertView.findViewById(R.id.friend_image);
            holder.name.setText(friend.username);
            holder.id.setText("ID: " + friend.id);

            //Drawable drawable = ViewUtil.StringToDrawable("");
            //holder.avatar.setImageDrawable(drawable);

            if(friend.avatar != null){
                holder.avatar.setImageDrawable(friend.avatar);
            } else {
                holder.avatar.setImageResource(R.drawable.avatar);
            }

        } else {
            convertView = layoutInflater.inflate(R.layout.item_friend_total, viewGroup, false);
            TextView count = convertView.findViewById(R.id.friend_total);
            if (i != 0) {
                count.setText("You have followed " + (getCount() - 1) + " friends.");
            } else {
                count.setText("You have not followed someone.");
            }
        }
        return convertView;
    }

    private static class ViewHolder {
        TextView name;
        TextView id;
        ImageView avatar;
    }
}

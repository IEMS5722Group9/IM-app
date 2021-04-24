package hk.edu.cuhk.ie.iems5722.a2_1155149902.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.util.ArrayList;

import hk.edu.cuhk.ie.iems5722.a2_1155149902.model.Chatroom;
import hk.edu.cuhk.ie.iems5722.a2_1155149902.R;
import hk.edu.cuhk.ie.iems5722.a2_1155149902.model.User;
import hk.edu.cuhk.ie.iems5722.a2_1155149902.util.HttpUtil;
import hk.edu.cuhk.ie.iems5722.a2_1155149902.util.ImageUtil;

public class RoomAdapter extends ArrayAdapter<Chatroom> {
    public ArrayList<Chatroom> rList;
    public Context mContext;
    public LayoutInflater layoutInflater;
    public String username;

    public RoomAdapter(Context context, ArrayList<Chatroom> list, String username) {
        super(context, 0, list);
        this.mContext = context;
        this.username = username;
        this.rList = list;
        layoutInflater = LayoutInflater.from(context);
    }

    public Chatroom getItem(int position) {
        return this.rList.get(position);
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        RoomAdapter.ViewHolder holder;
        Chatroom room = getItem(i);

        if (convertView == null) {
            //layoutInflater.inflate
            //使用这个布局里面的控件
            holder = new RoomAdapter.ViewHolder();
            convertView = layoutInflater.inflate(R.layout.item_chatroom, viewGroup, false);
            holder.name = (TextView) convertView.findViewById(R.id.room_name);
            holder.time = (TextView) convertView.findViewById(R.id.new_message_time);
            holder.message = (TextView) convertView.findViewById(R.id.new_message);
            holder.image = (ImageView) convertView.findViewById(R.id.room_image);
            convertView.setTag(holder);

        } else {
            holder = (RoomAdapter.ViewHolder) convertView.getTag();
        }
        if (room.room_type.equals("group")) {
            holder.image.setImageResource(R.drawable.group);
        } else {
            holder.image.setImageDrawable(room.avatar);
        }
        holder.name.setText(room.room_name);
        try {
            holder.time.setText(HttpUtil.convertDate(room.newest_message.getTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String messageStr=room.newest_message.getMessage();
        if (room.newest_message.getMessage().length() > 20) {
            messageStr=room.newest_message.getMessage().substring(0, 20) + "...";
        }
        if (room.newest_message.getName().equals(this.username))
            holder.message.setText(messageStr);
        else {
            holder.message.setText(room.newest_message.getName() + ": " + messageStr);
        }

        return convertView;
    }

    private static class ViewHolder {
        TextView name;
        TextView message;
        TextView time;
        ImageView image;
    }
}

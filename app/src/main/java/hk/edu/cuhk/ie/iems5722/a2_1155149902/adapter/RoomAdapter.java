package hk.edu.cuhk.ie.iems5722.a2_1155149902.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import hk.edu.cuhk.ie.iems5722.a2_1155149902.model.Chatroom;
import hk.edu.cuhk.ie.iems5722.a2_1155149902.R;

public class RoomAdapter extends ArrayAdapter<Chatroom> {
    public ArrayList<Chatroom> mList;
    public Context mContext;
    public LayoutInflater layoutInflater;

    public RoomAdapter(Context context, ArrayList<Chatroom> list) {
        super(context, 0, list);
        this.mContext = context;
        this.mList = list;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        RoomAdapter.ViewHolder holder;
        Chatroom room = getItem(i);

        if (convertView == null) {
            //layoutInflater.inflate
            //使用这个布局里面的控件
            holder = new RoomAdapter.ViewHolder();
            convertView = layoutInflater.inflate(R.layout.item_chatroom, viewGroup,false);
            holder.name = (TextView) convertView.findViewById(R.id.room_name);
            convertView.setTag(holder);

        } else {
            holder = (RoomAdapter.ViewHolder) convertView.getTag();
        }

        holder.name.setText(room.room_name);

        return convertView;
    }

    private static class ViewHolder {
        TextView name;

    }
}

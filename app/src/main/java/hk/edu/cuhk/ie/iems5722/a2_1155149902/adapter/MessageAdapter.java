package hk.edu.cuhk.ie.iems5722.a2_1155149902.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import hk.edu.cuhk.ie.iems5722.a2_1155149902.model.Message;
import hk.edu.cuhk.ie.iems5722.a2_1155149902.R;
import hk.edu.cuhk.ie.iems5722.a2_1155149902.util.HttpUtil;

public class MessageAdapter extends ArrayAdapter<Message> {
    public ArrayList<Message> mList;
    public Context mContext;
    public LayoutInflater layoutInflater;
    public String userId;

    public MessageAdapter(Context context, ArrayList<Message> list, String userId) {
        super(context, 0, list);
        this.mContext = context;
        this.mList = list;
        this.userId = userId;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public void addAll(@NonNull Collection<? extends Message> collection) {
        super.addAll(collection);
        this.mList.addAll(0, collection);
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        ViewHolder holder;
        Message m = getItem(i);

        if (m.getUserId().equals(this.userId)) {
            //layoutInflater.inflate
            //使用这个布局里面的控件
            holder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.item_message_send, viewGroup, false);
            holder.name = (TextView) convertView.findViewById(R.id.user_name);
            holder.content = (TextView) convertView.findViewById(R.id.message_content);
            holder.time = (TextView) convertView.findViewById(R.id.message_time);
            //获取List集合里new好的控件和得到里面的数据
            // 需要写在if null外面， 否则scroll无法更新
            holder.name.setText(String.format("User: %s", m.getName()));
            holder.content.setText(m.getMessage());
            try {
                holder.time.setText(HttpUtil.convertDate(m.getTime()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            holder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.item_message_receive, viewGroup, false);
            holder.name = (TextView) convertView.findViewById(R.id.r_user_name);
            holder.content = (TextView) convertView.findViewById(R.id.r_message_content);
            holder.time = (TextView) convertView.findViewById(R.id.r_message_time);
            //获取List集合里new好的控件和得到里面的数据
            // 需要写在if null外面， 否则scroll无法更新
            holder.name.setText(String.format("User: %s", m.getName()));
            holder.content.setText(m.getMessage());
            try {
                holder.time.setText(HttpUtil.convertDate(m.getTime()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return convertView;
    }

    private static class ViewHolder {
        TextView content;
        TextView time;
        TextView name;
    }
}

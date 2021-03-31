package hk.edu.cuhk.ie.iems5722.a2_1155149902;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class mAdapter extends ArrayAdapter<Message> {
    public static String TAG = "mAdapter";
    public ArrayList<Message> mList;
    public Context mContext;
    public LayoutInflater layoutInflater;

    public mAdapter(Context context, ArrayList<Message> list) {
        super(context, 0, list);
        this.mContext = context;
        this.mList = list;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        ViewHolder holder;
        Message m = getItem(i);

        if(m.getUserId().equals("1155149902")) {
            if (convertView == null) {
                //layoutInflater.inflate
                //使用这个布局里面的控件
                holder = new ViewHolder();
                convertView = layoutInflater.inflate(R.layout.message_item_send, viewGroup, false);
                holder.name = (TextView) convertView.findViewById(R.id.user_name);
                holder.content = (TextView) convertView.findViewById(R.id.message_content);
                holder.time = (TextView) convertView.findViewById(R.id.message_time);
                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            //获取List集合里new好的控件和得到里面的数据
            // 需要写在if null外面， 否则scroll无法更新
            holder.name.setText(String.format("User: %s", m.getName()));
            holder.content.setText(m.getMessage());
            holder.time.setText(m.getTime());
        }
        else {
            if (convertView == null) {

                holder = new ViewHolder();
                convertView = layoutInflater.inflate(R.layout.message_item_receive, viewGroup, false);
                holder.name = (TextView) convertView.findViewById(R.id.r_user_name);
                holder.content = (TextView) convertView.findViewById(R.id.r_message_content);
                holder.time = (TextView) convertView.findViewById(R.id.r_message_time);
                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.name.setText(String.format("User: %s", m.getName()));
            holder.content.setText(m.getMessage());
            holder.time.setText(m.getTime());
        }

        return convertView;
    }

    private static class ViewHolder {
        TextView content;
        TextView time;
        TextView name;
    }

}

package hk.edu.cuhk.ie.iems5722.a2_1155149902;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class mAdapter extends ArrayAdapter<Message> {
    public static String TAG = "mAdapter";
    public ArrayList<Message> mList;
    public Context mContext;
    public LayoutInflater layoutInflater;
    public String userId;

    public mAdapter(Context context, ArrayList<Message> list, String userId) {
        super(context, 0, list);
        this.mContext = context;
        this.mList = list;
        this.userId = userId;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        ViewHolder holder;
        Message m = getItem(i);

        if (m.getUserId().equals(this.userId)) {
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
        } else {
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
        }
        //获取List集合里new好的控件和得到里面的数据
        // 需要写在if null外面， 否则scroll无法更新
        holder.name.setText(String.format("User: %s", m.getName()));
        holder.content.setText(m.getMessage());
        try {
            holder.time.setText(convertDate(m.getTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return convertView;
    }

    private static class ViewHolder {
        TextView content;
        TextView time;
        TextView name;
    }

    private static String convertDate(String target) throws ParseException {
        SimpleDateFormat todayFormat = new SimpleDateFormat("HH:mm"); // define the format of time
        SimpleDateFormat currentYearFormat = new SimpleDateFormat("MM-dd HH:mm");
        SimpleDateFormat originFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date targetDate = originFormat.parse(target);
        Date nowDate = new Date(System.currentTimeMillis());

        Calendar pre = Calendar.getInstance();
        pre.setTime(nowDate);
        Calendar now = Calendar.getInstance();
        now.setTime(targetDate);
        if (now.get(Calendar.YEAR) == (pre.get(Calendar.YEAR))) {
            int diffDay = now.get(Calendar.DAY_OF_YEAR) - pre.get(Calendar.DAY_OF_YEAR);
            if (diffDay == 0) {
                return todayFormat.format(originFormat.parse(target));
            } else {
                return currentYearFormat.format(originFormat.parse(target));
            }
        } else {
            return target;
        }
    }
}

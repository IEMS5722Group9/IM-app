package hk.edu.cuhk.ie.iems5722.a2_1155149902.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import hk.edu.cuhk.ie.iems5722.a2_1155149902.R;
import hk.edu.cuhk.ie.iems5722.a2_1155149902.model.Message;
import hk.edu.cuhk.ie.iems5722.a2_1155149902.model.MessageList;
import hk.edu.cuhk.ie.iems5722.a2_1155149902.model.User;

public class HttpUtil {

    public static HttpURLConnection getConnection(String urlStr) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000);
        conn.setConnectTimeout(15000);
        conn.setDoInput(true);
        conn.setRequestProperty("Connection", "close");
        conn.connect();
        return conn;
    }

    /*
     * 通过InputStream去读取网络信息：
     * 传来的参数是一个InputStream的字节流is，
     * 通过InputStreamReader将字节流转化为字符流，
     * 再通过BufferedReader将字符流以Buffer的形式读取出来，
     * 最终拼接到result里面。
     * 这样就完成了整个数据的读取。
     * */
    public static String readStream(InputStream is) throws IOException {
        InputStreamReader isr;
        String result = "";
        try {
            String line = "";
            isr = new InputStreamReader(is, StandardCharsets.UTF_8);
            BufferedReader br = new BufferedReader(isr);
            while ((line = br.readLine()) != null) {
                result += line;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static User getUser(String... params) throws IOException, JSONException {
        User me = new User();
        String urlParams = "username=" + params[1];
        HttpURLConnection conn = postConnection(params[0], urlParams);
        OutputStream os = conn.getOutputStream();
        os.write(urlParams.getBytes());
        os.flush();

        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            InputStream inputStream = conn.getInputStream();
            String results = readStream(inputStream);
            JSONObject jsonObject;
            jsonObject = new JSONObject(String.valueOf(results));
            String status = jsonObject.getString("status");

            if (status.equals("OK")) {
                JSONArray data = jsonObject.getJSONArray("data");
                me.id = Integer.parseInt(data.getJSONObject(0).getString("id"));
                me.username = data.getJSONObject(0).getString("username");
                me.password = data.getJSONObject(0).getString("password");
                conn.disconnect();
                return me;
            }
        }
        conn.disconnect();
        return null;
    }

    public static String registerUser(String... params) throws IOException, JSONException {
        String urlParams = "username=" + params[1] + "&password=" + params[2];
        HttpURLConnection conn = postConnection(params[0], urlParams);
        OutputStream os = conn.getOutputStream();
        os.write(urlParams.getBytes());
        os.flush();
        os.close();

        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            InputStream inputStream = conn.getInputStream();
            String results = readStream(inputStream);
            JSONObject jsonObject;
            jsonObject = new JSONObject(String.valueOf(results));
            conn.disconnect();
            return jsonObject.getString("status");
        }
        conn.disconnect();
        return "ERROR";
    }

//    public static ArrayList<Chatroom> fetchChatRoom(String url) throws IOException {
//        ArrayList<Chatroom> rList = new ArrayList<>();
//        String results = readStream(new URL(url).openStream());
//        try {
//            JSONArray data = new JSONObject(results).getJSONArray("data");
//            for (int i = 0; i < data.length(); i++) {
//                JSONObject chatroom = data.getJSONObject(i);
//                rList.add(new Chatroom(chatroom.getString("id"), chatroom.getString("name")));
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        Log.d("Chatrooms", results);// 打印获取信息
//        return rList;
//    }

    public static MessageList fetchMessage(Context context, String url) throws MalformedURLException {
        MessageList dataList = new MessageList();
        dataList.messages = new ArrayList<Message>();
        String jsonString = null;
        try {
            jsonString = readStream(getConnection(url).getInputStream());
            Log.d("Chat", jsonString);// 打印获取信息
            JSONObject jsonObject;
            try {
                //解析JSON数据到List中
                jsonObject = new JSONObject(jsonString);
                if (jsonObject.getString("status").equals("OK")) {
                    JSONObject data = jsonObject.getJSONObject("data");
                    JSONArray messages = data.getJSONArray("messages");
                    dataList.total_pages = data.optString("total_pages");
                    dataList.current_page = data.optString("current_page");

                    for (int i = 0; i < messages.length(); i++) {
                        Message message = parseMessage(context, messages.getJSONObject(i));
                        //倒序展示，最新的一条在最底部
                        dataList.messages.add(0, message);
                    }
                } else {
                    return null;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return dataList;
    }

    public static Message parseMessage(Context context, JSONObject json) throws JSONException {
        String id = json.getString("id");
        String roomId = json.getString("chatroom_id");
        String userId = json.getString("user_id");
        String name = json.getString("name");
        String content = json.getString("message");
        String time = json.getString("message_time");
        String avatar = json.getString("avatar");
        Message m = new Message(id, roomId, userId, name, content, time);
        if (avatar.equals("") || avatar.equals("null"))
            m.setAvatar(context.getResources().getDrawable(R.drawable.avatar));
        else
            m.setAvatar(avatar);
        return m;
    }

    public static HttpURLConnection postConnection(String postUrl, String params) throws
            IOException {
        URL url = new URL(postUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//        Log.d("ChatActivity", postUrl + "?" + params);
        conn.setConnectTimeout(10000);

        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
        conn.setRequestProperty("Content-Length", params.length() + "");
        conn.setRequestProperty("Connection", "close");
        conn.connect();
        return conn;
    }

    public static void postMessage(String... params) throws IOException, JSONException {
        String urlParams = "chatroom_id=" + params[1] + "&user_id=" + params[2] + "&name=" + params[3] + "&message=" + params[4];
        HttpURLConnection conn = postConnection(params[0], urlParams);
        OutputStream os = conn.getOutputStream();
        os.write(urlParams.getBytes());
        os.flush();
        os.close();

        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            Log.d("ChatActivity", "success");
            InputStream inputStream = conn.getInputStream();
            String results = readStream(inputStream);
            Log.d("ChatActivity", "content    " + results);
        }
        conn.disconnect();
    }

    public static ArrayList<User> fetchFriendList(Context context, String url) throws IOException {
        ArrayList<User> fList = new ArrayList<>();
        String results = readStream(getConnection(url).getInputStream());
        try {
            JSONArray data = new JSONObject(results).getJSONArray("data");
            for (int i = 0; i < data.length(); i++) {
                JSONObject friend = data.getJSONObject(i);
                Drawable drawable;
                String avatarStr = friend.getString("avatar");
                User user = new User(friend.getInt("friend_id"), friend.getString("friend_name"));
                if (avatarStr.equals("") || avatarStr.equals("null")) {
                    drawable = context.getResources().getDrawable(R.drawable.avatar);
                } else {
                    drawable = ImageUtil.StringToDrawable(avatarStr);
                }
                user.setAvatar(drawable);
                fList.add(user);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("Friends", results);// 打印获取信息
        return fList;
    }

    public static User searchFriend(Context context, String... params) throws IOException, JSONException {
        User friend = new User();
        String urlParams = "&user_id=" + params[1] + "&friend_id=" + params[2];
        HttpURLConnection conn = postConnection(params[0], urlParams);
        OutputStream os = conn.getOutputStream();
        os.write(urlParams.getBytes());
        os.flush();
        os.close();

        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            InputStream inputStream = conn.getInputStream();
            String results = readStream(inputStream);
            JSONObject jsonObject;
            jsonObject = new JSONObject(String.valueOf(results));
            String status = jsonObject.getString("status");

            if (status.equals("OK")) {
                JSONArray data = jsonObject.getJSONArray("data");
                friend.id = Integer.parseInt(data.getJSONObject(0).getString("id"));
                friend.username = data.getJSONObject(0).getString("username");
                String avatarStr = data.getJSONObject(0).getString("avatar");
                if (avatarStr.equals("") || avatarStr.equals("null")) {
                    friend.setAvatar(context.getResources().getDrawable(R.drawable.avatar));
                } else {
                    friend.setAvatar(avatarStr);
                }
                conn.disconnect();
                return friend;
            }
        }
        conn.disconnect();
        return null;
    }

    public static String addFriend(String... params) throws IOException, JSONException {
        String urlParams = "&user_id=" + params[1] + "&friend_id=" + params[2] + "&friend_name=" + params[3];
        HttpURLConnection conn = postConnection(params[0], urlParams);
        OutputStream os = conn.getOutputStream();
        os.write(urlParams.getBytes());
        os.flush();
        os.close();

        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            InputStream inputStream = conn.getInputStream();
            String results = readStream(inputStream);
            JSONObject jsonObject;
            jsonObject = new JSONObject(String.valueOf(results));
            conn.disconnect();
            return jsonObject.getString("status");
        }
        conn.disconnect();
        return "ERROR";
    }

    public static String addRoom(String... params) throws IOException, JSONException {
        String urlParams = "user_name=" + params[1] + "&friend_name=" + params[2] + "&type=" + params[3];
        HttpURLConnection conn = postConnection(params[0], urlParams);
        OutputStream os = conn.getOutputStream();
        os.write(urlParams.getBytes());
        os.flush();
        os.close();

        //如果房间存在就不用再创建了，直接跳到房间
        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            InputStream inputStream = conn.getInputStream();
            String results = readStream(inputStream);
            conn.disconnect();
            JSONObject jsonObject;
            jsonObject = new JSONObject(String.valueOf(results));
            String status = jsonObject.getString("status");

            if (status.equals("OK")) {
                Log.d("AddRoom", results);
                JSONArray data = jsonObject.getJSONArray("data");
                return String.valueOf(data.getJSONObject(0).getInt("id"));
            }
        }
        conn.disconnect();
        return "ERROR";
    }

    public static String convertDate(String target) throws ParseException {
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


    public static String getAvatar(String url) throws IOException, JSONException {
        String str;
        String avatar;
        String results = readStream(getConnection(url).getInputStream());
        try {
            JSONArray data = new JSONObject(results).getJSONArray("data");
            avatar = data.getJSONObject(0).getString("avatar");

//            str = avatar;
//            int segmentSize = 3 * 1024;
//            long length = str.length();
//            while (str.length() > segmentSize ) {// 循环分段打印日志
//                String logContent = str.substring(0, segmentSize );
//                Log.e("avatar",logContent);
//                str = str.replace(logContent, "");
//            }
//            Log.e("avatar",str);// 打印剩余日志

            return avatar;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void postAvatar(String... params) throws IOException, JSONException {
        //String str = params[1].replace("+", "%2B").replace("=", "%3D").replaceAll("[\\s*\t\n\r]", "");
        //URLEncoder.encode(str) 特殊字符转义问题
        String urlParams = "avatar=" + URLEncoder.encode(params[1]) + "&user_id=" + params[2];
        HttpURLConnection conn = postConnection(params[0], urlParams);
        OutputStream os = conn.getOutputStream();
        os.write(urlParams.getBytes());
        os.flush();
        os.close();

        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            Log.d("Avatar", "success");
            InputStream inputStream = conn.getInputStream();
            String results = readStream(inputStream);
        }
        conn.disconnect();
    }
}

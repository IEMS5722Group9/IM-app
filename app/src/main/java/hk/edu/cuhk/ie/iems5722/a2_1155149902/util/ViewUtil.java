package hk.edu.cuhk.ie.iems5722.a2_1155149902.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Base64;

import com.example.qrcode.utils.CommonUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ViewUtil {
        /**
         * 将照片存储为字符串形式(经过BASE64编码)
         */
        public static String DrawableToString(Drawable drawable) {
            if(drawable == null)
            {
                return "";
            }
            BitmapDrawable bd = (BitmapDrawable)drawable;
            Bitmap bmp = bd.getBitmap();
            if(bmp == null)
                return "";
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            //压缩图片
            bmp.compress(Bitmap.CompressFormat.PNG, 5, stream);
            byte[] b = stream.toByteArray();
            // Base64Coder编码，将图片流以字符串形式存储下来
            return Base64Coder.encodeLines(b);
        }

        /**
         * 将Base64Coder编码后的字符串转换为Drawable
         */
        public static Drawable StringToDrawable(String encodeStr) {
            if(encodeStr==null || encodeStr.isEmpty())
            {
                return null;
            }
            //Base64Coder解码
            ByteArrayInputStream in = new ByteArrayInputStream(Base64Coder.decodeLines(encodeStr));
            Bitmap dBitmap = BitmapFactory.decodeStream(in);
            Drawable drawable = new BitmapDrawable(dBitmap);
            return drawable;

        }

    public static Bitmap stringtoBitmap(String string) {
        //将字符串转换成Bitmap类型
        Bitmap bitmap = null;
        try {
            byte[] bitmapArray;
            bitmapArray = Base64.decode(string, Base64.URL_SAFE);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }


    public static String bitmaptoString(Bitmap bitmap) {
        //将Bitmap转换成字符串
        String string = null;
        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bStream);
        byte[] bytes = bStream.toByteArray();
        string = Base64.encodeToString(bytes, Base64.NO_WRAP);
        return string;
    }

}

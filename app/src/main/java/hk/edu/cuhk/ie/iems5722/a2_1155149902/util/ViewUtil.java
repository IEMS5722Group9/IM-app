package hk.edu.cuhk.ie.iems5722.a2_1155149902.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Base64;
import android.util.Log;

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
            //bmp.compress(Bitmap.CompressFormat.PNG, 10, stream);

            bmp.compress(Bitmap.CompressFormat.JPEG, 90, stream);
            float zoom = (float)Math.sqrt(5 * 1024 / (float)stream.toByteArray().length);
            Matrix matrix = new Matrix();
            matrix.setScale(zoom, zoom);
            Bitmap result = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
            stream.reset();
            result.compress(Bitmap.CompressFormat.JPEG, 90, stream);
            while(stream.toByteArray().length > 5 * 1024){
                System.out.println(stream.toByteArray().length);
                matrix.setScale(0.6f, 0.6f);
                result = Bitmap.createBitmap(result, 0, 0, result.getWidth(), result.getHeight(), matrix, true);
                stream.reset();
                result.compress(Bitmap.CompressFormat.JPEG, 90, stream);
            }

//            Matrix matrix = new Matrix();
//            matrix.setScale(0.5f, 0.5f);
//            Bitmap afterBmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(),
//                    bmp.getHeight(), matrix, true);
//            stream.reset();
//            afterBmp.compress(Bitmap.CompressFormat.PNG, 1, stream);
            Log.i("wechat", "压缩后图片的大小" + (result.getByteCount() / 1024 )
                    + "kb宽度为" + result.getWidth() + "高度为" + result.getHeight());

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

}

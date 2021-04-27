package hk.edu.cuhk.ie.iems5722.a2_1155149902.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageUtil {
    /**
     * 保存图片到指定路径
     *
     * @param context
     * @param bitmap   要保存的图片
     * @param fileName 自定义图片名称
     * @return
     */
    public static boolean saveImageToGallery(Context context, Bitmap bitmap, String fileName) {
        // 保存图片至指定路径
        String galleryPath = Environment.getExternalStorageDirectory()
                + File.separator + Environment.DIRECTORY_DCIM
                + File.separator + "Camera" + File.separator;
        //String storePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "qrcode";
        File appDir = new File(galleryPath);

        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            //通过io流的方式来压缩保存图片(80代表压缩20%)
            boolean isSuccess = bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
            fos.flush();
            fos.close();

            //发送广播通知系统图库刷新数据
            Uri uri = Uri.fromFile(file);
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
            return isSuccess;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 将照片存储为字符串形式(经过BASE64编码)
     */
    public static String DrawableToString(Drawable drawable) {
        if (drawable == null) {
            return "";
        }
        BitmapDrawable bd = (BitmapDrawable) drawable;
        Bitmap bmp = bd.getBitmap();
        if (bmp == null)
            return "";
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        //压缩图片
        bmp.compress(Bitmap.CompressFormat.JPEG, 90, stream);
        float zoom = (float) Math.sqrt(5 * 1024 / (float) stream.toByteArray().length);
        Matrix matrix = new Matrix();
        matrix.setScale(zoom, zoom);
        Bitmap result = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
        stream.reset();
        result.compress(Bitmap.CompressFormat.JPEG, 90, stream);
        while (stream.toByteArray().length > 5 * 1024) {
            System.out.println(stream.toByteArray().length);
            matrix.setScale(0.6f, 0.6f);
            result = Bitmap.createBitmap(result, 0, 0, result.getWidth(), result.getHeight(), matrix, true);
            stream.reset();
            result.compress(Bitmap.CompressFormat.JPEG, 90, stream);
        }

        Log.i("wechat", "压缩后图片的大小" + (result.getByteCount() / 1024)
                + "kb宽度为" + result.getWidth() + "高度为" + result.getHeight());

        byte[] b = stream.toByteArray();
        // Base64Coder编码，将图片流以字符串形式存储下来
        return Base64Coder.encodeLines(b);
    }

    /**
     * 将Base64Coder编码后的字符串转换为Drawable
     */
    public static Drawable StringToDrawable(String encodeStr) {
        if (encodeStr == null || encodeStr.isEmpty()) {
            return null;
        }
        //Base64Coder解码
        ByteArrayInputStream in = new ByteArrayInputStream(Base64Coder.decodeLines(encodeStr));
        Bitmap dBitmap = BitmapFactory.decodeStream(in);
        return new BitmapDrawable(dBitmap);

    }

}
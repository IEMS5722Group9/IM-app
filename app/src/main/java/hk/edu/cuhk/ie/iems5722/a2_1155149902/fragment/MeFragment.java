package hk.edu.cuhk.ie.iems5722.a2_1155149902.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.json.JSONException;

import java.io.IOException;

import hk.edu.cuhk.ie.iems5722.a2_1155149902.activity.AddFriendsActivity;
import hk.edu.cuhk.ie.iems5722.a2_1155149902.activity.LoginActivity;
import hk.edu.cuhk.ie.iems5722.a2_1155149902.model.User;
import hk.edu.cuhk.ie.iems5722.a2_1155149902.util.HttpUtil;
import hk.edu.cuhk.ie.iems5722.a2_1155149902.util.ImageUtil;
import hk.edu.cuhk.ie.iems5722.a2_1155149902.util.QRCodeUtil;
import hk.edu.cuhk.ie.iems5722.a2_1155149902.R;
import hk.edu.cuhk.ie.iems5722.a2_1155149902.activity.MainActivity;
import hk.edu.cuhk.ie.iems5722.a2_1155149902.util.UrlUtil;

import static android.app.Activity.RESULT_CANCELED;

public class MeFragment extends Fragment {
    private String userId;
    private String username;
    private Button btn_QR;
    private ImageView iv_qrcode;
    private ImageView iv_me;
    private TextView tv_name;
    private TextView tv_id;
    private String avatar;

    private String content;//二维码内容
    private int width, height;//宽度，高度
    private int color_black, color_white;//黑色色块，白色色块

    private Bitmap qrcode_bitmap;//生成的二维码
    private Context context;

    private static final int ALBUM_RET_CODE = 1;
    private static final int CAMERA_RET_CODE = 2;
    private static final int FINISH_RET_CODE = 3;
    private static final String PHOTO_NAME = "avatar.jpg";

    private String baseUrl = UrlUtil.BaseUrl;
    private String getAvatarUrl = baseUrl + "/api/a3/get_avatar";
    private String postAvatarUrl = baseUrl + "/api/a3/post_avatar";

    public MeFragment(Context context) {
        //getContentResolver()需要通过activity来实现。
        this.context = context;
    }

    public MeFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_me, container, false);
        tv_name = (TextView) root.findViewById(R.id.username);
        tv_id = (TextView) root.findViewById(R.id.userId);
        tv_name.setText(username);
        tv_id.setText("ID: " + userId);
//        btn_QR = (Button) root.findViewById(R.id.QR_button);
//        btn_QR.setOnClickListener(this);
        iv_qrcode = (ImageView) root.findViewById(R.id.iv_qrcode);
        iv_qrcode.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                imgChooseDialog();
                return true;
            }
        });
        generateQrcodeAndDisplay();
        iv_me = (ImageView) root.findViewById(R.id.me_image);

        String URL = getAvatarUrl + "?username=" + username;
        new AvatarGetTask().execute(URL);
        //new AvatarGetTask().execute(getAvatarUrl, userId);

        iv_me.bringToFront();
        iv_me.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**打开相册*/
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                //返回时将结果也返回
                startActivityForResult(intent, ALBUM_RET_CODE);
            }
        });
        return root;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Bundle bundle = ((MainActivity) context).toValue();
        userId = bundle.getString("userId");
        username = bundle.getString("username");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //设置另外的menu
        menu.clear();
        inflater.inflate(R.menu.menu_logout, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.toolbar_logout) {
            AlertDialog.Builder choiceBuilder = new AlertDialog.Builder(getActivity());
            choiceBuilder.setCancelable(false);
            choiceBuilder
                    .setTitle("Log out？")
                    .setNeutralButton("Yes", new DialogInterface.OnClickListener() {//添加"Yes"按钮
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            requireActivity().startActivity(new Intent(getActivity(), LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {//添加取消
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) { }
                    });
            choiceBuilder.create();
            choiceBuilder.show();
        }
        return true;
    }

    /**
     * 生成二维码并显示
     */
    private void generateQrcodeAndDisplay() {
        content = userId;
        width = 650;
        height = 650;
        color_black = Color.BLACK;
        color_white = Color.WHITE;

        qrcode_bitmap = QRCodeUtil.createQRCodeBitmap(content, width, height, "UTF-8", color_black, color_white);
        iv_qrcode.setImageBitmap(qrcode_bitmap);
    }

    /**
     * 保存图片至本地
     *
     * @param bitmap
     */
    private void saveImg(Bitmap bitmap) {
        String fileName = "qr_" + System.currentTimeMillis() + ".jpg";
        boolean isSaveSuccess = ImageUtil.saveImageToGallery(getActivity(), bitmap, fileName);
        if (isSaveSuccess) {
            Toast.makeText(getActivity(), "Save picture successfully", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getActivity(), "Save failed", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 长按二维码图片弹出保存
     */
    private void imgChooseDialog() {
        AlertDialog.Builder choiceBuilder = new AlertDialog.Builder(getActivity());
        choiceBuilder.setCancelable(false);
        choiceBuilder
                .setTitle("Save QRcode to album？")
                .setNeutralButton("Yes", new DialogInterface.OnClickListener() {//添加"Yes"按钮
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        saveImg(qrcode_bitmap);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {//添加取消
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        choiceBuilder.create();
        choiceBuilder.show();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                // 从相册获取
                case ALBUM_RET_CODE:
                    startPhotoZoom(data.getData());
                    break;
                // 取得裁剪后的图片
                case FINISH_RET_CODE:
                    /**
                     * 非空判断一定要验证
                     *如果不验证的话,在剪裁之后如果发现不满意，丢弃当前照片时，会报NullException
                     */
                    if (data != null) {
                        setPicToView(data);
                    }
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 裁剪照片
     */
    public void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 下面这个crop=true是设置在开启的Intent中设置显示的view可裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, FINISH_RET_CODE);
    }

    private void setPicToView(Intent picdata) {
        Bundle extras = picdata.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            Drawable drawable = new BitmapDrawable(photo);
            String avatar = ImageUtil.DrawableToString(drawable);
            //Log.e("avatar", avatar);
            //String avatar = ViewUtil.bitmaptoString(photo);
            new AvatarPostTask().execute(postAvatarUrl, avatar, userId);
            iv_me.setImageDrawable(drawable);
            //iv_me.setImageBitmap(photo);
        }
    }

    class AvatarPostTask extends AsyncTask<String, Void, User> {
        @Override
        protected User doInBackground(String... params) {
            try {
                HttpUtil.postAvatar(params);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(User me) {

        }
    }

    class AvatarGetTask extends AsyncTask<String, Void, User> {
        @Override
        protected User doInBackground(String... params) {
            try {
                // avatar = HttpUtil.getAvatar(params);
                avatar = HttpUtil.getAvatar(params[0]);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(User me) {
            if (avatar == null || avatar.equals("null")) {
                iv_me.setImageResource(R.drawable.avatar);
            } else {
//                Bitmap bitmap = ViewUtil.stringtoBitmap(avatar);
//                iv_me.setImageBitmap(bitmap);
                Drawable drawable = ImageUtil.StringToDrawable(avatar);
                iv_me.setImageDrawable(drawable);
                //me.setAvatar(avatar);
                //((MainActivity)getActivity()).setAvatar(avatar);
            }
        }
    }
}
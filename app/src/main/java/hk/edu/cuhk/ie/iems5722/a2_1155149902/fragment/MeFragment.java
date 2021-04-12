package hk.edu.cuhk.ie.iems5722.a2_1155149902.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import hk.edu.cuhk.ie.iems5722.a2_1155149902.util.ImageUtil;
import hk.edu.cuhk.ie.iems5722.a2_1155149902.util.QRCodeUtil;
import hk.edu.cuhk.ie.iems5722.a2_1155149902.R;
import hk.edu.cuhk.ie.iems5722.a2_1155149902.activity.MainActivity;

public class MeFragment extends Fragment implements View.OnClickListener{
    private String userId;
    private Button btn_QR;
    private ImageView iv_qrcode;

    private String content;//二维码内容
    private int width, height;//宽度，高度
    private int color_black, color_white;//黑色色块，白色色块

    private Bitmap qrcode_bitmap;//生成的二维码

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_me, container, false);
        btn_QR = (Button) root.findViewById(R.id.QR_button);
        btn_QR.setOnClickListener(this);
        iv_qrcode = (ImageView) root.findViewById(R.id.iv_qrcode);
        return root;
    }

    @Override
    public void onClick(View view) {
        generateQrcodeAndDisplay();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Bundle bundle = ((MainActivity) context).toValue();
        userId = bundle.getString("userId");
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
     * @param bitmap
     */
    private void saveImg(Bitmap bitmap){
        String fileName = "qr_"+System.currentTimeMillis() + ".jpg";
        boolean isSaveSuccess = ImageUtil.saveImageToGallery(getActivity(), bitmap,fileName);
        if (isSaveSuccess) {
            Toast.makeText(getActivity(), "图片已保存至本地", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getActivity(), "保存图片失败，请稍后重试", Toast.LENGTH_SHORT).show();
        }
    }
}
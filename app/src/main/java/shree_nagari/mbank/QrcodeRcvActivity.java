package shree_nagari.mbank;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.FileProvider;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Key;
import java.security.PrivateKey;

public class QrcodeRcvActivity extends Fragment implements View.OnClickListener {
    MainActivity act;
    QrcodeRcvActivity receiveMoneyQRAct = null;
    ImageView myImage;
    TextView txt_heading, qr_str;
    ImageButton btn_home;//, btn_back;
    Button btn_share_qr;
    String accNo = "", accNm = "";
    Bitmap bitmap = null;
    FileOutputStream jpgFile = null;
    ImageView img_heading;
    PrivateKey var1 = null;
    String var5 = "", var3 = "";
    Key var2 = null;

    public QrcodeRcvActivity() {
    }

    @SuppressLint("ValidFragment")
    public QrcodeRcvActivity(MainActivity a) {
        // System.out.println("BalanceRep()" + a);
        act = a;
        receiveMoneyQRAct = this;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e("Debug", "ReceiveMoneyQR invoked");
        var1 = act.var1;
        var3 = act.var3;
        View rootView = inflater.inflate(R.layout.receive_qr, container, false);
        txt_heading = (TextView) rootView.findViewById(R.id.txt_heading);
        myImage = (ImageView) rootView.findViewById(R.id.img_result);
        btn_home = (ImageButton) rootView.findViewById(R.id.btn_home);
        /*btn_back = (ImageButton) rootView.findViewById(R.id.btn_back);*/
        qr_str = (TextView) rootView.findViewById(R.id.qr_str);
        btn_share_qr = (Button) rootView.findViewById(R.id.btn_share_qr);
        img_heading = (ImageView) rootView.findViewById(R.id.img_heading);
        img_heading.setBackgroundResource(R.mipmap.fund_trnsfer);
        txt_heading.setText(R.string.lbl_qr_code);
        //btn_home.setImageResource(R.drawable.ic_home_d);
        //btn_back.setImageResource(R.drawable.backover);

        //btn_back.setOnClickListener(this);
        btn_home.setOnClickListener(this);
        btn_share_qr.setOnClickListener(this);

        accNo = getArguments().getString("ACCNO");
        accNm = getArguments().getString("ACCNM");


        qr_str.setText("This Is Your QR Code For Account " + accNo
                + ". Use This To Request Money.");
        try {
            WindowManager manager = (WindowManager) getActivity()
                    .getSystemService(Context.WINDOW_SERVICE);
            Display display = manager.getDefaultDisplay();
            Point point = new Point();
            display.getSize(point);
            int width = point.x;
            int height = point.y;
            int smallerDimension = width < height ? width : height;
            smallerDimension = smallerDimension * 3 / 4;

            // Log.e("GenQRCode","accNo==="+accNo);
            QRCodeEncoder qrCodeEncoder = new QRCodeEncoder(strToSend(accNo),
                    null, Contents.Type.TEXT, BarcodeFormat.QR_CODE.toString(),
                    smallerDimension);
            try {
                bitmap = qrCodeEncoder.encodeAsBitmap();

                myImage.setImageBitmap(bitmap);
                myImage.setVisibility(ImageView.VISIBLE);
                qr_str.setVisibility(TextView.VISIBLE);

            } catch (WriterException e) {
                e.printStackTrace();
            }
        } catch (ActivityNotFoundException activity) {
            // qrDroidRequired(MainActivity.this);
        }

        return rootView;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_home) {
            Intent in = new Intent(act, NewDashboard.class);
            in.putExtra("VAR1", var1);
            in.putExtra("VAR3", var3);
            startActivity(in);
            act.finish();
        } /*else if (v.getId() == R.id.btn_back) {
			Fragment fragment = new ShowAccForQrcode(act);
			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.frame_container, fragment).commit();
			act.frgIndex = 5;
		} */ else if (v.getId() == R.id.btn_share_qr) {
            Bitmap icon = bitmap;
            Intent share = new Intent(Intent.ACTION_SEND);
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            icon.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
            Log.e("A", "accNo ==" + accNo);
            File f = new File(Environment.getExternalStorageDirectory() + File.separator + accNo + ".jpg");
            boolean flg = false;

            if (f.exists()) {
                flg = f.delete();
            }
            f = new File(Environment.getExternalStorageDirectory() + File.separator + accNm + accNo.substring(12) + ".jpg");

            try {

                FileOutputStream out = new FileOutputStream(f);
                Canvas canvas = new Canvas(bitmap);
                Paint paint = new Paint();
                paint.setColor(Color.BLACK); // Text Color
                //paint.setStrokeWidth(25); // Text Size
                paint.setTextSize(18);
                paint.setTextAlign(Align.LEFT);
                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER)); // Text Overlapping Pattern
                canvas.drawBitmap(bitmap, 0, 0, paint);
                canvas.drawText(accNm + "\n***" + accNo.substring(12), 20, bitmap.getHeight() - 9, paint);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                out.flush();
                out.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                share.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                Uri apkURI = FileProvider.getUriForFile(act, act.getPackageName(), f);
                share.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///sdcard/" + accNo + ".jpg"));
                share.putExtra(Intent.EXTRA_STREAM, apkURI);
                share.setType("image/*");
                startActivity(Intent.createChooser(share, "Share Image"));
            } catch (Exception e) {
				e.printStackTrace();
            }
        }
    }

    public String strToSend(String str) {
        int sum = 0, grandSum = 0;
        for (int i = 0; i < str.length(); i++) {
            sum = sum + Integer.parseInt("" + str.charAt(i));
        }
        while (sum > 9) {
            grandSum = 0;
            while (sum > 0) {
                int rem;
                rem = sum % 10;
                grandSum = grandSum + rem;
                sum = sum / 10;
            }
            sum = grandSum;
        }
        return str + grandSum;
    }
}

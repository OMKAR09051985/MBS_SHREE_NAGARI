package shree_nagari.mbank;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils.TruncateAt;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Map;

import javax.crypto.spec.SecretKeySpec;

import mbLib.CryptoClass;
import mbLib.CustomDialogClass;
import mbLib.DatabaseManagement;
import mbLib.DialogBox;
import mbLib.MBSUtils;

public class SplashPage extends Activity implements OnClickListener {
    Button continueBtn;
    TextView txt_version_no, dynamicmsg;
    TextView txt_welcome;
    ImageView continue_button;
    DialogBox dbs;
    String version = "", customerId = "", retVal = "";
    private static final int SWIPE_MIN_DISTANCE = 60;
    private static final int SWIPE_THRESHOLD_VELOCITY = 400;
    private ViewFlipper mViewFlipper;
    private AnimationListener mAnimationListener;
    private Context mContext;
    private static String NAMESPACE = "";
    private static String URL = "";
    private static String SOAP_ACTION = "";
    private static String METHOD_NAME1 = "";
    private static final int REQUEST_APP_SETTINGS = 168;
    DatabaseManagement dbms;
    String respcode = "", retval = "", respdesc = "", versionFlg = "", retMess = "", var3 = "", var5 = "";
    boolean custIdFlg = false;
    Map<String, Object> keys = null;
    static PrivateKey var1 = null;
    static PublicKey var4 = null;
    SecretKeySpec var2 = null;
    int flag = 0;
    String custId="";
    private final GestureDetector detector = new GestureDetector(new SwipeGestureDetector());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(LayoutParams.FLAG_SECURE, LayoutParams.FLAG_SECURE);
        setContentView(R.layout.splash_page);
        Log.e("Shubham", "---------------------in SplashPage onCreate  Activity----------------");
		/*if (!new DeviceUtils().isEmulator()) {
			MBSUtils.ifGooglePlayServicesValid(SplashPage.this);
		} else {
			MBSUtils.showAlertDialogAndExitApp(getString(R.string.alert_sup),SplashPage.this);
		}*/
        try {
            keys = CryptoClass.Function1();
            var1 = (PrivateKey) keys.get("private");
            var4 = (PublicKey) keys.get("public");
            Log.e("TAG", "SplashPagePrivateKey:- " + var1.toString() + " SplashPagePublicKey:- " + var4);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("TAG", "SplashPageKeyError" + e.getMessage());
        }
        Bundle bObj = getIntent().getExtras();

        txt_welcome = (TextView) findViewById(R.id.welcome);
        continue_button = (ImageView) findViewById(R.id.continue_button);
        continue_button.setOnClickListener(this);
        //continue_button.setEnabled(false);
        //Log.e("bObj=====","bObj======="+bObj);

        String fromActivity = "";
        if (bObj != null) {
            fromActivity = bObj.getString("FROMACT");
            //Log.e("fromActivity=====","fromActivity======="+fromActivity);
            if (fromActivity != null && (fromActivity.equalsIgnoreCase("THREAD")))//||fromActivity.equalsIgnoreCase("DASHBOARDACT")))
            {
                txt_welcome.setText(getString(R.string.txt_timeout));
            }
        } else {
            txt_welcome.setText(getString(R.string.lbl_welcome_to));
        }
        dynamicmsg = (TextView) findViewById(R.id.dynamicmsg);
        dynamicmsg.setSelected(true);
        dynamicmsg.setEllipsize(TruncateAt.MARQUEE);
        dynamicmsg.setSingleLine(true);
        continueBtn = (Button) findViewById(R.id.continue_btn);
        txt_version_no = (TextView) findViewById(R.id.txt_version_no);
        mContext = this;
        mViewFlipper = (ViewFlipper) this.findViewById(R.id.view_flipper);

        mViewFlipper.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(final View view, final MotionEvent event) {
                detector.onTouchEvent(event);
                return true;
            }
        });

        mAnimationListener = new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {
            }

            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {
            }
        };

        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(
                    getPackageName(), 0);
            version = pInfo.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        continueBtn.setOnClickListener(this);
        //continueBtn.setClickable(false);
        txt_version_no.setText("Version : " + version);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_PHONE_STATE)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
            }
        } else {

            flag = chkConnectivity();
            if (flag == 0) {
                new CallWSFirst().execute();
            }
			/*CallWebService_dynamic_msg c = new CallWebService_dynamic_msg();
			c.execute();*/
        }
		
		/*new Timer().schedule(new TimerTask(){
            public void run() {
                startActivity(new Intent(SplashPage.this, SBKLoginActivity.class));
                finish();
 
                Log.d("MainActivity:", "onCreate: waiting 5 seconds for MainActivity... loading PrimaryActivity.class");
            }
        }, 5000 );*/
        dbms = new DatabaseManagement("shree_nagari.mbank", "listMobileBanking");
        Cursor c1 = dbms.selectFromTable("SHAREDPREFERENCE", "", null);// ("select * from ",
        // null);
        if (c1 != null) {
            while (c1.moveToNext()) {
                custId = c1.getString(2);
                Log.e("Shubham","Cust ID from SQLITE:- "+custId);

            }
        }
    }

    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                if (permissions[0]
                        .equalsIgnoreCase(Manifest.permission.READ_PHONE_STATE)) {
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                        // Should we show an explanation?
                        if (ActivityCompat.shouldShowRequestPermissionRationale(
                                this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        } else {

                            ActivityCompat
                                    .requestPermissions(
                                            this,
                                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                            1);
                        }
                    }
                } else if (permissions[0]
                        .equalsIgnoreCase(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {

                        // Should we show an explanation?
                        if (ActivityCompat.shouldShowRequestPermissionRationale(
                                this, Manifest.permission.READ_CONTACTS)) {
                        } else {

                            ActivityCompat
                                    .requestPermissions(
                                            this,
                                            new String[]{Manifest.permission.READ_CONTACTS},
                                            1);
                        }
                    }
                } else if (permissions[0]
                        .equalsIgnoreCase(Manifest.permission.READ_CONTACTS)) {

                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                        // Should we show an explanation?
                        if (ActivityCompat.shouldShowRequestPermissionRationale(
                                this,
                                Manifest.permission.ACCESS_FINE_LOCATION)) {
                        } else {

                            ActivityCompat
                                    .requestPermissions(
                                            this,
                                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                            1);
                        }
                    }
                } else if (permissions[0]
                        .equalsIgnoreCase(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                        // Should we show an explanation?
                        if (ActivityCompat.shouldShowRequestPermissionRationale(
                                this, Manifest.permission.CAMERA)) {
                        } else {

                            ActivityCompat
                                    .requestPermissions(
                                            this,
                                            new String[]{Manifest.permission.CAMERA},
                                            1);
                        }
                    }
                } else if (permissions[0]
                        .equalsIgnoreCase(Manifest.permission.CAMERA)) {
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                        // Should we show an explanation?
                        if (ActivityCompat.shouldShowRequestPermissionRationale(
                                this, Manifest.permission.CAMERA)) {
                        } else {

                            ActivityCompat
                                    .requestPermissions(
                                            this,
                                            new String[]{Manifest.permission.CAMERA},
                                            1);
                        }
                    }
                }

                return;
            }
        }
    }

    @Override
    public void onClick(View arg0) {
        if (versionFlg.equalsIgnoreCase("2")) {
            retMess = getString(R.string.alert_oldversion);
            showAlert1(retMess);
            //setAlert();
        } else if ((ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                &&
                (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED)

                &&
                (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
            //Intent in = new Intent(this, SBKLoginActivity.class);
            Log.e("Shubham", "---------------------in SplashPage Activity on click----------1------");
            Intent in = new Intent(this, LoginActivity.class);
            in.putExtra("VAR1", var1);
            in.putExtra("VAR3", var3);
            startActivity(in);
            finish();

        } else {
            showAlert("Please grant all permissions");
        }
    }

    class SwipeGestureDetector extends SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {
            try {
                if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
                        && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    mViewFlipper.setInAnimation(AnimationUtils.loadAnimation(
                            mContext, R.anim.left_in));
                    mViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(
                            mContext, R.anim.left_out));
                    mViewFlipper.getInAnimation().setAnimationListener(
                            mAnimationListener);
                    mViewFlipper.showNext();
                    return true;
                } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
                        && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    mViewFlipper.setInAnimation(AnimationUtils.loadAnimation(
                            mContext, R.anim.right_in));
                    mViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(
                            mContext, R.anim.right_out));
                    mViewFlipper.getInAnimation().setAnimationListener(
                            mAnimationListener);
                    mViewFlipper.showPrevious();
                    return true;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return false;
        }
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
		/*if(new DeviceUtils().isDeviceRooted(getApplicationContext())){
	        showAlertDialogAndExitApp("This device is rooted. You can't use this app.");
	    }
		else{*/
        mViewFlipper.setInAnimation(AnimationUtils.loadAnimation(mContext,
                R.anim.left_in));
        mViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(mContext,
                R.anim.left_out));
        mViewFlipper.getInAnimation().setAnimationListener(mAnimationListener);
        mViewFlipper.showNext();
        //}

    }

    public void showAlertDialogAndExitApp(String message) {

        AlertDialog alertDialog = new AlertDialog.Builder(SplashPage.this).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                });

        alertDialog.show();
    }


    class CallWebService_dynamic_msg extends AsyncTask<Void, Void, Void> {
        JSONObject jsonObj = new JSONObject();
        String ValidationData = "";
        LoadProgressBar loadProBarObj = new LoadProgressBar(SplashPage.this);

        @Override
        protected void onPreExecute() {
            loadProBarObj.show();
            try {
                respcode = "";
                retval = "";
                respdesc = "";
                jsonObj.put("IMEINO", MBSUtils.getImeiNumber(SplashPage.this));
                jsonObj.put("SIMNO", MBSUtils.getSimNumber(SplashPage.this));
                jsonObj.put("VERSION", version);
                jsonObj.put("METHODCODE", "53");
                // ValidationData=MBSUtils.getValidationData(SplashPage.this,jsonObj.toString());
            } catch (JSONException je) {
                je.printStackTrace();
            }

        }

        ;

        @Override
        protected Void doInBackground(Void... arg0) {
            String value4 = getString(R.string.namespace);
            String value5 = getString(R.string.soap_action);
            String value6 = getString(R.string.url);
            final String value7 = "callWebservice";
            try {
                String keyStr = CryptoClass.Function2();
                var2 = CryptoClass.getKey(keyStr);
                SoapObject request = new SoapObject(value4, value7);

                request.addProperty("value1", CryptoClass.Function5(jsonObj.toString(), var2));
                request.addProperty("value2", CryptoClass.Function3(keyStr, var1));
                request.addProperty("value3", var3);
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.setOutputSoapObject(request);
                HttpTransportSE androidHttpTransport = new HttpTransportSE(value6, 15000);
                if (androidHttpTransport != null)
                    System.out.println("=============== androidHttpTransport is not null ");
                else
                    System.out.println("=============== androidHttpTransport is  null ");

                androidHttpTransport.call(value5, envelope);
                var5 = envelope.bodyIn.toString().trim();
                int i = envelope.bodyIn.toString().trim().indexOf("=");
                var5 = var5.substring(i + 1, var5.length() - 3);

            }// end try
            catch (Exception e) {
                System.out.println(e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(final Void result) {
            loadProBarObj.dismiss();
            JSONObject jsonObj;
            try {
                String str = CryptoClass.Function6(var5, var2);
                jsonObj = new JSONObject(str.trim());
				/*ValidationData=xml_data[1].trim();
				if(ValidationData.equals(MBSUtils.getValidationData(SplashPage.this, xml_data[0].trim())))
				{
					*/
                if (jsonObj.has("RESPCODE")) {
                    respcode = jsonObj.getString("RESPCODE");
                } else {
                    respcode = "-1";
                }
                if (jsonObj.has("RETVAL")) {
                    retval = jsonObj.getString("RETVAL");
                } else {
                    retval = "";
                }
                if (jsonObj.has("VERSIONFLG")) {
                    versionFlg = jsonObj.getString("VERSIONFLG");
                } else {
                    versionFlg = "-1";
                }
                if (jsonObj.has("RESPDESC")) {
                    respdesc = jsonObj.getString("RESPDESC");
                } else {
                    respdesc = "";
                }

                if (respdesc.length() > 0) {
                    if (respdesc.equalsIgnoreCase("Server Not Found")) {
                        showAlertserver(respdesc);
                    } else {
                        showAlert(respdesc);
                    }
                } else {
                    if (retval.indexOf("FAILED") > -1) {
                        respcode = "";
                        respdesc = "";
                    } else {
                        post_success(retval);
                    }
                }
				/*}
				else
				{
					MBSUtils.showInvalidResponseAlert(SplashPage.this);	
				}*/
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void showlogoutAlert(final String str) {
        CustomDialogClass alert = new CustomDialogClass(SplashPage.this, str) {
            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
            }

            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btn_ok:
                        if (str.equalsIgnoreCase(getString(R.string.alert_oldversionupdate))) {
                            try {
                                Intent viewIntent = new Intent("android.intent.action.VIEW",
                                        Uri.parse("https://play.google.com/store/apps/details?id=shree_nagari.mbank"));
                                startActivity(viewIntent);
                            } catch (Exception e) {
                                Toast.makeText(getApplicationContext(), "Unable to Connect Try Again...",
                                        Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                            }
                        }
                        break;

                    case R.id.btn_cancel: {
                        Log.e("Shubham", "---------------------in SplashPage Activity----------2------");
                        Intent intent = new Intent(SplashPage.this, LoginActivity.class);
                        intent.putExtra("VAR1", var1);
                        intent.putExtra("VAR3", var3);
                        startActivity(intent);
                        finish();
                    }
                    this.dismiss();
                    break;
                    default:
                        break;
                }
                dismiss();
            }

        };
        alert.show();

    }
	
	/*public void setAlert()
	{
		dbs = new DialogBox(this);
		dbs.get_adb().setMessage(retMess);
		dbs.get_adb().setPositiveButton("OK",new DialogInterface.OnClickListener() 
		{
			@Override
			public void onClick(DialogInterface arg0, int arg1) 
			{
				arg0.cancel();
				if (retMess == getString(R.string.alert_oldversion)) 
				{
					try 
					{
						Intent viewIntent = new Intent("android.intent.action.VIEW",
						Uri.parse("https://play.google.com/store/apps/details?id=shree_nagari.mbank"));
						startActivity(viewIntent);
					} 
					catch (Exception e) 
					{
						Toast.makeText(getApplicationContext(),	"Unable to Connect Try Again...",Toast.LENGTH_LONG).show();
						e.printStackTrace();
					}
				} 
						
			}
		});
		dbs.get_adb().show();
	}*/

    public void showAlert1(final String str) {
        ErrorDialogClass alert = new ErrorDialogClass(SplashPage.this, "" + str) {
            Intent in = null;

            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btn_ok:
                        if (retMess == getString(R.string.alert_oldversion)) {
                            try {
                                Intent viewIntent = new Intent("android.intent.action.VIEW",
                                        Uri.parse("https://play.google.com/store/apps/details?id=shree_nagari.mbank"));
                                startActivity(viewIntent);
                            } catch (Exception e) {
                                Toast.makeText(getApplicationContext(), "Unable to Connect Try Again...", Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                            }
                        }

                        break;
                }
                this.dismiss();

            }
        };
        alert.show();

    }

    class CallWSFirst extends AsyncTask<Void, Void, Void> {
        JSONObject jsonObj = new JSONObject();
        String version1 = "VERSION~" + version;
        LoadProgressBar loadProBarObj = new LoadProgressBar(SplashPage.this);

        protected void onPreExecute() {
            loadProBarObj.show();
            ;
            try {
                jsonObj.put("IMEINO", MBSUtils.getImeiNumber(SplashPage.this));
                jsonObj.put("PUBLICKEY", new String(Base64.encodeBase64(var4.getEncoded())));
                jsonObj.put("METHODCODE", "85");

            } catch (JSONException je) {
                je.printStackTrace();
            }
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // TODO Auto-generated method stub
            String value4 = getString(R.string.namespace);
            String value5 = getString(R.string.soap_action);
            String value6 = getString(R.string.url);
            final String value7 = "callWebservice";
            try {
                Log.e("DSP", "splashpage---version1===" + jsonObj.toString());

                SoapObject request = new SoapObject(value4, value7);
                String val = CryptoClass.Function3(jsonObj.toString(), CryptoClass.getPrivateKey());
                Log.e("SPLASHPAGE", "val==" + val);
                request.addProperty("value1", val);
                request.addProperty("value2", version1);
                request.addProperty("value3", "NA");
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                        SoapEnvelope.VER11);
                envelope.setOutputSoapObject(request);
                HttpTransportSE androidHttpTransport = new HttpTransportSE(value6,
                        45000);
                androidHttpTransport.call(value5, envelope);
                var5 = envelope.bodyIn.toString().trim();
                var5 = var5.substring(var5.indexOf("=") + 1,
                        var5.length() - 3);
            }// end try
            catch (Exception e) {
                e.printStackTrace();
                System.out.println("Exception 2");
                System.out.println("Splashpage   Exception" + e);
            }
            return null;
        }

        protected void onPostExecute(Void paramVoid) {
            loadProBarObj.dismiss();
            try {
                Log.e("DSP", "splashpage---var5===" + var5);
                String resp = CryptoClass.Function4(var5, CryptoClass.getPrivateKey());
                Log.e("DSP", "splashpage---str===" + resp);
                if (resp.indexOf("EXCEPTION") > -1) {
                    showAlertserver(respdesc);
                } else if (resp.indexOf("OLDVERSION") > -1) {
                    versionFlg = "2";
                    retMess = getString(R.string.alert_oldversion);
                    showversionAlert(retMess);
                } else {
                    JSONObject jsonObj = new JSONObject(resp.trim());

                    if (jsonObj.has("RESPCODE")) {
                        respcode = jsonObj.getString("RESPCODE");
                    } else {
                        respcode = "-1";
                    }
                    if (jsonObj.has("RESPDESC")) {
                        respdesc = jsonObj.getString("RESPDESC");
                    } else {
                        respdesc = "";
                    }

                    if (respdesc.length() > 0) {
                        if (respdesc.equalsIgnoreCase("Server Not Found")) {
                            showAlertserver(respdesc);
                        } else {
                            showAlert(respdesc);
                        }
                    } else {
                        if (respcode.equalsIgnoreCase("0")) {
                            var3 = jsonObj.getString("TOKEN");
                            Log.e("DSP", "splashpage---var3===" + var3);
                            CallWebService_dynamic_msg c = new CallWebService_dynamic_msg();
                            c.execute();
                        } else if (respcode.equalsIgnoreCase("1")) {
                            retMess = getString(R.string.alert_oldversion);
                            showversionAlert(retMess);
                        } else if (respcode.equalsIgnoreCase("2")) {
                            retMess = getString(R.string.alert_179_2);
                            showversionAlert(retMess);
                        }
                    }
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }

    public int chkConnectivity() {
        // pb_wait.setVisibility(ProgressBar.VISIBLE);
        System.out
                .println("========================= end chkConnectivity ==================");
        ConnectivityManager cm = (ConnectivityManager) this
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        try {
            NetworkInfo.State state = ni.getState();
            boolean state1 = ni.isAvailable();
            System.out
                    .println("BalanceEnquiry	in chkConnectivity () state1 ---------"
                            + state1);
            if (state1) {
                switch (state) {
                    case CONNECTED:
                        if (ni.getType() == ConnectivityManager.TYPE_MOBILE
                                || ni.getType() == ConnectivityManager.TYPE_WIFI) {

                        }
                        break;
                    case DISCONNECTED:
                        flag = 1;
                        // ////////retMess =
                        // "Network Disconnected. Please Check Network Settings.";
                        retMess = getString(R.string.alert_014);
                        showAlert(retMess);
                        /*
                         * dbs = new DialogBox(this);
                         * dbs.get_adb().setMessage(retMess);
                         * dbs.get_adb().setPositiveButton("Ok", new
                         * DialogInterface.OnClickListener() { public void
                         * onClick(DialogInterface arg0, int arg1) { arg0.cancel();
                         * } }); dbs.get_adb().show();
                         */
                        break;
                    default:
                        flag = 1;
                        // //////retMess = "Network Unavailable. Please Try Again.";
                        retMess = getString(R.string.alert_000);
                        showAlert(retMess);

                        /*
                         * dbs = new DialogBox(this);
                         * dbs.get_adb().setMessage(retMess);
                         * dbs.get_adb().setPositiveButton("Ok", new
                         * DialogInterface.OnClickListener() { public void
                         * onClick(DialogInterface arg0, int arg1) { arg0.cancel();
                         * Intent in = null; in = new
                         * Intent(getApplicationContext(), LoginActivity.class);
                         * startActivity(in); finish(); } }); dbs.get_adb().show();
                         */
                        break;
                }
            } else {
                flag = 1;
                // ////retMess = "Network Unavailable. Please Try Again.";
                retMess = getString(R.string.alert_000);
                showAlert(retMess);

                /*
                 * dbs = new DialogBox(this); dbs.get_adb().setMessage(retMess);
                 * dbs.get_adb().setPositiveButton("Ok", new
                 * DialogInterface.OnClickListener() { public void
                 * onClick(DialogInterface arg0, int arg1) { arg0.cancel();
                 * Intent in = null; in = new Intent(getApplicationContext(),
                 * LoginActivity.class); startActivity(in); finish(); } });
                 * dbs.get_adb().show();
                 */
            }
        } catch (NullPointerException ne) {

            Log.i("BalanceEnquiry", "NullPointerException Exception"
                    + ne);
            flag = 1;
            // ///////retMess = "Network Unavailable. Please Try Again.";
            retMess = getString(R.string.alert_000);
            showAlert(retMess);

            /*
             * dbs = new DialogBox(this); dbs.get_adb().setMessage(retMess);
             * dbs.get_adb().setPositiveButton("Ok", new
             * DialogInterface.OnClickListener() { public void
             * onClick(DialogInterface arg0, int arg1) { arg0.cancel(); Intent
             * in = null; in = new Intent(getApplicationContext(),
             * LoginActivity.class); startActivity(in); finish(); } });
             * dbs.get_adb().show();
             */

        } catch (Exception e) {
            Log.i("BalanceEnquiry   mayuri", "Exception" + e);
            flag = 1;
            // ///////retMess = "Network Unavailable. Please Try Again.";
            retMess = getString(R.string.alert_000);
            showAlert(retMess);

            /*
             * dbs = new DialogBox(this); dbs.get_adb().setMessage(retMess);
             * dbs.get_adb().setPositiveButton("Ok", new
             * DialogInterface.OnClickListener() { public void
             * onClick(DialogInterface arg0, int arg1) { arg0.cancel(); Intent
             * in = null; in = new Intent(getApplicationContext(),
             * LoginActivity.class); startActivity(in); finish(); } });
             * dbs.get_adb().show();
             */
        }
        System.out
                .println("========================= end chkConnectivity ==================");
        return flag;
    }// end chkConnectivity

    public void showversionAlert(final String str) {
        Log.e("SAM", "===ShowAlert ");
        ErrorDialogClass alert = new ErrorDialogClass(SplashPage.this, "" + str) {
            Intent in = null;

            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btn_ok:
                        if (retMess == getString(R.string.alert_oldversion)) {
                            try {
                                Intent viewIntent = new Intent("android.intent.action.VIEW",
                                        Uri.parse("https://play.google.com/store/apps/details?id=shree_nagari.mbank"));
                                startActivity(viewIntent);
                            } catch (Exception e) {
                                Toast.makeText(getApplicationContext(), "Unable to Connect Try Again...", Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                            }
                        }
                        break;
                }
                this.dismiss();

            }
        };
        alert.show();

    }

    public void post_success(String retval) {
        //continueBtn.setClickable(true);
        if (versionFlg.equalsIgnoreCase("1")) {
            showlogoutAlert(getString(R.string.alert_oldversionupdate));
        } else if (versionFlg.equalsIgnoreCase("2")) {
            retMess = getString(R.string.alert_oldversion);
            showAlert1(retMess);
            //setAlert();
        } else if ((ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                &&
                (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED)

                &&
                (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
           // if(custId.length()>0) {
                Intent in = new Intent(this, LoginActivity.class);
                in.putExtra("VAR1", var1);
                in.putExtra("VAR3", var3);
                startActivity(in);
                finish();
           // }

        } else {
            showAlert("Please grant all permissions");
        }
		
		/*try 
		{
			JSONArray ja = new JSONArray(retval);
			int count = 0;
			String data = "";
			for (int j = 0; j < ja.length(); j++) 
			{
				JSONObject jObj = ja.getJSONObject(j);
				if (data.length() == 0) 
				{
					data = jObj.getString("mm_msg");
				} 
				else 
				{
					data = data + ".    " + jObj.getString("mm_msg")
							+ ".   ";
				}
				count++;
			}
			data = MBSUtils.lPad(data, 51, " ");
			dynamicmsg.setText(data);
			
			
		}
		catch (JSONException je) 
		{
			je.printStackTrace();
		}
*/
    }

    private void goToSettings() {
        Intent i = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()));
        i.addCategory(Intent.CATEGORY_DEFAULT);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivityForResult(i, REQUEST_APP_SETTINGS);
    }

    public void showAlert(final String str) {
        ErrorDialogClass alert = new ErrorDialogClass(SplashPage.this, "" + str) {
            Intent in = null;

            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btn_ok:

                        if ((str.equalsIgnoreCase(respdesc)) && (respcode.equalsIgnoreCase("0"))) {
                            post_success(retval);
                        } else if ((str.equalsIgnoreCase(respdesc)) && (respcode.equalsIgnoreCase("1"))) {
                            this.dismiss();
                        }
                        //goToSettings();
                        finish();
                        dismiss();
                        break;
                }
                this.dismiss();
            }
        };
        alert.show();
    }

    public void showAlertserver(String str) {

        // Log.e("SAM","===ShowAlert ");
        ErrorDialogClass alert = new ErrorDialogClass(SplashPage.this, "" + str) {
            Intent in = null;

            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btn_ok:
//                        finish();
//                        System.exit(0);
                        restart();
                        dismiss();
                        break;
                }
                this.dismiss();

            }
        };
        alert.show();
    }

    public void restart(){
        Intent i = getBaseContext().getPackageManager().
                getLaunchIntentForPackage(getBaseContext().getPackageName());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        finish();
    }

}

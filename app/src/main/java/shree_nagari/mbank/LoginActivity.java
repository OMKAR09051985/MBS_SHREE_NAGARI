package shree_nagari.mbank;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.telephony.TelephonyManager;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.security.PrivateKey;
import java.util.Random;

import javax.crypto.spec.SecretKeySpec;

import mbLib.CryptoClass;
import mbLib.CryptoUtil;
import mbLib.CustomDialogClass;
import mbLib.DatabaseManagement;
import mbLib.DialogBox;
import mbLib.ListEncryption;
import mbLib.MBSUtils;

//@SuppressLint("NewApi")
public class LoginActivity extends CustomWindow implements OnClickListener, LocationListener {
    Button btnLogin;
    EditText et_custid, et_mpin;
    MainActivity act;
    LoginActivity loginAct = this;
    ImageButton cntus, locus;
    String imeiNo = "", strOTP = "", strRefId = "", retvalvalidate = "", respdescvalidate = "", tmpXMLString = "", retMess = "", newMpin = "", respcode = "", retvalweb = "", respdesc = "", strMobNo = "", retvalotp = "", respdescresend = "";
    private String userid, custname, retvalstr;
    TelephonyManager telephonyManager;
    TextView txt_register, txt_forgot_pass;
    EditText txt_mpin1, txt_mpin2, txt_mpin3, txt_mpin4, txt_mpin5, txt_mpin6, etMpin;
    int cnt = 0, flag = 0, restart_cnt = 0;
    private static String NAMESPACE = "";
    private static String URL = "";
    private static String SOAP_ACTION = "";
    private static String METHOD_NAME = "";
    private static String METHOD_NAME1 = "";
    private static String METHOD_NAMEfetch = "";
    String retVal = "", encrptdMpin = "", userId = "";
    DialogBox dbs;
    CustomDialogClass cdc;
    boolean isWSCalled = false, isWsCallSuccess = false;

    int netFlg, gpsFlg, expdt;
    int timeout = 5;
    String pref = "G";
    String version = "";

    private static final String MY_SESSION = "my_session";
    Editor e;
    public LocationManager locManager;
    public BatteryManager batteryManager;
    ImageView imageViewLogo;
    DatabaseManagement dbms;
    TextView tv_bankname;
    Cursor curSelectBankname;
    public String custid, customerId = "";
    public String mpin;
    public String tranMpin, custId = "";
    private String mobNo;
    boolean custIdFlg = false;
    String splitstr[];
    public String decryptedAccounts, strexpdate;
    PrivateKey var1 = null;
    String var3 = "", var5 = "";
    SecretKeySpec var2 = null;

    //Set Mpin View Controls
    int layOutFlag = 0;
    View mpinChild;
    Button button_1, button_2, button_3, button_4, button_5, button_6,
            button_7, button_8, button_9, button_0, button_erase_pin,
            button_view_pin;
    ImageButton contactus, locateus;
    EditText etCustId, edt_1, edt_2, edt_3, edt_4,
            edt_5, edt_6;
    private SparseArray<String> keyValues = new SparseArray<>();
    LinearLayout layout_login, layout_mpin;
    TextView txt_mpin;
    static int viewCnt = 0;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        var1 = (PrivateKey) getIntent().getSerializableExtra("VAR1");
        var3 = (String) getIntent().getSerializableExtra("VAR3");
        if (var1.toString().length() == 0 || var3.toString().length() == 0) {
            showAlert(getString(R.string.alert_restartapp));
        }
        //Log.e("Shubham", "---------------------in Login Activity loadOldView----------------");
        et_custid = (EditText) findViewById(R.id.etCustId);
        et_mpin = (EditText) findViewById(R.id.etMpin);
        btnLogin = (Button) findViewById(R.id.button1);
        txt_register = (TextView) findViewById(R.id.txt_register);
        txt_forgot_pass = (TextView) findViewById(R.id.txt_forgot_pass);

        txt_mpin = (TextView) findViewById(R.id.txt_mpin);

        layout_login = findViewById(R.id.layout_login);
        layout_mpin = findViewById(R.id.layout_mpin);

        button_1 = (Button) findViewById(R.id.button_1);
        button_2 = (Button) findViewById(R.id.button_2);
        button_3 = (Button) findViewById(R.id.button_3);
        button_4 = (Button) findViewById(R.id.button_4);
        button_5 = (Button) findViewById(R.id.button_5);
        button_6 = (Button) findViewById(R.id.button_6);
        button_7 = (Button) findViewById(R.id.button_7);
        button_8 = (Button) findViewById(R.id.button_8);
        button_9 = (Button) findViewById(R.id.button_9);
        button_0 = (Button) findViewById(R.id.button_0);

        edt_1 = (EditText) findViewById(R.id.edt_1);
        edt_2 = (EditText) findViewById(R.id.edt_2);
        edt_3 = (EditText) findViewById(R.id.edt_3);
        edt_4 = (EditText) findViewById(R.id.edt_4);
        edt_5 = (EditText) findViewById(R.id.edt_5);
        edt_6 = (EditText) findViewById(R.id.edt_6);

        button_erase_pin = (Button) findViewById(R.id.button_erase_pin);
        button_view_pin = (Button) findViewById(R.id.button_view_pin);
        txt_forgot_pass = (TextView) findViewById(R.id.txt_forgot_pass);
        txt_register = (TextView) findViewById(R.id.txt_register);
        locateus = (ImageButton) findViewById(R.id.locateus);
        contactus = (ImageButton) findViewById(R.id.contactus);

        button_1.setOnClickListener(this);
        button_2.setOnClickListener(this);
        button_3.setOnClickListener(this);
        button_4.setOnClickListener(this);
        button_5.setOnClickListener(this);
        button_6.setOnClickListener(this);
        button_7.setOnClickListener(this);
        button_8.setOnClickListener(this);
        button_9.setOnClickListener(this);
        button_0.setOnClickListener(this);

        button_erase_pin.setOnClickListener(this);
        button_view_pin.setOnClickListener(this);
        contactus.setOnClickListener(this);
        locateus.setOnClickListener(this);
        txt_forgot_pass.setOnClickListener(this);
        txt_register.setOnClickListener(this);
        txt_mpin.setOnClickListener(this);
        setKeyValues();

        btnLogin.setOnClickListener(this);
        txt_register.setOnClickListener(this);
        txt_forgot_pass.setOnClickListener(this);


        dbms = new DatabaseManagement("shree_nagari.mbank", "listMobileBanking");
        Cursor cust1 = dbms.selectFromTable("SHAREDPREFERENCE", "", null);//("select * from ", null);
        if (cust1 != null) {
            while (cust1.moveToNext()) {
                customerId = cust1.getString(2);
                strMobNo = cust1.getString(4);
                Log.e("customerId", "......" + customerId);
            }
        }
        imeiNo = MBSUtils.getImeiNumber(LoginActivity.this);
        mobNo = MBSUtils.getMyPhoneNO(LoginActivity.this);//telephonyManager.getLine1Number();
        dbs = new DialogBox(this);
        locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pInfo.versionName;
            Log.e("PackageInfo", "PackageInfo" + version);
        } catch (Exception e) {
            e.printStackTrace();
        }
        et_custid.setText(custId);
        createSharedPrefTable();
    }

    public void setPinValues(String str) {
        if (edt_1.getText().toString().length() == 0) {
            edt_1.setText(str);
            edt_1.setBackgroundResource(R.drawable.mpin_edt);
        } else if (edt_2.getText().toString().length() == 0) {
            edt_2.setText(str);
            edt_2.setBackgroundResource(R.drawable.mpin_edt);
        } else if (edt_3.getText().toString().length() == 0) {
            edt_3.setText(str);
            edt_3.setBackgroundResource(R.drawable.mpin_edt);
        } else if (edt_4.getText().toString().length() == 0) {
            edt_4.setText(str);
            edt_4.setBackgroundResource(R.drawable.mpin_edt);
        } else if (edt_5.getText().toString().length() == 0) {
            edt_5.setText(str);
            edt_5.setBackgroundResource(R.drawable.mpin_edt);
        } else if (edt_6.getText().toString().length() == 0) {
            edt_6.setText(str);
            edt_6.setBackgroundResource(R.drawable.mpin_edt);
            new CallLoginWebService_new().execute();
        }
    }

    public void erasePinValues() {
        if (edt_6.getText().toString().length() != 0) {
            edt_6.setText("");
            edt_6.setBackgroundResource(R.drawable.mpin_border);
        } else if (edt_5.getText().toString().length() != 0) {
            edt_5.setText("");
            edt_5.setBackgroundResource(R.drawable.mpin_border);
        } else if (edt_4.getText().toString().length() != 0) {
            edt_4.setText("");
            edt_4.setBackgroundResource(R.drawable.mpin_border);
        } else if (edt_3.getText().toString().length() != 0) {
            edt_3.setText("");
            edt_3.setBackgroundResource(R.drawable.mpin_border);
        } else if (edt_2.getText().toString().length() != 0) {
            edt_2.setText("");
            edt_2.setBackgroundResource(R.drawable.mpin_border);
        } else if (edt_1.getText().toString().length() != 0) {
            {
                edt_1.setText("");
                edt_1.setBackgroundResource(R.drawable.mpin_border);
            }

            edt_1.setTransformationMethod(PasswordTransformationMethod
                    .getInstance());
            edt_2.setTransformationMethod(PasswordTransformationMethod
                    .getInstance());
            edt_3.setTransformationMethod(PasswordTransformationMethod
                    .getInstance());
            edt_4.setTransformationMethod(PasswordTransformationMethod
                    .getInstance());
            edt_5.setTransformationMethod(PasswordTransformationMethod
                    .getInstance());
            edt_6.setTransformationMethod(PasswordTransformationMethod
                    .getInstance());
        }
    }

    public void loadNewView() {
        setContentView(R.layout.login1);
        Log.e("loadOldView onCreate", "loadOldView");
        btnLogin = (Button) findViewById(R.id.button2);
        et_custid = (EditText) findViewById(R.id.etCustId);
        etMpin = (EditText) findViewById(R.id.etMpin);
        txt_forgot_pass = (TextView) findViewById(R.id.txt_forgot_pass);
        cntus = (ImageButton) findViewById(R.id.contactus);
        locus = (ImageButton) findViewById(R.id.locateus);
        cntus.setOnClickListener(this);
        locus.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        txt_forgot_pass.setOnClickListener(this);
        et_custid.setText(customerId);
    }


    //
    public void setKeyValues() {
        Random random = new Random();
        int rand = 0;
        String str = "";

        for (int i = 0; i < 10; i++) {

            rand = random.nextInt(10);

            if (str.indexOf(rand + "") == -1) {
                str = str + rand;
                if (i == 0) {
                    keyValues.put(R.id.button_1, "" + rand);
                    button_1.setText("" + rand);
                } else if (i == 1) {
                    keyValues.put(R.id.button_2, "" + rand);
                    button_2.setText("" + rand);
                } else if (i == 2) {
                    keyValues.put(R.id.button_3, "" + rand);
                    button_3.setText("" + rand);
                } else if (i == 3) {
                    keyValues.put(R.id.button_4, "" + rand);
                    button_4.setText("" + rand);
                } else if (i == 4) {
                    keyValues.put(R.id.button_5, "" + rand);
                    button_5.setText("" + rand);
                } else if (i == 5) {
                    keyValues.put(R.id.button_6, "" + rand);
                    button_6.setText("" + rand);
                } else if (i == 6) {
                    keyValues.put(R.id.button_7, "" + rand);
                    button_7.setText("" + rand);
                } else if (i == 7) {
                    keyValues.put(R.id.button_8, "" + rand);
                    button_8.setText("" + rand);
                } else if (i == 8) {
                    keyValues.put(R.id.button_9, "" + rand);
                    button_9.setText("" + rand);
                } else if (i == 9) {
                    keyValues.put(R.id.button_0, "" + rand);
                    button_0.setText("" + rand);
                }
            } else {
                i--;
            }
        }
    }


    public void loadOldView() {
        setContentView(R.layout.login);
        Log.e("Shubham", "---------------------in Login Activity loadOldView----------------");
        et_custid = (EditText) findViewById(R.id.etCustId);
        et_mpin = (EditText) findViewById(R.id.etMpin);
        btnLogin = (Button) findViewById(R.id.button1);
        txt_register = (TextView) findViewById(R.id.txt_register);
        txt_forgot_pass = (TextView) findViewById(R.id.txt_forgot_pass);
        cntus = (ImageButton) findViewById(R.id.contactus);
        locus = (ImageButton) findViewById(R.id.locateus);
        cntus.setOnClickListener(this);
        locus.setOnClickListener(this);

        btnLogin.setOnClickListener(this);
        txt_register.setOnClickListener(this);
        txt_forgot_pass.setOnClickListener(this);

    }

    public void createConfigTable() {// createConfigTable
        String sts = "";
        String val[] = {"conf_bankname", "varchar(60)"};
    }// createConfigTable

    public void insertToCONFIG(String bankname) {// insertToCONFIG
        String[] columnNames = {"conf_bankname"};
        String[] columnValues = {bankname};
    }// insertToCONFIG

    private void setBankName() {
        int flag = 0;
        try {
            while (curSelectBankname.moveToNext()) {
                flag = 1;
            }
            curSelectBankname.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("EXCEPTION", "------------------" + e);
        }
        if (flag == 0) {
            //tv_bankname.setText(getString(R.string.lbl_ideal_mobile_banking));
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        Intent in;
        switch (v.getId()) {

            case R.id.txt_mpin:
                if (!txt_mpin.getText().toString().equalsIgnoreCase("Sign In")) {
                    layout_login.setVisibility(View.GONE);
                    layout_mpin.setVisibility(View.VISIBLE);
                    txt_mpin.setText(R.string.signin);
                } else {
                    layout_login.setVisibility(View.VISIBLE);
                    layout_mpin.setVisibility(View.GONE);
                    txt_mpin.setText(R.string.mpin);
                }
                break;

            case R.id.button_view_pin:
                viewCnt++;
                if (viewCnt % 2 == 1) {

                    //android:textColor="#a6c52b"
                    edt_1.setTextColor(Color.parseColor("#000000"));
                    edt_2.setTextColor(Color.parseColor("#000000"));// R.color.white);
                    edt_3.setTextColor(Color.parseColor("#000000"));
                    edt_4.setTextColor(Color.parseColor("#000000"));
                    edt_5.setTextColor(Color.parseColor("#000000"));
                    edt_6.setTextColor(Color.parseColor("#000000"));
                    edt_1.setTransformationMethod(HideReturnsTransformationMethod
                            .getInstance());
                    edt_2.setTransformationMethod(HideReturnsTransformationMethod
                            .getInstance());
                    edt_3.setTransformationMethod(HideReturnsTransformationMethod
                            .getInstance());
                    edt_4.setTransformationMethod(HideReturnsTransformationMethod
                            .getInstance());
                    edt_5.setTransformationMethod(HideReturnsTransformationMethod
                            .getInstance());
                    edt_6.setTransformationMethod(HideReturnsTransformationMethod
                            .getInstance());
                } else {
                    edt_1.setTextColor(Color.parseColor("#a6c52b"));
                    edt_2.setTextColor(Color.parseColor("#a6c52b"));
                    edt_3.setTextColor(Color.parseColor("#a6c52b"));
                    edt_4.setTextColor(Color.parseColor("#a6c52b"));
                    edt_5.setTextColor(Color.parseColor("#a6c52b"));
                    edt_6.setTextColor(Color.parseColor("#a6c52b"));
                    edt_1.setTransformationMethod(PasswordTransformationMethod
                            .getInstance());
                    edt_2.setTransformationMethod(PasswordTransformationMethod
                            .getInstance());
                    edt_3.setTransformationMethod(PasswordTransformationMethod
                            .getInstance());
                    edt_4.setTransformationMethod(PasswordTransformationMethod
                            .getInstance());
                    edt_5.setTransformationMethod(PasswordTransformationMethod
                            .getInstance());
                    edt_6.setTransformationMethod(PasswordTransformationMethod
                            .getInstance());
                }
                break;
            case R.id.button_erase_pin:
                erasePinValues();
                break;

            case R.id.button_1:
                setPinValues(button_1.getText().toString());
                break;
            case R.id.button_2:
                setPinValues(button_2.getText().toString());
                break;
            case R.id.button_3:
                setPinValues(button_3.getText().toString());
                break;
            case R.id.button_4:
                setPinValues(button_4.getText().toString());
                break;
            case R.id.button_5:
                setPinValues(button_5.getText().toString());
                break;
            case R.id.button_6:
                setPinValues(button_6.getText().toString());
                break;
            case R.id.button_7:
                setPinValues(button_7.getText().toString());
                break;
            case R.id.button_8:
                setPinValues(button_8.getText().toString());
                break;
            case R.id.button_9:
                setPinValues(button_9.getText().toString());
                break;
            case R.id.button_0:
                setPinValues(button_0.getText().toString());
                break;

            case R.id.txt_register:
                in = new Intent(this, Register.class);
                in.putExtra("VAR1", var1);
                in.putExtra("VAR3", var3);
                startActivity(in);
                finish();
                break;
            case R.id.txt_forgot_pass:
                in = new Intent(loginAct, ForgotPassword.class);
                Bundle b = new Bundle();
                b.putString("FROMACT", "FORGOT");
                in.putExtras(b);
                in.putExtra("VAR1", var1);
                in.putExtra("VAR3", var3);
                loginAct.startActivity(in);
                loginAct.finish();
                break;
            case R.id.button1:

                String strCustId = et_custid.getText().toString().trim();
                boolean isNumeric;
                try {
                    long t = Long.parseLong(strCustId);
                    isNumeric = true;
                } catch (Exception e) {
                    // TODO: handle exception
                    isNumeric = false;
                }
                String strMpin = et_mpin.getText().toString().trim();

			/*if(strCustId.length()!=10 && isNumeric)
			{
				retMess = getString(R.string.login_alert_010);
				setAlert();
			}*/
                if (strCustId.length() == 0) {
                    retMess = getString(R.string.login_alert_010);
                    showAlert1(retMess);
                    //setAlert();
                } else if (strMpin.length() == 0) {
                    retMess = getString(R.string.login_alert_011);
                    showAlert1(retMess);
                    //setAlert();
                } else {
                    newMpin = strMpin;
                    Log.e("onClick button2", "newMpin = " + strMpin);
                    Log.i("MBS", "login btn clicked...");
                    flag = chkConnectivity();
                    if (flag == 0) {
                        //new CallLoginWebService().execute();
                        new CallWebService().execute();
                    }
                }
                break;
            case R.id.button2:
                mpin = etMpin.getText().toString();//mpin.toString().trim();
                strMpin = mpin;
                Log.e("onClick button2", "Mpin = " + strMpin);
                //	et_mpin.setText(strMpin);
                newMpin = strMpin;
                Log.e("onClick button2", "newMpin = " + strMpin);
                if (strMpin.length() != 6) {
                    retMess = getString(R.string.login_alert_011);
                    showAlert1(retMess);
                    //setAlert();
                } else {
                    Log.i("MBS", "login btn clicked...");
                    flag = chkConnectivity();
                    if (flag == 0) {
                        //new CallLoginWebService().execute();
                        new CallWebService().execute();
                    }
                }
                break;
            case R.id.contactus:
                in = new Intent(loginAct, ContactUs.class);
                in.putExtra("VAR1", var1);
                in.putExtra("VAR3", var3);
                startActivity(in);
                loginAct.finish();
                break;

            case R.id.locateus:
                in = new Intent(loginAct, LocateUs.class);
                in.putExtra("VAR1", var1);
                in.putExtra("VAR3", var3);
                startActivity(in);
                loginAct.finish();
                break;
            default:
                break;
        }
    }

    @Override
    public void onDestroy() {
        // app.app_onDestroy();
        super.onDestroy();
        System.out.println("Mobile bank login - in onDestroy()");
    }

    public void onBackPressed() {
        CustomDialogClass alert = new CustomDialogClass(LoginActivity.this, getString(R.string.lbl_do_you_want_to_exit)) {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btn_ok:
                        finish();
                        System.exit(0);
                        break;

                    case R.id.btn_cancel:
                        this.dismiss();
                        break;
                    default:
                        break;
                }
                dismiss();
            }
        };
        alert.show();
		/*DialogBox dbs = new DialogBox(this);
		dbs.get_adb().setMessage(getString(R.string.lbl_do_you_want_to_exit));
		dbs.get_adb().setPositiveButton("Yes",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
						//startActivity(lang_activity);
						finish();
						System.exit(0);
					}
				});
		dbs.get_adb().setNegativeButton("No",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
						arg0.cancel();
					}
				});
		dbs.get_adb().show();*/
    }

    public int chkConnectivity() {
        Log.i("1111", "1111");
        // p_wait.setVisibility(ProgressBar.VISIBLE);
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        Log.i("2222", "2222");
        try {
            State state = ni.getState();
            Log.i("3333", "3333");
            boolean state1 = ni.isAvailable();
            Log.i("4444", "4444");
            System.out.println("state1 ---------" + state1);
            if (state1) {
                switch (state) {
                    case CONNECTED:

                        Log.i("5555", "5555");
                        if (ni.getType() == ConnectivityManager.TYPE_MOBILE
                                || ni.getType() == ConnectivityManager.TYPE_WIFI) {

                            gpsFlg = 1;
                            flag = 0;

                        }
                        break;
                    case DISCONNECTED:
                        Log.i("6666", "6666");
                        flag = 1;
                        // retMess = "Network Disconnected. Please Try Again.";
                        retMess = getString(R.string.alert_000);
                        dbs = new DialogBox(this);
                        dbs.get_adb().setMessage(retMess);
                        dbs.get_adb().setPositiveButton("Ok",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface arg0,
                                                        int arg1) {
                                        arg0.cancel();
                                    }
                                });
                        dbs.get_adb().show();

                        break;
                    default:
                        Log.i("7777", "7777");
                        flag = 1;
                        // retMess = "Network Unavailable. Please Try Again.";
                        retMess = getString(R.string.alert_000);
                        // setAlert();

                        dbs = new DialogBox(this);
                        dbs.get_adb().setMessage(retMess);
                        dbs.get_adb().setPositiveButton("Ok",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface arg0,
                                                        int arg1) {
                                        arg0.cancel();
                                        Intent in = null;
                                        in = new Intent(getApplicationContext(), LoginActivity.class);
                                        startActivity(in);
                                        finish();
                                    }
                                });
                        dbs.get_adb().show();

                        break;
                }
            } else {
                Log.i("8888", "8888");
                flag = 1;
                // retMess = "Network Unavailable. Please Try Again.";
                retMess = getString(R.string.alert_000);
                // setAlert();

                dbs = new DialogBox(this);
                dbs.get_adb().setMessage(retMess);
                dbs.get_adb().setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                                arg0.cancel();
                                Intent in = null;
                                in = new Intent(getApplicationContext(),
                                        LoginActivity.class);
                                startActivity(in);
                                finish();
                            }
                        });
                dbs.get_adb().show();

            }
        } catch (NullPointerException ne) {

            Log.e("EXCEPTION", "------------------" + ne);
            Log.i("mayuri", "NullPointerException Exception" + ne);
            flag = 1;
            // retMess = "Network Unavailable. Please Try Again.";
            retMess = getString(R.string.alert_000);
            // setAlert();

            dbs = new DialogBox(this);
            dbs.get_adb().setMessage(retMess);
            dbs.get_adb().setPositiveButton("Ok",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                            arg0.cancel();
                            Intent in = null;
                            in = new Intent(getApplicationContext(),
                                    LoginActivity.class);
                            startActivity(in);
                            finish();
                        }
                    });
            dbs.get_adb().show();


        } catch (Exception e) {
            Log.e("EXCEPTION", "------------------" + e);
            Log.i("mayuri", "Exception" + e);
            flag = 1;
            // retMess = "Network Unavailable. Please Try Again.";
            retMess = getString(R.string.alert_000);
            // setAlert();

            dbs = new DialogBox(this);
            dbs.get_adb().setMessage(retMess);
            dbs.get_adb().setPositiveButton("Ok",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                            arg0.cancel();
                            Intent in = null;
                            in = new Intent(getApplicationContext(),
                                    LoginActivity.class);
                            startActivity(in);
                            finish();
                        }
                    });
            dbs.get_adb().show();

        }
        return flag;
    }

    class CallLoginWebService_old extends AsyncTask<Void, Void, Void> {


        String[] xmlTags = {"PARAMS", "CHECKSUM"};// "CUSTID", "MPIN", "IMEINO" };
        String[] valuesToEncrypt = new String[2];
        // LoadProgressBar loadProBarObj=new LoadProgressBar(this);
        LoadProgressBar loadProBarObj = new LoadProgressBar(LoginActivity.this);
        String generatedXML = "";
        boolean isWSCalled = false, isWsCallSuccess = false;
        String ValidationData = "";
        /*
         * Key key_CustID,keyForMAC; String
         * encodedCustIDStr,encodedCustKeyStr,encodedCustMAC,encodedCustMACKey;
         */

        @Override
        protected void onPreExecute() {
            loadProBarObj.show();
            respcode = "";
            retvalweb = "";
            respdesc = "";
            // p_wait.setVisibility(ProgressBar.VISIBLE);

            custid = et_custid.getText().toString().trim();
            //	mpin = et_mpin.getText().toString().trim();
            mpin = newMpin;
            Log.e("IN onPreExecute()", "custid :" + custid);
            Log.e("IN onPreExecute()", "mpin :" + mpin);
            Log.e("IN onPreExecute()", "imeiNo :" + imeiNo);

            encrptdMpin = ListEncryption.encryptData(custid + mpin);
            //custid=custid;
            JSONObject obj = new JSONObject();
            try {

                String location = MBSUtils.getLocation(LoginActivity.this);

                obj.put("CUSTID", custid + "~#~" + version);
                obj.put("MPIN", encrptdMpin);
                obj.put("IMEINO", imeiNo + "~" + mobNo);
                obj.put("SIMNO", MBSUtils.getSimNumber(LoginActivity.this));
                //obj.put("VALIDATIONDATA", ValidationData);
                obj.put("MOBILENO", MBSUtils.getMyPhoneNO(LoginActivity.this));
                obj.put("IPADDRESS", MBSUtils.getLocalIpAddress());
                obj.put("OSVERSION", Build.VERSION.RELEASE);
                obj.put("LATITUDE", location.split("~")[0]);
                obj.put("LONGITUDE", location.split("~")[1]);
                obj.put("REQSTATUS", "R");
                obj.put("REQFROM", "MBS");
                obj.put("METHODCODE", "1");
                ValidationData = MBSUtils.getValidationData(LoginActivity.this, obj.toString());
                //Log.e("ValidationData-- ",ValidationData);

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            valuesToEncrypt[0] = obj.toString();
            valuesToEncrypt[1] = ValidationData;
		/*	valuesToEncrypt[0] = custid+"~#~"+version;
			valuesToEncrypt[1] = encrptdMpin;
			valuesToEncrypt[2] = imeiNo+"~"+mobNo;*/

            generatedXML = CryptoUtil.generateXML(xmlTags, valuesToEncrypt);

            Log.i("IN onPreExecute()", "generatedXML :" + generatedXML);

        }

        ;

        @Override
        protected Void doInBackground(Void... arg0) {
            NAMESPACE = getString(R.string.namespace);
            URL = getString(R.string.url);
            SOAP_ACTION = getString(R.string.soap_action);
            // p_wait.setVisibility(ProgressBar.VISIBLE);
            METHOD_NAME = "mbsInterCall";//"loginWS";
            int i = 0;
            Log.i("mayuri", "in doInBackground()");
            try {

                SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

                request.addProperty("para_value", generatedXML);

                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                        SoapEnvelope.VER11);
                envelope.setOutputSoapObject(request);
                HttpTransportSE androidHttpTransport = new HttpTransportSE(URL,
                        15000);
                String status = "";
                try {
                    androidHttpTransport.call(SOAP_ACTION, envelope);
                    System.out.println("here===" + envelope.bodyIn.toString());
                    status = envelope.bodyIn.toString().trim();
                    retVal = status;
                    int pos = envelope.bodyIn.toString().trim().indexOf("=");
                    if (pos > -1) {
                        status = status.substring(pos + 1, status.length() - 3);
                        retVal = status;
                        isWSCalled = true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    retMess = getString(R.string.alert_000);
                    System.out.println("Exception");
                    cnt = 0;
                    // return "FAILED";
                }
            } catch (Exception e) {
                retMess = getString(R.string.alert_000);
                System.out.println(e.getMessage());
                cnt = 0;
            }
            return null;
        }

        @Override
        protected void onPostExecute(final Void result) {
            loadProBarObj.dismiss();
            if (isWSCalled) {
                String[] xml_data = CryptoUtil.readXML(retVal, new String[]{"PARAMS", "CHECKSUM"});
                ValidationData = xml_data[1].trim();
                JSONObject jsonObj;
                try {
                    jsonObj = new JSONObject(xml_data[0]);

                    if (ValidationData.equals(MBSUtils.getValidationData(LoginActivity.this, xml_data[0].trim())))//if(jsonObj.getString("VALIDATIONDATA").equalsIgnoreCase(ValidationData))
                    {
                        if (jsonObj.has("RESPCODE")) {
                            respcode = jsonObj.getString("RESPCODE");
                        } else {
                            respcode = "-1";
                        }
                        if (jsonObj.has("RETVAL")) {
                            retvalweb = jsonObj.getString("RETVAL");
                        } else {
                            retvalweb = "";
                        }
                        if (jsonObj.has("RESPDESC")) {
                            respdesc = jsonObj.getString("RESPDESC");
                        } else {
                            respdesc = "";
                        }
                        if (respdesc.length() > 0) {
                            showAlert(respdesc);
                        } else {
                            if (retvalweb.indexOf("SUCCESS") > -1) {
                                post_success(retvalweb);
                            } else {
                                System.out.println("in else ***************************************==" + retvalweb);
                                if (retvalweb.indexOf("~") == -1) {
                                    retMess = getString(R.string.alert_network_problem_pease_try_again);
                                } else {
                                    String msg[] = retvalweb.split("~");

                                    Log.e("Check 1", msg[0]);
                                    Log.e("Check 2", msg[1]);

                                    if (msg[1].equals("1")) {
                                        retMess = getString(R.string.login_alert_009);
                                    } else if (msg[1].equals("2")) {
                                        retMess = getString(R.string.login_alert_002);
                                    } else if (msg[1].equals("3"))
                                        retMess = getString(R.string.login_alert_003);
                                    else if (msg[1].equals("4"))
                                        retMess = getString(R.string.login_alert_004);
                                    else if (msg[1].equals("5"))
                                        retMess = getString(R.string.login_alert_005);
                                    else if (msg[1].equals("6"))
                                        retMess = getString(R.string.login_alert_006);
                                    else if (msg[1].equals("7")) {
                                        Log.e("alskdaksjdlaksjd", "1231892731782");
                                        retMess = getString(R.string.login_alert_007);
                                    } else if (msg[1].equals("8")) {
                                        retMess = getString(R.string.login_alert_008);
                                    } else if (msg[1].equals("9"))
                                        retMess = getString(R.string.alert_login_fail);
                                    else if (msg[1].equals("10"))
                                        retMess = getString(R.string.login_alert_diffimei);
						/*else if(msg[1].indexOf("OLDVERSION")>-1)
							retMess = getString(R.string.alert_oldversion);*/
                                }
                                showAlert1(retMess);
                                //setAlert();
                            }
                        }
                    } else {

                        MBSUtils.showInvalidResponseAlert(LoginActivity.this);
                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else {
                retMess = getString(R.string.alert_000);
                showAlert1(retMess);
                //setAlert();
            }
        }

    }

    class CallLoginWebService_new extends AsyncTask<Void, Void, Void> {
        LoadProgressBar loadProBarObj = new LoadProgressBar(LoginActivity.this);
        boolean isWSCalled = false, isWsCallSuccess = false;
        String ValidationData = "";
        JSONObject obj = new JSONObject();

        @Override
        protected void onPreExecute() {
            loadProBarObj.show();
            respcode = "";
            retvalweb = "";
            respdesc = "";
            if (layOutFlag == 1) {
                custid = etCustId.getText().toString().trim();
                mpin = newMpin;
            } else {
                custid = customerId;
                mpin = edt_1.getText().toString() + edt_2.getText().toString() + edt_3.getText().toString() + edt_4.getText().toString() + edt_5.getText().toString() + edt_6.getText().toString();
            }
            encrptdMpin = ListEncryption.encryptData(custid + mpin);

            try {

                String location = MBSUtils.getLocation(LoginActivity.this);
                obj.put("CUSTID", custid + "~#~" + version);
                obj.put("LOGINAS", "MPIN");
                obj.put("MPIN", mpin);
                obj.put("IMEINO", imeiNo);// + "~" + mobNo
                obj.put("SIMNO", MBSUtils.getSimNumber(LoginActivity.this));
                obj.put("MOBILENO", MBSUtils.getMyPhoneNO(LoginActivity.this));
                obj.put("IPADDRESS", MBSUtils.getLocalIpAddress());
                obj.put("OSVERSION", Build.VERSION.RELEASE);
                obj.put("LATITUDE", location.split("~")[0]);
                obj.put("LONGITUDE", location.split("~")[1]);
                obj.put("REQSTATUS", "R");
                obj.put("REQFROM", "MBS");
                obj.put("METHODCODE", "1");

                Log.e("SessionTimeout===", "obj===" + obj);
                //ValidationData = MBSUtils.getValidationData(SBKLoginActivity.this, obj.toString());
            } catch (JSONException e) {
                e.printStackTrace();
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

                request.addProperty("value1", CryptoClass.Function5(obj.toString(), var2));
                request.addProperty("value2", CryptoClass.Function3(keyStr, var1));
                request.addProperty("value3", var3);
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.setOutputSoapObject(request);
                HttpTransportSE androidHttpTransport = new HttpTransportSE(value6, 15000);
                String status = "";
                try {
                    androidHttpTransport.call(value5, envelope);
                    status = envelope.bodyIn.toString().trim();
                    var5 = status;
                    Log.e("login===", "var5===" + var5);
                    int pos = envelope.bodyIn.toString().trim().indexOf("=");
                    if (pos > -1) {
                        status = status.substring(pos + 1, status.length() - 3);
                        var5 = status;
                        isWSCalled = true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    retMess = getString(R.string.alert_000);
                    cnt = 0;
                }
            } catch (Exception e) {
                retMess = getString(R.string.alert_000);
                System.out.println(e.getMessage());
                cnt = 0;
            }
            return null;
        }

        @Override
        protected void onPostExecute(final Void result) {
            loadProBarObj.dismiss();
            if (isWSCalled) {

                JSONObject jsonObj;
                try {
                    String str = CryptoClass.Function6(var5, var2);
                    jsonObj = new JSONObject(str.trim());
                    Log.e("login===", "str===" + str);
					/*ValidationData = xml_data[1].trim();
					if (ValidationData.equals(MBSUtils.getValidationData(
							SBKLoginActivity.this, xml_data[0].trim())))// if(jsonObj.getString("VALIDATIONDATA").equalsIgnoreCase(ValidationData))
					{*/
                    if (jsonObj.has("RESPCODE")) {
                        respcode = jsonObj.getString("RESPCODE");
                    } else {
                        respcode = "-1";
                    }
                    if (jsonObj.has("RETVAL")) {
                        retvalweb = jsonObj.getString("RETVAL");
                    } else {
                        retvalweb = "";
                    }
                    if (jsonObj.has("RESPDESC")) {
                        respdesc = jsonObj.getString("RESPDESC");
                    } else {
                        respdesc = "";
                    }
                    if (respdesc.length() > 0) {
                        showAlert(respdesc);
                    } else {
                        if (retvalweb.indexOf("SUCCESS") > -1) {
                            post_success(retvalweb);
                        } else {
                            if (retvalweb.indexOf("~") == -1) {
                                retMess = getString(R.string.alert_network_problem_pease_try_again);
                            } else {
                                String msg[] = retvalweb.split("~");
                                if (msg[1].equals("1")) {
                                    retMess = getString(R.string.login_alert_009);
                                } else if (msg[1].equals("2")) {
                                    retMess = getString(R.string.login_alert_002);
                                } else if (msg[1].equals("3"))
                                    retMess = getString(R.string.login_alert_003);
                                else if (msg[1].equals("4"))
                                    retMess = getString(R.string.alert_login_fail);
                                else if (msg[1].equals("5"))
                                    retMess = getString(R.string.login_alert_005);
                                else if (msg[1].equals("6"))
                                    retMess = getString(R.string.login_alert_006);
                                else if (msg[1].equals("7")) {
                                    retMess = getString(R.string.login_alert_007);
                                } else if (msg[1].equals("8")) {
                                    retMess = getString(R.string.login_alert_008);
                                } else if (msg[1].equals("9"))
                                    retMess = getString(R.string.alert_login_fail);
                                else if (msg[1].equals("10"))
                                    retMess = getString(R.string.login_alert_diffimei);
									/*else if (msg[1].indexOf("OLDVERSION") > -1)
										retMess = getString(R.string.alert_oldversion);*/
                            }
                            showAlert1(retMess);
                            //setAlert();
                        }
                    }
					/*} else {

						MBSUtils.showInvalidResponseAlert(SBKLoginActivity.this);
					}*/

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else {
                retMess = getString(R.string.alert_000);
                showAlert1(retMess);
                //setAlert();
            }
        }

    }

    class CallWebService extends AsyncTask<Void, Void, Void> {

        LoadProgressBar loadProBarObj = new LoadProgressBar(LoginActivity.this);

        boolean isWSCalled = false, isWsCallSuccess = false;
        JSONObject obj = new JSONObject();

        @Override
        protected void onPreExecute() {

            loadProBarObj.show();
            custid = et_custid.getText().toString().trim();
            mpin = newMpin;
            encrptdMpin = mpin;//ListEncryption.encryptData(custid+mpin);

            try {
                String location = MBSUtils.getLocation(LoginActivity.this);
                obj.put("CUSTID", custid + "~#~" + version);
                obj.put("MPIN", encrptdMpin);
                obj.put("IMEINO", imeiNo);// + "~" + mobNo
                obj.put("SIMNO", MBSUtils.getSimNumber(LoginActivity.this));
                obj.put("MOBILENO", MBSUtils.getMyPhoneNO(LoginActivity.this));
                obj.put("IPADDRESS", MBSUtils.getLocalIpAddress());
                obj.put("OSVERSION", Build.VERSION.RELEASE);
                obj.put("LATITUDE", location.split("~")[0]);
                obj.put("LONGITUDE", location.split("~")[1]);
                obj.put("REQSTATUS", "R");
                obj.put("REQFROM", "MBS");
                obj.put("METHODCODE", "1");
                Log.e("===========", "CallWebService_request" + obj.toString());
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
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
                request.addProperty("value1", CryptoClass.Function5(obj.toString(), var2));
                request.addProperty("value2", CryptoClass.Function3(keyStr, var1));
                request.addProperty("value3", var3);
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.setOutputSoapObject(request);
                HttpTransportSE androidHttpTransport = new HttpTransportSE(value6, 45000);
                androidHttpTransport.call(value5, envelope);
                var5 = envelope.bodyIn.toString().trim();
                var5 = var5.substring(var5.indexOf("=") + 1, var5.length() - 3);
                isWSCalled = true;
            }// end try
            catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(final Void result) {
            loadProBarObj.dismiss();
            if (isWSCalled) {

                JSONObject jsonObj;
                try {
                    String str = CryptoClass.Function6(var5, var2);
                    Log.e("login=", "after====" + str);
                    jsonObj = new JSONObject(str.trim());
                    Log.e("===========", "CallWebService_responce" + jsonObj.toString());
                    if (jsonObj.has("RESPCODE")) {
                        respcode = jsonObj.getString("RESPCODE");
                    } else {
                        respcode = "-1";
                    }
                    if (jsonObj.has("RETVAL")) {
                        retvalweb = jsonObj.getString("RETVAL");
                    } else {
                        retvalweb = "";
                    }
                    if (jsonObj.has("RESPDESC")) {
                        respdesc = jsonObj.getString("RESPDESC");
                    } else {
                        respdesc = "";
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                Log.e("retval", "respdesc---------1-" + respdesc);
                if (respdesc.length() > 0) {
                    showAlert(respdesc);
                } else {
                    Log.e("retval", "retval---------1-" + retvalweb);
                    if (retvalweb.indexOf("SUCCESS") > -1) {
                        post_success(retvalweb);
                    } else {
                        System.out.println("in else ***************************************");
                        if (retvalweb.indexOf("~") == -1) {
                            retMess = getString(R.string.alert_network_problem_pease_try_again);
                        } else {
                            String msg[] = retvalweb.split("~");

                            Log.e("Check 1", "msg[0]====" + msg[0]);
                            Log.e("Check 2", "msg[1]====" + msg[1]);

                            if (msg[1].equals("1")) {
                                retMess = getString(R.string.login_alert_009);
                            } else if (msg[1].equals("2")) {
                                retMess = getString(R.string.login_alert_002);
                                custId = msg[3];
                                userId = msg[2];
                            } else if (msg[1].equals("3"))
                                retMess = getString(R.string.login_alert_003);
                            else if (msg[1].equals("4"))
                                retMess = getString(R.string.alert_login_fail);
                            else if (msg[1].equals("5"))
                                retMess = getString(R.string.login_alert_005);
                            else if (msg[1].equals("6"))
                                retMess = getString(R.string.login_alert_006);
                            else if (msg[1].equals("7")) {
                                Log.e("alskdaksjdlaksjd", "1231892731782");
                                retMess = getString(R.string.login_alert_007);
                            } else if (msg[1].equals("8")) {
                                retMess = getString(R.string.login_alert_008);
                            } else if (msg[1].equals("9"))
                                retMess = getString(R.string.alert_login_fail);

							/*else if(msg[1].indexOf("OLDVERSION")>-1)
								retMess = getString(R.string.alert_oldversionupdate);	*/
                        }
                        showAlert1(retMess);
                    }
                }
            } else {
                retMess = getString(R.string.alert_000);
                showAlert1(retMess);
            }
        }

    }

    public void showlogoutAlert(final String str) {
        CustomDialogClass alert = new CustomDialogClass(LoginActivity.this, str) {

            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
            }

            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btn_ok:
                        if ((str.equalsIgnoreCase(getString(R.string.alert_mpinexp) + " " + expdt + " days. Do You Want To Change ?"))) {
                            Log.e("Login", "onCreate ==userId=  " + userId);
                            Log.e("Login", "onCreate ==custid=  " + custid);
                            Intent in = new Intent(loginAct, SetMPIN.class);
                            Bundle b = new Bundle();
                            b.putString("FROMACT", "FORGOT");
                            b.putString("USERNAME", userId);
                            b.putString("CUSTID", custid);

                            in.putExtras(b);
                            loginAct.startActivity(in);
                            loginAct.finish();
                        }
							/*else if(str.equalsIgnoreCase(getString(R.string.alert_oldversionupdate)))
							{
							 try {
				                    Intent viewIntent =new Intent("android.intent.action.VIEW",
				                    Uri.parse("https://play.google.com/store/apps/details?id=shree_nagari.mbank"));
				                    startActivity(viewIntent);
				        }catch(Exception e) {
				            Toast.makeText(getApplicationContext(),"Unable to Connect Try Again...",
				                    Toast.LENGTH_LONG).show();
				            e.printStackTrace();
						}
							//this.dismiss();
							}*/
                        break;

                    case R.id.btn_cancel:

                        if (expdt == 1) {
                            retMess = getString(R.string.alert_mpinexp) + " " + expdt + " day. Please Change MPIN";
                            showAlert1(retMess);
                            //setAlert();
                        } else if (expdt <= 7 && expdt >= 2) {
                            showlogoutAlert1(getString(R.string.alert_mpinexp) + " " + expdt + " days. Do You Want To Change ?");
                            //setAlert();
                        } else {

                            Log.e("no pressed", "1111111");
                            if (!decryptedAccounts.equals("FAILED#")) {
                                Bundle b = new Bundle();
                                String accounts = splitstr[0];
                                String mobno = splitstr[1];
                                tranMpin = splitstr[2];
                                custid = splitstr[3];
                                userId = splitstr[4];
                                Log.e("Sharayu--", "==Mob no==" + mobno);
                                Log.e("Sharayu--", "==accounts==" + accounts);
                                //Log.e("Sharayu--","==tranMpin=="+tranMpin);
                                Log.e("Sharayu--", "==custid==" + custid);
                                Log.e("Sharayu--", "==userId==" + userId);

                                System.out.println("mobno :" + mobno);

                                String[] columnNames = {"retval_str", "cust_name", "cust_id", "user_id", "cust_mobno"};
                                String[] columnValues = {accounts, "", custid, userId, mobno};

                                dbms.deleteFromTable("SHAREDPREFERENCE", "", null);
                                dbms.insertIntoTable("SHAREDPREFERENCE", 5, columnNames, columnValues);

                                if (!custIdFlg) {
                                    String str = "";
                                    String[] coulmnsAndTypes = {"CFG_CUST_ID", "varchar(10)"};
                                    String[] colNms = {"CFG_CUST_ID"};
                                    String[] val = new String[1];
                                    val[0] = custid;
                                    try {
                                        str = dbms.createTable("CONFIG", coulmnsAndTypes);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    Log.e("SETMPIN", "str after create table===" + str);
                                    int recCnt = 0;
                                    try {

                                        Cursor c1 = dbms.executePersonalQuery("select count(*) from CONFIG", null);
                                        if (c1.moveToNext()) {
                                            recCnt = c1.getInt(0);
                                            Log.e("Login ", "recCnt" + recCnt);
                                        }
                                        c1.close();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    try {

                                        if (recCnt == 0)
                                            str = dbms.insertIntoTable("CONFIG", 1, colNms, val);

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    Log.e("SETMPIN", "str after insert===" + str);
                                } else {
                                    String values[] = {et_custid.getText().toString()};
                                    String[] colNms = {"CFG_CUST_ID"};
                                    dbms.updateTable("CONFIG", colNms, null, values);
                                }


                                //intent = new Intent(loginAct, MainActivity.class);
                                //Intent intent = new Intent(loginAct, DashboardDesignActivity.class);
                                Intent intent = new Intent(loginAct, NewDashboard.class);
                                intent.putExtra("VAR1", var1);
                                intent.putExtra("VAR3", var3);
                                intent.putExtras(b);
                                startActivity(intent);
                                finish();
                            }
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

    public void showlogoutAlert1(final String str) {
        CustomDialogClass alert = new CustomDialogClass(LoginActivity.this, str) {

            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
            }

            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btn_ok:
                        if ((str.equalsIgnoreCase(getString(R.string.alert_mpinexp) + " " + expdt + " days. Do You Want To Change ?"))) {
                            Log.e("Login", "onCreate ==userId=  " + userId);
                            Log.e("Login", "onCreate ==custid=  " + custid);
                            Intent in = new Intent(loginAct, SetMPIN.class);
                            Bundle b = new Bundle();
                            b.putString("FROMACT", "FORGOT");
                            b.putString("USERNAME", userId);
                            b.putString("CUSTID", custid);

                            in.putExtras(b);
                            loginAct.startActivity(in);
                            loginAct.finish();
                        }

                        break;

                    case R.id.btn_cancel:


                        Log.e("no pressed", "1111111");
                        if (!decryptedAccounts.equals("FAILED#")) {
                            Bundle b = new Bundle();
                            String accounts = splitstr[0];
                            String mobno = splitstr[1];
                            tranMpin = splitstr[2];
                            custid = splitstr[3];
                            userId = splitstr[4];
                            Log.e("Sharayu--", "==Mob no==" + mobno);
                            Log.e("Sharayu--", "==accounts==" + accounts);
                            //Log.e("Sharayu--","==tranMpin=="+tranMpin);
                            Log.e("Sharayu--", "==custid==" + custid);
                            Log.e("Sharayu--", "==userId==" + userId);

                            System.out.println("mobno :" + mobno);

                            String[] columnNames = {"retval_str", "cust_name", "cust_id", "user_id", "cust_mobno"};
                            String[] columnValues = {accounts, "", custid, userId, mobno};

                            dbms.deleteFromTable("SHAREDPREFERENCE", "", null);
                            dbms.insertIntoTable("SHAREDPREFERENCE", 5, columnNames, columnValues);



					/*		e.putString("accounts", accounts);
							e.putString("retValStr", accounts);
							e.putString("custId", custid);
							e.putString("pin", encrptdMpin);
							e.putString("tranPin", tranMpin);
							e.putString("cust_mob_no", mobno);
							e.putString("userId",userId);
							e.commit();

							Log.e("LOGIN","accounts=="+accounts);
							Log.e("LOGIN","custid=="+custid);
							Log.e("LOGIN","encrptdMpin=="+encrptdMpin);
							Log.e("LOGIN","tranMpin=="+tranMpin);
							Log.e("LOGIN","mobno=="+mobno);
							Log.e("LOGIN","userId=="+userId);
							Log.e("LOGIN","custIdFlg=="+custIdFlg);*/

                            if (!custIdFlg) {
                                String str = "";
                                String[] coulmnsAndTypes = {"CFG_CUST_ID", "varchar(10)"};
                                String[] colNms = {"CFG_CUST_ID"};
                                String[] val = new String[1];
                                val[0] = custid;
                                try {
                                    str = dbms.createTable("CONFIG", coulmnsAndTypes);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                Log.e("SETMPIN", "str after create table===" + str);
                                int recCnt = 0;
                                try {

                                    Cursor c1 = dbms.executePersonalQuery("select count(*) from CONFIG", null);
                                    if (c1.moveToNext()) {
                                        recCnt = c1.getInt(0);
                                        Log.e("Login ", "recCnt" + recCnt);
                                    }
                                    c1.close();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                try {

                                    if (recCnt == 0)
                                        str = dbms.insertIntoTable("CONFIG", 1, colNms, val);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                Log.e("SETMPIN", "str after insert===" + str);
                            } else {
                                String values[] = {et_custid.getText().toString()};
                                String[] colNms = {"CFG_CUST_ID"};
                                dbms.updateTable("CONFIG", colNms, null, values);
                            }


                            //intent = new Intent(loginAct, MainActivity.class);
                            Intent intent = new Intent(loginAct, NewDashboard.class);
                            intent.putExtra("VAR1", var1);
                            intent.putExtra("VAR3", var3);
                            intent.putExtras(b);
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

    public void showAlert1(final String str) {
        ErrorDialogClass alert = new ErrorDialogClass(LoginActivity.this, "" + str) {
            Intent in = null;

            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btn_ok:
                        if (retMess == getString(R.string.login_alert_007)) {
                            Intent in = new Intent(loginAct, ChangeMobileNo.class);
                            Bundle b = new Bundle();
                            b.putString("custId", custid);
                            in.putExtras(b);
                            loginAct.startActivity(in);
                            loginAct.finish();
                        } else if (retMess == getString(R.string.login_alert_002)) {
                            Intent in = new Intent(loginAct, ForgotPassword.class);
                            Bundle b = new Bundle();
                            b.putString("FROMACT", "LOGIN");
                            in.putExtras(b);
                            loginAct.startActivity(in);
                            loginAct.finish();
                        } else if ((retMess.equalsIgnoreCase(getString(R.string.alert_mpinexp) + " " + expdt + " day. Please Change MPIN"))) {
                            Log.e("Login", "onCreate ==userId=  " + userId);
                            Log.e("Login", "onCreate ==custid=  " + custid);
                            Intent in = new Intent(loginAct, SetMPIN.class);
                            Bundle b = new Bundle();
                            b.putString("FROMACT", "FORGOT");
                            b.putString("USERNAME", userId);
                            b.putString("CUSTID", custid);

                            in.putExtras(b);
                            loginAct.startActivity(in);
                            loginAct.finish();
                        }

                        break;
                }
                this.dismiss();

            }
        };
        alert.show();

    }
	/*public void setAlert() {
		dbs = new DialogBox(this);
		dbs.get_adb().setMessage(retMess);
		dbs.get_adb().setPositiveButton("OK",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						arg0.cancel();

						*//*if(retMess == getString(R.string.login_alert_009))
						{
							Intent in = new Intent(loginAct,ValidateSecQueActivity.class);
							Bundle b = new Bundle();
							b.putString("custId", loginAct.custid);
							b.putString("mpin", loginAct.mpin);
							in.putExtras(b);
							loginAct.startActivity(in);
							loginAct.finish();
						}
						else *//*if(retMess == getString(R.string.login_alert_007))
						{
							Intent in = new Intent(loginAct,ChangeMobileNo.class);
							Bundle b = new Bundle();
							b.putString("custId", custid);
							in.putExtras(b);
							loginAct.startActivity(in);
							loginAct.finish();
						}
						else if(retMess == getString(R.string.login_alert_002))
						{
							Intent in = new Intent(loginAct,ForgotPassword.class);
							Bundle b = new Bundle();
							b.putString("FROMACT", "LOGIN");
							in.putExtras(b);
							loginAct.startActivity(in);
							loginAct.finish();
						}
						 *//*else if(retMess == getString(R.string.alert_oldversion))
			              {

				          try	{
						           Intent viewIntent = new Intent("android.intent.action.VIEW",Uri.parse("https://play.google.com/store/apps/details?id=shree_nagari.mbank"));
						           startActivity(viewIntent);
	                            }catch(Exception e) {
					            Toast.makeText(getApplicationContext(),"Unable to Connect Try Again...",
					                    Toast.LENGTH_LONG).show();
					            e.printStackTrace();
							}
							}*//*
						else if((retMess.equalsIgnoreCase(getString(R.string.alert_mpinexp)+" "+expdt+" day. Please Change MPIN")))
						{
							Log.e("Login","onCreate ==userId=  "+userId);
							Log.e("Login","onCreate ==custid=  "+custid);
							Intent in = new Intent(loginAct,SetMPIN.class);
							Bundle b = new Bundle();
							b.putString("FROMACT", "FORGOT");
							b.putString("USERNAME", userId);
							b.putString("CUSTID", custid);

							in.putExtras(b);
							loginAct.startActivity(in);
							loginAct.finish();
						}


					}
				});
		dbs.get_adb().show();

	}	*/

    public void post_successRetvalfailed(String retvalwebg) {

        if (retvalwebg.indexOf("NODATA") > -1) {
            retMess = "No Operative Account Found";
            showAlert1(retMess);
            //setAlert();
        }
		/*else if(retvalwebg.indexOf("OLDVERSION")>-1){
			retMess = getString(R.string.alert_oldversion);
			setAlert();}*/
    }

    public void post_success(String retvalwebg) {
        respcode = "";
        respdesc = "";
        isWsCallSuccess = true;
        System.out.println("xml_data.len :" + retvalwebg);

        if (retvalwebg.split("~").length >= 3) {
            if (retvalwebg.split("~")[2].equalsIgnoreCase("Y")) {
                flag = chkConnectivity();
                if (flag == 0) {
                    custId = retvalwebg.split("~")[3];
                    new CallGenerateOTPWebService().execute();
                }

            } else {
                LodandSave(retvalwebg);
            }
        } else {
            LodandSave(retvalwebg);
        }
    }

    public void LodandSave(String retrstr) {

        Log.e("In Login", "retrstr,,,,, :" + retrstr);
        decryptedAccounts = retrstr.split("SUCCESS~")[1];

        Log.e("In Login", "decryptedAccounts,,,,, :"
                + decryptedAccounts);

        if (!decryptedAccounts.equals("FAILED#")) {

            splitstr = decryptedAccounts.split("!@!");
            Bundle b = new Bundle();
            String accounts = splitstr[0];


            String mobno = splitstr[1];
            tranMpin = splitstr[2];
            custid = splitstr[3];
            userId = splitstr[4];

            System.out.println("in if ***************************************");
            System.out.println("decryptedAccounts :"
                    + decryptedAccounts);
            splitstr = decryptedAccounts.split("!@!");


            String oldversion = splitstr[5];
            strexpdate = splitstr[6];
            String otplog = splitstr[7];
            Log.e("strexpdate== ", "strexpdate==" + strexpdate);
            Double dt = Double.parseDouble(strexpdate);
            Log.e("dt== ", "dt==" + dt);
            //expdt=Integer.parseInt(strexpdate);
            expdt = dt.intValue();
            Log.e("expdt== ", "expdt==" + expdt);

            Log.e("userIdpostexecute== ", "userIdpostexecute==" + userId);
            Log.e("custidpostexecute== ", "custidpostexecute==" + custid);
		/*if(oldversion.equals("OLDVERSION"))
        {
      		showlogoutAlert(getString(R.string.alert_oldversionupdate));
      		 //setAlert();
        }
      	else*/
            if (expdt == 1) {
                retMess = getString(R.string.alert_mpinexp) + " " + expdt + " day. Please Change MPIN";
                showAlert1(retMess);
                //setAlert();
            } else if (expdt <= 7 && expdt >= 2) {
                showlogoutAlert1(getString(R.string.alert_mpinexp) + " " + expdt + " days. Do You Want To Change ?");
                //setAlert();
            } else {


                String[] columnNames = {"retval_str", "cust_name", "cust_id", "user_id", "cust_mobno"};
                String[] columnValues = {accounts, "", custid, userId, mobno};
                dbms.deleteFromTable("SHAREDPREFERENCE", "", null);
                dbms.insertIntoTable("SHAREDPREFERENCE", 5, columnNames, columnValues);
                Log.e("LOGIN", "accounts==" + accounts);
                Log.e("LOGIN", "custid==" + custid);
                Log.e("LOGIN", "encrptdMpin==" + encrptdMpin);
                Log.e("LOGIN", "tranMpin==" + tranMpin);
                Log.e("LOGIN", "mobno==" + mobno);
                Log.e("LOGIN", "userId==" + userId);
                Log.e("LOGIN", "custIdFlg==" + custIdFlg);

                if (!custIdFlg) {
                    String str = "";
                    String[] coulmnsAndTypes = {"CFG_CUST_ID", "varchar(10)"};
                    String[] colNms = {"CFG_CUST_ID"};
                    String[] val = new String[1];
                    val[0] = custid;
                    try {
                        str = dbms.createTable("CONFIG", coulmnsAndTypes);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Log.e("SETMPIN", "str after create table===" + str);
                    int recCnt = 0;
                    try {

                        Cursor c1 = dbms.executePersonalQuery("select count(*) from CONFIG", null);
                        if (c1.moveToNext()) {
                            recCnt = c1.getInt(0);
                            Log.e("Login ", "recCnt" + recCnt);
                        }
                        c1.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {

                        if (recCnt == 0)
                            str = dbms.insertIntoTable("CONFIG", 1, colNms, val);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Log.e("SETMPIN", "str after insert===" + str);
                } else {
                    String values[] = {et_custid.getText().toString()};
                    String[] colNms = {"CFG_CUST_ID"};
                    dbms.updateTable("CONFIG", colNms, null, values);
                }


                Intent intent = new Intent(loginAct, NewDashboard.class);
                intent.putExtra("VAR1", var1);
                intent.putExtra("VAR3", var3);
                intent.putExtras(b);
                startActivity(intent);
                finish();
            }
        } else {
            retMess = getString(R.string.alert_prblm_login);
            showAlert1(retMess);
            //setAlert();
        }

    }

    @Override
    public void onLocationChanged(Location location) {
        // TODO Auto-generated method stub
        Intent intent = null;
        if (flag == 0) {
            //new CallLoginWebService().doInBackground();
            new CallWebService().doInBackground();
            if (cnt == 1) {
                Log.i("mayuri success", "success");
                Bundle b = new Bundle();
                // add data to bundle
                Log.i("Return value:", retVal);
                /*
                 */
                // retVal="SUCCESS~ 5#101#SB#1#KADEKAR KAVITA KIRAN,5#101#SB#2#KADEKAR KAVITA KIRAN, 5#101#SB#3#Mrs. KADEKAR KAVITA KIRAN, 5#101#SB#6#KADEKAR DIGAMBAR HARI / KAVITA KIRAN, 5#101#SB#7#DESHPANDE JAGGANATH SHANKAR / KADEKAR KAVITA K., ~KAVITA KIRAN KADEKAR ~8007454533~";
                String string1[] = retVal.split("~");
                System.out.println("string1.length:" + string1.length);
                String cust_name = "", cust_mob_no = "";
                // for (int j = 0; j < string1.length; j++) {
                // System.out.println("[j]:" + j);
                System.out.println("string1[0]....:" + string1[0]);
                // System.out.println("string1[1]....:" + string1[1]);
                // System.out.println("string1[2]....:" + string1[2]);
                cust_name = string1[2];
                e.putString("cust_name", cust_name);

                cust_mob_no = string1[3];
                String string2[] = cust_mob_no.split(";");
                e.putString("cust_mob_no", string2[0]);
                // }
                b.putString("accounts", retVal);
                e.putString("retValStr", retVal);
                String custId = et_custid.getText().toString().trim();
                e.putString("custId", custId);
                String pin = et_mpin.getText().toString().trim();
                e.putString("pin", pin);
                e.commit();
                intent = new Intent(this, MainActivity.class);
                // add bundle to the intent
                intent.putExtras(b);
                // pb_wait.setVisibility(View.INVISIBLE);
                // pb_wait.setVisibility(ProgressBar.INVISIBLE);

                // intent = new Intent(getApplicationContext(),
                // BalanceEnquiry.class);
                startActivity(intent);
                finish();
            } else {
                showAlert1(retMess);
                //setAlert();
                // pb_wait.setVisibility(View.INVISIBLE);
                // et_custid.setText("");
                // et_mpin.setText("");
                et_custid.setFocusableInTouchMode(true);
                et_custid.requestFocus();
                cnt = 0;
            }
        }
        // p_wait.setVisibility(ProgressBar.INVISIBLE);
    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }

    public float getBatteryLevel() {
        Intent batteryIntent = registerReceiver(null, new IntentFilter(
                Intent.ACTION_BATTERY_CHANGED));
        int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        // Error checking that probably isn't needed but I added just in case.
        if (level == -1 || scale == -1) {
            return 50.0f;
        }

        return ((float) level / (float) scale) * 100.0f;
    }

    public class InputDialogBox extends Dialog implements OnClickListener {

        Activity activity;
        String msg, title;
        Context appAcontext;
        EditText bankname;
        Button btnSubmit;
        String strmpin = "";
        TextView txtLbl;
        boolean flg;

        public InputDialogBox(Activity activity) {

            super(activity);
        }// end InputDialogBox

        protected void onCreate(Bundle bdn) {

            super.onCreate(bdn);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.dialog_design_bankname_input);
            bankname = (EditText) findViewById(R.id.txtBankname);
            btnSubmit = (Button) findViewById(R.id.btnSubmit);
            bankname.setVisibility(EditText.VISIBLE);
            btnSubmit.setVisibility(Button.VISIBLE);
            btnSubmit.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            try {
                System.out
                        .println("========= inside onClick ============***********");
                String str = bankname.getText().toString().trim();
                Log.e("bank name =", str);
                insertToCONFIG(str);
                //tv_bankname.setText(str);
                this.hide();
            } catch (Exception e) {
                Log.e("EXCEPTION", "------------------" + e);
                e.printStackTrace();
                System.out
                        .println("Exception in InputDialogBox of onClick:=====>"
                                + e);
            }
        }// end onClick
    }// end InputDialogBox

    public String getCustId() {
        Log.e("LOGIN", " in getCustId");
        String customer_id = "";
        int cnt = 0;
        try {
            try {
                Cursor c = dbms.executePersonalQuery("select count(*) from CONFIG", null);
                if (c.moveToNext()) {
                    cnt = c.getInt(0);
                }
                c.close();
            } catch (Exception ex) {
                Log.e("LOGIN", "msg==" + ex.toString());
            }
            Log.e("LOGIN", "cnt===" + cnt);
            if (cnt > 0) {
                custIdFlg = true;
                Cursor c1 = dbms.executePersonalQuery("select CFG_CUST_ID from CONFIG", null);
                if (c1.moveToNext()) {
                    customer_id = c1.getString(0);
                    Log.e("Login ", "in loop" + customer_id);
                }
                c1.close();
            } else {
                custIdFlg = false;
                customer_id = "";
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        Log.e("Login", "in onCreate()" + customer_id);
        return customer_id;
    }

    public void createSharedPrefTable() {

        String sts = "";
        String val[] = {"retval_str", "varchar(2000)",
                "cust_name", "varchar(100)",
                "cust_id", "varchar(15)",
                "user_id", "varchar2(20)",
                "cust_mobno", "varchar(15)"};
        Log.e("createSharedPrefTable", "createSharedPrefTable==DBMS " + dbms);
        sts = dbms.createTable("SHAREDPREFERENCE", val);
        Log.e("LOGIN", "SHAREDPREFERENCE_create" + sts);


    }// createSharedPrefTable


    public void showAlert(final String str) {
        // Toast.makeText(this, str, Toast.LENGTH_LONG).show();
        ErrorDialogClass alert = new ErrorDialogClass(this, "" + str) {
            @Override
            public void onClick(View v) {
                //Log.e("SetMPIN","SetMPIN...btn CLicked=="+R.id.btn_ok+"==="+v.getId());
                switch (v.getId()) {
                    case R.id.btn_ok:
                        //Log.e("SetMPIN","SetMPIN...CASE trru="+WSCalled);
                        if ((str.equalsIgnoreCase(respdesc)) && (respcode.equalsIgnoreCase("0"))) {
                            post_success(retvalweb);
                        } else if ((str.equalsIgnoreCase(respdesc)) && (respcode.equalsIgnoreCase("1"))) {
                            post_successRetvalfailed(retvalweb);
                            //this.dismiss();
                        }
                        if ((str.equalsIgnoreCase(respdescvalidate)) && (respcode.equalsIgnoreCase("0"))) {
                            post_successvalidate(retvalvalidate);
                        } else if ((str.equalsIgnoreCase(respdescvalidate)) && (respcode.equalsIgnoreCase("1"))) {

                            this.dismiss();
                            //this.dismiss();
                        }
                        if ((str.equalsIgnoreCase(respdescresend)) && (respcode.equalsIgnoreCase("0"))) {
                            post_successresend(retvalotp);
                        } else if ((str.equalsIgnoreCase(respdescresend)) && (respcode.equalsIgnoreCase("1"))) {

                            this.dismiss();
                            //this.dismiss();
                        } else if (str.equalsIgnoreCase(getString(R.string.alert_restartapp))) {
//                            finish();
//                            System.exit(0);
                            restart();
                        } else
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

    public void restart() {
        Intent i = getBaseContext().getPackageManager().
                getLaunchIntentForPackage(getBaseContext().getPackageName());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        finish();
    }


    class CallfetchaccWebService_old extends AsyncTask<Void, Void, Void> {


        String[] xmlTags = {"PARAMS", "CHECKSUM"};// "CUSTID", "MPIN", "IMEINO" };
        String[] valuesToEncrypt = new String[2];
        // LoadProgressBar loadProBarObj=new LoadProgressBar(this);
        LoadProgressBar loadProBarObj = new LoadProgressBar(LoginActivity.this);
        String generatedXML = "";
        boolean isWSCalled = false, isWsCallSuccess = false;
        String ValidationData = "";
        /*
         * Key key_CustID,keyForMAC; String
         * encodedCustIDStr,encodedCustKeyStr,encodedCustMAC,encodedCustMACKey;
         */

        @Override
        protected void onPreExecute() {
            loadProBarObj.show();
            respcode = "";
            retvalweb = "";
            respdesc = "";
            // p_wait.setVisibility(ProgressBar.VISIBLE);

            custid = et_custid.getText().toString().trim();
            //	mpin = et_mpin.getText().toString().trim();
            mpin = newMpin;
            Log.e("IN onPreExecute()", "custid :" + custid);
            Log.e("IN onPreExecute()", "mpin :" + mpin);
            Log.e("IN onPreExecute()", "imeiNo :" + imeiNo);

            encrptdMpin = ListEncryption.encryptData(custid + mpin);
            //custid=custid;
            JSONObject obj = new JSONObject();
            try {

                String location = MBSUtils.getLocation(LoginActivity.this);
                obj.put("CUSTID", custid + "~#~" + version);
                obj.put("MPIN", encrptdMpin);
                obj.put("IMEINO", imeiNo + "~" + mobNo);
                obj.put("SIMNO", MBSUtils.getSimNumber(LoginActivity.this));
                //obj.put("VALIDATIONDATA", ValidationData);
                obj.put("MOBILENO", MBSUtils.getMyPhoneNO(LoginActivity.this));
                obj.put("IPADDRESS", MBSUtils.getLocalIpAddress());
                obj.put("OSVERSION", Build.VERSION.RELEASE);
                obj.put("LATITUDE", location.split("~")[0]);
                obj.put("LONGITUDE", location.split("~")[1]);
                obj.put("REQSTATUS", "R");
                obj.put("REQFROM", "MBS");
                obj.put("METHODCODE", "54");
                ValidationData = MBSUtils.getValidationData(LoginActivity.this, obj.toString());
                //Log.e("ValidationData-- ",ValidationData);

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            valuesToEncrypt[0] = obj.toString();
            valuesToEncrypt[1] = ValidationData;
		/*	valuesToEncrypt[0] = custid+"~#~"+version;
			valuesToEncrypt[1] = encrptdMpin;
			valuesToEncrypt[2] = imeiNo+"~"+mobNo;*/

            generatedXML = CryptoUtil.generateXML(xmlTags, valuesToEncrypt);

            Log.i("IN onPreExecute()", "generatedXML :" + generatedXML);

        }

        ;

        @Override
        protected Void doInBackground(Void... arg0) {
            NAMESPACE = getString(R.string.namespace);
            URL = getString(R.string.url);
            SOAP_ACTION = getString(R.string.soap_action);
            // p_wait.setVisibility(ProgressBar.VISIBLE);
            METHOD_NAMEfetch = "mbsInterCall";//"fetchAccountsWS";
            int i = 0;
            Log.i("mayuri", "in doInBackground()");
            try {

                SoapObject request = new SoapObject(NAMESPACE, METHOD_NAMEfetch);

                request.addProperty("para_value", generatedXML);

                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                        SoapEnvelope.VER11);
                envelope.setOutputSoapObject(request);
                HttpTransportSE androidHttpTransport = new HttpTransportSE(URL,
                        15000);
                String status = "";
                try {
                    Log.e("mayuri", "111");
                    androidHttpTransport.call(SOAP_ACTION, envelope);
                    Log.e("mayuri", "222");
                    System.out.println("here===" + envelope.bodyIn.toString());
                    Log.e("mayuri", "333");
                    status = envelope.bodyIn.toString().trim();
                    Log.i("mayuri status", status);
                    retVal = status;
                    int pos = envelope.bodyIn.toString().trim().indexOf("=");
                    if (pos > -1) {
                        status = status.substring(pos + 1, status.length() - 3);
                        retVal = status;
                        isWSCalled = true;
                    }
                    // return "SUCCESS";

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("In Login", "----------" + e);
                    // retMess = "Problem in login. Some error occured";
                    // retMess = "Network Unavailable. Please Try Again.";
                    retMess = getString(R.string.alert_000);
                    System.out.println("Exception");
                    cnt = 0;
                    // return "FAILED";
                }
            } catch (Exception e) {
                // retMess = "Error Getting IMEI NO";
                // retMess = "Network Unavailable. Please Try Again.";
                retMess = getString(R.string.alert_000);
                System.out.println(e.getMessage());
                cnt = 0;
                Log.e("EXCEPTION", "------------------" + e);
                // return "FAILED";
            }
            return null;
        }

        @Override
        protected void onPostExecute(final Void result) {
            loadProBarObj.dismiss();
            if (isWSCalled) {
                String[] xml_data = CryptoUtil.readXML(retVal, new String[]{"PARAMS", "CHECKSUM"});
                Log.e("OMG ERRO HERE", xml_data[0]);
                //Log.e("OMG ERRO HERE",xml_data[1]);

                ValidationData = xml_data[1].trim();
                JSONObject jsonObj;
                try {

                    jsonObj = new JSONObject(xml_data[0]);

                    if (ValidationData.equals(MBSUtils.getValidationData(LoginActivity.this, xml_data[0].trim())))//if(jsonObj.getString("VALIDATIONDATA").equalsIgnoreCase(ValidationData))
                    {
                        if (jsonObj.has("RESPCODE")) {
                            respcode = jsonObj.getString("RESPCODE");
                        } else {
                            respcode = "-1";
                        }
                        if (jsonObj.has("RETVAL")) {
                            retvalweb = jsonObj.getString("RETVAL");
                        } else {
                            retvalweb = "";
                        }
                        if (jsonObj.has("RESPDESC")) {
                            respdesc = jsonObj.getString("RESPDESC");
                        } else {
                            respdesc = "";
                        }


                        if (respdesc.length() > 0) {
                            showAlert(respdesc);
                        } else {
                            Log.e("retvalreturn ", "retval----------" + retvalweb);
                            if (retvalweb.indexOf("SUCCESS") > -1) {
                                LodandSave(retvalweb);
                            } else {
                                System.out.println("in else ***************************************==" + retvalweb);
                                if (retvalweb.indexOf("~") == -1) {
                                    retMess = getString(R.string.alert_network_problem_pease_try_again);
                                } else {
                                    String msg[] = retvalweb.split("~");

                                    Log.e("Check 1", msg[0]);
                                    Log.e("Check 2", msg[1]);

                                    if (msg[1].equals("1")) {
                                        retMess = getString(R.string.login_alert_009);
                                    } else if (msg[1].equals("2")) {
                                        retMess = getString(R.string.login_alert_002);
                                    } else if (msg[1].equals("3"))
                                        retMess = getString(R.string.login_alert_003);
                                    else if (msg[1].equals("4"))
                                        retMess = getString(R.string.login_alert_004);
                                    else if (msg[1].equals("5"))
                                        retMess = getString(R.string.login_alert_005);
                                    else if (msg[1].equals("6"))
                                        retMess = getString(R.string.login_alert_006);
                                    else if (msg[1].equals("7")) {
                                        Log.e("alskdaksjdlaksjd", "1231892731782");
                                        retMess = getString(R.string.login_alert_007);
                                    } else if (msg[1].equals("8")) {
                                        retMess = getString(R.string.login_alert_008);
                                    } else if (msg[1].equals("9"))
                                        retMess = getString(R.string.alert_login_fail);
                                    else if (msg[1].equals("10"))
                                        retMess = getString(R.string.login_alert_diffimei);
						/*else if(msg[1].indexOf("OLDVERSION")>-1)
							retMess = getString(R.string.alert_oldversion);*/
                                }
                                showAlert1(retMess);
                                //setAlert();
                            }
                        }
                    } else {

                        MBSUtils.showInvalidResponseAlert(LoginActivity.this);
                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else {
                retMess = getString(R.string.alert_000);
                showAlert1(retMess);
                //setAlert();
            }
        }

    }

    //Added by Shubham
    class CallfetchaccWebService_new extends AsyncTask<Void, Void, Void> {
        LoadProgressBar loadProBarObj = new LoadProgressBar(LoginActivity.this);
        boolean isWSCalled = false, isWsCallSuccess = false;
        JSONObject obj = new JSONObject();

        @Override
        protected void onPreExecute() {
            loadProBarObj.show();
            respcode = "";
            retvalweb = "";
            respdesc = "";
            custid = et_custid.getText().toString().trim();
            mpin = newMpin;
            encrptdMpin = mpin;//ListEncryption.encryptData(custid+mpin);

            try {
                String location = MBSUtils.getLocation(LoginActivity.this);
                obj.put("CUSTID", custid + "~#~" + version);
                obj.put("MPIN", encrptdMpin);
                obj.put("IMEINO", MBSUtils.getImeiNumber(LoginActivity.this));
                obj.put("SIMNO", MBSUtils.getSimNumber(LoginActivity.this));
                obj.put("MOBILENO", MBSUtils.getMyPhoneNO(LoginActivity.this));
                obj.put("IPADDRESS", MBSUtils.getLocalIpAddress());
                obj.put("OSVERSION", Build.VERSION.RELEASE);
                obj.put("LATITUDE", location.split("~")[0]);
                obj.put("LONGITUDE", location.split("~")[1]);
                obj.put("REQSTATUS", "R");
                obj.put("REQFROM", "MBS");
                obj.put("METHODCODE", "54");
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

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
                request.addProperty("value1", CryptoClass.Function5(obj.toString(), var2));
                request.addProperty("value2", CryptoClass.Function3(keyStr, var1));
                request.addProperty("value3", var3);
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.setOutputSoapObject(request);
                HttpTransportSE androidHttpTransport = new HttpTransportSE(value6, 45000);

                androidHttpTransport.call(value5, envelope);
                var5 = envelope.bodyIn.toString().trim();
                var5 = var5.substring(var5.indexOf("=") + 1, var5.length() - 3);
                isWSCalled = true;
            }// end try
            catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(final Void result) {
            loadProBarObj.dismiss();
            if (isWSCalled) {
                JSONObject jsonObj;
                try {
                    String str = CryptoClass.Function6(var5, var2);
                    jsonObj = new JSONObject(str.trim());
                    if (jsonObj.has("RESPCODE")) {
                        respcode = jsonObj.getString("RESPCODE");
                    } else {
                        respcode = "-1";
                    }
                    if (jsonObj.has("RETVAL")) {
                        retvalweb = jsonObj.getString("RETVAL");
                    } else {
                        retvalweb = "";
                    }
                    if (jsonObj.has("RESPDESC")) {
                        respdesc = jsonObj.getString("RESPDESC");
                    } else {
                        respdesc = "";
                    }
                    if (respdesc.length() > 0) {
                        showAlert(respdesc);
                    } else {
                        Log.e("retvalreturn ", "retval----------" + retvalweb);
                        if (retvalweb.indexOf("SUCCESS") > -1) {
                            LodandSave(retvalweb);
                        } else {
                            System.out.println("in else ***************************************==" + retvalweb);
                            if (retvalweb.indexOf("~") == -1) {
                                retMess = getString(R.string.alert_network_problem_pease_try_again);
                            } else {
                                String msg[] = retvalweb.split("~");

                                Log.e("Check 1", msg[0]);
                                Log.e("Check 2", msg[1]);

                                if (msg[1].equals("1")) {
                                    retMess = getString(R.string.login_alert_009);
                                } else if (msg[1].equals("2")) {
                                    retMess = getString(R.string.login_alert_002);
                                } else if (msg[1].equals("3"))
                                    retMess = getString(R.string.login_alert_003);
                                else if (msg[1].equals("4"))
                                    retMess = getString(R.string.login_alert_004);
                                else if (msg[1].equals("5"))
                                    retMess = getString(R.string.login_alert_005);
                                else if (msg[1].equals("6"))
                                    retMess = getString(R.string.login_alert_006);
                                else if (msg[1].equals("7")) {
                                    Log.e("alskdaksjdlaksjd", "1231892731782");
                                    retMess = getString(R.string.login_alert_007);
                                } else if (msg[1].equals("8")) {
                                    retMess = getString(R.string.login_alert_008);
                                } else if (msg[1].equals("9"))
                                    retMess = getString(R.string.alert_login_fail);
							/*else if(msg[1].equals("10"))
								retMess = getString(R.string.login_alert_diffimei);	*/
							/*else if(msg[1].indexOf("OLDVERSION")>-1)
								retMess = getString(R.string.alert_oldversion);*/
                            }
                            showAlert1(retMess);
                        }
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else {
                retMess = getString(R.string.alert_000);
                showAlert1(retMess);
            }
        }
    }  //End CallfetchaccWebService_new

    class CallGenerateOTPWebService extends AsyncTask<Void, Void, Void> {
        LoadProgressBar loadProBarObj = new LoadProgressBar(LoginActivity.this);
        String generatedXML = "", ValidationData = "";
        boolean isWSCalled = false;
        String[] xmlTags = {"PARAMS", "CHECKSUM"};
        String[] valuesToEncrypt = new String[2];
        JSONObject jsonObj = new JSONObject();

        @Override
        protected void onPreExecute() {
            loadProBarObj.show();
            //ValidationData=MBSUtils.getValidationData(logAct);

            try {
                jsonObj.put("CUSTID", custId);
                jsonObj.put("REQSTATUS", "R");
                jsonObj.put("REQFROM", "MBSL");
                jsonObj.put("MOBNO", strMobNo);
                jsonObj.put("IMEINO", MBSUtils.getImeiNumber(LoginActivity.this));
                jsonObj.put("SIMNO", MBSUtils.getSimNumber(LoginActivity.this));
                jsonObj.put("METHODCODE", "26");
                ValidationData = MBSUtils.getValidationData(LoginActivity.this, jsonObj.toString());

                METHOD_NAME1 = "mbsInterCall";//"buildOTPWS";
            } catch (Exception e) {
                e.printStackTrace();
            }

            valuesToEncrypt[0] = jsonObj.toString();
            valuesToEncrypt[1] = ValidationData;
            Log.e("ValidationData", ValidationData);
            generatedXML = CryptoUtil.generateXML(xmlTags, valuesToEncrypt);
        }

        ;

        @Override
        protected Void doInBackground(Void... arg0) {
            NAMESPACE = LoginActivity.this.getString(R.string.namespace);
            URL = LoginActivity.this.getString(R.string.url);
            SOAP_ACTION = LoginActivity.this.getString(R.string.soap_action);

            int i = 0;

            Log.i("mayuri", "in doInBackground()");
            try {

                SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME1);

                request.addProperty("para_value", generatedXML);

                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                        SoapEnvelope.VER11);
                envelope.setOutputSoapObject(request);
                HttpTransportSE androidHttpTransport = new HttpTransportSE(URL,
                        15000);
                String status = "";
                try {
                    Log.e("mayuri", "111");
                    androidHttpTransport.call(SOAP_ACTION, envelope);
                    Log.e("mayuri", "222");
                    System.out.println("here===" + envelope.bodyIn.toString());
                    Log.e("mayuri", "333");
                    status = envelope.bodyIn.toString().trim();
                    Log.i("mayuri status", status);
                    retVal = status;
                    int pos = envelope.bodyIn.toString().trim().indexOf("=");
                    if (pos > -1) {
                        status = status.substring(pos + 1, status.length() - 3);
                        retVal = status;
                        isWSCalled = true;
                    }
                    // return "SUCCESS";

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("In Login", "----------" + e);
                    // retMess = "Problem in login. Some error occured";
                    // retMess = "Network Unavailable. Please Try Again.";
                    retMess = getString(R.string.alert_000);
                    System.out.println("Exception");
                    cnt = 0;
                    // return "FAILED";
                }
            } catch (Exception e) {
                // retMess = "Error Getting IMEI NO";
                // retMess = "Network Unavailable. Please Try Again.";
                retMess = getString(R.string.alert_000);
                System.out.println(e.getMessage());
                cnt = 0;
                Log.e("EXCEPTION", "------------------" + e);
                // return "FAILED";
            }
            return null;

        }

        @Override
        protected void onPostExecute(final Void result) {
            loadProBarObj.dismiss();
            if (isWSCalled) {
                String[] xml_data = CryptoUtil.readXML(retVal, new String[]{"PARAMS", "CHECKSUM"});


                ValidationData = xml_data[1].trim();
                JSONObject jsonObj;
                try {
                    jsonObj = new JSONObject(xml_data[0]);
                    if (ValidationData.equals(MBSUtils.getValidationData(LoginActivity.this, xml_data[0].trim()))) {
                        if (jsonObj.has("RESPCODE")) {
                            respcode = jsonObj.getString("RESPCODE");
                        } else {
                            respcode = "-1";
                        }
                        if (jsonObj.has("RETVAL")) {
                            retvalotp = jsonObj.getString("RETVAL");
                        } else {
                            retvalotp = "";
                        }
                        if (jsonObj.has("RESPDESC")) {
                            respdescresend = jsonObj.getString("RESPDESC");
                        } else {
                            respdescresend = "";
                        }


                        if (respdescresend.length() > 0) {
                            showAlert(respdescresend);
                        } else {

                            if (retvalotp.split("~")[0].indexOf("SUCCESS") > -1) {
                                post_successresend(retvalotp);
                            } else {
                                retMess = LoginActivity.this.getString(R.string.alert_094);
                                showAlert(retMess);
                            }
                        }

                    } else {
                        MBSUtils.showInvalidResponseAlert(LoginActivity.this);
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            } else {
                retMess = LoginActivity.this.getString(R.string.alert_000);
                showAlert(retMess);
            }
        }
    }// CallWebService_resend_otp

    class CallWebServiceValidateOTP extends AsyncTask<Void, Void, Void> {
        String[] xmlTags = {"PARAMS", "CHECKSUM"};
        String[] valuesToEncrypt = new String[2];
        String ValidationData = "";
        JSONObject jsonObj = new JSONObject();
        LoadProgressBar loadProBarObj = new LoadProgressBar(LoginActivity.this);
        String generatedXML = "";
        boolean isWSCalled = false;

        @Override
        protected void onPreExecute() {
            loadProBarObj.show();


            try {
                jsonObj.put("CUSTID", custId);
                jsonObj.put("OTPVAL", ListEncryption.encryptData(strOTP + custId));
                jsonObj.put("IMEINO", MBSUtils.getImeiNumber(LoginActivity.this));
                jsonObj.put("REFID", strRefId);
                jsonObj.put("ISREGISTRATION", "N");
                jsonObj.put("SIMNO", MBSUtils.getSimNumber(LoginActivity.this));
                jsonObj.put("METHODCODE", "20");
                ValidationData = MBSUtils.getValidationData(LoginActivity.this, jsonObj.toString());

            } catch (Exception e) {
                e.printStackTrace();
            }
            valuesToEncrypt[0] = jsonObj.toString();
            valuesToEncrypt[1] = ValidationData;
            generatedXML = CryptoUtil.generateXML(xmlTags, valuesToEncrypt);
        }

        ;

        @Override
        protected Void doInBackground(Void... arg0) {
            NAMESPACE = getString(R.string.namespace);
            URL = getString(R.string.url);
            SOAP_ACTION = getString(R.string.soap_action);
            METHOD_NAME = "mbsInterCall";//"confirmOTPWS";
            int i = 0;
            try {

                SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

                request.addProperty("para_value", generatedXML);

                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.setOutputSoapObject(request);
                HttpTransportSE androidHttpTransport = new HttpTransportSE(URL, 15000);
                String status = "";
                try {
                    androidHttpTransport.call(SOAP_ACTION, envelope);
                    status = envelope.bodyIn.toString().trim();
                    retVal = status;
                    int pos = envelope.bodyIn.toString().trim().indexOf("=");
                    if (pos > -1) {
                        status = status.substring(pos + 1, status.length() - 3);
                        retVal = status;
                        isWSCalled = true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    retMess = getString(R.string.alert_000);
                    cnt = 0;
                }
            } catch (Exception e) {
                retMess = getString(R.string.alert_000);
                cnt = 0;
            }
            return null;
        }

        @Override
        protected void onPostExecute(final Void result) {
            loadProBarObj.dismiss();
            if (isWSCalled) {
                String[] xml_data = CryptoUtil.readXML(retVal, new String[]{"PARAMS", "CHECKSUM"});
                ValidationData = xml_data[1].trim();
                JSONObject jsonObj;
                try {
                    jsonObj = new JSONObject(xml_data[0]);
                    if (ValidationData.equals(MBSUtils.getValidationData(LoginActivity.this, xml_data[0].trim()))) {
                        if (jsonObj.has("RESPCODE")) {
                            respcode = jsonObj.getString("RESPCODE");
                        } else {
                            respcode = "-1";
                        }
                        if (jsonObj.has("RETVAL")) {
                            retvalvalidate = jsonObj.getString("RETVAL");
                        } else {
                            retvalvalidate = "";
                        }
                        if (jsonObj.has("RESPDESC")) {
                            respdescvalidate = jsonObj.getString("RESPDESC");
                        } else {
                            respdescvalidate = "";
                        }
                    } else {
                        MBSUtils.showInvalidResponseAlert(act);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (respdescvalidate.length() > 0) {
                    showAlert(respdescvalidate);
                } else {
                    if (retvalvalidate.indexOf("SUCCESS") > -1) {
                        post_successvalidate(retvalvalidate);
                    } else {
                        showAlert(getString(R.string.alert_076));
                    }
                }
            } else {
                showAlert(getString(R.string.alert_000));
            }
        }

    }

    public void post_successvalidate(String retval) {

        respdescvalidate = "";
        respcode = "";
        String decryptedAccounts = retval;//xml_data[0];

        flag = chkConnectivity();
        if (flag == 0) {
            new CallfetchaccWebService_new().execute();
        }
    }

    public void post_successresend(String retvalstr) {
        respdescresend = "";
        respcode = "";
        String returnstr = retvalstr.split("~")[1];
        String val[] = returnstr.split("!!");

        strRefId = val[2];
        String fromact = "LOGIN";

        InputDialogBoxotp inputBox = new InputDialogBoxotp(LoginActivity.this);
        inputBox.show();


	/*OtpDialog alert = new OtpDialog(LoginActivity.this){
		Button submit,resennd;
		TextView txt_ref_id;
		 EditText txt_otp;
		String textMessage,fromact,retstr;
		@Override
		protected void onCreate(Bundle savedInstanceState)
		{
			super.onCreate(savedInstanceState);
			submit = (Button)findViewById(R.id.btn_otp_submit);
			resennd = (Button)findViewById(R.id.btn_otp_resend);
			txt_ref_id=(TextView)findViewById(R.id.txt_ref_id);
			txt_otp=(EditText)findViewById(R.id.txt_otp);

			txt_ref_id.setText(txt_ref_id.getText().toString() + " :" + strRefId);
			submit.setOnClickListener(this);
			resennd.setOnClickListener(this);
		}

		@Override
		public void onClick(View v)
		{
			switch (v.getId())
			{
				case R.id.btn_otp_submit:
					strOTP=txt_otp.getText().toString();
					flag = chkConnectivity();
					if (strOTP.length() == 0) {
						retMess = LoginActivity.this.getString(R.string.alert_075);
						showAlert(retMess);
						this.show();
					} else if (strOTP.length() != 6) {
						retMess = LoginActivity.this.getString(R.string.alert_075);
						showAlert(retMess);// setAlert();
						this.show();
					} else {
						if (flag == 0)
						{
							new CallWebServiceValidateOTP().execute();
						}

					}

				  break;

				case R.id.btn_otp_resend:
					flag = chkConnectivity();
					if (flag == 0)
					{
						new CallGenerateOTPWebService().execute();
					}


					break;
				default:
				  break;
			}
			dismiss();
		}


	};
	alert.show();*/


    }

    public class InputDialogBoxotp extends Dialog implements OnClickListener {
        Activity activity;
        Button submit, resennd;
        TextView txt_ref_id;
        EditText txt_otp;
        String textMessage, fromact, retstr;
        boolean flg;

        public InputDialogBoxotp(Activity activity) {
            super(activity);
        }// end InputDialogBox

        protected void onCreate(Bundle bdn) {
            super.onCreate(bdn);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.otplogin);
            submit = (Button) findViewById(R.id.btn_otp_submit);
            resennd = (Button) findViewById(R.id.btn_otp_resend);
            txt_ref_id = (TextView) findViewById(R.id.txt_ref_id);
            txt_otp = (EditText) findViewById(R.id.txt_otp);

            txt_ref_id.setText(txt_ref_id.getText().toString() + " :" + strRefId);
            submit.setOnClickListener(this);
            resennd.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            try {

                switch (v.getId()) {
                    case R.id.btn_otp_submit:
                        strOTP = txt_otp.getText().toString();
                        flag = chkConnectivity();
                        if (strOTP.length() == 0) {
                            retMess = LoginActivity.this.getString(R.string.alert_075);
                            showAlert(retMess);
                            this.show();
                        } else if (strOTP.length() != 6) {
                            retMess = LoginActivity.this.getString(R.string.alert_075);
                            showAlert(retMess);// setAlert();
                            this.show();
                        } else {
                            if (flag == 0) {
                                new CallWebServiceValidateOTP().execute();
                            }

                        }

                        break;

                    case R.id.btn_otp_resend:

                        flag = chkConnectivity();
                        if (flag == 0) {

                            new CallGenerateOTPWebService().execute();
                        }
                        this.dismiss();

                        break;
                    default:
                        break;
                }
                //dismiss();

            } catch (Exception e) {
                e.printStackTrace();
                System.out
                        .println("Exception in InputDialogBox of onClick:=====>"
                                + e);
            }
        }// end onClick
    }// end InputDialogBox


}


class NetworkTimer extends CountDownTimer {
    LoginActivity logAct;

    @SuppressLint("MissingPermission")
    public NetworkTimer(long millisInFuture, long countDownInterval,
                        LoginActivity log) {
        super(millisInFuture, countDownInterval);
        logAct = log;
        // Toast.makeText(logAct, "Inside Countdown", Toast.LENGTH_LONG).show();
        // logAct.p_wait.setVisibility(ProgressBar.VISIBLE);
        // logAct.batteryManager= (BatteryManager)
        // logAct.getSystemService(Context.POWER_SERVICE);

        logAct.locManager = (LocationManager) logAct
                .getSystemService(Context.LOCATION_SERVICE);
        logAct.locManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER, 0, 0, logAct);
        start();
    }

    @Override
    public void onFinish() {
        // logAct.p_wait.setVisibility(ProgressBar.INVISIBLE);
        // Toast.makeText(logAct, "Inside Finish", Toast.LENGTH_LONG).show();
        // logAct.locManager.removeUpdates(logAct);
    }

    @Override
    public void onTick(long arg0) {
    }


}


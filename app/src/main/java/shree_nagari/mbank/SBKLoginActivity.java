package shree_nagari.mbank;

import java.security.PrivateKey;
import java.util.Random;

import javax.crypto.spec.SecretKeySpec;

import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import shree_nagari.mbank.R;
import mbLib.CryptoClass;
import mbLib.CustomDialogClass;
import mbLib.DatabaseManagement;
import mbLib.DialogBox;
import mbLib.ListEncryption;
import mbLib.MBSUtils;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.view.WindowManager.LayoutParams;
//import android.view.ViewGroup.LayoutParams;

public class SBKLoginActivity extends CustomWindow implements OnClickListener,OnTouchListener,
		LocationListener {
	
	TextView txt_forgot_pass, txt_register;
	
	ImageButton contactus, locateus,branches;
	EditText et_custid, et_mpin, etCustId, etMpin, edt_1, edt_2, edt_3, edt_4,
			edt_5, edt_6;
	LinearLayout pass_layout, mpin_layout, password_layout;
	Button button_1, button_2, button_3, button_4, button_5, button_6,
			button_7, button_8, button_9, button_0, button_erase_pin,
			button_view_pin;
	DialogBox dbs;
	View mpinChild, passwordChild;
	DatabaseManagement dbms;
	SBKLoginActivity loginAct = this;
	MainActivity act;
	public LocationManager locManager;
	public BatteryManager batteryManager;
	Button btn_mpin, btn_password,buttonLogin;
	String customerId = "", imeiNo = "", mobNo = "", version = "", custId = "",
			strMobNo = "", retMess = "", custid = "", newMpin = "",
			userId = "", respcode = "", retvalweb = "", respdesc = "",
			retVal = "", mpin = "", encrptdMpin = "", decryptedAccounts = "",
			retvalvalidate = "", respdescvalidate = "", respdescresend = "",
			retvalotp = "", strRefId = "", strOTP = "", tranMpin = "",
			strexpdate = "",loginAs="MPIN";
	private SparseArray<String> keyValues = new SparseArray<>();
	static int viewCnt = 0;
	int flag = 0, gpsFlg = 0, cnt = 0, expdt,layOutFlag=0;
	boolean custIdFlg = false, isWsCallSuccess = false;
	String[] splitstr;

	PrivateKey var1=null;	  
	String var5="",var3="";
	SecretKeySpec var2=null;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(LayoutParams.FLAG_SECURE, LayoutParams.FLAG_SECURE);
		setContentView(R.layout.sbk_login);
		/*if (!new DeviceUtils().isEmulator()) {
			MBSUtils.ifGooglePlayServicesValid(SBKLoginActivity.this);
		} else {
			MBSUtils.showAlertDialogAndExitApp(getString(R.string.alert_sup),SBKLoginActivity.this);
		}*/
		try {
			var1 = (PrivateKey) getIntent().getSerializableExtra("VAR1");
			var3 = (String) getIntent().getSerializableExtra("VAR3");
		}catch (Exception e){
			e.printStackTrace();
		}

		Log.e("strvarlognew","----"+var1);
		Log.e("strvarlognew","----"+var3);
		btn_mpin = (Button) findViewById(R.id.btn_mpin);
		btn_password = (Button) findViewById(R.id.btn_password);
		pass_layout = (LinearLayout) findViewById(R.id.pass_layout);
		
		btn_mpin.setOnClickListener(this);
		btn_password.setOnClickListener(this);
		
		dbms = new DatabaseManagement("shree_nagari.mbank", "listMobileBanking");
		Cursor cust1 = dbms.selectFromTable("SHAREDPREFERENCE", "", null);
		if (cust1 != null) {
			while (cust1.moveToNext()) {
				customerId = cust1.getString(2);
			}
		}

		imeiNo = MBSUtils.getImeiNumber(SBKLoginActivity.this);
		mobNo = MBSUtils.getMyPhoneNO(SBKLoginActivity.this);
		dbs = new DialogBox(this);
		locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		try {
			PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			version = pInfo.versionName;
		} catch (Exception e) {
			e.printStackTrace();
		}

		Cursor c1 = dbms.selectFromTable("SHAREDPREFERENCE", "", null);
		if (c1 != null) {
			while (c1.moveToNext()) {
				custId = c1.getString(2);
				strMobNo = c1.getString(4);
			}
		}

		// et_custid.setText(custId);
		createSharedPrefTable();
		if(customerId.length()==0)
			setPasswordView();
		else
			setMpinView();
		
		if(var1.toString().length()==0 || var3.toString().length()==0)
		{
			retMess = getString(R.string.alert_restart);
			showAlert(retMess);
		}
	}

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

	public void createSharedPrefTable() {
		String sts = "";
		String val[] = { "retval_str", "varchar(2000)", "cust_name",
				"varchar(100)", "cust_id", "varchar(15)", "user_id",
				"varchar2(20)", "cust_mobno", "varchar(15)" };
		sts = dbms.createTable("SHAREDPREFERENCE", val);
	}// createSharedPrefTable

	public void onBackPressed() {
		CustomDialogClass alert = new CustomDialogClass(SBKLoginActivity.this, getString(R.string.lbl_do_you_want_to_exit)) {
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

	}
	@Override
	public void onClick(View v) {
		Intent in;
		switch (v.getId()) {
		case R.id.btn_mpin:
			loginAs="MPIN";
			if(customerId.length()==0)
			{
				setPasswordView();
				Toast.makeText(this, "Please Login Using Password For First Time", Toast.LENGTH_SHORT).show();
			}
			else
			{
				pass_layout.removeView(mpinChild);
				pass_layout.removeView(passwordChild);
				setMpinView();
			}
			break;
		case R.id.btn_password:
			loginAs="PASSWORD";
			pass_layout.removeView(mpinChild);
			pass_layout.removeView(passwordChild);
			setPasswordView();
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
		case R.id.button_erase_pin:
			erasePinValues();
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
		case R.id.buttonLogin:
			String strCustId = etCustId.getText().toString().trim();
			boolean isNumeric;
			try {
				long t = Long.parseLong(strCustId);
				isNumeric = true;
			} catch (Exception e) {
				// TODO: handle exception
				isNumeric = false;
			}
			String strMpin = etMpin.getText().toString().trim();

			/*if (strCustId.length() != 10 && isNumeric) {
				retMess = getString(R.string.login_alert_010);
				setAlert();
			} else if (strMpin.length() != 6) {
				retMess = getString(R.string.login_alert_011);
				setAlert();
			} */
			if (strCustId.length()==0) {
				retMess = getString(R.string.alert_136);
				showAlert1(retMess);
				//setAlert();
			} else if (strMpin.length() == 0) {
				retMess = getString(R.string.alert_082);
				showAlert1(retMess);
				//setAlert();
			} 
			else {
				newMpin = strMpin;
				flag = chkConnectivity();
				
				if (flag == 0) {
					
					CallLoginWebService C = new CallLoginWebService();
					C.execute();
				}
			}
			break;
		case R.id.locateus:
			in = new Intent(loginAct, LocateUs.class);
			in.putExtra("VAR1", var1);
			in.putExtra("VAR3", var3);
			startActivity(in);
			loginAct.finish();
			break;
		case R.id.contactus:
			in = new Intent(loginAct, ContactUs.class);
			in.putExtra("VAR1", var1);
			in.putExtra("VAR3", var3);
			startActivity(in);
			loginAct.finish();
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
		default:
			break;
		}
	}

	public void setPinValues(String str) {
		if (edt_1.getText().toString().length() == 0)
		{
			edt_1.setText(str);
			edt_1.setBackgroundResource(R.drawable.mpin_edt);
		}
		else if (edt_2.getText().toString().length() == 0)
		{
			edt_2.setText(str);
			edt_2.setBackgroundResource(R.drawable.mpin_edt);
		}
		else if (edt_3.getText().toString().length() == 0)
		{
			edt_3.setText(str);
			edt_3.setBackgroundResource(R.drawable.mpin_edt);
		}
		else if (edt_4.getText().toString().length() == 0)
		{
			edt_4.setText(str);
			edt_4.setBackgroundResource(R.drawable.mpin_edt);
		}
		else if (edt_5.getText().toString().length() == 0)
		{
			edt_5.setText(str);
			edt_5.setBackgroundResource(R.drawable.mpin_edt);
		}
		else if (edt_6.getText().toString().length() == 0)
		{	
			edt_6.setText(str);
			edt_6.setBackgroundResource(R.drawable.mpin_edt);
			new CallLoginWebService().execute();
		}
	}

	public void erasePinValues() {
		if (edt_6.getText().toString().length() != 0)
		{
			edt_6.setText("");
			edt_6.setBackgroundResource(R.drawable.mpin_border);
		}
		else if (edt_5.getText().toString().length() != 0)
		{
			edt_5.setText("");
			edt_5.setBackgroundResource(R.drawable.mpin_border);
		}
		else if (edt_4.getText().toString().length() != 0)
		{
			edt_4.setText("");
			edt_4.setBackgroundResource(R.drawable.mpin_border);
		}
		else if (edt_3.getText().toString().length() != 0)
		{
			edt_3.setText("");
			edt_3.setBackgroundResource(R.drawable.mpin_border);
		}
		else if (edt_2.getText().toString().length() != 0)
		{
			edt_2.setText("");
			edt_2.setBackgroundResource(R.drawable.mpin_border);
		}
		else if (edt_1.getText().toString().length() != 0) {
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

	public void setMpinView() {
		btn_mpin.setTextColor(Color.parseColor("#fff8ad"));
		btn_password.setTextColor(Color.parseColor("#ffffff"));
		layOutFlag=0;
		mpinChild = getLayoutInflater().inflate(R.layout.mpin_login, null);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		params.setMargins(120, 0, 120, 0);
		mpinChild.setLayoutParams(params);
		pass_layout.addView(mpinChild);

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
		button_erase_pin = (Button) findViewById(R.id.button_erase_pin);
		button_view_pin = (Button) findViewById(R.id.button_view_pin);

		edt_1 = (EditText) findViewById(R.id.edt_1);
		edt_2 = (EditText) findViewById(R.id.edt_2);
		edt_3 = (EditText) findViewById(R.id.edt_3);
		edt_4 = (EditText) findViewById(R.id.edt_4);
		edt_5 = (EditText) findViewById(R.id.edt_5);
		edt_6 = (EditText) findViewById(R.id.edt_6);

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
		setKeyValues();
	}

	public void setPasswordView() {
		btn_password.setTextColor(Color.parseColor("#fff8ad"));
		btn_mpin.setTextColor(Color.parseColor("#ffffff"));
		layOutFlag=1;
		mpinChild = getLayoutInflater().inflate(R.layout.password_login, null);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		params.setMargins(100, 0, 100, 0);
		mpinChild.setLayoutParams(params);
		pass_layout.addView(mpinChild);

		txt_forgot_pass = (TextView) findViewById(R.id.txt_forgot_pass);
		txt_register = (TextView) findViewById(R.id.txt_register);
		etCustId = (EditText) findViewById(R.id.etCustId);
		etMpin = (EditText) findViewById(R.id.etMpin);
		buttonLogin = (Button) findViewById(R.id.buttonLogin);
		contactus = (ImageButton) findViewById(R.id.contactus);
		locateus = (ImageButton) findViewById(R.id.locateus);
		
		buttonLogin.setOnClickListener(this);
		contactus.setOnClickListener(this);
		locateus.setOnClickListener(this);
		txt_forgot_pass.setOnClickListener(this);
		txt_register.setOnClickListener(this);
	}

	public void showAlert1(final String str) {
		ErrorDialogClass alert = new ErrorDialogClass(SBKLoginActivity.this, "" + str)

		{
			Intent in = null;

			@Override
			public void onClick(View v)
			{
				switch (v.getId())
				{
					case R.id.btn_ok:
						if (retMess == getString(R.string.login_alert_007)) {
							Intent in = new Intent(loginAct,
									ChangeMobileNo.class);
							Bundle b = new Bundle();
							b.putString("custId", custid);
							in.putExtras(b);
							in.putExtra("VAR1", var1);
							in.putExtra("VAR3", var3);
							loginAct.startActivity(in);
							loginAct.finish();
						}else if(retMess.equalsIgnoreCase(getString(R.string.login_alert_009))){
							Log.e("sud----","sud---");
							Bundle bObj=new Bundle();
							Intent in=new Intent(getApplicationContext(),ValidateSecQueActivity.class);
							bObj.putString("custId",custid);
							bObj.putString("fromAct", "IMEIDIFF");
							in.putExtras(bObj);
							in.putExtra("VAR1", var1);
							in.putExtra("VAR3", var3);
							startActivity(in);
						}
						else if (retMess == getString(R.string.login_alert_002)) {
							Intent in = new Intent(loginAct,
									ForgotPassword.class);
							Bundle b = new Bundle();
							b.putString("FROMACT", "LOGIN");
							in.putExtras(b);
							in.putExtra("VAR1", var1);
							in.putExtra("VAR3", var3);
							loginAct.startActivity(in);
							loginAct.finish();
						}
						else if ((retMess
								.equalsIgnoreCase(getString(R.string.alert_mpinexp)
										+ " "
										+ expdt
										+ " day. Please Change MPIN"))) {

							Intent in = new Intent(loginAct, SetMPIN.class);
							Bundle b = new Bundle();
							b.putString("FROMACT", "FORGOT");
							b.putString("USERNAME", userId);
							b.putString("CUSTID", custid);

							in.putExtras(b);
							in.putExtra("VAR1", var1);
							in.putExtra("VAR3", var3);
							loginAct.startActivity(in);
							loginAct.finish();
						}

						break;
				}this.dismiss();

			}
		};alert.show();

	}
	/*public void setAlert() {
		dbs = new DialogBox(this);
		dbs.get_adb().setMessage(retMess);
		dbs.get_adb().setPositiveButton("OK",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						arg0.cancel();
						if (retMess == getString(R.string.login_alert_007)) {
							Intent in = new Intent(loginAct,
									ChangeMobileNo.class);
							Bundle b = new Bundle();
							b.putString("custId", custid);
							in.putExtras(b);
							in.putExtra("VAR1", var1);
							in.putExtra("VAR3", var3);
							loginAct.startActivity(in);
							loginAct.finish();
						} else if (retMess == getString(R.string.login_alert_002)) {
							Intent in = new Intent(loginAct,
									ForgotPassword.class);
							Bundle b = new Bundle();
							b.putString("FROMACT", "LOGIN");
							in.putExtras(b);
							in.putExtra("VAR1", var1);
							in.putExtra("VAR3", var3);
							loginAct.startActivity(in);
							loginAct.finish();
						} *//*else if (retMess == getString(R.string.alert_oldversion)) {

							try {
								Intent viewIntent = new Intent(
										"android.intent.action.VIEW",
										Uri.parse("https://play.google.com/store/apps/details?id=sbk.mbank"));
								startActivity(viewIntent);
							} catch (Exception e) {
								Toast.makeText(getApplicationContext(),
										"Unable to Connect Try Again...",
										Toast.LENGTH_LONG).show();
								e.printStackTrace();
							}
						} *//*else if ((retMess
								.equalsIgnoreCase(getString(R.string.alert_mpinexp)
										+ " "
										+ expdt
										+ " day. Please Change MPIN"))) {
							
							Intent in = new Intent(loginAct, SetMPIN.class);
							Bundle b = new Bundle();
							b.putString("FROMACT", "FORGOT");
							b.putString("USERNAME", userId);
							b.putString("CUSTID", custid);

							in.putExtras(b);
							in.putExtra("VAR1", var1);
							in.putExtra("VAR3", var3);
							loginAct.startActivity(in);
							loginAct.finish();
						}
						*//*else if(retMess.equalsIgnoreCase(getString(R.string.login_alert_009)))
						{
							//Toast.makeText(SBKLoginActivity.this, "in", Toast.LENGTH_SHORT).show();
							Intent in = new Intent(loginAct,ValidateSecQueActivity.class);
							Bundle b = new Bundle();
							b.putString("custId", loginAct.custid);
							b.putString("mpin", loginAct.mpin);
							b.putString("fromAct", "DIFFIMEI");
							in.putExtras(b);
							in.putExtra("VAR1", var1);
							in.putExtra("VAR3", var3);
							loginAct.startActivity(in);
							loginAct.finish();
						}*//*
					}
				});
		dbs.get_adb().show();
	}*/

	public int chkConnectivity() 
	{
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		try {
			State state = ni.getState();
			boolean state1 = ni.isAvailable();
			if (state1) {
				switch (state) {
				case CONNECTED:
					if (ni.getType() == ConnectivityManager.TYPE_MOBILE
							|| ni.getType() == ConnectivityManager.TYPE_WIFI) {
						gpsFlg = 1;
						flag = 0;
					}
					break;
				case DISCONNECTED:
					flag = 1;
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
					flag = 1;
					retMess = getString(R.string.alert_000);
					dbs = new DialogBox(this);
					dbs.get_adb().setMessage(retMess);
					dbs.get_adb().setPositiveButton("Ok",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface arg0,
										int arg1) {
									arg0.cancel();
									Intent in = null;
									in = new Intent(getApplicationContext(),
											SBKLoginActivity.class);
									in.putExtra("VAR1", var1);
									in.putExtra("VAR3", var3);
									startActivity(in);
									finish();
								}
							});
					dbs.get_adb().show();
					break;
				}
			} else {
				flag = 1;
				retMess = getString(R.string.alert_000);
				dbs = new DialogBox(this);
				dbs.get_adb().setMessage(retMess);
				dbs.get_adb().setPositiveButton("Ok",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface arg0, int arg1) {
								arg0.cancel();
								Intent in = null;
								in = new Intent(getApplicationContext(),
										SBKLoginActivity.class);
								in.putExtra("VAR1", var1);
								in.putExtra("VAR3", var3);
								startActivity(in);
								finish();
							}
						});
				dbs.get_adb().show();
			}
		} catch (NullPointerException ne) {
			flag = 1;
			retMess = getString(R.string.alert_000);
			dbs = new DialogBox(this);
			dbs.get_adb().setMessage(retMess);
			dbs.get_adb().setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface arg0, int arg1) {
							arg0.cancel();
							Intent in = null;
							in = new Intent(getApplicationContext(),
									SBKLoginActivity.class);
							in.putExtra("VAR1", var1);
							in.putExtra("VAR3", var3);
							startActivity(in);
							finish();
						}
					});
			dbs.get_adb().show();
		} catch (Exception e) {
			flag = 1;
			retMess = getString(R.string.alert_000);
			dbs = new DialogBox(this);
			dbs.get_adb().setMessage(retMess);
			dbs.get_adb().setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface arg0, int arg1) {
							arg0.cancel();
							Intent in = null;
							in = new Intent(getApplicationContext(),
									SBKLoginActivity.class);
							in.putExtra("VAR1", var1);
							in.putExtra("VAR3", var3);
							startActivity(in);
							finish();
						}
					});
			dbs.get_adb().show();
		}
		return flag;
	}

	class CallLoginWebService extends AsyncTask<Void, Void, Void> 
	{
		LoadProgressBar loadProBarObj = new LoadProgressBar(
				SBKLoginActivity.this);
		boolean isWSCalled = false, isWsCallSuccess = false;
		String ValidationData = "";
		JSONObject obj = new JSONObject();
		
		@Override
		protected void onPreExecute() {
			loadProBarObj.show();
			respcode = "";
			retvalweb = "";
			respdesc = "";
			if(layOutFlag==1)
			{
				custid = etCustId.getText().toString().trim();
				mpin = newMpin;
			}
			else
			{
				custid=customerId;
				mpin=edt_1.getText().toString()+edt_2.getText().toString()+edt_3.getText().toString()+edt_4.getText().toString()+edt_5.getText().toString()+edt_6.getText().toString();
			}
			encrptdMpin = ListEncryption.encryptData(custid + mpin);
			
			try {

				String location = MBSUtils.getLocation(SBKLoginActivity.this);
				obj.put("CUSTID", custid + "~#~" + version);
				obj.put("LOGINAS", loginAs);
				obj.put("MPIN", mpin);
				obj.put("IMEINO", imeiNo);// + "~" + mobNo
				obj.put("SIMNO", MBSUtils.getSimNumber(SBKLoginActivity.this));
				obj.put("MOBILENO",
						MBSUtils.getMyPhoneNO(SBKLoginActivity.this));
				obj.put("IPADDRESS", MBSUtils.getLocalIpAddress());
				obj.put("OSVERSION", Build.VERSION.RELEASE);
				obj.put("LATITUDE", location.split("~")[0]);
				obj.put("LONGITUDE", location.split("~")[1]);
				obj.put("REQSTATUS", "R");
				obj.put("REQFROM", "MBS");
				obj.put("METHODCODE","1");
				
				Log.e("SessionTimeout===","obj==="+obj);
				//ValidationData = MBSUtils.getValidationData(SBKLoginActivity.this, obj.toString());
			} catch (JSONException e) {
				e.printStackTrace();
			}

		};

		@Override
		protected Void doInBackground(Void... arg0) {
			String value4 = getString(R.string.namespace);
			String value5 = getString(R.string.soap_action);
			String value6 = getString(R.string.url);
			final String value7 = "callWebservice";		
			try {
				String keyStr=CryptoClass.Function2();
				var2=CryptoClass.getKey(keyStr);
				SoapObject request = new SoapObject(value4, value7);
										
				request.addProperty("value1", CryptoClass.Function5(obj.toString(), var2));
				request.addProperty("value2", CryptoClass.Function3(keyStr, var1));
				request.addProperty("value3", var3);
				SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
				envelope.setOutputSoapObject(request);
				HttpTransportSE androidHttpTransport = new HttpTransportSE(value6,15000);
				String status = "";
				try {
					androidHttpTransport.call(value5, envelope);
					status = envelope.bodyIn.toString().trim();
					var5 = status;
					Log.e("login===","var5==="+var5);
					int pos = envelope.bodyIn.toString().trim().indexOf("=");
					if (pos > -1) {
						status = status.substring(pos + 1, status.length() - 3);
						var5 = status;
						isWSCalled = true;
						}
				} 
				 catch (Exception e) {
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
					String str=CryptoClass.Function6(var5,var2);
					 jsonObj = new JSONObject(str.trim());
					 Log.e("login===","str==="+str);
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
			}

			else {
				retMess = getString(R.string.alert_000);
				showAlert1(retMess);
				//setAlert();
			}
		}

	}

	public void showAlert(final String str) {
		ErrorDialogClass alert = new ErrorDialogClass(this, "" + str) {
			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.btn_ok:
					if ((str.equalsIgnoreCase(respdesc))
							&& (respcode.equalsIgnoreCase("0"))) {
						post_success(retvalweb);
					} else if ((str.equalsIgnoreCase(respdesc))
							&& (respcode.equalsIgnoreCase("1"))) {
						post_successRetvalfailed(retvalweb);
						// this.dismiss();
					}
					if ((str.equalsIgnoreCase(respdescvalidate))
							&& (respcode.equalsIgnoreCase("0"))) {
						post_successvalidate(retvalvalidate);
					} else if ((str.equalsIgnoreCase(respdescvalidate))
							&& (respcode.equalsIgnoreCase("1"))) {
						this.dismiss();
					}
					if ((str.equalsIgnoreCase(respdescresend))
							&& (respcode.equalsIgnoreCase("0"))) {
						post_successresend(retvalotp);
					} else if ((str.equalsIgnoreCase(respdescresend))
							&& (respcode.equalsIgnoreCase("1"))) {
						this.dismiss();
					} else
						this.dismiss();
					
					if (str.equalsIgnoreCase(getString(R.string.alert_restart))) {
						finish();
						System.exit(0);
					}
					break;
				default:
					break;
				}
				dismiss();
			}
		};
		alert.show();
	}

	public void post_success(String retvalwebg) {
		respcode = "";
		respdesc = "";
		isWsCallSuccess = true;
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
		decryptedAccounts = retrstr.split("SUCCESS~")[1];
		if (!decryptedAccounts.equals("FAILED#")) {
			splitstr = decryptedAccounts.split("!@!");
			Bundle b = new Bundle();
			String accounts = splitstr[0];

			String mobno = splitstr[1];
			tranMpin = splitstr[2];
			custid = splitstr[3];
			userId = splitstr[4];
			splitstr = decryptedAccounts.split("!@!");
			String oldversion = splitstr[5];
			strexpdate = splitstr[6];
			String otplog = splitstr[7];
			Double dt = Double.parseDouble(strexpdate);
			expdt = dt.intValue();
			/*if (oldversion.equals("OLDVERSION")) {
				showlogoutAlert(getString(R.string.alert_oldversionupdate));
			} else*/ if (expdt == 1) {
				retMess = getString(R.string.alert_mpinexp) + " " + expdt
						+ " day. Please Change MPIN";
				showAlert1(retMess);
				//setAlert();
			} else if (expdt <= 7 && expdt >= 2) {
				showlogoutAlert1(getString(R.string.alert_mpinexp) + " "
						+ expdt + " days. Do You Want To Change ?");
			}
			else {
				String[] columnNames = { "retval_str", "cust_name", "cust_id",
						"user_id", "cust_mobno" };
				String[] columnValues = { accounts, "", custid, userId, mobno };
				dbms.deleteFromTable("SHAREDPREFERENCE", "", null);
				dbms.insertIntoTable("SHAREDPREFERENCE", 5, columnNames,
						columnValues);

				if (!custIdFlg) {
					String str = "";
					String[] coulmnsAndTypes = { "CFG_CUST_ID", "varchar(10)" };
					String[] colNms = { "CFG_CUST_ID" };
					String[] val = new String[1];
					val[0] = custid;
					try {
						str = dbms.createTable("CONFIG", coulmnsAndTypes);
					} catch (Exception e) {
						e.printStackTrace();
					}
					int recCnt = 0;
					try {

						Cursor c1 = dbms.executePersonalQuery(
								"select count(*) from CONFIG", null);
						if (c1.moveToNext()) {
							recCnt = c1.getInt(0);
						}
						c1.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
					try {

						if (recCnt == 0)
							str = dbms
									.insertIntoTable("CONFIG", 1, colNms, val);

					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					String values[] = { etCustId.getText().toString() };
					String[] colNms = { "CFG_CUST_ID" };
					dbms.updateTable("CONFIG", colNms, null, values);
				}
					
				Intent intent = new Intent(loginAct,
						NewDashboard.class);
				intent.putExtras(b);
				intent.putExtra("VAR1", var1);
				intent.putExtra("VAR3", var3);
				startActivity(intent);
				finish();
			}
		} else {
			retMess = getString(R.string.alert_prblm_login);
			showAlert1(retMess);
			//setAlert();
		}

	}

	class CallGenerateOTPWebService extends AsyncTask<Void, Void, Void> {
		LoadProgressBar loadProBarObj = new LoadProgressBar(
				SBKLoginActivity.this);
		String ValidationData = "";
		boolean isWSCalled = false;
		JSONObject jsonObj = new JSONObject();

		@Override
		protected void onPreExecute() {
			loadProBarObj.show();
			try {
				jsonObj.put("CUSTID", custId);
				jsonObj.put("REQSTATUS", "R");
				jsonObj.put("REQFROM", "MBSL");
				jsonObj.put("MOBNO", strMobNo);
				jsonObj.put("IMEINO",
						MBSUtils.getImeiNumber(SBKLoginActivity.this));
				jsonObj.put("SIMNO",
						MBSUtils.getSimNumber(SBKLoginActivity.this));
				jsonObj.put("METHODCODE","26");
				//ValidationData = MBSUtils.getValidationData(SBKLoginActivity.this, jsonObj.toString());

				
			} catch (Exception e) {
				e.printStackTrace();
			}

		};

		@Override
		protected Void doInBackground(Void... arg0) {
			String value4 = getString(R.string.namespace);
			String value5 = getString(R.string.soap_action);
			String value6 = getString(R.string.url);
			final String value7 = "callWebservice";
			try {
				String keyStr=CryptoClass.Function2();
				var2=CryptoClass.getKey(keyStr);
				SoapObject request = new SoapObject(value4, value7);
										
				request.addProperty("value1", CryptoClass.Function5(jsonObj.toString(), var2));
				request.addProperty("value2", CryptoClass.Function3(keyStr, var1));
				request.addProperty("value3", var3);
				SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
				envelope.setOutputSoapObject(request);
				HttpTransportSE androidHttpTransport = new HttpTransportSE(value6,15000);
				String status = "";
				try {
					androidHttpTransport.call(value5, envelope);
					status = envelope.bodyIn.toString().trim();
					var5 = status;
					int pos = envelope.bodyIn.toString().trim().indexOf("=");
					if (pos > -1) {
						status = status.substring(pos + 1, status.length() - 3);
						var5 = status;
						isWSCalled = true;
						}
				} 
				 catch (Exception e) {
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
			
				
				JSONObject jsonObj;
				try {
					String str=CryptoClass.Function6(var5,var2);
					 jsonObj = new JSONObject(str.trim());
					 
					/* ValidationData = xml_data[1].trim();
					if (ValidationData.equals(MBSUtils.getValidationData(
							SBKLoginActivity.this, xml_data[0].trim()))) {*/
					 Log.e("DSP","loginotp"+str);
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
								retMess = SBKLoginActivity.this
										.getString(R.string.alert_094);
								showAlert(retMess);
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
				retMess = SBKLoginActivity.this.getString(R.string.alert_000);
				showAlert(retMess);
			}
		}
	}// CallWebService_resend_otp

	public void post_successRetvalfailed(String retvalwebg) {
		if (retvalwebg.indexOf("NODATA") > -1) {
			retMess = "No Operative Account Found";
			showAlert1(retMess);
			//setAlert();
		} /*else if (retvalwebg.indexOf("OLDVERSION") > -1) {
			retMess = getString(R.string.alert_oldversion);
			setAlert();
		}*/
	}

	public void post_successresend(String retvalstr) {
		respdescresend = "";
		respcode = "";
		String returnstr = retvalstr.split("~")[1];
		String val[] = returnstr.split("!!");

		strRefId = val[2];
		String fromact = "LOGIN";

		InputDialogBoxotp inputBox = new InputDialogBoxotp(
				SBKLoginActivity.this);
		inputBox.show();
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

			txt_ref_id.setText(txt_ref_id.getText().toString() + " :"
					+ strRefId);
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
						retMess = SBKLoginActivity.this
								.getString(R.string.alert_076_01);
						showAlert(retMess);
						this.show();
					} /*else if (strOTP.length() != 6) {
						retMess = SBKLoginActivity.this
								.getString(R.string.alert_075);
						showAlert(retMess);// setAlert();
						this.show();
					}*/ else {
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
			} catch (Exception e) {
				e.printStackTrace();
			}
		}// end onClick
	}// end InputDialogBox

	class CallWebServiceValidateOTP extends AsyncTask<Void, Void, Void> {
			String ValidationData = "";
		JSONObject jsonObj = new JSONObject();
		LoadProgressBar loadProBarObj = new LoadProgressBar(
				SBKLoginActivity.this);
		boolean isWSCalled = false;

		@Override
		protected void onPreExecute() {
			loadProBarObj.show();

			try {
				jsonObj.put("CUSTID", custId);
				jsonObj.put("OTPVAL",strOTP);
						//ListEncryption.encryptData(strOTP + custId));
				jsonObj.put("IMEINO",
						MBSUtils.getImeiNumber(SBKLoginActivity.this));
				jsonObj.put("REFID", strRefId);
				jsonObj.put("ISREGISTRATION", "N");
				jsonObj.put("SIMNO",
						MBSUtils.getSimNumber(SBKLoginActivity.this));
				jsonObj.put("METHODCODE","20");
				//ValidationData = MBSUtils.getValidationData(SBKLoginActivity.this, jsonObj.toString());

			} catch (Exception e) {
				e.printStackTrace();
			}
		
		};

		@Override
		protected Void doInBackground(Void... arg0) {
			String value4 = getString(R.string.namespace);
			String value5 = getString(R.string.soap_action);
			String value6 = getString(R.string.url);
			//final String value7 = "callWebservice";
			final String value7 = getString(R.string.OTP_Validate_FUNCTION);
			try {
				String keyStr=CryptoClass.Function2();
				var2=CryptoClass.getKey(keyStr);
				SoapObject request = new SoapObject(value4, value7);
										
				request.addProperty("value1", CryptoClass.Function5(jsonObj.toString(), var2));
				request.addProperty("value2", CryptoClass.Function3(keyStr, var1));
				request.addProperty("value3", var3);
				SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
				envelope.setOutputSoapObject(request);
				HttpTransportSE androidHttpTransport = new HttpTransportSE(value6,15000);
				String status = "";
				try {
					androidHttpTransport.call(value5, envelope);
					status = envelope.bodyIn.toString().trim();
					var5 = status;
					int pos = envelope.bodyIn.toString().trim().indexOf("=");
					if (pos > -1) {
						status = status.substring(pos + 1, status.length() - 3);
						var5 = status;
						isWSCalled = true;
						}
				} 
				 catch (Exception e) {
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
				
				JSONObject jsonObj;
				try {
					String str=CryptoClass.Function6(var5,var2);
					 jsonObj = new JSONObject(str.trim());
					 
					 Log.e("DSP","loginOtpvalidate===="+str);
					/*ValidationData = xml_data[1].trim();
					if (ValidationData.equals(MBSUtils.getValidationData(
							SBKLoginActivity.this, xml_data[0].trim()))) {*/
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
					/*} else {
						MBSUtils.showInvalidResponseAlert(act);
					}*/

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
		String decryptedAccounts = retval;

		flag = chkConnectivity();
		if (flag == 0) {
			new CallfetchaccWebService().execute();
		}
	}

	class CallfetchaccWebService extends AsyncTask<Void, Void, Void> {

		LoadProgressBar loadProBarObj = new LoadProgressBar(
				SBKLoginActivity.this);
		boolean isWSCalled = false, isWsCallSuccess = false;
		String ValidationData = "";
		JSONObject obj = new JSONObject();
		
		@Override
		protected void onPreExecute() {
			loadProBarObj.show();
			respcode = "";
			retvalweb = "";
			respdesc = "";
			if(layOutFlag==1)
			{
				custid = etCustId.getText().toString().trim();
				mpin = newMpin;
			}
			else
			{
				custid=customerId;
				mpin=edt_1.getText().toString()+edt_2.getText().toString()+edt_3.getText().toString()+edt_4.getText().toString()+edt_5.getText().toString()+edt_6.getText().toString();
			}
			encrptdMpin = ListEncryption.encryptData(custid + mpin);
			
			try {
				String location = MBSUtils.getLocation(SBKLoginActivity.this);

				obj.put("CUSTID", custid + "~#~" + version);
				obj.put("MPIN", mpin);
				obj.put("IMEINO", imeiNo);// + "~" + mobNo
				obj.put("SIMNO", MBSUtils.getSimNumber(SBKLoginActivity.this));
				obj.put("MOBILENO",MBSUtils.getMyPhoneNO(SBKLoginActivity.this));
				obj.put("IPADDRESS", MBSUtils.getLocalIpAddress());
				obj.put("OSVERSION", Build.VERSION.RELEASE);
				obj.put("LATITUDE", location.split("~")[0]);
				obj.put("LONGITUDE", location.split("~")[1]);
				obj.put("REQSTATUS", "R");
				obj.put("REQFROM", "MBS");
				obj.put("METHODCODE","54");
				//ValidationData = MBSUtils.getValidationData(SBKLoginActivity.this, obj.toString());
			

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		};

		@Override
		protected Void doInBackground(Void... arg0) {
			String value4 = getString(R.string.namespace);
			String value5 = getString(R.string.soap_action);
			String value6 = getString(R.string.url);
			final String value7 = "callWebservice";		
			try {
				String keyStr=CryptoClass.Function2();
				var2=CryptoClass.getKey(keyStr);
				SoapObject request = new SoapObject(value4, value7);
										
				request.addProperty("value1", CryptoClass.Function5(obj.toString(), var2));
				request.addProperty("value2", CryptoClass.Function3(keyStr, var1));
				request.addProperty("value3", var3);
				SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
				envelope.setOutputSoapObject(request);
				HttpTransportSE androidHttpTransport = new HttpTransportSE(value6,15000);
				String status = "";
				try {
					androidHttpTransport.call(value5, envelope);
					status = envelope.bodyIn.toString().trim();
					var5 = status;
					int pos = envelope.bodyIn.toString().trim().indexOf("=");
					if (pos > -1) {
						status = status.substring(pos + 1, status.length() - 3);
						var5 = status;
						isWSCalled = true;
						}
				} 
				catch (Exception e) {
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
				
				JSONObject jsonObj;
				try {

					String str=CryptoClass.Function6(var5,var2);
					 jsonObj = new JSONObject(str.trim());
					 
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
								LodandSave(retvalweb);
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
										retMess = getString(R.string.login_alert_004);
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
					e.printStackTrace();
				}
			}

			else {
				retMess = getString(R.string.alert_000);
				showAlert1(retMess);
				//setAlert();
			}
		}

	}

	public void showlogoutAlert1(final String str) {
		CustomDialogClass alert = new CustomDialogClass(SBKLoginActivity.this,
				str) {

			@Override
			protected void onCreate(Bundle savedInstanceState) {
				super.onCreate(savedInstanceState);
			}

			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.btn_ok:
					if ((str.equalsIgnoreCase(getString(R.string.alert_mpinexp)
							+ " " + expdt + " days. Do You Want To Change ?"))) {
						Intent in = new Intent(loginAct, SetMPIN.class);
						Bundle b = new Bundle();
						b.putString("FROMACT", "FORGOT");
						b.putString("USERNAME", userId);
						b.putString("CUSTID", custid);

						in.putExtras(b);
						in.putExtra("VAR1", var1);
						in.putExtra("VAR3", var3);
						loginAct.startActivity(in);
						loginAct.finish();
					}

					break;

				case R.id.btn_cancel:

					if (!decryptedAccounts.equals("FAILED#")) {
						Bundle b = new Bundle();
						String accounts = splitstr[0];
						String mobno = splitstr[1];
						tranMpin = splitstr[2];
						custid = splitstr[3];
						userId = splitstr[4];

					
						String[] columnNames = { "retval_str", "cust_name",
								"cust_id", "user_id", "cust_mobno" };
						String[] columnValues = { accounts, "", custid, userId,
								mobno };

						dbms.deleteFromTable("SHAREDPREFERENCE", "", null);
						dbms.insertIntoTable("SHAREDPREFERENCE", 5,
								columnNames, columnValues);

						if (!custIdFlg) {
							String str = "";
							String[] coulmnsAndTypes = { "CFG_CUST_ID",
									"varchar(10)" };
							String[] colNms = { "CFG_CUST_ID" };
							String[] val = new String[1];
							val[0] = custid;
							try {
								str = dbms.createTable("CONFIG",
										coulmnsAndTypes);
							} catch (Exception e) {
								e.printStackTrace();
							}
							Log.e("SETMPIN", "str after create table===" + str);
							int recCnt = 0;
							try {

								Cursor c1 = dbms.executePersonalQuery(
										"select count(*) from CONFIG", null);
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
									str = dbms.insertIntoTable("CONFIG", 1,
											colNms, val);

							} catch (Exception e) {
								e.printStackTrace();
							}
						} else {
							String values[] = { etCustId.getText().toString() };
							String[] colNms = { "CFG_CUST_ID" };
							dbms.updateTable("CONFIG", colNms, null, values);
						}

						// intent = new Intent(loginAct, MainActivity.class);
						Intent intent = new Intent(loginAct,
								NewDashboard.class);
						// add bundle to the intent
						intent.putExtras(b);
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

	public void showlogoutAlert(final String str) {
		CustomDialogClass alert = new CustomDialogClass(SBKLoginActivity.this,
				str) {

			@Override
			protected void onCreate(Bundle savedInstanceState) {
				super.onCreate(savedInstanceState);
			}

			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.btn_ok:
					if ((str.equalsIgnoreCase(getString(R.string.alert_mpinexp)
							+ " " + expdt + " days. Do You Want To Change ?"))) {
						Intent in = new Intent(loginAct, SetMPIN.class);
						Bundle b = new Bundle();
						b.putString("FROMACT", "FORGOT");
						b.putString("USERNAME", userId);
						b.putString("CUSTID", custid);

						in.putExtras(b);
						in.putExtra("VAR1", var1);
						in.putExtra("VAR3", var3);
						loginAct.startActivity(in);
						loginAct.finish();
					} /*else if (str
							.equalsIgnoreCase(getString(R.string.alert_oldversionupdate))) {
						try {
							Intent viewIntent = new Intent(
									"android.intent.action.VIEW",
									Uri.parse("https://play.google.com/store/apps/details?id=sbk.mbank"));
							startActivity(viewIntent);
						} catch (Exception e) {
							Toast.makeText(getApplicationContext(),
									"Unable to Connect Try Again...",
									Toast.LENGTH_LONG).show();
							e.printStackTrace();
						}
						// this.dismiss();
					}*/
					break;

				case R.id.btn_cancel:

					if (expdt == 1) {
						retMess = getString(R.string.alert_mpinexp) + " "
								+ expdt + " day. Please Change MPIN";
						showAlert1(retMess);
						//setAlert();
					} else if (expdt <= 7 && expdt >= 2) {
						showlogoutAlert1(getString(R.string.alert_mpinexp)
								+ " " + expdt
								+ " days. Do You Want To Change ?");
						// setAlert();
					}

					else {

						Log.e("no pressed", "1111111");
						if (!decryptedAccounts.equals("FAILED#")) {
							Bundle b = new Bundle();
							String accounts = splitstr[0];
							String mobno = splitstr[1];
							tranMpin = splitstr[2];
							custid = splitstr[3];
							userId = splitstr[4];
						
						
							String[] columnNames = { "retval_str", "cust_name",
									"cust_id", "user_id", "cust_mobno" };
							String[] columnValues = { accounts, "", custid,
									userId, mobno };

							dbms.deleteFromTable("SHAREDPREFERENCE", "", null);
							dbms.insertIntoTable("SHAREDPREFERENCE", 5,
									columnNames, columnValues);

							if (!custIdFlg) {
								String str = "";
								String[] coulmnsAndTypes = { "CFG_CUST_ID",
										"varchar(10)" };
								String[] colNms = { "CFG_CUST_ID" };
								String[] val = new String[1];
								val[0] = custid;
								try {
									str = dbms.createTable("CONFIG",
											coulmnsAndTypes);
								} catch (Exception e) {
									e.printStackTrace();
								}
								Log.e("SETMPIN", "str after create table==="
										+ str);
								int recCnt = 0;
								try {

									Cursor c1 = dbms
											.executePersonalQuery(
													"select count(*) from CONFIG",
													null);
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
										str = dbms.insertIntoTable("CONFIG", 1,
												colNms, val);

								} catch (Exception e) {
									e.printStackTrace();
								}
								Log.e("SETMPIN", "str after insert===" + str);
							} else {
								String values[] = { etCustId.getText()
										.toString() };
								String[] colNms = { "CFG_CUST_ID" };
								dbms.updateTable("CONFIG", colNms, null, values);
							}

							// intent = new Intent(loginAct,
							// MainActivity.class);
							// Intent intent = new Intent(loginAct,
							// DashboardDesignActivity.class);
							Intent intent = new Intent(loginAct,
									NewDashboard.class);
							// add bundle to the intent
							intent.putExtras(b);
							intent.putExtra("VAR1", var1);
							intent.putExtra("VAR3", var3);
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


	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (v.getId()) {
		case R.id.btn_mpin:
			if(customerId.length()==0)
			{
				setPasswordView();
				Toast.makeText(this, "Please Login Using Password For First Time", Toast.LENGTH_SHORT).show();
			}
			else
			{
				pass_layout.removeView(mpinChild);
				pass_layout.removeView(passwordChild);
				setMpinView();
			}
			break;
		case R.id.btn_password:
			pass_layout.removeView(mpinChild);
			pass_layout.removeView(passwordChild);
			setPasswordView();
			break;
		}
		return false;
	}
}

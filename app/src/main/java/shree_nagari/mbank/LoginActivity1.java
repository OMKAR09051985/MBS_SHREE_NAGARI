package shree_nagari.mbank;


import mbLib.CryptoUtil;
import mbLib.CustomeTextChangeEvent;
import mbLib.DatabaseManagement;
import mbLib.DialogBox;
import mbLib.ListEncryption;

import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import shree_nagari.mbank.R;


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
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.NetworkInfo.State;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
//import android.annotation.SuppressLint;
import android.widget.Toast;

//if(CustID=="MBANK1" && MPIN=="12345" && imeiNo=="356330046479441")
//@SuppressLint("NewApi")
public class LoginActivity1 extends CustomWindow implements OnClickListener,
		LocationListener {
	// ProgressBar p_wait;
	Button btnLogin;
//	EditText et_custid, et_mpin;
	EditText txt_mpin1,txt_mpin2,txt_mpin3,txt_mpin4,txt_mpin5,txt_mpin6;
	LoginActivity1 loginAct = this;
	ImageButton cntus,locus;
	String imeiNo = "", tmpXMLString = "", retMess = "";
	
	TelephonyManager telephonyManager;
	TextView txt_forgot_pass;
	
	int cnt = 0, flag = 0;
	private static String NAMESPACE = "";
	private static String URL = "";
	private static String SOAP_ACTION = "";
	private static final String METHOD_NAME = "afterLogin";
	String retVal = "", encrptdMpin="";
	DialogBox dbs;
	DatabaseManagement dbms;

	int netFlg, gpsFlg;
	int timeout = 5;
	String pref = "G";
	String version="";

	private static final String MY_SESSION = "my_session";
	Editor e;
	public LocationManager locManager;
	public BatteryManager batteryManager;
	ImageView imageViewLogo;
	
	TextView tv_bankname;
	Cursor curSelectBankname;
	public String custid,customerId="";
	public String mpin;
	public String tranMpin;
	private String mobNo,userid,custname,retvalstr;
	boolean custIdFlg=false;
	String splitstr[]; String strMpin="";
	public String decryptedAccounts;

	@SuppressLint("MissingPermission")
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		Log.e("LoginActivity1 onCreate", "1111111");
		setContentView(R.layout.login1);
		System.out.println("222222");
		/*Typeface tf_mtcorsva = Typeface.createFromAsset(getAssets(),
				"fonts/Kozuka-Gothic-Pro-M_26793.ttf");*/
		//dbm = new DatabaseManagement("list.mbank", "listMobileBanking");
		//createConfigTable();
		/*
		 * final Window window = getWindow(); System.out.println("33333");
		 * boolean useTitleFeature = false; System.out.println("444444");
		 * Log.i("LoginActivity %%%%%%%%%%%%%%%%%%%%%%%%%%%%%",
		 * "window.getContainer()="+window.getContainer());
		 * System.out.println("555555"); if (window.getContainer() == null) {
		 * useTitleFeature = window.requestFeature(Window.FEATURE_CUSTOM_TITLE);
		 * } System.out.println("666666");
		 * Log.i("LoginActivity^^^^^^^^^^^^^^^^^^^^^^^^^^^",
		 * "useTitleFeature="+useTitleFeature);
		 */
		// setContentView(R.layout.login);

		/*
		 * 
		 * Typeface tf_aliquamREG = Typeface.createFromAsset(getAssets(),
		 * "fonts/Kozuka-Gothic-Pro-M_26793.ttf"); Typeface tf_kelvetica =
		 * Typeface.createFromAsset(getAssets(), "fonts/Kelvetica.ttf");
		 * 
		 * 
		 * TextView tv_formname = (TextView) findViewById(R.id.txtFormname);
		 * tv_formname.setTypeface(tf_calibri);
		 * 
		 * TextView tv_uname = (TextView) findViewById(R.id.textViewUname);
		 * tv_uname.setTypeface(tf_calibri);
		 * 
		 * TextView tv_pwd = (TextView) findViewById(R.id.textViewPwd);
		 * tv_pwd.setTypeface(tf_calibri);
		 */
		

		//tv_bankname = (TextView) findViewById(R.id.textViewBankName);
		//tv_bankname.setTypeface(tf_mtcorsva);
		
		
		//et_custid = (EditText) findViewById(R.id.etCustId);
	//	et_mpin = (EditText) findViewById(R.id.etMpin);
		btnLogin = (Button) findViewById(R.id.button1);
		
		txt_forgot_pass=(TextView)findViewById(R.id.txt_forgot_pass);
		cntus = (ImageButton) findViewById(R.id.contactus);
		locus = (ImageButton) findViewById(R.id.locateus);
		cntus.setOnClickListener(this);
		locus.setOnClickListener(this);
		
		//imageViewLogo = (ImageView) findViewById(R.id.login_imageViewLogo);

		/*imageViewLogo.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Log.e("MBS", "imageViewLogo clicked...");
				Log.e("MBS", "imageViewLogo clicked...");
				InputDialogBox inputBox = new InputDialogBox(loginAct);
				inputBox.show();

			}
		});*/
	      Bundle bObj = getIntent().getExtras();
		if (bObj != null)
		{
			customerId = bObj.getString("CUSTID");
			Log.e("onCreate","customerId== "+customerId);
			Log.e("onCreate","customerId== "+customerId);
		}

		btnLogin.setOnClickListener(this);
	//	txt_register.setOnClickListener(this);
		txt_forgot_pass.setOnClickListener(this);
		String service = Context.TELEPHONY_SERVICE;
		telephonyManager = (TelephonyManager) getSystemService(service);
		imeiNo = telephonyManager.getDeviceId();
		mobNo=telephonyManager.getLine1Number();
		// Log.i("MOB IMEI NO. :", imeiNo);
		dbs = new DialogBox(this);

		locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		
		try
		{
			PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			version = pInfo.versionName;
			Log.e("PackageInfo","PackageInfo"+version);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		//txt_version_no.setText("Version : "+version);
		
		// batteryManager=(BatteryManager)
		// getSystemService(Context.POWER_SERVICE);

		// INSTANTIATE SHARED PREFERENCES CLASS
	//	SharedPreferences sp = getSharedPreferences(MY_SESSION,
	//			Context.MODE_PRIVATE);
		// LOAD THE EDITOR REMEMBER TO COMMIT CHANGES!
		//e = sp.edit();
	/*	dbms = new DatabaseManagement("shree_nagari.mbank", "listMobileBanking");
		customerId=getCustId();
		if(customerId.length()>0)
		{
			et_custid.setText(customerId);
			//et_custid.setFocusable(false);
		}
		else
		{
			et_custid.setFocusable(true);
		}
		et_mpin.setFocusable(true);
		//setBankName();*/
	
		
		/*txt_mpin1=(EditText) findViewById(R.id.txt_mpin1);
		txt_mpin2=(EditText) findViewById(R.id.txt_mpin2);
		txt_mpin3=(EditText) findViewById(R.id.txt_mpin3);
		txt_mpin4=(EditText) findViewById(R.id.txt_mpin4);
		txt_mpin5=(EditText) findViewById(R.id.txt_mpin5);
		txt_mpin6=(EditText) findViewById(R.id.txt_mpin6);*/
		
		txt_mpin1.addTextChangedListener(new CustomeTextChangeEvent(txt_mpin1, txt_mpin2,null));
		txt_mpin2.addTextChangedListener(new CustomeTextChangeEvent(txt_mpin2,txt_mpin3,txt_mpin1));
		txt_mpin3.addTextChangedListener(new CustomeTextChangeEvent(txt_mpin3,txt_mpin4,txt_mpin2));
		txt_mpin4.addTextChangedListener(new CustomeTextChangeEvent(txt_mpin4,txt_mpin5,txt_mpin3));
		txt_mpin5.addTextChangedListener(new CustomeTextChangeEvent(txt_mpin5,txt_mpin6,txt_mpin4));
		txt_mpin6.addTextChangedListener(new CustomeTextChangeEvent(txt_mpin6,null,txt_mpin5));

        Log.e("txt_mpin6","txt_mpin6");
		Log.e("txt_mpin6","txt_mpin6");
		Log.e("txt_mpin6","txt_mpin6");
        txt_mpin6.setOnKeyListener(new View.OnKeyListener() {  
                 @Override
                 public boolean onKey(View v, int keyCode, KeyEvent event) {
                  Log.e("txt_mpin6","txt_mpin5 2222222");
                if ((keyCode == KeyEvent.KEYCODE_DEL)) { 
                Log.e("txt_mpin6","txt_mpin5 11111111");
	               txt_mpin5.requestFocus();     //	et_custid.setText(customerId);
                       return true;
			    } else
			     return false;
	            }
          });
		txt_mpin5.setOnKeyListener(new View.OnKeyListener() {
			
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				if ((keyCode == KeyEvent.KEYCODE_DEL)) {     
			    	Log.e("txt_mpin5","txt_mpin4 11111111");
			    	txt_mpin5.requestFocus();
			     return true;
			    }
				return false;
			}
		});
	
//	public void loadOldView()
	txt_mpin6.setOnFocusChangeListener(new OnFocusChangeListener()
	{
		/*setContentView(R.layout.login);
		et_custid = (EditText) findViewById(R.id.etCustId);
		et_mpin = (EditText) findViewById(R.id.etMpin);
		btnLogin = (Button) findViewById(R.id.button1);
		txt_register=(TextView)findViewById(R.id.txt_register);
		txt_forgot_pass=(TextView)findViewById(R.id.txt_forgot_pass);
		cntus = (ImageButton) findViewById(R.id.contactus);
		locus = (ImageButton) findViewById(R.id.locateus);
		cntus.setOnClickListener(this);
		locus.setOnClickListener(this);
		
		btnLogin.setOnClickListener(this);
		txt_register.setOnClickListener(this);
		txt_forgot_pass.setOnClickListener(this);*/
		
		public void onFocusChange(View v, boolean hasFocus) 
		        {
		        	Log.e("txt_mpin5","onFocusChange 5555");
		        	String txt1=txt_mpin6.getText().toString();
		            if (hasFocus == true)
		            {
		                if (txt1.length() == 0) // default text
		                {
		                	txt_mpin5.requestFocus();
		                }
		            }
		
		
		
		
	}
           });
	
	}
	public void createConfigTable() {// createConfigTable
		String sts = "";
		String val[] = { "conf_bankname", "varchar(60)" };

		//sts = dbm.createTable("CONFIG", val);
	}// createConfigTable

	public void insertToCONFIG(String bankname) {// insertToCONFIG
		String[] columnNames = { "conf_bankname" };
		String[] columnValues = { bankname };
		//boolean exists = dbm.CheckIsDataAlreadyInDBorNot("CONFIG",
		//		columnNames[0], columnValues[0]);
		//Log.e("MBS", "" + exists);
		//Log.e("MBS", "" + exists);

		//if (!exists) {
		//	dbm.insertIntoTable("CONFIG", 1, columnNames, columnValues);
		//}
	}// insertToCONFIG

	private void setBankName() {
		// TODO Auto-generated method stub
		//curSelectBankname = dbm.executePersonalQuery(
		//		"select conf_bankname from CONFIG", null);
		int flag = 0;
		try {
			while (curSelectBankname.moveToNext()) {
				//tv_bankname.setText(curSelectBankname.getString(0));
				flag = 1;
			}
			curSelectBankname.close();
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("EXCEPTION", "------------------"+e);
		}
		if (flag == 0) {
			//tv_bankname.setText(getString(R.string.lbl_ideal_mobile_banking));
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent in;
		switch (v.getId()) 
		{
		
		case R.id.txt_register:
			in=new Intent(this,Register.class);
			startActivity(in);
			finish();
			break;
		case R.id.txt_forgot_pass:
			in = new Intent(loginAct,ForgotPassword.class);
			Bundle b = new Bundle();
			b.putString("FROMACT", "FORGOT");
			in.putExtras(b);
			loginAct.startActivity(in);
			loginAct.finish();
			break;
            /*    case R.id.txt_mpin6:
			txt_mpin6.performClick();
			break;*/
		case R.id.button1:

		//	String strCustId = et_custid.getText().toString();
		/*	boolean isNumeric;
			try
			{
				long t=Long.parseLong(strCustId);
				isNumeric=true;
			}
			catch (Exception e) {
				// TODO: handle exception
				isNumeric=false;
			}
			String strMpin = et_mpin.getText().toString();
			Log.e("111111","22222222"+isNumeric);
			if(strCustId.length()!=10 && isNumeric)
			{
				retMess = getString(R.string.login_alert_010);
				setAlert();
			}
			else if(strMpin.length()!=6)
				{	
				retMess = getString(R.string.login_alert_011);
				setAlert(); 
			}
                        else*/
			//{
                       /*         newMpin=strMpin;
				Log.e("onClick button2","newMpin = "+strMpin);
				Log.e("onClick button2","newMpin = "+strMpin);
				Log.i("MBS", "login btn clicked...");
				flag = chkConnectivity();
				if (flag == 0)
				{
					CallWebService C = new CallWebService();
					C.execute();
				}
			}
			break;
                         case R.id.button2: */

				StringBuilder mpin = new StringBuilder();	
				String txtVal1 =  txt_mpin1.getText().toString();
				String txtVal2 =  txt_mpin2.getText().toString();
				String txtVal3 =  txt_mpin3.getText().toString();
				String txtVal4 =  txt_mpin4.getText().toString();
				String txtVal5 =  txt_mpin5.getText().toString();
				String txtVal6 =  txt_mpin6.getText().toString();
				//String strMpin = et_mpin.getText().toString();
				mpin.append("");
				mpin.append(txtVal1);
				mpin.append(txtVal2);
				mpin.append(txtVal3);
				mpin.append(txtVal4);
				mpin.append(txtVal5);
				mpin.append(txtVal6);
				strMpin=mpin.toString();
				Log.e("onClick button2","Mpin = "+strMpin);

			//	et_mpin.setText(strMpin);
			//	newMpin=strMpin;
				if(strMpin.length()!=6)
				{
					retMess = getString(R.string.login_alert_011);
					setAlert();
				}
				else
				{
					Log.i("MBS", "login btn clicked...");
					flag = chkConnectivity();
					if (flag == 0)
					{
						CallWebService C = new CallWebService();
						C.execute();
					}
				}
				break;
		
			
			
		case R.id.contactus:
			in=new Intent(loginAct,ContactUs.class);
			startActivity(in);
			loginAct.finish();
			break;
			
		case R.id.locateus:
			in=new Intent(loginAct,LocateUs.class);
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
		System.out.println("MObile bank login - in onDestroy()");
		System.out.println("MObile bank login - in onDestroy()");
		System.out.println("MObile bank login - in onDestroy()");
		System.out.println("MObile bank login - in onDestroy()");

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
									in = new Intent(getApplicationContext(),
											LoginActivity1.class);
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
										LoginActivity1.class);
								startActivity(in);
								finish();
							}
						});
				dbs.get_adb().show();
			
			}
		} catch (NullPointerException ne) {

			Log.e("EXCEPTION", "------------------"+ne);
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
									LoginActivity1.class);
							startActivity(in);
							finish();
						}
					});
			dbs.get_adb().show();
			

		} catch (Exception e) {
			Log.e("EXCEPTION", "------------------"+e);
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
									LoginActivity1.class);
							startActivity(in);
							finish();
						}
					});
			dbs.get_adb().show();
			
		}
		return flag;
	}

	class CallWebService extends AsyncTask<Void, Void, Void> {


		String[] xmlTags = {"PARAMS"};//"CUSTID", "MPIN", "IMEINO" };
		String[] valuesToEncrypt = new String[1];
                JSONObject jsonObj = new JSONObject();
		// LoadProgressBar loadProBarObj=new LoadProgressBar(this);
		LoadProgressBar loadProBarObj = new LoadProgressBar(LoginActivity1.this);
		String generatedXML = "";
		boolean isWSCalled = false;//,isWsCallSuccess=false;

		/*
		 * Key key_CustID,keyForMAC; String
		 * encodedCustIDStr,encodedCustKeyStr,encodedCustMAC,encodedCustMACKey;
		 */

		@Override
		protected void onPreExecute() {    
			try{
			loadProBarObj.show();

			// p_wait.setVisibility(ProgressBar.VISIBLE);

		//	custid = et_custid.getText().toString();
			//mpin = et_mpin.getText().toString();
			mpin=strMpin;
			Log.i("IN onPreExecute()", "custid :" + custid);
			Log.i("IN onPreExecute()", "mpin :" + mpin);
			Log.i("IN onPreExecute()", "imeiNo :" + imeiNo);

			encrptdMpin=ListEncryption.encryptData(custid+mpin);
			//custid=custid;
		//	JSONObject obj=new JSONObject();
		//	try {
				
				jsonObj.put("CUSTID", custid+"~#~"+version);
				jsonObj.put("MPIN", encrptdMpin);
				jsonObj.put("IMEINO", imeiNo+"~"+mobNo);
				
		
			} catch (JSONException je) {
				// TODO Auto-generated catch block
				je.printStackTrace();
			}
	valuesToEncrypt[0] =  jsonObj.toString();
			
		
			/*valuesToEncrypt[0] = custid+"~#~"+version;
			valuesToEncrypt[1] = encrptdMpin;
			valuesToEncrypt[2] = imeiNo+"~"+mobNo;*/

			generatedXML = CryptoUtil.generateXML(xmlTags, valuesToEncrypt);

			Log.i("IN onPreExecute()", "generatedXML :" + generatedXML);

		};

		@Override
		protected Void doInBackground(Void... arg0) 
		{
			NAMESPACE=getString(R.string.namespace);
			URL=getString(R.string.url);
			SOAP_ACTION=getString(R.string.soap_action);
			// p_wait.setVisibility(ProgressBar.VISIBLE);

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
					Log.e("mayuri", "111");
					androidHttpTransport.call(SOAP_ACTION, envelope);
					Log.e("mayuri", "222");
					System.out.println("here==="+envelope.bodyIn.toString());
					Log.e("mayuri", "333");
					status = envelope.bodyIn.toString().trim();
					Log.i("mayuri status", status);
					retVal = status;
					Log.e("Return value IN BACKGROUND () FUNCTION:", retVal);
					int pos = envelope.bodyIn.toString().trim().indexOf("=");
					if(pos>-1)
					{
						status = status.substring(pos + 1, status.length() - 3);
						retVal = status;
						isWSCalled = true;
					}
					// return "SUCCESS";

				}
				catch (Exception e) 
				{
					e.printStackTrace();
					Log.e("In Login", "----------" + e);
					// retMess = "Problem in login. Some error occured";
					// retMess = "Network Unavailable. Please Try Again.";
					retMess = getString(R.string.alert_000);
					System.out.println("Exception");
					cnt = 0;
					// return "FAILED";
				}
			} 
			catch (Exception e) 
			{
				// retMess = "Error Getting IMEI NO";
				// retMess = "Network Unavailable. Please Try Again.";
				retMess = getString(R.string.alert_000);
				System.out.println(e.getMessage());
				cnt = 0;
				Log.e("EXCEPTION", "------------------"+e);
				// return "FAILED";
			}
			return null;
		}

		@Override
		protected void onPostExecute(final Void result) 
		{
			 
			loadProBarObj.dismiss();
			if (isWSCalled) 
			{
				//String[] xmlTags = { "ACCOUNTS" };
				 String[] xml_data = CryptoUtil.readXML(retVal, new String[]{"PARAMS"});
				//String[] xml_data = CryptoUtil.readXML(retVal, xmlTags);
				Log.e("OMG ERRO HERE",xml_data[0]);
				
				if (xml_data[0].indexOf("SUCCESS") >-1) 
				{
					//isWsCallSuccess=true;
					System.out.println("xml_data.len :" + xml_data.length);
				String	decryptedAccounts = xml_data[0].split("SUCCESS~")[1];
					Log.e("In Login", "decryptedAccounts,,,,, :"
							+ decryptedAccounts);
					if (!decryptedAccounts.equals("FAILED#")) 
					{
                                            Bundle b = new Bundle();
						System.out.println("in if ***************************************");
						System.out.println("decryptedAccounts :"
								+ decryptedAccounts);
					String	splitstr[] =decryptedAccounts.split("!@!");
						System.out.println("splitstr.len :" + splitstr.length);
					//	String oldversion=splitstr[5];
					
							String accounts = splitstr[0];
                                                        Log.e("accounts :", accounts);
							String mobno =  splitstr[1];
							tranMpin =  splitstr[2];
							custid = splitstr[3];
							String userId=splitstr[4];
                                                      Log.e("LOGIN","userId=="+userId);
					//		System.out.println("mobno :" + mobno);

							
						/*	e.putString("accounts", accounts);
							e.putString("retValStr", accounts);
							e.putString("custId", custid);
							e.putString("pin", encrptdMpin);
							e.putString("tranPin", tranMpin);
							e.putString("cust_mob_no", mobno);
							e.putString("userId", userId);
							e.commit();*/
							
					     	createSharedPrefTable();
						
							
							String[] columnNames={"retval_str","cust_name","cust_id","user_id","cust_mobno"};
							String[] columnValues={accounts,"",custid,userId,mobno};
								
							dbms.deleteFromTable("SHAREDPREFERENCE", "",null);
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
								String values[] = {customerId};
								String[] colNms = { "CFG_CUST_ID" };
								dbms.updateTable("CONFIG", colNms, null, values);
							}

							// intent = new Intent(loginAct,
							// MainActivity.class);
							Intent intent = new Intent(loginAct,
									NewDashboard.class);
							// add bundle to the intent
							intent.putExtras(b);
							startActivity(intent);
							finish();
						
					
					}
					else
					{
						retMess = getString(R.string.alert_prblm_login);
						setAlert();
					}
					
				}
				else if(xml_data[0].indexOf("NODATA")>-1)
				{
					retMess = "No Operative Account Found";
					setAlert();
				}
				else if(xml_data[0].indexOf("OLDVERSION")>-1)
				{
					retMess = getString(R.string.alert_oldversion);
					setAlert();
				}
				else 
				{
					System.out.println("in else ***************************************");
					if(xml_data[0].indexOf("~")==-1)
					{
	                                       Log.e("Check",xml_data[0]);
						retMess = getString(R.string.alert_network_problem_pease_try_again);
					}
					else
					{
						String msg[] = xml_data[0].split("~");
						
						Log.e("Check 1",msg[0]);
						Log.e("Check 2",msg[1]);
						
						if(msg[1].equals("1"))
						{
							retMess = getString(R.string.login_alert_009);
						}
						else if(msg[1].equals("2"))
						{
							retMess = getString(R.string.login_alert_002);							
						}
						else if(msg[1].equals("3"))
						{
							retMess = getString(R.string.login_alert_003);
						}
						else if(msg[1].equals("4"))
						{
							retMess = getString(R.string.login_alert_004);
						}
						else if(msg[1].equals("5"))
						{
							retMess = getString(R.string.login_alert_005);
						}
						else if(msg[1].equals("6"))
						{
							retMess = getString(R.string.login_alert_006);
						}
						else if(msg[1].equals("7"))
						{
							Log.e("alskdaksjdlaksjd","1231892731782");
							retMess = getString(R.string.login_alert_007);						
						}
						else if(msg[1].equals("8"))
						{
							retMess = getString(R.string.login_alert_008);	
					     }
						else if(msg[1].equals("9"))
						{
							retMess = getString(R.string.alert_login_fail);	
						}
                        else if(msg[1].equals("NODATA")){
							retMess = "No Operative Account Found";
						}
					}
					setAlert();
				}
			} 
			else 
			{
				retMess = getString(R.string.alert_000);
				setAlert();
			}
		}

	}


	public void setAlert() {   
		dbs = new DialogBox(this);
		dbs.get_adb().setMessage(retMess);
		dbs.get_adb().setPositiveButton("OK",
				new DialogInterface.OnClickListener() {  
					@Override
					public void onClick(DialogInterface arg0, int arg1) {  
						arg0.cancel();
					/*	if(retMess.equalsIgnoreCase(getString(R.string.login_alert_009)))
						{
							Intent in = new Intent(loginAct,ValidateSecQueActivity.class);
							Bundle b = new Bundle();
							b.putString("custId", loginAct.custid);
							b.putString("mpin", loginAct.mpin);
							in.putExtras(b);
							loginAct.startActivity(in);
							loginAct.finish();
						}
						else*/ if(retMess == getString(R.string.login_alert_007))
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
						else if(retMess == getString(R.string.alert_oldversion))
						{
						
							try	{
									Intent viewIntent = new Intent("android.intent.action.VIEW",Uri.parse("https://play.google.com/store/apps/details?id=shree_nagari.mbank"));
									startActivity(viewIntent);
				   }catch(Exception e) {
								            Toast.makeText(getApplicationContext(),"Unable to Connect Try Again...",
								                    Toast.LENGTH_LONG).show();
								            e.printStackTrace();
										}
										}
						
					}
						
				
				});
		dbs.get_adb().show();
	/*	NewErrorDialogClass edc=new NewErrorDialogClass(LoginActivity.this,retMess)
		{
			@Override
			public void onClick(View v) 
			{
				//Toast.makeText(LoginActivity.this, "retMess==="+retMess, Toast.LENGTH_LONG).show();
				switch (v.getId()) 
				{
					case R.id.btn_ok:
						
						if(retMess == getString(R.string.login_alert_009))
						{
							Intent in = new Intent(loginAct,ValidateSecQueActivity.class);
							Bundle b = new Bundle();
							b.putString("custId", loginAct.custid);
							b.putString("mpin", loginAct.mpin);
							in.putExtras(b);
							loginAct.startActivity(in);
							loginAct.finish();
						}
						else if(retMess == getString(R.string.login_alert_007))
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
						dismiss();
					  break;			
					default:
					  break;
				}
				//dismiss();
			}
		};
		edc.show();*/
	}

	
	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		Intent intent = null;
		if (flag == 0) {
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
				String custId = customerId;
				e.putString("custId", custId);
			//	String pin = et_mpin.getText().toString();
                                String pin = strMpin;
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
				setAlert();
				// pb_wait.setVisibility(View.INVISIBLE);
				// et_custid.setText("");
				// et_mpin.setText("");
			//	et_custid.setFocusableInTouchMode(true);
			//	et_custid.requestFocus();
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
				String str = bankname.getText().toString();
				Log.e("bank name =", str);
				insertToCONFIG(str);
				//tv_bankname.setText(str);
				this.hide();
			} catch (Exception e) {
				Log.e("EXCEPTION", "------------------"+e);
				e.printStackTrace();
				System.out
						.println("Exception in InputDialogBox of onClick:=====>"
								+ e);
			}
		}// end onClick
	}// end InputDialogBox

	public String getCustId()
	{
		Log.e("LOGIN"," in getCustId");
		String customer_id="";
		int cnt=0;
		try
		{
			try
			{
				Cursor c=dbms.executePersonalQuery("select count(*) from CONFIG", null);
				if(c.moveToNext())
				{		
					cnt=c.getInt(0);
				}
				c.close();
			}
			catch(Exception ex)
			{
				Log.e("LOGIN", "msg=="+ex.toString());
			}
			Log.e("LOGIN","cnt==="+cnt);
			if(cnt>0)
			{	
				custIdFlg=true;
				Cursor c1=dbms.executePersonalQuery("select CFG_CUST_ID from CONFIG", null);
				if(c1.moveToNext())
				{
					customer_id=c1.getString(0);
					Log.e("Login ", "in loop"+customer_id);
				}
				c1.close();
			}
			else
			{	
				custIdFlg=false;
				customer_id="";
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		Log.e("Login", "in onCreate()"+customer_id);
		return customer_id;
	}

public void createSharedPrefTable(){
		
	//dbms.dropTable("SHAREDPREFERENCE");
		String sts="";
		String val[]={"retval_str","varchar(2000)",
					  "cust_name","varchar(100)",
					  "cust_id","varchar(15)",
					  "user_id","varchar2(20)",
					  "cust_mobno","varchar(15)"};
	    	sts=dbms.createTable("SHAREDPREFERENCE",val);
		Log.e("LOGIN","SHAREDPREFERENCE_create"+sts);
		
	}// createSharedPrefTable
}
/*	 public void onBackPressed() 
	 {
			
		showlogoutAlert(getString(R.string.lbl_do_you_want_to_exit));
		}
	 
	 public void showAlert(String str)
		{
				//Toast.makeText(this, str, Toast.LENGTH_LONG).show();	
				ErrorDialogClass alert = new ErrorDialogClass(loginAct,""+str);
				alert.show();
		}
	 public void showlogoutAlert(String str) { 
			CustomDialogClass alert = new CustomDialogClass(loginAct,str){ 
				
				@Override
				protected void onCreate(Bundle savedInstanceState)  
				{
					super.onCreate(savedInstanceState);
				}
				
				@Override
				public void onClick(View v)   
				{
					switch (v.getId()) 
					{
						case R.id.btn_ok:
							finish();
							//this.dismiss();
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
}*/


/*class NetworkTimer extends CountDownTimer {
	LoginActivity logAct;

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
}*/

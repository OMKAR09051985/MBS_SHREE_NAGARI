package shree_nagari.mbank;

import java.security.PrivateKey;

import javax.crypto.spec.SecretKeySpec;

import mbLib.ConnectionDetector;
import mbLib.CryptoClass;
import mbLib.MBSUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import shree_nagari.mbank.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class GetCustID extends Activity implements OnClickListener {

	MainActivity act;
	GetCustID getcustID;
	Button btn_proceed;
	EditText txt_accno, txt_mobileno;
	String accno = "", mobno = "", retMess = "",respcode="",retvalweb="",getcustidrespdesc="",sendotprespdesc="";
	Boolean isInternetPresent = false;
	ConnectionDetector cd;
	TextView txt_heading;
	private static String NAMESPACE = ""; // = "http://acct.com";
	private static String URL = ""; //
	private static String SOAP_ACTION = ""; // "http://acct.com/";
	private static String METHOD_NAME = ""; // "getCustomerID";
	private static String METHOD_NAME1 = "";
	private static String responseJSON = "NULL";
	Intent mainIntent;
	ImageView btn_home1,btn_logout;
	ImageButton btn_home;
	String[] presidents;
	static String imeiNo;
	String retVal = "";
	String custid = "";
	int flag = 0;
	ImageView img_heading;	
	TelephonyManager telephonyManager;
	PrivateKey var1=null;	  
	String var5="",var3="";
	SecretKeySpec var2=null;

	public GetCustID() {
	}

	public GetCustID(MainActivity a) {
		System.out.println("AddOtherBankBeneficiary()" + a);
		act = a;
		getcustID = this;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// dbm = new DatabaseManagement("mahavir.epassbook",
		// "ePassBookDataBase");
		getWindow().setFlags(LayoutParams.FLAG_SECURE, LayoutParams.FLAG_SECURE);
		setContentView(R.layout.customerid);
		var1 = (PrivateKey)getIntent().getSerializableExtra("VAR1");
		var3 = (String)getIntent().getSerializableExtra("VAR3");
		txt_accno = (EditText) findViewById(R.id.txt_accno);
		txt_mobileno = (EditText) findViewById(R.id.txt_mobileno);
		btn_proceed = (Button) findViewById(R.id.btn_proceed);
		btn_proceed.setOnClickListener(this);
		txt_heading = (TextView)findViewById(R.id.txt_heading);
		txt_heading.setText(getString(R.string.lbl_get_custID));
		img_heading = (ImageView) findViewById(R.id.img_heading);
		img_heading.setBackgroundResource(R.mipmap.newlogofordash);
		cd = new ConnectionDetector(getApplicationContext());
		presidents = getResources().getStringArray(R.array.Errorinwebservice);
		btn_home = (ImageButton) findViewById(R.id.btn_home);
		btn_home.setOnClickListener(this);
		
		btn_home1 = (ImageView) findViewById(R.id.btn_home1);
		btn_logout = (ImageView) findViewById(R.id.btn_logout);
		btn_home1.setVisibility(View.INVISIBLE);
		btn_logout.setVisibility(View.INVISIBLE);
		btn_home1.setOnClickListener(null);
		btn_logout.setOnClickListener(null);
		
		

		//String service = Context.TELEPHONY_SERVICE;
		//telephonyManager = (TelephonyManager) getSystemService(service);
		imeiNo = MBSUtils.getImeiNumber(GetCustID.this);//telephonyManager.getDeviceId();
		// LoadProgressBar loadProBarObj = new LoadProgressBar(GetCustID.this);
	}

	/*public static String getImeiNumber(Activity act) {
		TelephonyManager telephonyManager;
		String service = Context.TELEPHONY_SERVICE;
		telephonyManager = (TelephonyManager) act.getSystemService(service);
		imeiNo = telephonyManager.getDeviceId();
		Log.e("getImeiNumber ", "IMEI =" + imeiNo);
		Log.e("getImeiNumber ", "IMEI =" + imeiNo);
		Log.e("getImeiNumber ", "IMEI =" + imeiNo);
		Log.e("getImeiNumber ", "IMEI =" + imeiNo);
		Log.e("getImeiNumber ", "IMEI =" + imeiNo);
		return imeiNo;
	}*/

	@Override
	public void onClick(View v) { 
		// TODO Auto-generated method stub
		switch (v.getId()) {

		case R.id.btn_proceed:
			Log.e("=====", "111111111");
			accno = txt_accno.getText().toString().trim();
			mobno = txt_mobileno.getText().toString().trim();
			if (accno.length() == 0) {
				showAlert(getString(R.string.alert_acno));
			}
			if (accno.length() < 15) {
				showAlert(getString(R.string.alert_hint_accno));
			} else if (mobno.length() == 0) {
				showAlert(getString(R.string.alert_enter_mobile_number));
			} else if (mobno.length()!=10) {
				// showAlert(getString(R.string.alert_mobileno_len_min10_max15));
				showAlert(getString(R.string.alert_invalid_mobile_number));
			} else {
				Log.e("=====", "22222222222222222");
				// comment this code later
				/*
				 * Bundle bundle = new Bundle();
				 * bundle.putString("CUSTOMER_ID",customerId); mainIntent = new
				 * Intent(thisObj, ChooseMpin.class);
				 * mainIntent.putExtras(bundle); startActivity(mainIntent);
				 * finish();
				 */
				// comment this code later

				// get Internet status
			/*	isInternetPresent = cd.isConnectingToInternet();
				// check for Internet status
				if (isInternetPresent) {

					CallWebServiceGetCustID C = new CallWebServiceGetCustID();
					C.execute();

				} else {
					showAlert(getString(R.string.alert_internet_connection_not_available));
*/
					flag = chkConnectivity();
					if (flag == 0)
					{
						Log.e("Customer", "Before WS Call");
						CallWebServiceGetCustID C = new CallWebServiceGetCustID();
						C.execute();
						Log.e("Customer", "After WS Call");
					
				}
                               else
					{
					
				}
				/*
				 * flag = chkConnectivity();
				 * Log.e("Press Submit======","333333333333 value of flag   "
				 * +flag); if (flag == 0) {
				 * Log.e("================","44444444444444444");
				 * CallWebServiceGetCustID C = new CallWebServiceGetCustID();
				 * C.execute(); }
				 */
			}
	        /*  break;
		case R.id.btn_back:
			Intent in = new Intent(this, Register.class);
			startActivity(in);
			finish();
			break;
		default:
			break;*/
		}

	}

	public void showAlert(final String str) {
		// Toast.makeText(this, str, Toast.LENGTH_LONG).show();
		ErrorDialogClass alert = new ErrorDialogClass(GetCustID.this, "" + str)
		{@Override
			public void onClick(View v)

			{
				//Log.e("SetMPIN","SetMPIN...btn CLicked=="+R.id.btn_ok+"==="+v.getId());
				switch (v.getId()) 
				{
					case R.id.btn_ok:
						//Log.e("SetMPIN","SetMPIN...CASE trru="+WSCalled);
						if((str.equalsIgnoreCase(getcustidrespdesc)) &&  (respcode.equalsIgnoreCase("0")))
						{
							post_successCallWebServiceGetCustID(retvalweb);
						}
						else if((str.equalsIgnoreCase(getcustidrespdesc)) && (respcode.equalsIgnoreCase("1")))
						{
							this.dismiss();
						}
						else if((str.equalsIgnoreCase(sendotprespdesc)) &&  (respcode.equalsIgnoreCase("0")))
						{
							post_successCallWebServiceSendOTP(retvalweb);
						}
						else if((str.equalsIgnoreCase(sendotprespdesc)) && (respcode.equalsIgnoreCase("1")))
						{
							this.dismiss();
						}
						else
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

	public void onBackPressed() {
		Intent mainIntent;
		mainIntent = new Intent(GetCustID.this, Register.class);
		mainIntent.putExtra("VAR1", var1);
		mainIntent.putExtra("VAR3", var3);
		startActivityForResult(mainIntent, 500);
		// overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
		finish();
	}

	public class CallWebServiceGetCustID extends AsyncTask<Void, Void, Void> {

		LoadProgressBar loadProBarObj = new LoadProgressBar(GetCustID.this);
		JSONObject jsonobj = new JSONObject();
		String ValidationData="";
		@Override
		protected void onPreExecute() {
    
			loadProBarObj.show();
			respcode="";
			retvalweb="";
			getcustidrespdesc="";

				try {
					jsonobj.put("ACCNO", accno);
					jsonobj.put("MOBNO", mobno);
					jsonobj.put("IMEI", MBSUtils.getImeiNumber(GetCustID.this));
					jsonobj.put("SIMNO", MBSUtils.getSimNumber(GetCustID.this));
					jsonobj.put("METHODCODE","50");
					
					// ValidationData=MBSUtils.getValidationData(GetCustID.this,jsonobj.toString());
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
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
									
			request.addProperty("value1", CryptoClass.Function5(jsonobj.toString(), var2));
			request.addProperty("value2", CryptoClass.Function3(keyStr, var1));
			request.addProperty("value3", var3);
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
			envelope.setOutputSoapObject(request);
			HttpTransportSE androidHttpTransport = new HttpTransportSE(value6,15000);
			if(androidHttpTransport!=null)
				System.out.println("=============== androidHttpTransport is not null ");
			else
				System.out.println("=============== androidHttpTransport is  null ");
					      
			androidHttpTransport.call(value5, envelope);
			var5 = envelope.bodyIn.toString().trim();
			int i = envelope.bodyIn.toString().trim().indexOf("=");
			var5 = var5.substring(i + 1, var5.length() - 3);
			
		}// end try
		catch (Exception e) {
				Log.e("GenerateCUSTID ", "in doInBackground()	responseJSON :" + e);
				responseJSON = "NULL";
			}
			return null;
		}

		@Override
		protected void onPostExecute(final Void result) {
			
			loadProBarObj.dismiss();

					JSONObject jsonObj;
					try
					{
		
						String str=CryptoClass.Function6(var5,var2);
						 jsonObj = new JSONObject(str.trim());
						/*ValidationData=xml_data[1].trim();
						if(ValidationData.equals(MBSUtils.getValidationData(GetCustID.this, xml_data[0].trim())))
						{
						Log.e("IN return", "data :" + jsonObj.toString());*/
		               if (jsonObj.has("RESPCODE"))
						{
							respcode = jsonObj.getString("RESPCODE");
						}
						else
						{
							respcode="-1";
						}
						if (jsonObj.has("RETVAL"))
						{
							retvalweb = jsonObj.getString("RETVAL");
						}
						else
						{
							retvalweb = "";
						}
						if (jsonObj.has("RESPDESC"))
						{
							getcustidrespdesc = jsonObj.getString("RESPDESC");
						}
						else
						{	
							getcustidrespdesc = "";
						}
						
					if(getcustidrespdesc.length()>0)
					{
						showAlert(getcustidrespdesc);
					}
					else{
					
					// custid = json.getString("CUSTID");
					//System.out.println("RESPCODE :" + RESPCODE);

					if (respcode.equals("0")) {

						/*custid = retvalweb;//json.getString("CUSTID");
						Log.e("onPostExecute========", "CUSTID== " + custid);
						CallWebServiceSendOTP c = new CallWebServiceSendOTP();
						c.execute();*/
						
						post_successCallWebServiceGetCustID(retvalweb);
						

					} else if (!respcode.equals("0")) {
						showAlert(getString(R.string.alert_unable_to_getcustid));
					} else {
						if (!retvalweb.equalsIgnoreCase("NULL")) {
							String RESPREASON = getcustidrespdesc;//json.getString("RETVAL");
							int pos = Integer.parseInt(respcode);
							String errmsg = presidents[pos];
							Log.e("IN Choose MPin", errmsg);
							showAlert("" + errmsg);
						} else {
							showAlert(getString(R.string.alert_network_problem_pease_try_again));
						}
					}
					}
			
						/*}
						else{
							MBSUtils.showInvalidResponseAlert(GetCustID.this);	
						}*/
					} catch (JSONException e) 
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			

		}// onPostExecute

	}

	public 	void post_successCallWebServiceGetCustID(String retvalweb)
	{
		respcode="";getcustidrespdesc="";
		custid = retvalweb;//json.getString("CUSTID");
		Log.e("onPostExecute========", "CUSTID== " + custid);
		CallWebServiceSendOTP c = new CallWebServiceSendOTP();
		c.execute();
	}

	class CallWebServiceSendOTP extends AsyncTask<Void, Void, Void> {// CallWebService_resend_otp
		LoadProgressBar loadProBarObj = new LoadProgressBar(GetCustID.this);
		int cnt = 0, flag = 0;
	
		JSONObject jsonObj = new JSONObject();
		String  retVal = "";
		boolean isWSCalled = false;
		String ValidationData="";

		@Override
		protected void onPreExecute() {
			try{
		
			loadProBarObj.show();
			respcode="";
			retvalweb="";
			sendotprespdesc="";
		
			jsonObj.put("CUSTID", custid);
             jsonObj.put("REQSTATUS", "O");
             jsonObj.put("REQFROM", "MBSREG");
             jsonObj.put("SIMNO", MBSUtils.getSimNumber(GetCustID.this));
             jsonObj.put("METHODCODE","26");
           //  ValidationData=MBSUtils.getValidationData(GetCustID.this,jsonObj.toString());
			}  catch (JSONException je) {
                je.printStackTrace();
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
				} catch (Exception e) {
					e.printStackTrace();
					Log.e("In ", "----------" + e);
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
				// return "FAILED";
			}
			return null;

		}

		@Override
		protected void onPostExecute(final Void result) {
			loadProBarObj.dismiss();
				if (isWSCalled) {
		
				
				JSONObject jsonObj;
				try
				{
	
					String str=CryptoClass.Function6(var5,var2);
					 jsonObj = new JSONObject(str.trim());
					/*ValidationData=xml_data[1].trim();
					if(ValidationData.equals(MBSUtils.getValidationData(GetCustID.this, xml_data[0].trim())))
					{
					Log.e("IN return", "data :" + jsonObj.toString());*/
	               if (jsonObj.has("RESPCODE"))
					{
						respcode = jsonObj.getString("RESPCODE");
					}
					else
					{
						respcode="-1";
					}
					if (jsonObj.has("RETVAL"))
					{
						retvalweb = jsonObj.getString("RETVAL");
					}
					else
					{
						retvalweb = "";
					}
					if (jsonObj.has("RESPDESC"))
					{
						sendotprespdesc = jsonObj.getString("RESPDESC");
					}
					else
					{	
						sendotprespdesc = "";
					}
					
				if(sendotprespdesc.length()>0)
				{
					showAlert(sendotprespdesc);
				}
				else{
				
				
				if (retvalweb.split("~")[0].indexOf("SUCCESS") > -1) {
				
					post_successCallWebServiceSendOTP(retvalweb);
					
				} else {
					// System.out.println("in else ***************************************");
					retMess = getString(R.string.alert_094);
					showAlert(retMess);
				}
				}
					/*}
					else{
						MBSUtils.showInvalidResponseAlert(GetCustID.this);	
					}*/
				} catch (JSONException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			} else {
				retMess = getString(R.string.alert_000);
				showAlert(retMess);
			}
		}
	}// CallWebService_resend_otp

	public 	void post_successCallWebServiceSendOTP(String retvalweb)
	{
		respcode="";
		sendotprespdesc="";
		String decryptedAccounts = retvalweb.split("~")[1];
		Bundle bObj = new Bundle();
		Intent in = new Intent(GetCustID.this, OTPActivity.class);
		bObj.putString("RETVAL", decryptedAccounts);
		bObj.putString("CUSTID", custid);
		// bObj.putString("MOBNO",strMobNo);
		bObj.putString("FROMACT", "GetCustID");
		in.putExtras(bObj);
		in.putExtra("VAR1", var1);
		in.putExtra("VAR3", var3);
		startActivity(in);
		finish();
	}
	
	public int chkConnectivity() {
		// pb_wait.setVisibility(ProgressBar.VISIBLE);
		flag = 0;
		ConnectivityManager cm = (ConnectivityManager) this
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		// System.out.println("============= inside chkConnectivity 1 ================== ");
		NetworkInfo ni = cm.getActiveNetworkInfo();
		try {
			State state = ni.getState();
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
}

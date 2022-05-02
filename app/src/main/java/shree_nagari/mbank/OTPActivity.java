package shree_nagari.mbank;

import java.security.PrivateKey;

import javax.crypto.spec.SecretKeySpec;

import mbLib.CryptoClass;
import mbLib.DatabaseManagement;
import mbLib.DialogBox;
import mbLib.MBSUtils;
import mbLib.SMSReceiver;

import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
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
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class OTPActivity extends Activity implements OnClickListener {
	int cnt = 0, flag = 0;
	int netFlg, gpsFlg;
	public EditText txt_otp;
	DialogBox dbs;
	TextView txt_ref_id;
	public String strRefId;
	TextView txt_heading;
	ImageView btn_home1,btn_logout;
	ImageView img_heading;
	Button btn_otp_submit, btn_otp_resend;
	String strOTP,  retMess, retVal, strCustId, strFromAct, strRetVal,strMobNo;
	private SMSReceiver br;
	String from_activity = "", customer_id = "";
	String otp = "";
	String imeino = "";
	public String refno = "";
	private String[] presidents;
	private static String NAMESPACE = "";
	private static String URL = "";
	private static String SOAP_ACTION = "";
	private static String METHOD_NAME = "";
	private static String METHOD_NAME1 = "";
	private static String METHOD_NAME2 = "";
	private static String METHOD_NAME3 = "";
	private static String responseJSON = "NULL";
	TelephonyManager telephonyManager;
	String strActno="",cardno="",strimeino="",catdstatus="",version,stratm="";
	String imeiNo="",respcode="",retval="",respdescvalidate="",retvalwbs="",respdesc="",custId="",
			respdescresend="",respdescgent="",respdescsendcust="",respdesc_SaveATMCard="",stringValue="";
	private DatabaseManagement dbms;
	PrivateKey var1=null;	  
	String var5="",var3="";
	SecretKeySpec var2=null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(LayoutParams.FLAG_SECURE, LayoutParams.FLAG_SECURE);
		setContentView(R.layout.otp_activity);
		var1 = (PrivateKey)getIntent().getSerializableExtra("VAR1");
		var3 = (String)getIntent().getSerializableExtra("VAR3");
		dbms = new DatabaseManagement("shree_nagari.mbank", "listMobileBanking");
		Cursor c1 = dbms.selectFromTable("SHAREDPREFERENCE", "", null);
		if (c1 != null) 
		{
			while (c1.moveToNext()) 
			{
				stringValue = c1.getString(0);//ListEncryption.decryptData(c1.getString(0));
				custId = c1.getString(2);//ListEncryption.decryptData(c1.getString(2));
				
			}
		}
		txt_otp = (EditText) findViewById(R.id.txt_otp);
		txt_ref_id = (TextView) findViewById(R.id.txt_ref_id);
		btn_otp_submit = (Button) findViewById(R.id.btn_otp_submit);
		btn_otp_resend = (Button) findViewById(R.id.btn_otp_resend);
		txt_heading=(TextView)findViewById(R.id.txt_heading);
		txt_heading.setText(getString(R.string.lbl_otp_validtn));
		img_heading = (ImageView) findViewById(R.id.img_heading);
		img_heading.setBackgroundResource(R.mipmap.otp);
		btn_home1 = (ImageView) findViewById(R.id.btn_home1);
		btn_logout = (ImageView) findViewById(R.id.btn_logout);
		btn_logout.setVisibility(View.GONE);
		btn_home1.setOnClickListener(this);
		btn_logout.setOnClickListener(this);
		btn_otp_submit.setOnClickListener(this);
		btn_otp_resend.setOnClickListener(this);
		dbs = new DialogBox(this);
		presidents = getResources().getStringArray(R.array.Errorinwebservice);
		Bundle bObj = getIntent().getExtras();
		if (bObj != null) 
		{
			strCustId = bObj.getString("CUSTID");
			strFromAct = bObj.getString("FROMACT");
			strRetVal = bObj.getString("RETVAL");
			strMobNo = bObj.getString("MOBNO");
			
			String val[] = strRetVal.split("!!");
			
			strRefId=val[2];
			showAlert(getString(R.string.alert_074) + val[2]);
			txt_ref_id.setText(txt_ref_id.getText().toString() + " :" + val[2]);
			
			if(strFromAct.equals("ENABLEATM"))
			{
				strActno=val[3];
				cardno=val[4];
				catdstatus=val[5];
				strimeino=val[6];
			}
			else if(strFromAct.equals("FORGOT") || strFromAct.equals("GetCustID") || strFromAct.equals("IMEIDIFF")
					|| strFromAct.equals("REGISTER"))
			{
				btn_home1.setVisibility(View.INVISIBLE);
				btn_logout.setVisibility(View.INVISIBLE);
				btn_home1.setOnClickListener(null);
				btn_logout.setOnClickListener(null);
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_home1:
			Intent in1 = new Intent(this, NewDashboard.class);
			in1.putExtra("VAR1", var1);
			in1.putExtra("VAR3", var3);
			startActivity(in1);
			finish();
			break;
		case R.id.btn_logout:	
			dbs = new DialogBox(this);
			dbs.get_adb().setMessage(getString(R.string.lbl_exit));
			dbs.get_adb().setPositiveButton("Yes",new DialogInterface.OnClickListener() 
			{
				@Override
				public void onClick(DialogInterface arg0, int arg1) 
				{
					flag = chkConnectivity();
					if (flag == 0)
					{
						CallWebServicelog c=new CallWebServicelog();
						c.execute();
					}
				}
			});
			dbs.get_adb().setNegativeButton("No",new DialogInterface.OnClickListener() 
			{
				@Override
				public void onClick(DialogInterface arg0, int arg1) 
				{
					arg0.cancel();
				}
			});
			dbs.get_adb().show();
			break;	
		case R.id.btn_otp_submit:
			strOTP = txt_otp.getText().toString().trim();
			/*if (strOTP.length() != 6) 
			{
				showAlert(getString(R.string.alert_075));
			} */
			if (strOTP.length() == 0) 
			{
				showAlert(getString(R.string.alert_076_01));
			} 
			else 
			{
				Toast.makeText(OTPActivity.this, strFromAct, Toast.LENGTH_SHORT).show();
				flag = chkConnectivity();
				if (flag == 0)
				{
					/*if(strFromAct.equalsIgnoreCase("GetCustID"))
					{
						CallWebServiceSendCustId s=new CallWebServiceSendCustId();
						s.execute();
					}
					else
					{*/
						if(strFromAct.equalsIgnoreCase("IMEIDIFF"))
						{
							CallWebServiceValidateOTPDiffIMEI c = new CallWebServiceValidateOTPDiffIMEI();
							c.execute();
						}
						else
						{	
							CallWebServiceValidateOTP c = new CallWebServiceValidateOTP();
							c.execute();
						}	
					}
				//}
				 
			}
			break;
		case R.id.btn_otp_resend:
			flag = chkConnectivity();
			if (flag == 0)
			{
				CallWebService_resend_otp c = new CallWebService_resend_otp();
				c.execute();
			}
			break;
		default:
			break;
		}
	}

	class CallWebServicelog extends AsyncTask<Void, Void, Void> {
	            JSONObject jsonObj = new JSONObject();
		String ValidationData="";
		LoadProgressBar loadProBarObj = new LoadProgressBar(OTPActivity.this);

		@Override
		protected void onPreExecute() {
                   try{
                	   loadProBarObj.show();
                	   respcode="";
                	   retvalwbs="";
                	   respdesc="";
		         jsonObj.put("CUSTID", custId);
	              jsonObj.put("IMEINO", MBSUtils.getImeiNumber(OTPActivity.this));
	              jsonObj.put("SIMNO", MBSUtils.getSimNumber(OTPActivity.this));
	              jsonObj.put("METHODCODE","29");
	             // ValidationData=MBSUtils.getValidationData(OTPActivity.this,jsonObj.toString());
		
                       }
			   catch (JSONException je) {
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
				// retMess = "Error occured";
				getString(R.string.alert_000);
				System.out.println(e.getMessage());
				Log.e("ERROR-OUTER",e.getClass()+" : "+e.getMessage());
			}
			return null;
		}

		protected void onPostExecute(final Void result) {
			Log.e("@DEBUG","LOGOUT onPostExecute()");
			loadProBarObj.dismiss();
                	
                	//String[] xml_data = CryptoUtil.readXML(retval, new String[]{"PARAMS","CHECKSUM"});
                	JSONObject jsonObj;
    				try
    				{
    	
    					String str=CryptoClass.Function6(var5,var2);
   					 	jsonObj = new JSONObject(str.trim());
    					/*ValidationData=xml_data[1].trim();
    					if(ValidationData.equals(MBSUtils.getValidationData(OTPActivity.this, xml_data[0].trim())))
    					{*/
    					Log.e("IN return", "data :" + jsonObj.toString());
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
    						retvalwbs = jsonObj.getString("RETVAL");
    					}
    					else
    					{
    						retvalwbs = "";
    					}
    					if (jsonObj.has("RESPDESC"))
    					{
    						respdesc = jsonObj.getString("RESPDESC");
    					}
    					else
    					{	
    						respdesc = "";
    					}
    					
    				if(respdesc.length()>0)
    				{
    					showAlert(respdesc);
    				}
    				else{
			if (retvalwbs.indexOf("FAILED") > -1) {
				retMess = getString(R.string.alert_network_problem_pease_try_again);
				showAlert(retMess);

			}			
			else
			{
				post_successlog(retvalwbs);
				/*finish();
				System.exit(0);*/
			}
    				}
    					/*}
    					else{
    						
    						MBSUtils.showInvalidResponseAlert(OTPActivity.this);	
    					}*/
    				} catch (JSONException e) 
    				{
    					// TODO Auto-generated catch block
    					e.printStackTrace();
    				}
    				
		}
	}
	public void post_successlog(String retvalwbs)
	{
	  respcode="";
	      respdesc="";
	  finish();
		System.exit(0);
		
	}

	
	public void showAlert(final String str) 
	{
		ErrorDialogClass alert = new ErrorDialogClass(this, "" + str)
		{
			@Override
			public void onClick(View v)
			{
				switch (v.getId()) 
				{
					case R.id.btn_ok:
						if((str.equalsIgnoreCase(respdescvalidate)) &&  (respcode.equalsIgnoreCase("0")))
						{
							post_successvalidate(retval);
						}
						else if((str.equalsIgnoreCase(getString(R.string.alert_200))))
						{
							Intent in = new Intent(OTPActivity.this,SBKLoginActivity.class);
							in.putExtra("VAR1", var1);
							in.putExtra("VAR3", var3);
							startActivity(in);
							finish();
						}
						else if((str.equalsIgnoreCase(respdescvalidate)) && (respcode.equalsIgnoreCase("1")))
						{
							this.dismiss();
						}
						else if((str.equalsIgnoreCase(respdescresend)) &&  (respcode.equalsIgnoreCase("0")))
						{
							post_successresend(retval);
						}
						else if((str.equalsIgnoreCase(respdescresend)) && (respcode.equalsIgnoreCase("1")))
						{
							this.dismiss();
						}
						else if((str.equalsIgnoreCase(respdescsendcust)) &&  (respcode.equalsIgnoreCase("0")))
						{
							post_successsendcust(retval);
						}
						else if((str.equalsIgnoreCase(respdescsendcust)) && (respcode.equalsIgnoreCase("1")))
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
	
	public void showAlert1(String str) {
		// Toast.makeText(this, str, Toast.LENGTH_LONG).show();
		ErrorDialogClass1 alert = new ErrorDialogClass1(this, "" + str);
		alert.show();
	}
	
	public class ErrorDialogClass1 extends Dialog implements OnClickListener 
	{

		public ErrorDialogClass1(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
		}

		private Context activity;
		private Dialog d;
		private Button ok;
		private TextView txt_message; 
		public  String textMessage;
		public ErrorDialogClass1(Context activity,String textMessage) 
		{
			super(activity);		
			this.textMessage=textMessage;
		}
		
		@Override
		protected void onCreate(Bundle savedInstanceState) 
		{
			super.onCreate(savedInstanceState);
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			setCanceledOnTouchOutside(false);
			setContentView(R.layout.custom_dialog);		
			ok = (Button)findViewById(R.id.btn_ok);
			txt_message=(TextView)findViewById(R.id.txt_dia);
			txt_message.setText(textMessage);
			ok.setOnClickListener(this);		
		}//end onCreate

		@Override
		public void onClick(View v) 
		{
			switch (v.getId()) 
			{
				case R.id.btn_ok:
					if(strFromAct.equalsIgnoreCase("ENABLEATM"))
					{
						
						Intent in = new Intent(OTPActivity.this, NewDashboard.class);
						Bundle b1 = new Bundle();
						b1.putInt("FRAGINDEX",7);
						in.putExtras(b1);
						in.putExtra("VAR1", var1);
						in.putExtra("VAR3", var3);
						startActivity(in);
						finish();
					}
					else
					{
						Intent in = new Intent(OTPActivity.this,SBKLoginActivity.class);
						in.putExtra("VAR1", var1);
						in.putExtra("VAR3", var3);
						startActivity(in);
						finish();
					}
				  break;			
				default:
				  break;
			}
			dismiss();
		}
	}//end class
	
	public void onBackPressed() {
		/*Intent in = new Intent(this, SBKLoginActivity.class);
			startActivity(in);
			finish();*/
		
		if (strFromAct.equalsIgnoreCase("ENABLEATM")) {

			Intent in = new Intent(OTPActivity.this, NewDashboard.class);
			/*Bundle b1 = new Bundle();
			b1.putInt("FRAGINDEX", 7);
			in.putExtras(b1);*/
			in.putExtra("VAR1", var1);
			in.putExtra("VAR3", var3);
			startActivity(in);
			finish();
		} else if (strFromAct.equalsIgnoreCase("FORGOT")) {
			Intent in = new Intent(OTPActivity.this, ForgotPassword.class);
			in.putExtra("VAR1", var1);
			in.putExtra("VAR3", var3);
			in.putExtra("FROMACT", "OTPACTIVITY");
			startActivity(in);
			finish();
		}
		else
		{
			Intent in = new Intent(this, LoginActivity.class);
			in.putExtra("VAR1", var1);
			in.putExtra("VAR3", var3);
			startActivity(in);
			finish();
		}
	}

	class CallWebServiceValidateOTP extends AsyncTask<Void, Void, Void> 
	{
	    JSONObject jsonObj = new JSONObject();
		LoadProgressBar loadProBarObj = new LoadProgressBar(OTPActivity.this);
		boolean isWSCalled = false;
		String ValidationData="";

		@Override
		protected void onPreExecute() 
		{ 
			loadProBarObj.show();
            retval = "";
            respdescvalidate="";
            respcode="";
			
			strOTP = txt_otp.getText().toString().trim();
			strRefId=txt_ref_id.getText().toString().trim();
			strRefId=strRefId.substring(strRefId.indexOf(":")+1).trim();
					
	        try
			{
				jsonObj.put("CUSTID", strCustId);
				jsonObj.put("OTPVAL", strOTP);	
		        jsonObj.put("IMEINO", MBSUtils.getImeiNumber(OTPActivity.this));	
		        jsonObj.put("REFID", strRefId);	
		        jsonObj.put("ISREGISTRATION", "Y");
		        jsonObj.put("SIMNO", MBSUtils.getSimNumber(OTPActivity.this));   
		        jsonObj.put("METHODCODE","20"); 
		       // ValidationData=MBSUtils.getValidationData(OTPActivity.this,jsonObj.toString());
				Log.e("TAG", "onPreExecuteCallWebServiceValidateOTPACTVITY: "+jsonObj.toString() );
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
	
		};

		@Override
		protected Void doInBackground(Void... arg0) 
		{
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
				catch (Exception e) 
				{
					e.printStackTrace();
					retMess = getString(R.string.alert_000);
					System.out.println("Exception");
					cnt = 0;
				}
			} 
			catch (Exception e) 
			{
				retMess = getString(R.string.alert_000);
				System.out.println(e.getMessage());
				cnt = 0;
			}
			return null;
		}

		@Override
		protected void onPostExecute(final Void result) {
			loadProBarObj.dismiss();
			if (isWSCalled) 
			{
				
                JSONObject jsonObj;
				try
				{
	
					String str=CryptoClass.Function6(var5,var2);
					 jsonObj = new JSONObject(str.trim());
					/*ValidationData=xml_data[1].trim();
					if(ValidationData.equals(MBSUtils.getValidationData(OTPActivity.this, xml_data[0].trim())))
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
						retval = jsonObj.getString("RETVAL");
					}
					else
					{
						retval = "";
					}
					if (jsonObj.has("RESPDESC"))
					{
						respdescvalidate = jsonObj.getString("RESPDESC");
					}
					else
					{	
						respdescvalidate = "";
					}
					
				if(respdescvalidate.length()>0)
				{
					showAlert(respdescvalidate);
				}
				else{
					if (retval.indexOf("SUCCESS")>-1) 
					{				
						post_successvalidate(retval);
					} 
					else if(retval.indexOf("FAILED~MAXATTEMPT")>-1)
					{
							retMess = getString(R.string.alert_076_02);
							showAlert(retMess);
							
					}
					else
					{
							retMess = getString(R.string.alert_076);
							showAlert(retMess);
					}
				
			}
					/*}
					else{
						MBSUtils.showInvalidResponseAlert(OTPActivity.this);
					}*/
				} catch (JSONException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
			}
			else
			{
				showAlert(getString(R.string.alert_000));
			}
			
			
		}
	}

	public 	void post_successvalidate(String retval)
	{
		respdescvalidate="";
		respcode="";
		String decryptedAccounts = retval;//xml_data[0];
		
		if(strFromAct.equalsIgnoreCase("REGISTER"))
		{
			Bundle b1=new Bundle();
			Intent in = new Intent(OTPActivity.this,
			SecurityQuestion.class);
			b1.putString("CUSTID", strCustId);
			b1.putString("OTPVAL", strOTP);
			b1.putString("REFID", strRefId);
			b1.putString("FROMACT", strFromAct);
			b1.putString("MOBNO",strMobNo);
			in.putExtras(b1);
			in.putExtra("VAR1", var1);
			in.putExtra("VAR3", var3);
			startActivity(in);
			finish();
		}
		else if(strFromAct.equalsIgnoreCase("FORGOT"))
		{
			
			Bundle b1=new Bundle();
			Intent in = new Intent(OTPActivity.this,SetMPIN.class);
			//Intent in = new Intent(OTPActivity.this,LoginActivity.class);
			b1.putString("CUSTID", strCustId);
			b1.putString("OTPVAL", strOTP);
			b1.putString("REFID", strRefId);
			b1.putString("FROMACT", strFromAct);
			b1.putString("USERNAME",retval.split("~")[1]);
			
			in.putExtras(b1);
			in.putExtra("VAR1", var1);
			in.putExtra("VAR3", var3);
			startActivity(in);
			finish();
		}
		else if(strFromAct.equalsIgnoreCase("ENABLEATM"))
		{
			new CallWebServiceSaveATMCard().execute();
		}
		else if(strFromAct.equalsIgnoreCase("IMEIDIFF"))
		{
			flag = chkConnectivity();
			if (flag == 0) {
				CallWebServiceUpdateDiffIMEI difimei = new CallWebServiceUpdateDiffIMEI();
				difimei.execute();
			}
			/*Intent in = new Intent(OTPActivity.this,SBKLoginActivity.class);
			in.putExtra("VAR1", var1);
			in.putExtra("VAR3", var3);
			startActivity(in);
			finish();*/
		}
		else if(strFromAct.equalsIgnoreCase("GetCustID"))
		{
			CallWebServiceSendCustId s=new CallWebServiceSendCustId();
			s.execute();
		}
	}
	
	/*public 	void post_successresend(String retval)
	{
		
         respdescresend="";
     	respcode="";
		String decryptedAccounts = retval.split("~")[1];
		Bundle bObj=new Bundle();
		Intent in=new Intent(OTPActivity.this,OTPActivity.class);
		bObj.putString("RETVAL", decryptedAccounts);
		bObj.putString("CUSTID",strCustId);
		bObj.putString("MOBNO",strMobNo);
		bObj.putString("FROMACT", strFromAct);
		in.putExtras(bObj);
		startActivity(in);
		finish();
	}*/
	
	public 	void post_successresend(String retval)
	{
		respdescresend="";
     	respcode="";
		String decryptedAccounts = "";
		if(strFromAct.equals("ENABLEATM"))
			decryptedAccounts = retval.split("~")[1]+"!!"+strActno+"!!"+cardno+"!!"+stratm+"!!"+MBSUtils.getImeiNumber(this);
		else
			decryptedAccounts = retval.split("~")[1];
		Bundle bObj=new Bundle();
		Intent in=new Intent(OTPActivity.this,OTPActivity.class);
		bObj.putString("RETVAL", decryptedAccounts);
		bObj.putString("CUSTID",strCustId);
		bObj.putString("MOBNO",strMobNo);
		bObj.putString("FROMACT", strFromAct);
		in.putExtras(bObj);
		in.putExtra("VAR1", var1);
		in.putExtra("VAR3", var3);
		startActivity(in);
		finish();
	}
	
	public 	void post_successsendcust(String retval)
	{

         respdescsendcust="";
         respcode="";
		showAlert1(getString(R.string.alert_send_custID));
	}
	
	class CallWebService_resend_otp extends AsyncTask<Void, Void, Void>// CallWebService_resend_otp 
	{
		LoadProgressBar loadProBarObj = new LoadProgressBar(OTPActivity.this);
		boolean isWSCalled = false;
	    JSONObject jsonObj = new JSONObject();
	    String ValidationData="";

		@Override
		protected void onPreExecute() 
		{
			loadProBarObj.show();
			retval = "";
            respdescresend="";
            respcode="";
			if(strFromAct.equalsIgnoreCase("REGISTER"))
			{
				try
				{
					jsonObj.put("CUSTID", strCustId);
					jsonObj.put("REQSTATUS","R");
					jsonObj.put("REQFROM", "MBSREG");
					jsonObj.put("MOBNO", strMobNo);
					jsonObj.put("SIMNO", MBSUtils.getSimNumber(OTPActivity.this));
					jsonObj.put("IMEINO", MBSUtils.getImeiNumber(OTPActivity.this));
					jsonObj.put("METHODCODE","27");
					// ValidationData=MBSUtils.getValidationData(OTPActivity.this,jsonObj.toString());
					
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
			else
			{
				try
				{
					jsonObj.put("CUSTID", strCustId);
					jsonObj.put("REQSTATUS","F");
					jsonObj.put("REQFROM", "MBSREG");
					jsonObj.put("SIMNO", MBSUtils.getSimNumber(OTPActivity.this));
					jsonObj.put("METHODCODE","26");
					// ValidationData=MBSUtils.getValidationData(OTPActivity.this,jsonObj.toString());
					
	            }
				catch(Exception e)
				{
					e.printStackTrace();
				}
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
				}  catch (Exception e) {
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
				// return "FAILED";
			}
			return null;

		}

		@Override
		protected void onPostExecute(final Void result) {
			loadProBarObj.dismiss();
			if (isWSCalled) 
			{
			JSONObject jsonObj;
                				try
                				{
                	
                					String str=CryptoClass.Function6(var5,var2);
                					 jsonObj = new JSONObject(str.trim());
                					/*ValidationData=xml_data[1].trim();
                					if(ValidationData.equals(MBSUtils.getValidationData(OTPActivity.this, xml_data[0].trim())))
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
                						retval = jsonObj.getString("RETVAL");
                					}
                					else
                					{
                						retval = "";
                					}
                					if (jsonObj.has("RESPDESC"))
                					{
                						respdescresend = jsonObj.getString("RESPDESC");
                					}
                					else
                					{	
                						respdescresend = "";
                					}
                					
                				if(respdescresend.length()>0)
                				{
                					showAlert(respdescresend);
                				}
                				else{
				
								if(retval.split("~")[0].indexOf("SUCCESS")>-1)
								{
					                              post_successresend(retval);
								
								} 
								else 
								{
									//System.out.println("in else ***************************************");
									retMess = getString(R.string.alert_094);
									showAlert(retMess);
								}}
				                				
                					/*}
                					else{
                						MBSUtils.showInvalidResponseAlert(OTPActivity.this);	
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
	
	public int chkConnectivity() {
		//Log.i("1111", "1111");
		// p_wait.setVisibility(ProgressBar.VISIBLE);
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		//Log.i("2222", "2222");
		try {
			State state = ni.getState();
			//Log.i("3333", "3333");
			boolean state1 = ni.isAvailable();
			//Log.i("4444", "4444");
			//System.out.println("state1 ---------" + state1);
			if (state1) {
				switch (state) {
				case CONNECTED:

					//Log.i("5555", "5555");
					if (ni.getType() == ConnectivityManager.TYPE_MOBILE
							|| ni.getType() == ConnectivityManager.TYPE_WIFI) {

						gpsFlg = 1;
						flag = 0;

					}
					break;
				case DISCONNECTED:
					//Log.i("6666", "6666");
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
					//Log.i("7777", "7777");
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
				//Log.i("8888", "8888");
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
									SBKLoginActivity.class);
							in.putExtra("VAR1", var1);
							in.putExtra("VAR3", var3);
							startActivity(in);
							finish();
						}
					});
			dbs.get_adb().show();

		} catch (Exception e) {
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
	
	class CallWebServiceSendCustId extends AsyncTask<Void, Void, Void>  
	{
		LoadProgressBar loadProBarObj = new LoadProgressBar(OTPActivity.this);
	    JSONObject jsonObj = new JSONObject();
	     boolean isWSCalled = false;
	     String ValidationData="";

		@Override
		protected void onPreExecute() 
		{			
			loadProBarObj.show();
                         retval = "";
                         respdescsendcust="";
                         respcode="";
			strOTP = txt_otp.getText().toString().trim();
			strRefId=txt_ref_id.getText().toString().trim();
			strRefId=strRefId.substring(strRefId.indexOf(":")+1).trim();
					
				try 
				{					
					jsonObj.put("CUSTOMERID", strCustId);
					jsonObj.put("IMEINO", MBSUtils.getImeiNumber(OTPActivity.this));
					jsonObj.put("OTPVAL", strOTP);
					jsonObj.put("REFNO", strRefId);	
					jsonObj.put("SIMNO", MBSUtils.getSimNumber(OTPActivity.this));
					jsonObj.put("METHODCODE","51");
					//ValidationData=MBSUtils.getValidationData(OTPActivity.this,jsonObj.toString());
				} 
				catch (JSONException e1) 
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
		
             };
             @Override
		protected Void doInBackground(Void... arg0) 
		{
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
		}  catch (Exception e) {
					e.printStackTrace();
					retMess = getString(R.string.alert_000);
					System.out.println("Exception");
					cnt = 0;
					// return "FAILED";
			}
			}catch (Exception e) {
                           retMess = getString(R.string.alert_000);
				System.out.println(e.getMessage());
				cnt = 0;
		
			}
			return null;
		}

		@Override
		protected void onPostExecute(final Void result)  
		{
				loadProBarObj.dismiss();
			JSONObject jsonObj;
			try 
			{
				String str=CryptoClass.Function6(var5,var2);
				 jsonObj = new JSONObject(str.trim());
                 /*  ValidationData=xml_data[1].trim();
               	if(ValidationData.equals(MBSUtils.getValidationData(OTPActivity.this, xml_data[0].trim())))
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
				retval = jsonObj.getString("RETVAL");
			}
			else
			{
				retval = "";
			}
			if (jsonObj.has("RESPDESC"))
			{
				respdescsendcust = jsonObj.getString("RESPDESC");
			}
			else
			{	
				respdescsendcust = "";
			}
              
			if(respdescsendcust.length()>0)
			{
				showAlert(respdescsendcust);
			}
			else{
				if (!retval.equalsIgnoreCase("NULL")) 
				{
				
					if (respcode.equals("0")) 
					{
						post_successsendcust(retval);
						
					} 
					else if(!respcode.equals("0"))
					{
						showAlert(getString(R.string.alert_invalid_otp));
					}
					else 
					{
						if (!retval.equalsIgnoreCase("NULL")) 
						{
							String RESPREASON = retval;//json.getString("RETVAL");
							int pos=Integer.parseInt(respcode);
							String errmsg=presidents[pos];
							Log.e("IN getCustId",errmsg );
							showAlert("" + errmsg);
						}
						else 
						{
							showAlert(getString(R.string.alert_network_problem_pease_try_again));
						}
					}
				} 
				else 
				{
					System.out.println("in else");
					showAlert(getString(R.string.alert_network_problem_pease_try_again));
				}}
             	/*}
               	else{
               		MBSUtils.showInvalidResponseAlert(OTPActivity.this);	
               	}*/
		} catch (JSONException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
			
		}// onPostExecute
	}
	
	public class CallWebServiceSaveATMCard extends AsyncTask<Void, Void, Void>{
		String atmstatus="";
		
		JSONObject jsonobj = new JSONObject();
		LoadProgressBar loadProBarObj = new LoadProgressBar(OTPActivity.this);
		String ValidationData="";
		
		@Override
		protected void onPreExecute() {
			
			loadProBarObj.show();
			stratm="";retval="";
			if(catdstatus.equals("T"))
			{stratm="Disabled";}
			else
			{stratm="Enabled";}
			
		
			try {
				jsonobj.put("CUSTID", strCustId);
				jsonobj.put("ACCNO", strActno);
				jsonobj.put("CARDNO", cardno);
				jsonobj.put("IMEINO", strimeino);
				jsonobj.put("CARDSTATUS", catdstatus);
				jsonobj.put("SIMNO", MBSUtils.getSimNumber(OTPActivity.this));
				jsonobj.put("METHODCODE","63");
				// ValidationData=MBSUtils.getValidationData(OTPActivity.this,jsonobj.toString());
				
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
			if(ValidationData.equals(MBSUtils.getValidationData(OTPActivity.this, xml_data[0].trim())))
			{*/
			 Log.e("DSP","stratmmm....."+str);
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
				retval = jsonObj.getString("RETVAL");
			}
			else
			{
				retval = "";
			}
			if (jsonObj.has("RESPDESC"))
			{
				respdesc_SaveATMCard = jsonObj.getString("RESPDESC");
			}
			else
			{	
				respdesc_SaveATMCard = "";
			}
			
		if(respdesc_SaveATMCard.length()>0)
		{
			showAlert(respdesc_SaveATMCard);
		}
		else{
			 loadProBarObj.dismiss();
        if (retval.indexOf("SUCCESS~") > -1) {
        	Log.e("Sudarshan","SaveATMCard=="+retval);
        	post_SaveATMCard(retval);
        	}
        
       
		}/*}
			else{
				MBSUtils.showInvalidResponseAlert(OTPActivity.this);	
			}*/
		} catch (JSONException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
}// onPostExecute
	}	
	
	public void post_SaveATMCard(String retval){
		respcode="";respdesc_SaveATMCard = "";
		Log.e("Sudarshan","post_SaveATMCard=="+retval);
		String values[] = retval.split("~")[1].split("!!");
        String refn=values[0];
        showAlert1("Your ATM Card Is "+stratm+" Successfully With Request Id "+refn);
	}
	
	class CallWebServiceValidateOTPDiffIMEI extends AsyncTask<Void, Void, Void> 
	{
	    JSONObject jsonObj = new JSONObject();
		LoadProgressBar loadProBarObj = new LoadProgressBar(OTPActivity.this);
		boolean isWSCalled = false;
		String ValidationData="";

		@Override
		protected void onPreExecute() 
		{ 
			loadProBarObj.show();
            retval = "";
            respdescvalidate="";
            respcode="";
			
			strOTP = txt_otp.getText().toString().trim();
			strRefId=txt_ref_id.getText().toString().trim();
			strRefId=strRefId.substring(strRefId.indexOf(":")+1).trim();
			
	        try
			{
				jsonObj.put("CUSTID", strCustId);
				jsonObj.put("FROMACT", "DIFFIMEI");
				jsonObj.put("ISREGISTRATION", "N");
				jsonObj.put("OTPVAL", strOTP);	
		        jsonObj.put("IMEINO", MBSUtils.getImeiNumber(OTPActivity.this));	
		        jsonObj.put("REFID", strRefId);	
		        jsonObj.put("SIMNO", MBSUtils.getSimNumber(OTPActivity.this)); 
		        jsonObj.put("METHODCODE","20");
		     //   ValidationData=MBSUtils.getValidationData(OTPActivity.this,jsonObj.toString());
				Log.e("TAG", "CallWebServiceValidateOTPdifferent: "+jsonObj.toString() );
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
	
		};

		@Override
		protected Void doInBackground(Void... arg0) 
		{
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
				catch (Exception e) 
				{
					e.printStackTrace();
					retMess = getString(R.string.alert_000);
					System.out.println("Exception");
					cnt = 0;
				}
			} 
			catch (Exception e) 
			{
				retMess = getString(R.string.alert_000);
				System.out.println(e.getMessage());
				cnt = 0;
			}
			return null;
		}

		@Override
		protected void onPostExecute(final Void result) 
		{
			loadProBarObj.dismiss();
			if (isWSCalled) 
			{
			    JSONObject jsonObj;
				try
				{
					String str=CryptoClass.Function6(var5,var2);
					 jsonObj = new JSONObject(str.trim());
					/*ValidationData=xml_data[1].trim();
					if(ValidationData.equals(MBSUtils.getValidationData(OTPActivity.this, xml_data[0].trim())))
					{*/
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
						retval = jsonObj.getString("RETVAL");
					}
					else
					{
						retval = "";
					}
					if (jsonObj.has("RESPDESC"))
					{
						respdescvalidate = jsonObj.getString("RESPDESC");
					}
					else
					{	
						respdescvalidate = "";
					}
					
				if(respdescvalidate.length()>0)
				{
					showAlert(respdescvalidate);
				}
				else
				{
					if (retval.indexOf("SUCCESS")>-1) 
					{				
						post_successvalidate(retval);
					} 
					else 
					{
						showAlert(getString(R.string.alert_076));
					}
				}
				
					/*}
					else{
						MBSUtils.showInvalidResponseAlert(OTPActivity.this);	
					}*/
				} 
				catch (JSONException e) 
				{
					e.printStackTrace();
				}
			}
			else
			{
				showAlert(getString(R.string.alert_000));
			}
		}
	}
	class CallWebServiceUpdateDiffIMEI extends AsyncTask<Void, Void, Void>
	{
		JSONObject jsonObj = new JSONObject();
		LoadProgressBar loadProBarObj = new LoadProgressBar(OTPActivity.this);
		boolean isWSCalled = false;

		@Override
		protected void onPreExecute()
		{
			loadProBarObj.show();
			retval = "";
			respdescvalidate="";
			respcode="";
			try
			{
				jsonObj.put("CUSTID", strCustId);
				jsonObj.put("FROMACT", "DIFFIMEI");
				jsonObj.put("IMEINO", MBSUtils.getImeiNumber(OTPActivity.this));
				jsonObj.put("SIMNO", MBSUtils.getSimNumber(OTPActivity.this));
				jsonObj.put("METHODCODE","86");
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}

		};

		@Override
		protected Void doInBackground(Void... arg0)
		{
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
				catch (Exception e)
				{
					e.printStackTrace();
					retMess = getString(R.string.alert_000);
					System.out.println("Exception");
					cnt = 0;
				}
			}
			catch (Exception e)
			{
				retMess = getString(R.string.alert_000);
				System.out.println(e.getMessage());
				cnt = 0;
			}
			return null;
		}

		@Override
		protected void onPostExecute(final Void result)
		{
			loadProBarObj.dismiss();
			if (isWSCalled)
			{
				JSONObject jsonObj;
				try
				{
					String str=CryptoClass.Function6(var5,var2);
					Log.e("Sud---","str-"+str);
					jsonObj = new JSONObject(str.trim());

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
						retval = jsonObj.getString("RETVAL");
					}
					else
					{
						retval = "";
					}
					if (jsonObj.has("RESPDESC"))
					{
						respdescvalidate = jsonObj.getString("RESPDESC");
					}
					else
					{
						respdescvalidate = "";
					}

					if(respdescvalidate.length()>0)
					{
						showAlert(respdescvalidate);
					}
					else
					{
						if (retval.indexOf("SUCCESS")>-1)
						{
							showAlert(getString(R.string.alert_200));

						}
						else
						{
							showAlert(getString(R.string.alert_076));
						}
					}
				}
				catch (JSONException e)
				{
					e.printStackTrace();
				}
			}
			else
			{
				showAlert(getString(R.string.alert_000));
			}
		}
	}

}

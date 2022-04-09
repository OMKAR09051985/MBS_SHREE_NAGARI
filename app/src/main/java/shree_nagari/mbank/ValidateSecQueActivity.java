package shree_nagari.mbank;

import java.security.PrivateKey;

import javax.crypto.spec.SecretKeySpec;

import mbLib.CryptoClass;
import mbLib.MBSUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class ValidateSecQueActivity extends CustomWindow implements
		OnClickListener, LocationListener {

	private static final String MY_SESSION = "my_session";
	Button subButton;
	Button canButton;
	TextView queText, txt_heading;
	EditText sEditText;
	ValidateSecQueActivity valSecAct;
	String custId = null, mpin = null, retVal = null, retMess = null,
			que_one = null, ans_one = null, qOne = null,custCd=null;
	int cnt, flag = 0;
	String[] ques;
	JSONArray jsonArr;
	String respcode="",retval="",respdesc="",respdescval="",strfromact="";
	private static String NAMESPACE = "";
	private static String URL = "";
	private static String SOAP_ACTION = "";
	private static  String METHOD_NAME = "";
	private static  String METHOD_NAME1 = "";
	ImageView img_heading;
	ImageView btn_home1,btn_logout;
	PrivateKey var1=null;	  
	String var5="",var3="";
	SecretKeySpec var2=null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		getWindow().setFlags(LayoutParams.FLAG_SECURE, LayoutParams.FLAG_SECURE);
		setContentView(R.layout.validsecque);
		img_heading = (ImageView) findViewById(R.id.img_heading);
		img_heading.setBackgroundResource(R.mipmap.newlogofordash);
		subButton = (Button) findViewById(R.id.btn_submit_secu_que);
		subButton.setOnClickListener(this);
		queText = (TextView) findViewById(R.id.txt_security_que1);
		sEditText = (EditText) findViewById(R.id.edttxt_security_que1);
		txt_heading = (TextView) findViewById(R.id.txt_heading);
		txt_heading.setText(getString(R.string.lbl_security_que));
		btn_home1 = (ImageView) findViewById(R.id.btn_home1);
		btn_logout = (ImageView) findViewById(R.id.btn_logout);
		
		btn_home1.setOnClickListener(this);
		btn_logout.setOnClickListener(this);
		var1 = (PrivateKey)getIntent().getSerializableExtra("VAR1");
		var3 = (String)getIntent().getSerializableExtra("VAR3");
		Bundle b1 = this.getIntent().getExtras();

		if (b1 != null) {
			strfromact=b1.getString("fromAct");
			custId = b1.getString("custId");
			mpin = b1.getString("mpin");
		} else
			custId = "0000000000";

		// INSTANTIATE SHARED PREFERENCES CLASS
		SharedPreferences sp = getSharedPreferences(MY_SESSION,
				Context.MODE_PRIVATE);
		// LOAD THE EDITOR REMEMBER TO COMMIT CHANGES!
		// cntx = sp.edit();
		if(strfromact.equalsIgnoreCase("DIFFIMEI"))
		{
			btn_home1.setVisibility(View.INVISIBLE);
			btn_home1.setOnClickListener(null);
			btn_logout.setVisibility(View.INVISIBLE);
		}
		
		flag = chkConnectivity();
		if (flag == 0) {
			CallWebServiceFetchSecuQue c = new CallWebServiceFetchSecuQue();
			c.execute();
		}
	}

	@Override
	public void onLocationChanged(Location arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_submit_secu_que:
			ans_one = sEditText.getText().toString().trim();

			if (ans_one == null || ans_one.length() == 0) {
				showAlert(getString(R.string.alert_079));
			} 
			else 
			{
				flag = chkConnectivity();
				if (flag == 0) 
				{
					CallWebServiceValidateSecuQue c = new CallWebServiceValidateSecuQue();
					c.execute();
				}
			}
			break;
		default:
			break;
		}
	}

	class CallWebServiceFetchSecuQue extends AsyncTask<Void, Void, Void> {

		LoadProgressBar loadProBarObj = new LoadProgressBar(
				ValidateSecQueActivity.this);
		boolean isWSCalled = false;
		String ValidationData="";
		JSONObject obj = new JSONObject();
		
		@Override
		protected void onPreExecute() {
			loadProBarObj.show();
			respcode="";
			retval="";
			respdesc="";
			custId = ValidateSecQueActivity.this.custId;
			
			try {
				obj.put("CUSTID", custId);
				obj.put("IMEINO",
						MBSUtils.getImeiNumber(ValidateSecQueActivity.this));
				obj.put("SIMNO", MBSUtils.getSimNumber(ValidateSecQueActivity.this));
				obj.put("METHODCODE","25");
				// ValidationData=MBSUtils.getValidationData(ValidateSecQueActivity.this,obj.toString());
			
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
				}  catch (Exception e) {
					e.printStackTrace();
					retMess = getString(R.string.alert_000);
					System.out.println("Exception");
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
				try
				{
					String str=CryptoClass.Function6(var5,var2);
					 jsonObj = new JSONObject(str.trim());
					/*ValidationData=xml_data[1].trim();
					if(ValidationData.equals(MBSUtils.getValidationData(ValidateSecQueActivity.this, xml_data[0].trim())))
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
					post_success(retval);
				
				}
				
				/*	}
					else{
						MBSUtils.showInvalidResponseAlert(ValidateSecQueActivity.this);	
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

	}
	
	public 	void post_success(String retval){
		int count = 0;
		respcode="";
		respdesc="";
		JSONArray ja =null;
		int j = 0;
		try {
			if (retval.indexOf("FAILED") > -1) {
				showAlert(getString(R.string.alert_err));
			} else {
			 ja = new JSONArray(retval);
				jsonArr = ja;
				ques = new String[ja.length()];
				for (; j < ja.length(); j++) {
					JSONObject jObj = ja.getJSONObject(j);
					ques[j] = (jObj.getString("QUEDESC"));
					count++;
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			try {
				JSONObject jObj = ja.getJSONObject(j);
				custCd = jObj.getString("QUECD");
			      } catch (JSONException je) {
				// TODO Auto-generated catch block
			    	  e.printStackTrace();
			}
		}
		if (count > 0) {
			double y = Math.random();
			int que_num = (int) (y * 100) % 2;
			queText.setText(ques[que_num]);
		}
	}
	
	class CallWebServiceValidateSecuQue extends AsyncTask<Void, Void, Void> {

		LoadProgressBar loadProBarObj = new LoadProgressBar(ValidateSecQueActivity.this);
		boolean isWSCalled = false;
		String ValidationData="";
		JSONObject newobj = new JSONObject();
		
		@Override
		protected void onPreExecute() {
			loadProBarObj.show();
			respcode="";
			retval="";
			respdescval="";
			custId = ValidateSecQueActivity.this.custId;
			que_one = queText.getText().toString().trim();
			ans_one = sEditText.getText().toString().trim();
			try {
				for (int k = 0; k < jsonArr.length(); k++) {
					JSONObject obj = jsonArr.getJSONObject(k);
					if (obj.getString("QUEDESC").equalsIgnoreCase(que_one))
						qOne = obj.getString("QUECD");
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			try {

				newobj.put("CUSTID", custCd);
				newobj.put("QUE", qOne);
				newobj.put("ANS", ans_one);
				newobj.put("IMEINO",MBSUtils.getImeiNumber(ValidateSecQueActivity.this));
				newobj.put("SIMNO", MBSUtils.getSimNumber(ValidateSecQueActivity.this));
				newobj.put("METHODCODE","23");
			//	ValidationData=MBSUtils.getValidationData(ValidateSecQueActivity.this,newobj.toString());			
			
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
										
				request.addProperty("value1", CryptoClass.Function5(newobj.toString(), var2));
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
				try
				{
					String str=CryptoClass.Function6(var5,var2);
					 jsonObj = new JSONObject(str.trim());
					 Log.e("suddd---","jsonObj-"+jsonObj);
					/*ValidationData=xml_data[1].trim();
					if(ValidationData.equals(MBSUtils.getValidationData(ValidateSecQueActivity.this, xml_data[0].trim())))
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
						respdescval = jsonObj.getString("RESPDESC");
					}
					else
					{	
						respdescval = "";
					}
					
				if(respdescval.length()>0)
				{
					showAlert(respdescval);
				}
				else{
				if (retval.indexOf("SUCCESS") > -1) {
					post_successValidateSecuQue(retval);
					
				} else {
					if (retval.indexOf("WRONGANS") >= 0) {
						retMess = getString(R.string.alert_087);
					} else {
						retMess = getString(R.string.alert_err);
					}
					showAlert(retMess);
				}
				}
					/*}
					else{
						MBSUtils.showInvalidResponseAlert(ValidateSecQueActivity.this);	
					}*/
				} catch (JSONException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			} else {
				retMess = getString(R.string.alert_err);
				showAlert(retMess);
			}
		}
	}
	
	public 	void post_successValidateSecuQue(String retval)
	{
		respcode="";
		String decryptedAccounts = retval.split("~")[1];
		Bundle bObj=new Bundle();
		Intent in=new Intent(ValidateSecQueActivity.this,OTPActivity.class);
		bObj.putString("RETVAL", decryptedAccounts);
		bObj.putString("CUSTID",custCd);
		bObj.putString("FROMACT", "IMEIDIFF");
		in.putExtras(bObj);
		in.putExtra("VAR1", var1);
		in.putExtra("VAR3", var3);
		startActivity(in);
		
		finish();
	}
	
	public void showAlert(final String str) {
		ErrorDialogClass alert = new ErrorDialogClass(this, "" + str)
		{
			@Override
			public void onClick(View v)
			{
				switch (v.getId()) 
				{
					case R.id.btn_ok:
						if((str.equalsIgnoreCase(respdesc)) &&  (respcode.equalsIgnoreCase("0")))
						{
							post_success(retval);
						}
						else if((str.equalsIgnoreCase(respdesc)) && (respcode.equalsIgnoreCase("1")))
						{
							this.dismiss();
						}
						else if((str.equalsIgnoreCase(respdescval)) &&  (respcode.equalsIgnoreCase("0")))
						{
							post_successValidateSecuQue(retval);
						}
						else if((str.equalsIgnoreCase(respdescval)) && (respcode.equalsIgnoreCase("1")))
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

	public int chkConnectivity() {
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
					}
					break;
				case DISCONNECTED:
					flag = 1;
					retMess = getString(R.string.alert_014);
					showAlert(retMess);
					break;
				default:
					flag = 1;
					retMess = getString(R.string.alert_000);
					showAlert(retMess);
					break;
				}
			} else {
				flag = 1;
				retMess = getString(R.string.alert_000);
				showAlert(retMess);
			}
		} catch (NullPointerException ne) {
			flag = 1;
			retMess = getString(R.string.alert_000);
			showAlert(retMess);
		} catch (Exception e) {
			flag = 1;
			retMess = getString(R.string.alert_000);
			showAlert(retMess);
		}
		return flag;
	}
}

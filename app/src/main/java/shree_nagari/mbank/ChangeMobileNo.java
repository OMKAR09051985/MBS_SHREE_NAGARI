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

import shree_nagari.mbank.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import android.widget.TextView;

public class ChangeMobileNo extends Activity implements OnClickListener {
	Button btn_chng_mobno;
	// ImageButton btn_back;
	TextView txt_security_que, txt_heading;
	EditText txt_mpin, txt_new_mobno, txt_old_mobno, txt_cust_id, txt_ans;
	PrivateKey var1=null;	  
	String var5="",var3="";
	SecretKeySpec var2=null;


	String retVal = "", retMess = "", custId = "", newMobno = "",
			oldMobno = "", ans = "", que = "", mpin = "", qOne = "",
			que_one = "",respcode="",retvalweb="",respdescGetSecurityQue="",respdescChangeMobNo="";
	int cnt = 0, flag = 0;
	String[] ques;
	JSONArray jsonArr;
	int countseq = 0;
	boolean WSCalled = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(LayoutParams.FLAG_SECURE, LayoutParams.FLAG_SECURE);
		setContentView(R.layout.change_mobile_no);
		var1 = (PrivateKey)getIntent().getSerializableExtra("VAR1");
		var3 = (String)getIntent().getSerializableExtra("VAR3");
		Bundle b1 = new Bundle();
		b1 = getIntent().getExtras();
		if (b1 != null) {
			custId = b1.getString("custId");
		}
		txt_heading = (TextView) findViewById(R.id.txt_heading);
		txt_security_que = (TextView) findViewById(R.id.txt_security_que);
		txt_mpin = (EditText) findViewById(R.id.txt_mpin);
		txt_new_mobno = (EditText) findViewById(R.id.txt_new_mobno);
		txt_old_mobno = (EditText) findViewById(R.id.txt_old_mobno);
		txt_cust_id = (EditText) findViewById(R.id.txt_cust_id);
		txt_ans = (EditText) findViewById(R.id.txt_ans);

		btn_chng_mobno = (Button) findViewById(R.id.btn_chng_mobno);
		/* btn_back=(ImageButton)findViewById(R.id.btn_back); */
		// btn_back.setImageResource(R.drawable.backover);
		btn_chng_mobno.setOnClickListener(this);
		// btn_back.setOnClickListener(this);
		txt_heading.setText(getString(R.string.lbl_reg_mobno));

		flag = chkConnectivity();
		if (flag == 0) {
			CallWebServiceGetSecurityQue c = new CallWebServiceGetSecurityQue();
			c.execute();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_chng_mobno:
			custId = txt_cust_id.getText().toString().trim();
			newMobno = txt_new_mobno.getText().toString().trim();
			oldMobno = txt_old_mobno.getText().toString().trim();
			ans = txt_ans.getText().toString().trim();
			mpin = txt_mpin.getText().toString().trim();

			if (custId.trim().length() != 10)
				showAlert(getString(R.string.alert_072));
			else if (oldMobno.trim().length() != 10)
				showAlert(getString(R.string.alert_046));
			else if (newMobno.trim().length() != 10)
				showAlert(getString(R.string.alert_006));
			else if (ans.trim().length() == 0)
				showAlert(getString(R.string.alert_079));
			else if (mpin.trim().length() == 0)
				showAlert(getString(R.string.alert_082));
			else if (mpin.trim().length() != 6)
				showAlert(getString(R.string.alert_082));
			else {
				flag = chkConnectivity();
				if (flag == 0) {
					CallWebServiceChangeMobNo c = new CallWebServiceChangeMobNo();
					c.execute();
				}
			}
			break;
		/*
		 * case R.id.btn_back: Intent in=new Intent(this,LoginActivity.class);
		 * startActivity(in); finish(); break;
		 */
		default:
			break;
		}
	}

	class CallWebServiceGetSecurityQue extends AsyncTask<Void, Void, Void> {
		JSONObject jsonObj = new JSONObject();
		LoadProgressBar loadProBarObj = new LoadProgressBar(ChangeMobileNo.this);
		boolean isWSCalled = false;
		String ValidationData="";

		@Override
		protected void onPreExecute() {
			try {
				loadProBarObj.show();
				respcode="";
				retvalweb="";
				respdescGetSecurityQue="";
				jsonObj.put("CUSTID", custId);
				jsonObj.put("IMEINO",
						MBSUtils.getImeiNumber(ChangeMobileNo.this));
				jsonObj.put("SIMNO", MBSUtils.getSimNumber(ChangeMobileNo.this));
				jsonObj.put("METHODCODE","25");
				//ValidationData=MBSUtils.getValidationData(ChangeMobileNo.this,jsonObj.toString());
				
			} catch (JSONException je) {
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
				}  catch (Exception e) {
					e.printStackTrace();
					Log.e("In Login", "----------" + e);
					retMess = getString(R.string.alert_000);
					// System.out.println("Exception");
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
					if(ValidationData.equals(MBSUtils.getValidationData(ChangeMobileNo.this, xml_data[0].trim())))
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
						respdescGetSecurityQue = jsonObj.getString("RESPDESC");
					}
					else
					{	
						respdescGetSecurityQue = "";
					}
					
				if(respdescGetSecurityQue.length()>0)
				{
					showAlert(respdescGetSecurityQue);
				}
				else{
			
					
					if (retvalweb.indexOf("FAILED") > -1) {
						showAlert(getString(R.string.alert_err));
					} else {
						post_successGetSecurityQue(retvalweb);
					}
				
				if (countseq > 0) {
					// Log.e("HERE","===="+retVal);

					double y = Math.random();
					int que_num = (int) (y * 100) % 2;
					txt_security_que.setText(ques[que_num]);
				}

			}
				/*	}
					else{
						MBSUtils.showInvalidResponseAlert(ChangeMobileNo.this);	
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
	public 	void post_successGetSecurityQue(String retvalweb)
	{
		try {
			respcode="";
			
			respdescGetSecurityQue="";
		JSONArray ja = new JSONArray(retvalweb);
		jsonArr = ja;
		ques = new String[ja.length()];
		for (int j = 0; j < ja.length(); j++) {
			JSONObject jObj = ja.getJSONObject(j);
			ques[j] = (jObj.getString("QUEDESC"));
			countseq++;
		}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}

	class CallWebServiceChangeMobNo extends AsyncTask<Void, Void, Void> {
		JSONObject jsonObj = new JSONObject();
		LoadProgressBar loadProBarObj = new LoadProgressBar(ChangeMobileNo.this);
		boolean isWSCalled = false;
		String ValidationData="";

		@Override
		protected void onPreExecute() {
			loadProBarObj.show();
			respcode="";
			retvalweb="";
			respdescChangeMobNo="";
			custId = txt_cust_id.getText().toString().trim();
			newMobno = txt_new_mobno.getText().toString().trim();
			oldMobno = txt_old_mobno.getText().toString().trim();
			ans = txt_ans.getText().toString().trim();
			mpin = txt_mpin.getText().toString().trim();
			que_one = txt_security_que.getText().toString();
			try {
				jsonObj.put("CUSTID", custId);
				jsonObj.put("OLDMOB", oldMobno);
				jsonObj.put("NEWMOB", newMobno);
				jsonObj.put("MPIN", mpin);
				jsonObj.put("QUE", qOne);
				jsonObj.put("ANS",ans);
				jsonObj.put("IMEINO",
						MBSUtils.getImeiNumber(ChangeMobileNo.this));
				jsonObj.put("SIMNO", MBSUtils.getSimNumber(ChangeMobileNo.this));
				jsonObj.put("METHODCODE","30");
			//	ValidationData=MBSUtils.getValidationData(ChangeMobileNo.this,jsonObj.toString());
				for (int k = 0; k < jsonArr.length(); k++) {
					JSONObject obj = jsonArr.getJSONObject(k);
					Log.e("",
							"obj.getString(QUEDESC)=="
									+ obj.getString("QUEDESC"));
					if (obj.getString("QUEDESC").equalsIgnoreCase(que_one)) {
						qOne = obj.getString("QUECD");
					
					}
				}
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
					retMess = getString(R.string.alert_000);
					// System.out.println("Exception");
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
					if(ValidationData.equals(MBSUtils.getValidationData(ChangeMobileNo.this, xml_data[0].trim())))
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
						respdescChangeMobNo = jsonObj.getString("RESPDESC");
					}
					else
					{	
						respdescChangeMobNo = "";
					}
					
				if(respdescChangeMobNo.length()>0)
				{
					showAlert(respdescChangeMobNo);
				}
				else{
				if (retvalweb.indexOf("FAILED") > -1) {
					String msg[] = retvalweb.split("~");
					if (msg[1].equals("1"))
						retMess = getString(R.string.alert_err);
					else if (msg[1].equals("2"))
						retMess = getString(R.string.alert_089);
					else if (msg[1].equals("3"))
						retMess = getString(R.string.alert_090);
					else if (msg[1].equals("4"))
						retMess = getString(R.string.alert_100);
					else if (msg[1].equals("5"))
						retMess = getString(R.string.alert_101);
					else if (msg[1].equals("6"))
						retMess = getString(R.string.alert_102);

					showAlert(retMess);
				} else {
					post_successChangeMobNo(retvalweb);
				}}
                  /*}else{
						
						MBSUtils.showInvalidResponseAlert(ChangeMobileNo.this);	
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
	public 	void post_successChangeMobNo(String retvalweb)
	{
		respcode="";
		
		respdescChangeMobNo="";
		WSCalled = true;
		retMess = getString(R.string.alert_099);
		showAlert(retMess);
		/*
		 * Intent in=new
		 * Intent(ChangeMobileNo.this,LoginActivity.class);
		 * startActivity(in); finish();
		 */

	
	}

	public void showAlert(final String str) {
		ErrorDialogClass alert = new ErrorDialogClass(this, "" + str) {
			@Override
			public void onClick(View v) {
				// Log.e("SetMPIN","SetMPIN...btn CLicked=="+R.id.btn_ok+"==="+v.getId());
				switch (v.getId()) {
				case R.id.btn_ok:
					// Log.e("SetMPIN","SetMPIN...CASE trru="+WSCalled);
					if((str.equalsIgnoreCase(respdescGetSecurityQue)) &&  (respcode.equalsIgnoreCase("0")))
					{
						post_successGetSecurityQue(retvalweb);
					}
					else if((str.equalsIgnoreCase(respdescGetSecurityQue)) && (respcode.equalsIgnoreCase("1")))
					{
						this.dismiss();
					}
					else if((str.equalsIgnoreCase(respdescChangeMobNo)) &&  (respcode.equalsIgnoreCase("0")))
					{
						post_successChangeMobNo(retvalweb);
					}
					else if((str.equalsIgnoreCase(respdescChangeMobNo)) && (respcode.equalsIgnoreCase("1")))
					{
						this.dismiss();
					}
					else if (WSCalled) {
						
						// Log.e("SetMPIN","SetMPIN...mpin set");
						Intent in = new Intent(ChangeMobileNo.this,
								LoginActivity.class);
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
		};
		alert.show();
	}

	public int chkConnectivity() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		try {
			State state = ni.getState();
			boolean state1 = ni.isAvailable();
			// System.out.println("state1 ---------" + state1);
			if (state1) {
				switch (state) {
				case CONNECTED:
					if (ni.getType() == ConnectivityManager.TYPE_MOBILE
							|| ni.getType() == ConnectivityManager.TYPE_WIFI) {
					
					}
					break;
				case DISCONNECTED:
					flag = 1;
					// retMess =
					// "Network Disconnected. Please Check Network Settings.";
					retMess = getString(R.string.alert_014);
					showAlert(retMess);
				
					break;
				default:
					flag = 1;
					retMess = getString(R.string.alert_000);
					// setAlert();
					showAlert(retMess);
				
					break;
				}
			} else {
				flag = 1;
				retMess = getString(R.string.alert_000);
				// setAlert();
				showAlert(retMess);
				
			}
		} catch (NullPointerException ne) {

			Log.i("mayuri", "NullPointerException Exception" + ne);
			flag = 1;
			// retMess = "Can Not Get Connection. Please Try Again.";
			retMess = getString(R.string.alert_000);
			// setAlert();
			showAlert(retMess);
			

		} catch (Exception e) {
			Log.i("mayuri", "Exception" + e);
			flag = 1;
			// retMess = "Connection Problem Occured.";
			retMess = getString(R.string.alert_000);
			// setAlert();
		
		}
		return flag;
	}
}

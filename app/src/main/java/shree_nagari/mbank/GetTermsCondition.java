package shree_nagari.mbank;

import java.security.PrivateKey;

import javax.crypto.spec.SecretKeySpec;

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
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class GetTermsCondition extends Activity implements OnClickListener {

	MainActivity act;
	GetTermsCondition getcustID;
	Button btn_otp;
	EditText txt_accno, txt_mobileno;
	String accno = "", mobno = "", retMess = "";
	Boolean isInternetPresent = false;
	PrivateKey var1=null;	  
	String var5="",var3="";
	SecretKeySpec var2=null;
	private static String NAMESPACE = ""; 
	private static String URL = ""; //
	private static String SOAP_ACTION = ""; 
	private static String METHOD_NAME1 = "";
	private static String responseJSON = "NULL";
	Intent mainIntent;
	TextView txt_heading,txt_heading1,txt_heading2,txt_heading3,txt_heading4,txt_heading5,txt_heading6,txt_heading7,txt_heading8,term_textView;
	CheckBox term_checkBox;
	String[] presidents;
	static String imeiNo;
	ImageView btn_home1,btn_logout;
	ImageButton btn_back;
	String custid = "";
	int flag = 0;
	String retVal = "",respcode="",retval="",respdesc="";
	ImageView img_heading;
	TelephonyManager telephonyManager;
	private String strCustId;
	private String strFromAct;
	private String strRetVal;
	private String strMobNo;

	public GetTermsCondition() {
	}

	public GetTermsCondition(MainActivity a) {
		System.out.println("AddOtherBankBeneficiary()" + a);
		act = a;
		getcustID = this;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(LayoutParams.FLAG_SECURE, LayoutParams.FLAG_SECURE);
		setContentView(R.layout.terms_condition);
		var1 = (PrivateKey)getIntent().getSerializableExtra("VAR1");
		var3 = (String)getIntent().getSerializableExtra("VAR3");
		txt_heading = (TextView)findViewById(R.id.txt_heading);
		txt_heading.setText(getString(R.string.lbl_terms_cond));	
		txt_heading1 = (TextView)findViewById(R.id.txt_heading1);
		txt_heading2= (TextView)findViewById(R.id.txt_heading2);
		txt_heading3 = (TextView)findViewById(R.id.txt_heading3);
		txt_heading4 = (TextView)findViewById(R.id.txt_heading4);
		txt_heading5= (TextView)findViewById(R.id.txt_heading5);
		txt_heading6 = (TextView)findViewById(R.id.txt_heading6);
		txt_heading7 = (TextView)findViewById(R.id.txt_heading7);
		txt_heading8 = (TextView)findViewById(R.id.txt_heading8);
		term_checkBox=(CheckBox)findViewById(R.id.checkBox1);
		term_textView=(TextView)findViewById(R.id.textView1);	
		btn_otp=(Button)findViewById(R.id.btn_otp);
		btn_otp.setOnClickListener(this);
		img_heading = (ImageView) findViewById(R.id.img_heading);
		img_heading.setBackgroundResource(R.mipmap.newlogofordash);
		btn_home1 = (ImageView) findViewById(R.id.btn_home1);
		btn_logout = (ImageView) findViewById(R.id.btn_logout);
		btn_home1.setVisibility(View.INVISIBLE);
		btn_logout.setVisibility(View.INVISIBLE);
		btn_home1.setOnClickListener(null);
		btn_logout.setOnClickListener(null);
		imeiNo = MBSUtils.getImeiNumber(GetTermsCondition.this);
		Bundle bObj = getIntent().getExtras();
		if (bObj != null) {
			strCustId = bObj.getString("CUSTID");
			strFromAct = bObj.getString("FROMACT");			
			strMobNo = bObj.getString("MOBNO");
		}
	}

	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		
		case R.id.btn_otp:			
			if(!term_checkBox.isChecked())
			{
				showAlert(getString(R.string.alert_15009));
			}
			else
			{
				 new CallWebService().execute();
			}
			break;			
		default:
			break;
		}
	}
	public void showAlert(final String str)
	{
			ErrorDialogClass alert = new ErrorDialogClass(this,""+str){
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
		mainIntent = new Intent(this, Register.class);
		mainIntent.putExtra("termflg", true);
		mainIntent.putExtra("VAR1", var1);
		mainIntent.putExtra("VAR3", var3);
		startActivityForResult(mainIntent, 500);
		finish();
	}

	
	class CallWebService extends AsyncTask<Void, Void, Void> {// CallWebService_resend_otp
		LoadProgressBar loadProBarObj = new LoadProgressBar(GetTermsCondition.this);
	        JSONObject jsonObj = new JSONObject();
		boolean isWSCalled = false;
		String ValidationData="";

		@Override
		protected void onPreExecute() {
			loadProBarObj.show();
			respcode="";retval="";respdesc="";
			
			try
			{
				jsonObj.put("CUSTID", strCustId);
				jsonObj.put("REQSTATUS","R");
				jsonObj.put("REQFROM", "MBSREG");
				jsonObj.put("MOBNO", strMobNo);
				jsonObj.put("IMEINO", MBSUtils.getImeiNumber(GetTermsCondition.this));
				jsonObj.put("SIMNO", MBSUtils.getSimNumber(GetTermsCondition.this));
				jsonObj.put("METHODCODE","27");
				// ValidationData=MBSUtils.getValidationData(GetTermsCondition.this,jsonObj.toString());
			}
                        catch(Exception e)
			{
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
				} catch (Exception e) {
					e.printStackTrace();
					retMess = getString(R.string.alert_000);
				}
			} catch (Exception e) {
				retMess = getString(R.string.alert_000);
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
           					
           					if(ValidationData.equals(MBSUtils.getValidationData(GetTermsCondition.this, xml_data[0].trim())))
           					{*/
                          	if(jsonObj.has("RESPCODE"))
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
				if(retval.split("~")[0].indexOf("SUCCESS")>-1)
				{
					post_success(retval);
				} 
				else 
				{
					retMess = getString(R.string.alert_094);
					showAlert(retMess);
				}
				
           				}/*}
           					else{
           						MBSUtils.showInvalidResponseAlert(GetTermsCondition.this);	
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
	public 	void post_success(String retval)
	{
		respcode="";respdesc="";
		String decryptedAccounts = retval.split("~")[1];
		Bundle bObj=new Bundle();
		Intent in=new Intent(GetTermsCondition.this,OTPActivity.class);
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
}

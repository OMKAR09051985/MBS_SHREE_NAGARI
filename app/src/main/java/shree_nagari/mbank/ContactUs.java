/*
 * Added By SSP On 29/07/2016
 */

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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

@SuppressLint("NewApi") 
public class ContactUs extends Activity implements OnClickListener
{
	Activity act;
	ContactUs contactUsObj;
	ImageButton btn_home;//, btn_back;
	TextView txt_heading;
	ImageView img_heading;
	LinearLayout branch_dtls;
	ImageView btn_home1,btn_logout;
	private static final String MY_SESSION = "my_session";
	private static String NAMESPACE = "";
	private static String URL = "";
	private static String SOAP_ACTION = "";
	private static  String METHOD_NAME_GET_CONF = "";
	PrivateKey var1=null;	  
	String var5="",var3="";
	SecretKeySpec var2=null;
	TextView txt_bank_email,txt_bank_phone_number,txt_bank_web_url;
	
	String custId="",retMess="",retVal="",bankUrl="",bankPhoneNo="",bankEmail="",respcode="",retval="",respdesc="";
	int cnt = 0, flag = 0;
	public ContactUs() {
		//Log.e("ContactUs","Default CONSTR");
		act = this;
	}

	public ContactUs(Activity a) {
		Log.e("ContactUs","CONSTR");
		act = a;
		contactUsObj = this;
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getWindow().setFlags(LayoutParams.FLAG_SECURE, LayoutParams.FLAG_SECURE);
		setContentView(R.layout.contact_us);
		var1 = (PrivateKey)getIntent().getSerializableExtra("VAR1");
		var3 = (String)getIntent().getSerializableExtra("VAR3");
		txt_bank_email=(TextView)findViewById(R.id.txt_bank_email);
		txt_bank_phone_number=(TextView)findViewById(R.id.txt_bank_phone_number);
		txt_bank_web_url=(TextView)findViewById(R.id.txt_bank_web_url);
		/*btn_back = (ImageButton) findViewById(R.id.btn_back);*/
		//btn_back.setImageResource(R.drawable.backover);
		btn_home = (ImageButton) findViewById(R.id.btn_home);

		
		img_heading=(ImageView)findViewById(R.id.img_heading);
        img_heading.setBackgroundResource(R.mipmap.contact_us);
		//btn_home.setImageResource(R.drawable.ic_home_d);
		//btn_back.setImageResource(R.drawable.backover);
		txt_heading = (TextView) findViewById(R.id.txt_heading);
		//img_heading.setBackgroundResource(R.drawable.contact_us);
		branch_dtls = (LinearLayout) findViewById(R.id.branch_dtls);
		btn_home1 = (ImageView) findViewById(R.id.btn_home1);
		btn_logout = (ImageView) findViewById(R.id.btn_logout);
		btn_home1.setVisibility(View.VISIBLE);
		btn_logout.setVisibility(View.GONE);
		btn_home1.setOnClickListener(null);
		btn_logout.setOnClickListener(null);
		branch_dtls.setOnClickListener(this);
		txt_heading.setText("Contact Us");
		btn_home.setOnClickListener(this);
		
		//btn_back.setOnClickListener(this);
		txt_bank_email.setOnClickListener(this);
		txt_bank_phone_number.setOnClickListener(this);
		txt_bank_web_url.setOnClickListener(this);
		//SharedPreferences sp = act.getSharedPreferences(MY_SESSION,Context.MODE_PRIVATE);
		//custId = sp.getString("custId", "custId");
		flag = chkConnectivity();
		if (flag == 0) {
			CallWebServiceGetConfiguration C = new CallWebServiceGetConfiguration();
			C.execute();
		}
	}
	/*@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) 
	{
		View rootView = inflater.inflate(R.layout.contact_us,container, false);
		
		txt_bank_email=(TextView)rootView.findViewById(R.id.txt_bank_email);
		txt_bank_phone_number=(TextView)rootView.findViewById(R.id.txt_bank_phone_number);
		txt_bank_web_url=(TextView)rootView.findViewById(R.id.txt_bank_web_url);
		btn_back = (ImageButton) rootView.findViewById(R.id.btn_back);
		btn_home = (ImageButton) rootView.findViewById(R.id.btn_home);
		
		img_heading=(ImageView)rootView.findViewById(R.id.img_heading);
		btn_home.setImageResource(R.drawable.ic_home_d);
		btn_back.setImageResource(R.drawable.backover);
		txt_heading = (TextView) rootView.findViewById(R.id.txt_heading);
		img_heading.setBackgroundResource(R.drawable.contact_us);
		txt_heading.setText(getString(R.string.lbl_contact_us));
		
		btn_home.setOnClickListener(this);
		
		btn_back.setOnClickListener(this);
		txt_bank_email.setOnClickListener(this);
		txt_bank_phone_number.setOnClickListener(this);
		txt_bank_web_url.setOnClickListener(this);
		
		SharedPreferences sp = act.getSharedPreferences(MY_SESSION,
				Context.MODE_PRIVATE);
		
		custId = sp.getString("custId", "custId");
		
		flag = chkConnectivity();
		if (flag == 0) {
			CallWebServiceGetConfiguration C = new CallWebServiceGetConfiguration();
			C.execute();
		}
		return rootView;		
	}*/
	
	@Override
	public void onClick(View arg0) 
	{
		Intent in=null;
		switch (arg0.getId()) 
		{
			case R.id.txt_bank_email:
				in = new Intent (Intent.ACTION_VIEW, Uri.parse("mailto:" +bankEmail));	
				startActivity(in);
				break;
			case R.id.txt_bank_phone_number:
				if(bankPhoneNo.indexOf(",")>-1)
				{
					InputDialogBox inputBox = new InputDialogBox(act);
					inputBox.show();
				}
				else
				{
					in = new Intent(Intent.ACTION_DIAL);
					in.setData(Uri.parse("tel:"+bankPhoneNo));
					startActivity(in);
				}
				break;
			case R.id.txt_bank_web_url:
				
				if (!bankUrl.startsWith("http://") && !bankUrl.startsWith("https://")) 
					bankUrl = "http://" + bankUrl;
				
				in = new Intent(Intent.ACTION_VIEW, Uri.parse(bankUrl)); 
				startActivity(in); 
				break;
			case R.id.branch_dtls:
				in = new Intent(this, BranchDetails.class);
				in.putExtra("VAR1", var1);
				in.putExtra("VAR3", var3);
				startActivity(in);
				finish();
				break;
			default:
				break;
		}
	}
	
	public void showAlert(final String str) {
		// Toast.makeText(this, str, Toast.LENGTH_LONG).show();
		ErrorDialogClass alert = new ErrorDialogClass(act, "" + str)
		{@Override
			public void onClick(View v)

			{
				//Log.e("SetMPIN","SetMPIN...btn CLicked=="+R.id.btn_ok+"==="+v.getId());
				switch (v.getId()) 
				{
					case R.id.btn_ok:
						//Log.e("SetMPIN","SetMPIN...CASE trru="+WSCalled);
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
		Intent in = new Intent(this,LoginActivity.class);
		in.putExtra("VAR1", var1);
		in.putExtra("VAR3", var3);
			startActivity(in);
			finish();
	}
	class CallWebServiceGetConfiguration extends AsyncTask<Void, Void, Void> {
		LoadProgressBar loadProBarObj = new LoadProgressBar(act);
		JSONObject jsonObj = new JSONObject();
		String ValidationData="";

		protected void onPreExecute() {
	try{
		
		respcode="";
		retval="";
		respdesc="";
			
			loadProBarObj.show();

            jsonObj.put("CUSTID", custId);
            jsonObj.put("SIMNO", MBSUtils.getSimNumber(act));
            jsonObj.put("METHODCODE","43");
          //  ValidationData=MBSUtils.getValidationData(act,jsonObj.toString());
		
 }
			 catch (JSONException je) {
	                je.printStackTrace();
	            }
	
		}

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
				e.printStackTrace();
				System.out.println("Exception 2");
				
			}
			return null;
		}// end doInBackground

		protected void onPostExecute(Void paramVoid) 
		{
				loadProBarObj.dismiss();
			 JSONObject jsonObj;
				try
				{
	
					String str=CryptoClass.Function6(var5,var2);
					 jsonObj = new JSONObject(str.trim());	
					 
					/* ValidationData=xml_data[1].trim();
					if(ValidationData.equals(MBSUtils.getValidationData(act, xml_data[0].trim())))
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
			
			//decryptedBeneficiaries="SUCCESS";
			//Log.e("CONTACTUS","decryptedBeneficiaries=="+decryptedBeneficiaries);
			if(retval.indexOf("FAILED")>-1)
			{
				showAlert(getString(R.string.alert_133));
			}
			else
			{
				post_success(retval);
							
			}
			
				}
					/*}
					else{
						
						MBSUtils.showInvalidResponseAlert(act);	
					}*/
				} catch (JSONException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}// end onPostExecute

	}// end CallWebServiceGetConfiguration
	public 	void post_success(String retval)
	{
		try 
		{
			respcode="";
			respdesc="";
			JSONObject jObj= new JSONObject(retval);
			bankUrl=jObj.getString("URL");
			bankPhoneNo=jObj.getString("PHONE");
			bankEmail=jObj.getString("EMAIL");
			
			txt_bank_email.setText(bankEmail);
			txt_bank_phone_number.setText(bankPhoneNo);
			txt_bank_web_url.setText(bankUrl);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}	
		
	}
	public int chkConnectivity() {
		// pb_wait.setVisibility(ProgressBar.VISIBLE);
		ConnectivityManager cm = (ConnectivityManager) act.getSystemService(Context.CONNECTIVITY_SERVICE);
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
				Log.e("chkConnectivity","7");
				flag = 1;
				retMess = getString(R.string.alert_000);
				showAlert(retMess);
			}
		} catch (NullPointerException ne) {

			Log.i("BalanceEnquiry", "NullPointerException Exception"
					+ ne);
			flag = 1;
			retMess = getString(R.string.alert_000);
			showAlert(retMess);
		} catch (Exception e) {
			Log.i("BalanceEnquiry   mayuri", "Exception" + e);
			flag = 1;
			retMess = getString(R.string.alert_000);
			showAlert(retMess);
		}
		return flag;
	}// end chkConnectivity
	
	public class InputDialogBox extends Dialog implements OnClickListener {
		Spinner spi_select_phone;
		Button btn_dial;
		//ImageButton btn_spnr_phone;
		

		public InputDialogBox(Activity activity) {
			super(activity);
		}// end InputDialogBox

		protected void onCreate(Bundle bdn) 
		{
			super.onCreate(bdn);
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			setContentView(R.layout.phone_select_design);
			spi_select_phone = (Spinner) findViewById(R.id.spnr_select_phone);
			//btn_spnr_phone=(ImageButton)findViewById(R.id.btn_spnr_phone);
			btn_dial = (Button) findViewById(R.id.btn_dial);
			btn_dial.setVisibility(Button.VISIBLE);
			btn_dial.setOnClickListener(this);
			//btn_spnr_phone.setOnClickListener(this);
			
			String[] phoneNoArr = bankPhoneNo.split(",");
			
			CustomeSpinnerAdapter bankNames = new CustomeSpinnerAdapter(act,R.layout.spinner_item, phoneNoArr);
			bankNames.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spi_select_phone.setAdapter(bankNames);
		}

		@Override
		public void onClick(View v) 
		{
			switch(v.getId())
			{
				case R.id.btn_dial:
					try 
					{				
						String str = spi_select_phone.getSelectedItem().toString();
						Intent in = new Intent(Intent.ACTION_DIAL);
						in.setData(Uri.parse("tel:"+str));
						startActivity(in);
						this.hide();
					} 
					catch (Exception e) 
					{
						e.printStackTrace();
						System.out.println("Exception in InputDialogBox of onClick:=====>"+ e);
					}
					break;
				/*case R.id.btn_spnr_phone:
					spi_select_phone.performClick();
					break;*/
				default:
					break;
			}
		}// end onClick
	}// end InputDialogBox
}

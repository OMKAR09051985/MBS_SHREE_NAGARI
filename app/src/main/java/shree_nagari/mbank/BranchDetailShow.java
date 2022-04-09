package shree_nagari.mbank;

import java.security.PrivateKey;

import javax.crypto.spec.SecretKeySpec;

import mbLib.CryptoClass;
import mbLib.DatabaseManagement;
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
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class BranchDetailShow  extends Activity implements OnClickListener{
	MainActivity act;
	BranchDetailShow branchDetailShow;
	ImageView btn_home1,btn_logout;
	TextView txt_heading;
	ImageButton btn_back;
	int flag = 1;
	String retMess="",retval,respcode,respdesc,custid,brname,brcode;
	private static String NAMESPACE = "";
	private static String URL = "";
	private static String SOAP_ACTION = "";
	private static final String MY_SESSION = "my_session";
	DatabaseManagement dbms;
	PrivateKey var1=null;	  
	String var5="",var3="";
	SecretKeySpec var2=null;
	ImageView img_heading;
	EditText ed_Branch_Namet,ed_Branch_Address,ed_Branch_District,ed_Branch_State,ed_Branch_Manager,ed_Branch_coNumber,ed_Branch_IFSC;
	public BranchDetailShow() {
	}

	public BranchDetailShow(MainActivity a) {
		//System.out.println("AddOtherBankBeneficiary()" + a);
		act = a;
		branchDetailShow = this;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getWindow().setFlags(LayoutParams.FLAG_SECURE, LayoutParams.FLAG_SECURE);
		setContentView(R.layout.branch_detail_show);
		var1 = (PrivateKey)getIntent().getSerializableExtra("VAR1");
		var3 = (String)getIntent().getSerializableExtra("VAR3");
		ed_Branch_Namet=(EditText)findViewById(R.id.ed_Branch_Namet);
		ed_Branch_Address=(EditText)findViewById(R.id.ed_Branch_Address);
		ed_Branch_District=(EditText)findViewById(R.id.ed_Branch_District);
		ed_Branch_State=(EditText)findViewById(R.id.ed_Branch_State);
		ed_Branch_Manager=(EditText)findViewById(R.id.ed_Branch_Manager);
		ed_Branch_coNumber=(EditText)findViewById(R.id.ed_Branch_coNumber);
		ed_Branch_IFSC=(EditText)findViewById(R.id.ed_Branch_IFSC);
		img_heading=(ImageView)findViewById(R.id.img_heading);
		img_heading.setImageResource(R.mipmap.contact_us);
		txt_heading = (TextView)findViewById(R.id.txt_heading);
		txt_heading.setText(getString(R.string.lbl_branch_det));
		btn_home1 = (ImageView) findViewById(R.id.btn_home1);
		btn_logout = (ImageView) findViewById(R.id.btn_logout);
		btn_home1.setVisibility(View.INVISIBLE);
		btn_logout.setVisibility(View.INVISIBLE);
		btn_home1.setOnClickListener(null);
		btn_logout.setOnClickListener(null);
		//btn_back=(ImageButton)findViewById(R.id.btn_back);
		//btn_back.setImageResource(R.drawable.backover);		
		//btn_back.setOnClickListener(this);
		dbms = new DatabaseManagement("shree_nagari.mbank", "listMobileBanking");
		Cursor c1 = dbms.selectFromTable("SHAREDPREFERENCE", "", null);// ("select * from ",
		// null);
		if (c1 != null) {
			while (c1.moveToNext()) {
				custid = c1.getString(2);//ListEncryption.decryptData()
				Log.e("custId", "......" + custid);
			}
		}
		
		Bundle b1 = new Bundle();
		b1 = getIntent().getExtras();
		if (b1 != null) {
			brcode = b1.getString("brcode");
			
			
		}
		flag = chkConnectivity();
		if (flag == 0) {
			CallWebServiceGetFetchBranchesdet C = new CallWebServiceGetFetchBranchesdet();
			C.execute();
		}
		
	}
	
	
	class CallWebServiceGetFetchBranchesdet extends AsyncTask<Void, Void, Void> {

		LoadProgressBar loadProBarObj = new LoadProgressBar(BranchDetailShow.this);

		JSONObject jsonObj = new JSONObject();
		String ValidationData = "";
		

		@Override
		protected void onPreExecute() {
			try {

				loadProBarObj.show();
				retval = "";
				respcode = "";
				respdesc = "";

				jsonObj.put("CUSTID", custid);
                jsonObj.put("BRANCHCD",brcode);
				jsonObj.put("IMEINO",
						MBSUtils.getImeiNumber(BranchDetailShow.this));
				jsonObj.put("SIMNO", MBSUtils.getSimNumber(BranchDetailShow.this));
				jsonObj.put("METHODCODE","70");
				Log.e("DSP","Branchdetails===="+jsonObj);
				//ValidationData = MBSUtils.getValidationData(BranchDetailShow.this,jsonObj.toString());

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

				System.out.println("LoanAccountDetail   Exception" + e);
			}
			return null;
		}// end doInBackground

		protected void onPostExecute(final Void result) {

			loadProBarObj.dismiss();

			JSONObject jsonObj;
			try {
				
				String str=CryptoClass.Function6(var5,var2);
				 jsonObj = new JSONObject(str.trim());
				 Log.e("DSP","Branchdetails===="+str);
				/* ValidationData=xml_data[1].trim();
				if(ValidationData.equals(MBSUtils.getValidationData(BranchDetailShow.this, xml_data[0].trim()))) 
				{
					*/	if (jsonObj.has("RESPCODE")) {
							respcode = jsonObj.getString("RESPCODE");
						} else {
							respcode = "-1";
						}
						if (jsonObj.has("RETVAL")) {
							retval = jsonObj.getString("RETVAL");
						} else {
							retval = "";
						}
						if (jsonObj.has("RESPDESC")) {
							respdesc = jsonObj.getString("RESPDESC");
						} else {
							respdesc = "";
						}

						if (respdesc.length() > 0) {

							showAlert(respdesc);
						} else {
							if (retval.indexOf("FAILED") > -1) {
								retMess = getString(R.string.alrt_bran_fail);
								showAlert(retMess);
							} else {
								post_successGetBranchdetal(retval);
								Log.e("data return", retval);
								
							}
						}
					/*} else {
						MBSUtils.showInvalidResponseAlert(BranchDetailShow.this);
					}*/
				
			} catch (JSONException e) {

				e.printStackTrace();
			}
		}// end onPostExecute
	}// end

	public void post_successGetBranchdetal(String retval) {
		respdesc = "";
		respcode = "-1";

		JSONObject ret;
		try {
			//ret = new JSONObject(retval);

		   String allstr[] = retval.split("~");
		   
		   ed_Branch_Namet.setText(allstr[0]);  
		   ed_Branch_Address.setText(allstr[1]);
		   ed_Branch_coNumber.setText(allstr[2]);
		   ed_Branch_District.setText(allstr[3]);
		   ed_Branch_State.setText(allstr[6]);
		   ed_Branch_IFSC.setText(allstr[7]);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public int chkConnectivity() {
		// pb_wait.setVisibility(ProgressBar.VISIBLE);
		ConnectivityManager cm = (ConnectivityManager) BranchDetailShow.this
				.getSystemService(BranchDetailShow.this.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		try {
			State state = ni.getState();
			boolean state1 = ni.isAvailable();
			// System.out
			// .println("AddSameBankBeneficiary	in chkConnectivity () state1 ---------"
			// + state1);
			if (state1) {
				switch (state) {
				case CONNECTED:
					if (ni.getType() == ConnectivityManager.TYPE_MOBILE
							|| ni.getType() == ConnectivityManager.TYPE_WIFI) {
						// pb_wait.setVisibility(ProgressBar.VISIBLE);
						// locManager = (LocationManager)
						// getSystemService(Context.LOCATION_SERVICE);
						// netFlg = gpsFlg = 1;
						// Toast.makeText(this, ""+pref,
						// Toast.LENGTH_LONG).show();
						// if (pref.equals("G"))
						// new GpsTimer(timeout * 1000, 1000,
						// this);
						// else
						// new NetworkTimer(timeout * 1000,
						// 1000, this);
						flag = 0;
					}
					break;
				case DISCONNECTED:
					flag = 1;
					// ////retMess =
					// "Network Disconnected. Please Check Network Settings.";
					retMess = getString(R.string.alert_014);
					showAlert(retMess);
					break;
				default:
					flag = 1;
					// ////retMess = "Network Unavailable. Please Try Again.";
					retMess = getString(R.string.alert_000);
					showAlert(retMess);
					break;
				}
			} else {
				flag = 1;
				// ////retMess = "Network Unavailable. Please Try Again.";
				retMess = getString(R.string.alert_000);
				showAlert(retMess);
			}
		} catch (NullPointerException ne) {

			// log.i("AddSameBankBeneficiary    mayuri",
			// "NullPointerException Exception" + ne);
			flag = 1;
			// /////retMess = "Network Unavailable. Please Try Again.";
			retMess = getString(R.string.alert_000);
			// setAlert();
			showAlert(retMess);
		} catch (Exception e) {
			// log.i("AddSameBankBeneficiary   mayuri", "Exception" + e);
			flag = 1;
			// ////retMess = "Network Unavailable. Please Try Again.";
			retMess = getString(R.string.alert_000);
			showAlert(retMess);
		}
		return flag;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
	/*case R.id.btn_back:
		Intent in = new Intent(this, BranchDetails.class);
		startActivity(in);
		finish();
		break;*/
	default:
		break;
		}

	}
	public void onBackPressed() {
		Intent in = new Intent(this, BranchDetails.class);// LoginActivity.class);
		in.putExtra("VAR1", var1);
		in.putExtra("VAR3", var3);
		startActivity(in);
		finish();
	}
	public void showAlert(final String str) {

		ErrorDialogClass alert = new ErrorDialogClass(BranchDetailShow.this, ""
				+ str) {
			@Override
			public void onClick(View v)

			{

				switch (v.getId()) {
				case R.id.btn_ok:
					if ((str.equalsIgnoreCase(respdesc))
							&& (respcode.equalsIgnoreCase("0"))) {
						post_successGetBranchdetal(retval);
					} else if ((str.equalsIgnoreCase(respdesc))
							&& (respcode.equalsIgnoreCase("1"))) {
						this.dismiss();
					}
				}
				dismiss();
			}
		};
		alert.show();
	}

}

package shree_nagari.mbank;

import java.security.PrivateKey;
import java.util.ArrayList;

import javax.crypto.spec.SecretKeySpec;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import shree_nagari.mbank.R;
import mbLib.BranchBean;
import mbLib.CryptoClass;
import mbLib.CustomAdapterForBranch;
import mbLib.DatabaseManagement;
import mbLib.MBSUtils;
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
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class BranchDetails extends Activity implements OnClickListener {
	MainActivity act;
	BranchDetails branchDetails;
	ArrayList<BranchBean> branchbeansarr;
	String retMess, retval, respcode, respdesc, custid = "";
	ListView brnamelist;
	TextView txt_heading;
	ImageButton btn_back;
	ImageView img_heading;
	ImageView btn_home1,btn_logout;
	DatabaseManagement dbms;
	private static String NAMESPACE = "";
	private static String URL = "";
	private static String SOAP_ACTION = "";
	private static final String MY_SESSION = "my_session";
	int flag = 1;
	PrivateKey var1=null;	  
	String var5="",var3="";
	SecretKeySpec var2=null;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getWindow().setFlags(LayoutParams.FLAG_SECURE, LayoutParams.FLAG_SECURE);
		setContentView(R.layout.branch_details);
		var1 = (PrivateKey)getIntent().getSerializableExtra("VAR1");
		var3 = (String)getIntent().getSerializableExtra("VAR3");
		brnamelist = (ListView) findViewById(R.id.branch_listView);
		dbms = new DatabaseManagement("shree_nagari.mbank", "listMobileBanking");
		branchbeansarr = new ArrayList<BranchBean>();
		txt_heading = (TextView)findViewById(R.id.txt_heading);
		txt_heading.setText(getString(R.string.lbl_branch_ne));
		//btn_back=(ImageButton)findViewById(R.id.btn_back);
		//btn_back.setImageResource(R.drawable.backover);		
		//btn_back.setOnClickListener(this);
		img_heading=(ImageView)findViewById(R.id.img_heading);
		img_heading.setImageResource(R.mipmap.contact_us);
		btn_home1 = (ImageView) findViewById(R.id.btn_home1);
		btn_logout = (ImageView) findViewById(R.id.btn_logout);
		btn_home1.setVisibility(View.INVISIBLE);
		btn_logout.setVisibility(View.INVISIBLE);
		btn_home1.setOnClickListener(null);
		btn_logout.setOnClickListener(null);
		Cursor c1 = dbms.selectFromTable("SHAREDPREFERENCE", "", null);// ("select * from ",
		// null);
		if (c1 != null) {
			while (c1.moveToNext()) {
				custid = c1.getString(2);//ListEncryption.decryptData()
				Log.e("custId", "......" + custid);
			}
		}
		flag = chkConnectivity();
		if (flag == 0) {
			CallWebServiceGetFetchBranches C = new CallWebServiceGetFetchBranches();
			C.execute();
		}

	}

	class CallWebServiceGetFetchBranches extends AsyncTask<Void, Void, Void> {

		LoadProgressBar loadProBarObj = new LoadProgressBar(BranchDetails.this);

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

				jsonObj.put("IMEINO",
						MBSUtils.getImeiNumber(BranchDetails.this));
				jsonObj.put("SIMNO", MBSUtils.getSimNumber(BranchDetails.this));
				jsonObj.put("METHODCODE","68");
				
				//ValidationData = MBSUtils.getValidationData(BranchDetails.this,jsonObj.toString());

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
				 
				/*ValidationData=xml_data[1].trim();
					if(ValidationData.equals(MBSUtils.getValidationData(BranchDetails.this, xml_data[0].trim()))) 
				{*/
						if (jsonObj.has("RESPCODE")) {
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
								post_successGetBranch(retval);
								//Log.e("retval",retval);
							}
						}
					/*} else {
						MBSUtils.showInvalidResponseAlert(BranchDetails.this);
					}*/
				
			} catch (JSONException e) {

				e.printStackTrace();
			}
		}// end onPostExecute
	}// end

	public void post_successGetBranch(String retval) {
		respdesc = "";
		respcode = "-1";

		JSONObject ret;
		try {
			ret = new JSONObject(retval);
			branchbeansarr=new ArrayList<BranchBean>();
			JSONArray ja = new JSONArray(ret.getString("BRANCH"));
			JSONObject jObj ;
	         for (int j = 0; j < ja.length(); j++)
	         {
	        	 jObj = ja.getJSONObject(j);
	        	 BranchBean bean = new BranchBean();
	        	 bean.setBrname(jObj.getString("NAME"));
	        	 bean.setBrcode(jObj.getString("CODE"));
	        	 branchbeansarr.add(bean);
	         }
	      
			CustomAdapterForBranch branapter = new CustomAdapterForBranch(
					BranchDetails.this, branchbeansarr);

			brnamelist.setAdapter(branapter);
			brnamelist.setOnItemClickListener(new AdapterView.OnItemClickListener() 
			{
                  @Override
                  public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) 
                  {
                	  	branchbeansarr.get(i).getBrname();
                	  	Intent in = new Intent(BranchDetails.this,BranchDetailShow.class);
						Bundle b = new Bundle();
						b.putString("brcode", branchbeansarr.get(i).getBrcode());
						in.putExtras(b);
						in.putExtra("VAR1", var1);
						in.putExtra("VAR3", var3);
						BranchDetails.this.startActivity(in);
						BranchDetails.this.finish(); 
                  }
            });
		} 
		catch (JSONException e) 
		{
			e.printStackTrace();
		}
	}

	public int chkConnectivity() {
		// pb_wait.setVisibility(ProgressBar.VISIBLE);
		ConnectivityManager cm = (ConnectivityManager) BranchDetails.this
				.getSystemService(BranchDetails.this.CONNECTIVITY_SERVICE);
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
						flag = 0;
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

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
	
	default:
		break;
		}

	}
	public void onBackPressed() {
		Intent in = new Intent(this, ContactUs.class);// LoginActivity.class);
		in.putExtra("VAR1", var1);
		in.putExtra("VAR3", var3);
		startActivity(in);
		finish();
	}
	public void showAlert(final String str) 
	{
		ErrorDialogClass alert = new ErrorDialogClass(BranchDetails.this, ""+ str) 
		{
			@Override
			public void onClick(View v)
			{
				switch (v.getId()) {
				case R.id.btn_ok:
					if ((str.equalsIgnoreCase(respdesc))
							&& (respcode.equalsIgnoreCase("0"))) {
						post_successGetBranch(retval);
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

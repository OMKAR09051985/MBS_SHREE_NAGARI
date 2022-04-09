package shree_nagari.mbank;

import java.security.PrivateKey;
import java.util.ArrayList;

import javax.crypto.spec.SecretKeySpec;

import mbLib.CryptoClass;
import mbLib.CustomAdapterForFdint;
import mbLib.CustomDialogClass;
import mbLib.DatabaseManagement;
import mbLib.DialogBox;
import mbLib.FdIntrestBean;
import mbLib.MBSUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class DepositRateActivity extends Fragment implements OnClickListener{

	MainActivity act;
	DialogBox dbs;
	DepositRateActivity depositRateActivity;
	ImageButton btn_back;
	ImageView img_heading,btn_home1,btn_logout;
	ArrayList<FdIntrestBean> fdIntrestBeans;
	String custid = "";
	String  retval, respcode, respdesc,retvalwbs="";
	TextView txt_heading;
	ListView listView1;
	private static String NAMESPACE = "";
	private static String URL = "";
	private static String SOAP_ACTION = "";
	private static String METHOD_NAME = "";
	String retMess;
	DatabaseManagement dbms;
	private static final String MY_SESSION = "my_session";
	private static final String SHOWREPORT = "getHolidayReportURL";
	PrivateKey var1=null;	  
	String var5="",var3="";
	SecretKeySpec var2=null;
	int flag = 1;
	public DepositRateActivity() {
		depositRateActivity = this;
	}

	@SuppressLint("ValidFragment")
	public DepositRateActivity(MainActivity a) {
		// System.out.println("AddOtherBankBeneficiary()" + a);
		act = a;
		depositRateActivity = this;
	}
	
	
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		System.out.println("onCreateView() AddOtherBankBeneficiary");
		View rootView = inflater.inflate(R.layout.depositrates,
				container, false);

		var1 = act.var1;
		var3 = act.var3;
		txt_heading = (TextView) rootView.findViewById(R.id.txt_heading);
		txt_heading.setText(getString(R.string.lbl_fdInterest_Rates));
		img_heading = (ImageView) rootView.findViewById(R.id.img_heading);
		img_heading.setImageResource(R.mipmap.notification);
		btn_home1 = (ImageView) rootView.findViewById(R.id.btn_home1);
		btn_logout = (ImageView) rootView.findViewById(R.id.btn_logout);
		btn_home1.setOnClickListener(this);
		btn_logout.setOnClickListener(this);
	   //btn_back = (ImageButton) findViewById(R.id.btn_back);
		//btn_back.setImageResource(R.drawable.backover);
		//btn_back.setOnClickListener(this);
		listView1=(ListView)rootView.findViewById(R.id.listView1);
		dbms = new DatabaseManagement("shree_nagari.mbank", "listMobileBanking");
		Cursor c1 = dbms.selectFromTable("SHAREDPREFERENCE", "", null);// ("select * from ",
		// null);
		if (c1 != null) {
			while (c1.moveToNext()) {
				custid = c1.getString(2);//ListEncryption.decryptData();
				Log.e("custId", "......" + custid);
			}
		}
		flag = chkConnectivity();
		if (flag == 0) {
			CallWebServiceGetFetcgfdint C = new CallWebServiceGetFetcgfdint();
			C.execute();
		}
		return rootView;
		}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		/*case R.id.btn_back:
			Intent in = new Intent(this, IntrestRates.class);// LoginActivity.class);
			startActivity(in);
			finish();
			break;*/
		case R.id.btn_home1:
			Intent in1 = new Intent(act, NewDashboard.class);
			in1.putExtra("VAR1", var1);
			in1.putExtra("VAR3", var3);
			startActivity(in1);
			act.finish();
			break;
		case R.id.btn_logout:
			CustomDialogClass alert=new CustomDialogClass(act, getString(R.string.lbl_exit)) {
				@Override
				public void onClick(View v) {
					switch (v.getId()) {
						case R.id.btn_ok:
							flag = chkConnectivity();
							if (flag == 0)
							{
								CallWebServicelog c=new CallWebServicelog();
								c.execute();
							}
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
			/*dbs = new DialogBox(act);
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
			dbs.get_adb().show();*/
			break;	
		default:
			break;
		}
	}
	
	class CallWebServicelog extends AsyncTask<Void, Void, Void> {
	            JSONObject jsonObj = new JSONObject();
		String ValidationData="";
		LoadProgressBar loadProBarObj = new LoadProgressBar(act);
		
		@Override
		protected void onPreExecute() {
                   try{
                	   loadProBarObj.show();
                	   respcode="";
                	   retvalwbs="";
                	   respdesc="";
			Log.e("@DEBUG","LOGOUT preExecute()");
                  jsonObj.put("CUSTID", custid);
	              jsonObj.put("IMEINO", MBSUtils.getImeiNumber(act));
	              jsonObj.put("SIMNO", MBSUtils.getSimNumber(act));
	              jsonObj.put("METHODCODE","29");
	             // ValidationData=MBSUtils.getValidationData(act,jsonObj.toString());
		
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
			loadProBarObj.dismiss();
			   	JSONObject jsonObj;
    				try
    				{
    	
    					String str=CryptoClass.Function6(var5,var2);
    					 jsonObj = new JSONObject(str.trim());
    					/*ValidationData=xml_data[1].trim();
    					if(ValidationData.equals(MBSUtils.getValidationData(act, xml_data[0].trim())))
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
    						
    						MBSUtils.showInvalidResponseAlert(act);	
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
	  act.finish();
		System.exit(0);
		
	}

	
	public int chkConnectivity() {
		// pb_wait.setVisibility(ProgressBar.VISIBLE);
		ConnectivityManager cm = (ConnectivityManager) act
				.getSystemService(act.CONNECTIVITY_SERVICE);
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
	class CallWebServiceGetFetcgfdint extends AsyncTask<Void, Void, Void> {

		LoadProgressBar loadProBarObj = new LoadProgressBar(act);

		JSONObject jsonObj = new JSONObject();
		String ValidationData = "";
		String generatedXML = "";
	
		@Override
		protected void onPreExecute() {
			try {

				loadProBarObj.show();
				retval = "";
				respcode = "";
				respdesc = "";

				jsonObj.put("CUSTID", custid);
				jsonObj.put("IMEINO",
						MBSUtils.getImeiNumber(act));
				jsonObj.put("SIMNO", MBSUtils.getSimNumber(act));
				jsonObj.put("METHODCODE","72");
			//	ValidationData=MBSUtils.getValidationData(act,jsonObj.toString());
				

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

				System.out.println("fetchfdschemedtl   Exception" + e);
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
				if(ValidationData.equals(MBSUtils.getValidationData(act, xml_data[0].trim())))
				{
					*/	
				 Log.e("DSP","strdepositrt....."+str);
				 
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
								post_successGetfdint(retval);
								//Log.e("retval",retval);
							}
						}
					
				/*} else {
					MBSUtils.showInvalidResponseAlert(act);
				}*/
			} catch (JSONException e) {

				e.printStackTrace();
			}
		}// end onPostExecute
	}// end

	public void post_successGetfdint(String retval) {
		respdesc = "";
		respcode = "-1";

		JSONObject ret;
		try {
			ret = new JSONObject(retval);

			
			// bean.setBrname(brname);
			fdIntrestBeans=new ArrayList<FdIntrestBean>();
			 JSONArray ja = new JSONArray(ret.getString("FDINT"));
			  JSONObject jObj ;
	         Log.e("JSONArray length","22222222222 =  "+ja.length());
	        
	         for (int j = 0; j < ja.length(); j++)
	         {
	        	 jObj = ja.getJSONObject(j);
	        	 FdIntrestBean bean = new FdIntrestBean();
			 bean.setDates(jObj.getString("PIP_FROMDY"));
			 bean.setRegular(jObj.getString("PIP_INTRT"));
			 bean.setMaturityPeriod1(jObj.getString("SCHEME"));
			/* if(jObj.getString("PIP_TODY").equals("Onwards")){
				 bean.setMaturityPeriod2("Onwards");
			
			 }
			 else{
				 bean.setMaturityPeriod2(jObj.getString("PIP_TODY"));
			 }*/

			 
			
			 
			 fdIntrestBeans.add(bean);

	         }
	      
			CustomAdapterForFdint branapter = new CustomAdapterForFdint(
					act, fdIntrestBeans);

			listView1.setAdapter(branapter);
		
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void showAlert(final String str) {

		ErrorDialogClass alert = new ErrorDialogClass(act, ""
				+ str) {
			@Override
			public void onClick(View v)

			{

				switch (v.getId()) {
				case R.id.btn_ok:
					if ((str.equalsIgnoreCase(respdesc))
							&& (respcode.equalsIgnoreCase("0"))) {
						post_successGetfdint(retval);
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

package shree_nagari.mbank;

import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.crypto.spec.SecretKeySpec;

import mbLib.CryptoClass;
import mbLib.CustomDialogClass;
import mbLib.DatabaseManagement;
import mbLib.MBSUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import shree_nagari.mbank.R;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
//import mbLib.DialogBox;

public class ListBeneficiary extends Fragment implements OnClickListener {
	MainActivity act;
	ListBeneficiary listBenf;
	EditText txtAccNo;
	// Button btn_fetchName;
	EditText txtName;
	EditText txtmobNo;
	EditText txtEmail;
	EditText txtNick_Name;
	TextView lblTitle;
	// ProgressBar pro_bar;
	Button btn_submit;
	ImageView btn_home1,btn_logout;//, btn_back;
	int cnt = 0, flag = 0;
	String str = "", retMess = "", cust_name = "", tmpXMLString = "",
			retVal = "";
	//DialogBox dbs;
	DatabaseManagement dbms;

	private static final String MY_SESSION = "my_session";
	//Editor e;

	private static String NAMESPACE = "";
	private static String URL = "";
	private static String SOAP_ACTION = "";
	private static  String METHOD_NAME1 = "";

	String custId = "", accNo = "", accNm = "", mobNo = "", nickNm = "",
			mailId = "",respcode="",reTval="",getBeneficiariesrespdesc="";
	ListBeneficiary obj = this;

	private String benInfo = "";
	String mobPin = "";
	String benSrno = "";
	List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
	ListView lstRpt;
	TextView txt_heading;
	ImageView img_heading;
	PrivateKey var1=null;	  
	String var5="",var3="",retvalwbs = "",respdesc = "";
	SecretKeySpec var2=null;

	public ListBeneficiary() {
	}

	@SuppressLint("ValidFragment")
	public ListBeneficiary(MainActivity a) {
		//System.out.println("ListBeneficiary()" + a);
		act = a;
		listBenf = this;
	}


	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		System.out.println("onCreateView() ListBeneficiary");
		var1 = act.var1;
		var3 = act.var3;
		View rootView = inflater.inflate(R.layout.view_beneficiary, container,
				false);
                dbms = new DatabaseManagement("shree_nagari.mbank", "listMobileBanking");
		lstRpt = (ListView) rootView.findViewById(R.id.benList);

		txt_heading = (TextView) rootView.findViewById(R.id.txt_heading);
		img_heading = (ImageView) rootView.findViewById(R.id.img_heading);
		btn_logout =  rootView.findViewById(R.id.btn_logout);
		btn_home1 =  rootView.findViewById(R.id.btn_home1);
		/*btn_back = (ImageButton) rootView.findViewById(R.id.btn_back);*/
		//btn_home.setImageResource(R.drawable.ic_home_d);
		//btn_back.setImageResource(R.drawable.backover);

		//btn_back.setOnClickListener(this);
		btn_home1.setOnClickListener(this);
		btn_logout.setOnClickListener(this);
		txt_heading.setText(getString(R.string.lbl_list_benf));
		img_heading.setBackgroundResource(R.mipmap.benefeciary);
	//	SharedPreferences sp = act.getSharedPreferences(MY_SESSION,
	//			Context.MODE_PRIVATE);
		//e = sp.edit();
	//	custId = sp.getString("custId", "custId");
	Cursor c1=dbms.selectFromTable("SHAREDPREFERENCE", "", null);//("select * from ", null);
		        if(c1!=null)
		        {
		        	while(c1.moveToNext())
			        {	
		        		custId=c1.getString(2);
			        	Log.e("custId","......"+custId);
			        	
			        }
		        }
		
		flag = chkConnectivity();
		// flag=0;
		if (flag == 0) {
			//System.out.println("========== 1.0 ============");
			new CallWebService().execute();
			//System.out.println("========== 1.1 ============");
		}
		/*
		 * else { retMess="Problem in Internet Connection"; setAlert(); }//end
		 * else
		 */
		/*
		 * Button btn_back = (Button) findViewById(R.id.btn_back); // Listening
		 * to back button click btn_back.setOnClickListener(new
		 * View.OnClickListener() {
		 * 
		 * @Override public void onClick(View view) { // Launching News Feed
		 * Screen Intent i = new Intent(getApplicationContext(),
		 * ManageBeneficiaryMenuActivity.class); startActivity(i);finish(); }
		 * });
		 */

		return rootView;
	}

	private void setValues(String totalBenInfo) {
		try {
			//System.out
			//		.println("========= inside setValues of ListBeneficiary:===>"
			//				+ totalBenInfo);
			String allstr[] = totalBenInfo.split("~");
			//System.out.println("========= 1 ===========");
			String singleBenInfo[] = null;
			//System.out.println("========= 2 ===========");
			String benName = null;
			//System.out.println("========= 3 ===========");
			String benAccountNumber = null;
			//System.out.println("========= 4 ===========");
			//System.out
			//		.println("========= allstr[0] is ===========" + allstr[0]);
			if (allstr[0].equalsIgnoreCase("SUCCESS")) {
				List<String> content = new ArrayList<String>();

				for (int i = 1; i < allstr.length; i++) {
					//System.out.println("========= " + i + " ===========");
					singleBenInfo = allstr[i].split("#");
					// benName=singleBenInfo[2]+"("+singleBenInfo[1]+")";
					benName = singleBenInfo[1];
					benAccountNumber = singleBenInfo[3];
					HashMap<String, String> map = new HashMap<String, String>();
                                       if(benAccountNumber.equalsIgnoreCase("-9999"))
					{
						benAccountNumber="-";
					}
					else
					{
						benAccountNumber=benAccountNumber;
					}
					//System.out.println("Ben Name:============>" + benName);
					//System.out.println("benAccountNumber:============>"
					//		+ benAccountNumber);

					String[] from = new String[] { "rowid", "col_0", "col_1" };
					int[] to = new int[] { R.id.item1, R.id.item2, R.id.item3 };

					map.put("col_0", benName.trim());
					map.put("col_1", benAccountNumber.trim());

					fillMaps.add(map);
					SimpleAdapter adapter = new SimpleAdapter(act, fillMaps,
							R.layout.view_ben_rpt, from, to);
					lstRpt.setAdapter(adapter);
				}
			} else {
				retMess = getString(R.string.alert_040);
				showAlert(retMess);
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Exception in setValue of ListEbenefiary:" + e);
		}

	}// end setValues

	public void showAlert(final String str) {
		// Toast.makeText(this, str, Toast.LENGTH_LONG).show();
	//	ErrorDialogClass alert = new ErrorDialogClass(act, "" + str);
	ErrorDialogClass alert = new ErrorDialogClass(act, "" + str)
		
		{
            Intent in = null;
            
            @Override
            public void onClick(View v)
            {
                switch (v.getId())
                {
                    case R.id.btn_ok:
                    	//dismiss();
                    	if((str.equalsIgnoreCase(getBeneficiariesrespdesc)) &&  (respcode.equalsIgnoreCase("0")))
    					{
    						post_successfetch_all_beneficiaries(reTval);
    					}
    					else if((str.equalsIgnoreCase(getBeneficiariesrespdesc)) && (respcode.equalsIgnoreCase("1")))
    					{
    						this.dismiss();
    					}
                    	/*Fragment fragment = new ManageBeneficiaryMenuActivity(act);
            			FragmentManager fragmentManager = getFragmentManager();
            			fragmentManager.beginTransaction()
            					.replace(R.id.frame_container, fragment).commit();*/
            			//act.frgIndex = 7;
                    	
                    	
                }this.dismiss();

            }
		
	 };	alert.show();
	}

	public int chkConnectivity() {
		// pb_wait.setVisibility(ProgressBar.VISIBLE);
		ConnectivityManager cm = (ConnectivityManager) act
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		try {
			State state = ni.getState();
			boolean state1 = ni.isAvailable();
			//System.out
			//		.println("ListBeneficiary	in chkConnectivity () state1 ---------"
			//				+ state1);
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
					}
					break;
				case DISCONNECTED:
					flag = 1;
					// retMess =
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
				// retMess = "Network Unavailable. Please Try Again.";
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

			Log.i("ListBeneficiary", "NullPointerException Exception"
					+ ne);
			flag = 1;
			// retMess = "Network Unavailable. Please Try Again.";
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
			Log.i("ListBeneficiary", "Exception" + e);
			flag = 1;
			// retMess = "Network Unavailable. Please Try Again.";
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
		return flag;
	}// end CHeckConnection
		// this webservice to call and add all beniferies

	class CallWebService extends AsyncTask<Void, Void, Void> {

		String retval = "";
		LoadProgressBar loadProBarObj = new LoadProgressBar(act);
		JSONObject jsonObj = new JSONObject();
		String accNo, debitAccno, benAcNo, amt, reMark;
		String ValidationData="";

		protected void onPreExecute() {
	           try{
	        	   respcode="";
	        	   reTval="";
	        	   getBeneficiariesrespdesc="";
			// pb_wait.setVisibility(ProgressBar.VISIBLE);
			loadProBarObj.show();
		
              jsonObj.put("CUSTID", custId);
             jsonObj.put("SAMEBNK", "A");
             jsonObj.put("IMEINO", MBSUtils.getImeiNumber(act));
             jsonObj.put("SIMNO", MBSUtils.getSimNumber(act));
             jsonObj.put("METHODCODE","13");
             //ValidationData=MBSUtils.getValidationData(act,jsonObj.toString());
				   Log.e("Shubham", "ListBeneficiary_Request-->"+jsonObj.toString() );
		
			} catch (JSONException je) {
		                je.printStackTrace();
			}

		}// end onPreExecute

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
				// e.printStackTrace();
				//System.out.println("Exception 2");
				System.out.println("ListBeneficiary   Exception" + e);
			}
			return null;
		}// end dodoInBackground

		protected void onPostExecute(Void paramVoid) {
 
			loadProBarObj.dismiss();
			JSONObject jsonObj;
			try
			{

				String str=CryptoClass.Function6(var5,var2);
				 jsonObj = new JSONObject(str.trim());
				Log.e("Shubham", "ListBeneficiary_Responce-->"+jsonObj.toString() );
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
					reTval = jsonObj.getString("RETVAL");
				}
				else
				{
					reTval = "";
				}
				if (jsonObj.has("RESPDESC"))
				{
					getBeneficiariesrespdesc = jsonObj.getString("RESPDESC");
				}
				else
				{	
					getBeneficiariesrespdesc = "";
				}
				
			if(getBeneficiariesrespdesc.length()>0)
			{
				showAlert(getBeneficiariesrespdesc);
			}
			else{

			if (reTval.indexOf("SUCCESS") > -1) 
			{
				post_successfetch_all_beneficiaries(reTval);
					
			} else if (reTval.indexOf("NODATA") > -1)  {
				
				retMess = getString(R.string.alert_041);
				//  loadProBarObj.dismiss();
				showAlert(retMess);
			}
			else
			{
				 retMess=getString(R.string.alert_069);
		      	 // loadProBarObj.dismiss();
		      	  showAlert(retMess);
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

	}// end callWbService
	public 	void post_successfetch_all_beneficiaries(String reTval)
	{
		  respcode="";
   	   getBeneficiariesrespdesc="";
		benInfo = reTval;
		setValues(reTval);
		//loadProBarObj.dismiss();
		
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		/*case R.id.btn_back:
			//System.out.println("Clicked on back");

			Fragment fragment = new ManageBeneficiaryMenuActivity(act);
			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.frame_container, fragment).commit();
			act.frgIndex=6;
			break;*/

		case R.id.btn_home1:
			Intent in = new Intent(act, NewDashboard.class);
			in.putExtra("VAR1", var1);
			in.putExtra("VAR3", var3);
			startActivity(in);
			act.finish();
			break;


		case R.id.btn_logout:
			CustomDialogClass alert = new CustomDialogClass(act, getString(R.string.lbl_exit)) {
				@SuppressLint("NonConstantResourceId")
				@Override
				public void onClick(View v) {
					switch (v.getId()) {
						case R.id.btn_ok:
							flag = chkConnectivity();
							if (flag == 0) {
								CallWebServicelog c = new CallWebServicelog();
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
		}
	}

	public class  CallWebServicelog extends AsyncTask<Void, Void, Void> {
		JSONObject jsonObj = new JSONObject();
		String ValidationData = "";
		LoadProgressBar loadProBarObj = new LoadProgressBar(act);

		@Override
		protected void onPreExecute() {
			try {
				loadProBarObj.dismiss();
				respcode = "";
				retvalwbs = "";
				respdesc = "";
				Log.e("@DEBUG", "LOGOUT preExecute()");
				jsonObj.put("CUSTID", custId);
				jsonObj.put("IMEINO", MBSUtils.getImeiNumber(act));
				jsonObj.put("SIMNO", MBSUtils.getSimNumber(act));
				jsonObj.put("METHODCODE", "29");
				// ValidationData=MBSUtils.getValidationData(act,jsonObj.toString());

			} catch (JSONException je) {
				je.printStackTrace();
			}

		}

		;

		@Override
		protected Void doInBackground(Void... arg0) {
			String value4 = getString(R.string.namespace);
			String value5 = getString(R.string.soap_action);
			String value6 = getString(R.string.url);
			final String value7 = "callWebservice";
			try {
				String keyStr = CryptoClass.Function2();
				var2 = CryptoClass.getKey(keyStr);
				SoapObject request = new SoapObject(value4, value7);

				request.addProperty("value1", CryptoClass.Function5(jsonObj.toString(), var2));
				request.addProperty("value2", CryptoClass.Function3(keyStr, var1));
				request.addProperty("value3", var3);
				SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
				envelope.setOutputSoapObject(request);
				HttpTransportSE androidHttpTransport = new HttpTransportSE(value6, 15000);
				if (androidHttpTransport != null)
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
				Log.e("ERROR-OUTER", e.getClass() + " : " + e.getMessage());
			}
			return null;
		}

		protected void onPostExecute(final Void result) {
			loadProBarObj.dismiss();
			JSONObject jsonObj;
			try {

				String str = CryptoClass.Function6(var5, var2);
				jsonObj = new JSONObject(str.trim());
    					/*ValidationData=xml_data[1].trim();
    					if(ValidationData.equals(MBSUtils.getValidationData(act, xml_data[0].trim())))
    					{
    					Log.e("IN return", "data :" + jsonObj.toString());*/
				if (jsonObj.has("RESPCODE")) {
					respcode = jsonObj.getString("RESPCODE");
				} else {
					respcode = "-1";
				}
				if (jsonObj.has("RETVAL")) {
					retvalwbs = jsonObj.getString("RETVAL");
				} else {
					retvalwbs = "";
				}
				if (jsonObj.has("RESPDESC")) {
					respdesc = jsonObj.getString("RESPDESC");
				} else {
					respdesc = "";
				}

				if (respdesc.length() > 0) {
					showAlert(respdesc);
				} else {
					if (retvalwbs.indexOf("FAILED") > -1) {
						retMess = getString(R.string.alert_network_problem_pease_try_again);
						showAlert(retMess);

					} else {
						post_successlog(retvalwbs);
				/*finish();
				System.exit(0);*/
					}
				}
    					/*}
    					else{

    						MBSUtils.showInvalidResponseAlert(act);
    					}*/
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	public void post_successlog(String retvalwbs) {
		respcode = "";
		respdesc = "";
		act.finish();
		System.exit(0);

	}

}// end ListBeneficiary


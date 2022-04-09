package shree_nagari.mbank;

import java.security.PrivateKey;

import javax.crypto.spec.SecretKeySpec;

import mbLib.CryptoClass;
import mbLib.CustomDialogClass;
import mbLib.DatabaseManagement;
import mbLib.DialogBox;
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
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
//import android.annotation.SuppressLint;

//@SuppressLint("NewApi")
public class FdRdAccountDetail extends Fragment implements OnClickListener 
{
	Editor e;
	MainActivity act;
	FdRdAccountDetail fdrddtl;
	TextView txt_heading, cust_nm, accNo, txt_cust_name,txt_lbl_underLiened,tv_int_frq;
	EditText opening_amnt,opng_dt,maturity_date,maturity_amnt,intrest_value,
	underliened,txt_underliened,txt_instl_amt,txt_instl_frq,txt_curbal,txt_pnd_instl,
	txt_intvl_int,txt_intv_int_frq,ifsc_code,sanct_limit,txt_lien_amt;
	Button btnChangeMpin;
	public Bundle getBundle = null;
	Cursor curSelectBankname;
	TextView tv_bankname, tv_curbal, tv_pnd_instal, tv_intvl_int;
	ImageView btn_home1,btn_logout;//, btn_back;
	LinearLayout txtLayout,edtLayout,amtTxtLayout,amtEditLayout;
	String retMess = "", retVal = "",custid="",accountNo="",
			respcode="",retvalweb="",respdesc="",retvalwbs="",retval="",AccCustId;
	int cnt = 0, flag = 0;
	//private String strtext;
		DatabaseManagement dbms;
	private static String NAMESPACE = "";
	private static String URL = "";
	private static String SOAP_ACTION = "";
	private static  String METHOD_NAME = "";
	private static final String MY_SESSION = "my_session";
	String accstr="",accInfo="";
	ImageView img_heading;
	DialogBox dbs;
	PrivateKey var1=null;	  
	String var5="",var3="";
	SecretKeySpec var2=null;
	
	public FdRdAccountDetail() {
	}

	@SuppressLint("ValidFragment")
	public FdRdAccountDetail(MainActivity a) {
		act = a;
		fdrddtl = this;
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		accountNo = getArguments().getString("accountinfo");
		View rootView = inflater.inflate(R.layout.fdrdaccount_details,
				container, false);
		img_heading=(ImageView)rootView.findViewById(R.id.img_heading);
        img_heading.setBackgroundResource(R.mipmap.term_deposit);
        var1 = act.var1;
        var3 = act.var3;
		Bundle b1=getArguments();
		if(b1!=null)
		{
			accountNo=b1.getString("accountnumber");
			accstr=b1.getString("accountstr");
			accInfo=b1.getString("accountinfo");
			AccCustId=b1.getString("AccCustId");
			//Log.e("onCreateView","accountNo=="+accountNo);
			//Log.e("onCreateView","accountNo=="+accountNo);
		}
		//Log.tem.out.println("onCreateView() Fd RD Account Details" + strtext);
		//Log.e("onCreateView() Fd RD Account Details", strtext);
		accNo = (TextView) rootView.findViewById(R.id.accNo);
		txt_cust_name = (TextView) rootView.findViewById(R.id.cust_name);
		maturity_amnt = (EditText) rootView.findViewById(R.id.maturity_amnt);
		maturity_date = (EditText) rootView.findViewById(R.id.maturity_date);
		intrest_value = (EditText) rootView.findViewById(R.id.intrest_value);
		opening_amnt = (EditText) rootView.findViewById(R.id.opening_amnt);
		txt_heading = (TextView) rootView.findViewById(R.id.txt_heading);
		txt_underliened= (EditText) rootView.findViewById(R.id.underliened);
		opng_dt= (EditText) rootView.findViewById(R.id.opng_dt);
		txt_instl_amt= (EditText) rootView.findViewById(R.id.instmnt_amnt);
		txt_instl_frq= (EditText) rootView.findViewById(R.id.instmnt_frqncy);
		txt_curbal=(EditText) rootView.findViewById(R.id.curbal);
		tv_curbal= (TextView) rootView.findViewById(R.id.txtlbl_curbal);
		txt_lbl_underLiened=(TextView) rootView.findViewById(R.id.txtlbl_underliened);
		txtLayout=(LinearLayout)rootView.findViewById(R.id.rdtxtextrslayout);
		edtLayout=(LinearLayout)rootView.findViewById(R.id.rdedtextrslayout);
		amtTxtLayout=(LinearLayout)rootView.findViewById(R.id.lyt_amt_txt);
		amtEditLayout=(LinearLayout)rootView.findViewById(R.id.lyt_amt_edttxt);
		tv_pnd_instal=(TextView) rootView.findViewById(R.id.txtlbl_pnd_instl);
		tv_intvl_int=(TextView) rootView.findViewById(R.id.txtlbl_intrvl_int);
		tv_int_frq=(TextView) rootView.findViewById(R.id.txtlbl_intrvl_int_frq);
		txt_pnd_instl=(EditText) rootView.findViewById(R.id.pndt_instmnt);
		txt_intvl_int=(EditText) rootView.findViewById(R.id.intrvl_int);
		txt_intv_int_frq=(EditText) rootView.findViewById(R.id.intrvl_int_frq);
		ifsc_code=(EditText) rootView.findViewById(R.id.ifsc_code);
		//sanct_limit=(EditText) rootView.findViewById(R.id.sanct_limit);
		//txt_lien_amt=(EditText) rootView.findViewById(R.id.txt_lien_amt);
		
		btn_home1 = (ImageView) rootView.findViewById(R.id.btn_home1);
		btn_logout = (ImageView) rootView.findViewById(R.id.btn_logout);
		/*btn_back = (ImageButton) rootView.findViewById(R.id.btn_back);*/

		//btn_home.setImageResource(R.drawable.ic_home_d);
		//btn_back.setImageResource(R.drawable.backover);

		btn_home1.setOnClickListener(this);
		btn_logout.setOnClickListener(this);

		txt_heading.setText(getString(R.string.lbl_acc_details));
		//Log.e("FdRdAccountDetail", strtext);
		// 5-101-FD-3355-KADEKAR PRAKASH KIRAN
		dbms = new DatabaseManagement("shree_nagari.mbank", "listMobileBanking");

	//	SharedPreferences sp = act.getSharedPreferences(MY_SESSION,Context.MODE_PRIVATE);
	//	e = sp.edit();
	//	custid = sp.getString("custId", "custId");
		String txt; //sp.getString("retValStr", "retValStr");
	Cursor c1=dbms.selectFromTable("SHAREDPREFERENCE", "", null);//("select * from ", null);
        if(c1!=null)
        {
        	while(c1.moveToNext())
	        {	
        		custid=c1.getString(2);
	        	//Log.e("custId","......"+custid);
	        	txt=c1.getString(0);
		    	//Log.e("retvalstr","c......"+txt);
	        }
        }
		Log.e("OMKAR", accstr);
		accstr=accstr.replaceAll("#", "-");
		Log.e("OMKAR", accstr);
		if(accstr.split("-")[2].equalsIgnoreCase("PG") || 
				accstr.split("-")[2].equalsIgnoreCase("RA"))
		{
			txtLayout.setVisibility(LinearLayout.VISIBLE);
			edtLayout.setVisibility(LinearLayout.VISIBLE);
			txt_lbl_underLiened.setVisibility(LinearLayout.GONE);
			txt_underliened.setVisibility(LinearLayout.GONE);
			tv_curbal.setVisibility(LinearLayout.VISIBLE);
			txt_curbal.setVisibility(LinearLayout.VISIBLE);
			txt_intvl_int.setVisibility(EditText.GONE);
			tv_intvl_int.setVisibility(TextView.GONE);
			tv_int_frq.setVisibility(TextView.GONE);
			txt_intv_int_frq.setVisibility(EditText.GONE);
			if(accstr.split("-")[2].equalsIgnoreCase("PG"))
			{
				amtTxtLayout.setVisibility(LinearLayout.GONE);
				amtEditLayout.setVisibility(LinearLayout.GONE);
				tv_pnd_instal.setVisibility(TextView.GONE);
				txt_pnd_instl.setVisibility(EditText.GONE);
			}
			else
			{
				amtTxtLayout.setVisibility(LinearLayout.VISIBLE);
				amtEditLayout.setVisibility(LinearLayout.VISIBLE);
				tv_pnd_instal.setVisibility(TextView.VISIBLE);
				txt_pnd_instl.setVisibility(EditText.VISIBLE);
			}
		}
		else
		{
			txtLayout.setVisibility(LinearLayout.GONE);
			edtLayout.setVisibility(LinearLayout.GONE);
			txt_lbl_underLiened.setVisibility(LinearLayout.VISIBLE);
			txt_underliened.setVisibility(LinearLayout.VISIBLE);
			tv_curbal.setVisibility(LinearLayout.GONE);
			txt_curbal.setVisibility(LinearLayout.GONE);
			txt_intvl_int.setVisibility(EditText.VISIBLE);
			tv_intvl_int.setVisibility(TextView.VISIBLE);
			tv_pnd_instal.setVisibility(TextView.GONE);
			txt_pnd_instl.setVisibility(EditText.GONE);
			tv_int_frq.setVisibility(TextView.VISIBLE);
			txt_intv_int_frq.setVisibility(EditText.VISIBLE);
		}
		
		String accountNoStr = accNo.getText() + " "	+ accInfo;//MBSUtils.get16digitsAccNo(accstr);
		accNo.setText(accountNoStr);

		String custName = MBSUtils.getCustName(accountNo);
		txt_cust_name.setText(custName);

			
		flag = chkConnectivity();
		if (flag == 0) 
		{
			CallWebServiceGetFDAccDetails c=new CallWebServiceGetFDAccDetails();
			c.execute();
		}
		return rootView;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		/*case R.id.btn_back:
			//Log.tem.out.println("Clicked on back");
			Bundle bundle = new Bundle();
			Fragment fragment = new HomeFragment(act);
			bundle.putInt("CHECKACTTYPE", 2);
			fragment.setArguments(bundle);
			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.frame_container, fragment).commit();
			break;*/
		case R.id.btn_home1:
			Intent in = new Intent(act, NewDashboard.class);
			in.putExtra("VAR1", var1);
			in.putExtra("VAR3", var3);
			startActivity(in);
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
			dbs.get_adb().show();*//**/
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
			   	JSONObject jsonObj;
			   	loadProBarObj.dismiss();
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

	
	class CallWebServiceGetFDAccDetails extends AsyncTask<Void, Void, Void> {// CallWebServiceGetFDAccDetails

		String retval = "";
		LoadProgressBar loadProBarObj = new LoadProgressBar(act);
		JSONObject jsonObj = new JSONObject();
		String ValidationData="";

		@Override
		protected void onPreExecute() 
		{
try{
	respcode="";
	retvalweb="";
	respdesc="";
			loadProBarObj.show();
			  
			  jsonObj.put("CUSTID", custid+"#~#"+AccCustId);
              jsonObj.put("ACCNO", accountNo);
              jsonObj.put("IMEINO", MBSUtils.getImeiNumber(act));
              jsonObj.put("SIMNO", MBSUtils.getSimNumber(act));
              jsonObj.put("METHODCODE","31");
            //  ValidationData=MBSUtils.getValidationData(act,jsonObj.toString());
		
			 
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
				e.printStackTrace();
				//Log.e("FdRdAccountDetail", retVal);
				System.out.println("FdRdAccountDetail   Exception" + e);
			}
			return null;
		}// end doInBackground

		protected void onPostExecute(final Void result) 
		{
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
					retvalweb = jsonObj.getString("RETVAL");
				}
				else
				{
					retvalweb = "";
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
			//Log.e("Debug@decryptedRetVal", decryptedRetVal);

			if (retvalweb.indexOf("FAILED") > -1) 
			{
				retMess = getString(R.string.alert_092);
				showAlert(retMess);
			} 		
			else 
			{
				post_success(retvalweb);
				
				
			}
			}/*
				}
				else{
					MBSUtils.showInvalidResponseAlert(act);	
				}*/
			} catch (JSONException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}// end onPostExecute

	}// end CallWebServiceGetFDAccDetails


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
							post_success(retvalweb);
						}
						else if((str.equalsIgnoreCase(respdesc)) && (respcode.equalsIgnoreCase("1")))
						{
							this.dismiss();
						}
						else if(str.equalsIgnoreCase(getString(R.string.alert_092)))
						{
							System.out.println("Clicked on back");
							Bundle bundle=new Bundle();
							Fragment fragment = new HomeFragment(act);
							bundle.putInt("CHECKACTTYPE", 2);
							fragment.setArguments(bundle);
							FragmentManager fragmentManager = getFragmentManager();
							fragmentManager.beginTransaction()
									.replace(R.id.frame_container, fragment).commit();
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

		ConnectivityManager cm = (ConnectivityManager) act.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		try {
			State state = ni.getState();
			boolean state1 = ni.isAvailable();
			//Log.tem.out.println("state1 ---------" + state1);
			if (state1) {
				switch (state) {
				case CONNECTED:
					if (ni.getType() == ConnectivityManager.TYPE_MOBILE
							|| ni.getType() == ConnectivityManager.TYPE_WIFI) {
						//pb_wait.setVisibility(ProgressBar.VISIBLE);
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
					//retMess = "Network Disconnected. Please Check Network Settings.";
					retMess = getString(R.string.alert_014);
					showAlert(retMess);
					break;
				default:
					flag = 1;
					//retMess = "Network Unavailable. Please Try Again.";
					retMess=getString(R.string.alert_000);
					showAlert(retMess);
					break;
				}
			} else {
				flag = 1;
				retMess = getString(R.string.alert_000);
				showAlert(retMess);
			}
		} catch (NullPointerException ne) {

			Log.i("mayuri", "NullPointerException Exception" + ne);
			flag = 1;
			//retMess = "Can Not Get Connection. Please Try Again.";
			retMess = getString(R.string.alert_000);
			showAlert(retMess);

		} catch (Exception e) {
			Log.i("mayuri", "Exception" + e);
			flag = 1;
			//retMess = "Connection Problem Occured.";
			retMess = getString(R.string.alert_000);
			showAlert(retMess);
		}
		return flag;
	}
	
	public 	void post_success(String retvalweb)
	{
		respcode="";
		respdesc="";
		retvalweb=retvalweb.split("SUCCESS~")[1];
		String[] retValues=retvalweb.split("#");
		
		if(accstr.split("-")[2].equalsIgnoreCase("FD") ||
				accstr.split("-")[2].equalsIgnoreCase("CD") ||
				accstr.split("-")[2].equalsIgnoreCase("RP"))
		{
			//SUCCESS~2762#02-APR-18#8#2222#Y#02-APR-15#Mrs. SHARMA VIBHOR  P
			
			maturity_amnt.setText(MBSUtils.amountFormat(retValues[0], false, act));
			maturity_date.setText(retValues[1]);
			intrest_value.setText(retValues[2]);
			opening_amnt.setText(MBSUtils.amountFormat(retValues[3], false, act));
			if(retValues[4].equalsIgnoreCase("Y"))
			{
				txt_underliened.setText("Yes");
			}
			else
			{
				txt_underliened.setText("No");
			}
			opng_dt.setText(retValues[5]);
			txt_intvl_int.setText(MBSUtils.amountFormat(retValues[6], false, act));
			txt_intv_int_frq.setText(retValues[7]);
			txt_cust_name.setText(retValues[8]);
			ifsc_code.setText(retValues[9]);
			/*sanct_limit.setText(retValues[10]);
			txt_lien_amt.setText(retValues[11]);*/
			
		}
		else/*if(accstr.split("-")[2].equalsIgnoreCase("PG") || 
			accstr.split("-")[2].equalsIgnoreCase("RA"))*/
		{
			if(!accstr.split("-")[2].equalsIgnoreCase("PG"))
					maturity_amnt.setText(MBSUtils.amountFormat(retValues[0], false, act));
			maturity_date.setText(retValues[1]);
			intrest_value.setText(retValues[2]);
			opening_amnt.setText(MBSUtils.amountFormat(retValues[3], false, act));
			opng_dt.setText(retValues[4]);
			txt_instl_amt.setText(MBSUtils.amountFormat(retValues[5], false, act));
			txt_instl_frq.setText(retValues[6]);
			txt_curbal.setText(MBSUtils.amountFormat(retValues[7], false, act));
			txt_pnd_instl.setText(retValues[8]);
			txt_cust_name.setText(retValues[9]);
			ifsc_code.setText(retValues[10]);
			//SUCCESS~2762#02-APR-18#8#2222#Y#02-APR-15#Mrs. SHARMA VIBHOR  P
			
		}
	}
}

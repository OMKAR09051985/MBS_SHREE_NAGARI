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

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
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
import android.widget.LinearLayout;
import android.widget.TextView;

//@SuppressLint("NewApi")
public class LoanAccountDetail extends Fragment implements OnClickListener
{
	//Editor e;
	MainActivity act;
	LoanAccountDetail fdrddtl;
	LinearLayout layout_lable,layout_text1,layout_text2;
	TextView txt_heading,cust_nm,rate_of_interest, sanction_limit, drawing_power , accNo,txt_cust_name,txtlbl_instlmnt_frq,txtlbl_instlmnt_amt,txtlbl_pnd_instlmnts,txtlbl_principal,txtlbl_interest;
	EditText txt_current_bal,txt_utilisable_amt,txt_instlmnt_frq,txt_instlmnt_amt,txt_pnd_instlmnts,
	txt_pnd_interest,txt_instlmnt_interest,txt_pend_pinstlmntamt,lien_amt,ifsc_code;
	Button btnChangeMpin;
	public Bundle getBundle = null;
	Cursor curSelectBankname;
	ImageButton btn_home;//,btn_back;
	ImageView btn_home1,btn_logout;
	String retMess = "", retVal = "",custid="",accountNo="",retvalwbs="",retval="",
			accStr="",accountinfo="",respcode="",retvalweb="",respdesc="",AccCustId;
	private static String NAMESPACE = "";
	private static String URL = "";
	private static String SOAP_ACTION = "";
	private static  String METHOD_NAME = "";
	private static final String MY_SESSION = "my_session";
	int flag=0;
	ImageView img_heading;
	DatabaseManagement dbms;
	DialogBox dbs;
	PrivateKey var1=null;	  
	String var5="",var3="";
	SecretKeySpec var2=null;
	public LoanAccountDetail(){}
	
	@SuppressLint("ValidFragment")
	public LoanAccountDetail(MainActivity a) {
		act=a;
		fdrddtl=this;	
		///tf_mtcorsva = Typeface.createFromAsset(act.getAssets(),"fonts/Kozuka-Gothic-Pro-M_26793.ttf");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub	
		dbms = new DatabaseManagement("shree_nagari.mbank", "listMobileBanking");
		var1 = act.var1;
		var3 = act.var3;
		Bundle b1=getArguments();
		if(b1!=null)
		{
			accountNo=b1.getString("accountnumber");
			accStr=b1.getString("accountstr");
			accountinfo = b1.getString("accountinfo");
			AccCustId = b1.getString("AccCustId");
			Log.e("onCreateView","accountNo=="+accountNo);
			Log.e("onCreateView","accStr=="+accStr);
			Log.e("onCreateView","accountinfo=="+accountinfo);
		}
		View rootView = inflater.inflate(R.layout.loanaccount_details, container, false);
		img_heading=(ImageView)rootView.findViewById(R.id.img_heading);
        img_heading.setBackgroundResource(R.mipmap.loan);
		//System.out.println("onCreateView() LOAN Account Details");
		accNo = (TextView) rootView.findViewById(R.id.accNo);
		txt_cust_name=(TextView)rootView.findViewById(R.id.cust_name);
		rate_of_interest=(TextView)rootView.findViewById(R.id.txt_rate_of_interest);
		sanction_limit=(TextView)rootView.findViewById(R.id.txt_sanction_limit);
		drawing_power=(TextView)rootView.findViewById(R.id.txt_drawing_power);
		txt_heading=(TextView)rootView.findViewById(R.id.txt_heading);
		txt_current_bal=(EditText)rootView.findViewById(R.id.ed_current_bal);
		txt_utilisable_amt=(EditText)rootView.findViewById(R.id.txt_drawable_amt);
		
		//pending installments
		txtlbl_instlmnt_amt=(TextView)rootView.findViewById(R.id.txtlbl_instl_amt);
		txt_instlmnt_amt=(EditText)rootView.findViewById(R.id.txt_instlmnt_amt);
		txtlbl_instlmnt_frq=(TextView)rootView.findViewById(R.id.txtlbl_instl_frq);
		txt_instlmnt_frq=(EditText)rootView.findViewById(R.id.txt_instlmnt_frq);
		txtlbl_pnd_instlmnts=(TextView)rootView.findViewById(R.id.txtlbl_pnd_instlmnts);
		txt_pnd_instlmnts=(EditText)rootView.findViewById(R.id.txt_pnd_instlmnts);
		txtlbl_principal=(TextView)rootView.findViewById(R.id.txtlbl_principal);
		txtlbl_interest=(TextView)rootView.findViewById(R.id.txtlbl_interest);
		txt_pnd_interest=(EditText)rootView.findViewById(R.id.txt_pnd_interest);
		txt_instlmnt_interest=(EditText)rootView.findViewById(R.id.txt_instlmnt_interest);
		layout_lable=(LinearLayout)rootView.findViewById(R.id.layout_lable);
		layout_text1=(LinearLayout)rootView.findViewById(R.id.layout_text1);
		layout_text2=(LinearLayout)rootView.findViewById(R.id.layout_text2);
		txt_pend_pinstlmntamt=(EditText)rootView.findViewById(R.id.txt_pndinstlmnt_amt);
		lien_amt=(EditText)rootView.findViewById(R.id.txt_lien_amt);
		ifsc_code=(EditText)rootView.findViewById(R.id.txt_ifsc_code);
		
		if(accStr.split("-")[7].equalsIgnoreCase("I"))
		{
			txtlbl_instlmnt_amt.setVisibility(TextView.VISIBLE);
			txt_instlmnt_amt.setVisibility(EditText.VISIBLE);
			txtlbl_instlmnt_frq.setVisibility(TextView.VISIBLE);
			txt_instlmnt_frq.setVisibility(EditText.VISIBLE);
			txtlbl_pnd_instlmnts.setVisibility(TextView.VISIBLE);
			txt_pnd_instlmnts.setVisibility(EditText.VISIBLE);
			txt_pnd_interest.setVisibility(EditText.VISIBLE);
			txt_instlmnt_interest.setVisibility(EditText.VISIBLE);
			layout_lable.setVisibility(EditText.VISIBLE);
			layout_text1.setVisibility(EditText.VISIBLE);
			layout_text2.setVisibility(EditText.VISIBLE);
		}
		else
		{
			//txtlbl_instlmnt_amt.setVisibility(TextView.INVISIBLE);
			//txt_instlmnt_amt.setVisibility(EditText.INVISIBLE);
			txtlbl_instlmnt_frq.setVisibility(TextView.INVISIBLE);
			txt_instlmnt_frq.setVisibility(EditText.INVISIBLE);
			txtlbl_pnd_instlmnts.setVisibility(TextView.INVISIBLE);
			txt_pnd_instlmnts.setVisibility(EditText.INVISIBLE);
			txt_pnd_interest.setVisibility(EditText.INVISIBLE);
			txt_instlmnt_interest.setVisibility(EditText.INVISIBLE);
			layout_lable.setVisibility(EditText.INVISIBLE);
			layout_text1.setVisibility(EditText.INVISIBLE);
			layout_text2.setVisibility(EditText.INVISIBLE);
			
		}	
		btn_home=(ImageButton)rootView.findViewById(R.id.btn_home);
		/*btn_back=(ImageButton)rootView.findViewById(R.id.btn_back);*/
		btn_home1 = (ImageView) rootView.findViewById(R.id.btn_home1);
		btn_logout = (ImageView) rootView.findViewById(R.id.btn_logout);
btn_logout.setVisibility(View.GONE);
		/*btn_back = (ImageButton) rootView.findViewById(R.id.btn_back);*/
        //btn_home.setImageResource(R.drawable.ic_home_d);
		//btn_back.setImageResource(R.drawable.backover);

		btn_home1.setOnClickListener(this);
		btn_logout.setOnClickListener(this);
		
		txt_heading.setText(getString(R.string.lbl_acc_details));
		//Log.e("LoanAccountDetail",strtext);
		
		String accountNoStr = accNo.getText()+" "+accountinfo;//MBSUtils.get16digitsAccNo(strtext);
		accNo.setText(accountNoStr);
		
	//	SharedPreferences sp = act.getSharedPreferences(MY_SESSION,Context.MODE_PRIVATE);
		//e = sp.edit();
	//	custid = sp.getString("custId", "custId");
	Cursor c1=dbms.selectFromTable("SHAREDPREFERENCE", "", null);//("select * from ", null);
        if(c1!=null)
        {
        	while(c1.moveToNext())
	        {	
        		custid=c1.getString(2);
	        	Log.e("custId","......"+custid);
	        }
        }
		
		String custName = MBSUtils.getCustName(accStr);
		txt_cust_name.setText(custName);
		
		
		
		//btn_back.setOnClickListener(this);
		btn_home.setOnClickListener(this);
		 
		flag = chkConnectivity();
		if (flag == 0) 
		{
			new CallWebServiceGetLoanAccDetails().execute();
		}
		return rootView;
	}
	
	@Override
	public void onClick(View v) 
	{
		switch (v.getId()) 
		{
			/*case R.id.btn_back:
				//System.out.println("Clicked on back");
				Bundle bundle=new Bundle();
				Fragment fragment = new HomeFragment(act);
				bundle.putInt("CHECKACTTYPE", 3);
				fragment.setArguments(bundle);
				FragmentManager fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction()
						.replace(R.id.frame_container, fragment).commit();
				break;*/
				
			case R.id.btn_home:
				Intent in=new Intent(act,NewDashboard.class);
				in.putExtra("VAR1", var1);
				in.putExtra("VAR3", var3);
				startActivity(in);
				act.finish();
				break;
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

	class CallWebServiceGetLoanAccDetails extends AsyncTask<Void, Void, Void> {// CallWebServiceGetLoanAccDetails

		String retval = "";
		LoadProgressBar loadProBarObj = new LoadProgressBar(act);
		JSONObject jsonObj = new JSONObject();
		String ValidationData="";

		@Override
		protected void onPreExecute() {
	try{
		respcode="";
		retval="";
		respdesc="";
			// p_wait.setVisibility(ProgressBar.VISIBLE);
			loadProBarObj.show();
			jsonObj.put("CUSTID", custid+"#~#"+AccCustId);
            jsonObj.put("ACCNO", accountNo);
            jsonObj.put("IMEINO", MBSUtils.getImeiNumber(act));		
            jsonObj.put("SIMNO", MBSUtils.getSimNumber(act));
            jsonObj.put("METHODCODE","32");
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
				Log.e("LoanAccountDetail", retVal);
				System.out.println("LoanAccountDetail   Exception" + e);
			}
			return null;
		}// end doInBackground

		protected void onPostExecute(final Void result) 
		{
				JSONObject jsonObj;
			try
			{

				String str=CryptoClass.Function6(var5,var2);
				 jsonObj = new JSONObject(str.trim());
				loadProBarObj.hide();
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
			if (retvalweb.indexOf("FAILED") > -1) 
			{
				retMess = getString(R.string.alert_092);
				showAlert(retMess);
			} 		
			else 
			{

				post_success(retvalweb);
			
				
			}}/*
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
	}// end CallWebServiceGetLoanAccDetails


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
							bundle.putInt("CHECKACTTYPE", 3);
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
	
	
	public 	void post_success(String retvalweb)
	{
		Log.e("DSP","loandetails==="+retvalweb);
		respcode="";
		respdesc="";
	String data=retvalweb.split("SUCCESS~")[1];
		//retval=retval.split("SUCCESS~")[1];
		String[] retValues=data.split("#");
		
		rate_of_interest.setText(retValues[0]);
		sanction_limit.setText(MBSUtils.amountFormat(retValues[1], false, act));
		drawing_power.setText(MBSUtils.amountFormat(retValues[2], false, act));
		/*Double curBal=Double.parseDouble(retValues[3]);
		if(curBal>0)
			txt_current_bal.setText(curBal+" "+getString(R.string.lbl_credit_short));
		else if(curBal<0)
			txt_current_bal.setText((curBal*-1)+" "+getString(R.string.lbl_debit_short));
		else
			txt_current_bal.setText(""+curBal);*/
		txt_current_bal.setText(MBSUtils.amountFormat(retValues[3], true, act));
		txt_utilisable_amt.setText(MBSUtils.amountFormat(retValues[4], false, act));
		if(accStr.split("-")[7].equalsIgnoreCase("I"))
		{
			txt_instlmnt_frq.setText(retValues[5]);
			txt_instlmnt_amt.setText(MBSUtils.amountFormat(retValues[6], false, act));
			txt_pnd_instlmnts.setText(retValues[7]);
			txt_pnd_interest.setText(retValues[8]);
			txt_pend_pinstlmntamt.setText(retValues[9]);
			txt_instlmnt_interest.setText(retValues[10]);
			
		}
		ifsc_code.setText(retValues[12]);
		lien_amt.setText(retValues[13]);
		
	}
	public int chkConnectivity() { 
		ConnectivityManager cm = (ConnectivityManager) act.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		try {
			State state = ni.getState();
			boolean state1 = ni.isAvailable();
			//System.out.println("state1 ---------" + state1);
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
			Log.e("EXCEPTION", "---------------"+ne);
			Log.i("mayuri", "NullPointerException Exception" + ne);
			flag = 1;
			//retMess = "Can Not Get Connection. Please Try Again.";
			retMess = getString(R.string.alert_000);
			showAlert(retMess);

		} catch (Exception e) {
			Log.e("EXCEPTION", "---------------"+e);
			Log.i("mayuri", "Exception" + e);
			flag = 1;
			//retMess = "Connection Problem Occured.";
			retMess = getString(R.string.alert_000);
			showAlert(retMess);
		}
		return flag;
	}
}

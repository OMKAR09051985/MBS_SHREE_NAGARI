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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
//import android.annotation.SuppressLint;

//@SuppressLint("NewApi")
public class BalanceRep extends Fragment implements OnClickListener 
{
	//Editor e;
	MainActivity act;
	DialogBox dbs;
	BalanceRep balRep;
	TextView actype, branch, sch_acno, name, bal,txt_heading,ifsccode,lienamt,sanctionlimit;
	ImageView btn_home1,btn_logout;//, back;
	EditText unclearbal;
	ImageView img_heading;
	String actype_val, branch_val, sch_acno_val, name_val, bal_val,retvalwbs,retval;
	String str = "", spi_str = "";
	String balance;
	String retMess = "", retVal = "",custid="",accountNo="",respcode="",retvalweb="",respdesc="",AccCustId;
	int cnt = 0, flag = 0;
		DatabaseManagement dbms;
	private static String NAMESPACE = "";
	private static String URL = "";
	private static String SOAP_ACTION = "";
	private static  String METHOD_NAME = "";
	private static final String MY_SESSION = "my_session";
	PrivateKey var1=null;	  
	String var5="",var3="";
	SecretKeySpec var2=null;
	
	public BalanceRep(){}
	
	@SuppressLint("ValidFragment")
	public BalanceRep(MainActivity a)
	{
		//System.out.println("BalanceRep()"+a);
		try
		{
			act = a;
			balRep=this;
		}
		catch(Exception e)
		{
			Log.e("BalanceRep()","Exce:"+e);
		}
	}
	@SuppressLint("ValidFragment")
	public BalanceRep(MainActivity a, String str1, String spi_str1, String bal)
	{
		System.out.println("BalanceRep()"+a);
		try
		{
			act = a;
			balRep=this;
			
			str = str1;
			spi_str = spi_str1;
			balance = bal;
			/*Typeface tf_calibri = Typeface.createFromAsset(act.getAssets(),
			        "fonts/Kozuka-Gothic-Pro-M_26793.ttf");*/
		}
		catch(Exception e)
		{
			Log.e("BalanceRep()","Exce:"+e);
		}
	}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) 
	{		
		System.out.println("onCreateView() BalanceRep");
	dbms = new DatabaseManagement("shree_nagari.mbank", "listMobileBanking");	
        View rootView = inflater.inflate(R.layout.balance_report, container, false);
        var1 = act.var1;
        var3 = act.var3;
        img_heading=(ImageView)rootView.findViewById(R.id.img_heading);
        img_heading.setBackgroundResource(R.mipmap.savings);
			actype = (TextView) rootView.findViewById(R.id.txt_acnt_type);
		branch = (TextView) rootView.findViewById(R.id.txt_branch);
		sch_acno = (TextView) rootView.findViewById(R.id.txt_sch_acno);
		name = (TextView) rootView.findViewById(R.id.txt_name);
		bal = (TextView) rootView.findViewById(R.id.txt_bal);
		unclearbal=(EditText)rootView.findViewById(R.id.unclearbal);
		ifsccode = (TextView) rootView.findViewById(R.id.txt_ifsccode);
		lienamt = (TextView) rootView.findViewById(R.id.txt_lienamt);
		//sanctionlimit = (TextView) rootView.findViewById(R.id.txt_sanctionlimit);
	/*	back = (ImageButton) rootView.findViewById(R.id.btn_back);*/
		
		btn_home1= (ImageView) rootView.findViewById(R.id.btn_home1);
		btn_logout = (ImageView) rootView.findViewById(R.id.btn_logout);
btn_logout.setVisibility(View.GONE);
		txt_heading=(TextView) rootView.findViewById(R.id.txt_heading);
		txt_heading.setText(getString(R.string.lbl_acc_details));
		//btn_home.setImageResource(R.drawable.ic_home_d);
		//back.setImageResource(R.drawable.backover);
		//back.setOnClickListener(this);
		btn_home1.setOnClickListener(new View.OnClickListener() 
		{
			@Override
			public void onClick(View view) 
			{
			Intent in=new Intent(act,NewDashboard.class);
			in.putExtra("VAR1", var1);
			in.putExtra("VAR3", var3);
			startActivity(in);
			act.finish();
			}
		});
		btn_logout.setOnClickListener(new View.OnClickListener() 
        {		
			@Override
			public void onClick(View view) 
			{
				CustomDialogClass alert=new CustomDialogClass(act, getString(R.string.lbl_exit)) {
					@Override
					public void onClick(View v) {
						switch (v.getId()) {
							case R.id.btn_ok:
								flag = chkConnectivity();
								if (flag == 0)
								{
									CallWebService c=new CallWebService();
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
							CallWebService c=new CallWebService();
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
			}
		});
		Bundle b1=getArguments();
		if(b1!=null)
		{
			accountNo=b1.getString("accountnumber");
			AccCustId=b1.getString("AccCustId");

		}
			Cursor c1=dbms.selectFromTable("SHAREDPREFERENCE", "", null);//("select * from ", null);
        if(c1!=null)
        {
        	while(c1.moveToNext())
	        {	
        		custid=c1.getString(2);
	        	//Log.e("custId","......"+custid);
	        }
        }
		
		//setValues();
		
		flag = chkConnectivity();
		if (flag == 0) 
		{
			CallWebServiceGetOperativeAccDetails c=new CallWebServiceGetOperativeAccDetails();
			c.execute();
		}
        return rootView;
    }
	class CallWebService extends AsyncTask<Void, Void, Void> {
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
	public void setValues(String str) 
	{		
		String sterval=str.split("SUCCESS~")[1];
		
		String[] retValues=sterval.split("#");
		
		branch.setText(retValues[0]);
		
		
		if(retValues[1].equalsIgnoreCase("SB"))
		{
			actype.setText("Savings");
		}
		else if(retValues[1].equalsIgnoreCase("LO"))
		{
			actype.setText("Loan");
		}
		else if(retValues[1].equalsIgnoreCase("RP"))
		{
			actype.setText("Re-Investment Plan");
		}
		else if(retValues[1].equalsIgnoreCase("FD"))
		{
			actype.setText("Fixed Deposite");
		}
		else if(retValues[1].equalsIgnoreCase("CA"))
		{
			actype.setText("Current Account");
		}
		else if(retValues[1].equalsIgnoreCase("PG"))
		{
			actype.setText("Pigmi Account");
		}
		sch_acno.setText(accountNo);
		
		name.setText(retValues[2]);
		/*if(Double.parseDouble(retValues[3])>0)
			bal.setText(retValues[3]+" "+getString(R.string.lbl_credit_short));
		else if(Double.parseDouble(retValues[3])<0)
			bal.setText(retValues[3]+" "+getString(R.string.lbl_debit_short));
		else 
			bal.setText(retValues[3]);*/
		bal.setText(MBSUtils.amountFormat(retValues[3],true,act));
		unclearbal.setText(retValues[4]);
		ifsccode.setText(retValues[5]);
		lienamt.setText(retValues[6]);
		//sanctionlimit.setText(retValues[7]);
	}

	@Override
	public void onClick(View v) 
	{
		switch (v.getId()) 
		{
			/*case R.id.btn_back:
				System.out.println("Clicked on back");
				Bundle bundle=new Bundle();
				Fragment fragment = new HomeFragment(act);
				bundle.putInt("CHECKACTTYPE", 1);
				fragment.setArguments(bundle);
				FragmentManager fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction()
						.replace(R.id.frame_container, fragment).commit();
				break;*/
				
			case R.id.btn_home1:
				Intent in=new Intent(act,NewDashboard.class);
				in.putExtra("VAR1", var1);
				in.putExtra("VAR3", var3);
				startActivity(in);
				act.finish();
				break;
		}
	}
	
	public int chkConnectivity() {
		ConnectivityManager cm = (ConnectivityManager) act.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		try {
			State state = ni.getState();
			boolean state1 = ni.isAvailable();
			System.out.println("state1 ---------" + state1);
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

			//Log.i("mayuri", "NullPointerException Exception" + ne);
			flag = 1;
			//retMess = "Can Not Get Connection. Please Try Again.";
			retMess = getString(R.string.alert_000);
			showAlert(retMess);

		} catch (Exception e) {
			//Log.i("mayuri", "Exception" + e);
			flag = 1;
			//retMess = "Connection Problem Occured.";
			retMess = getString(R.string.alert_000);
			showAlert(retMess);
		}
		return flag;
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
							setValues(retvalweb);
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
							bundle.putInt("CHECKACTTYPE", 1);
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
	
	
	class CallWebServiceGetOperativeAccDetails extends AsyncTask<Void, Void, Void> {// CallWebServiceGetOperativeAccDetails

		String retval = "";
		LoadProgressBar loadProBarObj = new LoadProgressBar(act);
       JSONObject jsonObj = new JSONObject();
		String ValidationData="";

		@Override
		protected void onPreExecute() 
		{
      try{
			loadProBarObj.show();
			respcode="";
			retval="";
			respdesc="";
			  
              //jsonObj.put("CUSTID", custid+"#~#"+AccCustId);
              jsonObj.put("CUSTID", custid);
              jsonObj.put("ACCNO", accountNo);
              jsonObj.put("IMEINO",  MBSUtils.getImeiNumber(act));
              jsonObj.put("SIMNO", MBSUtils.getSimNumber(act));
              jsonObj.put("METHODCODE","33");
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
		try{	
			
			if (retvalweb.indexOf("FAILED") > -1) 
			{
				post_success(retvalweb);
				
			} 		
			else 
			{
			
				setValues(retvalweb);
			}
	}
		catch (Exception je) {
            je.printStackTrace();
        }}
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

	}// end CallWebServiceGetOperativeAccDetails

	public 	void post_success(String retvalweb)
	{
		respcode="";
		
		respdesc="";
		retMess = getString(R.string.alert_092);
		showAlert(retMess);
	}
}

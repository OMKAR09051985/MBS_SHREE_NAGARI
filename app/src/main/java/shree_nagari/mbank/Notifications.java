package shree_nagari.mbank;

import java.security.PrivateKey;

import javax.crypto.spec.SecretKeySpec;

import mbLib.CryptoClass;
import mbLib.CusFntTextView;
import mbLib.CustomDialogClass;
import mbLib.DatabaseManagement;
import mbLib.DialogBox;
import mbLib.MBSUtils;

import org.json.JSONArray;
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
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

@SuppressLint("ValidFragment")
public class Notifications extends Fragment implements OnClickListener
{
	MainActivity act;
	LinearLayout notification_layout;
	CusFntTextView dynamicmsg;
	ImageView img_heading;
	String respcode="",retval="",	respdesc="",retVal="",retvalwbs="",stringValue="",custId="";
	private static String NAMESPACE = "";
	private static String URL = "";
	private static String SOAP_ACTION = "";
	private static  String METHOD_NAME1 = "",METHOD_NAME="";
	ImageView btn_home1,btn_logout;
	Button btn_deptintrates,btn_loanintrates;
	DatabaseManagement dbms;
	DialogBox dbs;
	int flag=0;
	String retMess="";
	PrivateKey var1=null;	  
	String var5="",var3="";
	SecretKeySpec var2=null;
	
	@SuppressLint("ValidFragment")
	public Notifications(MainActivity a)
	{
		System.out.println("FinancialMenuActivity()"+a);
		act = a;
	}
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) 
	{	
		var1 = act.var1;
		var3 = act.var3;
		dbms = new DatabaseManagement("shree_nagari.mbank", "listMobileBanking");
		Cursor c1 = dbms.selectFromTable("SHAREDPREFERENCE", "", null);
		if (c1 != null) 
		{
			while (c1.moveToNext()) 
			{
				stringValue = c1.getString(0);//ListEncryption.decryptData(c1.getString(0));
				custId = c1.getString(2);//ListEncryption.decryptData(c1.getString(2));
				
			}
		}
		System.out.println("Notifications onCreateView()");		
        View rootView = inflater.inflate(R.layout.notifications, container, false);
        img_heading = (ImageView) rootView.findViewById(R.id.img_heading);
		img_heading.setBackgroundResource(R.mipmap.notification);
        notification_layout = (LinearLayout)rootView.findViewById(R.id.notification_layout);
        btn_home1 = (ImageView) rootView.findViewById(R.id.btn_home1);
		btn_logout = (ImageView) rootView.findViewById(R.id.btn_logout);
		btn_deptintrates = (Button) rootView.findViewById(R.id.btn_deptintrates);
		btn_loanintrates = (Button) rootView.findViewById(R.id.btn_loanintrates);
		btn_home1.setOnClickListener(this);
		btn_logout.setOnClickListener(this);
		btn_deptintrates.setOnClickListener(this);
		btn_loanintrates.setOnClickListener(this);
		
        dynamicmsg=new CusFntTextView(act);
        new CallWebService_dynamic_msg().execute();
        
        return rootView;
	}
	
	class CallWebService_dynamic_msg extends AsyncTask<Void, Void, Void> {
		JSONObject jsonObj = new JSONObject();
		String ValidationData="";

		@Override
		protected void onPreExecute() {
			try {
				respcode="";
				retval="";
				respdesc="";
				jsonObj.put("IMEINO", MBSUtils.getImeiNumber(act));
				jsonObj.put("SIMNO", MBSUtils.getSimNumber(act));
				jsonObj.put("METHODCODE","53");
				// ValidationData=MBSUtils.getValidationData(act,jsonObj.toString());
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

				System.out.println(e.getMessage());

			}
			return null;
		}

		@Override
		protected void onPostExecute(final Void result) {

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
			if (retval.indexOf("FAILED") > -1) {
				post_success(retval);
				/*Log.e("FAILED= ", "FAILED=");*/
			} else {
				try {
					Log.e("DDDDD1111", "TRY");
					JSONArray ja = new JSONArray(retval);

					Log.e("DDDDD2222", "DATA" + ja);
					int count = 0;
					String data = "";
					for (int j = 0; j < ja.length(); j++) {
						JSONObject jObj = ja.getJSONObject(j);
						// arrList.add(jObj.getString("mm_msg"));

						Log.e("LIST=", "length=" + data.length());
						if (data.length() == 0) {
							data = jObj.getString("mm_msg");
						} else {
							data = data + ".    " + jObj.getString("mm_msg")
									+ ".   ";
						}
						count++;
					}
					data = MBSUtils.lPad(data, 51, " ");

					dynamicmsg.setText(data);
			    	dynamicmsg.setTextSize(18);
			    	dynamicmsg.setGravity(Gravity.CENTER);
			    	LinearLayout.LayoutParams titleParam=new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
			    	titleParam.setMargins(0, 10, 0, 15);
			    	dynamicmsg.setLayoutParams(titleParam);
			    	notification_layout.addView(dynamicmsg);
				}

				catch (JSONException je) {
					je.printStackTrace();
				}
			}// else
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
	
	public 	void post_success(String retval)
	{
		respcode="";
		respdesc="";
		Log.e("FAILED= ", "FAILED=");
	}
	
	public void showAlert(final String str) 
	{	
		Log.e("SBK","===ShowAlert ");
		ErrorDialogClass alert = new ErrorDialogClass(act, "" + str)	
		{
            Intent in = null;
            
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
                       // finish();
                    	dismiss();
                        break;
                }this.dismiss();

            }
        };alert.show();
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_home1:
			Intent in1 = new Intent(act, NewDashboard.class);
			in1.putExtra("VAR1", var1);
			in1.putExtra("VAR3", var3);
			startActivity(in1);
			act.finish();
			break;
		case R.id.btn_deptintrates:
			Fragment fragment = new DepositRateActivity(act);
			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
			act.frgIndex=1001;
			
			break;
		case R.id.btn_loanintrates:
			Fragment fragmentln = new LoanRateActivity(act);
			FragmentManager fragmentManager1 = getFragmentManager();
			fragmentManager1.beginTransaction().replace(R.id.frame_container, fragmentln).commit();
			act.frgIndex=1002;
			
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
	public int chkConnectivity() { // chkConnectivity
		ConnectivityManager cm = (ConnectivityManager) act
				.getSystemService(Context.CONNECTIVITY_SERVICE);
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
					dbs = new DialogBox(act);
					dbs.get_adb().setMessage(retMess);
					dbs.get_adb().setPositiveButton("Ok",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface arg0,
										int arg1) {
									arg0.cancel();
								}
							});
					dbs.get_adb().show();
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

	class CallWebServicelog extends AsyncTask<Void, Void, Void> {
	            JSONObject jsonObj = new JSONObject();
		String ValidationData="";

		@Override
		protected void onPreExecute() {
                   try{
                	   respcode="";
                	   retvalwbs="";
                	   respdesc="";
			Log.e("@DEBUG","LOGOUT preExecute()");
                  jsonObj.put("CUSTID", custId);
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
	
}

package shree_nagari.mbank;

import java.security.PrivateKey;

import javax.crypto.spec.SecretKeySpec;

import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import mbLib.CryptoClass;
import mbLib.CustomDialogClass;
import mbLib.DatabaseManagement;
import mbLib.DialogBox;
import mbLib.MBSUtils;
import shree_nagari.mbank.R;

import android.annotation.SuppressLint;
import android.app.Fragment;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class Help extends Fragment implements OnClickListener{
	MainActivity act;
	Help helpObj;
	DatabaseManagement dbms;
	int flag=0;
	DialogBox dbs;
	String retMess="",retvalwbs="",respdesc="",retval="",respcode="",stringValue="",custId="";
	ImageView img_heading;
	TextView txt_heading;
	Context helpobj;
	ImageView btn_home1,btn_logout;
	private static String URL = "";
	private static String METHOD_NAME = "";
	private static String NAMESPACE = "";
	private static String SOAP_ACTION = "";
	PrivateKey var1=null;	  
	String var5="",var3="";
	SecretKeySpec var2=null;
	
	public Help() {
	}

	@SuppressLint("ValidFragment")
	public Help(MainActivity a) {
		act = a;
		helpObj = this;
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
		View rootView = inflater.inflate(R.layout.help, container,false);
		img_heading = (ImageView) rootView.findViewById(R.id.img_heading);
		txt_heading = (TextView) rootView.findViewById(R.id.txt_heading);
		img_heading.setBackgroundResource(R.mipmap.help);
		txt_heading.setText(act.getString(R.string.lbl_app_info));
		btn_home1 = (ImageView) rootView.findViewById(R.id.btn_home1);
		btn_logout = (ImageView) rootView.findViewById(R.id.btn_logout);
		btn_home1.setOnClickListener(this);
		btn_logout.setOnClickListener(this);
		return rootView;
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
	public void showAlert(final String str) 
	{
		ErrorDialogClass alert = new ErrorDialogClass(helpobj, "" + str)
		{
			@Override
			public void onClick(View v)
			{
				//Log.e("SetMPIN","SetMPIN...btn CLicked=="+R.id.btn_ok+"==="+v.getId());
				switch (v.getId()) 
				{
					case R.id.btn_ok:
						//Log.e("SetMPIN","SetMPIN...CASE trru="+WSCalled);
						if((str.equalsIgnoreCase(respdesc)) &&  (respcode.equalsIgnoreCase("0")))
						{
							post_success(retvalwbs);
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
	private void post_success(String retvalwbs) {
		// TODO Auto-generated method stub
		
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

}

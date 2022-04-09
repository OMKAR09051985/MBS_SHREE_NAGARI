package shree_nagari.mbank;

import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.crypto.spec.SecretKeySpec;

import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import mbLib.CryptoClass;
import mbLib.DatabaseManagement;
import mbLib.DialogBox;
import mbLib.MBSUtils;

import shree_nagari.mbank.R;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
//import android.annotation.SuppressLint;

//@SuppressLint("NewApi")
public class ChequeStatusRep extends Fragment implements OnClickListener {
	MainActivity act;
	ChequeStatusRep chqStatusRep;
	//TextView drcrAmt;
	Button back;
	DialogBox dbs;
	ImageButton btn_home;//btn_back;
	String actype_val, branch_val, sch_acno_val, name_val, bal_val, 
	drcr,retval="",respdesc="",respcode="",retvalwbs="",custId="";
	String str = "", spi_str = "";
	String balance, retMess;
	ListView lst_chqStatus;
	List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
	String stringValue="",fromWhere="";
	ImageView btn_home1,btn_logout,img_heading;
	TextView txt_heading;
	int flag=0;
	DatabaseManagement dbms;
	private static String NAMESPACE = "";
	private static String URL = "";
	private static String SOAP_ACTION = "";
	private static String METHOD_NAME = "";
	private static final String MY_SESSION = "my_session";
	PrivateKey var1=null;	  
	String var5="",var3="";
	SecretKeySpec var2=null;
	
	public ChequeStatusRep(){}
	
	@SuppressLint("ValidFragment")
	public ChequeStatusRep(MainActivity a)
	{
		//System.out.println("ChequeStatusRep()");
		act = a;
		chqStatusRep=this;
		SharedPreferences sp = act.getSharedPreferences(MY_SESSION, Context.MODE_PRIVATE);
		//Editor e = sp.edit();
		stringValue = sp.getString("retValStr", "retValStr");
		fromWhere  = sp.getString("fromWhere  ", "fromWhere  ");
		//System.out.println("value of retValStr :" + stringValue);
	}
	
	
	

	 public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState) {		
			System.out.println("onCreateView() ChequeStatusRep");
			var1 = act.var1;
			var3 = act.var3;
	        View rootView = inflater.inflate(R.layout.chq_status_rep, container, false);
	        dbms = new DatabaseManagement("shree_nagari.mbank", "listMobileBanking");
	    	Cursor c1=dbms.selectFromTable("SHAREDPREFERENCE", "", null);//("select * from ", null);
	            if(c1!=null)
	            {
	            	while(c1.moveToNext())
	    	        {	
	            		stringValue=c1.getString(0);
	            		//Log.e("retvalstr","....."+stringValue);
	            		custId=c1.getString(2);
	    	        	//Log.e("custId","......"+custId);
	    	        	
	    	        }
	            }
			
					
			Bundle bnd = getArguments();
			retMess = bnd.getString("transactions");
			
			String dbt = "Debit Amount";
			String crd = "Credit Amount";
			
			Log.e("Debug@ChqStatusRep",retMess);
			
			lst_chqStatus = (ListView) rootView.findViewById(R.id.lst_chk_status);
			btn_home=(ImageButton)rootView.findViewById(R.id.btn_home);
			/*btn_back=(ImageButton)rootView.findViewById(R.id.btn_back);*/
			
			txt_heading=(TextView)rootView.findViewById(R.id.txt_heading);
			txt_heading.setText(getString(R.string.lbl_title_cheque_status_report));
			img_heading = (ImageView) rootView.findViewById(R.id.img_heading);
			img_heading.setBackgroundResource(R.mipmap.checkbkstatus);
			btn_home1 = (ImageView) rootView.findViewById(R.id.btn_home1);
			btn_logout = (ImageView) rootView.findViewById(R.id.btn_logout);
			btn_home1.setOnClickListener(this);
			btn_logout.setOnClickListener(this);
			//btn_home.setImageResource(R.drawable.ic_home_d);
			//btn_back.setImageResource(R.drawable.backover);

			//btn_back.setOnClickListener(this);
			btn_home.setOnClickListener(this);
			
			setValues();
			
	        return rootView;
	    }
		
	

	public void setValues() {

		String trn_str = retMess;
		//Log.e("Debug@ setValues",trn_str);
		String string1[] = trn_str.split("~");
		//System.out.println("strring1.length:" + string1.length);
		List<String> content = new ArrayList<String>();
		for (int j = 0; j < string1.length; j++) {

			String string2[] = string1[j].split("#");
			//Log.e("@string2",string1[j]);
			HashMap<String, String> map = new HashMap<String, String>();
			//String[] from = new String[] { "rowid", "col_0", "col_3","col_1", "col_2" ,"col_4"};
			//int[] to = new int[] { R.id.itm_rowid, R.id.itm_date, R.id.itm_drcr, R.id.itm_desc, R.id.itm_chqno , R.id.itm_status};

			//String[] from = new String[] {"rowid", "col_0", "col_1", "col_2","col_3"};
			//int[] to = new int[] { R.id.item1, R.id.item2, R.id.item3, R.id.item4,R.id.item5 };

			String[] from = new String[] {"rowid", "col_0", "col_1", "col_2"};
			int[] to = new int[] { R.id.item1, R.id.item2, R.id.item3,R.id.item4};
			
			for (int k = 0; k < string2.length; k++) 
			{

				String toAdd = string1[j];
				content.add(toAdd);

				//Updated By AYB  Dummy 11/10/2005#By Cheque#140443#10000#Payment Done
				map.put("col_0", string2[0].trim());
				map.put("col_1", string2[4].trim()+", Cheque No. "+string2[2].trim()+", "+string2[5].trim());
				map.put("col_2", string2[3].trim()+" "+getString(R.string.currency));
				//map.put("col_2", "");
				//map.put("col_3", "");
				
			}
			fillMaps.add(map);
			SimpleAdapter adapter = new SimpleAdapter(act, fillMaps,R.layout.chq_rep_list, from, to);
					//R.layout.mini_stmt_list, from, to);
					
			lst_chqStatus.setAdapter(adapter);
		}
	}

	@Override
	public void onClick(View v) 
	{
		switch (v.getId()) 
		{
			/*case R.id.btn_back:
				//Log.e("From Where",fromWhere);
				if(fromWhere.equalsIgnoreCase("STOP_PAYMENT"))//If show from Stop Payment in NOTALL Case
				{
					Fragment fragment = new StopPayment(act);
					FragmentManager fragmentManager = getFragmentManager();
					fragmentManager.beginTransaction()
							.replace(R.id.frame_container, fragment).commit();
					act.frgIndex=72;
				}
				else//IF called from Issued/Deposited Cheque Status
				{
					Fragment fragment = new ChequeStatus(act);
					FragmentManager fragmentManager = getFragmentManager();
					fragmentManager.beginTransaction()
							.replace(R.id.frame_container, fragment).commit();
					act.frgIndex=73;
				}
				break;
				*/
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
				dbs = new DialogBox(act);
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
				dbs.get_adb().show();
				break;	
		}
	}
	
	public int chkConnectivity() {
		ConnectivityManager cm = (ConnectivityManager) act
				.getSystemService(Context.CONNECTIVITY_SERVICE);
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
					
					}
					break;
				case DISCONNECTED:
					flag = 1;
					// retMess =
					// "Network Disconnected. Please Check Network Settings.";
					retMess = getString(R.string.alert_014);
					showAlert(retMess);
					
					break;
				default:
					flag = 1;
					retMess = getString(R.string.alert_000);
					// setAlert();
					showAlert(retMess);
					
					break;
				}
			} else {
				flag = 1;
				retMess = getString(R.string.alert_000);
				// setAlert();
				showAlert(retMess);
			
			}
		} catch (NullPointerException ne) {

			Log.i("mayuri", "NullPointerException Exception" + ne);
			flag = 1;
			// retMess = "Can Not Get Connection. Please Try Again.";
			retMess = getString(R.string.alert_000);
			// setAlert();
			showAlert(retMess);
			

		} catch (Exception e) {
			Log.i("mayuri", "Exception" + e);
			flag = 1;
			// retMess = "Connection Problem Occured.";
			retMess = getString(R.string.alert_000);
			// setAlert();
			showAlert(retMess);
			
		}
		return flag;
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
		Log.e("From Where",fromWhere);
		if(fromWhere.equalsIgnoreCase("STOP_PAYMENT"))//If show from Stop Payment in NOTALL Case
		{
			Log.e("From Where1111",fromWhere);
			Fragment fragment = new StopPayment(act);
			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.frame_container, fragment).commit();
		}
		else//IF called from Issued/Deposited Cheque Status
		{
		/*	Fragment fragment = new ChequeStatus(act);
			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.frame_container, fragment).commit();*/
			Log.e("From Where222",fromWhere);
			Intent in = new Intent(act, ChequeStatus.class);
			in.putExtra("VAR1", var1);
			in.putExtra("VAR3", var3);
			startActivity(in);
			act.finish();
		}
	}

	public 	void post_success(String retval){

		try {
			
			respcode="";
			respdesc="";
			
		} catch (Exception e) {
			// TODO: handle exception
			Log.e("Debug@", "This shouldn't be here");
		}
		// startActivity(in);
		// finish();
		
	}
}

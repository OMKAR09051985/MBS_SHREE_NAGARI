package shree_nagari.mbank;

import java.security.PrivateKey;
import java.util.ArrayList;

import javax.crypto.spec.SecretKeySpec;

import mbLib.Accountbean;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
//import android.annotation.SuppressLint;

//@SuppressLint("NewApi")
public class StopPayment extends Fragment implements OnClickListener {
	MainActivity act;
	StopPayment stopPay;
	DatabaseManagement dbms;	
	Intent in = null;
	Button btn_stoppay;	
	Spinner spi_account;
	ImageButton spinner_btn;
	TextView cust_nm,txt_heading;
	EditText txt_chksrno, txt_frmno, txt_tono,txt_reason;
	String chqFrom = "", chqTo="", chkSrl = "",respdesc="";
	String acnt_inf, all_acnts;
	boolean isNotAll = false;
	String str = "", retMess = "", custId = "", imeino = "", cust_name = "", regno = "", retval = null;
	int cnt = 0;
	DialogBox dbs;
	ProgressBar pb_wait;
	private static final String MY_SESSION = "my_session";
	//Editor e;
	ImageView btn_home1,btn_logout;
	String stringValue;
	String decryptedRgno = "";
	String str2 = "",respcode="",retvalweb="",afterStopPayReqrespdesc="",
			afterChqIssueStatusReqrespdesc="",retvalwbs="",AccCustId;
	ArrayList<Accountbean> arrList1;
	
	int frmno = 0, tono = 0, flag = 0;
	private static String NAMESPACE = "";
	private static String URL = "";
	//private static final String URL = "http://172.100.30.251:8082/axis2/services/MobBankServices";
	// ivate static final String URL
	// ="http://listspl.zapto.org:8082/axis2/services/MobBankServices";
	private static String SOAP_ACTION = "";
	private static  String METHOD_NAME = "";
	private static  String METHOD_NAME_ISSUED = "";
	ArrayList<String> arrListTemp = new ArrayList<String>();
	PrivateKey var1=null;	  
	String var5="",var3="";
	SecretKeySpec var2=null;
	ImageButton btn_home;//, btn_back;
	ImageView img_heading;
	
	public void onBackPressed() 
	{
		Intent in = new Intent(act, NewDashboard.class);
		in.putExtra("VAR1", var1);
		in.putExtra("VAR3", var3);
		startActivity(in);
		act.finish();
	}
	
	public StopPayment(){}
	
	@SuppressLint("ValidFragment")
	public StopPayment(MainActivity a)
	{
		act = a;
		stopPay=this;
		dbs = new DialogBox(act);
		imeino = MBSUtils.getImeiNumber(act);
	}
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.stop_payment, container, false);
        var1 = act.var1;
        var3 = act.var3;
		
	dbms = new DatabaseManagement("shree_nagari.mbank", "listMobileBanking");

	img_heading = (ImageView) rootView.findViewById(R.id.img_heading);
	img_heading.setBackgroundResource(R.mipmap.checkbkstatus);
	Cursor c1 = dbms.selectFromTable("SHAREDPREFERENCE", "", null);
		if (c1 != null) {
			while (c1.moveToNext()) {
				stringValue = c1.getString(0);
				//Log.e("retValStr", "stringValue:-" + stringValue);
				custId=c1.getString(2);
	        	//Log.e("custId","......"+custId);
			}
		}
		
		spi_account = (Spinner) rootView.findViewById(R.id.spi_accounts);
		btn_stoppay = (Button) rootView.findViewById(R.id.btn_submit_stp);
		txt_chksrno = (EditText) rootView.findViewById(R.id.txt_chk_serial);
		txt_frmno = (EditText) rootView.findViewById(R.id.txt_frm_chkno);
		txt_tono = (EditText) rootView.findViewById(R.id.txt_to_chkno);
		txt_reason = (EditText) rootView.findViewById(R.id.txt_reason);
		all_acnts = stringValue;
		txt_heading=(TextView)rootView.findViewById(R.id.txt_heading);
		txt_heading.setText(getString(R.string.lbl_stop_payment));
		// CalcString c = new CalcString(all_acnts);
		// str = c.retSpiString();
		addAccounts(all_acnts);
		
		btn_home = (ImageButton) rootView.findViewById(R.id.btn_home);
		/*btn_back = (ImageButton) rootView.findViewById(R.id.btn_back);*/

		//btn_home.setImageResource(R.drawable.ic_home_d);
		//btn_back.setImageResource(R.drawable.backover);
		
		//btn_back.setOnClickListener(this);
		btn_home.setOnClickListener(this);
		 btn_home1 = (ImageView) rootView.findViewById(R.id.btn_home1);
			btn_logout = (ImageView) rootView.findViewById(R.id.btn_logout);
			btn_home1.setOnClickListener(this);
			btn_logout.setOnClickListener(this);
			
		pb_wait = (ProgressBar) rootView.findViewById(R.id.pb_wait7);
		pb_wait.setMax(10);
		pb_wait.setProgress(1);
		pb_wait.setVisibility(ProgressBar.INVISIBLE);

		btn_stoppay.setOnClickListener(this);
		spi_account.requestFocus();				
		//btn_stoppay.setTypeface(tf_calibri);		
		
		spinner_btn = (ImageButton)rootView.findViewById(R.id.spinner_btn);
		spinner_btn.setOnClickListener(stopPay);
		
        return rootView;
    }
	
	public String getAccounts() {
		Bundle bnd = act.getIntent().getExtras();
		String str = bnd.getString("accounts");
		//System.out.println("accounts--------------" + str);
		return str;
	}

	public void addAccounts(String str) {
		//System.out.println("BalanceEnquiry IN addAccounts()" + str);

		try {
			ArrayList<String> arrList = new ArrayList<String>();
			arrList1 = new ArrayList<>();
			String allstr[] = str.split("~");
			/*
			 * SUCCESS~
			 * 5#101#SB#7#KADEKAR KAVITA KIRAN~~
			 * 5#101#SB#2#KADEKAR KAVITA KIRAN~
			 * 5#101#SB#3#KADEKAR KAVITA KIRAN~
			 * 5#101#SB#6#KADEKAR KAVITA KIRAN
			 
			 * old  Accounts: 5#101#SB#1#KADEKAR KAVITA KIRAN,-, 5#101#SB#2#KADEKAR
			 * KAVITA KIRAN,-, 5#101#SB#3#Mrs. KADEKAR KAVITA KIRAN,-,
			 * 5#101#SB#6#KADEKAR DIGAMBAR HARI / KAVITA KIRAN,-,
			 * 5#101#SB#7#DESHPANDE JAGGANATH SHANKAR / KADEKAR KAVITA K.,-,
			 */
			//String str1[] = allstr[1].split(",-,");
			//int noOfAccounts = str1.length;
			int noOfAccounts = allstr.length;
			//System.out.println("BalanceEnquiry noOfAccounts:" + noOfAccounts);
			Accounts acArray[] = new Accounts[noOfAccounts];
			for (int i = 0; i < noOfAccounts; i++) {
				//System.out.println(i + "----STR1-----------" + str1[i]);
				//str2 = str1[i];
				str2 = allstr[i];
				acArray[i] = new Accounts(str2);
				str2 = str2.replaceAll("#", "-");
				String accType=str2.split("-")[2];
				String oprcd=str2.split("-")[7];
				String AccCustID = str2.split("-")[11];
				String str2Temp = str2;
				str2 = MBSUtils.get16digitsAccNo(str2);
				//if(!(accType.equals("FD"))&& !(accType.equals("RP")) && !(accType.equals("RD")))
				if (((accType.equals("SB")) ||(accType.equals("LO"))
						||(accType.equals("CA")))&& oprcd.equalsIgnoreCase("O"))
				{
					Accountbean accountbeanobj = new Accountbean();
					accountbeanobj.setAccountinfo(str2 + " (" + MBSUtils.getAccTypeDesc(accType) + ")");
					accountbeanobj.setAccountNumber(str2);
					accountbeanobj.setAcccustid(AccCustID);
					arrList1.add(accountbeanobj);

					arrList.add(str2+" ("+MBSUtils.getAccTypeDesc(accType)+")");
					arrListTemp.add(str2);
				}
			}

			/*ArrayAdapter<String> arrAdpt = new ArrayAdapter<String>(this,
					android.R.layout.simple_spinner_item, arrList);
			arrAdpt.setDropDownViewResource(R.layout.spinner_dropdown_item);
			spi_account.setAdapter(arrAdpt);*/
			
			String[] accArr = new String[arrList.size()];
			accArr = arrList.toArray(accArr);
			/*CustomeSpinnerAdapter accs=new CustomeSpinnerAdapter(act, R.layout.spinner_layout, accArr);*/
			ArrayAdapter<String> accs = new ArrayAdapter<String>(act,R.layout.spinner_item, accArr);
			/*CustomeSpinnerAdapter accs = new CustomeSpinnerAdapter(act,android.R.layout.simple_spinner_item, accArr);*/
			accs.setDropDownViewResource(R.layout.spinner_dropdown_item);
			spi_account.setAdapter(accs);			
			
			//Log.e("StopPayReq@AddAccount ", "Exiting from adding accounts");

			acnt_inf = spi_account.getItemAtPosition(spi_account.getSelectedItemPosition()).toString();
			AccCustId= arrList1.get(spi_account.getSelectedItemPosition()).getAcccustid();
			//Log.e("StopPayReq@AddAccount", acnt_inf);
			
			
	      
		} catch (Exception e) {
			System.out.println("" + e);
		}
	
	}

	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		//pb_wait.setVisibility(ProgressBar.VISIBLE);
		switch (v.getId()) {
		/*case R.id.btn_back:
			Fragment fragment = new ChequeMenuActivity(act);
			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.frame_container, fragment).commit();
			act.frgIndex=7;
			break;*/
		case R.id.btn_home:
			Intent in = new Intent(act, NewDashboard.class);
			in.putExtra("VAR1", var1);
			in.putExtra("VAR3", var3);
			startActivity(in);
			act.finish();
			break;
		case R.id.btn_submit_stp:
			int frmno = 0,
			tono = 0;
			//pb_wait.setVisibility(ProgressBar.VISIBLE);
			if (txt_frmno.getText().toString().equals("")) {
				//System.out.println("111111");
				//retMess = "Please Enter From No. of Chaque Book";
				retMess = getString(R.string.alert_064);
				
				showAlert(retMess,false);

			} else if (!txt_frmno.getText().toString().equals("")
					&& !txt_tono.getText().toString().equals("")) {
				frmno = Integer.parseInt("" + txt_frmno.getText());
				tono = Integer.parseInt("" + txt_tono.getText());
				if (frmno > tono && frmno != tono) {
					//System.out.println("from less than to");
					retMess = getString(R.string.alert_053);
					showAlert(retMess,false);
				} else {
					//flag = chkConnectivity();
					//if (flag == 0) 
					{
						callReport();
					}
				}
			} else {
				//flag = chkConnectivity();
				//if (flag == 0) 
				{
					callReport();
				}
			}
			break;
			
		case R.id.spinner_btn:
			spi_account.performClick();
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
    					showAlert(respdesc,false);
    				}
    				else{
			if (retvalwbs.indexOf("FAILED") > -1) {
				retMess = getString(R.string.alert_network_problem_pease_try_again);
				showAlert(retMess,false);

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
					//retMess = "Network Disconnected. Please Try Again.";
					retMess = getString(R.string.alert_014);
					showAlert(retMess,false);
					/*dbs = new DialogBox(this);
					dbs.get_adb().setMessage(retMess);
					dbs.get_adb().setPositiveButton("Ok",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface arg0,
										int arg1) {
									arg0.cancel();
								}
							});
					dbs.get_adb().show();*/
					break;
				default:
					flag = 1;
					//retMess = "Network Unavailable. Please Try Again.";
					retMess = getString(R.string.alert_000);
					// setAlert();
					showAlert(retMess,false);
					/*dbs = new DialogBox(this);
					dbs.get_adb().setMessage(retMess);
					dbs.get_adb().setPositiveButton("Ok",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface arg0,
										int arg1) {
									arg0.cancel();
									Intent in = null;
									in = new Intent(getApplicationContext(),
											LoginActivity.class);
									startActivity(in);
									finish();
								}
							});
					dbs.get_adb().show();*/
					break;
				}
			} else {
				flag = 1;
				//retMess = "Network Unavailable. Please Try Again.";
				retMess = getString(R.string.alert_000);
				// setAlert();
				showAlert(retMess,false);
				/*dbs = new DialogBox(this);
				dbs.get_adb().setMessage(retMess);
				dbs.get_adb().setPositiveButton("Ok",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface arg0, int arg1) {
								arg0.cancel();
								Intent in = null;
								in = new Intent(getApplicationContext(),
										LoginActivity.class);
								startActivity(in);
								finish();
							}
						});
				dbs.get_adb().show();*/
			}
		} catch (NullPointerException ne) {

			Log.e("StopPayReq", "NullPointerException Exception" + ne);
			flag = 1;
			//retMess = "Can Not Get Connection. Please Try Again.";
			//retMess = "Network Unavailable. Please Try Again.";
			retMess = getString(R.string.alert_000);
			// setAlert();
			showAlert(retMess,false);
			/*dbs = new DialogBox(this);
			dbs.get_adb().setMessage(retMess);
			dbs.get_adb().setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface arg0, int arg1) {
							arg0.cancel();
							Intent in = null;
							in = new Intent(getApplicationContext(),
									LoginActivity.class);
							startActivity(in);
							finish();
						}
					});
			dbs.get_adb().show();*/

		} catch (Exception e) {
			Log.e("StopPayReq", "Exception" + e);
			flag = 1;
			//retMess = "Connection Problem Occured.";
			//retMess = "Network Unavailable. Please Try Again.";
			retMess = getString(R.string.alert_000);
			// setAlert();
			showAlert(retMess,false);
			/*dbs = new DialogBox(this);
			dbs.get_adb().setMessage(retMess);
			dbs.get_adb().setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface arg0, int arg1) {
							arg0.cancel();
							Intent in = null;
							in = new Intent(getApplicationContext(),
									LoginActivity.class);
							startActivity(in);
							finish();
						}
					});
			dbs.get_adb().show();*/
		}
		return flag;
	}

	class CallWebService extends AsyncTask<Void, Void, Void> 
	{

		LoadProgressBar loadProBarObj=new LoadProgressBar(act);
		String retval = "";
		String all_str, branch_cd, schm_cd, acnt_no, reason = "", chksrno = "";
		int frmno = 0, tono = 0;
		String ValidationData="";
		JSONObject obj = new JSONObject();
		@Override
	    protected void onPreExecute()
	    {
			respcode="";
			retvalweb="";
			afterStopPayReqrespdesc="";
			//pb_wait.setVisibility(ProgressBar.VISIBLE);
			loadProBarObj.show();
			
			//all_str = spi_account.getSelectedItem().toString();
			all_str = arrListTemp.get(spi_account.getSelectedItemPosition());
			                AccCustId= arrList1.get(spi_account.getSelectedItemPosition()).getAcccustid();


			chksrno = txt_chksrno.getText().toString().trim();
			chkSrl = chksrno;
			reason =  txt_reason.getText().toString().trim();
			try {
				 chqFrom = txt_frmno.getText().toString().trim();
				frmno = Integer.parseInt(chqFrom);
				chqTo = txt_tono.getText().toString().trim();
				tono = Integer.parseInt("" +chqTo);
			} catch (NumberFormatException e) {
				// TODO: handle exception
				tono = 0;
			}
	
			try {

				obj.put("ACCNO",all_str );
				obj.put("CUSTID", custId+"#~#"+AccCustId);
				obj.put("CHQSRNO", chksrno.trim());
				obj.put("FROMNO", frmno + "".trim());
				obj.put("TONO", tono + "".trim());
				obj.put("REASON", reason.trim());
				obj.put("IMEINO", imeino);
				obj.put("SIMNO", MBSUtils.getSimNumber(act));
				obj.put("METHODCODE","10");
				// ValidationData=MBSUtils.getValidationData(act,obj.toString());
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
	    };
		@Override
		protected Void doInBackground(Void... arg0) 
		{
			String value4 = getString(R.string.namespace);
			String value5 = getString(R.string.soap_action);
			String value6 = getString(R.string.url);
			final String value7 = "callWebservice";		
			try {
				String keyStr=CryptoClass.Function2();
				var2=CryptoClass.getKey(keyStr);
				SoapObject request = new SoapObject(value4, value7);
										
				request.addProperty("value1", CryptoClass.Function5(obj.toString(), var2));
				request.addProperty("value2", CryptoClass.Function3(keyStr, var1));
				request.addProperty("value3", var3);
				SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
				envelope.setOutputSoapObject(request);
				HttpTransportSE androidHttpTransport = new HttpTransportSE(value6,15000);
				String status = "";
				try {
					androidHttpTransport.call(value5, envelope);
					status = envelope.bodyIn.toString().trim();
					var5 = status;
					int pos = envelope.bodyIn.toString().trim().indexOf("=");
					if (pos > -1) {
						status = status.substring(pos + 1, status.length() - 3);
						var5 = status;
						
						}
				}  catch (Exception e) {
					e.printStackTrace();
					System.out.println("Exception" + e);
					//retMess = "Your Request Could Not Be Registered";
					retMess =getString(R.string.alert_059);
				}
			} catch (Exception e) {
				//retMess = "Error occured" + e;
				//retMess = "Network Problem. Please Try Again.";
				retMess =getString(R.string.alert_000);
				System.out.println(e.getMessage());
			}
			return null;
		}
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
						afterStopPayReqrespdesc = jsonObj.getString("RESPDESC");
					}
					else
					{	
						afterStopPayReqrespdesc = "";
					}
					
				if(afterStopPayReqrespdesc.length()>0)
				{
					
					showAlert(afterStopPayReqrespdesc,isNotAll);
				}
				else{
			if (retvalweb.indexOf("SUCCESS") > -1)
			{
				
				post_successStopPayReq(retvalweb);
			} 
			else 
			{
				cnt = 2;
				retMess =getString(R.string.alert_065);
				txt_chksrno.setText("");
				txt_frmno.setText("");
				txt_tono.setText("");
				txt_reason.setText("");
				showAlert(retMess,isNotAll);
				
			}
			
			
		}
				
				/*	}
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
	public 	void post_successStopPayReq(String retvalweb)
	{
		respcode="";
		afterStopPayReqrespdesc="";
		if (retvalweb.indexOf("NODATA") > -1)
		{
				cnt = 2;
		}
		else if(retvalweb.indexOf("NOTALL") > -1)
		{
			cnt=3;
			isNotAll = true;
		}
		else
		{
			cnt = 1;
			String temp[] = retvalweb.split("~");
			retvalweb = temp[1];
		}
	
		
		if (cnt == 1 && retvalweb!=null) 
		{
			retMess = getString(R.string.alert_058)+ retvalweb;
		}
		else if (cnt == 2)
		{
			retMess =getString(R.string.alert_065);
		}
		else
		{
			retMess =getString(R.string.alert_059);
		}
		txt_chksrno.setText("");
		txt_frmno.setText("");
		txt_tono.setText("");
		txt_reason.setText("");
		showAlert(retMess,isNotAll);
	}

	public void callReport() {
		new CallWebService().execute();
		
	}

	public void showAlert(final String str,final boolean status)
	{
			//Toast.makeText(this, str, Toast.LENGTH_LONG).show();	
			ErrorDialogClass alert = new ErrorDialogClass(act,""+str){
				@Override
				public void onClick(View v) 
				{
					switch (v.getId()) 
					{
						case R.id.btn_ok:
							//Show Cheque Issued Status
							if((str.equalsIgnoreCase(afterStopPayReqrespdesc)) &&  (respcode.equalsIgnoreCase("0")))
							{
								post_successStopPayReq(retvalweb);
							}
							else if((str.equalsIgnoreCase(afterStopPayReqrespdesc)) && (respcode.equalsIgnoreCase("1")))
							{
								this.dismiss();
							}
							else if((str.equalsIgnoreCase(afterChqIssueStatusReqrespdesc)) &&  (respcode.equalsIgnoreCase("0")))
							{
								post_successChqIssueStatusReq(retvalweb);
							}
							else if((str.equalsIgnoreCase(afterChqIssueStatusReqrespdesc)) && (respcode.equalsIgnoreCase("1")))
							{
								this.dismiss();
							}
						    else if(status)
							{
								//Log.e("Debug@if:","I Am Here To Forward Cheque Report");
								new CallWebService_ChqStatus().execute();
								this.dismiss();
							}
							else
							{
								//Log.e("Debug@else:","Normal Dismiss");
								this.dismiss();
							}
						  break;			
						default:
						  break;
					}
				}
			};
			alert.show();
	}
	
	class CallWebService_ChqStatus extends AsyncTask<Void, Void, Void> {
		LoadProgressBar loadProBarObj = new LoadProgressBar(act);
			String all_str = "", branch_cd = "", schm_cd = "", acnt_no = "";
		String chksrno = "", frmDate = "", toDate = "";
		String frmno = "", tono ="", frmAmt ="", toAmt = "";
		String ValidationData="";
		  JSONObject obj = new JSONObject();
		  
		@Override
		protected void onPreExecute() {
		
			loadProBarObj.show();
			int i = 0;
			respcode="";
			retvalweb="";
			afterChqIssueStatusReqrespdesc="";
			
			all_str = arrListTemp.get(spi_account.getSelectedItemPosition());
			AccCustId= arrList1.get(spi_account.getSelectedItemPosition()).getAcccustid();


			chksrno = chkSrl;
			frmno = chqFrom;
			tono = chqTo;			
		
			frmAmt = "";
			toAmt = "";
			frmDate = "";
			toDate = "";
	 
			try {
				obj.put("ACCNO", MBSUtils.get16digitsAccNo(all_str));
				obj.put("CUSTID", custId+"#~#"+AccCustId);
				obj.put("CHQ_SR", chksrno);
				obj.put("FRM_CHQNO", frmno + "");
				obj.put("TO_CHQNO", tono + "");
				obj.put("FRM_AMT", frmAmt + "");
				obj.put("TO_AMT", toAmt + "");
				obj.put("FRM_DT", frmDate);
				obj.put("TO_DT", toDate);
				obj.put("DRCR", "");
				obj.put("IMEINO", imeino);
				obj.put("SIMNO", MBSUtils.getSimNumber(act));
				obj.put("METHODCODE","11");
				// ValidationData=MBSUtils.getValidationData(act,obj.toString());
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
										
				request.addProperty("value1", CryptoClass.Function5(obj.toString(), var2));
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
				//System.out.println(e.getMessage());
				Log.e("ERROR-OUTER", e.getClass() + " : " + e.getMessage());
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
					retvalweb = jsonObj.getString("RETVAL");
				}
				else
				{
					retvalweb = "";
				}
				if (jsonObj.has("RESPDESC"))
				{
					afterChqIssueStatusReqrespdesc = jsonObj.getString("RESPDESC");
				}
				else
				{	
					afterChqIssueStatusReqrespdesc = "";
				}
				
			if(afterChqIssueStatusReqrespdesc.length()>0)
			{
				
				showAlert(afterChqIssueStatusReqrespdesc,isNotAll);
			}
			else{
			if (retvalweb.indexOf("FAILED") > -1) {
				retMess = getString(R.string.alert_051);
				showAlert(retMess,false);

			} else if (retvalweb.indexOf("NODATA") > -1) {
				retMess = getString(R.string.alert_089);// No Record Found
				showAlert(retMess,false);
			} else if (retvalweb.indexOf("SUCCESS") > -1){
				 post_successChqIssueStatusReq(retvalweb);
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
	
	public 	void post_successChqIssueStatusReq(String retvalweb)
	{

	
		try {
			respcode="";
		
			afterChqIssueStatusReqrespdesc="";
			Bundle bnd1 = new Bundle();
			

			bnd1.putString("transactions", retvalweb.split("SUCCESS~")[1]);
			bnd1.putString("fromWhere", "STOP_PAYMENT");

			Fragment chqStatRptFragment = new ChequeStatusRep(act);

			chqStatRptFragment.setArguments(bnd1);

			act.setTitle(act.getString(R.string.lbl_title_cheque_status_report));
			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.frame_container, chqStatRptFragment)
					.commit();
		} catch (Exception e) {
			// TODO: handle exception
			Log.e("Debug@", "This shouldn't be here");
		}
		// startActivity(in);
		// finish();
	
		
	}
}

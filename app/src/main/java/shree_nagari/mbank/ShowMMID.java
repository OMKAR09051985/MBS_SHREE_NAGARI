package shree_nagari.mbank;

import mbLib.CryptoUtil;
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
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class ShowMMID extends Fragment implements View.OnClickListener //Fragment
{
	TextView text1,text2,txt_heading,text3;
	MainActivity act;
	ImageView img_heading;
	ImageButton btn_home;//,btn_back;
	Editor e;
	String mmid,message,req_id;
	private String accountNo;
	Button btn_delete;
	private static  String GET_MMID = "";
	private static String NAMESPACE = "";
	private static String SOAP_ACTION = "";
	private static String URL = "";
	int flag=0;
	int check=0;
	String respcode="",retvalweb="",deleteMMIDrespdesc="",retMess = "", retVal = "",custid="",mobno="",AccCustId;
	private String accStr;
	private static final String MY_SESSION = "my_session";
	
	public ShowMMID() { 
		
	}
	@SuppressLint("ValidFragment")
	public ShowMMID(MainActivity a) {
		act = a;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.mmid, container,false);
		SharedPreferences sp = act.getSharedPreferences(MY_SESSION,Context.MODE_PRIVATE);
		e = sp.edit();
	
		custid = sp.getString("custId", "custId");
		Bundle b1=getArguments();
		if(b1!=null)
		{
			accountNo=b1.getString("ACCNO");
			mmid=b1.getString("MMID");
			mobno=b1.getString("MOBNO");
			req_id=b1.getString("REQUEST");
			custid=b1.getString("CUSTID");
			AccCustId=b1.getString("AccCustId");
		}
		text1 = (TextView)rootView.findViewById(R.id.text1);
		text2 = (TextView)rootView.findViewById(R.id.text2);
		text3 = (TextView)rootView.findViewById(R.id.text3);
		txt_heading=(TextView)rootView.findViewById(R.id.txt_heading);
		txt_heading.setText(R.string.lbl_ur_mmid);
		img_heading=(ImageView)rootView.findViewById(R.id.img_heading);
		
		btn_home=(ImageButton)rootView.findViewById(R.id.btn_home);
		
		btn_delete=(Button)rootView.findViewById(R.id.btn_delete);
		btn_delete.setOnClickListener(this);
		btn_home.setOnClickListener(this);
		
		 message=getString(R.string.lbl_mmid_acc);//+" "+accountNo;
		 text1.setText(message);
		 text3.setText(accountNo+"  "+"Is");
		 text2.setText(mmid);
	
		return rootView;
		
	}
	
/*	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
			Bundle savedInstanceState) 
	{
		View rootView = inflater.inflate(R.layout.mmid, container, false);
		
				text1 = (TextView) rootView.findViewById(R.id.text1);
				text2 = (TextView) rootView.findViewById(R.id.text2);
				
				txt_heading=(TextView)rootView.findViewById(R.id.txt_heading);
				txt_heading.setText(R.string.lbl_ur_mmid);
		
		return rootView;
		

		//listView1 = (ListView) rootView.findViewById(R.id.listView1);
		//txt_heading=(TextView)rootView.findViewById(R.id.txt_heading);
		//img_heading=(ImageView)rootView.findViewById(R.id.img_heading);
		
	
	}*/
	@Override
	public void onClick(View v)  { 
		// TODO Auto-generated method stub
		//Log.e("onClick Event ","Clicked");
	
		/*if(v.getId()==R.id.btn_back)
				{
			Fragment fragment = new GenerateMMID(act);
			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.frame_container, fragment).commit();
		
				}		
				*/
		if(v.getId()==R.id.btn_home)
			{
				Intent in=new Intent(act,OtherServicesMenuActivity.class);
				startActivity(in);
				act.finish();
			}
		if(v.getId()==R.id.btn_delete)
		{
			
			try 
			{
				this.flag = chkConnectivity();
				Log.e("on click of delete mmid"," Delete MMID"+flag);
				if (this.flag == 0) 
				{
					//saveData();
					CallWebServiceDeleteMMID c=new CallWebServiceDeleteMMID();
					c.execute();
				}
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
				System.out.println("Exception in CallWebServiceGetSrvcCharg is:" + e);
			}
		
		}
		
		/*if(v.getId()==R.id.btn_back||v.getId()==R.id.btn_home)
		{
				Intent in=new Intent(act,DashboardDesignActivity.class);
				startActivity(in);
				act.finish();
		}*/

	}
	class CallWebServiceDeleteMMID extends AsyncTask<Void, Void, Void> {// CallWebServiceGetMMID

		String retval = "";
		String mmid=text2.getText().toString().trim();
		LoadProgressBar loadProBarObj = new LoadProgressBar(act);
		
		String[] xmlTags = {"PARAMS","CHECKSUM"};
		String[] valuesToEncrypt = new String[2];
        JSONObject jsonObj = new JSONObject();
		String generatedXML = "";
		String ValidationData="";

		@Override
		protected void onPreExecute() { 
			// p_wait.setVisibility(ProgressBar.VISIBLE);
			loadProBarObj.show();
			respcode="";
			retvalweb="";
			deleteMMIDrespdesc="";
			try{
			jsonObj.put("CUSTID", custid+"#~#"+AccCustId);
            jsonObj.put("ACCOUNTNO", accountNo);
            jsonObj.put("MMID", mmid);            
            jsonObj.put("IMEINO", MBSUtils.getImeiNumber(act));
            jsonObj.put("SIMNO", MBSUtils.getSimNumber(act));
            jsonObj.put("METHODCODE","52");
            ValidationData=MBSUtils.getValidationData(act,jsonObj.toString());
			}
			
			catch(JSONException e)
			{
				e.printStackTrace();
				Log.e("JSONException ","JSONException");
			}
            

			valuesToEncrypt[0] =  jsonObj.toString();
			valuesToEncrypt[1] =  ValidationData;
			generatedXML = CryptoUtil.generateXML(xmlTags, valuesToEncrypt);
			//System.out.println("&&&&&&&&&& generatedXML " + generatedXML);
			Log.e("Ganesh ","custid= "+custid);
			Log.e("Ganesh ","accountNo= "+accountNo);
			Log.e("Ganesh1111 ","mmid= "+mmid);
			Log.e("Ganesh1111 ","imei= "+MBSUtils.getImeiNumber(act));
			
			//Log.e("Ganesh ","valuesToEncrypt[3]="+valuesToEncrypt[3]);
		};

		@Override
		protected Void doInBackground(Void... arg0) {
			////System.out.println("============= inside doInBackground =================");
			NAMESPACE = getString(R.string.namespace);
			URL = getString(R.string.url);
			SOAP_ACTION = getString(R.string.soap_action);
			Log.e("LoanAccountDetail", "11111");
			 GET_MMID = "mbsInterCall";//"calldeleteMMIDWS";
			SoapObject request = new SoapObject(NAMESPACE, GET_MMID);

			request.addProperty("para_value", generatedXML);
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			envelope.setOutputSoapObject(request);
			HttpTransportSE androidHttpTransport = new HttpTransportSE(URL,
					15000);
			//System.out.println("============= inside doInBackground 2 =================");
			try {
				Log.e("LoanAccountDetail", "222");

				//Log.i("LoanAccountDetail   ", "111");
				androidHttpTransport.call(SOAP_ACTION, envelope);
				//Log.i("LoanAccountDetail   ", "222");
				//System.out.println(envelope.bodyIn.toString());
				//Log.i("LoanAccountDetail   ", "333");
				retval = envelope.bodyIn.toString().trim();
				//Log.e("LoanAccountDetail", retVal);
				//Log.i("LoanAccountDetail   retval", retval);
				//System.out.println("LoanAccountDetail    retval-----"+ retval);
				// pb_wait.setVisibility(ProgressBar.INVISIBLE);
				int pos = envelope.bodyIn.toString().trim().indexOf("=");
				Log.e("LoanAccountDetail", "333=="+pos);

				retval = retval.substring(pos + 1, retval.length() - 3);
				//System.out.println("LoanAccountDetail    retval AFTER SUBSTR-----"+ retval);
				Log.e("LoanAccountDetail", "444=="+retval);
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("LoanAccountDetail   Exception===" + e);
			}
			return null;
		}// end doInBackground

		protected void onPostExecute(final Void result) 
		{
			Log.e("onPostExecute", "onPostExecute 11");
			
			String[] xmlTags = { "PARAMS" };
			loadProBarObj.dismiss();
	
			Log.e("onPostExecute", "222");
			
			 String[] xml_data = CryptoUtil.readXML(retval, new String[]{"PARAMS","CHECKSUM"});
			Log.e("onPostExecute", "333");
		
			Log.e("onPostExecute", "444");
			
			JSONObject jsonObj;
			try
			{

				jsonObj = new JSONObject(xml_data[0]);
				ValidationData=xml_data[1].trim();
				if(ValidationData.equals(MBSUtils.getValidationData(act, xml_data[0].trim())))
				{
				Log.e("IN return", "data :" + jsonObj.toString());
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
					deleteMMIDrespdesc = jsonObj.getString("RESPDESC");
				}
				else
				{	
					deleteMMIDrespdesc = "";
				}
				
			if(deleteMMIDrespdesc.length()>0)
			{
				showAlert(deleteMMIDrespdesc);
			}
			else{
			if (retvalweb.indexOf("FAILED") > -1) 
			{
				String msg[] = retvalweb.split("~");
				req_id=msg[1];
				///retMess = getString(R.string.alert_167)+" "+req_id;
				//showAlert(retMess);
				Log.e("msg[1]= ","msg[1]== "+msg[1]);
				Log.e("msg[1]= ","msg[1]== "+msg[1]);
				Log.e("msg[1]= ","msg[1]== "+msg[1]);
					
				if(msg[1].equalsIgnoreCase("NA"))
				{
					if(msg[2]!=null || msg[2].length()>0)
					{
						req_id=msg[2];
						Log.e("Ganesh "," Failed NA req_id="+req_id);
						Log.e("Ganesh ","Failed NA req_id="+req_id);
						retMess = getString(R.string.alert_167)+" "+req_id;
					}
					else
					{
						retMess = getString(R.string.alert_167);
					}
					showAlert(retMess);
				}
				else
				{
					
					Log.e("Ganesh "," Failed NA req_id="+req_id);
					Log.e("Ganesh ","Failed NA req_id="+req_id);
					retMess = getString(R.string.alert_167)+" "+req_id;
					showAlert(retMess);
				}
				FragmentManager fragmentManager;
				Fragment fragment = new GenerateMMID(act);
				act.setTitle(getString(R.string.lbl_mmid));
				fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction()
						.replace(R.id.frame_container, fragment).commit();
			} 		
			else if (retvalweb.indexOf("SUCCESS") > -1) 
			{
				post_successDeleteMMID (retvalweb);
			}
				
				
				
			}	
				}
				else{
					MBSUtils.showInvalidResponseAlert(act);	
				}
			} catch (JSONException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		}// end onPostExecute
		}// end CallWebServiceGetMMID
	
	public 	void post_successDeleteMMID (String retvalweb)
	{

		respcode="";
		deleteMMIDrespdesc="";
		
		String msg[] = retvalweb.split("~");
		req_id=msg[1];
		retMess = getString(R.string.alert_168)+" "+req_id;
		showAlert(retMess);
		FragmentManager fragmentManager;
		Fragment fragment = new GenerateMMID(act);
		act.setTitle(getString(R.string.lbl_mmid));
		fragmentManager = getFragmentManager();
		fragmentManager.beginTransaction()
				.replace(R.id.frame_container, fragment).commit();	
		
		

	}
	
	/*class CallWebServiceDeleteMMID extends AsyncTask<Void, Void, Void> {// CallWebServiceGetMMID

		String retval = "";
		String mmid=text2.getText().toString().trim();
		LoadProgressBar loadProBarObj = new LoadProgressBar(act);
		String[] xmlTags = { "CUSTID","ACCOUNTNO", "MMID","IMEINO"};
		String[] valuesToEncrypt = new String[4];
		String generatedXML = "";

		@Override
		protected void onPreExecute() { 
			// p_wait.setVisibility(ProgressBar.VISIBLE);
			loadProBarObj.show();

			//System.out.println("custId:"+custid);
			//System.out.println("accountNo:"+accountNo);
			valuesToEncrypt[0] = custid;
			valuesToEncrypt[1] = accountNo; 
			valuesToEncrypt[2] = mmid;
			valuesToEncrypt[3] = MBSUtils.getImeiNumber(act);

			generatedXML = CryptoUtil.generateXML(xmlTags, valuesToEncrypt);
			//System.out.println("&&&&&&&&&& generatedXML " + generatedXML);
			Log.e("Ganesh ","custid= "+custid);
			Log.e("Ganesh ","accountNo= "+accountNo);
			Log.e("Ganesh ","mmid= "+mmid);
			Log.e("Ganesh ","valuesToEncrypt[3]="+valuesToEncrypt[3]);
		};

		@Override
		protected Void doInBackground(Void... arg0) {
			////System.out.println("============= inside doInBackground =================");
			NAMESPACE = getString(R.string.namespace);
			URL = getString(R.string.url);
			SOAP_ACTION = getString(R.string.soap_action);

			SoapObject request = new SoapObject(NAMESPACE, GET_MMID);

			request.addProperty("para_value", generatedXML);
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			envelope.setOutputSoapObject(request);
			HttpTransportSE androidHttpTransport = new HttpTransportSE(URL,
					15000);
			//System.out.println("============= inside doInBackground 2 =================");
			try {

				//Log.i("LoanAccountDetail   ", "111");
				androidHttpTransport.call(SOAP_ACTION, envelope);
				//Log.i("LoanAccountDetail   ", "222");
				//System.out.println(envelope.bodyIn.toString());
				//Log.i("LoanAccountDetail   ", "333");
				retval = envelope.bodyIn.toString().trim();
				//Log.e("LoanAccountDetail", retVal);
				//Log.i("LoanAccountDetail   retval", retval);
				//System.out.println("LoanAccountDetail    retval-----"+ retval);
				// pb_wait.setVisibility(ProgressBar.INVISIBLE);
				int pos = envelope.bodyIn.toString().trim().indexOf("=");
				retval = retval.substring(pos + 1, retval.length() - 3);
				//System.out.println("LoanAccountDetail    retval AFTER SUBSTR-----"+ retval);

			} catch (Exception e) {
				e.printStackTrace();
				Log.e("LoanAccountDetail", retVal);
				System.out.println("LoanAccountDetail   Exception" + e);
			}
			return null;
		}// end doInBackground

		protected void onPostExecute(final Void result) 
		{
			//LOANDETAILS
			String[] xmlTags = { "RETSTR" };
			loadProBarObj.dismiss();
			//Log.e("RetVal", retval);
			
			String[] xml_data = CryptoUtil.readXML(retval, xmlTags);
			String decryptedRetVal = xml_data[0];
			Log.e("Debug@decryptedRetVal", decryptedRetVal);
			
			if (decryptedRetVal.indexOf("FAILED") > -1) 
			{
				String msg[] = decryptedRetVal.split("~");
				req_id=msg[1];
				///retMess = getString(R.string.alert_167)+" "+req_id;
				//showAlert(retMess);
				Log.e("msg[1]= ","msg[1]== "+msg[1]);
				Log.e("msg[1]= ","msg[1]== "+msg[1]);
				Log.e("msg[1]= ","msg[1]== "+msg[1]);
					
				if(msg[1].equalsIgnoreCase("NA"))
				{
					if(msg[2]!=null || msg[2].length()>0)
					{
						req_id=msg[2];
						Log.e("Ganesh "," Failed NA req_id="+req_id);
						Log.e("Ganesh ","Failed NA req_id="+req_id);
						retMess = getString(R.string.alert_167)+" "+req_id;
					}
					else
					{
						retMess = getString(R.string.alert_167);
					}
					showAlert(retMess);
				}
				else
				{
					
					Log.e("Ganesh "," Failed NA req_id="+req_id);
					Log.e("Ganesh ","Failed NA req_id="+req_id);
					retMess = getString(R.string.alert_167)+" "+req_id;
					showAlert(retMess);
				}
				FragmentManager fragmentManager;
				Fragment fragment = new GenerateMMID(act);
				act.setTitle(getString(R.string.lbl_mmid));
				fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction()
						.replace(R.id.frame_container, fragment).commit();
			} 		
			else if (decryptedRetVal.indexOf("SUCCESS") > -1) 
			{
				
				
				String msg[] = decryptedRetVal.split("~");
				req_id=msg[1];
				retMess = getString(R.string.alert_168)+" "+req_id;
				showAlert(retMess);
				FragmentManager fragmentManager;
				Fragment fragment = new GenerateMMID(act);
				act.setTitle(getString(R.string.lbl_mmid));
				fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction()
						.replace(R.id.frame_container, fragment).commit();	
				
				
				
				//account no
				//mmid
		}
				
				
				
			}// end onPostExecute
		}// end CallWebServiceGetMMID
*/	
	
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
	
	public void showAlert(final String str)
	{
			//Toast.makeText(this, str, Toast.LENGTH_LONG).show();	
			ErrorDialogClass alert = new ErrorDialogClass(act,""+str)
			{@Override
				public void onClick(View v)

				{
					//Log.e("SetMPIN","SetMPIN...btn CLicked=="+R.id.btn_ok+"==="+v.getId());
					switch (v.getId()) 
					{
						case R.id.btn_ok:
							//Log.e("SetMPIN","SetMPIN...CASE trru="+WSCalled);
							if((str.equalsIgnoreCase(deleteMMIDrespdesc)) &&  (respcode.equalsIgnoreCase("0")))
							{
								post_successDeleteMMID(retvalweb);
							}
							else if((str.equalsIgnoreCase(deleteMMIDrespdesc)) && (respcode.equalsIgnoreCase("1")))
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
}

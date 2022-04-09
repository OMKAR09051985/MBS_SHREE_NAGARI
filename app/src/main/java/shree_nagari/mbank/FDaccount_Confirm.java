package shree_nagari.mbank;

import java.text.DecimalFormat; 

import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import shree_nagari.mbank.R;

import mbLib.CryptoUtil;
import mbLib.DatabaseManagement;
import mbLib.DialogBox;
import mbLib.MBSUtils;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.database.Cursor;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class FDaccount_Confirm extends Fragment implements OnClickListener{

	MainActivity act;
	DialogBox dbs;
	int flag = 0;
	ImageButton btn_back,btn_home;
	TextView txt_heading;
	TextView txt_amount,txt_duration,txt_interest_rates,txt_maturity_dates,intrest_Amt,txt_maturity_amt,txt_debit_acc,txt_conf_scheme,txt_conf_taxsaver;
	Button btn_confirm;
	String strDuration="",strAmount="",strInterest_rates="",tax_saver="",maturity="",strDebitacc="";
	String retMess="",retval = "",respcode="",respdesc_SaveDetails="",respdesc_OpenFdAccount="";
	String scheme="",maturityamt="",maturityDate="",intRates="",intamt="";
	String totalAmt="",strintRates="",retValStr="",tranId="";
	String debitAccno="", duation="", amt="", year="", month="", day="",custid="",responseJSON="";
	String strAmount1="",finalAmt="",conAmt="",monthDuration="";
	 LoadProgressBar loadProBarObj=null;

DatabaseManagement dbms,dbms1;
	
	private static String NAMESPACE = "";
	private static String SOAP_ACTION = "";
	private static String URL = "";
	public FDaccount_Confirm() {  
	}

	@SuppressLint("ValidFragment")
	public FDaccount_Confirm(MainActivity a) {
		System.out.println("OpenFDAccount()" + a);
		act = a;
		
	}

	public void onBackPressed() { 
		return;
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,    
			Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		System.out.println("onCreateView() OpenFDAccount");
		View rootView = inflater.inflate(R.layout.fdaccount_confirm,
				container, false);
		dbms = new DatabaseManagement("shree_nagari.mbank", "listMobileBanking");
		this.dbs = new DialogBox(act);
		DecimalFormat df=new DecimalFormat(".00");
		loadProBarObj=new LoadProgressBar(act);
		
		Bundle b1 = getArguments();
		if(b1!=null)
		{
			strDuration=b1.getString("DURATION");
			strAmount=b1.getString("AMOUNT");
			strInterest_rates=b1.getString("INTRESTTYPE");
			tax_saver=b1.getString("TAXSAVER");
			maturity=b1.getString("MATURITY");
			strDebitacc = b1.getString("DEBITACC");
			
			year = b1.getString("YEAR");
			month = b1.getString("MONTH");
			day = b1.getString("DAY");

			maturityamt=b1.getString("MATURITYAMT");
			maturityDate=b1.getString("MATURITYDATE");
			intRates = b1.getString("INTERATES");
			monthDuration=b1.getString("MONTHDURATION");
			intamt=b1.getString("INTERATESAMOUNT");
		}
		
		
		strDebitacc=strDebitacc.substring(0,16);
		strintRates =intRates+" %";
		
		strAmount =df.format(new Double(strAmount));
		conAmt=strAmount+" RS";
		
		
		String MaturityAmt = calcAmt()+"";
		finalAmt =df.format(new Double(MaturityAmt));
		finalAmt=finalAmt+" Rs";
	
		
		txt_heading=(TextView)rootView.findViewById(R.id.txt_heading);
		txt_heading.setText(getString(R.string.confirm));
		btn_home = (ImageButton) rootView.findViewById(R.id.btn_home);
		btn_home.setImageResource(R.mipmap.ic_home_d);
		btn_home.setOnClickListener(this);
		txt_amount=(TextView) rootView.findViewById(R.id.txt_amount);
		intrest_Amt=(TextView) rootView.findViewById(R.id.intrest_Amt);
		txt_duration=(TextView) rootView.findViewById(R.id.txt_duration);
		
		txt_interest_rates=(TextView) rootView.findViewById(R.id.txt_interest_rates);
		txt_maturity_dates=(TextView) rootView.findViewById(R.id.txt_maturity_dates);
		
		//txt_conf_scheme=(TextView) rootView.findViewById(R.id.txt_conf_scheme);
		txt_conf_taxsaver=(TextView) rootView.findViewById(R.id.txt_conf_taxsaver);
		
		txt_maturity_amt=(TextView) rootView.findViewById(R.id.txt_maturity_amt);
		txt_debit_acc=(TextView) rootView.findViewById(R.id.txt_debit_acc);
		btn_confirm=(Button) rootView.findViewById(R.id.btn_confirm);
		btn_confirm.setOnClickListener(this);
		
		Cursor c1 = dbms.selectFromTable("SHAREDPREFERENCE", "", null);// ("select * from ",
		// null);
			if (c1 != null) 
			{
				while (c1.moveToNext())
				{
				//retValStr = ListEncryption.decryptData(c1.getString(0));
				//////Log.("retValStr", "......" + retValStr);
				//custid = ListEncryption.decryptData(c1.getString(2));
				////Log.e("CustId", "c......" + custid);
				}
			}
		setValue();
		return rootView;
	
	}
	
	public Double calcAmt()
	{
		double amount = Double.parseDouble(strAmount);
		double rates= Double.parseDouble(intRates);
		double month= Double.parseDouble(monthDuration);
		double result=(amount/100)*(rates/12)*month;
		double totalAmt =amount+result;
		return totalAmt;
		
	}
	public void showAlert(final String str) {
		ErrorDialogClass alert = new ErrorDialogClass(act,""+str)  
		{
			
			@Override
			public void onClick(View v)   
			{
				////Log.e("SetMPIN","SetMPIN...btn CLicked=="+R.id.btn_ok+"==="+v.getId());
				switch (v.getId()) 
				{
					case R.id.btn_ok:
						////Log.e("SetMPIN","SetMPIN...CASE trru="+isWSCalled);
					
						
						if((str.equalsIgnoreCase(respdesc_SaveDetails)) &&  (respcode.equalsIgnoreCase("0")))
						{
							post_SaveDetails();
						}
						else if((str.equalsIgnoreCase(respdesc_SaveDetails)) && (respcode.equalsIgnoreCase("1")))
						{
							this.dismiss();
						}
						if((str.equalsIgnoreCase(respdesc_OpenFdAccount)) &&  (respcode.equalsIgnoreCase("0")))
						{
							post_OpenFdAccount();
						}
						else if((str.equalsIgnoreCase(respdesc_OpenFdAccount)) && (respcode.equalsIgnoreCase("1")))
						{
							this.dismiss();
						}
					  break;
					  
					  
					default:
					  break;
				}
				dismiss();
			}
		};
		alert.show();
	}
	public void setValue() 
	{
		txt_amount.setText(conAmt);
		txt_duration.setText(strDuration);
		txt_interest_rates.setText(strintRates);
		txt_maturity_dates.setText(maturityDate);
		txt_maturity_amt.setText(finalAmt);
		txt_debit_acc.setText(strDebitacc);
		intrest_Amt.setText(intamt);
		txt_conf_taxsaver.setText(tax_saver);
		
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		/*case R.id.btn_back:
			Fragment fragment = new OtherServicesMenuActivity(act);
			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.frame_container, fragment).commit();
			break;*/

		case R.id.btn_home:
			Intent in = new Intent(act, MainActivity.class);
			//in.putExtra("VAR1", var1);
			//in.putExtra("VAR3", var3);
			startActivity(in);
			act.finish();
			break;
			
			
		case R.id.btn_confirm:
			
			CallWebServiceOpenFdAccount C = new CallWebServiceOpenFdAccount();
			C.execute();
		}
	}
	

	public class CallWebService extends AsyncTask<Void, Void, Void> 
	{
	String[] xmlTags = { "DRACCNO", "AMOUNT","DURATION", "INSTTYPE","TAXSAVER","MATURITYACTION","SCHEME"};

	String[] valuesToEncrypt = new String[7];
	String generatedXML = "";
	
	protected void onPreExecute() 
	{    
		retval = "";respcode="";respdesc_SaveDetails="";respdesc_OpenFdAccount="";		
		valuesToEncrypt[0] = strDebitacc;
		valuesToEncrypt[1] = strAmount;
		valuesToEncrypt[2] = strDuration;
		valuesToEncrypt[3] = intRates;
		valuesToEncrypt[4] = tax_saver;
		
		valuesToEncrypt[5] = maturity;
		valuesToEncrypt[6] = scheme;
		valuesToEncrypt[7] = MBSUtils.getSimNumber(act);
		generatedXML = CryptoUtil.generateXML(xmlTags, valuesToEncrypt);	
	}

	

	protected Void doInBackground(Void... arg0) 
	{ 
		NAMESPACE = getString(R.string.namespace);
		URL = getString(R.string.url);
		SOAP_ACTION = getString(R.string.soap_action);
		final String METHOD_NAME = "saveDetails";
		try 
		{
			SoapObject request = new SoapObject(NAMESPACE,METHOD_NAME);
			request.addProperty("para_value", generatedXML);
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			envelope.setOutputSoapObject(request);
			HttpTransportSE androidHttpTransport = new HttpTransportSE(URL,
					20000);
			if (androidHttpTransport != null)
				System.out
						.println("=============== androidHttpTransport is not null ");
			else
				System.out
						.println("=============== androidHttpTransport is  null ");

			androidHttpTransport.call(SOAP_ACTION, envelope);
			retval = envelope.bodyIn.toString().trim();
			int i = envelope.bodyIn.toString().trim().indexOf("=");
			retval = retval.substring(i + 1,
					retval.length() - 3);
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("OpenRDAccount   Exception" + e);
		}
		return null;
	}// end dodoInBackground2

	protected void onPostExecute(Void paramVoid)  
	{
		String[] xmlTags = { "OPENACCOUNT" };
		String[] xml_data = CryptoUtil.readXML(retval, xmlTags);
		String decryptedValues ="SUCCESS";
		JSONObject jsonObj;
		try
		{
			jsonObj = new JSONObject(xml_data[0]);
		
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
				respdesc_SaveDetails= jsonObj.getString("RESPDESC");
			}
			else
			{	
				respdesc_SaveDetails= "";
			}
		} catch (JSONException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(respdesc_SaveDetails.length()>0)
		{
			showAlert(respdesc_SaveDetails);
		}
		else{
		if (retval.indexOf("SUCCESS") > -1) 
		{
			 post_SaveDetails();
		}
		
		
		else if(retval.indexOf("FAILED") > -1)
		{
			retMess = getString(R.string.alert_172);
			showAlert(retMess);//setAlert();
			FragmentManager fragmentManager;
			Fragment fragment = new OtherServicesMenuActivity(act);
			act.setTitle(getString(R.string.lbl_other_srvce));
			fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.frame_container, fragment).commit();
		}// end else
	}
	}// end onPostExecute
}// end CallWebServiceSaveDetails  */
	
	public void post_SaveDetails(){

		respcode="";respdesc_SaveDetails= "";
		retMess = getString(R.string.alert_171);//+" "+getString(R.string.alert_121)+" "+tranId;
		showAlert(retMess);
		FragmentManager fragmentManager1;
		Fragment fragment1 = new OtherServicesMenuActivity(act);
		act.setTitle(getString(R.string.lbl_other_srvce));
		fragmentManager1 = getFragmentManager();
		fragmentManager1.beginTransaction()
				.replace(R.id.frame_container, fragment1).commit();		
	}

	public class CallWebServiceOpenFdAccount extends AsyncTask<Void, Void, Void>
	{
		LoadProgressBar loadProBarObj = new LoadProgressBar(act);
		String generatedXML = "",retVal="";
		String ValidationData="";//MBSUtils.getValidationData(act);
		
		@Override
		protected void onPreExecute() 
		{
			loadProBarObj.show();
			retval = "";respcode="";respdesc_SaveDetails="";respdesc_OpenFdAccount="";
			String[] xmlTags = {"PARAMS","CHECKSUM"};
	        String[] valuesToEncrypt = new String[2];
			JSONObject jsonobj = new JSONObject();

			try {
				jsonobj.put("CUSTID", custid);
				jsonobj.put("DEPOSIT_TYPE", strInterest_rates);
				jsonobj.put("DEBITACCNO", strDebitacc);
				jsonobj.put("DEPOSIT_YEAR", year);
				jsonobj.put("DEPOSIT_MONTH", month);
				jsonobj.put("DEPOSIT_DAYS", day);
				jsonobj.put("DEPOSIT_AMOUNT", strAmount);
				jsonobj.put("INTEREST_AMOUNT", intamt);
				jsonobj.put("INTEREST_RATE", intRates);
				jsonobj.put("MATURITY_AMT", maturityamt);
				jsonobj.put("IMEINO", MBSUtils.getImeiNumber(act));
				jsonobj.put("SIMNO", MBSUtils.getSimNumber(act));
				jsonobj.put("METHODCODE","61");
				ValidationData=MBSUtils.getValidationData(act,jsonobj.toString());
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			valuesToEncrypt[0] = jsonobj.toString();
			valuesToEncrypt[1] = ValidationData;
			generatedXML = CryptoUtil.generateXML(xmlTags, valuesToEncrypt);
		};

		@Override
		protected Void doInBackground(Void... arg0) {
			NAMESPACE = getString(R.string.namespace);
			URL = getString(R.string.url);
			SOAP_ACTION = getString(R.string.soap_action);
			final String METHOD_NAME_1 = "mbsInterCall";//"OpenFdAccount";

			int i = 0;
			try {
					SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME_1);
					request.addProperty("para_value", generatedXML);
					SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
							SoapEnvelope.VER11);
					envelope.setOutputSoapObject(request);
					HttpTransportSE androidHttpTransport = new HttpTransportSE(URL,
							15000);

					if (androidHttpTransport != null)
						System.out
								.println("=============== androidHttpTransport is not null ");
					else
						System.out.println("=============== androidHttpTransport is  null ");
					androidHttpTransport.call(SOAP_ACTION, envelope);
					retVal = envelope.bodyIn.toString().trim();
					retVal = retVal.substring(retVal.indexOf("=") + 1,
							retVal.length() - 3);
				
			} catch (Exception e) {
				responseJSON = "NULL";
			}
			return null;
		}

		@Override
		protected void onPostExecute(final Void result) 
		{
			 loadProBarObj.dismiss();
			 String[] xml_data = CryptoUtil.readXML(retVal, new String[]{"PARAMS","CHECKSUM"});
			 String str=xml_data[0];
			 JSONObject jsonObj;
			 try
			 {
				 jsonObj = new JSONObject(xml_data[0]);
				 ValidationData=xml_data[1].trim();			
				 if(ValidationData.equals(MBSUtils.getValidationData(act, xml_data[0].trim())))
				 {
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
					respdesc_OpenFdAccount= jsonObj.getString("RESPDESC");
				}
				else
				{	
					respdesc_OpenFdAccount= "";
				}
								
				if(respdesc_OpenFdAccount.length()>0)
				{
					showAlert(respdesc_OpenFdAccount);
				}
				else
				{
			        if (retval.indexOf("SUCCESS~") > -1) 
			        {
			        	post_OpenFdAccount();
			        }
			        else
					{
						retMess = getString(R.string.alert_172);
						showAlert(retMess);//setAlert();
						FragmentManager fragmentManager;
						Fragment fragment = new OtherServicesMenuActivity(act);
						act.setTitle(getString(R.string.lbl_other_srvce));
						fragmentManager = getFragmentManager();
						fragmentManager.beginTransaction()
								.replace(R.id.frame_container, fragment).commit();
					}// end else             
				}
			}
			else
			{
				MBSUtils.showInvalidResponseAlert(act);
			}		
		} 
		catch (JSONException e) 
		{
			e.printStackTrace();
		}
		
		}// onPostExecute
	}
	public void post_OpenFdAccount(){
		respcode="";respdesc_OpenFdAccount= "";
        String str1=retval.split("~")[1];
        String values[] = retval.split("~");
        tranId=values[1];
        retMess = getString(R.string.alert_171)+" "+getString(R.string.alert_121)+" "+tranId;
		showAlert(retMess);
		FragmentManager fragmentManager1;
		Fragment fragment1 = new OtherServicesMenuActivity(act);
		act.setTitle(getString(R.string.lbl_other_srvce));
		fragmentManager1 = getFragmentManager();
		fragmentManager1.beginTransaction()
				.replace(R.id.frame_container, fragment1).commit();            
	}
	
}

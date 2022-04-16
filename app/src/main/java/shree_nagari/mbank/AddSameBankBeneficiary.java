package shree_nagari.mbank;

import java.security.PrivateKey;

import javax.crypto.spec.SecretKeySpec;

import mbLib.CryptoClass;
import mbLib.CustomDialogClass;
import mbLib.DatabaseManagement;
import mbLib.DialogBox;
import mbLib.ListEncryption;
import mbLib.MBSUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import shree_nagari.mbank.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
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
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
//import mbLib.DialogBox;

public class AddSameBankBeneficiary extends Fragment implements OnClickListener {
	MainActivity act;
	AddSameBankBeneficiary addSameBankBenf;
	ProgressBar p_wait;
	Button btn_fetchName, btn_submit;
	EditText txtName, txtmobNo, txtEmail, txtNick_Name, txtAccNo;
	TextView txt_heading;
	DatabaseManagement dbms;	
	int cnt = 0, flag = 0;
	String str = "", retMess = "", cust_name = "", tmpXMLString = "",
			retVal = "";
	DialogBox dbs;
	String flg = "false";
	public String encrptdMpin,Mpin="";
	private static final String MY_SESSION = "my_session";
	PrivateKey var1=null;	  
	String var5="",var3="",respdesc = "",retvalwbs="";
	SecretKeySpec var2=null;
	String custId = "", accNo = "", accNm = "", mobNo = "", nickNm = "", 
			mailId = "",userId="",respcode="",reTval="",saveBeneficiariesrespdesc="",validateAndGetAccountInforespdesc="";

	String mobPin = "",when_fetch = "",decryptedAccName="";
	Bundle bdn;
	ImageView img_heading,btn_home1,btn_logout;
	public AddSameBankBeneficiary() {
	}

	@SuppressLint("ValidFragment")
	public AddSameBankBeneficiary(MainActivity a) {
		System.out.println("AddSameBankBeneficiary()" + a);
		act = a;
		addSameBankBenf = this;
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		System.out.println("onCreateView() AddSameBankBeneficiary");
		var1 = act.var1;
		var3 = act.var3;
		View rootView = inflater.inflate(R.layout.add_samebank_beneficiary,
				container, false);
                dbms = new DatabaseManagement("shree_nagari.mbank", "listMobileBanking");
		
		txt_heading = (TextView) rootView.findViewById(R.id.txt_heading);
		txtAccNo = (EditText) rootView.findViewById(R.id.txtAccNo2);
		btn_fetchName = (Button) rootView.findViewById(R.id.btn_fetchName2);
		txtName = (EditText) rootView.findViewById(R.id.txtName2);
		txtmobNo = (EditText) rootView.findViewById(R.id.txtmobNo2);
		txtEmail = (EditText) rootView.findViewById(R.id.txtEmail2);
		txtNick_Name = (EditText) rootView.findViewById(R.id.txtNick_Name2);
		btn_submit = (Button) rootView.findViewById(R.id.btn_submit2);
		p_wait = (ProgressBar) rootView.findViewById(R.id.pro_bar);
		btn_home1 = (ImageView) rootView.findViewById(R.id.btn_home1);
		btn_logout = (ImageView) rootView.findViewById(R.id.btn_logout);
btn_logout.setVisibility(View.GONE);
		/*btn_back = (ImageButton) rootView.findViewById(R.id.btn_back);*/
		img_heading = (ImageView) rootView.findViewById(R.id.img_heading);
		img_heading.setBackgroundResource(R.mipmap.benefeciary);	//btn_home.setImageResource(R.drawable.ic_home_d);
		//btn_back.setImageResource(R.drawable.backover);
		txt_heading.setText(getString(R.string.frmtitle_add_same_bnk_bnf));
		//btn_back.setOnClickListener(this);
		btn_logout.setOnClickListener(this);
		btn_home1.setOnClickListener(this);
		p_wait.setMax(10);
		p_wait.setProgress(1);
		btn_fetchName.setOnClickListener(this);
		btn_submit.setOnClickListener(this);
	

        Cursor c1=dbms.selectFromTable("SHAREDPREFERENCE", "", null);//("select * from ", null);
        if(c1!=null)
        {
        	while(c1.moveToNext())
	        {	
        		custId=c1.getString(2);
	        	Log.e("custId","......"+custId);
	        	userId=c1.getString(3);
		    	Log.e("userId","......"+userId);
	        }
        }
		/*txtAccNo.addTextChangedListener(new TextWatcher() { 

			public void afterTextChanged(Editable s) { 
				// TODO Auto-generated method stub
				txtName.setText("");
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, 
					int after) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, 
					int count) {

			}

		});*/

		return rootView;
	}

	public void initAll() {
		txtAccNo.setText("");
		txtName.setText("");
		txtmobNo.setText("");
		txtEmail.setText("");
		txtNick_Name.setText("");
	}

	public void onClick(View v) {
		// TODO Auto-generated method stub
		// pb_wait.setVisibility(ProgressBar.VISIBLE);
		switch (v.getId()) {
		/*case R.id.btn_back:
			System.out.println("Clicked on back");
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
			break;
		case R.id.btn_fetchName2:
			accNo = txtAccNo.getText().toString().trim();
			when_fetch = "EXPLICIT";
			if (accNo.length() != 16) {
				// /////retMess = "Please Enter Account Number.";
				retMess = getString(R.string.alert_001);
				showAlert(retMess);
				txtAccNo.requestFocus();
			} else {
				flag = chkConnectivity();
				if (flag == 0) {
					CallWebService_fetch_ac_holdernm C = new CallWebService_fetch_ac_holdernm();
					C.execute();
					// setAlert();
				}
			}
			break;

		case R.id.btn_submit2:

			accNo = txtAccNo.getText().toString().trim();
			accNm = txtName.getText().toString().trim();
			mobNo = txtmobNo.getText().toString().trim();
			nickNm = txtNick_Name.getText().toString().trim();
			mailId = txtEmail.getText().toString().trim();
			
			int niknm_len = nickNm.length();
			// if(accNo.equalsIgnoreCase(""))
			if (accNo.length() != 16) {
				// /////retMess = "Please Enter Account Number.";
				retMess = getString(R.string.alert_001);
				showAlert(retMess);
				txtAccNo.requestFocus();
			} 
			else if (accNm.length() == 0) {
				retMess = getString(R.string.alert_096);
				showAlert(retMess);
				txtName.requestFocus();
			} 
			else if (accNm.length() > 40) {
				retMess = getString(R.string.alert_095);
				showAlert(retMess);
				txtName.requestFocus();
			} 
			/*else if (mobNo.length() != 10) 
			{
				retMess = getString(R.string.alert_002);
				showAlert(retMess);
				txtmobNo.requestFocus();
			}*/ 
			else if (nickNm.length() == 0) {
				// ////retMess = "Please Enter Nickname.";
				retMess = getString(R.string.alert_003);
				showAlert(retMess);
				txtNick_Name.requestFocus();
			} else if (nickNm.contains(" ") == true) {
				// ////retMess = "You Can Not Use Blank Spaces In Nickname.";
				retMess = getString(R.string.alert_004);
				showAlert(retMess);
				txtNick_Name.requestFocus();
			} else if (niknm_len < 4 || niknm_len > 15) {
				//Log.i("niknm_len violated","niknm_len violated.................");
				retMess = getString(R.string.alert_005);
				showAlert(retMess);
				txtNick_Name.requestFocus();
			} else if (mobNo.length() !=0 && !MBSUtils.validateMobNo(mobNo)) {
				// retMess = "Please Enter Valid Mobile Number.";
				retMess = getString(R.string.alert_006);
				showAlert(retMess);
				txtmobNo.requestFocus();
			} else if (mailId.length() > 0 && !MBSUtils.validateEmail(mailId)) {

				// /////retMess = "Please Enter Valid E-mail Id.";
				retMess = getString(R.string.alert_007);
				showAlert(retMess);
				txtEmail.requestFocus();
			}

			else {
				flag = chkConnectivity();

				if (flag == 0) {
					InputDialogBox inputBox = new InputDialogBox(act);
					inputBox.show();
					/*CallWebService_save_beneficiary c = new CallWebService_save_beneficiary();
					c.execute();*/
				}
				// SaveBeneficiary();
			}
			break;

		default:
			break;
		}
	}// end onClick

	public void post_successlog(String retvalwbs) {
		respcode = "";
		respdesc = "";
		act.finish();
		System.exit(0);

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

	public void SaveBeneficiary() {
		// flag = chkConnectivity();

		// if (flag == 0)
		{
			InputDialogBox inputBox = new InputDialogBox(act);
			inputBox.show();

		}

	}

	// Fetch account name
	class CallWebService_fetch_ac_holdernm extends AsyncTask<Void, Void, Void> {
		String accNo = "";

		LoadProgressBar loadProBarObj = new LoadProgressBar(act);
       JSONObject jsonObj = new JSONObject();
		String ValidationData="";

		@Override
		protected void onPreExecute() {
		try{
			respcode="";
			reTval="";
			validateAndGetAccountInforespdesc="";
			// p_wait.setVisibility(ProgressBar.VISIBLE);
			loadProBarObj.show();
			accNo = txtAccNo.getText().toString().trim();
		    jsonObj.put("ACCNO", accNo);
            jsonObj.put("CUSTID", custId);
            jsonObj.put("IMEINO", MBSUtils.getImeiNumber(act));
            jsonObj.put("SIMNO", MBSUtils.getSimNumber(act));
            jsonObj.put("METHODCODE","18");
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
			
				retMess = getString(R.string.alert_000);
				System.out.println(e.getMessage());
				cnt = 0;
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
				/* ValidationData=xml_data[1].trim();
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
					validateAndGetAccountInforespdesc= jsonObj.getString("RESPDESC");
				}
				else
				{	
					validateAndGetAccountInforespdesc= "";
				}
				
			if(validateAndGetAccountInforespdesc.length()>0)
			{
				showAlert(validateAndGetAccountInforespdesc);
			}
			else{
            try{
                        
			// retVal = "SUCCESS~KAVITA KIRAN KADEKAR";
			if (reTval.indexOf("FAILED") > -1) {
				if (reTval.indexOf("NOT_EXISTS") > -1) {

					// ////retMess = "Invalid Account Number.";
					retMess = getString(R.string.alert_008);
					txtAccNo.requestFocus();
					showAlert(retMess);
				} else if (reTval.indexOf("EXISTS") > -1) {
					loadProBarObj.dismiss();
					// ////retMess = "This Beneficiary Is Already Added.";
					retMess = getString(R.string.alert_009);
					txtAccNo.requestFocus();
					showAlert(retMess);
				} else {
					retMess = getString(R.string.alert_000);
					txtAccNo.requestFocus();
					showAlert(retMess);
				}
			} else {
				post_successGetAccountInfo(reTval);
				
			}}
			catch(Exception je)
			{
			je.printStackTrace();
			}
			loadProBarObj.dismiss();

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
	public 	void post_successGetAccountInfo(String reTval)
	{
    try{
    	respcode="";
    	
    	validateAndGetAccountInforespdesc="";
	JSONObject retJson = new JSONObject(reTval);
    String decryptedAccName=retJson.getString("ACCNAME");

		// SUCCESS~SHRI KHEBUDKAR JAGDISH GOVIND
		//Log.i("mayuri success", "retVal=============" + retVal);
		//Log.i("mayuri success", "success" + retVal.split("~")[1]);

	//	String[] xml_data = CryptoUtil.readXML(retVal.split("~")[1],
	//			xmlTags);
		//System.out.println("xml_data.len :" + xml_data.length);
	//	String decryptedAccName = xml_data[0];
		//System.out.println("decrypted Acc holder Name :"
		//		+ decryptedAccName);
		// String decryptedAccName = "KAVITA KIRAN KADEKAR";
		Bundle b = new Bundle();
		// add data to bundle
		//Log.i("Return value:", decryptedAccName);

		// retVal=""SUCCESS~KAVITA KIRAN KADEKAR";
		if (decryptedAccName.equalsIgnoreCase("")) {
			decryptedAccName = "ACCNAMEISNULL";
		} else {
			decryptedAccName = decryptedAccName;
		}

		txtName.setText(decryptedAccName);
		String acno = txtAccNo.getText().toString().trim();
		if (when_fetch == "AUTO") {
			dbs = new DialogBox(act);

			dbs.get_adb().setMessage(
					"Continue With Name \"" + decryptedAccName
							+ "\" For Account No." + acno + " ?");
			dbs.get_adb().setPositiveButton("Yes",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface arg0,
								int arg1) {
							// TODO Auto-generated method stub

							SaveBeneficiary();
							// System.exit(0);
						}
					});
			dbs.get_adb().setNegativeButton("No",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface arg0,
								int arg1) {
							// TODO Auto-generated method stub
							arg0.cancel();
						}
					});
			dbs.get_adb().show();
			// break;
		}
}
	catch(JSONException je)
	{
	je.printStackTrace();
				}
	}

	// Save Beneficiary
	class CallWebService_save_beneficiary extends AsyncTask<Void, Void, Void> {
		LoadProgressBar loadProBarObj = new LoadProgressBar(act);
	           JSONObject jsonObj = new JSONObject();
		
		String ValidationData="";

		@Override
		protected void onPreExecute() { 
                     try{
                    	 respcode="";
                    	 reTval="";
                    	 saveBeneficiariesrespdesc="";
			// p_wait.setVisibility(ProgressBar.VISIBLE);
			loadProBarObj.show();

			// txtName.setEnabled(true);
			accNm = txtName.getText().toString().trim();
              jsonObj.put("CUSTID", custId.trim());
              jsonObj.put("ACCNO", accNo.trim());
              jsonObj.put("ACCNM",accNm.trim());
              jsonObj.put("MOBNO", mobNo.trim());
              jsonObj.put("NICKNM", nickNm.trim());
              jsonObj.put("MAILID",mailId.trim());
              jsonObj.put("TRANSFERTYPE","Y");
              jsonObj.put("IFSCCD", "DUMMY");
              jsonObj.put("MMID","DUMMY");
              jsonObj.put("IINSERTUPDTDLT", "I");
              jsonObj.put("BENSRNO","00");
              jsonObj.put("IMEINO",MBSUtils.getImeiNumber(act));
              jsonObj.put("MPIN",Mpin);
              jsonObj.put("SIMNO", MBSUtils.getSimNumber(act));
              jsonObj.put("METHODCODE","14");
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
				
				retMess = getString(R.string.alert_000);
				System.out.println(e.getMessage());
				cnt = 0;
			}
			return null;
		}

		@Override
		protected void onPostExecute(final Void result) {
                     try{
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
					reTval = jsonObj.getString("RETVAL");
				}
				else
				{
					reTval = "";
				}
				if (jsonObj.has("RESPDESC"))
				{
					saveBeneficiariesrespdesc= jsonObj.getString("RESPDESC");
				}
				else
				{	
					saveBeneficiariesrespdesc= "";
				}
				
			if(saveBeneficiariesrespdesc.length()>0)
			{
				showAlert(saveBeneficiariesrespdesc);
			}
			else{


			if (reTval.indexOf("FAILED") > -1) {
			
				if (reTval.indexOf("DUPLICATEACCOUNT") > -1) {
				

					loadProBarObj.dismiss();
					
					retMess = getString(R.string.alert_010);

					showAlert(retMess);
				} else if (reTval.indexOf("DUPLICATENICKNAME") > -1) {
				

					loadProBarObj.dismiss();
					
					retMess = getString(R.string.alert_011);

					showAlert(retMess);
} 
				else if (reTval.indexOf("InvalidAccount") > -1) {
					

					loadProBarObj.dismiss();
				
					retMess = getString(R.string.alert_167_2);
					showAlert(retMess);
					
				}else if (reTval.indexOf("WRONGMPIN") > -1) 
				{
					loadProBarObj.dismiss();
					retMess = getString(R.string.alert_125);
					showAlert(retMess);
					//SaveBeneficiary();
				}
				 else {
					// ///retMess="Failed To Add Same Bank Beneficiary Due To Server Problem.";
					retMess = getString(R.string.alert_012);

					loadProBarObj.dismiss();
					showAlert(retMess);
					initAll();
					onCreate(bdn);
				}
			} else {
				
				loadProBarObj.dismiss();
				post_successsaveBeneficiaries(reTval);
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
              catch (Exception je) {
            je.printStackTrace();
        }
                     
                     
}
	}
	public 	void post_successsaveBeneficiaries(String reTval)
	{
	 respcode="";
   	 saveBeneficiariesrespdesc="";
		flg = "true";
		// ///retMess="Same Bank Beneficiary Added Successfully.";
		retMess = getString(R.string.alert_013);

		showAlert(retMess);
		initAll();
	}

	public void showAlert(final String str) {
		// Toast.makeText(this, str, Toast.LENGTH_LONG).show();
		ErrorDialogClass alert = new ErrorDialogClass(act, "" + str){
@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				super.onClick(v);
				if((str.equalsIgnoreCase(validateAndGetAccountInforespdesc)) &&  (respcode.equalsIgnoreCase("0")))
				{
					post_successGetAccountInfo(reTval);
				}
				else if((str.equalsIgnoreCase(validateAndGetAccountInforespdesc)) && (respcode.equalsIgnoreCase("1")))
				{
					this.dismiss();
				}
				else if((str.equalsIgnoreCase(saveBeneficiariesrespdesc)) &&  (respcode.equalsIgnoreCase("0")))
				{
					post_successsaveBeneficiaries(reTval);
				}
				else if((str.equalsIgnoreCase(saveBeneficiariesrespdesc)) && (respcode.equalsIgnoreCase("1")))
				{
					this.dismiss();
				}
				else if(str.equalsIgnoreCase(act.getString(R.string.alert_125)))
				{
					SaveBeneficiary();
				}
				/*if (flg == "true") 
				{
					Log.e("Inside If", "Inside if===" + flg);
					switch (v.getId()) {
					case R.id.btn_ok:
						// if (WSCalled) {
						Fragment fragment = new ManageBeneficiaryMenuActivity(
								act);
						FragmentManager fragmentManager = getFragmentManager();
						fragmentManager.beginTransaction()
								.replace(R.id.frame_container, fragment)
								.commit();
					}
					this.dismiss();
				}*/
			}
		};
		alert.show();
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
			//		.println("AddSameBankBeneficiary	in chkConnectivity () state1 ---------"
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
					// ////retMess =
					// "Network Disconnected. Please Check Network Settings.";
					retMess = getString(R.string.alert_014);
					showAlert(retMess);
					break;
				default:
					flag = 1;
					// ////retMess = "Network Unavailable. Please Try Again.";
					retMess = getString(R.string.alert_000);
					showAlert(retMess);
					break;
				}
			} else {
				flag = 1;
				// ////retMess = "Network Unavailable. Please Try Again.";
				retMess = getString(R.string.alert_000);
				showAlert(retMess);
			}
		} catch (NullPointerException ne) {

			Log.i("mayuri","NullPointerException Exception" + ne);
			flag = 1;
			// /////retMess = "Network Unavailable. Please Try Again.";
			retMess = getString(R.string.alert_000);
			// setAlert();
			showAlert(retMess);
		} catch (Exception e) {
			Log.i("mayuri", "Exception" + e);
			flag = 1;
			// ////retMess = "Network Unavailable. Please Try Again.";
			retMess = getString(R.string.alert_000);
			showAlert(retMess);
		}
		return flag;
	}

	// inner class
	public class InputDialogBox extends Dialog implements OnClickListener {
		Activity activity;
		String msg, title;
		Context appAcontext;
		EditText mpin;
		Button btnOk;
		String strmpin = "";
		TextView txtLbl;
		

		public InputDialogBox(Activity activity) {
			super(activity);
		}// end InputDialogBox

		protected void onCreate(Bundle bdn) {
			super.onCreate(bdn);
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			setContentView(R.layout.dialog_design);
			mpin = (EditText) findViewById(R.id.txtMpin);
			btnOk = (Button) findViewById(R.id.btnOK);
			mpin.setVisibility(EditText.VISIBLE);
			btnOk.setVisibility(Button.VISIBLE);
			btnOk.setOnClickListener(this);
		}

		@Override
		public void onClick(View v) {
			try {
				
				String str = mpin.getText().toString().trim();
				Mpin=str;
				 encrptdMpin=ListEncryption.encryptData(custId+str);
				
				String encrptdUserpin=ListEncryption.encryptData(userId+str);
				
				if (str.equalsIgnoreCase("")) {
					this.hide();
					// ///retMess = "Enter Valid MPIN.";
					retMess = getString(R.string.alert_015);
					showAlert(retMess);
					mpin.setText("");
				} else //if (encrptdMpin.equalsIgnoreCase(mobPin) ||encrptdUserpin.equalsIgnoreCase(mobPin) ) {
                                         {
					flag=chkConnectivity();
					if (flag == 0) {
						callValidateTranpinService C = new callValidateTranpinService();
						//CallWebService_save_beneficiary C = new CallWebService_save_beneficiary();
						C.execute();
						this.hide();
					}
				} /*else {
					///System.out
					//		.println("=========== inside else ==============");
					this.hide();
					// ///retMess = "Enter Valid MPIN.";
					retMess = getString(R.string.alert_125);
					showAlert(retMess);
					mpin.setText("");
				}*/
			} catch (Exception e) {
				e.printStackTrace();
				System.out
						.println("Exception in InputDialogBox of onClick:=====>"
								+ e);
			}
		}// end onClick
	}// end InputDialogBox
	
	class callValidateTranpinService extends AsyncTask<Void, Void, Void> 
	{
		String retval = "";
		LoadProgressBar loadProBarObj = new LoadProgressBar(act);
		String ValidationData="";
		JSONObject obj = new JSONObject();
		
		protected void onPreExecute() 
		{
			loadProBarObj.show();
		
			try 
			{
				String location=MBSUtils.getLocation(act);
				obj.put("SIMNO", MBSUtils.getSimNumber(act));
				obj.put("IMEINO", MBSUtils.getImeiNumber(act));
				obj.put("MPIN", Mpin);
				obj.put("CUSTID", custId);
				obj.put("MOBILENO", MBSUtils.getMyPhoneNO(act));
				obj.put("IPADDRESS", MBSUtils.getLocalIpAddress());
				obj.put("OSVERSION", Build.VERSION.RELEASE);
				obj.put("LATITUDE", location.split("~")[0]);
				obj.put("LONGITUDE", location.split("~")[1]);
				obj.put("METHODCODE","84"); 
				// ValidationData=MBSUtils.getValidationData(act,obj.toString());
			
			} 
			catch (JSONException e) 
			{
				e.printStackTrace();
			}
		}

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
				if(androidHttpTransport!=null)
					System.out.println("=============== androidHttpTransport is not null ");
				else
					System.out.println("=============== androidHttpTransport is  null ");
						      
				androidHttpTransport.call(value5, envelope);
				var5 = envelope.bodyIn.toString().trim();
				int i = envelope.bodyIn.toString().trim().indexOf("=");
				var5 = var5.substring(i + 1, var5.length() - 3);
				
			}// end try
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			return null;
		}// end dodoInBackground2

		protected void onPostExecute(Void paramVoid) 
		{
			  JSONObject jsonObj;
	   			try
	   			{
	   				String str=CryptoClass.Function6(var5,var2);
	   			 jsonObj = new JSONObject(str.trim());
	   		/*ValidationData=xml_data[1].trim();
	   		if(ValidationData.equals(MBSUtils.getValidationData(act, xml_data[0].trim())))
	   		{*/
			  String decryptedAccounts = str.trim();
			loadProBarObj.dismiss();
			
			if (decryptedAccounts.indexOf("SUCCESS") > -1) 
			{
				saveData();
			} 
			
			else if (decryptedAccounts.indexOf("FAILED#") > -1) 
			{
				retMess = getString(R.string.alert_032);
				showAlert(retMess);// setAlert();
			} 
			else if (decryptedAccounts.indexOf("BLOCKEDFORDAY") > -1) 
			{
				retMess = getString(R.string.login_alert_005);
				showAlert(retMess);// setAlert();
			} 
			else if (decryptedAccounts.indexOf("WRONGMPIN") > -1) 
			{
				JSONObject obj=null;
				try {
					//{"RETVAL":"FAILED~WRONGMPIN~0","RESPCODE":"1"}
					obj = new JSONObject(decryptedAccounts);
					String msg[] = obj.getString("RETVAL").split("~");
					String first = msg[1];
					String second = msg[2];
					Log.e("OMKAR", "---"+second+"----");
					int count = Integer.parseInt(second);
					count = 5 - count;
					loadProBarObj.dismiss();
					retMess = act.getString(R.string.alert_125) + " " + count + " "
							+ act.getString(R.string.alert_125_2);
					showAlert(retMess);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
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
	
	}// end callValidateTranpinService
	
	public void saveData() 
	{
		try 
		{
			JSONObject jsonObj = new JSONObject();
			try 
			{
			  jsonObj.put("CUSTID", custId.trim());
              jsonObj.put("ACCNO", accNo.trim());
              jsonObj.put("ACCNM",accNm.trim());
              jsonObj.put("MOBNO", mobNo.trim());
              jsonObj.put("NICKNM", nickNm.trim());
              jsonObj.put("MAILID",mailId.trim());
              jsonObj.put("TRANSFERTYPE","Y");
              jsonObj.put("IFSCCD", "DUMMY");
              jsonObj.put("MMID","DUMMY");
              jsonObj.put("IINSERTUPDTDLT", "I");
              jsonObj.put("BENSRNO","00");
              jsonObj.put("IMEINO",MBSUtils.getImeiNumber(act));
              jsonObj.put("MPIN",Mpin);
              jsonObj.put("SIMNO", MBSUtils.getSimNumber(act));
              
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			Bundle bundle=new Bundle();
		Fragment fragment = new BeneficiaryOTP(act);
		bundle.putString("CUSTID", custId);
		bundle.putString("FROMACT", "ADDSAMBENF");
		bundle.putString("JSONOBJ", jsonObj.toString());
		fragment.setArguments(bundle);
		FragmentManager fragmentManager = addSameBankBenf.getFragmentManager();
		fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
	} catch (Exception e) {
		e.printStackTrace();
	}
}

}// end AddSameBankBeneficiary

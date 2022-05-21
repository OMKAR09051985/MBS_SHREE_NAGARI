package shree_nagari.mbank;

import java.security.PrivateKey;
import java.util.ArrayList;

import javax.crypto.spec.SecretKeySpec;

import mbLib.CryptoClass;
import mbLib.CryptoUtil;
import mbLib.CustomDialogClass;
import mbLib.DatabaseManagement;
import mbLib.ListEncryption;
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
import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

public class AddOtherBankBeneficiary extends Fragment implements
		OnClickListener {
	MainActivity act;
	AddOtherBankBeneficiary addOtherBankBenf;
	EditText txtIFSC_Code, txtMMID, txtMobile_No, txtAccNo, txtName, txtBank,
			txtBranch, txtEmail, txtNick_Name;
	Button btn_submit, fetchIFSC;
	LinearLayout add_benf_layout, get_ifsc_layout;
	TextView txt_heading;
	ProgressBar p_wait;
	JSONArray jsonArr;
	DatabaseManagement dbms;
	String flg="false";
	Spinner spi_bank, spi_state, spi_district, spi_city, spi_branch;
	ImageButton spnr_btn1, spnr_btn2, spnr_btn3, spnr_btn4,
			spnr_btn5, btn_fetchBnkBrn;
	// DialogBox dbs;
	private static final String MY_SESSION = "my_session";
	private static String NAMESPACE = "";
	private static String URL = "";
	private static String SOAP_ACTION = "";
	private static  String METHOD_NAME2 = "";
	PrivateKey var1=null;	  
	String var5="",var3="",respdesc="",retvalwbs="";
	SecretKeySpec var2=null;
	
	String account_No = "", name = "", mobile_no = "", nick_name = "",
			email = "", same_bank = "", ifsc_code = "", insrtUpdtDlt = "";
	String stringValue, str2 = "", mobPin = "", tmpXMLString = "", retVal = "",
			userpin = "";
	String custId = "", accNo = "", accNm = "", mobNo = "", nickNm = "",
			mailId = "";
	String ifsCD = "", bnCD = "", brCD = "", mmId = "", strIfsc = "";
	String acnt_inf, all_acnts, str = "", retMess = "", cust_name = "",
			checkedValue = "";
	int cnt = 0, flag = 0, checkCnt = 0;
	Bundle bdn;
	public String encrptdMpin,Mpin="";
	String when_fetch = "",respcode="",retvalweb="",respdescget_bnkbrn="",respdescsave_beneficiary="";
	String respdescGetStates="",respdescGetDistricts="",respdescGetCities="",respdescGetBranches="",respdescGetIFSC="";
	private String userId;
	ImageView img_heading,btn_home1,btn_logout;
	public AddOtherBankBeneficiary() {
	}

	@SuppressLint("ValidFragment")
	public AddOtherBankBeneficiary(MainActivity a) {
		System.out.println("AddOtherBankBeneficiary()" + a);
		act = a;
		addOtherBankBenf = this;
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		System.out.println("onCreateView() AddOtherBankBeneficiary");
		var1 = act.var1;
		var3 = act.var3;
		View rootView = inflater.inflate(R.layout.add_otherbank_beneficiary,
				container, false);
		dbms = new DatabaseManagement("shree_nagari.mbank", "listMobileBanking");
		img_heading = (ImageView) rootView.findViewById(R.id.img_heading);
		img_heading.setBackgroundResource(R.mipmap.benefeciary);
		// SharedPreferences sp = act.getSharedPreferences(MY_SESSION,
		Cursor c1 = dbms.selectFromTable("SHAREDPREFERENCE", "", null);// ("select * from ",
																		// null);
		if (c1 != null) {
			while (c1.moveToNext()) {
				custId = c1.getString(2);
				Log.e("retValStr", "......" + custId);
			}
		}
		// Context.MODE_PRIVATE);

		// custId = sp.getString("custId", "custId");
		// userId=sp.getString("userId", "userId");
		System.out.println("============ inside onCreate 3 ===============");
		txtIFSC_Code = (EditText) rootView.findViewById(R.id.txtIFSC_Code2);
		txtMMID = (EditText) rootView.findViewById(R.id.txtMMID2);
		txtMobile_No = (EditText) rootView.findViewById(R.id.txtMobile_No2);
		txtAccNo = (EditText) rootView.findViewById(R.id.txtAccNo2);
		txtName = (EditText) rootView.findViewById(R.id.txtName2);
		txtBank = (EditText) rootView.findViewById(R.id.txt_bank_name);
		// txtBranch = (EditText) rootView.findViewById(R.id.txtBranch2);
		txtEmail = (EditText) rootView.findViewById(R.id.txtEmail2);
		txtNick_Name = (EditText) rootView.findViewById(R.id.txtNick_Name2);
		p_wait = (ProgressBar) rootView.findViewById(R.id.pro_bar);
		btn_home1 = (ImageView) rootView.findViewById(R.id.btn_home1);
		btn_logout = (ImageView) rootView.findViewById(R.id.btn_logout);
		btn_logout.setVisibility(View.GONE);
		// btn_back = (ImageButton) rootView.findViewById(R.id.btn_back);
		txt_heading = (TextView) rootView.findViewById(R.id.txt_heading);
		// btn_home.setImageResource(R.drawable.ic_home_d);
		// btn_back.setImageResource(R.drawable.backover);
		txt_heading.setText(getString(R.string.frmtitle_add_other_bnk_bnf));

		spi_bank = (Spinner) rootView.findViewById(R.id.spnr_bank_name);
		spi_state = (Spinner) rootView.findViewById(R.id.spnr_state);
		spi_district = (Spinner) rootView.findViewById(R.id.spnr_district);
		spi_city = (Spinner) rootView.findViewById(R.id.spnr_city);
		spi_branch = (Spinner) rootView.findViewById(R.id.spnr_branch);

		spnr_btn1 = (ImageButton) rootView.findViewById(R.id.spinner_btn1);
		spnr_btn2 = (ImageButton) rootView.findViewById(R.id.spinner_btn2);
		spnr_btn3 = (ImageButton) rootView.findViewById(R.id.spinner_btn3);
		spnr_btn4 = (ImageButton) rootView.findViewById(R.id.spinner_btn4);
		spnr_btn5 = (ImageButton) rootView.findViewById(R.id.spinner_btn5);

		// fetchIFSC.setOnClickListener(this);
		spnr_btn1.setOnClickListener(this);
		spnr_btn2.setOnClickListener(this);
		spnr_btn3.setOnClickListener(this);
		spnr_btn4.setOnClickListener(this);
		spnr_btn5.setOnClickListener(this);

		add_benf_layout = (LinearLayout) rootView
				.findViewById(R.id.add_benf_layout);
		get_ifsc_layout = (LinearLayout) rootView
				.findViewById(R.id.get_ifsc_layout);
		// btn_back.setOnClickListener(this);
		btn_home1.setOnClickListener(this);
		btn_logout.setOnClickListener(this);
		p_wait.setMax(10);
		p_wait.setProgress(1);

		btn_fetchBnkBrn = (ImageButton) rootView.findViewById(R.id.btn_fetchIFSC);
		btn_submit = (Button) rootView.findViewById(R.id.btn_submit2);

		System.out.println("============ inside onCreate 4 ===============");
		btn_fetchBnkBrn.setOnClickListener(this);
		// /btn_fetchName.setOnClickListener(this);
		btn_submit.setOnClickListener(this);
		// btn_fetchBnkBrn.setTypeface(tf_calibri);
		// btn_submit.setTypeface(tf_calibri);
		txtIFSC_Code.addTextChangedListener(new TextWatcher() {

			public void afterTextChanged(Editable s) {
				
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

		});
		spi_bank.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				String str = spi_bank.getSelectedItem().toString();
				if (str.equalsIgnoreCase("--Select Bank--")) {
					spi_state.setAdapter(null);
				} else {
					flag = chkConnectivity();
					if (flag == 0) {
						CallWebServiceGetStates C = new CallWebServiceGetStates();
						C.execute();
					}
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
		spi_state.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				String str = spi_state.getSelectedItem().toString();
				if (str.equalsIgnoreCase("--Select State--")) {
					spi_district.setAdapter(null);
				} else {
					flag = chkConnectivity();
					if (flag == 0) {
						CallWebServiceGetDistricts C = new CallWebServiceGetDistricts();
						C.execute();
					}
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
		spi_district.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				String str = spi_district.getSelectedItem().toString();
				if (str.equalsIgnoreCase("--Select District--")) {
					spi_city.setAdapter(null);
				} else {
					flag = chkConnectivity();
					if (flag == 0) {
						CallWebServiceGetCities C = new CallWebServiceGetCities();
						C.execute();
					}
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
		spi_city.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				String str = spi_city.getSelectedItem().toString();
				if (str.equalsIgnoreCase("--Select City--")) {
					spi_branch.setAdapter(null);
				} else {
					flag = chkConnectivity();
					if (flag == 0) {
						CallWebServiceGetBranches C = new CallWebServiceGetBranches();
						C.execute();
					}
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
		spi_branch.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				String str = spi_city.getSelectedItem().toString();
				if (str.equalsIgnoreCase("--Select Branch--")) {
					// showAlert();
				} else {
					flag = chkConnectivity();
					if (flag == 0) {
						CallWebServiceGetIFSC C = new CallWebServiceGetIFSC();
						C.execute();
					}
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		/*
		 * Button btn_back = (Button) findViewById(R.id.btn_back); // Listening
		 * to back button click btn_back.setOnClickListener(new
		 * View.OnClickListener() {
		 * 
		 * @Override public void onClick(View view) { // Launching News Feed
		 * Screen Intent i = new Intent(getApplicationContext(),
		 * OtherBankBeneficiary.class); startActivity(i); finish(); } });
		 */

		return rootView;
	}

	public void initAll() {
		txtIFSC_Code.setText("");
		txtMMID.setText("");
		txtMobile_No.setText("");
		txtAccNo.setText("");
		txtName.setText("");
		txtBank.setText("");
		// txtBranch.setText("");
		txtEmail.setText("");
		txtNick_Name.setText("");
	}

	// Get values from getBnkBrn method.....
	/*
	 * class CallWebService_get_bnkbrn extends AsyncTask<Void, Void, Void> {
	 * String retval = ""; LoadProgressBar loadProBarObj = new
	 * LoadProgressBar(act);
	 * 
	 * String[] xmlTags = { "IFSC", "CUSTID", "IMEINO" }; String[]
	 * valuesToEncrypt = new String[3]; String generatedXML = "";
	 * 
	 * protected void onPreExecute() { //
	 * p_wait.setVisibility(ProgressBar.VISIBLE); loadProBarObj.show();
	 * 
	 * System.out.println("ifsCD:" + ifsCD); valuesToEncrypt[0] = ifsCD;
	 * valuesToEncrypt[1] = custId; valuesToEncrypt[2] =
	 * MBSUtils.getImeiNumber(act); generatedXML =
	 * CryptoUtil.generateXML(xmlTags, valuesToEncrypt);
	 * System.out.println("&&&&&&&&&& generatedXML " + generatedXML); }
	 * 
	 * protected Void doInBackground(Void... arg0) { NAMESPACE =
	 * getString(R.string.namespace); URL = getString(R.string.url); SOAP_ACTION
	 * = getString(R.string.soap_action);
	 * 
	 * try { System.out
	 * .println("CallWebService_get_bnkbrn doInBackground  ------------ ");
	 * System.out
	 * .println("CallWebService_get_bnkbrn    selected all_str----------- ");
	 * 
	 * // checking benf account number...
	 * System.out.println("================== 1 ============"); SoapObject
	 * request = new SoapObject(NAMESPACE, METHOD_GET_BANK_BRANCH);
	 * System.out.println("inside doInBackground ifscCd id:=======>" + ifsCD);
	 * 
	 * System.out .println("==========IN doInBackground =========== 11111");
	 * request.addProperty("para_value", generatedXML); System.out
	 * .println("==========IN doInBackground =========== 2222");
	 * 
	 * // request.addProperty("ifscCd",ifsCD);
	 * System.out.println("================== 2 ============");
	 * SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
	 * SoapEnvelope.VER11);
	 * System.out.println("================== 3 ============");
	 * envelope.setOutputSoapObject(request);
	 * System.out.println("================== 4 ============"); HttpTransportSE
	 * androidHttpTransport = new HttpTransportSE(URL, 15000);
	 * System.out.println("================== 5 ============");
	 * 
	 * if (androidHttpTransport != null) System.out
	 * .println("=============== androidHttpTransport is not null "); else
	 * System.out .println("=============== androidHttpTransport is  null ");
	 * androidHttpTransport.call(SOAP_ACTION, envelope); retVal =
	 * envelope.bodyIn.toString().trim(); retVal =
	 * retVal.substring(retVal.indexOf("=") + 1, retVal.length() - 3); }// end
	 * try catch (Exception e) { e.printStackTrace();
	 * System.out.println("Exception 2");
	 * System.out.println("SameBankTransfer   Exception" + e); } return null;
	 * }// end doInBackground
	 * 
	 * protected void onPostExecute(Void paramVoid) { String[] xml_data =
	 * CryptoUtil.readXML(retVal, new String[] { "BNKBRN" }); String
	 * decryptedBeneficiaries = xml_data[0]; Log.e("EDIT BENF",
	 * decryptedBeneficiaries); if (decryptedBeneficiaries.indexOf("SUCCESS") >
	 * -1) {// 1 String allStr[] = decryptedBeneficiaries.split("~"); String
	 * bankBranch[] = allStr[1].split("#"); String bnkCD = bankBranch[0]; String
	 * brnCD = bankBranch[1]; bnCD = bnkCD; brCD = brnCD; txtBank.setText(bnCD);
	 * txtBranch.setText(brCD); loadProBarObj.dismiss();
	 * 
	 * if (when_fetch == "AUTO") { SaveBeneficiary(); } // txtBank.setText("2");
	 * // txtBranch.setText("1"); loadProBarObj.dismiss(); }// 1 else if
	 * (retVal.indexOf("no data found") > -1) {// 2 System.out .println(
	 * "================== in onPostExecute  else if ============================"
	 * ); loadProBarObj.dismiss(); // ///retMess = "Invalid IFSC Code."; retMess
	 * = getString(R.string.alert_018); showAlert(retMess); //
	 * loadProBarObj.dismiss(); }// 2 else {// 3 System.out .println(
	 * "================== in onPostExecute  else ============================"
	 * ); loadProBarObj.dismiss(); // ////retMess =
	 * "Network Unavailable. Please Try Again."; retMess =
	 * getString(R.string.alert_000); showAlert(retMess); }// 3 }// end
	 * onPostExecute
	 * 
	 * }// end callWbService2
	 */
	class CallWebServiceGetBanks extends AsyncTask<Void, Void, Void> {
		String retval = "";
		LoadProgressBar loadProBarObj = new LoadProgressBar(act);
		JSONObject jsonObj = new JSONObject();
		String ValidationData="";

		protected void onPreExecute() {
			try {
				respcode="";
				retvalweb="";
				respdescget_bnkbrn="";
				// p_wait.setVisibility(ProgressBar.VISIBLE);
				loadProBarObj.show();

				jsonObj.put("CUSTID", custId);
				jsonObj.put("IMEINO", MBSUtils.getImeiNumber(act));
				jsonObj.put("SIMNO", MBSUtils.getSimNumber(act));
				jsonObj.put("METHODCODE","34");
				//ValidationData=MBSUtils.getValidationData(act,jsonObj.toString());
				Log.e("Shubham", "AddOtherBankBeneficiary_Request-->"+jsonObj.toString() );
			
			} catch (JSONException je) {
				je.printStackTrace();
			}
	
		}

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
				System.out.println("Exception 2");
				System.out.println("SameBankTransfer   Exception" + e);
			}
			return null;
		}// end doInBackground

		protected void onPostExecute(Void paramVoid) {
				
			JSONObject jsonObj;
			try
			{
				String str=CryptoClass.Function6(var5,var2);
				 jsonObj = new JSONObject(str.trim());
				Log.e("Shubham", "AddOtherBankBeneficiary_Responce-->"+jsonObj.toString() );
				/* ValidationData=xml_data[1].trim();
				if(ValidationData.equals(MBSUtils.getValidationData(act, xml_data[0].trim())))
				{*/
				
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
					respdescget_bnkbrn = jsonObj.getString("RESPDESC");
				}
				else
				{	
					respdescget_bnkbrn = "";
				}
				
			if(respdescget_bnkbrn.length()>0)
			{
				showAlert(respdescget_bnkbrn);
			}
			else{
			loadProBarObj.dismiss();
			if (retvalweb.indexOf("FAILED") > -1) {
				showAlert(getString(R.string.alert_126));
			} else {
				post_successget_bnkbrn(retvalweb);
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

	}// end CallWebServiceGetBank
	
	public 	void post_successget_bnkbrn(String retvalweb)
	{
		respcode="";
		respdescget_bnkbrn="";
		int count = 0;
		ArrayList<String> arrList = new ArrayList<String>();
		arrList.add("--Select Bank--");
		try {
			JSONArray ja = new JSONArray(retvalweb);
			jsonArr = ja;
			for (int j = 0; j < ja.length(); j++) {
				JSONObject jObj = ja.getJSONObject(j);
				arrList.add(jObj.getString("BANKNAME"));
				count++;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if (count > 0) {
			String[] bankNamesArr = new String[arrList.size()];
			bankNamesArr = arrList.toArray(bankNamesArr);
			ArrayAdapter<String> bankNames = new ArrayAdapter<String>(act,R.layout.spinner_item, bankNamesArr);
			/*CustomeSpinnerAdapter bankNames = new CustomeSpinnerAdapter(
					act, R.layout.spinner_item,
					bankNamesArr);*/
			bankNames.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spi_bank.setAdapter(bankNames);
		}
	
	}

	class CallWebServiceGetStates extends AsyncTask<Void, Void, Void> {
		String retval = "";
		LoadProgressBar loadProBarObj = new LoadProgressBar(act);
		JSONObject jsonObj = new JSONObject();
		String ValidationData="";

		protected void onPreExecute() {
			try {
				respcode="";
				retvalweb="";
				respdescGetStates="";
				// p_wait.setVisibility(ProgressBar.VISIBLE);
				loadProBarObj.show();

				jsonObj.put("CUSTID", custId);
				jsonObj.put("BANKNAME", spi_bank.getSelectedItem().toString());
				jsonObj.put("IMEINO", MBSUtils.getImeiNumber(act));
				jsonObj.put("SIMNO", MBSUtils.getSimNumber(act));
				jsonObj.put("METHODCODE","35");
				Log.e("Shubham", "CallWebServiceGetStates: "+jsonObj.toString() );
				//ValidationData=MBSUtils.getValidationData(act,jsonObj.toString());
				
				
			} catch (JSONException je) {
				je.printStackTrace();
			}
		
		}

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
				System.out.println("Exception 2");
				System.out.println("SameBankTransfer   Exception" + e);
			}
			return null;
		}// end doInBackground

		protected void onPostExecute(Void paramVoid) {

			// Log.e("EDIT BENF", decryptedBeneficiaries);
			loadProBarObj.dismiss();
			JSONObject jsonObj;
			try
			{
				String str=CryptoClass.Function6(var5,var2);
				jsonObj = new JSONObject(str.trim());
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
					respdescGetStates= jsonObj.getString("RESPDESC");
				}
				else
				{
					respdescGetStates= "";
				}


				if(respdescGetStates.length()>0)
				{
					showAlert(respdescGetStates);
				}
				else{
					if (retvalweb.indexOf("FAILED") > -1) {
						showAlert(getString(R.string.alert_127));
					} else {
						post_successGetStates(retvalweb);
					}}

			} catch (JSONException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}// end onPostExecute

	}// end CallWebServiceGetStates

	public 	void post_successGetStates(String retvalweb)
	{
		respcode="";
		respdescGetStates="";
		int count = 0;
		ArrayList<String> arrList = new ArrayList<String>();
		arrList.add("--Select State--");
		try {
			JSONArray ja = new JSONArray(retvalweb);
			jsonArr = ja;
			for (int j = 0; j < ja.length(); j++) {
				JSONObject jObj = ja.getJSONObject(j);
				arrList.add(jObj.getString("STATE"));
				count++;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if (count > 0) {
			String[] statesArr = new String[arrList.size()];
			statesArr = arrList.toArray(statesArr);
			ArrayAdapter<String> states = new ArrayAdapter<String>(act,R.layout.spinner_item, statesArr);
			/*CustomeSpinnerAdapter states = new CustomeSpinnerAdapter(
					act, R.layout.spinner_item,
					statesArr);*/
			states.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spi_state.setAdapter(states);
		}
	
	}
	class CallWebServiceGetDistricts extends AsyncTask<Void, Void, Void> {
		String retval = "";
		LoadProgressBar loadProBarObj = new LoadProgressBar(act);
		JSONObject jsonObj = new JSONObject();
		String ValidationData="";

		protected void onPreExecute() {
			try {
				respcode="";
				retvalweb="";
				respdescGetDistricts="";
				// p_wait.setVisibility(ProgressBar.VISIBLE);
				loadProBarObj.show();

				System.out.println("ifsCD:" + ifsCD);
				jsonObj.put("CUSTID", custId);
				jsonObj.put("BANKNAME", spi_bank.getSelectedItem().toString());
				jsonObj.put("STATE", spi_state.getSelectedItem().toString());
				jsonObj.put("IMEINO", MBSUtils.getImeiNumber(act));
				jsonObj.put("SIMNO", MBSUtils.getSimNumber(act));
				jsonObj.put("METHODCODE","36");
				//ValidationData=MBSUtils.getValidationData(act,jsonObj.toString());
			
			} catch (JSONException je) {
				je.printStackTrace();
			}
		
		}

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
				System.out.println("Exception 2");
				System.out.println("SameBankTransfer   Exception" + e);
			}
			return null;
		}// end doInBackground

		protected void onPostExecute(Void paramVoid) {
			loadProBarObj.dismiss();
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
					retvalweb = jsonObj.getString("RETVAL");
				}
				else
				{
					retvalweb = "";
				}
				if (jsonObj.has("RESPDESC"))
				{
					respdescGetDistricts= jsonObj.getString("RESPDESC");
				}
				else
				{	
					respdescGetDistricts= "";
				}
				
			if(respdescGetDistricts.length()>0)
			{
				showAlert(respdescGetDistricts);
			}
			else{
			// Log.e("EDIT BENF", decryptedBeneficiaries);
			if (retvalweb.indexOf("FAILED") > -1) {
				showAlert(getString(R.string.alert_128));
			} else {
				post_successGetDistricts(retvalweb);
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

	}// end CallWebServiceGetDistricts
	public 	void post_successGetDistricts(String retvalweb)
	{
		respcode="";
		
		respdescGetDistricts="";
		int count = 0;
		ArrayList<String> arrList = new ArrayList<String>();
		arrList.add("--Select District--");
		try {
			JSONArray ja = new JSONArray(retvalweb);
			jsonArr = ja;
			for (int j = 0; j < ja.length(); j++) {
				JSONObject jObj = ja.getJSONObject(j);
				arrList.add(jObj.getString("DISTRICT"));
				count++;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if (count > 0) {
			String[] districtArr = new String[arrList.size()];
			districtArr = arrList.toArray(districtArr);
			ArrayAdapter<String> districts = new ArrayAdapter<String>(act,R.layout.spinner_item, districtArr);
			/*CustomeSpinnerAdapter districts = new CustomeSpinnerAdapter(
					act, R.layout.spinner_item,
					districtArr);*/
			districts
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spi_district.setAdapter(districts);
		}
	
	}

	class CallWebServiceGetCities extends AsyncTask<Void, Void, Void> {
		String retval = "";
		LoadProgressBar loadProBarObj = new LoadProgressBar(act);
		JSONObject jsonObj = new JSONObject();
		String ValidationData="";

		protected void onPreExecute() {
			try {
				respcode="";
				retvalweb="";
				respdescGetCities="";
				// p_wait.setVisibility(ProgressBar.VISIBLE);
				loadProBarObj.show();

				System.out.println("ifsCD:" + ifsCD);
				jsonObj.put("CUSTID", custId);
				jsonObj.put("BANKNAME", spi_bank.getSelectedItem().toString());
				jsonObj.put("STATE", spi_state.getSelectedItem().toString());
				jsonObj.put("DISTRICT", spi_district.getSelectedItem()
						.toString());
				jsonObj.put("IMEINO", MBSUtils.getImeiNumber(act));
				jsonObj.put("SIMNO", MBSUtils.getSimNumber(act));
				jsonObj.put("METHODCODE","37");
				//ValidationData=MBSUtils.getValidationData(act,jsonObj.toString());
			
			} catch (JSONException je) {
				je.printStackTrace();
			}
	
		}

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
				System.out.println("Exception 2");
				System.out.println("SameBankTransfer   Exception" + e);
			}
			return null;
		}// end doInBackground

		protected void onPostExecute(Void paramVoid) {
			
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
					respdescGetCities= jsonObj.getString("RESPDESC");
				}
				else
				{	
					respdescGetCities= "";
				}
				
			if(respdescGetCities.length()>0)
			{
				showAlert(respdescGetCities);
			}
			else{
			if (retvalweb.indexOf("FAILED") > -1) {
				showAlert(getString(R.string.alert_129));
			} else {
				post_successGetCities(retvalweb);
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

	}// end CallWebServiceGetCities
	public 	void post_successGetCities(String retvalweb)
	{
		respcode="";
		respdescGetCities="";
		int count = 0;
		ArrayList<String> arrList = new ArrayList<String>();
		arrList.add("--Select City--");
		try {
			JSONArray ja = new JSONArray(retvalweb);
			jsonArr = ja;
			for (int j = 0; j < ja.length(); j++) {
				JSONObject jObj = ja.getJSONObject(j);
				arrList.add(jObj.getString("CITY"));
				count++;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if (count > 0) {
			String[] cityArr = new String[arrList.size()];
			cityArr = arrList.toArray(cityArr);
			ArrayAdapter<String> cities = new ArrayAdapter<String>(act,R.layout.spinner_item, cityArr);
			/*CustomeSpinnerAdapter cities = new CustomeSpinnerAdapter(
					act, R.layout.spinner_item, cityArr);*/
			cities.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spi_city.setAdapter(cities);
		}
	
	}

	class CallWebServiceGetBranches extends AsyncTask<Void, Void, Void> {
		String retval = "";
		LoadProgressBar loadProBarObj = new LoadProgressBar(act);
		JSONObject jsonObj = new JSONObject();
		String ValidationData="";

		protected void onPreExecute() {
			try {
				respcode="";
				retvalweb="";
				respdescGetBranches="";
				// p_wait.setVisibility(ProgressBar.VISIBLE);
				loadProBarObj.show();

				System.out.println("ifsCD:" + ifsCD);
				jsonObj.put("CUSTID", custId);
				jsonObj.put("BANKNAME", spi_bank.getSelectedItem().toString());
				jsonObj.put("STATE", spi_state.getSelectedItem().toString());
				jsonObj.put("DISTRICT", spi_district.getSelectedItem()
						.toString());
				jsonObj.put("CITY", spi_city.getSelectedItem().toString());
				jsonObj.put("IMEINO", MBSUtils.getImeiNumber(act));
				jsonObj.put("SIMNO", MBSUtils.getSimNumber(act));
				jsonObj.put("METHODCODE","38");
			//	ValidationData=MBSUtils.getValidationData(act,jsonObj.toString());
				
			} catch (JSONException je) {
				je.printStackTrace();
			}
		
		}

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
				System.out.println("Exception 2");
				System.out.println("SameBankTransfer   Exception" + e);
			}
			return null;
		}// end doInBackground

		protected void onPostExecute(Void paramVoid) {
				
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
					respdescGetBranches= jsonObj.getString("RESPDESC");
				}
				else
				{	
					respdescGetBranches= "";
				}
				
			if(respdescGetBranches.length()>0)
			{
				showAlert(respdescGetBranches);
			}
			else{
			loadProBarObj.dismiss();
			if (retvalweb.indexOf("FAILED") > -1) {
				showAlert(getString(R.string.alert_130));
			} else {
				post_successGetBranches(retvalweb);
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

	}// end CallWebServiceGetBranches
	public 	void post_successGetBranches(String retvalweb)
	{
		respcode="";
	
		respdescGetBranches="";
		int count = 0;
		ArrayList<String> arrList = new ArrayList<String>();
		arrList.add("--Select Branch--");
		try {
			JSONArray ja = new JSONArray(retvalweb);
			jsonArr = ja;
			for (int j = 0; j < ja.length(); j++) {
				JSONObject jObj = ja.getJSONObject(j);
				arrList.add(jObj.getString("BRANCH"));
				count++;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if (count > 0) {
			String[] branchArr = new String[arrList.size()];
			branchArr = arrList.toArray(branchArr);
			ArrayAdapter<String> branches = new ArrayAdapter<String>(act,R.layout.spinner_item, branchArr);
			/*CustomeSpinnerAdapter branches = new CustomeSpinnerAdapter(
					act, R.layout.spinner_item,
					branchArr);*/
			branches.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spi_branch.setAdapter(branches);
		}
	
	}

	class CallWebServiceGetIFSC extends AsyncTask<Void, Void, Void> {
		String retval = "", bankName = "";
		LoadProgressBar loadProBarObj = new LoadProgressBar(act);
		JSONObject jsonObj = new JSONObject();
			String ValidationData="";

		protected void onPreExecute() {

			try {
				// p_wait.setVisibility(ProgressBar.VISIBLE);
				loadProBarObj.show();
				respcode="";
				retvalweb="";
				respdescGetIFSC="";
				System.out.println("ifsCD:" + ifsCD);
				bankName = spi_bank.getSelectedItem().toString();
			
				jsonObj.put("CUSTID", custId);
				jsonObj.put("BANKNAME", spi_bank.getSelectedItem().toString());
				jsonObj.put("STATE", spi_state.getSelectedItem().toString());
				jsonObj.put("DISTRICT", spi_district.getSelectedItem()
						.toString());
				jsonObj.put("CITY", spi_city.getSelectedItem().toString());
				jsonObj.put("BRANCH", spi_branch.getSelectedItem().toString());
				jsonObj.put("IMEINO", MBSUtils.getImeiNumber(act));
				jsonObj.put("SIMNO", MBSUtils.getSimNumber(act));
				jsonObj.put("METHODCODE","39");
			//	ValidationData=MBSUtils.getValidationData(act,jsonObj.toString());
			} catch (JSONException je) {
				je.printStackTrace();
			}
		
		}

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
				System.out.println("Exception 2");
				System.out.println("SameBankTransfer   Exception" + e);
			}
			return null;
		}// end doInBackground

		protected void onPostExecute(Void paramVoid) { // "IFSC"
			
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
					respdescGetIFSC= jsonObj.getString("RESPDESC");
				}
				else
				{	
					respdescGetIFSC= "";
				}
				
			if(respdescGetIFSC.length()>0)
			{
				showAlert(respdescGetIFSC);
			}
			else{
			if (retvalweb.indexOf("FAILED") > -1) {
				showAlert(getString(R.string.alert_131));
			} else {
				post_successGetIFSC(retvalweb);
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

	}// end CallWebServiceGetIFSC
	public 	void post_successGetIFSC(String retvalweb)
	{
		
		try {
			respcode="";
			
			respdescGetIFSC="";
			JSONObject jObj = new JSONObject(retvalweb);
			strIfsc = jObj.getString("IFSC");
			add_benf_layout.setVisibility(add_benf_layout.VISIBLE);
			get_ifsc_layout.setVisibility(get_ifsc_layout.INVISIBLE);
			txtIFSC_Code.setText(strIfsc);
			txtBank.setText(spi_bank.getSelectedItem().toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
	
	}

	// Save benefiaciary
	class CallWebService_save_beneficiary extends AsyncTask<Void, Void, Void> {// CallWebService

		String retval = "";
		LoadProgressBar loadProBarObj = new LoadProgressBar(act);
		
		JSONObject jsonObj = new JSONObject();
		
		String ValidationData="";

		@Override
		protected void onPreExecute() {

			try {
				respcode="";
				retvalweb="";
				respdescsave_beneficiary="";
				// p_wait.setVisibility(ProgressBar.VISIBLE);
				loadProBarObj.show();


				jsonObj.put("CUSTID", custId);
				jsonObj.put("ACCNO", accNo);
				jsonObj.put("ACCNM", accNm);
				jsonObj.put("MOBNO", mobNo);
				jsonObj.put("NICKNM", nickNm);
				jsonObj.put("MAILID", mailId);
				jsonObj.put("TRANSFERTYPE", same_bank);
				jsonObj.put("IFSCCD", ifsCD);
				jsonObj.put("MMID", mmId);
				jsonObj.put("IINSERTUPDTDLT", insrtUpdtDlt);
				jsonObj.put("BENSRNO", "00");
				jsonObj.put("IMEINO", MBSUtils.getImeiNumber(act));
				jsonObj.put("MPIN", Mpin);
				jsonObj.put("SIMNO", MBSUtils.getSimNumber(act));
				//ValidationData=MBSUtils.getValidationData(act,jsonObj.toString());
				
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
				e.printStackTrace();
				Log.e("AddOtherBankBeneficiary", retVal);
				System.out.println("AddOtherBankBeneficiary   Exception" + e);
			}
			return null;
		}// end doInBackground

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
					respdescsave_beneficiary= jsonObj.getString("RESPDESC");
				}
				else
				{	
					respdescsave_beneficiary= "";
				}
				
			if(respdescsave_beneficiary.length()>0)
			{
				showAlert(respdescsave_beneficiary);
			}
			else{
			// retMess = "SUCCESS";
			if (retvalweb.indexOf("FAILED") > -1) {
				if (retvalweb.indexOf("DUPLICATEACCOUNT") > -1) {
					retMess = getString(R.string.alert_019);
					showAlert(retMess);
				} else if (retvalweb.indexOf("DUPLICATENICKNAME") > -1) {
					retMess = getString(R.string.alert_020);
					showAlert(retMess);
				} else if (retvalweb.indexOf("INCORRECTIFSC") > -1) {
					retMess = getString(R.string.alert_185);//alert_018);
					showAlert(retMess);
				} else if (retvalweb.indexOf("WRONGMPIN") > -1) {
				//	loadProBarObj.dismiss();
					retMess = getString(R.string.alert_125);
					showAlert(retMess);
				} else {

					// /////retMess="Failed To Add Other Bank Beneficiary Due To Server Problem.";
					retMess = getString(R.string.alert_021);
					//loadProBarObj.dismiss();
					showAlert(retMess);
					//initAll();

				}
			} else {
				//loadProBarObj.dismiss();
				post_successsaveBeneficiaries(retvalweb);
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

	}// end CallWebService
	
	public 	void post_successsaveBeneficiaries(String retvalweb)
	{

		respcode="";
		respdescsave_beneficiary="";
		
		// ///////retMess="Other Bank Beneficiary Added Successfully.";
		flg="true";
		retMess = getString(R.string.alert_022);
		showAlert(retMess);
		initAll();
		onCreate(bdn);
	
	}

	public void onClick(View arg0) {
		System.out.println("=========== inside onClick ==========");
		System.out.println("VIew ID is:==========>" + arg0.getId());
		switch (arg0.getId()) {
	

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
	
		case R.id.btn_fetchIFSC:
			spi_bank.setAdapter(null);
			spi_state.setAdapter(null);
			spi_district.setAdapter(null);
			spi_city.setAdapter(null);
			spi_branch.setAdapter(null);
			add_benf_layout.setVisibility(add_benf_layout.INVISIBLE);
			get_ifsc_layout.setVisibility(get_ifsc_layout.VISIBLE);
			act.frgIndex = 651;
			flag = chkConnectivity();
			if (flag == 0) {
				new CallWebServiceGetBanks().execute();
			}
			break;
		case R.id.btn_submit2:
			String saveFlag = "";

			accNo = txtAccNo.getText().toString().trim();
			accNm = txtName.getText().toString().trim();
			mobNo = txtMobile_No.getText().toString().trim();
			nickNm = txtNick_Name.getText().toString().trim();
			mailId = txtEmail.getText().toString().trim();
			same_bank = "N";
			ifsCD = txtIFSC_Code.getText().toString().trim();
			mmId = txtMMID.getText().toString().trim();
			insrtUpdtDlt = "I";
			ifsCD = ifsCD.toUpperCase();
			int niknm_len = nickNm.length();

			int ifsc_len = ifsCD.length();
			int mmid_len = mmId.length();
				boolean isAccComplete = ((accNo.length() > 0 && ifsCD.length() > 0) || (accNo
					.length() == 0 && ifsCD.length() == 0)) ? true : false;
			boolean isMMIDComplete = (((mmId.length() > 0 && mobNo.length() > 0) || (mmId
					.length() == 0 && mobNo.length() == 0))) ? true : false;
			// saveFlag = "ERR";
			if (isAccOrMMID().equalsIgnoreCase("FAIL")) {
				saveFlag = "ERR";
				retMess = getString(R.string.alert_151);
				showAlert(retMess);
			} else // if(isAccComplete || isMMIDComplete)//Either One is true so
					// valid
			{
				
				if (ifsc_len != 0 && ifsc_len != 11) {
					saveFlag = "ERR";
					retMess = getString(R.string.alert_166);
					showAlert(retMess);
					txtIFSC_Code.requestFocus();
				} else if (accNm.length() == 0) {
					saveFlag = "ERR";
					retMess = getString(R.string.alert_132);
					showAlert(retMess);

					txtName.requestFocus();
				} else if (mmId.length() != 0 && mmid_len != 7) {
					
					saveFlag = "ERR";
					// ///////retMess = "Please Enter 7-digits MMID.";
					retMess = getString(R.string.alert_025);
					showAlert(retMess);
					txtMMID.requestFocus();
				} else if (mobNo.length() != 0 //&& //mobNo.length() != 10
						&& (!MBSUtils.validateMobNo(mobNo))) {
					
					saveFlag = "ERR";
					retMess = getString(R.string.alert_006);
					showAlert(retMess);
					txtMobile_No.requestFocus();
					// }
				}

				else if (nickNm.trim().length() == 0) {
					saveFlag = "ERR";
					retMess = getString(R.string.alert_003);
					showAlert(retMess);
					
					txtNick_Name.requestFocus();
				} else if (niknm_len < 4 || niknm_len > 15) {
					retMess = getString(R.string.alert_005);
					saveFlag = "ERR";
					showAlert(retMess);
				
					txtNick_Name.requestFocus();

				} else if (mailId.length() != 0
						&& !MBSUtils.validateEmail(mailId)) {
					
					saveFlag = "ERR";
					retMess = getString(R.string.alert_007);
					showAlert(retMess);
					txtEmail.requestFocus();
				} else {
					// SaveBeneficiary();
					saveFlag = "OK";
				}
			}
			/*
			 * else { saveFlag = "ERR"; retMess = getString(R.string.alert_151);
			 * showAlert(retMess); }
			 */

			/*
			 * else if(mmId.length() != 0) { int mmid_len = mmId.length();
			 * 
			 * Log.i("MMID entered","................. 4"); saveFlag = "OK";
			 * if(mmid_len != 7) {
			 * Log.i("mmId length not EQUAL TO 7","................. mmid_len :"
			 * +mmid_len); saveFlag = "ERR"; /////////retMess =
			 * "Please Enter 7-digits MMID."; retMess =
			 * getString(R.string.alert_025); showAlert(retMess);
			 * txtMMID.requestFocus(); } }
			 */
			/*
			 * if(accNo.length() == 0 && ifsCD.length() == 0 && mmId.length() ==
			 * 0 && mobNo.length()==0) { saveFlag = "ERR"; retMess =
			 * getString(R.string.alert_151); showAlert(retMess); } else {
			 * if(accNo.length() == 0 || ifsCD.length() == 0 ) {
			 * Log.e("Debug:","<<< 1 >>>"); if(mmId.length() == 0 &&
			 * mobNo.length()==0) { Log.e("Debug:","<<< 2 >>>"); saveFlag =
			 * "ERR"; retMess = getString(R.string.alert_153);
			 * showAlert(retMess); } else { Log.e("Debug:","<<< 3 >>>");
			 * saveFlag = "OK"; } Log.e("Debug:","<<< 1 End >>>"); }
			 * if(mmId.length() == 0 || mobNo.length() == 0 ) {
			 * Log.e("Debug:","<<< 4 >>>"); if(accNo.length() == 0 &&
			 * ifsCD.length()==0) { Log.e("Debug:","<<< 5 >>>"); saveFlag =
			 * "ERR"; retMess = getString(R.string.alert_153);
			 * showAlert(retMess); } else { saveFlag = "OK";
			 * Log.e("Debug:","<<< 6 >>>"); } Log.e("Debug:","<<< 4 End >>>"); }
			 * } Log.e("Debug: saveFlag",saveFlag );
			 */

			/* New Log Ends Here */

			/* Old Logic */
			/*
			 * if(accNo.length() != 0) { if(ifsCD.length() == 0) { saveFlag =
			 * "ERR"; retMess = getString(R.string.alert_145);
			 * showAlert(retMess); txtIFSC_Code.requestFocus(); } else if
			 * (accNm.length() == 0) { saveFlag = "ERR"; retMess =
			 * getString(R.string.alert_132); showAlert(retMess);
			 * txtMobile_No.requestFocus(); } else if (nickNm.trim().length() ==
			 * 0) { saveFlag = "ERR"; retMess = getString(R.string.alert_003);
			 * showAlert(retMess); txtNick_Name.requestFocus(); } else if
			 * (niknm_len < 4 || niknm_len > 15) { retMess =
			 * getString(R.string.alert_005); saveFlag = "ERR";
			 * showAlert(retMess); txtNick_Name.requestFocus();
			 * 
			 * } else if (mmId.length() != 0 && mmId.length() != 7) { saveFlag =
			 * "ERR"; retMess = getString(R.string.alert_025);
			 * showAlert(retMess); txtMMID.requestFocus(); } else if
			 * (mobNo.length()!=0 && !MBSUtils.validateMobNo(mobNo)) { saveFlag
			 * = "ERR"; retMess = getString(R.string.alert_006);
			 * showAlert(retMess); txtMobile_No.requestFocus();
			 * 
			 * } else if (mailId.length() != 0 &&
			 * !MBSUtils.validateEmail(mailId)) { saveFlag = "ERR"; retMess =
			 * getString(R.string.alert_007); showAlert(retMess);
			 * txtEmail.requestFocus(); } else saveFlag = "OK"; } else if
			 * (mmId.length() != 0) { if(mmId.length() != 7) { saveFlag = "ERR";
			 * retMess = getString(R.string.alert_025); showAlert(retMess);
			 * txtMMID.requestFocus(); } else if (accNm.length() == 0) {
			 * saveFlag = "ERR"; retMess = getString(R.string.alert_132);
			 * showAlert(retMess); txtMobile_No.requestFocus(); } else if
			 * (nickNm.trim().length() == 0) { saveFlag = "ERR"; retMess =
			 * getString(R.string.alert_003); showAlert(retMess);
			 * txtNick_Name.requestFocus(); } else if (niknm_len < 4 ||
			 * niknm_len > 15) { retMess = getString(R.string.alert_005);
			 * saveFlag = "ERR"; showAlert(retMess);
			 * txtNick_Name.requestFocus();
			 * 
			 * } else if (mobNo.length()==0) { saveFlag = "ERR"; retMess =
			 * getString(R.string.alert_006); showAlert(retMess);
			 * txtMobile_No.requestFocus();
			 * 
			 * } else if (mobNo.length()!=0 && !MBSUtils.validateMobNo(mobNo)) {
			 * saveFlag = "ERR"; retMess = getString(R.string.alert_006);
			 * showAlert(retMess); txtMobile_No.requestFocus();
			 * 
			 * } else if (mailId.length() != 0 &&
			 * !MBSUtils.validateEmail(mailId)) { saveFlag = "ERR"; retMess =
			 * getString(R.string.alert_007); showAlert(retMess);
			 * txtEmail.requestFocus(); } else saveFlag = "OK"; } else {
			 * saveFlag = "ERR"; retMess = getString(R.string.alert_146);
			 * showAlert(retMess); txtEmail.requestFocus(); } //Old Logic ends
			 * here
			 */
			/*
			 * if((accNo.length() != 0 && ifsCD.length() == 0) ||
			 * (accNo.length() == 0 && ifsCD.length() != 0) ) { saveFlag =
			 * "ERR"; retMess = getString(R.string.alert_145);
			 * showAlert(retMess); txtIFSC_Code.requestFocus(); } else if
			 * ((mobNo.length() != 0 && mmId.length() == 0) || (mobNo.length()
			 * == 0 && mmId.length() != 0)) { saveFlag = "ERR"; retMess =
			 * getString(R.string.alert_024); showAlert(retMess);
			 * txtIFSC_Code.requestFocus(); } else if (ifsCD.length() != 0) { if
			 * (accNo.length() == 0) { saveFlag = "ERR"; retMess =
			 * getString(R.string.alert_001); showAlert(retMess);
			 * txtAccNo.requestFocus(); } else if (accNm.length() == 0) {
			 * saveFlag = "ERR"; retMess = getString(R.string.alert_132);
			 * showAlert(retMess); txtMobile_No.requestFocus(); } else if
			 * (mobNo.length() == 0) { saveFlag = "ERR"; retMess =
			 * getString(R.string.alert_002); showAlert(retMess);
			 * txtMobile_No.requestFocus(); } else if (nickNm.length() == 0) {
			 * saveFlag = "ERR"; retMess = getString(R.string.alert_003);
			 * showAlert(retMess); txtNick_Name.requestFocus(); } else if
			 * (nickNm.contains(" ") == true) { retMess =
			 * getString(R.string.alert_004); saveFlag = "ERR";
			 * showAlert(retMess); txtNick_Name.requestFocus();
			 * 
			 * } else if (niknm_len < 4 || niknm_len > 15) { retMess =
			 * getString(R.string.alert_005); saveFlag = "ERR";
			 * showAlert(retMess); txtNick_Name.requestFocus();
			 * 
			 * } else if (bnCD.length() == 0 || brCD.length() == 0) { when_fetch
			 * = "AUTO"; CallWebService_get_bnkbrn C = new
			 * CallWebService_get_bnkbrn(); C.execute();
			 * 
			 * } else if (mobNo.length()!=0 && !MBSUtils.validateMobNo(mobNo)) {
			 * retMess = getString(R.string.alert_006); showAlert(retMess);
			 * txtMobile_No.requestFocus();
			 * 
			 * } else if (mailId.length() != 0 &&
			 * !MBSUtils.validateEmail(mailId)) { retMess =
			 * getString(R.string.alert_007); showAlert(retMess);
			 * txtEmail.requestFocus();
			 * 
			 * } else if (mmId.length() != 0) { int mmid_len = mmId.length();
			 * saveFlag = "OK"; if (mmid_len != 7) { saveFlag = "ERR"; retMess =
			 * getString(R.string.alert_025); showAlert(retMess);
			 * txtMMID.requestFocus(); } } else { saveFlag = "OK"; } } else if
			 * (mmId.length() != 0) { int mmid_len = mmId.length(); if (mmid_len
			 * != 7) { saveFlag = "ERR"; retMess =
			 * getString(R.string.alert_025); showAlert(retMess);
			 * txtMMID.requestFocus(); } else if (mobNo.length() == 0) {
			 * saveFlag = "ERR"; retMess = getString(R.string.alert_002);
			 * showAlert(retMess); txtMobile_No.requestFocus(); } else if
			 * (nickNm.length() == 0) { saveFlag = "ERR"; retMess =
			 * getString(R.string.alert_003); showAlert(retMess);
			 * txtNick_Name.requestFocus(); } else { saveFlag = "OK"; } }
			 */
			Log.e("On Submit button Click", "saveFlag=== " + saveFlag);
			if (saveFlag.equalsIgnoreCase("OK")) {
				SaveBeneficiary();
			}
			break;
		case R.id.spinner_btn1:
			spi_bank.performClick();
			break;
		case R.id.spinner_btn2:
			spi_state.performClick();
			break;
		case R.id.spinner_btn3:
			spi_district.performClick();
			break;
		case R.id.spinner_btn4:
			spi_city.performClick();
			break;
		case R.id.spinner_btn5:
			spi_branch.performClick();
			break;

		default:
			break;
		}// end switch
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

	public String isAccOrMMID() {
		String chkaccNo = txtAccNo.getText().toString().trim();
		String chkifsCD = txtIFSC_Code.getText().toString().trim();
		String chkmmId = txtMMID.getText().toString().trim();
		String chkmobNo = txtMobile_No.getText().toString().trim();

		checkCnt = 0;
		if (chkaccNo.length() != 0) {
			checkCnt++;
		}
		if (chkifsCD.length() != 0) {
			checkCnt++;
		}
		if (chkmmId.length() != 0) {
			checkCnt++;
		}
		if (chkmobNo.length() != 0) {
			checkCnt++;
		}

		if (checkCnt > 2) {
			checkedValue = "SUCCESS";
		} else if (checkCnt == 2) {
			if ((chkaccNo.length() != 0) && (chkifsCD.length() != 0)) {
				checkedValue = "SUCCESS";
			} else if ((chkmmId.length() != 0) && (chkmobNo.length() != 0)) {
				checkedValue = "SUCCESS";
			} else {
				checkedValue = "FAIL";
			}
		} else {
			checkedValue = "FAIL";
		}

		return checkedValue;
	}

	public void SaveBeneficiary() {
		/*
		 * SharedPreferences sp = act.getSharedPreferences(MY_SESSION,
		 * Context.MODE_PRIVATE); custId = sp.getString("custId", "custId");
		 * mobPin = sp.getString("pin", "pin"); userId = sp.getString("userId",
		 * "userId");
		 */

		Cursor c1 = dbms.selectFromTable("SHAREDPREFERENCE", "", null);// ("select * from ",
																		// null);
		if (c1 != null) {
			while (c1.moveToNext()) {
				custId = c1.getString(2);
				Log.e("CustId", "......" + custId);
				userId = c1.getString(3);
				Log.e("UserId", "......" + userId);
			}
		}
		InputDialogBox inputBox = new InputDialogBox(act);
		inputBox.show();
	}

	public void showAlert(final String str) {
		// Toast.makeText(this, str, Toast.LENGTH_LONG).show();
		ErrorDialogClass alert = new ErrorDialogClass(act, "" + str) {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				super.onClick(v);
				if((str.equalsIgnoreCase(respdescget_bnkbrn)) &&  (respcode.equalsIgnoreCase("0")))
				{
					post_successget_bnkbrn(retvalweb);
				}
				else if((str.equalsIgnoreCase(respdescget_bnkbrn)) && (respcode.equalsIgnoreCase("1")))
				{
					this.dismiss();
				}
				else if((str.equalsIgnoreCase(respdescsave_beneficiary)) &&  (respcode.equalsIgnoreCase("0")))
				{
					post_successsaveBeneficiaries(retvalweb);
				}
				else if((str.equalsIgnoreCase(respdescsave_beneficiary)) && (respcode.equalsIgnoreCase("1")))
				{
					this.dismiss();
				}
				else if((str.equalsIgnoreCase(respdescget_bnkbrn)) &&  (respcode.equalsIgnoreCase("0")))
				{
					post_successget_bnkbrn(retvalweb);
				}
				else if((str.equalsIgnoreCase(respdescget_bnkbrn)) && (respcode.equalsIgnoreCase("1")))
				{
					this.dismiss();
				}
				else if((str.equalsIgnoreCase(respdescGetStates)) &&  (respcode.equalsIgnoreCase("0")))
				{
					post_successGetStates(retvalweb);
				}
				else if((str.equalsIgnoreCase(respdescGetStates)) && (respcode.equalsIgnoreCase("1")))
				{
					this.dismiss();
				}
				else if((str.equalsIgnoreCase(respdescGetDistricts)) &&  (respcode.equalsIgnoreCase("0")))
				{
					post_successGetDistricts(retvalweb);
				}
				else if((str.equalsIgnoreCase(respdescGetDistricts)) && (respcode.equalsIgnoreCase("1")))
				{
					this.dismiss();
				}
				else if((str.equalsIgnoreCase(respdescGetCities)) &&  (respcode.equalsIgnoreCase("0")))
				{
					post_successGetCities(retvalweb);
				}
				else if((str.equalsIgnoreCase(respdescGetCities)) && (respcode.equalsIgnoreCase("1")))
				{
					this.dismiss();
				}
				else if((str.equalsIgnoreCase(respdescGetBranches)) &&  (respcode.equalsIgnoreCase("0")))
				{
					post_successGetBranches(retvalweb);
				}
				else if((str.equalsIgnoreCase(respdescGetBranches)) && (respcode.equalsIgnoreCase("1")))
				{
					this.dismiss();
				}
				else if((str.equalsIgnoreCase(respdescGetIFSC)) &&  (respcode.equalsIgnoreCase("0")))
				{
					post_successGetIFSC(retvalweb);
				}
				else if((str.equalsIgnoreCase(respdescGetIFSC)) && (respcode.equalsIgnoreCase("1")))
				{
					this.dismiss();
				}
				else if (act.getString(R.string.alert_125).equalsIgnoreCase(
						textMessage)) {
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
		System.out.println("========================= end chkConnectivity ==================");
		ConnectivityManager cm = (ConnectivityManager) act.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		try {
			State state = ni.getState();
			boolean state1 = ni.isAvailable();
			System.out.println("BalanceEnquiry	in chkConnectivity () state1 ---------"+ state1);
			if (state1) {
				switch (state) {
				case CONNECTED:
					if (ni.getType() == ConnectivityManager.TYPE_MOBILE|| ni.getType() == ConnectivityManager.TYPE_WIFI) {

					}
					break;
				case DISCONNECTED:
					flag = 1;
					// ////////retMess =
					// "Network Disconnected. Please Check Network Settings.";
					retMess = getString(R.string.alert_014);
					showAlert(retMess);
					/*
					 * dbs = new DialogBox(this);
					 * dbs.get_adb().setMessage(retMess);
					 * dbs.get_adb().setPositiveButton("Ok", new
					 * DialogInterface.OnClickListener() { public void
					 * onClick(DialogInterface arg0, int arg1) { arg0.cancel();
					 * } }); dbs.get_adb().show();
					 */
					break;
				default:
					flag = 1;
					// //////retMess = "Network Unavailable. Please Try Again.";
					retMess = getString(R.string.alert_000);
					showAlert(retMess);

					/*
					 * dbs = new DialogBox(this);
					 * dbs.get_adb().setMessage(retMess);
					 * dbs.get_adb().setPositiveButton("Ok", new
					 * DialogInterface.OnClickListener() { public void
					 * onClick(DialogInterface arg0, int arg1) { arg0.cancel();
					 * Intent in = null; in = new
					 * Intent(getApplicationContext(), LoginActivity.class);
					 * startActivity(in); finish(); } }); dbs.get_adb().show();
					 */
					break;
				}
			} else {
				flag = 1;
				// ////retMess = "Network Unavailable. Please Try Again.";
				retMess = getString(R.string.alert_000);
				showAlert(retMess);

				/*
				 * dbs = new DialogBox(this); dbs.get_adb().setMessage(retMess);
				 * dbs.get_adb().setPositiveButton("Ok", new
				 * DialogInterface.OnClickListener() { public void
				 * onClick(DialogInterface arg0, int arg1) { arg0.cancel();
				 * Intent in = null; in = new Intent(getApplicationContext(),
				 * LoginActivity.class); startActivity(in); finish(); } });
				 * dbs.get_adb().show();
				 */
			}
		} catch (NullPointerException ne) {

			Log.i("BalanceEnquiry", "NullPointerException Exception"
					+ ne);
			flag = 1;
			// ///////retMess = "Network Unavailable. Please Try Again.";
			retMess = getString(R.string.alert_000);
			showAlert(retMess);

			/*
			 * dbs = new DialogBox(this); dbs.get_adb().setMessage(retMess);
			 * dbs.get_adb().setPositiveButton("Ok", new
			 * DialogInterface.OnClickListener() { public void
			 * onClick(DialogInterface arg0, int arg1) { arg0.cancel(); Intent
			 * in = null; in = new Intent(getApplicationContext(),
			 * LoginActivity.class); startActivity(in); finish(); } });
			 * dbs.get_adb().show();
			 */

		} catch (Exception e) {
			Log.i("BalanceEnquiry   mayuri", "Exception" + e);
			flag = 1;
			// ///////retMess = "Network Unavailable. Please Try Again.";
			retMess = getString(R.string.alert_000);
			showAlert(retMess);

			/*
			 * dbs = new DialogBox(this); dbs.get_adb().setMessage(retMess);
			 * dbs.get_adb().setPositiveButton("Ok", new
			 * DialogInterface.OnClickListener() { public void
			 * onClick(DialogInterface arg0, int arg1) { arg0.cancel(); Intent
			 * in = null; in = new Intent(getApplicationContext(),
			 * LoginActivity.class); startActivity(in); finish(); } });
			 * dbs.get_adb().show();
			 */
		}
		System.out
				.println("========================= end chkConnectivity ==================");
		return flag;
	}// end chkConnectivity

	// inner class
	public class InputDialogBox extends Dialog implements OnClickListener {
		Activity activity;
		String msg, title;
		Context appAcontext;
		EditText mpin;
		Button btnOk;
		String strmpin = "";
		TextView txtLbl;
		boolean flg;

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
				encrptdMpin = ListEncryption.encryptData(custId + str);
				// String encrptdUpin = ListEncryption.encryptData(userId +
				// str);
				if (str.equalsIgnoreCase("")) {
					this.hide();
					retMess = getString(R.string.alert_015);
					showAlert(retMess);
					mpin.setText("");
				} else { // if (mobPin.equalsIgnoreCase(encrptdMpin)
							// || mobPin.equalsIgnoreCase(encrptdUpin)) {
					/*
					 * SharedPreferences sp =
					 * act.getSharedPreferences(MY_SESSION,
					 * Context.MODE_PRIVATE); custId = sp.getString("custId",
					 * "custId");
					 */

					flag = chkConnectivity();
					if (flag == 0) {
						callValidateTranpinService C = new callValidateTranpinService();
						//CallWebService_save_beneficiary C = new CallWebService_save_beneficiary();
						C.execute();
						this.hide();
					}
				} /*
				 * else { this.hide(); retMess = getString(R.string.alert_125);
				 * showAlert(retMess); mpin.setText("");
				 */
				// }
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("Exception in InputDialogBox of onClick:=====>"+ e);
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
				return null;
			}// end try
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			return null;
		}// end dodoInBackground2

		protected void onPostExecute(Void paramVoid) 
		{
			String str=CryptoClass.Function6(var5,var2);		 
			  JSONObject jsonObj;
	   			try
	   			{
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
				jsonObj.put("CUSTID", custId);
				jsonObj.put("ACCNO", accNo);
				jsonObj.put("ACCNM", accNm);
				jsonObj.put("MOBNO", mobNo);
				jsonObj.put("NICKNM", nickNm);
				jsonObj.put("MAILID", mailId);
				jsonObj.put("TRANSFERTYPE", same_bank);
				jsonObj.put("IFSCCD", ifsCD);
				jsonObj.put("MMID", mmId);
				jsonObj.put("IINSERTUPDTDLT", insrtUpdtDlt);
				jsonObj.put("BENSRNO", "00");
				jsonObj.put("IMEINO", MBSUtils.getImeiNumber(act));
				jsonObj.put("MPIN", Mpin);
				jsonObj.put("SIMNO", MBSUtils.getSimNumber(act));
              
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			Bundle bundle=new Bundle();
		Fragment fragment = new BeneficiaryOTP(act);
		bundle.putString("CUSTID", custId);
		bundle.putString("FROMACT", "ADDOTHBENF");
		bundle.putString("JSONOBJ", jsonObj.toString());
		fragment.setArguments(bundle);
		FragmentManager fragmentManager = act.getFragmentManager();
		fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
	} catch (Exception e) {
		e.printStackTrace();
	}
}

	public void onBackPressed() {
		Intent in = new Intent(act, ManageBeneficiaryMenuActivity.class);
		in.putExtra("VAR1", var1);
		in.putExtra("VAR3", var3);
		startActivity(in);
		act.finish();
	}

}// end AddOtherBankBeneficiary


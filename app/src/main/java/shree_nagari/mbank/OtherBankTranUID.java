package shree_nagari.mbank;

import java.util.ArrayList;

import mbLib.CryptoUtil;
import mbLib.ListEncryption;
import mbLib.MBSUtils;

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
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class OtherBankTranUID extends Fragment implements OnClickListener {// OtherBankTranIMPS

	// declaration
	MainActivity act;
	Button btn_submit,btn_confirm;// = (Button) rootView.findViewById(R.id.btn_confirm);

	Spinner spi_debit_account;
	
	ImageButton spinner_btn,  btn_home;//, btn_back;
	TextView cust_nm,txt_remark,txt_from,txt_to,txt_amount,txt_charges,txtTranId,txt_bnfname,txt_con_bnfname,lblben_name;
	EditText  txtMobID, txtMobNo;
	EditText txtUid, txtRemk, txtBank, txtBranch, txtIfsc,txtAmt,ohtertranBnfName;
	//String txtAmt;
	int cnt = 0;
	ProgressBar pb_wait;
	TextView txt_heading;
	Editor e;
	//DialogBox dbs;
	LinearLayout confirm_layout,other_bnk_layout;
	private static final String MY_SESSION = "my_session";
	String stringValue;
	String str = "", retMess = "", cust_name = "", custId = "",tranPin="";
	String str2 = "",chrgCrAccNo="",servChrg="",cess="",tranId="";
	StopPayment stp = null;
	String mobPin = "";
	String ifsCD = null;
	String benSrno = null;
	int frmno = 0, tono = 0, flag = 0;
	String postingStatus="",req_id= "";
	// private static final String NAMESPACE = "http://mbank.list";
	// private static final String URL =
	// "http://listspl.zapto.org:8082/axis2/services/MobBankServices";
	private static String NAMESPACE = "";
	private static String URL = "";
	private static  String METHOD_SAVE_TRANSFERTRAN = "";
	private static String SOAP_ACTION = "";
	private static  String METHOD_NAME = "";
	private static  String METHOD_GET_BANK_BRANCH = "fetchBnkBrnWS";
	private static  String METHOD_GET_TRANSFERCHARGE = "";

	String acnt_inf, all_acnts, bnCD, brCD;
	String benAccountNumber = "";
	private String benInfo = "";
	ImageView img_heading;
	String otherIfsctxtIFSCCode = "",drBrnCD = "",drSchmCD = "",drAcNo = "",strFromAccNo="",strToAccNo="",strAmount="",strRemark="",BnfName="";
	
	OtherBankTranUID otherUid = null;
	ArrayList<String> arrListTemp = new ArrayList<String>();
	
	public OtherBankTranUID() {
	}

	@SuppressLint("ValidFragment")
	public OtherBankTranUID(MainActivity a) {
		System.out.println("OtherBankTranIMPS()" + a);
		act = a;
		otherUid = this;
	}

	public void onBackPressed() {
		Fragment fragment = new FundTransferMenuActivity(act);
		FragmentManager fragmentManager = getFragmentManager();
		fragmentManager.beginTransaction()
				.replace(R.id.frame_container, fragment).commit();
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
			Bundle savedInstanceState) {
		System.out.println("onCreateView() OtherBankTranIMPS");
		View rootView = inflater.inflate(R.layout.other_bank_tranf_uid,
				container, false);
		//dbs = new DialogBox(act);
		SharedPreferences sp = act.getSharedPreferences(MY_SESSION,
				Context.MODE_PRIVATE);
		e = sp.edit();
		img_heading = (ImageView) rootView.findViewById(R.id.img_heading);
		img_heading.setBackgroundResource(R.mipmap.fund_trnsfer2);
		stringValue = sp.getString("retValStr", "retValStr");
		System.out.println("value of retValStr :" + stringValue);
		confirm_layout=(LinearLayout)rootView.findViewById(R.id.othr_confirm_layout);
		other_bnk_layout=(LinearLayout)rootView.findViewById(R.id.other_bnk_ifsc_layout);
		
		cust_name = sp.getString("cust_name", "cust_name");
		custId = sp.getString("custId", "custId");
		mobPin = sp.getString("pin", "pin");
		tranPin = sp.getString("tranPin", "tranPin");

		// String userDebitAccount=sp.getString("", arg1)
		System.out.println("========== 6 ============");
		System.out.println("OtherBankTranIMPS	value of cust_name :" + cust_name);
		System.out.println("========== 7 ============");

		spi_debit_account = (Spinner) rootView
				.findViewById(R.id.ohtertranImpsspi_debit_account);
		
		btn_submit = (Button) rootView
				.findViewById(R.id.ohtertranImpsbtn_submit);
		txtMobID = (EditText) rootView.findViewById(R.id.ohtertranImpstxtMMID);
		txtUid = (EditText) rootView.findViewById(R.id.ohtertranImpstxtAccNo);
		txtMobNo = (EditText) rootView.findViewById(R.id.ohtertranImpsMobNO);
		txtAmt = (EditText) rootView.findViewById(R.id.ohtertranImpstxtAmt);
	//	txtAmt=String.format("%.2f", txtAmt1);
		txtRemk = (EditText) rootView.findViewById(R.id.ohtertranImpstxtRemk);
		
		/*lblben_name=(TextView)rootView.findViewById(R.id.lblben_name);
		ohtertranBnfName = (EditText) rootView.findViewById(R.id.ohtertranBnfName);*/
		txt_heading=(TextView)rootView.findViewById(R.id.txt_heading);
		txt_heading.setText(getString(R.string.tabtitle_other_bank_fund_trans_uid));
		 txt_remark=(TextView)rootView.findViewById(R.id.txt_remark);
		 txt_from=(TextView)rootView.findViewById(R.id.txt_from);
			txt_to=(TextView)rootView.findViewById(R.id.txt_to);
			txt_amount=(TextView)rootView.findViewById(R.id.txt_amount);
			txt_charges=(TextView)rootView.findViewById(R.id.txt_charges);
			txtTranId=(TextView)rootView.findViewById(R.id.txt_tranid);
			btn_confirm = (Button) rootView.findViewById(R.id.btn_confirm);
		/*	txt_con_bnfname=(TextView)rootView.findViewById(R.id.txt_con_bnfname);*/
			btn_confirm.setOnClickListener(this);
		// pb_wait = (ProgressBar) findViewById(R.id.ohtertranImpspro_bar);
		all_acnts = stringValue;
		btn_submit.setOnClickListener(this);
		
		// btn_submit.setTypeface(tf_calibri);
		btn_home = (ImageButton) rootView.findViewById(R.id.btn_home);
		/*btn_back = (ImageButton) rootView.findViewById(R.id.btn_back);*/
		spinner_btn = (ImageButton) rootView.findViewById(R.id.spinner_btn);
		
		
		//btn_home.setImageResource(R.drawable.ic_home_d);
		//btn_back.setImageResource(R.drawable.backover);
		
		btn_home.setOnClickListener(this);
		//btn_back.setOnClickListener(this);
		spinner_btn.setOnClickListener(this);
	//	spinner_btn2.setOnClickListener(this);

		if (spi_debit_account == null)
			System.out.println("spi_debit_account is null");
		else
			System.out.println("spi_debit_account is not null");

		

		if (btn_submit == null)
			System.out.println("btn_submit is null");
		else
			System.out.println("btn_submit is not null");

		/*
		 * if(txtUid==null) System.out.println("txtUid is null"); else
		 * System.out.println("txtUid is not null");
		 */

		if (txtAmt == null)
			System.out.println("txtAmt is null");
		else
			System.out.println("txtAmt is not null");

		if (txtRemk == null)
			System.out.println("txtRemk is null");
		else
			System.out.println("txtRemk is not null");

		/*
		 * if(pb_wait==null) System.out.println("pb_wait is null"); else
		 * System.out.println("pb_wait is not null");
		 */

		// CalcString c = new CalcString(all_acnts);
		addAccounts(all_acnts);
		System.out.println("========== 1 ============");
		// flag = chkConnectivity();
		// if (flag == 0)
		{
			System.out.println("========== 1.0 ============");
			new CallWebService_fetch_all_beneficiaries().execute();
			System.out.println("========== 1.1 ============");
		}
		System.out.println("========== 2 ============");
		// pb_wait.setMax(10);// NOTE: here we got NPL exception b'coz we can
		// get obj of pb_wait
		System.out.println("========== 3 ============");
		// pb_wait.setProgress(1);
		System.out.println("========== 4 ============");
		// pb_wait.setVisibility(ProgressBar.INVISIBLE);
		System.out.println("========== 5 ============");

		

		spi_debit_account
				.setOnItemSelectedListener(new OnItemSelectedListener() {  

					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,  
							int arg2, long arg3) {

						// ///////String
						// str=spi_debit_account.getItemAtPosition(spi_debit_account.getSelectedItemPosition()).toString();
						String str = arrListTemp.get(spi_debit_account
								.getSelectedItemPosition());

						String debitAc[] = str.split("-");
						System.out.println("============account 1:"
								+ debitAc[0]);// 5
						System.out.println("============account 2:"
								+ debitAc[1]);// 101
						// System.out.println("account 3:"+debitAc[2]);//SB
						System.out.println("============account 4:"
								+ debitAc[3]);// 7

						drBrnCD = debitAc[0];
						drSchmCD = debitAc[1];
						drAcNo = debitAc[3];
					}// end onItemSelected

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {

					}

				});

		spi_debit_account.requestFocus();
		txtAmt.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(2)});
	//	strAmount=String.format("%.2f", txtAmt);
		return rootView;
	}

	public void showAlert(String str) {
		// Toast.makeText(this, str, Toast.LENGTH_LONG).show();
		ErrorDialogClass alert = new ErrorDialogClass(act, "" + str);
		alert.show();
	}

	
	public void addAccounts(String str) {
		System.out.println("OtherBankTranIFSC IN addAccounts()" + str);

		try 
		{
			ArrayList<String> arrList = new ArrayList<String>();
			String allstr[] = str.split("~");
			
			int noOfAccounts = allstr.length;
			Accounts acArray[] = new Accounts[noOfAccounts];
			
			for (int i = 0; i < noOfAccounts; i++) 
			{
				str2 = allstr[i];

				acArray[i] = new Accounts(str2);
				str2 = str2.replaceAll("#", "-");
				String accType = str2.split("-")[2];
				String oprcd=str2.split("-")[7];
				String str2Temp = str2;
				str2 = MBSUtils.get16digitsAccNo(str2);
				
				if (((accType.equals("SB")) || (accType.equals("CA"))
						|| (accType.equals("LO"))) && oprcd.equalsIgnoreCase("O"))
				{
					arrList.add(str2+" ("+MBSUtils.getAccTypeDesc(accType)+")");
					arrListTemp.add(str2Temp);
				}
			}

			String[] debAccArr = new String[arrList.size()];
			debAccArr = arrList.toArray(debAccArr);
			/*CustomeSpinnerAdapter debAccs = new CustomeSpinnerAdapter(act,
					R.layout.spinner_layout, debAccArr);*/
			CustomeSpinnerAdapter debAccs = new CustomeSpinnerAdapter(act,R.layout.spinner_item, debAccArr);
			debAccs.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spi_debit_account.setAdapter(debAccs);
			acnt_inf = spi_debit_account.getItemAtPosition(
					spi_debit_account.getSelectedItemPosition()).toString();
			Log.i("OtherBankTranIFSC", acnt_inf);
		} catch (Exception e) {
			System.out.println("" + e);
		}

	}// end addAccount

	public int chkConnectivity() {
		// pb_wait.setVisibility(ProgressBar.VISIBLE);
		ConnectivityManager cm = (ConnectivityManager) act
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		try {
			State state = ni.getState();
			boolean state1 = ni.isAvailable();
			System.out
					.println("OtherBankTranIMPS	in chkConnectivity () state1 ---------"
							+ state1);
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
					// retMess =
					// "Network Disconnected. Please Check Network Settings.";
					retMess = getString(R.string.alert_014);
					showAlert(retMess);

					break;
				default:
					flag = 1;
					retMess = getString(R.string.alert_000);
					;
					showAlert(retMess);
					break;
				}
			} else {
				flag = 1;
				// retMess = "Network Unavailable. Please Try Again.";
				retMess = getString(R.string.alert_000);
				;
				showAlert(retMess);
			}
		} catch (NullPointerException ne) {

			Log.i("OtherBankTranIMPS",
					"NullPointerException Exception" + ne);
			flag = 1;
			// retMess = "Network Unavailable. Please Try Again.";
			retMess = getString(R.string.alert_000);
			;
			showAlert(retMess);

		} catch (Exception e) {
			Log.i("OtherBankTranIMPS ", "Exception" + e);
			flag = 1;
			// retMess = "Network Unavailable. Please Try Again.";
			retMess = getString(R.string.alert_000);
			;
			showAlert(retMess);
		}
		return flag;
	}// end chkConnectivity

	// to get all Beneficiaries
	class CallWebService_fetch_all_beneficiaries extends
	AsyncTask<Void, Void, Void> {

String retval = "";
LoadProgressBar loadProBarObj = new LoadProgressBar(act);
String[] xmlTags = {"CUSTID","SAMEBNK","IMEINO"};
String[] valuesToEncrypt = new String[3];
String generatedXML = "";

protected void onPreExecute() {

	// pb_wait.setVisibility(ProgressBar.VISIBLE);
	loadProBarObj.show();

	valuesToEncrypt[0] = custId;
	valuesToEncrypt[1] = "N";
	valuesToEncrypt[2] = MBSUtils.getImeiNumber(act);
	generatedXML = CryptoUtil.generateXML(xmlTags, valuesToEncrypt);
	//System.out.println("&&&&&&&&&& generatedXML " + generatedXML);
}

@Override
protected Void doInBackground(Void... arg0) {
	NAMESPACE = getString(R.string.namespace);
	URL = getString(R.string.url);
	SOAP_ACTION = getString(R.string.soap_action);
	METHOD_NAME = "fetchBeneficiariesWS";
	try 
	{				
		SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
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
			System.out
					.println("=============== androidHttpTransport is  null ");
		androidHttpTransport.call("MobBankServices", envelope);
		
		retval = envelope.bodyIn.toString().trim();
		// benInfo=retval;
		int i = envelope.bodyIn.toString().trim().indexOf("=");
		
		this.retval = this.retval.substring(i + 1,this.retval.length() - 3);
		return null;

	} catch (Exception e) {
		// e.printStackTrace();
		System.out.println("Exception 2");
		System.out.println("EditOtherBankBeneficiary   Exception" + e);
	}
	return null;
}// end dodoInBackground

protected void onPostExecute(Void paramVoid) {

	/*
	 * retval = "SUCCESS~" +
	 * "2#omkarkushte#omkar#0020001010000052#SBI00010023#1234567#8657888773#omkarkushte@gmail.com~"
	 * +
	 * "3#mohit#mohit#0020001030000099#SBI00010222#1234567#7854894545#mohitsharma@gmail.com"
	 * ;
	 */
	// *******************************
	String[] xml_data = CryptoUtil.readXML(retval,	new String[] { "BENEFICIARIES" });
	System.out.println("xml_data.len :" + xml_data.length);
	
	Log.e("benificiary====","benificiary====="+xml_data.length);
	
	String decryptedBeneficiaries = xml_data[0];
	//decryptedBeneficiaries="SUCCESS";
	if (decryptedBeneficiaries.indexOf("SUCCESS") > -1) 
	{				
		loadProBarObj.dismiss();
		//decryptedBeneficiaries = decryptedBeneficiaries.split("SUCCESS~")[1];
		System.out.println("decryptedBeneficiaries:"
				+ decryptedBeneficiaries);

		
		 /*decryptedBeneficiaries = "" +
		 "2#omkarkushte#omkar#0020001010000052#SBI00010023#1234567#8657888773#omkarkushte@gmail.com~"
		 +
		 "3#mohit#mohit#0020001030000099#SBI00010222#1234567#7854894545#mohitsharma@gmail.com"
		  ;*/
		 
		//benInfo = decryptedBeneficiaries;
		//addBeneficiaries(decryptedBeneficiaries);
		loadProBarObj.dismiss();
	} 
	else 
	{
		loadProBarObj.dismiss();
		if(decryptedBeneficiaries.indexOf("NODATA")>-1)
		{
			Toast.makeText(act, getString(R.string.alert_041), Toast.LENGTH_LONG).show();
			Fragment fragment = new FundTransferMenuActivity(act);
			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.frame_container, fragment).commit();
		}
		else
		{
			retMess = getString(R.string.alert_069);
			showAlert(retMess);
		}
	}

}// end onPostExecute

}// end callWbService
	/*private void addBeneficiaries(String retval) {
		System.out
				.println("================ IN addBeneficiaries() ======================");
		System.out.println("SameBankTransfer IN addBeneficiaries()" + retval);

		try {
			// pb_wait.setVisibility(ProgressBar.VISIBLE);
			ArrayList<String> arrList = new ArrayList<String>();
			String allstr[] = retval.split("~");
			System.out.println("SameBankTransfer Mayuri.....................:"
					+ allstr.length);

			// System.out.println("SameBankTransfer Accounts:::" + allstr[1]);
			// int noOfAccounts = str1.length;
			int noOfben = allstr.length;
			String benName = "";
			System.out.println("=============== addbenificiary 1=====");
			arrList.add("Select Beneficiary");

			for (int i = 1; i <= noOfben; i++) {
				System.out.println("=============== addbenificiary 2=====");
				System.out.println(i + "----STR1-----------" + allstr[i - 1]);
				String[] str2 = allstr[i - 1].split("#");
				System.out.println("=============== addbenificiary 3=====");
				benName = str2[2] + "(" + str2[1] + ")";
				System.out.println("=============== addbenificiary 4=====");
				arrList.add(benName);
				System.out.println("=============== addbenificiary 5=====");
				System.out.println("=============== benificiary Name is:======"
						+ benName);
			}
			// spi_sel_beneficiery
			System.out.println("=============== addbenificiary 6=====");
			
			 * System.out.println(
			 * "================ IN addBeneficiaries() 1 ======================"
			 * ); ArrayAdapter<String> arrAdpt = new
			 * ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,
			 * arrList); System.out.println(
			 * "================ IN addBeneficiaries() 2 ======================"
			 * ); arrAdpt.setDropDownViewResource(android.R.layout.
			 * simple_spinner_dropdown_item); System.out.println(
			 * "================ IN addBeneficiaries() 3 ======================"
			 * ); spi_sel_beneficiery.setAdapter(arrAdpt);
			 

			String[] benfArr = new String[arrList.size()];
			benfArr = arrList.toArray(benfArr);
			CustomeSpinnerAdapter accs = new CustomeSpinnerAdapter(act,
					R.layout.spinner_layout, benfArr);
			spi_sel_beneficiery.setAdapter(accs);

			System.out.println("=============== addbenificiary 7=====");
			// pb_wait.setVisibility(ProgressBar.INVISIBLE);
			System.out
					.println("================ IN addBeneficiaries() 4 ======================");
			Log.i("SameBankTransfer ", "Exiting from adding accounts");
			System.out
					.println("================ IN addBeneficiaries() 5 ======================");
			acnt_inf = spi_debit_account.getItemAtPosition(
					spi_debit_account.getSelectedItemPosition()).toString();
			System.out
					.println("================ IN addBeneficiaries() 6 ======================");
			Log.i("SameBankTransfer MAYURI....", acnt_inf);
			System.out
					.println("================ exit from  addBeneficiaries() ======================");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Exception in addBeneficiaries:=====>" + e);
		}
	}// end addBeneficiaries
*/
	
	private void addBeneficiaries(String retval) {
		System.out
				.println("================ IN addBeneficiaries() of OtherBankTranIFSC ======================");
		System.out.println("OtherBankTranIFSC IN addBeneficiaries()" + retval);
		Log.e("OtherBankTranIMPS======","addBeneficiaries======"+retval);
		try {
			ArrayList<String> arrList = new ArrayList<String>();
			String allstr[] = retval.split("~");

			int noOfben = allstr.length;
			Log.e("OTHERBNK","noOfben=="+noOfben);
			Log.e("OTHERBNK","noOfben=="+noOfben);
			Log.e("OTHERBNK","noOfben=="+noOfben);
			
			String benName = "";
			arrList.add("Select Beneficiary");

			for (int i = 1; i < noOfben; i++) {
				System.out.println(i + "----STR1-----------" + allstr[i - 1]);
				String[] str2 = allstr[i].split("#");
				
			//	if(!(str2[5].equals("-9999"))) //|| str2[6].equals("NA"))
			if(!(str2[6].equals("NA")|| (str2[5].equals("-9999"))))
				{
				benName = str2[2] + "(" + str2[1] + ")";
				
				arrList.add(benName);
				System.out.println("=============== benificiary Name is:======"
						+ benName);
				}
				}
			
			String[] benfArr = new String[arrList.size()];
			benfArr = arrList.toArray(benfArr);
			/*CustomeSpinnerAdapter accs = new CustomeSpinnerAdapter(act,
					R.layout.spinner_layout, benfArr);*/
			CustomeSpinnerAdapter benfAccs = new CustomeSpinnerAdapter(act,R.layout.spinner_item, benfArr);
			benfAccs.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			//spi_sel_beneficiery.setAdapter(benfAccs);

		} catch (Exception e) {
			System.out.println("" + e);
		}
		
	}// end addBeneficiaries
	
	
	@Override
	public void onClick(View v) { 
		switch (v.getId()) {
		/*case R.id.btn_back:
			Fragment fragment = new FundTransferMenuActivity(act);
			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.frame_container, fragment).commit();
			break;*/

		case R.id.btn_home:
			Intent in = new Intent(act, NewDashboard.class);
			startActivity(in);
			act.finish();
			break;
		case R.id.spinner_btn:
			spi_debit_account.performClick();
			break;

		/*case R.id.spinner_btn2:
			Log.e("DROP DOWN IMG BTN CLICKED....spinner_btn2",
					"DROP DOWN IMG BTN CLICKED....");
			spi_sel_beneficiery.performClick();
			break;*/

		case R.id.ohtertranImpsbtn_submit:
			strFromAccNo=spi_debit_account.getSelectedItem().toString();
			strToAccNo = txtUid.getText().toString().trim();
			strAmount = txtAmt.getText().toString().trim();

			//strAmount=FormatAmount(strAmount);
			
			//strAmount=String.format("%.2f", strAmount);
			strRemark = txtRemk.getText().toString().trim();
			//String transferType=spi_payment_option.getSelectedItem().toString();
		//	double amt = Double.parseDouble(strAmount);
		
			if(strToAccNo.length()==0)
			{
				showAlert(getString(R.string.alert_156));
			}
		
			else if((strToAccNo.length() < 12)||(strToAccNo.length() > 12))
			{
				showAlert(getString(R.string.alert_157));
			}
			/*else if(strToAccNo.equalsIgnoreCase(strFromAccNo))
			{
				showAlert(getString(R.string.alert_107));
			}*/
			else if(strAmount.matches(""))
			{
				showAlert(getString(R.string.alert_033));
			}
			else if(Double.parseDouble(strAmount)==0)
			{
				showAlert(getString(R.string.alert_034));
			}
			
			/*else if(transferType.equalsIgnoreCase("NEFT") && Double.parseDouble(strAmount)>=200000)
			{
				showAlert(getString(R.string.alert_147));
			}
			else if(transferType.equalsIgnoreCase("RTGS") && Double.parseDouble(strAmount)<200000)
			{
				showAlert(getString(R.string.alert_148));
			}*/
			else
			{
				try 
				{
					this.flag = chkConnectivity();
					Log.e("ohtertranImpsbtn_submit"," SUBMIT SUBMIT"+flag);
					if (this.flag == 0) 
					{
						//saveData();
						CallWebServiceGetSrvcCharg c=new CallWebServiceGetSrvcCharg();
						c.execute();
					}
				} 
				catch (Exception e) 
				{
					e.printStackTrace();
					System.out.println("Exception in CallWebServiceGetSrvcCharg is:" + e);
				}
			}
			break;
		case R.id.btn_confirm:
			String str = txtAmt.getText().toString().trim();
			String str2 = txtRemk.getText().toString().trim();
			String accNo = txtUid.getText().toString().trim();
			//String benname=txt_con_bnfname.getText().toString();
			Log.e("btn_confirm","btn_confirm===="+str);
			Log.e("btn_confirm","btn_confirm===="+str2);
			Log.e("btn_confirm","btn_confirm===="+accNo);
			
			 if (str.length() == 0) {
				System.out.println("Cuttent thread name:==>"
						+ Thread.currentThread().getName());
				System.out.println("--------------- 22 ------------");
				retMess = getString(R.string.alert_033);
				System.out.println("--------------- 22.1 ------------");
				showAlert(retMess);
				System.out.println("--------------- 22.2 ------------");
				txtAmt.requestFocus();
				System.out.println("--------------- 22.3 ------------");
			} else {
			//int amt = Integer.parseInt(str); Gives error for fraction so
				double amt = Double.parseDouble(str);				
				if (amt <= 0) {
					System.out.println("--------------- 44 ------------");
					retMess = getString(R.string.alert_034);
					showAlert(retMess);
					txtAmt.requestFocus();
				} else {
					/*if (str2.length() == 0) {
						System.out.println("--------------- 33 ------------");
						retMess = getString(R.string.alert_035);
						showAlert(retMess);
						txtRemk.requestFocus();
					} else */
					
					{
						InputDialogBox inputBox = new InputDialogBox(act);
						inputBox.show();
					} // end else
				}
			}// end if
			break;

		default:
			break;
		}
	}// end onClick

	// for save transfer
	class CallWebServiceSaveTransfer extends AsyncTask<Void, Void, Void> { 
		String retval = "";
		LoadProgressBar loadProBarObj = new LoadProgressBar(act);
		String[] xmlTags = { "BENFSRNO", "CRACCNO","DRACCNO", "AMOUNT",        
				"REMARK", "TRANSFERTYPE", "IMEINO","CUSTID","CHARGES" ,
				"CHRGACCNO","TRANID","SERVCHRG","CESS"};
		String[] valuesToEncrypt = new String[13];
		String generatedXML = "";
		String  amt;
		String accNo, debitAccno, benAcNo, amtStr, reMark;
		protected void onPreExecute() {      
			// pb_wait.setVisibility(ProgressBar.VISIBLE);
			loadProBarObj.show();
			accNo = txtUid.getText().toString().trim();
			debitAccno = spi_debit_account.getItemAtPosition(
					spi_debit_account.getSelectedItemPosition()).toString();
			
			amt = txtAmt.getText().toString().trim();
			reMark = txtRemk.getText().toString().trim();
			amtStr = txtAmt.getText().toString().trim();
			/*tranType=spi_payment_option.getItemAtPosition(
					spi_payment_option.getSelectedItemPosition()).toString();*/
			String uid=txt_to.getText().toString().trim();
			String charges=txt_charges.getText().toString().split(" ")[1];
			String drAccNo=txt_from.getText().toString().trim();
			
			drAccNo=drAccNo.substring(0,16);
			Log.e("111111111111111","22222222222 111111"+drAccNo);
			/*int pos=amtStr.indexOf(".");
			valuesToEncrypt[3] = p os>-1?amtStr.substring(0,(amtStr.length()-pos)<=2?amtStr.length():pos+2):amtStr;*/
			valuesToEncrypt[0] = "";
			
			Log.e("OtherBankTranIMPS","CallWebServiceSaveTransfer 111111"+valuesToEncrypt[0]);
			valuesToEncrypt[1] = uid;
			Log.e("OtherBankTranIMPS","CallWebServiceSaveTransfer 222222"+valuesToEncrypt[1]);
			valuesToEncrypt[2] = drAccNo;
			Log.e("OtherBankTranIMPS","CallWebServiceSaveTransfer 333333"+valuesToEncrypt[2]);
			valuesToEncrypt[3] = amt;
			Log.e("OtherBankTranIMPS","CallWebServiceSaveTransfer  44444"+valuesToEncrypt[3]);
			valuesToEncrypt[4] = reMark;
			Log.e("OtherBankTranIMPS","CallWebServiceSaveTransfer   5555"+valuesToEncrypt[4]);
			valuesToEncrypt[5] = "P2U";
			Log.e("OtherBankTranIMPS","CallWebServiceSaveTransfer   6666"+valuesToEncrypt[5]);
			valuesToEncrypt[6] = MBSUtils.getImeiNumber(act);
			Log.e("OtherBankTranIMPS","CallWebServiceSaveTransfer   7777"+valuesToEncrypt[6]);
			valuesToEncrypt[7] = custId;
			Log.e("OtherBankTranIMPS","CallWebServiceSaveTransfer  88888"+valuesToEncrypt[7]);
			valuesToEncrypt[8] = charges;
			Log.e("OtherBankTranIMPS","CallWebServiceSaveTransfer  9999"+valuesToEncrypt[8]);
			valuesToEncrypt[9] = chrgCrAccNo;
			Log.e("OtherBankTranIMPS","CallWebServiceSaveTransfer  10 10"+valuesToEncrypt[9]);
			valuesToEncrypt[10] = tranId;
			Log.e("OtherBankTranIMPS","CallWebServiceSaveTransfer  11 11"+valuesToEncrypt[10]);
			valuesToEncrypt[11] = servChrg;
			Log.e("OtherBankTranIMPS","CallWebServiceSaveTransfer  12 12"+valuesToEncrypt[11]);
			valuesToEncrypt[12] = cess;
			Log.e("OtherBankTranIMPS","CallWebServiceSaveTransfer  13 13"+valuesToEncrypt[12]);
		/*	
			System.out.println("debitAccno:" + debitAccno);
			System.out.println("benAcNo:" + benAcNo);
			System.out.println("Value of acco No:" + accNo);
			System.out.println("Value of amt:" + amt);
			System.out.println("Value of reMark:" + reMark);

			valuesToEncrypt[0] = benSrno;
			valuesToEncrypt[1] = drBrnCD;
			valuesToEncrypt[2] = drSchmCD;
			valuesToEncrypt[3] = drAcNo;
			valuesToEncrypt[4] = amt;
			valuesToEncrypt[5] = reMark;
			valuesToEncrypt[6] = "IMPS";*/
			Log.e("generatedXML","pre execute before generatedXML  13 13"+generatedXML);
			generatedXML = CryptoUtil.generateXML(xmlTags, valuesToEncrypt);
			System.out.println("&&&&&&&&&& generatedXML " + generatedXML);
			Log.e("generatedXML","pre execute after generatedXML  13 13"+generatedXML);
			

		}

		protected Void doInBackground(Void... arg0) {
			Log.e("doInBackground","doInBackground  00000");
			NAMESPACE = getString(R.string.namespace);
			URL = getString(R.string.url);
			SOAP_ACTION = getString(R.string.soap_action);
			METHOD_SAVE_TRANSFERTRAN = "storeTransferTranWS";
			try {
				System.out.println("Value of acco No:" + accNo);
				// System.out.println("Value of debitAccno:"+benAccountNumber);
				System.out.println("Value of amt:" + amt);
				System.out.println("Value of reMark:" + reMark);
				Log.e("doInBackground","doInBackground  00000 accNo "+accNo);
				Log.e("doInBackground","doInBackground  00000  amt "+ amt);
				Log.e("doInBackground","doInBackground  00000 reMark " + reMark);
				
				// checking benf account number...
				System.out.println("================== 1 ============");
				SoapObject request = new SoapObject(NAMESPACE,
						METHOD_SAVE_TRANSFERTRAN);
				Log.e("doInBackground","doInBackground  00000" + reMark);
				System.out
						.println("==========IN doInBackground =========== 11111");
				request.addProperty("para_value", generatedXML);

				System.out.println("================== 2 ============");
				SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
						SoapEnvelope.VER11);
				System.out.println("================== 3 ============");
				envelope.setOutputSoapObject(request);
				System.out.println("================== 4 ============");
				HttpTransportSE androidHttpTransport = new HttpTransportSE(URL,
						15000);
				System.out.println("================== 5 ============");
				System.out.println("================= saveTransferTran  1 ----------- ");

				System.out.println("================= saveTransferTran  2 ----------- ");
				if (androidHttpTransport != null)
					System.out.println("=============== androidHttpTransport is not null ");
				else
					System.out.println("=============== androidHttpTransport is  null ");
				androidHttpTransport.call(SOAP_ACTION, envelope);

				retval = envelope.bodyIn.toString().trim();
				int i = envelope.bodyIn.toString().trim().indexOf("=");
				this.retval = this.retval.substring(i + 1,
						this.retval.length() - 3);
              Log.e("retval","retvalretval===="+retval);
				System.out
						.println("================= saveTransferTran  5 ----------- ");
				// Log.i("SameBankTransfer    retval", this.retval);
				System.out.println("saveTransferTran    seccessStr-----"
						+ retval);
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("Exception 2");
				System.out.println("SameBankTransfer   Exception" + e);
			}
			return null;
		}// end dodoInBackground2

		protected void onPostExecute(Void paramVoid) {
			// retval="SUCCESS";
			
			Log.e("onPostExecute","onPostExecute  00000");
			Log.e("onPostExecute","onPostExecute  00000");
			Log.e("onPostExecute","onPostExecute  00000");
			Log.e("onPostExecute","onPostExecute  00000");
			
			String[] xmlTags = { "TRANSFER" };
			String[] xml_data = CryptoUtil.readXML(retval, xmlTags);
			System.out.println("xml_data :" + xml_data);
			Log.e("onPostExecute","onPostExecute  00000"+ xml_data);
			Log.e("TRANSFER123123",xml_data[0]);
			String decryptedAccounts = xml_data[0];
			Log.e("onPostExecute","onPostExecute  00000"+decryptedAccounts);
			loadProBarObj.dismiss();
			if (decryptedAccounts.indexOf("SUCCESS") > -1) {
				
				/*String msg[] = decryptedAccounts.split("~");
				postingStatus=msg[1];
				req_id=msg[2];
				Log.e("onPostExecute","onPostExecute  In success");
				// retMess="Fund Transfer Successful";
				retMess = getString(R.string.alert_150)+" "+tranId+" "+req_id;
				showAlert(retMess);*/
				
				String msg[] = decryptedAccounts.split("~");
				if(msg.length>2)
				{
					if(msg[2]!=null || msg[2].length()>0)
					{
					postingStatus=msg[1];
					req_id=msg[2];
					Log.e("Ganesh "," Failed NA req_id="+req_id);
					Log.e("Ganesh ","Failed NA req_id="+postingStatus);
					retMess = getString(R.string.alert_150)+" "+req_id;
					}
				}
				else
				{
					retMess = getString(R.string.alert_163);
				}
				showAlert(retMess);
				
				FragmentManager fragmentManager;
				Fragment fragment = new FundTransferMenuActivity(act);
				act.setTitle(getString(R.string.lbl_same_bnk_trans));
				fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction()
						.replace(R.id.frame_container, fragment).commit();
				System.out
						.println("================== in onPostExecute  else ============================");
			} else {
				if (decryptedAccounts.indexOf("LIMIT_EXCEEDS") > -1) {
					Log.e("onPostExecute","onPostExecute  In LIMIT_EXCEEDS");
					// retMess="Problem in Fund Transfer,Your Transfer amount is exceeds Limite";
					retMess = getString(R.string.alert_031);
					loadProBarObj.dismiss();
					//showAlert(retMess);
					FragmentManager fragmentManager;
					Fragment fragment = new FundTransferMenuActivity(act);
					act.setTitle(getString(R.string.lbl_same_bnk_trans));
					fragmentManager = getFragmentManager();
					fragmentManager.beginTransaction()
							.replace(R.id.frame_container, fragment).commit();
					// loadProBarObj.dismiss();
				} 
				else if(decryptedAccounts.indexOf("DUPLICATE") > -1)
				{
					Log.e("onPostExecute","onPostExecute  In DUPLICATE");
					
					retMess = getString(R.string.alert_119)+tranId+"\n"+ getString(R.string.alert_120);
					showAlert(retMess);
					FragmentManager fragmentManager;
					Fragment fragment = new FundTransferMenuActivity(act);
					act.setTitle(getString(R.string.lbl_same_bnk_trans));
					fragmentManager = getFragmentManager();
					fragmentManager.beginTransaction()
							.replace(R.id.frame_container, fragment).commit();
				}
				else if(decryptedAccounts.indexOf("FAILED~") > -1)
				{
					String msg[] = decryptedAccounts.split("~");
					if(msg.length>2)
					{
					if(msg[2]!=null || msg[2].length()>0)
					{
						postingStatus=msg[1];
						req_id=msg[2];
						Log.e("Ganesh "," Failed NA req_id="+req_id);
						Log.e("Ganesh ","Failed NA req_id="+postingStatus);
						retMess = getString(R.string.alert_162)+" "+req_id;
					}
					}
					else
					{
						retMess = getString(R.string.alert_032);
					}
					showAlert(retMess);
					
				}
					/*String msg[] = decryptedAccounts.split("~");
					postingStatus=msg[1];
					req_id=msg[2];
					retMess = getString(R.string.alert_032)+""+req_id;
					showAlert(retMess);//setAlert();
				}*/
				else {
					// retMess="Fund Transfer Failed due to server problem ,Please try after some time";
					retMess = getString(R.string.alert_032);
					loadProBarObj.dismiss();
					showAlert(retMess);
					
					// loadProBarObj.dismiss();
					System.out
							.println("================== in onPostExecute 2 ============================");
				}
			}// end else
		}// end onPostExecute

	}// end callWbService2

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
			setContentView(R.layout.transfer_dialog);
			mpin = (EditText) findViewById(R.id.txtMpin);
			btnOk = (Button) findViewById(R.id.btnOK);
			mpin.setVisibility(EditText.VISIBLE);
			btnOk.setVisibility(Button.VISIBLE);
			btnOk.setOnClickListener(this);
		}

		@Override
		public void onClick(View v) {
			/*try {
				System.out
						.println("========= inside onClick ============***********");
				String str = mpin.getText().toString();
				System.out.println("======== strmpin:==" + str);
				System.out.println("======== mobPin:==" + mobPin);
				
				 * if(str.equals(mobPin)) { saveData(); this.hide(); } else {
				 * System.out.println("=========== inside else ==============");
				 * this.hide(); }
				 

				if (str == null) {
					retMess = getString(R.string.alert_015);
					showAlert(retMess);
					this.show();
				} else if (str.length() > 15 || str.length() < 4) {
					// retMess="MPIN must be within 4 to 15 character";
					retMess = getString(R.string.alert_037);
					showAlert(retMess);
					this.show();
				} else {
					System.out.println("======== strmpin:==" + str);
					System.out.println("======== mobPin:==" + mobPin);
					if (str.equals(mobPin)) {
						//saveData();
						this.hide();
					} else {
						// System.out.println("=========== inside else ==============");
						//retMess = getString(R.string.alert_015);
						
						//showAlert(retMess);
						//this.show();
					}
				}

				saveData();
				this.hide();
			} catch (Exception e) {
				e.printStackTrace();
				System.out
						.println("Exception in InputDialogBox of onClick:=====>"
								+ e);
			}*/
			
			try {
				
				 // System.out.println("========= inside onClick ============***********"); 
				  String str=mpin.getText().toString().trim(); 
				  String encrptdTranMpin=ListEncryption.encryptData(custId+str);
				  if(str.length()==0) 
				  {
				  	retMess=getString(R.string.alert_116); 
				  	showAlert(retMess);//setAlert();
				  	this.show(); 
				  } 
				  else if(str.length()!=6) 
				  {
					retMess=getString(R.string.alert_037); 
					showAlert(retMess);//setAlert();
				  	this.show(); 
				  } 
				  else 
				  {
				  	//System.out.println("======== strmpin:=="+str);
				  	//System.out.println("======== mobPin:=="+mobPin);
				  	if(encrptdTranMpin.equals(tranPin)) 
				  	{ 
				  		saveData(); 
				  		this.hide(); 
				 	} 
				  	else 
				  	{
				  		//System.out.println("=========== inside else ==============");
				  		retMess=getString(R.string.alert_118); 
				  		showAlert(retMess);//setAlert();
				  		this.show(); 
				  	} 
				  }
			} catch (Exception e) {
				e.printStackTrace();
				System.out
						.println("Exception in InputDialogBox of onClick:=====>"
								+ e);
			}
			
		}// end onClick
	}// end InputDialogBox

	class CallWebServiceGetSrvcCharg extends AsyncTask<Void, Void, Void> {
		String retval = "";
		LoadProgressBar loadProBarObj = new LoadProgressBar(act);
		
		String[] xmlTags = {"CUSTID","TRANTYPE","DRACCNO","AMOUNT","CRACCNO","IMEINO"};
		String[] valuesToEncrypt = new String[6];
		String generatedXML = "";
		String accNo, debitAccno, benAcNo, amt, reMark;

		protected void onPreExecute() { 
			// pb_wait.setVisibility(ProgressBar.VISIBLE);
			loadProBarObj.show();
			accNo = txtUid.getText().toString().trim();
			debitAccno = arrListTemp.get(spi_debit_account.getSelectedItemPosition());
			
			/*tranType=spi_payment_option.getItemAtPosition(
					spi_payment_option.getSelectedItemPosition()).toString();*/
			amt = txtAmt.getText().toString().trim();
			reMark = txtRemk.getText().toString().trim();
			
			/*if(tranType.equalsIgnoreCase("RTGS"))
				tranType="RT";
			else if(tranType.equalsIgnoreCase("NEFT"))
				tranType="NT";*/
			
			valuesToEncrypt[0] = custId;
			valuesToEncrypt[1] = "IMPS";
			valuesToEncrypt[2] = debitAccno;
			valuesToEncrypt[3] = amt;
			valuesToEncrypt[4] = accNo;
			valuesToEncrypt[5] = MBSUtils.getImeiNumber(act);
			

			generatedXML = CryptoUtil.generateXML(xmlTags, valuesToEncrypt);
		}

		protected Void doInBackground(Void... arg0) {
			NAMESPACE = getString(R.string.namespace);
			URL = getString(R.string.url);
			SOAP_ACTION = getString(R.string.soap_action);
			METHOD_GET_TRANSFERCHARGE = "fetchTransferChargesWS";
			try {
				SoapObject request = new SoapObject(NAMESPACE,
						METHOD_GET_TRANSFERCHARGE);

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
					System.out
							.println("=============== androidHttpTransport is  null ");

				androidHttpTransport.call(SOAP_ACTION, envelope);
				retval = envelope.bodyIn.toString().trim();
				int i = envelope.bodyIn.toString().trim().indexOf("=");
				this.retval = this.retval.substring(i + 1,
						this.retval.length() - 3);

				return null;
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("SameBankTransfer   Exception" + e);
			}
			return null;
		}// end dodoInBackground2

		protected void onPostExecute(Void paramVoid) {
			String[] xmlTags = { "RETVAL" };
			String[] xml_data = CryptoUtil.readXML(retval, xmlTags);
			String decryptedAccounts = xml_data[0];
			Log.e("DEBUG@ANAND",decryptedAccounts );
			if (decryptedAccounts.indexOf("SUCCESS") > -1) 
			{
				act.frgIndex=52;
				loadProBarObj.dismiss();
				other_bnk_layout.setVisibility(other_bnk_layout.INVISIBLE);
				confirm_layout.setVisibility(confirm_layout.VISIBLE);
				
				String retStr=xml_data[0].split("~")[1];
				Log.e("HELL",retStr);
				String[] val=retStr.split("#");
				txt_heading.setText("Confirmation");
				txt_remark.setText(strRemark);
				txt_from.setText(strFromAccNo);
				txt_to.setText(strToAccNo);
				txt_amount.setText("INR "+strAmount);
				txt_charges.setText("INR "+val[0]);
				
				chrgCrAccNo=val[1];
				tranId=val[2];
				servChrg=val[3];
				cess=val[4];
				Log.e("OTHERBNKTRAN","servChrg==="+servChrg+"==cess=="+cess);
				if(chrgCrAccNo.length()==0 || chrgCrAccNo.equalsIgnoreCase("null"))
					chrgCrAccNo="";
				
				if(servChrg.equalsIgnoreCase("null"))
					servChrg="0";
				
				if(cess.equalsIgnoreCase("null"))
					cess="0";
				
				Log.e("OTHERBNKTRAN","2222servChrg==="+servChrg+"==cess=="+cess);
				txt_charges.setText("INR "+(Float.parseFloat(val[0])+Float.parseFloat(servChrg)+Float.parseFloat(cess)));
			} 	
			else 
			{
				if (decryptedAccounts.indexOf("TRANAMTLIMIT") > -1) 
				{
					
					/*retMess = getString(R.string.alert_149)+
							decryptedAccounts.split("~")[2].split("#")[0]+
							 getString(R.string.alert_148) +
							decryptedAccounts.split("~")[2].split("#")[1];*/
					String errCd = decryptedAccounts.split("~")[2];
					if(errCd.equalsIgnoreCase("01"))
						retMess = getString(R.string.alert_148);
					else
						retMess = getString(R.string.alert_149);
					loadProBarObj.dismiss();
					showAlert(retMess);//setAlert();
					//showAlert("IF: "+decryptedAccounts);//setAlert();
				} else if (decryptedAccounts.indexOf("STOPTRAN") > -1)
				{
					retMess = getString(R.string.Stop_Tran);
					//loadProBarObj.dismiss();
					showAlert(retMess);
				}else {
					// this case consider when in retval string contains only  "FAILED"
					retMess = getString(R.string.alert_032);
					loadProBarObj.dismiss();
					showAlert(retMess);//setAlert();
					//System.out
					//		.println("================== in onPostExecute 2 ============================");
				}
			}// end else
		}// end onPostExecute
	}// end CallWebServiceGetSrvcCharg
	/*class CallWebServiceFetBnkBrn extends AsyncTask<Void, Void, Void> {
		String retVal = "";
		LoadProgressBar loadProBarObj = new LoadProgressBar(act);
		String[] xmlTags = { "IFSC","CUSTID", "IMEINO" };
		String[] valuesToEncrypt = new String[3];
		String generatedXML = "";

		// String accNo,debitAccno,benAcNo,amt,reMark;

		protected void onPreExecute() {
			loadProBarObj.show();

			System.out.println("ifsCD:" + ifsCD);

			valuesToEncrypt[0] = ifsCD;
			valuesToEncrypt[1] = custId;
			valuesToEncrypt[2] = MBSUtils.getImeiNumber(act);
			generatedXML = CryptoUtil.generateXML(xmlTags, valuesToEncrypt);
			System.out.println("&&&&&&&&&& generatedXML " + generatedXML);
		}

		protected Void doInBackground(Void... arg0) {
			NAMESPACE = getString(R.string.namespace);
			URL = getString(R.string.url);
			SOAP_ACTION = getString(R.string.soap_action);
			try {
				System.out.println("for save Transfer------------- ");
				System.out
						.println("CallWebService3    selected all_str----------- ");

				// checking benf account number...
				System.out.println("================== 1 ============");
				SoapObject request = new SoapObject(NAMESPACE,
						METHOD_GET_BANK_BRANCH);
				System.out.println("inside doInBackground 3 ifscCd id:=======>"
						+ ifsCD);

				System.out
						.println("==========IN doInBackground =========== 11111");
				request.addProperty("para_value", generatedXML);
				System.out
						.println("==========IN doInBackground =========== 2222");

				// request.addProperty("ifscCd",ifsCD);
				System.out.println("================== 2 ============");
				SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
						SoapEnvelope.VER11);
				System.out.println("================== 3 ============");
				envelope.setOutputSoapObject(request);
				System.out.println("================== 4 ============");
				HttpTransportSE androidHttpTransport = new HttpTransportSE(URL,
						15000);
				System.out.println("================== 5 ============");
				System.out
						.println("================= saveTransferTran  1 ----------- ");

				System.out
						.println("================= saveTransferTran  2 ----------- ");
				if (androidHttpTransport != null)
					System.out
							.println("=============== androidHttpTransport is not null ");
				else
					System.out
							.println("=============== androidHttpTransport is  null ");
				androidHttpTransport.call(SOAP_ACTION, envelope);
				retVal = envelope.bodyIn.toString().trim();

				retVal = retVal.substring(retVal.indexOf("=") + 1,
						retVal.length() - 3);

				System.out.println("================= retVal  5 ----------- "
						+ retVal);
			}// end try
			catch (Exception e) {
				e.printStackTrace();
				System.out.println("Exception 2");
				System.out.println("SameBankTransfer   Exception" + e);
			}
			return null;
		}// end dodoInBackground2

		protected void onPostExecute(Void paramVoid) 
		{
			txtBank.setText(bnCD);
			txtBranch.setText(brCD);
			String[] xml_data = CryptoUtil.readXML(retVal,
					new String[] { "BNKBRN" });
			String decryptedBeneficiaries = xml_data[0];
			decryptedBeneficiaries="SUCCESS";
			if (decryptedBeneficiaries.indexOf("SUCCESS") > -1) 
			{
				String allStr[] = decryptedBeneficiaries.split("~");
				String bankBranch[] = allStr[1].split("#");
				 
				bnCD =bankBranch[0];
				brCD = bankBranch[1];
				txtBank.setText(bnCD);
				txtBranch.setText(brCD);
				txtBank.setText("BOI");
				txtBranch.setText("Sangli");
				loadProBarObj.dismiss();
			} 
			else 
			{
				retMess = getString(R.string.alert_069);
				loadProBarObj.dismiss();
				showAlert(retMess);
			}
		}// end doPost
	}// end CallWebServiceFetBnkBrn
*/	public String FormatAmount(String amount) 
	{	
		String returnAmount="";
		String temp;//=new String[2];
		int i,count=0,flag=0;
		if(amount.length()==0)
			return "0";
		else
		Log.e("999999999","999999999....."+amount);
		if(amount.indexOf('.')>0)
		{
			Log.e("5555555555555","55555555555,,,,,"+amount.indexOf('.'));
			flag=1;
			
			temp=amount.substring(0, amount.indexOf('.'));
			//Log.e("5555555555555","55555555555,,,,,"+temp[0]);
			Log.e("5555555555555","55555555555...."+temp);
			i=temp.length()-1;
		}
		else		{
			temp=amount;
			i=temp.length()-1;
		}
			
		Log.e("amount length"," "+i);
		while(i>=0)
		{
			char ch=temp.charAt(i);
			Log.e("666666","66666...."+ch);
			if(count%2==1 && count>=3)
			{
				returnAmount=","+returnAmount;
			}
			returnAmount=ch+returnAmount;
			count++;
			i--;
		}
		
		if(flag==1)
			returnAmount=returnAmount+"."+amount.substring(amount.indexOf('.')+1);
		Log.e("1111111111111","1111111111111....."+returnAmount);
		return returnAmount;
	}
	public void saveData() {
		try {
			System.out.println("--------------- 44 ------------");
			this.flag = chkConnectivity();
			Log.e("saveData","saveDatasaveData "+flag);
			if (this.flag == 0) {
				new CallWebServiceSaveTransfer().execute();
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Exception is in onclick :" + e);
		}
	}// end saveData
}// end OtherBankTranIMPS


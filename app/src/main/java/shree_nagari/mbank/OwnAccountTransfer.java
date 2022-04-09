package shree_nagari.mbank;

import java.security.PrivateKey;
import java.util.ArrayList;

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
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class OwnAccountTransfer extends Fragment implements OnClickListener {
	private static String METHOD_NAME = "";
	private static String METHOD_SAVE_TRANSFERTRAN = "";
	private static String METHOD_GET_TRANSFERCHARGE = "";
	private static String METHOD_validateTranMPINWS = "";
	
	private static String NAMESPACE = "";
	private static String SOAP_ACTION = "";
	private static String URL = "";
	private String benInfo = "";
	ImageView btn_home1,btn_logout, btn_back;
	DialogBox dbs;
	String creditAcc="";
	DatabaseManagement dbms;
	Button btn_submit, btn_confirm, btn_con_back;
	TextView txt_heading, txt_remark, txt_from, txt_to, txt_amount,
			txt_charges;
	int cnt = 0;
	TextView cust_nm, txtTranId, txt_trantype;
	boolean status;
	int flag = 0, frmno = 0, tono = 0;
	Intent in;
	ProgressBar pb_wait;
	Spinner spi_debit_account, spi_sel_beneficiery;
	StopPayment stp = null;
	String str = "", str2 = "", stringValue = "", benSrno = "", strFromAccNo,
			strToAccNo, strAmount, strRemark, benAccountNumber = "",
			drBrnCD = "", drSchmCD = "", drAcNo = "", mobPin = "",
			chrgCrAccNo = "", tranPin = "", retMess = "", custId = "",
			cust_name = "", acnt_inf, all_acnts, tranId = "";
	EditText txtAccNo, txtAmt, txtRemk, txtBalance;
	OwnAccountTransfer Obj;
	MainActivity act;
	View mainView;
	LinearLayout confirm_layout, same_bnk_layout;
	boolean noAccounts;
	private ImageButton spinenr_btn2;
	private ImageButton spinenr_btn;
	ArrayList<String> arrListTemp = new ArrayList<String>();
	private LayoutInflater inflater;
	OwnAccountTransfer ownBnkTran;
	private String userId,errorCode="";
	public String encrptdTranMpin,Tranmpin="";
	public String encrptdUTranMpin;
	Accounts acArray[];
	CommonLib libObj;
	ImageView img_heading;
	PrivateKey var1=null;	  
	String var5="",var3="";
	SecretKeySpec var2=null;
	String respcode = "",retvalwbs = "",respdesc ="";
	
	public OwnAccountTransfer() {
		ownBnkTran = this;
	}

	@SuppressLint("ValidFragment")
	public OwnAccountTransfer(MainActivity m) {
		act = m;
		ownBnkTran = this;
	}

	@SuppressLint("WrongConstant")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		View rootView = inflater.inflate(R.layout.own_account_transfer,container, false);
		var1 = act.var1;
		var3 = act.var3;
		Log.e("Shubham", "var1-->"+var1+" var3-->"+var3 );
		libObj=new CommonLib(act);
		noAccounts = false;
		img_heading = (ImageView) rootView.findViewById(R.id.img_heading);
		img_heading.setBackgroundResource(R.mipmap.fund_trnsfer);
		dbms = new DatabaseManagement("shree_nagari.mbank", "listMobileBanking");
		this.dbs = new DialogBox(act);
		Cursor c1 = dbms.selectFromTable("SHAREDPREFERENCE", "", null);
		if (c1 != null) {
			while (c1.moveToNext()) {
				stringValue = c1.getString(0);
				custId = c1.getString(2);
				userId = c1.getString(3);
			}
		}

		btn_home1 = (ImageView) rootView.findViewById(R.id.btn_home1);
		btn_logout = (ImageView) rootView.findViewById(R.id.btn_logout);

		confirm_layout = (LinearLayout) rootView
				.findViewById(R.id.confirm_layout);
		same_bnk_layout = (LinearLayout) rootView
				.findViewById(R.id.same_bnk_layout);

		btn_home1.setImageResource(R.mipmap.ic_home_d);
		//btn_back.setImageResource(R.drawable.backover);
		btn_home1.setOnClickListener(this);
		btn_logout.setOnClickListener(this);
		//btn_back.setOnClickListener(this);
		txt_heading = (TextView) rootView.findViewById(R.id.txt_heading);
		txt_heading.setText(getString(R.string.lbl_own_account_trans));
		txt_trantype = (TextView) rootView.findViewById(R.id.txt_trantype);
		btn_confirm = (Button) rootView.findViewById(R.id.btn_confirm);
		txt_remark = (TextView) rootView.findViewById(R.id.txt_remark);
		txt_from = (TextView) rootView.findViewById(R.id.txt_from);
		txt_to = (TextView) rootView.findViewById(R.id.txt_to);
		txt_amount = (TextView) rootView.findViewById(R.id.txt_amount);
		txt_charges = (TextView) rootView.findViewById(R.id.txt_charges);
		txtTranId = (TextView) rootView.findViewById(R.id.txt_tranid);
		btn_confirm.setOnClickListener(this);
		spinenr_btn2 = (ImageButton) rootView.findViewById(R.id.spinner_btn2);
		spinenr_btn2.setOnClickListener(this);
		spinenr_btn = (ImageButton) rootView.findViewById(R.id.spinner_btn);
		spinenr_btn.setOnClickListener(this);
		spi_debit_account = (Spinner) rootView
				.findViewById(R.id.sameBnkTranspi_debit_account);

		if (spi_debit_account != null)	
			spi_debit_account.requestFocus();
		

		spi_sel_beneficiery = (Spinner) rootView.findViewById(R.id.sameBnkTranspi_sel_beneficiery);
		btn_submit = (Button) rootView.findViewById(R.id.sameBnkTranbtn_submit);
		txtAccNo = (EditText) rootView.findViewById(R.id.sameBnkTrantxtAccNo);
		txtBalance = (EditText) rootView.findViewById(R.id.sameBnkTrantxtBal);
		txtAmt = (EditText) rootView.findViewById(R.id.sameBnkTrantxtAmt);
		txtRemk = (EditText) rootView.findViewById(R.id.sameBnkTrantxtRemk);
		
		pb_wait = (ProgressBar) rootView.findViewById(R.id.sameBnkTranpro_bar);
		btn_submit.setOnClickListener(this);
		spi_sel_beneficiery.setOnItemSelectedListener(new OnItemSelectedListener() 
		{
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,int arg2, long arg3) 
			{
				if (spi_debit_account.getCount() > 0) 
				{
					String str = arrListTemp.get(spi_debit_account.getSelectedItemPosition());
					retMess = "Selected Account number" + str;
				}
			}// end onItemSelected

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}// end onNothingSelected
		});// end spi_debit_account

		spi_debit_account.setOnItemSelectedListener(new OnItemSelectedListener() 
		{
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,int arg2, long arg3) 
			{
				if (spi_debit_account.getCount() > 0) 
				{
					String str = arrListTemp.get(spi_debit_account.getSelectedItemPosition());
					retMess = "Selected Account number" + str;
					Accounts selectedDrAccount = acArray[spi_debit_account.getSelectedItemPosition()];
					String balStr = selectedDrAccount.getBalace();
					String drOrCr = "";
					float amt = Float.parseFloat(balStr);
					if (amt > 0)
						drOrCr = " Cr";
					else if (amt < 0)
						drOrCr = " Dr";
					if (balStr.indexOf(".") == -1)
						balStr = balStr + ".00";
					balStr = balStr + drOrCr;
					txtBalance.setText(balStr);
				}
			}// end onItemSelected

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}// end onNothingSelected

		});// end spi_debit_account
		all_acnts = stringValue;

		addAccounts(all_acnts);
		addCreditAccounts(all_acnts);
		
		if (!noAccounts) 
		{
			noAccounts = false;
			this.flag = libObj.chkConnectivity();
			if (this.flag == 0) {
			}
		}
		this.pb_wait.setMax(10);
		this.pb_wait.setProgress(1);
		this.pb_wait.setVisibility(4);
		txtAmt.setFilters(new InputFilter[] { new DecimalDigitsInputFilter(2) });
		return rootView;
	}


	private void addBeneficiaries(String retval) 
	{
		try 
		{
			ArrayList<String> arrList = new ArrayList<String>();
			String allstr[] = retval.split("~");
			int noOfben = allstr.length;
			String benName = "";
			arrList.add("Select Beneficiary");
			for (int i = 1; i <= noOfben; i++) {
				String[] str2 = allstr[i - 1].split("#");
				benName = str2[2] + "(" + str2[1] + ")";
				arrList.add(benName);
				
			}
			String[] benfArr = new String[arrList.size()];
			benfArr = arrList.toArray(benfArr);
			
			ArrayAdapter<String> accs = new ArrayAdapter<String>(act,
					R.layout.spinner_item, benfArr);
			accs.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spi_sel_beneficiery.setAdapter(accs);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}// end addBeneficiaries

	public void addAccounts(String str) 
	{
		try 
		{
			ArrayList<String> arrList = new ArrayList<String>();
			String allstr[] = str.split("~");
			
			int noOfAccounts = allstr.length;
			int j=0;
			acArray = new Accounts[noOfAccounts];
			{
			    for (int i = 0; i < noOfAccounts; i++) 
			    {
					str2 = allstr[i];
					String tempStr=str2;
					acArray[i] = new Accounts(str2);
					str2 = str2.replaceAll("#", "-");
					String accType = str2.split("-")[2];
					String oprcd = str2.split("-")[7];
					String str2Temp = str2;
					str2 = MBSUtils.get16digitsAccNo(str2);
					if (((accType.equals("SB")) || (accType.equals("LO")) || (accType
						.equals("CA"))) && oprcd.equalsIgnoreCase("O"))
					{
						acArray[j++] = new Accounts(tempStr);
						arrList.add(str2 + " (" + MBSUtils.getAccTypeDesc(accType)+ ")");
						arrListTemp.add(str2);
					}
			}
			
			if (arrList.size() == 0) {
				noAccounts = true;
				Toast.makeText(act, getString(R.string.alert_188),Toast.LENGTH_LONG).show();
				Fragment fragment = new FundTransferMenuActivity(act);
				FragmentManager fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();				
			}
			String[] debAccArr = new String[arrList.size()];
			debAccArr = arrList.toArray(debAccArr);
			
			ArrayAdapter<String> debAccs = new ArrayAdapter<String>(act,R.layout.spinner_item, debAccArr);
			debAccs.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spi_debit_account.setAdapter(debAccs);	
		}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}// end addAccount
	
	public void addCreditAccounts(String str) 
	{
		try {
			ArrayList<String> arrList = new ArrayList<String>();
			String allstr[] = str.split("~");
			
			int noOfAccounts = allstr.length;
			int j=0;
			{
			    for (int i = 0; i < noOfAccounts; i++) {
				str2 = allstr[i];
				String tempStr=str2;
				str2 = str2.replaceAll("#", "-");
				String accType = str2.split("-")[2];
				String oprcd = str2.split("-")[7];
				String str2Temp = str2;
				str2 = MBSUtils.get16digitsAccNo(str2);
				if ((!accType.equalsIgnoreCase("FD") && !accType.equalsIgnoreCase("RP") && 
						!accType.equalsIgnoreCase("PG")))
					{
						arrList.add(str2 + " (" + MBSUtils.getAccTypeDesc(accType)+ ")");
						arrListTemp.add(str2);
					}
			}
			
			if (arrList.size() == 1) {
				noAccounts = true;
				showAlert(getString(R.string.alert_188));
			}
			String[] debAccArr = new String[arrList.size()];
			debAccArr = arrList.toArray(debAccArr);
			ArrayAdapter<String> debAccs = new ArrayAdapter<String>(act,
					R.layout.spinner_item, debAccArr);
			debAccs.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spi_sel_beneficiery.setAdapter(debAccs);
		}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}// end addAccount

	class CallWebService_fetch_all_beneficiaries extends AsyncTask<Void, Void, Void> 
	{
		String retval = "";
		String ValidationData="";
		LoadProgressBar loadProBarObj = new LoadProgressBar(act);
		JSONObject obj = new JSONObject();
	
		protected void onPreExecute() 
		{
			loadProBarObj.show();
			
			
			try 
			{
				obj.put("SIMNO", MBSUtils.getMyPhoneNO(act));
				obj.put("CUSTID", custId);
				obj.put("SAMEBNK", "Y");
				obj.put("IMEINO", MBSUtils.getImeiNumber(act));
			
				//ValidationData=MBSUtils.getValidationData(act,obj.toString());
			} 
			catch (JSONException e) 
			{
				e.printStackTrace();
			}
		
		}// end onPreExecute

		protected Void doInBackground(Void[] paramArrayOfVoid) 
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
		}// end dodoInBackground

		protected void onPostExecute(Void paramVoid) 
		{
			String str=CryptoClass.Function6(var5,var2);
			// jsonObj = new JSONObject(str.trim());
			String decryptedBeneficiaries = str.trim();
			
			if (decryptedBeneficiaries.indexOf("SUCCESS") > -1) 
			{
				decryptedBeneficiaries = decryptedBeneficiaries.split("SUCCESS~")[1];
				benInfo = decryptedBeneficiaries;
				addBeneficiaries(decryptedBeneficiaries);
				loadProBarObj.dismiss();
			} 
			else 
			{			
				if (decryptedBeneficiaries.indexOf("NODATA") > -1) 
				{
					Toast.makeText(act, getString(R.string.alert_041),Toast.LENGTH_LONG).show();
					Fragment fragment = new FundTransferMenuActivity(act);
					FragmentManager fragmentManager = getFragmentManager();
					fragmentManager.beginTransaction()
							.replace(R.id.frame_container, fragment).commit();
				} else {
					retMess = getString(R.string.alert_069);
					showAlert(retMess);
				}
				loadProBarObj.dismiss();
			}
		}// end onPostExecute
	}// end callWbService

	class CallWebService2 extends AsyncTask<Void, Void, Void> 
	{
		String retval = "";
		LoadProgressBar loadProBarObj = new LoadProgressBar(act);
		String ValidationData="";
		String accNo, debitAccno, benAcNo, amt, reMark;
		JSONObject obj = new JSONObject();
		
		protected void onPreExecute() 
		{
			loadProBarObj.show();
			accNo = txtAccNo.getText().toString().trim();
			debitAccno = arrListTemp.get(spi_debit_account
					.getSelectedItemPosition());
			benAcNo = spi_sel_beneficiery.getItemAtPosition(
					spi_sel_beneficiery.getSelectedItemPosition()).toString();
			amt = txtAmt.getText().toString().trim();
			reMark = txt_remark.getText().toString().trim();
			String charges = txt_charges.getText().toString().split(" ")[1];
			String drAccNo = txt_from.getText().toString().trim();
			
			
			try 
			{
				obj.put("SIMNO", MBSUtils.getMyPhoneNO(act));
				obj.put("BENFSRNO", benSrno);
				obj.put("CRACCNO", creditAcc);
				obj.put("DRACCNO", debitAccno);
				obj.put("AMOUNT", amt);
				obj.put("REMARK", reMark);
				obj.put("TRANSFERTYPE", "OWN");
				obj.put("IMEINO", MBSUtils.getImeiNumber(act));
				obj.put("CUSTID", custId);
				obj.put("CHARGES", charges);
				obj.put("CHRGACCNO", chrgCrAccNo);
				obj.put("TRANID", tranId);
				obj.put("SERVCHRG", "0");
				obj.put("CESS", "0");
				obj.put("TRANPIN", Tranmpin);
				obj.put("METHODCODE","16");
				
				//ValidationData=MBSUtils.getValidationData(act,obj.toString());
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
			// jsonObj = new JSONObject(str.trim());
			String decryptedAccounts = str.trim();
			loadProBarObj.dismiss();
		
			if (decryptedAccounts.indexOf("SUCCESS") > -1) 
			{
				retMess = getString(R.string.alert_030) + " "
						+ getString(R.string.alert_121) + " " + tranId;
				showAlert(retMess);
				FragmentManager fragmentManager;
				Fragment fragment = new FundTransferMenuActivity(act);
				act.setTitle(getString(R.string.lbl_same_bnk_trans));
				fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction()
						.replace(R.id.frame_container, fragment).commit();
			} 
			else if (decryptedAccounts.indexOf("DUPLICATE") > -1) 
			{
				retMess = getString(R.string.alert_119) + tranId + "\n"
						+ getString(R.string.alert_120);
				showAlert(retMess);
				FragmentManager fragmentManager;
				Fragment fragment = new FundTransferMenuActivity(act);
				act.setTitle(getString(R.string.lbl_same_bnk_trans));
				fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction()
						.replace(R.id.frame_container, fragment).commit();
			} 
			else if (decryptedAccounts.indexOf("FAILED#") > -1) 
			{
				retMess = getString(R.string.alert_032);
				showAlert(retMess);// setAlert();
			} 
			else if (decryptedAccounts.indexOf("WRONGTRANPIN") > -1) 
			{
				String msg[] = decryptedAccounts.split("~");
				String first = msg[1];
				String second = msg[2];
				int count = Integer.parseInt(second);
				count = 5 - count;
				loadProBarObj.dismiss();
				retMess = getString(R.string.alert_125_1) + " " + count + " "
						+ getString(R.string.alert_125_2);
				showAlert(retMess);
			} else if (decryptedAccounts.indexOf("BLOCKEDFORDAY") > -1) {
				loadProBarObj.dismiss();
				retMess = getString(R.string.login_alert_005);
				showAlert(retMess);
			} 
			else if (decryptedAccounts.indexOf("FAILED") > -1)//FAILED~SingleLimitExceeded~1000
			{
				if(decryptedAccounts.split("~")[1]!="null" || decryptedAccounts.split("~")[1]!="")
				{
					errorCode=decryptedAccounts.split("~")[1];
				}
				else
				{
					errorCode="NA";
				}
				
				if(errorCode.equalsIgnoreCase("999"))
				{
					retMess = getString(R.string.alert_179);
					//showAlert(retMess);
				}
				else if(errorCode.equalsIgnoreCase("001"))
				{
					    retMess = getString(R.string.alert_180);
						//showAlert(retMess);
				}
				else if(errorCode.equalsIgnoreCase("002"))
				{
					    retMess = getString(R.string.alert_181);
						//showAlert(retMess);
				}
				else if(errorCode.equalsIgnoreCase("003"))
				{
					    retMess = getString(R.string.alert_182);
						//showAlert(retMess);
				}
				else if(errorCode.equalsIgnoreCase("004"))
				{
					retMess = getString(R.string.alert_179);
						//showAlert(retMess);
				}
				else if(errorCode.equalsIgnoreCase("005"))
				{
					    retMess = getString(R.string.alert_183);
						//showAlert(retMess);
				}
				else if(errorCode.equalsIgnoreCase("006"))
				{
					    retMess = getString(R.string.alert_184);
						//showAlert(retMess);
				}
				else if(errorCode.equalsIgnoreCase("007"))
				{
					retMess = getString(R.string.alert_179);
						//showAlert(retMess);
				}
				else if(errorCode.equalsIgnoreCase("008"))
				{
					    retMess = getString(R.string.alert_176);
						//showAlert(retMess);
				}
				else
				{
				retMess = getString(R.string.trnsfr_alert_001);
				showAlert(retMess);// setAlert();
				FragmentManager fragmentManager;
				Fragment fragment = new FundTransferMenuActivity(act);
				act.setTitle(getString(R.string.lbl_same_bnk_trans));
				fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction()
						.replace(R.id.frame_container, fragment).commit();
				
				}
			}// end else

		}// end onPostExecute
	}// end callWbService2

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
				obj.put("SIMNO", MBSUtils.getMyPhoneNO(act));
				obj.put("IMEINO", MBSUtils.getImeiNumber(act));
				obj.put("TRANPIN", Tranmpin);
				obj.put("CUSTID", custId);
				obj.put("METHODCODE","73");
				
				//ValidationData=MBSUtils.getValidationData(act,obj.toString());
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
			// jsonObj = new JSONObject(str.trim());
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
			else if (decryptedAccounts.indexOf("WRONGTRANPIN") > -1) 
			{
				JSONObject obj=null;
				try {
					obj = new JSONObject(decryptedAccounts);
				
				String msg[] = obj.getString("RETVAL").split("~");
				String first = msg[1];
				String second = msg[2];
				Log.e("OMKAR", "---"+second+"----");
				int count = Integer.parseInt(second);
				count = 5 - count;
				loadProBarObj.dismiss();
				retMess = act.getString(R.string.alert_125_1) + " " + count + " "
						+ act.getString(R.string.alert_125_2);
				showAlert(retMess);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} 
			

		}// end onPostExecute
	}// end callValidateTranpinService

	public int chkConnectivity() {
		ConnectivityManager cm = (ConnectivityManager) act.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		try {
			NetworkInfo.State state = ni.getState();
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
			Log.e("EXCEPTION", "---------------" + ne);
			Log.i("mayuri", "NullPointerException Exception" + ne);
			flag = 1;
			//retMess = "Can Not Get Connection. Please Try Again.";
			retMess = getString(R.string.alert_000);
			showAlert(retMess);

		} catch (Exception e) {
			Log.e("EXCEPTION", "---------------" + e);
			Log.i("mayuri", "Exception" + e);
			flag = 1;
			//retMess = "Connection Problem Occured.";
			retMess = getString(R.string.alert_000);
			showAlert(retMess);
		}
		return flag;
	}

	
	@Override
	public void onClick(View v) { // logic to show input box

		/*if (v.getId() == R.id.btn_back) {
			if (same_bnk_layout.getVisibility() == View.VISIBLE) {
				Fragment fragment = new FundTransferMenuActivity(act);

				FragmentManager fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction()
						.replace(R.id.frame_container, fragment).commit();
				act.frgIndex = 5;
			} else if (confirm_layout.getVisibility() == View.VISIBLE) {
				confirm_layout.setVisibility(confirm_layout.INVISIBLE);
				same_bnk_layout.setVisibility(same_bnk_layout.VISIBLE);
				txt_heading.setText("Same Bank Transfer");
				act.frgIndex = 51;
			}
		} else*/ if (v.getId() == R.id.btn_home1) {
			Intent in = new Intent(getActivity(), NewDashboard.class);
			in.putExtra("VAR1", var1);
			in.putExtra("VAR3", var3);
			startActivity(in);
		} else if (v.getId() == R.id.spinner_btn2) {
			spi_sel_beneficiery.performClick();
		}else if (v.getId() == R.id.btn_logout) {
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
		} else if (v.getId() == R.id.spinner_btn) {
			spi_debit_account.performClick();
		} else if (v.getId() == R.id.sameBnkTranbtn_submit) {
			strFromAccNo = spi_debit_account.getSelectedItem().toString();
			strToAccNo = spi_sel_beneficiery.getSelectedItem().toString();//txtAccNo.getText().toString().trim();
			strAmount = txtAmt.getText().toString().trim();
			strRemark = txtRemk.getText().toString().trim();
			String balString = acArray[spi_debit_account
					.getSelectedItemPosition()].getBalace();
			double balance = Double.parseDouble(balString);
			balance = Math.abs(balance);
			String debitAcc = strFromAccNo.substring(0, 16);
			creditAcc = strToAccNo.substring(0, 16);
			if (strToAccNo.length() == 0) {
				showAlert(getString(R.string.alert_098));
			} 
			else 
			if (creditAcc.equalsIgnoreCase(debitAcc)) {
				showAlert(getString(R.string.alert_107));
			} else if (strAmount.length() == 0) {
				showAlert(getString(R.string.alert_033));
			} else if (Double.parseDouble(strAmount) == 0) {
				showAlert(getString(R.string.alert_034));
			} else if (strRemark.length() == 0) {
				showAlert(getString(R.string.alert_035));
			} else if (Double.parseDouble(strAmount) > balance) {
				showAlert(getString(R.string.alert_176));
			} else {
				try {
					this.flag = libObj.chkConnectivity();
					if (this.flag == 0) {
						CallWebServiceGetSrvcCharg c = new CallWebServiceGetSrvcCharg();
						c.execute();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else if (v.getId() == R.id.btn_confirm) {
			if (strAmount.length() == 0) {
				strAmount = "0";
				retMess = getString(R.string.alert_033);
				showAlert(retMess);// setAlert();
				txtAmt.requestFocus();
			} else {
				if (Double.parseDouble(strAmount) <= 0) {
					retMess = getString(R.string.alert_034);
					showAlert(retMess);// setAlert();
					txtAmt.requestFocus();
				} else {
					if (strRemark.length() > 200) {
						retMess = getString(R.string.alert_097);
						showAlert(retMess);// setAlert();
						txtRemk.requestFocus();
					} else if (strToAccNo.length() == 0) {
						retMess = getString(R.string.alert_067);
						showAlert(retMess);// setAlert();
					} else {
						//saveData();
						InputDialogBox inputBox = new InputDialogBox(act);
						inputBox.show();
					} // end else
				}
			}// end if
		}

	}// end click

	class CallWebServicelog extends AsyncTask<Void, Void, Void> {
		JSONObject jsonObj = new JSONObject();
		String ValidationData = "";
		LoadProgressBar loadProBarObj = new LoadProgressBar(act);

		@Override
		protected void onPreExecute() {
			try {
				loadProBarObj.show();
				respcode = "";
				retvalwbs = "";
				respdesc = "";
				Log.e("@DEBUG", "LOGOUT preExecute()");
				jsonObj.put("CUSTID", custId);
				jsonObj.put("IMEINO", MBSUtils.getImeiNumber(act));
				jsonObj.put("SIMNO", MBSUtils.getSimNumber(act));
				jsonObj.put("METHODCODE", "29");
				//  ValidationData=MBSUtils.getValidationData(act,jsonObj.toString());

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

	public void post_successlog(String retvalwbs) {
		respcode = "";
		respdesc = "";
		act.finish();
		System.exit(0);

	}


	public void setAlert() 
	{
		showAlert(retMess);
	}// end setAlert

	public void saveData() 
	{
		String accNo = txtAccNo.getText().toString().trim();
		String debitAccno = arrListTemp.get(spi_debit_account
				.getSelectedItemPosition());
		String benAcNo = spi_sel_beneficiery.getItemAtPosition(
				spi_sel_beneficiery.getSelectedItemPosition()).toString();
		String amt = txtAmt.getText().toString().trim();
		String reMark = txt_remark.getText().toString().trim();

		String charges = txt_charges.getText().toString().split(" ")[1];
		String drAccNo = txt_from.getText().toString().trim();

		JSONObject obj = new JSONObject();
		try 
		{

			obj.put("SIMNO", MBSUtils.getMyPhoneNO(act));
			obj.put("BENFSRNO", benSrno);
			obj.put("CRACCNO", creditAcc);
			obj.put("DRACCNO", debitAccno);
			obj.put("AMOUNT", amt);
			obj.put("REMARK", reMark);
			obj.put("TRANSFERTYPE", "OWN");
			obj.put("IMEINO", MBSUtils.getImeiNumber(act));
			obj.put("CUSTID", custId);
			obj.put("CHARGES", charges);
			obj.put("CHRGACCNO", chrgCrAccNo);
			obj.put("TRANID", tranId);
			obj.put("SERVCHRG", "0");
			obj.put("CESS", "0");
			obj.put("TRANPIN", Tranmpin);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		Bundle bundle=new Bundle();
		Fragment fragment = new TransferOTP(act);
		bundle.putString("CUSTID", custId);
		bundle.putString("FROMACT", "OWNBANK");
		bundle.putString("JSONOBJ", obj.toString());
		fragment.setArguments(bundle);
		FragmentManager fragmentManager = act.getFragmentManager();
		fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
	}// end saveData

	// innser class
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
			try {
				String str = mpin.getText().toString().trim();
				Tranmpin=str;
				encrptdTranMpin = ListEncryption.encryptData(custId + str);
				if (str.length() == 0) {
					retMess = getString(R.string.alert_116);
					showAlert(retMess);// setAlert();
					this.show();
				}
//				else if (str.length() != 6) {
//					retMess = getString(R.string.alert_037);
//					showAlert(retMess);// setAlert();
//					this.show();
//				}
				else {
					//saveData();
					new callValidateTranpinService().execute();
					this.hide();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}// end onClick
	}// end InputDialogBox

	public void showAlert(String str) {
		ErrorDialogClass alert = new ErrorDialogClass(act, "" + str) {
			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.btn_ok:
					if (this.textMessage.equalsIgnoreCase(act
							.getString(R.string.alert_125_1))) {
						//saveData();
						/*InputDialogBox inputBox = new InputDialogBox(act);
						inputBox.show();*/
					} else {
						if (noAccounts) {
							if (same_bnk_layout.getVisibility() == View.VISIBLE) {
								Fragment fragment = new FundTransferMenuActivity(
										act);
								FragmentManager fragmentManager = ownBnkTran
										.getFragmentManager();
								fragmentManager
										.beginTransaction()
										.replace(R.id.frame_container, fragment)
										.commit();
								act.frgIndex = 5;
							} else if (confirm_layout.getVisibility() == View.VISIBLE) {
								confirm_layout
										.setVisibility(confirm_layout.INVISIBLE);
								same_bnk_layout
										.setVisibility(same_bnk_layout.VISIBLE);
								act.frgIndex = 51;
							}
						}
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

	public void clearFields() {
		txtAccNo.setText("");
		spi_debit_account.setSelection(0);
		spi_sel_beneficiery.setSelection(0);
		txtAmt.setText("");
		txtRemk.setText("");
	}

	class CallWebServiceGetSrvcCharg extends AsyncTask<Void, Void, Void> 
	{
		LoadProgressBar loadProBarObj = new LoadProgressBar(act);
		String ValidationData="";
		String accNo, debitAccno, benAcNo, amt, reMark;
		JSONObject obj = new JSONObject();

		protected void onPreExecute() {
			loadProBarObj.show();
			debitAccno = arrListTemp.get(spi_debit_account.getSelectedItemPosition());
			benAcNo = spi_sel_beneficiery.getItemAtPosition(spi_sel_beneficiery.getSelectedItemPosition()).toString();
			
			amt = txtAmt.getText().toString().trim();
			reMark = txtRemk.getText().toString().trim();
	
			
			try 
			{
				obj.put("SIMNO", MBSUtils.getMyPhoneNO(act));
				obj.put("CUSTID", custId);
				obj.put("TRANTYPE", "OWN");
				obj.put("DRACCNO", debitAccno);
				obj.put("AMOUNT", amt);
				obj.put("CRACCNO", creditAcc);
				obj.put("IMEINO", MBSUtils.getImeiNumber(act));
				
				obj.put("METHODCODE","28");
				//ValidationData=MBSUtils.getValidationData(act,obj.toString());
			} 
			catch (JSONException e) 
			{
				e.printStackTrace();
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
			// jsonObj = new JSONObject(str.trim());
			String decryptedAccounts = str.trim();
			if (decryptedAccounts.indexOf("SUCCESS") > -1) 
			{
				
				act.frgIndex = 52;
				loadProBarObj.dismiss();
				same_bnk_layout.setVisibility(same_bnk_layout.INVISIBLE);
				confirm_layout.setVisibility(confirm_layout.VISIBLE);

				String retStr = str.split("~")[1];
				String tranType = "INTBANK";
				String[] val = retStr.split("#");
				txt_heading.setText("Confirmation");
				txt_remark.setText(strRemark);
				txt_from.setText(strFromAccNo);
				txt_to.setText(strToAccNo);
				txt_amount.setText("INR " + strAmount);
				txt_charges.setText("INR " + val[0]);
				txt_trantype.setText(tranType);
				chrgCrAccNo = val[1];
				tranId = val[2];

				if (chrgCrAccNo.length() == 0 || chrgCrAccNo.equalsIgnoreCase("null"))
					chrgCrAccNo = "";
			} 
			else 
			{
				if (decryptedAccounts.indexOf("LIMIT_EXCEEDS") > -1) 
				{
					retMess = getString(R.string.alert_031);
					loadProBarObj.dismiss();
					showAlert(retMess);// setAlert();
				} 
				else if (decryptedAccounts.indexOf("LOWBALANCE") > -1) 
				{
					retMess = getString(R.string.alert_176);
					loadProBarObj.dismiss();
					showAlert(retMess);
				}
				else if (decryptedAccounts.indexOf("SingleLimitExceeded") > -1) 
				{
					retMess = getString(R.string.alert_signledaylmt);
					loadProBarObj.dismiss();
					showAlert(retMess);
				}
				else if (decryptedAccounts.indexOf("TotalLimitExceeded") > -1) 
				{
					retMess = getString(R.string.alert_194);
					loadProBarObj.dismiss();
					showAlert(retMess);
				}else if (decryptedAccounts.indexOf("STOPTRAN") > -1)
				{
					retMess = getString(R.string.Stop_Tran);
					//loadProBarObj.dismiss();
					showAlert(retMess);
				}
				else 
				{
					retMess = getString(R.string.alert_032);
					loadProBarObj.dismiss();
					showAlert(retMess);// setAlert();
				}
			}// end else
		}// end onPostExecute
	}// end CallWebServiceGetSrvcCharg

}// end class
package shree_nagari.mbank;

import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.regex.Pattern;

import javax.crypto.spec.SecretKeySpec;

import mbLib.CryptoClass;
import mbLib.CryptoUtil;
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
import android.content.SharedPreferences.Editor;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

public class EditSameBankBeneficiary extends Fragment implements
		OnClickListener {
	MainActivity act;
	EditSameBankBeneficiary editSameBnkBenf;
	ProgressBar p_wait;
	EditText txtAccNo, txtName, txtmobNo, txtEmail,txtNick_Name2;
	TextView txt_heading;
	Button btn_submit, btn_fetchname;
	ImageButton btn_home, spinner_btn;// , btn_back
	Spinner spi_sel_beneficiery;
	ImageView img_heading;
	DatabaseManagement dbms;
	boolean WSCalled = false;
	PrivateKey var1=null;	  
	String var5="",var3="";
	SecretKeySpec var2=null;
	Editor e;
	private String benInfo = "";
	private static String NAMESPACE = "";
	private static String URL = "";
	private static String SOAP_ACTION = "";
	private static  String METHOD_NAME1 = "";
	private static  String METHOD_NAME = "";
	private static  String METHOD_NAME2 = "";
	private static final String MY_SESSION = "my_session";

	int cnt = 0, flag = 0;
	String flg = "false";
	String benAccountNumber = "", benAccountName = "";
	String custId = "", accNo = "", accNm = "", mobNo = "", nickNm = "",
			mailId = "";
	String str = "", retMess = "", cust_name = "", tmpXMLString = "",
			retVal = "", userId = "";
	String mobPin = "", benNickname = "", when_fetch = "", benSrno = null,respcode="",reTval="",getBeneficiariesrespdesc="",GetAccountInforespdesc="",saveBeneficiariesrespdesc="";
	public String encrptdMpin,Mpin="";
	/*
	 * Typeface tf_calibri = Typeface.createFromAsset(act.getAssets(),
	 * "fonts/Kozuka-Gothic-Pro-M_26793.ttf");
	 */
	DialogBox dbs;

	public EditSameBankBeneficiary() {
	}

	@SuppressLint("ValidFragment")
	public EditSameBankBeneficiary(MainActivity a) {
		// System.out.println("EditSameBankBeneficiary()" + a);
		act = a;
		editSameBnkBenf = this;
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.edit_samebank_beneficiary,
				container, false);
		var1 = act.var1;
		var3 = act.var3;
		img_heading = (ImageView) rootView.findViewById(R.id.img_heading);
		img_heading.setBackgroundResource(R.mipmap.benefeciary);
		dbms = new DatabaseManagement("shree_nagari.mbank", "listMobileBanking");
		txtAccNo = (EditText) rootView.findViewById(R.id.txtAccNo2);
		btn_fetchname = (Button) rootView.findViewById(R.id.btn_fetchName2);
		txtName = (EditText) rootView.findViewById(R.id.txtName2);
		txtmobNo = (EditText) rootView.findViewById(R.id.txtmobNo2);
		txtEmail = (EditText) rootView.findViewById(R.id.txtEmail2);
		btn_submit = (Button) rootView.findViewById(R.id.btn_submit2);
		p_wait = (ProgressBar) rootView.findViewById(R.id.pro_bar);
		btn_home = (ImageButton) rootView.findViewById(R.id.btn_home);
		/* btn_back = (ImageButton) rootView.findViewById(R.id.btn_back); */
		txtNick_Name2=(EditText) rootView.findViewById(R.id.txtNick_Name2);
		// btn_home.setImageResource(R.drawable.ic_home_d);
		// btn_back.setImageResource(R.drawable.backover);
		txt_heading = (TextView) rootView.findViewById(R.id.txt_heading);
		txt_heading.setText(getString(R.string.frmtitle_edit_same_bnk_bnf));
		// btn_back.setOnClickListener(this);
		btn_home.setOnClickListener(this);
		p_wait.setMax(10);
		p_wait.setProgress(1);
		btn_fetchname.setOnClickListener(this);
		btn_submit.setOnClickListener(this);

		spi_sel_beneficiery = (Spinner) rootView
				.findViewById(R.id.sameBnkTranspi_sel_beneficiery);
		spinner_btn = (ImageButton) rootView.findViewById(R.id.spinner_btn);
		spinner_btn.setOnClickListener(this);

		this.flag = chkConnectivity();
		if (this.flag == 0) {
			// SharedPreferences sp = act.getSharedPreferences(MY_SESSION,
			Cursor c1 = dbms.selectFromTable("SHAREDPREFERENCE", "", null); // Context.MODE_PRIVATE);
			if (c1 != null) // e = sp.edit();
			{
				while (c1.moveToNext()) {
					custId = c1.getString(2);// sp.getString("custId",
												// "custId");
					// mobPin = sp.getString("pin", "pin");
					// userId = sp.getString("userId", "userId");
					Log.e("*********** custId:", custId);
				} // Log.e("*********** mobPin:", mobPin);
			}
			new CallWebService_fetch_all_beneficiaries().execute();
		}
		// SharedPreferences sp = act.getSharedPreferences(MY_SESSION,
		Cursor c1 = dbms.selectFromTable("SHAREDPREFERENCE", "", null);// Context.MODE_PRIVATE);
		// e = sp.edit();
		// custId = sp.getString("custId", "custId");
		if (c1 != null) {
			while (c1.moveToNext()) {
				custId = c1.getString(2);
				Log.e("custId", "......" + custId);
			}
		}
		/*
		 * txtAccNo.addTextChangedListener(new TextWatcher() {
		 * 
		 * public void afterTextChanged(Editable s) { // TODO Auto-generated
		 * method stub if (txtAccNo.getText().toString().trim()
		 * .equalsIgnoreCase(benAccountNumber)) {
		 * txtName.setText(benAccountName); //txtName.setEnabled(true);
		 * btn_fetchname.setEnabled(false);
		 * Log.e("EDITSAMEBAKBENF","account field has value"); } else {
		 * txtName.setText(""); //txtName.setEnabled(true);
		 * btn_fetchname.setEnabled(true);
		 * Log.e("EDITSAMEBAKBENF","account field blank"); }
		 * Log.e("EDITSAMEBAKBENF","account field changed");
		 * 
		 * }
		 * 
		 * @Override public void beforeTextChanged(CharSequence s, int start,
		 * int count, int after) { // TODO Auto-generated method stub }
		 * 
		 * @Override public void onTextChanged(CharSequence s, int start, int
		 * before, int count) { }
		 * 
		 * });
		 */

		// Beneficiary on select
		spi_sel_beneficiery
				.setOnItemSelectedListener(new OnItemSelectedListener() {
					String benMobNo = "";
					String benEmail = "";
                    String niknm="";
					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {

						// System.out.println("spi_sel_beneficiery.getSelectedItemPosition() ===="+
						// spi_sel_beneficiery.getSelectedItemPosition());
						String str = spi_sel_beneficiery.getItemAtPosition(
								spi_sel_beneficiery.getSelectedItemPosition())
								.toString();
						// System.out.println("selected
						// benefic////System.outstr);
						if (str.equalsIgnoreCase("Select Beneficiary")) {
							txtAccNo.setText("");
							txtName.setText("");
							txtmobNo.setText("");
							txtEmail.setText("");
							txtAccNo.setEnabled(false);
							txtName.setEnabled(false);
							txtmobNo.setEnabled(false);
							txtEmail.setEnabled(false);
							btn_fetchname.setEnabled(false);
							Log.e("EDITSAMEBAKBENF", "text field disabled");
						} else {
							txtAccNo.setEnabled(true);
							txtName.setEnabled(true);
							txtmobNo.setEnabled(true);
							txtEmail.setEnabled(true);
							btn_fetchname.setEnabled(true);
							Log.e("EDITSAMEBAKBENF", "text field enabled");
							String allStr[] = benInfo.split("~");

							for (int i = 1; i <= allStr.length; i++) {
								String str1[] = allStr[i - 1].split("#");
								niknm= str1[2] + "(" + str1[1] + ")";
								// if (str.indexOf("(" + str1[1] + ")") > -1) {
								if (str.equalsIgnoreCase(niknm)){//indexOf(str1[2]) > -1) {
									// //System.out.println("========== inside if ============");
									benSrno = str1[0];
									benAccountName = str1[1];
									benNickname = str1[2];
									benAccountNumber = str1[3];
									benMobNo = str1[6];
									benEmail = str1[7];

									if (str1[7].equalsIgnoreCase("NA")) {
										benEmail = "";
									}

									if (str1[6].equalsIgnoreCase("NA")) {
										benMobNo = "";
									}
									txtAccNo.setText(benAccountNumber);
									txtName.setText(benAccountName);
									txtmobNo.setText(benMobNo);
									txtEmail.setText(benEmail);
									txtNick_Name2.setText(benNickname);
									// txtNick_Name.setText(benNickname);
								}

							}// end for
						}

					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
					}
				});// end spi_sel_beneficiery

		return rootView;
	}

	private void addBeneficiaries(String retval) {
		// //System.out.println("================ IN addBeneficiaries() of OtherBankTransfer ======================");
		// System.out.println("SameBankTransfer IN addBeneficiaries()" +
		// retval);

		try {
			// String[] tempstr=retval.split("#");
			// retval=tempstr[1];
			// System.o////System.out("*****retval in addBeneficiaries:"+retval);
			ArrayList<String> arrList = new ArrayList<String>();
			String allstr[] = retval.split("~");
			// System.out.println("OtherBankTranIMPS Accounts:::" + allstr[1]);
			// int noOfAccounts = str1.length;
			int noOfben = allstr.length;
			// System.out.println("SameBankTransfer noOfben:" + noOfben);
			String benName = "";
			arrList.add("Select Beneficiary");
			for (int i = 1; i <= noOfben; i++) {
				String[] str2 = allstr[i - 1].split(Pattern.quote("#"));
				Log.e("Benificiary", allstr[i - 1] + "      Length="
						+ str2.length);
				benName = str2[2] + "(" + str2[1] + ")";
				arrList.add(benName);
				// System.out.println("=============== benificiary Name is:======"+
				// benName);
			}
			// spi_sel_beneficiery
			// System.out.println("================ IN addBeneficiaries() 1 ======================");
			/*
			 * ArrayAdapter<String> arrAdpt = new
			 * ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,
			 * arrList); System.out.println(
			 * "================ IN addBeneficiaries() 2 ======================"
			 * ); arrAdpt.setDropDownViewResource(android.R.layout.
			 * simple_spinner_dropdown_item); System.out.println(
			 * "================ IN addBeneficiaries() 3 ======================"
			 * ); spi_sel_beneficiery.setAdapter(arrAdpt);
			 */
			String[] benfArr = new String[arrList.size()];
			benfArr = arrList.toArray(benfArr);
			/*
			 * CustomeSpinnerAdapter accs = new CustomeSpinnerAdapter(act,
			 * R.layout.spinner_layout, benfArr);
			 */
			ArrayAdapter<String> accs = new ArrayAdapter<String>(act,R.layout.spinner_item, benfArr);
			/*CustomeSpinnerAdapter accs = new CustomeSpinnerAdapter(act,
					android.R.layout.simple_spinner_item, benfArr);*/
			accs.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spi_sel_beneficiery.setAdapter(accs);
		} catch (Exception e) {
			Log.e("IN ADDBenificiary", "" + e);
			e.printStackTrace();
		}
	}// end addBeneficiaries

	public void initAll() {
		txtAccNo.setText("");
		txtName.setText("");
		txtmobNo.setText("");
		txtEmail.setText("");
	}

	public void showAlert(final String str) {
		ErrorDialogClass alert = new ErrorDialogClass(act, "" + str) {
			@Override
			public void onClick(View v) {
				super.onClick(v);
				if((str.equalsIgnoreCase(getBeneficiariesrespdesc)) &&  (respcode.equalsIgnoreCase("0")))
				{
					post_successfetch_all_beneficiaries(reTval);
				}
				else if((str.equalsIgnoreCase(getBeneficiariesrespdesc)) && (respcode.equalsIgnoreCase("1")))
				{
					this.dismiss();
				}
				else if((str.equalsIgnoreCase(GetAccountInforespdesc)) &&  (respcode.equalsIgnoreCase("0")))
				{
					post_successfetch_ac_holdernm(reTval);
				}
				else if((str.equalsIgnoreCase(GetAccountInforespdesc)) && (respcode.equalsIgnoreCase("1")))
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
				else if (act.getString(R.string.alert_125).equalsIgnoreCase(
						textMessage)) {
					SaveBeneficiary();
				}
				else if(flg == "true"){
					Log.e("Inside If", "Inside if===" + flg);
					Log.e("Inside If", "Inside if===" + flg);
					Log.e("Inside If", "Inside if===" + flg);
					switch (v.getId()) {
					
					case R.id.btn_ok:
						// if (WSCalled) {
						/* if (flg == "true") 
						{*/
						Fragment fragment = new ManageBeneficiaryMenuActivity(
								act);
						FragmentManager fragmentManager = getFragmentManager();
						fragmentManager.beginTransaction()
								.replace(R.id.frame_container, fragment)
								.commit();
						this.dismiss();
					 /* }
						else
						{
					 this.dismiss();
						}*/
					}
					
				}
				else
				{
			 this.dismiss();
				}
				

			}
		};
		alert.show();
	}

	public void onClick(View v) {
		// TODO Auto-generated method stub
		// pb_wait.setVisibility(ProgressBar.VISIBLE);
		switch (v.getId()) {
		
		case R.id.btn_home:
			Intent in = new Intent(act, NewDashboard.class);
			in.putExtra("VAR1", var1);
			in.putExtra("VAR3", var3);
			startActivity(in);
			act.finish();
			break;
		case R.id.spinner_btn:

			spi_sel_beneficiery.performClick();
			break;

		case R.id.btn_fetchName2:
			when_fetch = "EXPLICIT";
			accNo = txtAccNo.getText().toString().trim();
			Log.i("mayuri", "accNo :" + accNo);

			if (accNo.equalsIgnoreCase("")) {
				// ///retMess = "Please Enter Account Number.";
				retMess = getString(R.string.alert_001);
				showAlert(retMess);
				txtAccNo.requestFocus();
			} else {
				// flag = chkConnectivity();
				// if (flag == 0)
				{
					CallWebService_fetch_ac_holdernm C = new CallWebService_fetch_ac_holdernm();
					C.execute();
				}
			}
			break;

		case R.id.btn_submit2:
			String str = spi_sel_beneficiery.getItemAtPosition(
					spi_sel_beneficiery.getSelectedItemPosition()).toString();
			accNo = txtAccNo.getText().toString().trim();
			accNm = txtName.getText().toString().trim();
			mobNo = txtmobNo.getText().toString().trim();
			//nickNm = benNickname;
			nickNm=txtNick_Name2.getText().toString().trim();
			mailId = txtEmail.getText().toString().trim();
			Log.e("mobNo==","mobNo=="+ mobNo);
			Log.e("mobNo==","mobNo=="+ mobNo);
			Log.e("mobNo==","mobNo length=="+ mobNo.length());
			Log.e("mobNo==","mobNo length=="+ mobNo.length());

			if (str.equalsIgnoreCase("Select Beneficiary")) {
				retMess = getString(R.string.alert_098);
				flg = "false";
				showAlert(retMess);
			} else if (accNo.length() == 0) {
				retMess = getString(R.string.alert_001);
				flg = "false";
				showAlert(retMess);
				txtAccNo.requestFocus();
			} else if (accNo.length() != 16) {
				retMess = getString(R.string.alert_175);
				flg = "false";
				showAlert(retMess);
				txtAccNo.requestFocus();
			}
			else if (mobNo.length()>0 && (!MBSUtils.validateMobNo(mobNo))) 
			{
				flg = "false";
				retMess = getString(R.string.alert_006);
				showAlert(retMess);
				txtmobNo.requestFocus();

			}  else if (mailId.length() > 0 && !MBSUtils.validateEmail(mailId)) {
				retMess = getString(R.string.alert_007);
				flg = "false";
				showAlert(retMess);
				txtEmail.requestFocus();
	
			} else if (accNm.length() == 0) {
				retMess = getString(R.string.alert_096);
				flg = "false";
				showAlert(retMess);
				txtName.requestFocus();
			} else if (accNm.length() > 100) {
				retMess = getString(R.string.alert_095);
				flg = "false";
				showAlert(retMess);
				txtName.requestFocus();
			}
			else if (nickNm.trim().length() == 0) 
			{
				//saveFlag = "ERR";
				flg = "false";
				retMess = getString(R.string.alert_003);
				showAlert(retMess);
				txtNick_Name2.requestFocus();
			}
			else if (nickNm.trim().length() < 4 || nickNm.trim().length() > 15) 
			{
				flg = "false";
				retMess = getString(R.string.alert_005);
				//saveFlag = "ERR";
				showAlert(retMess);
				txtNick_Name2.requestFocus();

			} 
			
			/*
			 * else if(nickNm.equalsIgnoreCase("")) { retMess =
			 * "Please Enter Nickname."; setAlert();
			 * txtNick_Name.requestFocus(); }
			 */
			/*
			 * else if(accNm.length() == 0) { //flag = chkConnectivity();
			 * 
			 * //if (flag == 0) { when_fetch = "AUTO";
			 * CallWebService_fetch_ac_holdernm C=new
			 * CallWebService_fetch_ac_holdernm(); C.execute(); //setAlert(); }
			 * }
			 */
			else {
				flag = chkConnectivity();

				if (flag == 0) {
					InputDialogBox inputBox = new InputDialogBox(act);
					inputBox.show();
				}
				/*
				 * showAlert(act.getString(R.string.alert_017)); initAll();
				 */
			}
			break;

		default:
			break;
		}
	}// end onClick

	public void SaveBeneficiary() {
		// flag = chkConnectivity();

		// if (flag == 0)
		{
			// SharedPreferences sp = act.getSharedPreferences(MY_SESSION,
			// Context.MODE_PRIVATE);
			// e = sp.edit();
			// custId = sp.getString("custId", "custId");
			// mobPin = sp.getString("pin", "pin");
			Cursor c1 = dbms.selectFromTable("SHAREDPREFERENCE", "", null);// ("select * from ",
																			// null);
			if (c1 != null) {
				while (c1.moveToNext()) {
					custId = c1.getString(2);
					Log.e("custId", "......" + custId);
				}
			}

			InputDialogBox inputBox = new InputDialogBox(act);
			inputBox.show();

		}

	}

	// this webservice to call and add all beniferies
	class CallWebService_fetch_all_beneficiaries extends
			AsyncTask<Void, Void, Void> {

		String retval = "";
		LoadProgressBar loadProBarObj = new LoadProgressBar(act);
		JSONObject jsonObj = new JSONObject();
		String ValidationData="";

		protected void onPreExecute() {
			try {
				respcode="";
				reTval="";
				getBeneficiariesrespdesc="";
				loadProBarObj.show();
				jsonObj.put("CUSTID", custId);
				jsonObj.put("SAMEBNK", "Y");
				jsonObj.put("IMEINO", MBSUtils.getImeiNumber(act));
				jsonObj.put("SIMNO", MBSUtils.getSimNumber(act));
				jsonObj.put("METHODCODE","13");
				//ValidationData=MBSUtils.getValidationData(act,jsonObj.toString());
			} catch (JSONException je) {
				je.printStackTrace();
			}
	
		}// end onPreExecute

		protected Void doInBackground(Void[] paramArrayOfVoid) {
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
				return null;
			}// end try
			catch (Exception e) {
				// e.printStackTrace();
				// System.out.println("Exception 2");
				System.out.println("SameBankTransfer   Exception" + e);
			}
			return null;
		}// end dodoInBackground

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
					reTval = jsonObj.getString("RETVAL");
				}
				else
				{
					reTval = "";
				}
				if (jsonObj.has("RESPDESC"))
				{
					getBeneficiariesrespdesc = jsonObj.getString("RESPDESC");
				}
				else
				{	
					getBeneficiariesrespdesc = "";
				}
				
			if(getBeneficiariesrespdesc.length()>0)
			{
				showAlert(getBeneficiariesrespdesc);
			}
			else{
			if (reTval.indexOf("SUCCESS") > -1) {
				post_successfetch_all_beneficiaries(reTval);
				
				
			} else {
				// this case consider when in retval string contains only
				// "FAILED"
				retMess = getString(R.string.alert_041);
				//loadProBarObj.dismiss();
				flg = "true";
				showAlert(retMess);
				// System.out.println("================== in onPostExecute 2 ============================");
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

	}// end callWbService

	public 	void post_successfetch_all_beneficiaries(String reTval)
	{
		respcode="";
		getBeneficiariesrespdesc="";
		//flg = "true";
		// stem.out.println("================== in onPostExecute 1 ============================");
		// ystem.out.println("================== in onPostExecute 2 ============================");
		String decryptedBeneficiaries = reTval
				.split("SUCCESS~")[1];
		Log.e("OMKAR BENEFICIEARIES", decryptedBeneficiaries);
		benInfo = decryptedBeneficiaries;
		addBeneficiaries(decryptedBeneficiaries);
		
	}
	// Fetch account name
	class CallWebService_fetch_ac_holdernm extends AsyncTask<Void, Void, Void> {

		LoadProgressBar loadProBarObj = new LoadProgressBar(act);
		JSONObject jsonObj = new JSONObject();
		String ValidationData="";

		@Override
		protected void onPreExecute() {
			try {
				respcode="";
				reTval="";
				GetAccountInforespdesc="";
				// p_wait.setVisibility(ProgressBar.VISIBLE);
				loadProBarObj.show();

				String accNo = txtAccNo.getText().toString().trim();

				jsonObj.put("ACCNO", accNo);
				jsonObj.put("CUSTID", custId);
				jsonObj.put("SIMNO", MBSUtils.getSimNumber(act));
				jsonObj.put("METHODCODE","18");
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
				// retMess = "Error Getting IMEI NO";
				retMess = getString(R.string.alert_000);
				System.out.println(e.getMessage());
				cnt = 0;
			}
			return null;
		}

		@Override
		protected void onPostExecute(final Void result) {
			Intent intent = null;
			
			String[] xml_data = CryptoUtil.readXML(retVal.split("~")[1], new String[]{"PARAMS","CHECKSUM"});
		
			loadProBarObj.dismiss();
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
					reTval = jsonObj.getString("RETVAL");
				}
				else
				{
					reTval = "";
				}
				if (jsonObj.has("RESPDESC"))
				{
					GetAccountInforespdesc= jsonObj.getString("RESPDESC");
				}
				else
				{	
					GetAccountInforespdesc= "";
				}
				
			if(GetAccountInforespdesc.length()>0)
			{
				showAlert(GetAccountInforespdesc);
			}
			else{
			if (reTval.indexOf("FAILED") > -1) {

				if (reTval.indexOf("NOT_EXISTS") > -1) {
					// ///retMess = "Invalid Account Number.";
					retMess = getString(R.string.alert_008);
					txtAccNo.requestFocus();
					showAlert(retMess);
				} else if (reTval.indexOf("EXISTS") > -1) {
					// ////retMess = "This Beneficiary Is Already Added.";
					retMess = getString(R.string.alert_009);
					txtAccNo.requestFocus();
					showAlert(retMess);
				} else {
					// ////retMess = "Network Unavailable. Please Try Again.";
					retMess = getString(R.string.alert_000);
					txtAccNo.requestFocus();
					showAlert(retMess);
				}

			} else {
				post_successfetch_ac_holdernm(reTval);
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
		}
	}
	public 	void post_successfetch_ac_holdernm(String reTval)
	{
		respcode="";
		GetAccountInforespdesc="";
		Log.i("mayuri success", "success");

		Bundle b = new Bundle();
		// add data to bundle
		Log.i("Return value:", retVal);
		// retVal=""SUCCESS~KAVITA KIRAN KADEKAR";
		Log.i("mayuri success", "success" + retVal.split("~")[1]);
		// String[] xml_data = CryptoUtil.readXML(retVal.split("~")[1],
		// xmlTags);
		//System.out.println("xml_data.len :" + reTval.length);
		// String decryptedAccName = xml_data[0];
		System.out.println("decrypted Acc holder Name :"
				+ reTval);

		// String decryptedAccName = "KAVITA KIRAN KADEKAR";
		// txtName.setText(decryptedAccName);
		// txtName.setEnabled(true);
		String acno = txtAccNo.getText().toString().trim();
		if (when_fetch == "AUTO") {
			dbs = new DialogBox(act);

			dbs.get_adb().setMessage(
					"Continue With Name \"" + reTval
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
	// Save Beneficiary
	class CallWebService_save_beneficiary extends AsyncTask<Void, Void, Void> {
		LoadProgressBar loadProBarObj = new LoadProgressBar(act);
		JSONObject jsonObj = new JSONObject();
		String ValidationData="";

		@Override
		protected void onPreExecute() {
			try {
				 respcode="";
            	 reTval="";
            	 saveBeneficiariesrespdesc="";
				// p_wait.setVisiility(ProgressBar.VISIBLE);
				loadProBarObj.show();
				jsonObj.put("CUSTID", custId.trim());
				jsonObj.put("ACCNO", accNo.trim());
				jsonObj.put("ACCNM", accNm.trim());
				jsonObj.put("MOBNO", mobNo.trim());
				jsonObj.put("NICKNM", nickNm.trim());
				jsonObj.put("MAILID", mailId.trim());
				jsonObj.put("TRANSFERTYPE", "Y");
				jsonObj.put("IFSCCD", "DUMMY");
				jsonObj.put("MMID", "DUMMY");
				jsonObj.put("IINSERTUPDTDLT", "U");
				jsonObj.put("BENSRNO", benSrno.trim());
				jsonObj.put("IMEINO", MBSUtils.getImeiNumber(act));
				jsonObj.put("MPIN", Mpin);
				jsonObj.put("SIMNO", MBSUtils.getSimNumber(act));
				jsonObj.put("METHODCODE","14");
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
				} 
				 catch (Exception e) {
					e.printStackTrace();
						retMess = getString(R.string.alert_000);
					System.out.println("Exception");
					cnt = 0;
				}
			} catch (Exception e) {
				// retMess = "Error Getting IMEI NO";
				Log.i("Exception2 ", "" + e);
				retMess = getString(R.string.alert_000);
				System.out.println(e.getMessage());
				cnt = 0;
			}
			return null;
		}

		@Override
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
			// retVal="SUCCESS~KAVITA KIRAN KADEKAR";
			Log.i("mayuri retVal", "retVal :" + retVal);
			if (reTval.indexOf("FAILED") > -1) {
				txtAccNo.setFocusableInTouchMode(true);
				txtAccNo.requestFocus();
				cnt = 0;
				flg = "false";
				
				if (reTval.indexOf("WRONGMPIN") > -1) {
					
					retMess = getString(R.string.alert_125);
					showAlert(retMess);
				}
				else if (reTval.indexOf("DUPLICATENICKNAME") > -1) {
							retMess = getString(R.string.alert_011);

					showAlert(retMess);
				} else {
					retMess = getString(R.string.alert_028_1);
					//loadProBarObj.dismiss();
					showAlert(retMess);
					//initAll();

				}

			} else {
				post_successsaveBeneficiaries(reTval);
			}

			// p_wait.setVisibility(ProgressBar.INVISIBLE);

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
	public 	void post_successsaveBeneficiaries(String reTval)
	{
		respcode="";
   	 saveBeneficiariesrespdesc="";
		WSCalled = true;
		retMess = getString(R.string.alert_017);
		flg = "true";
		showAlert(retMess);
		
	}

	public int chkConnectivity() {
		// pb_wait.setVisibility(ProgressBar.VISIBLE);
		ConnectivityManager cm = (ConnectivityManager) act
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		try {
			State state = ni.getState();
			boolean state1 = ni.isAvailable();
			System.out
					.println("editSameBankBeneficiary	in chkConnectivity () state1 ---------"
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
					// /////retMess =
					// "Network Disconnected. Please Check Network Settings.";
					retMess = getString(R.string.alert_014);
					showAlert(retMess);
					break;
				default:
					flag = 1;
					// ///////retMess =
					// "Network Unavailable. Please Try Again.";
					retMess = getString(R.string.alert_000);
					// setAlert();

					showAlert(retMess);
					break;
				}
			} else {
				flag = 1;
				// //////retMess = "Network Unavailable. Please Try Again.";
				retMess = getString(R.string.alert_000);
				// setAlert();

				showAlert(retMess);
			}
		} catch (NullPointerException ne) {

			Log.i("",
					"NullPointerException Exception" + ne);
			flag = 1;
			// ////retMess = "Network Unavailable. Please Try Again.";
			retMess = getString(R.string.alert_000);
			// setAlert();

			showAlert(retMess);

		} catch (Exception e) {
			Log.i("editSame", "Exception" + e);
			flag = 1;
			// ///retMess = "Network Unavailable. Please Try Again.";
			retMess = getString(R.string.alert_000);
			// setAlert();
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
					// ////retMess = "Enter Valid MPIN.";
					retMess = getString(R.string.alert_015);
					showAlert(retMess);
					mpin.setText("");
				} else {// if (encrptdMpin.equalsIgnoreCase(mobPin)
					// || mobPin.equalsIgnoreCase(encrptdUpin)) {
					// SharedPreferences sp =
					// act.getSharedPreferences(MY_SESSION,
					// Context.MODE_PRIVATE);
					// e = sp.edit();
					// custId = sp.getString("custId", "custId");

					Cursor c1 = dbms.selectFromTable("SHAREDPREFERENCE", "",
							null);// ("select * from ", null);
					if (c1 != null) {
						while (c1.moveToNext()) {
							custId = c1.getString(2);
							Log.e("custId", "......" + custId);
						}
					}
					callValidateTranpinService C = new callValidateTranpinService();
					//CallWebService_save_beneficiary C = new CallWebService_save_beneficiary();
					C.execute();
					this.hide();
				} /*
				 * else { this.hide(); retMess = getString(R.string.alert_125);
				 * showAlert(retMess); mpin.setText(""); }
				 */
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
				jsonObj.put("ACCNM", accNm.trim());
				jsonObj.put("MOBNO", mobNo.trim());
				jsonObj.put("NICKNM", nickNm.trim());
				jsonObj.put("MAILID", mailId.trim());
				jsonObj.put("TRANSFERTYPE", "Y");
				jsonObj.put("IFSCCD", "DUMMY");
				jsonObj.put("MMID", "DUMMY");
				jsonObj.put("IINSERTUPDTDLT", "U");
				jsonObj.put("BENSRNO", benSrno.trim());
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
		bundle.putString("FROMACT", "EDSAMBENF");
		bundle.putString("JSONOBJ", jsonObj.toString());
		fragment.setArguments(bundle);
		FragmentManager fragmentManager = editSameBnkBenf.getFragmentManager();
		fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
	} catch (Exception e) {
		e.printStackTrace();
	}
}

}// end editSameBankBeneficiary


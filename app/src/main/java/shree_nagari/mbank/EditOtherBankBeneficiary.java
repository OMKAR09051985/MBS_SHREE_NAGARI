package shree_nagari.mbank;

import java.security.PrivateKey;
import java.util.ArrayList;

import javax.crypto.spec.SecretKeySpec;

import mbLib.CryptoClass;
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
import android.content.SharedPreferences.Editor;
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

public class EditOtherBankBeneficiary extends Fragment implements
		OnClickListener {
	MainActivity act;
	EditOtherBankBeneficiary editOtherBankBenf;
	EditText txtIFSC_Code;
	String flg = "false";
	EditText txtMobile_No;
	EditText txtAccNo;
	EditText txtName;
	EditText txtBank;
	ImageView img_heading;
	EditText txtBranch;
	EditText txtEmail, txtMMID,txtNick_Name;
	// EditText txtNick_Name;
	JSONArray jsonArr;
	String strIfsc = "";
	// Button btn_fetchBnkBrn;
	Button btn_submit;
	// Button btn_fetchbnkbrn;
	TextView txt_heading;
	Intent in = null;

	// TextView cust_nm;
	ProgressBar p_wait;
	String acnt_inf, all_acnts;
	String str = "", retMess = "", cust_name = "";
	private static final String MY_SESSION = "my_session";
	Editor e;
	String stringValue,respcode="",retvalweb="",respdescget_bnkbrn="",respdescbeneficiaries="",respdescsave_beneficiary="";
	String respdescGetBanks="",respdescGetStates="",respdescGetDistricts="",respdescGetCities="",respdescGetBranches="",respdescGetIFSC="";
	String str2 = "";

	ImageButton btn_home;// ,btn_back;
	private String userId;
	
	String account_No = "", name = "", mobile_no = "", nick_name = "",
			email = "", same_bank = "", ifsc_code = "", insrtUpdtDlt = "";

	String tmpXMLString = "", retVal = "";
	String custId = "", accNo = "", accNm = "", mobNo = "", nickNm = "",
			mailId = "";
	String ifsCD = "", bnCD = "", brCD = "", mmId = "";
	int cnt = 0, flag = 0;
	DatabaseManagement dbms;
	EditOtherBankBeneficiary obj = null;
	Spinner spi_sel_beneficiery;
	ImageButton spinner_btn, btn_fetchBnkBrn;
	private String benInfo = "";
	String mobPin = "";
	Bundle bdn;
	String benNickname = "";
	String when_fetch = "";
	String saveFlag = "ERR";
	LinearLayout edit_benf_layout, get_ifsc_layout, ifsc_layout;
	String benSrno = null;
	PrivateKey var1=null;	  
	String var5="",var3="";
	SecretKeySpec var2=null;
	Spinner spi_bank, spi_state, spi_district, spi_city, spi_branch;
	ImageButton spnr_btn1, spnr_btn2, spnr_btn3, spnr_btn4, spnr_btn5;
	public String encrptdMpin,Mpin="";

	public EditOtherBankBeneficiary() {
	}

	@SuppressLint("ValidFragment")
	public EditOtherBankBeneficiary(MainActivity a) {
		System.out.println("EditOtherBankBeneficiary()" + a);
		act = a;
		editOtherBankBenf = this;
	}

	public void onBackPressed() {
		Fragment fragment = new ManageBeneficiaryMenuActivity(act);
		FragmentManager fragmentManager = getFragmentManager();
		fragmentManager.beginTransaction()
				.replace(R.id.frame_container, fragment).commit();
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		System.out.println("onCreateView() EditOtherBankBeneficiary");
		var1 = act.var1;
		var3 = act.var3;
		dbms = new DatabaseManagement("shree_nagari.mbank", "listMobileBanking");
		View rootView = inflater.inflate(R.layout.edit_otherbank_beneficiary,
				container, false);
		
		Cursor c1 = dbms.selectFromTable("SHAREDPREFERENCE", "", null);
		if (c1 != null) {
			while (c1.moveToNext()) {
				custId = c1.getString(2);
				//Log.e("custId", "......" + custId);
			}
		}
		
		img_heading = (ImageView) rootView.findViewById(R.id.img_heading);
		img_heading.setBackgroundResource(R.mipmap.benefeciary);
		txtIFSC_Code = (EditText) rootView.findViewById(R.id.txtIFSC_Code2);
		txtMMID = (EditText) rootView.findViewById(R.id.txtMMID2);
		txtMobile_No = (EditText) rootView.findViewById(R.id.txtMobile_No2);
		txtAccNo = (EditText) rootView.findViewById(R.id.txtAccNo2);
		txtName = (EditText) rootView.findViewById(R.id.txtName2);
		// txtBank=(EditText)rootView.findViewById(R.id.txtBank2);
		// txtBranch=(EditText)rootView.findViewById(R.id.txtBranch2);
		txtEmail = (EditText) rootView.findViewById(R.id.txtEmail2);
		txtNick_Name=(EditText)rootView.findViewById(R.id.txtNick_Name2);
		p_wait = (ProgressBar) rootView.findViewById(R.id.pro_bar);
		btn_home = (ImageButton) rootView.findViewById(R.id.btn_home);
		// btn_back=(ImageButton)rootView.findViewById(R.id.btn_back);
		txt_heading = (TextView) rootView.findViewById(R.id.txt_heading);
		txt_heading.setText(getString(R.string.frmtitle_edit_same_bnk_bnf));
		// btn_home.setImageResource(R.drawable.ic_home_d);
		// btn_back.setImageResource(R.drawable.backover);
		edit_benf_layout = (LinearLayout) rootView
				.findViewById(R.id.edit_benf_layout);
		get_ifsc_layout = (LinearLayout) rootView
				.findViewById(R.id.get_ifsc_layout);
		ifsc_layout = (LinearLayout) rootView.findViewById(R.id.ifsc_layout);
		// btn_back.setOnClickListener(this);
		btn_home.setOnClickListener(this);
		p_wait.setMax(10);
		p_wait.setProgress(1);

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

		// /btn_fetchName=(Button)findViewById(R.id.btn_fetchName2);
		// btn_fetchBnkBrn=(Button)rootView.findViewById(R.id.btn_fetchBnkBrn2);
		btn_submit = (Button) rootView.findViewById(R.id.btn_submit2);
		// btn_fetchbnkbrn =
		// (Button)rootView.findViewById(R.id.btn_fetchBnkBrn2);
		btn_fetchBnkBrn = (ImageButton) rootView
				.findViewById(R.id.btn_fetchIFSC);
		btn_fetchBnkBrn.setOnClickListener(this);
		// /btn_fetchName.setOnClickListener(this);
		btn_submit.setOnClickListener(this);
		// btn_fetchbnkbrn.setOnClickListener(this);
		spi_sel_beneficiery = (Spinner) rootView
				.findViewById(R.id.sameBnkTranspi_sel_beneficiery);

		// btn_fetchBnkBrn.setTypeface(tf_calibri);
		// btn_fetchbnkbrn.setTypeface(tf_calibri);
		// btn_submit.setTypeface(tf_calibri);
		spinner_btn = (ImageButton) rootView.findViewById(R.id.spinner_btn);
		spinner_btn.setOnClickListener(this);
		
		{
			
			Cursor c2 = dbms.selectFromTable("SHAREDPREFERENCE", "", null);// ("select * from ",
																			// null);
			if (c2 != null) {
				while (c2.moveToNext()) {
					custId = c2.getString(2);
					//Log.e("custId", "......" + custId);
					userId = c2.getString(3);
					//Log.e("CustId", "c......" + userId);
				}
			}
			new CallWebService_fetch_all_beneficiaries().execute();
			// CallWebService C=new CallWebService();
			// C.execute();

		

		}

		txtIFSC_Code.addTextChangedListener(new TextWatcher() {

			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				// txtBank.setText("");
				// txtBranch.setText("");
				btn_fetchBnkBrn.setEnabled(true);

				// txtBank2.setEnabled(true);

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

		// Beneficiary on select
		spi_sel_beneficiery
				.setOnItemSelectedListener(new OnItemSelectedListener() {
					String benIFSC = "";
					String benAccountNumber = "";
					String benAccountName = "";
					String benMMID = "";
					String benMobNo = "";
					String benEmail = "";
					String niknm="";
					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						String str = spi_sel_beneficiery.getItemAtPosition(
								spi_sel_beneficiery.getSelectedItemPosition())
								.toString();

						if (str.equalsIgnoreCase("Select Beneficiary")) {
							txtIFSC_Code.setText("");
							// txtBank.setText("");
							// txtBranch.setText("");
							txtAccNo.setText("");
							txtName.setText("");
							txtMobile_No.setText("");
							txtEmail.setText("");
							// txtNick_Name.setText("");
						} else {

							String allStr[] = benInfo.split("~");
							// String benAcc[]=benAcNo.split("(");

							for (int i = 1; i <= allStr.length; i++) {
								String str1[] = allStr[i - 1].split("#");
								niknm= str1[2] + "(" + str1[1] + ")";
								System.out.println("==== selected benInfo :"
										+ str);
								System.out.println("(" + str1[1] + ")");

								Log.i("str1[0]", "str1[0]:" + str1[0]);
								Log.i("str1[1]", "str1[1]:" + str1[1]);
								Log.i("str1[2]", "str1[2]:" + str1[2]);
								Log.i("str1[3]", "str1[3]:" + str1[3]);
								Log.i("str1[4]", "str1[4]:" + str1[4]);

								// if (str.indexOf("(" + str1[1] + ")") > -1) {
								if (str.equalsIgnoreCase(niknm)){//indexOf(str1[2]) > -1) {
									System.out
											.println("========== inside if ============");
									benSrno = str1[0];
									benAccountName = str1[1];
									benNickname = str1[2];
									System.out.println("benSrno=>>>>>>"
											+ benSrno);
									if (str1[3].equals("-9999")) {
										benAccountNumber = "";
									} else {
										benAccountNumber = str1[3];
									}

									if (str1[4].equals("NA")) {
										// benIFSC = "";
										ifsCD = "";
									} else {
										// benIFSC = str1[4];
										ifsCD = str1[4];
									}

									if (str1[5].equals("-9999")) {
										benMMID = "";
									} else {
										benMMID = str1[5];
									}

									if (str1[7].equals("NA")) {
										benEmail = "";
									} else {
										benEmail = str1[7];
									}

									if (str1[6].equals("NA")) {
										benMobNo = "";
									} else {
										benMobNo = str1[6];
									}

									txtIFSC_Code.setText(ifsCD);
									txtAccNo.setText(benAccountNumber);
									txtName.setText(benAccountName);
									txtMMID.setText(benMMID);
									txtMobile_No.setText(benMobNo);
									txtEmail.setText(benEmail);
									txtNick_Name.setText(benNickname);
									// txtNick_Name.setText(benNickname);

									/*
									 * if(benIFSC.length() == 0 ||
									 * benIFSC.equalsIgnoreCase("NA")) {} else {
									 * //flag = chkConnectivity();
									 * 
									 * //if (flag == 0) {
									 * //CallWebService_get_bnkbrn C=new
									 * CallWebService_get_bnkbrn();
									 * //C.execute(); } }
									 */
								}

							}// end for
						}
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
						// TODO Auto-generated method stub

					}

				});// end spi_sel_beneficiery
		/*
		 * Button btn_back = (Button) rootView.findViewById(R.id.btn_back); //
		 * Listening to back button click btn_back.setOnClickListener(new
		 * View.OnClickListener() {
		 * 
		 * @Override public void onClick(View view) { // Launching News Feed
		 * Screen Intent i = new Intent(getApplicationContext(),
		 * OtherBankBeneficiary.class); startActivity(i); finish(); } });
		 */

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

		return rootView;
	}

	public void initAll() {
		txtIFSC_Code.setText("");
		txtMMID.setText("");
		txtMobile_No.setText("");
		txtAccNo.setText("");
		txtName.setText("");
		// txtBank.setText("");
		// txtBranch.setText("");
		txtEmail.setText("");
		// txtNick_Name.setText("");
	}

	private void addBeneficiaries(String retval) {
		Log.e("addBeneficiaries",
				"================ IN addBeneficiaries() of EditOtherBankBeneficiary ======================");
		Log.e("addBeneficiaries", "SameBankTransfer IN addBeneficiaries()"
				+ retval);

		try {
			ArrayList<String> arrList = new ArrayList<String>();
			String allstr[] = retval.split("~");

			int noOfben = allstr.length;
			Log.e("addBeneficiaries", "noOfben===" + noOfben);
			String benName = "";
			arrList.add("Select Beneficiary");

			for (int i = 1; i <= noOfben; i++) {
				Log.e("addBeneficiaries", i + "----STR1-----------"
						+ allstr[i - 1]);
				String[] str2 = allstr[i - 1].split("#");
				benName = str2[2] + "(" + str2[1] + ")";
				arrList.add(benName);
				Log.e("addBeneficiaries",
						"=============== benificiary Name is:======" + benName);
			}
			// spi_sel_beneficiery
			Log.e("addBeneficiaries",
					"================ IN addBeneficiaries() 1 ======================");

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
			ArrayAdapter<String> accs = new ArrayAdapter<String>(act,R.layout.spinner_item, benfArr);
			/*CustomeSpinnerAdapter accs = new CustomeSpinnerAdapter(act,
					R.layout.spinner_layout, benfArr);*/
			accs.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spi_sel_beneficiery.setAdapter(accs);

		} catch (Exception e) {
			System.out.println("" + e);
		}
	}// end addBeneficiaries

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
				else if((str.equalsIgnoreCase(respdescbeneficiaries)) &&  (respcode.equalsIgnoreCase("0")))
				{
					post_successget_beneficiaries(retvalweb);
				}
				else if((str.equalsIgnoreCase(respdescbeneficiaries)) && (respcode.equalsIgnoreCase("1")))
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
				else if((str.equalsIgnoreCase(respdescGetBanks)) &&  (respcode.equalsIgnoreCase("0")))
				{
					post_successGetBanks(retvalweb);
				}
				else if((str.equalsIgnoreCase(respdescGetBanks)) && (respcode.equalsIgnoreCase("1")))
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
					Log.e("in alert ok click", "------");
					SaveBeneficiary();
				}
				else if(str.equalsIgnoreCase(act.getString(R.string.alert_029))){
				if (flg == "true") {
					Log.e("Inside If", "Inside if===" + flg);
					switch (v.getId()) {
					case R.id.btn_ok:
						Fragment fragment = new ManageBeneficiaryMenuActivity(
								act);
						FragmentManager fragmentManager = getFragmentManager();
						fragmentManager.beginTransaction()
								.replace(R.id.frame_container, fragment)
								.commit();
					}
					this.dismiss();
				}
			}
			}
		};
		alert.show();
	}

	// Get values from getBnkBrn method.....
	class CallWebService_get_bnkbrn extends AsyncTask<Void, Void, Void> {
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
				
				jsonObj.put("IFSC", ifsCD);
				jsonObj.put("SIMNO", MBSUtils.getSimNumber(act));
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
		
			if (retvalweb.indexOf("FAILED") > -1) {
				loadProBarObj.dismiss();
				
				retMess = getString(R.string.alert_000);
				showAlert(retMess);
				

			} else if (retvalweb.indexOf("no data found") > -1) {
				loadProBarObj.dismiss();
				
				retMess = getString(R.string.alert_018);
				showAlert(retMess);
			
			} else {
				loadProBarObj.dismiss();
				post_successget_bnkbrn(retvalweb);
				
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

	}// end callWbService2

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
				retvalweb="";
				respdescbeneficiaries="";
				
				// pb_wait.setVisibility(ProgressBar.VISIBLE);
				loadProBarObj.show();

				jsonObj.put("CUSTID", custId);
				jsonObj.put("SAMEBNK", "N");
				jsonObj.put("IMEINO", MBSUtils.getImeiNumber(act));
				jsonObj.put("SIMNO", MBSUtils.getSimNumber(act));
				//ValidationData=MBSUtils.getValidationData(act,jsonObj.toString());
				
			} catch (JSONException je) {
				je.printStackTrace();
			}
		
		}

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
				// e.printStackTrace();
				System.out.println("Exception 2");
				System.out.println("EditOtherBankBeneficiary   Exception" + e);
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
					retvalweb = jsonObj.getString("RETVAL");
				}
				else
				{
					retvalweb = "";
				}
				if (jsonObj.has("RESPDESC"))
				{
					respdescbeneficiaries = jsonObj.getString("RESPDESC");
				}
				else
				{	
					respdescbeneficiaries = "";
				}
				
			if(respdescbeneficiaries.length()>0)
			{
				showAlert(respdescbeneficiaries);
			}
			else{
			//Log.e("onPostExecute", retval);

			if (retvalweb.indexOf("SUCCESS") > -1) {
				
				
				post_successget_beneficiaries(retvalweb);
				
			} else {
				
				retMess = getString(R.string.alert_041);
				//loadProBarObj.dismiss();
				flg = "true";
				showAlert(retMess);
				
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

	}// end callWbService

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
				jsonObj.put("BENSRNO", benSrno);
				jsonObj.put("IMEINO", MBSUtils.getImeiNumber(act));
				jsonObj.put("MPIN", Mpin);
				jsonObj.put("SIMNO", MBSUtils.getSimNumber(act));
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
				e.printStackTrace();
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
			if (retvalweb.indexOf("FAILED") > -1) {

				// SUCCESS/FAILED~DUPLICATEACCOUNT/DUPLICATENICKNAME

				if (retvalweb.indexOf("DUPLICATEACCOUNT") > -1) {
					retMess = getString(R.string.alert_026);
					showAlert(retMess);
				} else if (retvalweb.indexOf("DUPLICATENICKNAME") > -1) {
					
					retMess = getString(R.string.alert_027);
					showAlert(retMess);
				}else if (retvalweb.indexOf("INCORRECTIFSC") > -1) {
					
						retMess = getString(R.string.alert_185);//alert_018);
						showAlert(retMess);
					}
				 else if (retvalweb.indexOf("WRONGMPIN") > -1) {
					//loadProBarObj.dismiss();
					retMess = getString(R.string.alert_125);
					showAlert(retMess);
				} else {

				
					retMess = getString(R.string.alert_028);
					//loadProBarObj.dismiss();
					showAlert(retMess);
					initAll();

				}
			} else {
				//loadProBarObj.dismiss();
				
				post_successsaveBeneficiaries(retvalweb);
				// onCreate(bdn);

			}
			saveFlag = "ERR";}
				/*}
				else{
					MBSUtils.showInvalidResponseAlert(act);	
				}*/
			} catch (JSONException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// p_wait.setVisibility(ProgressBar.INVISIBLE);
		}// end onPostExecute

	}// end CallWebService
	
	public 	void post_successsaveBeneficiaries(String retvalweb)
	{
		respcode="";
		respdescsave_beneficiary="";
		System.out
				.println("EditOtherBankBeneficiary	If Success----------------");
		System.out
				.println("EditOtherBankBeneficiary	If Success----------------");
		System.out
				.println("EditOtherBankBeneficiary	If Success----------------");
		System.out
				.println("EditOtherBankBeneficiary	If Success----------------");
		System.out
				.println("EditOtherBankBeneficiary	If Success----------------");
		
		flg = "true";
		// ///////retMess="Other Bank Beneficiary Updated Successfully.";
		retMess = getString(R.string.alert_029);
		showAlert(retMess);
		initAll();

		// onCreate(bdn);

	
	}

	class CallWebServiceGetBanks extends AsyncTask<Void, Void, Void> {
		String retval = "";
		LoadProgressBar loadProBarObj = new LoadProgressBar(act);
		JSONObject jsonObj = new JSONObject();
	
		String ValidationData="";

		protected void onPreExecute() {
			try {
				respcode="";
				retvalweb="";
				respdescGetBanks="";
				// p_wait.setVisibility(ProgressBar.VISIBLE);
				loadProBarObj.show();

				
				jsonObj.put("CUSTID", custId);
				jsonObj.put("IMEINO", MBSUtils.getImeiNumber(act));
				jsonObj.put("SIMNO", MBSUtils.getSimNumber(act));
				jsonObj.put("METHODCODE","34");
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
				Log.e("IN return", "data :" + jsonObj.toString());
             */  if (jsonObj.has("RESPCODE"))
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
					respdescGetBanks= jsonObj.getString("RESPDESC");
				}
				else
				{	
					respdescGetBanks= "";
				}
				
			if(respdescGetBanks.length()>0)
			{
				showAlert(respdescGetBanks);
			}
			else{
			if (retvalweb.indexOf("FAILED") > -1) {
				showAlert(getString(R.string.alert_126));
			} else {
				post_successGetBanks(retvalweb);
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
	public 	void post_successGetBanks(String retvalweb)
	{
		respcode="";
		respdescGetBanks="";
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
					act, android.R.layout.simple_spinner_item,
					bankNamesArr);*/
			bankNames
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
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

				//System.out.println("ifsCD:" + ifsCD);
				jsonObj.put("CUSTID", custId);
				jsonObj.put("BANKNAME", spi_bank.getSelectedItem().toString());
				jsonObj.put("IMEINO", MBSUtils.getImeiNumber(act));
				jsonObj.put("SIMNO", MBSUtils.getSimNumber(act));
				jsonObj.put("METHODCODE","35");
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
			String str=CryptoClass.Function6(var5,var2);
				String decryptedBeneficiaries = str.trim();
		
			loadProBarObj.dismiss();
			JSONObject jsonObj;
			try
			{

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
			
			if (decryptedBeneficiaries.indexOf("FAILED") > -1) {
				showAlert(getString(R.string.alert_127));
			} else {
				post_successGetStates(retvalweb);
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
					act, android.R.layout.simple_spinner_item,
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

			//	System.out.println("ifsCD:" + ifsCD);
				jsonObj.put("CUSTID", custId);
				jsonObj.put("BANKNAME", spi_bank.getSelectedItem().toString());
				jsonObj.put("STATE", spi_state.getSelectedItem().toString());
				jsonObj.put("IMEINO", MBSUtils.getImeiNumber(act));
				jsonObj.put("SIMNO", MBSUtils.getSimNumber(act));
				jsonObj.put("METHODCODE","36");
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
					act, android.R.layout.simple_spinner_item,
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
	
				jsonObj.put("CUSTID", custId);
				jsonObj.put("BANKNAME", spi_bank.getSelectedItem().toString());
				jsonObj.put("STATE", spi_state.getSelectedItem().toString());
				jsonObj.put("DISTRICT", spi_district.getSelectedItem()
						.toString());
				jsonObj.put("IMEINO", MBSUtils.getImeiNumber(act));
				jsonObj.put("SIMNO", MBSUtils.getSimNumber(act));
				jsonObj.put("METHODCODE","37");
				// ValidationData=MBSUtils.getValidationData(act,jsonObj.toString());
		
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
			String str=CryptoClass.Function6(var5,var2);
			
			String decryptedBeneficiaries = str.trim();

			loadProBarObj.dismiss();
			JSONObject jsonObj;
			try
			{

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
			// Log.e("EDIT BENF", decryptedBeneficiaries);
			if (decryptedBeneficiaries.indexOf("FAILED") > -1) {
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
					act, android.R.layout.simple_spinner_item, cityArr);*/
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

				jsonObj.put("CUSTID", custId);
				jsonObj.put("BANKNAME", spi_bank.getSelectedItem().toString());
				jsonObj.put("STATE", spi_state.getSelectedItem().toString());
				jsonObj.put("DISTRICT", spi_district.getSelectedItem()
						.toString());
				jsonObj.put("CITY", spi_city.getSelectedItem().toString());
				jsonObj.put("IMEINO", MBSUtils.getImeiNumber(act));
				jsonObj.put("SIMNO", MBSUtils.getSimNumber(act));
				jsonObj.put("METHODCODE","38");
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
			if (retvalweb.indexOf("FAILED") > -1) {
				showAlert(getString(R.string.alert_130));
			} else {post_successGetBranches(retvalweb);}}
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
					act, android.R.layout.simple_spinner_item,
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
				respcode="";
				retvalweb="";
				respdescGetIFSC="";
				// p_wait.setVisibility(ProgressBar.VISIBLE);
				loadProBarObj.show();
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
			//	 ValidationData=MBSUtils.getValidationData(act,jsonObj.toString());
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
			edit_benf_layout.setVisibility(edit_benf_layout.VISIBLE);
			get_ifsc_layout.setVisibility(get_ifsc_layout.INVISIBLE);
			txtIFSC_Code.setText(strIfsc);
			// txtBank.setText(bankName);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	
	}
	public void onClick(View arg0) {
			switch (arg0.getId()) {
		/*
		 * case R.id.btn_back: System.out.println("Clicked on back");
		 * 
		 * Fragment fragment = new ManageBeneficiaryMenuActivity(act);
		 * FragmentManager fragmentManager = getFragmentManager();
		 * fragmentManager.beginTransaction() .replace(R.id.frame_container,
		 * fragment).commit(); break;
		 */

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
		/*
		 * case R.id.btn_fetchBnkBrn2: when_fetch = "EXPLICIT";
		 * Log.i("btn_fetchBnkBrn2 clicked","btn_fetchBnkBrn2 clicked"); ifsCD =
		 * txtIFSC_Code.getText().toString();
		 * Log.i("btn_fetchBnkBrn2 clicked","btn_fetchBnkBrn2 clicked ifsCD:"
		 * +ifsCD); if(ifsCD.equalsIgnoreCase("")) { ///////retMess =
		 * "Please Enter IFSC Code."; retMess = getString(R.string.alert_023);
		 * showAlert(retMess); } else { flag = chkConnectivity(); if (flag == 0)
		 * { CallWebService_get_bnkbrn C=new CallWebService_get_bnkbrn();
		 * C.execute(); } } break;
		 */
		case R.id.btn_fetchIFSC:
			spi_bank.setAdapter(null);
			spi_state.setAdapter(null);
			spi_district.setAdapter(null);
			spi_city.setAdapter(null);
			spi_branch.setAdapter(null);
			edit_benf_layout.setVisibility(edit_benf_layout.INVISIBLE);
			get_ifsc_layout.setVisibility(get_ifsc_layout.VISIBLE);
			act.frgIndex = 651;
			flag = chkConnectivity();
			if (flag == 0) {
				CallWebServiceGetBanks C = new CallWebServiceGetBanks();
				C.execute();
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
		case R.id.btn_submit2:
			saveFlag = "ERR";

			
			String str = spi_sel_beneficiery.getItemAtPosition(
					spi_sel_beneficiery.getSelectedItemPosition()).toString();
			accNo = txtAccNo.getText().toString().trim();
			Log.i("accNo :", "" + accNo);

			accNm = txtName.getText().toString().trim();
			Log.i("accNm :", "" + accNm);

			mobNo = txtMobile_No.getText().toString().trim();
		

			//nickNm = benNickname;
			nickNm=txtNick_Name.getText().toString().trim();
			int niknm_len = nickNm.length();
			mailId = txtEmail.getText().toString().trim();
			same_bank = "N";
			ifsCD = txtIFSC_Code.getText().toString().trim();			
			mmId = txtMMID.getText().toString().trim();
			insrtUpdtDlt = "U";
			ifsCD = ifsCD.toUpperCase();
		
			if (str.equalsIgnoreCase("Select Beneficiary")) {
				retMess = getString(R.string.alert_098);
				showAlert(retMess);
			} else if (ifsCD.length() == 0 && mmId.length() == 0) {
				Log.e("All blankssssssssss", "................. 0");
				saveFlag = "ERR";
			
				retMess = getString(R.string.alert_024);
				showAlert(retMess);
				txtIFSC_Code.requestFocus();
			} else if (ifsCD.length() != 0)// ((IFSC Code and Account No) or
											// (MMID & Mobile No) Is Mandatory)
			{
				Log.e("IFSC code entered", "................. 1");

				int ifsc_len = ifsCD.length();
				if (ifsc_len != 11) {
					saveFlag = "ERR";
					retMess = getString(R.string.alert_166);
					showAlert(retMess);
					txtIFSC_Code.requestFocus();
				}

				else if (accNo.length() == 0) {
					saveFlag = "ERR";
					// /////retMess = "Please Enter Account Number.";
					retMess = getString(R.string.alert_001);
					showAlert(retMess);
					txtAccNo.requestFocus();
				} 
				else if ((mobNo.length() > 0) && (mobNo.length() != 10
						|| !MBSUtils.validateMobNo(mobNo)))
				
			{
						saveFlag = "ERR";
					
					retMess = getString(R.string.alert_006);
					showAlert(retMess);
					txtMobile_No.requestFocus();
					}
					
				
				else if (accNm.length() == 0) {
					retMess = getString(R.string.alert_096);
					showAlert(retMess);
					txtName.requestFocus();
				} 
				

				else if (mailId.length() != 0
						&& !MBSUtils.validateEmail(mailId)) {
					// ///retMess = "Please Enter Valid E-mail Id.";
					retMess = getString(R.string.alert_007);
					showAlert(retMess);
					txtEmail.requestFocus();

				} 
				
				else if (nickNm.trim().length() == 0) 
				{
					saveFlag = "ERR";
					retMess = getString(R.string.alert_003);
					showAlert(retMess);
					txtNick_Name.requestFocus();
				}
				else if (niknm_len < 4 || niknm_len > 15) 
				{
					retMess = getString(R.string.alert_005);
					saveFlag = "ERR";
					showAlert(retMess);
					txtNick_Name.requestFocus();

				} 
				else if (mmId.length() != 0) {
					int mmid_len = mmId.length();

					saveFlag = "OK";
					if (mmid_len != 7) {
						saveFlag = "ERR";
						retMess = getString(R.string.alert_025);
						showAlert(retMess);
						txtMMID.requestFocus();
					}
				}
				/*else if (mobNo.length() > 0
						|| !MBSUtils.validateMobNo(mobNo)) {
					Log.e("Mobile number not entered", "................. 44");
					saveFlag = "ERR";
					// ////retMess = "Please Enter Mobile Number.";
					retMess = getString(R.string.alert_002);
					showAlert(retMess);
					txtMobile_No.requestFocus();
				}*/else {
					

					saveFlag = "OK";

				}
			}  if (mmId.length() != 0) {
				int mmid_len = mmId.length();

				if (mmid_len != 7) {
					saveFlag = "ERR";
					// ////retMess = "Please Enter 7-digits MMID.";
					retMess = getString(R.string.alert_025);
					showAlert(retMess);
					txtMMID.requestFocus();
				} else if (mobNo.length() == 0) 
				{
					saveFlag = "ERR";
					// ////retMess = "Please Enter Mobile Number.";
					retMess = getString(R.string.alert_002);
					showAlert(retMess);
					txtMobile_No.requestFocus();
				}

				else if (mobNo.length() != 10 || !MBSUtils.validateMobNo(mobNo)) {
					saveFlag = "ERR";
					// ////retMess = "Please Enter Mobile Number.";
					retMess = getString(R.string.alert_006);
					showAlert(retMess);
					txtMobile_No.requestFocus();
				}
				
				else if (ifsCD.length() != 0) {
					int ifsc_len = ifsCD.length();
					if (ifsc_len != 11) {
						saveFlag = "ERR";
						retMess = getString(R.string.alert_166);
						showAlert(retMess);
						txtIFSC_Code.requestFocus();
					}
				} else if (mailId.length() != 0
						&& !MBSUtils.validateEmail(mailId)) {
					// ///retMess = "Please Enter Valid E-mail Id.";
					retMess = getString(R.string.alert_007);
					showAlert(retMess);
					txtEmail.requestFocus();

				} else {
					
					saveFlag = "OK";

				}
			}
			
			if (saveFlag.equalsIgnoreCase("OK")) {
				flag = chkConnectivity();

				if (flag == 0) {
					InputDialogBox inputBox = new InputDialogBox(act);
					inputBox.show();
				}
			}
			break;
		
		default:
			break;
		}// end switch
	}// end onClick

	public void SaveBeneficiary() {
		flag = chkConnectivity();
		if (flag == 0) {
			Log.i("VALIDATAIONS ALL OK", "Call webservice for Save beneficiary");
			// CallWebService_save_beneficiary C = new
			// CallWebService_save_beneficiary();
			// C.execute();
			InputDialogBox inputBox = new InputDialogBox(act);
			inputBox.show();
		}

	}

	public int chkConnectivity() {
		// pb_wait.setVisibility(ProgressBar.VISIBLE);
		System.out
				.println("========================= end chkConnectivity ==================");
		ConnectivityManager cm = (ConnectivityManager) act
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		try {
			State state = ni.getState();
			boolean state1 = ni.isAvailable();
			System.out
					.println("BalanceEnquiry	in chkConnectivity () state1 ---------"
							+ state1);
			if (state1) {
				switch (state) {
				case CONNECTED:
					if (ni.getType() == ConnectivityManager.TYPE_MOBILE
							|| ni.getType() == ConnectivityManager.TYPE_WIFI) {

					}
					break;
				case DISCONNECTED:
					flag = 1;
					// /////retMess =
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
					// ///retMess = "Network Unavailable. Please Try Again.";
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

			Log.i("BalanceEn", "NullPointerException Exception"
					+ ne);
			flag = 1;
			// //////retMess = "Network Unavailable. Please Try Again.";
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
				
				if (str.equalsIgnoreCase("")) {
					this.hide();
					// //////retMess = "Enter Valid MPIN.";
					retMess = getString(R.string.alert_015);
					showAlert(retMess);
					mpin.setText("");
				} else { 
                       Cursor c1=dbms.selectFromTable("SHAREDPREFERENCE", "", null);//("select * from ", null);
					        if(c1!=null)
					        {
					        	while(c1.moveToNext())
						        {	
					        		custId=c1.getString(2);
						        	//Log.e("custId","......"+custId);
						        }
					        }
					        callValidateTranpinService C= new callValidateTranpinService();
				//	CallWebService_save_beneficiary C = new CallWebService_save_beneficiary();
					C.execute();
					// SaveBeneficiary();
					this.hide();
				}/* else {
					Log.e("InputDialogBox",
							"=========== inside else ==============");
					this.hide();
					// ////retMess = "Enter Valid MPIN.";
					retMess = getString(R.string.alert_125);
					showAlert(retMess);
					mpin.setText("");
				}*/

				// SaveBeneficiary();
				// this.hide();
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
				
				//jsonObj.put("METHODCODE","14"); 
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
				jsonObj.put("BENSRNO", benSrno);
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
			bundle.putString("FROMACT", "EDOTHBENF");
			bundle.putString("JSONOBJ", jsonObj.toString());
			fragment.setArguments(bundle);
			FragmentManager fragmentManager = editOtherBankBenf.getFragmentManager();
			fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	} //end saveData
	
	
	public 	void post_successget_bnkbrn(String retvalweb)
	{
		respcode="";
		respdescget_bnkbrn="";
		String allStr[] = retvalweb.split("~");
		System.out.println(allStr[1]);
		String bankBranch[] = allStr[1].split("#");
		System.out.println("bank cD:" + bankBranch[0]);
		System.out.println("branch cD:" + bankBranch[1]);
		String bnkCD = bankBranch[0];
		String brnCD = bankBranch[1];
		bnCD = bnkCD;
		brCD = brnCD;
		// txtBank.setText(bnCD);
		// txtBranch.setText(brCD);
		//loadProBarObj.dismiss();

		if (when_fetch.equalsIgnoreCase("AUTO")) {
			SaveBeneficiary();
		}
		// txtBank.setText("2");
		// txtBranch.setText("1");
		//loadProBarObj.dismiss();
		
	}
	public 	void post_successget_beneficiaries(String retvalweb)
	{
		respcode="";
		respdescbeneficiaries="";

		System.out
				.println("================== in onPostExecute 1 ============================");
		System.out
				.println("================== in onPostExecute 2 ============================");

	//	System.out.println("xml_data.len :" + xml_data.length);
		String decryptedBeneficiaries = retvalweb.split("SUCCESS~")[1];
		System.out.println("decryptedBeneficiaries:"
				+ decryptedBeneficiaries);

		benInfo = decryptedBeneficiaries;
		Log.e("Ganesh ", "benInfo =" + decryptedBeneficiaries);
		Log.e("Ganesh ", "benInfo =" + decryptedBeneficiaries);
		Log.e("Ganesh ", "benInfo =" + decryptedBeneficiaries);
		Log.e("Ganesh ", "benInfo =" + decryptedBeneficiaries);
		addBeneficiaries(decryptedBeneficiaries);
		//loadProBarObj.dismiss();
		System.out
				.println("================== in onPostExecute 3 ============================");
	
	}
}// end AddOtherBankBeneficiary

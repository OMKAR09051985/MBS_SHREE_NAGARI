package shree_nagari.mbank;

import java.security.PrivateKey;
import java.util.ArrayList;

import javax.crypto.spec.SecretKeySpec;

import mbLib.CryptoClass;
import mbLib.CryptoUtil;
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
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
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

//import android.R;

public class OtherBankTranRTGS extends Fragment implements OnClickListener {
	OtherBankTranRTGS otherBnkIfsc = null;
	MainActivity act;
	Button btn_submit, btn_confirm,btn_con_back;
	Spinner spi_debit_account, spi_sel_beneficiery, spi_payment_option;
	ImageButton spinner_btn, spinner_btn2, btn_back, spinner_btn3;
	ImageView btn_home1,btn_logout;
	TextView cust_nm, txt_heading, txt_remark, txt_from, txt_to, txt_amount,
			txt_charges, txtTranId, txt_trantype;
	EditText txtAccNo, txtAmt, txtRemk, txtBank, txtBranch, txtIfsc;
	DialogBox dbs;
	ProgressBar pb_wait;
	Editor e;
	LinearLayout confirm_layout, other_bnk_layout;
	private String benInfo = "";
	private static String URL = "";
	private static String NAMESPACE = "";
	private static String SOAP_ACTION = "";
	private static final String MY_SESSION = "my_session";
	private static  String METHOD_NAME_GET_BENF = "";
	private static  String METHOD_SAVE_TRANSFERTRAN = "";
	private static  String METHOD_GET_TRANSFERCHARGE = "";
	private static  String METHOD_GET_BANK_BRANCH = "";
	String respcode="",reTval="",getBeneficiariesrespdesc="",saveTransferTranrespdesc="",getTransferChargesrespdesc="",getBnkBrnrespdesc;
	DatabaseManagement dbms;
	public String encrptdTranMpin,tranmpin="";
	String retStr = "", userId = "", cust_mob_no = "", transferType = "";
	String postingStatus = "", req_id = "",errorCode="";
	String balString="",gst="";
	 double balance ;
	int frmno = 0, tono = 0, flag = 0, cnt = 0;
	String stringValue, str = "", retMess = "", cust_name = "", custId = "",
			str2 = "", ifsCD = "", benSrno = null, tranPin = "";
	String mobPin = "", acnt_inf, all_acnts, bnCD, brCD, benAccountNumber = "",
			chrgCrAccNo = "", tranId = "", tranType = "", servChrg = "",
			cess = "",transaction="";
	String otherIfsctxtIFSCCode = "", drBrnCD = "", drSchmCD = "", drAcNo = "",
			strFromAccNo = "", strToAccNo = "", strAmount = "", strRemark = "";
	String onlyCharge = "";
	ArrayList<String> arrListTemp = new ArrayList<String>();
	private boolean noAccounts;
	private EditText txtBalance;
	Accounts acArray[];
	ImageView img_heading;
	PrivateKey var1=null;	  
	String var5="",var3="";
	SecretKeySpec var2=null;
	String retvalwbs = "",respdesc ="";
	public OtherBankTranRTGS() {
	}

	@SuppressLint("ValidFragment")
	public OtherBankTranRTGS(MainActivity a) {
		System.out.println("OtherBankTranIFSC()" + a);
		act = a;
		otherBnkIfsc = this;
	}

	public void onBackPressed() {
		Fragment fragment = new FundTransferMenuActivity(act);
		FragmentManager fragmentManager = getFragmentManager();
		fragmentManager.beginTransaction()
				.replace(R.id.frame_container, fragment).commit();
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		System.out.println("onCreateView() OtherBankTranIFSC");
		var1 = act.var1;
		var3 = act.var3;
		View rootView = inflater.inflate(R.layout.other_bank_tranf_rtgs,
				container, false);
		dbms = new DatabaseManagement("shree_nagari.mbank", "listMobileBanking");
		dbs = new DialogBox(act);
		img_heading = (ImageView) rootView.findViewById(R.id.img_heading);
		img_heading.setBackgroundResource(R.mipmap.fund_trnsfer);
		Cursor c1 = dbms.selectFromTable("SHAREDPREFERENCE", "", null);// ("select * from ",
																		// null);
		if (c1 != null) {
			while (c1.moveToNext()) {
				stringValue = c1.getString(0);
			//	Log.e("retValStr", "...." + stringValue);
				custId = c1.getString(2);
			//	Log.e("custId", "......" + custId);
				userId = c1.getString(3);
			//	Log.e("UserId", "......" + userId);
				cust_mob_no = c1.getString(4);
			//	Log.e("cust_mobNO", "..." + cust_mob_no);
			}
		}
	
		spi_debit_account = (Spinner) rootView
				.findViewById(R.id.otherIfsc_spi_debit_account);
		spi_sel_beneficiery = (Spinner) rootView
				.findViewById(R.id.otherIfscspi_sel_beneficiery);
		spi_payment_option = (Spinner) rootView
				.findViewById(R.id.payment_options);

		btn_submit = (Button) rootView.findViewById(R.id.otherIfscbtn_submit);
		txtAccNo = (EditText) rootView.findViewById(R.id.otherIfsctxtAccNo);
		txtAmt = (EditText) rootView.findViewById(R.id.otherIfsctxtAmt);
		txtRemk = (EditText) rootView.findViewById(R.id.otherIfsctxtRemk);
		pb_wait = (ProgressBar) rootView.findViewById(R.id.otherIfscpro_bar);
		txtBank = (EditText) rootView.findViewById(R.id.otherIfsctxtBank);
		txtBranch = (EditText) rootView.findViewById(R.id.otherIfsctxtBranch);
		txtIfsc = (EditText) rootView.findViewById(R.id.otherIfsctxtIFSCCode);
		all_acnts = stringValue;
		btn_submit.setOnClickListener(this);
		txtAccNo.setText("");
		txtIfsc.setText("");
		btn_confirm = (Button) rootView.findViewById(R.id.btn_confirm);
		btn_con_back = (Button) rootView.findViewById(R.id.btn_confirm_back);
		txt_remark = (TextView) rootView.findViewById(R.id.txt_remark);
		txt_from = (TextView) rootView.findViewById(R.id.txt_from);
		txt_to = (TextView) rootView.findViewById(R.id.txt_to);
		txt_amount = (TextView) rootView.findViewById(R.id.txt_amount);
		txt_charges = (TextView) rootView.findViewById(R.id.txt_charges);
		txtTranId = (TextView) rootView.findViewById(R.id.txt_tranid);
		txt_trantype=(TextView)rootView.findViewById(R.id.txt_trantype);
		btn_confirm.setOnClickListener(this);
		btn_con_back.setOnClickListener(this);
		// btn_submit.setTypeface(tf_calibri);
		btn_home1 = (ImageView) rootView.findViewById(R.id.btn_home1);
		btn_home1.setOnClickListener(this);
		btn_logout = (ImageView) rootView.findViewById(R.id.btn_logout);
		btn_logout.setOnClickListener(this);
		//btn_back = (ImageButton) rootView.findViewById(R.id.btn_back);
		spinner_btn = (ImageButton) rootView.findViewById(R.id.spinner_btn);
		spinner_btn2 = (ImageButton) rootView.findViewById(R.id.spinner_btn2);
		spinner_btn3 = (ImageButton) rootView.findViewById(R.id.spinner_btn3);
		txt_heading = (TextView) rootView.findViewById(R.id.txt_heading);
		txt_heading
				.setText(getString(R.string.tabtitle_other_bank_fund_trans_rtgs));
		//btn_home.setImageResource(R.drawable.ic_home_d);
		//btn_back.setImageResource(R.drawable.backover);
		confirm_layout = (LinearLayout) rootView
				.findViewById(R.id.othr_confirm_layout);
		other_bnk_layout = (LinearLayout) rootView
				.findViewById(R.id.other_bnk_ifsc_layout);
		//btn_home.setOnClickListener(this);
		//btn_back.setOnClickListener(this);
		spinner_btn.setOnClickListener(this);
		spinner_btn2.setOnClickListener(this);

		String[] arrList = { "NEFT"};//"RTGS"
		ArrayAdapter<String> paymentOption = new ArrayAdapter<String>(act,R.layout.spinner_item, arrList);
		paymentOption
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		spi_payment_option.setAdapter(paymentOption);

		if (spi_debit_account == null)
			System.out.println("spi_debit_account is null");
		else
			System.out.println("spi_debit_account is not null");

		if (spi_sel_beneficiery == null)
			System.out.println("spi_sel_beneficiery is null");
		else
			System.out.println("spi_sel_beneficiery is not null");

		if (btn_submit == null)
			System.out.println("btn_submit is null");
		else
			System.out.println("btn_submit is not null");

		if (txtAccNo == null)
			System.out.println("txtAccNo is null");
		else
			System.out.println("txtAccNo is not null");

		if (txtAmt == null)
			System.out.println("txtAmt is null");
		else
			System.out.println("txtAmt is not null");

		if (txtRemk == null)
			System.out.println("txtRemk is null");
		else
			System.out.println("txtRemk is not null");

		if (pb_wait == null)
			System.out.println("pb_wait is null");
		else
			System.out.println("pb_wait is not null");
		txtBalance = (EditText) rootView.findViewById(R.id.sameBnkTrantxtBal);
		if (txtBalance == null)
			System.out.println("txtBalance is null");
		else
			System.out.println("txtBalance is not null");
		addAccounts(all_acnts);

		flag = chkConnectivity();
		if (flag == 0) {
			new CallWebService_fetch_all_beneficiaries().execute();
		}

		pb_wait.setMax(10);
		pb_wait.setProgress(1);
		pb_wait.setVisibility(ProgressBar.INVISIBLE);

		spi_sel_beneficiery
				.setOnItemSelectedListener(new OnItemSelectedListener() {
					String otherIfsctxtIFSCCode = "";
					String otherIfsctxtBank = "";
					String otherIfsctxtBranch = "";

					// Toast.makeText(this, "str=111 ",
					// Toast.LENGTH_LONG).show();

					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {

						Log.e("spi_sel_beneficiery ", "arg2== " + arg2);
						Log.e("spi_sel_beneficiery ", "arg3== " + arg3);
						String str = spi_sel_beneficiery.getItemAtPosition(spi_sel_beneficiery.getSelectedItemPosition()).toString();

						System.out.println("ben info :===========>" + benInfo);
						String nickname="";
						
						if (str.indexOf("Select Beneficiary") > -1) {
							txtAccNo.setText("");
							txtIfsc.setText("");
							txtAmt.setText("");
							txtRemk.setText("");
							//spi_payment_option.setAdapter(null);
						}
						else if (arg2 != 0) {
							String[] allStr = benInfo.split("~");

							for (int i = 1; i < allStr.length; i++) {
								String[] str1 = allStr[i].split("#");
								 nickname=str1[2] + "(" + str1[1] + ")";
								System.out.println("==== str :" + str);
								System.out
										.println("Beneficiary serial number:=====>"
												+ str1[0]);
								System.out.println("(" + str1[1] + ")");

								// if (str.indexOf("(" + str1[1] + ")") > -1)
								//str.indexOf(str1[2]) > -1
								if (str.equalsIgnoreCase(nickname)) {
									System.out
											.println("========== inside if ============");
									benSrno = str1[0];
									nickname=str1[2];
									benAccountNumber = str1[3];
									otherIfsctxtIFSCCode = str1[4];
									ifsCD = otherIfsctxtIFSCCode;
									Log.e("OTHERBANK", "ifsCD=====" + ifsCD);

									flag = chkConnectivity();
									if (flag == 0) {
										// new
										// CallWebServiceFetBnkBrn().execute();
									}
								}
							}// end for

							txtAccNo.setText(benAccountNumber.trim());
							txtIfsc.setText(ifsCD.trim());
						}
					}// end onItemSelected

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
					}
				});

		spi_debit_account
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {

						str=spi_debit_account.getSelectedItem().toString();
						Log.e("arg2= ","arg2="+arg2);
						Log.e("arg2= ","arg2="+arg2);
						Log.e("arg2= ","arg2="+arg2);
						if (arg2 == 0)
						{
							txtBalance.setText("");
						}
							
						else if (arg2 != 0) {
						Log.e("str= ","str="+str);
						Log.e("str= ","str="+str);
						Log.e("str= ","str="+str);
                        if(str.equalsIgnoreCase("Select Debit Account"))
						{
	                           txtBalance.setText("");
						}else
						{
						if (spi_debit_account.getCount() > 0) {
							String str = arrListTemp.get(spi_debit_account
									.getSelectedItemPosition()-1);

							String debitAc[] = str.split("-");
							System.out.println("account 1:" + debitAc[0]);// 5
							System.out.println("account 2:" + debitAc[1]);// 101
							// System.out.println("account 3:"+debitAc[2]);//SB
							System.out.println("account 4:" + debitAc[3]);// 7

							drBrnCD = debitAc[0];
							drSchmCD = debitAc[1];
							drAcNo = debitAc[3];
							Accounts selectedDrAccount = acArray[spi_debit_account
									.getSelectedItemPosition()-1];
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
						}
					  }
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
						// TODO Auto-generated method stub

					}

				});

		spi_debit_account.requestFocus();
		txtAmt.setFilters(new InputFilter[] { new DecimalDigitsInputFilter(2) });
		return rootView;
	}

	public void addAccounts(String str) {
		System.out.println("OtherBankTranRTGS IN addAccounts()" + str);

		try {
			ArrayList<String> arrList = new ArrayList<String>();
			String allstr[] = str.split("~");
			  arrList.add("Select Debit Account");
			int noOfAccounts = allstr.length;
			acArray = new Accounts[noOfAccounts];
			int j=0;
			for (int i = 0; i < noOfAccounts; i++) {
				str2 = allstr[i];
				String tempStr=str2;
				acArray[i] = new Accounts(str2);
				str2 = str2.replaceAll("#", "-");
				String accType = str2.split("-")[2];
				String oprcd = str2.split("-")[7];
				String str2Temp = str2;
				str2 = MBSUtils.get16digitsAccNo(str2);
				String withdrawalAllowed=allstr[i].split("#")[10];
				if ((accType.equals("SB") || accType.equals("CA") || accType
						.equals("LO")) && oprcd.equalsIgnoreCase("O")&& withdrawalAllowed.equalsIgnoreCase("Y")) 
				{
					acArray[j++] = new Accounts(tempStr);
					arrList.add(str2);
					arrListTemp.add(str2Temp);
				}
			}

			String[] debAccArr = new String[arrList.size()];
			debAccArr = arrList.toArray(debAccArr);
			
			ArrayAdapter<String> debAccs = new ArrayAdapter<String>(act,R.layout.spinner_item, debAccArr);
			debAccs.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spi_debit_account.setAdapter(debAccs);
		} 
		catch (Exception e) {
			System.out.println("" + e);
			// Toast.makeText(act, ""+e, Toast.LENGTH_LONG).show();
		}

	}// end addAccount

	private void addBeneficiaries(String retval) {
		System.out
				.println("================ IN addBeneficiaries() of OtherBankTranRTGS ======================");
		System.out.println("OtherBankTranRTGS IN addBeneficiaries()" + retval);
		Log.e("retval==", "retval==" + retval);
		Log.e("retval==", "retval==" + retval);
		Log.e("retval==", "retval==" + retval);
		try {
			ArrayList<String> arrList = new ArrayList<String>();
			String allstr[] = retval.split("~");

			int noOfben = allstr.length;
			Log.e("OTHERBNK", "noOfben==" + noOfben);
			Log.e("OTHERBNK", "noOfben==" + noOfben);
			Log.e("OTHERBNK", "noOfben==" + noOfben);
			String benName = "";
			arrList.add("Select Beneficiary");

			for (int i = 1; i < noOfben; i++) {
				System.out.println(i + "----STR1-----------" + allstr[i - 1]);
				String[] str2 = allstr[i].split("#");

				Log.e("OTHERBNK", "noOfben==" + noOfben);
				Log.e("OTHERBNK", "noOfben==" + noOfben);
				Log.e("OTHERBNK", "forth====" + str2[4]);
				Log.e("OTHERBNK", "forth====" + str2[4]);
				Log.e("OTHERBNK", "third====" + str2[3]);
				Log.e("OTHERBNK", "third====" + str2[3]);
				// String benName = "";
				if (!(str2[4].equals("NA") || (str2[3].equals("-9999")))) {
					benName = str2[2] + "(" + str2[1] + ")";
					arrList.add(benName);
					System.out
							.println("=============== benificiary Name is:======"
									+ benName);
				}
			}

			String[] benfArr = new String[arrList.size()];
			benfArr = arrList.toArray(benfArr);
			/*
			 * CustomeSpinnerAdapter accs = new CustomeSpinnerAdapter(act,
			 * R.layout.spinner_layout, benfArr);
			 */
			ArrayAdapter<String> benfAccs = new ArrayAdapter<String>(act,
					R.layout.spinner_item, benfArr);
			benfAccs.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spi_sel_beneficiery.setAdapter(benfAccs);

		} catch (Exception e) {
			System.out.println("" + e);
		}
	}// end addBeneficiaries

	public int chkConnectivity() {
		// pb_wait.setVisibility(ProgressBar.VISIBLE);
		ConnectivityManager cm = (ConnectivityManager) act
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		try {
			State state = ni.getState();
			boolean state1 = ni.isAvailable();
			System.out.println("OtherBankTranRTGS	in chkConnectivity () state1 ---------"+ state1);
			if (state1) {
				switch (state) {
				case CONNECTED:
					if (ni.getType() == ConnectivityManager.TYPE_MOBILE|| ni.getType() == ConnectivityManager.TYPE_WIFI) {

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
					// retMess = "Network Unavailable. Please Try Again.";
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

			Log.i("OtherBankTranRTGS",
					"NullPointerException Exception" + ne);
			flag = 1;
			// retMess = "Network Unavailable. Please Try Again.";
			retMess = getString(R.string.alert_000);
			showAlert(retMess);

		} catch (Exception e) {
			Log.i("OtherBankTranRTGS", "Exception" + e);
			flag = 1;
			// retMess = "Network Unavailable. Please Try Again.";
			retMess = getString(R.string.alert_000);
			showAlert(retMess);

		}
		return flag;
	}// end chkConnectivity

	@SuppressLint("StaticFieldLeak")
	class CallWebService_fetch_all_beneficiaries extends AsyncTask<Void, Void, Void> {

		String retval = "";
		LoadProgressBar loadProBarObj = new LoadProgressBar(act);
		JSONObject jsonObj = new JSONObject();
	
		protected void onPreExecute() {
			try {
				 respcode="";
  				reTval="";
  				getBeneficiariesrespdesc="";
				loadProBarObj.show();

				jsonObj.put("CUSTID", custId);
				jsonObj.put("SAMEBNK", "N");
				jsonObj.put("IMEINO", MBSUtils.getImeiNumber(act));
				jsonObj.put("SIMNO", MBSUtils.getSimNumber(act));
				jsonObj.put("METHODCODE","13"); 
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
				return null;
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
			} catch (JSONException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(getBeneficiariesrespdesc.length()>0)
			{
				showAlert(getBeneficiariesrespdesc);
			}
			else{
			// decryptedBeneficiaries="SUCCESS";
			if (reTval.indexOf("SUCCESS") > -1) {
				

				
				post_successfetch_all_beneficiaries(reTval);
				
			} else {
				//loadProBarObj.dismiss();
				if (reTval.indexOf("NODATA") > -1) {
					Toast.makeText(act, getString(R.string.alert_041),
							Toast.LENGTH_LONG).show();
					Fragment fragment = new FundTransferMenuActivity(act);
					FragmentManager fragmentManager = getFragmentManager();
					fragmentManager.beginTransaction()
							.replace(R.id.frame_container, fragment).commit();
				} else {
					retMess = getString(R.string.alert_069);
					showAlert(retMess);
				}
			}
			}
		}// end onPostExecute

	}// end callWbService
		// to get all Beneficiaries(call in init)
	public 	void post_successfetch_all_beneficiaries(String reTval)
	{
		 respcode="";

			getBeneficiariesrespdesc="";
		System.out.println("decryptedBeneficiaries:"
				+ reTval);

		benInfo = reTval;
		addBeneficiaries(reTval);
	}
	
	class CallWebServiceSaveTransfer extends AsyncTask<Void, Void, Void> {
		String retval = "";
		LoadProgressBar loadProBarObj = new LoadProgressBar(act);
		JSONObject jsonObj = new JSONObject();
		
		String amt;
		String accNo, debitAccno, benAcNo, amtStr, reMark;

		protected void onPreExecute() {
			try {
				
				respcode="";
    			reTval="";
    			saveTransferTranrespdesc="";
				// pb_wait.setVisibility(ProgressBar.VISIBLE);
    			loadProBarObj.show();
				accNo = txtAccNo.getText().toString().trim();
				debitAccno = spi_debit_account.getItemAtPosition(
						spi_debit_account.getSelectedItemPosition()).toString();
				benAcNo = spi_sel_beneficiery.getItemAtPosition(
						spi_sel_beneficiery.getSelectedItemPosition())
						.toString();
				amt = txtAmt.getText().toString().trim();
				reMark = txt_remark.getText().toString().trim();
				amtStr = txtAmt.getText().toString().trim();

				tranType = spi_payment_option.getItemAtPosition(
						spi_payment_option.getSelectedItemPosition())
						.toString();

				if (tranType.equalsIgnoreCase("RTGS"))
					tranType = "RT";
				else if (tranType.equalsIgnoreCase("NEFT"))
					tranType = "NT";
				String crAccNo = txt_to.getText().toString().trim();
				String charges = txt_charges.getText().toString().split(" ")[1];
				String drAccNo = txt_from.getText().toString().trim();

				drAccNo = strFromAccNo.substring(0, 16);
				crAccNo = strToAccNo;
				jsonObj.put("BENFSRNO", benSrno);
				jsonObj.put("CRACCNO", crAccNo);
				jsonObj.put("DRACCNO", drAccNo);
				jsonObj.put("AMOUNT", amt);
				jsonObj.put("REMARK", reMark);
				jsonObj.put("TRANSFERTYPE", tranType);
				jsonObj.put("IMEINO", MBSUtils.getImeiNumber(act));
				jsonObj.put("CUSTID", custId);
				jsonObj.put("CHARGES", onlyCharge);
				jsonObj.put("CHRGACCNO", chrgCrAccNo);
				jsonObj.put("TRANID", tranId);
				jsonObj.put("SERVCHRG", servChrg);
				jsonObj.put("CESS", cess);
				jsonObj.put("TRANPIN", tranmpin);
				jsonObj.put("SIMNO", MBSUtils.getMyPhoneNO(act));
				jsonObj.put("METHODCODE","16");

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
		}// end dodoInBackground2

		protected void onPostExecute(Void paramVoid) {
				loadProBarObj.dismiss();
			JSONObject jsonObj;
			try
			{
				String str=CryptoClass.Function6(var5,var2);
				 jsonObj = new JSONObject(str.trim());
			
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
					saveTransferTranrespdesc= jsonObj.getString("RESPDESC");
				}
				else
				{	
					saveTransferTranrespdesc= "";
				}
			} catch (JSONException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(saveTransferTranrespdesc.length()>0)
			{
				showAlert(saveTransferTranrespdesc);
			}
			else{
				
			if (reTval.indexOf("SUCCESS") > -1) {
				post_successsaveTransferTran(reTval);
			} else {
				if (reTval.indexOf("LIMIT_EXCEEDS") > -1) {
					retMess = getString(R.string.alert_031);
					FragmentManager fragmentManager;
					Fragment fragment = new FundTransferMenuActivity(act);
					act.setTitle(getString(R.string.lbl_fund_transfer));
					fragmentManager = getFragmentManager();
					fragmentManager.beginTransaction()
							.replace(R.id.frame_container, fragment).commit();
					// loadProBarObj.dismiss();
				} else if (reTval.indexOf("DUPLICATE") > -1) {

					retMess = getString(R.string.alert_119) + tranId + "\n"
							+ getString(R.string.alert_120);
					showAlert(retMess);
					FragmentManager fragmentManager;
					Fragment fragment = new FundTransferMenuActivity(act);
					act.setTitle(getString(R.string.lbl_fund_transfer));
					fragmentManager = getFragmentManager();
					fragmentManager.beginTransaction()
							.replace(R.id.frame_container, fragment).commit();
				} else if (reTval.indexOf("WRONGTRANPIN") > -1) {
					String msg[] = reTval.split("~");
					String first = msg[1];
					String second = msg[2];
				
					int count = Integer.parseInt(second);
					count = 5 - count;
					//loadProBarObj.dismiss();
					retMess = getString(R.string.alert_125_1) + " " + count
							+ " " + getString(R.string.alert_125_2);
					showAlert(retMess);
				} else if (reTval.indexOf("BLOCKEDFORDAY") > -1) {
					//loadProBarObj.dismiss();
					retMess = getString(R.string.login_alert_005);
					showAlert(retMess);
				} else if (reTval.indexOf("FAILED~") > -1) {
					Log.e("in failed", "--------");
					String msg[] = reTval.split("~");
					if (msg.length > 3) {
						// String wrongtran=msg[1];
						postingStatus = msg[1];
						req_id = msg[2];
						String errorMsg = msg[3];
						// if(msg[2]!=null || msg[2].length()>0)
						if (req_id.length() > 0) {

							if (req_id != null || req_id.length() > 0)
								retMess = getString(R.string.alert_162) + " "
										+ req_id;

						} else if (errorMsg.length() > 0) {
							retMess = getString(R.string.alert_032) + errorMsg;
						}

					} else {
						retMess = getString(R.string.alert_032);
					}
					showAlert(retMess);

					FragmentManager fragmentManager;
					Fragment fragment = new FundTransferMenuActivity(act);
					act.setTitle(getString(R.string.lbl_fund_transfer));
					fragmentManager = getFragmentManager();
					fragmentManager.beginTransaction()
							.replace(R.id.frame_container, fragment).commit();

				}

				else if (reTval.indexOf("FAILED") > -1)
				{
					
					if(reTval.split("~")[1].length()>0)
					{
						errorCode=reTval.split("~")[1];
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
				else {
					retMess = getString(R.string.alert_032);
					showAlert(retMess);
		}
			}// end else
			}
		}// end onPostExecute

	}// end callWbService2
		// to get values from getBnkBrn method.....
	public 	void post_successsaveTransferTran(String reTval)
	{

		respcode="";
		saveTransferTranrespdesc="";
		String msg[] = reTval.split("~");
		if (msg.length > 2) {
			if (msg[2] != null || msg[2].length() > 0) {
				postingStatus = msg[1];
				req_id = msg[2];
				Log.e("Ganesh ", " Failed NA req_id=" + req_id);
				Log.e("Ganesh ", "Failed NA req_id=" + postingStatus);
				// retMess = getString(R.string.alert_150)+" "+req_id;
				retMess = getString(R.string.alert_030) + " "
						+ getString(R.string.alert_121) + " " + req_id;
			}
		} else {
			// retMess = getString(R.string.alert_163);
			retMess = getString(R.string.alert_030);
		}
		showAlert(retMess);

		FragmentManager fragmentManager;
		Fragment fragment = new FundTransferMenuActivity(act);
		act.setTitle(getString(R.string.lbl_fund_transfer));
		fragmentManager = getFragmentManager();
		fragmentManager.beginTransaction()
				.replace(R.id.frame_container, fragment).commit();
		System.out
				.println("================== in onPostExecute  else ============================");

	}
	class CallWebServiceFetBnkBrn extends AsyncTask<Void, Void, Void> {
		String retVal = "";
		LoadProgressBar loadProBarObj = new LoadProgressBar(act);
		JSONObject jsonObj = new JSONObject();
	
		protected void onPreExecute() {
			loadProBarObj.show();

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
		}// end dodoInBackground2

		protected void onPostExecute(Void paramVoid) {
			txtBank.setText(bnCD);
			txtBranch.setText(brCD);
			String str=CryptoClass.Function6(var5,var2);
			 
			String decryptedBeneficiaries = str.trim();
			decryptedBeneficiaries = "SUCCESS";
			loadProBarObj.dismiss();
			if (decryptedBeneficiaries.indexOf("SUCCESS") > -1) {
				
				txtBank.setText("BOI");
				txtBranch.setText("Sangli");
				
			} else {
				retMess = getString(R.string.alert_069);
				//loadProBarObj.dismiss();
				showAlert(retMess);
			}
		}// end doPost
	}// end CallWebServiceFetBnkBrn

	class CallWebServiceGetSrvcCharg extends AsyncTask<Void, Void, Void> {
		String retval = "";
		LoadProgressBar loadProBarObj = new LoadProgressBar(act);

		//	String[] xmlTags = {"CUSTID","TRANTYPE","DRACCNO","AMOUNT","CRACCNO","IMEINO"};
		String[] xmlTags = {"PARAMS"};
		String[] valuesToEncrypt = new String[1];
		JSONObject jsonObj = new JSONObject();
		String generatedXML = "";
		String accNo, debitAccno, benAcNo, amt, reMark;

		protected void onPreExecute() {
			try {
				// pb_wait.setVisibility(ProgressBar.VISIBLE);
				loadProBarObj.show();
				accNo = txtAccNo.getText().toString().trim();
				debitAccno = arrListTemp.get(spi_debit_account.getSelectedItemPosition());
				benAcNo = spi_sel_beneficiery.getItemAtPosition(
						spi_sel_beneficiery.getSelectedItemPosition()).toString();
			/*tranType=spi_payment_option.getItemAtPosition(
					spi_payment_option.getSelectedItemPosition()).toString();*/
				amt = txtAmt.getText().toString().trim();
				reMark = txtRemk.getText().toString().trim();

			/*if(tranType.equalsIgnoreCase("RTGS"))
				tranType="RT";
			else if(tranType.equalsIgnoreCase("NEFT"))
				tranType="NT";*/
				Log.e("ohtertranImpsbtn_submit", " onPreExecute ");
				jsonObj.put("CUSTID", custId);
				jsonObj.put("TRANTYPE", "IMPS");
				jsonObj.put("DRACCNO", debitAccno);
				jsonObj.put("AMOUNT", amt);
				jsonObj.put("CRACCNO", accNo);
				jsonObj.put("IMEINO", MBSUtils.getImeiNumber(act));
				jsonObj.put("SIMNO", MBSUtils.getMyPhoneNO(act));

				//	valuesToEncrypt[0] = custId;
				//	valuesToEncrypt[1] = "IMPS";
				//	valuesToEncrypt[2] = debitAccno;
				//	valuesToEncrypt[3] = amt;
				//	valuesToEncrypt[4] = accNo;
				//	valuesToEncrypt[5] = MBSUtils.getImeiNumber(act);
			} catch (JSONException je) {
				je.printStackTrace();
			}

		/*	Log.e("CallWebServiceGetSrvcCharg","custId=="+valuesToEncrypt[0] );
			Log.e("CallWebServiceGetSrvcCharg","tranType=="+valuesToEncrypt[1] );
			Log.e("CallWebServiceGetSrvcCharg","debitAccno=="+valuesToEncrypt[2] );
			Log.e("CallWebServiceGetSrvcCharg","amt=="+valuesToEncrypt[3] );
			Log.e("CallWebServiceGetSrvcCharg","accNo=="+valuesToEncrypt[4] );
			Log.e("CallWebServiceGetSrvcCharg","IMEI=="+valuesToEncrypt[5] );*/
			valuesToEncrypt[0] = jsonObj.toString();
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
			//	String[] xmlTags = { "RETVAL" };
			//	String[] xml_data = CryptoUtil.readXML(retval, xmlTags);
			String[] xml_data = CryptoUtil.readXML(retval,
					new String[]{"PARAMS"});
			String decryptedAccounts = xml_data[0];
			loadProBarObj.hide();
			Log.e("DEBUG@ANAND", decryptedAccounts);
			if (decryptedAccounts.indexOf("SUCCESS") > -1) {
				act.frgIndex = 52;

				other_bnk_layout.setVisibility(other_bnk_layout.INVISIBLE);
				confirm_layout.setVisibility(confirm_layout.VISIBLE);
				String trantype = "IMPS";
				String retStr = xml_data[0].split("~")[1];
				Log.e("HELL", retStr);
				String[] val = retStr.split("#");
				txt_heading.setText("Confirmation");
				txt_remark.setText(strRemark);
				txt_from.setText(cust_mob_no);
//				txt_to.setText(txtMobNo.getText().toString().trim());
//				txt_mmid.setText(txtMobID.getText().toString().trim());
				txt_amount.setText("INR " + strAmount);
				txt_charges.setText("INR " + val[0]);
				txt_trantype.setText(trantype);

				chrgCrAccNo = val[1];
				tranId = val[2];
				servChrg = val[3];
				cess = val[4];
				Log.e("OTHERBNKTRAN", "servChrg===" + servChrg + "==cess==" + cess);
				if (chrgCrAccNo.length() == 0 || chrgCrAccNo.equalsIgnoreCase("null"))
					chrgCrAccNo = "";

				if (servChrg.equalsIgnoreCase("null"))
					servChrg = "0";

				if (cess.equalsIgnoreCase("null"))
					cess = "0";

				Log.e("OTHERBNKTRAN", "2222servChrg===" + servChrg + "==cess==" + cess);
				txt_charges.setText("INR " + (Float.parseFloat(val[0]) + Float.parseFloat(servChrg) + Float.parseFloat(cess)));
			} else {
				if (decryptedAccounts.indexOf("TRANAMTLIMIT") > -1) {

					/*retMess = getString(R.string.alert_149)+
							decryptedAccounts.split("~")[2].split("#")[0]+
							 getString(R.string.alert_148) +
							decryptedAccounts.split("~")[2].split("#")[1];*/
					String errCd = decryptedAccounts.split("~")[2];
					if (errCd.equalsIgnoreCase("01"))
						retMess = getString(R.string.alert_148);
					else
						retMess = getString(R.string.alert_149);
					//loadProBarObj.dismiss();
					showAlert(retMess);//setAlert();
					//showAlert("IF: "+decryptedAccounts);//setAlert();
				} else if (decryptedAccounts.indexOf("SingleLimitExceeded") > -1) {
					retMess = getString(R.string.alert_193);
					//loadProBarObj.dismiss();
					showAlert(retMess);
				} else if (decryptedAccounts.indexOf("TotalLimitExceeded") > -1) {
					retMess = getString(R.string.alert_194);
					///loadProBarObj.dismiss();
					showAlert(retMess);
				} else if (decryptedAccounts.indexOf("STOPTRAN") > -1) {
					retMess = getString(R.string.Stop_Tran);
					//loadProBarObj.dismiss();
					showAlert(retMess);
				} else {
					// this case consider when in retval string contains only  "FAILED"
					retMess = getString(R.string.alert_032);
					//	loadProBarObj.dismiss();
					showAlert(retMess);//setAlert();
					//System.out
					//		.println("================== in onPostExecute 2 ============================");
				}
			}// end else
		}// end onPostExecute
	}// end CallWebServiceGetSrvcCharg

	/*class CallWebServiceGetSrvcCharg extends AsyncTask<Void, Void, Void> {
		String retval = "";
		LoadProgressBar loadProBarObj = new LoadProgressBar(act);
		JSONObject jsonObj = new JSONObject();
		
		String accNo, debitAccno, benAcNo, amt, reMark;

		protected void onPreExecute() {
			try {
				respcode="";
				reTval="";
				getTransferChargesrespdesc="";
				// pb_wait.setVisibility(ProgressBar.VISIBLE);
				loadProBarObj.show();
				accNo = txtAccNo.getText().toString().trim();
				debitAccno = spi_debit_account.getSelectedItem().toString();

				benAcNo = spi_sel_beneficiery.getSelectedItem().toString();

				tranType = spi_payment_option.getItemAtPosition(
						spi_payment_option.getSelectedItemPosition())
						.toString();

				amt = txtAmt.getText().toString().trim();
				reMark = txtRemk.getText().toString().trim();
				if (tranType.equalsIgnoreCase("RTGS"))
				{
					tranType = "RT";
				    transaction = "RTGS";
				}
				 else if (tranType.equalsIgnoreCase("NEFT"))
				 {
					 tranType = "NT";
					 transaction="NEFT";
				 }
				debitAccno=debitAccno.substring(0,16);
				jsonObj.put("CUSTID", custId);
				jsonObj.put("TRANTYPE", tranType);
				jsonObj.put("DRACCNO", debitAccno);
				jsonObj.put("AMOUNT", amt);
				jsonObj.put("CRACCNO", accNo);
				jsonObj.put("IMEINO", MBSUtils.getImeiNumber(act));
				jsonObj.put("SIMNO", MBSUtils.getMyPhoneNO(act));
				jsonObj.put("METHODCODE","28");

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
				return null;
			}// end try
			catch (Exception e) {
				e.printStackTrace();
				System.out.println("SameBankTransfer   Exception" + e);
			}
			return null;
		}// end dodoInBackground2

		protected void onPostExecute(Void paramVoid) {
			loadProBarObj.dismiss();
				 JSONObject jsonObj;
	   			try
	   			{
	   				String str=CryptoClass.Function6(var5,var2);
	   			 jsonObj = new JSONObject(str.trim());
	   			
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
	   					getTransferChargesrespdesc= jsonObj.getString("RESPDESC");
	   				}
	   				else
	   				{	
	   					getTransferChargesrespdesc= "";
	   				}
	   			} catch (JSONException e) 
	   			{
	   				// TODO Auto-generated catch block
	   				e.printStackTrace();
	   			}
	   			
	   			if(getTransferChargesrespdesc.length()>0)
	   			{
	   				showAlert(getTransferChargesrespdesc);
	   			}
	   			else{
			//Log.e("ohtertranImpsbtn_submit", " decryptedAccounts "
					//+ decryptedAccounts);
			// SUCCESS~55#null#180206603#2.76#0#SUCCESS~nextDay~NT

			if (reTval.indexOf("SUCCESS") > -1) {
			
				post_successGetSrvcCharg(reTval);
				
			} else {
				if (reTval.indexOf("TRANAMTLIMIT") > -1) {

					String errCd = reTval.split("~")[2];
					if (errCd.equalsIgnoreCase("01"))
						retMess = getString(R.string.alert_148);
					else
						retMess = getString(R.string.alert_149);
					//loadProBarObj.dismiss();
					showAlert(retMess);// setAlert();
					// showAlert("IF: "+decryptedAccounts);//setAlert();
				} 
				 else if (reTval.indexOf("LOWBALANCE") > -1) {
						retMess = getString(R.string.alert_176);
						//loadProBarObj.dismiss();
						showAlert(retMess);
					}
				 else if (reTval.indexOf("SingleLimitExceeded") > -1) {
						retMess = getString(R.string.alert_193);
						//loadProBarObj.dismiss();
						showAlert(retMess);
					}
				
				 else if (reTval.indexOf("TotalLimitExceeded") > -1) {
						retMess = getString(R.string.alert_194);
						//loadProBarObj.dismiss();
						showAlert(retMess);
					}
				 else if (reTval.indexOf("UNDERCONSTRUCTION") > -1) {
						retMess = getString(R.string.alert_underconstruction);
					//	loadProBarObj.dismiss();
						showAlert(retMess);
					}else if (reTval.indexOf("STOPTRAN") > -1)
				{
					retMess = getString(R.string.Stop_Tran);
					//loadProBarObj.dismiss();
					showAlert(retMess);
				}
				else {
					// this case consider when in retval string contains only
					// "FAILED"
					retMess = getString(R.string.alert_032);
					//loadProBarObj.dismiss();
					showAlert(retMess);// setAlert();
				}
			}// end else
	   			}
		}// end onPostExecute
	}// end CallWebServiceGetSrvcCharg*/

	public 	void post_successGetSrvcCharg(String reTval)
	{

		respcode="";
		getTransferChargesrespdesc="";

		act.frgIndex = 52;
		//loadProBarObj.dismiss();

		retStr = reTval.split("~")[1];

		String retStr1 = "";
		retStr1 = reTval.split("~")[2];
		// Toast.makeText(act, "xml_data[0]="+xml_data[0],
		// Toast.LENGTH_LONG).show();

		// Toast.LENGTH_LONG).show();
		if (retStr1.equalsIgnoreCase("nextDay")) {
			proceedTransaction();
		} else {
			Log.e("HELL", retStr);

			other_bnk_layout.setVisibility(other_bnk_layout.INVISIBLE);
			confirm_layout.setVisibility(confirm_layout.VISIBLE);
			String[] val = retStr.split("#");
			txt_heading.setText("Confirmation");
			txt_remark.setText(strRemark);
			txt_trantype.setText(transaction);
			txt_from.setText(strFromAccNo);
			txt_to.setText(strToAccNo);
			txt_amount.setText("INR " + strAmount);
			//txt_charges.setText("INR " + val[0]);
			onlyCharge = val[0];
			chrgCrAccNo = val[1];
			tranId = val[2];
			servChrg = val[3];
			cess = val[4];
                 	gst= val[5];
			Log.e("OTHERBNKTRAN", "servChrg===" + servChrg + "==cess=="
					+ cess);
			if (chrgCrAccNo.length() == 0
					|| chrgCrAccNo.equalsIgnoreCase("null"))
				chrgCrAccNo = "";

			if (servChrg.equalsIgnoreCase("null"))
				servChrg = "0";

			if (cess.equalsIgnoreCase("null"))
				cess = "0";
                        if (gst.equalsIgnoreCase("null"))
				gst = "0";

			Log.e("OTHERBNKTRAN", "2222servChrg===" + servChrg
					+ "==cess==" + cess);
			
			  txt_charges.setText("INR " + (Float.parseFloat(val[0]) +
			  Float.parseFloat(servChrg) + Float .parseFloat(cess)+
                          Float.parseFloat(gst)));
			 
		}

	
	}
	public void proceedTransaction() {
		DialogBox dbs = new DialogBox(act);
		dbs.get_adb().setMessage(getString(R.string.alert_172));
		dbs.get_adb().setPositiveButton("OK",
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface arg0, int arg1) {
						Intent in = new Intent(getActivity(),
								NewDashboard.class);
						startActivity(in);
						// arg0.cancel();
					}
				});

		dbs.get_adb().show();
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		/*case R.id.btn_back:
			Fragment fragment = new FundTransferMenuActivity(act);
			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.frame_container, fragment).commit();
			break;*/

		case R.id.btn_home1:
			Intent in = new Intent(act, NewDashboard.class);
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
		case R.id.spinner_btn:
			spi_debit_account.performClick();
			break;

		case R.id.spinner_btn2:
			spi_sel_beneficiery.performClick();
			break;
		case R.id.spinner_btn3:
			spi_payment_option.performClick();
			
			break;

		case R.id.otherIfscbtn_submit:
			strFromAccNo = spi_debit_account.getSelectedItem().toString();
			strToAccNo = txtAccNo.getText().toString().trim();
			strAmount = txtAmt.getText().toString().trim().trim();
			strRemark = txtRemk.getText().toString().trim().trim();
			transferType = spi_payment_option.getSelectedItem().toString();
			 String balString = txtBalance.getText().toString().trim();
			if(balString.length()>0)
			{
				balString=balString.substring(0,balString.length()-2);
				balance=Double.parseDouble(balString);
				balance=Math.abs(balance);
				}
			/* balance=Double.parseDouble(balString);
			balance=Math.abs(balance);*/
			String debitAcc = strFromAccNo.substring(0, 16);
			 if(debitAcc.indexOf("Select")> -1){
					showAlert(getString(R.string.alert_174));
				}
			 else if (strToAccNo.length() == 0) {
				showAlert(getString(R.string.alert_098));
			} else if (strToAccNo.equalsIgnoreCase(strFromAccNo)) {
				showAlert(getString(R.string.alert_107));
			} else if (strAmount.matches("")) {
				showAlert(getString(R.string.alert_033));
			} else if(strAmount.length()==1 && strAmount.equalsIgnoreCase("."))
			{
				showAlert(getString(R.string.alert_195));
			}
			else if (Double.parseDouble(strAmount) == 0) {
				showAlert(getString(R.string.alert_034));
			}/* else if(transferType.equalsIgnoreCase("RTGS") && (Double.parseDouble(strAmount) <200000)) {
				showAlert(getString(R.string.alert_186));
			}*/
			else if (strRemark.length() == 0) {
				showAlert(getString(R.string.alert_165));
			}
			else if(Double.parseDouble(strAmount)>balance)
			{
				showAlert(getString(R.string.alert_176));
			}
			else if(debitAcc.equalsIgnoreCase("Select Debit Acc")){
				showAlert(getString(R.string.alert_174));
			}
			
			else if(transferType.equalsIgnoreCase("NEFT") && Double.parseDouble(strAmount)>=200000) 
			{
			 showAlert(getString(R.string.alert_147)); 
			} 
			else if(transferType.equalsIgnoreCase("RTGS") && Double.parseDouble(strAmount)<200000) 
			{
			 showAlert(getString(R.string.alert_148_1)); 
			 }
			
			else {
				try {
					this.flag = chkConnectivity();
					if (this.flag == 0) {
						new CallWebServiceGetSrvcCharg().execute();
					}
				} catch (Exception e) {
					e.printStackTrace();
					System.out
							.println("Exception in CallWebServiceGetSrvcCharg is:"
									+ e);
				}
			}
			break;
		case R.id.btn_confirm:
			String str = txtAmt.getText().toString().trim();
			String str2 = txtRemk.getText().toString().trim();
			String accNo = txtAccNo.getText().toString().trim();
			String bnk = txtBank.getText().toString().trim();
			String brnch = txtBranch.getText().toString().trim();
			String ifscCd = txtIfsc.getText().toString().trim();
			if (accNo.length() == 0 || ifscCd.length() == 0) {
				if (accNo.length() == 0)
					retMess = "Account number " + getString(R.string.alert_068);
				if (ifscCd.length() == 0)
					retMess = "IFSC code " + getString(R.string.alert_068);
				if (retMess != null && retMess.length() > 0)
					showAlert(retMess);
			} else if (str.length() == 0) {
				retMess = getString(R.string.alert_033);
				showAlert(retMess);
				txtAmt.requestFocus();
				} else {
				// int amt = Integer.parseInt(str); Gives error for fraction so
				double amt = Double.parseDouble(str);
				if (amt <= 0) {
						retMess = getString(R.string.alert_034);
					showAlert(retMess);
					txtAmt.requestFocus();
				} else {

					{
						InputDialogBox inputBox = new InputDialogBox(act);
						inputBox.show();
					} // end else
				}
			}// end if
			break;
		case R.id.btn_confirm_back:
			confirm_layout.setVisibility(confirm_layout.INVISIBLE);
	         txt_heading
			.setText(getString(R.string.tabtitle_other_bank_fund_trans_rtgs));
	        other_bnk_layout.setVisibility(other_bnk_layout.VISIBLE);
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


	public void showAlert(final String str) {
		// Toast.makeText(this, str, Toast.LENGTH_LONG).show();
		ErrorDialogClass alert = new ErrorDialogClass(act, "" + str) {

			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.btn_ok:
					if((str.equalsIgnoreCase(getBeneficiariesrespdesc)) &&  (respcode.equalsIgnoreCase("0")))
					{
						post_successfetch_all_beneficiaries(reTval);
					}
					else if((str.equalsIgnoreCase(getBeneficiariesrespdesc)) && (respcode.equalsIgnoreCase("1")))
					{
						this.dismiss();
					}
					if((str.equalsIgnoreCase(saveTransferTranrespdesc)) &&  (respcode.equalsIgnoreCase("0")))
					{
						post_successsaveTransferTran(reTval);
					}
					else if((str.equalsIgnoreCase(saveTransferTranrespdesc)) && (respcode.equalsIgnoreCase("1")))
					{
						this.dismiss();
					}
					if((str.equalsIgnoreCase(getTransferChargesrespdesc)) &&  (respcode.equalsIgnoreCase("0")))
					{
						post_successGetSrvcCharg(reTval);
					}
					else if((str.equalsIgnoreCase(getTransferChargesrespdesc)) && (respcode.equalsIgnoreCase("1")))
					{
						this.dismiss();
					}
				else if (noAccounts) {
						if (other_bnk_layout.getVisibility() == View.VISIBLE) {
							Fragment fragment = new FundTransferMenuActivity(
									act);
							// act.setTitle(getString(R.string.lbl_fund_transfer));
							FragmentManager fragmentManager = otherBnkIfsc
									.getFragmentManager();
							fragmentManager.beginTransaction()
									.replace(R.id.frame_container, fragment)
									.commit();
							act.frgIndex = 5;
						} else if (confirm_layout.getVisibility() == View.VISIBLE) {
							confirm_layout
									.setVisibility(confirm_layout.INVISIBLE);
							other_bnk_layout
									.setVisibility(other_bnk_layout.VISIBLE);
							act.frgIndex = 51;
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

				// System.out.println("========= inside onClick ============***********");
				String str = mpin.getText().toString();
				tranmpin=str;
				encrptdTranMpin = ListEncryption.encryptData(custId + str);
				if (str.length() == 0) {
					retMess = getString(R.string.alert_116);
					showAlert(retMess);// setAlert();
					this.show();
				} else if (str.length() != 6) {
					retMess = getString(R.string.alert_037);
					showAlert(retMess);// setAlert();
					this.show();
				} else {

					{
						saveData();
						this.hide();
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

	public void saveData() {
		try {
			System.out.println("--------------- 44 ------------");
			flag = chkConnectivity();
			if (flag == 0) {
				new CallWebServiceSaveTransfer().execute();

			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Exception in onClick:==" + e);

		}
	}// end saveData
}// end OtherBankTranIFSC

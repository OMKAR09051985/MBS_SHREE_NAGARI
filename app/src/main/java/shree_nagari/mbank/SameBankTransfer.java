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
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.AsyncTask;
import android.os.Build;
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

public class SameBankTransfer extends Fragment implements OnClickListener {
	
	private String benInfo = "";
	
	ImageView btn_home1,btn_logout;// btn_back;
	 DialogBox dbs;
         DatabaseManagement dbms;
	Button btn_submit, btn_confirm, btn_con_back;
	TextView txt_heading, txt_remark, txt_from, txt_to, txt_amount,
			txt_charges,txt_trantype;
	int cnt = 0;
	TextView cust_nm, txtTranId;
	boolean status;
	 double balance ;
	// SharedPreferences.Editor e;
	int flag = 0, frmno = 0, tono = 0;
	Intent in;
	ProgressBar pb_wait;
	String flg ="false";
	Spinner spi_debit_account, spi_sel_beneficiery;
	StopPayment stp = null;
	String str = "", str2 = "", stringValue = "", benSrno = "", strFromAccNo,
			strToAccNo, strAmount, strRemark, benAccountNumber = "",
			drBrnCD = "", drSchmCD = "", drAcNo = "", mobPin = "",
			chrgCrAccNo = "", tranPin = "", retMess = "", custId = "",
			cust_name = "", acnt_inf, all_acnts, tranId = "";
	String respcode="",reTval="",getBeneficiariesrespdesc="",saveTransferTranrespdesc="",getTransferChargesrespdesc="";
	EditText txtAccNo, txtAmt, txtRemk, txtBalance;
	SameBankTransfer samBnkObj;
	MainActivity act;
	View mainView;
	LinearLayout confirm_layout, same_bnk_layout;
	boolean noAccounts;
	private ImageButton spinenr_btn2;
	private ImageButton spinenr_btn;
	ArrayList<String> arrListTemp = new ArrayList<String>();
	private LayoutInflater inflater;
	SameBankTransfer sameBnkTran;
	private String userId,errorCode="";
	public String encrptdTranMpin,tranmpin="";
	public String encrptdUTranMpin;
	Accounts acArray[];
	ImageView img_heading;
	PrivateKey var1=null;	  
	String var5="",var3="";
	SecretKeySpec var2=null;
	String retvalwbs = "",respdesc ="";
	
	public SameBankTransfer() {
		sameBnkTran = this;
	}

	@SuppressLint("ValidFragment")
	public SameBankTransfer(MainActivity m) {
		act = m;
		sameBnkTran = this;
		// System.out.println("============== in constructor 1============");
	}

	@SuppressLint("WrongConstant")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		View rootView = inflater.inflate(R.layout.same_bank_transfer,
				container, false);
		var1 = act.var1;
		var3 = act.var3;
		noAccounts = false;
        dbms = new DatabaseManagement("shree_nagari.mbank", "listMobileBanking");
		this.dbs = new DialogBox(act);
	

        Cursor c1=dbms.selectFromTable("SHAREDPREFERENCE", "", null);//("select * from ", null);
        if(c1!=null)
        {
        	while(c1.moveToNext())
	        {	
        		stringValue=c1.getString(0);
        	//	Log.e("retValStr","......"+stringValue);
        		custId=c1.getString(2);
	        //	Log.e("custId","......"+custId);
	        	userId=c1.getString(3);
		    //	Log.e("userId","......"+userId);
	        }
        }
		
		btn_home1 =  rootView.findViewById(R.id.btn_home1);
		btn_logout =  rootView.findViewById(R.id.btn_logout);
		btn_logout.setVisibility(View.GONE);
		confirm_layout = (LinearLayout) rootView
				.findViewById(R.id.confirm_layout);
		same_bnk_layout = (LinearLayout) rootView
				.findViewById(R.id.same_bnk_layout);
		img_heading = (ImageView) rootView.findViewById(R.id.img_heading);
		img_heading.setBackgroundResource(R.mipmap.fund_trnsfer);
		btn_home1.setOnClickListener(this);
		btn_logout.setOnClickListener(this);
		txt_heading = (TextView) rootView.findViewById(R.id.txt_heading);
		txt_heading.setText(getString(R.string.lbl_same_bnk_trans));
        txt_trantype=(TextView)rootView.findViewById(R.id.txt_trantype);
		btn_confirm = (Button) rootView.findViewById(R.id.btn_confirm);
		btn_con_back= (Button) rootView.findViewById(R.id.btn_confirm_back);
		txt_remark = (TextView) rootView.findViewById(R.id.txt_remark);
		txt_from = (TextView) rootView.findViewById(R.id.txt_from);
		txt_to = (TextView) rootView.findViewById(R.id.txt_to);
		txt_amount = (TextView) rootView.findViewById(R.id.txt_amount);
		txt_charges = (TextView) rootView.findViewById(R.id.txt_charges);
		txtTranId = (TextView) rootView.findViewById(R.id.txt_tranid);
		btn_confirm.setOnClickListener(this);
		btn_con_back.setOnClickListener(this);
		spinenr_btn2 = (ImageButton) rootView.findViewById(R.id.spinner_btn2);
		spinenr_btn2.setOnClickListener(this);
		spinenr_btn = (ImageButton) rootView.findViewById(R.id.spinner_btn);
		spinenr_btn.setOnClickListener(this);

		spi_debit_account = (Spinner) rootView
				.findViewById(R.id.sameBnkTranspi_debit_account);

		if (spi_debit_account == null)
			System.out.println("spi_debit_account is null");
		else {
			System.out.println("spi_debit_account is not null");
			spi_debit_account.requestFocus();
		}

		spi_sel_beneficiery = (Spinner) rootView
				.findViewById(R.id.sameBnkTranspi_sel_beneficiery);
		if (spi_sel_beneficiery == null)
			System.out.println("spi_sel_beneficiery is null");
		else
			System.out.println("spi_sel_beneficiery is not null");

		btn_submit = (Button) rootView.findViewById(R.id.sameBnkTranbtn_submit);
		if (btn_submit == null)
			System.out.println("btn_submit is null");
		else
			System.out.println("btn_submit is not null");

		txtAccNo = (EditText) rootView.findViewById(R.id.sameBnkTrantxtAccNo);
		if (txtAccNo == null)
			System.out.println("txtAccNo is null");
		else
			System.out.println("txtAccNo is not null");
               	txtBalance = (EditText) rootView.findViewById(R.id.sameBnkTrantxtBal);
		if (txtBalance == null)
			System.out.println("txtBalance is null");
		else
			System.out.println("txtBalance is not null");
		txtAmt = (EditText) rootView.findViewById(R.id.sameBnkTrantxtAmt);
		if (txtAmt == null)
			System.out.println("txtAmt is null");
		else
			System.out.println("txtAmt is not null");

		txtRemk = (EditText) rootView.findViewById(R.id.sameBnkTrantxtRemk);
		if (txtRemk == null)
			System.out.println("txtRemk is null");
		else
			System.out.println("txtRemk is not null");

		pb_wait = (ProgressBar) rootView.findViewById(R.id.sameBnkTranpro_bar);
		if (pb_wait == null)
			System.out.println("pb_wait is null");
		else
			System.out.println("pb_wait is not null");

		btn_submit.setOnClickListener(this);

		// btn_submit.setTypeface(tf_calibri);
		// logic that set text box value according to spinner
		spi_sel_beneficiery
				.setOnItemSelectedListener(new OnItemSelectedListener() {
					String benAccountNumber = "";
					String niknm="";
					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {

						// System.out.println("benInfo  ========>" + benInfo);

						String str = spi_sel_beneficiery.getItemAtPosition(
								spi_sel_beneficiery.getSelectedItemPosition())
								.toString();

						if (str.indexOf("Select Beneficiary") > -1) {
							txtAccNo.setText("");
						}
                                             if (arg2 != 0) {
						String allStr[] = benInfo.split("~");
						// String benAcc[]=benAcNo.split("(");
						for (int i = 1; i <= allStr.length; i++) {
							String str1[] = allStr[i - 1].split("#");
							niknm= str1[2] + "(" + str1[1] + ")";
							 //System.out.println("==== str :" + str);
							// System.out.println("Beneficiary serial number:=====>"
							// + str1[0]);
							// System.out.println("(" + str1[1] + ")");
						//	if (str.indexOf("(" + str1[1] + ")") > -1) {
                             if(str.equalsIgnoreCase(niknm))//indexOf(str1[2])>-1)
							 {
								// System.out.println("========== inside if ============");
								benAccountNumber = str1[3];
								benSrno = str1[0];
							}

						}// end for

						txtAccNo.setText(benAccountNumber);
						// System.out.println("benSrno:=====>" + benSrno);
                                             }
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
						// TODO Auto-generated method stub
						txtAccNo.setText("");
					}

				});// end spi_sel_beneficiery

		// logic to get debit a/c number from spi_debit_account according to
		// selected debit account
		spi_debit_account
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {

						// ////String
						// str=spi_debit_account.getItemAtPosition(spi_debit_account.getSelectedItemPosition()).toString();
                        
						str=spi_debit_account.getSelectedItem().toString();
						Log.e("arg2= ","arg2="+arg2);
						if (arg2 == 0)
						{
							txtBalance.setText("");
						}
							
						else if (arg2 != 0) {
						Log.e("str= ","str="+str);
                        if(str.equalsIgnoreCase("Select Debit Account"))
						{
	                           txtBalance.setText("");
						}else
						{
							if (spi_debit_account.getCount() > 0) {
							String str = arrListTemp.get(spi_debit_account
									.getSelectedItemPosition()-1);
	
							retMess = "Selected Account number" + str;
							// setAlert();
							Log.e("TRANSFER", "str===" + str);
							//String debitAc[] = str.split("-");
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
								Log.e("balStr", "str===balStr" + balStr);
								txtBalance.setText(balStr);
							}
						}
						
					  }	

					}// end onItemSelected

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {

					}// end onNothingSelected

				});// end spi_debit_account

		all_acnts = stringValue;

		addAccounts(all_acnts);
		// System.out.println("========== 1 ============");
		if (!noAccounts) {
			noAccounts = false;
			this.flag = chkConnectivity();
			if (this.flag == 0) {
				// System.out.println("========== 1.0 ============");
				new CallWebService_fetch_all_beneficiaries().execute();
				// System.out.println("========== 1.1 ============");
			}
		}
		// System.out.println("===================== alter if in init =====================");
		// System.out.println("========== 2 ============");
		this.pb_wait.setMax(10);
		// System.out.println("========== 3 ============");
		this.pb_wait.setProgress(1);
		// System.out.println("========== 4 ============");
		this.pb_wait.setVisibility(4);
		// System.out.println("========== 5 ============");
		txtAmt.setFilters(new InputFilter[] { new DecimalDigitsInputFilter(2) });
		return rootView;
	}

	private void addBeneficiaries(String retval) {
		// System.out.println("================ IN addBeneficiaries() of OtherBankTransfer ======================");
		// System.out.println("SameBankTransfer IN addBeneficiaries()" +
		// retval);

		try {
			// String[] tempstr=retval.split("#");
			// retval=tempstr[1];
			// System.out.println("*****retval in addBeneficiaries:"+retval);
			ArrayList<String> arrList = new ArrayList<String>();
			String allstr[] = retval.split("~");
			// System.out.println("OtherBankTranIMPS Accounts:::" + allstr[1]);
			// int noOfAccounts = str1.length;
			int noOfben = allstr.length;
			// System.out.println("SameBankTransfer noOfben:" + noOfben);
			String benName = "";
			arrList.add("Select Beneficiary");
			for (int i = 1; i <= noOfben; i++) {
				// System.out.println(i + "----STR1-----------" + allstr[i -
				// 1]);
				String[] str2 = allstr[i - 1].split("#");
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
					android.R.layout.simple_spinner_item, benfArr);*/// <String>(act,android.R.layout.simple_spinner_item,
																	// benfArr);
			accs.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spi_sel_beneficiery.setAdapter(accs);

		} catch (Exception e) {
			System.out.println("" + e);
		}
	}// end addBeneficiaries

	/*
	 * public void addAccounts(String paramString) {
	 * System.out.println("SameBankTransfer IN addAccounts()" + paramString);
	 * try { ArrayList localArrayList = new ArrayList(); String[] arrayOfString
	 * = paramString.split("~");
	 * System.out.println("SameBankTransfer Mayuri.....................:");
	 * System.out.println("SameBankTransfer Accounts:::" + arrayOfString[1]);
	 * int i = arrayOfString.length;
	 * System.out.println("SameBankTransfer noOfAccounts:" + i); Accounts[]
	 * arrayOfAccounts = new Accounts[i]; for (int j = 1; ; j++) { if (j >= i -
	 * 1) { ArrayAdapter localArrayAdapter = new ArrayAdapter(act, 17367048,
	 * localArrayList); localArrayAdapter.setDropDownViewResource(17367049);
	 * this.spi_debit_account.setAdapter(localArrayAdapter);
	 * Log.i("SameBankTransfer ", "Exiting from adding accounts"); this.acnt_inf
	 * = spi_debit_account.getItemAtPosition(this.spi_debit_account.
	 * getSelectedItemPosition()).toString();
	 * Log.i("SameBankTransfer MAYURI....", this.acnt_inf); return; }
	 * System.out.println(j + "----STR1-----------" + arrayOfString[j]);
	 * this.str2 = arrayOfString[j]; System.out.println(j + "str2-----------" +
	 * this.str2); arrayOfAccounts[j] = new Accounts(this.str2); this.str2 =
	 * this.str2.replaceAll("#", "-"); localArrayList.add(this.str2); }
	 * 
	 * 
	 * } catch (Exception localException) { System.out.println(localException);
	 * } }
	 */

	public void addAccounts(String str) {
		try {
			ArrayList<String> arrList = new ArrayList<String>();
			String[] allstr = str.split("~");
			
			   arrList.add("Select Debit Account");

			int noOfAccounts = allstr.length;
			int j=0;	// System.out.println("SameBankTransfer noOfAccounts:" +
			// noOfAccounts);
		acArray = new Accounts[noOfAccounts];
			for (int i = 0; i < noOfAccounts; i++) {
				
				str2 = allstr[i];
	                       String tempStr=str2;
				// System.out.println(i + "str2-----------" + str2);
				acArray[i] = new Accounts(str2);
				str2 = str2.replaceAll("#", "-");
				String accType = str2.split("-")[2];
				String oprcd = str2.split("-")[7];
				String str2Temp = str2;
				str2 = MBSUtils.get16digitsAccNo(str2);
	           // Log.e("add accounts","accType"+accType);
				//Log.e("add accounts","oprcd"+oprcd);
				String withdrawalAllowed=allstr[i].split("#")[10];
				if (((accType.equals("SB")) || (accType.equals("LO")) || (accType
						.equals("CA"))) && oprcd.equalsIgnoreCase("O")&& withdrawalAllowed.equalsIgnoreCase("Y")) {
	       acArray[j++] = new Accounts(tempStr);
					arrList.add(str2 + " (" + MBSUtils.getAccTypeDesc(accType)
							+ ")");
					arrListTemp.add(str2);
				}
			}
			/*
			 * ArrayAdapter<String> arrAdpt = new ArrayAdapter<String>(this,
			 * android.R.layout.simple_spinner_item, arrList);
			 * arrAdpt.setDropDownViewResource
			 * (android.R.layout.simple_spinner_dropdown_item);
			 * spi_debit_account.setAdapter(arrAdpt);
			 * Log.i("OtherBankTranIMPS ", "Exiting from adding accounts");
			 */
			if (arrList.size() == 0) {
				noAccounts = true;
				showAlert(getString(R.string.alert_089));

			}
			String[] debAccArr = new String[arrList.size()];
			debAccArr = arrList.toArray(debAccArr);
			/*
			 * CustomeSpinnerAdapter debAccs = new CustomeSpinnerAdapter(act,
			 * R.layout.spinner_layout, debAccArr);
			 */
			//CustomeSpinnerAdapter debAccs = new CustomeSpinnerAdapter(act,android.R.layout.simple_spinner_item, debAccArr);
			ArrayAdapter<String> debAccs = new ArrayAdapter<String>(act,R.layout.spinner_item, debAccArr);
			debAccs.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spi_debit_account.setAdapter(debAccs);

			acnt_inf = spi_debit_account.getItemAtPosition(
					spi_debit_account.getSelectedItemPosition()).toString();
			// Log.i("SameBankTransfer MAYURI....", acnt_inf);
		} catch (Exception e) {
			System.out.println("" + e);
		}

	}// end addAccount

	public int chkConnectivity() {// chkConnectivity
		// System.out.println("============= inside chkConnectivity ================== ");
		// pb_wait.setVisibility(ProgressBar.VISIBLE);
		flag = 0;
		ConnectivityManager cm = (ConnectivityManager) act
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		// System.out.println("============= inside chkConnectivity 1 ================== ");
		NetworkInfo ni = cm.getActiveNetworkInfo();
		// System.out.println("============= inside chkConnectivity  2 ================== ");
		try {
			// System.out.println("============= inside chkConnectivity 3 ================== ");
			State state = ni.getState();
			boolean state1 = ni.isAvailable();
			// System.out.println("state1 ---------" + state1);
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
				//	showAlert(retMess);
					
					 dbs = new DialogBox(act);
					 dbs.get_adb().setMessage(retMess);
					  dbs.get_adb().setPositiveButton("Ok", new
					  DialogInterface.OnClickListener() { public void
					  onClick(DialogInterface arg0, int arg1) { arg0.cancel();
					  } }); dbs.get_adb().show();
					 
					break;
				default:
					flag = 1;
					retMess = getString(R.string.alert_000);
					// setAlert();

					showAlert(retMess);
					break;
				}
			} else {
				flag = 1;
				retMess = getString(R.string.alert_000);
				// setAlert();

				showAlert(retMess);

			}
		} catch (NullPointerException ne) {

			Log.i("mayuri", "NullPointerException Exception" + ne);
			flag = 1;
			retMess = getString(R.string.alert_000);
			// setAlert();

			showAlert(retMess);

		} catch (Exception e) {
			Log.i("mayuri", "Exception" + e);
			flag = 1;
			retMess = getString(R.string.alert_000);
			// setAlert();

			showAlert(retMess);
		}
		// System.out.println("=========== Exit from chkConnectivity ================");
		return flag;
	}

	// this webservice to call and add all beniferies
	class CallWebService_fetch_all_beneficiaries extends
			AsyncTask<Void, Void, Void> {
		JSONObject obj = new JSONObject();
		String retval = "";
		LoadProgressBar loadProBarObj = new LoadProgressBar(act);
		String ValidationData="";

		protected void onPreExecute() {
			// pb_wait.setVisibility(ProgressBar.VISIBLE);
			loadProBarObj.show();
			
			
			try {
				respcode="";
				reTval="";
				getBeneficiariesrespdesc="";
				obj.put("CUSTID", custId);
				obj.put("SAMEBNK", "Y");
				obj.put("IMEINO", MBSUtils.getImeiNumber(act));
				obj.put("SIMNO", MBSUtils.getSimNumber(act));
				obj.put("METHODCODE","13"); 
				// ValidationData=MBSUtils.getValidationData(act,obj.toString());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
				Log.e("TAG", "doInBackground12345: "+var5);
				//return null;
			}// end try
			catch (Exception e) {
				Log.e("TAG", "doInBackground: "+e.getMessage() );
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
			String decryptedBeneficiaries = reTval;
				Log.e("Shubham", "onPostExecute----------------->: "+decryptedBeneficiaries );

			
			if (decryptedBeneficiaries.indexOf("SUCCESS") > -1) {
				
				post_successfetch_all_beneficiaries(reTval);
				
			
			} else {
			
				if (decryptedBeneficiaries.indexOf("NODATA") > -1) {
					// retMess = getString(R.string.alert_041);
					//flg="true";
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
			/*	}
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
		//Log.e("TAG", "post_successfetch_all_beneficiaries: "+reTval );
		respcode="";
		getBeneficiariesrespdesc="";
		String decryptedBeneficiaries=reTval;
		decryptedBeneficiaries = decryptedBeneficiaries
				.split("SUCCESS~")[1];
		// Log.e("OMKAR BENEFICIEARIES", decryptedBeneficiaries);
		benInfo = decryptedBeneficiaries;
		addBeneficiaries(benInfo);
		
	}
	// webservice to save data
	class CallWebService2 extends AsyncTask<Void, Void, Void> {
		String retval = "";
		LoadProgressBar loadProBarObj = new LoadProgressBar(act);
		String accNo, debitAccno, benAcNo, amt, reMark;
		String ValidationData="";
		JSONObject obj=new JSONObject();
		
		protected void onPreExecute() {
			respcode="";
			reTval="";
			saveTransferTranrespdesc="";
			loadProBarObj.show();
			accNo = txtAccNo.getText().toString().trim();
			debitAccno = arrListTemp.get(spi_debit_account
					.getSelectedItemPosition()-1);
			benAcNo = spi_sel_beneficiery.getItemAtPosition(
					spi_sel_beneficiery.getSelectedItemPosition()).toString();
			amt = txtAmt.getText().toString().trim();
			reMark = txt_remark.getText().toString().trim();

			String crAccNo = txt_to.getText().toString().trim();
			String charges = txt_charges.getText().toString().split(" ")[1];
			String drAccNo = txt_from.getText().toString().trim();
			debitAccno=debitAccno.substring(0,16);
			
			try {
				String location=MBSUtils.getLocation(act);
				obj.put("BENFSRNO", benSrno);
				obj.put("CRACCNO", crAccNo);
				obj.put("DRACCNO", debitAccno);
				obj.put("AMOUNT", amt);
				obj.put("REMARK", reMark);
				obj.put("TRANSFERTYPE", "INTBANK");
				obj.put("IMEINO", MBSUtils.getImeiNumber(act));
				obj.put("CUSTID", custId);
				obj.put("CHARGES", charges);
				obj.put("CHRGACCNO", chrgCrAccNo);
				obj.put("TRANID", tranId);
				obj.put("SERVCHRG",  "0");
				obj.put("CESS", "0");
				obj.put("TRANPIN", tranmpin);
				obj.put("SIMNO", MBSUtils.getSimNumber(act));
				obj.put("MOBILENO", MBSUtils.getMyPhoneNO(act));
				obj.put("IPADDRESS", MBSUtils.getLocalIpAddress());
				obj.put("OSVERSION", Build.VERSION.RELEASE);
				obj.put("LATITUDE", location.split("~")[0]);
				obj.put("LONGITUDE", location.split("~")[1]);
				obj.put("METHODCODE","16");
				// ValidationData=MBSUtils.getValidationData(act,obj.toString());
     	
			} catch (JSONException e) {
				// TODO Auto-generated catch block
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
			catch (Exception e) {
				e.printStackTrace();
				// System.out.println("Exception 2");
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
			
				/*ValidationData=xml_data[1].trim();
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
				
			
			if(saveTransferTranrespdesc.length()>0)
			{
				showAlert(saveTransferTranrespdesc);
			}
			else{
			if (reTval.indexOf("SUCCESS") > -1) {
				
				post_successsaveTransferTran(reTval);
				
			} else if (reTval.indexOf("DUPLICATE") > -1) {

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
			else if(reTval.indexOf("BENFACCINVALID") > -1)
			{
				retMess = getString(R.string.alert_benfivalid) ;
						
				showAlert(retMess);
				FragmentManager fragmentManager;
				Fragment fragment = new FundTransferMenuActivity(act);
				act.setTitle(getString(R.string.lbl_same_bnk_trans));
				fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction()
						.replace(R.id.frame_container, fragment).commit();
				
				
			}
			else if(reTval.indexOf("INVALIDTRANS") > -1)
			{
				retMess = getString(R.string.alert_benfivalid) ;
						
				showAlert(retMess);
				FragmentManager fragmentManager;
				Fragment fragment = new FundTransferMenuActivity(act);
				act.setTitle(getString(R.string.lbl_same_bnk_trans));
				fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction()
						.replace(R.id.frame_container, fragment).commit();
				
				
			}
			
			else if (reTval.indexOf("FAILED#") > -1) {
				retMess = getString(R.string.alert_032);
				showAlert(retMess);// setAlert();
		                 	}
           else if (reTval.indexOf("WRONGTRANPIN") > -1) 
			{
				String msg[] = reTval.split("~");
				String first=msg[1];
				String second=msg[2];
				Log.e("first", "-------"+first);
				Log.e("second", "-------"+second);

			int count=Integer.parseInt(second);
				count= 5-count;
				//loadProBarObj.dismiss();
				retMess = getString(R.string.alert_125_1)+" "+count+" "+getString(R.string.alert_125_2);
				showAlert(retMess);
			}
                	else if (reTval.indexOf("BLOCKEDFORDAY") > -1) 
			{
				//loadProBarObj.dismiss();
				retMess = getString(R.string.login_alert_005);
				showAlert(retMess);
			}
			 else if (reTval.indexOf("FAILED") > -1) {
               if(reTval.split("~")[1]!="null" || reTval.split("~")[1]!="")
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

	public 	void post_successsaveTransferTran(String reTval)
	{
		
		
		respcode="";
		saveTransferTranrespdesc="";
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
	@Override
	public void onClick(View v) 
	{
		if (v.getId() == R.id.btn_home1)
		{
			Intent in = new Intent(getActivity(), NewDashboard.class);
			in.putExtra("VAR1", var1);
			in.putExtra("VAR3", var3);
			startActivity(in);
		} 
		else if (v.getId() == R.id.spinner_btn2) 
		{
			spi_sel_beneficiery.performClick();
		} else if (v.getId() == R.id.btn_logout)
		{
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
		}
		else if (v.getId() == R.id.spinner_btn) 
		{
			spi_debit_account.performClick();
		} 
		else if (v.getId() == R.id.sameBnkTranbtn_submit) 
		{
			strFromAccNo = spi_debit_account.getSelectedItem().toString();
			strToAccNo = txtAccNo.getText().toString().trim();
			strAmount = txtAmt.getText().toString().trim();
			strRemark = txtRemk.getText().toString().trim();
            String balString = txtBalance.getText().toString().trim();
           	if(balString.length()>0)
    		{
    			balString=balString.substring(0,balString.length()-2);
    			balance=Double.parseDouble(balString);
    			balance=Math.abs(balance);
    		}
        	String debitAcc = strFromAccNo.substring(0, 16);
			 if(debitAcc.indexOf("Select")> -1){
					showAlert(getString(R.string.alert_174));
				}
			 else if (strToAccNo.length() == 0) {
				showAlert(getString(R.string.alert_098));
			} else if (strToAccNo.equalsIgnoreCase(debitAcc)) {
				showAlert(getString(R.string.alert_107));
			} else if (strAmount.length() == 0) {
				showAlert(getString(R.string.alert_033));
			} 
			else if(strAmount.length()==1 && strAmount.equalsIgnoreCase("."))
			{
				showAlert(getString(R.string.alert_195));
			}else if (Double.parseDouble(strAmount) == 0) {
				showAlert(getString(R.string.alert_034));
			}
			else if(strRemark.length()==0)
			{
				showAlert(getString(R.string.alert_035));
			}else if (Double.parseDouble(strAmount) > balance) {
			showAlert(getString(R.string.alert_176));
			}else {
				try {
					this.flag = chkConnectivity();
					if (this.flag == 0) {
						CallWebServiceGetSrvcCharg c = new CallWebServiceGetSrvcCharg();
						c.execute();
					}
				} catch (Exception e) {
					e.printStackTrace();
					System.out
							.println("Exception in CallWebServiceGetSrvcCharg is:"
									+ e);
				}
			}
		} 
		else if (v.getId() == R.id.btn_confirm) 
		{
			if (strAmount.length() == 0) {
				strAmount = "0";
				retMess = getString(R.string.alert_033);
				// System.out.println("--------------- 22.1 ------------");
				showAlert(retMess);// setAlert();
				// System.out.println("--------------- 22.2 ------------");
				txtAmt.requestFocus();
				// System.out.println("--------------- 22.3 ------------");
			} else {
				// int amt = Double.parseDouble(strAmount);
				if (Double.parseDouble(strAmount) <= 0) {
					// System.out.println("--------------- 44 ------------");
					retMess = getString(R.string.alert_034);
					showAlert(retMess);// setAlert();
					txtAmt.requestFocus();
				} else {
					if (strRemark.length() > 200) {
						// System.out.println("--------------- 33 ------------");
						retMess = getString(R.string.alert_097);
						showAlert(retMess);// setAlert();
						txtRemk.requestFocus();
					} else if (strToAccNo.length() == 0) {
						retMess = getString(R.string.alert_067);
						showAlert(retMess);// setAlert();
					} else {
						InputDialogBox inputBox = new InputDialogBox(act);
						inputBox.show();
					} // end else
				}
			}// end if
		}
		else if(v.getId() == R.id.btn_confirm_back){
			same_bnk_layout.setVisibility(same_bnk_layout.VISIBLE);
			confirm_layout.setVisibility(confirm_layout.INVISIBLE);
			txt_heading.setText(getString(R.string.lbl_same_bnk_trans));
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


	public void setAlert() {

		// System.out.println("Cuttent thread name:==>"
		// + Thread.currentThread().getName());
		System.out.println("======== in set alert ==========");
		showAlert(retMess);
	}// end setAlert

	/*public void saveData() {
		try {
			// System.out.println("--------------- 44 ------------");
			this.flag = chkConnectivity();
			if (this.flag == 0) {
				
				new CallWebService2().execute();
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Exception in saveTransferTran is:" + e);
		}
	}// end saveData
*/
	
	public void saveData() 
	{
		try 
		{
			String accNo = txtAccNo.getText().toString().trim();
			String debitAccno = arrListTemp.get(spi_debit_account.getSelectedItemPosition()-1);
			String benAcNo = spi_sel_beneficiery.getItemAtPosition(spi_sel_beneficiery.getSelectedItemPosition()).toString();
			String amt = txtAmt.getText().toString().trim();
			String reMark = txt_remark.getText().toString().trim();

			String crAccNo = txt_to.getText().toString().trim();
			String charges = txt_charges.getText().toString().split(" ")[1];
			String drAccNo = txt_from.getText().toString().trim();

			JSONObject obj = new JSONObject();
			try 
			{
				obj.put("BENFSRNO", benSrno);
				obj.put("CRACCNO", crAccNo);
				obj.put("DRACCNO", debitAccno);
				obj.put("AMOUNT", amt);
				obj.put("REMARK", reMark);
				obj.put("TRANSFERTYPE", "INTBANK");
				obj.put("IMEINO", MBSUtils.getImeiNumber(act));
				obj.put("CUSTID", custId);
				obj.put("CHARGES", charges);
				obj.put("CHRGACCNO", chrgCrAccNo);
				obj.put("TRANID", tranId);
				obj.put("SERVCHRG", "0");
				obj.put("CESS", "0");
				obj.put("TRANPIN", tranmpin);
				obj.put("SIMNO", MBSUtils.getSimNumber(act));
	
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			Bundle bundle=new Bundle();
			Fragment fragment = new TransferOTP(act);
			bundle.putString("CUSTID", custId);
			bundle.putString("FROMACT", "SAMEBANK");
			bundle.putString("JSONOBJ", obj.toString());
			fragment.setArguments(bundle);
			FragmentManager fragmentManager = sameBnkTran.getFragmentManager();
			fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
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

				// System.out.println("========= inside onClick ============***********");
				String str = mpin.getText().toString().trim();
				tranmpin=str;
				 encrptdTranMpin = ListEncryption.encryptData(custId
						+ str);
	

				if (str.length() == 0) {
					retMess = getString(R.string.alert_116);
					showAlert(retMess);// setAlert();
					this.show();
				} else if (str.length() != 6) {
					retMess = getString(R.string.alert_037);
					showAlert(retMess);// setAlert();
					this.show();
				} else {
					callValidateTranpinService validateTran=new callValidateTranpinService();
					validateTran.execute();
						this.hide();
			
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.out
						.println("Exception in InputDialogBox of onClick:=====>"
								+ e);
			}
		}// end onClick
	}// end InputDialogBox

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
					else if(this.textMessage.equalsIgnoreCase(act.getString(R.string.alert_125_1)))
						{
							InputDialogBox inputBox = new InputDialogBox(act);
							inputBox.show();
					    }
                                 	else{
					if (noAccounts) {
						if (same_bnk_layout.getVisibility() == View.VISIBLE) {
							Fragment fragment = new FundTransferMenuActivity(
									act);
							// act.setTitle(getString(R.string.lbl_fund_transfer));
							FragmentManager fragmentManager = sameBnkTran
									.getFragmentManager();
							fragmentManager.beginTransaction()
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
		/*
		 * spi_debit_account.getItemAtPosition(
		 * spi_debit_account.getSelectedItemPosition()).toString(); benAcNo =
		 * spi_sel_beneficiery.getItemAtPosition(
		 * spi_sel_beneficiery.getSelectedItemPosition()).toString();
		 */
		txtAmt.setText("");
		txtRemk.setText("");
	}

	class CallWebServiceGetSrvcCharg extends AsyncTask<Void, Void, Void> {
		String retval = "";
		LoadProgressBar loadProBarObj = new LoadProgressBar(act);
		String accNo, debitAccno, benAcNo, amt, reMark;
		String ValidationData="";
		JSONObject obj=new JSONObject();
		
		// inputBox=new InputDialogBox(samBnkObj);
		protected void onPreExecute() {
			respcode="";
			reTval="";
			getTransferChargesrespdesc="";
			// pb_wait.setVisibility(ProgressBar.VISIBLE);
			loadProBarObj.show();
			accNo = txtAccNo.getText().toString().trim();
			debitAccno = arrListTemp.get(spi_debit_account
					.getSelectedItemPosition()-1);
			benAcNo = spi_sel_beneficiery.getItemAtPosition(
					spi_sel_beneficiery.getSelectedItemPosition()).toString();

			amt = txtAmt.getText().toString().trim();
			reMark = txtRemk.getText().toString().trim();
			debitAccno=debitAccno.substring(0, 16);
		try {
				
				obj.put("CUSTID", custId);
				obj.put("TRANTYPE", "INTBANK");
				obj.put("DRACCNO", debitAccno);
				obj.put("AMOUNT", amt);
				obj.put("CRACCNO", accNo);
				obj.put("BENFSRNO", benSrno);	
				obj.put("IMEINO",  MBSUtils.getImeiNumber(act));
				obj.put("SIMNO", MBSUtils.getSimNumber(act));
				obj.put("METHODCODE","28");
				// ValidationData=MBSUtils.getValidationData(act,obj.toString());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
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
			}// end try\
			catch (Exception e) {
				e.printStackTrace();
				// System.out.println("Exception 2");
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
	   				/*ValidationData=xml_data[1].trim();
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
	   				
	   			
	   			if(getTransferChargesrespdesc.length()>0)
	   			{
	   				showAlert(getTransferChargesrespdesc);
	   			}
	   			else{
			// retval = "SUCCESS";
			if (reTval.indexOf("SUCCESS") > -1) {
				//act.frgIndex = 52;///511
				
				post_successGetSrvcCharg(reTval);
				
			} else {
				if (reTval.indexOf("LIMIT_EXCEEDS") > -1) {
					retMess = getString(R.string.alert_031);
					//loadProBarObj.dismiss();
					showAlert(retMess);// setAlert();
                       	} else if (reTval.indexOf("LOWBALANCE") > -1) {
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
    				 else if(reTval.indexOf("BENFACCINVALID") > -1)
    				 {
    					 retMess = getString(R.string.alert_benfivalid);
 						//loadProBarObj.dismiss();
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
	}// end CallWebServiceGetSrvcCharg
	public 	void post_successGetSrvcCharg(String reTval)
	{
		respcode="";
		getTransferChargesrespdesc="";
		act.frgIndex = 52;///511
		same_bnk_layout.setVisibility(same_bnk_layout.INVISIBLE);
		confirm_layout.setVisibility(confirm_layout.VISIBLE);
		// Log.e("SAMEBANKTRANSFER","xml_data[0]=="+xml_data[0]);

		String retStr = reTval.split("~")[1];
         String tranType=reTval.split("~")[2];
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
		// txtTranId.setText(tranId);
		if (chrgCrAccNo.length() == 0
				|| chrgCrAccNo.equalsIgnoreCase("null"))
			chrgCrAccNo = "";
		// Log.e("SAMEBANKTRANSFER","val[0]=="+val[0]);
		// Log.e("SAMEBANKTRANSFER","val[1]=="+val[1]);
		// Log.e("SAMEBANKTRANSFER","chrgCrAccNo=="+chrgCrAccNo);
		
	}
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
				obj.put("TRANPIN", tranmpin);
				obj.put("CUSTID", custId);
				obj.put("MOBILENO", MBSUtils.getMyPhoneNO(act));
				obj.put("IPADDRESS", MBSUtils.getLocalIpAddress());
				obj.put("OSVERSION", Build.VERSION.RELEASE);
				obj.put("LATITUDE", location.split("~")[0]);
				obj.put("LONGITUDE", location.split("~")[1]);
				obj.put("METHODCODE","73");
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
}// end class

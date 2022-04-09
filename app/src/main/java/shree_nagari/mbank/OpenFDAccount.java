package shree_nagari.mbank;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import mbLib.CryptoUtil;
import mbLib.CustomDialogClass;
import mbLib.DatabaseManagement;
import mbLib.DialogBox;
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
import android.content.SharedPreferences;
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
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class OpenFDAccount extends Fragment implements OnClickListener {
	MainActivity act;
	TextView txt_heading, txt_con_amount, txt_con_duration,
			txt_con_interest_dates, txt_con_maturity_dates;
	private static String responseJSON = "NULL";
	
	String[] presidents;
	TextView txt_con_maturity_amt, txt_con_debit_acc, txt_con_scheme,
			lbl_int_rates;
	EditText txtAmt, txt_scheme, txtBalance,TrantxtBal;
	Spinner spi_duration1, spi_duration2, spi_duration3, spi_interest_type,
			spi_tax, spi_maturity_action, spi_debit_account, scheme;
	ImageButton spinner_btn_interest, spinner_btn_tax, spinner_btn_maturity,
			spinner_btn_debit_acc, btn_back, btn_home, spinner_btn_scheme;
	Button fd_account_submit, btn_confirm;
	DialogBox dbs;
	Accounts acArray[];
	Intent in;
	ProgressBar pb_wait;
	String retMess = "", str2 = "",str = "", acnt_inf = "", all_acnts,retvalwbs="",respdesc="";
	String strAmount = "", strDebitacc = "", strDuration = "",
			strInterest_rates = "", strMaturity_dates = "",
			strMaturity_amt = "", strConf_scheme = "", strYear = "",
			strMonth = "", strDay = "",year="",month="",day="",retValStr="",custid="";
	String drBrnCD = "", drSchmCD = "", drAcNo = "", stringValue = "",
			cust_name = "", custId = "", mobPin = "", intrestrate="",intrestamount="",maturitydate="",maturityamount="";
	int flag = 0;
	String tax_saver = "", maturity = "",retval = "",respcode="",respdesc_GetFDInterestRates="",respdesc_GetFDScheme="",respdesc_fetchMaturityDetails="",respdesc_EntryToFdAccount="";

	double ed_instrate = 0.0;
	boolean noAccounts = false;
	LinearLayout fd_account_layout, confirm_layout;
	ProgressBar fd_confirm_bar, fd_account_bar;
	DatabaseManagement dbms;
	String retStr = "", userId = "", cust_mob_no = "", transferType = "";
	ArrayList<String> arrListTemp = new ArrayList<String>();
	private static String URL = "";
	private static String METHOD_NAME = "";
	private static String NAMESPACE = "";
	private static String SOAP_ACTION = "";
	private static final String MY_SESSION = "my_session";
	private String benInfo = "";
	TextView txt_amount, txt_duration, txt_interest_rates, txt_maturity_dates,
			txt_maturity_amt, txt_debit_acc, txt_conf_scheme;
	ImageView img_heading;
	String trn_str;
	ImageView btn_home1,btn_logout;
	List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();

	public OpenFDAccount() {
	}

	@SuppressLint("ValidFragment")
	public OpenFDAccount(MainActivity a) {
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
		View rootView = inflater.inflate(R.layout.fd_account, container, false);
		this.dbs = new DialogBox(act);
		SharedPreferences localSharedPreferences = act.getSharedPreferences("my_session", 0);
		dbms = new DatabaseManagement("shree_nagari.mbank", "listMobileBanking");
		
		img_heading = (ImageView) rootView.findViewById(R.id.img_heading);
		img_heading.setBackgroundResource(R.mipmap.term_deposit2);
		txt_heading = (TextView) rootView.findViewById(R.id.txt_heading);
		txt_heading.setText(getString(R.string.lbl_title_fd_account));
		lbl_int_rates = (TextView) rootView.findViewById(R.id.lbl_int_rates);
		txtAmt = (EditText) rootView.findViewById(R.id.ed_Amt);
		txtBalance = (EditText) rootView.findViewById(R.id.sameBnkTrantxtBal);
		presidents = getResources().getStringArray(R.array.Errorinwebservice);
		Cursor c1 = dbms.selectFromTable("SHAREDPREFERENCE", "", null);
		if (c1 != null) 
		{
			while (c1.moveToNext()) 
			{
				stringValue = c1.getString(0);//ListEncryption.decryptData(c1.getString(0));
				custId = c1.getString(2);//ListEncryption.decryptData(c1.getString(2));
				//userId = ListEncryption.decryptData(c1.getString(3));
				cust_mob_no = c1.getString(4);//ListEncryption.decryptData(c1.getString(4));
			}
		}
		
		spi_duration1 = (Spinner) rootView.findViewById(R.id.spi_duration1);
		if (spi_duration1 == null)
			System.out.println("spi_duration1 is null");
		else {
			System.out.println("spi_duration1 is not null");
			spi_duration1.requestFocus();
		}
		spi_duration2 = (Spinner) rootView.findViewById(R.id.spi_duration2);
		if (spi_duration2 == null)
			System.out.println("spi_duration2 is null");
		else {
			System.out.println("spi_duration2 is not null");
			spi_duration2.requestFocus();
		}
		spi_duration3 = (Spinner) rootView.findViewById(R.id.spi_duration3);
		if (spi_duration3 == null)
			System.out.println("spi_duration3 is null");
		else {
			System.out.println("spi_duration3 is not null");
			spi_duration3.requestFocus();
		}
		spi_interest_type = (Spinner) rootView
				.findViewById(R.id.spi_interest_type);
		if (spi_interest_type == null)
			System.out.println("spi_interest_type is null");
		else {
			System.out.println("spi_interest_type is not null");
			spi_interest_type.requestFocus();
		}
		spi_tax = (Spinner) rootView.findViewById(R.id.spi_tax);
		if (spi_tax == null)
			System.out.println("spi_tax is null");
		else {
			System.out.println("spi_tax is not null");
			spi_tax.requestFocus();
		}
		spi_maturity_action = (Spinner) rootView
				.findViewById(R.id.spi_maturity_action);
		if (spi_maturity_action == null)
			System.out.println("spi_maturity_action is null");
		else {
			System.out.println("spi_maturity_action is not null");
			spi_maturity_action.requestFocus();
		}
		spi_debit_account = (Spinner) rootView
				.findViewById(R.id.spi_debit_account);
		if (spi_debit_account == null)
			System.out.println("spi_debit_account is null");
		else {
			System.out.println("spi_debit_account is not null");
			spi_debit_account.requestFocus();
		}

		all_acnts = stringValue;
		addAccounts(all_acnts);

		fd_account_submit = (Button) rootView.findViewById(R.id.fd_account_submit);
		fd_account_submit.setOnClickListener(this);
		btn_home = (ImageButton) rootView.findViewById(R.id.btn_home);
		btn_home.setImageResource(R.mipmap.ic_home_d);
		btn_home.setOnClickListener(this);
		btn_home1 = (ImageView) rootView.findViewById(R.id.btn_home1);
		btn_logout = (ImageView) rootView.findViewById(R.id.btn_logout);
		btn_home1.setOnClickListener(this);
		btn_logout.setOnClickListener(this);
		
		List<String> interest = new ArrayList<String>();
		interest.add("At Maturity");
		interest.add("Monthly");
		interest.add("Quarterly");
		interest.add("Reinvestment Plan");

		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(
				getActivity(), R.layout.spinner_item, interest);

		// Drop down layout style - list view with radio button
		dataAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);

		// attaching data adapter to spinner
		spi_interest_type.setAdapter(dataAdapter);

		List<String> tax = new ArrayList<String>();
		tax.add("YES");
		tax.add("NO");

		ArrayAdapter<String> taxAdapter = new ArrayAdapter<String>(
				getActivity(), R.layout.spinner_item, tax);

		taxAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);

		// attaching data adapter to spinner
		spi_tax.setAdapter(taxAdapter);

		List<String> maturity = new ArrayList<String>();
		// maturity.add("Selected");
		maturity.add("Auto Renewal");
		maturity.add("Deposit To Saving Account");

		// Creating adapter for spinner
		ArrayAdapter<String> maturityAdapter = new ArrayAdapter<String>(
				getActivity(), R.layout.spinner_item, maturity);

		// Drop down layout style - list view with radio button
		maturityAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);

		// attaching data adapter to spinner
		spi_maturity_action.setAdapter(maturityAdapter);

		List<String> year = new ArrayList<String>();

		// year.add("Year");
		for (int i = 0; i <= 15; i++) {
			String strYear = "" + i + " Year";
			year.add(strYear);
		}
		// Creating adapter for spinner
		ArrayAdapter<String> yearAdapter = new ArrayAdapter<String>(
				getActivity(), R.layout.spinner_item, year);

		// Drop down layout style - list view with radio button
		yearAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);

		// attaching data adapter to spinner
		spi_duration1.setAdapter(yearAdapter);

		List<String> month = new ArrayList<String>();

		// month.add("Month");
		for (int i = 0; i <= 12; i++) {
			String strMonth = "" + i + " Month";
			month.add(strMonth);
		}
		// Creating adapter for spinner
		ArrayAdapter<String> monthAdapter = new ArrayAdapter<String>(
				getActivity(), R.layout.spinner_item, month);

		// Drop down layout style - list view with radio button
		monthAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);

		// attaching data adapter to spinner
		spi_duration2.setAdapter(monthAdapter);

		List<String> day = new ArrayList<String>();

		// day.add("Day");
		for (int i = 0; i <= 31; i++) {
			String strDay = "" + i + " Day";
			day.add(strDay);
		}
		// Creating adapter for spinner
		ArrayAdapter<String> dayAdapter = new ArrayAdapter<String>(
				getActivity(), R.layout.spinner_item, day);

		// Drop down layout style - list view with radio button
		dayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);

		// attaching data adapter to spinner
		spi_duration3.setAdapter(dayAdapter);

		spi_debit_account.setOnItemSelectedListener(new OnItemSelectedListener() 
		{
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,int arg2, long arg3) 
			{
				if (arg2 == 0)
				{
					txtBalance.setText("");
				}
				if(arg2 !=0)
				{
					if(!str.equalsIgnoreCase("Select Debit Account"))
					{			
						if (spi_debit_account.getCount() > 0) 
						{
							String str = arrListTemp.get(spi_debit_account.getSelectedItemPosition()-1);

							String debitAc[] = str.split("-");
							drBrnCD = debitAc[0];
							drSchmCD = debitAc[1];
							drAcNo = debitAc[3];
							Accounts selectedDrAccount = acArray[spi_debit_account.getSelectedItemPosition()-1];
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
					}
		});// end spi_debit_account

		lbl_int_rates.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				InputDialogBox inputBox = new InputDialogBox(act);
				inputBox.show();

			}
		});

		txtAmt.setFilters(new InputFilter[] { new DecimalDigitsInputFilter(2) });
		return rootView;

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
			startActivity(in);
			act.finish();
			break;
		case R.id.btn_home1:
			Intent in1 = new Intent(act, NewDashboard.class);
			startActivity(in1);
			act.finish();
			break;
		case R.id.btn_logout:
			CustomDialogClass alert=new CustomDialogClass(act, getString(R.string.lbl_exit)) {
				@Override
				public void onClick(View v) {
					switch (v.getId()) {
						case R.id.btn_ok:
							flag = chkConnectivity();
							if (flag == 0)
							{
								CallWebServicelog c=new CallWebServicelog();
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
			/*dbs = new DialogBox(act);
			dbs.get_adb().setMessage(getString(R.string.lbl_exit));
			dbs.get_adb().setPositiveButton("Yes",new DialogInterface.OnClickListener() 
			{
				@Override
				public void onClick(DialogInterface arg0, int arg1) 
				{
					flag = chkConnectivity();
					if (flag == 0)
					{
						CallWebServicelog c=new CallWebServicelog();
						c.execute();
					}
				}
			});
			dbs.get_adb().setNegativeButton("No",new DialogInterface.OnClickListener() 
			{
				@Override
				public void onClick(DialogInterface arg0, int arg1) 
				{
					arg0.cancel();
				}
			});
			dbs.get_adb().show();*/
			break;	
		case R.id.spi_debit_account:
			spi_debit_account.performClick();
			break;
		case R.id.lbl_int_rates:
			lbl_int_rates.performClick();
			break;
		case R.id.spi_interest_type:
			spi_interest_type.performClick();
			break;

		case R.id.spi_tax:
			spi_tax.performClick();
			break;

		case R.id.spi_maturity_action:
			spi_maturity_action.performClick();
			break;

		case R.id.fd_account_submit:
			strDebitacc = spi_debit_account.getSelectedItem().toString();
			strAmount = txtAmt.getText().toString();
			strInterest_rates = spi_interest_type.getSelectedItem().toString();
			tax_saver = spi_tax.getSelectedItem().toString();
			maturity = spi_maturity_action.getSelectedItem().toString();
			strYear = spi_duration1.getSelectedItem().toString();
			strMonth = spi_duration2.getSelectedItem().toString();
			strDay = spi_duration3.getSelectedItem().toString();
			String balString = txtBalance.getText().toString().trim();
			double balance=0.0;
			if(balString.length()>0)
			{
				balString=balString.substring(0,balString.length()-2);
				balance=Double.parseDouble(balString);
				balance=Math.abs(balance);
			}
			
			strDuration = strYear + " " + strMonth + " " + strDay + " ";
			int d1 = spi_duration1.getSelectedItemPosition();
			int d2 = spi_duration2.getSelectedItemPosition();
			int d3 = spi_duration3.getSelectedItemPosition();
			int d4 = spi_debit_account.getSelectedItemPosition();

			if (d4 == 0)
			{
				showAlert(getString(R.string.alert_0981));
			}
			else if (strAmount.length() == 0)
			{
				showAlert(getString(R.string.alert_033));
			}
			else if(Double.parseDouble(strAmount)>balance)
			{
				showAlert(getString(R.string.alert_176));
			}
			else if (Double.parseDouble(strAmount) == 0) {
				showAlert(getString(R.string.alert_034));
			} else if (d1 == 0 && d2 == 0 && d3 == 0) {
				showAlert(getString(R.string.alert_167_2));
			} else {
				try {
					{
						strDebitacc = spi_debit_account.getSelectedItem().toString();
						strAmount = txtAmt.getText().toString();
						strInterest_rates = spi_interest_type.getSelectedItem().toString();
						tax_saver = spi_tax.getSelectedItem().toString();
						maturity = spi_maturity_action.getSelectedItem().toString();
						strYear = spi_duration1.getSelectedItem().toString();
						strMonth = spi_duration2.getSelectedItem().toString();
						strDay = spi_duration3.getSelectedItem().toString();

						if (strYear.equals("0 Year")) {
							strYear = "0 Year";
						} else {
							strYear = strYear + "s";
						}

						if (strMonth.equals("0 Month")) {
							strMonth = "0 Month";
						} else {
							strMonth = strMonth + "s";
						}
						

						if (strDay.equals("0 Day")) {
							strDay = "0 Day";
						} else {
							strDay = strDay + "s";
						}

						strDuration = strYear + " " + strMonth + " " + strDay+ " ";
						CallWebServiceEntryToFdAccount C = new CallWebServiceEntryToFdAccount();
						C.execute();

					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			break;

		default:
			break;
		}

	}// end onClick
	class CallWebServicelog extends AsyncTask<Void, Void, Void> {
		//String[] xmlTags = { "CUSTID","IMEINO" };
    	  String[] xmlTags = {"PARAMS","CHECKSUM"};
  		String[] valuesToEncrypt = new String[2];
                JSONObject jsonObj = new JSONObject();
		String generatedXML = "";
		String ValidationData="";

		@Override
		protected void onPreExecute() {
                   try{
                	   respcode="";
                	   retvalwbs="";
                	   respdesc="";
			Log.e("@DEBUG","LOGOUT preExecute()");
                  jsonObj.put("CUSTID", custId);
	              jsonObj.put("IMEINO", MBSUtils.getImeiNumber(act));
	              jsonObj.put("SIMNO", MBSUtils.getSimNumber(act));
	              jsonObj.put("METHODCODE","60");
	              ValidationData=MBSUtils.getValidationData(act,jsonObj.toString());
		//	valuesToEncrypt[0] = custid;
		//	valuesToEncrypt[1] = MBSUtils.getImeiNumber(DashboardDesignActivity.this);
                       }
			   catch (JSONException je) {
	                je.printStackTrace();
	            }
			//valuesToEncrypt[0] = jsonObj.toString();
                   valuesToEncrypt[0] =  jsonObj.toString();
               	valuesToEncrypt[1] =  ValidationData;
			generatedXML = CryptoUtil.generateXML(xmlTags, valuesToEncrypt);
			Log.e("Debug","Trying: "+generatedXML);
		};

		@Override
		protected Void doInBackground(Void... arg0) {
			//Log.e("@DEBUG","LOGOUT doInBackground()");
			NAMESPACE = getString(R.string.namespace);
			URL = getString(R.string.url);
			SOAP_ACTION = getString(R.string.soap_action);
			METHOD_NAME = "mbsInterCall";//"logoutWS";
			SoapObject request = null;
			try {
				request  = new SoapObject(NAMESPACE, METHOD_NAME);
				//Log.e("Debug@","********");
				request.addProperty("para_value", generatedXML);
				//Log.e("Debug@","@@@@@@@@@");
				SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
						SoapEnvelope.VER11);
				envelope.setOutputSoapObject(request);
				//Log.e("Debug@","$$$$$$$$");
				HttpTransportSE androidHttpTransport = new HttpTransportSE(URL,
						15000);

				try {
					androidHttpTransport.call(SOAP_ACTION, envelope);
					//System.out.println(envelope.bodyIn.toString());
					retval = envelope.bodyIn.toString().trim();
					int pos = envelope.bodyIn.toString().trim().indexOf("=");
					retval = retval.substring(pos + 1, retval.length() - 3);
				} catch (Exception e) {
					e.printStackTrace();
					getString(R.string.alert_000);
					System.out.println("Exception" + e);
					Log.e("ERROR-INNER",e.getClass()+" : "+e.getMessage());
				}
			} catch (Exception e) {
				// retMess = "Error occured";
				getString(R.string.alert_000);
				System.out.println(e.getMessage());
				Log.e("ERROR-OUTER",e.getClass()+" : "+e.getMessage());
			}
			return null;
		}

		protected void onPostExecute(final Void result) {
			Log.e("@DEBUG","LOGOUT onPostExecute()");
		
                	
                	String[] xml_data = CryptoUtil.readXML(retval, new String[]{"PARAMS","CHECKSUM"});
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
    						retvalwbs = jsonObj.getString("RETVAL");
    					}
    					else
    					{
    						retvalwbs = "";
    					}
    					if (jsonObj.has("RESPDESC"))
    					{
    						respdesc = jsonObj.getString("RESPDESC");
    					}
    					else
    					{	
    						respdesc = "";
    					}
    					
    				if(respdesc.length()>0)
    				{
    					showAlert(respdesc);
    				}
    				else{
			if (retvalwbs.indexOf("FAILED") > -1) {
				retMess = getString(R.string.alert_network_problem_pease_try_again);
				showAlert(retMess);

			}			
			else
			{
				post_successlog(retvalwbs);
				/*finish();
				System.exit(0);*/
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
	public void post_successlog(String retvalwbs)
	{
	  respcode="";
	      respdesc="";
	  act.finish();
		System.exit(0);
		
	}

	public int chkConnectivity() { // chkConnectivity
		ConnectivityManager cm = (ConnectivityManager) act
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		try {
			State state = ni.getState();
			boolean state1 = ni.isAvailable();
			if (state1) {
				switch (state) {
				case CONNECTED:
					if (ni.getType() == ConnectivityManager.TYPE_MOBILE
							|| ni.getType() == ConnectivityManager.TYPE_WIFI) {
					}
					break;
				case DISCONNECTED:
					flag = 1;
					retMess = getString(R.string.alert_014);
					dbs = new DialogBox(act);
					dbs.get_adb().setMessage(retMess);
					dbs.get_adb().setPositiveButton("Ok",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface arg0,
										int arg1) {
									arg0.cancel();
								}
							});
					dbs.get_adb().show();
					break;
				default:
					flag = 1;
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
			flag = 1;
			retMess = getString(R.string.alert_000);
			showAlert(retMess);

		} catch (Exception e) {
			flag = 1;
			retMess = getString(R.string.alert_000);
			showAlert(retMess);
		}
		return flag;
	}

	public void setAlert() { 
		showAlert(retMess);
	}// end setAlert

	public void saveData() {
		try {
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Exception in saveTransferTran is:" + e);
		}
	}// end saveData

	public void showAlert(final String str) {
		ErrorDialogClass alert = new ErrorDialogClass(act, "" + str) {
			@Override 
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.btn_ok:
					if((str.equalsIgnoreCase(respdesc_GetFDInterestRates)) &&  (respcode.equalsIgnoreCase("0")))
					{
						post_GetFDInterestRates();
					}
					else if((str.equalsIgnoreCase(respdesc_GetFDInterestRates)) && (respcode.equalsIgnoreCase("1")))
					{
						this.dismiss();
					}
					if((str.equalsIgnoreCase(respdesc_GetFDScheme)) &&  (respcode.equalsIgnoreCase("0")))
					{
						post_GetFDScheme(retval);
					}
					else if((str.equalsIgnoreCase(respdesc_GetFDScheme)) && (respcode.equalsIgnoreCase("1")))
					{
						this.dismiss();
					}
					if((str.equalsIgnoreCase(respdesc_fetchMaturityDetails)) &&  (respcode.equalsIgnoreCase("0")))
					{
						post_fetchMaturityDetails();
					}
					else if((str.equalsIgnoreCase(respdesc_fetchMaturityDetails)) && (respcode.equalsIgnoreCase("1")))
					{
						this.dismiss();
					}
					if((str.equalsIgnoreCase(respdesc_EntryToFdAccount)) &&  (respcode.equalsIgnoreCase("0")))
					{
						post_EntryToFdAccount(retval);
					}
					else if((str.equalsIgnoreCase(respdesc_EntryToFdAccount)) && (respcode.equalsIgnoreCase("1")))
					{
						this.dismiss();
					}
					else
						this.dismiss();
				default:
					break;
				}
				dismiss();
			}
		};
		alert.show();
	}

	@SuppressLint({"NewApi", "WrongConstant"})
	public void clearFields() {
		spinner_btn_interest.setLayoutDirection(0);
		spi_duration1.setLayoutDirection(0);
		spi_duration2.setLayoutDirection(0);
		spi_duration3.setLayoutDirection(0);
		spi_interest_type.setLayoutDirection(0);
		spi_tax.setLayoutDirection(0);
		spi_maturity_action.setLayoutDirection(0);
		spi_debit_account.setLayoutDirection(0);

		txtAmt.setText("");
		txt_scheme.setText("");
	}

	public void addAccounts(String str) {
		System.out.println("OtherBankTranRTGS IN addAccounts()" + str);

		try {
			ArrayList<String> arrList = new ArrayList<String>();
			String allstr[] = str.split("~");

			int noOfAccounts = allstr.length;
			arrList.add("Select Debit Account");
			acArray = new Accounts[noOfAccounts];
			int j=0;
			for (int i = 0; i < noOfAccounts; i++) {
				str2 = allstr[i];
				String tempStr=str2;
				
				str2 = str2.replaceAll("#", "-");
				String accType = str2.split("-")[2];
				String oprcd = str2.split("-")[7];
				String str2Temp = str2;
				str2 = MBSUtils.get16digitsAccNo(str2);
				
				String withdrawalAllowed=allstr[i].split("#")[10];
				if ((accType.equals("SB") || accType.equals("CA") || accType.equals("LO"))
						&& oprcd.equalsIgnoreCase("O") && withdrawalAllowed.equalsIgnoreCase("Y")) {
					acArray[j++] = new Accounts(tempStr);
					//arrList.add(str2);
					arrList.add(str2+" ("+MBSUtils.getAccTypeDesc(accType)+")");
					arrListTemp.add(str2Temp);
				}
			}

			String[] debAccArr = new String[arrList.size()];
			debAccArr = arrList.toArray(debAccArr);
			
			ArrayAdapter<String> debAccs = new ArrayAdapter<String>(act,
					R.layout.spinner_item, debAccArr);
			debAccs.setDropDownViewResource(R.layout.spinner_dropdown_item);
			spi_debit_account.setAdapter(debAccs);
			
		} catch (Exception e) {
			System.out.println("" + e);
		}

	}// end addAccount
	
	public void post_GetFDInterestRates(){
		
	}
	
	public class InputDialogBox extends Dialog implements OnClickListener {
		OpenFDAccount act;
		Activity activity;
		String msg, title;
		Context appAcontext;
		EditText mpin;
		Button btnOk;
		TextView interest;
		String strmpin = "";
		TextView txtLbl;
		ListView listview;
		boolean flg;
		String trn_str;
		List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();

		public InputDialogBox(Activity activity) { 
			super(activity);
			this.activity=activity;
		}// end InputDialogBox

		protected void onCreate(Bundle bdn) {
			super.onCreate(bdn);
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			setContentView(R.layout.dialog_int_rates);
			listview = (ListView) findViewById(R.id.listView1);
			btnOk = (Button) findViewById(R.id.btnOK);
			btnOk.setVisibility(Button.VISIBLE);
			btnOk.setOnClickListener(this);

			setValues();
		}

		public void setValues() { 
			trn_str = "SUCCESS~    Duration                      # ROI ~30 Days - 90 Days          # 6.5 %~91 Days - 180 Days       # 7.0 %~181 Days - 270 Days     # 7.5 %~271 Days - 365 Days     # 8.0 %~366 Days & above          # 9.0 %";
			String str1[] = trn_str.split("~");
			if (str1[0].equalsIgnoreCase("SUCCESS")) {
			
				for (int j = 1; j < str1.length; j++) {
					String string2[] = str1[j].split("#");
					HashMap<String, String> map = new HashMap<String, String>();
					String[] from = new String[] { "col_0", "col_1" };
					int[] to = new int[] { R.id.item1, R.id.item2 };

					map.put("col_0", string2[0]);
					map.put("col_1", string2[1].trim());

					fillMaps.add(map);
					SimpleAdapter adapter = new SimpleAdapter(activity,
							fillMaps, R.layout.interest_rats_list, from, to);
					listview.setAdapter(adapter);
				}
			}
		}

		@Override
		public void onClick(View v) { 
			try {
				this.hide();

			} catch (Exception e) {
				e.printStackTrace();
				System.out
						.println("Exception in InputDialogBox of onClick:=====>"
								+ e);
			}
		}// end onClick
 
	}// end InputDialogBox
	
	public void post_GetFDScheme(String retval){
		respcode="";respdesc_GetFDScheme= "";
		retval = retval.split("SUCCESS~")[1];
		benInfo = retval;
		addScheme(retval);
	}
	
	private double assignROI() {
		int d1 = spi_duration1.getSelectedItemPosition();
		int d2 = spi_duration2.getSelectedItemPosition();
		int d3 = spi_duration3.getSelectedItemPosition();
		if (d1 != 0 || d2 != 0 || d3 != 0) {
			String dur1 = spi_duration1.getSelectedItem().toString();
			String dur2 = spi_duration2.getSelectedItem().toString();
			String dur3 = spi_duration3.getSelectedItem().toString();
		
			int totalDays = (d1 * 365) + (d2 * 30) + d3;
			if (totalDays > 30 && totalDays <= 90)
				ed_instrate = 6.5;
			else if (totalDays > 91 && totalDays <= 180)
				ed_instrate = 7.0;
			else if (totalDays > 181 && totalDays <= 270)
				ed_instrate = 7.5;
			else if (totalDays > 271 && totalDays <= 365)
				ed_instrate = 8.0;
			else
				ed_instrate = 9.0;

		} else {
			ed_instrate = 0.0;
		}
		return ed_instrate;

	}
	
	private int duratioInMonth() {
		int d1 = spi_duration1.getSelectedItemPosition();
		int d2 = spi_duration2.getSelectedItemPosition();
		int d3 = spi_duration3.getSelectedItemPosition();
		int totalDays=0,months=0;
		if (d1 != 0 || d2 != 0 || d3 != 0)
		{
			String dur1 = spi_duration1.getSelectedItem().toString();
			String dur2 = spi_duration2.getSelectedItem().toString();
			String dur3 = spi_duration3.getSelectedItem().toString();
			
			 totalDays = (d1 * 365) + (d2 * 30) + d3;
			 months=totalDays/30;
			
		}
		return months;
	}

	public Double calcAmt() {
		int amount = Integer.parseInt(strAmount);	
		double result = (amount * ed_instrate) / 100;
		double totalAmt = amount + result;
		return totalAmt;

	}

	public void addScheme(String retval) {

		try {
			ArrayList<String> arrList = new ArrayList<String>();
			String allstr[] = retval.split("~");
			int noOfben = allstr.length;
			String benName = "";
			arrList.add("Select Scheme");
			for (int i = 1; i <= noOfben; i++) {
				String[] str2 = allstr[i - 1].split("#");
				benName = str2[2] + "(" + str2[1] + ")";
				arrList.add(benName);
			}
			String[] benfArr = new String[arrList.size()];
			benfArr = arrList.toArray(benfArr);

			ArrayAdapter<String> accs = new ArrayAdapter<String>(act,
					R.layout.spinner_item, arrList);
			accs.setDropDownViewResource(R.layout.spinner_dropdown_item);
			scheme.setAdapter(accs);

		} catch (Exception e) {
			System.out.println("" + e);
		}
	}// end addBeneficiaries

	public void post_fetchMaturityDetails(){
		respcode="-1";respdesc_fetchMaturityDetails= "";
		String msg[] = retval.split("~");

		String scheme = msg[1];
		String maturityamt = strAmount;
		String maturityDate = msg[3];
		String intRates = assignROI() + "";
		String monthDuration = duratioInMonth()+"";

		int ylength=strYear.length();
		int mlength=strMonth.length();
		int dlength=strDay.length();

		int yind=strYear.indexOf("Y",1);
		int mind=strMonth.indexOf("M",1);
		int dind=strDay.indexOf("D",1);
		
		year=strYear.substring(0,yind);
		month=strMonth.substring(0,mind);
		day=strDay.substring(0,dind);
		
		
		FragmentManager fragmentManager1;
		Fragment fragment1 = new FDaccount_Confirm(act);
		act.setTitle(getString(R.string.confirm));
		Bundle b = new Bundle();
		b.putString("DURATION", strDuration);
		b.putString("AMOUNT", strAmount);
		b.putString("INTRESTTYPE", strInterest_rates);
		b.putString("TAXSAVER", tax_saver);
		b.putString("MATURITY", maturity);
		b.putString("DEBITACC", strDebitacc);	
		b.putString("YEAR", year);
		b.putString("MONTH", month);
		b.putString("DAY", day);
		b.putString("SCHEME", scheme);
		b.putString("MATURITYAMT", maturityamount);
		b.putString("MATURITYDATE", maturitydate);
		b.putString("INTERATES", intrestrate);
		b.putString("INTERATESAMOUNT", intrestamount);
		b.putString("MONTHDURATION", monthDuration);

		fragment1.setArguments(b);

		fragmentManager1 = getFragmentManager();
		fragmentManager1.beginTransaction().replace(R.id.frame_container, fragment1).commit();
	
	}
	
	public class CallWebServiceEntryToFdAccount extends AsyncTask<Void, Void, Void>
	{
		LoadProgressBar loadProBarObj = new LoadProgressBar(act);
		String generatedXML = "",retVal="";
		String ValidationData="";
		
		@Override
		protected void onPreExecute() 
		{
			retval = "";respcode="";respdesc_GetFDInterestRates="";respdesc_GetFDScheme="";respdesc_fetchMaturityDetails="";respdesc_EntryToFdAccount="";
			int ylength=strYear.length();
			int mlength=strMonth.length();
			int dlength=strDay.length();
	
			int yind=strYear.indexOf("Y",1);
			int mind=strMonth.indexOf("M",1);
			int dind=strDay.indexOf("D",1);
			
			year=strYear.substring(0,yind);
			month=strMonth.substring(0,mind);
			day=strDay.substring(0,dind);
			loadProBarObj.show();
			
			String[] xmlTags = {"PARAMS","CHECKSUM"};
	        String[] valuesToEncrypt = new String[2];
			JSONObject jsonobj = new JSONObject();

			try 
			{
				jsonobj.put("CUSTID", custId);
				jsonobj.put("DEPOSIT_TYPE", strInterest_rates);
				jsonobj.put("DEBITACCNO", strDebitacc);
				jsonobj.put("DEPOSIT_YEAR", year);
				jsonobj.put("DEPOSIT_MONTH", month);
				jsonobj.put("DEPOSIT_DAYS", day);
				jsonobj.put("DEPOSIT_AMOUNT", strAmount);
				jsonobj.put("IMEINO", MBSUtils.getImeiNumber(act));
				jsonobj.put("SIMNO", MBSUtils.getSimNumber(act));
				jsonobj.put("METHODCODE","60");
				ValidationData=MBSUtils.getValidationData(act,jsonobj.toString());
				
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			valuesToEncrypt[0] = jsonobj.toString();
			valuesToEncrypt[1] = ValidationData;//jsonobj.toString();
			generatedXML = CryptoUtil.generateXML(xmlTags, valuesToEncrypt);
		};

		@Override
		protected Void doInBackground(Void... arg0) {
			NAMESPACE = getString(R.string.namespace);
			URL = getString(R.string.url);
			SOAP_ACTION = getString(R.string.soap_action);
			final String METHOD_NAME="mbsInterCall";//"EntryToFdAccount";
			int i = 0;
			try {

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
						System.out.println("=============== androidHttpTransport is  null ");
					androidHttpTransport.call(SOAP_ACTION, envelope);
					retVal = envelope.bodyIn.toString().trim();
					retVal = retVal.substring(retVal.indexOf("=") + 1,
							retVal.length() - 3);
					//Log.e("GenerateCUSTID ", "in doInBackground()	retVal :" +retVal);
				
			} catch (Exception e) {
				//Log.e("GenerateCUSTID ", "in doInBackground()	responseJSON :" + e);
				responseJSON = "NULL";
			}
			return null;
		}

		@Override
		protected void onPostExecute(final Void result) {  
			 loadProBarObj.dismiss();
        String[] xml_data = CryptoUtil.readXML(retVal, new String[]{"PARAMS","CHECKSUM"});

        //String str=xml_data[0];
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
					respdesc_EntryToFdAccount= jsonObj.getString("RESPDESC");
				}
				else
				{	
					respdesc_EntryToFdAccount= "";
				}
					
				if(respdesc_EntryToFdAccount.length()>0)
				{
					showAlert(respdesc_EntryToFdAccount);
				}
				else
				{
					if (retval.indexOf("SUCCESS~") > -1) 
					{
						post_EntryToFdAccount(retval);
					}
		        }
			}
			else
			{
				MBSUtils.showInvalidResponseAlert(act);
			}
		} 
		catch (JSONException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
}// onPostExecute
	}
	
	public void post_EntryToFdAccount(String retval){
		respdesc_EntryToFdAccount="";
        String str1=retval.split("~")[1];
        String values[] = retval.split("~")[1].split("!@!");       
        intrestrate=values[0];
        intrestamount=Float.parseFloat(values[1]) + "";
        maturitydate=values[2];
        maturityamount=Float.parseFloat(values[3]) + "";
		String monthDuration = duratioInMonth()+"";
		
		FragmentManager fragmentManager1;
		Fragment fragment1 = new FDaccount_Confirm(act);
		act.setTitle(getString(R.string.confirm));
		Bundle b = new Bundle();
		b.putString("DURATION", strDuration);
		b.putString("AMOUNT", strAmount);
		b.putString("INTRESTTYPE", strInterest_rates);
		b.putString("TAXSAVER", tax_saver);
		b.putString("MATURITY", maturity);
		b.putString("DEBITACC", strDebitacc);		
		b.putString("YEAR", year);
		b.putString("MONTH", month);
		b.putString("DAY", day);
		b.putString("MATURITYAMT", maturityamount);
		b.putString("MATURITYDATE", maturitydate);
		b.putString("INTERATES", intrestrate);
		b.putString("INTERATESAMOUNT", intrestamount);
		b.putString("MONTHDURATION", monthDuration);

		fragment1.setArguments(b);

		fragmentManager1 = getFragmentManager();
		fragmentManager1.beginTransaction().replace(R.id.frame_container, fragment1).commit();  
	}
	
}

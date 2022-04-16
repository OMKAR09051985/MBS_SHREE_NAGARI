package shree_nagari.mbank;

import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.crypto.spec.SecretKeySpec;

import mbLib.Accountbean;
import mbLib.CryptoClass;
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

public class EnableDisableATMCard extends Fragment implements OnClickListener {
	MainActivity act;
	TextView txt_heading, txt_con_amount, txt_con_duration,
			txt_con_interest_dates, txt_con_maturity_dates;
	private static String responseJSON = "NULL";
	private static final String METHOD_NAME="callWebservice";//"getATMCard";
	String[] presidents;
	FragmentManager fragmentManager;
	Fragment fragment;
	String[] acountno;
	String cardno="",atmstatus="",crno="";
	String actno="",crdno="",atmstaus="", retval = "",respcode="",respdesc="",respdesc_GetATMNo="",AccCustId;
	//Switch switch1;
	TextView txt_con_maturity_amt, txt_con_debit_acc, txt_con_scheme,
			lbl_int_rates,card_no;
	EditText atmcardno,TrantxtBal;
	private static String METHOD_NAME1 = "generateOTP";
	Spinner spi_debit_account,spi_status;
	ImageButton spinner_btn_interest, spinner_btn_tax, spinner_btn_maturity,
			spinner_btn_debit_acc, btn_back, btn_home, spinner_btn_status;
	Button fd_account_submit, btn_confirm;
	DialogBox dbs;
	Accounts acArray[];
	Intent in;
	ProgressBar pb_wait;
	String ed="";
	String retMess = "", str2 = "",str = "", acnt_inf = "", all_acnts,retVal="",status="",statval="";
	String stringValue = "",custId="",accno="";
	int flag = 0;
	String tax_saver = "", maturity = "";

	double ed_instrate = 0.0;
	boolean noAccounts = false;
	LinearLayout fd_account_layout, confirm_layout;
	ProgressBar fd_confirm_bar, fd_account_bar;
	DatabaseManagement dbms;
	String retStr = "", userId = "", cust_mob_no = "", transferType = "",retvalwbs="";
	ArrayList<String> arrListTemp = new ArrayList<String>();
	ImageView btn_home1,btn_logout;
	
	private static final String MY_SESSION = "my_session";

	PrivateKey var1=null;	  
	String var5="",var3="";
	SecretKeySpec var2=null;
	private String benInfo = "";
	TextView txt_amount, txt_duration, txt_interest_rates, txt_maturity_dates,
			txt_maturity_amt, txt_debit_acc, txt_conf_scheme;
	ImageView img_heading;
	String trn_str;
	List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
	ArrayList<String> arrList = new ArrayList<String>();
	ArrayList<Accountbean> arrList1;

	public EnableDisableATMCard() {
	}

	@SuppressLint("ValidFragment")
	public EnableDisableATMCard(MainActivity a) {
		System.out.println("EnableDisableATMCard()" + a);
		act = a;
		}

	public void onBackPressed() {
		return;
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		System.out.println("onCreateView() OpenFDAccount");
		View rootView = inflater.inflate(R.layout.enable_disable_atm_card, container, false);
		var1 = act.var1;
		var3 = act.var3;
		Log.e("DSP","var1atm===="+var1);
		Log.e("DSP","var3atm===="+var3);
		img_heading = (ImageView) rootView.findViewById(R.id.img_heading);
		img_heading.setBackgroundResource(R.mipmap.atm);
		
		spinner_btn_debit_acc=(ImageButton)rootView.findViewById(R.id.spinner_btn_debit_acc);
		spinner_btn_debit_acc.setOnClickListener(this);
		spinner_btn_status=(ImageButton)rootView.findViewById(R.id.spinner_btn_status);
		spinner_btn_status.setOnClickListener(this);
		this.dbs = new DialogBox(act);
		SharedPreferences localSharedPreferences = act.getSharedPreferences(
				"my_session", 0);
		dbms = new DatabaseManagement("shree_nagari.mbank", "listMobileBanking");
		// this.e = localSharedPreferences.edit();
		/*this.stringValue = localSharedPreferences.getString("retValStr",
				"retValStr");
	/	this.cust_name = localSharedPreferences.getString("cust_name",
				"cust_name");
		this.custId = localSharedPreferences.getString("custId", "custId");
		mobPin = localSharedPreferences.getString("pin", "pin");
	*/	// this.dbs = new DialogBox(act);
		txt_heading = (TextView) rootView.findViewById(R.id.txt_heading);
		txt_heading.setText(getString(R.string.lbl_atm_enable_desable));
		/*lbl_int_rates = (TextView) rootView.findViewById(R.id.lbl_int_rates);*/
		card_no = (TextView) rootView.findViewById(R.id.card_no);
		// /*****EditText
		// txtAmt =(EditText) rootView.findViewById(R.id.txt_amounts);
		atmcardno = (EditText) rootView.findViewById(R.id.atmcardno);
		atmcardno.setEnabled(false);
		atmcardno.setVisibility(View.INVISIBLE);
		//switch1 = (Switch) rootView.findViewById(R.id.switch1);
		presidents = getResources().getStringArray(R.array.Errorinwebservice);
		Cursor c1 = dbms.selectFromTable("SHAREDPREFERENCE", "", null);// ("select * from ",
		// null);
				if (c1 != null) {
				while (c1.moveToNext()) {
				stringValue = c1.getString(0);
				//Log.e("retValStr", "...." + stringValue);
				custId = c1.getString(2);
				//Log.e("custId", "......" + custId);
				userId = c1.getString(3);
			//	Log.e("UserId", "......" + userId);
				cust_mob_no = c1.getString(4);
				//Log.e("cust_mobNO", "..." + cust_mob_no);
				}
				}
		
		// /*********Spinner
		spi_debit_account = (Spinner) rootView
				.findViewById(R.id.spi_debit_account);
		if (spi_debit_account == null)
			System.out.println("spi_debit_account is null");
		else {
			System.out.println("spi_debit_account is not null");
			spi_debit_account.requestFocus();
		}
		spi_status = (Spinner) rootView.findViewById(R.id.spi_status);
		if (spi_status == null)
			System.out.println("spi_status is null");
		else {
			System.out.println("spi_status is not null");
			spi_status.requestFocus();
		}
		all_acnts = stringValue;
		addAccounts(all_acnts);

		// /*********Button
		fd_account_submit = (Button) rootView
				.findViewById(R.id.fd_account_submit);
		fd_account_submit.setOnClickListener(this);
		// btn_confirm=(Button) rootView.findViewById(R.id.btn_confirm);
		// btn_confirm.setOnClickListener(this);
		//btn_home = (ImageButton) rootView.findViewById(R.id.btn_home);
		//btn_home.setImageResource(R.drawable.ic_home_d);
		//btn_home.setOnClickListener(this);
		//btn_back = (ImageButton) rootView.findViewById(R.id.btn_back);
		//btn_back.setImageResource(R.drawable.backover);
		//btn_back.setOnClickListener(this);
		
		btn_home1 = (ImageView) rootView.findViewById(R.id.btn_home1);
		btn_logout = (ImageView) rootView.findViewById(R.id.btn_logout);
btn_logout.setVisibility(View.GONE);
		btn_home1.setOnClickListener(this);
		btn_logout.setOnClickListener(this);
		List<String> interest = new ArrayList<String>();
		// interest.add("Select");
		interest.add("At Maturity");
		interest.add("Monthly");
		interest.add("Quarterly");
		interest.add("Reinvestment Plan");

		// Creating adapter for spinner
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(
				getActivity(), R.layout.spinner_item, interest);

		// Drop down layout style - list view with radio button
		dataAdapter
				.setDropDownViewResource(R.layout.spinner_dropdown_item);

		// attaching data adapter to spinner
		
		/*switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            	if (isChecked) {
                	atmcardno.setVisibility(View.VISIBLE);
                	card_no.setText("Card No");
                	atmcardno.setEnabled(true);
                   atmstaus="U";
                } else {
                	atmcardno.setVisibility(View.INVISIBLE);
                	card_no.setText("Card No:(XXXX XXXX XXXX "+crno+")");
                	atmcardno.setEnabled(false);
                	atmstaus="T";
                }
            }
        });*/


		arrList.add("Select Status");
		arrList.add("Enable");
		arrList.add("Disable");

		String[] statusArr = new String[arrList.size()];
		statusArr = arrList.toArray(statusArr);
		ArrayAdapter<String> stat = new ArrayAdapter<String>(act,R.layout.spinner_item, statusArr);
		stat.setDropDownViewResource(R.layout.spinner_dropdown_item);
		spi_status.setAdapter(stat);

		spi_status
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
											   int arg2, long arg3) {
						status = spi_status.getItemAtPosition(arg2).toString();
						if(status.equalsIgnoreCase("Enable")){
							atmcardno.setVisibility(View.VISIBLE);
							card_no.setText("Card No");
							atmcardno.setEnabled(true);
							atmstaus="U";
						} else if(status.equalsIgnoreCase("Disable")){
							atmcardno.setVisibility(View.INVISIBLE);
							card_no.setText("Card No:(XXXX XXXX XXXX "+crno+")");
							atmcardno.setEnabled(false);
							atmstaus="T";
						}
					}
					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
						// TODO Auto-generated method stub

					}

				});// end spi_status
		

		spi_debit_account
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						accno = spi_debit_account.getItemAtPosition(arg2).toString();
						Accountbean dataModel = (Accountbean) arg0.getItemAtPosition(arg2);
						//accountNo = dataModel.getAccountNumber();
						AccCustId = dataModel.getAcccustid();
						if(!accno.equals("Select Account")){
							new CallWebServiceGetATMN().execute();
                        }
                        else {
                        	card_no.setText("Card No");
                        	atmcardno.setEnabled(false);
                        	atmcardno.setText("");
                        	//switch1.setChecked(false);
                        }
					}
					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
						// TODO Auto-generated method stub

					}

				});// end spi_debit_account

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

		/*case R.id.btn_home:
			Intent in = new Intent(act, DashboardDesignActivity.class);
			startActivity(in);
			act.finish();
			break;*/
		case R.id.btn_home1:
			Intent in1 = new Intent(act, NewDashboard.class);
			in1.putExtra("VAR1", var1);
			in1.putExtra("VAR3", var3);
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
	
		case R.id.spinner_btn_debit_acc:
			spi_debit_account.performClick();
			break;

			case R.id.spinner_btn_status:
				spi_status.performClick();
				break;

			case R.id.fd_account_submit:
			
			
			actno=spi_debit_account.getSelectedItem().toString().trim();
			acountno=actno.split(" ");
			actno=acountno[0];
			Log.e("atmstaus","atmstausatmstausatmstaus=="+atmstaus);
			Log.e("atmstatus","atmstatusatmstatusatmstatus=="+atmstatus);
			Log.e("ed","ededed=="+ed);
			crdno=atmcardno.getText().toString();
			
			if(atmstaus.equalsIgnoreCase("U") && (crdno.length())==0)
			{
					showAlert(getString(R.string.lbl_enteratm));
				
			}
			else
			{
				if(actno.equals("Select")){
					showAlert(getString(R.string.alrt_selec_accno));
				}
				else if(atmstaus.equalsIgnoreCase("U") && (!crdno.equals(cardno)||((crdno.length())!=16))){
					showAlert(getString(R.string.lbl_vadileatm));
				}
				else if(atmstaus.equals(atmstatus)){
					showAlert("Your Card Is Already "+ed);
				}
				else{
					CustomDialogClass alert1=new CustomDialogClass(act, getString(R.string.lbl_doproceed)) {
						@Override
						public void onClick(View v) {
							switch (v.getId()) {
								case R.id.btn_ok:
									flag = chkConnectivity();
									if (flag == 0) {
										CallWebService callWebService=new CallWebService();
										callWebService.execute();
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
					alert1.show();
					/*dbs = new DialogBox(act);
					dbs.get_adb().setMessage(getString(R.string.lbl_doproceed));
					dbs.get_adb().setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface arg0, int arg1) {
									flag = chkConnectivity();
									if (flag == 0) {
										CallWebService callWebService=new CallWebService();
										callWebService.execute();
									}
								}
							});
					dbs.get_adb().setNegativeButton("No",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface arg0, int arg1) {
									arg0.cancel();
								}
							});
					dbs.get_adb().show();*/
				}
			}
			
			break;

		default:
			break;
		}

	}// end onClick
	class CallWebServicelog extends AsyncTask<Void, Void, Void> {
	            JSONObject jsonObj = new JSONObject();
		String ValidationData="";
		LoadProgressBar loadProBarObj = new LoadProgressBar(act);

		@Override
		protected void onPreExecute() {
                   try{
                	   loadProBarObj.show();
                	   respcode="";
                	   retvalwbs="";
                	   respdesc="";
			Log.e("@DEBUG","LOGOUT preExecute()");
                  jsonObj.put("CUSTID", custId);
	              jsonObj.put("IMEINO", MBSUtils.getImeiNumber(act));
	              jsonObj.put("SIMNO", MBSUtils.getSimNumber(act));
	              jsonObj.put("METHODCODE","29");
	             // ValidationData=MBSUtils.getValidationData(act,jsonObj.toString());
		
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
				// retMess = "Error occured";
				getString(R.string.alert_000);
				System.out.println(e.getMessage());
				Log.e("ERROR-OUTER",e.getClass()+" : "+e.getMessage());
			}
			return null;
		}

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
	public void post_successlog(String retvalwbs)
	{
	  respcode="";
	      respdesc="";
	  act.finish();
		System.exit(0);
		
	}

	public int chkConnectivity() { // chkConnectivity
		// System.out.println("============= inside chkConnectivity ================== ");
		// pb_wait.setVisibility(ProgressBar.VISIBLE);
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

	public void setAlert() { 

	
		showAlert(retMess);
	}// end setAlert

	public void showAlert(final String str) {
		// Toast.makeText(this, str, Toast.LENGTH_LONG).show();
		ErrorDialogClass alert = new ErrorDialogClass(act, "" + str) {
			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.btn_ok:
					if(atmstatus.equals("S")||atmstatus.equals("B")||atmstatus.equals("L")||cardno.equals("NA")){
						fragment = new EnableDisableATMCard(act);
						act.setTitle(getString(R.string.lbl_atm_enable_desable));
						fragmentManager = getFragmentManager();
						fragmentManager.beginTransaction()
								.replace(R.id.frame_container, fragment).commit();
						break; 
		            }
					if((str.equalsIgnoreCase(respdesc)) &&  (respcode.equalsIgnoreCase("0")))
					{
						post_success(retval);
					}
					else if((str.equalsIgnoreCase(respdesc)) && (respcode.equalsIgnoreCase("1")))
					{
						this.dismiss();
					}
					if((str.equalsIgnoreCase(respdesc_GetATMNo)) &&  (respcode.equalsIgnoreCase("0")))
					{
						post_GetATMNo(retval);
					}
					else if((str.equalsIgnoreCase(respdesc_GetATMNo)) && (respcode.equalsIgnoreCase("1")))
					{
						this.dismiss();
					}
					/*
					 * if(noAccounts) { Fragment fragment = new
					 * FundTransferMenuActivity(act); FragmentManager
					 * fragmentManager = getFragmentManager();
					 * fragmentManager.beginTransaction()
					 * .replace(R.id.frame_container, fragment).commit(); }
					 * break;
					 */
				default:
					break;
				}
				dismiss();
			}
		};
		alert.show();
	}

	public void clearFields() {}

	/*public void addAccounts(String str) {
		System.out.println("OtherBankTranIFSC IN addAccounts()" + str);

		try {
			ArrayList<String> arrList = new ArrayList<String>();
			String allstr[] = str.split("~");

			int noOfAccounts = allstr.length;
			Accounts acArray[] = new Accounts[noOfAccounts];

			for (int i = 0; i < noOfAccounts; i++) {
				str2 = allstr[i];

				acArray[i] = new Accounts(str2);
				str2 = str2.replaceAll("#", "-");
				String accType = str2.split("-")[2];
				String oprcd = str2.split("-")[7];
				String str2Temp = str2;
				str2 = MBSUtils.get16digitsAccNo(str2);

				if (accType.equals("SB")) // && oprcd.equalsIgnoreCase("O"))
				{
					arrList.add(str2 + " (" + MBSUtils.getAccTypeDesc(accType)
							+ ")");
					arrListTemp.add(str2Temp);
				}
			}

			String[] debAccArr = new String[arrList.size()];
			debAccArr = arrList.toArray(debAccArr);
			
			 * CustomeSpinnerAdapter debAccs = new CustomeSpinnerAdapter(act,
			 * R.layout.spinner_layout, debAccArr);
			 
			ArrayAdapter<String> debAccs = new ArrayAdapter<String>(act,
					android.R.layout.simple_spinner_item, debAccArr);
			debAccs.setDropDownViewResource(R.layout.spinner_dropdown_item);
			spi_debit_account.setAdapter(debAccs);
			acnt_inf = spi_debit_account.getItemAtPosition(
					spi_debit_account.getSelectedItemPosition()).toString();
			// Log.i("OtherBankTranIFSC MAYURI....", acnt_inf);
		} catch (Exception e) {
			System.out.println("" + e);
		}

	}// end addAccount
*/
	public void addAccounts(String str) {
		System.out.println("OtherBankTranRTGS IN addAccounts()" + str);

		try {
			ArrayList<String> arrList = new ArrayList<String>();
			arrList1 = new ArrayList<>();
			String allstr[] = str.split("~");

			int noOfAccounts = allstr.length;
			arrList.add("Select Account");
			acArray = new Accounts[noOfAccounts];
			int j=0;
			for (int i = 0; i < noOfAccounts; i++) {
				str2 = allstr[i];
				String tempStr=str2;
				
				str2 = str2.replaceAll("#", "-");
				String accType = str2.split("-")[2];
				String oprcd = str2.split("-")[7];

				String AccCustID = str2.split("-")[11];

				String str2Temp = str2;
				str2 = MBSUtils.get16digitsAccNo(str2);
				// String withdrawalAllowed=allstr[i].split("#")[10];
				// Log.e("withdrawalAllowed", withdrawalAllowed);
				// Log.e("withdrawalAllow444ed", withdrawalAllowed);
				// showAlert("withdrawalAllowed="+withdrawalAllowed);
				// Toast.makeText(act, "withdrawalAllowed="+withdrawalAllowed,
				// Toast.LENGTH_LONG).show();
				String withdrawalAllowed=allstr[i].split("#")[10];
				if ((accType.equals("SB") || accType.equals("CA") || accType.equals("LO"))
						&& oprcd.equalsIgnoreCase("O") && withdrawalAllowed.equalsIgnoreCase("Y")) {
					acArray[j++] = new Accounts(tempStr);
					Accountbean accountbeanobj = new Accountbean();
					accountbeanobj.setAccountinfo(str2 + " (" + MBSUtils.getAccTypeDesc(accType) + ")");
					accountbeanobj.setAccountNumber(str2);
					accountbeanobj.setAcccustid(AccCustID);
					arrList1.add(accountbeanobj);
					//arrList.add(str2);
					arrList.add(str2+" ("+MBSUtils.getAccTypeDesc(accType)+")");
					arrListTemp.add(str2Temp);
				}
			}

			String[] debAccArr = new String[arrList1.size()];
			debAccArr = arrList1.toArray(debAccArr);
			/*
			 * CustomeSpinnerAdapter debAccs = new CustomeSpinnerAdapter(act,
			 * R.layout.spinner_layout, debAccArr);
			 */
			ArrayAdapter<String> debAccs = new ArrayAdapter<String>(act,
					R.layout.spinner_item, debAccArr);
			debAccs.setDropDownViewResource(R.layout.spinner_dropdown_item);
			spi_debit_account.setAdapter(debAccs);
			
		} catch (Exception e) {
			System.out.println("" + e);
			// Toast.makeText(act, ""+e, Toast.LENGTH_LONG).show();
		}

	}// end addAccount


	/* public class InputDialogBox extends Dialog implements OnClickListener { 
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
		SimpleAdapter adapter;
		public InputDialogBox(Activity activity) { 
			super(activity);
			this.activity = activity;
		}// end InputDialogBox

		protected void onCreate(Bundle bdn) { 
			super.onCreate(bdn);
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			setContentView(R.layout.dialog_int_rates);
			// mpin = (EditText) findViewById(R.id.txtMpin);
			// interest=(TextView) findViewById(R.id.interest);
			listview = (ListView) findViewById(R.id.listView1);
			btnOk = (Button) findViewById(R.id.btnOK);
			// mpin.setVisibility(EditText.VISIBLE);
			btnOk.setVisibility(Button.VISIBLE);
			btnOk.setOnClickListener(this);
			setValues();
		}

		public void setValues() {
			// trn_str =
			// "SUCCESS~    Duration                 # ROI ~30 - 90 Days               # 6.5 %~91 - 180 Days            # 7.0 %~181 - 270 Days          # 7.5 %~271 - 365 Days          # 8.0 %~366 Days & above     # 9.0 %";
			// String trn_str=retMess;
			trn_str = "SUCCESS~    Duration                      # ROI ~30 Days - 90 Days          # 6.5 %~91 Days - 180 Days       # 7.0 %~181 Days - 270 Days     # 7.5 %~271 Days - 365 Days     # 8.0 %~366 Days & above          # 9.0 %";

			// trn_str="SUCCESS~Account Opening#Address
			// System.out.println("trn_str:"+trn_str);
			String str1[] = trn_str.split("~");
			// System.out.println("str1[0]:"+str1[0]);
			if (str1[0].equalsIgnoreCase("SUCCESS")) {
				// System.out.println("str1[1]:"+str1[1]);
				// String string1[]=str1[1].split(",-,");
				// System.out.println("strring1.length:"+str1.length);
				// List<String> content = new ArrayList<String>();
				for (int j = 1; j < str1.length; j++) {
					// Log.i("MINI STMT","IN 2nd FOR........");
					// System.out.println("j..............................:"+j);
					// System.out.println("string1"+str1[j]);

					String string2[] = str1[j].split("#");
					HashMap<String, String> map = new HashMap<String, String>();
					String[] from = new String[] { "col_0", "col_1" };
					int[] to = new int[] { R.id.item1, R.id.item2 };

					map.put("col_0", string2[0]);
					map.put("col_1", string2[1].trim());

					fillMaps.add(map);
					Log.e("Fillmap", "length" + fillMaps.size());
					 adapter = new SimpleAdapter(activity,
							fillMaps, R.layout.interest_rats_list, from, to);
					listview.setAdapter(adapter);
				}
				// accNo.setText(MBSUtils.get16digitsAccNo(str));
			}
		}

		@Override
		public void onClick(View v) {
			try {
				listview.setAdapter(null);
				//adapter.notifyDataSetChanged();
				//((BaseAdapter) listview.getAdapter()).notifyDataSetChanged();
				// saveData();
				this.hide();

			} catch (Exception e) {
				e.printStackTrace();
				System.out
						.println("Exception in InputDialogBox of onClick:=====>"
								+ e);
			}
		}// end onClick
	}// end InputDialogBox */
	
	public class InputDialogBox extends Dialog implements OnClickListener {
		//OpenFDAccount act;
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
			// mpin = (EditText) findViewById(R.id.txtMpin);
			listview = (ListView) findViewById(R.id.listView1);
			btnOk = (Button) findViewById(R.id.btnOK);
			// mpin.setVisibility(EditText.VISIBLE);
			btnOk.setVisibility(Button.VISIBLE);
			btnOk.setOnClickListener(this);

			setValues();
		}

		public void setValues() { 

			// String trn_str=retMess;
			//	trn_str = "SUCCESS~    Duration                 # ROI ~30 - 90 Days               # 6.5 %~91 - 180 Days            # 7.0 %~181 - 270 Days          # 7.5 %~271 - 365 Days          # 8.0 %~366 Days & above     # 9.0 %";
			trn_str = "SUCCESS~    Duration                      # ROI ~30 Days - 90 Days          # 6.5 %~91 Days - 180 Days       # 7.0 %~181 Days - 270 Days     # 7.5 %~271 Days - 365 Days     # 8.0 %~366 Days & above          # 9.0 %";
			// trn_str="SUCCESS~Account Opening#Address
			// System.out.println("trn_str:"+trn_str);
			String str1[] = trn_str.split("~");
			// System.out.println("str1[0]:"+str1[0]);
			if (str1[0].equalsIgnoreCase("SUCCESS")) {
				// System.out.println("str1[1]:"+str1[1]);
				// String string1[]=str1[1].split(",-,");
				// System.out.println("strring1.length:"+str1.length);
				// List<String> content = new ArrayList<String>();
				for (int j = 1; j < str1.length; j++) {
					// Log.i("MINI STMT","IN 2nd FOR........");
					// System.out.println("j..............................:"+j);
					// System.out.println("string1"+str1[j]);

					String string2[] = str1[j].split("#");
					HashMap<String, String> map = new HashMap<String, String>();
					String[] from = new String[] { "col_0", "col_1" };
					int[] to = new int[] { R.id.item1, R.id.item2 };

					map.put("col_0", string2[0]);
					map.put("col_1", string2[1].trim());

					fillMaps.add(map);
					Log.e("Fillmap","length"+fillMaps.size());
					SimpleAdapter adapter = new SimpleAdapter(activity,
							fillMaps, R.layout.interest_rats_list, from, to);
					listview.setAdapter(adapter);
				}
				// accNo.setText(MBSUtils.get16digitsAccNo(str));
			}
		}

		@Override
		public void onClick(View v) { 
			try {

				// saveData();
				this.hide();

			} catch (Exception e) {
				e.printStackTrace();
				System.out
						.println("Exception in InputDialogBox of onClick:=====>"
								+ e);
			}
		}// end onClick
		}
	// end InputDialogBox


public class CallWebServiceGetATMN extends AsyncTask<Void, Void, Void>{
		LoadProgressBar loadProBarObj = new LoadProgressBar(act);
		String generatedXML = "",retVal="";
		String ValidationData="";
		JSONObject jsonobj = new JSONObject();
		
		@Override
		protected void onPreExecute() {
			  	loadProBarObj.show();
			retval = "";respcode="";respdesc="";respdesc_GetATMNo="";	
			
			try {
				jsonobj.put("CUSTID", custId+"#~#" + AccCustId);
				jsonobj.put("ACCNO", accno);
				jsonobj.put("IMEINO", MBSUtils.getImeiNumber(act));
				jsonobj.put("SIMNO", MBSUtils.getSimNumber(act));
				jsonobj.put("METHODCODE","62");
				
				Log.e("DSP","jsonobjatm===="+jsonobj);
				// ValidationData=MBSUtils.getValidationData(act,jsonobj.toString());
				
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
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
										
				request.addProperty("value1", CryptoClass.Function5(jsonobj.toString(), var2));
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
				Log.e("GenerateCUSTID ", "in doInBackground()	responseJSON :" + e);
				responseJSON = "NULL";
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
			*/
			 Log.e("DSP","strenableAtm=="+str);
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
				respdesc_GetATMNo= jsonObj.getString("RESPDESC");
			}
			else
			{	
				respdesc_GetATMNo= "";
			}
			
		
		if(respdesc_GetATMNo.length()>0)
		{
			showAlert(respdesc_GetATMNo);
		}
		else{
        if (retval.indexOf("SUCCESS~") > -1) {
        	post_GetATMNo(retval);
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
		
}// onPostExecute
}	
public void post_GetATMNo(String retval){
	try{
		
			respcode="-1";respdesc_GetATMNo= "";
		    String str1=retval.split("~")[1];
		    String values[] = retval.split("~")[1].split("#");
		    cardno=values[0];
		    if(cardno.equals("NA"))
		    {
		    	showAlert(getString(R.string.alrt_cardEnabledesableNoAtmIssue));
		    }
		    else if(cardno.length()!=16)
		    {
		    	showAlert(getString(R.string.alrt_cardEnabledesableInvalidcardNo));
		    }
		    else
		    {
		        crno=cardno.substring(12, 16);
		    	atmstatus=values[1];
	    
		    	if(atmstatus.equals("T")){
		        	//1011234567890987
		        	ed="Disable";
		        	atmstaus="T";
		        	atmcardno.setVisibility(View.INVISIBLE);
		        	card_no.setText("Card No:(XXXX XXXX XXXX "+crno+")");		        	
		        	//atmcardno.setEnabled(true);
		        	//switch1.setChecked(false);
					ArrayAdapter<String> spinnerAdap = (ArrayAdapter<String>) spi_status.getAdapter();
					int spinnerPosition = spinnerAdap.getPosition("Disable");
					spi_status.setSelection(spinnerPosition);
		        }
		        else if(atmstatus.equals("U")){
		        	ed="Enabled";
		        	atmstaus="U";
		        	atmcardno.setText(cardno);
		        	//switch1.setChecked(true);
					ArrayAdapter<String> spinnerAdap = (ArrayAdapter<String>) spi_status.getAdapter();
					int spinnerPosition = spinnerAdap.getPosition("Enable");
					spi_status.setSelection(spinnerPosition);
		        	atmcardno.setEnabled(false);
		        	card_no.setText("Card No");
		        }
			    else if(atmstatus.equals("B"))
			    {
			    	showAlert(getString(R.string.alrt_cardEnabledesableATMBlocked));
			    }
			    else if(atmstatus.equals("L"))
			    {
			    	showAlert(getString(R.string.alrt_cardEnabledesableATMLoss));
			    }
			    else if(atmstatus.equals("S"))
			    {
			    	showAlert(getString(R.string.alrt_cardEnabledesableATMStolen));
			    }
		    } 
		
	}
	catch(Exception e){
		System.out.println("Exception "+e);
		 Log.e("Exception=SAM=","Exception= "+e);
		e.printStackTrace();
	}
}

class CallWebService extends AsyncTask<Void, Void, Void> {// CallWebService_resend_otp
	LoadProgressBar loadProBarObj = new LoadProgressBar(act);
	JSONObject jsonObj = new JSONObject();
	boolean isWSCalled = false;
	String ValidationData="";

	@Override
	protected void onPreExecute() {
		loadProBarObj.show();
		retval = "";respcode="";respdesc="";respdesc_GetATMNo="";	
		
		try
		{
			jsonObj.put("CUSTID", custId);
			jsonObj.put("REQSTATUS","R");
			jsonObj.put("REQFROM", "MBSREG");
			jsonObj.put("MOBNO", cust_mob_no);
			jsonObj.put("IMEINO", MBSUtils.getImeiNumber(act));
			jsonObj.put("SIMNO", MBSUtils.getSimNumber(act));
			jsonObj.put("METHODCODE","26");
			
			Log.e("DSP","jsonobjenableatm===="+jsonObj);
			//ValidationData=MBSUtils.getValidationData(act,jsonObj.toString());
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
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
					isWSCalled = true;
					}
			}  catch (Exception e) {
				e.printStackTrace();
				Log.e("In Login", "----------" + e);
				retMess = getString(R.string.alert_000);
				System.out.println("Exception");
				}
		} catch (Exception e) {
				retMess = getString(R.string.alert_000);
			System.out.println(e.getMessage());
			}
		return null;

	}

	@Override
	protected void onPostExecute(final Void result) {
		loadProBarObj.dismiss();
		if (isWSCalled) 
			
		{
				
			JSONObject jsonObj;
			try
			{
				String str=CryptoClass.Function6(var5,var2);
				 jsonObj = new JSONObject(str.trim());
				/*ValidationData=xml_data[1].trim();
				if(ValidationData.equals(MBSUtils.getValidationData(act, xml_data[0].trim())))
				{*/
				 Log.e("DSP","strenableAtm=="+str);
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
					respdesc= jsonObj.getString("RESPDESC");
				}
				else
				{	
					respdesc= "";
				}
				
			
			if(respdesc.length()>0)
			{
				showAlert(respdesc);
			}
			else{
			if(retval.split("~")[0].indexOf("SUCCESS")>-1)
			{
				post_success(retval);
			} 
			else 
			{
				//System.out.println("in else ***************************************");
				retMess = getString(R.string.alert_094);
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
		} else {
			retMess = getString(R.string.alert_000);
			showAlert(retMess);
		}
		
	}
}// CallWebService_resend_otp
public void post_success(String retval){
	respcode="-1";respdesc= "";
	String decryptedAccounts = retval.split("~")[1]+"!!"+actno+"!!"+crdno+"!!"+atmstaus+"!!"+MBSUtils.getImeiNumber(act);
	Intent intent = new Intent(getActivity(), OTPActivity.class);
	Bundle bObj=new Bundle();
	bObj.putString("RETVAL", decryptedAccounts);
	bObj.putString("CUSTID",custId);
	bObj.putString("MOBNO",cust_mob_no);
	bObj.putString("FROMACT","ENABLEATM");
	intent.putExtras(bObj);
	intent.putExtra("VAR1", var1);
	intent.putExtra("VAR3", var3);
	getActivity().startActivity(intent); 
	getActivity().finish();

}
}

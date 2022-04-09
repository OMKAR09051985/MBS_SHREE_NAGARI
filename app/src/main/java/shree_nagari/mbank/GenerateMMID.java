package shree_nagari.mbank;

import java.util.ArrayList;

import mbLib.Accountbean;
import mbLib.CryptoUtil;
import mbLib.DatabaseManagement;
import mbLib.MBSUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import shree_nagari.mbank.R;

import android.annotation.SuppressLint;
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
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

//@SuppressLint("NewApi")
@SuppressLint("ValidFragment")
public class GenerateMMID extends Fragment implements OnClickListener {

	private static  String GET_MMID = "";
	private static String NAMESPACE = "";
	private static String SOAP_ACTION = "";
	private static String URL = "";
	private ListView listView1;
	MainActivity act;
	int flag = 0;
	int check = 0;
	DatabaseManagement dbms;
	Context context;
	private static final String MY_SESSION = "my_session";
	Editor e;
	String retMess = "", retVal = "";
	String stringValue = "", custid = "";// ,accountNo="";
	String all_acnts = "", str2 = "", str = "", req_id = "";
	String acc_type = "SAVING_CUR",respcode="",retvalweb="",generateMMIDrespdesc="";
	int chekacttype = 0;
	TextView txt_heading;
	ImageView img_heading;
	ImageButton btn_home;// ,btn_back;
	Button btn_show_details;
	String acnt_inf = "", accountinfo = "";
	String accNumber = null;
	String[] prgmNameList, prgmNameListTemp;
	RadioButton radio;
	private ArrayList<Accountbean> Accountbean_arr;
	protected String accStr,AccCustId;

	@SuppressLint("ValidFragment")
	public GenerateMMID(MainActivity a) {
		act = a;

	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View rootView = inflater.inflate(R.layout.generatemmid, container,
				false);
		act.frgIndex=83;
		listView1 = (ListView) rootView.findViewById(R.id.listView1);
		txt_heading = (TextView) rootView.findViewById(R.id.txt_heading);
		img_heading = (ImageView) rootView.findViewById(R.id.img_heading);
		img_heading.setBackgroundResource(R.mipmap.other_services);

		btn_home = (ImageButton) rootView.findViewById(R.id.btn_home);
		
		btn_home.setOnClickListener(this);

		btn_show_details = (Button) rootView.findViewById(R.id.btnShowDetails);
		btn_show_details.setOnClickListener(this);
		dbms = new DatabaseManagement("shree_nagari.mbank", "listMobileBanking");
		// SharedPreferences sp = act.getSharedPreferences(MY_SESSION,
		// Context.MODE_PRIVATE);
		// e = sp.edit();
		// String mmid = sp.getString("mmid","mmid");
		// e = sp.edit();
		// stringValue = sp.getString("retValStr", "retValStr");
		Cursor c1 = dbms.selectFromTable("SHAREDPREFERENCE", "", null);// ("select * from ",
																		// null);
		if (c1 != null) {
			while (c1.moveToNext()) {
				stringValue = c1.getString(0);
				//Log.e("retvalstr", "....." + stringValue);
				custid = c1.getString(2);
				//Log.e("custId", "......" + custid);

			}
		}
		all_acnts = stringValue;
		txt_heading.setText(getString(R.string.lbl_mmid));
		addAccounts(all_acnts, acc_type);

		// SharedPreferences sp =
		// act.getSharedPreferences(MY_SESSION,Context.MODE_PRIVATE);
		// e = sp.edit();
		// custid = sp.getString("custId", "custId");
		flag = chkConnectivity();
		/*
		 * if (flag == 0) { new CallWebServiceGetMMID().execute(); }
		 */

		return rootView;
	}

	public void addAccounts(String all_accstr, String acc_type) {
		try {
			Accountbean_arr = new ArrayList<Accountbean>();
			ArrayList<Accountbean> bean_arr = new ArrayList<Accountbean>();

			ArrayList<String> savingArrList = new ArrayList<String>();
			ArrayList<String> arrListTemp = new ArrayList<String>();

			ArrayList<String> arrList = new ArrayList<String>();
			String allstr[] = all_accstr.split("~");

			int noOfAccounts = allstr.length;
			Accounts acArray[] = new Accounts[noOfAccounts];
			// System.out.println("HomeFragment noOfAccounts:" + noOfAccounts);
			for (int i = 0; i < noOfAccounts; i++) {
				str2 = allstr[i];
				acArray[i] = new Accounts(str2);
				str2 = str2.replaceAll("#", "-");

				String acType = str2.split("-")[2];
				String oprcd = str2.split("-")[7];
				String AccCustID = str2.split("-")[11];
				String str2Temp = str2;
				str2 = MBSUtils.get16digitsAccNo(str2);

				Accountbean accountbean = new Accountbean();

				if (((acType.equals("SB")) || (acType.equals("LO")) || (acType
						.equals("CA"))) && oprcd.equalsIgnoreCase("O")) {
					bean_arr.add(accountbean);
					arrList.add(str2 + " (" + MBSUtils.getAccTypeDesc(acType)
							+ ")");
					arrListTemp.add(str2Temp);
					// arrListTemp.add(str2);
				}
				Log.e("arrList", "arrList" + arrList);
				accountbean.setAccStr(str2Temp);
				accountbean.setAccountinfo(str2 + " ("
						+ MBSUtils.getAccTypeDesc(acType) + ")");
				accountbean.setAccountNumber(str2);
				accountbean.setAcccustid(AccCustID);
				// accountbean.setMainType("LO");
				// accountbean.setOprcd(str2Temp.split("-")[7]);
				// }
			}
			Accountbean_arr = bean_arr;
			arrList = savingArrList;

			// Log.e("HomeFragment","arrList=="+arrList);
			int[] prgmImages = new int[arrList.size()];

			for (int x = 0; x < arrList.size(); x++) {
				prgmImages[x] = R.mipmap.arrow;
			}
			prgmNameList = new String[arrList.size()];
			prgmNameList = arrList.toArray(prgmNameList);

			prgmNameListTemp = new String[arrListTemp.size()];
			prgmNameListTemp = arrListTemp.toArray(prgmNameListTemp);

			// Log.e("Debug@HomeFragment ","Before from adding accounts");

			if (Accountbean_arr.size() > 0) {
				// Customlist_radioadt adapter = new
				// Customlist_radioadt(act,Accountbean_arr);
				Customlist_radioadt adapter = new Customlist_radioadt(act,Accountbean_arr);
				listView1.setAdapter(adapter);
				listView1.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
			} else {
				// showAlert(getString(R.string.alert_089));
				Toast.makeText(act, getString(R.string.alert_089),
						Toast.LENGTH_LONG).show();

				Intent in = new Intent(act, NewDashboard.class);
				//in1.putExtra("VAR1", var1);
				//in1.putExtra("VAR3", var3);
				startActivity(in);
				act.finish();
			}
			// Log.e("Debug@HomeFragment ","After from adding accounts");
			listView1
					.setOnItemClickListener(new AdapterView.OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> adapterView,
								View view, int i, long l) {
							// Log.e("Debug@HomeFragment ","Click Added for Radio.");
							btn_show_details.setEnabled(true);

							// f = prgmNameListTemp[i];
							accNumber = Accountbean_arr.get(i)
									.getAccountNumber();
							accStr = Accountbean_arr.get(i).getAccStr();
							acnt_inf = Accountbean_arr.get(i).getAccountinfo();
							AccCustId = Accountbean_arr.get(i).getAcccustid();
							Log.e("Omkar ", "accStr=" + accStr);
							Log.e("Omkar ", "accNumber=" + accNumber);
							for (int i1 = 0; i1 < adapterView.getCount(); i1++) {

								try {

									View v = adapterView.getChildAt(i1);
									// RadioButton
									radio = (RadioButton) v
											.findViewById(R.id.radio);
									radio.setChecked(false);

								} catch (Exception e) {
									Log.e("radio button", "radio");
									e.printStackTrace();
								}

							}

							try {
								// RadioButton

								radio = (RadioButton) view
										.findViewById(R.id.radio);
								radio.setChecked(true);

							} catch (Exception e) {
								Log.e("radio button", "radio");
								e.printStackTrace();
							}

							act.setTitle(act
									.getString(R.string.lbl_acc_details));
						}
					});

		} catch (Exception e) {
			Log.e("EXCEPTION", "---------------" + e);
			System.out.println("" + e);
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		// Log.e("onClick Event ","Clicked");
		if (v.getId() == R.id.btn_home)// v.getId()==R.id.btn_back||
		{
			Intent in = new Intent(act, NewDashboard.class);
			startActivity(in);
			act.finish();
		} else if (v.getId() == R.id.btnShowDetails) {

			try {
				this.flag = chkConnectivity();
				Log.e("ohtertranImpsbtn_submit", " SUBMIT SUBMIT" + flag);
				if (this.flag == 0) {
					// saveData();
					CallWebServiceGetMMID c = new CallWebServiceGetMMID();
					c.execute();
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("Exception in CallWebServiceGetSrvcCharg is:"+ e);
			}

		}
	}

	public void showAlert(final String str) {
		// Toast.makeText(this, str, Toast.LENGTH_LONG).show();
		ErrorDialogClass alert = new ErrorDialogClass(act, "" + str)
		{@Override
			public void onClick(View v)

			{
				//Log.e("SetMPIN","SetMPIN...btn CLicked=="+R.id.btn_ok+"==="+v.getId());
				switch (v.getId()) 
				{
					case R.id.btn_ok:
						//Log.e("SetMPIN","SetMPIN...CASE trru="+WSCalled);
						if((str.equalsIgnoreCase(generateMMIDrespdesc)) &&  (respcode.equalsIgnoreCase("0")))
						{
							post_successgenerateMMID(retvalweb);
						}
						else if((str.equalsIgnoreCase(generateMMIDrespdesc)) && (respcode.equalsIgnoreCase("1")))
						{
							this.dismiss();
						}
						else
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

	class CallWebServiceGetMMID extends AsyncTask<Void, Void, Void> {// CallWebServiceGetMMID

		String retval = "";
		
		LoadProgressBar loadProBarObj = new LoadProgressBar(act);
		// String[] xmlTags = { "CUSTID", "ACCOUNTNO","IMEINO"};
		String[] xmlTags = {"PARAMS","CHECKSUM"};
		String[] valuesToEncrypt = new String[2];
		JSONObject jsonObj = new JSONObject();
		String generatedXML = "";
		String ValidationData="";

		@Override
		protected void onPreExecute() {
			try {
				respcode="";
				retvalweb="";
				generateMMIDrespdesc="";
				// p_wait.setVisibility(ProgressBar.VISIBLE);
				loadProBarObj.show();
				Log.e("Jayesh ", "Jayesh=" + custid);
				// System.out.println("custId:"+custid);
				// System.out.println("accountNo:"+accountNo);

				jsonObj.put("CUSTID", custid+"#~#"+AccCustId);
				jsonObj.put("ACCOUNTNO", accNumber);
				jsonObj.put("IMEINO", MBSUtils.getImeiNumber(act));
				jsonObj.put("SIMNO", MBSUtils.getSimNumber(act));
				jsonObj.put("METHODCODE","45");
				ValidationData=MBSUtils.getValidationData(act,jsonObj.toString());
				// valuesToEncrypt[0] = custid;
				// valuesToEncrypt[1] = accNumber;
				// valuesToEncrypt[2] = MBSUtils.getImeiNumber(act);
			} catch (JSONException je) {
				je.printStackTrace();
			}
			valuesToEncrypt[0] =  jsonObj.toString();
			valuesToEncrypt[1] =  ValidationData;
			generatedXML = CryptoUtil.generateXML(xmlTags, valuesToEncrypt);
			// System.out.println("&&&&&&&&&& generatedXML " + generatedXML);
			Log.e("Ganesh ", "custid=" + custid);
			Log.e("Ganesh ", "accountNo=" + accNumber);
			//Log.e("Ganesh ", "valuesToEncrypt[2]=" + valuesToEncrypt[2]);
		};

		@Override
		protected Void doInBackground(Void... arg0) {
			// //System.out.println("============= inside doInBackground =================");
			NAMESPACE = getString(R.string.namespace);
			URL = getString(R.string.url);
			SOAP_ACTION = getString(R.string.soap_action);
			GET_MMID = "mbsInterCall";//"buildMMIDWS";
			SoapObject request = new SoapObject(NAMESPACE, GET_MMID);

			request.addProperty("para_value", generatedXML);
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			envelope.setOutputSoapObject(request);
			HttpTransportSE androidHttpTransport = new HttpTransportSE(URL,
					15000);
			// System.out.println("============= inside doInBackground 2 =================");
			try {

				// Log.i("LoanAccountDetail   ", "111");
				androidHttpTransport.call(SOAP_ACTION, envelope);
				// Log.i("LoanAccountDetail   ", "222");
				// System.out.println(envelope.bodyIn.toString());
				// Log.i("LoanAccountDetail   ", "333");
				retval = envelope.bodyIn.toString().trim();
				// Log.e("LoanAccountDetail", retVal);
				// Log.i("LoanAccountDetail   retval", retval);
				// System.out.println("LoanAccountDetail    retval-----"+
				// retval);
				// pb_wait.setVisibility(ProgressBar.INVISIBLE);
				int pos = envelope.bodyIn.toString().trim().indexOf("=");
				retval = retval.substring(pos + 1, retval.length() - 3);
				// System.out.println("LoanAccountDetail    retval AFTER SUBSTR-----"+
				// retval);

			} catch (Exception e) {
				e.printStackTrace();
				Log.e("LoanAccountDetail", retVal);
				System.out.println("LoanAccountDetail   Exception" + e);
			}
			return null;
		}// end doInBackground

		protected void onPostExecute(final Void result) {
			// LOANDETAILS
			// String[] xmlTags = { "RETSTR" };
			
			loadProBarObj.dismiss();
			// Log.e("RetVal", retval);
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
					retvalweb = jsonObj.getString("RETVAL");
				}
				else
				{
					retvalweb = "";
				}
				if (jsonObj.has("RESPDESC"))
				{
					generateMMIDrespdesc = jsonObj.getString("RESPDESC");
				}
				else
				{	
					generateMMIDrespdesc = "";
				}
				
			if(generateMMIDrespdesc.length()>0)
			{
				showAlert(generateMMIDrespdesc);
			}
			else{
			if (retvalweb.indexOf("FAILED") > -1) {
				String msg[] = retvalweb.split("~");
				if (msg[1].equalsIgnoreCase("NA")) {
					// if(msg[2]!=null || msg[2].length()>0)
					Log.e("Ganesh ",
							" Failed NA req_id.length()=" + msg[2].length());
					if (msg[2].length() > 0) // msg[2]==null ||
												// msg[2].length()==0)
					{
						req_id = msg[2];
						Log.e("Ganesh ", " Failed NA req_id=" + req_id);
						Log.e("Ganesh ", "Failed NA req_id=" + req_id);
						retMess = getString(R.string.alert_160) + " " + req_id;
						// retMess = getString(R.string.alert_158);
					} else {
						/*
						 * req_id=msg[2];
						 * Log.e("Ganesh "," Failed NA req_id="+req_id);
						 * Log.e("Ganesh ","Failed NA req_id="+req_id); retMess
						 * = getString(R.string.alert_160)+" "+req_id;
						 */
						retMess = getString(R.string.alert_158);
					}
					showAlert(retMess);
				}
				/*
				 * else {
				 * 
				 * Log.e("Ganesh "," Failed NA req_id="+req_id);
				 * Log.e("Ganesh ","Failed NA req_id="+req_id); retMess =
				 * getString(R.string.alert_158)+" "+req_id; showAlert(retMess);
				 * }
				 */
			} else if (retvalweb.indexOf("SUCCESS") > -1) {
				post_successgenerateMMID(retvalweb);
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
		}// end onPostExecute
	}// end CallWebServiceGetMMID

	public 	void post_successgenerateMMID(String retvalweb)
	{
		String mmid = "";
		respcode="";
		generateMMIDrespdesc="";
		// decryptedRetVal=decryptedRetVal.split("SUCCESS~")[1];
		// String[] retValues=decryptedRetVal.split("#");
		String msg[] = retvalweb.split("~");
		if (msg[1].equals("NA")) {
			if (msg[2] != null || msg[2].length() > 0) {
				req_id = msg[2];
				Log.e("Ganesh ", " Failed NA req_id=" + req_id);
				Log.e("Ganesh ", "Failed NA req_id=" + req_id);
				retMess = getString(R.string.alert_159) + " " + req_id;
			}

			/*
			 * req_id=msg[2]; Log.e("Ganesh ","req_id="+req_id);
			 * Log.e("Ganesh ","req_id="+req_id); retMess =
			 * getString(R.string.alert_159)+" "+req_id;
			 */
			// retMess = getString(R.string.);
			showAlert(retMess);
		} else {
			// String msg[] = xml_data[0].split("~");
			/*
			 * mmid=msg[1]; FragmentManager fragmentManager; Fragment
			 * fragment = new OtherServicesMenuActivity(act); Bundle
			 * b=new Bundle(); b.putString("MMID", mmid);
			 * b.putString("ACCNO", accNumber);
			 * b.putString("REQUEST",req_id); fragment.setArguments(b);
			 * act.setTitle(getString(R.string.lbl_ur_mmid));
			 * fragmentManager = getFragmentManager();
			 * fragmentManager.beginTransaction()
			 * .replace(R.id.frame_container, fragment).commit();
			 */
			mmid = msg[1];
			Log.e("Ganesh ", "mmid=" + mmid);
			String mobno = msg[2];
			FragmentManager fragmentManager;
			Fragment fragment = new ShowMMID(act);
			Bundle b = new Bundle();
			b.putString("MMID", mmid);
			b.putString("ACCNO", accNumber);
			b.putString("REQUEST", req_id);
			b.putString("MOBNO", mobno);
			b.putString("CUSTID", custid);
			b.putString("AccCustId", AccCustId);

			fragment.setArguments(b);
			act.setTitle(getString(R.string.lbl_ur_mmid));
			fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.frame_container, fragment).commit();

		}

		// account no
		// mmid
	
	}
	public int chkConnectivity() {
		ConnectivityManager cm = (ConnectivityManager) act
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		try {
			State state = ni.getState();
			boolean state1 = ni.isAvailable();
			// System.out.println("state1 ---------" + state1);
			if (state1) {
				switch (state) {
				case CONNECTED:
					if (ni.getType() == ConnectivityManager.TYPE_MOBILE
							|| ni.getType() == ConnectivityManager.TYPE_WIFI) {

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
			Log.e("EXCEPTION", "---------------" + ne);
			Log.i("mayuri", "NullPointerException Exception" + ne);
			flag = 1;
			// retMess = "Can Not Get Connection. Please Try Again.";
			retMess = getString(R.string.alert_000);
			showAlert(retMess);

		} catch (Exception e) {
			Log.e("EXCEPTION", "---------------" + e);
			Log.i("mayuri", "Exception" + e);
			flag = 1;
			// retMess = "Connection Problem Occured.";
			retMess = getString(R.string.alert_000);
			showAlert(retMess);
		}
		return flag;
	}

}

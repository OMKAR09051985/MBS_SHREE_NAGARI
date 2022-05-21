package shree_nagari.mbank;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.Hashtable;

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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.Uri;
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

import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.client.android.IntentIntegrator;
import com.google.zxing.common.HybridBinarizer;

import shree_nagari.mbank.R;

public class QrcodeSendActivity extends Fragment implements OnClickListener
{
	
	private String benInfo = "",errorCode="";
	ImageView btn_home1,btn_logout;//, btn_back;
	DialogBox dbs;
	DatabaseManagement dbms;
	String respcode="",reTval="",getTransferChargesrespdesc="",saveTransferTranrespdesc="";
	Button btn_submit,btn_confirm,btn_con_back;
	TextView txt_heading,txt_remark,txt_from,txt_to,txt_amount,txt_charges;
	TextView cust_nm,txtTranId,txt_trantype;
	boolean status;
	double balance;
	//SharedPreferences.Editor e;
	int cnt = 0,flag = 0,frmno = 0,tono = 0;
	Intent in;
	ProgressBar pb_wait;
	Spinner spi_debit_account;
	StopPayment stp = null;
	String str = "",str2 = "",stringValue="",benSrno ="",strFromAccNo,strToAccNo, 
			strAmount,strRemark,benAccountNumber = "",drBrnCD = "",drSchmCD = "",
			drAcNo = "",mobPin = "",chrgCrAccNo="",tranPin="",retMess = "",
			custId = "",cust_name = "",acnt_inf,all_acnts,tranId="";
	EditText txtAccNo,txtAmt,txtRemk,txtBalance;
	MainActivity act;
	View mainView;
	LinearLayout confirm_layout,same_bnk_layout;
	boolean noAccounts;
	public String encrptdTranMpin,tranmpin="";
	private ImageButton spinenr_btn2;
	private ImageButton spinenr_btn;
	ArrayList<String> arrListTemp = new ArrayList<String>();
	private LayoutInflater inflater;
	QrcodeSendActivity qrcodeSendAct;
	public String debitAccno="",amt="",reMark="",accNo="",barcode="";
	CustomeSpinnerAdapter debAccs=null;
	PrivateKey var1=null;	  
	String var5="",var3="";
	SecretKeySpec var2=null;
	int flg=0;
	Accounts acArray[];
	ImageView img_heading;
	String retvalwbs = "",respdesc ="";
	public QrcodeSendActivity() 
	{
		qrcodeSendAct = this;
	}

	@SuppressLint("ValidFragment")
	public QrcodeSendActivity(MainActivity m)
	{
		act = m;
		qrcodeSendAct = this;
		flg=0;
	}
	
	@SuppressLint("ValidFragment")
	public QrcodeSendActivity(MainActivity m, String CUSTID, String DBTACCNO, String AMT, String CRACCNO, String REMARK)
	{
		act = m;
		qrcodeSendAct = this;
		custId=CUSTID;
		debitAccno=DBTACCNO;
		amt=AMT;
		accNo=CRACCNO;
		reMark=REMARK;
		flg=1;
		
	}

	@SuppressLint("WrongConstant")
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		View rootView = inflater.inflate(R.layout.sendviaqrcode,container, false);
		noAccounts=false;
		var1 = act.var1;
		var3 = act.var3;
		try {
			this.dbs = new DialogBox(act);
		}catch (Exception e){
			e.printStackTrace();
		}
        dbms = new DatabaseManagement("shree_nagari.mbank", "listMobileBanking");	
        Cursor c1=dbms.selectFromTable("SHAREDPREFERENCE", "", null);//("select * from ", null);
        if(c1!=null)
        {
        	while(c1.moveToNext())
	        {	
        		stringValue=c1.getString(0);
        		//Log.e("retValStr","......"+stringValue);
        		custId=c1.getString(2);
	        	//Log.e("custId","......"+custId);
	        }
        }
      	btn_home1 = (ImageView) rootView.findViewById(R.id.btn_home1);
		btn_logout = (ImageView) rootView.findViewById(R.id.btn_logout);
btn_logout.setVisibility(View.GONE);
		/*btn_back = (ImageButton) rootView.findViewById(R.id.btn_back);*/
		img_heading = (ImageView) rootView.findViewById(R.id.img_heading);
		img_heading.setBackgroundResource(R.mipmap.fund_trnsfer);
		confirm_layout=(LinearLayout)rootView.findViewById(R.id.confirm_layout);
		same_bnk_layout=(LinearLayout)rootView.findViewById(R.id.same_bnk_layout);
		
		//btn_home.setImageResource(R.drawable.ic_home_d);
		//btn_back.setImageResource(R.drawable.backover);
		btn_home1.setOnClickListener(this);
		btn_logout.setOnClickListener(this);
		//btn_back.setOnClickListener(this);
		txt_heading=(TextView)rootView.findViewById(R.id.txt_heading);
		txt_heading.setText(getString(R.string.lbl_qr_send));
		txt_trantype=(TextView)rootView.findViewById(R.id.txt_trantype);		
		btn_confirm = (Button) rootView.findViewById(R.id.btn_confirm);
		btn_con_back= (Button) rootView.findViewById(R.id.btn_confirm_back);
	    txt_remark=(TextView)rootView.findViewById(R.id.txt_remark);
		txt_from=(TextView)rootView.findViewById(R.id.txt_from);
		txt_to=(TextView)rootView.findViewById(R.id.txt_to);
		txt_amount=(TextView)rootView.findViewById(R.id.txt_amount);
		//txt_amount.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(2)});
		txt_charges=(TextView)rootView.findViewById(R.id.txt_charges);
		txtTranId=(TextView)rootView.findViewById(R.id.txt_tranid);
		btn_confirm.setOnClickListener(this);
		btn_con_back.setOnClickListener(this);
		spinenr_btn = (ImageButton) rootView.findViewById(R.id.spinner_btn);
		spinenr_btn.setOnClickListener(this);
		
		spi_debit_account = (Spinner) rootView.findViewById(R.id.sameBnkTranspi_debit_account);
		txtBalance = (EditText) rootView.findViewById(R.id.sameBnkTrantxtBal);
		
		btn_submit = (Button) rootView.findViewById(R.id.sameBnkTranbtn_submit);
		txtAccNo = (EditText) rootView.findViewById(R.id.sameBnkTrantxtAccNo);
		txtAmt = (EditText) rootView.findViewById(R.id.sameBnkTrantxtAmt);
		txtRemk = (EditText) rootView.findViewById(R.id.sameBnkTrantxtRemk);
		pb_wait = (ProgressBar) rootView.findViewById(R.id.sameBnkTranpro_bar);
		btn_submit.setOnClickListener(this);
		
		if(flg==0)
		{	
		/*	spi_debit_account.setOnItemSelectedListener(new OnItemSelectedListener() 
			{
				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) 
				{
	
					String str = arrListTemp.get(spi_debit_account.getSelectedItemPosition());
					retMess = "Selected Account number" + str;
					// setAlert();
					Log.e("TRANSFER","str==="+str);
					String debitAc[] = str.split("-");
				}// end onItemSelected
	
				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
	
				}// end onNothingSelected
	
*/
	spi_debit_account
			.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {

		//String str=spi_debit_account.getItemAtPosition(spi_debit_account.getSelectedItemPosition()).toString();
		//	if(arg2 !=0)
					str=spi_debit_account.getSelectedItem().toString();
					
					if (arg2 == 0)
					{
						txtBalance.setText("");
					}
						
					else if (arg2 != 0) {
					
                    if(str.equalsIgnoreCase("Select Debit Account"))
					{
                           txtBalance.setText("");
					}else
					{
				if (spi_debit_account.getCount() > 0) {
					
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
				}// end onItemSelected

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {

				}// end onNothingSelected
			});// end spi_debit_account
			all_acnts = stringValue;

			addAccounts(all_acnts);
			this.pb_wait.setMax(10);
			this.pb_wait.setProgress(1);
			this.pb_wait.setVisibility(4);
			txtAmt.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(2)});
		}
		else{
			new CallWebServiceGetSrvcCharg().execute();
		}
		return rootView;
	}

	public void addAccounts(String str) {
		//System.out.println("SameBankTransfer IN addAccounts()" + str);

		try {
			ArrayList<String> arrList = new ArrayList<String>();
			String allstr[] = str.split("~");
			arrList.add("Select Debit Account");

			int noOfAccounts = allstr.length;
			//System.out.println("SameBankTransfer noOfAccounts:" + noOfAccounts);
			acArray = new Accounts[noOfAccounts];
			int j=0;
			for (int i = 0; i < noOfAccounts; i++) {
				// System.out.println(i + "----STR1-----------" + str1[i]);
				// str2 = str1[i];
				//System.out.println(i + "----STR1-----------" + allstr[i]);
				str2 = allstr[i];
				String tempStr=str2;
				//System.out.println(i + "str2-----------" + str2);
				
				str2 = str2.replaceAll("#", "-");
				String accType = str2.split("-")[2];
				String oprcd=str2.split("-")[7];
				String AccCustID = str2.split("-")[11];

				String str2Temp = str2;
				str2 = MBSUtils.get16digitsAccNo(str2);
				String withdrawalAllowed=allstr[i].split("#")[10];
				Log.e("add accounts","accType"+accType);
				Log.e("add accounts","oprcd"+oprcd);
				if (((accType.equals("SB")) ||(accType.equals("LO"))
						||(accType.equals("CA")))&& oprcd.equalsIgnoreCase("O")&& withdrawalAllowed.equalsIgnoreCase("Y"))
				{
					acArray[j++] = new Accounts(tempStr);
					arrList.add(str2+" ("+MBSUtils.getAccTypeDesc(accType)+")");
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
			if(arrList.size()==0)
			{
				noAccounts=true;
				showAlert(getString(R.string.alert_089));
				
			}
			String[] debAccArr = new String[arrList.size()];
			debAccArr = arrList.toArray(debAccArr);
			/*CustomeSpinnerAdapter debAccs = new CustomeSpinnerAdapter(act,
					R.layout.spinner_layout, debAccArr);*/
			ArrayAdapter<String> debAccs = new ArrayAdapter<String>(act,R.layout.spinner_item, debAccArr);
			debAccs.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spi_debit_account.setAdapter(debAccs);

	//Me..		acnt_inf = spi_debit_account.getItemAtPosition(
	//				spi_debit_account.getSelectedItemPosition()).toString();
			//Log.i("SameBankTransfer MAYURI....", acnt_inf);
		} catch (Exception e) {
			System.out.println("" + e);
			e.printStackTrace();
			Log.e("Exception","Exception in add accounts"+e);
		}

	}// end addAccount

	public int chkConnectivity() {// chkConnectivity
		//System.out.println("============= inside chkConnectivity ================== ");
		// pb_wait.setVisibility(ProgressBar.VISIBLE);
		flag = 0;
		ConnectivityManager cm = (ConnectivityManager) act
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		//System.out.println("============= inside chkConnectivity 1 ================== ");
		NetworkInfo ni = cm.getActiveNetworkInfo();
		//System.out.println("============= inside chkConnectivity  2 ================== ");
		try {
			//System.out.println("============= inside chkConnectivity 3 ================== ");
			State state = ni.getState();
			boolean state1 = ni.isAvailable();
			//System.out.println("state1 ---------" + state1);
			if (state1) {
				switch (state) 
				{
					case CONNECTED:
						if (ni.getType() == ConnectivityManager.TYPE_MOBILE
							|| ni.getType() == ConnectivityManager.TYPE_WIFI) 
						{}
						break;
					case DISCONNECTED:
						flag = 1;
						retMess = getString(R.string.alert_014);
					//	showAlert(retMess);
						dbs = new DialogBox(act);
						dbs.get_adb().setMessage(retMess);
						dbs.get_adb().setPositiveButton("Ok",
								new DialogInterface.OnClickListener()  
								{
									public void onClick(DialogInterface arg0, int arg1) 
									{
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
		//System.out.println("=========== Exit from chkConnectivity ================");
		return flag;
	}

	// webservice to save data
	class CallWebService2 extends AsyncTask<Void, Void, Void> {
		String retval = "";
		LoadProgressBar loadProBarObj = new LoadProgressBar(act);
		String ValidationData="";
		JSONObject obj=new JSONObject();
		
		protected void onPreExecute() 
		{
			 respcode="";
			 reTval="";
			 
			 saveTransferTranrespdesc="";
			
			loadProBarObj.show();
			String charges=txt_charges.getText().toString().split(" ")[1];
			
			debitAccno=debitAccno.substring(0,16);
				
	
			try {
				String location=MBSUtils.getLocation(act);
				obj.put("BENFSRNO", "");
				obj.put("CRACCNO", accNo);
				obj.put("DRACCNO", debitAccno);
				obj.put("AMOUNT", amt);
				obj.put("REMARK", reMark);
				obj.put("TRANSFERTYPE", "QR");
				obj.put("IMEINO", MBSUtils.getImeiNumber(act));
				obj.put("CUSTID", custId);
				obj.put("CHARGES", charges);
				obj.put("CHRGACCNO", chrgCrAccNo);
				obj.put("TRANID", tranId);
				obj.put("SERVCHRG", "0");
				obj.put("CESS", "0");
				obj.put("TRANPIN", tranmpin);
				obj.put("SIMNO", MBSUtils.getSimNumber(act));
				obj.put("MOBILENO", MBSUtils.getMyPhoneNO(act));
				obj.put("IPADDRESS", MBSUtils.getLocalIpAddress());
				obj.put("OSVERSION", Build.VERSION.RELEASE);
				obj.put("LATITUDE", location.split("~")[0]);
				obj.put("LONGITUDE", location.split("~")[1]);
				obj.put("METHODCODE","16");
				Log.e("Shubham", "QRSENDService2_Request-->"+obj.toString() );

				// ValidationData=MBSUtils.getValidationData(act,obj.toString());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
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
				System.out.println("SameBankTransfer   Exception" + e);
			}
			return null;
		}// end dodoInBackground2

		protected void onPostExecute(Void paramVoid) 
		{
			
			loadProBarObj.dismiss();
			JSONObject jsonObj;
			try
			{
				String str=CryptoClass.Function6(var5,var2);
				 jsonObj = new JSONObject(str.trim());
				Log.e("Shubham", "QRSENDService2_Responce-->"+jsonObj.toString() );
				//Log.e("Shubham", "onPostExecute: "+ jsonObj);
			
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
			else
			{
				if (reTval.indexOf("SUCCESS") > -1) 
				{
					post_successsaveTransferTran(reTval);
				}
				else if(reTval.indexOf("DUPLICATE") > -1)
				{
				retMess = getString(R.string.alert_119)+tranId+"\n"+ getString(R.string.alert_120);
				showAlert(retMess);
				FragmentManager fragmentManager;
				Fragment fragment = new FundTransferMenuActivity(act);
				act.setTitle(getString(R.string.lbl_same_bnk_trans));
				fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction()
						.replace(R.id.frame_container, fragment).commit();
			}
			else if(reTval.indexOf("FAILED#") > -1)
			{
				retMess = getString(R.string.alert_032);
				showAlert(retMess);//setAlert();

	        }
                       else if (reTval.indexOf("WRONGTRANPIN") > -1) 
			{
				String msg[] = reTval.split("~");
				String first=msg[1];
				String second=msg[2];
			
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
			else if(reTval.indexOf("FAILED") > -1)
			{
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
					showAlert(retMess);
				}
				else if(errorCode.equalsIgnoreCase("001"))
				{
					    retMess = getString(R.string.alert_180);
						showAlert(retMess);
				}
				else if(errorCode.equalsIgnoreCase("002"))
				{
					    retMess = getString(R.string.alert_181);
						showAlert(retMess);
				}
				else if(errorCode.equalsIgnoreCase("003"))
				{
					    retMess = getString(R.string.alert_182);
						showAlert(retMess);
				}
				else if(errorCode.equalsIgnoreCase("004"))
				{
					retMess = getString(R.string.alert_179);
						showAlert(retMess);
				}
				else if(errorCode.equalsIgnoreCase("005"))
				{
					    retMess = getString(R.string.alert_183);
						showAlert(retMess);
				}
				else if(errorCode.equalsIgnoreCase("006"))
				{
					    retMess = getString(R.string.alert_184);
						showAlert(retMess);
				}
				else if(errorCode.equalsIgnoreCase("007"))
				{
					retMess = getString(R.string.alert_179);
						showAlert(retMess);
				}
				else if(errorCode.equalsIgnoreCase("008"))
				{
					    retMess = getString(R.string.alert_176);
						showAlert(retMess);
				}
				else
				{
				retMess = getString(R.string.trnsfr_alert_001);
				showAlert(retMess);//setAlert();
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
		retMess = getString(R.string.alert_030)+" "+getString(R.string.alert_121)+" "+tranId;
		showAlert(retMess);
		FragmentManager fragmentManager;
		Fragment fragment = new FundTransferMenuActivity(act);
		act.setTitle(getString(R.string.lbl_same_bnk_trans));
		fragmentManager = getFragmentManager();
		fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
	}
	
	@Override
	public void onClick(View v) 
	{ // logic to show input box
		
		/*if(v.getId() == R.id.btn_back)
		{
			if(same_bnk_layout.getVisibility()==View.VISIBLE)
			{
				Fragment fragment = new FundTransferMenuActivity(act);
				FragmentManager fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
				act.frgIndex=5;
			}
			else if(confirm_layout.getVisibility()==View.VISIBLE)
			{
				Log.e("QRSEND","act.scanOption===="+act.scanOption);
				if(act.scanOption==2)
				{	
					confirm_layout.setVisibility(confirm_layout.INVISIBLE);
					same_bnk_layout.setVisibility(same_bnk_layout.VISIBLE);
					txt_heading.setText("Send Money Via QR Code");
					act.frgIndex=51;
				}
				else
				{
					confirm_layout.setVisibility(confirm_layout.INVISIBLE);
					same_bnk_layout.setVisibility(same_bnk_layout.VISIBLE);
					txt_heading.setText("Send Money Via QR Code");
					act.frgIndex=51;
					addAccounts(stringValue);
					spi_debit_account.setSelection(act.selectedItem);
					txtAmt.setText(amt);
					txtRemk.setText(reMark);
				}
			}
		}
		else*/ if(v.getId() == R.id.btn_home1)
		{
			//Intent in=new Intent(getActivity(),DashboardDesignActivity.class);
			Intent in=new Intent(act,NewDashboard.class);
			in.putExtra("VAR1", var1);
			in.putExtra("VAR3", var3);
			startActivity(in);
		}
		else if (v.getId() == R.id.spinner_btn) 
		{
			spi_debit_account.performClick();
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
		else if (v.getId() == R.id.sameBnkTranbtn_submit) 
		{
			strFromAccNo = spi_debit_account.getSelectedItem().toString();
			strAmount = txtAmt.getText().toString().trim();
			strRemark = txtRemk.getText().toString().trim();
			String debitAcc = strFromAccNo.substring(0, 16);
			String balString=txtBalance.getText().toString().trim();
				if(balString.length()>0)
			{
				balString=balString.substring(0,balString.length()-2);
				balance=Double.parseDouble(balString);
				balance=Math.abs(balance);
				
			}
			
			if(debitAcc.indexOf("Select")> -1){
				showAlert(getString(R.string.alert_174));
			}                                        
			else if(strAmount.length()==0)
			{	
				showAlert(getString(R.string.alert_033));
			}	
			else if(strAmount.length()==1 && strAmount.equalsIgnoreCase("."))
			{
				showAlert(getString(R.string.alert_195));
			}
			
			else if(Double.parseDouble(strAmount)==0)
			{
				showAlert(getString(R.string.alert_034));
			}
			
			else if (Double.parseDouble(strAmount) > balance) {
				showAlert(getString(R.string.alert_176));
			}
			else if(strRemark.length()==0)
			{
				showAlert(getString(R.string.alert_035));
			}
			else
			{
				ScanInputDialogBox inputBox = new ScanInputDialogBox(act);
				inputBox.show();				
			}
		}
		else if (v.getId() == R.id.btn_confirm)
		{
			if (amt.length() == 0) 
			{
				amt = "0";
				retMess = getString(R.string.alert_033);
				showAlert(retMess);//setAlert();
				txtAmt.requestFocus();
			} 
			else 
			{
				//int amt = Integer.parseInt(strAmount);
				if (Double.parseDouble(amt)==0) 
				{
					retMess = getString(R.string.alert_034);
					showAlert(retMess);//setAlert();
					txtAmt.requestFocus();
				} 
				else 
				{
					if (reMark.length() > 200) 
					{
						retMess = getString(R.string.alert_097);
						showAlert(retMess);//setAlert();
						txtRemk.requestFocus();
					} 
					else if (accNo.length() == 0) 
					{
						retMess = getString(R.string.alert_067);
						showAlert(retMess);//setAlert();
					} 
					else 
					{
						InputDialogBox inputBox = new InputDialogBox(act);
						inputBox.show();
					} // end else
				}
			}// end if
		}
		else if (v.getId() == R.id.btn_confirm_back)
		{
			confirm_layout.setVisibility(confirm_layout.INVISIBLE);
			same_bnk_layout.setVisibility(same_bnk_layout.VISIBLE);
			txt_heading.setText(getString(R.string.lbl_qr_send));
		}
	}// end click
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

	public void onBackPressed() 
{
	//	 super.onBackPressed();
		if((confirm_layout.getVisibility()==View.VISIBLE)||(same_bnk_layout.getVisibility()==View.VISIBLE))
		{
		Fragment fragment = new FundTransferMenuActivity(act);
		//act.setTitle(getString(R.string.lbl_fund_transfer));
		FragmentManager fragmentManager = qrcodeSendAct.getFragmentManager();
		fragmentManager.beginTransaction()
				.replace(R.id.frame_container, fragment).commit();
		act.frgIndex=5;
	}
	}
	
	public void setAlert() 
	{
		System.out.println("======== in set alert ==========");
		showAlert(retMess);
	}// end setAlert

	public void saveData() {
		try {
//			this.flag = chkConnectivity();
//			if (this.flag == 0) {
//				new CallWebService2().execute();
//			}

			String charges=txt_charges.getText().toString().split(" ")[1];
			JSONObject obj=new JSONObject();
			String location=MBSUtils.getLocation(act);
			obj.put("BENFSRNO", "");
			obj.put("CRACCNO", accNo);
			obj.put("DRACCNO", debitAccno);
			obj.put("AMOUNT", amt);
			obj.put("REMARK", reMark);
			obj.put("TRANSFERTYPE", "QR");
			obj.put("IMEINO", MBSUtils.getImeiNumber(act));
			obj.put("CUSTID", custId);
			obj.put("CHARGES", charges);
			obj.put("CHRGACCNO", chrgCrAccNo);
			obj.put("TRANID", tranId);
			obj.put("SERVCHRG", "0");
			obj.put("CESS", "0");
			obj.put("TRANPIN", tranmpin);
			obj.put("SIMNO", MBSUtils.getSimNumber(act));

//			obj.put("MOBILENO", MBSUtils.getMyPhoneNO(act));
//			obj.put("IPADDRESS", MBSUtils.getLocalIpAddress());
//			obj.put("OSVERSION", Build.VERSION.RELEASE);
//			obj.put("LATITUDE", location.split("~")[0]);
//			obj.put("LONGITUDE", location.split("~")[1]);
//			obj.put("METHODCODE","16");

			Bundle bundle=new Bundle();

			bundle.putString("CUSTID", custId);
			bundle.putString("FROMACT", "QRSEND");
			bundle.putString("JSONOBJ", obj.toString());

			FragmentManager fragmentManager;
			Fragment fragment = new TransferOTP(act);
			fragment.setArguments(bundle);
			act.setTitle(getString(R.string.lbl_same_bnk_trans));
			fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();

			Log.e("Shubham", "QRSENDService2_Request-->"+obj.toString() );


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
		public void onClick(View v) 
		{
			try 
			{
				  String str=mpin.getText().toString().trim(); 
				  tranmpin=str;
				Log.e("Shubham", "tranmpin1: "+tranmpin );
				   encrptdTranMpin=ListEncryption.encryptData(custId+str);
				  if(str.length()==0) 
				  {
				  	retMess=getString(R.string.alert_116); 
				  	showAlert(retMess);//setAlert();
				  	this.show(); 
				  } 
//				  else if(str.length()!=6)
//				  {
//					retMess=getString(R.string.alert_037);
//					showAlert(retMess);//setAlert();
//				  	this.show();
//				  }
				  else 
				  {
				  	
				  		
				  		new callValidateTranpinService().execute();
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
	
	public class ScanInputDialogBox extends Dialog implements OnClickListener 
	{
		Activity activity;
		Button btnGalary,btnCamera;

		public ScanInputDialogBox(Activity activity) 
		{
			super(activity);
		}// end InputDialogBox

		protected void onCreate(Bundle bdn) 
		{
			super.onCreate(bdn);
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			setContentView(R.layout.scan_option);
			
			btnGalary = (Button) findViewById(R.id.btnGalary);
			btnCamera = (Button) findViewById(R.id.btnCamera);
			btnGalary.setVisibility(Button.VISIBLE);
			btnGalary.setOnClickListener(this);
			btnCamera.setVisibility(Button.VISIBLE);
			btnCamera.setOnClickListener(this);
		}

		@Override
		public void onClick(View v) 
		{
			switch (v.getId()) 
			{
				case R.id.btnCamera:
					act.QRAMT=strAmount;
					act.QRCUSTID=custId;
					act.QRDBTACCNO=strFromAccNo.substring(0,16);
					act.QRREMARK=strRemark;
					act.scanOption=1;
					new IntentIntegrator(act).initiateScan();
					break;
				case R.id.btnGalary:
					custId=custId;
					debitAccno=strFromAccNo;
					amt=strAmount;
					reMark=strRemark;
					act.scanOption=2;
					Intent photoPic = new Intent(Intent.ACTION_PICK);
			        photoPic.setType("image/*");
			        startActivityForResult(photoPic, act.SELECT_PHOTO);
					/*new IntentIntegrator(act).initiateScan();*/
					break;
				default:
					break;
			}
			this.hide();
		}// end onClick
	}// end InputDialogBox

	public void showAlert(final String str) 
	{
		ErrorDialogClass alert = new ErrorDialogClass(act, "" + str)
		{
			@Override
			public void onClick(View v) 
			{
				switch (v.getId()) 
				{
					case R.id.btn_ok:
						
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
						if(noAccounts)
						{
							if(same_bnk_layout.getVisibility()==View.VISIBLE)
							{
								Fragment fragment = new FundTransferMenuActivity(act);
								//act.setTitle(getString(R.string.lbl_fund_transfer));
								FragmentManager fragmentManager = qrcodeSendAct.getFragmentManager();
								fragmentManager.beginTransaction()
										.replace(R.id.frame_container, fragment).commit();
								act.frgIndex=5;
							}
							else if(confirm_layout.getVisibility()==View.VISIBLE)
							{
								confirm_layout.setVisibility(confirm_layout.INVISIBLE);
								same_bnk_layout.setVisibility(same_bnk_layout.VISIBLE);
								act.frgIndex=51;
							}
						}
					 } break;			
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
		
		txtAmt.setText("");
		txtRemk.setText("");
	}
	
	class CallWebServiceGetSrvcCharg extends AsyncTask<Void, Void, Void> 
	{
		String retval = "";
		LoadProgressBar loadProBarObj;//
		String benAcNo;
		String ValidationData="";
		JSONObject obj=new JSONObject();
		
		protected void onPreExecute() 
		{
			loadProBarObj = new LoadProgressBar(act);
			respcode="";
			reTval="";
			getTransferChargesrespdesc="";
			
			try {
				
				obj.put("CUSTID", custId);
				obj.put("TRANTYPE", "QR");
				obj.put("DRACCNO", debitAccno);
				obj.put("AMOUNT", amt);
				obj.put("CRACCNO", accNo);
				obj.put("IMEINO", MBSUtils.getImeiNumber(act));
				obj.put("SIMNO", MBSUtils.getSimNumber(act));
				obj.put("METHODCODE","28");
			//	 ValidationData=MBSUtils.getValidationData(act,obj.toString());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		
			loadProBarObj.show();
		}

		protected Void doInBackground(Void... arg0) 
		{
			try
			{
				Thread.sleep(2000);
				//txtAmt.setText(""+amt);
				//txtRemk.setText(reMark);
			}
			catch(Exception e)
			{e.printStackTrace();}
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
				System.out.println("SameBankTransfer   Exception" + e);
			}
			return null;
		}// end dodoInBackground2

		protected void onPostExecute(Void paramVoid) 
		{
	
		try
		{
	       
	       JSONObject jsonObj;
	       String str=CryptoClass.Function6(var5,var2);
	       jsonObj = new JSONObject(str.trim());
	       
	      /* ValidationData=xml_data[1].trim();
	       if(ValidationData.equals(MBSUtils.getValidationData(act, xml_data[0].trim())))
	       {*/
			String decryptedAccounts = str.trim();
			loadProBarObj.dismiss();
			 
	   			try
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
			if (reTval.indexOf("SUCCESS") > -1) 
			{
				post_successGetSrvcCharg(reTval);
				
			} 	
			else 
			{
				if (reTval.indexOf("LIMIT_EXCEEDS") > -1) 
				{
					retMess = getString(R.string.alert_031);
					//loadProBarObj.dismiss();
					showAlert(retMess);//setAlert();
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
				}
				else 
				{
					retMess = getString(R.string.alert_032);
					//loadProBarObj.dismiss();
					showAlert(retMess);//setAlert();
				}
			}// end else
	   			}
	      /* }else{
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
		act.frgIndex=55;
		//loadProBarObj.dismiss();
		same_bnk_layout.setVisibility(same_bnk_layout.INVISIBLE);
		confirm_layout.setVisibility(confirm_layout.VISIBLE);
		
		String retStr=reTval.split("~")[1];
        String tranType=reTval.split("~")[2];
		Log.e("PostExecute==","tranType==="+tranType);
		String[] val=retStr.split("#");
		txt_heading.setText("Confirmation");
		txt_remark.setText(reMark);
		txt_from.setText(debitAccno);
		txt_to.setText(accNo);
		txt_amount.setText("INR "+amt);
		txt_charges.setText("INR "+val[0]);
		txt_trantype.setText(tranType);	
		chrgCrAccNo=val[1];
		tranId=val[2];
		if(chrgCrAccNo.length()==0 || chrgCrAccNo.equalsIgnoreCase("null"))
			chrgCrAccNo="";
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		super.onActivityResult(requestCode, resultCode, data);
		Log.e("Shubham","Here onActivityResult QrcodeSendActivity");
		Log.e("DEMOHOME","11====="+requestCode);
		Log.e("DEMOHOME","22====="+resultCode);
		Log.e("DEMOHOME","33====="+data);
		if(requestCode==100)
		{
			if(data==null || data.equals(null))
        	{
				Toast.makeText(act, "Image Not Selected", Toast.LENGTH_SHORT).show();
        		Fragment fragment = new QrcodeSendActivity(act);
        		act.setTitle(getString(R.string.lbl_qr_send));
				FragmentManager fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
				act.frgIndex=55;
        	}
			else
			{	
				InputStream imageStream = null;
	            try 
	            {
	            		
	    			Uri selectedImage = data.getData();
	                //getting the image
	                imageStream = act.getContentResolver().openInputStream(selectedImage);
	            	
	            		
	            } 
	            catch (FileNotFoundException e) 
	            {
	                Toast.makeText(act, "File not found", Toast.LENGTH_SHORT).show();
	                e.printStackTrace();
	            }
	            //decoding bitmap
	            Bitmap bMap = BitmapFactory.decodeStream(imageStream);
	            //Scan.setImageURI(selectedImage);// To display selected image in image view
	            int[] intArray = new int[bMap.getWidth() * (bMap.getHeight()-25)];
	            // copy pixel data from the Bitmap into the 'intArray' array
	            bMap.getPixels(intArray, 0, bMap.getWidth(), 0, 0, bMap.getWidth(),(bMap.getHeight()-25));
	
	            Log.e("DEMOHOME","img ht==="+bMap.getHeight());
	            LuminanceSource source = new RGBLuminanceSource(bMap.getWidth(),(bMap.getHeight()-25), intArray);
	            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
	
	            Reader reader = new MultiFormatReader();// use this otherwise
	            Log.e("DEMOHOME","reader===="+reader);
	            // ChecksumException
	            try 
	            {
	                Hashtable<DecodeHintType, Object> decodeHints = new Hashtable<DecodeHintType, Object>();
	               
	                //Log.e("DEMOHOME","barcode=111="+barcode);
	                decodeHints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
	               // Log.e("DEMOHOME","barcode=222="+barcode);
	                decodeHints.put(DecodeHintType.PURE_BARCODE, Boolean.TRUE);
	                Result result = reader.decode(bitmap, decodeHints);
	                barcode =  result.getText().toString().trim();
	                Log.e("DEMOHOME","barcode=555="+barcode);
	               
	                if(barcode!=null)
	                {
	                	if(validateAccNo(barcode))
	    				{
	                		accNo=barcode.substring(0,barcode.length()-1);
	                		Toast.makeText(act, accNo+"=="+debitAccno, Toast.LENGTH_LONG).show();
	                		debitAccno=debitAccno.substring(0,16);
	                		if(accNo.equals(debitAccno) )
	                		{
	                			showAlert(getString(R.string.alert_107));
	                		}
	                		else
	                		{
	                		flg=1;
	                		new CallWebServiceGetSrvcCharg().execute();
	                		}
	    				}
	    				else
	    				{
	    					Toast.makeText(act, "Invalid Account Number", Toast.LENGTH_LONG).show();
	    					Fragment fragment = new FundTransferMenuActivity(act);
	    					FragmentManager fragmentManager = getFragmentManager();
	    					fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
	    				}
	            		
	            		
	                }
	                else
	                {
	                	Log.e("QRSEND","ERROR");
	                }
	             //the end of do something with the button statement.
	
	            } catch (NotFoundException e) 
	            {
	            	Log.e("DEMOHOME","invalid image");
	                Toast.makeText(act, "Nothing Found", Toast.LENGTH_SHORT).show();
	                e.printStackTrace();
	            } catch (ChecksumException e) {
	                Toast.makeText(act, "Something weird happen, i was probably tired to solve this issue", Toast.LENGTH_SHORT).show();
	                e.printStackTrace();
	            } /*catch (FormatException e) {
	                Toast.makeText(getApplicationContext(), "Wrong Barcode/QR format", Toast.LENGTH_SHORT).show();
	                e.printStackTrace();
	            }*/ catch (NullPointerException e) {
	                Toast.makeText(act, "Something weird happen, i was probably tired to solve this issue", Toast.LENGTH_SHORT).show();
	                e.printStackTrace();
	            } 
	            catch (com.google.zxing.NotFoundException e) 
	            {
	            	Log.e("DEMOHOME","invalid image 111111111");
	            	Toast.makeText(act, "Invalid Image", Toast.LENGTH_LONG).show();
					e.printStackTrace();
				} catch (com.google.zxing.FormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	public boolean validateAccNo(String str)
	{
		String regex = "[0-9]+";
		if(str.matches(regex)) 
		{
			int sum=0,grandSum=0;
			String str2=str.substring(0,str.length()-1);
			
			for(int i=0;i<str2.length();i++)
			{
				sum=sum+Integer.parseInt(""+str2.charAt(i));
			}
			while (sum > 9 ) 
			{
				grandSum=0;
	            while (sum > 0) 
	            {
	            	int rem;
	            	rem = sum % 10;
	            	grandSum = grandSum + rem;
	            	sum = sum / 10;
	            }
	            sum = grandSum;
			}
			if(grandSum==Integer.parseInt(str.substring(str.length()-1,str.length())))
				return true;
			else
				return false;
		}
		else
			return false;
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
			Log.e("Shubham", "tranmpin2: "+tranmpin );
			
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
				Log.e("Shubham", "QRSEND_callValidateTranpinService_Request-->"+obj.toString() );
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
			  JSONObject jsonObj;
	   			try
	   			{
	   				String str=CryptoClass.Function6(var5,var2);
	   			 jsonObj = new JSONObject(str.trim());
	   				/*ValidationData=xml_data[1].trim();
	   				if(ValidationData.equals(MBSUtils.getValidationData(act, xml_data[0].trim())))
	   				{*/
			  String decryptedAccounts =str.trim();
					Log.e("Shubham", "QRSEND_callValidateTranpinService_decryptedAccounts-->"+decryptedAccounts);

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
					Log.e("Shubham", "QRSEND_callValidateTranpinService_Responce-->"+obj);

					String msg[] = obj.getString("RETVAL").split("~");
					String first = msg[1];
					String second = msg[2];
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

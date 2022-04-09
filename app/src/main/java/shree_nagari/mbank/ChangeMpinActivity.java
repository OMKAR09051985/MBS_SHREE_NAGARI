package shree_nagari.mbank;

import java.security.PrivateKey;

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
import android.app.Fragment;
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
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
//import mbLib.DialogBox;

public class ChangeMpinActivity extends Fragment implements OnClickListener {
	MainActivity act;
	ChangeMpinActivity changMpin;
	LinearLayout layout_mpin,layout_tranmpin,layout_otp,layout_pass;
	TextView cust_nm, txt_heading,txt_ref_id;
	Button btnChangeMpin;
    RadioButton radiobtn_tranmpin,radiobtn_mpin,radiobtn_pass;
	RadioGroup groupradio;
	int index;
	ImageView btn_home1,btn_logout;
	String strmpin,respcode="",retval="",strRefId="",respdescchangempin="",
			respdescchangeTranMPIN="",retvalotp="",respdescresend="",respdescchangePASS="",retvalwbs="",respdesc="";
	EditText et_old_mpin, et_new_mpin, et_renew_mpin;
	EditText et_old_pass, et_new_pass, et_renew_pass;
	EditText et_old_tran_mpin, et_new_tran_mpin, et_renew_tran_mpin,etotptxt;
	ChangeMpinActivity changeAct = this;
	String imeiNo = "", tmpXMLString = "", retMess = "", pin = "",
			tranPin = "", userId = "",strMobNo="";
	TelephonyManager telephonyManager;
	ImageButton btn_home;// , btn_back;
	ImageView img_heading;
	int cnt = 0, flag = 0,radioflag=0;
	
	String retVal = "";DatabaseManagement dbms;
	DialogBox dbs;
	// ProgressBar pb_wait;
	private static final String MY_SESSION = "my_session";
	boolean isWSCalled = false;
	Editor e;
	String custId = "", cust_name = "", encrptMpin = "", respdescvalidateotp="",encrptOldPass="",encrptPass="",
			retvalvalidateotp="",encrptTranMpin = "",otpvalset="",
			encrptOldMpin = "", encrptOldTranMpin = "", encrptOldUserMpin = "",
			encrptUserMpin = "";
	ChangeMpinActivity obj;
	PrivateKey var1=null;	  
	String var5="",var3="";
	SecretKeySpec var2=null;

	public ChangeMpinActivity() {
	}

	@SuppressLint("ValidFragment")
	public ChangeMpinActivity(MainActivity a) {
		// System.out.println("v()"+a);
		act = a;
		changMpin = this;
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// System.out.println("onCreateView()  ChangeMpinActivity");
		var1 = act.var1;
		var3 = act.var3;
		dbms = new DatabaseManagement("shree_nagari.mbank", "listMobileBanking");
		View rootView = inflater
				.inflate(R.layout.change_mpin, container, false);
		img_heading = (ImageView) rootView.findViewById(R.id.img_heading);
		img_heading.setBackgroundResource(R.mipmap.changempin);
		et_old_mpin = (EditText) rootView.findViewById(R.id.etOldMpin);
		et_new_mpin = (EditText) rootView.findViewById(R.id.etNewMpin);
		et_renew_mpin = (EditText) rootView.findViewById(R.id.etRetypeNewMpin);

		et_old_pass = (EditText) rootView.findViewById(R.id.etOldPass);
		et_new_pass = (EditText) rootView.findViewById(R.id.etNewPass);
		et_renew_pass = (EditText) rootView.findViewById(R.id.etRetypeNewPass);
		et_old_tran_mpin = (EditText) rootView.findViewById(R.id.etOldTranMpin);
		et_new_tran_mpin = (EditText) rootView.findViewById(R.id.etNewTranMpin);
		et_renew_tran_mpin = (EditText) rootView.findViewById(R.id.etRetypeNewTranMpin);
		etotptxt= (EditText) rootView.findViewById(R.id.etotptxt);
         	layout_mpin=(LinearLayout)rootView.findViewById(R.id.layout_mpin);
		layout_pass=(LinearLayout)rootView.findViewById(R.id.layout_pass);
	        layout_tranmpin=(LinearLayout)rootView.findViewById(R.id.layout_tranmpin);
	        layout_otp=(LinearLayout)rootView.findViewById(R.id.layout_otp);
		txt_heading = (TextView) rootView.findViewById(R.id.txt_heading);
		txt_ref_id= (TextView) rootView.findViewById(R.id.txt_ref_id);
		txt_heading.setText(getString(R.string.lbl_title_change_mpin));
		btn_home = (ImageButton) rootView.findViewById(R.id.btn_home);
		groupradio=(RadioGroup)rootView.findViewById(R.id.groupradio);
			radiobtn_mpin=(RadioButton)rootView.findViewById(R.id.radiobtn_mpin);
			radiobtn_tranmpin=(RadioButton)rootView.findViewById(R.id.radiobtn_tranmpin);
			radiobtn_pass=(RadioButton)rootView.findViewById(R.id.radiobtn_pass);
			groupradio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(RadioGroup group, int checkedId) {
					 View radioButton =groupradio.findViewById(checkedId);
					 radioflag=1;
					 index = checkedId;
					 
					 Log.e("iiiiii", "1111111*****"+radioButton.getId());
					 Log.e("iiiiii", "1111111*****"+ R.id.radiobtn_mpin);
					 Log.e("iiiiii", "22222*****"+ R.id.radiobtn_tranmpin);
					 if(radioButton.getId()==R.id.radiobtn_mpin)
		                {
						// Log.e("index", "11111"+index);
						 radiobtn_tranmpin.setSelected(false);
						 radiobtn_tranmpin.setChecked(false);
						radiobtn_pass.setSelected(false);
						 radiobtn_pass.setChecked(false);
						 strmpin="MPIN";
						 layout_mpin.setVisibility(LinearLayout.VISIBLE);
						 layout_pass.setVisibility(LinearLayout.GONE);
						 layout_tranmpin.setVisibility(LinearLayout.GONE);
						  et_old_mpin.setText("");
						  et_new_mpin.setText("");
						  et_renew_mpin.setText("");
						  layout_otp.setVisibility(LinearLayout.GONE);
						  btnChangeMpin.setText(getString(R.string.lbl_genotp_btn));
							txt_ref_id.setText("");
						///  layout_otp.setVisibility(LinearLayout.VISIBLE);
							
		                }
					 else if(radioButton.getId()==R.id.radiobtn_tranmpin)
		                {
						 radiobtn_mpin.setSelected(false);
						 radiobtn_mpin.setChecked(false);
						 radiobtn_pass.setSelected(false);
						 radiobtn_pass.setChecked(false);
						strmpin="TRANMPIN";
						layout_tranmpin.setVisibility(LinearLayout.VISIBLE);
						layout_pass.setVisibility(LinearLayout.GONE);
						layout_mpin.setVisibility(LinearLayout.GONE);
						et_old_tran_mpin.setText("");
						et_new_tran_mpin.setText("");
						et_renew_tran_mpin.setText("");
						layout_otp.setVisibility(LinearLayout.GONE);
						btnChangeMpin.setText(getString(R.string.lbl_genotp_btn));
						// layout_otp.setVisibility(LinearLayout.VISIBLE);		
		                }
					 else if(radioButton.getId()==R.id.radiobtn_pass)
		                {
						 radiobtn_mpin.setSelected(false);
						 radiobtn_mpin.setChecked(false);
						 radiobtn_tranmpin.setSelected(false);
						 radiobtn_tranmpin.setChecked(false);
						strmpin="PASSWORD";
						layout_pass.setVisibility(LinearLayout.VISIBLE);
						layout_tranmpin.setVisibility(LinearLayout.GONE);
						layout_mpin.setVisibility(LinearLayout.GONE);
						et_old_pass.setText("");
						et_new_pass.setText("");
						et_renew_pass.setText("");
						layout_otp.setVisibility(LinearLayout.GONE);
						btnChangeMpin.setText(getString(R.string.lbl_genotp_btn));
						// layout_otp.setVisibility(LinearLayout.VISIBLE);		
		                }
					 else
					 {
						 strmpin="NOTSELECT";
					 }
					// TODO Auto-generated method stub
					
				}
			});
		// btn_home.setImageResource(R.drawable.ic_home_d);
		// btn_back.setImageResource(R.drawable.backover);
		// btn_back.setOnClickListener(this);
		btn_home.setOnClickListener(this);
		btnChangeMpin = (Button) rootView.findViewById(R.id.btnChangeMpin);
		btnChangeMpin.setOnClickListener(this);
		btn_home1 = (ImageView) rootView.findViewById(R.id.btn_home1);
		btn_logout = (ImageView) rootView.findViewById(R.id.btn_logout);
		btn_home1.setOnClickListener(this);
		btn_logout.setOnClickListener(this);
		// btnChangeMpin.setTypeface(tf_calibri);
    
		 dbs = new DialogBox(act);
		
		 Cursor c1=dbms.selectFromTable("SHAREDPREFERENCE", "", null);//("select * from ", null);
	        if(c1!=null)
	        {
	        	while(c1.moveToNext())
		        {	
	        		cust_name=c1.getString(0);
	        		Log.e("retvatstr","...."+cust_name);
	        		custId=c1.getString(2);
		        	Log.e("custId","......"+custId);
		        	userId=c1.getString(3);
			    	Log.e("UserId","c......"+userId);
			    	strMobNo=c1.getString(4);
			    	//Log.e("UserId","c......"+userId);
		        }
	        }
		return rootView;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		// pb_wait.setVisibility(ProgressBar.VISIBLE);
		// Log.i("mayuri success", "In Onclick");
		switch (v.getId()) {
		case R.id.btnChangeMpin:
					
			 if(btnChangeMpin.getText().equals(getString(R.string.lbl_genotp_btn)))
			 {
				if(radioflag==0)
				{				 
					retMess="Please select MPIN";
								         	showAlert(retMess);
					}
					else 
					{
				 String oldMpin = et_old_mpin.getText().toString().trim();
					String newMpin = et_new_mpin.getText().toString().trim();
					String reNewMpin = et_renew_mpin.getText().toString().trim();
					String oldPass = et_old_pass.getText().toString().trim();
					String newPass = et_new_pass.getText().toString().trim();
					String reNewPass = et_renew_pass.getText().toString().trim();

					String oldTranMpin = et_old_tran_mpin.getText().toString().trim();
					String newTranMpin = et_new_tran_mpin.getText().toString().trim();
					String reNewTranMpin = et_renew_tran_mpin.getText().toString()
							.trim();

					encrptOldMpin = ListEncryption.encryptData(custId + oldMpin);
					encrptOldPass =oldPass;
					encrptUserMpin = ListEncryption.encryptData(userId + oldMpin);
					encrptOldTranMpin = ListEncryption.encryptData(custId + oldTranMpin);
					encrptOldUserMpin = ListEncryption.encryptData(userId + oldMpin);

					encrptMpin = ListEncryption.encryptData(custId + newMpin);
					encrptPass =newPass;
					encrptTranMpin = ListEncryption.encryptData(custId + newTranMpin);
					encrptUserMpin = ListEncryption.encryptData(userId + newMpin);
				
				 flag = chkConnectivity();
					if (flag == 0)
					{
						

						if (strmpin.equalsIgnoreCase("MPIN"))//("") || newMpin.equals("")
			               {
							if (oldMpin.equals("") || newMpin.equals("") || reNewMpin.equals("")){//	|| reNewMpin.equals("") || oldTranMpin.equals("")
							//	|| newTranMpin.equals("") || reNewTranMpin.equals("")) {
							// Log.i("here", "111");
							cnt = 0;
							retMess = "Please fill all fields!";
							showAlert(retMess);
							// Log.i("here", "222");
						} /*else if (!(encrptOldMpin.equals(pin) || encrptOldUserMpin
								.equals(pin))) {
							cnt = 0;
							retMess = getString(R.string.alert_050);
							showAlert(retMess);
						}*/ else if (!newMpin.equals(reNewMpin)) {
							cnt = 0;
							retMess = getString(R.string.alert_049);
							showAlert(retMess);
						} /*else if (!(encrptOldMpin.equals(pin) || encrptOldUserMpin
								.equals(pin))) {
							cnt = 0;
							retMess = getString(R.string.alert_115);
							showAlert(retMess);
						}*/ else if (newMpin.length() != 6 || reNewMpin.length() != 6) {
							showAlert(getString(R.string.alert_086));
			                       }
			                        else
						{
			                        	 layout_otp.setVisibility(LinearLayout.VISIBLE);	
			         						
			                        	new CallGenerateOTPWebService().execute();
						
						}
							
							
			     }
						else if (strmpin.equalsIgnoreCase("PASSWORD"))//("") || newMpin.equals("")
		               {
						if (oldPass.equals("") || newPass.equals("") || reNewPass.equals("")){//	|| reNewMpin.equals("") || oldTranMpin.equals("")
						//	|| newTranMpin.equals("") || reNewTranMpin.equals("")) {
						// Log.i("here", "111");
						cnt = 0;
						retMess = "Please fill all fields!";
						showAlert(retMess);
						// Log.i("here", "222");
					} 
					 else if (!newPass.equals(reNewPass)) {
						cnt = 0;
						retMess = getString(R.string.alert_172_1);
						showAlert(retMess);
					} /*else if (!(encrptOldMpin.equals(pin) || encrptOldUserMpin
							.equals(pin))) {
						cnt = 0;
						retMess = getString(R.string.alert_115);
						showAlert(retMess);
					}*/ else if (newPass.length() != 6 || reNewPass.length() != 6) {
						showAlert(getString(R.string.alert_086_01));
		                       }
		                 else
					{
		                 layout_otp.setVisibility(LinearLayout.VISIBLE);
				new CallGenerateOTPWebService().execute();
			}
						
						
		     }
			      else if(strmpin.equalsIgnoreCase("TRANMPIN"))
			      {
			             if (oldTranMpin.equals("") || newTranMpin.equals("") || reNewTranMpin.equals(""))
				     {
				           cnt = 0;
					   retMess = "Please fill all fields!";
					   showAlert(retMess);
				      }
						 else if (newTranMpin.length() != 6 || reNewTranMpin.length() != 6) {
							showAlert(getString(R.string.alert_113));
			                        }
					/*	} else if (encrptMpin.equalsIgnoreCase(encrptTranMpin)) {
							showAlert(getString(R.string.alert_124));
						}*/ else if (!newTranMpin.equals(reNewTranMpin)) {
							cnt = 0;
							retMess = getString(R.string.alert_114);
							showAlert(retMess);
						} else {
						/*	flag = chkConnectivity();
							if (flag == 0) {*/
							 layout_otp.setVisibility(LinearLayout.VISIBLE);
							new CallGenerateOTPWebService().execute();
								// Log.i("mayuri success", retMess);
							}
						}
			                  	else{
								
									retMess="Please select MPIN";
									showAlert(retMess);
						}
			                    	
						
						
						
				}
					}
			 }
			 else{
				 
			String oldMpin = et_old_mpin.getText().toString().trim();
			String newMpin = et_new_mpin.getText().toString().trim();
			String reNewMpin = et_renew_mpin.getText().toString().trim();
			String oldPass = et_old_pass.getText().toString().trim();
			String newPass = et_new_pass.getText().toString().trim();
			String reNewPass = et_renew_pass.getText().toString().trim();
            String txtotp=etotptxt.getText().toString().trim();
			String oldTranMpin = et_old_tran_mpin.getText().toString().trim();
			String newTranMpin = et_new_tran_mpin.getText().toString().trim();
			String reNewTranMpin = et_renew_tran_mpin.getText().toString()
					.trim();

			encrptOldMpin = ListEncryption.encryptData(custId + oldMpin);
			encrptOldPass = oldPass;
			encrptUserMpin = ListEncryption.encryptData(userId + oldMpin);
			encrptOldTranMpin = ListEncryption.encryptData(custId + oldTranMpin);
			encrptOldUserMpin = ListEncryption.encryptData(userId + oldMpin);

			encrptMpin = ListEncryption.encryptData(custId + newMpin);
			encrptPass = newPass;
			encrptTranMpin = ListEncryption.encryptData(custId + newTranMpin);
			encrptUserMpin = ListEncryption.encryptData(userId + newMpin);
			if(radioflag==0)
			{
		        	retMess="Please select MPIN";
		         	showAlert(retMess);
			}
			else 
			{
			flag = chkConnectivity();
			if (flag == 0) 
			{
			if (strmpin.equalsIgnoreCase("MPIN"))//("") || newMpin.equals("")
               {
				if (oldMpin.equals("") || newMpin.equals("") || reNewMpin.equals("")){//	|| reNewMpin.equals("") || oldTranMpin.equals("")
				//	|| newTranMpin.equals("") || reNewTranMpin.equals("")) {
				// Log.i("here", "111");
				cnt = 0;
				retMess = "Please fill all fields!";
				showAlert(retMess);
				// Log.i("here", "222");
			} /*else if (!(encrptOldMpin.equals(pin) || encrptOldUserMpin
					.equals(pin))) {
				cnt = 0;
				retMess = getString(R.string.alert_050);
				showAlert(retMess);
			}*/ else if (!newMpin.equals(reNewMpin)) {
				cnt = 0;
				retMess = getString(R.string.alert_049);
				showAlert(retMess);
			} /*else if (!(encrptOldMpin.equals(pin) || encrptOldUserMpin
					.equals(pin))) {
				cnt = 0;
				retMess = getString(R.string.alert_115);
				showAlert(retMess);
			}*/ else if (newMpin.length() != 6 || reNewMpin.length() != 6) {
				showAlert(getString(R.string.alert_086));
                       }
			/*else if(txtotp.length()!=6)
			{
				showAlert(getString(R.string.alert_075));
			}*/
				else if(txtotp.length() == 0)
				{
					showAlert(getString(R.string.alert_076_01));
				}
                        else
			{
                        	new CallWebServiceValidateOTP().execute();
         						
			//new CallWebService().execute();
		}
				
				
     }
	else if (strmpin.equalsIgnoreCase("PASSWORD"))
            {
				if (oldPass.equals("") || newPass.equals("") || reNewPass.equals("")){
				
				cnt = 0;
				retMess = "Please fill all fields!";
				showAlert(retMess);
				
			} 
			 else if (!newPass.equals(reNewPass)) {
				cnt = 0;
				retMess = getString(R.string.alert_172_1);
				showAlert(retMess);
			} else if (newPass.length() != 6 || reNewPass.length() != 6) {
				showAlert(getString(R.string.alert_086_01));
                    }
			/*else if(txtotp.length()!=6)
			{
				showAlert(getString(R.string.alert_075));
			}*/
              else
			{
            new CallWebServiceValidateOTP().execute();	
			
			}
				
				
     }
      else if(strmpin.equalsIgnoreCase("TRANMPIN"))
      {
             if (oldTranMpin.equals("") || newTranMpin.equals("") || reNewTranMpin.equals(""))
	     {
	           cnt = 0;
		   retMess = "Please fill all fields!";
		   showAlert(retMess);
	      }
			 else if (newTranMpin.length() != 6 || reNewTranMpin.length() != 6) {
				showAlert(getString(R.string.alert_113));
                        }
		/*	} else if (encrptMpin.equalsIgnoreCase(encrptTranMpin)) {
				showAlert(getString(R.string.alert_124));
			}*/ else if (!newTranMpin.equals(reNewTranMpin)) {
				cnt = 0;
				retMess = getString(R.string.alert_114);
				showAlert(retMess);
			} 
			/*else if(txtotp.length()!=6)
			{
				showAlert(getString(R.string.alert_075));
			}*/
			 else if(txtotp.length() == 0)
			 {
				 showAlert(getString(R.string.alert_076_01));
			 }
			else {
			
					//new CallWebServicetran().execute();
				new CallWebServiceValidateOTP().execute();	
				}
			}
                  	else{
					
						retMess="Please select MPIN";
						showAlert(retMess);
			}
                    }
                
}
		}

			break;
		/*
		 * case R.id.btn_back: Fragment OthrSrvcFragment = new
		 * OtherServicesMenuActivity(act);
		 * act.setTitle(getString(R.string.lbl_title_change_mpin));
		 * FragmentManager fragmentManager = getFragmentManager();
		 * fragmentManager.beginTransaction() .replace(R.id.frame_container,
		 * OthrSrvcFragment).commit(); act.frgIndex=8; break;
		 */
		case R.id.btn_home:
			Intent in = new Intent(act, NewDashboard.class);
			in.putExtra("VAR1", var1);
			in.putExtra("VAR3", var3);
			startActivity(in);
			act.finish();
			break;
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
		default:
			break;
		}
		
	}

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

	public void showAlert(final String str) {	
		ErrorDialogClass alert = new ErrorDialogClass(act, "" + str) {
			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.btn_ok:
					if((str.equalsIgnoreCase(respdescchangempin)) &&  (respcode.equalsIgnoreCase("0")))
					{
						post_successchangempin(retval);
					}
					else if((str.equalsIgnoreCase(respdescchangempin)) && (respcode.equalsIgnoreCase("1")))
					{
						this.dismiss();
					}
					else if((str.equalsIgnoreCase(respdescchangeTranMPIN)) &&  (respcode.equalsIgnoreCase("0")))
					{
						post_successcchangeTranMPIN(retval);
					}
					else if((str.equalsIgnoreCase(respdescchangeTranMPIN)) && (respcode.equalsIgnoreCase("1")))
					{
						this.dismiss();
					}
					else if((str.equalsIgnoreCase(respdescresend)) &&  (respcode.equalsIgnoreCase("0")))
					{
						post_successresend(retvalotp);
					}
					else if((str.equalsIgnoreCase(respdescresend)) && (respcode.equalsIgnoreCase("1")))
					{
						this.dismiss();
					}
					else if ((str.equalsIgnoreCase(respdescvalidateotp))
							&& (respcode.equalsIgnoreCase("0"))) {
						post_successvalidate(retvalvalidateotp);
					} else if ((str.equalsIgnoreCase(respdescvalidateotp))
							&& (respcode.equalsIgnoreCase("1"))) {
						this.dismiss();
					}
					else if (isWSCalled) {
						/*Fragment OthrSrvcFragment = new OtherServicesMenuActivity(
								act);
						act.setTitle(getString(R.string.lbl_title_change_mpin));
						FragmentManager fragmentManager = getFragmentManager();
						fragmentManager
								.beginTransaction()
								.replace(R.id.frame_container, OthrSrvcFragment)
								.commit();*/
						Intent in = new Intent(act, NewDashboard.class);
						in.putExtra("VAR1", var1);
						in.putExtra("VAR3", var3);
						startActivity(in);
						act.finish();
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

			Log.i("mayuri", "NullPointerException Exception" + ne);
			flag = 1;
			// retMess = "Can Not Get Connection. Please Try Again.";
			retMess = getString(R.string.alert_000);
			showAlert(retMess);

		} catch (Exception e) {
			Log.i("mayuri", "Exception" + e);
			flag = 1;
			// retMess = "Connection Problem Occured.";
			retMess = getString(R.string.alert_000);
			showAlert(retMess);
		}
		return flag;
	}
	 class CallGenerateOTPWebService extends AsyncTask<Void, Void, Void> 
		{
			LoadProgressBar loadProBarObj = new LoadProgressBar(act);
			String generatedXML = "",ValidationData="";
			boolean isWSCalled = false;
		    JSONObject jsonObj = new JSONObject();

			@Override
			protected void onPreExecute() 
			{ 
				loadProBarObj.show();
				try
				{
					jsonObj.put("CUSTID", custId);
					jsonObj.put("REQSTATUS","R");
					jsonObj.put("REQFROM", "MBSCH");
					jsonObj.put("MOBNO", strMobNo);
					jsonObj.put("IMEINO", MBSUtils.getImeiNumber(act));
					jsonObj.put("SIMNO", MBSUtils.getSimNumber(act));
					jsonObj.put("METHODCODE","26");
					//ValidationData=MBSUtils.getValidationData(act,jsonObj.toString());

				
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
		
			};

			@Override
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
					} 
					catch (Exception e) 
					{
						e.printStackTrace();
						Log.e("In Login", "----------" + e);
							retMess = getString(R.string.alert_000);
						System.out.println("Exception");
						cnt = 0;
						// return "FAILED";
					}
				} 
				catch (Exception e) 
				{
					retMess = getString(R.string.alert_000);
					System.out.println(e.getMessage());
					cnt = 0;
					Log.e("EXCEPTION", "------------------"+e);
					// return "FAILED";
				}
				return null;

			}

			@Override
			protected void onPostExecute(final Void result) 
			{
				loadProBarObj.dismiss();
				if (isWSCalled) 
				{
						
					JSONObject jsonObj;
		            try
		            {
		            	String str=CryptoClass.Function6(var5,var2);
		            	 jsonObj = new JSONObject(str.trim());
		            	/* ValidationData=xml_data[1].trim();
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
								retvalotp = jsonObj.getString("RETVAL");
							}
							else
							{
								retvalotp = "";
							}
							if (jsonObj.has("RESPDESC"))
							{
								respdescresend = jsonObj.getString("RESPDESC");
							}
							else
							{	
								respdescresend = "";
							}
		            	
		            
					if(respdescresend.length()>0)
					{
						showAlert(respdescresend);
					}
					else{
					
					if(retvalotp.split("~")[0].indexOf("SUCCESS")>-1)
					{
		            	post_successresend(retvalotp);
					} 
					else 
					{
						retMess = act.getString(R.string.alert_094);
						showAlert(retMess);
					}}
					
		            	/*}	
		            	else
		            	{
		            		MBSUtils.showInvalidResponseAlert(act);
		            	}*/
					} 
		            catch (JSONException e) 
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				} else {
					retMess = act.getString(R.string.alert_000);
					showAlert(retMess);
				}
			}
		}// CallWebService_resend_otp
	 
	  public void post_successresend(String retvalstr)
	    {
		
		String returnstr = retvalstr.split("~")[1];
		String val[] = returnstr.split("!!");
		strRefId=val[2];
		layout_otp.setVisibility(LinearLayout.VISIBLE);
	//	txt_ref_id.setText(txt_ref_id.getText().toString() + " :" + strRefId);
	txt_ref_id.setText(act.getString(R.string.lbl_ref_id) + " :" + strRefId);
		btnChangeMpin.setText(getString(R.string.lbl_change_btn));
		
	    } 
	class CallWebService extends AsyncTask<Void, Void, Void> {
		String retVal = "";
		LoadProgressBar loadProBarObj = new LoadProgressBar(act);
		String oldMpin = "", newMpin = "", reNewMpin = "", newTranMpin = "",otpval="";
		JSONObject jsonObj = new JSONObject();
		String ValidationData="";

		@Override
		protected void onPreExecute() {
			// pb_wait.setVisibility(ProgressBar.VISIBLE);
			loadProBarObj.show();
			respcode="";
			retval="";
			respdescchangempin="";
			oldMpin = et_old_mpin.getText().toString().trim();
			newMpin = et_new_mpin.getText().toString().trim();
			otpval=etotptxt.getText().toString().trim();
			strRefId=txt_ref_id.getText().toString().trim();
			strRefId=strRefId.substring(strRefId.indexOf(":")+1).trim();
			encrptUserMpin = ListEncryption.encryptData(userId + newMpin);
			encrptMpin = ListEncryption.encryptData(custId + newMpin);
			try {
				String location=MBSUtils.getLocation(act);
				jsonObj.put("CUSTID", custId);
				jsonObj.put("MPIN", newMpin);
				jsonObj.put("IMEINO", MBSUtils.getImeiNumber(act));
				jsonObj.put("USRMPIN", newMpin);
				jsonObj.put("USRID", userId);
				jsonObj.put("OLDMPIN",oldMpin);
				  jsonObj.put("SIMNO", MBSUtils.getSimNumber(act));
				  jsonObj.put("MOBILENO", MBSUtils.getMyPhoneNO(act));
	              jsonObj.put("IPADDRESS", MBSUtils.getLocalIpAddress());
	              jsonObj.put("OSVERSION", Build.VERSION.RELEASE);
	             
	              jsonObj.put("LATITUDE", location.split("~")[0]);
	              jsonObj.put("LONGITUDE", location.split("~")[1]);
	              jsonObj.put("REFNO", strRefId);
	              jsonObj.put("OTPVAL",otpval);
	              jsonObj.put("METHODCODE","4");
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
				String status = "";
				try {
					androidHttpTransport.call(value5, envelope);
					status = envelope.bodyIn.toString().trim();
					var5 = status;
					int pos = envelope.bodyIn.toString().trim().indexOf("=");
					if (pos > -1) {
						status = status.substring(pos + 1, status.length() - 3);
						var5 = status;
						//isWSCalled = true;
						}
				} catch (Exception e) {
					e.printStackTrace();
					retMess = getString(R.string.alert_000);
					System.out.println("Exception");
				}
			} catch (Exception e) {
				retMess = getString(R.string.alert_000);
				System.out.println(e.getMessage());
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
					retval = jsonObj.getString("RETVAL");
				}
				else
				{
					retval = "";
				}
				if (jsonObj.has("RESPDESC"))
				{
					respdescchangempin = jsonObj.getString("RESPDESC");
				}
				else
				{	
					respdescchangempin = "";
				}
				
			if(respdescchangempin.length()>0)
			{
				showAlert(respdescchangempin);
			}
			else{
			
			if (retval.indexOf("SUCCESS") > -1) {
				post_successchangempin(retval);
			
			} else if (retval.indexOf("FAILED~") > -1) {
				String retCode = retval.split("~")[1];
				if (retCode.equalsIgnoreCase("1"))
					showAlert(getString(R.string.alert_122));
				/*else if (retCode.equalsIgnoreCase("2"))
					showAlert(getString(R.string.alert_123));*/
				else if (retCode.equalsIgnoreCase("3"))
					showAlert(getString(R.string.alert_050));
				else if (retCode.equalsIgnoreCase("4"))
					showAlert(getString(R.string.alrt_mpin2));
				else if (retCode.equalsIgnoreCase("5"))
					showAlert(getString(R.string.alert_076));
				else
					showAlert(getString(R.string.alert_085));
			} else {
				retMess = getString(R.string.alert_085);
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
		}
	}
	
	public 	void post_successchangempin(String retval){
		respcode="";
		respdescchangempin="";
		retMess = getString(R.string.alert_070);
		isWSCalled = true;
		showAlert(retMess);
	}
	public 	void post_successcchangeTranMPIN(String retval){
		
		respcode="";
		respdescchangeTranMPIN="";
		retMess = getString(R.string.alert_tran);
		isWSCalled = true;
		showAlert(retMess);
	}
	
public 	void post_successcchangePass(String retval){
		
		respcode="";
		respdescchangePASS="";
		retMess = getString(R.string.alert_pass);
		isWSCalled = true;
		showAlert(retMess);
	}
	class CallWebServicetran extends AsyncTask<Void, Void, Void> {
		String retVal = "";
		LoadProgressBar loadProBarObj=new LoadProgressBar(act);
		String oldMpin = "", newMpin = "", oldTranMpin ="",newTranMpin="",otpval="";
		JSONObject jsonObj = new JSONObject();
		String ValidationData="";
		@Override
	    protected void onPreExecute()
	    {
			loadProBarObj.show();
			respcode="";
			retval="";
			respdescchangeTranMPIN="";
			oldTranMpin = et_old_tran_mpin.getText().toString().trim();
			newTranMpin = et_new_tran_mpin.getText().toString().trim();
			encrptUserMpin=ListEncryption.encryptData(userId+newMpin);
			otpval=etotptxt.getText().toString().trim();
			strRefId=txt_ref_id.getText().toString().trim();
			strRefId=strRefId.substring(strRefId.indexOf(":")+1).trim();
			encrptTranMpin=ListEncryption.encryptData(custId+newTranMpin);

			try{
				String location=MBSUtils.getLocation(act);
					  jsonObj.put("CUSTID", custId);
					  jsonObj.put("TRANMPIN", newTranMpin);
		              jsonObj.put("IMEINO", MBSUtils.getImeiNumber(act));
		              jsonObj.put("USRTRANMPIN", newMpin);
		              jsonObj.put("USRID",userId);
		              jsonObj.put("OLDTRANMPIN", oldTranMpin);
		              jsonObj.put("SIMNO", MBSUtils.getSimNumber(act));
		              jsonObj.put("MOBILENO", MBSUtils.getMyPhoneNO(act));
		              jsonObj.put("IPADDRESS", MBSUtils.getLocalIpAddress());
		              jsonObj.put("OSVERSION", Build.VERSION.RELEASE);
		              jsonObj.put("LATITUDE", location.split("~")[0]);
		              jsonObj.put("LONGITUDE", location.split("~")[1]);
		              jsonObj.put("REFNO", strRefId);
		              jsonObj.put("OTPVAL",otpval);
		              jsonObj.put("METHODCODE","5");
		            //  ValidationData=MBSUtils.getValidationData(act,jsonObj.toString());
				}
				   catch (JSONException je) {
		                je.printStackTrace();
		            }
		
	    };
	    
		@Override
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
						//isWSCalled = true;
						}
				}  catch (Exception e) {
					e.printStackTrace();
					retMess = getString(R.string.alert_000);
					System.out.println("Exception");
				}
			} catch (Exception e) {
				retMess = getString(R.string.alert_000);
				System.out.println(e.getMessage());
			}
			return null;
		}
		protected void onPostExecute(final Void result)
		{
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
					retval = jsonObj.getString("RETVAL");
				}
				else
				{
					retval = "";
				}
				if (jsonObj.has("RESPDESC"))
				{
					respdescchangeTranMPIN = jsonObj.getString("RESPDESC");
				}
				else
				{	
					respdescchangeTranMPIN = "";
				}
				
			if(respdescchangeTranMPIN.length()>0)
			{
				showAlert(respdescchangeTranMPIN);
			}
			else{
			
			if(retval.indexOf("SUCCESS")>-1)
			{
				post_successcchangeTranMPIN(retval);
			}
			else if (retval.indexOf("FAILED~") > -1)
			{
				String retCode=retval.split("~")[1];
			    if(retCode.equalsIgnoreCase("1"))
					showAlert(getString(R.string.alert_123));
				else if(retCode.equalsIgnoreCase("2"))
					showAlert(getString(R.string.alert_123));
				else if(retCode.equalsIgnoreCase("3"))
					showAlert(getString(R.string.alert_171));
				else if(retCode.equalsIgnoreCase("5"))
					showAlert(getString(R.string.alrt_tranpin));
				else if (retCode.equalsIgnoreCase("6"))
					showAlert(getString(R.string.alert_076));
				else
					showAlert(getString(R.string.alert_085));
			}
			else
			{
				retMess = getString(R.string.alert_085);
				showAlert(retMess);
			}					
		}
			/*
				}
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
	
	class CallWebServicepass extends AsyncTask<Void, Void, Void> {
		String retVal = "";
		LoadProgressBar loadProBarObj=new LoadProgressBar(act);
		String oldMpin = "", newMpin = "", oldPass="",newPass="",otpval="";
		 //changed 
		
		JSONObject jsonObj = new JSONObject();
	
		@Override
	    protected void onPreExecute()
	    {
			loadProBarObj.show();
			respcode="";
			retval="";
			respdescchangeTranMPIN="";
			oldPass = et_old_pass.getText().toString().trim();
			newPass = et_new_pass.getText().toString().trim();
			encrptUserMpin=newMpin;//ListEncryption.encryptData(userId+newMpin);
			otpval=etotptxt.getText().toString().trim();
			strRefId=txt_ref_id.getText().toString().trim();
			strRefId=strRefId.substring(strRefId.indexOf(":")+1).trim();
			encrptPass=newPass;//ListEncryption.encryptData(custId+newPass);

			try{
				String location=MBSUtils.getLocation(act);
					  jsonObj.put("CUSTID", custId);
					  jsonObj.put("PASSWORD", newPass);
		              jsonObj.put("IMEINO", MBSUtils.getImeiNumber(act));
		             // jsonObj.put("USRPASSWORD", encrptUserMpin);
		             // jsonObj.put("USRID",userId);
		              jsonObj.put("OLDPASSWORD", oldPass);//ListEncryption.encryptData(custId+oldPass));
		              jsonObj.put("SIMNO", MBSUtils.getSimNumber(act));
		              jsonObj.put("MOBILENO", MBSUtils.getMyPhoneNO(act));
		              jsonObj.put("IPADDRESS", MBSUtils.getLocalIpAddress());
		              jsonObj.put("OSVERSION", Build.VERSION.RELEASE);
		              jsonObj.put("LATITUDE", location.split("~")[0]);
		              jsonObj.put("LONGITUDE", location.split("~")[1]);
		              jsonObj.put("REFNO", strRefId);
		              jsonObj.put("OTPVAL",otpval);//ListEncryption.encryptData(otpval+custId));
		              jsonObj.put("METHODCODE","77"); 
		              Log.e("json pass", "=-----------"+jsonObj.toString());
				}
				   catch (JSONException je) {
		                je.printStackTrace();
		            }
			
	    };
	    
		@Override
		protected Void doInBackground(Void... arg0) 
		{
			 String value4 = getString(R.string.namespace);
				String value5 = getString(R.string.soap_action);
				String value6 = getString(R.string.url);
				final String value7 = "callWebservice";

				try 
				{
					String keyStr=CryptoClass.Function2();
					var2=CryptoClass.getKey(keyStr);
					SoapObject request = new SoapObject(value4, value7);
					request.addProperty("value1", CryptoClass.Function5(jsonObj.toString(), var2));
					request.addProperty("value2", CryptoClass.Function3(keyStr, var1));
					request.addProperty("value3", var3);
					SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
					envelope.setOutputSoapObject(request);
					HttpTransportSE androidHttpTransport = new HttpTransportSE(value6,45000);

					androidHttpTransport.call(value5, envelope);
					var5 = envelope.bodyIn.toString().trim();
					var5 = var5.substring(var5.indexOf("=") + 1,var5.length() - 3);
				}// end try
				catch (Exception e) 
				{
					e.printStackTrace();
				}
				return null;
		}
		protected void onPostExecute(final Void result)
		{
			 
			
			
			loadProBarObj.dismiss();
			JSONObject jsonObj;
			try
			{
				String str=CryptoClass.Function6(var5,var2);
				jsonObj = new JSONObject(str.trim());

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
					retval = jsonObj.getString("RETVAL");
				}
				else
				{
					retval = "";
				}
				if (jsonObj.has("RESPDESC"))
				{
					respdescchangePASS = jsonObj.getString("RESPDESC");
				}
				else
				{	
					respdescchangePASS = "";
				}
				
			if(respdescchangePASS.length()>0)
			{
				showAlert(respdescchangePASS);
			}
			else{
			
			if(retval.indexOf("SUCCESS")>-1)
			{
				post_successcchangePass(retval);
			}
			else if (retval.indexOf("FAILED~") > -1)
			{
				String retCode=retval.split("~")[1];
			    if(retCode.equalsIgnoreCase("1"))
					showAlert(getString(R.string.alert_123_01));
				else if(retCode.equalsIgnoreCase("2"))
					showAlert(getString(R.string.alert_123_01));
				else if(retCode.equalsIgnoreCase("3"))
					showAlert(getString(R.string.alert_171_3));
				/*else if(retCode.equalsIgnoreCase("5"))
					showAlert(getString(R.string.alrt_tranpin));*/
				else if (retCode.equalsIgnoreCase("6"))
					showAlert(getString(R.string.alert_076));
				else
					showAlert(getString(R.string.alert_085_1));
			}
			else
			{
				retMess = getString(R.string.alert_085_1);
				showAlert(retMess);
			}					
		}
			
				
			} catch (JSONException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
	}
		
	
	}
	
class CallWebServiceValidateOTP extends AsyncTask<Void, Void, Void> {
		
		JSONObject jsonObj = new JSONObject();
		LoadProgressBar loadProBarObj = new LoadProgressBar(
				act);
		
		boolean isWSCalled = false;

		@Override
		protected void onPreExecute() {
			loadProBarObj.show();
			otpvalset=etotptxt.getText().toString().trim();
			strRefId=txt_ref_id.getText().toString().trim();
			strRefId=strRefId.substring(strRefId.indexOf(":")+1).trim();
			try {
				jsonObj.put("CUSTID", custId);
				jsonObj.put("OTPVAL",otpvalset);
						//ListEncryption.encryptData(strOTP + custId));
				jsonObj.put("IMEINO",
						MBSUtils.getImeiNumber(act));
				jsonObj.put("REFID", strRefId);
				jsonObj.put("ISREGISTRATION", "N");
				jsonObj.put("SIMNO",
						MBSUtils.getSimNumber(act));
				
				jsonObj.put("METHODCODE","20");  
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		};

		@Override
		protected Void doInBackground(Void... arg0) {
			   String value4 = getString(R.string.namespace);
				String value5 = getString(R.string.soap_action);
				String value6 = getString(R.string.url);
				//final String value7 = "callWebservice";
			final String value7 = getString(R.string.OTP_Validate_FUNCTION);

				try 
				{
					String keyStr=CryptoClass.Function2();
					var2=CryptoClass.getKey(keyStr);
					SoapObject request = new SoapObject(value4, value7);
					request.addProperty("value1", CryptoClass.Function5(jsonObj.toString(), var2));
					request.addProperty("value2", CryptoClass.Function3(keyStr, var1));
					request.addProperty("value3", var3);
					SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
					envelope.setOutputSoapObject(request);
					HttpTransportSE androidHttpTransport = new HttpTransportSE(value6,45000);

					androidHttpTransport.call(value5, envelope);
					var5 = envelope.bodyIn.toString().trim();
					var5 = var5.substring(var5.indexOf("=") + 1,var5.length() - 3);
					isWSCalled=true;
				}// end try
				catch (Exception e) 
				{
					e.printStackTrace();
				}
				return null;
		}

		@Override
		protected void onPostExecute(final Void result) {
			loadProBarObj.dismiss();
			if (isWSCalled) {
			
		
				JSONObject jsonObj;
				try {
					String str=CryptoClass.Function6(var5,var2);
					jsonObj = new JSONObject(str.trim());
					
						if (jsonObj.has("RESPCODE")) {
							respcode = jsonObj.getString("RESPCODE");
						} else {
							respcode = "-1";
						}
						if (jsonObj.has("RETVAL")) {
							retvalvalidateotp = jsonObj.getString("RETVAL");
						} else {
							retvalvalidateotp = "";
						}
						if (jsonObj.has("RESPDESC")) {
							respdescvalidateotp = jsonObj.getString("RESPDESC");
						} else {
							respdescvalidateotp = "";
						}
					

				} catch (JSONException e) {
					e.printStackTrace();
				}
				if (respdescvalidateotp.length() > 0) {
					showAlert(respdescvalidateotp);
				} else {
					if (retvalvalidateotp.indexOf("SUCCESS") > -1) {
						post_successvalidate(retvalvalidateotp);
					}
					else if(retvalvalidateotp.indexOf("FAILED~MAXATTEMPT")>-1)
					{
						retMess = act.getString(R.string.alert_076_02);
						showAlert(retMess);
					}else {
						showAlert(getString(R.string.alert_076));
					}
				}
			} else {
				showAlert(getString(R.string.alert_000));
			}
		}

	}	
public void post_successvalidate(String retval) {

	respdescvalidateotp = "";
	respcode = "";
	

	flag = chkConnectivity();
	if (flag == 0) {
		if(strmpin.equalsIgnoreCase("MPIN"))
		{
			new CallWebService().execute();
		}
		else if(strmpin.equalsIgnoreCase("TRANMPIN"))
		{
			Log.e("DSP","strchgtran......");
			new CallWebServicetran().execute();
		}
		else if(strmpin.equalsIgnoreCase("PASSWORD"))
			{
				new CallWebServicepass().execute();
			}
		
	}
}
}


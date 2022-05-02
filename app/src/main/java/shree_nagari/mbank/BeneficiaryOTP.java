package shree_nagari.mbank;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.security.PrivateKey;

import javax.crypto.spec.SecretKeySpec;

import mbLib.CryptoClass;
import mbLib.CustomDialogClass;
import mbLib.DatabaseManagement;
import mbLib.MBSUtils;

public class BeneficiaryOTP extends Fragment implements OnClickListener {

    MainActivity act;
    EditText txt_otp;
    TextView txt_heading, txt_ref_id;
    Button btn_otp_submit, btn_otp_resend;
    ImageButton btn_back;
    ImageView btn_home,btn_logout,img_heading;


    int cnt = 0;
    String encrptdTranMpin = "", Tranmpin = "", strOTP = "", strRefId = "", strCustId = "", retVal = "", retMess = "", respcode = "", retval = "", respdescvalidate = "",
            respdescresend = "", respdescsendcust = "", strFromAct = "", strRetVal = "", strMobNo = "", regenOtp = "", retvalwbs = "", respdesc = "";
    JSONObject jObj;
    PrivateKey var1 = null;
    String var5 = "", var3 = "";
    SecretKeySpec var2 = null;
    CommonLib comnObj;
    DatabaseManagement dbms;

    public BeneficiaryOTP() {
    }

    @SuppressLint("ValidFragment")
    public BeneficiaryOTP(MainActivity a) {
        // TODO Auto-generated constructor stub
        act = a;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        var1 = act.var1;
        var3 = act.var3;
        act.frgIndex = 3213;
        View rootView = inflater.inflate(R.layout.otp_activity, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        dbms = new DatabaseManagement("shree_nagari.mbank", "listMobileBanking");
        txt_otp = (EditText) rootView.findViewById(R.id.txt_otp);
        txt_otp.setTextSize(22);
        txt_ref_id = (TextView) rootView.findViewById(R.id.txt_ref_id);
        btn_otp_submit = (Button) rootView.findViewById(R.id.btn_otp_submit);
        btn_otp_resend = (Button) rootView.findViewById(R.id.btn_otp_resend);
        txt_heading = (TextView) rootView.findViewById(R.id.txt_heading);
        btn_home = (ImageView) rootView.findViewById(R.id.btn_home1);
		btn_logout = (ImageView) rootView.findViewById(R.id.btn_logout);
        img_heading = (ImageView) rootView.findViewById(R.id.img_heading);
        txt_heading.setText(act.getString(R.string.lbl_otp_validtn));

        btn_otp_submit.setOnClickListener(this);
        btn_otp_resend.setOnClickListener(this);
        btn_home.setOnClickListener(this);
		btn_logout.setVisibility(View.GONE);
        img_heading.setImageResource(R.mipmap.otp);
		//btn_home.setVisibility(View.GONE);

        comnObj = new CommonLib(act);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            strCustId = bundle.getString("CUSTID");
            strFromAct = bundle.getString("FROMACT");
            try {
                jObj = new JSONObject(bundle.getString("JSONOBJ"));
                Log.e("jObj====", "jObj=====" + jObj);
            } catch (Exception je) {
                je.printStackTrace();
            }
        }
        Cursor c1 = dbms.selectFromTable("SHAREDPREFERENCE", "", null);//("select * from ", null);
        if (c1 != null) {
            while (c1.moveToNext()) {
                strMobNo = c1.getString(4);
            }
        }
        
        /*InputDialogBox inputBox = new InputDialogBox(act);
		inputBox.show();*/

        int flag = comnObj.chkConnectivity();
        if (flag == 0) {
            regenOtp = "N";
            CallWebServiceGenerateOtp c = new CallWebServiceGenerateOtp();
            c.execute();
        }

        return rootView;
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
		Fragment fragment=null;
		FragmentManager fragmentManager=null;
        switch (v.getId()) {

            case R.id.btn_otp_submit:
                strOTP = txt_otp.getText().toString().trim();
			
			/*if (strOTP.length() != 6) {
				showAlert(act.getString(R.string.alert_075));
			}*/
                if (strOTP.length() == 0) {
                    showAlert(act.getString(R.string.alert_076_01));
                } else {
                    int flag = comnObj.chkConnectivity();
                    if (flag == 0) {
                        CallWebServiceValidateOTP c = new CallWebServiceValidateOTP();
                        c.execute();
                    }

                }
                break;
            case R.id.btn_otp_resend:
                int flag = comnObj.chkConnectivity();
                if (flag == 0) {
                    regenOtp = "Y";
                    CallWebServiceGenerateOtp c = new CallWebServiceGenerateOtp();
                    c.execute();
                }
                break;

            case R.id.btn_home1:
                CustomDialogClass alert = new CustomDialogClass(act, getString(R.string.canel_otp_process)) {
                    @Override
                    public void onClick(View v) {
                        switch (v.getId()) {
                            case R.id.btn_ok:
                                Intent i = new Intent(act, NewDashboard.class);
                                i.putExtra("VAR1", var1);
                                i.putExtra("VAR3", var3);
                                startActivity(i);
                                act.finish();
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


				/*fragment = new ManageBeneficiaryMenuActivity(act);
				fragmentManager = this.getFragmentManager();
				fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();*/
                //((Activity) act).overridePendingTransition(0, 0);

                break;
            case R.id.btn_home:
                if (strFromAct.equalsIgnoreCase("ADDOTHBENF")) {
                    fragment = new AddOtherBankBeneficiary(act);
                    fragmentManager = this.getFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
                } else if (strFromAct.equalsIgnoreCase("ADDSAMBENF")) {
                    fragment = new AddSameBankBeneficiary(act);
                    fragmentManager = this.getFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
                } else if (strFromAct.equalsIgnoreCase("EDOTHBENF")) {
                    fragment = new EditOtherBankBeneficiary(act);
                    fragmentManager = this.getFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
                } else if (strFromAct.equalsIgnoreCase("EDSAMBENF")) {
                    fragment = new EditSameBankBeneficiary(act);
                    fragmentManager = this.getFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
                }
                break;
            default:
                break;
        }
    }

    class CallWebServiceValidateOTP extends AsyncTask<Void, Void, Void> {
        JSONObject jsonObj = new JSONObject();
        LoadProgressBar loadProBarObj = new LoadProgressBar(act);
        String generatedXML = "", ValidationData = "";
        boolean isWSCalled = false;

        @Override
        protected void onPreExecute() {
            loadProBarObj.show();
            strOTP = txt_otp.getText().toString().trim();
            strRefId = txt_ref_id.getText().toString().trim();
            strRefId = strRefId.substring(strRefId.indexOf(":") + 1).trim();
            //Valid 16 Digit Acc No:- 0020001010009747

            try {
				Log.e("Shubham", "OTP_VAL:--"+strOTP );
                jsonObj.put("CUSTID", strCustId);
                jsonObj.put("OTPVAL", strOTP);
                jsonObj.put("IMEINO", MBSUtils.getImeiNumber(act));
                jsonObj.put("REFID", strRefId);
                jsonObj.put("ISREGISTRATION", "N");
                jsonObj.put("SIMNO", MBSUtils.getSimNumber(act));
                jsonObj.put("METHODCODE", "20");
                Log.e("Shubham", "Ben_CallWebServiceValidateOTP_Request--->" + jsonObj.toString());
                //	ValidationData=MBSUtils.getValidationData(act,jObj.toString());

                Log.e("jObj====", "validatejObj=====" + jObj);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        ;

        @Override
        protected Void doInBackground(Void... arg0) {
            String value4 = getString(R.string.namespace);
            String value5 = getString(R.string.soap_action);
            String value6 = getString(R.string.url);
            //final String value7 = "callWebservice";
            final String value7 = getString(R.string.OTP_Validate_FUNCTION);

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
                String status = "";

                androidHttpTransport.call(value5, envelope);
                status = envelope.bodyIn.toString().trim();
                var5 = status;
                int pos = envelope.bodyIn.toString().trim().indexOf("=");
                if (pos > -1) {
                    status = status.substring(pos + 1, status.length() - 3);
                    var5 = status;
                    isWSCalled = true;
                }
            } catch (Exception e) {
                retMess = act.getString(R.string.alert_000);
                cnt = 0;
            }
            return null;
        }

        @Override
        protected void onPostExecute(final Void result) {
            loadProBarObj.dismiss();
            String errorCode = "";
            if (isWSCalled) {
                JSONObject jsonObj;
                try {
                    String str = CryptoClass.Function6(var5, var2);
                    jsonObj = new JSONObject(str.trim());
                    Log.e("Shubham", "Ben_CallWebServiceValidateOTP_Responce here--->" + jsonObj);
					/*ValidationData=xml_data[1].trim();
					if(ValidationData.equals(MBSUtils.getValidationData(act, xml_data[0].trim())))
					{*/
                    if (jsonObj.has("RESPCODE")) {
                        respcode = jsonObj.getString("RESPCODE");
                    } else {
                        respcode = "-1";
                    }
                    if (jsonObj.has("RETVAL")) {
                        retval = jsonObj.getString("RETVAL");
                    } else {
                        retval = "";
                    }
                    if (jsonObj.has("RESPDESC")) {
                        respdescvalidate = jsonObj.getString("RESPDESC");
                    } else {
                        respdescvalidate = "";
                    }

                    if (respdescvalidate.length() > 0 && respdescvalidate.indexOf("Success") == -1) {
                        showAlert(respdescvalidate);
                    } else {
                        Log.e("DSP=", "retval===" + retval);
                        if (retval.indexOf("SUCCESS") > -1) {
                            postSuccess_validateOTP(retval);
                        } else if(retval.indexOf("FAILED~MAXATTEMPT") > -1) {
							retMess = act.getString(R.string.alert_076_02);
							showAlert(retMess);
                        }else if(retval.indexOf("FAILED~INVALIDOTP") > -1){
							retMess = act.getString(R.string.alert_076);
							showAlert(retMess);
						}else {
							retMess = act.getString(R.string.alert_076);
							showAlert(retMess);
						}
						/*if (retval.indexOf("FAILED") > -1) {

							if (retval.indexOf("MAXATTEMPT") > -1) {
								retMess = act.getString(R.string.alert_076_02);
								showAlert(retMess);
							}
						} else if(retval.indexOf("INVALIDOTP") > -1){
							retMess = act.getString(R.string.alert_076);
							showAlert(retMess);
						}else {
							retMess = act.getString(R.string.alert_076);
							showAlert(retMess);
						}*/

                    }
				/*}	
				else
				{
					MBSUtils.showInvalidResponseAlert(act);
				}*/

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else {
                showAlert(act.getString(R.string.alert_000));
            }
        }
    }

    public void postSuccess_validateOTP(String reTval) {
        CallWebService_save_beneficiary savebenf = new CallWebService_save_beneficiary();
        savebenf.execute();
		/*CallWebServicestoreTransferTranWS storeTran= new CallWebServicestoreTransferTranWS();
		storeTran.execute();*/
    }

    class CallWebService_save_beneficiary extends AsyncTask<Void, Void, Void> {

        JSONObject jsonObj = new JSONObject();
        LoadProgressBar loadProBarObj = new LoadProgressBar(act);
        String generatedXML = "", ValidationData = "";
        boolean isWSCalled = false;

        @Override
        protected void onPreExecute() {
            loadProBarObj.show();
            strOTP = txt_otp.getText().toString().trim();
            strRefId = txt_ref_id.getText().toString().trim();
            strRefId = strRefId.substring(strRefId.indexOf(":") + 1).trim();

            try {
                //jObj.put("OTPVAL", ListEncryption.encryptData(strOTP+strCustId));
                //jObj.put("REFID", strRefId);
                String location = MBSUtils.getLocation(act);
                jObj.put("IMEINO", MBSUtils.getImeiNumber(act));
                jObj.put("ISREGISTRATION", "N");
                jObj.put("SIMNO", MBSUtils.getSimNumber(act));
                //jObj.put("TRANPIN", Tranmpin);
                jObj.put("MOBILENO", strMobNo);//MBSUtils.getMyPhoneNO(act)
                jObj.put("IPADDRESS", MBSUtils.getLocalIpAddress());
                jObj.put("OSVERSION", Build.VERSION.RELEASE);
                jObj.put("LATITUDE", location.split("~")[0]);
                jObj.put("LONGITUDE", location.split("~")[1]);
                jObj.put("METHODCODE", "14");
                Log.e("Shubham", "Ben_CallWebServiceValidateOTP2_Request--->" + jObj.toString());
                //ValidationData=MBSUtils.getValidationData(act,jObj.toString());
            } catch (Exception e) {
                e.printStackTrace();
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

                request.addProperty("value1", CryptoClass.Function5(jObj.toString(), var2));
                request.addProperty("value2", CryptoClass.Function3(keyStr, var1));
                request.addProperty("value3", var3);
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.setOutputSoapObject(request);
                HttpTransportSE androidHttpTransport = new HttpTransportSE(value6, 15000);
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
                } catch (Exception e) {
                    e.printStackTrace();
                    retMess = act.getString(R.string.alert_000);
                    cnt = 0;
                }
            } catch (Exception e) {
                retMess = act.getString(R.string.alert_000);
                cnt = 0;
            }
            return null;
        }

        @Override
        protected void onPostExecute(final Void result) {
            loadProBarObj.dismiss();
            String errorCode = "";
            if (isWSCalled) {

                JSONObject jsonObj;
                try {

                    String str = CryptoClass.Function6(var5, var2);
                    jsonObj = new JSONObject(str.trim());
                    Log.e("Shubham", "Ben_CallWebServiceValidateOTP2_Responce--->" + jsonObj.toString());
					/*ValidationData=xml_data[1].trim();
					if(ValidationData.equals(MBSUtils.getValidationData(act, xml_data[0].trim())))
					{*/
                    if (jsonObj.has("RESPCODE")) {
                        respcode = jsonObj.getString("RESPCODE");
                    } else {
                        respcode = "-1";
                    }
                    if (jsonObj.has("RETVAL")) {
                        retval = jsonObj.getString("RETVAL");
                    } else {
                        retval = "";
                    }
                    if (jsonObj.has("RESPDESC")) {
                        respdescvalidate = jsonObj.getString("RESPDESC");
                    } else {
                        respdescvalidate = "";
                    }

                    if (respdescvalidate.length() > 0) {
                        showAlertPost(respdescvalidate);
                    } else {
                        if (retval.indexOf("FAILED") > -1) {
                            if (retval.indexOf("DUPLICATEACCOUNT") > -1) {

                                retMess = act.getString(R.string.alert_019);
                                showAlert(retMess);

                            } else if (retval.indexOf("DUPLICATENICKNAME") > -1) {

                                retMess = act.getString(R.string.alert_020);
                                showAlert(retMess);
                            } else if (retval.indexOf("INCORRECTIFSC") > -1) {
                                retMess = act.getString(R.string.alert_185);//alert_018);
                                showAlert(retMess);
                            } else if (retval.indexOf("WRONGMPIN") > -1) {
                                //	loadProBarObj.dismiss();
                                retMess = act.getString(R.string.alert_125);
                                showAlert(retMess);
                            } else if (retval.indexOf("INVALIDTRANS") > -1) {
                                retMess = getString(R.string.alert_ivalidtransaction);

                                showAlert(retMess);


                            } else {

                                // /////retMess="Failed To Add Other Bank Beneficiary Due To Server Problem.";
                                retMess = act.getString(R.string.alert_021);
                                //loadProBarObj.dismiss();
                                showAlert(retMess);
                                //initAll();

                            }
                            FragmentManager fragmentManager;
                            Fragment fragment = new ManageBeneficiaryMenuActivity(act);
                            act.setTitle(getString(R.string.lbl_manage_beneficiary));
                            fragmentManager = getFragmentManager();
                            fragmentManager.beginTransaction()
                                    .replace(R.id.frame_container, fragment).commit();
                        } else {
                            //loadProBarObj.dismiss();
                            post_successsaveBeneficiaries(retval);
                        }
                    }
				
					
					/*}	
					else
					{
						MBSUtils.showInvalidResponseAlert(act);
					}*/

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            } else {
                showAlert(act.getString(R.string.alert_000));
            }
        }
    }

    public void showAlertPost(final String str) {

        ErrorDialogClass alert = new ErrorDialogClass(act, "" + str) {
            @Override
            public void onClick(View v) {
                this.dismiss();
			/*	FragmentManager fragmentManager;
				Fragment fragment = new FundTransferMenuActivity(act);
				act.setTitle(getString(R.string.lbl_same_bnk_trans));
				fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();*/
            }

        };
        //this.dismiss();
        FragmentManager fragmentManager;
        Fragment fragment = new ManageBeneficiaryMenuActivity(act);
        act.setTitle(getString(R.string.lbl_manage_beneficiary));
        fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
        alert.show();
    }

    public void post_successsaveBeneficiaries(String reTval) {
        if (strFromAct.equalsIgnoreCase("ADDOTHBENF")) {
            respcode = "";
            retMess = act.getString(R.string.alert_022);
            //showAlert(retMess);
        } else if (strFromAct.equalsIgnoreCase("ADDSAMBENF")) {
            respcode = "";
            retMess = act.getString(R.string.alert_013);

            //showAlert(retMess);
        } else if (strFromAct.equalsIgnoreCase("EDOTHBENF")) {
            respcode = "";
            retMess = act.getString(R.string.alert_029);
            //showAlert(retMess);
        } else {
            respcode = "";
            retMess = act.getString(R.string.alert_017);
        }

        showAlert(retMess);
        FragmentManager fragmentManager;
        Fragment fragment = new ManageBeneficiaryMenuActivity(act);
        act.setTitle(act.getString(R.string.lbl_manage_beneficiary));
        fragmentManager = act.getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
    }

    class CallWebServiceGenerateOtp extends AsyncTask<Void, Void, Void> {
        LoadProgressBar loadProBarObj = new LoadProgressBar(act);
        String generatedXML = "", ValidationData = "";
        boolean isWSCalled = false;
        JSONObject jsonObj = new JSONObject();

        @Override
        protected void onPreExecute() {
            loadProBarObj.show();

            try {
                jsonObj.put("CUSTID", strCustId);
                jsonObj.put("REQSTATUS", "R");
                jsonObj.put("REQFROM", strFromAct);
                jsonObj.put("MOBNO", strMobNo);
                jsonObj.put("IMEINO", MBSUtils.getImeiNumber(act));
                jsonObj.put("SIMNO", MBSUtils.getSimNumber(act));
                jsonObj.put("METHODCODE", "26");
                //ValidationData=MBSUtils.getValidationData(act,jsonObj.toString());

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        ;

        @Override
        protected Void doInBackground(Void... arg0) {
            String value4 = getString(R.string.namespace);
            String value5 = getString(R.string.soap_action);
            String value6 = getString(R.string.url);
            //final String value7 = "callWebservice";
            final String value7 = getString(R.string.OTP_Generate_FUNCTION);
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
                String status = "";

                androidHttpTransport.call(value5, envelope);
                status = envelope.bodyIn.toString().trim();
                var5 = status;
                int pos = envelope.bodyIn.toString().trim().indexOf("=");
                if (pos > -1) {
                    status = status.substring(pos + 1, status.length() - 3);
                    var5 = status;
                    isWSCalled = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
                retMess = act.getString(R.string.alert_000);
                cnt = 0;
            }

            return null;

        }

        @Override
        protected void onPostExecute(final Void result) {
            loadProBarObj.dismiss();
            if (isWSCalled) {

                JSONObject jsonObj;
                try {
                    String str = CryptoClass.Function6(var5, var2);
                    jsonObj = new JSONObject(str.trim());
                	/*ValidationData=xml_data[1].trim();
                	if(ValidationData.equals(MBSUtils.getValidationData(act, xml_data[0].trim())))
                	{*/
                    if (jsonObj.has("RESPCODE")) {
                        respcode = jsonObj.getString("RESPCODE");
                    } else {
                        respcode = "-1";
                    }
                    if (jsonObj.has("RETVAL")) {
                        retval = jsonObj.getString("RETVAL");
                    } else {
                        retval = "";
                    }
                    if (jsonObj.has("RESPDESC")) {
                        respdescresend = jsonObj.getString("RESPDESC");
                    } else {
                        respdescresend = "";
                    }


                    if (respdescresend.length() > 0) {
                        showAlert(respdescresend);
                    } else {

                        if (retval.split("~")[0].indexOf("SUCCESS") > -1) {
                            post_successresend(retval);
                        } else {
                            retMess = act.getString(R.string.alert_094);
                            showAlert(retMess);
                        }
                    }
                	/*}	
                	else
                	{
                		MBSUtils.showInvalidResponseAlert(act);
                	}*/
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else {
                retMess = act.getString(R.string.alert_000);
                showAlert(retMess);
            }
        }
    }// CallWebService_resend_otp

    public void post_successresend(String retval) {
        txt_ref_id.setText("");
        txt_ref_id.setText(act.getString(R.string.lbl_ref_id) + " :" + retval.split("~")[1].split("!!")[2]);
    }

    public void post_successsendcust(String retval) {

    }

    public void post_successvalidate(String retval) {
        respdescvalidate = "";
        respcode = "";
        String decryptedAccounts = retval;
    }

    public void onBackPressed() {

        FragmentManager fragmentManager;
        Fragment fragment = new ManageBeneficiaryMenuActivity(act);
        act.setTitle(getString(R.string.lbl_manage_beneficiary));
        fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();

    }

    public void showAlert(final String str) {
        ErrorDialogClass alert = new ErrorDialogClass(act, "" + str) {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btn_ok:
                        if ((str.equalsIgnoreCase(respdescvalidate)) && (respcode.equalsIgnoreCase("0"))) {
                            post_successvalidate(retval);
                        } else if ((str.equalsIgnoreCase(respdescvalidate)) && (respcode.equalsIgnoreCase("1"))) {
                            this.dismiss();
                        } else if ((str.equalsIgnoreCase(respdescresend)) && (respcode.equalsIgnoreCase("0"))) {
                            post_successresend(retval);
                        } else if ((str.equalsIgnoreCase(respdescresend)) && (respcode.equalsIgnoreCase("1"))) {
                            this.dismiss();
                        } else if ((str.equalsIgnoreCase(respdescsendcust)) && (respcode.equalsIgnoreCase("0"))) {
                            post_successsendcust(retval);
                        } else if ((str.equalsIgnoreCase(respdescsendcust)) && (respcode.equalsIgnoreCase("1"))) {
                            this.dismiss();
                        }
						/*else if(str.equalsIgnoreCase(act.getString(R.string.alert_111)) || str.indexOf(act.getString(R.string.alert_125_1))>-1)
						{
							InputDialogBox inputBox = new InputDialogBox(act);
							inputBox.show();
						}*/

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

}

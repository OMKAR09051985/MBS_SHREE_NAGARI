package shree_nagari.mbank;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.FileProvider;

import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.File;
import java.io.FileOutputStream;
import java.security.PrivateKey;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.crypto.spec.SecretKeySpec;

import mbLib.CryptoClass;
import mbLib.CustomDialogClass;
import mbLib.DatabaseManagement;
import mbLib.ListEncryption;
import mbLib.MBSUtils;

public class TransferOTP extends Fragment implements OnClickListener {
    MainActivity act;
    EditText txt_otp;
    TextView txt_heading, txt_ref_id;
    Button btn_otp_submit, btn_otp_resend;
    PrivateKey var1 = null;
    String var5 = "", var3 = "";
    SecretKeySpec var2 = null;
    ImageView btn_home, btn_logout, img_heading;
    int cnt = 0;
    String encrptdTranMpin = "", tranmpin = "", strOTP = "", strRefId = "", strCustId = "", retVal = "", retMess = "", respcode = "", retval = "", respdescvalidate = "",
            respdescresend = "", respdescsendcust = "", strFromAct = "", strRetVal = "", strMobNo = "", regenOtp = "", retvalwbs = "", respdesc = "";
    JSONObject jObj;

    CommonLib comnObj;
    DatabaseManagement dbms;

    public TransferOTP() {
    }

    @SuppressLint("ValidFragment")
    public TransferOTP(MainActivity a) {
        act = a;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.otp_activity, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        var1 = act.var1;
        var3 = act.var3;
        dbms = new DatabaseManagement("shree_nagari.mbank", "listMobileBanking");
        txt_otp = (EditText) rootView.findViewById(R.id.txt_otp);
        txt_otp.setTextSize(22);
        txt_ref_id = (TextView) rootView.findViewById(R.id.txt_ref_id);
        btn_otp_submit = (Button) rootView.findViewById(R.id.btn_otp_submit);
        btn_otp_resend = (Button) rootView.findViewById(R.id.btn_otp_resend);
        btn_home = (ImageView) rootView.findViewById(R.id.btn_home1);
        btn_logout = (ImageView) rootView.findViewById(R.id.btn_logout);
        txt_heading = (TextView) rootView.findViewById(R.id.txt_heading);
        img_heading = (ImageView) rootView.findViewById(R.id.img_heading);
        txt_heading.setText(act.getString(R.string.lbl_otp_validtn));

        btn_otp_submit.setOnClickListener(this);
        btn_otp_resend.setOnClickListener(this);
        btn_home.setOnClickListener(this);
        btn_logout.setOnClickListener(this);
        btn_logout.setVisibility(View.GONE);
        img_heading.setImageResource(R.mipmap.otp);

        comnObj = new CommonLib(act);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            strCustId = bundle.getString("CUSTID");
            strFromAct = bundle.getString("FROMACT");
            try {
                jObj = new JSONObject(bundle.getString("JSONOBJ"));
                if (jObj.has("TRANPIN")) {
                    tranmpin = jObj.getString("TRANPIN");
                }
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

        int flag = comnObj.chkConnectivity();
        if (flag == 0) {
            regenOtp = "N";
            new CallWebServiceGenerateOtp().execute();
        }

        return rootView;
    }

    @Override
    public void onClick(View v) {
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
                        new CallWebServiceValidateOTP().execute();
                    }

                }
                break;
            case R.id.btn_otp_resend:
                int flag = comnObj.chkConnectivity();
                if (flag == 0) {
                    regenOtp = "Y";
                    new CallWebServiceGenerateOtp().execute();
                }
                break;
            case R.id.btn_home1:
                try {
                    CustomDialogClass alert = new CustomDialogClass(act, getString(R.string.canel_otp_process)) {
                         @SuppressLint("NonConstantResourceId")
                        @Override
                        public void onClick(View v) {
                            switch (v.getId()) {
                                case R.id.btn_ok:
                                    switch (strFromAct) {
                                        case "RTNTBANK":
                                            getFragmentManager().beginTransaction().replace(R.id.frame_container, new OtherBankTranRTGS(act)).commit();
                                            break;

                                        case "IMPSBANK":
                                            getFragmentManager().beginTransaction().replace(R.id.frame_container, new OtherBankTranIFSC(act)).commit();
                                            break;

                                        case "QRSEND":
                                            getFragmentManager().beginTransaction().replace(R.id.frame_container, new QrcodeSendActivity(act)).commit();
                                            break;

                                        case "SAMEBANK":
                                            getFragmentManager().beginTransaction().replace(R.id.frame_container, new SameBankTransfer(act)).commit();
                                            break;

                                        case "OWNBANK":
                                            getFragmentManager().beginTransaction().replace(R.id.frame_container, new OwnAccountTransfer(act)).commit();
                                            break;

                                        default:
                                            break;

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
                } catch (Exception e) {
                    Log.e("Shubham", "Home Button Click Error-> " + e.getMessage());
                }

                break;
            default:
                break;
        }
    }

    class CallWebServiceValidateOTP extends AsyncTask<Void, Void, Void> {
        JSONObject jsonObj = new JSONObject();
        LoadProgressBar loadProBarObj = new LoadProgressBar(act);
        String ValidationData = "";
        boolean isWSCalled = false;

        @Override
        protected void onPreExecute() {
            loadProBarObj.show();
            strOTP = txt_otp.getText().toString().trim();
            strRefId = txt_ref_id.getText().toString().trim();
            strRefId = strRefId.substring(strRefId.indexOf(":") + 1).trim();
            try {
                jObj.put("OTPVAL", strOTP);
                jObj.put("CUSTID", strCustId);
                jObj.put("IMEINO", MBSUtils.getImeiNumber(act));
                jObj.put("REFID", strRefId);
                jObj.put("ISREGISTRATION", "N");
                jObj.put("SIMNO", MBSUtils.getSimNumber(act));
                jObj.put("METHODCODE", "20");
                //ValidationData=MBSUtils.getValidationData(act,jObj.toString());
                Log.e("TAG", "onPreExecuteCallWebServiceValidateOTP: " + jObj.toString());


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
                    Log.e("Shubham", "onPostExecuteCallWebServiceValidateOTP: " + jsonObj.toString());
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
                        //Log.e("SAM=","retval==="+retval);
                        if (retval.indexOf("SUCCESS") > -1) {
                            postSuccess_validateOTP(retval);
                        } else {

                            Log.e("DSP=", "retval===" + retval);
                            if (retval.indexOf("SUCCESS") > -1) {
                                postSuccess_validateOTP(retval);
                            } else if (retval.indexOf("FAILED~MAXATTEMPT") > -1) {
                                retMess = act.getString(R.string.alert_076_02);
                                showAlert(retMess);
                            } else if (retval.indexOf("FAILED~INVALIDOTP") > -1) {
                                retMess = act.getString(R.string.alert_076);
                                showAlert(retMess);
                            } else {
                                retMess = act.getString(R.string.alert_076);
                                showAlert(retMess);
                            }
                        }

                    }

					/*}else{

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
        CallWebServicestoreTransferTranWS storeTran = new CallWebServicestoreTransferTranWS();
        storeTran.execute();
    }

    class CallWebServicestoreTransferTranWS extends AsyncTask<Void, Void, Void> {
        JSONObject jsonObj = new JSONObject();
        LoadProgressBar loadProBarObj = new LoadProgressBar(act);
        String ValidationData = "";
        boolean isWSCalled = false;

        @Override
        protected void onPreExecute() {
            loadProBarObj.show();
            strOTP = txt_otp.getText().toString().trim();
            strRefId = txt_ref_id.getText().toString().trim();
            strRefId = strRefId.substring(strRefId.indexOf(":") + 1).trim();
            //Log.e("Shubham", "tranmpin2: "+tranmpin );


            try {
                //jObj.put("OTPVAL", ListEncryption.encryptData(strOTP+strCustId));
                if (strFromAct.equalsIgnoreCase("QRSEND")) {
                    jObj.put("TRANSFERTYPE", "QR");
                } else if (strFromAct.equalsIgnoreCase("IMPSBANK")) {
                    jObj.put("TRANSFERTYPE", "P2A");
                }
                String location = MBSUtils.getLocation(act);
                jObj.put("IMEINO", MBSUtils.getImeiNumber(act));
                jObj.put("REFID", strRefId);
                jObj.put("ISREGISTRATION", "N");
                jObj.put("SIMNO", MBSUtils.getSimNumber(act));
                jObj.put("TRANPIN", tranmpin);
                jObj.put("MOBILENO", MBSUtils.getMyPhoneNO(act));
                jObj.put("IPADDRESS", MBSUtils.getLocalIpAddress());
                jObj.put("OSVERSION", Build.VERSION.RELEASE);
                jObj.put("LATITUDE", location.split("~")[0]);
                jObj.put("LONGITUDE", location.split("~")[1]);
                if (strFromAct.equalsIgnoreCase("RTNTBANK") ||strFromAct.equalsIgnoreCase("IMPSBANK")) {
                    jObj.put("METHODCODE", "96");
                } else {
                    jObj.put("METHODCODE", "16");
                }

                Log.e("Shubham", "CallWebServicestoreTransferTranWS_Request: " + jObj.toString());

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
                    Log.e("Shubham", "CallWebServicestoreTransferTranWS_Responce: " + jsonObj.toString());
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
                        showAlertPost(respdescvalidate);
                    } else {
                        //Log.e("TRANSFEROTP","retval==="+retval);
                        if (retval.indexOf("SUCCESS") > -1) {
                            post_successsaveTransferTran(retval);
                        } else {
                            if (retval.indexOf("LIMIT_EXCEEDS") > -1) {
                                retMess = act.getString(R.string.alert_031);
                                FragmentManager fragmentManager;
                                Fragment fragment = new FundTransferMenuActivity(act);
                                act.setTitle(getString(R.string.lbl_fund_transfer));
                                fragmentManager = getFragmentManager();
                                fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
                            } else if (retval.indexOf("DUPLICATE") > -1) {
                                try {
                                    retMess = act.getString(R.string.alert_119) + jObj.getString("TRANID") + "\n" + act.getString(R.string.alert_120);
                                } catch (JSONException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                                showAlert(retMess);
                                FragmentManager fragmentManager;
                                Fragment fragment = new FundTransferMenuActivity(act);
                                act.setTitle(getString(R.string.lbl_fund_transfer));
                                fragmentManager = getFragmentManager();
                                fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
                            } else if (retval.indexOf("WRONGTRANPIN") > -1) {

                                String msg[] = retval.split("~");
                                String first = msg[1];
                                String second = msg[2];

                                int count = Integer.parseInt(second);
                                count = 5 - count;
                                retMess = act.getString(R.string.alert_125_1) + " " + count
                                        + " " + act.getString(R.string.alert_125_2);
                                showAlert(retMess);
                            } else if (retval.indexOf("BENFACCINVALID") > -1) {
                                retMess = getString(R.string.alert_benfivalid);

                                showAlert(retMess);
                                FragmentManager fragmentManager;
                                Fragment fragment = new FundTransferMenuActivity(act);
                                act.setTitle(getString(R.string.lbl_same_bnk_trans));
                                fragmentManager = getFragmentManager();
                                fragmentManager.beginTransaction()
                                        .replace(R.id.frame_container, fragment).commit();


                            } else if (retval.indexOf("INVALIDTRANS") > -1) {
                                retMess = getString(R.string.alert_ivalidtransaction);

                                showAlert(retMess);


                            } else if (retval.indexOf("BLOCKEDFORDAY") > -1) {
                                //loadProBarObj.dismiss();
                                retMess = act.getString(R.string.login_alert_005);
                                showAlert(retMess);
                            } else if (retval.indexOf("FAILED~") > -1) {
                                String msg[] = retval.split("~");
                                if (msg.length > 3) {
                                    String postingStatus = msg[1];
                                    String req_id = msg[2];
                                    String errorMsg = msg[3];
                                    if (req_id.length() > 0) {
                                        if (req_id != null || req_id.length() > 0)
                                            retMess = act.getString(R.string.alert_162) + " " + req_id;
                                    } else if (errorMsg.length() > 0) {
                                        retMess = act.getString(R.string.alert_032) + errorMsg;
                                    }
                                } else {
                                    retMess = act.getString(R.string.alert_032);
                                }
                                showAlert(retMess);

                                FragmentManager fragmentManager;
                                Fragment fragment = new FundTransferMenuActivity(act);
                                act.setTitle(getString(R.string.lbl_fund_transfer));
                                fragmentManager = getFragmentManager();
                                fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
                            } else if (retval.indexOf("FAILED") > -1) {
                                if (retval.split("~")[1] != "null" || retval.split("~")[1] != "") {
                                    errorCode = retval.split("~")[1];
                                } else {
                                    errorCode = "NA";
                                }
                                if (errorCode.equalsIgnoreCase("999")) {
                                    retMess = act.getString(R.string.alert_179);
                                } else if (errorCode.equalsIgnoreCase("001")) {
                                    retMess = act.getString(R.string.alert_180);
                                } else if (errorCode.equalsIgnoreCase("002")) {
                                    retMess = act.getString(R.string.alert_181);
                                } else if (errorCode.equalsIgnoreCase("003")) {
                                    retMess = act.getString(R.string.alert_182);
                                } else if (errorCode.equalsIgnoreCase("004")) {
                                    retMess = act.getString(R.string.alert_179);
                                } else if (errorCode.equalsIgnoreCase("005")) {
                                    retMess = act.getString(R.string.alert_183);
                                } else if (errorCode.equalsIgnoreCase("006")) {
                                    retMess = act.getString(R.string.alert_184);
                                } else if (errorCode.equalsIgnoreCase("007")) {
                                    retMess = act.getString(R.string.alert_179);
                                } else if (errorCode.equalsIgnoreCase("008")) {
                                    retMess = act.getString(R.string.alert_176);
                                } else {
                                    retMess = act.getString(R.string.trnsfr_alert_001);
                                    showAlert(retMess);// setAlert();
                                    FragmentManager fragmentManager;
                                    Fragment fragment = new FundTransferMenuActivity(act);
                                    act.setTitle(getString(R.string.lbl_same_bnk_trans));
                                    fragmentManager = getFragmentManager();
                                    fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
                                }
                            } else {
                                retMess = act.getString(R.string.alert_032);
                                showAlert(retMess);
                            }
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
                FragmentManager fragmentManager;
                Fragment fragment = new FundTransferMenuActivity(act);
                act.setTitle(getString(R.string.lbl_same_bnk_trans));
                fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
            }
        };
        alert.show();
    }

    public void post_successsaveTransferTran(String reTval) {
        if (strFromAct.equalsIgnoreCase("RTNTBANK")) {
            respcode = "";
            String msg[] = reTval.split("~");
            if (msg.length > 2) {
                if (msg[2] != null || msg[2].length() > 0) {
                    String postingStatus = msg[1];
                    String req_id = msg[2];
                    retMess = act.getString(R.string.alert_030) + " " + act.getString(R.string.alert_121) + " " + req_id;
                }
            } else {
                retMess = act.getString(R.string.alert_030);
            }
        } else if (strFromAct.equalsIgnoreCase("IMPSBANK")) {
            respcode = "";
            String msg[] = reTval.split("~");
            if (msg.length > 2) {
                if (msg[2] != null || msg[2].length() > 0) {
                    String postingStatus = msg[0];
                    String req_id = msg[1];

                    retMess = act.getString(R.string.alert_030) + " " + act.getString(R.string.alert_121) + " " + req_id;
                }
            } else {
                retMess = act.getString(R.string.alert_030);
            }
        } else if (strFromAct.equalsIgnoreCase("QRSEND")) {
            respcode = "";
            String tranId = reTval.split("~")[2];
            retMess = act.getString(R.string.alert_030) + " " + act.getString(R.string.alert_121) + " " + tranId;
        } else {
            respcode = "";
            String tranId = reTval.split("~")[2];
            retMess = act.getString(R.string.alert_030) + " " + act.getString(R.string.alert_121) + " " + tranId;
        }

        //showAlert(retMess);
        showshareAlert(retMess);
		/*FragmentManager fragmentManager;
		Fragment fragment = new FundTransferMenuActivity(act);
		act.setTitle(act.getString(R.string.lbl_same_bnk_trans));
		fragmentManager = act.getFragmentManager();
		fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();*/
    }

    public void post_successvalidate(String retval) {
        respdescvalidate = "";
        respcode = "";
        String decryptedAccounts = retval;
    }

    public void post_successresend(String retval) {
        txt_ref_id.setText("");
        txt_ref_id.setText(act.getString(R.string.lbl_ref_id) + " :" + retval.split("~")[1].split("!!")[2]);
    }

    public void post_successsendcust(String retval) {

    }

    class CallWebServiceGenerateOtp extends AsyncTask<Void, Void, Void> {
        LoadProgressBar loadProBarObj = new LoadProgressBar(act);
        String ValidationData = "";
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
                // ValidationData=MBSUtils.getValidationData(act,jsonObj.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
            if (isWSCalled) {

                JSONObject jsonObj;
                try {
                    String str = CryptoClass.Function6(var5, var2);
                    jsonObj = new JSONObject(str.trim());
                    Log.e("Shubham", "onPostExecuteCallWebServiceGenerateOtp: " + jsonObj.toString());
                	 
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
                            Log.e("Shubham", "retvalSUCCESS: " + retval);
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

    public void showshareAlert(final String str) {
        CustomDialogClass alert = new CustomDialogClass(act, str) {
            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.custom_dialog_box);
                Button btn = (Button) findViewById(R.id.btn_cancel);
                TextView txt_message = (TextView) findViewById(R.id.txt_dia);
                txt_message.setText(str);
                btn.setOnClickListener(this);
                btn.setText("Share");
                Button btnok = (Button) findViewById(R.id.btn_ok);
                btnok.setOnClickListener(this);
                btnok.setText("OK");
            }

            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btn_ok:
                        this.dismiss();
                        FragmentManager fragmentManager;
                        Fragment fragment = new FundTransferMenuActivity(act);
                        act.setTitle(act.getString(R.string.lbl_same_bnk_trans));
                        fragmentManager = act.getFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
                        break;

                    case R.id.btn_cancel:
//						String shareBody = null;
//						try {
//							shareBody = "Beneficiary Name : "+"name_val"+"\n" +
//									"Account Number : "+jObj.getString("DRACCNO")+"\n" +
//									"Amount : "+jObj.getString("AMOUNT")+"\n" +
//									"Reference ID : "+strRefId;
//						} catch (JSONException e) {
//							e.printStackTrace();
//						}
//						Intent sharingIntent = new Intent(Intent.ACTION_SEND);
//						sharingIntent.setType("text/plain");
//						sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here");
//						sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
//						//startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.share_using)));
//						startActivity(sharingIntent);

                        String shareBody = null;
                        try {
                            Calendar c = Calendar.getInstance();
                            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                            String CurrentDateTime = sdf.format(c.getTime());
                            String dbaccountno = jObj.getString("DRACCNO");
                            String craccountno = jObj.getString("CRACCNO");
                            dbaccountno = "XXXXXXXXXXXX" + dbaccountno.substring(12);
                            craccountno = "XXXXXXXXXXXX" + craccountno.substring(12);
                            //shareBody = "Paid On : "+CurrentDateTime+"\nFrom : "+dbaccountno+"\nTo : "+craccountno+"\nAmount : "+jObj.getString("AMOUNT")+".00 Rs"+"\nReference ID : "+request_id;
                            Intent share = new Intent(Intent.ACTION_SEND);
                            Bitmap src = BitmapFactory.decodeResource(getResources(), R.mipmap.share_bg_final); // the original file yourimage.jpg i added in resources
                            Bitmap dest = Bitmap.createBitmap(src.getWidth(), src.getHeight(), Bitmap.Config.ARGB_8888);

                            String shareBody1 = "Paid On : " + "CurrentDateTime";
                            Typeface typeface1 = Typeface.createFromAsset(getContext().getAssets(), "fonts/calibri.ttf");
                            //Typeface typeface = Typeface.create(typeface1, Typeface.DEFAULT_BOLD);
                            Canvas cs = new Canvas(dest);
                            Paint tPaint = new Paint(Paint.LINEAR_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
                            tPaint.setTypeface(typeface1);
                            tPaint.setTextSize(22);
                            tPaint.setColor(Color.BLACK);
                            cs.drawBitmap(src, 0f, 0f, null);
                            float height = tPaint.measureText("yY");
                            float width = tPaint.measureText(shareBody1);
                            float x_coord = (src.getWidth() - width) / 2;
                            cs.drawText("Paid On : " + CurrentDateTime, x_coord, height + 150f, tPaint);
                            cs.drawText("From : " + dbaccountno, x_coord, height + 200f, tPaint);
                            cs.drawText("To : " + craccountno, x_coord, height + 250f, tPaint);
                            cs.drawText("Amount : " + jObj.getString("AMOUNT") + ".00 Rs", x_coord, height + 300f, tPaint);
                            cs.drawText("Reference ID : " + strRefId, x_coord, height + 350f, tPaint);// 15f is to put space between top edge and the text, if you want to change it, you can

                            dest.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(new File("/sdcard/SharedImage.jpg")));
                            // dest is Bitmap, if you want to preview the final image, you can display it on screen also before saving
                            share.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            Uri apkURI = FileProvider.getUriForFile(act, act.getPackageName(), new File("/sdcard/SharedImage.jpg"));
                            share.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///sdcard/SharedImage.jpg"));
                            share.putExtra(Intent.EXTRA_STREAM, apkURI);
                            share.setType("image/*");
                            //startActivity(Intent.createChooser(share, "Share Image"));
                            //Change here by Shubham
                            startActivityForResult(Intent.createChooser(share, "Share via"), 1);
                        } catch (Exception e) {
                            e.printStackTrace();
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
                        } else if ((str.equalsIgnoreCase(respdesc)) && (respcode.equalsIgnoreCase("0"))) {
                            post_success(retvalwbs);
                        } else if ((str.equalsIgnoreCase(respdesc)) && (respcode.equalsIgnoreCase("1"))) {
                            this.dismiss();
                        } else if (str.equalsIgnoreCase(act.getString(R.string.alert_111)) || str.indexOf(act.getString(R.string.alert_125_1)) > -1) {
                            InputDialogBox inputBox = new InputDialogBox(act);
                            inputBox.show();
                        } else if (str.equals(act.getString(R.string.alert_ivalidtransaction))) {
                            int flag = comnObj.chkConnectivity();
                            if (flag == 0) {
                                CallWebService c = new CallWebService();
                                c.execute();

                            }

                        } else
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

    class CallWebService extends AsyncTask<Void, Void, Void> {
        JSONObject jsonObj = new JSONObject();
        String ValidationData = "";

        @Override
        protected void onPreExecute() {
            try {
                respcode = "";
                retvalwbs = "";
                respdesc = "";
                Log.e("@DEBUG", "LOGOUT preExecute()");
                jsonObj.put("CUSTID", strCustId);
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
                        post_success(retvalwbs);
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

    public void post_success(String retvalwbs) {
        respcode = "";
        respdesc = "";
        act.finish();
        System.exit(0);

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
            txt_otp.setText("");
        }

        @Override
        public void onClick(View v) {
            try {
                String str = mpin.getText().toString().trim();
                tranmpin = str;
                Log.e("Shubham", "tranmpin1: " + tranmpin);
                encrptdTranMpin = ListEncryption.encryptData(strCustId + str);
                if (str.length() == 0) {
                    encrptdTranMpin = "";
                    retMess = getString(R.string.alert_116);
                    showAlert(retMess);// setAlert();
                    this.show();
                }/* else if (str.length() != 6) {
					encrptdTranMpin="";
					retMess = getString(R.string.alert_037);
					showAlert(retMess);// setAlert();
					this.show();
				} */ else {
				/*	int flag = comnObj.chkConnectivity();
					if (flag == 0)
					{
						regenOtp="N";
						CallWebServiceGenerateOtp c = new CallWebServiceGenerateOtp();
						c.execute();
					}*/
                    this.hide();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }// end onClick
    }// end InputDialogBox

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == 1) {
                FragmentManager fragmentManager;
                Fragment fragment = new FundTransferMenuActivity(act);
                act.setTitle(act.getString(R.string.lbl_same_bnk_trans));
                fragmentManager = act.getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
            }
        } catch (Exception e) {
            Log.e("Shubham", "onActivityResult: " + e.getMessage());
        }
    }
}
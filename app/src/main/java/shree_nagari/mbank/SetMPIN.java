package shree_nagari.mbank;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.security.PrivateKey;
import java.util.Random;

import javax.crypto.spec.SecretKeySpec;

import mbLib.CryptoClass;
import mbLib.DatabaseManagement;
import mbLib.DialogBox;
import mbLib.MBSUtils;

public class SetMPIN extends Activity implements OnClickListener {

    EditText txt_enter_pass, txt_re_enter_pass;
    DatabaseManagement dbms;
    MainActivity act;
    DialogBox dbs;
    EditText txt_enter_tran_pass, txt_re_enter_tran_pass, txt_password, txt_new_password;
    ImageButton btn_home;//, btn_back;
    Button btn_save_pass, btn_reset;
    TextView txt_heading;
    String enterMPIN, validusernm = "", reEnteredMPIN, retVal, retMess, strCustId, queOne, queTwo, ansOne, ansTwo, strMobNo, enterpass, enter_new_pass;
    String fromAct = "", userid = "", enterTranMPIN = "", reEnteredTranMPIN = "", cust_name = "", respdesc = "", retvalwbs = "", custId = "",
            respcode = "", retval = "", respdescSetMPIN = "", respdescChangeMPIN = "", respdescCheckUsrNmAvailability = "";
    int cnt = 0, flag = 0;
    Bundle b1;
    PrivateKey var1 = null;
    String var5 = "", var3 = "";
    SecretKeySpec var2 = null;
    boolean WSCalled = false, isValidUser = false;
    boolean isWSCalled = false;
    private EditText txt_user_id;
    SetMPIN setmpinobj;
    private String userId;
    private TextView lbl_chk_avail;
    private LinearLayout lyt_usrnm_txt;
    private LinearLayout lyt_usrnm_lbl;
    ImageView img_heading;
    ImageView btn_home1, btn_logout;
    private static final String MY_SESSION = "my_session";
    String tran_mpin="",re_tran_mpin="";

    public SetMPIN() {

    }

    public SetMPIN(MainActivity a) {
        // System.out.println("v()"+a);
        act = a;
        setmpinobj = this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(LayoutParams.FLAG_SECURE, LayoutParams.FLAG_SECURE);
        setContentView(R.layout.set_mpin);
        img_heading = (ImageView) findViewById(R.id.img_heading);
        img_heading.setBackgroundResource(R.mipmap.newlogofordash);
        var1 = (PrivateKey) getIntent().getSerializableExtra("VAR1");
        var3 = (String) getIntent().getSerializableExtra("VAR3");
        dbms = new DatabaseManagement("shree_nagari.mbank", "listMobileBanking");
        b1 = new Bundle();
        b1 = getIntent().getExtras();
        if (b1 != null) {
            strCustId = b1.getString("CUSTID");
            strMobNo = b1.getString("MOBNO");
            queOne = b1.getString("QUE1");
            queTwo = b1.getString("QUE2");
            ansOne = b1.getString("ANSWR1");
            ansTwo = b1.getString("ANSWR2");
            fromAct = b1.getString("FROMACT");
            userId = b1.getString("USERNAME");

        }
        txt_user_id = (EditText) findViewById(R.id.txt_prefer_usrname);
        lbl_chk_avail = (TextView) findViewById(R.id.check_availability);
        lyt_usrnm_txt = (LinearLayout) findViewById(R.id.lyt_usrnm_txt);
        lyt_usrnm_lbl = (LinearLayout) findViewById(R.id.lyt_usrnm_lbl);
        txt_enter_pass = (EditText) findViewById(R.id.txt_enter_pass);
        txt_re_enter_pass = (EditText) findViewById(R.id.txt_re_enter_pass);
        txt_enter_tran_pass = (EditText) findViewById(R.id.txt_enter_tran_pass);
        txt_re_enter_tran_pass = (EditText) findViewById(R.id.txt_re_enter_tran_pass);
        txt_password = (EditText) findViewById(R.id.txt_password);
        txt_new_password = (EditText) findViewById(R.id.txt_new_password);
        btn_save_pass = (Button) findViewById(R.id.btn_save_pass);
        btn_reset = (Button) findViewById(R.id.btn_reset);

        btn_home = (ImageButton) findViewById(R.id.btn_home);
        /*btn_back = (ImageButton) findViewById(R.id.btn_back);*/
        //btn_home.setImageResource(R.drawable.bank_logo);
        //btn_back.setImageResource(R.drawable.backover);
        txt_heading = (TextView) findViewById(R.id.txt_heading);
        txt_heading.setText(getString(R.string.lbl_set_mpin));
        btn_save_pass.setOnClickListener(this);
        btn_reset.setOnClickListener(this);
        btn_home1 = (ImageView) findViewById(R.id.btn_home1);
        btn_home1.setVisibility(View.INVISIBLE);
        btn_logout = (ImageView) findViewById(R.id.btn_logout);
        btn_logout.setVisibility(View.INVISIBLE);
        btn_home1.setOnClickListener(null);
        btn_logout.setOnClickListener(null);
        // btn_home.setOnClickListener(this);
        //btn_back.setOnClickListener(this);
        Log.e("SETMPIN", "fromAct......" + fromAct + "===" + userId);
        if (fromAct.equalsIgnoreCase("REGISTER") || userId.equals("NA")) {
            lbl_chk_avail.setOnClickListener(this);
        } else {
            Log.e("SETMPIN", "fromAct......" + fromAct);
            lyt_usrnm_txt.setVisibility(LinearLayout.GONE);
            lyt_usrnm_lbl.setVisibility(LinearLayout.GONE);
            txt_user_id.setText(userId);
        }
        //	SharedPreferences sp = this.getSharedPreferences(MY_SESSION,
        //			Context.MODE_PRIVATE);
        //	cust_name = sp.getString("userId", "userId");
        Cursor c1 = dbms.selectFromTable("SHAREDPREFERENCE", "", null);
        if (c1 != null) {
            while (c1.moveToNext()) {
                cust_name = c1.getString(3);
                //Log.e("userId","......"+cust_name);
                custId = c1.getString(2);
                //Log.e("custId","......"+custId);
            }
        }
    }

    public boolean isAlphaNumeric(String s) {
        String pattern = "^[a-zA-Z0-9]*$";
        Log.e("SETMPIN", "string==" + s + "===pattern match===" + s.matches(pattern));
        if (s.matches(pattern)) {
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
		/*case R.id.btn_back:
			Intent in = new Intent(this, LoginActivity.class);
			startActivity(in);
			finish();
			break;*/

            case R.id.btn_save_pass:
                userId = txt_user_id.getText().toString().trim();
                enterMPIN = txt_enter_pass.getText().toString().trim();
                reEnteredMPIN = txt_re_enter_pass.getText().toString().trim();
                Random rand = new Random();
                //txt_enter_tran_pass.setText(rand.nextInt(1000000) + "");        //removechage
                enterpass = txt_password.getText().toString().trim();
                enter_new_pass = txt_new_password.getText().toString().trim();

                enterTranMPIN=txt_enter_tran_pass.getText().toString();
                reEnteredTranMPIN=txt_re_enter_tran_pass.getText().toString();

                Log.e("Shubham", "MPIN Values"+"\n"+"enterMPIN:- "+enterMPIN+"\t"+"reEnteredMPIN:- "+reEnteredMPIN+
                        "\n"+"enterTranMPIN:- "+enterTranMPIN+"\t"+"reEnteredTranMPIN:- "+reEnteredTranMPIN);
                if (enterMPIN == null || enterMPIN.length() == 0) {
                    showAlert(getString(R.string.alert_082));
                } else if (reEnteredMPIN == null || reEnteredMPIN.length() == 0) {
                    showAlert(getString(R.string.alert_083));
                }
                /*else if (enterpass == null || enterpass.length() == 0) {
                    showAlert(getString(R.string.alert_169_01));
                } else if (enter_new_pass == null || enter_new_pass.length() == 0) {
                    showAlert(getString(R.string.alert_169_02));
                }*/
			else if (enterTranMPIN == null || enterTranMPIN.length() == 0)
			{
				showAlert(getString(R.string.alert_111));
			} 
			else if (reEnteredTranMPIN == null || reEnteredTranMPIN.length() == 0) 
			{
				showAlert(getString(R.string.alert_112));
			}
                else if (enterMPIN.length() != 6 || reEnteredMPIN.length() != 6) {
                    showAlert(getString(R.string.alert_086));
                }
//                else if (enterpass.length() != 6 || enter_new_pass.length() != 6) {
//                    showAlert(getString(R.string.alert_086_01));
//                }
			/*else if(enterTranMPIN.length()!=6||reEnteredTranMPIN.length()!=6)
			{
				showAlert(getString(R.string.alert_113));
			}*/
                else if (!enterMPIN.equals(reEnteredMPIN)) {
                    showAlert(getString(R.string.alert_081));
                }

               /* else if (!enterpass.equals(enter_new_pass)) {
                    showAlert(getString(R.string.alert_169));
                } else if (enterpass.equalsIgnoreCase(enterMPIN)) {
                    showAlert(getString(R.string.alert_124_1));
                }*/

                else if(enterMPIN.equalsIgnoreCase(enterTranMPIN))
                {
                    showAlert(getString(R.string.alert_124));
                }
			/*else if (!enterTranMPIN.equals(reEnteredTranMPIN)) {
				showAlert(getString(R.string.alert_114));
			}
			*/
			/*else if (userId == null || userId.length() == 0) 
			{
				showAlert(getString(R.string.alert_136));
			} 
			else if (userId.trim().length() <5 || userId.trim().length() > 9) 
			{
				showAlert(getString(R.string.alert_155));
			} 
			else if(!isAlphaNumeric(userId.trim()))
			{		
				 showAlert(getString(R.string.alert_164));						
			} */
                else {
                    if (fromAct.equalsIgnoreCase("REGISTER")) {
                        flag = chkConnectivity();
                        if (flag == 0) {
                            Log.e("isValidUser", "isValidUser " + isValidUser);
                            if (!isValidUser) {
                                showAlert(getString(R.string.alert_154));
								/*CallWebServiceCheckUsrNmAvailability c = new CallWebServiceCheckUsrNmAvailability();
								c.execute();*/
                            } else if (isValidUser && !validusernm.equals(txt_user_id.getText().toString().trim())) {
                                showAlert(getString(R.string.alert_154_01));
                            } else if (isValidUser && validusernm.equals(txt_user_id.getText().toString().trim())) {
                                CallWebServiceSetMPIN c = new CallWebServiceSetMPIN();
                                c.execute();
                            }
                        }
                    } else if (fromAct.equalsIgnoreCase("FORGOT")) {
                        flag = chkConnectivity();
                        if (flag == 0) {
                            CallWebServiceChangeMPIN c = new CallWebServiceChangeMPIN();
                            c.execute();
                        }
                    }
                }
                break;
            case R.id.btn_reset:
                //txt_user_id.setEnabled(true);
                txt_enter_pass.setText("");
                txt_enter_pass.requestFocus();
                txt_password.setText("");
                txt_new_password.setText("");
                txt_re_enter_pass.setText("");
                txt_enter_tran_pass.setText("");
                txt_re_enter_tran_pass.setText("");
                break;
            case R.id.check_availability:
                userId = txt_user_id.getText().toString().trim();
                if (userId == null || userId.length() == 0) {
                    showAlert(getString(R.string.alert_136));
                } else if (userId.trim().length() < 5 || userId.trim().length() > 9) {
                    showAlert(getString(R.string.alert_155));
                } else if (!isAlphaNumeric(userId.trim())) {
                    showAlert(getString(R.string.alert_164));
                } else {
                    CallWebServiceCheckUsrNmAvailability c = new CallWebServiceCheckUsrNmAvailability();
                    c.execute();
                }
                break;

            default:
                break;
        }
    }

    public void showAlert(final String str) {
        ErrorDialogClass alert = new ErrorDialogClass(this, "" + str) {

            @Override
            public void onClick(View v) {
                //Log.e("SetMPIN","SetMPIN...btn CLicked=="+R.id.btn_ok+"==="+v.getId());
                switch (v.getId()) {
                    case R.id.btn_ok:

                        if ((str.equalsIgnoreCase(respdescSetMPIN)) && (respcode.equalsIgnoreCase("0"))) {
                            post_successSetMPIN(retval);
                        } else if ((str.equalsIgnoreCase(respdescSetMPIN)) && (respcode.equalsIgnoreCase("1"))) {
                            this.dismiss();
                        } else if ((str.equalsIgnoreCase(respdescChangeMPIN)) && (respcode.equalsIgnoreCase("0"))) {
                            post_successChangeMPIN(retval);
                        } else if ((str.equalsIgnoreCase(respdescChangeMPIN)) && (respcode.equalsIgnoreCase("1"))) {
                            this.dismiss();
                        } else if ((str.equalsIgnoreCase(respdescCheckUsrNmAvailability)) && (respcode.equalsIgnoreCase("0"))) {
                            post_successUsrNmAvailability(retval);
                        } else if ((str.equalsIgnoreCase(respdescCheckUsrNmAvailability)) && (respcode.equalsIgnoreCase("1"))) {
                            this.dismiss();
                        }
                        //Log.e("SetMPIN","SetMPIN...CASE trru="+isWSCalled);
                        if (textMessage.equalsIgnoreCase(SetMPIN.this.getString(R.string.alert_070))) {
                            //Log.e("SetMPIN","SetMPIN...mpin set");
                            Intent in = new Intent(SetMPIN.this, SBKLoginActivity.class);
                            in.putExtra("VAR1", var1);
                            in.putExtra("VAR3", var3);
                            startActivity(in);
                            finish();
                        } else if (textMessage.equalsIgnoreCase(SetMPIN.this.getString(R.string.alert_103))) {
                            //Log.e("SetMPIN","SetMPIN...mpin set");
                            Bundle bObj = new Bundle();
                            Intent in = new Intent(SetMPIN.this, SBKLoginActivity.class);
                            bObj.putString("CUSTID", strCustId);
                            in.putExtra("VAR1", var1);
                            in.putExtra("VAR3", var3);
                            in.putExtras(bObj);
                            startActivity(in);
                            finish();
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

    class CallWebServiceSetMPIN extends AsyncTask<Void, Void, Void> {
        LoadProgressBar loadProBarObj = new LoadProgressBar(SetMPIN.this);
        boolean isWSCalled = false;
        JSONObject jsonObj = new JSONObject();
        String ValidationData = "";

        @Override
        protected void onPreExecute() {
            loadProBarObj.show();
            retval = "";
            respdescSetMPIN = "";
            respcode = "";
            enterMPIN = txt_enter_pass.getText().toString().trim();
            enterTranMPIN = txt_enter_tran_pass.getText().toString().trim();
            enterpass = txt_password.getText().toString().trim();
            userId = txt_user_id.getText().toString().trim();


            try {
                String location = MBSUtils.getLocation(SetMPIN.this);
                jsonObj.put("CUSTID", strCustId);
                jsonObj.put("QUE1", queOne);
                jsonObj.put("ANS1", ansOne);
                jsonObj.put("QUE2", queTwo);
                jsonObj.put("ANS2", ansTwo);
                jsonObj.put("MPIN", enterMPIN);
                jsonObj.put("TRANMPIN", enterTranMPIN);
                jsonObj.put("PASSWORD", enterpass);
                jsonObj.put("REGMOBNO", strMobNo);
                jsonObj.put("USERNAME", userId);
                jsonObj.put("IMEINO", MBSUtils.getImeiNumber(SetMPIN.this));

                jsonObj.put("USRMPIN", enterMPIN);
                jsonObj.put("SIMNO", MBSUtils.getSimNumber(SetMPIN.this));
                jsonObj.put("MOBILENO", MBSUtils.getMyPhoneNO(SetMPIN.this));
                jsonObj.put("IPADDRESS", MBSUtils.getLocalIpAddress());
                jsonObj.put("OSVERSION", Build.VERSION.RELEASE);
                jsonObj.put("LATITUDE", location.split("~")[0]);
                jsonObj.put("LONGITUDE", location.split("~")[1]);
                jsonObj.put("METHODCODE", "22");
                // ValidationData=MBSUtils.getValidationData(SetMPIN.this,jsonObj.toString());
            } catch (JSONException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
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
                    Log.e("In Login", "----------" + e);
                    retMess = getString(R.string.alert_000);
                    System.out.println("Exception");
                    cnt = 0;
                }
            } catch (Exception e) {
                retMess = getString(R.string.alert_000);
                System.out.println(e.getMessage());
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
                    Log.e("Shubham", "SET_MPIN_WS:-" + jsonObj);
	                  /*  ValidationData=xml_data[1].trim();
	                    if(ValidationData.equals(MBSUtils.getValidationData(SetMPIN.this, xml_data[0].trim())))
	                    {
				Log.e("IN return", "data :" + jsonObj.toString());*/
                    if (jsonObj.has("RESPCODE")) {
                        respcode = jsonObj.getString("RESPCODE");
                    } else {
                        respcode = "-1";
                    }
                    if (jsonObj.has("RETVAL")) {
                        retval = jsonObj.getString("RETVAL");
                        Log.e("Shubham", "RETURN_VAL_MPIN:--" + jsonObj);
                    } else {
                        retval = "";
                    }
                    if (jsonObj.has("RESPDESC")) {
                        respdescSetMPIN = jsonObj.getString("RESPDESC");
                    } else {
                        respdescSetMPIN = "";
                    }

                    if (respdescSetMPIN.length() > 0) {
                        showAlert(respdescSetMPIN);
                    } else {

                        if (retval.indexOf("SUCCESS") > -1) {

                            post_successSetMPIN(retval);


                        } else if (retval.indexOf("FAILED~") > -1) {
                            String retCode = retval.split("~")[1];
                            if (retCode.equalsIgnoreCase("1"))
                                showAlert(getString(R.string.alert_122));
                            else if (retCode.equalsIgnoreCase("2"))
                                showAlert(getString(R.string.alert_123));
                            else
                                Log.e("TAG", "alert_085-1");
                            showAlert(getString(R.string.alert_085));
                        } else {
                            Log.e("TAG", "alert_085-2");
                            showAlert(getString(R.string.alert_085));
                        }
                    }
	                   /* }
	                    else{
	                    	
	                    	MBSUtils.showInvalidResponseAlert(SetMPIN.this);	
	                    }*/
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            } else {
                retMess = getString(R.string.alert_000);
                showAlert(retMess);
            }
        }

    }

    class CallWebServiceChangeMPIN extends AsyncTask<Void, Void, Void> {

        LoadProgressBar loadProBarObj = new LoadProgressBar(SetMPIN.this);
        boolean isWSCalled = false;
        JSONObject jsonObj = new JSONObject();
        String ValidationData = "";

        @Override
        protected void onPreExecute() {
            loadProBarObj.show();
            retval = "";
            respdescChangeMPIN = "";
            respcode = "";
            enterMPIN = txt_enter_pass.getText().toString().trim();
            enterpass = txt_password.getText().toString().trim();
            enterTranMPIN = txt_enter_tran_pass.getText().toString().trim();

            try {
                String location = MBSUtils.getLocation(SetMPIN.this);
                jsonObj.put("CUSTID", strCustId);
                jsonObj.put("MPIN", enterMPIN.trim());
                jsonObj.put("TRANMPIN", enterTranMPIN.trim());
                jsonObj.put("PASSWORD", enterpass.trim());
                jsonObj.put("IMEINO", MBSUtils.getImeiNumber(SetMPIN.this));

                jsonObj.put("USRMPIN", enterMPIN);
                jsonObj.put("USRID", userId);
                jsonObj.put("SIMNO", MBSUtils.getSimNumber(SetMPIN.this));
                jsonObj.put("MOBILENO", MBSUtils.getMyPhoneNO(SetMPIN.this));
                jsonObj.put("IPADDRESS", MBSUtils.getLocalIpAddress());
                jsonObj.put("OSVERSION", Build.VERSION.RELEASE);
                jsonObj.put("LATITUDE", location.split("~")[0]);
                jsonObj.put("LONGITUDE", location.split("~")[1]);
                jsonObj.put("METHODCODE", "03");
                // ValidationData=MBSUtils.getValidationData(SetMPIN.this,jsonObj.toString());
                Log.e("Shubham", "SETMPIN_REQUEST" + jsonObj.toString());

            } catch (JSONException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
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
                    Log.e("In Login", "----------" + e);
                    retMess = getString(R.string.alert_000);
                    System.out.println("Exception");
                    cnt = 0;
                }
            } catch (Exception e) {
                retMess = getString(R.string.alert_000);
                System.out.println(e.getMessage());
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
                    Log.e("Shubham", "SETMPIN_RESPONCET" + jsonObj.toString());
                	                  /* ValidationData=xml_data[1].trim();
                	                   if(ValidationData.equals(MBSUtils.getValidationData(SetMPIN.this, xml_data[0].trim())))
                	                   {
                				Log.e("IN return", "data :" + jsonObj.toString());*/
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
                        respdescChangeMPIN = jsonObj.getString("RESPDESC");
                    } else {
                        respdescChangeMPIN = "";
                    }

                    if (respdescChangeMPIN.length() > 0) {
                        showAlert(respdescChangeMPIN);
                    } else {
                        Log.e("TAG", "retval====" + retval);
                        if (retval.indexOf("SUCCESS") > -1) {
                            post_successChangeMPIN(retval);

                        } else if (retval.indexOf("FAILED~") > -1) {
                            String retCode = retval.split("~")[1];
                            if (retCode.equalsIgnoreCase("1"))
                                showAlert(getString(R.string.alert_122));
                            else if (retCode.equalsIgnoreCase("2"))
                                showAlert(getString(R.string.alert_123));
                            else
                                Log.e("TAG", "alert_085-3");
                            showAlert(getString(R.string.alert_085));
                        } else {
                            // System.out.println("in else ***************************************");
                            Log.e("TAG", "alert_085-4");
                            showAlert(getString(R.string.alert_085));
                        }
                    }
                	                  /* }
                	                   else{
                	                	   MBSUtils.showInvalidResponseAlert(SetMPIN.this);	
                	                   }*/
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            } else {
                retMess = getString(R.string.alert_000);
                showAlert(retMess);
            }
        }

    }

    class CallWebServiceCheckUsrNmAvailability extends
            AsyncTask<Void, Void, Void> {
        JSONObject jsonObj = new JSONObject();
        LoadProgressBar loadProBarObj = new LoadProgressBar(SetMPIN.this);
        String ValidationData = "";

        @Override
        protected void onPreExecute() {
            loadProBarObj.show();
            userId = txt_user_id.getText().toString().trim();
            retval = "";
            respdescCheckUsrNmAvailability = "";
            respcode = "";

            try {
                jsonObj.put("CUSTID", strCustId);
                jsonObj.put("USERNAME", userId);
                jsonObj.put("IMEINO", MBSUtils.getImeiNumber(SetMPIN.this));
                jsonObj.put("SIMNO", MBSUtils.getSimNumber(SetMPIN.this));
                jsonObj.put("METHODCODE", "40");
                // ValidationData=MBSUtils.getValidationData(SetMPIN.this,jsonObj.toString());

            } catch (JSONException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
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
                    Log.e("In Login", "----------" + e);
                    retMess = getString(R.string.alert_000);
                    System.out.println("Exception");
                    cnt = 0;
                }
            } catch (Exception e) {
                retMess = getString(R.string.alert_000);
                System.out.println(e.getMessage());
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
				
	                 /*  ValidationData=xml_data[1].trim();
	                   if(ValidationData.equals(MBSUtils.getValidationData(SetMPIN.this, xml_data[0].trim())))
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
                        respdescCheckUsrNmAvailability = jsonObj.getString("RESPDESC");
                    } else {
                        respdescCheckUsrNmAvailability = "";
                    }

                    if (respdescCheckUsrNmAvailability.length() > 0) {
                        showAlert(respdescCheckUsrNmAvailability);
                    } else {
                        if (retval.indexOf("UNAVAILABLE") > -1) {
                            post_successUsrNmAvailability(retval);

                        } else {
                            isValidUser = true;
                            validusernm = txt_user_id.getText().toString().trim();
                            showAlert(getString(R.string.alert_usrav));
                            //txt_user_id.setEnabled(false);
                        }
                    }
	                 /*  }
	                   else{
	                	   MBSUtils.showInvalidResponseAlert(SetMPIN.this);
	                   }*/
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            } else {
                retMess = getString(R.string.alert_000);
                showAlert(retMess);
            }
        }

    }

    public void post_successSetMPIN(String retval) {
        respdescSetMPIN = "";
        respcode = "";
        WSCalled = true;
        showAlert(getString(R.string.alert_103));
        String str = "";

        String[] coulmnsAndTypes = {"CFG_CUST_ID", "varchar(10)"};
        String[] colNms = {"CFG_CUST_ID"};
        String[] val = new String[1];
        val[0] = strCustId;

        try {
            str = dbms.createTable("CONFIG", coulmnsAndTypes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e("SETMPIN", "str after create table===" + str);
        try {
            str = dbms.deleteFromTable("CONFIG", null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            str = dbms.insertIntoTable("CONFIG", 1, colNms, val);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e("SETMPIN", "str after insert===" + str);
    }

    public void post_successChangeMPIN(String retval) {

        respdescChangeMPIN = "";
        respcode = "";
        WSCalled = true;
        showAlert(getString(R.string.alert_070));
    }

    public void post_successUsrNmAvailability(String retval) {

        respdescCheckUsrNmAvailability = "";
        respcode = "";
        isValidUser = false;
        validusernm = "";
        showAlert(getString(R.string.alert_usrunav));
        txt_user_id.setText("");
    }

    public int chkConnectivity() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        try {
            State state = ni.getState();
            boolean state1 = ni.isAvailable();
            //System.out.println("state1 ---------" + state1);
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
                        showAlert(retMess);

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

            showAlert(retMess);


        } catch (Exception e) {
            Log.i("mayuri", "Exception" + e);
            flag = 1;
            retMess = getString(R.string.alert_000);
            showAlert(retMess);

        }
        return flag;
    }

    public void onBackPressed() {
        Intent in = null;
        Bundle bObj = new Bundle();
        if (fromAct.equalsIgnoreCase("REGISTER")) {
            in = new Intent(this, Register.class);
            in.putExtra("VAR1", var1);
            in.putExtra("VAR3", var3);
            startActivity(in);
            finish();
        } else if (fromAct.equalsIgnoreCase("FORGOT")) {
            in = new Intent(this, ForgotPassword.class);
            bObj.putString("FROMACT", "SETMPIN");
            in.putExtras(bObj);
            in.putExtra("VAR1", var1);
            in.putExtra("VAR3", var3);
            startActivity(in);
            finish();
        }


    }
}

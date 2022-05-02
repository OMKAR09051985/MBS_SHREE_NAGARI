package shree_nagari.mbank;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.AsyncTask;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.security.PrivateKey;

import javax.crypto.spec.SecretKeySpec;

import mbLib.CryptoClass;
import mbLib.DatabaseManagement;
import mbLib.DeviceUtils;
import mbLib.DialogBox;
import mbLib.MBSUtils;

public class ForgotPassword extends Activity implements OnClickListener {
    TextView txt_heading, txt_que_one;
    EditText txt_ans_one, txt_cust_id;
    Button btn_validate_que, btn_proceed;
    String retMess, retVal, custId, ans_one, ans_two, custCd = "", stringValue = "";
    LinearLayout secu_que_layout, cust_id_layout;
    ImageButton btn_home;//,btn_back;
    private static String NAMESPACE = "";
    private static String URL = "";
    private static String SOAP_ACTION = "";
    private static String METHOD_NAME = "";
    PrivateKey var1 = null;
    String var5 = "", var3 = "";
    SecretKeySpec var2 = null;
    private static String METHOD_NAME1 = "";
    int cnt = 0, flag = 0;
    String[] ques;
    ImageView btn_home1, btn_logout;
    int count = 0;
    DatabaseManagement dbms;
    JSONArray jsonArr;
    String qOne = "", que_one = "", fromAct = "", respcode = "", retvalweb = "", respdesc = "", retval = "", retvalwbs = "", ExistingCustomerrespdesc = "", SecQuestionrespdesc = "";
    DialogBox dbs;
    int netFlg, gpsFlg;
    ImageView img_heading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(LayoutParams.FLAG_SECURE, LayoutParams.FLAG_SECURE);
        setContentView(R.layout.forgot_password);
        if (!new DeviceUtils().isEmulator()) {
            MBSUtils.ifGooglePlayServicesValid(ForgotPassword.this);
        } else {
            MBSUtils.showAlertDialogAndExitApp(getString(R.string.alert_sup), ForgotPassword.this);
        }
        img_heading = (ImageView) findViewById(R.id.img_heading);
        img_heading.setBackgroundResource(R.mipmap.newlogofordash);
        var1 = (PrivateKey) getIntent().getSerializableExtra("VAR1");
        var3 = (String) getIntent().getSerializableExtra("VAR3");
        fromAct = (String) getIntent().getSerializableExtra("OTPACTIVITY");
        Bundle b1 = new Bundle();
        b1 = getIntent().getExtras();
        if (b1 != null) {
            fromAct = b1.getString("FROMACT");
        }
        secu_que_layout = (LinearLayout) findViewById(R.id.secu_que_layout);
        cust_id_layout = (LinearLayout) findViewById(R.id.cust_id_layout);
        txt_cust_id = (EditText) findViewById(R.id.txt_cust_id);
        txt_ans_one = (EditText) findViewById(R.id.edttxt_security_que1);

        btn_proceed = (Button) findViewById(R.id.btn_proceed);
        btn_validate_que = (Button) findViewById(R.id.btn_submit_secu_que);
        txt_heading = (TextView) findViewById(R.id.txt_heading);
        txt_que_one = (TextView) findViewById(R.id.txt_security_que1);

        btn_home = (ImageButton) findViewById(R.id.btn_home);
        //btn_home.setEnabled(false);
        /*btn_back=(ImageButton)findViewById(R.id.btn_back);*/
        //btn_home.setImageResource(R.drawable.bank_logo);
        //btn_back.setImageResource(R.drawable.backover);
        btn_home1 = (ImageView) findViewById(R.id.btn_home1);
        btn_home1.setVisibility(View.INVISIBLE);
        btn_logout = (ImageView) findViewById(R.id.btn_logout);
        btn_logout.setVisibility(View.INVISIBLE);
        if (fromAct.equalsIgnoreCase("LOGIN")) {
            txt_heading.setText(getString(R.string.reset_mpin));
        } else if (fromAct.equalsIgnoreCase("OTPACTIVITY")) {
            txt_heading.setText(getString(R.string.forgot_mpin));
        } else {
            txt_heading.setText(getString(R.string.forgot_mpin));
        }
        //btn_back.setOnClickListener(this);

        btn_proceed.setOnClickListener(this);
        btn_validate_que.setOnClickListener(this);

        btn_home1.setOnClickListener(null);
        btn_logout.setOnClickListener(null);
        dbs = new DialogBox(this);
        dbms = new DatabaseManagement("shree_nagari.mbank", "listMobileBanking");
        Cursor c1 = dbms.selectFromTable("SHAREDPREFERENCE", "", null);
        if (c1 != null) {
            while (c1.moveToNext()) {
                stringValue = c1.getString(0);
                custId = c1.getString(2);

            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
			/*case R.id.btn_back:
				Intent in=new Intent(this,LoginActivity.class);
				startActivity(in);
				finish();
				break;*/
		/*case R.id.btn_home1:
			Intent in1 = new Intent(this, SBKLoginActivity.class);
			startActivity(in1);
			finish();
			break;
		case R.id.btn_logout:	
			dbs = new DialogBox(this);
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
			dbs.get_adb().show();
			break;	*/
            case R.id.btn_proceed:
                custId = txt_cust_id.getText().toString().trim();
                if (custId.length() <= 0) {
                    showAlert(getString(R.string.alert_validcustid));
                } else {
                    //showAlert("custId==="+custId);
                    flag = chkConnectivity();
                    if (flag == 0) {
                        CallWebServiceValidateExtngCust c = new CallWebServiceValidateExtngCust();
                        c.execute();
                    }
                }
                break;
            case R.id.btn_submit_secu_que:
                ans_one = txt_ans_one.getText().toString().trim();

                if (ans_one == null || ans_one.length() == 0) {
                    showAlert(getString(R.string.alert_079));
                } else {
                    //showAlert("ans_one=="+ans_one+"==ans_two=="+ans_two);
                    flag = chkConnectivity();
                    if (flag == 0) {
                        CallWebServiceValidateSecuQue c = new CallWebServiceValidateSecuQue();
                        c.execute();
                    }
                }
                break;

            default:
                break;
        }
    }

    /*class CallWebServicelog extends AsyncTask<Void, Void, Void> {
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
                  jsonObj.put("IMEINO", MBSUtils.getImeiNumber(ForgotPassword.this));
                  jsonObj.put("SIMNO", MBSUtils.getSimNumber(ForgotPassword.this));
                  ValidationData=MBSUtils.getValidationData(ForgotPassword.this,jsonObj.toString());
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
            METHOD_NAME = "logoutWS";
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
                        if(ValidationData.equals(MBSUtils.getValidationData(ForgotPassword.this, xml_data[0].trim())))
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
                finish();
                System.exit(0);
            }
                    }
                        }
                        else{

                            MBSUtils.showInvalidResponseAlert(ForgotPassword.this);
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
        finish();
        System.exit(0);

    }
*/
    class CallWebServiceValidateExtngCust extends AsyncTask<Void, Void, Void> {

        JSONObject jsonObj = new JSONObject();
        LoadProgressBar loadProBarObj = new LoadProgressBar(ForgotPassword.this);
        boolean isWSCalled = false;
        String ValidationData = "";

        @Override
        protected void onPreExecute() {
            respcode = "";
            retvalweb = "";
            ExistingCustomerrespdesc = "";

            loadProBarObj.show();

            custId = txt_cust_id.getText().toString().trim();
            try {
                jsonObj.put("CUSTID", custId);
                jsonObj.put("IMEINO", MBSUtils.getImeiNumber(ForgotPassword.this));
                jsonObj.put("SIMNO", MBSUtils.getSimNumber(ForgotPassword.this));
                jsonObj.put("METHODCODE", "25");
                Log.e("Shubham", "CallWebServiceValidateExtngCust_Req: " + jsonObj.toString());
                // ValidationData=MBSUtils.getValidationData(ForgotPassword.this,jsonObj.toString());
            } catch (JSONException e) {
                // TODO Auto-generated catch block
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
                    // return "FAILED";
                }
            } catch (Exception e) {
                retMess = getString(R.string.alert_000);
                System.out.println(e.getMessage());
                cnt = 0;
                // return "FAILED";
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
                    Log.e("Shubham", "CallWebServiceValidateExtngCust_Respomce: " + jsonObj);
						/*ValidationData=xml_data[1].trim();
						if(ValidationData.equals(MBSUtils.getValidationData(ForgotPassword.this, xml_data[0].trim()))){
						Log.e("IN return", "data :" + jsonObj.toString());*/
                    if (jsonObj.has("RESPCODE")) {
                        respcode = jsonObj.getString("RESPCODE");
                    } else {
                        respcode = "-1";
                    }
                    if (jsonObj.has("RETVAL")) {
                        retvalweb = jsonObj.getString("RETVAL");
                    } else {
                        retvalweb = "";
                    }
                    if (jsonObj.has("RESPDESC")) {
                        ExistingCustomerrespdesc = jsonObj.getString("RESPDESC");
                    } else {
                        ExistingCustomerrespdesc = "";
                    }

                    if (ExistingCustomerrespdesc.length() > 0) {
                        showAlert(ExistingCustomerrespdesc);
                    } else {

                        if (retvalweb.indexOf("NODATA") > -1) {
                            showAlert(getString(R.string.alert_072));
                        } else if (retvalweb.indexOf("FAILED") > -1) {
                            showAlert(getString(R.string.alert_for_fail));
                        } else {
                            post_successExistingCustomer(retvalweb);
                        }


                        if (count > 0) {
                            //Log.e("HERE","===="+retVal);
                            cust_id_layout.setVisibility(cust_id_layout.INVISIBLE);
                            secu_que_layout.setVisibility(secu_que_layout.VISIBLE);
                            double y = Math.random();
                            int que_num = (int) (y * 100) % 2;
                            txt_que_one.setText(ques[que_num]);
                        }

                    }
						/*}
						else{
							MBSUtils.showInvalidResponseAlert(ForgotPassword.this);		
							
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

    public void post_successExistingCustomer(String retvalweb) {
        respcode = "";
        ExistingCustomerrespdesc = "";
        JSONArray ja = null;


        int j = 0;
        try {

            //[{"QUECD":"1","QUEDESC":"What is your favourite color? "},{"QUECD":"3","QUEDESC":"What is your birth place?"}]
            ja = new JSONArray(retvalweb);

            Log.e("JSONException ", "retvalweb=" + retvalweb);
            jsonArr = ja;
            ques = new String[ja.length()];
            for (; j < ja.length(); j++) {
                JSONObject jObj = ja.getJSONObject(j);
                ques[j] = (jObj.getString("QUEDESC"));
                count++;
            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            try {
                JSONObject jObj = ja.getJSONObject(j);
                custCd = jObj.getString("QUECD");
            } catch (JSONException je) {
                // TODO Auto-generated catch block
                je.printStackTrace();
            }
        }
        if (count > 0) {
            // Log.e("HERE","===="+retVal);
            cust_id_layout.setVisibility(cust_id_layout.INVISIBLE);
            secu_que_layout.setVisibility(secu_que_layout.VISIBLE);
            double y = Math.random();
            int que_num = (int) (y * 100) % 2;
            txt_que_one.setText(ques[que_num]);
        }

    }

    class CallWebServiceValidateSecuQue extends AsyncTask<Void, Void, Void> {
        JSONObject jsonObj = new JSONObject();
        LoadProgressBar loadProBarObj = new LoadProgressBar(ForgotPassword.this);
        boolean isWSCalled = false;
        String ValidationData = "";

        @Override
        protected void onPreExecute() {
            loadProBarObj.show();
            respcode = "";
            retvalweb = "";
            SecQuestionrespdesc = "";
            que_one = txt_que_one.getText().toString().trim();
            ans_one = txt_ans_one.getText().toString().trim();
            try {
                for (int k = 0; k < jsonArr.length(); k++) {
                    JSONObject obj = jsonArr.getJSONObject(k);
                    if (obj.getString("QUEDESC").equalsIgnoreCase(que_one))
                        qOne = obj.getString("QUECD");

                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            try {

                jsonObj.put("CUSTID", custCd);
                jsonObj.put("QUE", qOne);
                jsonObj.put("ANS", ans_one);
                jsonObj.put("IMEINO", MBSUtils.getImeiNumber(ForgotPassword.this));
                jsonObj.put("SIMNO", MBSUtils.getSimNumber(ForgotPassword.this));
                jsonObj.put("METHODCODE", "23");
                // ValidationData=MBSUtils.getValidationData(ForgotPassword.this,jsonObj.toString());

            } catch (JSONException e) {
                // TODO Auto-generated catch block
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
                    // retMess = "Problem in login. Some error occured";
                    // retMess = "Network Unavailable. Please Try Again.";
                    retMess = getString(R.string.alert_000);
                    System.out.println("Exception");
                    cnt = 0;
                    // return "FAILED";
                }
            } catch (Exception e) {
                // retMess = "Error Getting IMEI NO";
                // retMess = "Network Unavailable. Please Try Again.";
                retMess = getString(R.string.alert_000);
                System.out.println(e.getMessage());
                cnt = 0;
                // return "FAILED";
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
					if(ValidationData.equals(MBSUtils.getValidationData(ForgotPassword.this, xml_data[0].trim())))
					{
					Log.e("IN return", "data :" + jsonObj.toString());*/
                    if (jsonObj.has("RESPCODE")) {
                        respcode = jsonObj.getString("RESPCODE");
                    } else {
                        respcode = "-1";
                    }
                    if (jsonObj.has("RETVAL")) {
                        retvalweb = jsonObj.getString("RETVAL");
                    } else {
                        retvalweb = "";
                    }
                    if (jsonObj.has("RESPDESC")) {
                        SecQuestionrespdesc = jsonObj.getString("RESPDESC");
                    } else {
                        SecQuestionrespdesc = "";
                    }

                    if (SecQuestionrespdesc.length() > 0) {
                        showAlert(SecQuestionrespdesc);
                    } else {
                        if (retvalweb.indexOf("SUCCESS") > -1) {
                            post_successValidateSecuQue(retvalweb);
                        } else {
                            //System.out.println("in else ***************************************");
                            if (retvalweb.indexOf("WRONGANS") >= 0) {
                                retMess = getString(R.string.alert_087);
                            } else {
                                retMess = getString(R.string.alert_err);
                            }
                            showAlert(retMess);
                        }
                    }
					/*}
					else{
						MBSUtils.showInvalidResponseAlert(ForgotPassword.this);
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

    public void post_successValidateSecuQue(String retvalweb) {
        respcode = "";
        SecQuestionrespdesc = "";
        String decryptedAccounts = retvalweb.split("~")[1];
        Bundle bObj = new Bundle();
        Intent in = new Intent(ForgotPassword.this, OTPActivity.class);
        bObj.putString("RETVAL", decryptedAccounts);
        bObj.putString("CUSTID", custCd);
        bObj.putString("FROMACT", "FORGOT");
        in.putExtras(bObj);
        in.putExtra("VAR1", var1);
        in.putExtra("VAR3", var3);
        startActivity(in);

        finish();

    }

    public void showAlert(final String str) {
        ErrorDialogClass alert = new ErrorDialogClass(this, "" + str) {
            @Override
            public void onClick(View v) {
                //Log.e("SetMPIN","SetMPIN...btn CLicked=="+R.id.btn_ok+"==="+v.getId());
                switch (v.getId()) {
                    case R.id.btn_ok:
                        //Log.e("SetMPIN","SetMPIN...CASE trru="+WSCalled);
                        if ((str.equalsIgnoreCase(ExistingCustomerrespdesc)) && (respcode.equalsIgnoreCase("0"))) {
                            post_successExistingCustomer(retvalweb);
                        } else if ((str.equalsIgnoreCase(ExistingCustomerrespdesc)) && (respcode.equalsIgnoreCase("1"))) {
                            this.dismiss();
                        } else if ((str.equalsIgnoreCase(SecQuestionrespdesc)) && (respcode.equalsIgnoreCase("0"))) {
                            post_successValidateSecuQue(retvalweb);
                        } else if ((str.equalsIgnoreCase(SecQuestionrespdesc)) && (respcode.equalsIgnoreCase("1"))) {
                            this.dismiss();
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

    public int chkConnectivity() {
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
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

    @Override
    public void onBackPressed() {

        Intent in = new Intent(this, LoginActivity.class);
        in.putExtra("VAR1", var1);
        in.putExtra("VAR3", var3);
        startActivity(in);

        finish();
    }

}

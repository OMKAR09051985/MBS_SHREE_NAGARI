package shree_nagari.mbank;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
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
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.spec.SecretKeySpec;

import mbLib.Accountbean;
import mbLib.CryptoClass;
import mbLib.CustomDialogClass;
import mbLib.DatabaseManagement;
import mbLib.DialogBox;
import mbLib.MBSUtils;

public class EmailReg extends Fragment implements OnClickListener,
        OnFocusChangeListener {
    ImageView btn_home1, btn_logout;
    MainActivity act;
    Intent in = null;
    Button btn_email_submit;
    TextView email_status, txt_heading;
    Spinner spi_email_chng, spi_email_freq, spnr_account;
    EditText txt_email;// , txt_mpin;
    TextView cust_nm;
    String str = "", Freq = "", retMess = "", cust_name = "", email = "", freq = "", isregi = "", custId = "", AccCustId;
    int cnt = 0, cnt2 = 0, flag = 0;
    String pin = "";
    EmailReg emr;
    SharedPreferences.Editor e;
    private static final String MY_SESSION = "my_session";
    // Editor edit;
    String stringValue, retval = "", respcode = "", respdesc = "", retvalwbs = "",
            respdesc_web1 = "";
    DialogBox dbs;
    ProgressBar pb_wait;
    private static String NAMESPACE = "";
    private static String URL = "";
    private static String SOAP_ACTION = "";
    private static String METHOD_NAME = "";
    ImageView img_heading;
    ArrayList<String> arrListTemp = new ArrayList<String>();
    ImageButton spinner_btn, spinner_btn2, spinner_acc_btn, btn_home, btn_back;
    boolean wsCalled = false;
    DatabaseManagement dbms;
    PrivateKey var1 = null;
    String var5 = "", var3 = "";
    SecretKeySpec var2 = null;
    ArrayList<Accountbean> arrList1;

    public EmailReg() {
    }

    @SuppressLint("ValidFragment")
    public EmailReg(MainActivity a) {
        // //System.out.println("EmailReg()");
        act = a;
        emr = this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.email_reg, container, false);
        act.frgIndex=813;
        var1 = act.var1;
        var3 = act.var3;
        email_status = (TextView) rootView.findViewById(R.id.email_status_val);
        email_status.setText("Not Registered");
        txt_heading = (TextView) rootView.findViewById(R.id.txt_heading);
        txt_heading.setText(getString(R.string.lbl_email_registration));
        btn_home = (ImageButton) rootView.findViewById(R.id.btn_home);
        //btn_back = (ImageButton) rootView.findViewById(R.id.btn_back);
        //btn_home.setImageResource(R.drawable.ic_home_d);
        //btn_back.setImageResource(R.drawable.backover);
        //btn_back.setOnClickListener(this);
        //	btn_home.setOnClickListener(this);
        spi_email_chng = (Spinner) rootView.findViewById(R.id.spi_email_chg);
        spnr_account = (Spinner) rootView.findViewById(R.id.spnr_account);
        txt_email = (EditText) rootView.findViewById(R.id.email_val);
        // txt_mpin = (EditText) rootView.findViewById(R.id.mpin_val);
        btn_email_submit = (Button) rootView.findViewById(R.id.btn_submit_emailreg);
        spi_email_freq = (Spinner) rootView.findViewById(R.id.spi_email_freq);
        spinner_btn = (ImageButton) rootView.findViewById(R.id.spinner_btn);
        spinner_btn2 = (ImageButton) rootView.findViewById(R.id.spinner_btn2);
        spinner_acc_btn = (ImageButton) rootView
                .findViewById(R.id.spinner_acc_btn);

        img_heading = (ImageView) rootView.findViewById(R.id.img_heading);

        img_heading.setBackgroundResource(R.mipmap.other_services);

        spnr_account.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                Accountbean dataModel = arrList1.get(arg2);
                AccCustId = dataModel.getAcccustid();
                // TODO Auto-generated method stub
                flag = chkConnectivity();
                if (flag == 0) {
                    new CallWebService1().execute();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }

        });
        dbms = new DatabaseManagement("shree_nagari.mbank", "listMobileBanking");
        Cursor c1 = dbms.selectFromTable("SHAREDPREFERENCE", "", null);
        if (c1 != null) {
            while (c1.moveToNext()) {
                custId = c1.getString(2);
                stringValue = c1.getString(0);
            }
        }

        CustomeSpinnerAdapter email_change = new CustomeSpinnerAdapter(act,
                android.R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.email_chng));
		/*ArrayAdapter<String> email_change = new ArrayAdapter<String>(act,
				android.R.layout.simple_spinner_item, getResources()
						.getStringArray(R.array.email_chng));*/
        email_change
                .setDropDownViewResource(R.layout.spinner_dropdown_item);
        spi_email_chng.setAdapter(email_change);
        spinner_btn.setOnClickListener(this);

        CustomeSpinnerAdapter email_freq = new CustomeSpinnerAdapter(act,
                android.R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.email_freq));
		/*ArrayAdapter<String> email_freq = new ArrayAdapter<String>(act,
				android.R.layout.simple_spinner_item, getResources()
						.getStringArray(R.array.email_freq));*/
        email_freq
                .setDropDownViewResource(R.layout.spinner_dropdown_item);
        spi_email_freq.setAdapter(email_freq);

        spinner_btn2.setOnClickListener(this);
        spinner_acc_btn.setOnClickListener(this);

        btn_home1 = (ImageView) rootView.findViewById(R.id.btn_home1);
        btn_logout = (ImageView) rootView.findViewById(R.id.btn_logout);
        btn_home1.setOnClickListener(this);
        btn_logout.setOnClickListener(this);

        pb_wait = (ProgressBar) rootView.findViewById(R.id.pb_wait6);
        pb_wait.setMax(5);
        pb_wait.setProgress(1);
        pb_wait.setVisibility(ProgressBar.INVISIBLE);

        btn_email_submit.setOnClickListener(this);
        txt_email.setOnFocusChangeListener(this);
        // Log.e("EMAILREG", "BEFORE ADDACCOUNTS");
        addAccounts(stringValue);

        return rootView;
    }

    public void initAll() {

        // dashborad back btn
        /*
         * Button btn_back = (Button) findViewById(R.id.btn_back); // Listening
         * to back button click btn_back.setOnClickListener(new
         * View.OnClickListener() {
         *
         * @Override public void onClick(View view) { // Launching News Feed
         * Screen Intent i = new Intent(getApplicationContext(),
         * DashboardDesignActivity.class); startActivity(i);finish(); } });
         */
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.spinner_btn:
                spi_email_chng.performClick();
                break;

            case R.id.spinner_btn2:
                spi_email_freq.performClick();
                break;

            case R.id.spinner_acc_btn:
                spnr_account.performClick();
                break;
            case R.id.btn_home1:
                Intent in1 = new Intent(act, NewDashboard.class);
                in1.putExtra("VAR1", var1);
                in1.putExtra("VAR3", var3);
                startActivity(in1);
                act.finish();
                break;
            case R.id.btn_logout:
                CustomDialogClass alert = new CustomDialogClass(act, getString(R.string.lbl_exit)) {
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
			dbs.get_adb().show();*//**/
                break;
            case R.id.btn_submit_emailreg:
                String status = email_status.getText().toString();
                String currentStatus = "",
                        afterchangeStatus = "";
                String changeStatus = spi_email_chng.getSelectedItem().toString();
                if (status.equalsIgnoreCase("Registered")) {
                    currentStatus = "REG";
                } else {
                    currentStatus = "NOREG";
                }

                if (changeStatus.equalsIgnoreCase("Register")) {
                    afterchangeStatus = "REG";
                } else {
                    afterchangeStatus = "NOREG";
                }

                if (txt_email.getText().toString().trim().equals("")) {
                    retMess = getString(R.string.alert_060);
                    showAlert(retMess);
                } else if (!validEmail(txt_email.getText().toString().trim())) {
                    retMess = getString(R.string.alert_197);
                    showAlert(retMess);
                }// end if


                else {
                    // flag = chkConnectivity();
                    flag = 0;
                    if (flag == 0) {

                        {

                            new CallWebService().execute();
                            if (cnt == 1) {
                                // ////System.out.println("If Success121212121212----------------");
                                if (isregi.equalsIgnoreCase("Y")) {
                                    email_status.setText("Registered");
                                } else {
                                    email_status.setText("Not Registered");
                                }
                                // txt_email.setText(email);

                            } else {

                            }
                            spi_email_chng.requestFocus();

                        }

                    }
                }
                break;

            case R.id.btn_home:
                Intent in = new Intent(act, MainActivity.class);
                in.putExtra("VAR1", var1);
                in.putExtra("VAR3", var3);
                startActivity(in);
                act.finish();
                break;
            default:
                break;
        }
    }

    public int chkConnectivity() {
        ConnectivityManager cm = (ConnectivityManager) act
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        try {
            State state = ni.getState();
            boolean state1 = ni.isAvailable();
            // ////System.out.println("state1 ---------" + state1);
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
                // retMess = "Network Unavailable. Please Try Again.";
                retMess = getString(R.string.alert_000);
                showAlert(retMess);
            }
        } catch (NullPointerException ne) {

            // //Log.i("mayuri", "NullPointerException Exception" + ne);
            flag = 1;
            // retMess = "Can Not Get Connection. Please Try Again.";
            // retMess = "Network Unavailable. Please Try Again.";
            retMess = getString(R.string.alert_000);
            showAlert(retMess);

        } catch (Exception e) {
            // //Log.i("mayuri", "Exception" + e);
            flag = 1;
            // retMess = "Connection Problem Occured.";
            // retMess = "Network Unavailable. Please Try Again.";
            retMess = getString(R.string.alert_000);
            showAlert(retMess);
        }
        return flag;
    }

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

    public void post_successlog(String retvalwbs) {
        respcode = "";
        respdesc = "";
        act.finish();
        System.exit(0);

    }

    class CallWebService extends AsyncTask<Void, Void, Void> {
        String flag, Email, Freq, mpin;
        LoadProgressBar loadProBarObj = new LoadProgressBar(act);
        String ValidationData = "";

        JSONObject jsonObj = new JSONObject();

        protected void onPreExecute() {
            try {
                loadProBarObj.show();
                dbs = new DialogBox(act);
                Email = txt_email.getText().toString();
                if (spi_email_chng.getSelectedItemPosition() == 0) {
                    flag = "REG";
                } else if (spi_email_chng.getSelectedItemPosition() == 1) {
                    flag = "DREG";
                }
                Freq = spi_email_freq.getItemAtPosition(
                        spi_email_freq.getSelectedItemPosition()).toString();
                if (Freq.equalsIgnoreCase("Monthly")) {
                    Freq = "3";
                } else if (Freq.equalsIgnoreCase("Quarterly")) {
                    Freq = "4";
                } else if (Freq.equalsIgnoreCase("Half Yearly")) {
                    Freq = "5";
                } else if (Freq.equalsIgnoreCase("Yearly")) {
                    Freq = "6";
                }

                mpin = "";
                jsonObj.put("CUSTID", custId + "#~#" + AccCustId);
                jsonObj.put("ACCNO",
                        arrListTemp.get(spnr_account.getSelectedItemPosition()));
                jsonObj.put("FLAG", flag);
                jsonObj.put("EMAIL", Email);
                jsonObj.put("FREQ", Freq);
                jsonObj.put("IMEINO", MBSUtils.getImeiNumber(act));
                jsonObj.put("SIMNO", MBSUtils.getSimNumber(act));
                jsonObj.put("METHODCODE", "8");
                //ValidationData=MBSUtils.getValidationData(act,jsonObj.toString());
                Log.e("EMAIL11", "jsonObj======" + jsonObj);
            } catch (JSONException je) {
                je.printStackTrace();
            }

        }

        ;

        protected Void doInBackground(Void... arg0) {// doInBackground2
            String value4 = getString(R.string.namespace);
            String value5 = getString(R.string.soap_action);
            String value6 = getString(R.string.url);
            final String value7 = "callWebservice";
            try {
                Email = txt_email.getText().toString();
                int i = 0;
                String all_str = "", branch_cd, schm_cd, acnt_no;
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
                i = envelope.bodyIn.toString().trim().indexOf("=");
                var5 = var5.substring(i + 1, var5.length() - 3);

            }// end try
            catch (Exception e) {// 1
                e.printStackTrace();
                // retMess = "Network Unavailable. Please Try Again.";
                retMess = getString(R.string.alert_000);
                // //System.out.println("Exception" + e);
            }// 1
            return null;
        }// end //doInBackground2

        protected void onPostExecute(Void result) {
            loadProBarObj.dismiss();
            JSONObject jsonObj;
            try {
                String str = CryptoClass.Function6(var5, var2);
                jsonObj = new JSONObject(str.trim());
                Log.e("EMAIL11", "======" + str.trim());
				/*ValidationData=xml_data[1].trim();
				if(ValidationData.equals(MBSUtils.getValidationData(act, xml_data[0].trim())))
				{ Log.e("EMAIL11",xml_data[0]);*/

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
                    respdesc = jsonObj.getString("RESPDESC");
                } else {
                    respdesc = "";
                }


                if (respdesc.length() > 0) {
                    showAlert(respdesc);
                } else {
                    // Log.e("retval==","retval=="+retval);
                    if (retval.indexOf("SUCCESS") > -1) {
                        post_success(retval);
                    } else {
                        retMess = getString(R.string.alert_110);
                        showAlert(retMess);
                    }
                    spi_email_chng.requestFocus();

                    // showAlert(retMess);
                }
				/*} else {
					MBSUtils.showInvalidResponseAlert(act);
				}*/
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }// End CallWebservice

    public void post_success(String retval) {
        respcode = "-1";
        respdesc = "";
        // Log.e("EMAIL","onPostExecute SUCCESS");
        String retStr = retval.split("SUCCESS~")[1];
        // Log.e("retStr===","retStr="+retStr);

        if (retStr.equalsIgnoreCase("1")) {
            retMess = getString(R.string.alert_109);
        } else if (retStr.equalsIgnoreCase("2")) {
            retMess = getString(R.string.alert_061);
        } else if (retStr.equalsIgnoreCase("3")) {
            retMess = getString(R.string.alert_108);
        } else if (retStr.equalsIgnoreCase("4")) {
            retMess = getString(R.string.alert_062);
        } else if (retStr.equalsIgnoreCase("5")) {
            retMess = getString(R.string.alert_179_1);
        }
        showAlert(retMess);
        wsCalled = true;
    }

    class CallWebService1 extends AsyncTask<Void, Void, Void> {
        String flag, Email, mpin;
        LoadProgressBar loadProBarObj = new LoadProgressBar(act);
        String ValidationData = "";

        JSONObject jsonObj = new JSONObject();

        protected void onPreExecute() {
            try {
                loadProBarObj.show();
                Freq = "";
                dbs = new DialogBox(act);
                jsonObj.put("CUSTID", custId + "#~#" + AccCustId);
                jsonObj.put("ACCNO", spnr_account.getSelectedItem().toString().substring(0, 16));
                jsonObj.put("IMEINO", MBSUtils.getImeiNumber(act));
                jsonObj.put("SIMNO", MBSUtils.getSimNumber(act));
                jsonObj.put("METHODCODE", "7");
                //ValidationData=MBSUtils.getValidationData(act,jsonObj.toString());
                Log.e("EMAILonload", "jsonObj====" + jsonObj);

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
                retMess = getString(R.string.alert_000);
            }
            return null;
        }

        protected void onPostExecute(Void result) {
            loadProBarObj.dismiss();
            JSONObject jsonObj;
            try {
                String str = CryptoClass.Function6(var5, var2);
                jsonObj = new JSONObject(str.trim());

                Log.e("EMAILonload", "onPostExecute SUCCESS====" + str);
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
                    respdesc_web1 = jsonObj.getString("RESPDESC");
                } else {
                    respdesc_web1 = "";
                }


                if (respdesc_web1.length() > 0) {

                    showAlert(respdesc_web1);
                } else {
                    if (retval.indexOf("SUCCESS") > -1) {
                        loadProBarObj.dismiss();
                        post_success_web1(retval);
                    }
                }
					/*} else {
						MBSUtils.showInvalidResponseAlert(act);
					}*/

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }// End CallWebservice1

    public void post_success_web1(String retval) {
        respcode = "-1";
        respdesc_web1 = "";
        // spi_email_chng.requestFocus();

        // Log.e("EMAIL","onPostExecute SUCCESS");
        String retStr = retval.split("SUCCESS~")[1];

        if (retStr.indexOf("DREG") > -1) {
            email_status.setText(getString(R.string.lbl_not_registered));
            txt_email.setText("");
            // Log.e("EMAIL","onPostExecute Not Registered");
        } else {
            // Log.e("+","onPostExecute Registered");
            email_status.setText(getString(R.string.lbl_registered));
            String[] temp = retStr.split("REG~")[1].split("#");
            txt_email.setText(temp[0]);
            Freq = temp[1];
            // String frequency="";
            // String []
            // freArr=act.getResources().getStringArray(R.array.email_freq);
            if (Freq.equalsIgnoreCase("3")) {
                // frequency=freArr[0];
                spi_email_freq.setSelection(0);
            } else if (Freq.equalsIgnoreCase("4")) {
                // frequency="Q";
                spi_email_freq.setSelection(1);
            } else if (Freq.equalsIgnoreCase("5")) {
                // frequency="H";
                spi_email_freq.setSelection(2);
            } else if (Freq.equalsIgnoreCase("6")) {
                // frequency="Y";
                spi_email_freq.setSelection(3);
            }
        }

    }

    public void showAlert(final String str) {
        // Toast.makeText(this, str, Toast.LENGTH_LONG).show();
        ErrorDialogClass alert = new ErrorDialogClass(act, str) {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btn_ok:
                        if (wsCalled) {
                            Fragment OthrSrvcFragment = new OtherServicesMenuActivity(
                                    act);
                            //act.setTitle(getString(R.string.lbl_title_change_mpin));
                            FragmentManager fragmentManager = getFragmentManager();
                            fragmentManager
                                    .beginTransaction()
                                    .replace(R.id.frame_container, OthrSrvcFragment)
                                    .commit();
                        }

                        if ((str.equalsIgnoreCase(respdesc))
                                && (respcode.equalsIgnoreCase("0"))) {
                            post_success(retval);
                        } else if ((str.equalsIgnoreCase(respdesc))
                                && (respcode.equalsIgnoreCase("1"))) {
                            this.dismiss();
                        }
                        if ((str.equalsIgnoreCase(respdesc_web1))
                                && (respcode.equalsIgnoreCase("0"))) {
                            post_success_web1(retval);
                        } else if ((str.equalsIgnoreCase(respdesc_web1))
                                && (respcode.equalsIgnoreCase("1"))) {
                            this.dismiss();
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

    public void clearAll() {
        // txt_mpin.setText("");
        txt_email.setText("");
    }

    /*
     * @Override public void onFocusChange(View v, boolean hasFocus) { // TODO
     * Auto-generated method stub switch (v.getId()) { case R.id.email_val:
     * ////System.out.println("After focus change in emailId"); if (!hasFocus &&
     * !txt_email.getText().toString().equals("")) { if
     * (!validEmail(txt_email.getText().toString())) {
     * ////System.out.println("If invalid email");
     *
     * showAlert("Invalid E-Mail ID"); // txt_email.setText(""); //
     * txt_email.requestFocus(); } } break; default: break; }
     *
     * }
     */
    boolean validEmail(String Email) {
        // //System.out.println("Email--" + Email);
        boolean isValid = false;

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = Email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            // //System.out.println("If matches");
            isValid = true;
        }
        return isValid;
    }

    public void addAccounts(String str) {
        String acnt_inf = "", str2 = "";
        // //System.out.println("SameBankTransfer IN addAccounts()" + str);

        try {
            ArrayList<String> arrList = new ArrayList<String>();
            arrList1 = new ArrayList<>();
            String allstr[] = str.split("~");

            // ////System.out.println("SameBankTransfer Mayuri.....................:");
            // ////System.out.println("SameBankTransfer Accounts:::" +
            // allstr[0]);

            int noOfAccounts = allstr.length;
            // //System.out.println("SameBankTransfer noOfAccounts:" +
            // noOfAccounts);
            Accounts acArray[] = new Accounts[noOfAccounts];
            for (int i = 0; i < noOfAccounts; i++) {
                // ////System.out.println(i + "----STR1-----------" + str1[i]);
                // str2 = str1[i];
                // //System.out.println(i + "----STR1-----------" + allstr[i]);
                str2 = allstr[i];

                // //System.out.println(i + "str2-----------" + str2);
                acArray[i] = new Accounts(str2);
                str2 = str2.replaceAll("#", "-");
                String accType = str2.split("-")[2];

                String AccCustID = str2.split("-")[11];

                // String str2Temp = str2;
                str2 = MBSUtils.get16digitsAccNo(str2);
                if ((accType.equals("SB")) || (accType.equals("CA")) || (accType.equals("LO"))) {
                    Accountbean accountbeanobj = new Accountbean();
                    accountbeanobj.setAccountinfo(str2 + " (" + MBSUtils.getAccTypeDesc(accType) + ")");
                    accountbeanobj.setAccountNumber(str2);
                    accountbeanobj.setAcccustid(AccCustID);
                    arrList1.add(accountbeanobj);

                    arrList.add(str2 + " (" + MBSUtils.getAccTypeDesc(accType) + ")");
                    arrListTemp.add(str2);
                }
            }

            String[] debAccArr = new String[arrList.size()];
            debAccArr = arrList.toArray(debAccArr);

            ArrayAdapter<String> debAccs = new ArrayAdapter<String>(act,
                    R.layout.spinner_item, debAccArr);
			 /*CustomeSpinnerAdapter email_freq=new CustomeSpinnerAdapter(act,
					 android.R.layout.simple_spinner_item,
			 getResources().getStringArray(R.array.email_freq));*/

            debAccs.setDropDownViewResource(R.layout.spinner_dropdown_item);

            spnr_account.setAdapter(debAccs);

            // Log.i("SameBankTransfer MAYURI....", acnt_inf);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }// end addAccount

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        // TODO Auto-generated method stub

    }

}

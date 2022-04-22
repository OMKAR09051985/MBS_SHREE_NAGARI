package shree_nagari.mbank;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.TimerTask;

import javax.crypto.spec.SecretKeySpec;

import mbLib.CryptoClass;
import mbLib.CustomDialogClass;
import mbLib.CustomSpinner;
import mbLib.DatabaseManagement;
import mbLib.DialogBox;
import mbLib.MBSUtils;
import mbLib.MyThread;

public class NewDashboard extends Activity implements OnClickListener, OnTouchListener, OnItemSelectedListener {
    NewDashboard obj;
    TextView balance, cust_nm;
    Spinner select_accnt;
    ImageButton spinner_btn;
    ImageView notification, help;
    LinearLayout btn_saving_and_current, btn_term_deposite, btn_loan, btn_mini_stmt, btn_fund_tran,
            btn_manage_benf, btn_chq_related, btn_other_srvces, btn_atm, btn_recharge, btn_changempin,btn_pps_menu;
    ImageView btn_logout;
    private static final String MY_SESSION = "my_session";
    String retValStr, respcode = "", retvalwbs = "", respdesc = "", respdescgetacc = "",
            str = "", retMess = "", custid = "", retval = "";
    Editor cntx = null;
    DialogBox dbs;
    PrivateKey var1 = null;
    String var5 = "", var3 = "";
    SecretKeySpec var2 = null;
    boolean logout = false;
    private MyThread t1;
    //int timeOutInSecs = 300;
    DatabaseManagement dbms;
    int flag = 0, gpsFlg = 0;
    Accounts acArray[];
    ArrayList<String> arrListTemp = new ArrayList<String>();

    public NewDashboard() {
        obj = this;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(LayoutParams.FLAG_SECURE, LayoutParams.FLAG_SECURE);
        setContentView(R.layout.sbk_dash);
        var1 = (PrivateKey) getIntent().getSerializableExtra("VAR1");
        var3 = (String) getIntent().getSerializableExtra("VAR3");
        Log.e("DSP", "dashboard====" + var1);
        Log.e("DSP", "dashboard====" + var3);
        select_accnt = (Spinner) findViewById(R.id.select_accnt);
        balance = (TextView) findViewById(R.id.balance);
        cust_nm = (TextView) findViewById(R.id.cust_nm);
        notification = (ImageView) findViewById(R.id.notification);
        spinner_btn = (ImageButton) findViewById(R.id.spinner_btn);
        spinner_btn.setOnClickListener(this);
        notification.setOnClickListener(this);
        help = (ImageView) findViewById(R.id.help);
        help.setOnClickListener(this);
        Bundle bObj = getIntent().getExtras();
        try {
            if (bObj != null) {
                String strFromAct = bObj.getString("FROMACT");
                Log.e("SHubham", "strFromAct:-" + strFromAct);
                if (!strFromAct.equalsIgnoreCase("LOGIN")) {
                    new CallWebService_getAccounts().execute();
                }
            }
        } catch (Exception e) {
            Log.e("newDASHBOARD", "newDASHBOARD1" + e);
        }

        dbms = new DatabaseManagement("shree_nagari.mbank", "listMobileBanking");
        Cursor c1 = dbms.selectFromTable("SHAREDPREFERENCE", "", null);
        if (c1 != null) {
            while (c1.moveToNext()) {
                retValStr = c1.getString(0);
                custid = c1.getString(2);
            }
        }
        String[] arr = retValStr.split("~");
        String name = arr[0].split("#")[4];
        name = name.replace("?", " ");
        cust_nm.setText(name);
        addAccounts(retValStr);
        select_accnt.setOnItemSelectedListener(this);

        btn_saving_and_current = (LinearLayout) findViewById(R.id.btn_saving_and_current);
        btn_term_deposite = (LinearLayout) findViewById(R.id.btn_term_deposite);
        btn_loan = (LinearLayout) findViewById(R.id.btn_loan);
        btn_mini_stmt = (LinearLayout) findViewById(R.id.btn_mini_stmt);
        btn_fund_tran = (LinearLayout) findViewById(R.id.btn_fund_tran);
        btn_manage_benf = (LinearLayout) findViewById(R.id.btn_manage_benf);
        btn_chq_related = (LinearLayout) findViewById(R.id.btn_chq_related);
        btn_other_srvces = (LinearLayout) findViewById(R.id.btn_other_srvces);
        //btn_atm = (LinearLayout) findViewById(R.id.btn_atm);
        btn_logout = (ImageView) findViewById(R.id.btn_logout);
        btn_recharge = (LinearLayout) findViewById(R.id.btn_recharge);
        btn_changempin = (LinearLayout) findViewById(R.id.btn_changempin);
		btn_pps_menu = (LinearLayout) findViewById(R.id.btn_pps_menu);

        btn_recharge.setOnClickListener(this);
        btn_saving_and_current.setOnClickListener(this);
        btn_term_deposite.setOnClickListener(this);
        btn_loan.setOnClickListener(this);
        btn_mini_stmt.setOnClickListener(this);
        btn_fund_tran.setOnClickListener(this);
        btn_manage_benf.setOnClickListener(this);
        btn_chq_related.setOnClickListener(this);
        btn_other_srvces.setOnClickListener(this);
        //btn_atm.setOnClickListener(this);
        btn_changempin.setOnClickListener(this);
		btn_pps_menu.setOnClickListener(this);

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomDialogClass alert = new CustomDialogClass(obj, getString(R.string.lbl_exit)) {
                    @SuppressLint("NonConstantResourceId")
                    @Override
                    public void onClick(View v) {
                        switch (v.getId()) {
                            case R.id.btn_ok:
                                flag = chkConnectivity();
                                if (flag == 0) {
                                    new CallWebService().execute();
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
				/*dbs = new DialogBox(obj);
				dbs.get_adb().setMessage(getString(R.string.lbl_exit));
				dbs.get_adb().setPositiveButton("Yes",new DialogInterface.OnClickListener() 
				{
					@Override
					public void onClick(DialogInterface arg0, int arg1) 
					{
						flag = chkConnectivity();
						if (flag == 0)
						{
							CallWebService c=new CallWebService();
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
            }
        });
        t1 = new MyThread(Integer.parseInt(getString(R.string.timeOutInSecs)), this, var1, var3);
        t1.start();
        int fragIndex = -1;
    }

    public void addAccounts(String str) {
        try {
            ArrayList<String> arrList = new ArrayList<String>();
            String allstr[] = str.split("~");
            //Log.e("DASH", "str==" + str);
            int noOfAccounts = allstr.length;
            Log.e("DASH", "noOfAccounts==" + noOfAccounts);
            acArray = new Accounts[noOfAccounts];
            int j = 0;
            for (int i = 0; i < noOfAccounts; i++) {
                String str2 = allstr[i];
                String str3 = allstr[i];
                String tempStr = str2;
                //Log.e("DASH", "str2==" + str2);
                str2 = str2.replaceAll("#", "-");
                String accType = str2.split("-")[2];
                String oprcd = str2.split("-")[7];
                String str2Temp = str2;

               // Log.e("DASH", "BEFORE account added==" + j + "====" + acArray.length);
                if (accType.equals("SB")) {
                   // Log.e("DASH", "str2==" + str2);
                    acArray[j] = new Accounts(str3);
                   // Log.e("DASH", "account added");
                    acArray[j++] = new Accounts(tempStr);
                    str2 = MBSUtils.get16digitsAccNo(str2);
                    arrList.add(str2 + " (" + MBSUtils.getAccTypeDesc(accType) + ")");
                    arrListTemp.add(str2Temp);
                }
            }

            for (int i = 0; i < noOfAccounts; i++) {
                String str2 = allstr[i];
                String str3 = allstr[i];
                String tempStr = str2;

                str2 = str2.replaceAll("#", "-");
                String accType = str2.split("-")[2];
                String oprcd = str2.split("-")[7];
                String str2Temp = str2;


                if (!accType.equals("SB")) {
                    acArray[j] = new Accounts(str3);
                  //  Log.e("DASH", "account added");
                    acArray[j++] = new Accounts(tempStr);
                    str2 = MBSUtils.get16digitsAccNo(str2);
                    arrList.add(str2 + " (" + MBSUtils.getAccTypeDesc(accType) + ")");
                    arrListTemp.add(str2Temp);
                }
            }

            String[] debAccArr = new String[arrList.size()];
            debAccArr = arrList.toArray(debAccArr);

            CustomSpinner debAccs = new CustomSpinner(NewDashboard.this, R.layout.spinner_item, debAccArr);
            debAccs.setDropDownViewResource(R.layout.spinner_dropdown_item);
            select_accnt.setAdapter(debAccs);

            Accounts selectedDrAccount = acArray[0];
            String balStr = selectedDrAccount.getBalace();
            String drOrCr = "";
            float amt = Float.parseFloat(balStr);
            Log.e("DSP", "balStr111====" + balStr);
            if (amt > 0) {
                drOrCr = " Cr";
            } else if (amt < 0) {
                drOrCr = " Dr";
                balStr = balStr.substring(1);
                Log.e("DSP", "balStr222====" + balStr);
            }

            if (balStr.indexOf(".") == -1)
                balStr = balStr + ".00";
            balStr = balStr + drOrCr;
            balance.setText(getString(R.string.lbl_available_balance) + " " + balStr);

        } catch (Exception e) {
            System.out.println("" + e);
            e.printStackTrace();
        }

    }// end addAccount

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        t1.sec = -1;
        System.gc();
    }

    @SuppressLint("NonConstantResourceId")
    @Override

    public void onClick(View v) {

        int fragIndex = -1;
        Fragment fragment = null;

        switch (v.getId()) {
            case R.id.spinner_btn:
                select_accnt.performClick();
                break;
            case R.id.btn_saving_and_current:
                fragIndex = 0;
                break;
            case R.id.btn_term_deposite:
                fragIndex = 1;
                break;
            case R.id.btn_loan:
                fragIndex = 2;
                break;
            case R.id.btn_mini_stmt:
                fragIndex = 3;
                break;
            case R.id.btn_fund_tran:
                fragIndex = 4;
                break;
            case R.id.btn_manage_benf:
                fragIndex = 5;
                break;
            case R.id.btn_chq_related:
                fragIndex = 6;
                break;
            case R.id.btn_other_srvces:
                fragIndex = 7;
                break;
			/*case R.id.btn_atm:
				fragIndex=8;
				break;*/
            case R.id.btn_recharge:
                fragIndex = 9;
                break;
            case R.id.notification:
                fragIndex = 9999;
                break;
            case R.id.help:
                fragIndex = 9990;
                break;
            case R.id.btn_changempin:
                fragIndex = 9991;
                break;

            case R.id.btn_pps_menu:
                fragIndex = 10;
                break;

            default:
                break;
        }

        if (fragIndex != -1 && fragIndex != 9) {
            try {
                Intent in = new Intent(this, MainActivity.class);
                Bundle b1 = new Bundle();
                //Log.e("Shubham", "NewDashboard_Fragindex: " + fragIndex);
                b1.putInt("FRAGINDEX", fragIndex);
                in.putExtras(b1);
                in.putExtra("VAR1", var1);
                in.putExtra("VAR3", var3);
                startActivity(in);
                finish();
            } catch (Exception ex) {
                Log.e("NewDASHBOARD", "NewDASHBOARD:-" + ex);
            }
        } else {
            Toast.makeText(NewDashboard.this, "Comming Soon!", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // TODO Auto-generated method stub
        t1.sec = Integer.parseInt(getString(R.string.timeOutInSecs));
        return false;
    }

    private class LogOutTimerTask extends TimerTask {

        @Override
        public void run() {
            logout = true;
            //redirect user to login screen

        }
    }

    class CallWebService_getAccounts extends AsyncTask<Void, Void, Void> {
        JSONObject jsonObj = new JSONObject();
        String retVal = "";
        String ValidationData = "";

        @Override
        protected void onPreExecute() {
            try {
                respcode = "";
                retvalwbs = "";

                respdescgetacc = "";
                String imeiNo = MBSUtils.getImeiNumber(NewDashboard.this);
                jsonObj.put("CUSTID", custid);
                jsonObj.put("IMEINO", imeiNo);
                jsonObj.put("SIMNO", MBSUtils.getSimNumber(NewDashboard.this));
                jsonObj.put("METHODCODE", "54");
                //ValidationData=MBSUtils.getValidationData(NewDashboard.this,jsonObj.toString());
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

                System.out.println(e.getMessage());

            }
            return null;
        }

        @Override
        protected void onPostExecute(final Void result) {


            JSONObject jsonObj;
            try {

                String str = CryptoClass.Function6(var5, var2);
                jsonObj = new JSONObject(str.trim());
				/*ValidationData=xml_data[1].trim();
				if(ValidationData.equals(MBSUtils.getValidationData(NewDashboard.this, xml_data[0].trim())))
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
                    respdescgetacc = jsonObj.getString("RESPDESC");
                } else {
                    respdescgetacc = "";
                }

                if (respdescgetacc.length() > 0) {
                    showAlert(respdescgetacc);
                } else {
                    if (retvalwbs.indexOf("FAILED") > -1) {

                        Log.e("FAILED= ", "FAILED=");
                    } else {
                        if (retvalwbs.indexOf("SUCCESS") > -1) {
                            post_successgetacc(retvalwbs);
                        }
                    }//else
                }
				/*}
				else{
					MBSUtils.showInvalidResponseAlert(NewDashboard.this);	
				}*/
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public void showAlert(final String str) {
        ErrorDialogClass alert = new ErrorDialogClass(obj, "" + str) {
            @Override
            public void onClick(View v) {
                //Log.e("SetMPIN","SetMPIN...btn CLicked=="+R.id.btn_ok+"==="+v.getId());
                switch (v.getId()) {
                    case R.id.btn_ok:
                        //Log.e("SetMPIN","SetMPIN...CASE trru="+WSCalled);
                        if ((str.equalsIgnoreCase(respdesc)) && (respcode.equalsIgnoreCase("0"))) {
                            post_success(retvalwbs);
                        } else if ((str.equalsIgnoreCase(respdesc)) && (respcode.equalsIgnoreCase("1"))) {
                            this.dismiss();
                        }
                        if ((str.equalsIgnoreCase(respdescgetacc)) && (respcode.equalsIgnoreCase("0"))) {
                            post_successgetacc(retvalwbs);
                        } else if ((str.equalsIgnoreCase(respdescgetacc)) && (respcode.equalsIgnoreCase("1"))) {
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

    public void post_success(String retvalwbs) {
        respcode = "";
        respdesc = "";
        finish();
        System.exit(0);

    }

    public void post_successgetacc(String retvalwbs) {
        respcode = "";
        respdescgetacc = "";
        String decryptedAccounts = retval.split("SUCCESS~")[1];
        if (!decryptedAccounts.equals("FAILED#")) {
            String splitstr[] = decryptedAccounts.split("!@!");
            String oldversion = splitstr[5];
            if (oldversion.equals("OLDVERSION")) {
                //showlogoutAlert(getString(R.string.alert_oldversionupdate));
            } else {
                Bundle b = new Bundle();
                String accounts = splitstr[0];
                String mobno = splitstr[1];
                String tranMpin = splitstr[2];
                custid = splitstr[3];
                String userId = splitstr[4];
                System.out.println("mobno :" + mobno);

                String[] columnNames = {"retval_str", "cust_name", "cust_id", "user_id", "cust_mobno"};
                String[] columnValues = {accounts, "", custid, userId, mobno};

                dbms.deleteFromTable("SHAREDPREFERENCE", "", null);
                dbms.insertIntoTable("SHAREDPREFERENCE", 5, columnNames, columnValues);
            }
        } else {
            retMess = getString(R.string.alert_prblm_login);
        }

    }

    class CallWebService extends AsyncTask<Void, Void, Void> {
        LoadProgressBar loadProBarObj = new LoadProgressBar(NewDashboard.this);
        JSONObject jsonObj = new JSONObject();
        String ValidationData = "";

        @Override
        protected void onPreExecute() {
            try {
                loadProBarObj.show();
                respcode = "";
                retvalwbs = "";
                respdesc = "";
                jsonObj.put("CUSTID", custid);
                jsonObj.put("IMEINO", MBSUtils.getImeiNumber(NewDashboard.this));
                jsonObj.put("SIMNO", MBSUtils.getSimNumber(NewDashboard.this));
                jsonObj.put("METHODCODE", "29");
                Log.e("Shubham", "CallWebService_Exit_Request: "+jsonObj.toString() );
                //ValidationData=MBSUtils.getValidationData(NewDashboard.this,jsonObj.toString());

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
                Log.e("Shubham", "CallWebService_Exit_Response: "+var5);

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
            loadProBarObj.dismiss();
            try {
                String str = CryptoClass.Function6(var5, var2);
                jsonObj = new JSONObject(str.trim());
    					/*ValidationData=xml_data[1].trim();
    					if(ValidationData.equals(MBSUtils.getValidationData(NewDashboard.this, xml_data[0].trim())))
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
    						
    						MBSUtils.showInvalidResponseAlert(NewDashboard.this);	
    					}*/
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }

    public int chkConnectivity() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        try {
            State state = ni.getState();
            boolean state1 = ni.isAvailable();
            if (state1) {
                switch (state) {
                    case CONNECTED:
                        if (ni.getType() == ConnectivityManager.TYPE_MOBILE
                                || ni.getType() == ConnectivityManager.TYPE_WIFI) {

                            gpsFlg = 1;
                            flag = 0;

                        }
                        break;
                    case DISCONNECTED:
                        //Log.i("6666", "6666");
                        flag = 1;
                        // retMess = "Network Disconnected. Please Try Again.";
                        retMess = getString(R.string.alert_000);
                        dbs = new DialogBox(this);
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
                        //Log.i("7777", "7777");
                        flag = 1;
                        // retMess = "Network Unavailable. Please Try Again.";
                        retMess = getString(R.string.alert_000);
                        // setAlert();

                        dbs = new DialogBox(this);
                        dbs.get_adb().setMessage(retMess);
                        dbs.get_adb().setPositiveButton("Ok",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface arg0,
                                                        int arg1) {
                                        arg0.cancel();
                                        Intent in = null;
                                        in = new Intent(getApplicationContext(),
                                                SBKLoginActivity.class);
                                        in.putExtra("VAR1", var1);
                                        in.putExtra("VAR3", var3);
                                        startActivity(in);
                                        finish();
                                    }
                                });
                        dbs.get_adb().show();
                        break;
                }
            } else {
                //Log.i("8888", "8888");
                flag = 1;
                // retMess = "Network Unavailable. Please Try Again.";
                retMess = getString(R.string.alert_000);
                // setAlert();

                dbs = new DialogBox(this);
                dbs.get_adb().setMessage(retMess);
                dbs.get_adb().setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                                arg0.cancel();
                                Intent in = null;
                                in = new Intent(getApplicationContext(),
                                        SBKLoginActivity.class);
                                in.putExtra("VAR1", var1);
                                in.putExtra("VAR3", var3);
                                startActivity(in);
                                finish();
                            }
                        });
                dbs.get_adb().show();
            }
        } catch (NullPointerException ne) {

            Log.i("mayuri", "NullPointerException Exception" + ne);
            flag = 1;
            // retMess = "Network Unavailable. Please Try Again.";
            retMess = getString(R.string.alert_000);
            // setAlert();

            dbs = new DialogBox(this);
            dbs.get_adb().setMessage(retMess);
            dbs.get_adb().setPositiveButton("Ok",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                            arg0.cancel();
                            Intent in = null;
                            in = new Intent(getApplicationContext(),
                                    SBKLoginActivity.class);
                            in.putExtra("VAR1", var1);
                            in.putExtra("VAR3", var3);
                            startActivity(in);
                            finish();
                        }
                    });
            dbs.get_adb().show();

        } catch (Exception e) {
            Log.i("mayuri", "Exception" + e);
            flag = 1;
            // retMess = "Network Unavailable. Please Try Again.";
            retMess = getString(R.string.alert_000);
            // setAlert();

            dbs = new DialogBox(this);
            dbs.get_adb().setMessage(retMess);
            dbs.get_adb().setPositiveButton("Ok",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                            arg0.cancel();
                            Intent in = null;
                            in = new Intent(getApplicationContext(),
                                    SBKLoginActivity.class);
                            in.putExtra("VAR1", var1);
                            in.putExtra("VAR3", var3);
                            startActivity(in);
                            finish();
                        }
                    });
            dbs.get_adb().show();
        }
        return flag;
    }


    public void onBackPressed() {
        showlogoutAlert(getString(R.string.lbl_do_you_want_to_exit));
    }

    public void showlogoutAlert(String str) {
        CustomDialogClass alert = new CustomDialogClass(obj, str) {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                switch (v.getId()) {
                    case R.id.btn_ok:
                        flag = chkConnectivity();
                        if (flag == 0) {
                            CallWebService c = new CallWebService();
                            c.execute();
                        }
                        break;

                    case R.id.btn_cancel:

                        this.dismiss();
                        break;
                    default:
                        break;
                }
            }
        };
        alert.show();
    }

    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String str = select_accnt.getSelectedItem().toString();
        //Log.e("arg2= ","position="+position);
        str = arrListTemp.get(select_accnt.getSelectedItemPosition());
        String[] debitAc = str.split("-");
        System.out.println("account 1:" + debitAc[0]);// 5
        System.out.println("account 2:" + debitAc[1]);// 101
        System.out.println("account 4:" + debitAc[3]);// 7

        String mmid = debitAc[8];
        //Log.e("MMID","MMID  "+mmid);
        if (mmid.equals("NA")) {
            //showAlert( getString(R.string.lbl_mmid_msg));
        }

        Accounts selectedDrAccount = acArray[select_accnt.getSelectedItemPosition()];
        String balStr = selectedDrAccount.getBalace();
        String drOrCr = "";
        float amt = Float.parseFloat(balStr);
        if (amt > 0)
            drOrCr = " Cr";
        else if (amt < 0) {
            drOrCr = " Dr";
            balStr = balStr.substring(1);
        }
        if (balStr.indexOf(".") == -1)
            balStr = balStr + ".00";
        balStr = balStr + drOrCr;
        balance.setText(getString(R.string.lbl_available_balance) + " " + balStr);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // TODO Auto-generated method stub

    }
}

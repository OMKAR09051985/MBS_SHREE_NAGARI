package shree_nagari.mbank;

import java.security.PrivateKey;
import java.util.ArrayList;

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
import android.app.Fragment;
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
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

public class ChkBookRequest extends Fragment implements OnClickListener {
    MainActivity act;
    Intent in = null;
    Button btn_submit;
    Spinner spi_account;
    Spinner noof_chkbook;
    ImageButton spinner_btn_acc, btn_home;//,btn_back;
    ImageButton spinenr_btn_no_of_pages;
    TextView cust_nm;
    ImageView btn_home1, btn_logout;
    String acnt_inf, all_acnts;
    String str = "", retMess = "", custId = "", retvalwbs = "",
            cust_name = "", reTval = "", regno = "", imeino = "", respcode = "", retval = "", respdesc = "", AccCustId;
    int cnt = 0, flag = 0;
    private static final String MY_SESSION = "my_session";
    Editor e;
    String stringValue;
    String str2 = "";
    DialogBox dbs;
    ProgressBar pb_wait;
    TextView txt_heading;
    ImageView img_heading;
    DatabaseManagement dbms;
    private static String NAMESPACE = "";
    private static String URL = "";
    private static String SOAP_ACTION = "";
    private static String METHOD_NAME = "";
    ChkBookRequest chqBkReq;
    ArrayList<String> arrListTemp = new ArrayList<String>();
    PrivateKey var1 = null;
    String var5 = "", var3 = "";
    SecretKeySpec var2 = null;
    ArrayList<Accountbean> arrList1;

    public ChkBookRequest() {
    }

    @SuppressLint("ValidFragment")
    public ChkBookRequest(MainActivity a) {
        act = a;
        chqBkReq = this;
        dbs = new DialogBox(act);
        imeino = MBSUtils.getImeiNumber(act);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.chk_book_request, container,
                false);
        var1 = act.var1;
        var3 = act.var3;
        dbms = new DatabaseManagement("shree_nagari.mbank", "listMobileBanking");
        Cursor c1 = dbms.selectFromTable("SHAREDPREFERENCE", "", null);//("select * from ", null);
        if (c1 != null) {
            while (c1.moveToNext()) {
                stringValue = c1.getString(0);
                //Log.e("retvalstr","stringValuestringValue;- "+stringValue);
                custId = c1.getString(2);
                //Log.e("custId","......"+custId);
            }
        }
        btn_submit = (Button) rootView.findViewById(R.id.btn_submit_cbr);
        // btn_submit.setOnClickListener(this);
        spi_account = (Spinner) rootView.findViewById(R.id.spi_accounts);
        noof_chkbook = (Spinner) rootView.findViewById(R.id.txt_noof_chkbook);
        txt_heading = (TextView) rootView.findViewById(R.id.txt_heading);
        img_heading = (ImageView) rootView.findViewById(R.id.img_heading);
        // all_acnts = getAccounts();
        btn_home = (ImageButton) rootView.findViewById(R.id.btn_home);
        /* btn_back=(ImageButton)rootView.findViewById(R.id.btn_back);*/
        //btn_home.setImageResource(R.drawable.ic_home_d);
        // btn_back.setImageResource(R.drawable.backover);
        //btn_back.setOnClickListener(this);
        btn_home.setOnClickListener(this);

        btn_home1 = (ImageView) rootView.findViewById(R.id.btn_home1);
        btn_logout = (ImageView) rootView.findViewById(R.id.btn_logout);
        btn_home1.setOnClickListener(this);
        btn_logout.setOnClickListener(this);
        all_acnts = stringValue;
        txt_heading.setText(getString(R.string.chq_bk_req));
        img_heading.setBackgroundResource(R.mipmap.checkbkstatus);
        addAccounts(all_acnts);
        spinenr_btn_no_of_pages = (ImageButton) rootView.findViewById(R.id.spinner_btn2);
        spinenr_btn_no_of_pages.setOnClickListener(chqBkReq);
        pb_wait = (ProgressBar) rootView.findViewById(R.id.pb_wait3);
        pb_wait.setMax(10);
        pb_wait.setProgress(1);
        pb_wait.setVisibility(ProgressBar.INVISIBLE);
        // cust_name = getCustName(str);

        //btn_submit.setTypeface(tf_calibri);
        btn_submit.setOnClickListener(this);
        spi_account.requestFocus();

        spinner_btn_acc = (ImageButton) rootView.findViewById(R.id.spinner_btn);
        spinner_btn_acc.setOnClickListener(chqBkReq);

        spi_account.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                // TODO Auto-generated method stub
                String accno = spi_account.getSelectedItem().toString();

                ArrayList<String> arrList = new ArrayList<String>();
                if (accno.indexOf("Savings") > -1) {
                    //String[] arrList = { "15", "60" };
                    arrList.add("15");
                    arrList.add("30");
                } else if (accno.indexOf("Current") > -1) {
                    arrList.add("30");
                    arrList.add("60");
                }

		/*CustomeSpinnerAdapter noOfPages = new CustomeSpinnerAdapter(act,
				R.layout.spinner_layout, arrList);*/
                ArrayAdapter<String> noOfPages = new ArrayAdapter<String>(act, R.layout.spinner_item, arrList);
                /*CustomeSpinnerAdapter noOfPages = new CustomeSpinnerAdapter(act,android.R.layout.simple_spinner_item, arrList);*/
                noOfPages.setDropDownViewResource(R.layout.spinner_dropdown_item);
                noof_chkbook.setAdapter(noOfPages);

                if (arg0.getItemAtPosition(arg2).toString().toLowerCase().indexOf("saving") > -1) {
                    noof_chkbook.setSelection(0);
                } else {
                    noof_chkbook.setSelection(1);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });
        spinenr_btn_no_of_pages.setEnabled(false);
        noof_chkbook.setEnabled(false);
        return rootView;
    }

    public String getAccounts() {
        Bundle bnd = act.getIntent().getExtras();
        String str = bnd.getString("accounts");
        //System.out.println("accounts--------------" + str);
        return str;
    }

    public void addAccounts(String str) {
        //System.out.println("addAccounts@ChkbkReq" + str);

        try {
            ArrayList<String> arrList = new ArrayList<String>();
            arrList1 = new ArrayList<>();
            String allstr[] = str.split("~");
            /*
             * SUCCESS~ 5#101#SB#7#KADEKAR KAVITA KIRAN~~ 5#101#SB#2#KADEKAR
             * KAVITA KIRAN~ 5#101#SB#3#KADEKAR KAVITA KIRAN~ 5#101#SB#6#KADEKAR
             * KAVITA KIRAN
             *
             * old Accounts: 5#101#SB#1#KADEKAR KAVITA KIRAN,-,
             * 5#101#SB#2#KADEKAR KAVITA KIRAN,-, 5#101#SB#3#Mrs. KADEKAR KAVITA
             * KIRAN,-, 5#101#SB#6#KADEKAR DIGAMBAR HARI / KAVITA KIRAN,-,
             * 5#101#SB#7#DESHPANDE JAGGANATH SHANKAR / KADEKAR KAVITA K.,-,
             */
            // String str1[] = allstr[1].split(",-,");
            // int noOfAccounts = str1.length;
            int noOfAccounts = allstr.length;

            Accounts acArray[] = new Accounts[noOfAccounts];
            //for (int i = 1; i < noOfAccounts; i++) {
            for (int i = 0; i < noOfAccounts; i++) {
                // System.out.println(i + "----STR1-----------" + str1[i]);
                // str2 = str1[i];
                str2 = allstr[i];
                acArray[i] = new Accounts(str2);
                str2 = str2.replaceAll("#", "-");
                String accType = str2.split("-")[2];
                String oprcd = str2.split("-")[7];

                String AccCustID = str2.split("-")[11];

                String str2Temp = str2;
                str2 = MBSUtils.get16digitsAccNo(str2);
                if (((accType.equals("SB")) || (accType.equals("CA"))
                        || (accType.equals("LO"))) && oprcd.equalsIgnoreCase("O")) {
                    Accountbean accountbeanobj = new Accountbean();
                    accountbeanobj.setAccountinfo(str2 + " (" + MBSUtils.getAccTypeDesc(accType) + ")");
                    accountbeanobj.setAccountNumber(str2);
                    accountbeanobj.setAcccustid(AccCustID);
                    arrList1.add(accountbeanobj);

                    arrList.add(str2 + " (" + MBSUtils.getAccTypeDesc(accType) + ")");
                    arrListTemp.add(str2);
                }
            }

            /*
             * ArrayAdapter<String> arrAdpt = new ArrayAdapter<String>(this,
             * android.R.layout.simple_spinner_item, arrList);
             * arrAdpt.setDropDownViewResource
             * (android.R.layout.simple_spinner_dropdown_item);
             * spi_account.setAdapter(arrAdpt); Log.i("BalanceEnquiry ",
             * "Exiting from adding accounts");
             */

            String[] accArr = new String[arrList.size()];
//            for (int i=0;i<=accArr.length; i++) {
//                System.out.println("This is Sysout:- " + accArr[i]);
//            }
            accArr = arrList.toArray(accArr);
			/*CustomeSpinnerAdapter accs = new CustomeSpinnerAdapter(act,
					R.layout.spinner_layout, accArr);
			spi_account.setAdapter(accs);*/
            ArrayAdapter<String> debAccs = new ArrayAdapter<String>(act, R.layout.spinner_item, accArr);
            /*CustomeSpinnerAdapter debAccs = new CustomeSpinnerAdapter(act,android.R.layout.simple_spinner_item, accArr);*/
            debAccs.setDropDownViewResource(R.layout.spinner_dropdown_item);

            spi_account.setAdapter(debAccs);

            acnt_inf = spi_account.getItemAtPosition(spi_account.getSelectedItemPosition()).toString();
            AccCustId = arrList1.get(spi_account.getSelectedItemPosition()).getAcccustid();
            //Log.e("@ChkBkReq", acnt_inf);

        } catch (Exception e) {
            Log.e("ExceptionaddAccounts: ", e.getClass() + " " + e.getMessage());
        }
        /*
         * try { ArrayList<String> arrList = new ArrayList<String>();
         *
         * String in_str[] = str.split("#"); for (int i = 0; i < in_str.length;
         * i++) { arrList.add(in_str[i]);
         * System.out.println("in_str[j]--------------------" + in_str[i]); }
         *
         * ArrayAdapter<String> arrAdpt = new ArrayAdapter<String>(this,
         * android.R.layout.simple_spinner_item, arrList);
         * spi_account.setAdapter(arrAdpt); Log.i("STMT ACT",
         * "Exiting from adding accounts");
         *
         * acnt_inf = spi_account.getItemAtPosition(
         * spi_account.getSelectedItemPosition()).toString();
         * Log.i("MAYURI....",acnt_inf); } catch (Exception e) { // TODO: handle
         * exception
         *
         * }
         */
    }

    public void onClick(View v) {
        // TODO Auto-generated method stub
        // pb_wait.setVisibility(ProgressBar.VISIBLE);
        switch (v.getId()) {
		/*case R.id.btn_back:
			Fragment fragment = new ChequeMenuActivity(act);
			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.frame_container, fragment).commit();
			act.frgIndex=7;
			break;*/
            case R.id.btn_home:
                Intent in = new Intent(act, NewDashboard.class);
                in.putExtra("VAR1", var1);
                in.putExtra("VAR3", var3);
                startActivity(in);
                act.finish();
                break;
            case R.id.btn_submit_cbr:
                if (noof_chkbook.getSelectedItem().toString().equals("")) {
                    /*
                     * Toast.makeText(this, "Please Enter No. Of Pages",
                     * Toast.LENGTH_LONG).show();
                     */
                    retMess = "Please Enter No. Of Pages";
                    showAlert(retMess);
                    noof_chkbook.requestFocus();
                } else {

                    // flag = chkConnectivity();
                    // if (flag == 0)
                    {
                        new CallWebService().execute();

                    }
                }
                break;

            case R.id.spinner_btn:
                spi_account.performClick();
                break;

            case R.id.spinner_btn2:
                noof_chkbook.performClick();
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
			dbs.get_adb().show();*/
                break;
            default:
                break;
        }

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

    public int chkConnectivity() {
        ConnectivityManager cm = (ConnectivityManager) act
                .getSystemService(Context.CONNECTIVITY_SERVICE);
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
                        /*
                         * dbs = new DialogBox(this);
                         * dbs.get_adb().setMessage(retMess);
                         * dbs.get_adb().setPositiveButton("Ok", new
                         * DialogInterface.OnClickListener() { public void
                         * onClick(DialogInterface arg0, int arg1) { arg0.cancel();
                         * } }); dbs.get_adb().show();
                         */

                        break;
                    default:
                        flag = 1;
                        // retMess = "Network Unavailable. Please Try Again.";
                        retMess = getString(R.string.alert_000);
                        // setAlert();
                        showAlert(retMess);
                        /*
                         * dbs = new DialogBox(this);
                         * dbs.get_adb().setMessage(retMess);
                         * dbs.get_adb().setPositiveButton("Ok", new
                         * DialogInterface.OnClickListener() { public void
                         * onClick(DialogInterface arg0, int arg1) { arg0.cancel();
                         * Intent in = null; in = new
                         * Intent(getApplicationContext(), LoginActivity.class);
                         * startActivity(in); finish(); } }); dbs.get_adb().show();
                         */
                        break;
                }
            } else {
                flag = 1;
                // retMess = "Network Unavailable. Please Try Again.";
                retMess = getString(R.string.alert_000);
                // setAlert();
                showAlert(retMess);
                /*
                 * dbs = new DialogBox(this); dbs.get_adb().setMessage(retMess);
                 * dbs.get_adb().setPositiveButton("Ok", new
                 * DialogInterface.OnClickListener() { public void
                 * onClick(DialogInterface arg0, int arg1) { arg0.cancel();
                 * Intent in = null; in = new Intent(getApplicationContext(),
                 * LoginActivity.class); startActivity(in); finish(); } });
                 * dbs.get_adb().show();
                 */
            }
        } catch (NullPointerException ne) {

            Log.i("mayuri", "NullPointerException Exception" + ne);
            flag = 1;
            // retMess = "Can Not Get Connection. Please Try Again.";
            retMess = "Network Unavailable. Please Try Again.";
            // setAlert();
            showAlert(retMess);
            /*
             * dbs = new DialogBox(this); dbs.get_adb().setMessage(retMess);
             * dbs.get_adb().setPositiveButton("Ok", new
             * DialogInterface.OnClickListener() { public void
             * onClick(DialogInterface arg0, int arg1) { arg0.cancel(); Intent
             * in = null; in = new Intent(getApplicationContext(),
             * LoginActivity.class); startActivity(in); finish(); } });
             * dbs.get_adb().show();
             */

        } catch (Exception e) {
            Log.i("mayuri", "Exception" + e);
            flag = 1;
            // retMess = "Connection Problem Occured.";
            // retMess = "Network Unavailable. Please Try Again.";
            retMess = getString(R.string.alert_000);
            // setAlert();
            showAlert(retMess);
            /*
             * dbs = new DialogBox(this); dbs.get_adb().setMessage(retMess);
             * dbs.get_adb().setPositiveButton("Ok", new
             * DialogInterface.OnClickListener() { public void
             * onClick(DialogInterface arg0, int arg1) { arg0.cancel(); Intent
             * in = null; in = new Intent(getApplicationContext(),
             * LoginActivity.class); startActivity(in); finish(); } });
             * dbs.get_adb().show();
             */
        }
        return flag;
    }

    class CallWebService extends AsyncTask<Void, Void, Void> {

        LoadProgressBar loadProBarObj = new LoadProgressBar(act);
        JSONObject jsonObj = new JSONObject();
        String all_str, branch_cd, schm_cd, acnt_no, no_of_pages;
        String ValidationData = "";

        @Override
        protected void onPreExecute() {
            try {
                respcode = "";
                retval = "";
                respdesc = "";
                loadProBarObj.show();
                all_str = arrListTemp.get(spi_account.getSelectedItemPosition());
                no_of_pages = noof_chkbook.getSelectedItem().toString() + "";

                jsonObj.put("ACCNO", all_str);
                jsonObj.put("CUSTID", custId + "#~#" + AccCustId);
                jsonObj.put("NOOFCHQBKS", no_of_pages);
                jsonObj.put("IMEINO", imeino);
                jsonObj.put("SIMNO", MBSUtils.getSimNumber(act));
                jsonObj.put("METHODCODE", "6");
                //  ValidationData=MBSUtils.getValidationData(act,jsonObj.toString());
                Log.e("IN return", "data :" + jsonObj.toString());
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
                // retMess = "Some error occured";
                // retMess = "Network Unavailable. Please Try Again.";
                retMess = getString(R.string.alert_000);
                System.out.println(e.getMessage());
            }
            return null;
        }

        protected void onPostExecute(final Void result) {
            cnt = 0;
            loadProBarObj.dismiss();
            JSONObject jsonObj;
            try {

                String str = CryptoClass.Function6(var5, var2);
                jsonObj = new JSONObject(str.trim());
					/*ValidationData=xml_data[1].trim();
					if(ValidationData.equals(MBSUtils.getValidationData(act, xml_data[0].trim())))
					{*/
                Log.e("IN return", "data :" + str.trim());
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
                    //Log.e("CHKBKREQ","retval=="+retval);
                    if (retval.indexOf("FAILED") > -1) {
                        retMess = getString(R.string.alert_088);
                        showAlert(retMess);

                    } else if (retval.indexOf("ALREADYEXISTS") > -1) {
                        retMess = getString(R.string.alert_071);
                        showAlert(retMess);
                    } else if (retval.indexOf("SUCCESS") > -1) {

                        post_success(retval);
                    } else {

                        retMess = getString(R.string.alert_059);
                        showAlert(retMess);
                    }

                }
				/*	}
					else{
						MBSUtils.showInvalidResponseAlert(act);	
					}*/
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public void post_success(String retval) {
        respcode = "";
        respdesc = "";

        Log.e("IN return", "post_success : " + retval);
        // retMess = "Your request Has Been Registered With No: "
        retval = retval.split("~")[1];
        //decryptedRgno = "7579";
        retMess = getString(R.string.alert_058) + retval;

        showAlert(retMess);
    }

    public void showAlert(final String str) {
        // Toast.makeText(this, str, Toast.LENGTH_LONG).show();
        ErrorDialogClass alert = new ErrorDialogClass(act, "" + str) {
            @Override
            public void onClick(View v) {
                //Log.e("SetMPIN","SetMPIN...btn CLicked=="+R.id.btn_ok+"==="+v.getId());
                switch (v.getId()) {
                    case R.id.btn_ok:
                        //Log.e("SetMPIN","SetMPIN...CASE trru="+WSCalled);
                        if ((str.equalsIgnoreCase(respdesc)) && (respcode.equalsIgnoreCase("0"))) {
                            post_success(retval);
                        } else if ((str.equalsIgnoreCase(respdesc)) && (respcode.equalsIgnoreCase("1"))) {
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
        // Fragment fragment = new ChequeMenuActivity(act);
    }

}

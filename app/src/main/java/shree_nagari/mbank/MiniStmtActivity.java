package shree_nagari.mbank;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
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

import javax.crypto.spec.SecretKeySpec;

import mbLib.Accountbean;
import mbLib.CryptoClass;
import mbLib.CustomDialogClass;
import mbLib.DatabaseManagement;
import mbLib.DialogBox;
import mbLib.MBSUtils;
//import mbLib.DialogBox;
//import android.annotation.SuppressLint;
//import android.annotation.SuppressLint;

//@SuppressLint("NewApi")
public class MiniStmtActivity extends Fragment implements OnClickListener,
        android.view.View.OnKeyListener {
    MainActivity act;
    DialogBox dbs;
    MiniStmtActivity miniStmt;
    Intent in = null;
    TextView cust_nm, txt_heading;
    ImageView img_heading;
    Button btn_get_stmt;
    ImageButton btn_home;//, btn_back;
    ImageView btn_home1, btn_logout;
    ImageButton spinenr_btn;
    Spinner spi_account;
    ProgressBar pb_wait;
    ListView acnt_listView;
    String acnt_inf = "", all_acnts = "", avil_bal = "";
    String str = "", retMess = "", cust_name = "", retvalwbs;
    private static final String MY_SESSION = "my_session";
    PrivateKey var1 = null;
    String var5 = "", var3 = "";
    SecretKeySpec var2 = null;
    String stringValue = "";
    String all_str = "", branch_cd = "", schm_cd = "", acnt_no = "", custId = "";
    String selAcc = "", respcode = "", retval = "", respdesc = "";
    String str2 = "";
    String balnaceamnt = "", accountNo = "", AccCustId;
    //DialogBox dbs;
    DatabaseManagement dbms;
    // ProgressBar pb_wait;
    int flag = 0, noOfTran = 5;

    MiniStmtActivity mini;

    public MiniStmtActivity() {
    }

    @SuppressLint("ValidFragment")
    public MiniStmtActivity(MainActivity a) {
        //System.out.println("MiniStmtActivity()");
        act = a;
        miniStmt = this;
    }

    public void onBackPressed() {
        Intent in = new Intent(act, NewDashboard.class);
        in.putExtra("VAR1", var1);
        in.putExtra("VAR3", var3);
        startActivity(in);
        act.finish();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //System.out.println("onCreateView()");

        //dbms = new DatabaseManagement("shree_nagari.mbank", "listMobileBanking");
        dbms = new DatabaseManagement("shree_nagari.mbank", "listMobileBanking");
        View rootView = inflater.inflate(R.layout.mini_statement, container,
                false);
        var1 = act.var1;
        var3 = act.var3;
        Cursor c1 = dbms.selectFromTable("SHAREDPREFERENCE", "", null);
        if (c1 != null) {
            while (c1.moveToNext()) {
                stringValue = c1.getString(0);
                //Log.e("retvalstr","...."+stringValue);
                custId = c1.getString(2);
                //Log.e("custId","......"+custId);
            }
        }
        btn_home = (ImageButton) rootView.findViewById(R.id.btn_home);
        /*btn_back = (ImageButton) rootView.findViewById(R.id.btn_back);*/
        btn_home1 = (ImageView) rootView.findViewById(R.id.btn_home1);
        btn_logout = (ImageView) rootView.findViewById(R.id.btn_logout);
btn_logout.setVisibility(View.GONE);
        btn_logout.setVisibility(View.GONE);
        //btn_home.setImageResource(R.drawable.ic_home_d);
        //btn_back.setImageResource(R.drawable.backover);

        acnt_listView = (ListView) rootView.findViewById(R.id.acnt_listView);
        txt_heading = (TextView) rootView.findViewById(R.id.txt_heading);
        img_heading = (ImageView) rootView.findViewById(R.id.img_heading);

        btn_get_stmt = (Button) rootView.findViewById(R.id.btnGetStmt);
        btn_get_stmt.setOnClickListener(this);

        // btn_get_stmt.setTypeface(tf_calibri);
        all_acnts = stringValue;

        txt_heading.setText(getString(R.string.lbl_mini_statement));
        img_heading.setBackgroundResource(R.mipmap.ministmnt);
        //btn_back.setOnClickListener(this);
        btn_home.setOnClickListener(this);
        btn_home1.setOnClickListener(this);
        btn_logout.setOnClickListener(this);
        addAccounts(all_acnts);

        pb_wait = (ProgressBar) rootView.findViewById(R.id.pb_wait2);
        pb_wait.setMax(10);
        pb_wait.setProgress(1);
        pb_wait.setVisibility(ProgressBar.INVISIBLE);
        //System.out.println("MiniStmtActivity END OF INITALL()");
		/*rootView.setOnKeyListener( new OnKeyListener()
		{
		    @Override
		    public boolean onKey( View v, int keyCode, KeyEvent event )
		    {
		        if( keyCode == KeyEvent.KEYCODE_BACK )
		        {
		        	onBackPressed();
		        }
		        return false;
		    }
		} );*/
        return rootView;
    }

    public void addAccounts(String str) {
        //System.out.println("MiniStmtActivity IN addAccounts()" + str);

        try {
            ArrayList<String> arrList = new ArrayList<>();
            String allstr[] = str.split("~");

            //System.out.println("MiniStmtActivity Mayuri.....................:");
            //System.out.println("MiniStmtActivity Accounts:::" + allstr[0]);
            // String str1[] = allstr[1].split(",-,");
            // int noOfAccounts = str1.length;
            int noOfAccounts = allstr.length;
            //System.out.println("MiniStmtActivity noOfAccounts:" + noOfAccounts);

            ArrayList<Accountbean> Accountbean_arr = new ArrayList<Accountbean>();
            final ArrayList<String> Account_arrTemp = new ArrayList<String>();
            Accounts acArray[] = new Accounts[noOfAccounts];
            for (int i = 0; i < noOfAccounts; i++) {
                str2 = allstr[i];
                acArray[i] = new Accounts(str2);
                str2 = str2.replaceAll("#", "-");
                String acctype = str2.split("-")[2];
                String AccCustID = str2.split("-")[11];
                // arrList.add(str2);
                //	if (str2.indexOf("FD") == -1 && str2.indexOf("RP") == -1 && str2.indexOf("PG") == -1 && str2.indexOf("CA") == -1)
                if ((!acctype.equalsIgnoreCase("FD") && !acctype.equalsIgnoreCase("RP"))) {
                    //Log.e("MINISTMT","str2 added=="+str2);
                    Accountbean Accountbeanobj = new Accountbean();
                    Accountbean_arr.add(Accountbeanobj);
                    Account_arrTemp.add(str2);
                    //	String acctype=str2.split("-")[2];
                    str2 = MBSUtils.get16digitsAccNo(str2);
                    Accountbeanobj.setAccountinfo(str2 + " (" + MBSUtils.getAccTypeDesc(acctype) + ")");
                    Accountbeanobj.setAccountNumber(str2);
                    Accountbeanobj.setAcccustid(AccCustID);
                }
            }


            Customlist_radioadt adapter = new Customlist_radioadt(act, Accountbean_arr);
            acnt_listView.setAdapter(adapter);
            acnt_listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            acnt_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView,View view, int i, long l) {
                            btn_get_stmt.setEnabled(true);
                            Accountbean dataModel = (Accountbean) adapterView.getItemAtPosition(i);
                            accountNo = dataModel.getAccountNumber();
                            AccCustId = dataModel.getAcccustid();
                            acnt_inf = Account_arrTemp.get(i);
                            for (int i1 = 0; i1 < adapterView.getCount(); i1++) {
                                try {
                                    View v = adapterView.getChildAt(i1);
                                    RadioButton radio = (RadioButton) v.findViewById(R.id.radio);
                                    radio.setChecked(false);
                                } catch (Exception e) {
                                    Log.e("radio button", "radio");
                                }
                            }
                            try {
                                RadioButton radio = (RadioButton) view.findViewById(R.id.radio);
                                radio.setChecked(true);
                            } catch (Exception e) {
                                Log.e("radio button", "radio");
                            }
                        }
                    });
            } catch (Exception e) {
            System.out.println("" + e);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnGetStmt:

                InputDialogBox inputBox = new InputDialogBox(act);
                inputBox.show();
                break;
            //case R.id.btn_back:
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

    public class  CallWebServicelog extends AsyncTask<Void, Void, Void> {
        JSONObject jsonObj = new JSONObject();
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

                        }
                        break;
                    case DISCONNECTED:
                        flag = 1;
                        // retMess = "Network Disconnected. Please Try Again.";
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
                showAlert(retMess);
                // setAlert();
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

        } catch (Exception e) {
            Log.i("mayuri", "Exception" + e);
            flag = 1;
            // retMess = "Connection Problem Occured.";
            // retMess = "Network Unavailable. Please Try Again.";
            retMess = getString(R.string.alert_000);
            showAlert(retMess);
            // setAlert();
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
        String ValidationData = "";

        @Override
        protected void onPreExecute() {
            try {
                respcode = "";
                retval = "";
                respdesc = "";
                loadProBarObj.show();
                Log.e("SHUbham", "AccCustId123: "+AccCustId+" custId"+custId );

                all_str = acnt_inf;
               // jsonObj.put("CUSTID", custId);
				jsonObj.put("CUSTID", custId);
				jsonObj.put("ACCNO", accountNo);
                jsonObj.put("NOOFTRAN", "" + noOfTran);
                jsonObj.put("IMEINO", MBSUtils.getImeiNumber(act));
                jsonObj.put("SIMNO", MBSUtils.getSimNumber(act));
                jsonObj.put("METHODCODE", "2");
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
                if (var5.equals("FAILED"))
                    retMess = "FAILED";
                else
                    retMess = var5;
            }// end try
            catch (Exception e) {
                // retMess = "Error occured";
                retMess = getString(R.string.alert_err);
                // retMess = "Network Unavailable. Please Try Again.";
                retMess = getString(R.string.alert_000);
                System.out.println(e.getMessage());
            }
            return null;
        }

        //@SuppressLint("NewApi")
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
                    if (retval.indexOf("FAILED") > -1) {
                        // retMess="Can Not Get Mini Statement. Please Retry";
                        retMess = "Network Problem. Please Try Again.";
                        // Show message dialouge
                        // setAlert();
                        showAlert(retMess);
                    } else if (retval.indexOf("NODATA") > -1) {
                        // retMess="Can Not Get Mini Statement. Please Retry";
                        retMess = "Can Not Get Mini Statement Please Contact To Bank.";
                        // Show message dialouge
                        // setAlert();
                        showAlert(retMess);
                    } else {
                        post_success(retval);


                    }
                    // pb_wait.setVisibility(ProgressBar.INVISIBLE);
                    loadProBarObj.dismiss();
                }/*}
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
        try {
            respcode = "";
            respdesc = "";
            String[] values = retval.split("~@~");

            balnaceamnt = MBSUtils.amountFormat(values[1], true, act);
            avil_bal = MBSUtils.amountFormat(values[2], true, act);

            retval = values[0];

            str = acnt_inf;

            // Log.e("decryptedStatments :", decryptedStatments);
            act.setTitle(act.getString(R.string.lbl_mini_statement));
            Fragment miniStmtRepFragment = new MiniStmtReport(act, str,
                    all_str, retval, balnaceamnt, avil_bal);

            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.frame_container, miniStmtRepFragment).commit();
            act.frgIndex = 41;

        } catch (Exception e) {
            // TODO: handle exception
            Log.e("MInistmnts", "" + e);
            e.printStackTrace();
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        return false;
    }

    public void onFinish() {
        // //////////mini.pb_wait.setVisibility(ProgressBar.INVISIBLE);
    }

    /*
     * public void setAlert() { dbs = new DialogBox(this);
     * dbs.get_adb().setMessage(retMess); dbs.get_adb().setPositiveButton("OK",
     * new DialogInterface.OnClickListener() {
     *
     * @Override public void onClick(DialogInterface arg0, int arg1) { // TODO
     * Auto-generated method stub
     * //////////////pb_wait.setVisibility(ProgressBar.INVISIBLE);
     * arg0.cancel(); } }); dbs.get_adb().show(); }
     */

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
    }

    public class InputDialogBox extends Dialog implements OnClickListener {
        Activity activity;
        String msg, title;
        Context appAcontext;
        EditText mpin;
        TextView txt_dia;
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
            setContentView(R.layout.dialog_design);
            txt_dia = (TextView) findViewById(R.id.txt_dia);
            mpin = (EditText) findViewById(R.id.txtMpin);
            mpin.setInputType(InputType.TYPE_CLASS_NUMBER);
            btnOk = (Button) findViewById(R.id.btnOK);
            txt_dia.setText(getString(R.string.no_of_tran));
            mpin.setText("5");
            mpin.setVisibility(EditText.VISIBLE);
            btnOk.setVisibility(Button.VISIBLE);
            btnOk.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            try {

                //System.out.println("========= inside onClick ============***********");
                String str = mpin.getText().toString().trim();

                if (str.length() != 0) {
                    int no = Integer.parseInt(str);
                    if (no == 0) {
                        retMess = getString(R.string.alert_104);
                        showAlert(retMess);
                        this.show();
                    } else if (no > 25) {
                        retMess = getString(R.string.alert_106);
                        showAlert(retMess);
                        this.show();
                    } else {
                        noOfTran = no;
                        flag = chkConnectivity();
                        //System.out.println("flag in Ministatement---" + flag);
                        if (flag == 0) {
                            new CallWebService().execute();
                            this.hide();
                        }
                    }
                } else {
                    retMess = getString(R.string.alert_105);
                    showAlert(retMess);
                    this.show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Exception in InputDialogBox of onClick:=====>" + e);
            }
        }// end onClick
    }// end InputDialogBox

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        System.gc();
    }
}

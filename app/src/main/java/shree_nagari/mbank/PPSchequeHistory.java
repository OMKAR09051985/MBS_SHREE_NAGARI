package shree_nagari.mbank;


import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.security.PrivateKey;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import javax.crypto.spec.SecretKeySpec;

import mbLib.Accountbean;
import mbLib.CryptoClass;
import mbLib.CustomDialogClass;
import mbLib.DatabaseManagement;
import mbLib.MBSUtils;

public class PPSchequeHistory extends Fragment implements View.OnClickListener {
    PPSchequeHistory entryHighValCheq;
    MainActivity act;
    DatabaseManagement dbms;
    ImageView img_heading, btn_home1, btn_logout;
    PrivateKey var1 = null;
    String var5 = "", var3 = "";
    SecretKeySpec var2 = null;
    Spinner spi_debit_account, spi_cheques;
    private ImageButton spinner_btn, spinner_btn1, btn_back, btn_home;
    String respcode = "", retval = "", respdesc = "";
    ArrayList<String> arrListTemp = new ArrayList<String>();
    Button btn_proceed, btnCalender;
    EditText noofrequest, payeename, instruDate, trancode;
    TextView txt_accno, txt_heading, txt_amountword;
    String str = "", str2 = "", accno = "", cheq_no = "", nooftrn = "", payee, curDate = "", date = "", tranNo = "", custId = "", all_acnts = "", retvalwbs = "",
            retMess = "", stringValue = "", mobno = "", accstr = "", AccCustId;
    Calendar dateandtime;
    SimpleDateFormat df;
    LoadProgressBar loadProBarObj;
    Accounts[] acArray;
    int cnt = 0, flag = 0;
    boolean noAccounts;
    ArrayList<Accountbean> arrList1;

    public PPSchequeHistory() {
        entryHighValCheq = this;
    }

    @SuppressLint("ValidFragment")
    public PPSchequeHistory(MainActivity m) {
        act = m;
        entryHighValCheq = this;
    }

    @Nullable
    //testing
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.pps_histrory_main,
                container, false);
        dateandtime = Calendar.getInstance(Locale.US);
        df = new SimpleDateFormat("dd/MM/yyyy");
        curDate = df.format(dateandtime.getTime());
        Log.e("sud--", "sud----");
        dbms = new DatabaseManagement("shree_nagari.mbank", "listMobileBanking");
        btn_proceed = (Button) rootView.findViewById(R.id.btn_proceed);
        btnCalender = (Button) rootView.findViewById(R.id.btnCalender);
        noofrequest = (EditText) rootView.findViewById(R.id.nooftran);


        txt_heading = (TextView) rootView.findViewById(R.id.txt_heading);
        txt_heading.setText(getString(R.string.lbl_chq_history));

        spinner_btn = (ImageButton) rootView.findViewById(R.id.spinner_btn);
        spinner_btn.setOnClickListener(this);

        btn_home = (ImageButton) rootView.findViewById(R.id.btn_home);
        btn_home.setImageResource(R.mipmap.ic_home_d);
        btn_home1 = (ImageView) rootView.findViewById(R.id.btn_home1);
        btn_logout = (ImageView) rootView.findViewById(R.id.btn_logout);
btn_logout.setVisibility(View.GONE);
        btn_home1.setOnClickListener(this);
        btn_logout.setOnClickListener(this);
        btn_home.setOnClickListener(this);
        img_heading = (ImageView) rootView.findViewById(R.id.img_heading);
        img_heading.setBackgroundResource(R.mipmap.pps_menu);
        //btn_back = (ImageButton) rootView.findViewById(R.id.btn_back);
        //btn_back.setImageResource(R.mipmap.backover);
        //btn_back.setOnClickListener(this);

        spi_debit_account = (Spinner) rootView
                .findViewById(R.id.spi_accounts);

        if (spi_debit_account != null)

            spi_debit_account.requestFocus();

        spi_cheques = (Spinner) rootView
                .findViewById(R.id.spi_cheques);

        if (spi_cheques != null)

            spi_cheques.requestFocus();

        loadProBarObj = new LoadProgressBar(act);
        btn_proceed.setOnClickListener(this);

        //act.frgIndex=3;
        var1 = act.var1;
        var3 = act.var3;
        Cursor c1 = dbms.selectFromTable("SHAREDPREFERENCE", "", null);// ("select * from ",
        // null);
        if (c1 != null) {
            while (c1.moveToNext()) {
                stringValue = c1.getString(0);
                // Log.e("retValStr","......"+stringValue);
                custId = c1.getString(2);
                mobno = c1.getString(4);
            }
        }
        spi_debit_account.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> arg0, View arg1,int arg2, long arg3) {
                        try {
                            String temp = (String) arg0.getItemAtPosition(arg2);
                            Log.e("Shubham", "spi_debit_accountposition: " + arg2 + " " + "spi_debit_accountposition " + temp);
                            if (arg2 == 0) {
                                //txtBalance.setText("");
                            }
                            if (arg2 > 0) {
                                if (!temp.equalsIgnoreCase("Select Debit Account")) {
                                    Accountbean dataModel = (Accountbean) arg0.getItemAtPosition(arg2);
                                    //accountNo = dataModel.getAccountNumber();
                                    AccCustId = dataModel.getAcccustid();
                                    if (spi_debit_account.getCount() > 0) {
                                        String str = arrListTemp.get(spi_debit_account.getSelectedItemPosition() - 1);

                                        retMess = "Selected Account number" + str;
                                        Accounts selectedDrAccount = acArray[spi_debit_account
                                                .getSelectedItemPosition() - 1];

                                    }
                                }// end onItemSelected
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {

                    }// end onNothingSelected

                });// end spi_debit_account

        all_acnts = stringValue;

        addAccounts(all_acnts);


        return rootView;
    }

    public void addAccounts(String str) {
        try {
            ArrayList<String> arrList = new ArrayList<String>();
            arrList1 = new ArrayList<>();
            String allstr[] = str.split("~");

            int noOfAccounts = allstr.length;
            arrList.add("Select Debit Account");
            // //System.out.println("SameBankTransfer noOfAccounts:" +
            // noOfAccounts);
            acArray = new Accounts[noOfAccounts];
            int j = 0;
            for (int i = 0; i < noOfAccounts; i++) {
                str2 = allstr[i];
                String tempStr = str2;

                str2 = str2.replaceAll("#", "-");
                String accType = str2.split("-")[2];
                String oprcd = str2.split("-")[7];

                String AccCustID = str2.split("-")[11];

                String withdrawalAllowed = str2.split("-")[10];
                String str2Temp = str2;
                str2 = MBSUtils.get16digitsAccNo(str2);

                if (((accType.equals("SB")) || (accType.equals("LO")) || (accType.equals("CA")))
                        && oprcd.equalsIgnoreCase("O")) {// && withdrawalAllowed.equalsIgnoreCase("Y")
                    acArray[j++] = new Accounts(tempStr);

                    Accountbean accountbeanobj = new Accountbean();
                    accountbeanobj.setAccountinfo(str2 + " (" + MBSUtils.getAccTypeDesc(accType) + ")");
                    accountbeanobj.setAccountNumber(str2);
                    accountbeanobj.setAcccustid(AccCustID);
                    arrList1.add(accountbeanobj);

                    arrList.add(str2 + " (" + MBSUtils.getAccTypeDesc(accType) + ")");
                    arrListTemp.add(str2);
                }
            }

            if (arrList.size() == 0) {
                noAccounts = true;
                showAlert(getString(R.string.alert_089));

            }
            String[] debAccArr = new String[arrList.size()];
            debAccArr = arrList.toArray(debAccArr);
            ArrayAdapter<String> debAccs = new ArrayAdapter<String>(act,
                    R.layout.spinner_item, debAccArr);
            debAccs.setDropDownViewResource(R.layout.spinner_dropdown_item);
            spi_debit_account.setAdapter(debAccs);
        } catch (Exception e) {
            // System.out.println("" + e);
            e.printStackTrace();
            // Log.e("Exception in add accounts","Exception in add accounts"+e);
        }

    }// end addAccount

    public void onClick(View v) {
        nooftrn = noofrequest.getText().toString().trim();

        // int accLength=Integer.parseInt("16");

        if (v.getId() == R.id.spinner_btn) {
            spi_debit_account.performClick();
        }

         /*if (v.getId() == R.id.btn_back) {
            Fragment fragment = new OtherServicesMenuActivity(act);
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_container, fragment).commit();
        } */
        else if (v.getId() == R.id.btn_home) {
            Intent in = new Intent(getActivity(), DashboardDesignActivity.class);
            in.putExtra("VAR1", var1);
            in.putExtra("VAR3", var3);
            startActivity(in);
        } else if (v.getId() == R.id.btn_home1) {
            Intent in1 = new Intent(act, NewDashboard.class);
            in1.putExtra("VAR1", var1);
            in1.putExtra("VAR3", var3);
            startActivity(in1);
            act.finish();

        } else if (v.getId() == R.id.btn_logout) {
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

        } else if (v.getId() == R.id.btn_proceed) {
           // try {
                String str = spi_debit_account.getSelectedItem().toString();
                AccCustId = arrList1.get(spi_debit_account.getSelectedItemPosition()-1).getAcccustid();
                //String chq = spi_cheques.getSelectedItem().toString();
                if (str.equalsIgnoreCase("Select Debit Account")) {
                    showAlert(getString(R.string.alert_0981));
                }
            /*else if (chq.equalsIgnoreCase("Select Instrument Number")) {
                showAlert(getString(R.string.alert_201));
            }*/

                else if (nooftrn.length() == 0) {
                    showAlert(getString(R.string.alert_208));
                } else if (Integer.parseInt(nooftrn) <= 0) {
                    showAlert(getString(R.string.alert_209));
                } else if (Integer.parseInt(nooftrn) > 5) {
                    showAlert(getString(R.string.alert_210));
                } else {
                    flag = chkConnectivity();
                    ////System.out.println("flag in Ministatement---" + flag);
                    if (flag == 0) {
                        new CallWebService().execute();
                    }
                }
           // } catch (Exception e) {
            //    e.printStackTrace();
            //    Log.e("Shubham", "PPSCHEQUEHISTORY_PROCEED_BUTTON: " + e.getMessage());
           // }
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
            NetworkInfo.State state = ni.getState();
            boolean state1 = ni.isAvailable();
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

            //Log.i("mayuri", "NullPointerException Exception" + ne);
            flag = 1;
            retMess = getString(R.string.alert_000);
            // setAlert();
            showAlert(retMess);


        } catch (Exception e) {
            //Log.i("mayuri", "Exception" + e);
            flag = 1;
            // retMess = "Connection Problem Occured.";
            // retMess = "Network Unavailable. Please Try Again.";
            retMess = getString(R.string.alert_000);
            showAlert(retMess);

        }
        return flag;
    }

    class CallWebService extends AsyncTask<Void, Void, Void> {
        JSONObject jsonObj = new JSONObject();
        JSONObject mainjsonObj = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        LoadProgressBar loadProBarObj = new LoadProgressBar(act);

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        protected void onPreExecute() {
            loadProBarObj.show();
            try {
                Log.e("DSP", "in onPreExecute");
                String account = spi_debit_account.getSelectedItem().toString();
                AccCustId = arrList1.get(spi_debit_account.getSelectedItemPosition()-1).getAcccustid();
                accstr = account;
                Log.e("DSP", "account==" + account);

                nooftrn = noofrequest.getText().toString().trim();


                jsonObj.put("MSGID", "5580");
                jsonObj.put("MOBILENO", mobno);
                jsonObj.put("SMSDATETIME", "");
                jsonObj.put("SMSTEXT", "Entered From MBS");
                jsonObj.put("CUSTID", custId + "#~#" + AccCustId);
                jsonObj.put("ACCNO", account.substring(0, Integer.parseInt(getString(R.string.alert_accleng))));
                jsonObj.put("TRANNO", nooftrn);
                jsonObj.put("DEVICEID", MBSUtils.getImeiNumber(act));

                jsonObj.put("IMEINO", MBSUtils.getImeiNumber(act));
                jsonObj.put("SIMNO", MBSUtils.getSimNumber(act));
                jsonObj.put("METHODCODE", "89");
                // jsonArray.put(jsonObj);
                // mainjsonObj.put("SMSDATA",jsonArray);
                // mainjsonObj.put("MSGID","2250");
                //  mainjsonObj.put("METHODCODE","89");
                Log.e("DSP","jsonObj=="+jsonObj);
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
                HttpTransportSE androidHttpTransport = new HttpTransportSE(value6, 45000);

                androidHttpTransport.call(value5, envelope);
                var5 = envelope.bodyIn.toString().trim();
                var5 = var5.substring(var5.indexOf("=") + 1, var5.length() - 3);
            }// end try
            catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        protected void onPostExecute(final Void result) {
            loadProBarObj.dismiss();
            try {
                String str = CryptoClass.Function6(var5, var2);
                jsonObj = new JSONObject(str.trim());
                Log.e("DSP", "str====" + str);

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
                        String[] arr = retval.split("~");
                        showAlert(arr[1]);
                    } else if (retval.indexOf("SUCCESS") > -1) {
                        post_success(retval);//showAlert(getString(R.string.alert_207)); show request
                    } else {

                        showAlert(getString(R.string.alert_err));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    public void post_success(String retval) {
        respcode = "";
        respdesc = "";
        String valstr = "";
        try {
            String values[] = retval.split("~");
            valstr = values[1];
            // balnaceamnt=MBSUtils.amountFormat(values[1],true,act);
            // avil_bal = MBSUtils.amountFormat(values[2],true,act);
            //Log.e("Ministmnt", "balnaceamnt==" + balnaceamnt);
            //Log.e("Ministmnt", "avil_bal==" + avil_bal);
            //retval = values[0];
            ////Log.e("Ministmnt", "decryptedStatments=="	+ decryptedStatments);


            ////Log.e("decryptedStatments :", decryptedStatments);
            act.setTitle(act.getString(R.string.lbl_nooftran));
            Fragment ppschqRepFragment = new PPS_history_report(act,
                    valstr, accstr);

            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_container, ppschqRepFragment)
                    .commit();
            act.frgIndex = 87;

        } catch (Exception e) {
            // TODO: handle exception
            //Log.e("MInistmnts", "" + e);
            e.printStackTrace();
        }


    }


    public void showAlert(final String str) {
        ErrorDialogClass alert = new ErrorDialogClass(act, "" + str) {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btn_ok:
                        if (str.equalsIgnoreCase(getString(R.string.alert_207))) {
                            Fragment OthrSrvcFragment = new OtherServicesMenuActivity(act);
                            act.setTitle(getString(R.string.lbl_title_change_mpin));
                            FragmentManager fragmentManager = getFragmentManager();
                            fragmentManager.beginTransaction().replace(R.id.frame_container, OthrSrvcFragment).commit();
                        }
                        this.dismiss();
                        break;
                    default:
                        break;
                }
            }
        };
        alert.show();
    }
}


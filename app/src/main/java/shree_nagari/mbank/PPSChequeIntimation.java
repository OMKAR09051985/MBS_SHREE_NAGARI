package shree_nagari.mbank;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
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
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
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
import android.widget.Toast;

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
import mbLib.DatePickerDailog;
import mbLib.EnglishNumberToWords;
import mbLib.MBSUtils;

public class PPSChequeIntimation extends Fragment implements View.OnClickListener {
    PPSChequeIntimation entryHighValCheq;
    MainActivity act;
    DatabaseManagement dbms;
    PrivateKey var1 = null;
    ImageView img_heading, btn_home1, btn_logout;
    String var5 = "", var3 = "";
    SecretKeySpec var2 = null;
    Spinner spi_debit_account, spi_cheques;
    private ImageButton spinner_btn, spinner_btn1, btn_back, btn_home;
    String respcode = "", retval = "", respdesc = "";
    ArrayList<String> arrListTemp = new ArrayList<String>();
    Button btn_proceed, btnCalender;
    EditText amount, payeename, instruDate, trancode;
    TextView txt_accno, txt_heading, txt_amountword;
    String str = "", str2 = "", accno = "", cheq_no = "", amt = "", payee, curDate = "", date = "", tranNo = "", custId = "", all_acnts = "", retvalwbs = "",
            retMess = "", stringValue = "", mobno = "", AccCustId;
    Calendar dateandtime;
    SimpleDateFormat df;
    LoadProgressBar loadProBarObj;
    Accounts acArray[];
    int cnt = 0, flag = 0;
    int mYear, mMonth, mDay;
    boolean noAccounts;
    ArrayList<Accountbean> arrList1;

    public PPSChequeIntimation() {
        entryHighValCheq = this;
    }

    @SuppressLint("ValidFragment")
    public PPSChequeIntimation(MainActivity m) {
        act = m;
        entryHighValCheq = this;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.pps_chq_intimation,
                container, false);
        dateandtime = Calendar.getInstance(Locale.US);
        df = new SimpleDateFormat("dd/MM/yyyy");
        curDate = df.format(dateandtime.getTime());
        Log.e("sud--", "sud----");
        dbms = new DatabaseManagement("shree_nagari.mbank", "listMobileBanking");

        btn_proceed = (Button) rootView.findViewById(R.id.btn_proceed);
        btnCalender = (Button) rootView.findViewById(R.id.btnCalender);
        amount = (EditText) rootView.findViewById(R.id.amount);
        amount.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(2)});
        payeename = (EditText) rootView.findViewById(R.id.payeename);
        instruDate = (EditText) rootView.findViewById(R.id.instruDate);
        trancode = (EditText) rootView.findViewById(R.id.trancode);
        trancode.setEnabled(false);
        txt_accno = (TextView) rootView.findViewById(R.id.txt_accno);
        txt_heading = (TextView) rootView.findViewById(R.id.txt_heading);
        txt_heading.setText(getString(R.string.lbl_chq_intimation));
        txt_amountword = (TextView) rootView.findViewById(R.id.txt_amountword);
        spinner_btn = (ImageButton) rootView.findViewById(R.id.spinner_btn);
        spinner_btn.setOnClickListener(this);
        spinner_btn1 = (ImageButton) rootView.findViewById(R.id.spinner_btn1);
        spinner_btn1.setOnClickListener(this);
        btn_home = (ImageButton) rootView.findViewById(R.id.btn_home);
        btn_home.setImageResource(R.mipmap.ic_home_d);
        btn_home.setOnClickListener(this);
        btn_home1 = (ImageView) rootView.findViewById(R.id.btn_home1);
        btn_logout = (ImageView) rootView.findViewById(R.id.btn_logout);

        img_heading = (ImageView) rootView.findViewById(R.id.img_heading);
        img_heading.setBackgroundResource(R.mipmap.pps_menu);
        // btn_back = (ImageButton) rootView.findViewById(R.id.btn_back);
        // btn_back.setImageResource(R.mipmap.backover);
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
        btnCalender.setOnClickListener(this);
        instruDate.setOnClickListener(this);
        btn_home1.setOnClickListener(this);
        btn_logout.setOnClickListener(this);
        // act.frgIndex=3;
        var1 = act.var1;
        var3 = act.var3;
        Cursor c1 = dbms.selectFromTable("SHAREDPREFERENCE", "", null);// ("select * from ",
        // null);
        if (c1 != null) {
            while (c1.moveToNext()) {
                stringValue = c1.getString(0);
                //Log.e("retValStr","Shree_Nagari_SpinnerData:----"+stringValue);
                custId = c1.getString(2);
                mobno = c1.getString(4);
            }
        }
        all_acnts = stringValue;

        addAccounts(all_acnts);
        spi_debit_account.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                //Log.e("TAG", "spi_debit_accountonItemSelected: "+arg0.getItemAtPosition(arg2) );
                String temp = (String) arg0.getItemAtPosition(arg2);
                String temp1 = spi_debit_account.getSelectedItem().toString();
                int pos = arg2;
                if (arg2 == 0) {
                    //txtBalance.setText("");
                }
                if (arg2 > 0) {
                    if (!temp.equalsIgnoreCase("Select Debit Account")) {
                        //Toast.makeText(getActivity(),"selected index="+arg2, Toast.LENGTH_SHORT).show();
                        Accountbean dataModel = arrList1.get(arg2-1);
                        //accountNo = dataModel.getAccountNumber();
                        AccCustId = dataModel.getAcccustid();
                        if (spi_debit_account.getCount() > 0) {
                            txt_amountword.setText("");
                            payeename.setText("");
                            amount.setText("");
                            trancode.setText("");
                            String str = arrListTemp.get(spi_debit_account.getSelectedItemPosition() - 1);
                            retMess = "Selected Account number" + str;
                            Accounts selectedDrAccount = acArray[spi_debit_account.getSelectedItemPosition() - 1];
                            new CallWebService_getCheques().execute();
                        }
                    } else {
                        showAlert(getString(R.string.alert_0981));
                    }// end onItemSelected
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }// end onNothingSelected

        });// end spi_debit_account


        amount.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
                Log.e("sud00", s + " - " + start + " - " + " - " + count + " - " + after);
            }

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                Log.e("s-", s + " " + start + " " + before + " " + count);
                Log.e("s-", s.length() + " ");
                if (start != 0) {
                    try {
                        String amtwrd = EnglishNumberToWords.convertToIndianCurrency(s.toString());
                        txt_amountword.setText(amtwrd);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    txt_amountword.setText("");
                }
            }
        });
        return rootView;
    }

    public void addAccounts(String str) {
        try {
            ArrayList<String> arrList = new ArrayList<String>();
            arrList1 = new ArrayList<Accountbean>();
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
                String withdrawalAllowed = str2.split("-")[10];
                String AccCustID = str2.split("-")[11];
                String str2Temp = str2;
                str2 = MBSUtils.get16digitsAccNo(str2);

                if (((accType.equals("SB")) || (accType.equals("LO")) || (accType
                        .equals("CA")))
                        && oprcd.equalsIgnoreCase("O")
                ) {// && withdrawalAllowed.equalsIgnoreCase("Y")
                    acArray[j++] = new Accounts(tempStr);
                    Accountbean accountbeanobj = new Accountbean();
                    accountbeanobj.setAccountinfo(str2 + " (" + MBSUtils.getAccTypeDesc(accType) + ")");
                    accountbeanobj.setAccountNumber(str2);
                    accountbeanobj.setAcccustid(AccCustID);
                    arrList1.add(accountbeanobj);
                    arrList.add(str2 + " (" + MBSUtils.getAccTypeDesc(accType)
                            + ")");
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
        amt = amount.getText().toString().trim();
        payee = payeename.getText().toString().trim();
        date = instruDate.getText().toString().trim();
        tranNo = trancode.getText().toString().trim();
        int accLength = Integer.parseInt("16");

        if (v.getId() == R.id.btnCalender) {
            //onFromDateCalendarClick(v);
            newDatePickers();
        } else if (v.getId() == R.id.instruDate) {
            //onFromDateCalendarClick(v);
            newDatePickers();
        } else if (v.getId() == R.id.spinner_btn) {
            spi_debit_account.performClick();
        } else if (v.getId() == R.id.spinner_btn1) {
            spi_cheques.performClick();
        }
        /*if (v.getId() == R.id.btn_back) {
            Fragment fragment = new OtherServicesMenuActivity(act);
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_container, fragment).commit();
        }*/
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
                            // Toast.makeText(act,""+flag,Toast.LENGTH_SHORT).show();
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
            String chq = "NA";
            String str = spi_debit_account.getSelectedItem().toString();
            AccCustId = arrList1.get(spi_debit_account.getSelectedItemPosition()-1).getAcccustid();
            if (!str.equalsIgnoreCase("Select Debit Account")) {
                chq = spi_cheques.getSelectedItem().toString();

            }
            //String chq = spi_cheques.getSelectedItem().toString();
            if (str.equalsIgnoreCase("Select Debit Account")) {
                showAlert(getString(R.string.alert_0981));
            } else if (chq.equalsIgnoreCase("Select Instrument Number")) {
                showAlert(getString(R.string.alert_ppscheque));
            } else if (date.length() == 0) {
                showAlert(getString(R.string.alert_202));
            } else if (tranNo.length() == 0) {//|| tranNo.length()<2 || tranNo.length()>3
                showAlert(getString(R.string.alert_203));
            } else if (payee.length() == 0) {
                showAlert(getString(R.string.alert_204));
            } else if (amt.length() == 0) {
                showAlert(getString(R.string.alert_205));
            } else if (Double.parseDouble(amt) <= 0) {
                showAlert(getString(R.string.alert_206));
            } else {
                new CallWebService().execute();
            }
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

    class CallWebService_getCheques extends AsyncTask<Void, Void, Void> {

        JSONObject jsonObj = new JSONObject();

        LoadProgressBar loadProBarObj = new LoadProgressBar(act);

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        protected void onPreExecute() {
            try {
                loadProBarObj.show();
                String str = spi_debit_account.getSelectedItem().toString();
                AccCustId = arrList1.get(spi_debit_account.getSelectedItemPosition()-1).getAcccustid();
                jsonObj.put("CUSTID", custId + "#~#" + AccCustId);
                jsonObj.put("ACCNO", str.substring(0, 16));
                jsonObj.put("IMEINO", MBSUtils.getImeiNumber(act));
                jsonObj.put("SIMNO", MBSUtils.getSimNumber(act));
                jsonObj.put("METHODCODE", "88");

                Log.e("DSP", "jsonObj====" + jsonObj);
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

        @Override
        protected void onPostExecute(final Void result) {
            loadProBarObj.dismiss();
            JSONObject jsonObj;
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
                        showAlert(getString(R.string.alert_err));
                    } else {
                        if (retval.indexOf("SUCCESS") > -1) {

                            post_success(retval);
                        } else if (retval.equalsIgnoreCase("NODATA~")) {
                            showAlert(getString(R.string.alert_nodata));
                        }
                    }
                }//else


            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public void post_success(String retval) {
        respcode = "-1";
        respdesc = "";
        retval = retval.split("SUCCESS~")[1];
        addCheques(retval);
    }

    class CallWebService extends AsyncTask<Void, Void, Void> {
        JSONObject jsonObj = new JSONObject();
        JSONObject mainjsonObj = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        LoadProgressBar loadProBarObj = new LoadProgressBar(act);

        @Override
        protected void onPreExecute() {
            loadProBarObj.show();
            try {
                Log.e("DSP", "in onPreExecute");
                String account = spi_debit_account.getSelectedItem().toString();
                AccCustId = arrList1.get(spi_debit_account.getSelectedItemPosition()-1).getAcccustid();

                Log.e("DSP", "account==" + account);
                String cheque = spi_cheques.getSelectedItem().toString();
                Log.e("DSP", "cheque==" + cheque);
                amt = amount.getText().toString().trim();
                payee = payeename.getText().toString().trim();
                date = instruDate.getText().toString().trim();
                date = date.split("/")[0] + date.split("/")[1] + date.split("/")[2];
                tranNo = trancode.getText().toString().trim();
                Log.e("DSP", "date==" + date);

                jsonObj.put("MSGID", "2250");
                jsonObj.put("MOBILENO", mobno);
                jsonObj.put("SMSDATETIME", "");
                jsonObj.put("SMSTEXT", "Entered From MBS");
                jsonObj.put("CUSTID", custId + "#~#" + AccCustId);
                jsonObj.put("ACCNO", account.substring(0, Integer.parseInt(getString(R.string.alert_accleng))));
                jsonObj.put("CHQNO", cheque);
                jsonObj.put("AMOUNT", amt);
                jsonObj.put("DEVICEID", MBSUtils.getImeiNumber(act));
                jsonObj.put("PAYEENAME", payee);
                jsonObj.put("INSTRUMENTDATE", date);
                jsonObj.put("TRANCODE", tranNo);
                jsonObj.put("IMEINO", MBSUtils.getImeiNumber(act));
                jsonObj.put("SIMNO", MBSUtils.getSimNumber(act));

                jsonArray.put(jsonObj);
                mainjsonObj.put("SMSDATA", jsonArray);
                mainjsonObj.put("MSGID", "2250");
                mainjsonObj.put("METHODCODE", "89");
                Log.e("DSP", "jsonObj==" + jsonObj);
            } catch (JSONException je) {
                je.printStackTrace();
            }
        }



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
                request.addProperty("value1", CryptoClass.Function5(mainjsonObj.toString(), var2));
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
                        showAlert(getString(R.string.alert_err));
                    } else if (retval.indexOf("SUCCESS") > -1) {
                        showAlert(getString(R.string.alert_207));
                    } else {
                        showAlert(getString(R.string.alert_err));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void addCheques(String retval) {
        try {
            ArrayList<String> arrList = new ArrayList<String>();
            String allstr[] = retval.split("~");
            trancode.setText(allstr[1]);
            String[] str = allstr[0].split("#");
            int noOfcheq = str.length;
            String cheq = "";
            arrList.add("Select Instrument Number");
            for (int i = 0; i < noOfcheq; i++) {
                cheq = str[i];
                //Log.e("shubham", "addCheques: " + str[i]);
                arrList.add(cheq);
            }
            String[] cheqArr = new String[arrList.size()];
            cheqArr = arrList.toArray(cheqArr);
            ArrayAdapter<String> accs = new ArrayAdapter<String>(act,
                    R.layout.spinner_item, cheqArr);
            accs.setDropDownViewResource(R.layout.spinner_dropdown_item);
            spi_cheques.setAdapter(accs);

        } catch (Exception e) {
            // System.out.println("" + e);
        }
    }// end addBeneficiaries

    public void newDatePickers() {
        Calendar mcurrentDate = Calendar.getInstance();
        mYear = mcurrentDate.get(Calendar.YEAR);
        mMonth = mcurrentDate.get(Calendar.MONTH);
        mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog mDatePicker = new DatePickerDialog(act, android.R.style.Theme_DeviceDefault_Dialog, (datepicker, selectedyear, selectedmonth, selectedday) -> {
            mYear = selectedyear;
            mMonth = selectedmonth + 1;
            mDay = selectedday;
            if (mDay < 10 || mMonth < 10) {
                if (mMonth < 10 && mDay < 10) {
                    String fm = "0" + mMonth;
                    String fd = "0" + mDay;
                    instruDate.setText(fd + "/" + fm + "/" + mYear);

                } else if (mMonth < 10) {
                    String fm = "0" + mMonth;
                    instruDate.setText(mDay + "/" + fm + "/" + mYear);

                } else if (mDay < 10) {
                    String fd = "0" + mDay;
                    instruDate.setText(fd + "/" + mMonth + "/" + mYear);
                }

            } else {
                instruDate.setText(mDay + "/" + mMonth + "/" + mYear);
            }
        }, mYear, mMonth, mDay);
        mDatePicker.show();
    }

    public void onFromDateCalendarClick(View v) {
        //log.e("Calendar clicked", "######");
        DatePickerDailog dp = new DatePickerDailog(act, dateandtime, new DatePickerDailog.DatePickerListner() {

            public void OnDoneButton(Dialog datedialog, Calendar c) {
                datedialog.dismiss();
                dateandtime.set(Calendar.YEAR, c.get(Calendar.YEAR));
                dateandtime.set(Calendar.MONTH, c.get(Calendar.MONTH));
                dateandtime.set(Calendar.DAY_OF_MONTH,
                        c.get(Calendar.DAY_OF_MONTH));
                String strDate = new SimpleDateFormat("dd/MM/yyyy")
                        .format(c.getTime());
                SimpleDateFormat formatter = new SimpleDateFormat(
                        "dd/MM/yyyy");
                try {

                    instruDate.setText(strDate);


                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            public void OnCancelButton(Dialog datedialog) {
                // TODO Auto-generated method stub
                datedialog.dismiss();
            }
        });
        dp.show();

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

package shree_nagari.mbank;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.security.PrivateKey;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.crypto.spec.SecretKeySpec;

import mbLib.CryptoClass;
import mbLib.CustomDialogClass;
import mbLib.DatabaseManagement;
import mbLib.DatePickerDailog;
import mbLib.DialogBox;
import mbLib.MBSUtils;
//import mbLib.DialogBox;


public class TransferHistory extends Fragment implements OnClickListener {
    MainActivity act;
    TransferHistory transferHistoryObj;
    TextView txt_heading;
    Spinner spi_account_no, spi_transfer_type, spi_status;
    EditText txt_from_dt, txt_to_dt;
    ImageButton spinner_btn1, spinner_btn2, spinner_btn3;// btn_back;
    ImageView btn_home1, btn_logout;
    Button btn_show_history, btn_from_date, btn_to_date;
    ArrayList<String> arrListTemp = new ArrayList<String>();
    DialogBox dbs;
    boolean noAccounts;
    private DatePicker datePicker;
    Calendar dateandtime;
    DatabaseManagement dbms;
    private static String NAMESPACE = "";
    private static String URL = "";
    private static String SOAP_ACTION = "";
    private static final String MY_SESSION = "my_session";
    private static String METHOD_NAME_TRANSFER_HISTORY = "";
    //private static final String METHOD_NAME_TRANSFER_HISTORY = "get";
    SimpleDateFormat df;
    Date dt1, dt2, fromDate;
    String tranType = "", postingStatus = "", fromDt = "", toDt = "", curDate = "", retMess = "", custId = "", str2 = "", stringValue = "", acnt_inf = "", all_acnts = "", accNo = "";
    String respcode = "", retvalweb = "", fundTransferHistoryrespdesc = "";
    int cnt = 0, flag = 0;
    ImageView img_heading;
    PrivateKey var1 = null;
    String var5 = "", var3 = "";
    SecretKeySpec var2 = null;
    String retvalwbs = "",respdesc = "";

    public TransferHistory() {
    }

    @SuppressLint("ValidFragment")
    public TransferHistory(MainActivity a) {
        act = a;
        transferHistoryObj = this;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.transfer_history, container, false);
        var1 = act.var1;
        var3 = act.var3;

        img_heading = (ImageView) rootView.findViewById(R.id.img_heading);
        img_heading.setBackgroundResource(R.mipmap.fund_trnsfer);
        spi_account_no = (Spinner) rootView.findViewById(R.id.spi_account_no);
        spi_transfer_type = (Spinner) rootView.findViewById(R.id.spi_transfer_type);
        spi_status = (Spinner) rootView.findViewById(R.id.spi_status);
        txt_from_dt = (EditText) rootView.findViewById(R.id.txt_from_dt);
        txt_to_dt = (EditText) rootView.findViewById(R.id.txt_to_dt);
        btn_show_history = (Button) rootView.findViewById(R.id.btn_show_history);
        btn_from_date = (Button) rootView.findViewById(R.id.btn_from_date);
        btn_to_date = (Button) rootView.findViewById(R.id.btn_to_date);
        txt_heading = (TextView) rootView.findViewById(R.id.txt_heading);
        txt_heading.setText(getString(R.string.lbl_transfer_history));
        btn_home1 = (ImageView) rootView.findViewById(R.id.btn_home1);
        btn_logout = (ImageView) rootView.findViewById(R.id.btn_logout);
btn_logout.setVisibility(View.GONE);
        spinner_btn1 = (ImageButton) rootView.findViewById(R.id.spinner_btn1);
        spinner_btn2 = (ImageButton) rootView.findViewById(R.id.spinner_btn2);
        spinner_btn3 = (ImageButton) rootView.findViewById(R.id.spinner_btn3);

        //btn_back = (ImageButton) rootView.findViewById(R.id.btn_back);
        //btn_home.setImageResource(R.drawable.ic_home_d);
        //btn_back.setImageResource(R.drawable.backover);
        //btn_back.setOnClickListener(this);
        btn_home1.setOnClickListener(this);
        btn_logout.setOnClickListener(this);
        btn_show_history.setOnClickListener(this);
        btn_from_date.setOnClickListener(this);
        btn_to_date.setOnClickListener(this);
        spinner_btn1.setOnClickListener(this);
        spinner_btn2.setOnClickListener(this);
        spinner_btn3.setOnClickListener(this);
        dateandtime = Calendar.getInstance(Locale.US);
        df = new SimpleDateFormat("dd/MM/yyyy");
        curDate = df.format(dateandtime.getTime());
        dbms = new DatabaseManagement("shree_nagari.mbank", "listMobileBanking");

        //SharedPreferences sp = act.getSharedPreferences(MY_SESSION,Context.MODE_PRIVATE);
        Cursor c1 = dbms.selectFromTable("SHAREDPREFERENCE", "", null);
        if (c1 != null) {
            while (c1.moveToNext()) {
                custId = c1.getString(2);
                Log.e("custId", "......" + custId);
                stringValue = c1.getString(0);
                Log.e("retValStr", "c......" + stringValue);
            }
        }
	/*	custId = sp.getString("custId", "custId");
		stringValue= sp.getString("retValStr","retValStr");*/

        addAccounts(stringValue);
        return rootView;
    }

    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case R.id.btn_show_history:
                accNo = spi_account_no.getSelectedItem().toString();
                fromDt = txt_from_dt.getText().toString().trim();
                toDt = txt_to_dt.getText().toString().trim();
                if (fromDt.length() == 0) {
                    showAlert(getString(R.string.alert_055));
                } else if (toDt.length() == 0) {
                    showAlert(getString(R.string.alert_056));
                }
				/*else if(fromDt>toDt)
				{
					showAlert(getString(R.string.alert_056));
				}*/
                else {
                    CallWebServiceGetTranHistory c = new CallWebServiceGetTranHistory();
                    c.execute();
                }
                break;
            case R.id.btn_from_date:
                onFromDateCalendarClick(arg0);
                break;
            case R.id.spinner_btn1:
                spi_account_no.performClick();
                break;
            case R.id.spinner_btn2:
                spi_transfer_type.performClick();
                break;
            case R.id.spinner_btn3:
                spi_status.performClick();
                break;
            case R.id.btn_to_date:
                //onToDateCalendarClick(arg0);
                String fromdate1 = txt_from_dt.getText().toString().trim();
                if (fromdate1.toString().equals("")) {
                    retMess = getString(R.string.alert_190);
                    showAlert(retMess);

                } else {
                    onToDateCalendarClick(arg0);
                }
                break;

            case R.id.btn_home1:
                Intent in = new Intent(act, NewDashboard.class);
                in.putExtra("VAR1", var1);
                in.putExtra("VAR3", var3);
                startActivity(in);
                act.finish();
                break;

            case R.id.btn_logout:
                CustomDialogClass alert = new CustomDialogClass(act, getString(R.string.lbl_exit)) {
                    @SuppressLint("NonConstantResourceId")
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
                break;
            default:
                break;
        }

    }

    public void post_successlog(String retvalwbs) {
        respcode = "";
        respdesc = "";
        act.finish();
        System.exit(0);

    }

    public class CallWebServicelog extends AsyncTask<Void, Void, Void> {
        JSONObject jsonObj = new JSONObject();
        String ValidationData = "";
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

    public void addAccounts(String str) {
        try {
            ArrayList<String> arrList = new ArrayList<String>();
            String allstr[] = str.split("~");

            int noOfAccounts = allstr.length;
            Accounts acArray[] = new Accounts[noOfAccounts];
            for (int i = 0; i < noOfAccounts; i++) {
                str2 = allstr[i];
                acArray[i] = new Accounts(str2);
                str2 = str2.replaceAll("#", "-");
                String accType = str2.split("-")[2];
                String str2Temp = str2;
                str2 = MBSUtils.get16digitsAccNo(str2);
                if ((accType.equals("SB")) || (accType.equals("CA"))
                        || (accType.equals("LO"))) {
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
            ArrayAdapter<String> debAccs = new ArrayAdapter<String>(act, R.layout.spinner_item, debAccArr);
            //CustomeSpinnerAdapter debAccs = new CustomeSpinnerAdapter(act,android.R.layout.simple_spinner_item, debAccArr);
            debAccs.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spi_account_no.setAdapter(debAccs);

            String[] tranTypeArr = {"Same Bank", "NEFT", "RTGS", "IMPS", "All"};//"QRCODE",
            ArrayAdapter<String> tranTypes = new ArrayAdapter<String>(act, R.layout.spinner_item, tranTypeArr);
            //CustomeSpinnerAdapter tranTypes = new CustomeSpinnerAdapter(act,android.R.layout.simple_spinner_item, tranTypeArr);
            tranTypes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spi_transfer_type.setAdapter(tranTypes);

            String[] postingStatusArr = {"Successful", "Failed", "Pending", "All"};
            ArrayAdapter<String> postingStatus = new ArrayAdapter<String>(act, R.layout.spinner_item, postingStatusArr);
            //CustomeSpinnerAdapter postingStatus = new CustomeSpinnerAdapter(act,android.R.layout.simple_spinner_item, postingStatusArr);
            postingStatus.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spi_status.setAdapter(postingStatus);

            acnt_inf = spi_account_no.getItemAtPosition(
                    spi_account_no.getSelectedItemPosition()).toString();
        } catch (Exception e) {
            System.out.println("" + e);
        }

    }// end addAccount

    public void onFromDateCalendarClick(View v) {
        Log.i("Calendar clicked", "######");
        DatePickerDailog dp = new DatePickerDailog(act,
                dateandtime, new DatePickerDailog.DatePickerListner() {

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
                    dt1 = df.parse(curDate);
                    dt2 = formatter.parse(strDate);
                    if (dt2.compareTo(dt1) > 0) {
                        showAlert(getString(R.string.alert_139));
                        txt_from_dt.setText("");
                    } else {
                        fromDate = dt2;
                        txt_from_dt.setText(strDate);
                    }

                } catch (ParseException e) {
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

    public void onToDateCalendarClick(View v) {
        Log.i("Calendar clicked", "######");
        DatePickerDailog dp = new DatePickerDailog(act,
                dateandtime, new DatePickerDailog.DatePickerListner() {

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
                    dt1 = df.parse(curDate);
                    dt2 = formatter.parse(strDate);
                    long diff = dt2.getTime() - fromDate.getTime();
                    long day = diff / (1000 * 60 * 60 * 24);
                    if (dt2.compareTo(dt1) > 0) {
                        showAlert(getString(R.string.alert_140));
                        txt_to_dt.setText("");
                    } else if (dt2.compareTo(fromDate) < 0) {
                        showAlert(getString(R.string.alert_141));
                        txt_to_dt.setText("");
                    }
							/*else if(day>10)
							{
								showAlert(getString(R.string.alert_142));
								txt_to_dt.setText("");
							}*/
                    else {
                        txt_to_dt.setText(strDate);
                    }

                } catch (ParseException e) {
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
                //Log.e("SetMPIN","SetMPIN...btn CLicked=="+R.id.btn_ok+"==="+v.getId());
                switch (v.getId()) {
                    case R.id.btn_ok:
                        //Log.e("SetMPIN","SetMPIN...CASE trru="+WSCalled);
                        if ((str.equalsIgnoreCase(fundTransferHistoryrespdesc)) && (respcode.equalsIgnoreCase("0"))) {
                            post_successGetTranHistory(retvalweb);
                        } else if ((str.equalsIgnoreCase(fundTransferHistoryrespdesc)) && (respcode.equalsIgnoreCase("1"))) {
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

    class CallWebServiceGetTranHistory extends AsyncTask<Void, Void, Void> {
        String retval = "";
        LoadProgressBar loadProBarObj = new LoadProgressBar(act);
        String ValidationData = "";
        JSONObject obj = new JSONObject();

        protected void onPreExecute() {
            loadProBarObj.show();
            respcode = "";
            retvalweb = "";
            fundTransferHistoryrespdesc = "";
            accNo = spi_account_no.getSelectedItem().toString();
            accNo = accNo.substring(0, 16);
            fromDt = txt_from_dt.getText().toString().trim();
            toDt = txt_to_dt.getText().toString().trim();

            if (spi_transfer_type.getSelectedItemPosition() == 0)
                tranType = "INTBANK";
            else if (spi_transfer_type.getSelectedItemPosition() == 1)
                tranType = "NT";
            else if (spi_transfer_type.getSelectedItemPosition() == 2)
                tranType = "RT";
            else if (spi_transfer_type.getSelectedItemPosition() == 3)
                tranType = "IMPS";
            else
                tranType = "ALL";


            if (spi_status.getSelectedItemPosition() == 0)
                postingStatus = "0";//success
            else if (spi_status.getSelectedItemPosition() == 1)
                postingStatus = "1";//failed
            else if (spi_status.getSelectedItemPosition() == 2)
                postingStatus = "2";//pending
            else
                postingStatus = "3";//all
            //postingStatus=spi_status.getSelectedItem().toString();

            try {
                obj.put("CUSTID", custId);
                obj.put("FRMDATE", fromDt);
                obj.put("TODATE", toDt);
                obj.put("DRACCNO", accNo);
                obj.put("TRANTYPE", tranType);
                obj.put("POSTSTATUS", postingStatus);
                obj.put("IMEINO", MBSUtils.getImeiNumber(act));
                obj.put("SIMNO", MBSUtils.getSimNumber(act));
                obj.put("METHODCODE", "41");
                //ValidationData=MBSUtils.getValidationData(act,obj.toString());
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

        protected Void doInBackground(Void... arg0) {
            String value4 = getString(R.string.namespace);
            String value5 = getString(R.string.soap_action);
            String value6 = getString(R.string.url);
            final String value7 = "callWebservice";
            try {
                String keyStr = CryptoClass.Function2();
                var2 = CryptoClass.getKey(keyStr);
                SoapObject request = new SoapObject(value4, value7);

                request.addProperty("value1", CryptoClass.Function5(obj.toString(), var2));
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
                return null;
            }// end try
            catch (Exception e) {
                e.printStackTrace();
                System.out.println("TransferHistory   Exception" + e);
            }
            return null;
        }// end dodoInBackground2

        protected void onPostExecute(Void paramVoid) {
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
                    retvalweb = jsonObj.getString("RETVAL");
                } else {
                    retvalweb = "";
                }
                if (jsonObj.has("RESPDESC")) {
                    fundTransferHistoryrespdesc = jsonObj.getString("RESPDESC");
                } else {
                    fundTransferHistoryrespdesc = "";
                }

                if (fundTransferHistoryrespdesc.length() > 0) {
                    showAlert(fundTransferHistoryrespdesc);
                } else {
                    if (retvalweb.indexOf("NOREC") > -1) {
                        showAlert(getString(R.string.alert_089));
                    } else if (retvalweb.length() > 0) {
                        post_successGetTranHistory(retvalweb);
                    } else {
                        showAlert(getString(R.string.alert_143));
                    }// end else


                }
				/*		}
					else{
						MBSUtils.showInvalidResponseAlert(act);	
					}*/
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }

    public void post_successGetTranHistory(String retvalweb) {
        respcode = "";
        fundTransferHistoryrespdesc = "";
        JSONArray ja = null;
        try {
            ja = new JSONArray(retvalweb);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (ja.length() > 0) {
            act.setTitle(act.getString(R.string.lbl_transfer_history));
            Fragment fragment = new TransferHistoryRpt(act, retvalweb, accNo);
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_container, fragment).commit();
            act.frgIndex = 541;
        } else {
            showAlert(getString(R.string.alert_089));
        }

    }


    public int chkConnectivity() {// chkConnectivity
        flag = 0;
        ConnectivityManager cm = (ConnectivityManager) act
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        try {
            State state = ni.getState();
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
                        // retMess =
                        // "Network Disconnected. Please Check Network Settings.";
                        retMess = getString(R.string.alert_014);
                        //	showAlert(retMess);
                        dbs = new DialogBox(act);
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
                        flag = 1;
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
}  
	

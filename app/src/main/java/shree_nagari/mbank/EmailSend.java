package shree_nagari.mbank;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
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
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

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

import mbLib.Accountbean;
import mbLib.CryptoClass;
import mbLib.CustomDialogClass;
import mbLib.DatabaseManagement;
import mbLib.DatePickerDailog;
import mbLib.DialogBox;
import mbLib.MBSUtils;

public class EmailSend extends Fragment implements OnClickListener,
        OnFocusChangeListener {

    MainActivity act;
    Context context;
    EmailSend ems;
    TextView txt_heading, txt_from_dt, txt_to_dt;
    Spinner spnr_account;
    DatabaseManagement dbms;
    int dtdff = 0;
    String custId = "", curDate, retMess = "", retval = "", respcode = "", retvalwbs = "", respdesc = "", Freq = "",
            respdesc_web1 = "";
    String stringValue;
    SimpleDateFormat df;
    Date dt1, dt2, fromDate;
    ImageButton btn_home, spinner_btn1;// btn_back;
    Button btn_send_email, btn_from_date, btn_to_date;
    ArrayList<String> arrListTemp = new ArrayList<String>();
    String accNo = "", fromDt = "", toDt = "", Flags = "DREG";
    Calendar dateandtime;
    private static String NAMESPACE = "";
    private static String URL = "";
    private static String SOAP_ACTION = "";
    int cnt = 0, cnt2 = 0, flag = 0;
    boolean wsCalled = false;
    ImageView img_heading;
    ImageView btn_home1, btn_logout;
    DialogBox dbs;
    private static String METHOD_NAME = "";
    PrivateKey var1 = null;
    String var5 = "", var3 = "",AccCustId;
    SecretKeySpec var2 = null;
    int mYear, mMonth, mDay;
    ArrayList<Accountbean> arrList1;


    public EmailSend() {
    }

    @SuppressLint("ValidFragment")
    public EmailSend(MainActivity a) {
        // //System.out.println("EmailReg()");
        act = a;
        ems = this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.emailsend, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        hideKeyboard(getActivity(), rootView);
        closeSoftKeyboard();
        var1 = act.var1;
        var3 = act.var3;
        txt_heading = (TextView) rootView.findViewById(R.id.txt_heading);
        txt_heading.setText(getString(R.string.lbl_send_email));
        spnr_account = (Spinner) rootView.findViewById(R.id.spi_account_no);
        txt_from_dt = (EditText) rootView.findViewById(R.id.txt_from_dt);
        txt_to_dt = (EditText) rootView.findViewById(R.id.txt_to_dt);
        btn_send_email = (Button) rootView.findViewById(R.id.btn_send_email);
        btn_from_date = (Button) rootView.findViewById(R.id.btn_from_date);
        btn_to_date = (Button) rootView.findViewById(R.id.btn_to_date);
        img_heading = (ImageView) rootView.findViewById(R.id.img_heading);

        img_heading.setBackgroundResource(R.mipmap.other_services);
        btn_home = (ImageButton) rootView.findViewById(R.id.btn_home);
        spinner_btn1 = (ImageButton) rootView.findViewById(R.id.spinner_btn1);
        btn_home1 = (ImageView) rootView.findViewById(R.id.btn_home1);
        btn_logout = (ImageView) rootView.findViewById(R.id.btn_logout);
btn_logout.setVisibility(View.GONE);
        btn_home1.setOnClickListener(this);
        btn_logout.setOnClickListener(this);
        dateandtime = Calendar.getInstance(Locale.US);
        btn_send_email.setOnClickListener(this);
        btn_from_date.setOnClickListener(this);
        btn_to_date.setOnClickListener(this);
        spinner_btn1.setOnClickListener(this);


        df = new SimpleDateFormat("dd/MM/yyyy");
        curDate = df.format(dateandtime.getTime());
        dbms = new DatabaseManagement("shree_nagari.mbank", "listMobileBanking");
        Cursor c1 = dbms.selectFromTable("SHAREDPREFERENCE", "", null);// ("select * from ",
        // null);
        if (c1 != null) {
            while (c1.moveToNext()) {
                custId = c1.getString(2);
                // Log.e("custId","......"+custId);
                stringValue = c1.getString(0);
                // Log.e("retvalstr","c......"+stringValue);
            }
        }
        addAccounts(stringValue);

        spnr_account.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,int arg2, long arg3) {
                // TODO Auto-generated method stub
               // Accounts dataModel = (Accounts) arg0.getItemAtPosition(arg2);
                Accountbean dataModel = arrList1.get(arg2);
                AccCustId = dataModel.getAcccustid();

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
        return rootView;
    }

    public void addAccounts(String str) {
        String acnt_inf = "", str2 = "";
        try {
            ArrayList<String> arrList = new ArrayList<String>();
            arrList1 = new ArrayList<>();
            String[] allstr = str.split("~");
            int noOfAccounts = allstr.length;
            Accounts[] acArray = new Accounts[noOfAccounts];
            for (int i = 0; i < noOfAccounts; i++) {

                str2 = allstr[i];
                acArray[i] = new Accounts(str2);

                str2 = str2.replaceAll("#", "-");
                String accType = str2.split("-")[2];
                String AccCustID = str2.split("-")[11];
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
//            String[] debAccArr = new String[arrList.size()];
//            debAccArr = arrList.toArray(debAccArr);
            String[] debAccArr = new String[arrList.size()];
            debAccArr = arrList.toArray(debAccArr);
            ArrayAdapter<String> debAccs = new ArrayAdapter<String>(act,R.layout.spinner_item, debAccArr);
			 //CustomeSpinnerAdapter email_freq=new CustomeSpinnerAdapter(act,android.R.layout.simple_spinner_item,getResources().getStringArray(R.array.email_freq));
            debAccs.setDropDownViewResource(R.layout.spinner_dropdown_item);
            spnr_account.setAdapter(debAccs);
        } catch (Exception e) {
            Log.e("Shubham", "addAccounts: "+e.getMessage() );        }
    }// end addAccount


    public void hideKeyboard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

    }

    private void closeSoftKeyboard() {
        //If using a fragment use getActivity().getCurrentFocus()
        View v = getActivity().getCurrentFocus();

        // If Soft Keyboard is visible, it will be hide
        if (v != null) {
            //If using a fragment use getActivity().getSystemService(...)
            InputMethodManager inputManager
                    = (InputMethodManager)
                    getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }


    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onClick(View arg0) {
        // TODO Auto-generated method stub
        switch (arg0.getId()) {
            case R.id.btn_send_email:
                accNo = spnr_account.getSelectedItem().toString();
                fromDt = txt_from_dt.getText().toString().trim();
                toDt = txt_to_dt.getText().toString().trim();


                if (fromDt.length() == 0) {
                    showAlert(getString(R.string.alert_055));
                } else if (toDt.length() == 0) {
                    showAlert(getString(R.string.alert_056));
                } else if (Flags.equalsIgnoreCase("DREG")) {
                    showAlert(getString(R.string.lbl_not_registered));
                } else {
                    new CallWebService().execute();
                }
                break;
            case R.id.btn_from_date:
                //onFromDateCalendarClick(arg0);
                newDateFromPickers();
                break;
            case R.id.spinner_btn1:
                spnr_account.performClick();
                break;

            case R.id.btn_to_date:
                //onToDateCalendarClick(arg0);
                String fromdate1 = txt_from_dt.getText().toString().trim();
                if (fromdate1.toString().equals("")) {
                    retMess = getString(R.string.alert_190);
                    showAlert(retMess);

                } else {
                    //onToDateCalendarClick(arg0);
                    newDateToPickers();
                }
                break;

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



    public void newDateFromPickers() {
        Calendar mcurrentDate = Calendar.getInstance();
        mYear = mcurrentDate.get(Calendar.YEAR);
        mMonth = mcurrentDate.get(Calendar.MONTH);
        mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);
        @SuppressLint("SetTextI18n")
        DatePickerDialog mDatePicker = new DatePickerDialog(getActivity(), android.R.style.Theme_DeviceDefault_Dialog, (datepicker, selectedyear, selectedmonth, selectedday) -> {
            mYear = selectedyear;
            mMonth = selectedmonth + 1;
            mDay = selectedday;
            if (mDay < 10 || mMonth < 10) {
                if (mMonth < 10 && mDay < 10) {
                    String fm = "0" + mMonth;
                    String fd = "0" + mDay;
                    txt_from_dt.setText(fd + "/" + fm + "/" + mYear);
                } else if (mMonth < 10) {
                    String fm = "0" + mMonth;
                    txt_from_dt.setText(mDay + "/" + fm + "/" + mYear);
                } else if (mDay < 10) {
                    String fd = "0" + mDay;
                    txt_from_dt.setText(fd + "/" + mMonth + "/" + mYear);
                }
            } else {
                txt_from_dt.setText(mDay + "/" + mMonth + "/" + mYear);
            }
        }, mYear, mMonth, mDay);
        mDatePicker.show();
    }

    public void newDateToPickers() {
        Calendar mcurrentDate = Calendar.getInstance();
        mYear = mcurrentDate.get(Calendar.YEAR);
        mMonth = mcurrentDate.get(Calendar.MONTH);
        mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);
        SimpleDateFormat newFormat = new SimpleDateFormat("dd/MM/yyyy");
        @SuppressLint("SetTextI18n") DatePickerDialog mDatePicker = new DatePickerDialog(getActivity(), android.R.style.Theme_DeviceDefault_Dialog, (datepicker, selectedyear, selectedmonth, selectedday) -> {
            try {
                mYear = selectedyear;
                mMonth = selectedmonth + 1;
                mDay = selectedday;
                if (mDay < 10 || mMonth < 10) {
                    if (mMonth < 10 && mDay < 10) {
                        String fm = "0" + mMonth;
                        String fd = "0" + mDay;
                        txt_to_dt.setText(fd + "/" + fm + "/" + mYear);
                    } else if (mMonth < 10) {
                        String fm = "0" + mMonth;
                        txt_to_dt.setText(mDay + "/" + fm + "/" + mYear);

                    } else if (mDay < 10) {
                        String fd = "0" + mDay;
                        txt_to_dt.setText(fd + "/" + mMonth + "/" + mYear);
                    }
                } else {
                    txt_to_dt.setText(mDay + "/" + mMonth + "/" + mYear);
                }
                Date d1 = (newFormat.parse(txt_from_dt.getText().toString().trim()));
                Date d2 = (newFormat.parse(txt_to_dt.getText().toString().trim()));
                long diff = d1.getTime() - d2.getTime();
                long day = diff / (1000 * 60 * 60 * 24);
                if (d1.after(d2)) {
                    showAlert("Enter To Date Greater than From Date");
                    txt_to_dt.setText("");
                } else if (d1.compareTo(d2) == 0) {
                    showAlert(getString(R.string.alert_139));
                    txt_to_dt.setText("");
                } else if (day > 90) {
                    showAlert(getString(R.string.alert_142));
                    txt_to_dt.setText("");
                }

            } catch (Exception e) {
                Log.e("Shubham", "newDateToPickersException: " + e.getMessage());
            }
        }, mYear, mMonth, mDay);
        mDatePicker.show();
    }

    public void onFromDateCalendarClick(View v) {
        Log.i("Calendar clicked", "######");
        DatePickerDailog dp = new DatePickerDailog(act, dateandtime, new DatePickerDailog.DatePickerListner() {

            public void OnDoneButton(Dialog datedialog, Calendar c) {
                datedialog.dismiss();
                dateandtime.set(Calendar.YEAR, c.get(Calendar.YEAR));
                dateandtime.set(Calendar.MONTH, c.get(Calendar.MONTH));
                dateandtime.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH));
                String strDate = new SimpleDateFormat("dd/MM/yyyy").format(c.getTime());
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
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
        DatePickerDailog dp = new DatePickerDailog(act, dateandtime, new DatePickerDailog.DatePickerListner() {

            public void OnDoneButton(Dialog datedialog, Calendar c) {
                datedialog.dismiss();
                dateandtime.set(Calendar.YEAR, c.get(Calendar.YEAR));
                dateandtime.set(Calendar.MONTH, c.get(Calendar.MONTH));
                dateandtime.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH));
                String strDate = new SimpleDateFormat("dd/MM/yyyy").format(c.getTime());
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                try {
                    dt1 = df.parse(curDate);
                    dt2 = formatter.parse(strDate);
                    long diff = dt2.getTime() - fromDate.getTime();
                    long day = diff / (1000 * 60 * 60 * 24);  //
                    if (dt2.compareTo(dt1) > 0) {
                        showAlert(getString(R.string.alert_140));
                        txt_to_dt.setText("");
                    } else if (dt2.compareTo(fromDate) < 0) {
                        showAlert(getString(R.string.alert_141));
                        txt_to_dt.setText("");
                    } else if (day > 90) {
                        showAlert(getString(R.string.alert_142));
                        txt_to_dt.setText("");
                    } else {
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
                        if ((str.equalsIgnoreCase(respdesc_web1))
                                && (respcode.equalsIgnoreCase("0"))) {
                            post_success_web1(retval);
                        } else if ((str.equalsIgnoreCase(respdesc_web1))
                                && (respcode.equalsIgnoreCase("1"))) {
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
                        if (ni.getType() == ConnectivityManager.TYPE_MOBILE || ni.getType() == ConnectivityManager.TYPE_WIFI) {

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


    class CallWebService1 extends AsyncTask<Void, Void, Void> {
        String flag, Email, mpin;
        LoadProgressBar loadProBarObj = new LoadProgressBar(act);
        String ValidationData = "";

        JSONObject jsonObj = new JSONObject();

        protected void onPreExecute() {
            try {
                loadProBarObj.show();
                Freq = "";
                jsonObj.put("CUSTID", custId+"#~#" + AccCustId);
                jsonObj.put("ACCNO", spnr_account.getSelectedItem().toString().substring(0, 16));
                jsonObj.put("IMEINO", MBSUtils.getImeiNumber(act));
                jsonObj.put("SIMNO", MBSUtils.getSimNumber(act));
                jsonObj.put("METHODCODE", "7");
                //ValidationData=MBSUtils.getValidationData(act,jsonObj.toString());

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
        String retStr = retval.split("SUCCESS~")[1];

        if (retStr.indexOf("DREG") > -1) {
            showAlert(getString(R.string.lbl_not_registeredstmt));
            Flags = "DREG";
        } else {
            showAlert(getString(R.string.lbl_registeredstmt));
            Flags = "REG";
            String[] temp = retStr.split("REG~")[1].split("#");
            Freq = temp[1];

        }

    }

    class CallWebService extends AsyncTask<Void, Void, Void> {
        String flag, Email, Freq, mpin;
        LoadProgressBar loadProBarObj = new LoadProgressBar(act);
        String ValidationData = "";
        JSONObject jsonObj = new JSONObject();

        protected void onPreExecute() {
            try {
                loadProBarObj.show();
                mpin = "";
                jsonObj.put("CUSTID", custId+"#~#"+AccCustId);
                jsonObj.put("ACCNO", arrListTemp.get(spnr_account.getSelectedItemPosition()));
                jsonObj.put("FROMDT", fromDt);
                jsonObj.put("TODT", toDt);
                jsonObj.put("IMEINO", MBSUtils.getImeiNumber(act));
                jsonObj.put("SIMNO", MBSUtils.getSimNumber(act));
                jsonObj.put("METHODCODE", "9");
                Log.e("DSP", "emailsend===" + jsonObj);
                //ValidationData=MBSUtils.getValidationData(act,jsonObj.toString());
            } catch (JSONException je) {
                je.printStackTrace();
            }
        }

        protected Void doInBackground(Void... arg0) {// doInBackground2
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
				/*ValidationData=xml_data[1].trim();
				if(ValidationData.equals(MBSUtils.getValidationData(act, xml_data[0].trim())))
				{*/
                Log.e("DSP", "emailsend===" + str);
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
                    post_success(retval);

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
        retMess = retval;
        if (retMess.equalsIgnoreCase("FAILED")) {
            retMess = getString(R.string.login_alert_008);
            //showAlert(retMess);

        }
        //spnr_account.setSelection(0);
        txt_from_dt.setText("");
        txt_to_dt.setText("");
        showAlert(retMess);
        wsCalled = true;
    }

}

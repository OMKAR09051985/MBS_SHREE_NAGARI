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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import mbLib.DateValidator;
import mbLib.DialogBox;
import mbLib.MBSUtils;
//import mbLib.DialogBox;

public class ChequeStatus extends Fragment implements OnClickListener {
    MainActivity act;
    Intent in = null;
    private static String METHOD_NAME = "";
    Button btn_getstatus;
    Spinner spi_account;
    TextView cust_nm, txt_heading;
    EditText txt_chksrno, txt_frmno, txt_tono, txt_amt_frm, txt_amt_to,
            txt_date_frm, txt_date_to;
    RadioGroup radioGroup;
    RadioButton rad_debit, rad_credit;
    int checkedRadioButton;
    String str2 = "";
    Calendar dateandtime;
    SimpleDateFormat df;
    Date dt1, dt2, fromDate;
    String acnt_inf, all_acnts, drcr = "";
    String str = "", retMess = "", cust_name = "", custId = "", regno = "", reTval = "", retvalwbs = "", retval = "";
    int cnt = 0, flag = 0;
    DialogBox dbs;
    ProgressBar pb_wait;
    DateValidator dv;
    private static final String MY_SESSION = "my_session";
    //Editor e;
    String stringValue, fromDt = "", toDt = "", curDate = "", respcode = "", respdesc = "";
    ImageButton spinner_btn, btn_home;//, btn_back;
    ImageView img_heading;
    DatabaseManagement dbms;
    ChequeStatus chqStat = null;
    Button btn_from_date, btn_to_date;
    ImageView btn_home1, btn_logout;
    private static String NAMESPACE = "";
    private static String URL = "";

    private static String SOAP_ACTION = "";
    private static String METHOD_NAME_ISSUED = "";
    private static String METHOD_NAME_DEPOSITED = "";
    PrivateKey var1 = null;
    String var5 = "", var3 = "";
    SecretKeySpec var2 = null;
    String imeino = null, AccCustId;
    int mYear, mMonth, mDay;
    ArrayList<String> arrListTemp = new ArrayList<String>();
    ArrayList<Accountbean> arrList1;

    public ChequeStatus() {
    }

    @SuppressLint("ValidFragment")
    public ChequeStatus(MainActivity a) {
        act = a;
        chqStat = this;
        dbs = new DialogBox(act);

        cust_name = "";
        imeino = MBSUtils.getImeiNumber(act);

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        var1 = act.var1;
        var3 = act.var3;
        dbms = new DatabaseManagement("shree_nagari.mbank", "listMobileBanking");
        Cursor c1 = dbms.selectFromTable("SHAREDPREFERENCE", "", null);//("select * from ", null);
        if (c1 != null) {
            while (c1.moveToNext()) {
                stringValue = c1.getString(0);
                //Log.e("retvalstr","....."+stringValue);
                custId = c1.getString(2);
                //Log.e("custId","......"+custId);

            }
        }
        View rootView = inflater.inflate(R.layout.chq_status_query, container,
                false);
        txt_heading = (TextView) rootView.findViewById(R.id.txt_heading);
        img_heading = (ImageView) rootView.findViewById(R.id.img_heading);
        dateandtime = Calendar.getInstance(Locale.US);
        df = new SimpleDateFormat("dd/MM/yyyy");
        curDate = df.format(dateandtime.getTime());
        //System.out.println("initAll()");
        /*
         * Typeface tf_calibri = Typeface.createFromAsset(act.getAssets(),
         * "fonts/Kozuka-Gothic-Pro-M_26793.ttf");
         */
        dv = new DateValidator();
        //SharedPreferences sp = act.getSharedPreferences(MY_SESSION,
        //			Context.MODE_PRIVATE);
        //e = sp.edit();
        btn_from_date = (Button) rootView.findViewById(R.id.btn_from_date);
        btn_to_date = (Button) rootView.findViewById(R.id.btn_to_date);
        spi_account = (Spinner) rootView.findViewById(R.id.spi_accounts);
        radioGroup = (RadioGroup) rootView.findViewById(R.id.radiogroup1);
        rad_debit = (RadioButton) rootView.findViewById(R.id.radio_debit);
        rad_credit = (RadioButton) rootView.findViewById(R.id.radio_credit);
        rad_debit.setChecked(true);
        btn_getstatus = (Button) rootView.findViewById(R.id.btn_getStaus);
        txt_chksrno = (EditText) rootView.findViewById(R.id.txt_chk_ser);
        txt_frmno = (EditText) rootView.findViewById(R.id.txt_chk_from);
        txt_tono = (EditText) rootView.findViewById(R.id.txt_chk_to);
        txt_amt_frm = (EditText) rootView.findViewById(R.id.txt_amt_frm);
        txt_amt_to = (EditText) rootView.findViewById(R.id.txt_amt_to);
        txt_date_frm = (EditText) rootView.findViewById(R.id.txt_date_frm);
        txt_date_to = (EditText) rootView.findViewById(R.id.txt_date_to);
        btn_home = (ImageButton) rootView.findViewById(R.id.btn_home);
        /*btn_back = (ImageButton) rootView.findViewById(R.id.btn_back);*/
        btn_from_date.setOnClickListener(this);
        btn_to_date.setOnClickListener(this);

        btn_home1 = (ImageView) rootView.findViewById(R.id.btn_home1);
        btn_logout = (ImageView) rootView.findViewById(R.id.btn_logout);
btn_logout.setVisibility(View.GONE);
        btn_home1.setOnClickListener(this);
        btn_logout.setOnClickListener(this);

        //btn_home.setImageResource(R.drawable.ic_home_d);
        //btn_back.setImageResource(R.drawable.backover);
		/*Calendar c = Calendar.getInstance();
		System.out.println("Current time => " + c.getTime());

		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		String formattedDate = df.format(c.getTime());
		txt_date_to.setText(formattedDate);
		c.add(Calendar.DAY_OF_MONTH, -7);
		formattedDate = df.format(c.getTime());
		txt_date_frm.setText("01/01/2015");
		System.out.println("" + formattedDate);*/
        //Log.e("@stringValue", stringValue);
        all_acnts = stringValue;
        txt_heading.setText(getString(R.string.lbl_cheque_status));
        img_heading.setBackgroundResource(R.mipmap.checkbkstatus);
        //btn_back.setOnClickListener(this);
        btn_home.setOnClickListener(this);
        addAccounts(all_acnts);
        pb_wait = (ProgressBar) rootView.findViewById(R.id.pb_wait8);
        pb_wait.setMax(10);
        pb_wait.setProgress(1);
        pb_wait.setVisibility(ProgressBar.INVISIBLE);

        // btn_getstatus.setTypeface(tf_calibri);

        //cust_name = sp.getString("cust_name", "cust_name");
        // cust_nm = (TextView) findViewById(R.id.txt_custname);
        // cust_nm.setText(cust_name);

        btn_getstatus.setOnClickListener(this);
        radioGroup.setOnClickListener(this);
        rad_debit.setOnClickListener(this);
        spi_account.requestFocus();
        spinner_btn = (ImageButton) rootView.findViewById(R.id.spinner_btn);
        spinner_btn.setOnClickListener(chqStat);
        return rootView;
    }

    public void addAccounts(String str) {
        //Log.e("addAccounts()@CheqStatus", str);

        try {
            ArrayList<String> arrList = new ArrayList<String>();
            arrList1 = new ArrayList<>();
            String allstr[] = str.split("~");
            // Log.e("@addAccounts","Records Are :"+allstr[0]);
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
            // Log.e("@addAccounts","BalanceEnquiry Accounts:::" + allstr[0]);
            // String str1[] = allstr[1].split(",-,");
            // int noOfAccounts = str1.length;
            int noOfAccounts = allstr.length;
            // Log.e("@addAccounts","noOfAccounts:" + noOfAccounts);
            Accounts acArray[] = new Accounts[noOfAccounts];
            for (int i = 0; i < noOfAccounts; i++) {
                // System.out.println(i + "----STR1-----------" + str1[i]);
                // str2 = str1[i];
                // Log.e("Debug@>>>>>>>"+i,"<"+allstr[i]+">");
                str2 = allstr[i];
                // Log.e("Debug@"+i,"str2: "+allstr[i]);
                acArray[i] = new Accounts(str2);
                // Log.e("@Here I M 1",str2);
                str2 = str2.replaceAll("#", "-");
                // Log.e("@Here I M 2",str2);
                String accType = str2.split("-")[2];
                String oprcd = str2.split("-")[7];
                String AccCustID = str2.split("-")[11];

                //String str2Temp = str2;
                // Log.e("@Here I M 3",str2Temp);
                str2 = MBSUtils.get16digitsAccNo(str2);
                // Log.e("@Here I M 4",str2);

                if (((accType.equals("SB")) || (accType.equals("CA"))
                        || (accType.equals("LO"))) && oprcd.equalsIgnoreCase("O")) {
                    Accountbean accountbeanobj = new Accountbean();
                    accountbeanobj.setAccountinfo(str2 + " (" + MBSUtils.getAccTypeDesc(accType) + ")");
                    accountbeanobj.setAccountNumber(str2);
                    accountbeanobj.setAcccustid(AccCustID);
                    arrList1.add(accountbeanobj);

                    arrList.add(str2 + " (" + MBSUtils.getAccTypeDesc(accType) + ")");
                    // Log.e("IMP@>>>>>>>",str2);
                    arrListTemp.add(str2);
                }
            }

            /*
             * ArrayAdapter<String> arrAdpt = new ArrayAdapter<String>(this,
             * android.R.layout.simple_spinner_item, arrList);
             * arrAdpt.setDropDownViewResource
             * (android.R.layout.simple_spinner_dropdown_item);
             * spi_account.setAdapter(arrAdpt);
             */

            String[] accArr = new String[arrList.size()];
            accArr = arrList.toArray(accArr);
			/*CustomeSpinnerAdapter accs = new CustomeSpinnerAdapter(act,
					R.layout.spinner_layout, accArr);*/
            ArrayAdapter<String> accs = new ArrayAdapter<String>(act, R.layout.spinner_item, accArr);
            //CustomeSpinnerAdapter accs = new CustomeSpinnerAdapter(act,android.R.layout.simple_spinner_item, accArr);
            accs.setDropDownViewResource(R.layout.spinner_dropdown_item);
            spi_account.setAdapter(accs);

            //Log.e("@ChqStatus ", "Exiting from adding accounts");

            acnt_inf = spi_account.getItemAtPosition(spi_account.getSelectedItemPosition()).toString();
            AccCustId = arrList1.get(spi_account.getSelectedItemPosition()).getAcccustid();
            //Log.e("@ChqStatu", acnt_inf);

        } catch (Exception e) {
            System.out.println("" + e);
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

    public String getAccounts() {
        Bundle bnd = act.getIntent().getExtras();
        String str = bnd.getString("accounts");
        System.out.println("accounts--------------" + str);
        return str;
    }


    class CallWebService extends AsyncTask<Void, Void, Void> {
        LoadProgressBar loadProBarObj = new LoadProgressBar(act);
        JSONObject jsonObj = new JSONObject();
        String all_str = "", branch_cd = "", schm_cd = "", acnt_no = "";
        String chksrno = "", frmDate = "", toDate = "";
        String frmno = "", tono = "", frmAmt = "", toAmt = "";
        String ValidationData = "";

        @Override
        protected void onPreExecute() {
            try {
                respcode = "";
                retval = "";
                respdesc = "";

                loadProBarObj.show();

                int i = 0;


                all_str = arrListTemp.get(spi_account.getSelectedItemPosition());
                chksrno = txt_chksrno.getText().toString().trim();
                if (chksrno == null)
                    chksrno = "";

                frmno = txt_frmno.getText().toString().trim();
                frmno = !frmno.equals("") ? frmno : "";

                if (frmno == null)
                    frmno = "";

                tono = txt_tono.getText().toString().trim();
                if (tono == null)
                    tono = "";

                frmAmt = txt_amt_frm.getText().toString().trim();
                if (frmAmt == null)
                    frmAmt = "";

                toAmt = txt_amt_to.getText().toString().trim();
                if (toAmt == null)
                    toAmt = "";

                toAmt = txt_amt_to.getText().toString().trim();
                if (toAmt == null)
                    toAmt = "";


                frmDate = txt_date_frm.getText().toString().trim();
                if (frmDate == null)
                    frmDate = "";
                toDate = txt_date_to.getText().toString().trim();
                if (toDate == null)
                    toDate = "";

                jsonObj.put("ACCNO", all_str);
                jsonObj.put("CUSTID", custId + "#~#" + AccCustId);
                jsonObj.put("CHQ_SR", chksrno);
                jsonObj.put("FRM_CHQNO", frmno + "");
                jsonObj.put("TO_CHQNO", tono + "");
                jsonObj.put("FRM_AMT", frmAmt + "");
                jsonObj.put("TO_AMT", toAmt + "");
                jsonObj.put("FRM_DT", frmDate);
                jsonObj.put("TO_DT", toDate);
                jsonObj.put("DRCR", drcr);
                jsonObj.put("IMEINO", imeino);
                jsonObj.put("SIMNO", MBSUtils.getSimNumber(act));
                if (drcr.equalsIgnoreCase("DR")) {

                    jsonObj.put("METHODCODE", "11");
                } else {

                    jsonObj.put("METHODCODE", "12");
                }
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
                HttpTransportSE androidHttpTransport = new HttpTransportSE(value6, 90000);
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
                //System.out.println(e.getMessage());
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
                    ///Log.e("Debug@decryptedRetVal", decryptedRetVal);

                    if (retval.indexOf("FAILED") > -1) {

                        retMess = getString(R.string.alert_051);// No Cheques Found For
                        // Given Criteria.
                        showAlert(retMess);

                    } else if (retval.indexOf("NODATA") > -1) {
                        retMess = getString(R.string.alert_089);// No Record Found
                        showAlert(retMess);
                        clearForm();
                    } else if (retval.indexOf("SUCCESS") > -1) {

                        post_success(retval);

                    }
                }
/*
				}else{
					MBSUtils.showInvalidResponseAlert(act);	
				}*/
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }


        private void clearForm() {
            // TODO Auto-generated method stub
            rad_debit.setChecked(true);
            txt_chksrno.setText("");
            txt_frmno.setText("");
            txt_tono.setText("");
            txt_amt_frm.setText("");
            txt_amt_to.setText("");
            txt_date_frm.setText("");
            txt_date_to.setText("");
        }
    }

    @Override
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
            case R.id.spinner_btn:
                spi_account.performClick();
                break;
            case R.id.btn_from_date:
                //onFromDateCalendarClick(v);
                newDateFromPickers();
                break;
            case R.id.btn_to_date:
                //onToDateCalendarClick(v);
                String fromdate = txt_date_frm.getText().toString().trim();
                if (fromdate.toString().equals("")) {
                    retMess = getString(R.string.alert_190);
                    showAlert(retMess);
                } else {
                    //onToDateCalendarClick(v);
                    newDateToPickers();
                }
                break;
            case R.id.btn_getStaus:
                Log.e("@DEBUG", "in btn_getStaus()");
                int frmno = 0,tono = 0,frmamt = 0,toamt = 0;
                String frmdt = txt_date_frm.getText().toString().trim();
                String todt = txt_date_to.getText().toString().trim();
                String frmnum = txt_frmno.getText().toString().trim();
                String tonum = txt_tono.getText().toString().trim();
                String frm_amt = txt_amt_frm.getText().toString().trim();
                String to_amt = txt_amt_to.getText().toString().trim();

                if (frmnum.equals("") && frm_amt.equals("") && frmdt.equals("")) {
                    retMess = getString(R.string.alert_052);
                    showAlert(retMess);

                }
                // Cheque No Range validation
                else if (!frmnum.equals("") && !tonum.equals("")) {
                    frmno = Integer.parseInt("" + frmnum);
                    tono = Integer.parseInt("" + tonum);
                    //System.out.println("From No----" + frmno);
                    //System.out.println("To No----" + tono);
                    // if (frmno > tono && frmno != tono) {
                    if (frmno > tono) {
                        System.out.println("from less than to");
                        // retMess = "To No. Of Cheque Must Greater Than From No.";
                        retMess = getString(R.string.alert_053);
                        showAlert(retMess);
                    } else {
                        // flag = chkConnectivity();
                        // if (flag == 0)
                        {
                            callReport();
                        }
                    }
                }
                // Amount Range validation
			else if (!frm_amt.equals("") && !to_amt.equals("")) {
				frmamt = Integer.parseInt("" + frm_amt);
				toamt = Integer.parseInt("" + to_amt);
				//System.out.println("From Amount----" + frmamt);
				//System.out.println("To Amount----" + toamt);

				if (frmamt > toamt && frmamt != toamt) {
					//System.out.println("from less than to");
					// retMess = "To Amount Must Greater Than From Amount.";
					retMess = getString(R.string.alert_054);
					showAlert(retMess);
				} else {
					// flag = chkConnectivity();
					// if (flag == 0)
					{
						callReport();
					}
				}
			}
                else if (!frmdt.equals("")) {
                    if (!dv.validate(frmdt)) {
                        // retMess = "Please Enter Valiid From Date.";
                        retMess = getString(R.string.alert_055);
                        showAlert(retMess);
                    } else {
                        // flag = chkConnectivity();
                        // if (flag == 0)
                        {
                            callReport();
                        }
                    }
                } else if (!todt.equals("")) {

                    if (!dv.validate(todt)) {
                        // retMess = "Please Enter Valiid To Date.";
                        retMess = getString(R.string.alert_056);
                        showAlert(retMess);
                    } else {
                        // flag = chkConnectivity();
                        // if (flag == 0)
                        {
                            callReport();
                        }
                    }
                }
                // Date Range validation
                else if (!frmdt.equals("") && !todt.equals("")) {

                    //System.out.println("For Date Range Validation");
                    if (compareDate(frmdt, todt)) {
                        // retMess = "To Date Must Greater Than From Date.";
                        retMess = getString(R.string.alert_057);
                        showAlert(retMess);
                    } else {
                        // flag = chkConnectivity();
                        // if (flag == 0)
                        {
                            callReport();
                        }

                    }
                } else {

                    {
                        callReport();
                    }

                }
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

    /*
     * public void onRadioButtonClicked(View view) { // Is the button now
     * checked? boolean checked = ((RadioButton) view).isChecked();
     *
     * // Check which radio button was clicked switch (view.getId()) { case
     * R.id.radio_debit: if (checked) { drcr = "DR";
     * System.out.println("If Debit------" + drcr); } break; case
     * R.id.radio_credit: if (checked) { drcr = "CR";
     * System.out.println("If Credit------" + drcr); } break; } }
     */

    public void callReport() {
        if (rad_debit.isChecked()) {
            drcr = "DR";
        } else
            drcr = "CR";
        new CallWebService().execute();

    }

    public boolean compareDate(String sysdate, String date1) {
        // If date1<sysdate then returns true otherwise returns false
        //System.out.println("Sysdate:" + sysdate + "colDate :" + date1);
        boolean flg = false;
        String sdate = sysdate;
        String othrdate = date1;

        int sdtln = sdate.length();
        int odtln = date1.length();

        int first = sdate.indexOf("/");
        //System.out.println("first=" + first);
        int second = sdate.indexOf("/", first + 1);
        //System.out.println("second=" + second);

        int first1 = date1.indexOf("/");
        //System.out.println("first1=" + first1);
        int second1 = date1.indexOf("/", first1 + 1);
        //System.out.println("second1=" + second1);

        int year = Integer.parseInt(sdate.substring(second + 1, sdtln));
        int year1 = Integer.parseInt(othrdate.substring(second1 + 1, odtln));
        // System.out.println("year="+sdate.substring(6,
        // 10)+"  year1="+othrdate.substring(6, 10));
        //System.out.println("year=" + year + "  year1=" + year1);
        if (year1 <= year) {
            if (year1 < year) {
                //System.out.println("year1<year");
                flg = true;
                return true;
            } else if (year1 == year) {
                int mon = Integer.parseInt(sdate.substring(first + 1, second));
                int mon1 = Integer.parseInt(othrdate.substring(first1 + 1,
                        second1));
                //System.out.println("mon=" + mon + "  mon1=" + mon1);
                if (mon1 > mon) {
                    ///System.out.println("mon1>mon");
                    flg = false;
                    return false;
                } else if (mon1 < mon) {
                    //System.out.println("mon1<mon");
                    flg = true;
                    return true;
                } else if (mon1 == mon) {
                    ///System.out.println("mon1==mon");
                    int day = Integer.parseInt(sdate.substring(0, first));
                    int day1 = Integer.parseInt(othrdate.substring(0, first1));
                    //System.out.println("day=" + day + "  day1=" + day1);
                    if (day1 >= day) {
                        //System.out.println("day1>=day");
                        flg = false;
                        return false;
                    } else {
                        ///System.out.println("day1<day-----");
                        flg = true;
                        return true;
                    }
                }
            }

        } else {
            //System.out.println("Flag=" + flg);
            return false;
        }
        //System.out.println("Flag=" + flg);
        return flg;
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
    }

    public void newDateFromPickers() {
        Calendar mcurrentDate = Calendar.getInstance();
        mYear = mcurrentDate.get(Calendar.YEAR);
        mMonth = mcurrentDate.get(Calendar.MONTH);
        mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);

        @SuppressLint("SetTextI18n")
        DatePickerDialog mDatePicker = new DatePickerDialog(act, android.R.style.Theme_DeviceDefault_Dialog, (datepicker, selectedyear, selectedmonth, selectedday) -> {
            mYear = selectedyear;
            mMonth = selectedmonth + 1;
            mDay = selectedday;
            if (mDay < 10 || mMonth < 10) {
                if (mMonth < 10 && mDay < 10) {
                    String fm = "0" + mMonth;
                    String fd = "0" + mDay;
                    txt_date_frm.setText(fd + "/" + fm + "/" + mYear);

                } else if (mMonth < 10) {
                    String fm = "0" + mMonth;
                    txt_date_frm.setText(mDay + "/" + fm + "/" + mYear);

                } else if (mDay < 10) {
                    String fd = "0" + mDay;
                    txt_date_frm.setText(fd + "/" + mMonth + "/" + mYear);
                }
            } else {
                txt_date_frm.setText(mDay + "/" + mMonth + "/" + mYear);
            }
        }, mYear, mMonth, mDay);
        mDatePicker.show();
    }

    public void newDateToPickers() {
        Calendar mcurrentDate = Calendar.getInstance();
        mYear = mcurrentDate.get(Calendar.YEAR);
        mMonth = mcurrentDate.get(Calendar.MONTH);
        mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);
        SimpleDateFormat newFormat = new SimpleDateFormat("dd/mm/yyyy");

        DatePickerDialog mDatePicker = new DatePickerDialog(act, android.R.style.Theme_DeviceDefault_Dialog, (datepicker, selectedyear, selectedmonth, selectedday) -> {
            mYear = selectedyear;
            mMonth = selectedmonth + 1;
            mDay = selectedday;
            if (mDay < 10 || mMonth < 10) {
                if (mMonth < 10 && mDay < 10) {
                    String fm = "0" + mMonth;
                    String fd = "0" + mDay;
                    txt_date_to.setText(fd + "/" + fm + "/" + mYear);
                } else if (mMonth < 10) {
                    String fm = "0" + mMonth;
                    txt_date_to.setText(mDay + "/" + fm + "/" + mYear);

                } else if (mDay < 10) {
                    String fd = "0" + mDay;
                    txt_date_to.setText(fd + "/" + mMonth + "/" + mYear);
                }
            } else {
                txt_date_to.setText(mDay + "/" + mMonth + "/" + mYear);
            }
            if (txt_date_frm.getText().toString().equals(txt_date_to.getText().toString())) {
                showAlert(getString(R.string.alert_139));
                txt_date_to.setText("");
            }
            try {
                Date d1 = (newFormat.parse(txt_date_frm.getText().toString().trim()));
                Date d2 = (newFormat.parse(txt_date_to.getText().toString().trim()));
                if (d1.after(d2)) {
                    showAlert("Enter to date greater than from date");
                    txt_date_to.setText("");
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }, mYear, mMonth, mDay);
        mDatePicker.show();
    }


    public void onFromDateCalendarClick(View v) {
        Log.e("Calendar clicked", "######");
        DatePickerDailog dp = new DatePickerDailog(act,
                dateandtime, new DatePickerDailog.DatePickerListner() {

            public void OnDoneButton(Dialog datedialog, Calendar c) {
                datedialog.dismiss();
                dateandtime.set(Calendar.YEAR, c.get(Calendar.YEAR));
                dateandtime.set(Calendar.MONTH, c.get(Calendar.MONTH));
                dateandtime.set(Calendar.DAY_OF_MONTH,
                        c.get(Calendar.DAY_OF_MONTH));
                String strDate = new SimpleDateFormat("dd/MM/yyyy").format(c.getTime());
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                try {
                    dt1 = df.parse(curDate);
                    dt2 = formatter.parse(strDate);
                    if (dt2.compareTo(dt1) > 0) {
                        showAlert(getString(R.string.alert_139));
                        txt_date_frm.setText("");
                    } else {
                        fromDate = dt2;
                        txt_date_frm.setText(strDate);
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
        Log.e("Calendar clicked", "######");
        DatePickerDailog dp = new DatePickerDailog(act,
                dateandtime, new DatePickerDailog.DatePickerListner() {

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
                    long day = diff / (1000 * 60 * 60 * 24);
                    if (dt2.compareTo(dt1) > 0) {
                        showAlert(getString(R.string.alert_140));
                        txt_date_to.setText("");
                    } else if (dt2.compareTo(fromDate) < 0) {
                        showAlert(getString(R.string.alert_141));
                        txt_date_to.setText("");
                    }
							/*else if(day>10)
							{
								showAlert(getString(R.string.alert_142));
								txt_to_dt.setText("");
							}*/
                    else {
                        txt_date_to.setText(strDate);
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

    public void post_success(String retval) {


        try {
            Bundle bnd1 = new Bundle();
            respcode = "";
            respdesc = "";

            bnd1.putString("transactions", retval.split("SUCCESS~")[1]);
            bnd1.putString("fromWhere", "CHQ_STATUS");

            Fragment chqStatRptFragment = new ChequeStatusRep(act);

            chqStatRptFragment.setArguments(bnd1);

            act.setTitle(act.getString(R.string.lbl_title_cheque_status_report));
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_container, chqStatRptFragment)
                    .commit();
            act.frgIndex = 74;
        } catch (Exception e) {
            // TODO: handle exception
            Log.e("Debug@", "This shouldn't be here");
        }


    }


}

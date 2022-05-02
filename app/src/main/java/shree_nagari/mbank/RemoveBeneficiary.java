package shree_nagari.mbank;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
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

import android.app.Fragment;

import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.security.PrivateKey;
import java.util.ArrayList;

import javax.crypto.spec.SecretKeySpec;

import mbLib.CryptoClass;
import mbLib.CustomDialogClass;
import mbLib.DatabaseManagement;
import mbLib.DialogBox;
import mbLib.MBSUtils;
import mbLib.MyThread;

public class RemoveBeneficiary extends Fragment implements OnClickListener {
    MainActivity act;
    ProgressBar p_wait;
    EditText txtAccNo;
    EditText txtName, txtmobNo, txtEmail, txtNick_Name;
    TextView lblTitle, txt_heading;
    ImageButton  btn_back, spinner_btn;
    Button btn_remove_bnf, btn_fetchName;
    Spinner spi_sel_beneficiery;
    ImageView img_heading,btn_home1,btn_logout;
    DialogBox dbs;
    DatabaseManagement dbms;
    String flg = "false", respcode = "", retval = "", respdesc = "", respdesc_web2 = "";
    String retvalweb = "", respdescbeneficiaries = "", respdescsave_beneficiary = "";
    private static final String MY_SESSION = "my_session";
    private String benInfo = "";
    private static String NAMESPACE = "";
    private static String URL = "";
    private static String SOAP_ACTION = "";
    private static String METHOD_NAME1 = "";
    private static String METHOD_NAME2 = "";
    private MyThread t1;

    int cnt = 0, flag = 0;
    String custId = "", accNo = "", accNm = "", mobNo = "", nickNm = "", mailId = "";
    String str = "", retMess = "", cust_name = "", tmpXMLString = "", retVal = "", mobPin = "", benSrno = null, nickname = "";
    private String userId;
    boolean WSCalled = false;
    public String encrptdMpin, encrptdUTranMpin;
    PrivateKey var1 = null;
    String var5 = "", var3 = "",retvalwbs="";
    SecretKeySpec var2 = null;

    public RemoveBeneficiary() {
    }

    @SuppressLint("ValidFragment")
    public RemoveBeneficiary(MainActivity a) {
        //System.out.println("ListBeneficiary()" + a);
        act = a;
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        var1 = act.var1;
        var3 = act.var3;
        View rootView = inflater.inflate(R.layout.remove_beneficiary, container, false);
        act.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        dbms = new DatabaseManagement("shree_nagari.mbank", "listMobileBanking");
        txtAccNo = (EditText) rootView.findViewById(R.id.txtAccNo2);
        lblTitle = (TextView) rootView.findViewById(R.id.lblTitle);
        //btn_fetchName=(Button)rootView.findViewById(R.id.btn_fetchName2);
        txtName = (EditText) rootView.findViewById(R.id.txtName2);
        txtmobNo = (EditText) rootView.findViewById(R.id.txtmobNo2);
        txtEmail = (EditText) rootView.findViewById(R.id.txtEmail2);
        txtNick_Name = (EditText) rootView.findViewById(R.id.txtNick_Name2);
        btn_remove_bnf = (Button) rootView.findViewById(R.id.btn_remove_bnf);
        //btn_remove_bnf.setText("Remove");
        txt_heading = (TextView) rootView.findViewById(R.id.txt_heading);
        img_heading = (ImageView) rootView.findViewById(R.id.img_heading);
        //img_heading.setBackgroundResource(R.mipmap.remove_beneficiary);
        btn_home1 = (ImageView) rootView.findViewById(R.id.btn_home1);
        btn_logout = (ImageView) rootView.findViewById(R.id.btn_logout);
btn_logout.setVisibility(View.GONE);
        //btn_back=(ImageButton)rootView.findViewById(R.id.btn_back);
        //btn_home.setImageResource(R.mipmap.ic_home_d);
        //btn_back.setImageResource(R.mipmap.backover);
        img_heading.setBackgroundResource(R.mipmap.benefeciary);
        btn_home1.setOnClickListener(this);
        btn_logout.setOnClickListener(this);
        txt_heading.setText(getString(R.string.lbl_remove_benf));
        lblTitle.setText(getString(R.string.lbl_remove_benf));
        p_wait = (ProgressBar) rootView.findViewById(R.id.pro_bar);
        p_wait.setMax(10);
        p_wait.setProgress(1);
        //btn_fetchName.setVisibility(Button.INVISIBLE);
        txtAccNo.setEnabled(false);
        txtName.setEnabled(false);
        txtmobNo.setEnabled(false);
        txtEmail.setEnabled(false);
        txtNick_Name.setEnabled(false);
        //btn_fetchName.setOnClickListener(this);
        btn_remove_bnf.setOnClickListener(this);
        spi_sel_beneficiery = (Spinner) rootView.findViewById(R.id.sameBnkTranspi_sel_beneficiery);
        spinner_btn = (ImageButton) rootView.findViewById(R.id.spinner_btn);
        spinner_btn.setOnClickListener(this);
        Cursor c1 = dbms.selectFromTable("SHAREDPREFERENCE", "", null);//("select * from ", null);
        if (c1 != null) {
            while (c1.moveToNext()) {
                custId = c1.getString(2);
                Log.e("custId", "......" + custId);
                userId = c1.getString(3);
                Log.e("UserId", "......" + userId);
            }
        }

        this.flag = chkConnectivity();
        if (this.flag == 0) {
            new CallWebFetchBenfService().execute();
        }
        spi_sel_beneficiery.setOnItemSelectedListener(new OnItemSelectedListener() {
		  String benAccountNumber = "";
		  String benAccountName = "";
		  String benMobNo = "";
		  String benEmail = "";
		  String benNickname = "";

		  @Override
		  public void onItemSelected(AdapterView<?> arg0, View arg1,
									 int arg2, long arg3) {
			  String str = spi_sel_beneficiery.getItemAtPosition(spi_sel_beneficiery.getSelectedItemPosition()).toString();

			  if (str.equalsIgnoreCase("Select Beneficiary")) {
				  txtAccNo.setText("");
				  txtName.setText("");
				  txtmobNo.setText("");
				  txtEmail.setText("");
				  txtNick_Name.setText("");
			  }

			  if (arg2 != 0) {
				  if (str.equalsIgnoreCase("Select Beneficiary")) {
					  txtAccNo.setText("");
					  txtName.setText("");
					  txtmobNo.setText("");
					  txtEmail.setText("");
					  txtNick_Name.setText("");
				  }
				  String allStr[] = benInfo.split("~");
				  Log.e("Str  == ", "Str == " + str);
				  Log.e("Str  == ", "Str == " + str);
				  for (int i = 1; i <= allStr.length; i++) {
					  String str1[] = allStr[i - 1].split("#");

					  nickname = str1[2] + "(" + str1[1] + ")";
					  //  if(str.indexOf("("+str1[1]+")")>-1)
					  // if(str.indexOf(str1[2])>-1)
					  if (str.equalsIgnoreCase(nickname)) {
						  Log.e("indexOf == ", " indexOf " + (str1[2]));
						  System.out.println("========== inside if ============");
						  benSrno = str1[0];
						  benAccountName = str1[1];
						  benNickname = str1[2];
						  benAccountNumber = str1[3];
						  benMobNo = str1[6];
						  benEmail = str1[7];

						  txtAccNo.setText(benAccountNumber);
						  txtName.setText(benAccountName);

						  if (benEmail.equals("NA")) {
							  benEmail = "";
						  }
						  if (benMobNo.equals("NA")) {
							  benMobNo = "";
						  }
						  txtmobNo.setText(benMobNo);
						  txtEmail.setText(benEmail);
						  txtNick_Name.setText(benNickname);
					  }

				  }//end for
			  }
		  }

		  @Override
		  public void onNothingSelected(AdapterView<?> arg0) {
			  // TODO Auto-generated method stub

		  }

	  }
        );

        t1 = new MyThread(Integer.parseInt(getString(R.string.timeOutInSecs)), act, var1, var3);
        t1.start();

        return rootView;
    }


    private void addBeneficiaries(String retval) {
        //System.out.println("================ IN addBeneficiaries() of RemoveBeneficiary ======================");
        //System.out.println("SameBankTransfer IN addBeneficiaries()" + retval);

        try {
            ArrayList<String> arrList = new ArrayList<String>();
            String allstr[] = retval.split("~");
            //System.out.println("OtherBankTranIMPS Accounts:::" + allstr[1]);
            //int noOfAccounts = str1.length;
            int noOfben = allstr.length;
            //System.out.println("SameBankTransfer noOfAccounts:" + noOfAccounts);
            String benName = "";
            arrList.add("Select Beneficiary");

            for (int i = 1; i <= noOfben; i++) {
                //System.out.println(i + "----STR1-----------" + allstr[i-1]);
                String[] str2 = allstr[i - 1].split("#");
                benName = str2[2] + "(" + str2[1] + ")";
                arrList.add(benName);
                //System.out.println("=============== benificiary Name is:======"+benName);
            }
            //spi_sel_beneficiery
            //System.out.println("================ IN addBeneficiaries() 1 ======================");
			/*
			ArrayAdapter<String> arrAdpt = new ArrayAdapter<String>(this,R.layout.spinner_item, arrList);
			System.out.println("================ IN addBeneficiaries() 2 ======================");
			arrAdpt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			System.out.println("================ IN addBeneficiaries() 3 ======================");
			spi_sel_beneficiery.setAdapter(arrAdpt);
			System.out.println("================ IN addBeneficiaries() 4 ======================");
			*/

            String[] benfArr = new String[arrList.size()];
            benfArr = arrList.toArray(benfArr);
            //CustomeSpinnerAdapter accs=new CustomeSpinnerAdapter(act, R.layout.spinner_layout, benfArr);
            ArrayAdapter<String> accs = new ArrayAdapter<String>(act, R.layout.spinner_item, benfArr);
            accs.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spi_sel_beneficiery.setAdapter(accs);


            //System.out.println("================ exit from  addBeneficiaries() ======================");
        } catch (Exception e) {
            System.out.println("" + e);
        }
    }//end addBeneficiaries

    public void initAll() {
        txtAccNo.setText("");
        //btn_fetchName.setText("");
        txtName.setText("");
        txtmobNo.setText("");
        txtEmail.setText("");
        txtNick_Name.setText("");
    }

    public void showAlert(final String str) {

        ErrorDialogClass alert = new ErrorDialogClass(act, "" + str) {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btn_ok:
                        if ((str.equalsIgnoreCase(respdescbeneficiaries))
                                && (respcode.equalsIgnoreCase("0"))) {
                            post_successget_beneficiaries(retvalweb);
                        } else if ((str.equalsIgnoreCase(respdescbeneficiaries))
                                && (respcode.equalsIgnoreCase("1"))) {
                            this.dismiss();
                        } else if ((str.equalsIgnoreCase(respdescsave_beneficiary))
                                && (respcode.equalsIgnoreCase("0"))) {
                            post_successsaveBeneficiaries(retvalweb);
                        } else if ((str.equalsIgnoreCase(respdescsave_beneficiary))
                                && (respcode.equalsIgnoreCase("1"))) {
                            this.dismiss();
                        } else if (str.equalsIgnoreCase(act
                                .getString(R.string.alert_125))) {
                            removeBeneficiary();
                        } else if (WSCalled) {
							Fragment fragment = new ManageBeneficiaryMenuActivity(act);
							FragmentManager fragmentManager = getFragmentManager();
							fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();

                          /*  Intent in = new Intent(act, ManageBeneficiaryMenuActivity.class);
                            in.putExtra("var1", var1);
                            in.putExtra("var3", var3);
                            startActivity(in);
                            finish();*/
                        } else if (flg == "true") {
							Fragment fragment = new ManageBeneficiaryMenuActivity(act);
							FragmentManager fragmentManager = getFragmentManager();
							fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();

//                            Intent in = new Intent(act, ManageBeneficiaryMenuActivity.class);
//                            in.putExtra("var1", var1);
//                            in.putExtra("var3", var3);
//                            startActivity(in);
//                            finish();
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

    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.spinner_btn:
                //Log.e("DROP DOWN IMG BTN CLICKED....", "DROP DOWN IMG BTN CLICKED....");
                spi_sel_beneficiery.performClick();
                break;
            case R.id.btn_remove_bnf:
                try {
                    Log.e("REMOVE", "butoon clicked");
                    accNo = txtAccNo.getText().toString().trim();
                    accNm = txtName.getText().toString().trim();
                    mobNo = txtmobNo.getText().toString().trim();
                    nickNm = txtNick_Name.getText().toString().trim();
                    mailId = txtEmail.getText().toString().trim();
                    String benf = spi_sel_beneficiery.getItemAtPosition(spi_sel_beneficiery.getSelectedItemPosition()).toString();

                    if (benf.equalsIgnoreCase("Select Beneficiary")) {
                        showAlert(getString(R.string.lbl_add_ben));
                    } else {
                        flag = chkConnectivity();
                        if (flag == 0) {
                            InputDialogBox inputBox = new InputDialogBox(act);
                            inputBox.show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Exception in RemoveBeneficiary of onClick:" + e);
                }
                break;
           /* case R.id.btn_back:
                //System.out.println("Clicked on back");
                Intent in = new Intent(act, ManageBeneficiaryMenuActivity.class);
                in.putExtra("var1", var1);
                in.putExtra("var3", var3);
                act.startActivity(in);
                act.finish();
                break;*/

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
    }//end onClick

    public void post_successlog(String retvalwbs) {
        respcode = "";
        respdesc = "";
        act.finish();
        System.exit(0);

    }

    public class  CallWebServicelog extends AsyncTask<Void, Void, Void> {
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

    class CallWebFetchBenfService extends AsyncTask<Void, Void, Void> {
        String retval = "";
        LoadProgressBar loadProBarObj = new LoadProgressBar(act);
        JSONObject obj = new JSONObject();

        protected void onPreExecute() {
            loadProBarObj.show();

            try {
                obj.put("CUSTID", custId);
                obj.put("SAMEBNK", "A");
                obj.put("IMEINO", MBSUtils.getImeiNumber(act));
                obj.put("SIMNO", MBSUtils.getSimNumber(act));
                obj.put("METHODCODE", "13");
                Log.e("Shubham", "RemoveBeneficiary_Request-->"+obj.toString() );

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
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
                request.addProperty("value1", CryptoClass.Function5(obj.toString(), var2));
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
        }// end dodoInBackground

        protected void onPostExecute(Void paramVoid) {


            loadProBarObj.dismiss();
            JSONObject jsonObj;
            try {

                String str = CryptoClass.Function6(var5, var2);
                jsonObj = new JSONObject(str.trim());
                Log.e("Shubham", "RemoveBeneficiary_Responce-->"+jsonObj.toString() );
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
                    respdescbeneficiaries = jsonObj.getString("RESPDESC");
                } else {
                    respdescbeneficiaries = "";
                }

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if (respdescbeneficiaries.length() > 0) {
                showAlert(respdescbeneficiaries);
            } else {
                // Log.e("OMKAR BENEFICIEARIES", decryptedBeneficiaries);

                if (retvalweb.indexOf("SUCCESS") > -1) {
                    post_successget_beneficiaries(retvalweb);
                    // loadProBarObj.dismiss();

                } else if (retvalweb.indexOf("NODATA") > -1) {

                    retMess = getString(R.string.alert_041);
                    // loadProBarObj.dismiss();
                    flg = "true";
                    showAlert(retMess);
                } else {
                    // this case consider when in retval string contains only
                    // "FAILED"
                    retMess = getString(R.string.alert_069);
                    // loadProBarObj.dismiss();
                    showAlert(retMess);
                }
            }
        }// end onPostExecute
    }// end callWbService

    public void post_webService(String retval) {
        respcode = "";
        respdesc = "";
        retval = retval.split("SUCCESS~")[1];
        //Log.e("OMKAR BENEFICIEARIES", decryptedBeneficiaries);
        benInfo = retval;
        addBeneficiaries(retval);
    }

    class CallDeleteBenfWebService extends AsyncTask<Void, Void, Void> {
        LoadProgressBar loadProBarObj = new LoadProgressBar(act);


        String generatedXML = "", retVal = "";
        String accNo, debitAccno, benAcNo, amt, reMark;
        JSONObject obj = new JSONObject();

        @Override
        protected void onPreExecute() {
            loadProBarObj.show();

            accNo = txtAccNo.getText().toString().trim();
            benAcNo = spi_sel_beneficiery.getItemAtPosition(
                    spi_sel_beneficiery.getSelectedItemPosition()).toString();


            try {

                obj.put("CUSTID", custId);
                obj.put("ACCNO", accNo);
                obj.put("ACCNM", accNm);
                obj.put("MOBNO", mobNo);
                obj.put("NICKNM", nickNm);
                obj.put("MAILID", mailId);
                obj.put("TRANSFERTYPE", "A");
                obj.put("IFSCCD", "dummy");
                obj.put("MMID", "dummy");
                obj.put("IINSERTUPDTDLT", "D");
                obj.put("BENSRNO", benSrno);
                obj.put("IMEINO", MBSUtils.getImeiNumber(act));
                obj.put("MPIN", encrptdMpin);
                obj.put("SIMNO", MBSUtils.getSimNumber(act));
                obj.put("METHODCODE", "14");
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


            // System.out.println("&&&&&&&&&& generatedXML "+generatedXML);

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
                request.addProperty("value1", CryptoClass.Function5(obj.toString(), var2));
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


            // String status = xml_data[0];
            // Log.e("success", "success" + status);
            JSONObject jsonObj;
            try {

                String str = CryptoClass.Function6(var5, var2);
                jsonObj = new JSONObject(str.trim());
                Log.e("IN return", "data :" + jsonObj.toString());
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
                    respdescsave_beneficiary = jsonObj.getString("RESPDESC");
                } else {
                    respdescsave_beneficiary = "";
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if (respdescsave_beneficiary.length() > 0) {
                showAlert(respdescsave_beneficiary);
            } else {
                if (retvalweb.indexOf("SUCCESS") > -1) {
                    post_successsaveBeneficiaries(retvalweb);
                } else if (retvalweb.indexOf("WRONGMPIN") > -1) {
                    // loadProBarObj.dismiss();
                    retMess = getString(R.string.alert_125);
                    showAlert(retMess);
                    // SaveBeneficiary();
                } else {
                    txtAccNo.setFocusableInTouchMode(true);
                    txtAccNo.requestFocus();
                    cnt = 0;
                    // retMess = "Network Unavailable. Please Try Again.";
                    retMess = getString(R.string.alert_043);

                    showAlert(retMess);
                }
            }

        }
    }

    public int chkConnectivity() {
        //pb_wait.setVisibility(ProgressBar.VISIBLE);
        ConnectivityManager cm = (ConnectivityManager) act.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        try {
            State state = ni.getState();
            boolean state1 = ni.isAvailable();
            //System.out.println("RemoveBeneficiary	in chkConnectivity () state1 ---------" + state1);
            if (state1) {
                switch (state) {
                    case CONNECTED:
                        if (ni.getType() == ConnectivityManager.TYPE_MOBILE
                                || ni.getType() == ConnectivityManager.TYPE_WIFI) {
                        }
                        break;
                    case DISCONNECTED:
                        flag = 1;
                        //retMess = "Network Disconnected. Please Check Network Settings.";
                        retMess = getString(R.string.alert_014);
                        showAlert(retMess);
                        break;
                    default:
                        flag = 1;
                        //retMess = "Network Unavailable. Please Try Again.";
                        retMess = getString(R.string.alert_000);
                        showAlert(retMess);

                        break;
                }
            } else {
                flag = 1;
                //retMess = "Network Unavailable. Please Try Again.";
                retMess = getString(R.string.alert_000);
                showAlert(retMess);
            }
        } catch (NullPointerException ne) {

            Log.i("RemoveBeneficiary", "NullPointerException Exception" + ne);
            flag = 1;
            //retMess = "Network Unavailable. Please Try Again.";
            retMess = getString(R.string.alert_000);
            showAlert(retMess);

        } catch (Exception e) {
            Log.i("RemoveBeneficiary", "Exception" + e);
            flag = 1;
            //retMess = "Network Unavailable. Please Try Again.";
            retMess = getString(R.string.alert_000);
            showAlert(retMess);
        }
        return flag;
    }

    public void post_success_web2(String retval) {
        respcode = "";
        respdesc = "";
        retMess = getString(R.string.alert_bef_rmv_succ);
        showAlert(retMess);
        WSCalled = true;
    }

    public class InputDialogBox extends Dialog implements OnClickListener {
        Activity activity;
        Context appAcontext;
        EditText mpin;
        Button btnOk;
        TextView txtLbl;
        String msg, title, strmpin = "";
        boolean flg;

        public InputDialogBox(Activity activity) {
            super(activity);
        }//end InputDialogBox

        protected void onCreate(Bundle bdn) {
            super.onCreate(bdn);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.dialog_design);
            mpin = (EditText) findViewById(R.id.txtMpin);
            btnOk = (Button) findViewById(R.id.btnOK);
            mpin.setVisibility(EditText.VISIBLE);
            btnOk.setVisibility(Button.VISIBLE);
            btnOk.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            try {
                String str = mpin.getText().toString();
                encrptdMpin = str;//ListEncryption.encryptData(custId+str);
                encrptdUTranMpin = str;//ListEncryption.encryptData(userId+str);

                if (str.equalsIgnoreCase("")) {
                    this.hide();
                    retMess = getString(R.string.enter_pass);
                    showAlert(retMess);
                    mpin.setText("");
                } else
                // if(encrptdMpin.equalsIgnoreCase(mobPin)||encrptdUTranMpin.equals(mobPin))
                {
                    flag = chkConnectivity();
                    if (flag == 0) {
                        new CallDeleteBenfWebService().execute();
                        this.hide();
                    }
                }
						/*else
						{
							this.hide();
							retMess =getString(R.string.alert_125);
							showAlert(retMess);
							mpin.setText("");
						}	*/
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Exception in InputDialogBox of onClick:=====>" + e);
            }
        }//end onClick
    }//end InputDialogBox

    public void removeBeneficiary() {
        InputDialogBox inputBox = new InputDialogBox(act);
        inputBox.show();
    }


    public void post_successget_beneficiaries(String retvalweb) {
        respcode = "";
        respdescbeneficiaries = "";
        String decryptedBeneficiaries = retvalweb;

        decryptedBeneficiaries = retvalweb.split("SUCCESS~")[1];
        // Log.e("OMKAR BENEFICIEARIES", decryptedBeneficiaries);
        benInfo = decryptedBeneficiaries;
        addBeneficiaries(decryptedBeneficiaries);
    }

    public void post_successsaveBeneficiaries(String retvalweb) {

        respcode = "";
        respdescsave_beneficiary = "";
        retMess = getString(R.string.alert_042);
        WSCalled = true;
        showAlert(retMess);

    }
}

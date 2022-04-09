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
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.security.Key;
import java.security.PrivateKey;
import java.util.ArrayList;

import javax.crypto.spec.SecretKeySpec;

import mbLib.Accountbean;
import mbLib.CryptoClass;
import mbLib.CustomDialogClass;
import mbLib.DatabaseManagement;
import mbLib.MBSUtils;

public class ShowAccForQrcode extends Fragment implements OnClickListener,
        android.view.View.OnKeyListener {
    MainActivity act;
    ShowAccForQrcode showAccForQrcodeAct;
    private static final String MY_SESSION = "my_session";

    ListView acnt_listView;
    Button btn_get_stmt;
    TextView txt_heading;
    ImageView img_heading, btn_home1, btn_logout;//, btn_back;
    DatabaseManagement dbms;
    String stringValue = "", str2 = "", accountNo = "", acnt_inf = "", AccCustId;
    protected String accName;
    PrivateKey var1 = null;
    String var5 = "", var3 = "";
	SecretKeySpec var2 = null;
    String custId,respcode = "",retvalwbs = "",respdesc = "",retMess;
    int flag=0;

    public ShowAccForQrcode() {
    }

    @SuppressLint("ValidFragment")
    public ShowAccForQrcode(MainActivity a) {
        //System.out.println("MiniStmtActivity()");
        act = a;
        showAccForQrcodeAct = this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.mini_statement, container, false);
        var1 = act.var1;
        var3 = act.var3;
        txt_heading = (TextView) rootView.findViewById(R.id.txt_heading);
        img_heading = (ImageView) rootView.findViewById(R.id.img_heading);
        img_heading.setBackgroundResource(R.mipmap.fund_trnsfer);
        acnt_listView = (ListView) rootView.findViewById(R.id.acnt_listView);
        btn_get_stmt = (Button) rootView.findViewById(R.id.btnGetStmt);
        btn_home1 = (ImageView) rootView.findViewById(R.id.btn_home1);
        btn_logout = (ImageView) rootView.findViewById(R.id.btn_logout);
        //btn_back = (ImageButton) rootView.findViewById(R.id.btn_back);
        //btn_home.setImageResource(R.drawable.ic_home_d);
        //btn_back.setImageResource(R.drawable.backover);
        txt_heading.setText(getString(R.string.lbl_qr_receive));
        btn_get_stmt.setText("Generate QR Code");
        btn_get_stmt.setOnClickListener(this);
        //btn_back.setOnClickListener(this);
        btn_home1.setOnClickListener(this);
        btn_logout.setOnClickListener(this);
        dbms = new DatabaseManagement("shree_nagari.mbank", "listMobileBanking");

        //	SharedPreferences sp = act.getSharedPreferences(MY_SESSION,	Context.MODE_PRIVATE);


        Cursor c1 = dbms.selectFromTable("SHAREDPREFERENCE", "", null);//("select * from ", null);
        if (c1 != null) {
            while (c1.moveToNext()) {
                stringValue = c1.getString(0);
				custId=c1.getString(2);
                Log.e("retValStr", "......" + stringValue);//	stringValue = sp.getString("retValStr", "retValStr");
            }
        }

        addAccounts(stringValue);

        return rootView;
    }

    public void addAccounts(String str) {

        try {
            ArrayList<String> arrList = new ArrayList<String>();
            String allstr[] = str.split("~");
            int noOfAccounts = allstr.length;
            ArrayList<Accountbean> Accountbean_arr = new ArrayList<Accountbean>();
            final ArrayList<String> Account_arrTemp = new ArrayList<String>();
            Accounts acArray[] = new Accounts[noOfAccounts];
            for (int i = 0; i < noOfAccounts; i++) {
                str2 = allstr[i];
                Log.e("ShowAccForQrcode", "str=====" + str2);
                acArray[i] = new Accounts(str2);
                str2 = str2.replaceAll("#", "-");
                if (str2.indexOf("FD") == -1 && str2.indexOf("RP") == -1 && str2.indexOf("PG") == -1) {
                    Accountbean Accountbeanobj = new Accountbean();
                    Accountbean_arr.add(Accountbeanobj);
                    Account_arrTemp.add(str2);
                    String acctype = str2.split("-")[2];
                    String AccCustId = str2.split("-")[11];
                    str2 = MBSUtils.get16digitsAccNo(str2);
                    Accountbeanobj.setAccountinfo(str2 + " (" + MBSUtils.getAccTypeDesc(acctype) + ")");
                    Accountbeanobj.setAccountNumber(str2);
                    Accountbeanobj.setAcccustid(AccCustId);
                }
            }

            Customlist_radioadt adapter = new Customlist_radioadt(act, Accountbean_arr);
            acnt_listView.setAdapter(adapter);
            acnt_listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            acnt_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    btn_get_stmt.setEnabled(true);
                    Accountbean dataModel = (Accountbean) adapterView.getItemAtPosition(i);
                    accountNo = dataModel.getAccountNumber();
                    AccCustId = dataModel.getAcccustid();
                    acnt_inf = Account_arrTemp.get(i);
                    accName = acnt_inf.split("-")[4];
                    Log.e("ACC", "acnt_inf===" + acnt_inf);
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
    public boolean onKey(View arg0, int arg1, KeyEvent arg2) {
        return false;
    }

    @Override
    public void onClick(View v) {
        Intent in;
        Fragment fragment;
        FragmentManager fragmentManager;

        switch (v.getId()) {
            case R.id.btnGetStmt:
                Log.e("ShowAccForQRCode", "accountNo===" + accountNo);
                Bundle bundle = new Bundle();
                bundle.putString("ACCNO", accountNo);
                bundle.putString("ACCNM", accName);
                bundle.putString("AccCustId", AccCustId);
                fragment = new QrcodeRcvActivity(act);
                fragment.setArguments(bundle);
                fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
                act.frgIndex = 561;
                break;
			/*case R.id.btn_back:
				 fragment = new FundTransferMenuActivity(act);
				 fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction()
						.replace(R.id.frame_container, fragment).commit();
				act.frgIndex=5;
				break;*/
            case R.id.btn_home1:
                in = new Intent(act, NewDashboard.class);
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

	public void showAlert(final String str) {
		ErrorDialogClass alert = new ErrorDialogClass(act, "" + str) {
			@Override
			public void onClick(View v) {
				//Log.e("SetMPIN","SetMPIN...btn CLicked=="+R.id.btn_ok+"==="+v.getId());
				switch (v.getId()) {
					case R.id.btn_ok:
						//Log.e("SetMPIN","SetMPIN...CASE trru="+WSCalled);
						if ((str.equalsIgnoreCase(respdesc)) && (respcode.equalsIgnoreCase("0"))) {
							//post_success(retval);
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


    public void post_successlog(String retvalwbs) {
        respcode = "";
        respdesc = "";
        act.finish();
        System.exit(0);

    }


    public int chkConnectivity() {
        ConnectivityManager cm = (ConnectivityManager) act.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        try {
            NetworkInfo.State state = ni.getState();
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
                retMess = getString(R.string.alert_000);
                showAlert(retMess);
            }
        } catch (NullPointerException ne) {
            Log.e("EXCEPTION", "---------------" + ne);
            Log.i("mayuri", "NullPointerException Exception" + ne);
            flag = 1;
            //retMess = "Can Not Get Connection. Please Try Again.";
            retMess = getString(R.string.alert_000);
            showAlert(retMess);

        } catch (Exception e) {
            Log.e("EXCEPTION", "---------------" + e);
            Log.i("mayuri", "Exception" + e);
            flag = 1;
            //retMess = "Connection Problem Occured.";
            retMess = getString(R.string.alert_000);
            showAlert(retMess);
        }
        return flag;
    }
}

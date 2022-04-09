package shree_nagari.mbank;

//import android.annotation.SuppressLint;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.security.PrivateKey;

import javax.crypto.spec.SecretKeySpec;

import mbLib.CryptoClass;
import mbLib.CustomDialogClass;
import mbLib.DatabaseManagement;
import mbLib.MBSUtils;
//import mbLib.DialogBox;

//@SuppressLint("NewApi")
public class
FundTransferMenuActivity extends Fragment implements
        AdapterView.OnItemClickListener, View.OnClickListener {
    MainActivity act;
    //DashboardDesignActivity obj = null;
    FundTransferMenuActivity fundTranfMenuAct;
    ArrayAdapter<MenuIcon> aa;
	ImageView btn_home,btn_logout;//, btn_back;
    Button but_exit;
    //DialogBox dbs;
    private ListView listView1;
    ListView lst_dpt;
    TextView txt_heading;
    ImageView img_heading;
    String retMess = "", custid = "", retValStr = "", version = "", respcode = "", retval = "", respdesc = "",retvalwbs="";
    private static String METHOD_NAME1 = "";
    private static String NAMESPACE = "";
    private static String URL = "";
    private static String SOAP_ACTION = "";
    DatabaseManagement dbms, dbms1;
    int flag = 0;
    PrivateKey var1 = null;
    String var5 = "", var3 = "";
    SecretKeySpec var2 = null;

    public FundTransferMenuActivity() {
    }

    @SuppressLint("ValidFragment")
    public FundTransferMenuActivity(MainActivity a) {
        //System.out.println("BalanceRep()" + a);
        act = a;
        fundTranfMenuAct = this;
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //System.out.println("FundTransferMenuActivity	onCreateView()	");
        Log.e("Debug", "FundTransferMenuActivity invoked");
        View rootView = inflater.inflate(R.layout.finance_submenu, container, false);
        dbms = new DatabaseManagement("shree_nagari.mbank", "listMobileBanking");
        Cursor c1 = dbms.selectFromTable("SHAREDPREFERENCE", "", null);//("select * from ", null);
        if (c1 != null) {
            while (c1.moveToNext()) {
                //stringValue = c1.getString(0);
                //Log.e("retvalstr","....."+stringValue);
                custid = c1.getString(2);
                //Log.e("custId","......"+custId);
            }
        }
        var1 = act.var1;
        var3 = act.var3;
        txt_heading = (TextView) rootView.findViewById(R.id.txt_heading);
        img_heading = (ImageView) rootView.findViewById(R.id.img_heading);
        btn_home = (ImageView) rootView.findViewById(R.id.btn_home1);
        btn_logout = (ImageView) rootView.findViewById(R.id.btn_logout);
        btn_home.setOnClickListener(this);
        btn_logout.setOnClickListener(this);
        txt_heading.setText(getString(R.string.lbl_fund_transfer));
        img_heading.setBackgroundResource(R.mipmap.fund_trnsfer);
        MenuIcon menuItem[] = new MenuIcon[]{
                new MenuIcon(getString(R.string.lbl_own_account_trans),
                        R.mipmap.arrow),
                new MenuIcon(getString(R.string.lbl_same_bnk_trans),
                        R.mipmap.arrow),
                new MenuIcon(getString(R.string.tabtitle_other_bank_fund_trans_rtgs),
                        R.mipmap.arrow),
                new MenuIcon(
                        getString(R.string.tabtitle_other_bank_fund_trans_ifsc),
                        R.mipmap.arrow),
                /*new MenuIcon(
                        getString(R.string.tabtitle_other_bank_fund_trans_imps),
                        R.drawable.arrow),
                new MenuIcon(
                        getString(R.string.tabtitle_other_bank_fund_trans_uid),
                        R.drawable.arrow),*/

                new MenuIcon(getString(R.string.lbl_qr_send), R.mipmap.arrow),
                new MenuIcon(getString(R.string.lbl_qr_receive), R.mipmap.arrow),
                new MenuIcon(getString(R.string.lbl_transfer_history), R.mipmap.arrow),
        };


        MenuAdaptor adapter = new MenuAdaptor(act, R.layout.listview_item_row, menuItem);

        listView1 = (ListView) rootView.findViewById(R.id.listView1);
        View header = (View) act.getLayoutInflater().inflate(R.layout.fundmenu_listview_header_row, null);
        listView1.addHeaderView(header);
        listView1.setAdapter(adapter);
        listView1.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView1.setOnItemClickListener(this);

        try {
            this.flag = chkConnectivity();
            if (this.flag == 0) {
                //new CallWebService_getAccounts().execute();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Exception in CallWebServiceGetSrvcCharg is:" + e);
        }
        return rootView;
    }


    public void onClick(View v) {
        if (v.getId() == R.id.btn_home1)//v.getId()==R.id.btn_back||
        {
			//Toast.makeText(act, "Clicked", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(act, NewDashboard.class);
            i.putExtra("VAR1", var1);
            i.putExtra("VAR3", var3);
            startActivity(i);
            //((Activity) act).overridePendingTransition(0, 0);
            act.finish();

        } else if(v.getId() == R.id.btn_logout){
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
        }
    }

    public void post_successlog(String retvalwbs) {
        respcode = "";
        respdesc = "";
        act.finish();
        System.exit(0);

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
                jsonObj.put("CUSTID", custid);
                jsonObj.put("IMEINO", MBSUtils.getImeiNumber(act));
                jsonObj.put("SIMNO", MBSUtils.getSimNumber(act));
                jsonObj.put("METHODCODE", "29");
                //  ValidationData=MBSUtils.getValidationData(act,jsonObj.toString());

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

    public void onItemClick(AdapterView<?> paramAdapterView, View paramView,
                            int paramInt, long paramLong) {
        int pos = listView1.getCheckedItemPosition();
        Intent in = null;
        Bundle b = new Bundle();
        Fragment fragment;
        FragmentManager fragmentManager;

        switch (pos) {
            case 1:
                fragment = new OwnAccountTransfer(act);
                act.setTitle(getString(R.string.lbl_same_bnk_trans));
                fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.frame_container, fragment).commit();
                act.frgIndex = 51;
                break;
            case 2:

                //Log.i("MBS Case -1", "11 Same Bank transfer");

                fragment = new SameBankTransfer(act);
                act.setTitle(getString(R.string.lbl_same_bnk_trans));
                fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.frame_container, fragment).commit();
                act.frgIndex = 51;
                break;


            case 3:
                //Log.e("MBS Case -2", "........11 Other Bank transfer with IFSC");

                fragment = new OtherBankTranRTGS(act);
                act.setTitle(getString(R.string.tabtitle_other_bank_fund_trans_rtgs));
                fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.frame_container, fragment).commit();
                act.frgIndex = 52;
                break;
			
			
		/*case 3:

			//Log.e("MBS Case -3", "11 Other Bank transfer with IMPS");
			Log.e("MBS Case -2", "........11 Other Bank transfer with IFSC");

			fragment = new OtherBankTranIFSC(act);
			act.setTitle(getString(R.string.tabtitle_other_bank_fund_trans_ifsc));
			fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction().
				replace(R.id.frame_container, fragment).commit();
			act.frgIndex=52;
			break;*/
			/*fragment = new OtherBankTranIMPS(act);

			act.setTitle(getString(R.string.tabtitle_other_bank_fund_trans_imps));
			fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction().
				replace(R.id.frame_container, fragment).commit();	
			act.frgIndex=53;*/
            //break;
			
		
		/*case 4:

			Log.e("MBS Case -6", "11 transfer using UID");

			fragment = new OtherBankTranUID(act);

			act.setTitle(getString(R.string.lbl_tran_uid));
			fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.frame_container, fragment).commit();
			act.frgIndex=57;
			break;
		
		
		case 5:

			
			break;*/
            case 4:

                fragment = new OtherBankTranIMPS(act);

                act.setTitle(getString(R.string.tabtitle_other_bank_fund_trans_imps));
                fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().
                        replace(R.id.frame_container, fragment).commit();
                act.frgIndex = 53;

                break;

            case 5:

                Log.e("MBS Case -5", "11 Send QR Code");

                fragment = new QrcodeSendActivity(act);

                act.setTitle(getString(R.string.lbl_qr_send));
                fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.frame_container, fragment).commit();
                act.frgIndex = 55;

                break;

            case 6:

                Log.e("MBS Case -6", "11 Receive QR Code");

                fragment = new ShowAccForQrcode(act);

                act.setTitle(getString(R.string.lbl_qr_receive));
                fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.frame_container, fragment).commit();
                act.frgIndex = 56;

                break;
            case 7:
                Log.e("MBS Case -4", "11 Transfer History");

                fragment = new TransferHistory(act);

                act.setTitle(getString(R.string.lbl_transfer_history));
                fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
                act.frgIndex = 54;
                break;
            default:
                //Log.e("FUNDTRANSMENUACT","OnItemClick default case");
                break;


        }
    }

    class CallWebService_getAccounts extends AsyncTask<Void, Void, Void> {

        JSONObject jsonObj = new JSONObject();
        String retVal = "";
        String ValidationData = "";
        LoadProgressBar loadProBarObj = new LoadProgressBar(act);

        @Override
        protected void onPreExecute() {
            try {
                respcode = "";
                retval = "";
                respdesc = "";
                loadProBarObj.show();
                dbms1 = new DatabaseManagement("shree_nagari.mbank", "listMobileBanking");

                Cursor c2 = dbms1.selectFromTable("SHAREDPREFERENCE", "", null);// ("select * from ",
                // null);
                if (c2 != null) {
                    while (c2.moveToNext()) {
                        retValStr = c2.getString(0);
                        custid = c2.getString(2);

                    }
                }
                try {
                    PackageInfo pInfo = act.getPackageManager().getPackageInfo(act.getPackageName(), 0);
                    version = pInfo.versionName;
                    Log.e("PackageInfo", "PackageInfo" + version);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String imeiNo = MBSUtils.getImeiNumber(act);
                jsonObj.put("CUSTID", custid + "~#~" + version);
                jsonObj.put("IMEINO", imeiNo);
                jsonObj.put("SIMNO", MBSUtils.getSimNumber(act));
                jsonObj.put("METHODCODE", "54");
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
                e.printStackTrace();

            }

            return null;
        }

        @Override
        protected void onPostExecute(final Void result) {

            JSONObject jsonObj;
            try {
                String str = CryptoClass.Function6(var5, var2);
                jsonObj = new JSONObject(str.trim());
                loadProBarObj.dismiss();
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

                        Log.e("FAILED= ", "FAILED=");
                    } else {
                        if (retval.indexOf("SUCCESS") > -1) {

                            post_success(retval);

                        }
                    }//else
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

    public void post_success(String retval) {

        respcode = "";
        respdesc = "";
        String decryptedAccounts = retval.split("SUCCESS~")[1];
        if (!decryptedAccounts.equals("FAILED#")) {
            String splitstr[] = decryptedAccounts.split("!@!");
            {
                Bundle b = new Bundle();
                String accounts = splitstr[0];
                String mobno = splitstr[1];
                //String tranMpin =  splitstr[2];
                custid = splitstr[3];
                String userId = splitstr[4];
                System.out.println("mobno :" + mobno);

                String[] columnNames = {"retval_str", "cust_name", "cust_id", "user_id", "cust_mobno"};
                String[] columnValues = {accounts, "", custid, userId, mobno};

                dbms1.deleteFromTable("SHAREDPREFERENCE", "", null);
                dbms1.insertIntoTable("SHAREDPREFERENCE", 5, columnNames, columnValues);


            }
        } else {
            retMess = getString(R.string.alert_prblm_login);
            showAlert(retMess);
        }
    }

    public int chkConnectivity() {
        ConnectivityManager cm = (ConnectivityManager) act.getSystemService(Context.CONNECTIVITY_SERVICE);
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


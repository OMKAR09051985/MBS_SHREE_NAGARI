package shree_nagari.mbank;


import android.annotation.SuppressLint;
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
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

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
import mbLib.DialogBox;
import mbLib.MBSUtils;

public class OtherServicesMenuActivity extends Fragment implements
        AdapterView.OnItemClickListener, View.OnClickListener {

    MainActivity act;
    OtherServicesMenuActivity OthrServcMenu;
    ListView lst_dpt;
    int flag = 0;
    DatabaseManagement dbms;
    DialogBox dbs;
    ArrayAdapter<MenuIcon> aa;
    String lstopt[] = {"Change MPIN", /*"Interest Calculation",*/
            "Generate MMID", "Open FD Account"};//"Enable Disable ATM Card"
    private ListView listView1;
    Button but_exit;
    TextView txt_heading;
    ImageView img_heading;
    ImageButton btn_home;//btn_back,
    ImageView btn_home1, btn_logout;
    String retMess = "", respdesc = "", retval = "", respcode = "", retvalwbs = "", stringValue = "", custId = "";
    private static String NAMESPACE = "";
    private static String URL = "";
    private static String SOAP_ACTION = "";
    private static String METHOD_NAME = "";
    PrivateKey var1 = null;
    String var5 = "", var3 = "";
    SecretKeySpec var2 = null;
    MenuIcon[] menuItem = null;
    String check_pps = "";

    public OtherServicesMenuActivity() {
    }

    @SuppressLint("ValidFragment")
    public OtherServicesMenuActivity(MainActivity a) {
        //Log.e("OtherServicesMenuActivity","Constructor Invoked");
        act = a;
        OthrServcMenu = this;
    }

    @SuppressLint("SetTextI18n")
    @Override
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
        View rootView = inflater.inflate(R.layout.other_servc_submune, container, false);
        img_heading = (ImageView) rootView.findViewById(R.id.img_heading);

        check_pps = getArguments().getString("PPS_Menus");
        Log.e("Shubham", "check_pps: 2" + check_pps);
        txt_heading = (TextView) rootView.findViewById(R.id.txt_heading);

        if (check_pps.equalsIgnoreCase("PPS")) {
            act.frgIndex = 1111;
            txt_heading.setText("" + getString(R.string.PPS_Actvity_Name));
            img_heading.setBackgroundResource(R.mipmap.pps_menu);
            String[] othrSrvcMenu = getResources().getStringArray(R.array.othrSrvcMenu);
            menuItem = new MenuIcon[]{
                    new MenuIcon(othrSrvcMenu[2], R.mipmap.arrow),
                    new MenuIcon(othrSrvcMenu[3], R.mipmap.arrow),
            };

        } else if (check_pps.equalsIgnoreCase("EMAIL")) {
            act.frgIndex = 1112;
            txt_heading.setText("" + getString(R.string.lbl_other_srvce));
            img_heading.setBackgroundResource(R.mipmap.other_services);
            String[] othrSrvcMenu = getResources().getStringArray(R.array.othrSrvcMenu);
            menuItem = new MenuIcon[]{
                    new MenuIcon(othrSrvcMenu[0], R.mipmap.arrow),
                    new MenuIcon(othrSrvcMenu[1], R.mipmap.arrow),
                    //new MenuIcon(othrSrvcMenu[4], R.drawable.arrow),
            };
        }
        MenuAdaptor adapter = new MenuAdaptor(act, R.layout.listview_item_row,
                menuItem);

        listView1 = (ListView) rootView.findViewById(R.id.listView1);
        btn_home = (ImageButton) rootView.findViewById(R.id.btn_home);
        /*btn_back = (ImageButton) rootView.findViewById(R.id.btn_back);*/

        //btn_home.setImageResource(R.drawable.ic_home_d);
        //btn_back.setImageResource(R.drawable.backover);

        //img_heading.setBackgroundResource(R.drawable.other_services);
        //btn_back.setOnClickListener(this);
        btn_home.setOnClickListener(this);
        btn_home1 = (ImageView) rootView.findViewById(R.id.btn_home1);
        btn_logout = (ImageView) rootView.findViewById(R.id.btn_logout);
btn_logout.setVisibility(View.GONE);
        btn_home1.setOnClickListener(this);
        btn_logout.setOnClickListener(this);
        View header = (View) act.getLayoutInflater().inflate(R.layout.chq_listview_header_row, null);
        listView1.addHeaderView(header);

        listView1.setAdapter(adapter);

        listView1.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView1.setOnItemClickListener(this);

        return rootView;

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

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
        }
    }


    class CallWebServicelog extends AsyncTask<Void, Void, Void> {
        JSONObject jsonObj = new JSONObject();
        String ValidationData = "";

        @Override
        protected void onPreExecute() {
            try {
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

    public void post_success(String retval) {
        respdesc = "";

    }

    public int chkConnectivity() {
        ConnectivityManager cm = (ConnectivityManager) act.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        try {
            State state = ni.getState();
            boolean state1 = ni.isAvailable();
            // System.out.println("state1 ---------" + state1);
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
                retMess = getString(R.string.alert_000);
                showAlert(retMess);
            }
        } catch (NullPointerException ne) {

            Log.i("mayuri", "NullPointerException Exception" + ne);
            flag = 1;
            // retMess = "Can Not Get Connection. Please Try Again.";
            retMess = getString(R.string.alert_000);
            showAlert(retMess);

        } catch (Exception e) {
            Log.i("mayuri", "Exception" + e);
            flag = 1;
            // retMess = "Connection Problem Occured.";
            retMess = getString(R.string.alert_000);
            showAlert(retMess);
        }
        return flag;
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        try {
            int pos = listView1.getCheckedItemPosition();
            FragmentManager fragmentManager = null;
            Fragment fragment = null;
            if (check_pps.equalsIgnoreCase("EMAIL")) {
                switch (pos) {
                    case 1:
                        fragment = new EmailReg(act);
                        act.setTitle(getString(R.string.lbl_mmid));
                        fragmentManager = getFragmentManager();
                        act.frgIndex = 813;
                        break;

                    case 2:
                        fragment = new EmailSend(act);
                        act.setTitle(getString(R.string.lbl_mmid));
                        fragmentManager = getFragmentManager();
                        act.frgIndex = 814;
                        break;
                }
            } else if (check_pps.equalsIgnoreCase("PPS")) {
                switch (pos) {

                    case 1:
                        fragment = new PPSChequeIntimation(act);
                        act.setTitle(getString(R.string.lbl_chq_intimation));
                        fragmentManager = getFragmentManager();
                        act.frgIndex = 86;
                        break;

                    case 2:
                        fragment = new PPSchequeHistory(act);
                        act.setTitle(getString(R.string.lbl_chq_history));
                        fragmentManager = getFragmentManager();
                        act.frgIndex = 87;
                        break;
                }
            }
            fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


  /*	case 1:change mpin
		Log.e("MBS onItemClick 1", "Change MPIN");
			fragment = new ChangeMpinActivity(act);
			act.setTitle(getString(R.string.lbl_title_change_mpin));
			fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.frame_container, fragment).commit();
			act.frgIndex=81;
			break;
    case 2:	interest calculation
    Log.e("MBS onItemClick 2", "Interest Calculation");
			/*Fragment stopPaymentFragment = new StopPayment(act);
			act.setTitle(getString(R.string.lbl_stop_payment));
			FragmentManager stopPaymentfragmentManager = getFragmentManager();
			stopPaymentfragmentManager.beginTransaction()
					.replace(R.id.frame_container, stopPaymentFragment)
					.commit();
			break;
    case 1:// generate MMID
    Log.e("MBS onItemClick 3","Generate MMID");

			/*fragment = new GenerateMMID(act);
			act.setTitle(getString(R.string.lbl_mmid));
			fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.frame_container, fragment).commit();
			act.frgIndex=82;

    Toast.makeText(act, "Under Construction", Toast.LENGTH_SHORT).show();

			By SAM 18012020 3.07pm
			  fragment = new GenerateMMID(act);
			act.setTitle(getString(R.string.lbl_mmid));
			fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.frame_container, fragment).commit();
    break;

		case 1:
			Toast.makeText(act, "Under Construction", Toast.LENGTH_SHORT).show();
			fragment = new OpenFDAccount(act);
			act.setTitle(getString(R.string.lbl_atm_enable_desable));
			fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();

     Statement On Email
   Log.e("MBS onItemClick 3","Statement On Email");

			fragment = new EmailReg(act);
			act.setTitle(getString(R.string.lbl_title_email_registration));
			fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.frame_container, fragment).commit();
			act.frgIndex=81;
			break;

			*/
}




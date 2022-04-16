
package shree_nagari.mbank;

        import android.annotation.SuppressLint;
import android.app.Fragment;
        import android.content.Context;
        import android.content.Intent;
        import android.database.Cursor;
        import android.net.ConnectivityManager;
        import android.net.NetworkInfo;
        import android.os.AsyncTask;
        import android.os.Bundle;
        import android.util.Log;
        import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

        import org.json.JSONException;
        import org.json.JSONObject;
        import org.ksoap2.SoapEnvelope;
        import org.ksoap2.serialization.SoapObject;
        import org.ksoap2.serialization.SoapSerializationEnvelope;
        import org.ksoap2.transport.HttpTransportSE;

        import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import javax.crypto.spec.SecretKeySpec;

        import mbLib.CryptoClass;
        import mbLib.CustomDialogClass;
        import mbLib.DatabaseManagement;
        import mbLib.MBSUtils;
//import android.annotation.SuppressLint;

//@SuppressLint("NewApi")
public class PPS_history_report extends Fragment implements View.OnClickListener {
    MainActivity act;
    PPS_history_report ppsStmtRpt;
    TextView accNo, branch, sch_acno, name, bal,avil_bal;
    ImageButton back,btn_home;
    DatabaseManagement dbms;
    ImageView btn_home1,btn_logout;
    String actype_val, branch_val, sch_acno_val, name_val, bal_val;
    String str = "",custId = "", spi_str = "",balance,retMess,stringValue,amnt="",avilablebal="",accstr="",respcode = "",retvalwbs="", respdesc = "" ;
    ListView listView1 ;
    List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
    public PPS_history_report(){}
    TextView txt_heading,txt_actype;
    ImageView img_heading;
    PrivateKey var1 = null;
    String var5 = "", var3 = "";
    SecretKeySpec var2 = null;
    int flag = 0;

    @SuppressLint("ValidFragment")
    public PPS_history_report(MainActivity a,  String transactions,String Accstr)
    {
        ////System.out.println("MiniStmtReport()");
        act = a;
        ppsStmtRpt=this;

        retMess = transactions;
        stringValue =transactions;
        accstr=Accstr;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.view_cheque_history_report, container, false);
        var1 = act.var1;
        var3 =act.var3;
        accNo = (TextView) rootView.findViewById(R.id.txt_actype);
        txt_actype=(TextView)rootView.findViewById(R.id.txt_actype);
        txt_heading=(TextView)rootView.findViewById(R.id.txt_heading);
        img_heading=(ImageView)rootView.findViewById(R.id.img_heading);

        btn_home1 = (ImageView) rootView.findViewById(R.id.btn_home1);
        btn_logout = (ImageView) rootView.findViewById(R.id.btn_logout);
btn_logout.setVisibility(View.GONE);
        //back = (ImageButton) rootView.findViewById(R.id.btn_back);
        btn_home=(ImageButton)rootView.findViewById(R.id.btn_home);

        btn_home.setImageResource(R.mipmap.ic_home_d);
        //back.setImageResource(R.mipmap.backover);

        btn_home.setOnClickListener(this);
        btn_home1.setOnClickListener(this);
        btn_logout.setOnClickListener(this);
        //back.setOnClickListener(this);

        //back.setTypeface(tf_calibri);
        listView1 = (ListView) rootView.findViewById(R.id.listView1);
        dbms = new DatabaseManagement("shree_nagari.mbank", "listMobileBanking");
        Cursor c1 = dbms.selectFromTable("SHAREDPREFERENCE", "", null);// ("select * from ",
        // null);
        if (c1 != null) {
            while (c1.moveToNext()) {
                custId = c1.getString(2);
                // Log.e("custId","......"+custId);
               // stringValue = c1.getString(0);
                // Log.e("retvalstr","c......"+stringValue);
            }
        }
        setValues();
        return rootView;
    }

    public void setValues()
    {
        txt_heading.setText(getString(R.string.showppshistory));
        img_heading.setBackgroundResource(R.mipmap.ministmnt);
        txt_actype.setText(accstr);
            actype_val = "Re-Investment Plan";


        String trn_str=retMess;

        String str1[] = trn_str.split("#");

       // if(str1[0].indexOf("SUCCESS")>-1)
        //{
            for (int j = 0; j < str1.length; j++)
            {
                String string2[] = str1[j].split("!");
              //  Log.e("DSP","mini111"+ MBSUtils.amountFormat(string2[2].trim(),true,act));
                HashMap<String, String> map = new HashMap<String, String>();
                String[] from = new String[] {"rowid", "col_0", "col_1", "col_2","col_3","col_4"};
                int[] to = new int[] { R.id.item1, R.id.item2, R.id.item4, R.id.item41,R.id.item3,R.id.item5 };

                map.put("col_0", properCase(string2[1].trim()));
                map.put("col_1", string2[0].trim());
                map.put("col_2",""+MBSUtils.amountFormatchange(string2[2].trim(),true,act));
                map.put("col_3",  string2[4].trim() );
                map.put("col_4",  string2[3].trim() );
                fillMaps.add(map);
                SimpleAdapter adapter = new SimpleAdapter(act, fillMaps, R.layout.pps_cheque_list, from, to);
                listView1.setAdapter(adapter);
            }

        //}

    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            /*case R.id.btn_back:
                Fragment  PPSchequeFragment = new PPSchequeHistory(act);

                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.frame_container, PPSchequeFragment).commit();
                act.frgIndex=8;
                break;*/
            case R.id.btn_home:
                Intent in=new Intent(act,DashboardDesignActivity.class);
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
                CustomDialogClass alert=new CustomDialogClass(act, getString(R.string.lbl_exit)) {
                    @Override
                    public void onClick(View v) {
                        switch (v.getId()) {
                            case R.id.btn_ok:
                                flag = chkConnectivity();
                                if (flag == 0)
                                {
                                    CallWebServicelog c=new CallWebServicelog();
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

        }
    }

    class CallWebServicelog extends AsyncTask<Void, Void, Void> {
        JSONObject jsonObj = new JSONObject();
        String ValidationData="";
        LoadProgressBar loadProBarObj = new LoadProgressBar(act);

        @Override
        protected void onPreExecute() {
            try{
                loadProBarObj.show();
                respcode="";
                retvalwbs="";
                respdesc="";
                Log.e("@DEBUG","LOGOUT preExecute()");
                jsonObj.put("CUSTID", custId);
                jsonObj.put("IMEINO", MBSUtils.getImeiNumber(act));
                jsonObj.put("SIMNO", MBSUtils.getSimNumber(act));
                jsonObj.put("METHODCODE","29");
                // ValidationData=MBSUtils.getValidationData(act,jsonObj.toString());

            }
            catch (JSONException je) {
                je.printStackTrace();
            }

        };

        @Override
        protected Void doInBackground(Void... arg0) {
            String value4 = getString(R.string.namespace);
            String value5 = getString(R.string.soap_action);
            String value6 = getString(R.string.url);
            final String value7 = "callWebservice";
            try {
                String keyStr= CryptoClass.Function2();
                var2=CryptoClass.getKey(keyStr);
                SoapObject request = new SoapObject(value4, value7);

                request.addProperty("value1", CryptoClass.Function5(jsonObj.toString(), var2));
                request.addProperty("value2", CryptoClass.Function3(keyStr, var1));
                request.addProperty("value3", var3);
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.setOutputSoapObject(request);
                HttpTransportSE androidHttpTransport = new HttpTransportSE(value6,15000);
                if(androidHttpTransport!=null)
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
                Log.e("ERROR-OUTER",e.getClass()+" : "+e.getMessage());
            }
            return null;
        }

        protected void onPostExecute(final Void result) {
            loadProBarObj.dismiss();
            JSONObject jsonObj;
            try
            {

                String str=CryptoClass.Function6(var5,var2);
                jsonObj = new JSONObject(str.trim());
    					/*ValidationData=xml_data[1].trim();
    					if(ValidationData.equals(MBSUtils.getValidationData(act, xml_data[0].trim())))
    					{
    					Log.e("IN return", "data :" + jsonObj.toString());*/
                if (jsonObj.has("RESPCODE"))
                {
                    respcode = jsonObj.getString("RESPCODE");
                }
                else
                {
                    respcode="-1";
                }
                if (jsonObj.has("RETVAL"))
                {
                    retvalwbs = jsonObj.getString("RETVAL");
                }
                else
                {
                    retvalwbs = "";
                }
                if (jsonObj.has("RESPDESC"))
                {
                    respdesc = jsonObj.getString("RESPDESC");
                }
                else
                {
                    respdesc = "";
                }

                if(respdesc.length()>0)
                {
                    showAlert(respdesc);
                }
                else{
                    if (retvalwbs.indexOf("FAILED") > -1) {
                        retMess = getString(R.string.alert_network_problem_pease_try_again);
                        showAlert(retMess);

                    }
                    else
                    {
                        post_successlog(retvalwbs);
				/*finish();
				System.exit(0);*/
                    }
                }
    					/*}
    					else{

    						MBSUtils.showInvalidResponseAlert(act);
    					}*/
            } catch (JSONException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }

    public void showAlert(final String str)
    {
        ErrorDialogClass alert = new ErrorDialogClass(act,""+str)
        {@Override
        public void onClick(View v)

        {
            //Log.e("SetMPIN","SetMPIN...btn CLicked=="+R.id.btn_ok+"==="+v.getId());
            switch (v.getId())
            {
                case R.id.btn_ok:
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
            NetworkInfo.State state = ni.getState();
            boolean state1 = ni.isAvailable();
            // ////System.out.println("state1 ---------" + state1);
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
    public void post_successlog(String retvalwbs)
    {
        respcode="";
        respdesc="";
        act.finish();
        System.exit(0);

    }
    public String properCase(String input)
    {
        StringBuffer sb = new StringBuffer();

        StringTokenizer tokens = new StringTokenizer(input, " ");
        while (tokens.hasMoreTokens())
        {
            String part=tokens.nextToken();
            char[] chars = part.toLowerCase().toCharArray();
            chars[0] = Character.toUpperCase(chars[0]);

            sb.append(new String(chars)).append(" ");
        }
        return sb.toString().trim();
    }
}

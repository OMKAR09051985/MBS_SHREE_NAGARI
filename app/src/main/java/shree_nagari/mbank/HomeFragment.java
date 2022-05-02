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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.security.PrivateKey;
import java.util.ArrayList;

import javax.crypto.spec.SecretKeySpec;

import mbLib.Accountbean;
import mbLib.CryptoClass;
import mbLib.CustomDialogClass;
import mbLib.DatabaseManagement;
import mbLib.DialogBox;
import mbLib.MBSUtils;
//import android.annotation.SuppressLint;

//@SuppressLint("NewApi")
@SuppressLint("ValidFragment")
public class HomeFragment extends Fragment implements OnClickListener {
    private ListView listView1;
    MainActivity act;
    //HomeFragment homeFrag;
    Context context;
    DialogBox dbs;
    int flag = 0;
    private static final String MY_SESSION = "my_session";
    private static String NAMESPACE = "";
    private static String URL = "";
    private static String SOAP_ACTION = "";
    private static String METHOD_NAME = "";
    //Editor e;
    String stringValue = "";
    String all_acnts = "", str2 = "", str = "", respcode = "",
            retval = "", respdesc = "", retvalwbs = "", custId = "", retMess = "", AccCustId;
    String acc_type = "SAVING_CUR";
    int chekacttype = 0;
    TextView txt_heading;
    ImageView img_heading, btn_home1, btn_logout;
    //ImageButton btn_home;//,btn_back;
    Button btn_show_details;
    //Button btn_saving_cur, btn_deposits, btn_loan;
    //ImageButton stmntbtn,imgBtnChequeRelated,imgBtnTransfer;
    //ImageButton img_btn_transfer, img_btn_mini_stmt, img_btn_chq_related;
    String acnt_inf = "";
    String accNumber = null;
    String[] prgmNameList, prgmNameListTemp;
    private ArrayList<Accountbean> Accountbean_arr;
    protected String accStr;
    DatabaseManagement dbms;
    PrivateKey var1 = null;
    String var5 = "", var3 = "";
    SecretKeySpec var2 = null;

    @SuppressLint("ValidFragment")
    public HomeFragment(MainActivity a) {
        act = a;

    }

	/*public HomeFragment(MainActivity a) {
		System.out.println("HomeFragment()" + a);
		act = a;
		homeFrag = this;
		context = a;
	}*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        dbms = new DatabaseManagement("shree_nagari.mbank", "listMobileBanking");
        View rootView = inflater.inflate(R.layout.home, container, false);
        var1 = act.var1;
        var3 = act.var3;

        listView1 = (ListView) rootView.findViewById(R.id.listView1);
        txt_heading = (TextView) rootView.findViewById(R.id.txt_heading);
        img_heading = (ImageView) rootView.findViewById(R.id.img_heading);

        btn_home1 = (ImageView) rootView.findViewById(R.id.btn_home1);
        btn_logout = (ImageView) rootView.findViewById(R.id.btn_logout);
        btn_logout.setVisibility(View.GONE);

        //	btn_home=(ImageButton)rootView.findViewById(R.id.btn_home);
        /*btn_back=(ImageButton)rootView.findViewById(R.id.btn_back);*/

        //btn_home.setImageResource(R.drawable.ic_home_d);
        //btn_back.setImageResource(R.drawable.backover);

        //btn_back.setOnClickListener(this);
        btn_home1.setOnClickListener(this);
        btn_logout.setOnClickListener(this);
        btn_logout.setVisibility(View.GONE);
        btn_show_details = (Button) rootView.findViewById(R.id.btnShowDetails);
        btn_show_details.setOnClickListener(this);

        //	SharedPreferences sp = act.getSharedPreferences(MY_SESSION,
        //			Context.MODE_PRIVATE);
        //e = sp.edit();
        //	stringValue = sp.getString("retValStr", "retValStr");
        Cursor c1 = dbms.selectFromTable("SHAREDPREFERENCE", "", null);//("select * from ", null);
        if (c1 != null) {
            while (c1.moveToNext()) {
                stringValue = c1.getString(0);
                //Log.e("retvalstr","......"+stringValue);
                custId = c1.getString(2);
                //Log.e("custId","......"+custId);
            }
        }
        all_acnts = stringValue;
        chekacttype = getArguments().getInt("CHECKACTTYPE");
        //Log.e("HOME FRAGMENT11111","stringValue=="+stringValue);
        if (chekacttype == 1) {
            img_heading.setBackgroundResource(R.mipmap.savings);
            txt_heading.setText(getString(R.string.lbl_saving_and_current));
        } else if (chekacttype == 2) {
            img_heading.setBackgroundResource(R.mipmap.term_deposit);
            txt_heading.setText(getString(R.string.lbl_deposits));
        } else {
            img_heading.setBackgroundResource(R.mipmap.loan);
            txt_heading.setText(getString(R.string.lbl_loan));
        }

        addAccounts(all_acnts, acc_type);

        return rootView;
    }

    public void addAccounts(String all_accstr, String acc_type) {
        //System.out.println("HomeFragment IN addAccounts()" + all_accstr);

        try {
            Accountbean_arr = new ArrayList<Accountbean>();
            ArrayList<Accountbean> savingbean_arr = new ArrayList<Accountbean>();
            ArrayList<Accountbean> depositbean_arr = new ArrayList<Accountbean>();
            ArrayList<Accountbean> loanbean_arr = new ArrayList<Accountbean>();

            ArrayList<String> arrList = new ArrayList<String>();
            ArrayList<String> savingArrList = new ArrayList<String>();
            ArrayList<String> depositArrList = new ArrayList<String>();
            ArrayList<String> loanArrList = new ArrayList<String>();
            String allstr[] = all_accstr.split("~");

            ArrayList<String> arrListTemp = new ArrayList<String>();

            //System.out.println("HomeFragment Mayuri.....................:");
            //System.out.println("HomeFragment Accounts:::" + allstr[1]);
            int noOfAccounts = allstr.length;
            //System.out.println("HomeFragment noOfAccounts:" + noOfAccounts);
            for (int i = 0; i < noOfAccounts; i++) {
                //System.out.println(i + "----STR1-----------" + allstr[i]);
                str2 = allstr[i];
                //System.out.println(i + "str2-----------" + str2);
                str2 = str2.replaceAll("#", "-");

                String acType = str2.split("-")[2];
                String AccCustID = str2.split("-")[11];
                //System.out.println("mbs============="+str2);
                String str2Temp = str2;
                str2 = MBSUtils.get16digitsAccNo(str2);
				/*if (acc_type.equalsIgnoreCase("SAVING_CUR")) // SAVING AND
																// CURRENT
				{
					*/
                Accountbean accountbean = new Accountbean();
                if (acType.equalsIgnoreCase("SB") || acType.equalsIgnoreCase("CA")) {

                    //Accountbean_arr.add(accountbean);
                    savingbean_arr.add(accountbean);
                    savingArrList.add(str2);
                    arrListTemp.add(str2Temp);
                }
				/*}
				else if (acc_type.equalsIgnoreCase("DEPOSITS")) // DEPOSITS
				{
					*/
                else if (acType.equalsIgnoreCase("FD")
                        || acType.equalsIgnoreCase("CD")
                        || acType.equalsIgnoreCase("RP")
                        || acType.equalsIgnoreCase("PG")
                        || acType.equalsIgnoreCase("RA")
                        || acType.equalsIgnoreCase("RD")) {

                    //Accountbean accountbean = new Accountbean();
                    //accountbean.setAccountinfo(str2);
                    //Accountbean_arr.add(accountbean);
                    depositbean_arr.add(accountbean);

                    depositArrList.add(str2);
                    arrListTemp.add(str2Temp);
                }
				/*} 
				else if (acc_type.equalsIgnoreCase("LOAN")) // LOAN ACCOUNTS
				{
					*/
                else if (acType.equalsIgnoreCase("LO")) {
                    //Accountbean accountbean = new Accountbean();
                    //accountbean.setAccountinfo(str2);
                    //Accountbean_arr.add(accountbean);
                    loanbean_arr.add(accountbean);
                    loanArrList.add(str2);
                    arrListTemp.add(str2Temp);
                }
                accountbean.setAccStr(str2Temp);
                accountbean.setAccountinfo(str2 + " (" + MBSUtils.getAccTypeDesc(acType) + ")");
                accountbean.setAccountNumber(str2);
                accountbean.setAcccustid(AccCustID);
                accountbean.setMainType("LO");
                accountbean.setOprcd(str2Temp.split("-")[7]);
                //}
            }

            if (chekacttype == 1) {
                Accountbean_arr = savingbean_arr;
                arrList = savingArrList;
            } else if (chekacttype == 2) {
                Accountbean_arr = depositbean_arr;
                arrList = depositArrList;
            } else {
                Accountbean_arr = loanbean_arr;
                arrList = loanArrList;
            }
            //Log.e("HomeFragment","arrList=="+arrList);
            int[] prgmImages = new int[arrList.size()];

            for (int x = 0; x < arrList.size(); x++) {
                prgmImages[x] = R.mipmap.arrow;
            }
            prgmNameList = new String[arrList.size()];
            prgmNameList = arrList.toArray(prgmNameList);

            prgmNameListTemp = new String[arrListTemp.size()];
            prgmNameListTemp = arrListTemp.toArray(prgmNameListTemp);

            //Log.e("Debug@HomeFragment ","Before from adding accounts");

            if (Accountbean_arr.size() > 0) {
                //Customlist_radioadt adapter = new Customlist_radioadt(act,Accountbean_arr);
                Customlist_radioadt adapter = new Customlist_radioadt(act, Accountbean_arr);
                listView1.setAdapter(adapter);
                listView1.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            } else {
                //showAlert(getString(R.string.alert_089));
                Toast.makeText(act, getString(R.string.alert_089), Toast.LENGTH_LONG).show();

                //Log.e("HomeFragment","NO RECORDS");
                //Log.e("HomeFragment","NO RECORDS");
                //Log.e("HomeFragment","NO RECORDS");
                //Log.e("HomeFragment","NO RECORDS");
                //Log.e("HomeFragment","NO RECORDS");
                Intent in = new Intent(act, NewDashboard.class);
                in.putExtra("VAR1", var1);
                in.putExtra("VAR3", var3);
                startActivity(in);
                act.finish();
            }
            //Log.e("Debug@HomeFragment ","After from adding accounts");
            listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView,
                                        View view, int i, long l) {
                    //Log.e("Debug@HomeFragment ","Click Added for Radio.");
                    btn_show_details.setEnabled(true);

                    //f = prgmNameListTemp[i];
                    accNumber = Accountbean_arr.get(i).getAccountNumber();
                    accStr = Accountbean_arr.get(i).getAccStr();
                    acnt_inf = Accountbean_arr.get(i).getAccountinfo();
                    AccCustId = Accountbean_arr.get(i).getAcccustid();
                    Log.e("Omkar ", "accStr=" + accStr);
                    Log.e("Omkar ", "accNumber=" + accNumber);
                    for (int i1 = 0; i1 < adapterView.getCount(); i1++) {

                        try {

                            View v = adapterView.getChildAt(i1);
                            RadioButton radio = (RadioButton) v.findViewById(R.id.radio);
                            radio.setChecked(false);

                        } catch (Exception e) {
                            Log.e("radio button", "radio");
                            e.printStackTrace();
                        }

                    }

                    try {
                        RadioButton radio = (RadioButton) view.findViewById(R.id.radio);
                        radio.setChecked(true);
                    } catch (Exception e) {
                        Log.e("radio button", "radio");
                        e.printStackTrace();
                    }

                    act.setTitle(act.getString(R.string.lbl_acc_details));
                }
            });
			
			
			/* prgmNameList = new String[arrList.size()];
			prgmNameList = arrList.toArray(prgmNameList);

			prgmNameListTemp = new String[arrListTemp.size()];
			prgmNameListTemp = arrListTemp.toArray(prgmNameListTemp);

			listView1.setAdapter(new CustomAccAdapter(act, prgmNameList,prgmImages));
			listView1.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

			listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() 
			{
				@Override
				public void onItemClick(AdapterView<?> adapterView,
						View view, int i, long l) 
				{
					String f = prgmNameListTemp[i];
					System.out.println("@@ setOnItemClickListener @@");
					System.out.println("12345678987456321"+ f);
					
					Log.e("HOME FRAGMENT","12345678987456321"+ f);
					Log.e("HOME FRAGMENT","98745632123456789"+ prgmNameList[i]);
					// acnt_inf = dataModel.getAccountinfo();
					Bundle b = new Bundle();

					// Storing data into bundle
					b.putString("accountinfo", prgmNameListTemp[i]);

					if (chekacttype == 1 ) 
					{
						Log.i("12345678987456321","chekacttype=="+chekacttype);
						String decryptedBalance = "SUCCESS#1227";
						String bal = decryptedBalance.split("#")[1];
						decryptedBalance = bal;

						// fragmentManager.putFragment(b, key,
						// fragment)(b);

						Fragment balRepFragment = new BalanceRep(act,
								str, prgmNameListTemp[i], decryptedBalance);
						
						FragmentManager fragmentManager = getFragmentManager();
						fragmentManager
								.beginTransaction()
								.replace(R.id.frame_container,
										balRepFragment).commit();

					} else if (chekacttype == 2) 
					{
						
						Fragment FdRdAccountDetail = new FdRdAccountDetail(
								act);
						FdRdAccountDetail.setArguments(b);
						android.app.FragmentManager fragmentManager = getFragmentManager();
						fragmentManager
								.beginTransaction()
								.replace(R.id.frame_container,
										FdRdAccountDetail).commit();
					} 
					else if (chekacttype == 3) 
					{
						
						Fragment LoanAccountDetail = new LoanAccountDetail(
							act);
				LoanAccountDetail.setArguments(b);
					android.app.FragmentManager fragmentManager = getFragmentManager();
					fragmentManager
							.beginTransaction()
							.replace(R.id.frame_container,
									LoanAccountDetail).commit();
				} 
				act.setTitle(act.getString(R.string.lbl_acc_details));
				}
			});*/
        } catch (Exception e) {
            Log.e("EXCEPTION", "---------------" + e);
            System.out.println("" + e);
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        //Log.e("onClick Event ","Clicked");
        if (v.getId() == R.id.btn_home1) {
            Intent in = new Intent(act, NewDashboard.class);
            in.putExtra("VAR1", var1);
            in.putExtra("VAR3", var3);
            startActivity(in);
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
        } else if (v.getId() == R.id.btnShowDetails) {

            Bundle b = new Bundle();
            b.putString("accountinfo", acnt_inf);
            b.putString("accountstr", accStr);
            b.putString("accountnumber", accNumber);
            b.putString("AccCustId", AccCustId);
            if (chekacttype == 1) {
                act.frgIndex = 11;
                Fragment balRepFragment = new BalanceRep(act);
                balRepFragment.setArguments(b);
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.frame_container, balRepFragment).commit();

            } else if (chekacttype == 2) {
                act.frgIndex = 21;
                Fragment FdRdAccountDetail = new FdRdAccountDetail(act);
                FdRdAccountDetail.setArguments(b);
                android.app.FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.frame_container, FdRdAccountDetail).commit();

            } else if (chekacttype == 3) {
                act.frgIndex = 31;
                Fragment LoanAccountDetail = new LoanAccountDetail(act);
                LoanAccountDetail.setArguments(b);
                android.app.FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.frame_container, LoanAccountDetail).commit();
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

                        }
                        break;
                    case DISCONNECTED:
                        flag = 1;
                        // retMess = "Network Disconnected. Please Try Again.";
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
                        // retMess = "Network Unavailable. Please Try Again.";
                        retMess = getString(R.string.alert_000);
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
                // retMess = "Network Unavailable. Please Try Again.";
                retMess = getString(R.string.alert_000);
                showAlert(retMess);
                // setAlert();
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
            // retMess = "Network Unavailable. Please Try Again.";
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
            // retMess = "Network Unavailable. Please Try Again.";
            retMess = getString(R.string.alert_000);
            showAlert(retMess);
            // setAlert();
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

    public void showAlert(String str) {
        //Toast.makeText(this, str, Toast.LENGTH_LONG).show();
        ErrorDialogClass alert = new ErrorDialogClass(act, "" + str);
        alert.show();
    }

}

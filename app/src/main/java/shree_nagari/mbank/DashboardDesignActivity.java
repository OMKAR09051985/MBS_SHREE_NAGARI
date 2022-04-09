package shree_nagari.mbank;


import java.util.ArrayList;
import java.util.TimerTask;
import mbLib.CryptoUtil;
import mbLib.CustomDialogClass;
import mbLib.CustomSpinner;
import mbLib.DatabaseManagement;
import mbLib.DialogBox;
import mbLib.MBSUtils;
import mbLib.MyThread;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import shree_nagari.mbank.R;


import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class DashboardDesignActivity extends Activity implements OnClickListener,OnTouchListener,OnItemSelectedListener
{
	Spinner select_accnt;
	Editor e;
	DialogBox dbs;
	int flag=0,gpsFlg=0;
	DashboardDesignActivity obj=null;
	Button btn_saving_and_current,btn_term_deposite,btn_loan,btn_mini_stmt,btn_fund_tran;
	Button btn_manage_benf,btn_chq_related,btn_other_srvces,btn_atm;
	//ImageButton btn_logout;
	ImageView img_heading,btn_logout;
	TextView txt_heading,balance;
	LinearLayout dashboard;
	String retValStr,respcode="",retvalwbs="",respdesc="",respdescgetacc="";
	boolean logout=false;
	String str = "", retMess = "", custid = "", retval = "";
	private static String NAMESPACE = "";
	private static String URL = "";
	private static String SOAP_ACTION = "";
	private static  String METHOD_NAME = "";
	private static  String METHOD_NAME1="";
	private static final String MY_SESSION = "my_session";
	Editor cntx = null;
	DatabaseManagement dbms;
	//private Timer timer;
	LinearLayout savinglayout,otherservices,cheqlayout,trmdepositelayout,ministmt_layout,
	fundtransfer_layout,manageben_layout,loan_layout;
    private MyThread t1;
	//int timeOutInSecs=300;
	Accounts acArray[];
	ArrayList<String> arrListTemp = new ArrayList<String>();
	public DashboardDesignActivity() {
		// TODO Auto-generated constructor stub
		obj=this;
	}
	
/*	public DashboardDesignActivity(MainActivity a)
	{
		//System.out.println("ManageBeneficiaryMenuActivity()"+a);
		act = a;
		obj=this;
	}*/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.dashboard_layout);
        setContentView(R.layout.sbk_dash);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

		select_accnt=(Spinner)findViewById(R.id.select_accnt);
        balance=(TextView)findViewById(R.id.balance);
        Bundle bObj = getIntent().getExtras();
		try {
			if (bObj != null) {
				String strFromAct = bObj.getString("FROMACT");
				if(!strFromAct.equalsIgnoreCase("LOGIN"))
				{
					new CallWebService_getAccounts().execute();
				}
			}
		} catch (Exception e) {
			Log.e("DASHBOARD", "" + e);
		}
		dbms = new DatabaseManagement("shree_nagari.mbank", "listMobileBanking");
		Cursor c1=dbms.selectFromTable("SHAREDPREFERENCE", "", null);//("select * from ", null);
        if(c1!=null)
        {
        	while(c1.moveToNext())
	        {	
	        	retValStr=c1.getString(0);
	        	Log.e("retValStr","......"+retValStr);
	        	custid=c1.getString(2);
		    	Log.e("CustId","c......"+custid);
	        }
        }
        addAccounts(retValStr);
		select_accnt.setOnItemSelectedListener(this);
		/* dashboard=(LinearLayout)findViewById(R.id.home_root);
         //dashboard.setOnTouchListener(this);
        dbms = new DatabaseManagement("shree_nagari.mbank", "listMobileBanking");
        btn_saving_and_current = (Button) findViewById(R.id.btn_saving_and_current); 
       // btn_saving_and_current.setTypeface(FontCache.get("fonts/Kozuka-Gothic-Pro-M_26793.ttf", this));
        btn_term_deposite = (Button) findViewById(R.id.btn_term_deposite);
        //btn_term_deposite.setTypeface(FontCache.get("fonts/Kozuka-Gothic-Pro-M_26793.ttf", this));
        btn_loan = (Button) findViewById(R.id.btn_loan);      
        //btn_loan.setTypeface(FontCache.get("fonts/Kozuka-Gothic-Pro-M_26793.ttf", this));
        btn_mini_stmt = (Button) findViewById(R.id.btn_mini_stmt); 
        //btn_mini_stmt.setTypeface(FontCache.get("fonts/Kozuka-Gothic-Pro-M_26793.ttf", this));
        btn_fund_tran = (Button) findViewById(R.id.btn_fund_tran); 
        //btn_fund_tran.setTypeface(FontCache.get("fonts/Kozuka-Gothic-Pro-M_26793.ttf", this));
        btn_manage_benf = (Button) findViewById(R.id.btn_manage_benf);  
        //btn_manage_benf.setTypeface(FontCache.get("fonts/Kozuka-Gothic-Pro-M_26793.ttf", this));
        btn_chq_related = (Button) findViewById(R.id.btn_chq_related);     
        //btn_chq_related.setTypeface(FontCache.get("fonts/Kozuka-Gothic-Pro-M_26793.ttf", this));
        btn_other_srvces = (Button) findViewById(R.id.btn_other_srvces);
        //btn_other_srvces.setTypeface(FontCache.get("fonts/Kozuka-Gothic-Pro-M_26793.ttf", this));
        /*btn_logout= (ImageButton) findViewById(R.id.btn_back);
        
        txt_heading=(TextView)findViewById(R.id.txt_heading);
        img_heading=(ImageView)findViewById(R.id.img_heading);
        
        //btn_logout.setBackgroundResource(R.drawable.cat_logout);
       // img_heading.setBackgroundResource(R.drawable.trans);
            
       // btn_saving_and_current.setOnClickListener(this);
      //  btn_term_deposite.setOnClickListener(this);
       // btn_loan.setOnClickListener(this);
       // btn_mini_stmt.setOnClickListener(this);
      //  btn_fund_tran.setOnClickListener(this);
       // btn_manage_benf.setOnClickListener(this);
       // btn_chq_related.setOnClickListener(this);
       // btn_other_srvces.setOnClickListener(this);
        savinglayout=(LinearLayout)findViewById(R.id.savinglayout);
        trmdepositelayout=(LinearLayout)findViewById(R.id.trmdepositelayout);
        loan_layout=(LinearLayout)findViewById(R.id.loan_layout);
        ministmt_layout=(LinearLayout)findViewById(R.id.ministmt_layout);
        fundtransfer_layout=(LinearLayout)findViewById(R.id.fundtransfer_layout);
        manageben_layout=(LinearLayout)findViewById(R.id.manageben_layout);
        cheqlayout=(LinearLayout)findViewById(R.id.cheqlayout);
        otherservices=(LinearLayout)findViewById(R.id.otherservices);
        
        SharedPreferences sp = obj.getSharedPreferences(MY_SESSION,Context.MODE_PRIVATE);
		e = sp.edit();
		retValStr = sp.getString("retValStr", "retValStr");
		custid = sp.getString("custId", "custId");
      	
        Bundle b1=getIntent().getExtras();
        if(b1!=null)
        {
        	retValStr=b1.getString("accounts");
        }
        Log.e("DashboardDesignActivity","retValStr==53454545"+retValStr);
        String[] arr=retValStr.split("~"); 
       // Log.e("DashboardDesignActivity","arr[0]=="+arr[0]);
        String name=arr[0].split("#")[4];
        //L//og.e("DashboardDesignActivity","name=="+name);
        
        txt_heading.setText("Welcome "+name);
        // Listening to back button click
        btn_logout.setOnClickListener(new View.OnClickListener() 
        {		
			@Override
			public void onClick(View view) 
			{
				dbs = new DialogBox(obj);
				dbs.get_adb().setMessage(getString(R.string.lbl_exit));
				dbs.get_adb().setPositiveButton("Yes",
						new DialogInterface.OnClickListener() 
						{
							@Override
							public void onClick(DialogInterface arg0, int arg1) 
							{
								flag = chkConnectivity();
								if (flag == 0)
								{
									CallWebService c=new CallWebService();
									c.execute();
								}
							}
						});
				dbs.get_adb().setNegativeButton("No",
						new DialogInterface.OnClickListener() 
						{
							@Override
							public void onClick(DialogInterface arg0, int arg1) 
							{
								arg0.cancel();
							}
						});
				dbs.get_adb().show();
			}
		});
   
       t1=new MyThread(timeOutInSecs, this);	
		 t1.start();   
		int fragIndex=-1;*/
		//final Intent mainIntent;
         
       // LinearLayout app_layer = (LinearLayout) findViewById (R.id.);
		/*savinglayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DashboardDesignActivity.this, "hello", Toast.LENGTH_LONG).show();
                Fragment fragment = null;
                FragmentManager fragmentManager;
                //fragIndex=0;
                fragment = new OtherBankTranIFSC(act);
    			//act.setTitle(getString(R.string.tabtitle_other_bank_fund_trans_ifsc));
    			fragmentManager = getFragmentManager();
    			fragmentManager.beginTransaction().
    				replace(R.id.frame_container, fragment).commit();
    			//act.frgIndex=0;
				//break;
            }
        }); 
		*/
		 
		/*otherservices.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
    			Intent in=new Intent(DashboardDesignActivity.this,MainActivity.class);
				Bundle b1=new Bundle();
				b1.putInt("FRAGINDEX", 7);
				in.putExtras(b1);
				startActivity(in);
				finish();
    			
			}
		});
        cheqlayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
    			Intent in=new Intent(DashboardDesignActivity.this,MainActivity.class);
				Bundle b1=new Bundle();
				b1.putInt("FRAGINDEX", 6);
				in.putExtras(b1);
				startActivity(in);
				finish();
    			
			}
		});
        
        manageben_layout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
    			Intent in=new Intent(DashboardDesignActivity.this,MainActivity.class);
				Bundle b1=new Bundle();
				b1.putInt("FRAGINDEX", 5);
				in.putExtras(b1);
				startActivity(in);
				finish();
    			
			}
		});
        fundtransfer_layout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				 
    			Intent in=new Intent(DashboardDesignActivity.this,MainActivity.class);
				Bundle b1=new Bundle();
				b1.putInt("FRAGINDEX", 4);
				in.putExtras(b1);
				startActivity(in);
				finish();
    			
			}
		});
        
        ministmt_layout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
    			Intent in=new Intent(DashboardDesignActivity.this,MainActivity.class);
				Bundle b1=new Bundle();
				b1.putInt("FRAGINDEX", 3);
				in.putExtras(b1);
				startActivity(in);
				finish();
    			
			}
		});
        loan_layout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
    			Intent in=new Intent(DashboardDesignActivity.this,MainActivity.class);
				Bundle b1=new Bundle();
				b1.putInt("FRAGINDEX", 2);
				in.putExtras(b1);
				startActivity(in);
				finish();
    			
			}
		});
        trmdepositelayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//Toast.makeText(DashboardDesignActivity.this, "Term and Loan", Toast.LENGTH_LONG).show();
    			Intent in=new Intent(DashboardDesignActivity.this,MainActivity.class);
				Bundle b1=new Bundle();
				b1.putInt("FRAGINDEX", 1);
				in.putExtras(b1);
				startActivity(in);
				finish();
    			
			}
		});
        savinglayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//Toast.makeText(DashboardDesignActivity.this, "Saving and Current", Toast.LENGTH_LONG).show();
    			Intent in=new Intent(DashboardDesignActivity.this,MainActivity.class);
				Bundle b1=new Bundle();
				b1.putInt("FRAGINDEX", 0);
				in.putExtras(b1);
				startActivity(in);
				finish();
    			
			}
		});*/
		
    }
    
    public void addAccounts(String str) 
	{
		try 
		{
			ArrayList<String> arrList = new ArrayList<String>();
			String allstr[] = str.split("~");
			
			int noOfAccounts = allstr.length;
			acArray = new Accounts[noOfAccounts];
			int j=0;
			for (int i = 0; i < noOfAccounts; i++) 
			{
				String str2 = allstr[i];
				String tempStr=str2;
				acArray[i] = new Accounts(str2);
				str2 = str2.replaceAll("#", "-");
				String accType = str2.split("-")[2];
				String oprcd=str2.split("-")[7];
				//Log.e("accType=","accType="+accType);
				//Log.e("oprcd=","oprcd="+oprcd);
				String str2Temp = str2;
				str2 = MBSUtils.get16digitsAccNo(str2);
				
				/*if (((accType.equals("SB")) || (accType.equals("CA"))
						|| (accType.equals("LO"))) && oprcd.equalsIgnoreCase("O"))*/
				{
					acArray[j++] = new Accounts(tempStr);
					arrList.add(str2+" ("+MBSUtils.getAccTypeDesc(accType)+")");
					arrListTemp.add(str2Temp);
					Log.e("str2Temp=","str2Temp="+str2Temp);
				}
			}

			String[] debAccArr = new String[arrList.size()];
			debAccArr = arrList.toArray(debAccArr);
			
			CustomSpinner debAccs = new CustomSpinner(DashboardDesignActivity.this,R.layout.spinner_item, debAccArr);
			debAccs.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			select_accnt.setAdapter(debAccs);
			
			Accounts selectedDrAccount=acArray[0];
			String balStr=selectedDrAccount.getBalace();
			String drOrCr="";
			float amt=Float.parseFloat(balStr);
			if(amt>0)
				drOrCr=" Cr";
			else if(amt<0)
				drOrCr=" Dr";
			if(balStr.indexOf(".")==-1)
				balStr=balStr+".00";
			balStr=balStr+drOrCr;
			balance.setText("Rs. "+balStr);
			
		} catch (Exception e) {
			System.out.println("" + e);
		}

	}// end addAccount
    
    @Override
    protected void onDestroy() {
    	// TODO Auto-generated method stub
    	super.onDestroy();
  t1.sec=-1;
    	System.gc();
    }
    
    @Override
	public void onClick(View v)
    {
    	int fragIndex=-1;
		//final Intent mainIntent;
		Fragment fragment = null;

		switch (v.getId()) 
		{
			case R.id.btn_saving_and_current:
				fragIndex=0;
				break;
			case R.id.btn_term_deposite:
				fragIndex=1;
				break;
			case R.id.btn_loan:
				fragIndex=2;
				break;
			case R.id.btn_mini_stmt:
				fragIndex=3;
				break;
			case R.id.btn_fund_tran:
				fragIndex=4;
				break;
			case R.id.btn_manage_benf:
				fragIndex=5;
				break;
			case R.id.btn_chq_related:
				fragIndex=6;
				break;
			case R.id.btn_other_srvces:
				fragIndex=7;
				break;	
			
			default:
				break;
		}

		if (fragIndex != -1) 
		{		
				try
				{
					Intent in=new Intent(this,MainActivity.class);
					Bundle b1=new Bundle();
					b1.putInt("FRAGINDEX", fragIndex);
					in.putExtras(b1);
					startActivity(in);
					finish();
				}
				catch(Exception ex)
				{
					Log.e("DASHBOARD ",""+ex);
				}	
		} else {
			Log.e("MainActivity", "Error in creating fragment");
		}
	}
    public void onBackPressed() {
		/*DialogBox dbs = new DialogBox(this);
		dbs.get_adb().setMessage(getString(R.string.lbl_do_you_want_to_exit));
		dbs.get_adb().setPositiveButton("Yes",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
						//startActivity(lang_activity);
						finish();
						System.exit(0);
					}
				});
		dbs.get_adb().setNegativeButton("No",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
						arg0.cancel();
					}
				});
		dbs.get_adb().show();*/
    	showlogoutAlert(getString(R.string.lbl_do_you_want_to_exit));
	}
    public void showAlert(final String str) {
    	ErrorDialogClass alert = new ErrorDialogClass(obj, "" + str)
    	{@Override
			public void onClick(View v)

			{
				//Log.e("SetMPIN","SetMPIN...btn CLicked=="+R.id.btn_ok+"==="+v.getId());
				switch (v.getId()) 
				{
					case R.id.btn_ok:
						//Log.e("SetMPIN","SetMPIN...CASE trru="+WSCalled);
						if((str.equalsIgnoreCase(respdesc)) &&  (respcode.equalsIgnoreCase("0")))
						{
							post_success(retvalwbs);
						}
						else if((str.equalsIgnoreCase(respdesc)) && (respcode.equalsIgnoreCase("1")))
						{
							this.dismiss();
						}
						if((str.equalsIgnoreCase(respdescgetacc)) &&  (respcode.equalsIgnoreCase("0")))
						{
							post_successgetacc(retvalwbs);
						}
						else if((str.equalsIgnoreCase(respdescgetacc)) && (respcode.equalsIgnoreCase("1")))
						{
							this.dismiss();
						}
						else
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
    public void showlogoutAlert(String str) {
    	CustomDialogClass alert = new CustomDialogClass(obj,str)
    	{
    		@Override
    		public void onClick(View v) {
    			// TODO Auto-generated method stub
    			switch (v.getId()) 
    			{
    				case R.id.btn_ok:
    					//finish();
    					flag = chkConnectivity();
						if (flag == 0) {
    					CallWebService c = new CallWebService();
						c.execute();
						}
    					//System.exit(0);
    				  break;	
    				  
    				case R.id.btn_cancel:

    					this.dismiss();
    					break;
    				default:
    				  break;
    			}
    		}
    	};
		alert.show();
    }
      class CallWebService extends AsyncTask<Void, Void, Void> {
		//String[] xmlTags = { "CUSTID","IMEINO" };
    	  String[] xmlTags = {"PARAMS","CHECKSUM"};
  		String[] valuesToEncrypt = new String[2];
                JSONObject jsonObj = new JSONObject();
		String generatedXML = "";
		String ValidationData="";

		@Override
		protected void onPreExecute() {
                   try{
                	   respcode="";
                	   retvalwbs="";
                	   respdesc="";
			Log.e("@DEBUG","LOGOUT preExecute()");
                  jsonObj.put("CUSTID", custid);
	              jsonObj.put("IMEINO", MBSUtils.getImeiNumber(DashboardDesignActivity.this));
	              jsonObj.put("SIMNO", MBSUtils.getSimNumber(DashboardDesignActivity.this));
	              jsonObj.put("METHODCODE","29");
	              ValidationData=MBSUtils.getValidationData(DashboardDesignActivity.this,jsonObj.toString());
		//	valuesToEncrypt[0] = custid;
		//	valuesToEncrypt[1] = MBSUtils.getImeiNumber(DashboardDesignActivity.this);
                       }
			   catch (JSONException je) {
	                je.printStackTrace();
	            }
			//valuesToEncrypt[0] = jsonObj.toString();
                   valuesToEncrypt[0] =  jsonObj.toString();
               	valuesToEncrypt[1] =  ValidationData;
			generatedXML = CryptoUtil.generateXML(xmlTags, valuesToEncrypt);
			Log.e("Debug","Trying: "+generatedXML);
		};

		@Override
		protected Void doInBackground(Void... arg0) {
			//Log.e("@DEBUG","LOGOUT doInBackground()");
			NAMESPACE = getString(R.string.namespace);
			URL = getString(R.string.url);
			SOAP_ACTION = getString(R.string.soap_action);
			METHOD_NAME = "mbsInterCall";//"logoutWS";
			SoapObject request = null;
			try {
				request  = new SoapObject(NAMESPACE, METHOD_NAME);
				//Log.e("Debug@","********");
				request.addProperty("para_value", generatedXML);
				//Log.e("Debug@","@@@@@@@@@");
				SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
						SoapEnvelope.VER11);
				envelope.setOutputSoapObject(request);
				//Log.e("Debug@","$$$$$$$$");
				HttpTransportSE androidHttpTransport = new HttpTransportSE(URL,
						15000);

				try {
					androidHttpTransport.call(SOAP_ACTION, envelope);
					//System.out.println(envelope.bodyIn.toString());
					retval = envelope.bodyIn.toString().trim();
					int pos = envelope.bodyIn.toString().trim().indexOf("=");
					retval = retval.substring(pos + 1, retval.length() - 3);
				} catch (Exception e) {
					e.printStackTrace();
					getString(R.string.alert_000);
					System.out.println("Exception" + e);
					Log.e("ERROR-INNER",e.getClass()+" : "+e.getMessage());
				}
			} catch (Exception e) {
				// retMess = "Error occured";
				getString(R.string.alert_000);
				System.out.println(e.getMessage());
				Log.e("ERROR-OUTER",e.getClass()+" : "+e.getMessage());
			}
			return null;
		}

		protected void onPostExecute(final Void result) {
			Log.e("@DEBUG","LOGOUT onPostExecute()");
		
                	
                	String[] xml_data = CryptoUtil.readXML(retval, new String[]{"PARAMS","CHECKSUM"});
                	JSONObject jsonObj;
    				try
    				{
    	
    					jsonObj = new JSONObject(xml_data[0]);
    					ValidationData=xml_data[1].trim();
    					if(ValidationData.equals(MBSUtils.getValidationData(DashboardDesignActivity.this, xml_data[0].trim())))
    					{
    					Log.e("IN return", "data :" + jsonObj.toString());
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
				post_success(retvalwbs);
				/*finish();
				System.exit(0);*/
			}
    				}
    					}
    					else{
    						
    						MBSUtils.showInvalidResponseAlert(DashboardDesignActivity.this);	
    					}
    				} catch (JSONException e) 
    				{
    					// TODO Auto-generated catch block
    					e.printStackTrace();
    				}
    				
		}
	}
      public void post_success(String retvalwbs)
  	{
    	  respcode="";
   	      respdesc="";
    	  finish();
			System.exit(0);
  		
  	}
      public void post_successgetacc(String retvalwbs)
    	{
    	    respcode="";
			respdescgetacc="";
    	  String decryptedAccounts = retval.split("SUCCESS~")[1];
			Log.e("In Login", "decryptedAccounts,,,,, :"
					+ decryptedAccounts);
			if (!decryptedAccounts.equals("FAILED#")) 
			{
				System.out.println("in if ***************************************");
				System.out.println("decryptedAccounts :"
						+ decryptedAccounts);
				String splitstr[] =decryptedAccounts.split("!@!");
				Log.e("==--==","splitstr.len :" + splitstr);
				Log.e("==--==","splitstr.len :" + splitstr.length);
				String oldversion=splitstr[5];
				if(oldversion.equals("OLDVERSION"))
				{
					//showlogoutAlert(getString(R.string.alert_oldversionupdate));
				}
				else
				{
					Bundle b = new Bundle();
					String accounts = splitstr[0];
					String mobno =  splitstr[1];
					String tranMpin =  splitstr[2];
					custid = splitstr[3];
					String userId=splitstr[4];
					System.out.println("mobno :" + mobno);
					
					String[] columnNames={"retval_str","cust_name","cust_id","user_id","cust_mobno"};
					String[] columnValues={accounts,"",custid,userId,mobno};
						
					dbms.deleteFromTable("SHAREDPREFERENCE", "",null);
					dbms.insertIntoTable("SHAREDPREFERENCE", 5, columnNames, columnValues);
						

					Log.e("LOGIN", "accounts==" + accounts);
					Log.e("LOGIN", "custid==" + custid);
					
					Log.e("LOGIN", "tranMpin==" + tranMpin);
					Log.e("LOGIN", "mobno==" + mobno);
					Log.e("LOGIN", "userId==" + userId);					
				}
			}
			else
			{
				retMess = getString(R.string.alert_prblm_login);
			}
    		
    	}
    
    public int chkConnectivity() {
		//Log.i("1111", "1111");
		// p_wait.setVisibility(ProgressBar.VISIBLE);
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		//Log.i("2222", "2222");
		try {
			State state = ni.getState();
			//Log.i("3333", "3333");
			boolean state1 = ni.isAvailable();
			//Log.i("4444", "4444");
			//System.out.println("state1 ---------" + state1);
			if (state1) {
				switch (state) {
				case CONNECTED:

					//Log.i("5555", "5555");
					if (ni.getType() == ConnectivityManager.TYPE_MOBILE
							|| ni.getType() == ConnectivityManager.TYPE_WIFI) {

						gpsFlg = 1;
						flag = 0;

					}
					break;
				case DISCONNECTED:
					//Log.i("6666", "6666");
					flag = 1;
					// retMess = "Network Disconnected. Please Try Again.";
					retMess = getString(R.string.alert_000);
					dbs = new DialogBox(this);
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
					//Log.i("7777", "7777");
					flag = 1;
					// retMess = "Network Unavailable. Please Try Again.";
					retMess = getString(R.string.alert_000);
					// setAlert();

					dbs = new DialogBox(this);
					dbs.get_adb().setMessage(retMess);
					dbs.get_adb().setPositiveButton("Ok",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface arg0,
										int arg1) {
									arg0.cancel();
									Intent in = null;
									in = new Intent(getApplicationContext(),
											LoginActivity.class);
									startActivity(in);
									finish();
								}
							});
					dbs.get_adb().show();
					break;
				}
			} else {
				//Log.i("8888", "8888");
				flag = 1;
				// retMess = "Network Unavailable. Please Try Again.";
				retMess = getString(R.string.alert_000);
				// setAlert();

				dbs = new DialogBox(this);
				dbs.get_adb().setMessage(retMess);
				dbs.get_adb().setPositiveButton("Ok",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface arg0, int arg1) {
								arg0.cancel();
								Intent in = null;
								in = new Intent(getApplicationContext(),
										LoginActivity.class);
								startActivity(in);
								finish();
							}
						});
				dbs.get_adb().show();
			}
		} catch (NullPointerException ne) {

			Log.i("mayuri", "NullPointerException Exception" + ne);
			flag = 1;
			// retMess = "Network Unavailable. Please Try Again.";
			retMess = getString(R.string.alert_000);
			// setAlert();

			dbs = new DialogBox(this);
			dbs.get_adb().setMessage(retMess);
			dbs.get_adb().setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface arg0, int arg1) {
							arg0.cancel();
							Intent in = null;
							in = new Intent(getApplicationContext(),
									LoginActivity.class);
							startActivity(in);
							finish();
						}
					});
			dbs.get_adb().show();

		} catch (Exception e) {
			Log.i("mayuri", "Exception" + e);
			flag = 1;
			// retMess = "Network Unavailable. Please Try Again.";
			retMess = getString(R.string.alert_000);
			// setAlert();

			dbs = new DialogBox(this);
			dbs.get_adb().setMessage(retMess);
			dbs.get_adb().setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface arg0, int arg1) {
							arg0.cancel();
							Intent in = null;
							in = new Intent(getApplicationContext(),
									LoginActivity.class);
							startActivity(in);
							finish();
						}
					});
			dbs.get_adb().show();
		}
		return flag;
	}
   /* 
    @Override
    protected void onPause() {
        super.onPause();

        timer = new Timer();
        Log.i("Main", "Invoking logout timer");
        LogOutTimerTask logoutTimeTask = new LogOutTimerTask();
        timer.schedule(logoutTimeTask, 60000); //auto logout in 5 minutes
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (timer != null) {
            timer.cancel();
            Log.i("Main", "cancel timer");
            timer = null;
                       
           
        }
      
        if(logout==true)
        {
        	Intent i = new Intent(DashboardDesignActivity.this, LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
        }
    }
    */
    	@Override
	public boolean onTouch(View v, MotionEvent event) {
		
		t1.sec = Integer.parseInt(getString(R.string.timeOutInSecs));
		//Toast.makeText(this, "in touch dashboard", Toast.LENGTH_SHORT).show();
		// TODO Auto-generated method stub
		return false;
	}
    
    private class LogOutTimerTask extends TimerTask { 

        @Override
        public void run() { 
        	logout=true;
            //redirect user to login screen
           
        }
    }


    class CallWebService_getAccounts extends AsyncTask<Void, Void, Void> {
		

		
    	String[] xmlTags = {"PARAMS","CHECKSUM"};
		String[] valuesToEncrypt = new String[2];
		JSONObject jsonObj = new JSONObject();
		String generatedXML = "",retVal="";
		String ValidationData="";
		

		@Override
		protected void onPreExecute() {
			try{
				respcode="";
				retvalwbs="";
				
				respdescgetacc="";
				String service = Context.TELEPHONY_SERVICE;
				TelephonyManager telephonyManager = (TelephonyManager) getSystemService(service);
			//	String imeiNo = telephonyManager.getDeviceId();
				String imeiNo = MBSUtils.getImeiNumber(DashboardDesignActivity.this);
				jsonObj.put("CUSTID", custid);
				jsonObj.put("IMEINO", imeiNo);
				jsonObj.put("SIMNO", MBSUtils.getSimNumber(DashboardDesignActivity.this));
				jsonObj.put("METHODCODE","54");
				ValidationData=MBSUtils.getValidationData(DashboardDesignActivity.this,jsonObj.toString());
			/*	valuesToEncrypt[1] = custId;
			valuesToEncrypt[2] = MBSUtils.getImeiNumber(act);*/
			}
			 catch (JSONException je) {
	                je.printStackTrace();
	            }
			// valuesToEncrypt[0] = jsonObj.toString();
			valuesToEncrypt[0] =  jsonObj.toString();
			valuesToEncrypt[1] =  ValidationData;
			generatedXML = CryptoUtil.generateXML(xmlTags, valuesToEncrypt);
			//System.out.println("generatedXML" + generatedXML);
		};

		@Override
		protected Void doInBackground(Void... arg0) {
			NAMESPACE = getString(R.string.namespace);
			URL = getString(R.string.url);
			SOAP_ACTION = getString(R.string.soap_action);
			// p_wait.setVisibility(ProgressBar.VISIBLE);
			METHOD_NAME1="mbsInterCall";//"fetchAccountsWS";
			int i = 0;
			//Log.i("mayuri", "in doInBackground()");
			try {
				//Log.i("mayuri", "in doInBackground()" + "1");
				
				SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME1);

				request.addProperty("para_value", generatedXML);

				SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
						SoapEnvelope.VER11);
				envelope.setOutputSoapObject(request);
				HttpTransportSE androidHttpTransport = new HttpTransportSE(URL,
						15000);
				String status = "";
				try {
				
					androidHttpTransport.call(SOAP_ACTION, envelope);
				
					status = envelope.bodyIn.toString().trim();
				
					retVal = status;
				
					int len = retVal.length();
		
					int pos = envelope.bodyIn.toString().trim().indexOf("=");
					retVal = retVal.substring(pos + 1, retVal.length() - 3);
				
				} catch (Exception e) {
					e.printStackTrace();
			
				}
			} catch (Exception e) {
		
				System.out.println(e.getMessage());
		
			}
			return null;
		}

		@Override
		protected void onPostExecute(final Void result) {
			
			Log.e("DAAAAAAAAAAAAAAAA","INside  Post Excute");
			
			String[] xml_data = CryptoUtil.readXML(retVal, new String[]{"PARAMS","CHECKSUM"});
			
			
			JSONObject jsonObj;
			try
			{

				jsonObj = new JSONObject(xml_data[0]);
				
				ValidationData=xml_data[1].trim();
				if(ValidationData.equals(MBSUtils.getValidationData(DashboardDesignActivity.this, xml_data[0].trim())))
				{
				Log.e("IN return", "data :" + jsonObj.toString());
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
					respdescgetacc = jsonObj.getString("RESPDESC");
				}
				else
				{	
					respdescgetacc = "";
				}
				
			if(respdescgetacc.length()>0)
			{
				showAlert(respdescgetacc);
			}
			else{
				Log.e("LIST== ","tryyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyy");
			if (retvalwbs.indexOf("FAILED") > -1) {
				
				Log.e("FAILED= ","FAILED=");							
			} 
			else
			{
				if (retvalwbs.indexOf("SUCCESS") >-1) 
				{
					post_successgetacc(retvalwbs);
					
					
				}
			}//else
		}
				}
				else{
					MBSUtils.showInvalidResponseAlert(DashboardDesignActivity.this);	
				}
			} catch (JSONException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	

	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) 
	{
		String str=select_accnt.getSelectedItem().toString();
		//Log.e("arg2= ","position="+position);
		str = arrListTemp.get(select_accnt.getSelectedItemPosition());
		String debitAc[] = str.split("-");
		System.out.println("account 1:" + debitAc[0]);// 5
		System.out.println("account 2:" + debitAc[1]);// 101
		System.out.println("account 4:" + debitAc[3]);// 7
	
		String mmid=debitAc[8];
		//Log.e("MMID","MMID  "+mmid);
		if(mmid.equals("NA"))
		{
			//showAlert( getString(R.string.lbl_mmid_msg));
		}
			
		Accounts selectedDrAccount=acArray[select_accnt.getSelectedItemPosition()];
		String balStr=selectedDrAccount.getBalace();
		String drOrCr="";
		float amt=Float.parseFloat(balStr);
		if(amt>0)
			drOrCr=" Cr";
		else if(amt<0)
			drOrCr=" Dr";
		if(balStr.indexOf(".")==-1)
			balStr=balStr+".00";
		balStr=balStr+drOrCr;
		balance.setText("Rs. "+balStr);	
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub
		
	}
}

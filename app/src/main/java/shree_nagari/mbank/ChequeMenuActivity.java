package shree_nagari.mbank;


import java.security.PrivateKey;

import javax.crypto.spec.SecretKeySpec;

import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import mbLib.CryptoClass;
import mbLib.CustomDialogClass;
import mbLib.DatabaseManagement;
import mbLib.DialogBox;
import mbLib.MBSUtils;
import shree_nagari.mbank.R;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

//@SuppressLint("NewApi")
public class ChequeMenuActivity extends Fragment implements
		OnItemClickListener, OnClickListener {
	MainActivity act;
	ChequeMenuActivity chqMenu;
	DatabaseManagement dbms;
	ListView lst_dpt;
	ArrayAdapter<MenuIcon> aa;
	String lstopt[] = { "Cheque Book Request", "Stop Payment Request",
			"Cheque Status View" };
	private ListView listView1;
	Button but_exit;
	TextView txt_heading;
	ImageView btn_home1,btn_logout;
	ImageView img_heading;
	ImageButton btn_home;//btn_back,
	DialogBox dbs;	
	int flag=0;
	String respcode="",respdesc="",retvalwbs="",retval="",custId="",stringValue="",retMess="";
	
	PrivateKey var1=null;	  
	String var5="",var3="";
	SecretKeySpec var2=null;
	
	public ChequeMenuActivity() {
	}

	@SuppressLint("ValidFragment")
	public ChequeMenuActivity(MainActivity a) {
		
		act = a;
		chqMenu = this;
	}

	public void onBackPressed() {
		Intent in=new Intent(act,NewDashboard.class);
		in.putExtra("VAR1", var1);
		in.putExtra("VAR3", var3);
		startActivity(in);
		act.finish(); 
		
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		dbms = new DatabaseManagement("shree_nagari.mbank", "listMobileBanking");
		var1 = act.var1;
		var3 = act.var3;
		Cursor c1=dbms.selectFromTable("SHAREDPREFERENCE", "", null);//("select * from ", null);
        if(c1!=null)
        {
        	while(c1.moveToNext())
	        {	
        		stringValue=c1.getString(0);
        		//Log.e("retvalstr","....."+stringValue);
        		custId=c1.getString(2);
	        	//Log.e("custId","......"+custId);
	        }
        }

		View rootView = inflater.inflate(R.layout.cheque_submenu, container,
				false);
		MenuIcon menuItem[] = new MenuIcon[] {
				new MenuIcon("Cheque Book Request", R.mipmap.arrow),
				new MenuIcon("Stop Payment Request", R.mipmap.arrow),
				new MenuIcon("Cheque Status View", R.mipmap.arrow),
		};

		MenuAdaptor adapter = new MenuAdaptor(act, R.layout.listview_item_row,
				menuItem);

		listView1 = (ListView) rootView.findViewById(R.id.listView1);
		btn_home=(ImageButton)rootView.findViewById(R.id.btn_home);
		txt_heading = (TextView) rootView.findViewById(R.id.txt_heading);
		img_heading = (ImageView) rootView.findViewById(R.id.img_heading);
		txt_heading.setText(getString(R.string.lbl_cheque_related));
		img_heading.setBackgroundResource(R.mipmap.checkbkstatus);
		btn_home.setOnClickListener(this);
		btn_home1 = (ImageView) rootView.findViewById(R.id.btn_home1);
		btn_logout = (ImageView) rootView.findViewById(R.id.btn_logout);
		btn_home1.setOnClickListener(this);
		btn_logout.setOnClickListener(this);

		View header = (View) act.getLayoutInflater().inflate(
				R.layout.chq_listview_header_row, null);
		listView1.addHeaderView(header);

		listView1.setAdapter(adapter);

		listView1.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		listView1.setOnItemClickListener(this);

		return rootView;
	}

	@Override
	public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
		// TODO Auto-generated method stub
		int pos = listView1.getCheckedItemPosition();
		Intent in = null;
		Bundle b = new Bundle();
		switch (pos) {
		case 1:
			Fragment chqBkReqFragment = new ChkBookRequest(act);
			act.setTitle(getString(R.string.lbl_title_cheque_book_request));// "Check Book Request");
			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.frame_container, chqBkReqFragment).commit();
			act.frgIndex=71;
			
			break;
		case 2:
			Fragment stopPaymentFragment = new StopPayment(act);
			act.setTitle(getString(R.string.lbl_stop_payment));
			FragmentManager stopPaymentfragmentManager = getFragmentManager();
			stopPaymentfragmentManager.beginTransaction()
					.replace(R.id.frame_container, stopPaymentFragment)
					.commit();
			act.frgIndex=72;
			
			break;
		case 3:// cheque status view
			Fragment chqStatFragment = new ChequeStatus(act);
			act.setTitle(getString(R.string.lbl_cheque_status));
			FragmentManager chqStatfragmentManager = getFragmentManager();
			chqStatfragmentManager.beginTransaction()
					.replace(R.id.frame_container, chqStatFragment).commit();
			act.frgIndex=73;
			
			break;

		}

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		
		 // case R.id.btn_back:
		  case R.id.btn_home:		
				Intent in=new Intent(act,NewDashboard.class);
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
								CallWebServicelog c=new CallWebServicelog();
								c.execute();
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
						*//*flag = chkConnectivity();
						if (flag == 0)
						{*//*
							CallWebServicelog c=new CallWebServicelog();
							c.execute();
						//}
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
				String keyStr=CryptoClass.Function2();
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

	public void post_successlog(String retvalwbs)
	{
	  respcode="";
	      respdesc="";
	  act.finish();
		System.exit(0);
		
	}
	public void showAlert(final String str) {
		// Toast.makeText(this, str, Toast.LENGTH_LONG).show();
		ErrorDialogClass alert = new ErrorDialogClass(act, "" + str)
		{@Override
			public void onClick(View v)
			{
				switch (v.getId()) 
				{
					case R.id.btn_ok:
						//Log.e("SetMPIN","SetMPIN...CASE trru="+WSCalled);
						if((str.equalsIgnoreCase(respdesc)) &&  (respcode.equalsIgnoreCase("0")))
						{
							post_success(retval);
						}
						else if((str.equalsIgnoreCase(respdesc)) && (respcode.equalsIgnoreCase("1")))
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
		// Fragment fragment = new ChequeMenuActivity(act);
	}
	public 	void post_success(String retval)
	{
		respcode="";
   	    respdesc="";
   		retval = retval.split("~")[1];
		retMess = getString(R.string.alert_058) + retval;
		
		showAlert(retMess);
	}
	
}
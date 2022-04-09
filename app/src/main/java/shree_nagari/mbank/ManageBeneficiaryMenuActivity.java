package shree_nagari.mbank;

//import android.annotation.SuppressLint;

import java.security.Key;
import java.security.PrivateKey;

import mbLib.CryptoClass;
import mbLib.CustomDialogClass;
import mbLib.DatabaseManagement;
import mbLib.MBSUtils;
import shree_nagari.mbank.R;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
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

import javax.crypto.spec.SecretKeySpec;
//import mbLib.DialogBox;

//@SuppressLint("NewApi")
public class ManageBeneficiaryMenuActivity extends Fragment  implements View.OnClickListener
{
	MainActivity act;
	ManageBeneficiaryMenuActivity mngBenfMenu;
  ArrayAdapter<MenuIcon> aa;
  ImageView btn_home1,btn_logout;//, btn_back;
  Button but_exit;
  //DialogBox dbs;
  private ListView listView1,listView2;
  ListView lst_dpt;
  TextView txt_heading,list_benf,remove_benf;
  ImageView img_heading;
  PrivateKey var1=null;	  
  String var5="",var3="",custId,respcode = "",retvalwbs = "",respdesc = "",retMess;
	SecretKeySpec var2=null;
  DatabaseManagement dbms;
  int flag=0;
  
  public ManageBeneficiaryMenuActivity(){}
	
	@SuppressLint("ValidFragment")
	public ManageBeneficiaryMenuActivity(MainActivity a)
	{
		//System.out.println("ManageBeneficiaryMenuActivity()"+a);
		act = a;
		mngBenfMenu=this;
	}

	 public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState) 
	 {		
			//System.out.println("ManageBeneficiaryMenuActivity	onCreateView()	");
			
	        View rootView = inflater.inflate(R.layout.mngben_submenu, container, false);
		 dbms = new DatabaseManagement("shree_nagari.mbank", "listMobileBanking");
		 Cursor c1 = dbms.selectFromTable("SHAREDPREFERENCE", "", null);//("select * from ", null);
		 if (c1 != null) {
			 while (c1.moveToNext()) {
				 //stringValue = c1.getString(0);
				 //Log.e("retvalstr","....."+stringValue);
				 custId = c1.getString(2);
				 //Log.e("custId","......"+custId);
			 }
		 }
	       
	        var1 = act.var1;
	        var3 = act.var3;
			
			list_benf=(TextView)rootView.findViewById(R.id.list_benf);
			remove_benf=(TextView)rootView.findViewById(R.id.remove_benf);
			txt_heading=(TextView)rootView.findViewById(R.id.txt_heading);
			img_heading=(ImageView)rootView.findViewById(R.id.img_heading);
			
			 btn_home1=(ImageView)rootView.findViewById(R.id.btn_home1);
		 btn_logout=(ImageView)rootView.findViewById(R.id.btn_logout);
			/* btn_back=(ImageButton)rootView.findViewById(R.id.btn_back);*/
			 
			// btn_home.setImageResource(R.drawable.ic_home_d);
			// btn_back.setImageResource(R.drawable.backover);
			 
			// btn_back.setOnClickListener(this);
			 btn_home1.setOnClickListener(this);
		 btn_logout.setOnClickListener(this);

			txt_heading.setText(getString(R.string.lbl_manage_beneficiary));
			img_heading.setBackgroundResource(R.mipmap.benefeciary);
			MenuIcon menuItem[] = new MenuIcon[]
	      	{
	      			new MenuIcon(getString(R.string.frmtitle_add_same_bnk_bnf),R.mipmap.arrow),
	      			//new MenuIcon(getString(R.string.frmtitle_edit_same_bnk_bnf),R.mipmap.arrow),
	      	};
			MenuIcon menuItem2[] = new MenuIcon[]
	      	{
	      			new MenuIcon(getString(R.string.frmtitle_add_other_bnk_bnf),R.mipmap.arrow),
	      			//new MenuIcon(getString(R.string.frmtitle_edit_other_bnk_bnf),R.mipmap.arrow),
	      	};
					
			MenuAdaptor adapter1 = new MenuAdaptor(act, R.layout.listview_item_row, menuItem);
			MenuAdaptor adapter2 = new MenuAdaptor(act, R.layout.listview_item_row, menuItem2);
			
			list_benf.setOnClickListener(this);
			remove_benf.setOnClickListener(this);
			
			 listView1 = (ListView)rootView.findViewById(R.id.listView1);
			 listView2 = (ListView)rootView.findViewById(R.id.listView2);
			 /*View header = (View)act.getLayoutInflater().inflate(R.layout.mngbenefiaciary_listview_header_row, null);
		     listView1.addHeaderView(header);
		      */  
		     listView1.setAdapter(adapter1);
		      
			 listView1.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		     //listView1.setOnItemClickListener(this);
			 listView1.setOnItemClickListener(new OnItemClickListener()
			 {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) 
				{
					int pos=listView1.getCheckedItemPosition();
					Intent in =null;
					Bundle b = new Bundle();
					//Log.i("Testing","pos :"+pos);
					Fragment fragment;
					FragmentManager fragmentManager;
					switch(pos)
					{		
						case 0:						
							//Log.i("case 1","Same Bank Beneficiary");
							//Log.i("MBS Case -1", "11");	
							
							fragment = new AddSameBankBeneficiary(act);				
							act.setTitle(getString(R.string.frmtitle_add_same_bnk_bnf));
							fragmentManager = getFragmentManager();
							fragmentManager.beginTransaction()
									.replace(R.id.frame_container, fragment).commit();
							act.frgIndex=63;
							break;
						/*case 1:
							
							//Log.i("case 2","otherrrrrrr Bank Beneficiary");
							//Log.i("MBS Case -2", "11");	
											
							act.setTitle(getString(R.string.frmtitle_edit_same_bnk_bnf));
							fragment = new EditSameBankBeneficiary(act);			
							fragmentManager = getFragmentManager();
							fragmentManager.beginTransaction()
									.replace(R.id.frame_container, fragment).commit();
							act.frgIndex=64;
							break;*/
					}
				}
				 
			 });
		     
		     listView2.setAdapter(adapter2);
		      
			 listView2.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		    // listView2.setOnItemClickListener(this);
			 listView2.setOnItemClickListener(new OnItemClickListener()
			 {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) 
				{
					int pos=listView2.getCheckedItemPosition();
					Intent in =null;
					Bundle b = new Bundle();
					Log.i("Testing","pos :"+pos);
					Fragment fragment;
					FragmentManager fragmentManager;
					switch(pos)
					{		
						case 0:						
							Log.i("case 1","Other Bank Beneficiary");
							Log.i("MBS Case -1", "11");	
							
							fragment = new AddOtherBankBeneficiary(act);				
							act.setTitle(getString(R.string.frmtitle_add_other_bnk_bnf));
							fragmentManager = getFragmentManager();
							fragmentManager.beginTransaction()
									.replace(R.id.frame_container, fragment).commit();
							act.frgIndex=65;
							break;
						/*case 1:
							
							Log.i("case 2","otherrrrrrr Bank Beneficiary");
							Log.i("MBS Case -2", "11");	
											
							act.setTitle(getString(R.string.frmtitle_edit_other_bnk_bnf));
							fragment = new EditOtherBankBeneficiary(act);			
							fragmentManager = getFragmentManager();
							fragmentManager.beginTransaction()
									.replace(R.id.frame_container, fragment).commit();
							act.frgIndex=66;
							break;*/
					}
				}
				 
			 });	     
			
	        return rootView;
	    }
	  
	 @SuppressLint("NonConstantResourceId")
	 public void onClick(View v)
	 {
		 Fragment fragment;
		 FragmentManager fragmentManager;
		 switch(v.getId())
		 {
		 	/*case R.id.btn_back:	
		 		fragment = new ManageBeneficiaryMenuActivity(act);
				fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction()
						.replace(R.id.frame_container, fragment).commit();
				act.frgIndex=6;*/
		 	case R.id.btn_home1:
				Intent in=new Intent(act,NewDashboard.class);
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
			case R.id.list_benf:
				act.setTitle(getString(R.string.lbl_list_benf));
				fragment = new ListBeneficiary(act);			
				fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction()
						.replace(R.id.frame_container, fragment).commit();
				act.frgIndex=61;
				break;
			case R.id.remove_benf:
				act.setTitle(getString(R.string.frmtitle_rem_bnf));
				fragment = new RemoveBeneficiary(act);			
				fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction()
						.replace(R.id.frame_container, fragment).commit();
				act.frgIndex=62;
				break;
		}
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

  /*public void onItemClick(AdapterView<?> paramAdapterView, View paramView, int paramInt, long paramLong)
  {//onItemClick
	  Log.i("Testing","Inside itemclicklistener");
		//int pos=lst_dpt.getCheckedItemPosition();
		int pos=listView1.getCheckedItemPosition();
		Intent in =null;
		Bundle b = new Bundle();
		Log.i("Testing","pos :"+pos);
		Fragment fragment;
		FragmentManager fragmentManager;
		switch(pos)
		{		
			case 0:
				
				Log.i("case 1","Same Bank Beneficiary");
				Log.i("MBS Case -1", "11");	
				
				fragment = new AddSameBankBeneficiary(act);				
				act.setTitle(getString(R.string.frmtitle_add_same_bnk_bnf));
				fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction()
						.replace(R.id.frame_container, fragment).commit();		
				
				in =new Intent(getApplicationContext(), SameBankBeneficiary.class);
				Log.i("MBS Case -1", "22");
				in.putExtra("sel_manage_beneficiary_tab", "0");
				in.putExtra("sel_add_or_edit", "menu");
				in.putExtra("sel_add_or_edit_other", "menu");
				Log.i("MBS Case -1", "33");
							
				try
				{
					Log.i("MBS Case -1", "44");
					Bundle bnd1=this.getIntent().getExtras();
					Log.i("MBS Case -1", "55");
					in.putExtras(bnd1);
					Log.i("MBS Case -1", "66");
					
					
				}
				catch(NullPointerException npe)
				{
					Log.i("MBS Appli Error", "Can't find bundle...1");
				}
				finally
				{
					Log.i("MBS Case -1", "DONE");
					startActivity(in);
					finish(); 
				}
				break;
				
			case 1:
				
				Log.i("case 2","otherrrrrrr Bank Beneficiary");
				Log.i("MBS Case -2", "11");	
								
				act.setTitle(getString(R.string.frmtitle_edit_same_bnk_bnf));
				fragment = new EditSameBankBeneficiary(act);			
				fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction()
						.replace(R.id.frame_container, fragment).commit();		
				
				in =new Intent(getApplicationContext(), OtherBankBeneficiary.class);
				Log.i("MBS Case -2", "22");
				in.putExtra("sel_manage_beneficiary_tab", "1");
				in.putExtra("sel_add_or_edit", "menu");
				in.putExtra("sel_add_or_edit_other", "menu");
				Log.i("MBS Case -2", "33");
							
				try
				{
					Log.i("MBS Case -2", "44");
					Bundle bnd1=this.getIntent().getExtras();
					Log.i("MBS Case -2", "55");
					in.putExtras(bnd1);
					Log.i("MBS Case -2", "66");
					
					
				}
				catch(NullPointerException npe)
				{
					Log.i("MBS Appli Error", "Can't find bundle...case 2");
				}
				finally
				{
					Log.i("MBS Case -2", "DONE");
					startActivity(in);
					finish(); 
				}
				break;
			
			case 3:
				Log.i("case 3",  "Remove Beneficiary");
				Log.i("MBS Case -3", "11");
				
				
				act.setTitle(getString(R.string.lbl_remove_benf));
				fragment = new RemoveBeneficiary(act);				
				fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction()
						.replace(R.id.frame_container, fragment).commit();		
				
				in =new Intent(getApplicationContext(), RemoveBeneficiary.class);
				Log.i("MBS Case -3", "22");
				in.putExtra("sel_manage_beneficiary_tab", "2");
				in.putExtra("sel_add_or_edit", "menu");
				in.putExtra("sel_add_or_edit_other", "menu");
				Log.i("MBS Case -3", "33");
							
				try
				{
					Log.i("MBS Case -3", "44");
					Bundle bnd1=this.getIntent().getExtras();
					Log.i("MBS Case -3", "55");
					in.putExtras(bnd1);
					Log.i("MBS Case -3", "66");
					
					
				}
				catch(NullPointerException npe)
				{
					Log.i("MBS Appli Error", "Can't find bundle...3");
				}
				finally
				{
					Log.i("MBS Case -3", "DONE");
					startActivity(in);
					finish(); 
				}
				break;
			case 4:
				Log.i("case 4",  "List Beneficiary");
				Log.i("MBS Case -4", "11");
				
				act.setTitle(getString(R.string.lbl_list_benf));
				fragment = new ListBeneficiary(act);				
				fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction()
						.replace(R.id.frame_container, fragment).commit();		
				
				in =new Intent(getApplicationContext(), ListBeneficiary.class);
				Log.i("MBS Case -4", "22");
				in.putExtra("sel_manage_beneficiary_tab", "3");
				in.putExtra("sel_add_or_edit", "menu");
				in.putExtra("sel_add_or_edit_other", "menu");
				Log.i("MBS Case -4", "33");
							
				try
				{
					Log.i("MBS Case -4", "44");
					Bundle bnd1=this.getIntent().getExtras();
					Log.i("MBS Case -4", "55");
					in.putExtras(bnd1);
					Log.i("MBS Case -4", "66");
					
					
				}
				catch(NullPointerException npe)
				{
					Log.i("MBS Appli Error", "Can't find bundle...3");
				}
				finally
				{
					Log.i("MBS Case -4", "DONE");
					startActivity(in);
					finish(); 
				}
				break;
		}
		
	}*/
}//end   ManageBeneficiaryMenuActivity
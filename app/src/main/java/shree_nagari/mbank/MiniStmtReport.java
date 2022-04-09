package shree_nagari.mbank;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.security.PrivateKey;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import javax.crypto.spec.SecretKeySpec;

import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import shree_nagari.mbank.R;
import mbLib.CryptoClass;
import mbLib.CustomDialogClass;
import mbLib.DatabaseManagement;
import mbLib.DialogBox;
import mbLib.MBSUtils;
import mbLib.MiniStatementBean;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.core.content.FileProvider;
//import android.annotation.SuppressLint;

//@SuppressLint("NewApi")
public class MiniStmtReport extends Fragment implements OnClickListener {
	MainActivity act;
	DialogBox dbs;	
	MiniStmtReport miniStmtRpt;
	TextView accNo, branch, sch_acno, name, bal,avil_bal;
	ImageButton btn_home;//back,
	String actype_val, branch_val, sch_acno_val, name_val, bal_val
	,respcode="",retval="",respdesc="",custId="",retvalwbs="";
	String str = "", spi_str = "";
	String balance,retMess,avilablebal;
	ListView listView1 ;
	List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
	public MiniStmtReport(){}
	 DatabaseManagement dbms;
		private static String NAMESPACE = "";
		private static String URL = "";
		private static String SOAP_ACTION = "";
		private static  String METHOD_NAME = "";
		PrivateKey var1=null;	  
		String var5="",var3="";
		SecretKeySpec var2=null;
	String stringValue;
	String amnt="";
	TextView txt_heading;
	ImageView img_heading;
	ImageView btn_home1,btn_logout;
	int flag=0;
	ArrayList<MiniStatementBean> MiniStmntBeanArray;

	@SuppressLint("ValidFragment")
	public MiniStmtReport(MainActivity a, String str1, String all_str1, String transactions, String pasamnt, String avibal)
	{
		//System.out.println("MiniStmtReport()");
		act = a;
		miniStmtRpt=this;
		amnt=pasamnt;
		str =str1;
		spi_str = all_str1;		
		retMess = transactions;
        avilablebal = avibal;
		
		stringValue =transactions;
	}
	

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) 
	{	
		var1 = act.var1;
		var3 = act.var3;
		dbms = new DatabaseManagement("shree_nagari.mbank", "listMobileBanking");
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
	    View rootView = inflater.inflate(R.layout.view_mini_stmt_report, container, false);
    	accNo = (TextView) rootView.findViewById(R.id.txt_actype);
		bal=(TextView) rootView.findViewById(R.id.cur_bal);
	    avil_bal=(TextView)rootView.findViewById(R.id.avil_bal);
		txt_heading=(TextView)rootView.findViewById(R.id.txt_heading);
		img_heading=(ImageView)rootView.findViewById(R.id.img_heading);
		try
		{
			bal.setText(amnt.trim());
         	avil_bal.setText(avilablebal.trim());
		}
		catch(Exception e)
		{
			Log.e("Mini Statement Report", ""+e);
		}
		btn_home=(ImageButton)rootView.findViewById(R.id.btn_home);
		btn_home.setOnClickListener(this);			
		listView1 = (ListView) rootView.findViewById(R.id.listView1);
		btn_home1 = (ImageView) rootView.findViewById(R.id.btn_home1);
		btn_logout = (ImageView) rootView.findViewById(R.id.btn_logout);
		btn_home1.setOnClickListener(this);
		btn_logout.setOnClickListener(this);
		setValues();
        return rootView;
    }
	
	public void setValues() 
	{
		txt_heading.setText(getString(R.string.mini_statement));
		img_heading.setBackgroundResource(R.mipmap.ministmnt);
		String sel_str = spi_str;
		sel_str=sel_str.replaceAll("-", "#");
		Accounts acObj=new Accounts(sel_str);
		if(acObj.getAccType().equalsIgnoreCase("SB"))
		{
			actype_val = "Savings";
		}
		else if(acObj.getAccType().equalsIgnoreCase("LO"))
		{
			actype_val = "Loan";
		}
		else if(acObj.getAccType().equalsIgnoreCase("RP"))
		{
			actype_val = "Re-Investment Plan";
		}
		else if(acObj.getAccType().equalsIgnoreCase("FD"))
		{
			actype_val = "Fixed Deposite";
		}
		else if(acObj.getAccType().equalsIgnoreCase("CA"))
		{
			actype_val = "Current Account";
		}
		
		branch_val = acObj.getBrCd(); 
		sch_acno_val= acObj.getSchCd()+ "-" + acObj.getAccNo();
		name_val = acObj.getHolderName();
		
		String trn_str=retMess;
		String str1[] = trn_str.split("~");
		if(str1[0].indexOf("SUCCESS")>-1) {
			MiniStmntBeanArray = new ArrayList<MiniStatementBean>();
			for (int j = 1; j < str1.length; j++) {
				MiniStatementBean beanObj = new MiniStatementBean();
				String string2[] = str1[j].split("#");
				HashMap<String, String> map = new HashMap<String, String>();
				String[] from = new String[]{"rowid", "col_0", "col_1", "col_2", "col_3", "col_5"};
				int[] to = new int[]{R.id.item1, R.id.item2, R.id.item3, R.id.item4, R.id.item5, R.id.item7};
				map.put("col_0", string2[0].trim());
				map.put("col_1", properCase(string2[1].trim()));
				map.put("col_2", "" + MBSUtils.amountFormat(string2[2].trim(), false, act));
				map.put("col_3", string2[3].trim());

				beanObj.setDate(string2[0].trim());
				beanObj.setDescr(properCase(string2[1].trim()));
				beanObj.setAmount("" + MBSUtils.amountFormat(string2[2].trim(), false, act));
				beanObj.setDrCr(string2[3].trim());
				MiniStmntBeanArray.add(beanObj);

				fillMaps.add(map);
				/*SimpleAdapter adapter = new SimpleAdapter(act, fillMaps, R.layout.mini_stmt_list, from, to);
		        listView1.setAdapter(adapter);*/
			}
			if (MiniStmntBeanArray.size() > 0) {
				CustomAdapterMiniStatement ada = new CustomAdapterMiniStatement(act, MiniStmntBeanArray);
				listView1.setAdapter(ada);
			}
			accNo.setText(MBSUtils.get16digitsAccNo(str));
			listView1.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
			listView1
					.setOnItemClickListener(new AdapterView.OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> adapterView,
												View view, int i, long l) {
							ArrayList<MiniStatementBean> MiniStmntBean = new ArrayList<MiniStatementBean>();
							MiniStmntBean=MiniStmntBeanArray;
							String strdate=MiniStmntBean.get(i).getDate();
							String strcrdr=MiniStmntBean.get(i).getDrCr();
							String stramount=MiniStmntBean.get(i).getAmount();
							String strdesc=MiniStmntBean.get(i).getDescr();
							String sharestring=strdate+"\t"+strcrdr+"\t"+stramount+"\t"+strdesc;
							Log.e("TAG", "ShareReceiptValues:---->123\n" + sharestring);
							showshareAlert(sharestring);
						}
					});
		}
	}

	public void showshareAlert(final String str)
	{
		final String accno="Deepika Pille";
		CustomDialogClass alert=new CustomDialogClass(act, str) {
			@Override
			protected void onCreate(Bundle savedInstanceState)
			{
				super.onCreate(savedInstanceState);
				setContentView(R.layout.custom_dialog_box);
				Button btn = (Button) findViewById(R.id.btn_cancel);
				TextView txt_message=(TextView)findViewById(R.id.txt_dia);
				txt_message.setText(str);
				btn.setOnClickListener(this);
				btn.setText("Share");
				Button btnok = (Button) findViewById(R.id.btn_ok);
				btnok.setOnClickListener(this);
				btnok.setText("OK");
			}
			@Override
			public void onClick(View v) {
				switch (v.getId()) {
					case R.id.btn_ok:
						this.dismiss();
//						Intent share = new Intent(Intent.ACTION_SEND);
//						Bitmap src = BitmapFactory.decodeResource(getResources(), R.mipmap.bg); // the original file yourimage.jpg i added in resources
//						Bitmap dest = Bitmap.createBitmap(src.getWidth(), src.getHeight(), Bitmap.Config.ARGB_8888);
//
//						String shareBody1 = "Paid On : "+"CurrentDateTime";
//						Typeface typeface1 = Typeface.createFromAsset(getContext().getAssets(), "fonts/calibri.ttf");
//						//Typeface typeface = Typeface.create(typeface1, Typeface.DEFAULT_BOLD);
//						Canvas cs = new Canvas(dest);
//						Paint tPaint = new Paint(Paint.LINEAR_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
//
//						tPaint.setTypeface(typeface1);
//						tPaint.setTextSize(22);
//						tPaint.setColor(Color.BLACK);
//						//tPaint.setStyle(Paint.Style.FILL);
//						cs.drawBitmap(src, 0f, 0f, null);
//						float height = tPaint.measureText("yY");
//						float width = tPaint.measureText(shareBody1);
//						float x_coord = (src.getWidth() - width)/2;
//						cs.drawText("Paid On : "+"CurrentDateTime", x_coord, height+150f, tPaint);
//						cs.drawText("From : "+"dbaccountno", x_coord, height+200f, tPaint);
//						cs.drawText("To : "+"craccountno", x_coord, height+250f, tPaint);
//						cs.drawText("Amount : "+"1000.00 Rs", x_coord, height+300f, tPaint);
//						cs.drawText("Reference ID : "+"request_id", x_coord, height+350f, tPaint);// 15f is to put space between top edge and the text, if you want to change it, you can
//						try {
//							dest.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(new File("/sdcard/ImageAfterAddingText.jpg")));
//							// dest is Bitmap, if you want to preview the final image, you can display it on screen also before saving
//						} catch (FileNotFoundException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//						share.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//						share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//						Uri apkURI = FileProvider.getUriForFile(act,act.getPackageName(), new File("/sdcard/ImageAfterAddingText.jpg"));
//						share.putExtra(Intent.EXTRA_STREAM,	Uri.parse("file:///sdcard/ImageAfterAddingText.jpg"));
//						share.putExtra(Intent.EXTRA_STREAM,apkURI);
//						share.setType("image/*");
//						startActivity(Intent.createChooser(share, "Share Image"));
						break;

					case R.id.btn_cancel:
						/*String shareBody = null;
						shareBody = "Beneficiary Name : ";
						Intent sharingIntent = new Intent(Intent.ACTION_SEND);
						sharingIntent.setType("text/plain");
						sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here");
						sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
						startActivity(sharingIntent);*/

						Calendar c = Calendar.getInstance();
						SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
						String CurrentDateTime = sdf.format(c.getTime());
						Intent share = new Intent(Intent.ACTION_SEND);
						Bitmap src = BitmapFactory.decodeResource(getResources(), R.mipmap.bg); // the original file yourimage.jpg i added in resources
						Bitmap dest = Bitmap.createBitmap(src.getWidth(), src.getHeight(), Bitmap.Config.ARGB_8888);

						String shareBody1 = "Paid On : "+"CurrentDateTimeMMMMMMMMMMMMM";//mm
						Typeface typeface1 = Typeface.createFromAsset(getContext().getAssets(), "fonts/calibri.ttf");
						Canvas cs = new Canvas(dest);
						Paint tPaint = new Paint(Paint.LINEAR_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
						tPaint.setTypeface(typeface1);
						tPaint.setTextSize(20);
						tPaint.setColor(Color.BLACK);
						cs.drawBitmap(src, 0f, 0f, null);
						float height = tPaint.measureText("yY");
						float width = tPaint.measureText(shareBody1);
						float x_coord = (src.getWidth() - width)/2;//jObj.getString("BENFNAME")
						height=100f;
						//String sharestring=strdate+"\t"+strcrdr+" \t"+stramount+"\t\n"+strdesc;
						String []arr = str.split("\t");

//						for (String s : arr) {
//							Log.e("TAG", "ShareReceiptValues:---->" + s);
//						}
						cs.drawText("Date : "+arr[0], 20, height+20f, tPaint);//270 100
						cs.drawText("Account No : "+accNo.getText().toString(), 25, height+60f, tPaint);//220
						cs.drawText("Amount : "+arr[2], 20, height+100f, tPaint);//320
						cs.drawText("Remark : ", 20, height+130f, tPaint);//370
						//cs.drawText(chenextlne(str), x_coord, height+370f, tPaint);//370
						MBSUtils.drawMultilineText(cs,arr[3],20,height+150f,tPaint);
						try {
							dest.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(new File("/sdcard/SharedImage.jpg")));
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						}
						// dest is Bitmap, if you want to preview the final image, you can display it on screen also before saving
						share.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
						share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
						Uri apkURI = FileProvider.getUriForFile(act,act.getPackageName(), new File("/sdcard/SharedImage.jpg"));
						share.putExtra(Intent.EXTRA_STREAM,	Uri.parse("file:///sdcard/SharedImage.jpg"));
						share.putExtra(Intent.EXTRA_STREAM,apkURI);
						share.setType("image/*");
						startActivityForResult(Intent.createChooser(share, "Share Image"),0);

						break;
					default:
						break;
				}
				dismiss();
			}
		};
		alert.show();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
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
				//Log.e("SetMPIN","SetMPIN...btn CLicked=="+R.id.btn_ok+"==="+v.getId());
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
					// retMess =
					// "Network Disconnected. Please Check Network Settings.";
					retMess = getString(R.string.alert_014);
					showAlert(retMess);
					
					break;
				default:
					flag = 1;
					// retMess = "Network Unavailable. Please Try Again.";
					retMess = getString(R.string.alert_000);
					// setAlert();
					showAlert(retMess);
					
					break;
				}
			} else {
				flag = 1;
				// retMess = "Network Unavailable. Please Try Again.";
				retMess = getString(R.string.alert_000);
				// setAlert();
				showAlert(retMess);
				
			}
		} catch (NullPointerException ne) {

			Log.i("mayuri", "NullPointerException Exception" + ne);
			flag = 1;
			
			retMess = "Network Unavailable. Please Try Again.";
			// setAlert();
			showAlert(retMess);
			

		} catch (Exception e) {
			Log.i("mayuri", "Exception" + e);
			flag = 1;
		    retMess = getString(R.string.alert_000);
			
			showAlert(retMess);
			
		}
		return flag;
	}
	public 	void post_success(String retval)
	{
		respcode="";
   	    respdesc="";
   	
		retval = retval.split("~")[1];
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

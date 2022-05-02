package shree_nagari.mbank;



import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Map;

import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import mbLib.CryptoClass;
import mbLib.CustomDialogClass;
import mbLib.DialogBox;
import mbLib.MBSUtils;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

public class SessionOut extends Activity implements OnClickListener {
	ImageView continueBtn;
	TextView txt_version_no;
	TextView txt_welcome,dyn_msg;
	String version = "",retVal = "",retMess="";;
	private static final int SWIPE_MIN_DISTANCE = 60;
	private static final int SWIPE_THRESHOLD_VELOCITY = 400;
	private ViewFlipper mViewFlipper;
	ImageView splash_logo;
	private AnimationListener mAnimationListener;
	private Context mContext;
	DialogBox dbs;
	int cnt = 0, flag = 0;
	int netFlg, gpsFlg;
	String retval = "",respcode="",respdesc="";
	private static final int REQUEST_APP_SETTINGS = 168;
	private static String NAMESPACE = "";
	private static String URL = "";
	private static String SOAP_ACTION = "";
	Map<String, Object> keys = null;
	static PrivateKey var1 = null;
	SecretKeySpec var2 = null;
	static PublicKey var4 = null;
	String var5 = "", var3 = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(LayoutParams.FLAG_SECURE, LayoutParams.FLAG_SECURE);
		setContentView(R.layout.splash_page);
		
		//continueBtn = (Button) findViewById(R.id.continue_button);
		continueBtn=(ImageView)findViewById(R.id.continue_button);
		txt_version_no = (TextView) findViewById(R.id.txt_version_no);
		dyn_msg = (TextView) findViewById(R.id.dynamicmsg);
		
		txt_welcome=(TextView)findViewById(R.id.welcome);
		txt_welcome.setText(getString(R.string.txt_timeout));
		txt_version_no.setVisibility(View.INVISIBLE);
	 		
		continueBtn.setOnClickListener(this);
		//continueBtn.setEnabled(false);
		//txt_version_no.setText("Version : " + version);
		
		dbs = new DialogBox(this);
	
		try {
			keys = CryptoClass.Function1();
			var1 = (PrivateKey) keys.get("private");
			var4 = (PublicKey) keys.get("public");
		} catch (Exception e) {
			e.printStackTrace();
		}
		flag = chkConnectivity();
		if (flag == 0) {
		/*CallWSFirst c = new CallWSFirst();
		c.execute();*/
		}
		//Commiting
	}
	
	
	@Override
	public void onClick(View arg0) {
		
		Log.e("SAM","ONCLICK1 ");
		Log.e("SAM2","ONCLICK if 2 ");
		Log.e("sessionvar1","----"+var1);
		Log.e("sessionva3","----"+var3);

		//System.exit(0);
		Intent in = new Intent(this, SplashPage.class);
		in.putExtra("VAR1", var1);
		in.putExtra("VAR3", var3);
		startActivity(in);
		finish();
	}
	
   
		public void onBackPressed() {
			/*DialogBox dbs = new DialogBox(this);
			dbs.get_adb().setMessage(getString(R.string.lbl_do_you_want_to_exit));
			dbs.get_adb().setPositiveButton("Yes",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface arg0, int arg1) {
							//startActivity(lang_activity);
							//finish();
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
			CustomDialogClass alert = new CustomDialogClass(this,getString(R.string.lbl_do_you_want_to_exit))
			{
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					switch (v.getId())
					{
						case R.id.btn_ok:
							System.exit(0);
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
		
	
		/*class CallWSFirst extends AsyncTask<Void, Void, Void> {
			String[] xmlTags = { "PARAMS" };
			String[] valuesToEncrypt = new String[1];
			String generatedXML = "";
			String ValidationData = "";
			JSONObject jsonObj = new JSONObject();

			protected void onPreExecute() {
				try {
					jsonObj.put("METHODCODE", "85");
					jsonObj.put("IMEINO", MBSUtils.getImeiNumber(SessionOut.this));
					jsonObj.put("PUBLICKEY",
							new String(Base64.encodeBase64(var4.getEncoded())));
					Log.e("jsonObj","----"+jsonObj.toString());
				} catch (JSONException je) {
					je.printStackTrace();
				}
			}

			protected Void doInBackground(Void... arg0) {
				String value4 = getString(R.string.namespace);
				String value5 = getString(R.string.soap_action);
				String value6 = getString(R.string.url);
				final String value7 = "callWebservice";

				try {
					SoapObject request = new SoapObject(value4, value7);
					request.addProperty("value1", jsonObj.toString());
					request.addProperty("value2", "NA");
					request.addProperty("value3", "NA");
					SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
							SoapEnvelope.VER11);
					envelope.setOutputSoapObject(request);
					HttpTransportSE androidHttpTransport = new HttpTransportSE(
							value6, 45000);
					androidHttpTransport.call(value5, envelope);
					var5 = envelope.bodyIn.toString().trim();
					var5 = var5.substring(var5.indexOf("=") + 1, var5.length() - 3);
				}// end try
				catch (Exception e) {
					e.printStackTrace();
					System.out.println("Exception 2");
					System.out.println("SameBankTransfer   Exception" + e);
				}
				return null;
			}// end doInBackground

			protected void onPostExecute(Void paramVoid) // "BANKNAMES"
			{
				var3 = var5;// xml_data[0];
				Log.e("strvarsessionnnnnnnnnn","----"+var3);
				try {
					continueBtn.setEnabled(true);
					
				}// try
				catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}// end onPostExecute

		}// end CallWebServiceGetBank*/

	class CallWSFirst extends AsyncTask<Void, Void, Void>
	{
		JSONObject jsonObj = new JSONObject();
		String version1="VERSION~"+version;
		LoadProgressBar loadProBarObj = new LoadProgressBar(SessionOut.this);

		protected void onPreExecute()
		{
			loadProBarObj.show();;
			try
			{
				jsonObj.put("IMEINO", MBSUtils.getImeiNumber(SessionOut.this));
				jsonObj.put("PUBLICKEY", new String(Base64.encodeBase64(var4.getEncoded())));
				jsonObj.put("METHODCODE", "85");
				Log.e("DSP","jsonObj==="+jsonObj);

			}
			catch (JSONException je)
			{
				je.printStackTrace();
			}
		}
		@Override
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			String value4 = getString(R.string.namespace);
			String value5 = getString(R.string.soap_action);
			String value6 = getString(R.string.url);
			final String value7 = "callWebservice";
			try {
				Log.e("DSP","splashpage---version1==="+jsonObj.toString());

				SoapObject request = new SoapObject(value4, value7);
				String val=CryptoClass.Function3(jsonObj.toString(),CryptoClass.getPrivateKey());
				Log.e("SPLASHPAGE","val=="+val);
				request.addProperty("value1", val);
				request.addProperty("value2", version1);
				request.addProperty("value3", "NA");
				SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
						SoapEnvelope.VER11);
				envelope.setOutputSoapObject(request);
				HttpTransportSE androidHttpTransport = new HttpTransportSE(value6,
						45000);
				androidHttpTransport.call(value5, envelope);
				var5 = envelope.bodyIn.toString().trim();
				var5 = var5.substring(var5.indexOf("=") + 1,
						var5.length() - 3);
			}// end try
			catch (Exception e) {
				e.printStackTrace();
				System.out.println("Exception 2");
				System.out.println("Splashpage   Exception" + e);
			}
			return null;
		}

		protected void onPostExecute(Void paramVoid)
		{
			loadProBarObj.dismiss();
			try {
				continueBtn.setEnabled(true);
				Log.e("DSP","splashpage---var5==="+var5);
				String resp=CryptoClass.Function4(var5,CryptoClass.getPrivateKey());
				Log.e("DSP","splashpage---str==="+resp);

				if(resp.indexOf("EXCEPTION")>-1)
				{
					showAlertserver(respdesc);
				}
				/*else if(resp.indexOf("OLDVERSION")>-1)
				{
					versionFlg="2";
					retMess = getString(R.string.alert_oldversion);
					showversionAlert(retMess);
				}*/
				else {
					JSONObject jsonObj = new JSONObject(resp.trim());

					if (jsonObj.has("RESPCODE")) {
						respcode = jsonObj.getString("RESPCODE");
					} else {
						respcode = "-1";
					}
					if (jsonObj.has("RESPDESC")) {
						respdesc = jsonObj.getString("RESPDESC");
					} else {
						respdesc = "";
					}

					if (respdesc.length() > 0) {
						if (respdesc.equalsIgnoreCase("Server Not Found")) {
							showAlertserver(respdesc);
						} else {
							showAlert(respdesc);
						}
					} else {
						if (respcode.equalsIgnoreCase("0")) {

							var3 = jsonObj.getString("TOKEN");
							Log.e("DSP", "splashpage---var3===" + var3);

						}
					}
				}
			}catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	public void showAlertserver(String str) {

		// Log.e("SAM","===ShowAlert ");
		ErrorDialogClass alert = new ErrorDialogClass(SessionOut.this, "" + str)

		{
			Intent in = null;

			@Override
			public void onClick(View v) {
				switch (v.getId()) {
					case R.id.btn_ok:
						finish();
						System.exit(0);
						dismiss();
						break;
				}
				this.dismiss();

			}
		};
		alert.show();
	}

	public int chkConnectivity() {
			ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
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
						retMess = getString(R.string.alert_000);
						// setAlert();
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
					retMess = getString(R.string.alert_000);
					// setAlert();
					showAlert(retMess);
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
			}
			return flag;
		}
		
		public void showAlert(final String str) {
			ErrorDialogClass alert = new ErrorDialogClass(SessionOut.this,""+str);
			alert.show();
		}


}

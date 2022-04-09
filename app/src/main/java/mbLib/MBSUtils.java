package mbLib;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.safetynet.SafetyNetApi;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import org.apache.commons.codec.binary.Hex;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.KeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import androidx.annotation.NonNull;
import shree_nagari.mbank.ErrorDialogClass;
import shree_nagari.mbank.MainActivity;
import shree_nagari.mbank.R;
import shree_nagari.mbank.SBKLoginActivity;
@SuppressLint({"NewApi","MissingPermission"})
public class MBSUtils {
	static double Latitude;
	static double Longitude;
	static String loc = "";
	static int flag = 0;
	static String retMess="";

	public static String get16digitsAccNo(String accountStr) {// get16digitsAccNo

		String branch = "", scheme = "", accNo = "";
		try {// 1
				// 5-301-LO-5899-KADEKAR KAVITA KIRAN
			/*branch = lPad("" + accountStr.split("-")[0], 3, "0");
			scheme = lPad("" + accountStr.split("-")[1], 4, "0");
			accNo = lPad("" + accountStr.split("-")[3], 7, "0");*/
			accNo = accountStr.split("-")[6];
		}// 1
		catch (Exception e) {// 1
			System.out.println("Exception in get16digitsAccNo()::::" + e);
		}// 1
		return accNo;

	}// get16digitsAccNo

	public static String getCustName(String accountStr) {// get16digitsAccNo

		String custName = "";
		try {// 1
				// 5-301-LO-5899-KADEKAR KAVITA KIRAN
			custName = accountStr.split("-")[4];
		}// 1
		catch (Exception e) {// 1
			System.out.println("Exception in getCustName()::::" + e);
		}// 1
		return custName;
	}

	public static String lPad(String str, int noOfChars, String padChar) {// lPad
		String retVal = "";
		try {// 1
			for (int i = 0; i < (noOfChars - str.length()); i++) {// 2
				retVal = retVal + padChar;
			}// 2
			retVal = retVal + str;
		}// 1
		catch (Exception e) {// 1
			System.out.println("Exception in lPad()::::" + e);
		}// 1
		return retVal;
	}// lPad

	public static String rPad(String str, int noOfChars, String padChar) {// lPad
		String retVal = "";
		try {// 1
			retVal = str;
			for (int i = str.length(); i < noOfChars; i++) {// 2
				retVal = retVal + padChar;
			}// 2
		}// 1
		catch (Exception e) {// 1
			System.out.println("Exception in lPad()::::" + e);
		}// 1
		return retVal;
	}// lPad

	public static String getImeiNumber(Activity act)
	{

		String imeiNo="",osVersion="";
		String deviceUniqueIdentifier = null;
		TelephonyManager tm = (TelephonyManager) act.getSystemService(Context.TELEPHONY_SERVICE);

		try
		{
			osVersion=Build.VERSION.RELEASE;
			if (osVersion.indexOf(".")>-1)
				osVersion=osVersion.substring(0, osVersion.indexOf("."));
			String regexStr = "^[0-9]*$";

			if(osVersion.trim().matches(regexStr))
			{
				if (Integer.parseInt(osVersion)<5)
					deviceUniqueIdentifier = tm.getDeviceId();
				else if (Integer.parseInt(osVersion)<10)
				{
					deviceUniqueIdentifier = tm.getDeviceId(0);
					if(deviceUniqueIdentifier==null || deviceUniqueIdentifier.length()<15)
						deviceUniqueIdentifier = tm.getDeviceId(1);
				}
				else
					deviceUniqueIdentifier = Settings.Secure.getString(act.getContentResolver(), Settings.Secure.ANDROID_ID);
			}
			else {
				deviceUniqueIdentifier = Settings.Secure.getString(act.getContentResolver(), Settings.Secure.ANDROID_ID);
			}
			imeiNo= deviceUniqueIdentifier;
		}
		catch(SecurityException e)
		{
			e.printStackTrace();
			imeiNo = deviceUniqueIdentifier = Settings.Secure.getString(act.getContentResolver(), Settings.Secure.ANDROID_ID);
		}
		return imeiNo;

	}

	public static boolean validateMobNo(String mobNo) {
		Pattern pattern = Pattern.compile("^[789]\\d{9}$");
		Matcher matcher = pattern.matcher(mobNo);

		if (matcher.matches()) {
			System.out.println("valid");
			return true;
		} else {
			System.out.println("invalid");
			return false;
		}
	}

	public static String amountFormatnew(String amt,boolean isBalance,Activity c)
	{
		String str="";
		double d=Double.parseDouble(amt);
		String drOrCr="";
		//drOrCr=" "+(d>0?c.getString(R.string.lbl_credit_short):d<0?c.getString(R.string.lbl_debit_short):"");
		d=Math.abs(d);
		str=""+d;
		str=str.substring(0, str.indexOf("."))+rPad(str.substring(str.indexOf(".")),3,"0");
		if(isBalance)
			str=str.substring(0, str.indexOf(".")+3)+drOrCr;
		return str;
	}
	public static String amountFormatchange(String amt,boolean isBalance,Activity a)
	{
		String strset="";
		if(amt.contains("."))
		{
			strset=amt.split("\\.")[0]+"."+amt.split("\\.")[1].substring(0,2);
		}
		else{
			strset=amt+".00";
		}
		return strset;
	}

	public static boolean validateEmail(String email) {
		String EMAIL_REGEX = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
		Boolean b;
		if (email.length() > 0) {
			b = email.matches(EMAIL_REGEX);
			System.out.println(" email: " + email + " :Valid = " + b);
		} else
			b = false;
		return b;
	}

	public static String getAccTypeDesc(String acctype) {
		String str = "";
		if (acctype.equalsIgnoreCase("SB")) {
			str = "Savings";
		} else if (acctype.equalsIgnoreCase("LO")) {
			str = "Loan";
		} else if (acctype.equalsIgnoreCase("RP")) {
			str = "Re-Investment Plan";
		} else if (acctype.equalsIgnoreCase("FD")) {
			str = "Fixed Deposit";
		} else if (acctype.equalsIgnoreCase("CA")) {
			str = "Current Account";
		} else if (acctype.equalsIgnoreCase("PG")) {
			str = "Pigmi";
		} else if (acctype.equalsIgnoreCase("RA")) {
			str = "RD Account";
		}
		if (str.length() == 0) {
			str = acctype;
		}
		return str;
	}

	public static String amountFormat(String amt, boolean isBalance,
			MainActivity c) {
		String str = "";
		double d = Double.parseDouble(amt);
		
		String drOrCr = "";
		drOrCr = " "
				+ (d > 0 ? c.getString(R.string.lbl_credit_short) : d < 0 ? c
						.getString(R.string.lbl_debit_short) : "");
		d = Math.abs(d);
		
		DecimalFormat df = new DecimalFormat("0.00");
		df.setMaximumFractionDigits(15);

		//System.out.println(df.format(d));
		str = "" + df.format(d);
		str = str.substring(0, str.indexOf("."))
				+ rPad(str.substring(str.indexOf(".")), 3, "0");
		if (isBalance)
			str = str.substring(0, str.indexOf(".") + 3) + drOrCr;
		return str;
	}

	public static boolean isNumeric(String str) {

		try {
			Double num = Double.parseDouble(str);

		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

	/*
	 * public static String getMyPhoneNO(Activity act) { String
	 * getSimSerialNumber =""; String getSimNumber ="";
	 * 
	 * try{ TelephonyManager telemamanger = (TelephonyManager)
	 * act.getSystemService(Context.TELEPHONY_SERVICE); getSimSerialNumber =
	 * telemamanger.getSimSerialNumber(); getSimNumber =
	 * telemamanger.getLine1Number();
	 * 
	 * if(getSimNumber.length()==0) { getSimNumber="NOTAVAILABLE"; }
	 * 
	 * } catch(Exception e) { getSimNumber="NOTAVAILABLE";
	 * 
	 * } return getSimNumber; }
	 */

	public static String getMyPhoneNO(Activity act) {
		String getSimSerialNumber = "";
		String getSimNumber = "";

		try {
			TelephonyManager telemamanger = (TelephonyManager) act.getSystemService(Context.TELEPHONY_SERVICE);
			getSimSerialNumber = telemamanger.getSimSerialNumber();
			getSimNumber = telemamanger.getLine1Number();

			if (getSimSerialNumber.length() == 0) {
				getSimSerialNumber = "NOTAVAILABLE";
			}

		} catch (Exception e) {
			getSimSerialNumber = "NOTAVAILABLE";

		}
		return getSimNumber;//="9798987676";// getSimSerialNumber;
	}

	public static String getLocalIpAddress() {
		String ip = "", port = "";
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						ip = Formatter.formatIpAddress(inetAddress.hashCode());
						Log.e("Pigmi", "***** IP=" + ip);
						return ip;
					}
				}
			}
		} catch (SocketException ex) {
			Log.e("Pigmi", ex.toString());
		}
		return ip;
	}

	public static String getLocation(Activity act) {

		LocationManager locationManager = (LocationManager) act
				.getSystemService(Context.LOCATION_SERVICE);

		// Define a listener that responds to location updates
		LocationListener locationListener = new LocationListener() {

			public void onStatusChanged(String provider, int status,
					Bundle extras) {
			}

			public void onProviderEnabled(String provider) {
			}

			public void onProviderDisabled(String provider) {
			}

			@Override
			public void onLocationChanged(Location location) {
				// TODO Auto-generated method stub
				Latitude = location.getLatitude();
				Longitude = location.getLongitude();
			}
		};

		// Register the listener with the Location Manager to receive location
		// updates
		locationManager.requestLocationUpdates(
				LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
		loc = Latitude + "~" + Longitude;
		return loc;
	}

	public static String getSimNumber(Activity act) {
		String getSimSerialNumber = "";
		String getSimNumber = "";

		try {
			TelephonyManager telemamanger = (TelephonyManager) act.getSystemService(Context.TELEPHONY_SERVICE);
			getSimSerialNumber = telemamanger.getSimSerialNumber();
			getSimNumber = telemamanger.getLine1Number();

			if (getSimNumber.length() == 0) {
				getSimNumber = "NOTAVAILABLE";
			}

		} catch (Exception e) {
			getSimNumber = "NOTAVAILABLE";

		}
		return getSimSerialNumber="9798987676";//="976693949855";// getSimNumber;
	}
	public static String getYYYYMMDD() {
		java.util.Date today = new java.util.Date();
		String toDate = lPad("" + (today.getYear() + 1900), 2, "0")
				+ lPad("" + (today.getMonth() + 1), 2, "0")
				+ lPad("" + today.getDate(), 2, "0");
		return toDate;
	}
	
	public static String getHHMISS() {
		java.util.Date today = new java.util.Date();
		String toDate = lPad("" + today.getHours(), 2, "0")
				+ lPad("" + (today.getMinutes()), 2, "0")
				+ lPad("" + (today.getSeconds()), 2, "0");
		return toDate;
	}
	
	public static String getValidationData(Activity act,String jObj)
	{
		String hMacValue="",reqHMacString="";
		try
		{
			String secret=act.getString(R.string.lbl_bank_cd)+getYYYYMMDD();
			//Log.e("MBUtil","secret==="+secret);
			//Log.e("MBUtil","jObj==="+jObj+"==");
		    String message = jObj.trim();
		    //JSONObject jobj=new JSONObject(message);
		    //Log.e("MBUtil","jObj==="+jobj+"==");
		    //message=Base64.encode(message.getBytes());
		    //message=message.replaceAll("\/","/");
		    MessageDigest reqRigest = MessageDigest.getInstance("SHA-256");
			byte[] reqEncodedHash = reqRigest.digest(message.getBytes("UTF-8"));
			char[] requestBodyHash = Hex.encodeHex(reqEncodedHash);		
			reqHMacString=new String(requestBodyHash).toUpperCase();
			Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
		    SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes("UTF-8"), "HmacSHA256");
		    sha256_HMAC.init(secret_key);
		    
		    char[] hash = Hex.encodeHex(sha256_HMAC.doFinal(reqHMacString.getBytes("UTF-8")));
		    hMacValue=new String(hash).toUpperCase();
		}
		catch(KeyException ke)
		{
			ke.printStackTrace();
			hMacValue="NA";
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			hMacValue="NA";
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			hMacValue="NA";
		} /*catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	
		return hMacValue;
	}
	public static void showInvalidResponseAlert(final Activity act)
	{
		String str="Invalid Responce";
	ErrorDialogClass alert = new ErrorDialogClass(act, "" + str){
			
			@Override
			public void onClick(View v) {
				super.onClick(v);
				switch (v.getId()) 
				{
					case R.id.btn_ok:
						Intent intent = new Intent(act, SBKLoginActivity.class);					
						act.startActivity(intent);
						act.finish();
					  break;				  
					default:
					  break;
				}
				dismiss();
			
			}
		};
		alert.show();
		
	
	}

	public static void showAlertDialogAndExitApp(String str,final Activity act) {
		ErrorDialogClass alert = new ErrorDialogClass(act, "" + str){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				super.onClick(v);
				switch (v.getId())
				{
					case R.id.btn_ok:
						Intent intent = new Intent(Intent.ACTION_MAIN);
						intent.addCategory(Intent.CATEGORY_HOME);
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						act.startActivity(intent);
						act.finish();
						break;


					default:
						break;
				}
				dismiss();

			}
		};
		alert.show();
       /* AlertDialog alertDialog = new AlertDialog.Builder(act).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        act.startActivity(intent);
                        act.finish();
                    }
                });

        alertDialog.show();*/
	}
	public static void ifGooglePlayServicesValid(final Activity act) {

		flag = chkConnectivity(act);
		if (flag == 0) {
			if (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(act.getApplicationContext())
					== ConnectionResult.SUCCESS) {
				Log.e("REG", "google service available callSafetyNetAttentationApi");
				callSafetyNetAttentationApi(act);

			} else {
				Log.e("REG", "google service not available");
				showAlertDialogAndExitApp(act.getString(R.string.alert_sup),act);
			}
		}
	}
	public static void callSafetyNetAttentationApi(final Activity act){
		Log.e("MAIN", "inside callSafetyNetAttentationApi==");
		SafetyNet.getClient(act).attest(generateNonce(), "AIzaSyCuNlidpGKguwkJd0hZiPZF_oKg4ngXwCI")
				.addOnSuccessListener(act,
						new OnSuccessListener<SafetyNetApi.AttestationResponse>() {
							@Override
							public void onSuccess(SafetyNetApi.AttestationResponse response) {
								String jwsResponse = new DeviceUtils().decodeJws(response.getJwsResult());
								//  Log.e("MAIN","jwsResponse=="+jwsResponse);
								try {
									JSONObject jObj = new JSONObject(jwsResponse);
									if (jObj.getBoolean("ctsProfileMatch") && jObj.getBoolean("basicIntegrity"))
										// Toast.makeText(SplashPage.this,"Not Rooted Device/VM",Toast.LENGTH_LONG).show();
										Log.e("Bhingar", "Not Rooted Device/VM");
									else {
										showAlertDialogAndExitApp(act.getString(R.string.alert_sup),act);
										Log.e("Bhingar", "Rooted Device/VM");
									}
									//Toast.makeText(SplashPage.this,"Rooted Device/VM",Toast.LENGTH_LONG).show();

								} catch (JSONException je) {
									je.printStackTrace();
								}
								// Indicates communication with the service was successful.
								// Use response.getJwsResult() to get the result data.
							}
						})
				.addOnFailureListener(act, new OnFailureListener() {
					@Override
					public void onFailure(@NonNull Exception e) {
						e.printStackTrace();
						showAlertDialogAndExitApp(act.getString(R.string.alert_sup),act);
						// An error occurred while communicating with the service.
                        /*if (e instanceof ApiException) {
                            // An error with the Google Play services API contains some
                            // additional details.
                            ApiException apiException = (ApiException) e;
                            // You can retrieve the status code using the
                            // apiException.getStatusCode() method.
                        } else {
                            // A different, unknown type of error occurred.
                            Log.d(TAG, "Error: " + e.getMessage());
                        }*/
					}
				});
	}
	public static byte[] generateNonce() {
		byte[] nonce = new byte[16];
		new SecureRandom().nextBytes(nonce);
		Log.e("MAIN", " inside generateNonce");
		return nonce;
	}
	public static int chkConnectivity(final Activity act) {
		// pb_wait.setVisibility(ProgressBar.VISIBLE);
		ConnectivityManager cm = (ConnectivityManager) act.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		try {
			NetworkInfo.State state = ni.getState();
			boolean state1 = ni.isAvailable();

			if (state1) {
				switch (state) {
					case CONNECTED:
						if (ni.getType() == ConnectivityManager.TYPE_MOBILE
								|| ni.getType() == ConnectivityManager.TYPE_WIFI) {

						}
						break;
					case DISCONNECTED:
						flag = 1;
						retMess = act.getString(R.string.alert_014);
						showAlert(retMess,act);

						break;
					default:
						flag = 1;
						retMess = act.getString(R.string.alert_000);
						showAlert(retMess,act);
						break;
				}
			} else {
				Log.e("chkConnectivity", "7");
				flag = 1;
				retMess = act.getString(R.string.alert_000);
				showAlert(retMess,act);
			}
		} catch (NullPointerException ne) {

			Log.i("BalanceEnquiry ", "NullPointerException Exception"
					+ ne);
			flag = 1;
			retMess = act.getString(R.string.alert_000);
			showAlert(retMess,act);
		} catch (Exception e) {
			Log.i("BalanceEnquiry   mayuri", "Exception" + e);
			flag = 1;
			retMess = act.getString(R.string.alert_000);
			showAlert(retMess,act);
		}
		return flag;
	}// end chkConnectivity

	public static void showAlert(final String str,final Activity act) {

		Log.e("SAM","===ShowAlert ");
		ErrorDialogClass alert = new ErrorDialogClass(act, "" + str)
		{
			Intent in = null;
			@Override
			public void onClick(View v)
			{
				switch (v.getId())
				{
					case R.id.btn_ok:

						this.dismiss();

						break;
					default:
						break;
				}this.dismiss();

			}
		};alert.show();
	}

	public static float drawMultilineText(Canvas cs, String str, float x_coord, float height, Paint tPaint)
	{
		str=chenextlne(str);
		String []lines=str.split("\n");
		for(int i=0;i<lines.length;i++,height+=20)
		{
			String tempStr=lines[i];
			cs.drawText(tempStr, x_coord, height, tPaint);
		}
		//370
		return height;
	}

	public static String chenextlne(String str)
	{
		StringBuilder sb = new StringBuilder(str);

		int i = 0;
		while ((i = sb.indexOf(" ", i + 35)) != -1) {
			sb.replace(i, i + 1, "\n");
		}

		System.out.println(sb.toString());
		return sb.toString();
	}

}

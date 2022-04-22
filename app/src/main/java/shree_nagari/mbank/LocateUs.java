package shree_nagari.mbank;

import java.security.PrivateKey;
import java.util.ArrayList;

import javax.crypto.spec.SecretKeySpec;

import mbLib.CryptoClass;
import mbLib.MBSUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import shree_nagari.mbank.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class LocateUs extends FragmentActivity implements LocationListener,
		OnClickListener {
	ArrayList<Marker> marker;
	Marker currMarker;
	StringBuffer urlString = new StringBuffer();
	ArrayList<LatLng> locList;
	Button btAddLoc, btCalcArea, btStart;
	ImageButton btn_home, btn_back;
	TextView txt_heading;
	ImageView img_heading;
	EditText txtArea, txtLatitude, txtLongitude;
	ToggleButton toggleMap, toggleDrag;
	private LatLng curlatlng;
	private CameraUpdate center;
	private LocationManager locManager;
	private Marker temp;
	private GoogleMap map;
	ImageView btn_home1,btn_logout;
	static final LatLng HAMBURG = new LatLng(53.558, 74.927);
	static LocateUs obj;
	private static final String MY_SESSION = "my_session";
	private static String NAMESPACE = "";
	private static String URL = "";
	private static String SOAP_ACTION = "";
	private static  String METHOD_NAME_GET_MAP_INFO = "";
	PrivateKey var1=null;	  
	String var5="",var3="";
	SecretKeySpec var2=null;

	String custId = "", retMess = "", retVal = "", currentLocation = "",respcode="",retvalweb="",respdesc="";
	int cnt = 0, flag = 0;

	@SuppressLint("MissingPermission")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			Log.e("LocateUs", "onCreate");
			getWindow().setFlags(LayoutParams.FLAG_SECURE, LayoutParams.FLAG_SECURE);
			setContentView(R.layout.locate_us);
			var1 = (PrivateKey)getIntent().getSerializableExtra("VAR1");
			var3 = (String)getIntent().getSerializableExtra("VAR3");
			txt_heading = (TextView) findViewById(R.id.txt_heading);
			txt_heading.setText(getString(R.string.lbl_locate_us));
			btn_home = (ImageButton) findViewById(R.id.btn_home);
			img_heading=(ImageView)findViewById(R.id.img_heading);
	        img_heading.setBackgroundResource(R.mipmap.locate_us);
			/*
			 * btn_back = (ImageButton) findViewById(R.id.btn_back);
			 * btn_back.setImageResource(R.drawable.backover);
			 * btn_back.setOnClickListener(this);
			 */
	        btn_home1 = (ImageView) findViewById(R.id.btn_home1);
			btn_logout = (ImageView) findViewById(R.id.btn_logout);
			btn_home1.setVisibility(View.INVISIBLE);
			btn_logout.setVisibility(View.INVISIBLE);
			btn_home1.setOnClickListener(null);
			btn_logout.setOnClickListener(null);
			btn_home.setOnClickListener(this);
			obj = this;
			/*Fragment fragment = getSupportFragmentManager().findFragmentById(
					R.id.map);
			SupportMapFragment mapFragment = (SupportMapFragment) fragment;
			map = mapFragment.getMap();*/
			SupportMapFragment supportmapfragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
			supportmapfragment.getMapAsync(new OnMapReadyCallback()
			{
				@Override
				public void onMapReady(GoogleMap googleMap) {
					map=googleMap;
					map.setMyLocationEnabled(true);
				}
			});
			//map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
			// map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

			// map.setMyLocationEnabled(true);

			new CallWebServiceGetBnkBrnMapInfo().execute();
			/*if (map == null) {
				showAlert("Sorry! unable to create maps");
			}*/
			locList = new ArrayList<LatLng>();
			marker = new ArrayList<Marker>();

			map.setOnMapLongClickListener(new OnMapLongClickListener() {

				@Override
				public void onMapLongClick(LatLng point) {

					// TODO Auto-generated method stub
					if (currMarker != null) {
						if (point != currMarker.getPosition()) {
							// currMarker.setPosition(new LatLng(16.8,74.6));
							String str = "http://maps.google.com/maps?saddr="
									+ currMarker.getPosition().latitude + ","
									+ currMarker.getPosition().longitude
									+ "&daddr=" + point.latitude + ","
									+ point.longitude;// +"&key="+getString(R.string.map_key);
							// String
							// str="http://maps.google.com/maps?saddr=16.8,74.6&daddr="+point.latitude+","+point.longitude;//+"&key="+getString(R.string.map_key);
							Uri gmmIntentUri = Uri.parse(str);

							Intent mapIntent = new Intent(Intent.ACTION_VIEW,
									gmmIntentUri);
							mapIntent
									.setPackage("com.google.android.apps.maps");
							startActivity(mapIntent);
						}
					}
				}
			});
			/*
			 * map.setOnMarkerClickListener(new OnMarkerClickListener() {
			 * 
			 * @Override public boolean onMarkerClick(Marker marker) { // TODO
			 * Auto-generated method stub if(currMarker!=null ) {
			 * if(marker.getPosition()!=currMarker.getPosition()) {
			 * //currMarker.setPosition(new LatLng(16.8,74.6)); String
			 * str="http://maps.google.com/maps?saddr="
			 * +currMarker.getPosition().
			 * latitude+","+currMarker.getPosition().longitude
			 * +"&daddr="+marker.getPosition
			 * ().latitude+","+marker.getPosition().
			 * longitude;//+"&key="+getString(R.string.map_key); //String
			 * str="http://maps.google.com/maps?saddr=16.8,74.6&daddr="
			 * +marker.getPosition
			 * ().latitude+","+marker.getPosition().longitude;
			 * //+"&key="+getString(R.string.map_key); Uri gmmIntentUri =
			 * Uri.parse(str);
			 * 
			 * Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
			 * mapIntent.setPackage("com.google.android.apps.maps");
			 * startActivity(mapIntent); } } return false; } });
			 */
			/*
			 * map.setOnMarkerDragListener(new OnMarkerDragListener() {
			 * 
			 * @Override public void onMarkerDragStart(Marker marker) { // TODO
			 * Auto-generated method stub }
			 * 
			 * @Override public void onMarkerDragEnd(Marker marker) { // TODO
			 * Auto-generated method stub }
			 * 
			 * @Override public void onMarkerDrag(Marker marker) { // TODO
			 * Auto-generated method stub String tempLat = "" +
			 * currMarker.getPosition().latitude; String tempLong = "" +
			 * currMarker.getPosition().longitude; //
			 * txtLatitude.setText(tempLat.substring(0,tempLat.indexOf(".")+3));
			 * //
			 * txtLongitude.setText(tempLong.substring(0,tempLong.indexOf(".")
			 * +3)); } });
			 */
			locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
					0, (LocationListener) this);
		} catch (Exception e) {
			//Toast.makeText(this, "" + e, Toast.LENGTH_SHORT).show();
			Log.e("Exception",""+e);
		}
	}// end onCreate

	public void onMapReady(GoogleMap googleMap) {
		map = googleMap;

		// Add a marker in Sydney, Australia, and move the camera.
		Log.e("DEBUG", "OMGOMG");
		LatLng sydney = new LatLng(-34, 151);
		map.addMarker(new MarkerOptions().position(sydney).title(
				"Marker in Sydney"));
		map.moveCamera(CameraUpdateFactory.newLatLng(sydney));
	}

	@Override
	public void onResume() {
		super.onResume();

		/*
		 * int status =
		 * GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext
		 * ());
		 * 
		 * if(status!=ConnectionResult.SUCCESS) { int requestCode = 10; Dialog
		 * dialog = GooglePlayServicesUtil.getErrorDialog(status, this,
		 * requestCode); dialog.show(); } else
		 */
		{
			// 1: call webservice for getting address for merchant
			// 2: show all merchant on google map
			// 3: get current location of device .
			// 4: while onClick of any of merchant then show path from current
			// device location and clicked merchant.

			// this is step 2:
			// progress.setMessage("Merchants are loading");
			// progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			// progress.setIndeterminate(true);

			// map.setOnMapClickListener(this);

			// System.out.println("map:"+map);
			// System.out.println("fragment:"+fragment);
			// System.out.println("mapFragment:"+mapFragment);
			// kop = map.addMarker(new
			// MarkerOptions().position(Kolhapur).title("Agent-Kolhapur Station"));
			// map.moveCamera(CameraUpdateFactory.newLatLngZoom(Kolhapur, 15));
			// shiroli_ = map.addMarker(new
			// MarkerOptions().position(shiroli).title("Agent-Shiroli MIDC"));
			// map.moveCamera(CameraUpdateFactory.newLatLngZoom(shiroli,8));
			// locationFinder=new FindCurrentLocation(MapActivity.this);
			// locationFinder.setMap(map);
			// locationFinder.getCurrentLocation();
			// map.animateCamera(CameraUpdateFactory.zoomTo(10.0f));
			// kop = map.addMarker(new
			// MarkerOptions().position().title("Agent-Kolhapur Station"));
			// String area=locationFinder.getCurrentArea();
			/*
			 * Log.e("==== area ====",area); Log.e("==== area ====",area);
			 * Log.e("==== area ====",area);
			 */
			/*
			 * if(area!=null &&
			 * !(area.equalsIgnoreCase("Failed to collect area information"))) {
			 * progress.dismiss(); }
			 */
		}

	}// end onResume

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		if (arg0.getId() == R.id.btn_home) {
			//locManager.removeUpdates(this);
			Intent in = new Intent(this, SBKLoginActivity.class);
			in.putExtra("VAR1", var1);
			in.putExtra("VAR3", var3);
			startActivity(in);
			finish();
		}
		/*
		 * if(arg0.getId()==R.id.btAddLoc) {
		 * locList.add(currMarker.getPosition()); temp=map.addMarker(new
		 * MarkerOptions
		 * ().position(currMarker.getPosition()).title("Location "+marker
		 * .size()+1)); marker.add(temp);
		 * marker.get(marker.size()-1).setPosition(currMarker.getPosition());
		 * for(int i=0;i<locList.size()-1;i++) { Polyline
		 * line=map.addPolyline(new PolylineOptions()
		 * .add(locList.get(i),locList.get(i+1)) .width(5)
		 * .color(Color.parseColor("#FF0000"))); }
		 * currMarker.setPosition(curlatlng);
		 * center=CameraUpdateFactory.newLatLng(curlatlng);
		 * map.moveCamera(center); toggleDrag.setChecked(false);
		 * Toast.makeText(this, "Size Of Marker List is "+marker.size(),
		 * Toast.LENGTH_LONG).show(); } else if(arg0.getId()==R.id.btStart) {
		 * locManager = (LocationManager)
		 * getSystemService(Context.LOCATION_SERVICE);
		 * locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0, 0,
		 * (LocationListener) this); } else if(arg0.getId()==R.id.btCalcArea) {
		 * String tempArea=""+CalculatePolygonArea(locList);
		 * tempArea=tempArea.substring(0,tempArea.indexOf(".")+3);
		 * tempArea=""+(Float.parseFloat(tempArea)*3.29)*1000;
		 * tempArea=tempArea.substring(0,tempArea.indexOf(".")+3);
		 * txtArea.setText(tempArea); currMarker.setDraggable(false); Polygon
		 * polygon=map.addPolygon(new
		 * PolygonOptions().addAll(locList).fillColor(
		 * Color.parseColor("#00FF00"))); locManager = (LocationManager)
		 * getSystemService(Context.LOCATION_SERVICE); } else
		 * if(arg0.getId()==R.id.toglMap) { Toast.makeText(this,
		 * "toggleMap.isChecked()="+toggleMap.isChecked(),
		 * Toast.LENGTH_LONG).show(); if(toggleMap.isChecked()) {
		 * map.setMapType(GoogleMap.MAP_TYPE_SATELLITE); } else {
		 * map.setMapType(GoogleMap.MAP_TYPE_TERRAIN); } } else
		 * if(arg0.getId()==R.id.toglDrag) { Toast.makeText(this,
		 * "toggleDrag.isChecked()="+toggleDrag.isChecked(),
		 * Toast.LENGTH_LONG).show(); if(toggleDrag.isChecked()) {
		 * locManager.removeUpdates(this); currMarker.setDraggable(true); } else
		 * { locManager = (LocationManager)
		 * getSystemService(Context.LOCATION_SERVICE);
		 * locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0, 0,
		 * (LocationListener) this); currMarker.setDraggable(false); } }
		 */
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		curlatlng = new LatLng(location.getLatitude(), location.getLongitude());
		// Toast.makeText(this, "Set to currenr location" + currMarker,
		// Toast.LENGTH_SHORT).show();

		if (currMarker == null) {
			// Toast.makeText(this, "In null If", Toast.LENGTH_SHORT).show();
			currMarker = map.addMarker(new MarkerOptions()
					.position(curlatlng)
					.title("Current Location")
					.icon(BitmapDescriptorFactory
							.fromResource(R.drawable.current_location)));
		} else {
			currMarker.setPosition(curlatlng);
		}

	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub

	}

	public void onBackPressed() {
		//locManager.removeUpdates(this);
		Intent in = new Intent(this, LoginActivity.class);
		in.putExtra("VAR1", var1);
		in.putExtra("VAR3", var3);
		startActivity(in);
		finish();

	}

	public void showAlert(final String str) {
		// Toast.makeText(this, str, Toast.LENGTH_LONG).show();
		ErrorDialogClass alert = new ErrorDialogClass(this, "" + str)
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
							post_success(retvalweb);
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
	}

	public int chkConnectivity() {
		// pb_wait.setVisibility(ProgressBar.VISIBLE);
		System.out
				.println("========================= end chkConnectivity ==================");
		ConnectivityManager cm = (ConnectivityManager) this
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		try {
			State state = ni.getState();
			boolean state1 = ni.isAvailable();
			System.out
					.println("BalanceEnquiry	in chkConnectivity () state1 ---------"
							+ state1);
			if (state1) {
				switch (state) {
				case CONNECTED:
					if (ni.getType() == ConnectivityManager.TYPE_MOBILE
							|| ni.getType() == ConnectivityManager.TYPE_WIFI) {

					}
					break;
				case DISCONNECTED:
					flag = 1;
					retMess = getString(R.string.alert_014);
					showAlert(retMess);

					break;
				default:
					flag = 1;
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

			Log.i("BalanceEnquiry", "NullPointerException Exception"
					+ ne);
			flag = 1;
			retMess = getString(R.string.alert_000);
			showAlert(retMess);
		} catch (Exception e) {
			Log.i("BalanceEnquiry   mayuri", "Exception" + e);
			flag = 1;
			retMess = getString(R.string.alert_000);
			showAlert(retMess);
		}
		System.out
				.println("========================= end chkConnectivity ==================");
		return flag;
	}// end chkConnectivity

	class CallWebServiceGetBnkBrnMapInfo extends AsyncTask<Void, Void, Void> {
		LoadProgressBar loadProBarObj = new LoadProgressBar(LocateUs.this);

		JSONObject jsonObj = new JSONObject();
		String ValidationData="";

		protected void onPreExecute() {
			try {
				respcode="";
				retvalweb="";
				respdesc="";
				// p_wait.setVisibility(ProgressBar.VISIBLE);
				loadProBarObj.show();
				jsonObj.put("CUSTID", custId);
				 jsonObj.put("SIMNO", MBSUtils.getSimNumber(LocateUs.this));
				 jsonObj.put("METHODCODE","44");
			//	 ValidationData=MBSUtils.getValidationData(LocateUs.this,jsonObj.toString());
				
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
				e.printStackTrace();
				System.out.println("Exception 2");

			}
			return null;
		}// end doInBackground

		protected void onPostExecute(Void paramVoid) {
			
			loadProBarObj.dismiss();
			 JSONObject jsonObj;
				try
				{
					String str=CryptoClass.Function6(var5,var2);
					 jsonObj = new JSONObject(str.trim());
					 
					/*ValidationData=xml_data[1].trim();
						if(ValidationData.equals(MBSUtils.getValidationData(LocateUs.this, xml_data[0].trim())))
					{*/
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
						retvalweb = jsonObj.getString("RETVAL");
					}
					else
					{
						retvalweb = "";
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
			if (retvalweb.indexOf("FAILED") > -1) {
				showAlert(getString(R.string.alert_134));
			} else {
				post_success(retvalweb);
			}}
					/*}
					else{
						MBSUtils.showInvalidResponseAlert(LocateUs.this);	
					}*/
				} catch (JSONException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
				
		}// end onPostExecute

	}// end CallWebServiceGetBnkBrnMapInfo
	public 	void post_success(String retvalweb)
	{
		respcode="";
		respdesc="";
		double maxLat = 0.0, maxLong = 0.0, minLat = 0.0, minLong = 0.0;
		try {
			Log.e("onPostExecute", retvalweb);
			JSONArray jsArr = new JSONArray(retvalweb);
			int i = 0;
			for (; i < jsArr.length(); i++) {
				JSONObject json_data = jsArr.getJSONObject(i);
				String desc = json_data.getString("nm");
				String lat = json_data.getString("lat");
				String lng = json_data.getString("lng");
				double tempLat = Double.parseDouble(lat);
				double tempLong = Double.parseDouble(lng);
					maxLat = maxLat + tempLat;
				maxLong = maxLong + tempLong;
				Log.e("OMG" + i, desc + "=" + lat + "=" + lng);

				curlatlng = new LatLng(new Double(lat), new Double(lng));

				temp = map.addMarker(new MarkerOptions().position(
						curlatlng).title(desc));
				

				temp.showInfoWindow();
				marker.add(temp);
				// Log.e("THANK","GOD");
			}
			if (i > 0) {
				center = CameraUpdateFactory
						.newLatLng(new LatLng(new Double(maxLat / i),
								new Double(maxLong / i)));
				map.moveCamera(center);
				map.animateCamera(CameraUpdateFactory.zoomTo(10.0f));
			}
		} catch (Exception ex) {
			Log.e("OMG: Error", ex.toString());
			ex.printStackTrace();
		}

	
	
		
	}
	/*@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		locManager.removeUpdates(this);
		finish();
		// onDestroy();

	}*/
	/*
	 * @Override protected void onDestroy() { // TODO Auto-generated method stub
	 * 
	 * //super.onDestroy(); locManager.removeUpdates(this); }
	 */
}

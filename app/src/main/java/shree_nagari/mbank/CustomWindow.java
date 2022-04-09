package shree_nagari.mbank;


import shree_nagari.mbank.R;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

public class CustomWindow extends Activity {
	

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		/*
		 * requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		 * 
		 * setContentView(R.layout.login_x); //
		 * setContentView(R.layout.balance_enq);
		 * getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
		 * R.layout.window_title);
		 * 
		 * // title = (TextView) findViewById(R.id.title); // icon = (ImageView)
		 * // findViewById(R.id.icon);
		 */
		final Window window = getWindow();
		boolean useTitleFeature = false;
		Log.i("%%%%%%%%", "window.getContainer()="+window.getContainer());
		/*if (window.getContainer() == null) {
			useTitleFeature = window.requestFeature(Window.FEATURE_CUSTOM_TITLE);
		}
		*/
		Log.i("@@@@@@@@@@@@", "useTitleFeature="+useTitleFeature);
		
		setContentView(R.layout.login);
		
		/*if (useTitleFeature) {
			window.setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.window_title);
	
		}*/
		
	}
}
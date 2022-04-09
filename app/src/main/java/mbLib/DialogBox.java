package mbLib;

import shree_nagari.mbank.R;
import android.app.Activity;
import android.app.AlertDialog;

public class DialogBox 
{

	AlertDialog.Builder adb;
	Activity activity;
	String msg, title;

	public DialogBox(final Activity activity) {
		this.activity = activity;
		adb = new AlertDialog.Builder(activity);
		adb.setTitle(activity.getString(R.string.app_name));
		adb.setMessage("Are You Sure To Exit?");
		adb.create();
	}
	
	public AlertDialog.Builder get_adb()
	{
		return adb;
	}
}

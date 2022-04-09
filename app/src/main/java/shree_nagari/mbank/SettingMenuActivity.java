package shree_nagari.mbank;



import shree_nagari.mbank.R;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Typeface;
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
import android.widget.ListView;

public class SettingMenuActivity extends Fragment implements OnItemClickListener,OnClickListener{
	MainActivity act;
	SettingMenuActivity settingMenu;
	ListView lst_dpt;
	ArrayAdapter<MenuIcon> aa;
	
	String lstopt[] = { "Change MPIN", "Change Mobile No"};
	private ListView listView1;
	
	Button but_exit,but_back;
	
	
public SettingMenuActivity(){}
	
	@SuppressLint("ValidFragment")
	public SettingMenuActivity(MainActivity a)
	{
		System.out.println("SettingMenuActivity()"+a);
		act = a;
		settingMenu=this;
	}

	public void onBackPressed() {
		return ;
	}
	 public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState) {		
			System.out.println("onCreateView()  SettingMenuActivity");
			
	        View rootView = inflater.inflate(R.layout.setting_submenu, container, false);
	       
			Typeface tf_calibri = Typeface.createFromAsset(act.getAssets(),
			        "fonts/Kozuka-Gothic-Pro-M_26793.ttf");
			
			MenuIcon menuItem[] = new MenuIcon[]
	      	{
					new MenuIcon(getString(R.string.lbl_change_mpin),R.mipmap.arrow),
	      	};
					
					 MenuAdaptor adapter = new MenuAdaptor(act, R.layout.listview_item_row, menuItem);
					 
					 listView1 = (ListView)rootView.findViewById(R.id.listView1);
					 View header = (View)act.getLayoutInflater().inflate(R.layout.setting_listview_header_row, null);
				     listView1.addHeaderView(header);
				        
				     listView1.setAdapter(adapter);
				      
					 listView1.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
				     listView1.setOnItemClickListener(this);
				     
			
	        return rootView;
	    }
		
	@Override
	public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
		// TODO Auto-generated method stub
		Log.i("Testing","Inside itemclicklistener");
		//int pos=lst_dpt.getCheckedItemPosition();
		int pos=listView1.getCheckedItemPosition();
		Intent in =null;
		Bundle b = new Bundle();
		switch(pos)
		{
			case 1:
				//in =new Intent(getApplicationContext(), ChkBookRequest.class);
				Log.i("MBS Case -1", "11");
				Fragment changMpinFragment = new ChangeMpinActivity(act);
				
				FragmentManager fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction()
						.replace(R.id.frame_container, changMpinFragment).commit();				
				/*in =new Intent(getApplicationContext(), ChangeMpinActivity.class);
				Log.i("MBS Case -1", "22");
				in.putExtra("sel_setting_tab", "0");
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
				}*/
				break;
				
			/*case 2:
				//in =new Intent(getApplicationContext(), ChkBookRequest.class);
				Log.i("MBS Case -2", "11");
							
				in =new Intent(getApplicationContext(), ChangeMobNoActivity.class);
				Log.i("MBS Case -2", "22");
				in.putExtra("sel_setting_tab", "1");
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
					Log.i("MBS Appli Error", "Can't find bundle...2");
				}
				finally
				{
					Log.i("MBS Case -2", "DONE");
					startActivity(in);
					finish(); 
				}
				break;*/
			
		}
		
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId())
		{
		/*case R.id.but_back:
			Intent in = new Intent(this, LoginActivity.class);
			startActivity(in);
			finish();
			break;*/
	/*	case R.id.but_exit:
			dbs = new DialogBox(this);
			dbs.get_adb().setMessage(getString(R.string.lbl_exit));
			dbs.get_adb().setPositiveButton("Yes",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(
								DialogInterface arg0, int arg1) {
							// TODO Auto-generated method stub
							finish();
						}
					});
			dbs.get_adb().setNegativeButton("No",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(
								DialogInterface arg0, int arg1) {
							// TODO Auto-generated method stub
							arg0.cancel();
						}
					});
			dbs.get_adb().show();*/
		
		}
	}
}
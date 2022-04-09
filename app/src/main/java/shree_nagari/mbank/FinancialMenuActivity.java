package shree_nagari.mbank;


//import mbLib.DialogBox;
import shree_nagari.mbank.R;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
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
//import android.annotation.SuppressLint;

//@SuppressLint("NewApi")
public class FinancialMenuActivity extends Fragment implements OnItemClickListener,OnClickListener{
	MainActivity act;
	FinancialMenuActivity finMenu;
	ListView lst_dpt;
	ArrayAdapter<MenuIcon> aa;
	
	String lstopt[] = { "Manage Beneficiary", "Transfer Funds"};
	private ListView listView1;
	
	Button but_exit;//,but_back;
	//DialogBox dbs;
	/*Typeface tf_calibri = Typeface.createFromAsset(act.getAssets(),
	        "fonts/Kozuka-Gothic-Pro-M_26793.ttf");*/
	public FinancialMenuActivity(){}
	
	@SuppressLint("ValidFragment")
	public FinancialMenuActivity(MainActivity a)
	{
		System.out.println("FinancialMenuActivity()"+a);
		act = a;
		finMenu=this;
	}
	
	public void onBackPressed() {
		return ;
	}

	 public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState) {		
			System.out.println("FinancialMenuActivity onCreateView()");
			
	        View rootView = inflater.inflate(R.layout.finance_submenu, container, false);
	       
			
			
			MenuIcon menuItem[] = new MenuIcon[]
	      	{
					new MenuIcon(act.getString(R.string.lbl_manage_beneficiary),R.mipmap.arrow),
	      			new MenuIcon(act.getString(R.string.lbl_transfer_funds),R.mipmap.arrow),
	      			
	      	};
					
					 MenuAdaptor adapter = new MenuAdaptor(act, R.layout.listview_item_row, menuItem);
					 
					 listView1 = (ListView)rootView.findViewById(R.id.listView1);
					 View header = (View)act.getLayoutInflater().inflate(R.layout.financialmenu_listview_header_row, null);
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
		int pos=listView1.getCheckedItemPosition();
		Intent in =null;
		Bundle b = new Bundle();
		Log.i("Testing","pos :"+pos);
		Fragment fragment;
		FragmentManager fragmentManager;
		
		switch(pos)
		{
			case 1:
				//"Manage Beneficiary"
				Log.i("MBS Case -1", "11	Manage Beneficiary");	
				
				fragment = new ManageBeneficiaryMenuActivity(act);				
				act.setTitle(getString(R.string.lbl_manage_beneficiary));
				fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction()
						.replace(R.id.frame_container, fragment).commit();		
				
				/*in =new Intent(getApplicationContext(), ManageBeneficiaryMenuActivity.class);
				Log.i("MBS Case -1", "22");
				in.putExtra("sel_finance_tab", "0");
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
			case 2:
				//"Transfer Funds"
				Log.i("MBS Case -2", "11	Transfer Funds");
				fragment = new FundTransferMenuActivity(act);				
				act.setTitle(getString(R.string.lbl_transfer_funds));
				fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction()
						.replace(R.id.frame_container, fragment).commit();		
				
				/*in =new Intent(getApplicationContext(), FundTransferMenuActivity.class);
				Log.i("MBS Case -2", "22");
				in.putExtra("sel_finance_tab", "1");
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
				}*/
				break;
			
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
			/*case R.id.but_exit:
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
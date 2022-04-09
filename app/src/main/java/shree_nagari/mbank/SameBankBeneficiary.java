package shree_nagari.mbank;


import shree_nagari.mbank.R;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class SameBankBeneficiary extends Fragment implements OnClickListener{
	MainActivity act;
	SameBankBeneficiary sameBankBenf;
	Button btnAddNew;
	Button btnEdit;
	/*Typeface tf_calibri = Typeface.createFromAsset(act.getAssets(),
	        "fonts/Kozuka-Gothic-Pro-M_26793.ttf");*/
	public SameBankBeneficiary(){}
	
	@SuppressLint("ValidFragment")
	public SameBankBeneficiary(MainActivity a)
	{
		//System.out.println("SameBankBeneficiary()"+a);
		act = a;
		sameBankBenf=this;
	}

	 public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState) {		
			//System.out.println("onCreateView() SameBankBeneficiary");
			
	        View rootView = inflater.inflate(R.layout.same_bank_beneficiary, container, false);
	       
			
			
			btnAddNew = (Button) rootView.findViewById(R.id.btnAddNew);
			btnEdit = (Button) rootView.findViewById(R.id.btnEdit);
			
			btnAddNew.setOnClickListener(this);
			btnEdit.setOnClickListener(this);
			
			//btnAddNew.setTypeface(tf_calibri);		
			//btnEdit.setTypeface(tf_calibri);			
			
			
			/*Button btn_back = (Button) rootView.findViewById(R.id.btn_back);        
	        // Listening to back button click
	        btn_back.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View view) {
					// Launching News Feed Screen
					Intent i = new Intent(getApplicationContext(), ManageBeneficiaryMenuActivity.class);
					startActivity(i);finish();
				}
			});*/
				     
			
	        return rootView;
	    }
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Fragment fragment;
		FragmentManager fragmentManager;
		
		switch (v.getId()) {
		case R.id.btnAddNew:
			//AddSameBankBeneficiary
			Intent in1 =null;
			//Log.i("case 1","Add Same Bank Beneficiary");
			//Log.i("MBS Case -1", "11");	
			fragment = new AddSameBankBeneficiary(act);				
			act.setTitle(getString(R.string.frmtitle_add_same_bnk_bnf));
			fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.frame_container, fragment).commit();	
			/*in1 =new Intent(getApplicationContext(), AddSameBankBeneficiary.class);
			Log.i("MBS Case -1", "22");
			in1.putExtra("sel_manage_beneficiary_tab", "0");
			in1.putExtra("sel_add_or_edit", "add");
			
			in1.putExtra("sel_add_or_edit_other", "menu");
			Log.i("MBS Case -1", "33");
						
			try
			{
				Log.i("MBS Case -1", "44");
				Bundle bnd1=this.getIntent().getExtras();
				Log.i("MBS Case -1", "55");
				in1.putExtras(bnd1);
				Log.i("MBS Case -1", "66");
				
				
			}
			catch(NullPointerException npe)
			{
				Log.i("MBS Appli Error", "Can't find bundle...1");
			}
			finally
			{
				Log.i("MBS Case -1", "DONE");
				startActivity(in1);
				finish(); 
			}*/
			break;

		case R.id.btnEdit:
			//EditSameBankBeneficiary
			Intent in2 =null;
			//Log.i("case 1","Edit Same Bank Beneficiary");
			//Log.i("MBS Case -2", "11");	
			fragment = new EditSameBankBeneficiary(act);		
			act.setTitle(getString(R.string.frmtitle_edit_same_bnk_bnf));
			fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.frame_container, fragment).commit();	
			/*in2 =new Intent(getApplicationContext(), EditSameBankBeneficiary.class);
			Log.i("MBS Case -2", "22");
			in2.putExtra("sel_manage_beneficiary_tab", "0");
			in2.putExtra("sel_add_or_edit", "edit");
			in2.putExtra("sel_add_or_edit_other", "menu");
			Log.i("MBS Case -2", "33");
						
			try
			{
				Log.i("MBS Case -2", "44");
				Bundle bnd1=this.getIntent().getExtras();
				Log.i("MBS Case -2", "55");
				in2.putExtras(bnd1);
				Log.i("MBS Case -2", "66");
			}
			catch(NullPointerException npe)
			{
				Log.i("MBS Appli Error", "Can't find bundle...1");
			}
			finally
			{
				Log.i("MBS Case -2", "DONE");
				startActivity(in2);
				finish(); 
			}*/
			break;
		default:
			break;
		}
	}
	
}

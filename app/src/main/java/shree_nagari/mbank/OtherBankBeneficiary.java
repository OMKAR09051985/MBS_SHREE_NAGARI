package shree_nagari.mbank;

//import android.annotation.SuppressLint;

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
import android.widget.Button;

//@SuppressLint("NewApi")
public class OtherBankBeneficiary extends Fragment implements OnClickListener{
	MainActivity act;
	OtherBankBeneficiary othrBankBenf;
	Button btnAddNew;
	Button btnEdit;
	/*Typeface tf_calibri = Typeface.createFromAsset(act.getAssets(),
	        "fonts/Kozuka-Gothic-Pro-M_26793.ttf");*/
	public OtherBankBeneficiary(){}
	
	@SuppressLint("ValidFragment")
	public OtherBankBeneficiary(MainActivity a)
	{
		System.out.println("OtherBankBeneficiary()"+a);
		act = a;
		othrBankBenf=this;
	}

	 public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState) {		
			System.out.println("onCreateView() OtherBankBeneficiary");
			
	        View rootView = inflater.inflate(R.layout.other_bank_beneficiary, container, false);
	       
			
			
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
			Log.i("case 1","Add Same Bank Beneficiary");
			Log.i("MBS Case -1", "11");	
			fragment = new AddOtherBankBeneficiary(act);
			act.setTitle(getString(R.string.frmtitle_add_other_bnk_bnf));
			fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.frame_container, fragment).commit();	
			/*in1 =new Intent(getApplicationContext(), AddOtherBankBeneficiary.class);
			Log.i("MBS Case -1", "22");
			in1.putExtra("sel_manage_beneficiary_tab", "1");
			in1.putExtra("sel_add_or_edit", "menu");
			in1.putExtra("sel_add_or_edit_other", "add");
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
			Log.i("case 1","Edit Same Bank Beneficiary");
			Log.i("MBS Case -2", "11");	
			fragment = new EditOtherBankBeneficiary(act);				
			act.setTitle(getString(R.string.frmtitle_edit_other_bnk_bnf));
			fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.frame_container, fragment).commit();
			/*in2 =new Intent(getApplicationContext(), EditOtherBankBeneficiary.class);
			Log.i("MBS Case -2", "22");
			in2.putExtra("sel_manage_beneficiary_tab", "1");
			in2.putExtra("sel_add_or_edit", "menu");
			in2.putExtra("sel_add_or_edit_other", "edit");
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

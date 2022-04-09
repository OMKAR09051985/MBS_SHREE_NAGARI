package shree_nagari.mbank;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mbLib.MBSUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import shree_nagari.mbank.R;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class TransferHistoryRpt extends Fragment implements OnClickListener
{
	MainActivity act;
	TransferHistoryRpt transferHistoryRptObj;
	ImageButton btn_home;
	
	String retMess,accNo;
	ListView listView1 ;
	List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
	TextView txt_heading,txt_acc_no;
	ImageView img_heading;
	
	public TransferHistoryRpt(){}
	@SuppressLint("ValidFragment")
	public TransferHistoryRpt(MainActivity a, String transactions, String str)
	{
		//Log.e("TransferHistoryRpt"," in constructor");
		act = a;
		transferHistoryRptObj=this;		
		retMess = transactions;
		accNo=str;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) 
	{
		View rootView = inflater.inflate(R.layout.transfer_history_rpt, container, false);
		txt_heading=(TextView)rootView.findViewById(R.id.txt_heading);
		txt_acc_no=(TextView)rootView.findViewById(R.id.txt_acc_no);
		img_heading = (ImageView) rootView.findViewById(R.id.img_heading);
		img_heading.setBackgroundResource(R.mipmap.fund_trnsfer);
		//back = (ImageButton) rootView.findViewById(R.id.btn_back);
		btn_home=(ImageButton)rootView.findViewById(R.id.btn_home);
		//btn_home.setImageResource(R.drawable.ic_home_d);
		//back.setImageResource(R.drawable.backover);
		btn_home.setOnClickListener(this);
		//back.setOnClickListener(this);
		listView1 = (ListView) rootView.findViewById(R.id.listView1);
		//Log.e("TransferHistoryRpt"," onCreateView");
		setValues();
		return rootView;
	}
	@Override
	public void onClick(View v) 
	{
		switch (v.getId()) 
		{
			/*case R.id.btn_back:
				Fragment fragment = new TransferHistory(act);				
				FragmentManager fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction()
						.replace(R.id.frame_container, fragment).commit();
				act.frgIndex=54;
				break;*/
			case R.id.btn_home:
				Intent in=new Intent(act,NewDashboard.class);
				startActivity(in);
				act.finish();
				break;
			default:
				break;
		}
	}
	
	public void setValues() 
	{
		
		
		
		txt_heading.setText(getString(R.string.lbl_transfer_history));
		//img_heading.setBackgroundResource(R.drawable.mini_statement);
		//HashMap<String, String> map = new HashMap<String, String>();
		String[] from = new String[] {"rowid", "col_0", "col_1", "col_2","col_3","col_6"};
		int[] to = new int[] { R.id.item1, R.id.item2, R.id.item3, R.id.item4,R.id.item5,R.id.item6};
		int count=0;
		try
		{
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("col_0","Date");
			map.put("col_1","Nick Name");
			map.put("col_2","Amount(Rs.)");
			map.put("col_3","Status");
			map.put("col_6","TYPE");
			//fillMaps.add(map);
			
			//JSONObject mainObj= new JSONObject(retMess);
			JSONArray ja=new JSONArray(retMess);
			for(int j=0;j<ja.length();j++)
			{
				JSONObject jObj=ja.getJSONObject(j);
				map = new HashMap<String, String>();
				//Log.e("TransferHistoryRpt","benf nm=="+jObj.getString("BNM_NICKNAME"));
				//jObj.getString("MFT_REQID"));
				map.put("col_0",jObj.getString("DATE"));
				map.put("col_2",jObj.getString("NICKNAME"));
				map.put("col_6",""+MBSUtils.amountFormat(jObj.getString("AMOUNT"),false,act));
				/*if(jObj.getString("STATUS").equalsIgnoreCase("TRUE"))
					map.put("col_3","Success");
				else
					map.put("col_3","Failed");*/
				map.put("col_1",jObj.getString("STATUS"));
				/*if(jObj.getString("TYPE").equalsIgnoreCase("INTBANK"))
					map.put("col_6","SAMEBNK");
				else
					map.put("col_6",jObj.getString("TYPE"));*/
				
				fillMaps.add(map);
				
				count++;
			}
			SimpleAdapter adapter = new SimpleAdapter(act, fillMaps, R.layout.transfer_history_list, from, to);
			listView1.setAdapter(adapter);
			txt_acc_no.setText(accNo);
		}
		catch(Exception ex)
		{
			Log.e("Error",ex.toString());
		}
		
	}
}

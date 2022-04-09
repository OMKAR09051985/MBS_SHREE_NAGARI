package mbLib;

import java.util.ArrayList;

import shree_nagari.mbank.*;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CustomAdapterForSchemeInt extends ArrayAdapter<SchemeIntBean>{

	 private ArrayList<SchemeIntBean> schembean;
	    Activity context;
	public CustomAdapterForSchemeInt(Activity context, ArrayList<SchemeIntBean> schembean) {
		super(context, R.layout.custlistschemeint,schembean);
		 this.context = context;
		 this.schembean=schembean;
		// TODO Auto-generated constructor stub
	}
	@SuppressLint("ViewHolder") @Override
    public View getView(int position, View view, ViewGroup parent)
    {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.custlistschemeint ,null, true);

        
        try
        {
        	TextView loantype = (TextView) rowView.findViewById(R.id.loantypeid);
            TextView rate = (TextView) rowView.findViewById(R.id.rateid);
            
           
     
        	
            loantype.setText(schembean.get(position).getLoantype());
            rate.setText(schembean.get(position).getRate()+"%");
        	
          
          
        	
        	//this.notify();
        }
        catch(Exception e) 
        {
            e.printStackTrace();
            Log.e("Jayesh ----", e+"");
        }
        return rowView;
    }
}

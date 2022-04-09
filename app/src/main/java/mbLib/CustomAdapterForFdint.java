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

public class CustomAdapterForFdint extends ArrayAdapter<FdIntrestBean>{

	 private ArrayList<FdIntrestBean> fdintbean;
	    Activity context;
	public CustomAdapterForFdint(Activity context, ArrayList<FdIntrestBean> fdintbean) {
		super(context, R.layout.custlisforfdint,fdintbean);
		 this.context = context;
		 this.fdintbean=fdintbean;
		// TODO Auto-generated constructor stub
	}
	@SuppressLint("ViewHolder") @Override
    public View getView(int position, View view, ViewGroup parent)
    {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.custlisforfdint ,null, true);

        
        try
        {
        	TextView maturity = (TextView) rowView.findViewById(R.id.maturityperiod);
            TextView regular = (TextView) rowView.findViewById(R.id.regular);
            TextView dates = (TextView) rowView.findViewById(R.id.dates);

            maturity.setText(fdintbean.get(position).getMaturityPeriod1());
            regular.setText(fdintbean.get(position).getRegular()+" %");
            dates.setText(fdintbean.get(position).getDates());
            /*if(Integer.parseInt(fdintbean.get(position).getMaturityPeriod1())>=365 && fdintbean.get(position).getMaturityPeriod2().equals("Onwards"))
        	{
        		maturity.setText(Integer.parseInt(fdintbean.get(position).getMaturityPeriod1())/365+"  to "+  "Onwards");
                regular.setText(fdintbean.get(position).getRegular()+" %");
        	}
            else if(fdintbean.get(position).getMaturityPeriod1().equals("1")){
            maturity.setText("upto "+fdintbean.get(position).getMaturityPeriod2()+" Days");
            regular.setText(fdintbean.get(position).getRegular()+" %");
        	}
        	else if(Integer.parseInt(fdintbean.get(position).getMaturityPeriod1())<365 ||Integer.parseInt(fdintbean.get(position).getMaturityPeriod2())<365)
        	{
        		maturity.setText(fdintbean.get(position).getMaturityPeriod1()+"  to "+  fdintbean.get(position).getMaturityPeriod2()+" Days");
                regular.setText(fdintbean.get(position).getRegular()+" %");
        	}
        	else if(Integer.parseInt(fdintbean.get(position).getMaturityPeriod1())>=365 ||Integer.parseInt(fdintbean.get(position).getMaturityPeriod2())>=365 ) {
        		maturity.setText(Integer.parseInt(fdintbean.get(position).getMaturityPeriod1())/365+"  to "+ Integer.parseInt( fdintbean.get(position).getMaturityPeriod2())/365+" Year");
                regular.setText(fdintbean.get(position).getRegular()+" %");
        	}*/
        	 
          
          
        	
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

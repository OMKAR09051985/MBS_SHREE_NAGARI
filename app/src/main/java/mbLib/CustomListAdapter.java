package mbLib;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;
//import shree_nagari.mbank.R;

public class CustomListAdapter extends SimpleAdapter{
	
	
	Context c;
	public CustomListAdapter(Context context, List<HashMap<String, String>> items,
	            int resource, String[] from, int[] to) {
	        super(context, items, resource, from, to);
	        c=context;
	    }

	    @Override
	    public View getView(int position, View convertView, ViewGroup parent) {
	        View view = super.getView(position, convertView, parent);

	       // Typeface typeFace = setTypeface(FontCache.get("fonts/futura-book-bt.ttf", getContext()));
	  // Typeface.createFromAsset(getAssets(),"fontsfolder/B Yekan.ttf");

	        setTypeface(FontCache.get("fonts/calibri.ttf",c));  

	        return view;
	    }

		private void setTypeface(Typeface typeface) {
			// TODO Auto-generated method stub
			
		}

}

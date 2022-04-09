package mbLib;

import shree_nagari.mbank.R;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class SbkTextView extends TextView 
{
	//Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/Kozuka-Gothic-Pro-M_26793.ttf");
	public SbkTextView(Context context, AttributeSet attrs, int defStyle) {
	    super(context, attrs, defStyle);
	    init();
	}
	
	public SbkTextView(Context context, AttributeSet attrs) {
	    super(context, attrs);
	    init();
	}
	
	public SbkTextView(Context context) {
	    super(context);
	    init();
	}
	
	private void init() 
	{
	    if (!isInEditMode()) 
	    {        
	        //setTypeface(tf);	    
	    	//setTypeface(FontCache.get("fonts/Kozuka-Gothic-Pro-M_26793.ttf", getContext()));
	    	setTypeface(FontCache.get("fonts/calibrib.ttf", getContext()));
	    	setTextColor(R.color.sbk_text_clr); 
	    	//setTextSize(18);
	    	//setTextColor(Color.parseColor("#386c46"));
	    }
	}
}
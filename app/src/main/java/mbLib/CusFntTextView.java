package mbLib;

import android.R;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.TextView;

public class CusFntTextView extends TextView 
{
	//Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/Kozuka-Gothic-Pro-M_26793.ttf");
	public CusFntTextView(Context context, AttributeSet attrs, int defStyle) {
	    super(context, attrs, defStyle);
	    init();
	}
	
	public CusFntTextView(Context context, AttributeSet attrs) {
	    super(context, attrs);
	    init();
	}
	
	public CusFntTextView(Context context) {
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
	    	//setTextSize(16);
	    	//setTextColor(R.color.bhingar_text_clr); 
	    	//setTextColor(Color.parseColor("#386c46"));
	    }
	}
}
package mbLib;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

public class CustomButton extends Button 
{
	//Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/Kozuka-Gothic-Pro-M_26793.ttf");
	public CustomButton(Context context, AttributeSet attrs, int defStyle) {
	    super(context, attrs, defStyle);
	    init();
	}
	
	public CustomButton(Context context, AttributeSet attrs) {
	    super(context, attrs);
	    init();
	}
	
	public CustomButton(Context context) {
	    super(context);
	    init();
	}
	
	private void init() {
	    //if (!isInEditMode()) 
	    {
	       // setTypeface(FontCache.get("fonts/Kozuka-Gothic-Pro-M_26793.ttf", getContext()));
	    	//setTypeface(tf);
	    	 setTypeface(FontCache.get("fonts/calibrib.ttf", getContext()));
	    	 //setTextSize(16);
	    	//setTextColor(Color.parseColor("#FFFFFF"));
	    	
	    }
	}
	protected void finalize()
	{
		System.gc();
	}
	/*private void destroy()
	{
		tf=null;
	}*/
}
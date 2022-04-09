package mbLib;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.RadioButton;
public class CusFntRadioButton extends RadioButton{

	public CusFntRadioButton(Context context, AttributeSet attrs, int defStyle) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	public CusFntRadioButton(Context context, AttributeSet attrs) {
	    super(context, attrs);
	    init();
	}
	
	public CusFntRadioButton(Context context) {
	    super(context);
	    init();
	}
	private void init() 
	{
	    if (!isInEditMode()) 
	    {        
	        //setTypeface(tf);	    
	    	setTypeface(FontCache.get("fonts/calibri.ttf", getContext()));
	    	setTextColor(Color.parseColor("#340C6F"));
	    }
	}
}

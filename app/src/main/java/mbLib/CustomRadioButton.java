package mbLib;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RadioButton;

/**
 * Created by OM on 12-Jul-17.
 */

public class CustomRadioButton extends RadioButton
{
    public CustomRadioButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public CustomRadioButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomRadioButton(Context context) {
        super(context);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            /*Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/futura-book-bt.ttf");
            setTypeface(tf);*/
        	setTypeface(FontCache.get("fonts/calibri.ttf", getContext()));
            
        }
    }
}

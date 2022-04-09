package mbLib;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
public class CustomeTextChangeEvent implements TextWatcher{
	EditText frmText,toText,preText;
	public CustomeTextChangeEvent(EditText f,EditText t,EditText p)
	{
		frmText=f;
		toText=t;
		preText=p;
	}
	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub
		if(preText!=null)
		{
		
			if(frmText.length()==0)
			{
				preText.requestFocus();
			}
			else if(frmText.length()==2)
			{
				if(toText!=null)
				{
					String val=frmText.getText().toString();
					frmText.setText(val.substring(0, 1));
					toText.setText(val.substring(1, 2));
				}
			}
		}
		
	}
	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub3
		if(toText==null && frmText.getText().toString().length()>0)
		{
			frmText.requestFocus();
		}
		
	}
	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub
		
		
		if(toText!=null && frmText.getText().toString().length()>0)
		{
			toText.requestFocus();
		}
		
		
		
		
		
	}
}

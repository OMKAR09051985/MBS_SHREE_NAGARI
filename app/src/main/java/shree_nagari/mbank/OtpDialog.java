package shree_nagari.mbank;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import shree_nagari.mbank.R;


	public class OtpDialog extends Dialog implements OnClickListener 
	{

		private Context activity;
		private Dialog d;
		private Button submit,resennd;
		private TextView txt_ref_id; 
		private EditText txt_otp;
		private String textMessage,fromact,retstr;
		public OtpDialog(Context activity) 
		{
			super(activity);		
			/*this.textMessage=textMessage;
			this.fromact=fromact;*/
		
		}

		@Override
		protected void onCreate(Bundle savedInstanceState)  
		{
			super.onCreate(savedInstanceState);
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			setCanceledOnTouchOutside(false);
			setContentView(R.layout.otp_activity);		
			submit = (Button)findViewById(R.id.btn_otp_submit);
			resennd = (Button)findViewById(R.id.btn_otp_resend);
			txt_ref_id=(TextView)findViewById(R.id.txt_ref_id);
			txt_otp=(EditText)findViewById(R.id.txt_otp);
			
			//txt_message.setText(textMessage);
			submit.setOnClickListener(this);
			resennd.setOnClickListener(this);
		}//end onCreate

		@Override
		public void onClick(View v)  
		{
			switch (v.getId()) 
			{
				case R.id.btn_otp_submit:
					//this.finish();
					//this.dismiss();
				  break;	
				  
				case R.id.btn_otp_resend:

					this.dismiss();
					break;
				default:
				  break;
			}
			dismiss();
		}
	}//end class


package shree_nagari.mbank;


import shree_nagari.mbank.R;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CustomAccAdapter extends BaseAdapter {
	String[] result;
	Context context;
	int[] imageId;
	private static LayoutInflater inflater = null;

	public CustomAccAdapter(Activity mainActivity, String[] prgmNameList,
			int[] prgmImages) {
		// TODO Auto-generated constructor stub
		result = prgmNameList;
		context = mainActivity;
		imageId = prgmImages;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return result.length;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public class Holder {
		TextView tv;
		ImageView img;
		LinearLayout listLayout;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		Holder holder = new Holder();
		View rowView;
		rowView = inflater.inflate(R.layout.account_list, null);
		
		holder.img = (ImageView) rowView.findViewById(R.id.imageView1);
		holder.tv = (TextView) rowView.findViewById(R.id.textView1);
		holder.tv.setText(result[position]);
		holder.listLayout=(LinearLayout)rowView.findViewById(R.id.acclistLayout);
		holder.listLayout.setBackgroundColor(Color.WHITE);
		holder.tv.setTextColor(Color.BLACK);
		
		if(position%2==0)
		{
			holder.img.setImageResource(R.mipmap.arrow);
		}
		else
		{
			holder.img.setImageResource(R.mipmap.arrowover);
		}
		
		return rowView;
	}

}
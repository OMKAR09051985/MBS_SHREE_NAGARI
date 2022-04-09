package mbLib;

import java.util.ArrayList;

import shree_nagari.mbank.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CustomAdapterForBranch extends ArrayAdapter<BranchBean> {

	private ArrayList<BranchBean> branchBeans;
	Activity context;

	public CustomAdapterForBranch(Activity context,
			ArrayList<BranchBean> branchBeans) {
		super(context, R.layout.custom_list_branch, branchBeans);
		this.context = context;
		this.branchBeans = branchBeans;
	}

	@SuppressLint("ViewHolder")
	@Override
	public View getView(int position, View view, ViewGroup parent) {
		LayoutInflater inflater = context.getLayoutInflater();
		View rowView = inflater.inflate(R.layout.custom_list_branch, null, true);
		try {
			TextView branchnm = (TextView) rowView.findViewById(R.id.branchnam);
			branchnm.setText(branchBeans.get(position).getBrname());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rowView;
	}
}

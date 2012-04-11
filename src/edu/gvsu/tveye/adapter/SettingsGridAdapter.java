package edu.gvsu.tveye.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SettingsGridAdapter extends BaseAdapter {

	private String[] names;
	private Context context;
	
	public SettingsGridAdapter(Context context) {
		this(context, new String[0]);
	}
	
	public SettingsGridAdapter(Context context, String[] names) {
		this.context = context;
		this.names = names;
	}
	
	public void setNames(String[] names) {
		this.names = names;
		notifyDataSetChanged();
	}
	
	public int getCount() {
		return names.length;
	}

	public Object getItem(int i) {
		return names[i];
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		TextView textView;
		if(convertView == null) {
			textView = new TextView(context);
		} else {
			textView = (TextView) convertView;
		}
		textView.setText(names[position]);
		return textView;
	}

}

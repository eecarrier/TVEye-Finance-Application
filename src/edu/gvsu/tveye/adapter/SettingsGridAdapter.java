package edu.gvsu.tveye.adapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
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
	
	public void setHistory(JSONArray list) {
		String[] names = new String[list.length()]; //{"a", "b", "c", "" + object.get("s"), this.toString()};
		for (int i = 0; i < list.length(); ++i) {
		    JSONObject rec;
			try {
				rec = list.getJSONObject(i);
				names[i] = rec.getString("action") + " " + rec.getString("targetId");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
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
			textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 22);
			textView.setPadding(5, 5, 5, 5);
		} else {
			textView = (TextView) convertView;
		}
		textView.setText(names[position]);
		return textView;
	}

}

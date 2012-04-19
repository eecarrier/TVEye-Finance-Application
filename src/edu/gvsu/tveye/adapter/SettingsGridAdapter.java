package edu.gvsu.tveye.adapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.gvsu.tveye.R;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SettingsGridAdapter extends BaseAdapter {

	private String[] names;
	private String[] action;
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
		String[] names = new String[list.length()];
		String[] action = new String[list.length()];
		for (int i = 0; i < list.length(); ++i) {
		    JSONObject rec;
			try {
				rec = list.getJSONObject(i);
				names[i] = rec.getString("targetId");
				action[i] = rec.getString("action");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		this.names = names;
		this.action = action;
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
		if (action[position].equals("like"))
			textView.setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.thumbs_up), null, null, null);
		else
			textView.setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.thumbs_down), null, null, null);
		textView.setText("id: " + names[position]);
		return textView;
	}

}

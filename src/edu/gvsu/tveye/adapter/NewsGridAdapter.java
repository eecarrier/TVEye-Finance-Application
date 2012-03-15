package edu.gvsu.tveye.adapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import edu.gvsu.tveye.fragment.NewsTileFragment;

public class NewsGridAdapter extends FragmentPagerAdapter {
	
	private JSONObject response;
	
	public NewsGridAdapter(FragmentManager fm, JSONObject response) {
		super(fm);
		setData(response);
	}
	
	public void setData(JSONObject object) {
		response = object;
		notifyDataSetChanged();
	}
	
	public int getCount() {
		try {
			return (int) Math.ceil(response.getJSONArray("list").length() / 6);
		} catch(JSONException e) {
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public Fragment getItem(int position) {
		try {
			JSONArray set = new JSONArray(), list = response.getJSONArray("list");
			for(int i = 0; i < 6 && position * 6 + i < list.length(); i++) {
				set.put(list.getJSONObject(position * 6 + i));
			}
			return new NewsTileFragment(set, position);
		} catch(JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}

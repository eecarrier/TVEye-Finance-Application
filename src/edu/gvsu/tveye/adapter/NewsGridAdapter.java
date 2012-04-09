package edu.gvsu.tveye.adapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import edu.gvsu.tveye.fragment.NewsTileFragment;

public class NewsGridAdapter extends FragmentStatePagerAdapter {
	
	private JSONObject response;
	
	public NewsGridAdapter(FragmentManager fm, JSONObject response) {
		super(fm);
		setData(response);
	}
	
	public void setData(JSONObject object) {
		response = object;
		notifyDataSetChanged();
	}
	
	public JSONObject getData() {
		return response;
	}
	
	public int getCount() {
		JSONArray list = response.optJSONArray("list");
		if(list == null)
			return 0;
		else
			return (int) Math.ceil(list.length() / 6);
	}

	@Override
	public Fragment getItem(int position) {
		try {
			JSONArray set = new JSONArray(), list = response.getJSONArray("list");
			for(int i = 0; i < 6 && position * 6 + i < list.length(); i++) {
				set.put(list.getJSONObject(position * 6 + i));
			}
			return NewsTileFragment.newInstance(set, position);
		} catch(JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}

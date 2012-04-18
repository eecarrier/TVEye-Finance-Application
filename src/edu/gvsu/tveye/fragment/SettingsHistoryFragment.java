package edu.gvsu.tveye.fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import edu.gvsu.tveye.R;
import edu.gvsu.tveye.api.APIWrapper;
import edu.gvsu.tveye.api.APIWrapper.JSONObjectCallback;
import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.Toast;

public class SettingsHistoryFragment extends ListFragment{
	
	ListAdapter adapter;
		
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.settings_history_fragment, null);
		
		new APIWrapper.GetAnalyticsTask(new JSONObjectCallback() {
			public void onError(JSONObject object) {
				Log.d("LikeFragment", object.toString());
				Toast.makeText(getActivity(),
						"Received error!", Toast.LENGTH_LONG)
						.show();
			}

			public void onComplete(JSONObject object) {
				try {
					Log.d("LikeFragment", object.toString());
					JSONArray list = object.getJSONArray("list");
					String[] names = new String[list.length()]; //{"a", "b", "c", "" + object.get("s"), this.toString()};
					for (int i = 0; i < list.length(); ++i) {
					    JSONObject rec = list.getJSONObject(i);
					    names[i] = rec.toString();
					}
					setListAdapter(new ArrayAdapter<String>(getActivity(),
		                    android.R.layout.simple_list_item_1, names));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			public Context getContext() {
				return getActivity();
			}
		}).execute();		
		
		return view;
	}
	
	
}

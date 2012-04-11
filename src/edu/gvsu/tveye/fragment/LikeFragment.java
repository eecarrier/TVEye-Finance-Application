package edu.gvsu.tveye.fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.gvsu.tveye.R;
import edu.gvsu.tveye.api.APIWrapper;
import edu.gvsu.tveye.api.APIWrapper.JSONObjectCallback;
import edu.gvsu.tveye.util.TVEyePreferences;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class LikeFragment  extends DialogFragment{
	
	private TextView name;
	private JSONArray set;
	private int position;
	
	public LikeFragment() {
		
		// TODO Auto-generated constructor stub
	}
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getArguments() != null ? getArguments().getInt("position") : 0;
        try {
			set = new JSONArray(getArguments() != null ? getArguments().getString("set") : "[]");
		} catch (JSONException e) {
			e.printStackTrace();
		}
    }
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.like_fragment, null);
		name = (TextView) view.findViewById(R.id.settings_name);
		name.setText("lame");

		return view;
	}

}

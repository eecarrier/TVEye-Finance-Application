package edu.gvsu.tveye.fragment;

import java.net.URISyntaxException;

import org.apache.http.client.utils.URIUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.gvsu.tveye.NewsArticleActivity;
import edu.gvsu.tveye.R;
import edu.gvsu.tveye.adapter.SettingsGridAdapter;
import edu.gvsu.tveye.api.APIWrapper;
import edu.gvsu.tveye.api.APIWrapper.JSONObjectCallback;
import edu.gvsu.tveye.util.ImageDownloadTask;
import edu.gvsu.tveye.util.TVEyePreferences;
import edu.gvsu.tveye.util.ImageDownloadTask.ImageCallback;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class LikeFragment extends Fragment{
	
	GridView grid;
	SettingsGridAdapter adapter;
		
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.like_fragment, null);
		grid = (GridView)view.findViewById(R.id.gridview);
		
		String[] names = new String[] {"a", "b", "c", this.toString()};
		adapter = new SettingsGridAdapter(getActivity(), names);
		grid.setAdapter(adapter);
		
		return view;
	}
	
}

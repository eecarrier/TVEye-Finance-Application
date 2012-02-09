package edu.gvsu.tveye.fragment;


import org.json.JSONException;
import org.json.JSONObject;

import edu.gvsu.tveye.R;
import android.app.Fragment;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * NewsTileFragment is each individual story seen on the 
 * NewsGridActivity screen.
 * 
 * @author gregzavitz
 */
public class NewsTileFragment extends Fragment {
	
	private JSONObject story;
	private TextView title, content;
	
	public NewsTileFragment(JSONObject object) {
		story = object;
	}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.news_tile, null);
		title = (TextView) view.findViewById(R.id.news_title);
		content = (TextView) view.findViewById(R.id.news_content);
		populate();
		return view;
    }
	
	public void populate() {
		try {
			title.setText(Html.fromHtml(story.getString("title")));
			content.setText(Html.fromHtml(story.getString("content")));
		} catch(JSONException e) {
			e.printStackTrace();
		}
	}
	
}

package edu.gvsu.tveye.fragment;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.client.utils.URIUtils;
import org.apache.http.impl.cookie.DateParseException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import edu.gvsu.tveye.NewsArticleActivity;
import edu.gvsu.tveye.R;
import edu.gvsu.tveye.util.ImageDownloadTask;
import edu.gvsu.tveye.util.ImageDownloadTask.ImageCallback;
import edu.gvsu.tveye.view.AutoResizeTextView;

/**
 * NewsTileFragment is a set of stories seen on the NewsGridActivity screen.
 * This uses the v4 support package Fragment because it is embedded in a
 * ViewPager.
 * 
 * @author gregzavitz
 */
public class NewsTileFragment extends Fragment {

	private JSONArray set;
	private int position;
	
	public static NewsTileFragment newInstance(JSONArray set, int position) {
		NewsTileFragment fragment = new NewsTileFragment();
		Bundle bundle = new Bundle();
		bundle.putInt("position", position);
		bundle.putString("set", set.toString());
		fragment.setArguments(bundle);
		return fragment;
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

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.news_tiles, null);
		view.setId(position);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		try {
			createTiles();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public void createTiles() throws JSONException {
		LinearLayout[] rows = new LinearLayout[] {
				(LinearLayout) getView().findViewById(R.id.news_tile_row_1),
				(LinearLayout) getView().findViewById(R.id.news_tile_row_2) 
		};
		ArrayList<JSONObject> stories = new ArrayList<JSONObject>();
		int n = set.length();
		for(int i = 0; i < n; i++) {
			// TODO: Sort when adding
			stories.add(set.getJSONObject(i));
		}
		Collections.sort(stories, new Comparator<JSONObject>() {
			public int compare(JSONObject lhs, JSONObject rhs) {
				double left = lhs.optDouble("interestLevel", 0);
				double right = rhs.optDouble("interestLevel", 0);
				return (left < right ? -1 : 1);
			}
		});
		LayoutInflater inflater = getActivity().getLayoutInflater();
		for(int i = 0; i < n; i++) {
			LinearLayout row = rows[(i < n / 2 ? 0 : 1)];
			float rowSum = row.getWeightSum();
			// number of tiles on this row:
			//  if there is an odd # of tiles there will be one more on the second row
			//  if there is an even number of rows it's simply n / 2
			int tileCount = (n % 2 == 1 ? (i >= n / 2 ? n / 2 + 1 : n / 2) : n / 2);
			// TODO: Biased weighting for tiles... evenly distributed tiles for now
			float tileWeight = rowSum / tileCount;
			
			JSONObject story = stories.get(i);
			View tile = inflater.inflate(R.layout.news_tile, null);
			tile.setTag(story.toString());
			tile.setLayoutParams(new LinearLayout.LayoutParams(0,
					LayoutParams.FILL_PARENT, tileWeight));
			tile.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Intent intent = new Intent(getActivity(),
							NewsArticleActivity.class);
					intent.putExtra("metadata", (String) v.getTag());
					getActivity().startActivity(intent);
				}
			});
			populateTile(tile, story);
			row.addView(tile);
		}
		
	}

	private void populateTile(final View tile, final JSONObject story)
			throws JSONException {
		TextView title = (TextView) tile.findViewById(R.id.news_title);
		title.setText(Html.fromHtml(story.optString("title")));
		
		TextView timestamp = (TextView) tile.findViewById(R.id.news_timestamp);
		timestamp.setText(story.optString("publishDate"));
		
		TextView origin = (TextView) tile.findViewById(R.id.news_origin);
		String origin_path = story.optString("origin");
		origin.setText(origin_path);
		if (story.has("source")) {
			try {
				String path = URIUtils.createURI("http", "www.google.com", -1,
						"/s2/favicons", "domain=" + origin_path, null)
						.toString();
				final ImageView icon = (ImageView) tile.findViewById(R.id.news_icon);
				new ImageDownloadTask(new ImageCallback() {

					public void imageFailed(String url) {
					}

					public void imageDownloaded(String url, Bitmap bitmap) {
						icon.setImageBitmap(bitmap);
					}
				}).execute(path);
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		}
	}

}

package edu.gvsu.tveye.fragment;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.apache.http.client.utils.URIUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.content.ClipData.Item;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import edu.gvsu.tveye.NewsArticleActivity;
import edu.gvsu.tveye.R;
import edu.gvsu.tveye.util.ImageDownloadTask;
import edu.gvsu.tveye.util.ImageDownloadTask.ImageCallback;

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
	// The interestMultiplier says how much a tiles size can be changed simply by interest variance
	// The higher the multiplier the larger difference in tile sizes
	float interestMultiplier = 0.2f;
	// Focus on these articles by changing the background to blue, indicates high interest level
	float highlightThreshold = 0.82f;
	
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
		float averageInterest = 0;
		for(int i = 0; i < set.length(); i++) {
			JSONObject story = set.getJSONObject(i);
			averageInterest += story.optDouble("interestLevel", 0);
			stories.add(story);
		}
		averageInterest /= set.length();
		Collections.sort(stories, new Comparator<JSONObject>() {
			public int compare(JSONObject lhs, JSONObject rhs) {
				double left = lhs.optDouble("interestLevel", 0);
				double right = rhs.optDouble("interestLevel", 0);
				return (left > right ? -1 : 1);
			}
		});
		int mid = stories.size() / 2;
		fillRow(rows[0], stories.subList(0, mid), averageInterest);
		fillRow(rows[1], stories.subList(mid, stories.size()), averageInterest);		
	}

	// Calculate the value of each tile's weight by first
	// (1): 3/5 * averageWeight + 2/5 * (interest - averageInterest)
	// Add this value to a E(1), then on the next iteration do this
	// weight = (1) / E(1)
	private void fillRow(LinearLayout row, List<JSONObject> stories, float averageInterest) throws JSONException {
		LayoutInflater inflater = getActivity().getLayoutInflater();
		ArrayList<View> interestViews = new ArrayList<View>();
		float rowSum = row.getWeightSum();
		float averageWeight = rowSum / stories.size();
		float sumWeights = 0;
		for(int i = 0; i < stories.size(); i++) {
			JSONObject story = stories.get(i);
			final String metadata = story.toString();
			View tile = inflater.inflate(R.layout.news_tile, null);
			tile.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Intent intent = new Intent(getActivity(),
							NewsArticleActivity.class);
					intent.putExtra("metadata", metadata);
					getActivity().startActivity(intent);
				}
			});
			float interest = (float) story.optDouble("interestLevel", rowSum / stories.size());
			float interestVariance = interest - averageInterest;
			float weight = (1 - interestMultiplier) * averageWeight + interestMultiplier * interestVariance;
			sumWeights += weight;
			tile.setTag(weight);
			interestViews.add(tile);
			populateTile(tile, story);
			row.addView(tile);
		}
		for(View tile : interestViews) {
			float normalized = ((Float) tile.getTag()).floatValue() / sumWeights;
			tile.setLayoutParams(new LinearLayout.LayoutParams(0, -1, normalized));
		}
	}

	private void populateTile(View tile, JSONObject story)
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
		if(story.optDouble("interestLevel", 0) > highlightThreshold) {
			tile.setBackgroundResource(R.drawable.tile_background_high_interest);
			title.setTextColor(getResources().getColor(R.color.tile_heading_high_interest));
			title.setShadowLayer(2, 1, 1, getResources().getColor(R.color.tile_heading_shadow_high_interest));
			timestamp.setTextColor(getResources().getColor(R.color.tile_timestamp_high_interest));
			origin.setTextColor(getResources().getColor(R.color.tile_timestamp_high_interest));
		}
	}

}

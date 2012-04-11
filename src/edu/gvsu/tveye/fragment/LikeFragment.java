package edu.gvsu.tveye.fragment;

import java.net.URISyntaxException;

import org.apache.http.client.utils.URIUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.gvsu.tveye.NewsArticleActivity;
import edu.gvsu.tveye.R;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
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
	
	public void createTiles() throws JSONException {
		final LinearLayout[] tile_group = new LinearLayout[] {
				(LinearLayout) getView().findViewById(R.id.news_tile_row_1),
				(LinearLayout) getView().findViewById(R.id.news_tile_row_2) };
		tile_group[0].setTag(new Float(0.5));
		tile_group[1].setTag(new Float(0.5));

		LayoutInflater inflater = getActivity().getLayoutInflater();
		for (int i = 0; i < set.length(); i++) {
			float weightSum = tile_group[i / 3].getWeightSum();
			float tileWeight = i < 3 ? (i == 0 ? weightSum / 2 : weightSum / 4)
					: weightSum / 3;

			final JSONObject story = set.getJSONObject(i);
			final View tile = inflater.inflate(R.layout.news_tile, null);
			tile.setTag(new Float(tileWeight));
			final LinearLayout group = tile_group[i / 3];
			tile.setLayoutParams(new LinearLayout.LayoutParams(0,
					LayoutParams.FILL_PARENT, tileWeight));

			tile.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Intent intent = new Intent(getActivity(),
							NewsArticleActivity.class);
					intent.putExtra("metadata", story.toString());
					getActivity().startActivity(intent);
				}
			});

			populateTile(tile, story);
			group.addView(tile);
		}
	}
	
	private void populateTile(final View tile, final JSONObject story)
			throws JSONException {
		final TextView title = (TextView) tile.findViewById(R.id.news_title);
		TextView timestamp = (TextView) tile.findViewById(R.id.news_timestamp);
		TextView origin = (TextView) tile.findViewById(R.id.news_origin);
		final ImageView icon = (ImageView) tile.findViewById(R.id.news_icon);
		final ImageView picture = (ImageView) tile
				.findViewById(R.id.news_picture);
		title.setText(Html.fromHtml(story.optString("title")));
		timestamp.setText(story.optString("publishDate"));
		String origin_path = story.optString("origin");
		origin.setText(origin_path);
		if (story.has("imageUrl")) {
			new ImageDownloadTask(new ImageCallback() {
				public void imageFailed(String url) {
					picture.setVisibility(View.GONE);
				}

				public void imageDownloaded(String url, Bitmap bitmap) {
					picture.setLayoutParams(new FrameLayout.LayoutParams(
							LayoutParams.FILL_PARENT, (int) (tile
									.getMeasuredHeight() * 0.33f)));
					picture.setImageBitmap(bitmap);
					title.setBackgroundColor(getResources().getColor(
							R.color.tile_heading_shadow));
				}
			}).execute(story.getString("imageUrl"));
		}
		if (story.has("source")) {
			try {
				String path = URIUtils.createURI("http", "www.google.com", -1,
						"/s2/favicons", "domain=" + origin_path, null)
						.toString();
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

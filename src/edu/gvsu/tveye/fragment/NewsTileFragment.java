package edu.gvsu.tveye.fragment;

import java.util.Date;

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

	public NewsTileFragment(JSONArray set, int position) {
		this.set = set;
		this.position = position;
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
					Intent intent = new Intent(getActivity(), NewsArticleActivity.class);
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
		TextView content = (TextView) tile.findViewById(R.id.news_content);
		TextView timestamp = (TextView) tile.findViewById(R.id.news_timestamp);
		Button read = (Button) tile.findViewById(R.id.read_button);
		title.setText(Html.fromHtml(story.getString("title")));
		content.setText(Html.fromHtml(story.getString("content")));
		timestamp.setText(formatDate(story));
		read.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), NewsArticleActivity.class);
				intent.putExtra("metadata", story.toString());
				getActivity().startActivity(intent);
			}
		});
		if (story.has("imageUrl")) {
			final ImageView picture = (ImageView) tile
					.findViewById(R.id.news_picture);
			new ImageDownloadTask(new ImageCallback() {
				public void imageFailed(String url) {
					picture.setVisibility(View.GONE);
				}

				public void imageDownloaded(String url, Bitmap bitmap) {
					picture.setLayoutParams(new FrameLayout.LayoutParams(
							LayoutParams.FILL_PARENT, (int) (tile
									.getMeasuredHeight() * 0.33f)));
					picture.setImageBitmap(bitmap);
					title.setBackgroundColor(getResources().getColor(R.color.tile_heading_shadow));
				}
			}).execute(story.getString("imageUrl"));
		}
	}

	private String formatDate(JSONObject story) {
		try {
			Date date = org.apache.http.impl.cookie.DateUtils
					.parseDate(
							story.getString("publishDate"),
							new String[] { org.apache.http.impl.cookie.DateUtils.PATTERN_RFC1123 });
			return DateUtils.getRelativeDateTimeString(getActivity(),
					date.getTime(), DateUtils.MINUTE_IN_MILLIS,
					DateUtils.WEEK_IN_MILLIS, 0).toString();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (DateParseException e) {
			e.printStackTrace();
		}
		return "";
	}
	
}

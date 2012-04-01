package edu.gvsu.tveye.fragment;

import java.net.URISyntaxException;
import java.util.Date;

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
	//04-01 13:13:07.440: D/NewsTileFragment(26385): {"id":1510,"isAcquired":"true","author":"ed carson and scott stoddard","interestLevel":0.15797263194432132,"title":"Surprising Jobs Data Show Jobless Down But So Are Payrolls","tickers":[],"source":"Investors Economy","origin":"www.investors.com","isIndexed":"false","publishDate":"2 years ago","url":"http:\/\/news.investors.com\/Article.aspx?id=520390"}
	private void populateTile(final View tile, final JSONObject story)
			throws JSONException {
		Log.d("NewsTileFragment" , story.toString());
		final TextView title = (TextView) tile.findViewById(R.id.news_title);
		TextView content = (TextView) tile.findViewById(R.id.news_content);
		TextView timestamp = (TextView) tile.findViewById(R.id.news_timestamp);
		TextView origin = (TextView) tile.findViewById(R.id.news_origin);
		final ImageView icon = (ImageView) tile.findViewById(R.id.news_icon);
		final ImageView picture = (ImageView) tile
				.findViewById(R.id.news_picture);
		title.setText(Html.fromHtml(story.getString("title")));
		content.setText(Html.fromHtml(story.optString("content", "Chao Removed me")));
		timestamp.setText(story.getString("publishDate"));
		String origin_path = story.getString("origin");
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

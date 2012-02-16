package edu.gvsu.tveye.fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.gvsu.tveye.NewsArticleActivity;
import edu.gvsu.tveye.R;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TableLayout.LayoutParams;

/**
 * NewsTileFragment is a set of stories seen on the NewsGridActivity
 * screen.
 * 
 * @author gregzavitz
 */
public class NewsTileFragment extends Fragment {

	public static final int VERTICAL = 0, HORIZONTAL = 1;
	private JSONArray set;
	private int orientation = VERTICAL, position;

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
			populate();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	private void setTileOrientation(View view, int orientation) {
    	this.orientation = orientation;
    	LinearLayout tiles = (LinearLayout) view.findViewById(R.id.news_tiles);
		LinearLayout[] tile_group = new LinearLayout[] {
			(LinearLayout) view.findViewById(R.id.news_tile_row_1),
			(LinearLayout) view.findViewById(R.id.news_tile_row_2)
		};
		tiles.setOrientation(orientation == HORIZONTAL ? LinearLayout.HORIZONTAL : LinearLayout.VERTICAL);
		tile_group[0].setOrientation(orientation == HORIZONTAL ? LinearLayout.VERTICAL : LinearLayout.HORIZONTAL);
		tile_group[1].setOrientation(orientation == HORIZONTAL ? LinearLayout.VERTICAL : LinearLayout.HORIZONTAL);
    }

	public void populate() throws JSONException {
		LinearLayout[] tile_group = new LinearLayout[] {
				(LinearLayout) getView().findViewById(R.id.news_tile_row_1),
				(LinearLayout) getView().findViewById(R.id.news_tile_row_2)
			};
		
		LayoutInflater inflater = getActivity().getLayoutInflater();
		for(int i = 0; i < set.length(); i++) {
			final JSONObject story = set.getJSONObject(i);
			View tile = inflater.inflate(R.layout.news_tile, null);
			tile.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, 0.3f));
			tile.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Intent intent = new Intent(getActivity(), NewsArticleActivity.class);
					intent.putExtra("metadata", story.toString());
					getActivity().startActivity(intent);
				}
			});

			TextView title = (TextView) tile.findViewById(R.id.news_title), 
			content = (TextView) tile.findViewById(R.id.news_content);
			title.setText(Html.fromHtml(story.getString("title")));
			content.setText(Html.fromHtml(story.getString("content")));
			
			tile_group[i / 3].addView(tile);
		}
	}

}

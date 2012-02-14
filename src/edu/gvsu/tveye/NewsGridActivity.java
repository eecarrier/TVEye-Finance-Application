package edu.gvsu.tveye;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TableLayout.LayoutParams;
import android.widget.TextView;
import edu.gvsu.tveye.api.APIWrapper;
import edu.gvsu.tveye.api.APIWrapper.JSONObjectCallback;


/**
 * NewsGridActivity is the primary screen used for displaying
 * NewsTileFragments in an interesting way.
 * 
 * @author gregzavitz
 */
public class NewsGridActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_tiles);
        loadNews();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_menu, menu);
        return true;
    }
    
    private void loadNews() {
        new APIWrapper.NewsTask(new JSONObjectCallback() {
			public void onError(JSONObject object) {
				AlertDialog.Builder builder = new AlertDialog.Builder(NewsGridActivity.this);
				builder.setTitle("Failed to load news");
				builder.setMessage(object.toString());
				builder.create().show();
			}
			
			public void onComplete(JSONObject object) {
				LinearLayout[] rows = new LinearLayout[] {
					(LinearLayout) findViewById(R.id.news_tile_row_1),
					(LinearLayout) findViewById(R.id.news_tile_row_2)
				};
				LayoutInflater inflate = getLayoutInflater();
				try {
					JSONArray stories = object.getJSONArray("list");
					for(int i = 0; i < stories.length() && i < 6; i++) {
						final JSONObject story = stories.getJSONObject(i);
						View tile = inflate.inflate(R.layout.news_tile, null);
						tile.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, 0.3f));
						tile.setOnClickListener(new OnClickListener() {
							public void onClick(View v) {
								Intent intent = new Intent(NewsGridActivity.this, NewsArticleActivity.class);
								intent.putExtra("metadata", story.toString());
								startActivity(intent);
							}
						});

						TextView title = (TextView) tile.findViewById(R.id.news_title), 
						content = (TextView) tile.findViewById(R.id.news_content);
						title.setText(Html.fromHtml(story.getString("title")));
						content.setText(Html.fromHtml(story.getString("content")));
						
						rows[i / 3].addView(tile);
					}
				} catch(JSONException e) {
					e.printStackTrace();
				}
			}
		}).execute();
    }
	
}

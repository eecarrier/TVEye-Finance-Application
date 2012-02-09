package edu.gvsu.tveye;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.TableLayout;
import android.widget.TableLayout.LayoutParams;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
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
				LayoutInflater inflate = getLayoutInflater();
				try {
					JSONArray stories = object.getJSONArray("list");
					ViewGroup grid = (ViewGroup) findViewById(R.id.tile_grid);
					TableRow row = null;
					for(int i = 0; i < stories.length() && i < 6; i++) {
						final JSONObject story = stories.getJSONObject(i);
						View tile = inflate.inflate(R.layout.news_tile, null);
						tile.setLayoutParams(new TableRow.LayoutParams(0, LayoutParams.FILL_PARENT, 0f));
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
						
						if(i % 3 == 0) {
							row = new TableRow(NewsGridActivity.this);
							row.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, 1f));
							grid.addView(row);
						}
						row.addView(tile);
					}
				} catch(JSONException e) {
					e.printStackTrace();
				}
			}
		}).execute();
    }
	
}

package edu.gvsu.tveye;

import java.util.Date;

import org.apache.http.impl.cookie.DateParseException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.text.format.DateUtils;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import edu.gvsu.tveye.api.APIWrapper;

public class NewsArticleActivity extends Activity {
	
	//TODO
	//how to use progressBar
	//how to get actual content
	private JSONObject story;
	private JSONArray tickers;
	private ImageButton thumbsUp;
	private ImageButton thumbsDown;
	private ImageView picture;
	private TextView title;
	private TextView timestamp;
	private TextView content;
	private Toast toast;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_story);
        
        try {
			story = new JSONObject(getIntent().getExtras().getString("metadata"));
			setTitle(story.getString("title"));
			
	        new APIWrapper.NewsDetailsTask(new APIWrapper.StringCallback() {
				public void onError(Exception e) {
					
					Context context = getApplicationContext();
					int duration = Toast.LENGTH_LONG;
					toast = Toast.makeText(context,  e.getMessage(), duration);
					toast.show();
					
					/*if (type.equals(AuthenticationException.class.getName())) {
						if (message.equals(APIWrapper.CREDENTIALS_INVALID)) {
							toast.setText("Login credentials are invalid... Try again.");
							//dropdown.showMessage("Login credentials are invalid... Try again.", dropdown.errorBackground, 3000);
						} else if (message.equals(APIWrapper.CREDENTIALS_MISSING)) {
							toast.setText("Credentials are missing");
							//dropdown.showMessage("Credentials are missing", dropdown.errorBackground, 3000);
						}
					} else {
						toast.setText("Unable to retrieve the latest news");
						//dropdown.showMessage("Unable to retrieve the latest news", dropdown.errorBackground, 3000);
					}
					toast.show();*/
				}

				public void onComplete(final String data) {
					//picture = (ImageView) findViewById(R.id.news_detail_picture);
					//title = (TextView) findViewById(R.id.news_detail_title);
					//timestamp = (TextView) findViewById(R.id.news_detail_timestamp);
					content = (TextView) findViewById(R.id.news_detail_content);
					
					//title.setText(Html.fromHtml(story.getString("title")));
					content.setText(Html.fromHtml(data.substring(data.indexOf("<div class=\'author\'>"))));
					content.setMovementMethod(LinkMovementMethod.getInstance());
					try {
						LinearLayout references = (LinearLayout) findViewById(R.id.references);
						tickers = story.getJSONArray("tickers");
						for(int i = 0; i < tickers.length(); i++) {
							Button button = new Button(getContext());
							button.setText(tickers.getJSONObject(i).getString("company"));
							references.addView(button);
						}
						System.out.println("Set references");
					} catch (Exception e) {
						e.printStackTrace();
					}
					//timestamp.setText(formatDate(story));
					/*if (story.has("imageUrl")) {
						try {
							new ImageDownloadTask(new ImageCallback() {
								public void imageFailed(String url) {
									picture.setVisibility(View.GONE);
								}

								public void imageDownloaded(String url, Bitmap bitmap) {
									/*picture.setLayoutParams(new FrameLayout.LayoutParams(
											LayoutParams.FILL_PARENT, (int) (tile
													.getMeasuredHeight() * 0.33f)));
									picture.setImageBitmap(bitmap);
									title.setBackgroundColor(Color.argb(0xAA, 0xFF, 0xFF, 0xFF));
								}
							}).execute(story.getString("imageUrl"));
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}*/
				}

				public Context getContext() {
					return NewsArticleActivity.this;
				}
			}).execute(story);
	        
		} catch (JSONException e) {
			e.printStackTrace();
			// TODO: Let the user know that something went wrong loading the story
			finish();
		}
    }
    
    private String formatDate(JSONObject story) {
		try {
			Date date = org.apache.http.impl.cookie.DateUtils
					.parseDate(
							story.getString("publishDate"),
							new String[] { org.apache.http.impl.cookie.DateUtils.PATTERN_RFC1123 });
			return DateUtils.getRelativeDateTimeString(NewsArticleActivity.this,
					date.getTime(), DateUtils.MINUTE_IN_MILLIS,
					DateUtils.WEEK_IN_MILLIS, 0).toString();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (DateParseException e) {
			e.printStackTrace();
		}
		return "";
	}
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.story_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_up: {
			Toast t = Toast.makeText(NewsArticleActivity.this, "Thumbs Up!", Toast.LENGTH_SHORT);
    		t.show();
			return true;
		}
		case R.id.menu_down: {
			Toast t = Toast.makeText(NewsArticleActivity.this, "Thumbs Down!", Toast.LENGTH_SHORT);
    		t.show();
			return true;
		}
		default:
            return super.onOptionsItemSelected(item);
		}
	}
	
}

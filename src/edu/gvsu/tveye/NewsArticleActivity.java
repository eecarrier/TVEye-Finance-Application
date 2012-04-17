package edu.gvsu.tveye;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.auth.AuthenticationException;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.cookie.DateParseException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.text.Spanned;
import android.text.format.DateUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;
import edu.gvsu.tveye.api.APIWrapper;
import edu.gvsu.tveye.util.ImageDownloadTask;

public class NewsArticleActivity extends Activity {
	
	private final String THUMBSUP = ".5";
	private final String THUMBSDOWN = ".5";
	private final String REFERENCELIKE = ".8";
	private final String TIMEONPAGELIKE = ".1";
	private final Long BEFORELIKE = 20000L;
	private final Long LIKEINTERVAL = 20000L;
	private final String UPDATEMESSAGE = "Preferences successfully updated!";
	private final String QUIETUPDATE = "No Toast";
	private final int MAXLIKES = 5;
	private final int TEXTSIZECHANGE = 3;
	
	private JSONObject story;
	private JSONArray tickers;
	private Button smallerText;
	private Button largerText;
	private ImageView picture;
	private TextView title;
	private TextView content;
	private Toast toast;
	private String id;
	private Timer timer=null;
	private int numLikes = 0;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_story);
        
        try {
			story = new JSONObject(getIntent().getExtras().getString("metadata"));
			id = story.getString("id");
			setTitle(Html.fromHtml(story.getString("title")));
			
	        new APIWrapper.NewsDetailsTask(new APIWrapper.StringCallback() {
				public void onError(Exception e) {
					Context context = getApplicationContext();
					String type = e.getClass().getName();
					String message = e.getMessage();
					int duration = Toast.LENGTH_LONG;
					toast = Toast.makeText(context, message, duration);

					if (type.equals(AuthenticationException.class.getName())) {
						if (message.equals(APIWrapper.CREDENTIALS_INVALID)) {
							toast.setText("Login credentials are invalid... Try again.");
						} else if (message.equals(APIWrapper.CREDENTIALS_MISSING)) {
							toast.setText("Credentials are missing");
						}
					} else {
						toast.setText("Unable to retrieve the latest news");
					}
					toast.show();
				}

				public void onComplete(final String data) {
					picture = (ImageView) findViewById(R.id.news_detail_picture);
					title = (TextView) findViewById(R.id.news_detail_title);
					content = (TextView) findViewById(R.id.news_detail_content);
					smallerText = (Button) findViewById(R.id.smallerTextButton);
					smallerText.setOnClickListener(new OnClickListener() {
						public void onClick(View V) {
							content.setTextSize(content.getTextSize()-TEXTSIZECHANGE);
						}
					});
					largerText = (Button) findViewById(R.id.largerTextButton);
					largerText.setOnClickListener(new OnClickListener() {
						public void onClick(View V) {
							content.setTextSize(content.getTextSize()+TEXTSIZECHANGE);
						}
					});
					timer = new Timer();
					timer.scheduleAtFixedRate(new TimerTask() {
						public void run() {
							new APIWrapper.PostAnalyticsTask(analytics).execute("like","news",id,TIMEONPAGELIKE,QUIETUPDATE);
							numLikes++;
							if (numLikes >= MAXLIKES) {
								timer.cancel();
								timer.purge();
							}
						}
					}, BEFORELIKE, LIKEINTERVAL);
					
					if (story.has("imageUrl")) {
						try {
							if (!story.getString("imageUrl").equals("")) {
								new ImageDownloadTask(new ImageDownloadTask.ImageCallback() {
									public void imageFailed(String url) {
										picture.setVisibility(View.GONE);
										Log.d("image", "no good");
									}
	
									public void imageDownloaded(String url, Bitmap bitmap) {
										picture.setLayoutParams(new LinearLayout.LayoutParams(
												bitmap.getWidth(), bitmap.getHeight()));
										picture.setImageBitmap(bitmap);
										Log.d("image", "good");
									}
								}).execute(story.getString("imageUrl"));
							}
						}
						catch(Exception e) {
							e.printStackTrace();
						}
					}
					
					String titleContent = String.format("<a href=\"%s\">%s</a>", story.optString("url", "#"), story.optString("title", ""));
					title.setText(Html.fromHtml(titleContent));
					title.setMovementMethod(LinkMovementMethod.getInstance());
					content.setText(Html.fromHtml(data.substring(data.indexOf("<div class=\'author\'>"))));
					content.setMovementMethod(LinkMovementMethod.getInstance());
					
					try {
						LinearLayout references = (LinearLayout) findViewById(R.id.references);
						tickers = story.getJSONArray("tickers");
						LayoutInflater inflate = LayoutInflater.from(getContext());
						for (int i = 0; i < tickers.length(); i++) {
							Button button = (Button) inflate.inflate(R.layout.like_button, null);
							button.setText(tickers.getJSONObject(i).getString("company"));
							final String tickerId = tickers.getJSONObject(i).getString("id");
							button.setOnClickListener(new OnClickListener() {
								public void onClick(View v) {
									new APIWrapper.PostAnalyticsTask(analytics).execute("like","ticker",tickerId, REFERENCELIKE, UPDATEMESSAGE);
								}
							});
							references.addView(button);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				public Context getContext() {
					return NewsArticleActivity.this;
				}
			}).execute(story);
	        
		} catch (JSONException e) {
			e.printStackTrace();
			toast = Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG);
			toast.show();
			finish();
		}
    }
    
    private APIWrapper.StringCallback analytics = new APIWrapper.StringCallback() {
    	public void onError(Exception e) {
			Context context = getApplicationContext();
			String type = e.getClass().getName();
			String message = e.getMessage();
			int duration = Toast.LENGTH_LONG;
			toast = Toast.makeText(context, message, duration);

			if (type.equals(AuthenticationException.class.getName())) {
				if (message.equals(APIWrapper.CREDENTIALS_INVALID)) {
					toast.setText("Login credentials are invalid... Try again.");
				} else if (message.equals(APIWrapper.CREDENTIALS_MISSING)) {
					toast.setText("Credentials are missing");
				}
			} else {
				toast.setText("Unable to update preferences");
			}
			
			toast.show();
		}

		public void onComplete(String data) {
			if (!data.equals(QUIETUPDATE)) {
				toast = Toast.makeText(getApplicationContext(), data, Toast.LENGTH_SHORT);
				toast.show();
			}
		}
		
		public Context getContext() {
			return NewsArticleActivity.this;
		}
	};
    
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
			new APIWrapper.PostAnalyticsTask(analytics).execute("like","news",id.toString(),THUMBSUP,UPDATEMESSAGE);
			return true;
		}
		case R.id.menu_down: {
			new APIWrapper.PostAnalyticsTask(analytics).execute("dislike","news",id.toString(),THUMBSDOWN,UPDATEMESSAGE);
			return true;
		}
		default:
            return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	public void onPause() {
		if (timer != null) {
			timer.cancel();
			timer.purge();
		}
		super.onPause();
	}
}

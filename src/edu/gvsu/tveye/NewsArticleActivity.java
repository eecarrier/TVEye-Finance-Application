package edu.gvsu.tveye;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

import org.apache.http.auth.AuthenticationException;
import org.apache.http.impl.cookie.DateParseException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.text.Spanned;
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
	
	private JSONObject story;
	private JSONArray tickers;
	private ImageButton thumbsUp;
	private ImageButton thumbsDown;
	private ImageView picture;
	private TextView title;
	private TextView timestamp;
	private TextView content;
	private Toast toast;
	private Integer id;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_story);
        
        try {
			story = new JSONObject(getIntent().getExtras().getString("metadata"));
			id = (Integer) story.get("id");
			setTitle(story.getString("title"));
			
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
					//picture = (ImageView) findViewById(R.id.news_detail_picture);
					title = (TextView) findViewById(R.id.news_detail_title);
					content = (TextView) findViewById(R.id.news_detail_content);
					
					Spanned fullContent = Html.fromHtml(data.substring(data.indexOf("<div class=\'author\'>")), new ImageGetter() {
						public InputStream imageFetch(String source) throws MalformedURLException,IOException {
							URL url = new URL(source);
							Object obj = url.getContent();
							InputStream content = (InputStream)obj; 
						    return content;
						}

						public Drawable getDrawable(String source){
							Drawable d = null;
							try {
									InputStream src = imageFetch(source);
									d = Drawable.createFromStream(src, "src");
									if(d != null){
										d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
									}
							}
							catch (Exception e){
								e.printStackTrace();
							}
							return d;
						}
					}, null);
					
					//This is not the ideal way to find the title, but there is no element with id=title
					title.setText(Html.fromHtml(data.substring(data.indexOf("<h1>"), data.indexOf("</h1>"))));
					title.setMovementMethod(LinkMovementMethod.getInstance());
					content.setText(fullContent);
					content.setMovementMethod(LinkMovementMethod.getInstance());
					
					try {
						LinearLayout references = (LinearLayout) findViewById(R.id.references);
						tickers = story.getJSONArray("tickers");
						for (int i = 0; i < 8; i++) {
						//for(int i = 0; i < tickers.length(); i++) {
							Button button = new Button(getContext());
							//button.setText(tickers.getJSONObject(i).getString("company"));
							button.setText("Sample");
							button.setPadding(40,20,40,20);
							/*button.setOnClickListener(new OnClickListener() {
								public void onClick(View v) {
									//TODO how to get different actions for each button
									String refId = tickers.getJSONObject(i).getString("id");
									new APIWrapper.PostAnalyticsTask(analytics).execute("like","ticker",refId);
								}
							});*/
							references.addView(button);
						}
						System.out.println("Set references");
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
    
    //TODO Will using only one object cause problems?
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
			String message = "Preferences successfully updated!";
			toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
			toast.show();
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
			new APIWrapper.PostAnalyticsTask(analytics).execute("like","news",id.toString());
			return true;
		}
		case R.id.menu_down: {
			new APIWrapper.PostAnalyticsTask(analytics).execute("dislike","news",id.toString());
			return true;
		}
		default:
            return super.onOptionsItemSelected(item);
		}
	}
}

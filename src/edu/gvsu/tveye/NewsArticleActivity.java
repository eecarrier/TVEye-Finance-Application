package edu.gvsu.tveye;

import org.apache.http.auth.AuthenticationException;
import org.apache.http.impl.cookie.DateParseException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;
import android.text.Html;
import android.widget.Toast;
import android.text.format.DateUtils;
import android.text.method.LinkMovementMethod;
import edu.gvsu.tveye.api.APIWrapper;
import edu.gvsu.tveye.api.APIWrapper.JSONObjectCallback;
import edu.gvsu.tveye.util.ImageDownloadTask;
import edu.gvsu.tveye.util.ImageDownloadTask.ImageCallback;
import edu.gvsu.tveye.view.DropDown;

import java.util.Date;

public class NewsArticleActivity extends Activity {
	
	//TODO
	//how to use progressBar
	//how to get actual content
	private JSONObject story;
	private JSONArray tickers;
	private Button[] newsRefs;
	private Button newsRef0;
	private Button newsRef1;
	private Button newsRef2;
	private Button newsRef3;
	private Button newsRef4;
	private Button newsRef5;
	private Button newsRef6;
	private Button newsRef7;
	private ImageButton thumbsUp;
	private ImageButton thumbsDown;
	private ImageView picture;
	private TextView title;
	private TextView timestamp;
	private TextView content;
	private Toast toast;
	
	private OnClickListener thumbsUpClick = new OnClickListener() {
    	public void onClick(View v) {
    		Toast t = Toast.makeText(getApplicationContext(), "Thumbs Up!", Toast.LENGTH_SHORT);
    		t.show();
    	}
    };
    
    private OnClickListener thumbsDownClick = new OnClickListener() {
    	public void onClick(View v) {
    		Toast t = Toast.makeText(getApplicationContext(), "Thumbs Down!", Toast.LENGTH_SHORT);
    		t.show();
    	}
    };
	
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
					
					newsRefs = new Button[8];
					newsRef0 = (Button) findViewById(R.id.news_detail_ref0);
					newsRef1 = (Button) findViewById(R.id.news_detail_ref1);
					newsRef2 = (Button) findViewById(R.id.news_detail_ref2);
					newsRef3 = (Button) findViewById(R.id.news_detail_ref3);
					newsRef4 = (Button) findViewById(R.id.news_detail_ref4);
					newsRef5 = (Button) findViewById(R.id.news_detail_ref5);
					newsRef6 = (Button) findViewById(R.id.news_detail_ref6);
					newsRef7 = (Button) findViewById(R.id.news_detail_ref7);
					thumbsUp = (ImageButton) findViewById(R.id.thumbs_up);
					thumbsUp.setOnClickListener(thumbsUpClick);
					thumbsDown = (ImageButton) findViewById(R.id.thumbs_down);
					thumbsDown.setOnClickListener(thumbsDownClick);
					//picture = (ImageView) findViewById(R.id.news_detail_picture);
					//title = (TextView) findViewById(R.id.news_detail_title);
					//timestamp = (TextView) findViewById(R.id.news_detail_timestamp);
					content = (TextView) findViewById(R.id.news_detail_content);
					
					//title.setText(Html.fromHtml(story.getString("title")));
					content.setText(Html.fromHtml(data));
					content.setMovementMethod(LinkMovementMethod.getInstance());
					try {
						tickers = story.getJSONArray("tickers");
						newsRefs[0] = newsRef0;
						newsRefs[1] = newsRef1;
						newsRefs[2] = newsRef2;
						newsRefs[3] = newsRef3;
						newsRefs[4] = newsRef4;
						newsRefs[5] = newsRef5;
						newsRefs[6] = newsRef6;
						newsRefs[7] = newsRef7;
						for (int i = 0; i < 8; i++)
						{
							if (!tickers.isNull(i))
							{
								newsRefs[i].setText(tickers.getJSONObject(i).getString("company"));
							}
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
	
}

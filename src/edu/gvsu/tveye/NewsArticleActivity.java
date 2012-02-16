package edu.gvsu.tveye;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class NewsArticleActivity extends Activity {

	private JSONObject story;
	private WebView webView;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_story);
        webView = (WebView) findViewById(R.id.web_view);    
        webView.getSettings().setJavaScriptEnabled(true);  
        webView.setWebViewClient(new WebViewClient() {
        	public boolean shouldOverrideUrlLoading(WebView webView, String url) {
        		webView.loadUrl(url);
        		return true;
        	}
        });
        try {
			story = new JSONObject(getIntent().getExtras().getString("metadata"));
			setTitle(story.getString("title"));
			webView.loadUrl(story.getString("link"));
		} catch (JSONException e) {
			e.printStackTrace();
			// TODO: Let the user know that something went wrong loading the story
			finish();
		}
    }
	
}

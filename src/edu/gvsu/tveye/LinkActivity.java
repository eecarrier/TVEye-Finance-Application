package edu.gvsu.tveye;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;

public class LinkActivity extends Activity {
	WebView linkView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		setContentView(R.layout.link);
		linkView = (WebView) findViewById(R.id.link_webview);
		Log.d("linking", "Made it into LinkActivity");
		Log.d("linkviewinfo", linkView.toString());
		super.onCreate(savedInstanceState);
		
		if (savedInstanceState == null) {
			String url = getIntent().getDataString();
			//linkView.loadUrl(url.replace("tveye://", ""));
			if(url != null)
				linkView.loadUrl(url.replace("tveye://", "http://"));
		}
	}
}

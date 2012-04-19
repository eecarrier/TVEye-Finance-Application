package edu.gvsu.tveye.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.Html.ImageGetter;
import android.view.View;

public class ImageParser implements ImageGetter{
	
	public View view;
	private Context context;
	
	public ImageParser(View view, Context context) {
		this.view = view;
		this.context = context;
	}
	
	public Drawable getDrawable(String source) {
		HtmlDrawable hd = new HtmlDrawable();
		
		ImageGetterTask imageGetter = new ImageGetterTask(hd);
		imageGetter.execute(source);
		
		return hd;
	}
	
	public class ImageGetterTask extends AsyncTask<String, Void, Drawable>{
		HtmlDrawable hDraw;
		
		public ImageGetterTask (HtmlDrawable hd) {
			this.hDraw = hd;
		}
		
		@Override
		protected Drawable doInBackground(String... params) {
			String source = params[0];
			return getDrawable(source);
		}
		
		@Override
		protected void onPostExecute(Drawable d) {
			hDraw.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
			hDraw.d = d;
			ImageParser.this.view.invalidate();
		}
		public Drawable getDrawable(String source){
			try {
				InputStream content = imageFetch(source);
				Drawable d = Drawable.createFromStream(content, "src");
				d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
				return d;
			}
			catch(Exception e){
				e.printStackTrace();
				return null;
			}
		}
		public InputStream imageFetch(String source) throws MalformedURLException,IOException {
			HttpClient httpClient = new DefaultHttpClient();
			HttpGet request = new HttpGet(source);
			InputStream content = httpClient.execute(request).getEntity().getContent();
			return content;
		}
	}



}

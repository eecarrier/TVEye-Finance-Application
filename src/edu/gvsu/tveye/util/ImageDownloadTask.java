package edu.gvsu.tveye.util;

import java.net.URL;
import java.util.HashMap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

public class ImageDownloadTask extends AsyncTask<String, Void, HashMap<String, Bitmap>>{

	private ImageCallback callback;
	
	public ImageDownloadTask(ImageCallback callback) {
		this.callback = callback;
	}
	
	@Override
	protected HashMap<String, Bitmap> doInBackground(String... params) {
		HashMap<String, Bitmap> map = new HashMap<String, Bitmap>();
		for(String param : params) {
			try {
				map.put(param, BitmapFactory.decodeStream(new URL(param).openStream()));
			} catch (Exception e) {
				e.printStackTrace();
				map.put(param, null);
			}
		}
		return map;
	}
	
	@Override
	public void onPostExecute(HashMap<String, Bitmap> map) {
		for(String key : map.keySet()) {
			Bitmap value = map.get(key);
			if(value == null)
				callback.imageFailed(key);
			else
				callback.imageDownloaded(key, value);
		}
	}
	
	public static interface ImageCallback {
		
		public void imageDownloaded(String url, Bitmap bitmap);
		public void imageFailed(String url);
		
	}

}

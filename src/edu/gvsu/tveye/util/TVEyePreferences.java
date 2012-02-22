package edu.gvsu.tveye.util;

import org.apache.http.auth.UsernamePasswordCredentials;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class TVEyePreferences {

	private SharedPreferences preferences;

	public TVEyePreferences(Context context) {
		preferences = context.getSharedPreferences("tveye",
				Context.MODE_MULTI_PROCESS);
	}

	public JSONObject getCache() throws JSONException {
		if (hasCache()) {
			String cache = preferences.getString("cache", null);
			if (cache != null) {
				return new JSONObject(cache);
			}
		}
		return null;
	}
	
	public boolean hasCache() {
		return preferences.contains("cache");
	}

	public boolean hasCredentials() {
		return preferences.contains("username")
				&& preferences.contains("password");
	}

	public UsernamePasswordCredentials getCredentials() {
		if (hasCredentials()) {
			return new UsernamePasswordCredentials(preferences.getString(
					"username", ""), preferences.getString("password", ""));
		}
		return null;
	}
	
	public boolean setCredentials(String username, String password) {
		Editor editor = preferences.edit();
		if(username == null)
			editor.remove("username");
		else 
			editor.putString("username", username);
		if(password == null)
			editor.remove("password");
		else
			editor.putString("password", password);
		return editor.commit();
	}
	
	public boolean setCache(JSONObject cache) {
		Editor editor = preferences.edit();
		if(cache == null)
			editor.remove("cache");
		else
			editor.putString("cache", cache.toString());
		return editor.commit();
	}
	
	public boolean clearCredentials() {
		return setCredentials(null, null);
	}
	
	public boolean clearCache() {
		return setCache(null);
	}

}

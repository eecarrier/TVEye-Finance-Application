package edu.gvsu.tveye.api;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

public class APIWrapper {

	private static final String HOSTNAME = "betaworks-dev.arctry.com";
	private static final int PORT = 8964;
	private static final HttpClient httpClient = new DefaultHttpClient();
	
	private static URI createURI(String method) throws URISyntaxException {
		return URIUtils.createURI("http", HOSTNAME, PORT, method, "", null);
	}
	
	private static byte[] consumeStream(InputStream input) throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream(); 
		byte[] buffer = new byte[2048];
		int read;
		while((read = input.read(buffer)) > -1) {
			output.write(buffer, 0, read);
		}
		input.close();
		return output.toByteArray();
	}
	
	private static JSONObject exceptionToJSON(Exception e) {
		JSONObject object = new JSONObject();
		try {
			object.put("error", e.getMessage());
			object.put("exception", e.getClass().getName());
		} catch(JSONException e1) {
			e1.printStackTrace();
		}
		return object;
	}
	
	public static class RegisterTask extends AsyncTask<RegisterTask.Params, Void, JSONObject> {

		private JSONObjectCallback callback;
		
		public RegisterTask(JSONObjectCallback callback) {
			this.callback = callback;
		}
		
		@Override
		protected JSONObject doInBackground(Params... params) {
			if(params.length > 0) {
				try {
					// Create an HTTP request using Apache's HTTP client library
					HttpPost request = new HttpPost(createURI("/register"));
					request.setEntity(params[0].getEntity());
					request.setHeader("DevKey", Config.DEV_KEY);
					request.setHeader("Accept", "application/json");
		
					// Execute the request using an HttpClient
					HttpResponse response = httpClient.execute(request);
					HttpEntity responseEntity = response.getEntity();
					if(response.getStatusLine().getStatusCode() != 200) {
						return exceptionToJSON(new Exception("Server responded with status code " + response.getStatusLine().getStatusCode()));
					} else {
						// Consume the HTTP response and create a JSONObject from content
						String content = new String(consumeStream(responseEntity.getContent()));
						Log.d("RegisterTask", "Received content:\n" + content);
						return new JSONObject(content);
					}
				} catch(Exception e) {
					e.printStackTrace();
					return exceptionToJSON(e);
				}
			} else {
				return exceptionToJSON(new Exception("Developer did not provide parameters"));
			}
		}
		
		@Override
		protected void onPostExecute(JSONObject result) {
			if(result.has("error")) {
				callback.onError(result);
			} else {
				callback.onComplete(result);
			}
		}
		
		public static class Params {
			
			public String email, password, firstName, lastName;
			
			public Params(String email, String password, String firstName, String lastName) {
				this.email = email;
				this.password = password;
				this.firstName = firstName;
				this.lastName = lastName;
			}
			
			public UrlEncodedFormEntity getEntity() throws UnsupportedEncodingException {
				List<NameValuePair> data = new ArrayList<NameValuePair>();
				data.add(new BasicNameValuePair("email", email));
				data.add(new BasicNameValuePair("password", password));
				// Only include first and last names if the user provided them (not required)
				if(firstName != null)
					data.add(new BasicNameValuePair("firstName", firstName));
				if(lastName != null)
					data.add(new BasicNameValuePair("lastName", lastName));
				return new UrlEncodedFormEntity(data);
			}
		}
		
	}

	public static class NewsTask extends AsyncTask<NewsTask.Params, Void, JSONObject> {

		private JSONObjectCallback callback;
		
		public NewsTask(JSONObjectCallback callback) {
			this.callback = callback;
		}
		
		@Override
		protected JSONObject doInBackground(Params... params) {
			if(params.length > 0) {
				try {
					// Create an HTTP request using Apache's HTTP client library
					HttpGet request = new HttpGet(createURI("/news/list?" + params[0].getParam()));
					request.setHeader("DevKey", Config.DEV_KEY);
					request.setHeader("Accept", "application/json");
		
					// Execute the request using an HttpClient
					HttpClient httpClient = new DefaultHttpClient();
					HttpResponse response = httpClient.execute(request);
					HttpEntity responseEntity = response.getEntity();
					if(response.getStatusLine().getStatusCode() != 200) {
						return exceptionToJSON(new Exception("Server responded with status code " + response.getStatusLine().getStatusCode()));
					} else {
						// Consume the HTTP response and create a JSONObject from content
						String content = new String(consumeStream(responseEntity.getContent()));
						Log.d("NewsTask", "Received content:\n" + content);
						return new JSONObject(content);
					}
				} catch(Exception e) {
					e.printStackTrace();
					return exceptionToJSON(e);
				}
			} else {
				return exceptionToJSON(new Exception("Developer did not provide parameters"));
			}
		}
		
		@Override
		protected void onPostExecute(JSONObject result) {
			if(result.has("error")) {
				callback.onError(result);
			} else {
				callback.onComplete(result);
			}
		}
		
		public static class Params {
			
			public int s;
			
			public Params() {
				this(0);
			}
			
			public Params(int s) {
				this.s = s;
			}
			
			public String getParam() {
				return "s=" + s;
			}
			
		}
		
	}
	
	public static interface JSONObjectCallback {
		
		public void onComplete(JSONObject object);
		public void onError(JSONObject object);
		
	}
	
}

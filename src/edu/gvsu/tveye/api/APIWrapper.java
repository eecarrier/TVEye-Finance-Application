package edu.gvsu.tveye.api;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.impl.auth.BasicSchemeFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import edu.gvsu.tveye.util.TVEyePreferences;

public class APIWrapper {

	public static final String CREDENTIALS_MISSING = "Credentials were not supplied";
	public static final String CREDENTIALS_INVALID = "Credentials were invalid";

	private static final String HOSTNAME = "betaworks-dev.arctry.com";
	private static final int PORT = 8964;

	private static HttpClient httpClient = new DefaultHttpClient();

	private static URI createURI(String method) throws URISyntaxException {
		return URIUtils.createURI("http", HOSTNAME, PORT, method, null, null);
	}

	private static void authenticate(HttpRequest request, Context context)
			throws AuthenticationException {
		TVEyePreferences preferences = new TVEyePreferences(context);
		if (!preferences.hasCredentials())
			throw new AuthenticationException(CREDENTIALS_MISSING);
		else {
			UsernamePasswordCredentials credentials = preferences
					.getCredentials();
			request.addHeader(new BasicSchemeFactory().newInstance(
					request.getParams()).authenticate(credentials, request));
		}
	}

	private static byte[] consumeStream(InputStream input) throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		byte[] buffer = new byte[2048];
		int read;
		while ((read = input.read(buffer)) > -1) {
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
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		return object;
	}

	public static class RegisterTask extends
			AsyncTask<RegisterTask.Params, Void, JSONObject> {

		private JSONObjectCallback callback;

		public RegisterTask(JSONObjectCallback callback) {
			this.callback = callback;
		}

		@Override
		protected JSONObject doInBackground(Params... params) {
			if (params.length > 0) {
				try {
					// Create an HTTP request using Apache's HTTP client library
					HttpPost request = new HttpPost(createURI("/register"));
					request.setEntity(params[0].getEntity());
					request.setHeader("Pragma", Config.DEV_KEY);
					request.setHeader("Accept", "application/json");

					// Execute the request using an HttpClient
					HttpResponse response = httpClient.execute(request);
					HttpEntity responseEntity = response.getEntity();
					if (response.getStatusLine().getStatusCode() != 200) {
						return exceptionToJSON(new Exception(
								"Server responded with status code "
										+ response.getStatusLine()
												.getStatusCode()));
					} else {
						// Consume the HTTP response and create a JSONObject
						// from content
						String content = new String(
								consumeStream(responseEntity.getContent()));
						Log.d("RegisterTask", "Received content:\n" + content);
						return new JSONObject(content);
					}
				} catch (IOException e) {
					e.printStackTrace();
					return exceptionToJSON(e);
				} catch (JSONException e) {
					e.printStackTrace();
					return exceptionToJSON(e);
				} catch (URISyntaxException e) {
					e.printStackTrace();
					return exceptionToJSON(e);
				}
			} else {
				return exceptionToJSON(new Exception(
						"Developer did not provide parameters"));
			}
		}

		@Override
		protected void onPostExecute(JSONObject result) {
			if (result.has("error")) {
				callback.onError(result);
			} else {
				callback.onComplete(result);
			}
		}

		public static class Params {

			public String email, password, firstName, lastName;

			public Params(String email, String password, String firstName,
					String lastName) {
				this.email = email;
				this.password = password;
				this.firstName = firstName;
				this.lastName = lastName;
			}

			public UrlEncodedFormEntity getEntity()
					throws UnsupportedEncodingException {
				List<NameValuePair> data = new ArrayList<NameValuePair>();
				data.add(new BasicNameValuePair("email", email));
				data.add(new BasicNameValuePair("password", password));
				// Only include first and last names if the user provided them
				// (not required)
				if (firstName != null)
					data.add(new BasicNameValuePair("firstName", firstName));
				if (lastName != null)
					data.add(new BasicNameValuePair("lastName", lastName));
				return new UrlEncodedFormEntity(data);
			}
		}

	}

	public static class NewsTask extends
			AsyncTask<NewsTask.Params, Void, JSONObject> {

		private JSONObjectCallback callback;

		public NewsTask(JSONObjectCallback callback) {
			this.callback = callback;
		}

		@Override
		protected JSONObject doInBackground(Params... params) {
			try {
				// Create an HTTP request using Apache's HTTP client library
				String path = "/news/list"
						+ (params.length > 0 ? "?" + params[0].getParam() : "");
				HttpGet request = new HttpGet(createURI(path));
				request.setHeader("Pragma", Config.DEV_KEY);
				request.setHeader("Accept", "application/json");
				authenticate(request, callback.getContext());

				// Execute the request using an HttpClient
				HttpResponse response = httpClient.execute(request);
				HttpEntity responseEntity = response.getEntity();
				int statusCode = response.getStatusLine().getStatusCode();
				if (statusCode == 302) {
					for (Header header : response.getHeaders("Location")) {
						if (header.getValue().contains("login")) {
							return exceptionToJSON(new AuthenticationException(
									CREDENTIALS_INVALID));
						}
					}
				}
				if (statusCode == 403) {
					return exceptionToJSON(new AuthenticationException(
							CREDENTIALS_INVALID));
				} else if (statusCode == 200) {
					// Consume the HTTP response and create a JSONObject from
					// content
					String content = new String(
							consumeStream(responseEntity.getContent())).trim();
					JSONObject jobj = new JSONObject(content);
					if (!jobj.optBoolean("login", true))
						return exceptionToJSON(new AuthenticationException(
								CREDENTIALS_INVALID));
					return jobj;
				} else {
					return exceptionToJSON(new Exception(
							"Server responded with status code "
									+ response.getStatusLine().getStatusCode()));
				}
			} catch (IOException e) {
				e.printStackTrace();
				return exceptionToJSON(e);
			} catch (JSONException e) {
				// e.printStackTrace();
				System.err.println(e.getMessage());
				return exceptionToJSON(e);
			} catch (AuthenticationException e) {
				e.printStackTrace();
				return exceptionToJSON(e);
			} catch (URISyntaxException e) {
				e.printStackTrace();
				return exceptionToJSON(e);
			}
		}

		@Override
		protected void onPostExecute(JSONObject result) {
			if (result.has("error")) {
				callback.onError(result);
			} else {
				callback.onComplete(result);
			}
		}

		public static class Params {

			public int n;

			public Params() {
				this(0);
			}

			public Params(int s) {
				this.n = s;
			}

			public String getParam() {
				return "s=" + n;
			}

		}

	}

	public static class NewsDetailsTask extends
			AsyncTask<JSONObject, Void, Object> {

		private StringCallback callback;

		public NewsDetailsTask(StringCallback callback) {
			this.callback = callback;
		}

		@Override
		protected Object doInBackground(JSONObject... stories) {
			try {
				// Create an HTTP request using Apache's HTTP client library
				String path = "/news/details?newsID="
						+ stories[0].optInt("id", 0);
				HttpGet request = new HttpGet(createURI(path));
				request.setHeader("Pragma", Config.DEV_KEY);
				request.setHeader("Accept", "text/html");
				authenticate(request, callback.getContext());

				// Execute the request using an HttpClient
				HttpResponse response = httpClient.execute(request);
				HttpEntity responseEntity = response.getEntity();
				int statusCode = response.getStatusLine().getStatusCode();
				if (statusCode == 403) {
					return new AuthenticationException(CREDENTIALS_INVALID);
				} else if (statusCode == 200) {
					// Consume the HTTP response and create a JSONObject from
					// content
					String content = new String(
							consumeStream(responseEntity.getContent()));
					Log.d("NewsDetailsTask", "Received content:\n" + content);
					return content;
				} else {
					return new Exception("Server responded with status code "
							+ response.getStatusLine().getStatusCode());
				}
			} catch (IOException e) {
				e.printStackTrace();
				return e;
			} catch (AuthenticationException e) {
				e.printStackTrace();
				return e;
			} catch (URISyntaxException e) {
				e.printStackTrace();
				return e;
			}
		}

		@Override
		protected void onPostExecute(Object result) {
			if (result instanceof Exception) {
				callback.onError((Exception) result);
			} else if (result instanceof String) {
				callback.onComplete((String) result);
			}
		}

	}

	public static class GetAnalyticsTask extends
			AsyncTask<Void, Void, JSONObject> {

		private JSONObjectCallback callback;

		public GetAnalyticsTask(JSONObjectCallback callback) {
			this.callback = callback;
		}

		@Override
		protected JSONObject doInBackground(Void... v) {
			try {
				// Create an HTTP request using Apache's HTTP client library
				String path = "/my/analytics";
				HttpGet request = new HttpGet(createURI(path));
				request.setHeader("Pragma", Config.DEV_KEY);
				request.setHeader("Accept", "application/json");
				authenticate(request, callback.getContext());

				// Execute the request using an HttpClient
				HttpResponse response = httpClient.execute(request);
				HttpEntity responseEntity = response.getEntity();
				int statusCode = response.getStatusLine().getStatusCode();
				if (statusCode == 403) {
					return exceptionToJSON(new AuthenticationException(
							CREDENTIALS_INVALID));
				} else if (statusCode == 200) {
					// Consume the HTTP response and create a JSONObject from
					// content
					String content = new String(
							consumeStream(responseEntity.getContent()));
					content = content.replaceAll("\"s\":,", "\"s\":0,");
					return new JSONObject(content);
				} else {
					return exceptionToJSON(new Exception(
							"Server responded with status code "
									+ response.getStatusLine().getStatusCode()));
				}
			} catch (IOException e) {
				e.printStackTrace();
				return exceptionToJSON(e);
			} catch (AuthenticationException e) {
				e.printStackTrace();
				return exceptionToJSON(e);
			} catch (URISyntaxException e) {
				e.printStackTrace();
				return exceptionToJSON(e);
			} catch (JSONException e) {
				e.printStackTrace();
				return exceptionToJSON(e);
			}
		}

		@Override
		protected void onPostExecute(JSONObject result) {
			if (result.has("error")) {
				callback.onError(result);
			} else {
				callback.onComplete(result);
			}
		}

	}

	public static class AccumulatedAnalyticsTask extends
			AsyncTask<Void, Void, JSONObject> {

		private JSONObjectCallback callback;

		public AccumulatedAnalyticsTask(JSONObjectCallback callback) {
			this.callback = callback;
		}

		@Override
		protected JSONObject doInBackground(Void... v) {
			try {
				// Create an HTTP request using Apache's HTTP client library
				String path = "/my/analytics?isAccumulated=true";
				HttpGet request = new HttpGet(createURI(path));
				request.setHeader("Pragma", Config.DEV_KEY);
				request.setHeader("Accept", "application/json");
				authenticate(request, callback.getContext());

				// Execute the request using an HttpClient
				HttpResponse response = httpClient.execute(request);
				HttpEntity responseEntity = response.getEntity();
				int statusCode = response.getStatusLine().getStatusCode();
				if (statusCode == 403) {
					return exceptionToJSON(new AuthenticationException(
							CREDENTIALS_INVALID));
				} else if (statusCode == 200) {
					// Consume the HTTP response and create a JSONObject from
					// content
					String content = new String(
							consumeStream(responseEntity.getContent()));
					content = content.replaceAll("\"s\":,", "\"s\":0,");
					return new JSONObject(content);
				} else {
					return exceptionToJSON(new Exception(
							"Server responded with status code "
									+ response.getStatusLine().getStatusCode()));
				}
			} catch (IOException e) {
				e.printStackTrace();
				return exceptionToJSON(e);
			} catch (AuthenticationException e) {
				e.printStackTrace();
				return exceptionToJSON(e);
			} catch (URISyntaxException e) {
				e.printStackTrace();
				return exceptionToJSON(e);
			} catch (JSONException e) {
				e.printStackTrace();
				return exceptionToJSON(e);
			}
		}

		@Override
		protected void onPostExecute(JSONObject result) {
			if (result.has("error")) {
				callback.onError(result);
			} else {
				callback.onComplete(result);
			}
		}

	}

	public static class PostAnalyticsTask extends
			AsyncTask<String, Void, Object> {

		private StringCallback callback;

		public PostAnalyticsTask(StringCallback callback) {
			this.callback = callback;
		}

		@Override
		protected Object doInBackground(String... params) {
			try {
				// Create an HTTP request using Apache's HTTP client library
				String path = "/my/analytics";
				HttpPost post = new HttpPost(createURI(path));
				post.setHeader("Pragma", Config.DEV_KEY);
				authenticate(post, callback.getContext());

				List<NameValuePair> nvps = new ArrayList<NameValuePair>();
				nvps.add(new BasicNameValuePair("action", params[0]));
				nvps.add(new BasicNameValuePair("target", params[1]));
				nvps.add(new BasicNameValuePair("targetId", params[2]));
				nvps.add(new BasicNameValuePair("degree", params[3]));
				post.setEntity(new UrlEncodedFormEntity(nvps));

				// Execute the request using an HttpClient
				HttpResponse response = httpClient.execute(post);
				int statusCode = response.getStatusLine().getStatusCode();
				if (statusCode == 403) {
					return new AuthenticationException(CREDENTIALS_INVALID);
				} else if (statusCode == 200) {
					return params[4];
				} else {
					return new Exception("Server responded with status code "
							+ response.getStatusLine().getStatusCode());
				}
			} catch (Exception e) {
				e.printStackTrace();
				return e;
			}
		}

		@Override
		protected void onPostExecute(Object result) {
			if (result instanceof Exception) {
				callback.onError((Exception) result);
			} else if (result instanceof String) {
				callback.onComplete((String) result);
			}
		}
	}

	public static interface StringCallback {

		public Context getContext();

		public void onComplete(String data);

		public void onError(Exception error);

	}

	public static interface JSONObjectCallback {

		public Context getContext();

		public void onComplete(JSONObject object);

		public void onError(JSONObject object);

	}

}

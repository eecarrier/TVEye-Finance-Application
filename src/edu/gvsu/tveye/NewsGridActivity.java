package edu.gvsu.tveye;

import org.apache.http.auth.AuthenticationException;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.SimpleOnPageChangeListener;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import edu.gvsu.tveye.adapter.NewsGridAdapter;
import edu.gvsu.tveye.api.APIWrapper;
import edu.gvsu.tveye.api.APIWrapper.JSONObjectCallback;
import edu.gvsu.tveye.fragment.LoginFragment;
import edu.gvsu.tveye.fragment.LoginFragment.LoginCallback;
import edu.gvsu.tveye.util.TVEyePreferences;
import edu.gvsu.tveye.view.DropDown;

/**
 * NewsGridActivity is the primary screen used for displaying NewsTileFragments
 * in an interesting way.
 * 
 * @author gregzavitz
 */
public class NewsGridActivity extends FragmentActivity implements LoginCallback {

	private NewsGridAdapter adapter;
	private ViewPager pager;
	private TextView more;
	private DropDown dropdown;
	private Animation hide, show, bump, fade_in, fade_out;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.news_pager);
		dropdown = (DropDown) findViewById(R.id.drop_down);
		pager = (ViewPager) findViewById(R.id.pager);
		pager.setOnPageChangeListener(new SimpleOnPageChangeListener() {
			public void onPageSelected(int page) {
				if (page < adapter.getCount() - 1) {
					if(more.getVisibility() == View.GONE) {
						more.setVisibility(View.VISIBLE);
						more.startAnimation(show);
					} else {
						more.startAnimation(bump);
					}
				} else {
					more.startAnimation(hide);
					more.setVisibility(View.GONE);
				}
			}
		});
		more = (TextView) findViewById(R.id.more);
		more.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				pager.setCurrentItem(pager.getCurrentItem() + 1, true);
			}
		});
		hide = AnimationUtils.loadAnimation(this, R.anim.slide_out_to_right);
		show = AnimationUtils.loadAnimation(this, R.anim.slide_in_from_right);
		bump = AnimationUtils.loadAnimation(this, R.anim.bump);
		fade_in = AnimationUtils.loadAnimation(this, R.anim.fade_in);
		fade_out = AnimationUtils.loadAnimation(this, R.anim.fade_out);
		loadCache();
		loadNews();
	}

	private void loadCache() {
		TVEyePreferences preferences = new TVEyePreferences(this);
		if (preferences.hasCache()) {
			try {
				pager.setAdapter((adapter = new NewsGridAdapter(
						getSupportFragmentManager(), preferences.getCache())));
				more.setVisibility(View.VISIBLE);
				more.startAnimation(show);
				dropdown.showMessage("The content displayed below is outdated", dropdown.messageBackground);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	private void displayLogin() {
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.addToBackStack(null);
		new LoginFragment(this).show(ft, "login");
	}

	private void loadNews() {
		new APIWrapper.NewsTask(new JSONObjectCallback() {
			public void onError(JSONObject object) {
				String type = object.optString("exception", "exception");
				String message = object.optString("error", "Error");
				if (type.equals(AuthenticationException.class.getName())) {
					if (message.equals(APIWrapper.CREDENTIALS_INVALID)) {
						dropdown.showMessage("Login credentials are invalid... Try again.", dropdown.errorBackground, 3000);
						displayLogin();
					} else if (message.equals(APIWrapper.CREDENTIALS_MISSING)) {
						displayLogin();
					}
				} else {
					dropdown.showMessage("Unable to retrieve the latest news", dropdown.errorBackground, 3000);
				}
			}

			public void onComplete(final JSONObject object) {
				more.setVisibility(View.VISIBLE);
				more.startAnimation(show);
				if (adapter == null)
					pager.setAdapter((adapter = new NewsGridAdapter(
							getSupportFragmentManager(), object)));
				else {
					dropdown.dismiss();
					fade_out.setAnimationListener(new AnimationListener() {
						public void onAnimationEnd(Animation animation) {
							adapter.setData(object);
							pager.setVisibility(View.VISIBLE);
							pager.startAnimation(fade_in);
						}

						public void onAnimationRepeat(Animation animation) {
						}

						public void onAnimationStart(Animation animation) {
						}
					});
					pager.startAnimation(fade_out);
					pager.setVisibility(View.GONE);
				}
				new TVEyePreferences(NewsGridActivity.this).setCache(object);
			}

			public Context getContext() {
				return NewsGridActivity.this;
			}
		}).execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.action_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_logout: {
			new TVEyePreferences(this).clearCredentials();
			displayLogin();
			break;
		}
		case R.id.menu_settings: {
			Intent intent = new Intent(this, SettingsActivity.class);
			startActivityForResult(intent, 1);
			break;
		}
		case R.id.menu_refresh: {
			loadNews();
			break;
		}
		default:
			return true;
		}
		return false;
	}

	public void setCredentials() {
		loadNews();
	}

}
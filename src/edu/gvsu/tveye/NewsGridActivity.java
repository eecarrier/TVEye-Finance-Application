package edu.gvsu.tveye;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.BounceInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;
import edu.gvsu.tveye.adapter.NewsGridAdapter;
import edu.gvsu.tveye.api.APIWrapper;
import edu.gvsu.tveye.api.APIWrapper.JSONObjectCallback;


/**
 * NewsGridActivity is the primary screen used for displaying
 * NewsTileFragments in an interesting way.
 * 
 * @author gregzavitz
 */
public class NewsGridActivity extends FragmentActivity {
	
	private NewsGridAdapter adapter;
	private ViewPager pager;
	private TextView more;
	private Animation hide, show, bump;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); 
        setContentView(R.layout.news_pager);
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setOnPageChangeListener(new OnPageChangeListener() {
			public void onPageSelected(int page) {
				if(page < adapter.getCount() - 1) {
					Log.d("NewsGrid", "Bumping");
					more.startAnimation(bump);
					more.setVisibility(View.VISIBLE);
				} else {
					Log.d("NewsGrid", "Hide");
					more.startAnimation(hide);
					more.setVisibility(View.GONE);
				}
			}
			
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}
			
			public void onPageScrollStateChanged(int arg0) {
			}
		});
        more = (TextView) findViewById(R.id.more);
        more.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				pager.setCurrentItem(pager.getCurrentItem() + 1, true);
			}
		});
        int rself = TranslateAnimation.RELATIVE_TO_SELF;
        hide = new TranslateAnimation(rself, 0f, rself, 1f, rself, 0f, rself, 0);
        hide.setDuration(1000);
        show = new TranslateAnimation(rself, 1f, rself, 0f, rself, 0f, rself, 0);
        show.setDuration(1000);
        bump = new TranslateAnimation(rself, .7f, rself, 1f, rself, 0f, rself, 0);
        bump.setInterpolator(new BounceInterpolator());
        bump.setDuration(1000);
        loadNews();
    }
    
    public void loadNews() {
        new APIWrapper.NewsTask(new JSONObjectCallback() {
			public void onError(JSONObject object) {
				AlertDialog.Builder builder = new AlertDialog.Builder(NewsGridActivity.this);
				builder.setTitle("Failed to load news");
				builder.setMessage(object.toString());
				builder.create().show();
			}
			
			public void onComplete(JSONObject object) {
				more.setVisibility(View.VISIBLE);
				more.startAnimation(show);
		        pager.setAdapter((adapter = new NewsGridAdapter(getSupportFragmentManager(), object)));
			}
		}).execute();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_menu, menu);
        return true;
    }
    
}
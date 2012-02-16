package edu.gvsu.tveye;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Menu;
import android.view.MenuInflater;
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
	private TextView debug;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); 
        setContentView(R.layout.news_pager);
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setOnPageChangeListener(new OnPageChangeListener() {
			
			public void onPageSelected(int page) {
				debug.setText("Page selected: " + (1 + page) + "/" + adapter.getCount());
			}
			
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}
			
			public void onPageScrollStateChanged(int arg0) {
			}
		});
        debug = (TextView) findViewById(R.id.debug);
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
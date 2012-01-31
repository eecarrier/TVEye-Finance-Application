package edu.gvsu.tveye;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.TextView;


/**
 * NewsGridActivity is the primary screen used for displaying
 * NewsTileFragments in an interesting way.
 * 
 * @author gregzavitz
 */
public class NewsGridActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupViews();
    }
    
    private void setupViews() {
    	Button sample = new Button(this);
    	sample.setText("Click to move to the next activity");
    	sample.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				Intent intent = new Intent(NewsGridActivity.this, SplashActivity.class);
				startActivity(intent);
			}
		});
    	setContentView(sample);
    }
	
}

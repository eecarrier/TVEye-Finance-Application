package edu.gvsu.tveye;

import android.app.Activity;
import android.os.Bundle;
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
    	TextView sample = new TextView(this);
    	sample.setText("Sample");
    	setContentView(sample);
    }
	
}

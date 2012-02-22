package edu.gvsu.tveye;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import edu.gvsu.tveye.api.APIWrapper;
import edu.gvsu.tveye.api.APIWrapper.JSONObjectCallback;

public class SplashActivity extends Activity {
	
	private Button registerButton;
	private TextView responseView;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        setupViews();
    }
    
    private void setupViews() {
    	responseView = (TextView) findViewById(R.id.response);
    	
    	registerButton = (Button) findViewById(R.id.button1);
    	registerButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				try {
					Toast.makeText(SplashActivity.this, "Registering with API", Toast.LENGTH_LONG).show();
					new APIWrapper.RegisterTask(new JSONObjectCallback() {
						public void onError(JSONObject object) {
							Toast.makeText(SplashActivity.this, "Received error!", Toast.LENGTH_LONG).show();
							responseView.setText(object.toString());
						}
						
						public void onComplete(JSONObject object) {
							Toast.makeText(SplashActivity.this, "Received response!", Toast.LENGTH_LONG).show();
							responseView.setText(object.toString());
						}
						
						public Context getContext() {
							return SplashActivity.this;
						}
					}).execute(new APIWrapper.RegisterTask.Params("test-no-6@email.com", "motomoto", "John", "Smith"));
				} catch (Exception e) {
					responseView.setText(e.getMessage());
				}
			}
		});
    }
    
}
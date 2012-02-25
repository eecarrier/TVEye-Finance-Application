package edu.gvsu.tveye;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Toast;
import edu.gvsu.tveye.api.APIWrapper;
import edu.gvsu.tveye.api.APIWrapper.JSONObjectCallback;

public class RegisterActivity extends Activity {
	
	private Button regButton;
	private EditText first, last, email, password;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        setupViews();
    }
    
    private void setupViews() {
    	first = (EditText) findViewById(R.id.reg_first);
    	last = (EditText) findViewById(R.id.reg_last);
    	email = (EditText) findViewById(R.id.reg_email);
    	password = (EditText) findViewById(R.id.reg_password);
    	
    	regButton = (Button) findViewById(R.id.reg_regButton);
    	regButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				try {
					Toast.makeText(RegisterActivity.this, "Registering your account", Toast.LENGTH_LONG).show();
					new APIWrapper.RegisterTask(new JSONObjectCallback() {
						public void onError(JSONObject object) {
							Toast.makeText(RegisterActivity.this, "Received error!", Toast.LENGTH_LONG).show();
						}
						
						public void onComplete(JSONObject object) {
							Toast.makeText(RegisterActivity.this, "Received response!", Toast.LENGTH_LONG).show();
						}

						public Context getContext() {
							// TODO Auto-generated method stub
							return null;
						}
					}).execute(new APIWrapper.RegisterTask.Params(email.getText().toString(), password.getText().toString(), first.getText().toString(), last.getText().toString()));
				} catch (Exception e) {
				}
			}
		});
    }
    
}
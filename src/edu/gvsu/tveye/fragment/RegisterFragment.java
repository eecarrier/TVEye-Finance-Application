package edu.gvsu.tveye.fragment;

import org.json.JSONObject;

import android.app.ActionBar;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import edu.gvsu.tveye.R;
import edu.gvsu.tveye.RegisterActivity;
import edu.gvsu.tveye.api.APIWrapper;
import edu.gvsu.tveye.api.APIWrapper.JSONObjectCallback;
import edu.gvsu.tveye.fragment.LoginFragment.LoginCallback;
import edu.gvsu.tveye.util.TVEyePreferences;

public class RegisterFragment extends DialogFragment {

	private LoginCallback callback;
	private TextView firstName;
	private TextView lastName;
	private TextView email;
	private TextView password;

	public RegisterFragment(LoginCallback callback) {
		this.callback = callback;
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.register, null);
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
		Button register_button = (Button) view.findViewById(R.id.reg_regButton);
		firstName = (TextView) view.findViewById(R.id.reg_first);
		lastName = (TextView) view.findViewById(R.id.reg_last);
		email = (TextView) view.findViewById(R.id.reg_email);
		password = (TextView) view.findViewById(R.id.reg_password);
		register_button.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				try {

					Toast.makeText(getActivity(), "Registering your account",
							Toast.LENGTH_LONG).show();

					new APIWrapper.RegisterTask(new JSONObjectCallback() {
						public void onError(JSONObject object) {
							Toast.makeText(getActivity(), "Received error!",
									Toast.LENGTH_LONG).show();
						}

						public void onComplete(JSONObject object) {
							Toast.makeText(getActivity(),
									"You have Successfully Registered!",
									Toast.LENGTH_LONG).show();
							new TVEyePreferences(getActivity()).setCredentials(
									email.getText().toString(), password
											.getText().toString());
							callback.setCredentials();
							dismiss();
						}

						public Context getContext() {
							// TODO Auto-generated method stub
							return getActivity();
						}
					}).execute(new APIWrapper.RegisterTask.Params(email
							.getText().toString(), password.getText()
							.toString(), firstName.getText().toString(),
							lastName.getText().toString()));
				} catch (Exception e) {
				}
			}
		});
		return view;
	}

}

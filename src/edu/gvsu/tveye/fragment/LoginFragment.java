package edu.gvsu.tveye.fragment;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import edu.gvsu.tveye.R;
import edu.gvsu.tveye.util.TVEyePreferences;

public class LoginFragment extends DialogFragment {

	private LoginCallback callback;

	public LoginFragment(LoginCallback callback) {
		this.callback = callback;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.login, null);
		Button button = (Button) view.findViewById(R.id.login_button);
		button.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				new TVEyePreferences(getActivity()).setCredentials(
						"gzavitz@gmail.com", "motorola");
				callback.setCredentials();
				dismiss();
			}

		});
		return view;
	}

	public static interface LoginCallback {
		public void setCredentials();
	}

}
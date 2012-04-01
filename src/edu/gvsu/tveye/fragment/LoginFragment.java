package edu.gvsu.tveye.fragment;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import edu.gvsu.tveye.R;
import edu.gvsu.tveye.util.TVEyePreferences;
import android.app.FragmentTransaction;

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
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
		button.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				new TVEyePreferences(getActivity()).setCredentials(
						"gzavitz@gmail.com", "motorola");
				callback.setCredentials();
				dismiss();
			}

		});
		
		Button new_account_button = (Button) view.findViewById(R.id.create_account_button);
		new_account_button.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				FragmentTransaction ft = getFragmentManager().beginTransaction();
				ft.addToBackStack(null);
				dismiss();
				new RegisterFragment(callback).show(ft, "register");
				
			}

		});
		return view;
	}

	public static interface LoginCallback {
		public void setCredentials();
	}

}

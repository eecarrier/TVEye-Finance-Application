package edu.gvsu.tveye.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * NewsTileFragment is each individual story seen on the 
 * NewsGridActivity screen.
 * 
 * @author gregzavitz
 */
public class NewsTileFragment extends Fragment {

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
		TextView dummy = new TextView(inflater.getContext());
		dummy.setText("Place holder");
        return dummy;
    }
	
}

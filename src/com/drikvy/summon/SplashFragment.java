package com.drikvy.summon;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SplashFragment extends Fragment {

	
    private static final int SPLASH_DURATION = 2000;
	
    FragmentActivity activity;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	
    	activity = getActivity();
    }
    
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.splash_fragment, container, false);
        return view;
    }
	
	@Override
	public void onResume() {
		super.onResume();
		
		new SplashTimer().execute();
	}
	
	private class SplashTimer extends AsyncTask<String, Integer, Void> {
		Handler handler = new Handler();
		
		Runnable r = new Runnable() {
			@Override
			public void run() {
				//activity.finish();
				
            	//Intent intent = new Intent(activity, MapsActivity.class);
                //activity.startActivity(intent);
			}
		};
		
		@Override
		protected Void doInBackground(String... params) {
			handler.postDelayed(r, SPLASH_DURATION);
			
			return null;
		}
	}
	
}
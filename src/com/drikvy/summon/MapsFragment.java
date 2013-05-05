package com.drikvy.summon;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsFragment extends Fragment{

	GoogleMap map;
	LatLng userCoordinate;
	LocationManager locManager;
	MyLocationListener locListener;
	ArrayList<Marker> markerList = new ArrayList<Marker>();
	
	MainActivity mainActivity;
	Connection connection;
	ChangeListener changeListener = null;
	
	private class MyLocationListener implements LocationListener {
		
		@Override
		public void onLocationChanged(Location location) {
			System.out.println("Location Updated");
			userCoordinate = new LatLng(location.getLatitude(), location.getLongitude());
			map.moveCamera(CameraUpdateFactory.newLatLngZoom(userCoordinate, 14));
			map.animateCamera(CameraUpdateFactory.zoomTo(14), 2000, null);
			
			Toast.makeText(getActivity(), "Update!!!", Toast.LENGTH_SHORT).show();
		}
	
		@Override
		public void onProviderDisabled(String provider) {
			Log.v(getTag(), "onProviderDisabled");
		}
	
		@Override
		public void onProviderEnabled(String provider) {
			Log.v(getTag(), "onProviderEnabled");
		}
	
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			Log.v(getTag(), "onStatusChanged");
		}
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		locListener = new MyLocationListener();
		locManager = (LocationManager) getActivity()
				.getSystemService(Context.LOCATION_SERVICE);
		
		mainActivity = (MainActivity) getActivity();
		new SetUpListener().execute();
	}
	
	public void refreshMarkers() {
		// TODO Auto-generated method stub
		MarkerOptions[] mos = Connections.getInstance()
				.getConnection(mainActivity.listener.clientHandle).event();
		Log.v("MapsFragment", "mos Length: "+mos.length);
		int i=0;
		while(i < mos.length) {
			map.addMarker(mos[i]);
			i++;
		}
	}
	
	protected void showMarkerDialog(Marker marker) {
		EventDetails hotel = new EventDetails((MainActivity) getActivity()
				, marker.getSnippet(), marker.getPosition());
		hotel.setTitle(marker.getTitle());
		hotel.show();
		
		if(marker.getTitle().equalsIgnoreCase("Click me!"))
			marker.remove();
	}

	protected void addMarkerOnMap(LatLng point) {
		Marker marker = map.addMarker(new MarkerOptions()
				.position(point)
				.title("Click me!")
				.snippet("add message here")
				.icon(BitmapDescriptorFactory
						.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
		
		markerList.add(marker);
	}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.maps_fragment, container, false);
        return view;
    }
	
	@Override
	public void onResume() {
		super.onResume();
		
		setUpMapIfNeeded();
		map.setMyLocationEnabled(true);
		
		Criteria criteria = new Criteria();
	    String provider = locManager.getBestProvider(criteria, false);
	    Location location = locManager.getLastKnownLocation(provider);
	    if (location != null) {
	        System.out.println("Provider " + provider + " has been selected.");
	        locListener.onLocationChanged(location);
	    }
	    
	    locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, this.locListener);
	    
	    map.setOnMapClickListener(new OnMapClickListener() {
			@Override
			public void onMapClick(LatLng point) {
				// TODO Auto-generated method stub
				addMarkerOnMap(point);
			}
		});
		
		map.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
			@Override
			public void onInfoWindowClick(Marker marker) {
				// TODO Auto-generated method stub
				showMarkerDialog(marker);
			}
		});
		
	// TODO	
	}
	
	@Override
	public void onPause() {
		super.onPause();
		
		locManager.removeUpdates(locListener);
	}
	
	private void setUpMapIfNeeded() {
        if (map == null) {
            map = ((SupportMapFragment) getActivity()
            		.getSupportFragmentManager().findFragmentById(R.id.frMap)).getMap();
            if (map != null) {
                setUpMap();
            }
        }
    }
	
	private void setUpMap() {
		final View mapView = getActivity()
				.getSupportFragmentManager().findFragmentById(R.id.frMap).getView();
		if (mapView.getViewTreeObserver().isAlive()) {
		    mapView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
		        @Override
		        public void onGlobalLayout() {
		            mapView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
		        }
		    });
		}
	}
	
	private class SetUpListener extends AsyncTask<String, Integer, Void> {

		@Override
		protected Void doInBackground(String... params) {
			
			boolean isRun = true;
			while(isRun) {
				try {
					Thread.sleep(1000);
					if(mainActivity.listener != null) {
						isRun = false;
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			Log.v("Listener", "register LISTENER!");
			/*Toast.makeText(getActivity(), "Connected to "
					+mainActivity.listener.clientHandle, Toast.LENGTH_SHORT).show();*/
			
			connection = Connections.getInstance()
	        		.getConnection(mainActivity.listener.clientHandle);
			if(connection != null) {
				Toast.makeText(getActivity(), "Connected to "
						+mainActivity.listener.clientHandle, Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(getActivity(), "Connection failed!!!", Toast.LENGTH_SHORT).show();
			}
				
			changeListener = new ChangeListener();
			connection.registerChangeListener(changeListener);
		}
		
	}
	
	private class ChangeListener implements PropertyChangeListener {

		/**
		 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
		 */
		@Override
		public void propertyChange(PropertyChangeEvent event) {
			// connection object has change refresh the UI
			// Log.v("MainActivity", "PropertyChangeEvent");
			if (event.getPropertyName().equals(ActivityConstants.eventProperty)) {
				Log.d("MainActivity", "Event Property!!!");
				refreshMarkers();
		    }
		}
	}
}
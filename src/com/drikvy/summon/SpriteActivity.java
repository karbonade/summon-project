package com.drikvy.summon;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

public class SpriteActivity extends FragmentActivity implements OnTouchListener {
	
	Sprite sprite;
	SpriteView spriteView;
	Bitmap bmpLauncher, circleFace;
	float x, y;
	
	GoogleMap map;
	LatLng userCoordinate;
	LocationManager locManager;
	MyLocationListener locListener;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.maps_fragment);
		
		locListener = new MyLocationListener();
		locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		
		setUpMapIfNeeded();
		
		Criteria criteria = new Criteria();
	    String provider = locManager.getBestProvider(criteria, false);
	    Location location = locManager.getLastKnownLocation(provider);
	    if (location != null) {
	        System.out.println("Provider " + provider + " has been selected.");
	        locListener.onLocationChanged(location);
	    }
	    
	    // locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, this.locListener);
		
		bmpLauncher = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
		circleFace = BitmapFactory.decodeResource(getResources(), R.drawable.face_circle_tiled);
		x = y = 0;
		
		spriteView = new SpriteView(this);
		spriteView.setZOrderOnTop(true);    // necessary
		SurfaceHolder sfhTrack = spriteView.getHolder();
		sfhTrack.setFormat(PixelFormat.TRANSPARENT);
		spriteView.setOnTouchListener(this);
		
		addContentView(spriteView, new LayoutParams
				(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		spriteView.resume();
		locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, this.locListener);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		spriteView.pause();
	}
	
	private void setUpMapIfNeeded() {
        if (map == null) {
            map = ((SupportMapFragment) 
            			getSupportFragmentManager().findFragmentById(R.id.frMap)).getMap();
            if (map != null) {
                // setUpMap();
            }
        }
    }
		/*
	private void setUpMap() {
		map.setMyLocationEnabled(true);
		final View mapView = 
				getSupportFragmentManager().findFragmentById(R.id.frMap).getView();
		if (mapView.getViewTreeObserver().isAlive()) {
		    mapView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
		        @Override
		        public void onGlobalLayout() {
		            mapView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
		        }
		    });
		}
	}
	*/
	
	public class SpriteView extends SurfaceView implements Runnable {

		Thread thread = null;
		SurfaceHolder holder;
		boolean isOk = false;
		Paint clearPaint = new Paint();
		
		public SpriteView(Context context) {
			super(context);
			holder = getHolder();
			//sprite = new Sprite(SpriteView.this, circleFace);
			clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
		}
		
		@Override
		public void run() {
			sprite = new Sprite(SpriteView.this, circleFace);
			while(isOk) {
				if(!holder.getSurface().isValid()) {
					continue;
				}
				
				Canvas canvas = holder.lockCanvas();
				onDraw(canvas);
				holder.unlockCanvasAndPost(canvas);
				
			}
		}
		
		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);
			canvas.drawPaint(clearPaint);
			canvas.drawBitmap(bmpLauncher
					, x-(bmpLauncher.getWidth()/2), y-(bmpLauncher.getHeight()/2), null);
			sprite.onDraw(canvas);
		}
		
		public void resume() {
			isOk = true;
			thread = new Thread(this);
			thread.start();
		}
		
		public void pause() {
			isOk = false;
			while(true) {
				try {
					thread.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				break;
			}
			thread = null;
		}
	}

	private class MyLocationListener implements LocationListener {
		
		@Override
		public void onLocationChanged(Location location) {
			System.out.println("Location Updated");
			userCoordinate = new LatLng(location.getLatitude(), location.getLongitude());
			map.moveCamera(CameraUpdateFactory.newLatLngZoom(userCoordinate, 14));
			map.animateCamera(CameraUpdateFactory.zoomTo(14), 2000, null);
			
			Toast.makeText(SpriteActivity.this, "Update!!!", Toast.LENGTH_SHORT).show();
		}
	
		@Override
		public void onProviderDisabled(String provider) {
			
		}
	
		@Override
		public void onProviderEnabled(String provider) {
			
		}
	
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			
		}
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		switch(event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			x = event.getX();
			y = event.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			x = event.getX();
			y = event.getY();
			
			map.moveCamera(CameraUpdateFactory.scrollBy(x, y));
			
			break;
		}
		
		return true;
	}
}
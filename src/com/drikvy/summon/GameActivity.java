package com.drikvy.summon;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;

public class GameActivity extends FragmentActivity implements OnTouchListener {
	
	SpriteView spriteView;
	Bitmap bmpLauncher;
	float x, y;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		spriteView = new SpriteView(this);
		spriteView.setOnTouchListener(this);
		
		bmpLauncher = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
		x = y = 0;
		
		setContentView(spriteView);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		spriteView.resume();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		spriteView.pause();
	}
	
		
	public class SpriteView extends SurfaceView implements Runnable {

		Thread thread = null;
		SurfaceHolder holder;
		boolean isOk = false;
		Paint clearPaint = new Paint();
		
		public SpriteView(Context context) {
			super(context);
			holder = getHolder();
			clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
		}
		
		@Override
		public void run() {
			while(isOk) {
				if(!holder.getSurface().isValid()) {
					continue;
				}
				
				Canvas canvas = holder.lockCanvas();
				canvas.drawPaint(clearPaint);
				canvas.drawBitmap(bmpLauncher
						, x-(bmpLauncher.getWidth()/2), y-(bmpLauncher.getHeight()/2), null);
				holder.unlockCanvasAndPost(canvas);
			}
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
			break;
		}
		
		return true;
	}
}
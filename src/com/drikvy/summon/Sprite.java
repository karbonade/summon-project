package com.drikvy.summon;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

import com.drikvy.summon.SpriteActivity.SpriteView;

public class Sprite {

	int x, y;
	int xSpeed, ySpeed;
	int height, width;
	Bitmap bitmap;
	SpriteView view;
	
	int currentFrame = 0;
	int direction = 0;
	
	public Sprite(SpriteView spriteView, Bitmap circleFace) {
		bitmap = circleFace;
		view = spriteView;
		// 1 row
		height = circleFace.getHeight();
		// 2 column
		width = circleFace.getWidth()/2;
		x = y = 0;
		xSpeed = 5;
		ySpeed = 0;
	}

	public void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		update();
		int srcX = currentFrame * width;
		Rect src = new Rect(srcX, 0, srcX + width, height);// cutoff the image
		Rect dst = new Rect(x, y, x+width, y+height);// this one to scaled
		canvas.drawBitmap(bitmap, src, dst, null);
	}

	private void update() {
		if(x > view.getWidth() - width - xSpeed) {
			xSpeed = 0;
			ySpeed = 5;
		}
		if(y > view.getHeight() - height - ySpeed) {
			xSpeed = -5;
			ySpeed = 0;
		}
		if(x + xSpeed < 0) {
			x = 0;
			xSpeed = 0;
			ySpeed = -5;
		}
		if(y + ySpeed < 0) {
			y = 0;
			xSpeed = 5;
			ySpeed = 0;
		}
		
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		currentFrame = ++currentFrame % 2;
		
		x += xSpeed;
		y += ySpeed;
		
		
	}

}

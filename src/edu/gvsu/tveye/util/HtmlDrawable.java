package edu.gvsu.tveye.util;

import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class HtmlDrawable extends BitmapDrawable{
	
	protected Drawable d;
	
	@Override
	public void draw(Canvas canv) {
		if(d != null) {
			d.draw(canv);
		}
	}
}

package edu.gvsu.tveye.view;

import java.util.Stack;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import edu.gvsu.tveye.R;

public class DropDown extends TextView {
	
	public int defaultBackground, errorBackground, messageBackground;
	private Animation slideDown, slideUp, fadeIn, fadeOut;
	private Stack<Message> messages = new Stack<Message>();

	public DropDown(Context context) {
		this(context, null);
	}
	
	public DropDown(Context context, AttributeSet attr) {
		this(context, attr, R.style.DropDown);
	}
	
	public DropDown(Context context, AttributeSet attr, int defStyle) {
		super(context, attr, defStyle);
		slideDown = AnimationUtils.loadAnimation(context, R.anim.slide_down);
		slideUp = AnimationUtils.loadAnimation(context, R.anim.slide_up);
		fadeIn = AnimationUtils.loadAnimation(context, R.anim.fade_in);
		fadeIn.setDuration(500);
		fadeOut = AnimationUtils.loadAnimation(context, R.anim.fade_out);
		fadeOut.setDuration(500);
		defaultBackground = getResources().getColor(R.color.dropdown_default);
		errorBackground = getResources().getColor(R.color.dropdown_error);
		messageBackground = getResources().getColor(R.color.dropdown_message);
	}
	
	public void showMessage(String text, int backgroundColor) {
		final Message message = new Message(text, backgroundColor);
		if(getVisibility() == View.VISIBLE) {
			Drawable[] layers = new Drawable[] {
				new ColorDrawable(messages.peek().backgroundColor),
				new ColorDrawable(backgroundColor)
			};
			TransitionDrawable drawable = new TransitionDrawable(layers);
			setBackgroundDrawable(drawable);
			drawable.startTransition(1000);
			setText("");
			new Handler().postDelayed(new Runnable() {
				public void run() {
					setText(message.text);
				}
			}, 1000);
		} else {
			setBackgroundColor(message.backgroundColor);
			setText(message.text);
		}
		messages.push(message);
		
		
		if(getVisibility() != View.VISIBLE) {
			setVisibility(View.VISIBLE);
			startAnimation(slideDown);
		}
	}
	
	public void showMessage(String message, int backgroundColor, int hide_after) {
		showMessage(message, backgroundColor);
		new Handler().postDelayed(new Runnable() {
			public void run() {
				dismiss();
			}
		}, hide_after);
	}
	
	public void dismiss() {
		if(messages.size() > 1) {
			Message popped = messages.pop();
			final Message message = messages.peek();
			Drawable[] layers = new Drawable[] {
				new ColorDrawable(popped.backgroundColor),
				new ColorDrawable(message.backgroundColor)
			};
			TransitionDrawable drawable = new TransitionDrawable(layers);
			setBackgroundDrawable(drawable);
			drawable.startTransition(1000);
			setText("");
			new Handler().postDelayed(new Runnable() {
				public void run() {
					setText(message.text);
				}
			}, 1000);
		} else {
			if(getVisibility() == View.VISIBLE) {
				startAnimation(slideUp);
				setVisibility(View.GONE);
			}
		}
	}
	
	private static class Message {
		
		CharSequence text;
		int backgroundColor;
		
		public Message(CharSequence text, int backgroundColor) {
			this.text = text;
			this.backgroundColor = backgroundColor;
		}
		
	}

}

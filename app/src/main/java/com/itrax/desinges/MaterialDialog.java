package com.itrax.desinges;

import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.itrax.R;


public class MaterialDialog extends android.app.Dialog{

	Context context;
	View view;
	View backView;
	String message;
	TextView messageTextView;
	String title;
	TextView titleTextView;

	ButtonFlat buttonAccept;
	ButtonFlat buttonCancel;

	String buttonCancelText,buttonAcceptText;

	View.OnClickListener onAcceptButtonClickListener;
	View.OnClickListener onCancelButtonClickListener;


	public MaterialDialog(Context context, String title, String message) {
		super(context, android.R.style.Theme_Translucent);
		this.context = context;// init Context
		this.message = message;
		this.title = title;
	}

	public void setCancelButton(String buttonCancelText){
		this.buttonCancelText = buttonCancelText;
	}
	public void setAcceptButton(String buttonText){
		this.buttonAcceptText = buttonText;
	}

	public void addCancelButton(String buttonCancelText, View.OnClickListener onCancelButtonClickListener){
		this.buttonCancelText = buttonCancelText;
		this.onCancelButtonClickListener = onCancelButtonClickListener;
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_default_popup);

		view     = findViewById(R.id.contentDialog);
		backView = findViewById(R.id.dialog_rootView);
		backView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getX() < view.getLeft() 
						|| event.getX() >view.getRight()
						|| event.getY() > view.getBottom() 
						|| event.getY() < view.getTop()) {
					dismiss();
				}
				return false;
			}
		});

		this.titleTextView = (TextView) findViewById(R.id.title);
		setTitle(title);

		this.messageTextView = (TextView) findViewById(R.id.message);
		setMessage(message);

		this.buttonAccept = (ButtonFlat) findViewById(R.id.button_accept);

		if(buttonAcceptText!=null&&!buttonAcceptText.equals(""))
		{
			this.buttonAccept.setVisibility(View.VISIBLE);
			this.buttonAccept.setText(buttonAcceptText);
		}else
		{
			this.buttonAccept.setVisibility(View.GONE);
		}
		buttonAccept.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v) {
				dismiss();
				if(onAcceptButtonClickListener != null)
					onAcceptButtonClickListener.onClick(v);
			}
		});

		if(buttonCancelText != null)
		{
			this.buttonCancel = (ButtonFlat) findViewById(R.id.button_cancel);
			if(buttonCancelText.equals(""))
			{
				this.buttonCancel.setVisibility(View.GONE);
			}else
			{
				this.buttonCancel.setVisibility(View.VISIBLE);
				this.buttonCancel.setText(buttonCancelText);
				buttonCancel.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						dismiss();	
						if(onCancelButtonClickListener != null)
							onCancelButtonClickListener.onClick(v);
					}
				});

			}


		}
	}

	@Override
	public void show() {
		super.show();
		// set dialog enter animations
		view.startAnimation(AnimationUtils.loadAnimation(context, R.anim.dialog_main_show_amination));
	}

	// GETERS & SETTERS

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
		messageTextView.setText(message);
	}

	public TextView getMessageTextView() {
		return messageTextView;
	}

	public void setMessageTextView(TextView messageTextView) {
		this.messageTextView = messageTextView;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
		if(title == null)
			titleTextView.setVisibility(View.GONE);
		else{
			titleTextView.setVisibility(View.VISIBLE);
			titleTextView.setText(title);
		}
	}

	public TextView getTitleTextView() {
		return titleTextView;
	}

	public void setTitleTextView(TextView titleTextView) {
		this.titleTextView = titleTextView;
	}

	public ButtonFlat getButtonAccept() {
		return buttonAccept;
	}

	public void setButtonAccept(ButtonFlat buttonAccept) {
		this.buttonAccept = buttonAccept;
	}

	public ButtonFlat getButtonCancel() {
		return buttonCancel;
	}

	public void setButtonCancel(ButtonFlat buttonCancel) {
		this.buttonCancel = buttonCancel;
	}

	public void setOnAcceptButtonClickListener(
			View.OnClickListener onAcceptButtonClickListener) {
		this.onAcceptButtonClickListener = onAcceptButtonClickListener;
		if(buttonAccept != null)
			buttonAccept.setOnClickListener(onAcceptButtonClickListener);
	}

	public void setOnCancelButtonClickListener(
			View.OnClickListener onCancelButtonClickListener) {
		this.onCancelButtonClickListener = onCancelButtonClickListener;
		if(buttonCancel != null)
			buttonCancel.setOnClickListener(onCancelButtonClickListener);
	}

	@Override
	public void dismiss() {
		

		view.post(new Runnable() {
			@Override
			public void run() {
				MaterialDialog.super.dismiss();
			}
		});

	

/*
		Animation anim = AnimationUtils.loadAnimation(context, R.anim.abc_slide_out_bottom);
		anim.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {}
		});

		view.startAnimation(anim);*/

	}



}
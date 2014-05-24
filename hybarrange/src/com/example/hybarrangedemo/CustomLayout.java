package com.example.hybarrangedemo;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class CustomLayout extends LinearLayout {

	public CustomLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public CustomLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public CustomLayout(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean hasFocusable() {
	    return false;
	}
}

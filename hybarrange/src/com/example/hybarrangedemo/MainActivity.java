package com.example.hybarrangedemo;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.AbsListView;
import android.widget.ListView;

public class MainActivity extends Activity {
	protected static final String STATE_PAUSE_ON_SCROLL = "STATE_PAUSE_ON_SCROLL";
	protected static final String STATE_PAUSE_ON_FLING = "STATE_PAUSE_ON_FLING";

	protected AbsListView mListView;

	// 列表滚动时加载图片，fling 时暂停加载。
	protected boolean mPauseOnScroll = false;
	protected boolean mPauseOnFling = true;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		getActionBar().setBackgroundDrawable(new ColorDrawable(0xff5991e5));
		
		mListView = (ListView) findViewById(android.R.id.list);
		((ListView) mListView).setAdapter(new RichTextAdapter(this));
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		mPauseOnScroll = savedInstanceState.getBoolean(STATE_PAUSE_ON_SCROLL, false);
		mPauseOnFling = savedInstanceState.getBoolean(STATE_PAUSE_ON_FLING, true);
	}

	@Override
	public void onResume() {
		super.onResume();
		applyScrollListener();
	}

	private void applyScrollListener() {
		mListView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), mPauseOnScroll, mPauseOnFling));
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putBoolean(STATE_PAUSE_ON_SCROLL, mPauseOnScroll);
		outState.putBoolean(STATE_PAUSE_ON_FLING, mPauseOnFling);
	}
}

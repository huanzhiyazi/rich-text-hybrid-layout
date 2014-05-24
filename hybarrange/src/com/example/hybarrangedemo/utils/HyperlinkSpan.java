package com.example.hybarrangedemo.utils;

import com.example.hybarrangedemo.HybArrangeApp;

import android.view.View;
import android.widget.Toast;

/**
 * 超链接 span。
 * @author Whiz
 *
 */
public class HyperlinkSpan extends WeburlSpan {

	public HyperlinkSpan(String url) {
		super(url);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onClick(View widget) {
		// 实现超链接的点击事件，这里仅仅进行 toast 测试。
		if (HybArrangeApp.TEST) {
			Toast.makeText(widget.getContext(), "I am a hyperlink: " + mUrl, Toast.LENGTH_SHORT).show();
		} else {
			// do something special to hype link span
		}
	}
}

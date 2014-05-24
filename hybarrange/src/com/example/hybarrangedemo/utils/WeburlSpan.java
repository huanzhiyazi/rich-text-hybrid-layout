package com.example.hybarrangedemo.utils;

import com.example.hybarrangedemo.HybArrangeApp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Toast;
import android.provider.Browser;

/**
 * Web url span。
 * @author Whiz
 *
 */
public class WeburlSpan extends ClickableSpan {
	private static final int LINK_COLOR = 0XFF6C8AA1;
	protected final String mUrl;
	
	public WeburlSpan(String url) {
		mUrl = url;
	}
	
	public String getUrl() {
		return mUrl;
	}

	@Override
	public void onClick(View widget) {
		// TODO Auto-generated method stub
		// 实现 Web url 的点击事件，这里仅仅进行 toast 测试。
		if (HybArrangeApp.TEST) {
			Toast.makeText(widget.getContext(), "I am a pure web url: " + mUrl, Toast.LENGTH_SHORT).show();
		} else {
			Uri uri = Uri.parse(mUrl);
	        Context context = widget.getContext();
	        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
	        intent.putExtra(Browser.EXTRA_APPLICATION_ID, context.getPackageName());
	        context.startActivity(intent);
		}
	}

	@Override
	public void updateDrawState(TextPaint ds) {
		ds.setColor(LINK_COLOR);
		ds.setUnderlineText(false);
	}
}

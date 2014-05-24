package com.example.hybarrangedemo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.example.hybarrangedemo.utils.HyperlinkParser;
import com.example.hybarrangedemo.utils.IParser;
import com.example.hybarrangedemo.utils.ImageParser;
import com.example.hybarrangedemo.utils.MatchInfo.MatchType;
import com.example.hybarrangedemo.utils.ParseManager;
import com.example.hybarrangedemo.utils.ParseManager.ParsedSegment;
import com.example.hybarrangedemo.utils.SmileyParser;
import com.example.hybarrangedemo.utils.WeburlParser;
import com.example.hybarrangedemo.utils.WeburlSpan;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.BackgroundColorSpan;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class RichTextAdapter extends BaseAdapter {
	private ImageLoadingListener mAnimateFirstListener = new AnimateFirstDisplayListener();
	private ImageLoader mImageLoader = ImageLoader.getInstance();
	DisplayImageOptions mOptions;
	private final Context mContext;
	
	public RichTextAdapter(Context context) {
		mContext = context;
		
		// 初始化图片加载选项。
		mOptions = new DisplayImageOptions.Builder()
							.showImageOnLoading(R.drawable.ic_stub)
							.showImageForEmptyUri(R.drawable.ic_empty)
							.showImageOnFail(R.drawable.ic_error)
							.cacheInMemory(true)
							.cacheOnDisk(true)
							.considerExifParams(true)
							.displayer(new FadeInBitmapDisplayer(20))
							.build();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return Constants.RICH_TEXTS.length;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		String rich = Constants.RICH_TEXTS[position];
		
		// 通过迭代装饰方式构造解析器。
		IParser parser = new SmileyParser(mContext);
		parser = new ImageParser(mContext, parser);
		parser = new HyperlinkParser(mContext, parser);
		parser = new WeburlParser(mContext, parser);
		
		// 执行解析并返回解析文本段队列。
		ParseManager manager = new ParseManager();
		ArrayList<ParsedSegment> segments = manager.parse(parser, rich);
		
		// 用 Holder 模式更新列表数据。
		CustomLayout layout = null;
		ViewHolder holder = null;
		if (null == convertView) {
			layout = new CustomLayout(mContext);
			AbsListView.LayoutParams params = new AbsListView.LayoutParams(
					AbsListView.LayoutParams.MATCH_PARENT, 
					AbsListView.LayoutParams.WRAP_CONTENT);
			layout.setLayoutParams(params);
			layout.setOrientation(LinearLayout.VERTICAL);
			layout.setBackgroundResource(R.drawable.list_item_background);
			
			holder = new ViewHolder();
			holder.views = new ArrayList<View>();
			layout.setTag(holder);
		} else {
			layout = (CustomLayout) convertView;
			holder = (ViewHolder) convertView.getTag();
		}
		
		int i = 0;
		for (ParsedSegment segment : segments) {
			if (TextUtils.isEmpty(segment.text)) {
				continue;
			}
			
			if (MatchType.None == segment.type) {
				TextView textView = null;
				if (null == convertView) {
					textView = new TextView(mContext);
					LayoutParams p = new LayoutParams(
							LayoutParams.MATCH_PARENT, 
							LayoutParams.WRAP_CONTENT);
					p.bottomMargin = mContext.getResources().getDimensionPixelSize(R.dimen.rich_element_margin);
					textView.setLayoutParams(p);
					textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
					
					// 自定义 ClickableSpan 点击背景，有两种方法：
					// 1、自定义 LinkMovementMethod，重写其 onTouchEvent 方法；
					// 2、重写 TextView 的 OnTouchListener。
					// 两种方法的核心思想都是获取 touch event 事件，通过对 TextView 中的 ClickableSpan
					// 文本设置和移除 BackgroundColorSpan 来改变其点击背景。
					if (Constants.USE_CUSTOM_LINK) {
						textView.setMovementMethod(mLinkMovementMethod);
					} else {
						textView.setMovementMethod(LinkMovementMethod.getInstance());
						textView.setOnTouchListener(onTouchListener);
					}
					
					textView.setClickable(false);
					
					layout.addView(textView);
					holder.views.add(textView);
				} else {
					textView = (TextView) holder.views.get(i);
				}
				
				textView.setText(segment.text);
				
				++i;
				
			} else if (MatchType.Image == segment.type) {
				ImageView imgView = null;
				if (null == convertView) {
					imgView = new ImageView(mContext);
					LayoutParams p = new LayoutParams(
							LayoutParams.MATCH_PARENT, 
							LayoutParams.WRAP_CONTENT);
					p.bottomMargin = mContext.getResources().getDimensionPixelSize(R.dimen.rich_element_margin);
					imgView.setMaxHeight(mContext.getResources().getDimensionPixelSize(R.dimen.image_max_height));
					imgView.setLayoutParams(p);
					
					layout.addView(imgView);
					holder.views.add(imgView);
				} else {
					imgView = (ImageView) holder.views.get(i);
				}
				
				// 异步加载图片。
				mImageLoader.displayImage(segment.text.toString(), imgView, mOptions, mAnimateFirstListener);
				
				++i;
			}
		}
		
		return layout;
	}
	
	private static class ViewHolder {
		ArrayList<View> views;
	}
	
	private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

		static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

		@Override
		public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
			if (loadedImage != null) {
				ImageView imageView = (ImageView) view;
				boolean firstDisplay = !displayedImages.contains(imageUri);
				if (firstDisplay) {
					FadeInBitmapDisplayer.animate(imageView, 500);
					displayedImages.add(imageUri);
				}
			}
		}
	}
	
	private LinkMovementMethod mLinkMovementMethod = new LinkMovementMethod() {
		@Override
	    public boolean onTouchEvent(TextView widget, Spannable buffer,
	                                MotionEvent event) {
	        int action = event.getAction();

	        if (action == MotionEvent.ACTION_UP ||
	            action == MotionEvent.ACTION_DOWN ||
	            action == MotionEvent.ACTION_CANCEL) {
	            int x = (int) event.getX();
	            int y = (int) event.getY();

	            x -= widget.getTotalPaddingLeft();
	            y -= widget.getTotalPaddingTop();

	            x += widget.getScrollX();
	            y += widget.getScrollY();

	            Layout layout = widget.getLayout();
	            int line = layout.getLineForVertical(y);
	            int off = layout.getOffsetForHorizontal(line, x);

	            ClickableSpan[] link = buffer.getSpans(off, off, ClickableSpan.class);
	            if (link.length != 0) {
	                if (action == MotionEvent.ACTION_UP) {
	                	// 按下后松开，移除所有 BackgroundColorSpan。
	                	BackgroundColorSpan[] backgroundColorSpans = buffer
	                            .getSpans(0, buffer.length(), BackgroundColorSpan.class);
	                	for (BackgroundColorSpan bkcolor : backgroundColorSpans) {
	                		buffer.removeSpan(bkcolor);
                        }
	                	
	                	//Selection.removeSelection(buffer);
	                    link[0].onClick(widget);
	                } else if (action == MotionEvent.ACTION_DOWN) {
	                	// 按下，给按下的 ClickableSpan 设置 BackgroundColorSpan。
	                	/*
	                    Selection.setSelection(buffer,
	                                           buffer.getSpanStart(link[0]),
	                                           buffer.getSpanEnd(link[0]));
	                    */
	                    
	                    BackgroundColorSpan bkcolor = new BackgroundColorSpan(0xff89660f);
	                    buffer.setSpan(bkcolor, 
	                    		buffer.getSpanStart(link[0]), 
	                    		buffer.getSpanEnd(link[0]),
                                Spanned.SPAN_INCLUSIVE_INCLUSIVE);
	                } else if (action == MotionEvent.ACTION_CANCEL) {
	                	// 按下不松开而是移动，则变成取消事件，移除所有 BackgroundColorSpan。
	                	BackgroundColorSpan[] backgroundColorSpans = buffer
	                            .getSpans(0, buffer.length(), BackgroundColorSpan.class);
	                	for (BackgroundColorSpan bkcolor : backgroundColorSpans) {
	                		buffer.removeSpan(bkcolor);
                        }
	                }

	                return true;
	            } else {
	            	BackgroundColorSpan[] backgroundColorSpans = buffer
                            .getSpans(0, buffer.length(), BackgroundColorSpan.class);
                	for (BackgroundColorSpan bkcolor : backgroundColorSpans) {
                		buffer.removeSpan(bkcolor);
                    }
	                //Selection.removeSelection(buffer);
	            }
	        }

	        return super.onTouchEvent(widget, buffer, event);
	    }
	};
	
	private View.OnTouchListener onTouchListener = new View.OnTouchListener() {


        @Override
        public boolean onTouch(View v, MotionEvent event) {

            Layout layout = ((TextView) v).getLayout();

            int x = (int) event.getX();
            int y = (int) event.getY();
            int offset = 0;
            if (layout != null) {

                int line = layout.getLineForVertical(y);
                offset = layout.getOffsetForHorizontal(line, x);
            }

            TextView tv = (TextView) v;
            SpannableString value = SpannableString.valueOf(tv.getText());

            LinkMovementMethod.getInstance().onTouchEvent(tv, value, event);

            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    WeburlSpan[] urlSpans = value.getSpans(0, value.length(), WeburlSpan.class);
                    boolean find = false;
                    int findStart = 0;
                    int findEnd = 0;
                    for (WeburlSpan urlSpan : urlSpans) {
                        int start = value.getSpanStart(urlSpan);
                        int end = value.getSpanEnd(urlSpan);
                        if (start <= offset && offset <= end) {
                            find = true;
                            findStart = start;
                            findEnd = end;

                            break;
                        }
                    }

                    if (find) {
                        BackgroundColorSpan bkcolor = new BackgroundColorSpan(0xff89660f);
                        value.setSpan(bkcolor, findStart, findEnd,
                                Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                        ((TextView) v).setText(value);
                    }

                    return find;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    BackgroundColorSpan[] backgroundColorSpans = value
                            .getSpans(0, value.length(), BackgroundColorSpan.class);
                    for (BackgroundColorSpan bkcolor : backgroundColorSpans) {
                        value.removeSpan(bkcolor);
                        ((TextView) v).setText(value);
                    }
                    break;
            }

            return false;

        }
    };

}

package com.example.hybarrangedemo.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.example.hybarrangedemo.utils.MatchInfo.MatchType;

import android.content.Context;
import android.util.Log;

/**
 * 图片解析器，需要被分割处理。
 * @author Whiz
 *
 */
public class ImageParser extends IParser {
	private final Pattern mPattern;
	
	public ImageParser(Context context, IParser decor) {
		super(context, decor);
		// TODO Auto-generated constructor stub
		mPattern = Patterns.getInstance().mImagePattern;
		
		// 需要被分割处理。
		mNeedSplit = true;
	}

	public ImageParser(Context context) {
		this(context, null);
	}
	
	/**
	 * 通过图片正则表达式匹配输入文本中所有的图片。
	 * 
	 * @param text 输入文本，其中可能含有需要解析的图片。
	 */
	@Override
    public CharSequence parse(CharSequence text) {
		// 先由其装饰解析器解析出其它必要的富文本。
		CharSequence toParse = super.parse(text);
		
		// 逐段匹配输入文本中所有的图片，并存储在富文本信息队列中。
		Matcher matcher = mPattern.matcher(toParse);
        while (matcher.find()) {
        	// 检查是否有冲突。
        	checkAreaLegitimacy(toParse, MatchType.Image, matcher.start(), matcher.end());
        	
        	String fmt = matcher.group();
        	int valuestart = 5; 				// remove head "<img="
        	int valueend = fmt.length() - 7;	// remove tail "></img>"
        	
        	// 图片匹配信息中无需构造 span，需要分割处理。
        	mMatchInfos.add(new MatchInfo(
        			this, matcher.start(), matcher.end(), 
        			MatchType.Image, 
        			fmt.substring(valuestart, valueend),
        			null));
        }
        
        return toParse;
	}
}

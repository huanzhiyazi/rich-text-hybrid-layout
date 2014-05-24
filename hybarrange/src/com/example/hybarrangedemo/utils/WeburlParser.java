package com.example.hybarrangedemo.utils;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.example.hybarrangedemo.utils.MatchInfo.MatchType;

import android.content.Context;
import android.util.Log;

/**
 * Web url 解析器。解析 Web url 时需要过滤掉所有类 Web url 富文本，以避免重复解析，类  Web url 包括：超链接、图片。
 * Web url 是存粹的 url，不包括如图片和超链接那样的前后缀说明。
 * @author Whiz
 *
 */
public class WeburlParser extends IParser {
	private final Pattern mPattern;

	public WeburlParser(Context context, IParser decor) {
		super(context, decor);
		// TODO Auto-generated constructor stub
		
		// 获取 Web url 正则解析器。
		mPattern = Patterns.getInstance().mWeburlPattern;
	}

	public WeburlParser(Context context) {
		this(context, null);
	}
	
	/**
	 * 通过 Web url 正则表达式匹配输入文本中的所有 Web url。同时过滤掉类 Web url 富文本。
	 * 
	 * @param text 输入文本，其中可能含有需要被解析的 Web url。
	 */
	@Override
    public CharSequence parse(CharSequence text) {
		// 先由其装饰解析器解析出其它必要的富文本。
    	CharSequence toParse = super.parse(text);
    	
    	// 遍历所有的装饰解析器，如果其中含有某一类类 Web url 解析器，说明该类 Web url 已经被解析过，且存储在
    	// 富文本信息队列中，解析 Web url 时，只需从富文本信息队列中过滤掉该类类 Web url。
    	boolean imageParsed = false, hyperlinkParsed = false;
    	IParser decor = mDecorParser;
    	while (decor != null) {
    		if (decor instanceof ImageParser) {
    			imageParsed = true;
    		} else if (decor instanceof HyperlinkParser) {
    			hyperlinkParsed = true;
    		}
    		
    		if (imageParsed && hyperlinkParsed) break;
    		
    		decor = decor.mDecorParser;
    	}
    	
    	// 如果仍然有类 Web url 富文本未被解析过，则解析它们，并将其存储在剩余类 Web url 过滤队列，
    	// 当解析 Web url 时，需要同时从类 Web url 队列和富文本信息队列中过滤所有类 Web url。
    	ArrayList<MatchInfo> filters = new ArrayList<MatchInfo>();
    	fillFilters(filters, toParse, imageParsed, hyperlinkParsed);
    	
    	Matcher matcher = mPattern.matcher(toParse);
    	while (matcher.find()) {
    		// 从类 Web url 队列和富文本信息队列中过滤所有类 Web url。
    		if (filtered(filters, matcher.start(), matcher.end()) ||
    				filtered(mMatchInfos, matcher.start(), matcher.end())) {
    			continue;
    		}
    		
    		// 检查是否有冲突。
    		checkAreaLegitimacy(toParse, MatchType.Weburl, matcher.start(), matcher.end());
    		
    		// 将解析到的合法 Web url 存入富文本信息队列。 
    		String value = matcher.group();
        	mMatchInfos.add(new MatchInfo(
        			this, matcher.start(), matcher.end(), 
        			MatchType.Weburl, 
        			value,
        			new WeburlSpan(value)));
    	}
    	
    	return toParse;
	}
	
	/**
	 * 填充类 Web url 过滤队列。
	 * @param filters 类 Web url 过滤队列。
	 * @param toParse 需要被解析的输入文本。
	 * @param imageParsed 图片是否被解析过。
	 * @param hyperlinkParsed 超链接是否被解析过。
	 */
	private void fillFilters(ArrayList<MatchInfo> filters, CharSequence toParse,
			boolean imageParsed, boolean hyperlinkParsed) {
		if (!imageParsed) {
    		Pattern image = Patterns.getInstance().mImagePattern;
    		Matcher matcher = image.matcher(toParse);
            while (matcher.find()) {
            	checkAreaLegitimacy(toParse, MatchType.Image, matcher.start(), matcher.end());
            	
            	// just for filter, there is no need to compute the value and span.
            	filters.add(new MatchInfo(
            			this, matcher.start(), matcher.end(), 
            			MatchType.Image, 
            			null,
            			null));
            }
    	}
    	
    	if (!hyperlinkParsed) {
    		Pattern hyperlink = Patterns.getInstance().mHyperlinkPattern;
    		Matcher matcher = hyperlink.matcher(toParse);
            while (matcher.find()) {
            	checkAreaLegitimacy(toParse, MatchType.Hyperlink, matcher.start(), matcher.end());
            	
            	// just for filter, there is no need to compute the value and span.
            	filters.add(new MatchInfo(
            			this, matcher.start(), matcher.end(), 
            			MatchType.Hyperlink, 
            			null,
            			null));
            }
    	}
	}
	
	/**
	 * 判断被解析出的 Web url 是否在过滤队列和富文本解析队列中的类 Web url 范围中。
	 * @param filters 过滤队列或富文本解析队列。
	 * @param start 被解析出的 Web url 的起始位置。
	 * @param end 被解析出的 Web url 的终点位置的下一个位置。
	 * @return 如果 Web url 是类 Web url 的子集则返回 true，否则返回 false。
	 */
	private boolean filtered(ArrayList<MatchInfo> filters, int start, int end) {
		for (MatchInfo info : filters) {
			MatchType type = info.getMatchType();
			if (MatchType.Image == type || MatchType.Hyperlink == type) {
				int fstart = info.getStart();
				int fend = info.getEnd();
				if (start >= fstart && end <= fend) return true;
			}
		}
		
		return false;
	}
}

package com.example.hybarrangedemo.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.example.hybarrangedemo.utils.MatchInfo.MatchType;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.util.Log;

/**
 * 超链接解析器。
 * @author Whiz
 *
 */
public class HyperlinkParser extends IParser {
	private final Pattern mPattern;

	public HyperlinkParser(Context context, IParser decor) {
		super(context, decor);
		// TODO Auto-generated constructor stub
		// 获取超链接正则解析器。
		mPattern = Patterns.getInstance().mHyperlinkPattern;
	}

	public HyperlinkParser(Context context) {
		this(context, null);
	}
	
	/**
	 * 通过超链接正则表达式匹配输入文本中的所有超链接。
	 * 
	 * @param text 输入文本，其中可能含有需要被解析的超链接。
	 */
	@Override
    public CharSequence parse(CharSequence text) {
		// 先由其装饰解析器解析出其它必要的富文本。
		CharSequence toParse = super.parse(text);

		// 逐段匹配输入文本中所有的超链接，并存储在富文本信息队列中。
        Matcher matcher = mPattern.matcher(toParse);
        while (matcher.find()) {
        	// 检查是否有冲突。
        	checkAreaLegitimacy(toParse, MatchType.Hyperlink, matcher.start(), matcher.end());
        	
        	String fmt = matcher.group();
        	int urltart = 5; 				// remove head "<url="
        	int urlend = fmt.indexOf(">");	// remove tail ">.*</url>"
        	String url = fmt.substring(urltart, urlend);
        	
        	int valuestart = fmt.indexOf(">") + 1;		// remove head "<url=...>"
        	int valueend = fmt.indexOf("</url>");		// remove tail "</url>"
        	String value = fmt.substring(valuestart, valueend);
        	
        	mMatchInfos.add(new MatchInfo(
        			this, matcher.start(), matcher.end(),
        			MatchType.Hyperlink,
        			value,
        			new HyperlinkSpan(url)));
        }
        
        return toParse;
	}
}

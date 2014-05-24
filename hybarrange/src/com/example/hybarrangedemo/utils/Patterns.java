package com.example.hybarrangedemo.utils;

import java.util.regex.Pattern;

import com.example.hybarrangedemo.R;

import android.content.Context;

/**
 * 富文本正则表达式解析器构造类。
 * @author Whiz
 *
 */
public class Patterns {
	private static Patterns sInstance;
	static Patterns getInstance() { return sInstance; }
	public static void init(Context context) {
		sInstance = new Patterns(context);
	}
	
	private Patterns(Context context) {
		mWeburlPattern = Pattern.compile(WEBURL_REG);
		mImagePattern = Pattern.compile(IMAGE_REG);
		mHyperlinkPattern = Pattern.compile(HYPERLINK_REG);
		mSmileyPattern = buildSmileyPattern(context);
	}
	
	public final Pattern mWeburlPattern;
	public final Pattern mImagePattern;
	public final Pattern mHyperlinkPattern;
	public final Pattern mSmileyPattern;
	
	private static final String WEBURL_REG = getUrlReg();
	private static final String IMAGE_REG = getImageReg();
	private static final String HYPERLINK_REG = getHyperlinkReg();

	// 构造 web url 正则式。
	private static String getUrlReg() {
		StringBuilder builder = new StringBuilder();
		
		return
		builder.append("((https|http|ftp|rtsp|mms)?://)")
			   .append("?(([0-9a-zA-Z_!~*'().&=+$%-]+: )?[0-9a-zA-Z_!~*'().&=+$%-]+@)?")
			   .append("(([0-9]{1,3}\\.){3}[0-9]{1,3}")
			   .append("|")
			   .append("([0-9a-zA-Z_!~*'()-]+\\.)*")
			   .append("([0-9a-zA-Z][0-9a-zA-Z-]{0,61})?[0-9a-zA-Z]\\.")
			   .append("[a-zA-Z]{2,6})")
			   .append("(:[0-9]{1,4})?")
			   .append("(((/[0-9a-zA-Z_!~*'().;?:@&=+$,%#-]+)+/?)|")
			   .append("(/?))")
			   .toString();
	}
	
	// 构造图片正则式
	private static String getImageReg() {
		StringBuilder builder = new StringBuilder();
		
		return
		builder.append("<img=")
			   .append(WEBURL_REG)
			   .append("></img>")
			   .toString();
	}
	
	// 投资超链接正则式。
	private static String getHyperlinkReg() {
		StringBuilder builder = new StringBuilder();
		
		return
		builder.append("<url=")
			   .append(WEBURL_REG)
			   .append(">(.*?)</url>")
			   .toString();
	}
	
	// 构造表情正则式。
	private Pattern buildSmileyPattern(Context context) {
		String[] smileyTexts = context.getResources().getStringArray(
				R.array.default_smiley_texts);
		// Set the StringBuilder capacity with the assumption that the average
        // smiley is 3 characters long.
        StringBuilder patternString = new StringBuilder(smileyTexts.length * 3);

        // Build a regex that looks like (:-)|:-(|...), but escaping the smilies
        // properly so they will be interpreted literally by the regex matcher.
        patternString.append('(');
        for (String s : smileyTexts) {
            patternString.append(Pattern.quote(s));
            patternString.append('|');
        }
        // Replace the extra '|' with a ')'
        patternString.replace(patternString.length() - 1, patternString.length(), ")");

        return Pattern.compile(patternString.toString());
	}
	
}

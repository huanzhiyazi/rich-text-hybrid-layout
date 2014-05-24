package com.example.hybarrangedemo.utils;

/**
 * 富文本信息类，其中存储的信息有：富文本类型、该类富文本解析器、该富文本始终点、该富文本有效值，该富文本 span。
 * @author Whiz
 *
 */
public final class MatchInfo {
	private final IParser mParser;	// 该类富文本解析器。
	private final int mStart;		// 该富文本起始点位置。
	private final int mEnd;			// 该富文本终点位置的下一个位置。
	
	// 富文本类型枚举类。
	public enum MatchType {
		None,		// 未知类型，可表示 纯文本和富文本（不包括需要分割的富文本）的混合文本。
		Weburl,		// Web url。
		Image,		// 图片。
		Hyperlink,	// 超链接。
		Smiley		// 表情。
	}
	private final MatchType mType;	// 富文本类型。
	
	private final String mValue;	// 该富文本有效值，不包括标签。
	private final Object mSpan;		// 该富文本 span，可以不需要。
	
	public MatchInfo(IParser parser, int start, int end, 
			MatchType type, 
			String value, 
			Object span) {
		mParser = parser;
		mStart = start;
		mEnd = end;
		mType = type;
		mValue = value;
		mSpan = span;
	}
	
	public IParser getParser() {
		return mParser;
	}
	
	public int getStart() {
		return mStart;
	}
	
	public int getEnd() {
		return mEnd;
	}
	
	public MatchType getMatchType() {
		return mType;
	}
	
	public String getValue() {
		return mValue;
	}
	
	public Object getSpan() {
		return mSpan;
	}
}

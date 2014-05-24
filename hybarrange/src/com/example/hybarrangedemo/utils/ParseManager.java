package com.example.hybarrangedemo.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.Log;

import com.example.hybarrangedemo.utils.MatchInfo.MatchType;

/**
 * 富文本解析管理器。执行富文本解析，给每一个需要进行 span 修饰的富文本进行 span 修饰，如果有需要进行分割的富文本（如图片），
 * 则对富文本进行分段存储以便分别进行视觉处理。
 * @author Whiz
 *
 */
public class ParseManager {
	
	/**
	 * 解析后的输入文本分段。每个分段要么是一个纯文本和富文本（不包括需要分割的富文本）的混合文本，要么是一个单独的需要分割
	 * 的富文本（如图片）。
	 * @author Whiz
	 *
	 */
	public class ParsedSegment {
		public final CharSequence text;	// 文本分段。
		public final MatchType type;	// 分段类型。
		public ParsedSegment(CharSequence text, MatchType type) {
			this.text = text;
			this.type = type;
		}
		
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append(super.toString())
				   .append(" Parsed segment: ")
				   .append(text)
				   .append(", type is: ")
				   .append(type);
			return builder.toString();
		}
	}
	
	/**
	 * 执行富文本解析，给每一个需要进行 span 修饰的富文本进行 span 修饰，如果有需要进行分割的富文本（如图片），
	 * 则对富文本进行分段存储以便分别进行视觉处理。
	 * @param parser 富文本解析器。
	 * @param text 需要解析的输入文本。
	 * @return 解析后被分段存储的解析文本队列。
	 */
	public ArrayList<ParsedSegment> parse(IParser parser, CharSequence text) {
		// 解析器未初始化、输入文本为空，不执行解析。
		if (null == parser || TextUtils.isEmpty(text)) return null;
		
		// 对输入文本进行解析。
		CharSequence toParse = parser.parse(text);
		
		// 解析后，对富文本信息队列按每个富文本所在位置进行排序。
		ArrayList<MatchInfo> matchInfos = parser.mMatchInfos;
		Collections.sort(matchInfos, new Comparator<MatchInfo>() {

			@Override
			public int compare(MatchInfo lhs, MatchInfo rhs) {
				// TODO Auto-generated method stub
				if (lhs.getEnd() <= rhs.getStart()) {
					return -1;
				} else if (rhs.getEnd() <= lhs.getStart()) {
					return 1;
				}
				
				throw new IllegalArgumentException("Cannot compare for the parsed segments are conflicting!");
			}
			
		});
		
		// 从排序后的富文本信息队列中逐段取出富文本，并对其进行分段或 span 修饰。
		ArrayList<ParsedSegment> mSegments = new ArrayList<ParsedSegment>();
		SpannableStringBuilder builder = new SpannableStringBuilder();
        int cursor = 0;
        for (MatchInfo info : parser.mMatchInfos) {
        	if (info.getParser().mNeedSplit) {
        		// 需要分段
        		if (0 == cursor) {
        			builder.append(toParse.subSequence(cursor, info.getStart()));
        		}
        		mSegments.add(new ParsedSegment(builder, MatchType.None));
        		mSegments.add(new ParsedSegment(info.getValue(), info.getMatchType()));
        		builder = new SpannableStringBuilder();
        		
        	} else {
        		// 直接进行 span 修饰并逐段按序合并。
        		builder.append(toParse.subSequence(cursor, info.getStart()))
        			   .append(info.getValue());
        		int end = builder.length();
            	int start = end - info.getValue().length();
            	builder.setSpan(info.getSpan(), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        	}
        	
        	cursor = info.getEnd();
        }
        
        // 存储剩余纯文本段。
        builder.append(toParse.subSequence(cursor, toParse.length()));
        mSegments.add(new ParsedSegment(builder, MatchType.None));
		
		return mSegments;
	}
}

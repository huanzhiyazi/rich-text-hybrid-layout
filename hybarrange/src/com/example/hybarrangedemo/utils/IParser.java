package com.example.hybarrangedemo.utils;

import java.util.ArrayList;

import com.example.hybarrangedemo.utils.MatchInfo.MatchType;

import android.content.Context;
import android.util.Log;

/**
 * 富文本解析器抽象类，定义和实现共有的装饰接口。
 * @author Whiz
 *
 */
public abstract class IParser {
	protected boolean mEndurePollute = false;
	
	// 装饰类，可以通过装饰来实现多种富文本类型的解析。
	protected IParser mDecorParser;
	
	protected Context mContext;
	
	// 富文本信息队列，存储所有匹配的富文本段信息，以便进行 span 修饰
	protected ArrayList<MatchInfo> mMatchInfos = new ArrayList<MatchInfo>();
	
	// 有些富文本（如图片）是不能进行 span 修饰的，而需要单独进行视觉处理，所以它们需要被分割。比如：图片段需要分割
	// 出来分配 ImageView 进行显示。
	protected boolean mNeedSplit = false;
	
	public IParser(Context context, IParser decor) {
		mContext = context;
		mDecorParser = decor;
	}
	
	/**
	 * 解析输入文本中的富文本，并将它们存储在富文本信息队列中。需要解析的富文本类型由本身的解析器类型和装饰解析器类型决定。
	 * 你需要重写该方法实现具体解析器的解析过程，并在重写的方法中继承父类的实现：
	 * <p><b>CharSequence toParse = super.parse(text);</b></p>
	 * 
	 * @param text 输入文本，其中可能含有需要解析的富文本。
	 * @return 输入文本自身，以便被其被装饰的解析器进一步利用。
	 */
	protected CharSequence parse(CharSequence text) {
		CharSequence toParse = text;
		if (mDecorParser != null) {
			toParse = mDecorParser.parse(text);
			mMatchInfos = mDecorParser.mMatchInfos;
		}
		
		return toParse;
	}
	
	/**
	 * 检查在解析过程中匹配到的富文本是否和富文本信息队列中已经存储的富文本产生了冲突（即是否产生重叠），产生冲突的富文本是
	 * 不合法的，会抛出 IllegalArgumentException 异常。
	 * @param toParse 需要解析的输入文本。
	 * @param areaT 当前匹配到富文本类型。
	 * @param areaS 当前匹配到的富文本的起始位置。
	 * @param areaE 当前匹配到的富文本的终点位置的下一个位置。
	 * 
	 * @throws IllegalArgumentException
	 */
	protected final void checkAreaLegitimacy(CharSequence toParse, MatchType areaT, int areaS, int areaE) {
		// 当前匹配到的富文本始终点不合法。
		if (areaS > areaE) {
			throw new IllegalArgumentException("The bound of the parsed segment is illegal! " +
					"start: " + areaS + ", end: " + areaE);
		}
		
		int polluteStart = -1, polluteEnd = -1;
		MatchType polluteType = MatchType.None;
		boolean polluted = false;
		CharSequence area = null, target = null;
		for (MatchInfo info : mMatchInfos) {
			if (mEndurePollute && info.getParser().mEndurePollute) {
				continue;
			}
			
			int targetS = info.getStart();
			int targetE = info.getEnd();
			if (areaE <= targetS || targetE <= areaS) {
				// areaS - areaE - targetS - targetE
				// OR 
				// targetS - targetE - areaS - areaE
				// 没有冲突（没有重叠），继续比较。
				continue;
			}
			
			// 冲突的形式总共有四种：相互包含或者首尾重叠。
			if (areaS >= targetS && areaE <= targetE) {
				// targetS - areaS - areaE - targetE
				polluteStart = areaS;
				polluteEnd = areaE;
				polluteType = info.getMatchType();
				polluted = true;
				area = toParse.subSequence(areaS, areaE);
				target = toParse.subSequence(targetS, targetE);
				break;
				
			} else if (targetS >= areaS && targetE <= areaE) {
				// areaS - targetS - targetE - areaE
				polluteStart = targetS;
				polluteEnd = targetE;
				polluteType = info.getMatchType();
				polluted = true;
				area = toParse.subSequence(areaS, areaE);
				target = toParse.subSequence(targetS, targetE);
				break;
				
			} else if (areaE > targetE) {
				// targetS - areaS - targetE - areaE
				polluteStart = areaS;
				polluteEnd = targetE;
				polluteType = info.getMatchType();
				polluted = true;
				area = toParse.subSequence(areaS, areaE);
				target = toParse.subSequence(targetS, targetE);
				break;
				
			} else if (targetE > areaE) {
				// areaS - targetS - areaE - targetE
				polluteStart = targetS;
				polluteEnd = areaE;
				polluteType = info.getMatchType();
				polluted = true;
				area = toParse.subSequence(areaS, areaE);
				target = toParse.subSequence(targetS, targetE);
				break;
			}
		}
		
		if (polluted) {
			// 被污染了（冲突），抛出异常。
			throw new IllegalArgumentException("The parsed segments are conflicting! The conflict " +
					"area is from " + polluteStart + " to " + polluteEnd + ": " + 
					toParse.subSequence(polluteStart, polluteEnd) + 
					". Area part is: " + area +
					". Target part is: " + target +
					". And the conflict segment types are " + areaT + " and " + polluteType);
		}
	}
	
	/**
	 * 返回解析到的富文本信息队列。
	 * @return 富文本信息队列。
	 */
	public final ArrayList<MatchInfo> getMatchInfos() {
		return mMatchInfos;
	}
}

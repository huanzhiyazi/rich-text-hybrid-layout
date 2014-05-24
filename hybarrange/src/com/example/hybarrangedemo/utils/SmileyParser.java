package com.example.hybarrangedemo.utils;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.example.hybarrangedemo.R;
import com.example.hybarrangedemo.utils.MatchInfo.MatchType;

import android.content.Context;
import android.text.style.ImageSpan;
import android.util.Log;

/**
 * 表情解析器。之前做过的 android 原生 Mms 项目有做过表情解析功能，该类即参考了该项目中的 SmileyParser 类：
 * 
 * <p><b>http://grepcode.com/file/repository.grepcode.com/java/ext/com.google.android/android-apps/4.3.1_r1/com/android/mms/util/SmileyParser.java#SmileyParser</b></p>
 * 
 * BTW，引用而不尊重别人的知识成果是可耻的，嗯嗯。
 * @author Whiz
 *
 */
public class SmileyParser extends IParser {
    private final String[] mSmileyTexts;
    private final Pattern mPattern;
    private final HashMap<String, Integer> mSmileyToRes;

    public SmileyParser(Context context, IParser decor) {
    	super(context, decor);
        mSmileyTexts = mContext.getResources().getStringArray(DEFAULT_SMILEY_TEXTS);
        mSmileyToRes = buildSmileyToRes();
        mPattern = Patterns.getInstance().mSmileyPattern;
    }
    
    public SmileyParser(Context context) {
    	this(context, null);
    }

    static class Smileys {
        private static final int[] sIconIds = {
            R.drawable.emo_im_happy,
            R.drawable.emo_im_sad,
            R.drawable.emo_im_winking,
            R.drawable.emo_im_tongue_sticking_out,
            R.drawable.emo_im_surprised,
            R.drawable.emo_im_kissing,
            R.drawable.emo_im_yelling,
            R.drawable.emo_im_cool,
            R.drawable.emo_im_money_mouth,
            R.drawable.emo_im_foot_in_mouth,
            R.drawable.emo_im_embarrassed,
            R.drawable.emo_im_angel,
            R.drawable.emo_im_undecided,
            R.drawable.emo_im_crying,
            R.drawable.emo_im_lips_are_sealed,
            R.drawable.emo_im_laughing,
            R.drawable.emo_im_wtf,
            R.drawable.emo_im_heart,
            R.drawable.emo_im_mad,
            R.drawable.emo_im_smirk,
            R.drawable.emo_im_pokerface
        };

        public static int HAPPY = 0;
        public static int SAD = 1;
        public static int WINKING = 2;
        public static int TONGUE_STICKING_OUT = 3;
        public static int SURPRISED = 4;
        public static int KISSING = 5;
        public static int YELLING = 6;
        public static int COOL = 7;
        public static int MONEY_MOUTH = 8;
        public static int FOOT_IN_MOUTH = 9;
        public static int EMBARRASSED = 10;
        public static int ANGEL = 11;
        public static int UNDECIDED = 12;
        public static int CRYING = 13;
        public static int LIPS_ARE_SEALED = 14;
        public static int LAUGHING = 15;
        public static int WTF = 16;
        public static int MAD = 17;
        public static int HEART = 18;
        public static int SMIRK = 19;
        public static int POKERFACE = 20;

        public static int getSmileyResource(int which) {
            return sIconIds[which];
        }
    }

    // NOTE: if you change anything about this array, you must make the corresponding change
    // to the string arrays: default_smiley_texts and default_smiley_names in res/values/arrays.xml
    public static final int[] DEFAULT_SMILEY_RES_IDS = {
        Smileys.getSmileyResource(Smileys.HAPPY),                //  0
        Smileys.getSmileyResource(Smileys.SAD),                  //  1
        Smileys.getSmileyResource(Smileys.WINKING),              //  2
        Smileys.getSmileyResource(Smileys.TONGUE_STICKING_OUT),  //  3
        Smileys.getSmileyResource(Smileys.SURPRISED),            //  4
        Smileys.getSmileyResource(Smileys.KISSING),              //  5
        Smileys.getSmileyResource(Smileys.YELLING),              //  6
        Smileys.getSmileyResource(Smileys.COOL),                 //  7
        Smileys.getSmileyResource(Smileys.MONEY_MOUTH),          //  8
        Smileys.getSmileyResource(Smileys.FOOT_IN_MOUTH),        //  9
        Smileys.getSmileyResource(Smileys.EMBARRASSED),          //  10
        Smileys.getSmileyResource(Smileys.ANGEL),                //  11
        Smileys.getSmileyResource(Smileys.UNDECIDED),            //  12
        Smileys.getSmileyResource(Smileys.CRYING),               //  13
        Smileys.getSmileyResource(Smileys.LIPS_ARE_SEALED),      //  14
        Smileys.getSmileyResource(Smileys.LAUGHING),             //  15
        Smileys.getSmileyResource(Smileys.WTF),                  //  16
        Smileys.getSmileyResource(Smileys.MAD),                  //  17
        Smileys.getSmileyResource(Smileys.HEART),                //  18
        Smileys.getSmileyResource(Smileys.SMIRK),                //  19
        Smileys.getSmileyResource(Smileys.POKERFACE),            //  20
    };

    public static final int DEFAULT_SMILEY_TEXTS = R.array.default_smiley_texts;
    public static final int DEFAULT_SMILEY_NAMES = R.array.default_smiley_names;

    /**
     * Builds the hashtable we use for mapping the string version
     * of a smiley (e.g. ":-)") to a resource ID for the icon version.
     */
    private HashMap<String, Integer> buildSmileyToRes() {
        if (DEFAULT_SMILEY_RES_IDS.length != mSmileyTexts.length) {
            // Throw an exception if someone updated DEFAULT_SMILEY_RES_IDS
            // and failed to update arrays.xml
            throw new IllegalStateException("Smiley resource ID/text mismatch");
        }

        HashMap<String, Integer> smileyToRes =
                            new HashMap<String, Integer>(mSmileyTexts.length);
        for (int i = 0; i < mSmileyTexts.length; i++) {
            smileyToRes.put(mSmileyTexts[i], DEFAULT_SMILEY_RES_IDS[i]);
        }

        return smileyToRes;
    }


    /**
     * 通过表情正则表达式匹配输入文本中所有的表情。
     * 
     * @param text 输入文本，其中可能含有需要被解析的表情。
     */
    @Override
    public CharSequence parse(CharSequence text) {
    	// 先由其装饰解析器解析出其它必要的富文本。
    	CharSequence toParse = super.parse(text);

    	// 逐段匹配输入文本中所有的表情，并存储在富文本信息队列中。
        Matcher matcher = mPattern.matcher(toParse);
        while (matcher.find()) {
        	// 检查是否有冲突。
        	checkAreaLegitimacy(toParse, MatchType.Smiley, matcher.start(), matcher.end());

            int resId = mSmileyToRes.get(matcher.group());
            mMatchInfos.add(new MatchInfo(
            		this, matcher.start(), matcher.end(), 
            		MatchType.Smiley, 
            		matcher.group(),
            		new ImageSpan(mContext, resId)));
        }

        return toParse;
    }
}


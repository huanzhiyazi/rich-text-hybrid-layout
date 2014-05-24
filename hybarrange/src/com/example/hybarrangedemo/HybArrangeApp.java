package com.example.hybarrangedemo;

import com.example.hybarrangedemo.Constants.Config;
import com.example.hybarrangedemo.utils.Patterns;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.StrictMode;

public class HybArrangeApp extends Application {
	private static HybArrangeApp sApp = null;
	public static final boolean TEST = true;
	
	@SuppressWarnings("unused")
	@Override
	public void onCreate() {
		if (Config.DEVELOPER_MODE && Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().penaltyDialog().build());
			StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyDeath().build());
		}
		super.onCreate();
		
		// 初始化所有正则式解析器。
		Patterns.init(this);
		
		// 初始化图片加载配置参数。
		initImageLoader(getApplicationContext());
		
		sApp = this;
	}
	
	synchronized public HybArrangeApp getApplication() {
		return sApp;
	}
	
	/**
	 * 全局初始化图片加载配置参数。在整个应用程序生命周期内有效。
	 * <p>图片异步加载使用了@nostra13 大神的 Android-Universal-Image-Loader 组件
	 * （https://github.com/nostra13/Android-Universal-Image-Loader）</p>
	 * @param context
	 */
	public static void initImageLoader(Context context) {
		// This configuration tuning is custom. You can tune every option, you may tune some of them,
		// or you can create default configuration by
		//  ImageLoaderConfiguration.createDefault(this);
		// method.
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.diskCacheFileNameGenerator(new Md5FileNameGenerator())
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.writeDebugLogs() // Remove for release app
				.build();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);
	}
}

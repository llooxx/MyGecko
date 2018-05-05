package com.linorz.mygecko;


import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

/**
 * Created by linorz on 2018/3/8.
 */
class GeckoApplication extends Application {
//    private static Context app_context;
//    private static DisplayImageOptions mOptions;
//    public static SharedPreferences sp;
//
//    public static Context getContext() {
//        return app_context;
//    }
//
//    public static DisplayImageOptions getOptions() {
//        return mOptions;
//    }
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//        app_context = this.getApplicationContext();
//        //初始化一些服务
//        initImageLoader();
//        //缓存
//        sp = getSharedPreferences("MyGecko", Context.MODE_PRIVATE);
//    }
//
//    private void initImageLoader() {
//        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
//                .threadPriority(Thread.NORM_PRIORITY - 2)
//                .denyCacheImageMultipleSizesInMemory()
//                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
//                .diskCacheSize(200 * 1024 * 1024) // 200 Mb
//                .tasksProcessingOrder(QueueProcessingType.LIFO)
//                .build();
//        ImageLoader.getInstance().init(config);
//        //统一使用
//        mOptions = new DisplayImageOptions.Builder()
//                .showImageOnLoading(android.R.color.white)   //加载过程中
//                .showImageForEmptyUri(android.R.color.white) //uri为空时
//                .showImageOnFail(android.R.color.white)      //加载失败时
//                .cacheOnDisk(true)
//                .cacheInMemory(true)                             //允许cache在内存和磁盘中
//                .bitmapConfig(Bitmap.Config.RGB_565)             //图片压缩质量参数
//                .build();
//    }

}

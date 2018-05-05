package com.linorz.mygecko.tools;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.LruCache;
import android.view.View;
import android.widget.Toast;


import com.google.gson.Gson;
import com.linorz.mygecko.DealFile;
import com.linorz.mygecko.GeckoGson;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;


import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by linorz on 2016/4/25.
 */
public class StaticMethod {
    private static DisplayImageOptions mOptions;

    public static DisplayImageOptions getOptions() {
        return mOptions;
    }

    public static String getRealPath(Context context, Uri imageUri) {
        return GetPathFromUri4kitkat.getPath(context, imageUri);
    }
    //    }
    //        return res;
    //        cursor.close();
    //        }
    //            res = cursor.getString(column_index);
    //            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
    //        if (cursor.moveToFirst()) {
    //        Cursor cursor = context.getContentResolver().query(uri, proj, null, null, null);
    //        String[] proj = {MediaStore.Images.Media.DATA};
    //        String res = null;
//    public static String getRealPath(Context context, Uri uri) {


    public static Uri getImageContentUri(Context context, String path) {
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Images.Media._ID}, MediaStore.Images.Media.DATA + "=? ", new String[]{path}, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DATA, path);
            return context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        }
    }

    public static Bitmap getDiskBitmap(String pathString) {
        Bitmap bitmap = null;
        try {
            File file = new File(pathString);
            if (file.exists()) {
                bitmap = BitmapFactory.decodeFile(pathString);
            }
        } catch (Exception e) {
        }
        return bitmap;
    }

    @TargetApi(Build.VERSION_CODES.M)
    private static boolean checkSelfPermissionWrapper(Object cxt, String permission) {
        if (cxt instanceof Activity) {
            Activity activity = (Activity) cxt;
            return ActivityCompat.checkSelfPermission(activity,
                    permission) == PackageManager.PERMISSION_GRANTED;
        } else if (cxt instanceof Fragment) {
            Fragment fragment = (Fragment) cxt;
            return fragment.getActivity().checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
        } else {
            throw new RuntimeException("cxt is net a activity or fragment");
        }
    }
    //批量申请权限

    public static String[] checkSelfPermissionArray(Object cxt, String[] permission) {
        ArrayList<String> permiList = new ArrayList<>();
        for (String p : permission) {
            if (!checkSelfPermissionWrapper(cxt, p)) {
                permiList.add(p);
            }
        }
        if (permiList.size() > 0) {
            return permiList.toArray(new String[permiList.size()]);
        } else {
            return new String[]{};
        }
    }

    public static void requestPermissions(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] permissions = checkSelfPermissionArray(activity, new String[]{
                    Manifest.permission.SYSTEM_ALERT_WINDOW,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS
            });
            if (permissions.length > 0) {
                ActivityCompat.requestPermissions(activity, permissions, 1);
            }
        }
    }


    public static List<GeckoGson.GeckoBean> getGeckoList(Context context) {
        SharedPreferences sp = context.getSharedPreferences("MyGecko", Context.MODE_PRIVATE);
        String str_list = sp.getString("geckolist", "");
//        System.out.println("!!!:" + str_list);
        Gson gson = new Gson();
        GeckoGson gg = gson.fromJson(str_list, GeckoGson.class);
        if (gg == null) return new ArrayList<GeckoGson.GeckoBean>();
        else return gg.getGeckos();
    }

    public static void initSomthing(Context context) {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .diskCacheSize(20 * 1024 * 1024) // 20 Mb
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .build();
        ImageLoader.getInstance().init(config);
        //统一使用
        mOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(android.R.color.white)   //加载过程中
                .showImageForEmptyUri(android.R.color.white) //uri为空时
                .showImageOnFail(android.R.color.white)      //加载失败时
                .cacheOnDisk(false)
                .cacheInMemory(false)                             //允许cache在内存和磁盘中
                .bitmapConfig(Bitmap.Config.RGB_565)             //图片压缩质量参数
                .build();
    }

    public static Bitmap writeOnBitmap(Context context, Bitmap bitmap, String str) {
        try {
            float scale = context.getResources().getDisplayMetrics().density;
            scale = scale * Math.min(bitmap.getWidth(), bitmap.getHeight()) / 1000;


            android.graphics.Bitmap.Config bitmapConfig = bitmap.getConfig();
            // 如果没有配置，设置默认 bitmap 配置
            if (bitmapConfig == null) {
                bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888;
            }
            bitmap = bitmap.copy(bitmapConfig, true);

            Canvas canvas = new Canvas(bitmap);

            TextPaint paint = new TextPaint();
            paint.setColor(Color.rgb(255, 225, 0));
            paint.setFakeBoldText(true);
            paint.setShadowLayer(1f, 5f, 5f, Color.DKGRAY);
            paint.setTextSize(Math.min(bitmap.getWidth(), bitmap.getHeight()) / 20);
            StaticLayout currentLayout = new StaticLayout(str, paint, bitmap.getWidth(),
                    Layout.Alignment.ALIGN_NORMAL, 1.5f, 0f, false);
            canvas.translate(bitmap.getWidth() / 20, bitmap.getHeight() / 20);
            currentLayout.draw(canvas);

            return bitmap;
        } catch (Exception e) {
            return null;
        }
    }


    public static String saveBitmapToSDCard(Bitmap bitmap, String filepath, String imagename) {
        String path = filepath + "/" + imagename;
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(path);
            if (fos != null) {
                if (imagename.contains(".jpg") || imagename.contains(".jpeg"))
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                else if (imagename.contains(".png"))
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.close();
            }

            return path;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void saveGeckoLIst(List<GeckoGson.GeckoBean> list, SharedPreferences.Editor editor) {
        for (int i = 0; i < list.size(); i++) {
            GeckoGson.GeckoBean gecko = list.get(i);
            String name_old = gecko.getPicture();
            if (name_old != null && !name_old.isEmpty()) {
                String name_new = (i + 1) + gecko.getKind() + gecko.getBirth() + gecko.getGender() + gecko.getPicture().substring(gecko.getPicture().indexOf("."));
                if (!name_new.equals(name_old)) {
                    DealFile.renameFile(DealFile.getFilePath() + "/" +
                            name_old, DealFile.getFilePath() + "/" +
                            name_new);
                    gecko.setPicture(name_new);
                }
            }
            list.get(i).setNum(i + 1);
        }
        GeckoGson geckos = new GeckoGson();
        geckos.setGeckos(list);
        editor.putString("geckolist", new Gson().toJson(geckos));
        editor.apply();
    }

    public static Bitmap shotRecyclerView(RecyclerView view) {
        RecyclerView.Adapter adapter = view.getAdapter();
        Bitmap bigBitmap = null;
        if (adapter != null) {
            int size = adapter.getItemCount();
            int height = 0;
            Paint paint = new Paint();
            int iHeight = 0;
            final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

            //   Use 1/8th of the available memory for this memory cache.
            final int cacheSize = maxMemory / 8;
            LruCache<String, Bitmap> bitmaCache = new LruCache<>(cacheSize);
            for (int i = 0; i < size; i++) {
                RecyclerView.ViewHolder holder = adapter.createViewHolder(view, adapter.getItemViewType(i));
                adapter.onBindViewHolder(holder, i);
                holder.itemView.measure(
                        View.MeasureSpec.makeMeasureSpec(view.getWidth(), View.MeasureSpec.EXACTLY),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                holder.itemView.layout(0, 0, holder.itemView.getMeasuredWidth(),
                        holder.itemView.getMeasuredHeight());
                holder.itemView.setDrawingCacheEnabled(true);
                holder.itemView.buildDrawingCache();
                Bitmap drawingCache = holder.itemView.getDrawingCache();
                if (drawingCache != null) {

                    bitmaCache.put(String.valueOf(i), drawingCache);
                }
                height += holder.itemView.getMeasuredHeight();
            }

            bigBitmap = Bitmap.createBitmap(view.getMeasuredWidth(), height, Bitmap.Config.ARGB_8888);
            Canvas bigCanvas = new Canvas(bigBitmap);
            Drawable lBackground = view.getBackground();
            if (lBackground instanceof ColorDrawable) {
                ColorDrawable lColorDrawable = (ColorDrawable) lBackground;
                int lColor = lColorDrawable.getColor();
                bigCanvas.drawColor(lColor);
            }

            for (int i = 0; i < size; i++) {
                Bitmap bitmap = bitmaCache.get(String.valueOf(i));
                bigCanvas.drawBitmap(bitmap, 0f, iHeight, paint);
                iHeight += bitmap.getHeight();
                bitmap.recycle();
            }
        }
        return bigBitmap;
    }

    static Toast mToast;
    @SuppressLint("ShowToast")
    public static void makeText(Context context, String text, int time) {
        if (mToast == null) {
            mToast = Toast.makeText(context, text, time);
        } else {
            mToast.setText(text);
            mToast.setDuration(time);
        }
        mToast.show();
    }
}

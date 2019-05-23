package com.example.horizontionview.Util;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class BitmapUtil {
    /**
     * 采用图片的绝对路径获取图片并采样压缩 降低bitmap占用的内存 但是此方法获得图片的原长和原宽为0 目前还不知道为什么
     */
    public static Bitmap getBitmap(String filePath){
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath,options);
        options.inSampleSize = calculateInSampleSize(options,300,300);
        //计算采样率
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath,options);
    }
    //采样率压缩照片
    public static int calculateInSampleSize(BitmapFactory.Options options,int reqWidth,int reqHeight){
        int height = options.outHeight;
        int width = options.outWidth;
        int inSimpleSize = 1;

        if(height > reqHeight || width > reqWidth){
            final int halfHeight = height/2;
            final int halfWidth = width/2;
            while ((halfHeight/inSimpleSize)>=reqHeight
                    && (halfWidth/inSimpleSize)>=reqWidth){
                inSimpleSize *= 2;
            }
        }
        return inSimpleSize;
    }

    /**
     * 通过获得uri来压缩图片
     */
    public static Bitmap getBitmapFormUri(Activity ac, Uri uri) throws IOException {
        InputStream input = ac.getContentResolver().openInputStream(uri);
        if (input == null) {
            return null;
        } else {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(input, null, options);
            input.close();
            int originalWidth = options.outWidth;
            int originalHeight = options.outHeight;
            if ((originalWidth == -1) || (originalHeight == -1))
                return null;
            options.inSampleSize = calculateInSampleSize(options, 360, 360);//设置缩放比例
            options.inJustDecodeBounds = false;
            input = ac.getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(input, null, options);
            if (input != null) {
                input.close();
            }
            return bitmap;
        }
    }

    /**
     *质量压缩
     */
    public static Bitmap compressImage(Bitmap image){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        //质量压缩方法 100表示不压缩 把压缩后的数据存到baos中
        image.compress(Bitmap.CompressFormat.JPEG,50,baos);
        int options = 50;
        //循环判断压缩后图片是否大于100kb 大于继续压缩
        while (baos.toByteArray().length/1024 > 100){
            baos.reset();//重置baos即清空baos
            //这里压缩options% 压缩后的数据存放到baos中
            image.compress(Bitmap.CompressFormat.JPEG,options,baos);
            //每次都减少10
            options -= 10;
        }
        ByteArrayInputStream bis = new ByteArrayInputStream(baos.toByteArray());
        Bitmap bitmap = BitmapFactory.decodeStream(bis,null,null);
        return bitmap;
    }

    /**
     *保存图片需要先质量压缩 后采样率压缩
     */

}

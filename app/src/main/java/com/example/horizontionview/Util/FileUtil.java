package com.example.horizontionview.Util;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FileUtil {
    public static final String dir = Environment.getExternalStorageDirectory().getAbsolutePath()+"/表情工坊/";

    private static SimpleDateFormat simpleDate = new SimpleDateFormat("yyyyMMDD-HHmmss", Locale.getDefault());
    private static String fileName = simpleDate.format(new Date().getTime());

    public static void saveBitmap(Context context,Bitmap bitmap){
        if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            Toast.makeText(context,"sdcard不存在！！！",Toast.LENGTH_SHORT);
        }
        //目录转化成文件
        File dirFile = new File(dir);
        if(!dirFile.exists()){
            dirFile.mkdirs();
        }
        File file = new File(dirFile,fileName+".jpg");
        FileOutputStream fos = null;
        if(!file.exists()){
            try {
                file.createNewFile();
                fos = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG,90,fos);
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                if(fos != null){
                    try {
                        fos.flush();
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

//        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//        Uri uri = Uri.fromFile(file);
//        intent.setData(uri);
//        context.sendBroadcast(intent);
//
//        if(!bitmap.isRecycled()){
//            System.gc();
//        }
        savePhotoToMedia(context,bitmap);
    }

    //保存到数据库
    public static void savePhotoToMedia(Context context,Bitmap bitmap){
        String uriString = MediaStore.Images.Media.insertImage(context.getContentResolver(),
                bitmap,null,null);
        File dirFile = new File(getRealPathFromURL(Uri.parse(uriString),context));
        updatePhotoMedia(dirFile,context);
        if(!bitmap.isRecycled()){
            System.gc();
        }
        Toast.makeText(context,"保存成功",Toast.LENGTH_SHORT);
    }

    public static void updatePhotoMedia(File file,Context context){
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(file));
        context.sendBroadcast(intent);
    }

    //得到绝对地址
    private static String getRealPathFromURL(Uri contentUri,Context context){
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(contentUri,proj,null,null,null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String fileStr = cursor.getString(column_index);
        cursor.close();
        return fileStr;
    }
}

package com.example.horizontionview;
/**
 * author:lixinyi
 * date:2019/5/15
 */
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.horizontionview.Util.BitmapUtil;
import com.example.horizontionview.Util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    /**
     * 使用相机获取照片
     */
    public final static int SELECT_PIC_BY_TAKE_PHOTO = 1;
    /**
     * 从相册获取照片
     */
    public final static int SELECT_PIC_BY_PICK_PHOTO= 2;

    private Button button_album;
    private Button button_camera;
    private Button button_save;
    private ImageView imageView;
    private Bitmap background;

    /**
     *保存照片的根目录
     */
    public final String IMAGE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + Locale.CHINA;
    public String timeStamp = new SimpleDateFormat("yyyy_MM_DD").format(new Date());
    private File phote_file = new File(IMAGE_PATH);
    private String photePath;

    private Uri photoUri;
    private Uri imageUri;

    private String string = "猪猪猪猪";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    public void initView(){
        button_album = findViewById(R.id.button_album);
        button_camera = findViewById(R.id.button_camera);
        button_save = findViewById(R.id.button_save);
        imageView = findViewById(R.id.photo);
        button_album.setOnClickListener(this);
        button_camera.setOnClickListener(this);
        button_save.setOnClickListener(this);
    }
    /**
     * 打开相册
     */
    public void pickPhoto(){
        Intent intent = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,SELECT_PIC_BY_PICK_PHOTO);
    }
    /**
     * 拍照获取图片
     */
    public void takePhoto(){
        String SDState = Environment.getExternalStorageState();
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(SDState.equals(Environment.MEDIA_MOUNTED)){
            File outputImage = new File(getExternalCacheDir(),"output_image.jpg");
            if(!outputImage.exists()){
                outputImage.mkdir();
            }
            if(Build.VERSION.SDK_INT>=24){
                imageUri = FileProvider.getUriForFile(this,
                        this.getPackageName()+".android7.fileprovider",outputImage);
                Log.e("lixinyi",imageUri.toString());
            }else {
                imageUri = Uri.fromFile(outputImage);
            }
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
        startActivityForResult(intent,SELECT_PIC_BY_TAKE_PHOTO);
    }

    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        //RESULT_OK 标准活动结果：操作成功
        if(resultCode == Activity.RESULT_OK){
            switch (requestCode){
                case SELECT_PIC_BY_PICK_PHOTO:
                    Uri uri = data.getData();
                    //获得图片的绝对路径
                    String path = getPhotoPath(data);
                    if(path==null || path.equals("")){
                        Toast.makeText(this,"path有问题！！！",Toast.LENGTH_SHORT);
                        return;
                    }
                    Bitmap bitmap = null;
                    try {
                        bitmap = BitmapUtil.getBitmapFormUri(this,uri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //获取bitmap本身的大小作为原长和原宽
                    background = addText(bitmap,bitmap.getWidth(),bitmap.getHeight(),string);
                    break;
                case SELECT_PIC_BY_TAKE_PHOTO:
                    Bitmap bitmap2 = null;
                    try {
                        bitmap2 = BitmapUtil.getBitmapFormUri(this,imageUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if(bitmap2==null){
                        Toast.makeText(this,"bitmap为空！！！",Toast.LENGTH_SHORT);
                        return;
                    }
                    imageView.setImageBitmap(bitmap2);
                    break;
            }
        }
    }

    public String getPhotoPath(Intent data){
        /**
         *获取到的图片路径
         */
        String picPath = null;
        //获取系统照片的URL
        photoUri = data.getData();
        if(data == null || photoUri == null){
            Toast.makeText(this,"获取图片失败！！！",Toast.LENGTH_SHORT);
            return "";
        }
        //DATA 磁盘上文件的路径 第三方应用可能没有直接访问文件系统权限
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        //从系统表中查询指定uri对应的照片
        Cursor cursor = getContentResolver().query(photoUri,filePathColumn,
                null,null,null);
        cursor.moveToFirst();
        //返回给定列名的从0开始的索引 不存在返回-1
        int columIndex = cursor.getColumnIndex(filePathColumn[0]);
        //获取照片路径
        picPath = cursor.getString(columIndex);
        cursor.close();
        return picPath;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.button_album:
                pickPhoto();
                break;
            case R.id.button_camera:
                takePhoto();
                break;
            case R.id.button_save:
                FileUtil.saveBitmap(this,background);
                default:
                    Toast.makeText(this,"default",Toast.LENGTH_LONG);
                    break;
        }
    }

    public Bitmap addText(Bitmap bitmap,int width,int height,String str){
        int destWidth = width;
        int destHeight = height;
        Log.e("width_height",String.valueOf(destWidth)+" "+String.valueOf(destHeight));

        Bitmap background = Bitmap.createBitmap(destWidth,destHeight,Bitmap.Config.ARGB_8888);
        //初始化画布绘制的图像到background上
        Canvas canvas = new Canvas(background);

        Paint photoPaint = new Paint();
        photoPaint.setDither(true);//获取更清晰的图像采样
        photoPaint.setFilterBitmap(true);//对位图进行滤波处理

        Rect src = new Rect(0,0, bitmap.getWidth(),bitmap.getHeight());
        Log.e("bitmap_width_height",String.valueOf(bitmap.getWidth())+" "+String.valueOf(bitmap.getHeight()));
        Rect dst = new Rect(0,0,destWidth,destHeight);
        canvas.drawBitmap(bitmap,src,dst,photoPaint);

        Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DEV_KERN_TEXT_FLAG);
        textPaint.setTextSize(60);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setAntiAlias(true);
        textPaint.setStrokeWidth(3);
        textPaint.setStyle(Paint.Style.STROKE);
        textPaint.setColor(Color.BLACK);
        canvas.drawText(str,destWidth/2,destHeight-100,textPaint);
        canvas.save();
        canvas.restore();
        bitmap.recycle();
        imageView.setImageBitmap(background);
        //return FileUtil.saveBitmap(background,FileUtil.getImageFile());
        return background;
    }
}

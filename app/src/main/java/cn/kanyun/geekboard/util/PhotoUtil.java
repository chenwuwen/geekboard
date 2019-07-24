package cn.kanyun.geekboard.util;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import androidx.fragment.app.Fragment;

import com.yalantis.ucrop.util.FileUtils;

import java.io.File;


/**
 * 头像工具类（拍照 、相册查看）
 * Created by dong.yuan on 2017/5/19.
 */

public class PhotoUtil {
    public final static int ALBUM_REQUEST_CODE = 1;//相册
    public final static int CROP_REQUEST_CODE = 2;//裁切
    public final static int CAMERA_REQUEST_CODE = 3;//照相


    //拍照
    public static Uri toCamera(Activity activity) {
        String imagePath = "";
        Uri uri = Uri.fromFile(new File(imagePath));
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        activity.startActivityForResult(intent, CAMERA_REQUEST_CODE);
        return uri;
    }

    //相册
    public static void toAlbum(Activity activity) {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        activity.startActivityForResult(Intent.createChooser(intent, "选择图片"), ALBUM_REQUEST_CODE);
    }

    //剪切
    public static void toCrop(Uri originalUri, Activity activity) {
        String imagePath = toCreateImagePath(activity);
//        裁剪后的图片Uri
        Uri destinationUri = Uri.fromFile(new File(imagePath));
        //使用Ucrop进行剪切
        UcropUtil.startUcropWithUri(activity, originalUri, destinationUri);
        //使用Android系统剪切功能
        // UcropUtil.startCropWithUri(activity,originalUri);
    }

    //剪切
    public static void toCrop(Uri originalUri, Fragment fragment, Context context) {

//        这个是裁剪完后的图片的存放路径
        String imagePath = toCreateImagePath(fragment.getActivity());
//        裁剪后的图片Uri
        Uri destinationUri = Uri.fromFile(new File(imagePath));
        //使用Ucrop进行剪切
        UcropUtil.startUcropWithUri(fragment, context, originalUri, destinationUri);
        //使用Android系统剪切功能
        // UcropUtil.startCropWithUri(activity,originalUri);
    }


    //获得图片路径
    public static String getImagePath(final Context context, final Uri uri) {
        if (null == uri) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri,
                    new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

    //随机创建图片路径
    public static String toCreateImagePath(Activity activity) {
        String path = getDiskCacheDir(activity.getBaseContext(), "ucrop");
        //判断文件夹是否存在
        File file = new File(path);
        if (!file.exists()) {
            try {
                //不存在则要创建
                //noinspection ResultOfMethodCallIgnored
                file.mkdirs();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //创建拍照生成的名字
        String imageName = System.currentTimeMillis() + ".jpg";
        return path + imageName;
    }

    /**
     * 获取diskCache的文件完整路径
     *
     * @param context    上下文
     * @param uniqueName 目录名
     * @return diskCache的文件完整路径
     */
    public static String getDiskCacheDir(Context context, String uniqueName) {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        File dir = new File(cachePath + File.separator + uniqueName + File.separator);
        // 判断文件夹是否存在，不存在则创建
        if (!dir.exists()) {
            dir.mkdir();
        }
        return (cachePath + File.separator + uniqueName + File.separator);
    }


    /**
     * @param context 上下文对象
     * @param uri     当前相册照片的Uri
     * @return 解析后的Uri对应的String
     */
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        String pathHead = "file:///";
        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (FileUtils.isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return pathHead + Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (FileUtils.isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);

                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return pathHead + FileUtils.getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (FileUtils.isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return pathHead + FileUtils.getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return pathHead + FileUtils.getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return pathHead + uri.getPath();
        }
        return null;
    }


}

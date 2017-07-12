package com.zero.sharetest;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.tencent.connect.share.QQShare;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXMusicObject;
import com.tencent.mm.opensdk.modelmsg.WXTextObject;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

/**
 * Created by WatchVoice04 on 2017/6/14.
 */

public class ShareUtil {
    private static String QQ_APP_ID="1105632898";
    private static String WEIXIN_APP_ID ="wx8954b928da52c490";
    private static String SINA_APP_ID ="141246853";

    /**
     * 分享图片给微信好友,不需导入微信SDK
     * @param context
     * @param file
     */
    public static void shareToFriend(Context context, File file) {
        Intent intent = new Intent();
        ComponentName comp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareImgUI");
        intent.setComponent(comp);
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
        context.startActivity(intent);
    }

    /**
     * 分享图片到朋友圈，不需导入微信SDK
     * @param context
     * @param files
     */
    public static void shareMultiplePictureToTimeLine(Context context,File... files) {
        Intent intent = new Intent();
        ComponentName comp = new ComponentName("com.tencent.mm",
                "com.tencent.mm.ui.tools.ShareToTimeLineUI");
        intent.setComponent(comp);
        intent.setAction(Intent.ACTION_SEND_MULTIPLE);
        intent.setType("image/*");

        ArrayList<Uri> imageUris = new ArrayList<Uri>();
        for (File f : files) {
            imageUris.add(Uri.fromFile(f));
        }
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris);
        //设置描述
        intent.putExtra("Kdescription", "描述");
        context.startActivity(intent);
    }

    /**
     * 获取和保存当前屏幕的截图
     */
    private void saveCurrentImagePrivate(Activity activity)
    {
        Log.i("ssss","截图开始");
        //1.构建Bitmap
        WindowManager windowManager = activity.getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        int w = display.getWidth();
        int h = display.getHeight();

        Bitmap Bmp = Bitmap.createBitmap( w, h, Bitmap.Config.ARGB_8888 );

        //2.获取屏幕
        View decorview = activity.getWindow().getDecorView();
        decorview.setDrawingCacheEnabled(true);
        Bmp = decorview.getDrawingCache();
        String SavePath = activity.getExternalFilesDir(null).getAbsolutePath();
        //3.保存Bitmap
        try {
            //文件
            String filepath = SavePath + "/image.jpg";
            File file = new File(filepath);
            if (file.exists()){
                file.delete();
            }
            FileOutputStream fos = null;
            fos = new FileOutputStream(file);
            if (null != fos) {
                //设置生成jpg格式可大大提高截图速率，不建议使用PNG格式
                Bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.flush();
                fos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i("ssss","截图完成");
    }

    public static void saveCurrentImage(Activity activity){
        new ShareUtil().saveCurrentImagePrivate(activity);
    }

    public static String getImagePath(Activity activity){
        return new ShareUtil().getImagePathPrivate(activity);
    }

    private String getImagePathPrivate(Activity activity){
        return activity.getExternalFilesDir(null).getAbsolutePath() + "/image.jpg";
    }

    /**
     * 分享图片到qq，需导入qq SDK，并配置好
     * @param activity
     */
    public static void shareToQQ_image(final Activity activity, String imagePath){
        Bundle params = new Bundle();
        Tencent mTencent = Tencent.createInstance(QQ_APP_ID,activity);
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL,imagePath);
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME,activity.getString(R.string.app_name));
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_IMAGE);
//                params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN);//取消注释则默认分享到qq空间
        mTencent.shareToQQ(activity, params, new IUiListener() {
            @Override
            public void onComplete(Object o) {
               Toast.makeText(activity,"分享成功",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(UiError uiError) {
                Toast.makeText(activity,"分享失败",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
                Toast.makeText(activity,"取消分享",Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 分享音乐到qq，需导入qq SDK，并配置好
     * @param activity
     */
    public static void shareToQQ_music(final Activity activity, String imagePath, String url_music, String url_web){
        Bundle params = new Bundle();
        Tencent mTencent = Tencent.createInstance(QQ_APP_ID,activity);
        //缩略图
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL,imagePath);
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME,activity.getString(R.string.app_name));
        params.putString(QQShare.SHARE_TO_QQ_TITLE,"标题");
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY,"摘要");
        //点击打开的网页地址
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL,  url_web);
        //音乐地址
        params.putString(QQShare.SHARE_TO_QQ_AUDIO_URL, url_music);
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_AUDIO);
//                params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN);//取消注释则默认分享到qq空间
        mTencent.shareToQQ(activity, params, new IUiListener() {
            @Override
            public void onComplete(Object o) {
                Toast.makeText(activity,"分享成功",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(UiError uiError) {
                Toast.makeText(activity,"分享失败",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
                Toast.makeText(activity,"取消分享",Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 分享音乐到qq，需导入qq SDK，并配置好
     * @param activity
     */
    public static void shareToQQ_web(final Activity activity, String imagePath, String url_web){
        Bundle params = new Bundle();
        Tencent mTencent = Tencent.createInstance(QQ_APP_ID,activity);
        //缩略图
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL,imagePath);
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME,activity.getString(R.string.app_name));
        params.putString(QQShare.SHARE_TO_QQ_TITLE,"标题");
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY,"摘要");
        //点击打开的网页地址
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL,  url_web);
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
//                params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN);//取消注释则默认分享到qq空间
        mTencent.shareToQQ(activity, params, new IUiListener() {
            @Override
            public void onComplete(Object o) {
                Toast.makeText(activity,"分享成功",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(UiError uiError) {
                Toast.makeText(activity,"分享失败",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
                Toast.makeText(activity,"取消分享",Toast.LENGTH_SHORT).show();
            }
        });
    }

//    /**
//     * 分享图片到微博 需导入微博 SDK，并配置好
//     * 仅当安装了微博客户端有效
//     * @param activity
//     */
//    public static void shareToSINA(final Activity activity, String imagePath){
//        IWeiboShareAPI iWeiboShareAPI = WeiboShareSDK.createWeiboAPI(activity, SINA_APP_ID);
//        iWeiboShareAPI.registerApp();//将应用注册到微博客户端
//        WeiboMultiMessage weiboMessage = new WeiboMultiMessage();//初始化微博的分享消息
//        //设置描述
////        TextObject textObject = new TextObject();
////        textObject.text =  activity.getString(R.string.VoicegameShareText);
////        weiboMessage.textObject = textObject;
//        //设置图片
//        ImageObject imageObject = new ImageObject();
//        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
//        imageObject.setImageObject(bitmap);
//        weiboMessage.mediaObject = imageObject;
//
//        SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
//        request.transaction = String.valueOf(System.currentTimeMillis());
//        request.multiMessage = weiboMessage;
//        iWeiboShareAPI.sendRequest(activity, request);//发送请求消息到微博，唤起微博分享界面
//    }

    /**
     * 分享图片给微信朋友，需导入微信sdk
     * @param context
     * @param imagePath
     */
    public static void shareToWeChat_image(Context context, String imagePath){
        IWXAPI api;
        api = WXAPIFactory.createWXAPI(context, WEIXIN_APP_ID);
        WXImageObject imgObj = new WXImageObject();
        imgObj.setImagePath(imagePath);

        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = imgObj;

        Bitmap bmp = BitmapFactory.decodeFile(imagePath);
        Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, 150, 150, true);//注意大小不能比原图小
        bmp.recycle();
        msg.thumbData = bmpToByteArray(thumbBmp, true);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("img");
        req.message = msg;
        req.scene = SendMessageToWX.Req.WXSceneSession;
        api.sendReq(req);
    }

    /**
     * 分享图片到朋友圈，需导入sdk
     * @param context
     * @param imagePath
     */
    public static void shareToTimeline_image(Context context, String imagePath){
        IWXAPI api;
        api = WXAPIFactory.createWXAPI(context, WEIXIN_APP_ID);

        WXImageObject imgObj = new WXImageObject();
        imgObj.setImagePath(imagePath);

        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = imgObj;
        Bitmap bmp = BitmapFactory.decodeFile(imagePath);
        Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, 150, 150, true);//注意大小不能比原图小
        bmp.recycle();
        msg.thumbData = bmpToByteArray(thumbBmp, true);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("img");
        req.message = msg;
        req.scene = SendMessageToWX.Req.WXSceneTimeline;
        api.sendReq(req);
    }

    /**
     * 分享文字给微信朋友，需导入微信sdk
     * @param context
     * @param text
     */
    public static void shareToWeChat_text(Context context, String text){
        IWXAPI api;
        api = WXAPIFactory.createWXAPI(context, WEIXIN_APP_ID);
        WXTextObject textObject = new WXTextObject();
        textObject.text = text;

        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = textObject;
        msg.description = text;//必须设置，否则没反应

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("text");
        req.message = msg;
        req.scene = SendMessageToWX.Req.WXSceneSession;
        api.sendReq(req);
    }

    /**
     * 分享文字到朋友圈，需导入微信sdk
     * @param context
     * @param text
     */
    public static void shareToTimeline_text(Context context, String text){
        IWXAPI api;
        api = WXAPIFactory.createWXAPI(context, WEIXIN_APP_ID);
        WXTextObject textObject = new WXTextObject();
        textObject.text = text;

        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = textObject;
//        msg.description = text;//必须设置，否则没反应

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("text");
        req.message = msg;
        req.scene = SendMessageToWX.Req.WXSceneTimeline;
        api.sendReq(req);
    }

    /**
     * 分享网页到朋友圈，需导入微信sdk
     * @param context
     * @param url
     */
    public static void shareToWeChat_web(Context context, String url){
        IWXAPI api;
        api = WXAPIFactory.createWXAPI(context, WEIXIN_APP_ID);
        WXWebpageObject wxWebpageObject = new WXWebpageObject();
        wxWebpageObject.webpageUrl = url;

        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = wxWebpageObject;
        msg.description = "网页描述";
        Bitmap thumb = BitmapFactory.decodeResource(context.getResources(),R.drawable.ic_launcher);
        msg.thumbData = bmpToByteArray(thumb, true);//设置缩略图

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("webpage");
        req.message = msg;
        req.scene = SendMessageToWX.Req.WXSceneSession;
        api.sendReq(req);
    }

    /**
     * 分享网页到朋友圈，需导入微信sdk
     * @param context
     * @param url
     */
    public static void shareToTimeline_web(Context context, String url){
        IWXAPI api;
        api = WXAPIFactory.createWXAPI(context, WEIXIN_APP_ID);
        WXWebpageObject wxWebpageObject = new WXWebpageObject();
        wxWebpageObject.webpageUrl = url;

        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = wxWebpageObject;
        msg.description = "网页描述";
        Bitmap thumb = BitmapFactory.decodeResource(context.getResources(),R.drawable.ic_launcher);
        msg.thumbData = bmpToByteArray(thumb, true);//设置缩略图

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("webpage");
        req.message = msg;
        req.scene = SendMessageToWX.Req.WXSceneTimeline;
        api.sendReq(req);
    }

    /**
     * 分享网页到朋友圈，需导入微信sdk
     * @param context
     * @param url
     */
    public static void shareToWeChat_music(Context context, String url){
        IWXAPI api;
        api = WXAPIFactory.createWXAPI(context, WEIXIN_APP_ID);
        WXMusicObject musicObject = new WXMusicObject();
        musicObject.musicUrl = url;

        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = musicObject;
        msg.description = "音乐描述";
        msg.title = "音乐标题";
        Bitmap thumb = BitmapFactory.decodeResource(context.getResources(),R.drawable.ic_launcher);
        msg.thumbData = bmpToByteArray(thumb, true);//设置音乐缩略图

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("music");
        req.message = msg;
        req.scene = SendMessageToWX.Req.WXSceneSession;
        api.sendReq(req);
    }

    /**
     * 分享网页到朋友圈，需导入微信sdk
     * @param context
     * @param url
     */
    public static void shareToTimeline_music(Context context, String url){
        IWXAPI api;
        api = WXAPIFactory.createWXAPI(context, WEIXIN_APP_ID);
        WXMusicObject musicObject = new WXMusicObject();
        musicObject.musicUrl = url;

        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = musicObject;
        msg.description = "音乐描述";
        msg.title = "音乐标题";
        Bitmap thumb = BitmapFactory.decodeResource(context.getResources(),R.drawable.ic_launcher);
        msg.thumbData = bmpToByteArray(thumb, true);//设置音乐缩略图

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("music");
        req.message = msg;
        req.scene = SendMessageToWX.Req.WXSceneTimeline;
        api.sendReq(req);
    }



    //使用微信sdk分享图片时会用到的方法
    private static byte[] bmpToByteArray(Bitmap bmp, boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, output);//注意跟照片格式对应
        if (needRecycle) {
            bmp.recycle();
        }

        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    //使用微信sdk分享图片时会用到的方法
    private static String buildTransaction(String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }
}

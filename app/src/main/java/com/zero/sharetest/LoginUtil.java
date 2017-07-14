package com.zero.sharetest;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.sina.weibo.sdk.WbSdk;
import com.sina.weibo.sdk.auth.AccessTokenKeeper;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WbAuthListener;
import com.sina.weibo.sdk.auth.WbConnectErrorMessage;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.tencent.connect.UserInfo;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;

import static android.content.ContentValues.TAG;

/**
 * Created by zero on 2017/7/13.
 */

public class LoginUtil {
    private static String QQ_APP_ID="1105632898";
    private static String WEIXIN_APP_ID ="wx8954b928da52c490";
    private static String SINA_APP_ID ="1042075203";

    private static Tencent mTencent;


    /**
     * 用于新浪微博授权，使用时务必在activity中的onActivityResult方法中调用
     * mSsoHandler.authorizeCallBack(requestCode, resultCode, data)方法，否则无法获取数据
     * @param activity
     * @return
     */
    public static SsoHandler LoginSina(final Activity activity){
        //第三个第四个参数详情看开发文档，一般不用改
        WbSdk.install(activity,new AuthInfo(activity,SINA_APP_ID,"https://api.weibo.com/oauth2/default.html",null));
        SsoHandler mSsoHandler = new SsoHandler(activity);
        mSsoHandler.authorize(new WbAuthListener() {
            @Override
            public void onSuccess(final Oauth2AccessToken oauth2AccessToken) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Oauth2AccessToken mAccessToken = oauth2AccessToken;
                        if (mAccessToken.isSessionValid()) {
                            // 获取信息
//                                    updateTokenView(false);
                            String date = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss").format(
                                    new java.util.Date(mAccessToken.getExpiresTime()));
                            Log.i("ssss","Uid:"+mAccessToken.getUid()+
                                    "\nToken:"+mAccessToken.getToken() + "\nRefreshToken:"+mAccessToken.getRefreshToken()
                                    + "\nPhoneNum:"+mAccessToken.getPhoneNum()
                                    + "\nExpiresTime:"+date);

                            // 保存 Token 到 SharedPreferences
                            AccessTokenKeeper.writeAccessToken(activity, mAccessToken);
                            Toast.makeText(activity,"登陆成功",Toast.LENGTH_SHORT).show();

                            //获取用户信息
//                            //url后的键值对查看官网api接口按需要修改
//                            OkHttpClient client = new OkHttpClient();
//                            Request request = new Request.Builder()
//                                    .url("https://api.weibo.com/2/eps/user/info.json"+"?access_token="+mAccessToken.getToken()+"&uid="+mAccessToken.getUid())
//                                    .build();
//                            Call call = client.newCall(request);
//                            call.enqueue(new Callback() {
//                                @Override
//                                public void onFailure(Call call, IOException e) {
//
//                                }
//
//                                @Override
//                                public void onResponse(Call call, Response response) throws IOException {
//                                    ResponseBody body = response.body();
//                                    Log.i("ssss",body.string());
//                                }
//                            });
                        }
                    }
                });
            }

            @Override
            public void cancel() {
                Toast.makeText(activity,"取消登陆",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(WbConnectErrorMessage wbConnectErrorMessage) {
                Toast.makeText(activity,"登陆失败",Toast.LENGTH_SHORT).show();
            }
        });
        return mSsoHandler;
    }

    public static IUiListener LoginQQ(final Activity activity){
        mTencent = Tencent.createInstance(QQ_APP_ID,activity);
        IUiListener iUiListener = new IUiListener() {
            @Override
            public void onComplete(Object o) {
                JSONObject jsonObject = (JSONObject) o;
                //获取token、openId等信息
                try {
                    String token = jsonObject.getString(Constants.PARAM_ACCESS_TOKEN);
                    String expires = jsonObject.getString(Constants.PARAM_EXPIRES_IN);
                    String openId = jsonObject.getString(Constants.PARAM_OPEN_ID);
                    Log.i("ssss","token:"+token+"\n"+"expires:"+expires+"\n"+"openId:"+openId);

                    //这一步很重要，没有设置token和openid，则无法获取用户资料
                    if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(expires)
                            && !TextUtils.isEmpty(openId)) {
                        mTencent.setAccessToken(token, expires);
                        mTencent.setOpenId(openId);
                    }
                    //getQQInfo(activity);//可以在此处直接获取用户资料
                } catch(Exception e) {
                }
            }

            @Override
            public void onError(UiError uiError) {
                Toast.makeText(activity, "登陆失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
                Toast.makeText(activity, "取消登陆", Toast.LENGTH_SHORT).show();
            }
        };
        if (!mTencent.isSessionValid()){
            //直接调用QQ app授权
            mTencent.login(activity, "all", iUiListener);
        }else {
            //在本app内授权
            mTencent.loginServerSide(activity, "all", iUiListener);
        }
        return iUiListener;
    }

    public static void getQQInfo(final Activity activity){
        if(mTencent!=null){
            UserInfo info = new UserInfo(activity, mTencent.getQQToken());
            info.getUserInfo(new IUiListener() {
                @Override
                public void onComplete(Object o) {
                    final JSONObject json = (JSONObject)o;
                    try {
                        //tips 可以在此处用SharedPreferences把要用到的变量存起来
                        //还有qq等级等其他信息可以获取，按需要查看官方文档获取或打印json.toString查看
                        String nickname = json.getString("nickname");//获取昵称
                        String sex = json.getString("gender");//获取性别
                        Log.i("ssss",nickname+sex);
                        Toast.makeText(activity,nickname+sex,Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    new Thread(){
                        @Override
                        public void run() {
                            if(json.has("figureurl")){
                                Bitmap bitmap = null;
                                try {
                                    //获取头像
                                    bitmap = getbitmap(json.getString("figureurl_qq_2"));
                                    saveBitmap(bitmap, getStorePath(activity));
                                } catch (JSONException e) {

                                }
                            }
                        }

                    }.start();
                }
                @Override
                public void onError(UiError uiError) {

                }

                @Override
                public void onCancel() {

                }
            });
        }
    }

    /**
     * 根据一个网络连接(String)获取bitmap图像
     *
     * @param imageUri
     * @return
     * @throws MalformedURLException
     */
    public static Bitmap getbitmap(String imageUri) {
        Log.v("ssss", "getbitmap:" + imageUri);
        // 显示网络上的图片
        Bitmap bitmap = null;
        try {
            URL myFileUrl = new URL(imageUri);
            HttpURLConnection conn = (HttpURLConnection) myFileUrl
                    .openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();

            Log.v("ssss", "image download finished." + imageUri);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            bitmap = null;
        } catch (IOException e) {
            e.printStackTrace();
            Log.v("ssss", "getbitmap bmp fail---");
            bitmap = null;
        }
        return bitmap;
    }

    /** 保存头像 */
    public static void saveBitmap(Bitmap bitmap, String storePath) {
        Log.e(TAG, "保存图片");
        File f = new File(storePath);
        if (f.exists()) {
            f.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
            Log.i(TAG, "已经保存");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //获取qq头像保存路径
    public static String getStorePath(Activity activity){
        boolean sdExist = Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
        String path;
        if (sdExist) {
            if (activity.getExternalFilesDir(null) != null) {
                path = activity.getExternalFilesDir(null).getAbsolutePath()  + "/QQicon.png";  //设置音频文件保存路径
            } else {
                path = activity.getFilesDir().getAbsolutePath() + "/QQicon.png";
            }
        } else {
            path = activity.getFilesDir().getAbsolutePath() + "/QQicon.png";
        }
        return path;
    }
}

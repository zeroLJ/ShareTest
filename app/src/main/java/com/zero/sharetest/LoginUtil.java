package com.zero.sharetest;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.sina.weibo.sdk.WbSdk;
import com.sina.weibo.sdk.auth.AccessTokenKeeper;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WbAuthListener;
import com.sina.weibo.sdk.auth.WbConnectErrorMessage;
import com.sina.weibo.sdk.auth.sso.SsoHandler;

import java.text.SimpleDateFormat;

/**
 * Created by zero on 2017/7/13.
 */

public class LoginUtil {
    private static String QQ_APP_ID="1105632898";
    private static String WEIXIN_APP_ID ="wx8954b928da52c490";
    private static String SINA_APP_ID ="1042075203";


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



}

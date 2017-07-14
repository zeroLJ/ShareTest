package com.zero.sharetest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.share.WbShareCallback;
import com.sina.weibo.sdk.share.WbShareHandler;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;

public class MainActivity extends AppCompatActivity  implements WbShareCallback{
    //新浪分享接口实例，用于接收返回数据
    WbShareHandler shareHandler;
    //新浪授权接口实例，用于接收返回数据
    SsoHandler mSsoHandler;
    //接收QQ授权结果
    IUiListener iUiListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void onClick(View view) {
        switch (view.getId()){
            case R.id.shareToWeChat_image:
                ShareUtil.saveCurrentImage(this);
                ShareUtil.shareToWeChat_image(this,ShareUtil.getImagePath(this));
                break;
            case R.id.shareToTimeline_image:
                ShareUtil.saveCurrentImage(this);
                ShareUtil.shareToTimeline_image(this,ShareUtil.getImagePath(this));
                break;
            case R.id.shareToWeChat_text:
                ShareUtil.shareToWeChat_text(this,"来自应用的文字");
                break;
            case R.id.shareToTimeline_text:
                ShareUtil.shareToTimeline_text(this,"来自应用的文字");
                break;
            case R.id.shareToWeChat_web:
                ShareUtil.shareToWeChat_web(this,"www.baidu.com");
                break;
            case R.id.shareToTimeline_web:
                ShareUtil.shareToTimeline_web(this,"www.baidu.com");
                break;
            case R.id.shareToWeChat_music:
                ShareUtil.shareToWeChat_music(this,"http://www.kugou.com/song/#hash=99632FF0CF903BA89A8E03234F6B9530&album_id=2783406");
                break;
            case R.id.shareToTimeline_music:
                ShareUtil.shareToTimeline_music(this,"www.baidu.com");
                break;
            case R.id.shareToQQ_image:
                ShareUtil.saveCurrentImage(this);
                iUiListener = ShareUtil.shareToQQ_image(this,ShareUtil.getImagePath(this));
                break;
            case R.id.shareToQQ_music:
                ShareUtil.saveCurrentImage(this);
                iUiListener = ShareUtil.shareToQQ_music(this,ShareUtil.getImagePath(this),
                        "http://www.kugou.com/song/#hash=99632FF0CF903BA89A8E03234F6B9530&album_id=2783406", "http://www.baidu.com");
                break;
            case R.id.shareToQQ_web:
                ShareUtil.saveCurrentImage(this);
                iUiListener = ShareUtil.shareToQQ_web(this,ShareUtil.getImagePath(this), "http://www.baidu.com");
                break;
            case R.id.shareToSINA:
                ShareUtil.saveCurrentImage(this);
                shareHandler = ShareUtil.shareToSINA(this,ShareUtil.getImagePath(this));
                break;
            case R.id.loginSINA:
                mSsoHandler = LoginUtil.LoginSina(this);
                break;
            case R.id.loginQQ:
                iUiListener = LoginUtil.LoginQQ(this);
                break;
            case R.id.getQQInfo:
                LoginUtil.getQQInfo(this);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
            mSsoHandler = null;
        }
        if (iUiListener!= null){
            //此句很关键，不写此句则无法获取QQ授权后返回的信息
            Tencent.onActivityResultData(requestCode,resultCode,data,iUiListener);
            iUiListener = null;
        }
    }

    //以下方法都是新浪微博分享相关
    @Override
    public void onWbShareSuccess() {
        Toast.makeText(this,"分享成功",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onWbShareCancel() {
        Toast.makeText(this,"取消分享",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onWbShareFail() {
        Toast.makeText(this,"分享失败",Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        shareHandler.doResultIntent(intent,this);
    }
}

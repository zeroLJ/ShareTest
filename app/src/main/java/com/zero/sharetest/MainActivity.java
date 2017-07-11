package com.zero.sharetest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

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
        }
    }
}

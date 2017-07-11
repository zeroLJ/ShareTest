package com.zero.sharetest;

import android.app.Application;

import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

/**
 * Created by WatchVoice04 on 2017/7/11.
 */
public class app extends Application {
    private static final String APP_ID = "wx8954b928da52c490";
    private IWXAPI api;
    @Override
    public void onCreate() {
        super.onCreate();
        api = WXAPIFactory.createWXAPI(this, APP_ID, true);
        api.registerApp(APP_ID);
    }
}


package com.zero.sharetest.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.zero.sharetest.R;

/**
 * 此类为微信返回窗口，必须放在此路径
 */
public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
	private static final String APP_ID = "wx8954b928da52c490";
    private IWXAPI api;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);
    	api = WXAPIFactory.createWXAPI(this, APP_ID, false);
		api.handleIntent(getIntent(), this);//此句很重要
    }

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
        api.handleIntent(intent, this);
	}

	//从应用发到微信的信息
	@Override
	public void onReq(BaseReq req) {
		Log.i("ssss","reqType"+req.getType());
		switch (req.getType()) {
			case ConstantsAPI.COMMAND_SENDMESSAGE_TO_WX:
				break;
			case ConstantsAPI.COMMAND_GETMESSAGE_FROM_WX:
				break;
			case ConstantsAPI.COMMAND_SHOWMESSAGE_FROM_WX:
				break;
			default:
				break;
		}
	}

	//从微信返回来的信息
	@Override
	public void onResp(BaseResp resp) {
		String result = "null";
		//可以通过type的值再细分下面switch要执行的内容
		Log.i("ssss","respType"+resp.getType());
		switch (resp.errCode) {
			case BaseResp.ErrCode.ERR_OK:
				result = "分享成功";
				break;
			case BaseResp.ErrCode.ERR_USER_CANCEL:
				result = "取消分享";
				break;
			case BaseResp.ErrCode.ERR_AUTH_DENIED:
				result = "DENIED";
				break;
			case BaseResp.ErrCode.ERR_UNSUPPORT:
				result = "不支持分享";
				break;
			default:
				break;
		}
		Toast.makeText(this, result, Toast.LENGTH_LONG).show();
		finish();
	}
	

}
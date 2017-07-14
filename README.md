# ShareTest
微信、qq、微博分享demo

微信登录、获取信息部分需要开发者认证，暂时没写。

微博无法获取用户信息，不知是否因为应用未审核的原因。

 
MainActivity 里这部分代码很重要，否则无法获取返回信息
 
 
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

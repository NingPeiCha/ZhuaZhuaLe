package com.zhuazhuale.changsha.module.login.presenter;

import android.content.Context;
import android.content.Intent;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.zhuazhuale.changsha.app.MyApplication;
import com.zhuazhuale.changsha.app.constant.ICallListener;
import com.zhuazhuale.changsha.module.home.Bean.LoginInfoBean;
import com.zhuazhuale.changsha.module.home.model.AddressModel;
import com.zhuazhuale.changsha.module.home.model.OrderModel;
import com.zhuazhuale.changsha.module.home.ui.HomeActivity;
import com.zhuazhuale.changsha.module.login.model.LoginModel;
import com.zhuazhuale.changsha.module.login.ui.ILoginView;
import com.zhuazhuale.changsha.presenter.base.BasePresenter;
import com.zhuazhuale.changsha.util.Constant;
import com.zhuazhuale.changsha.util.HashCoderUtil;
import com.zhuazhuale.changsha.util.log.LogUtil;
import com.zhuazhuale.changsha.wxapi.WXEntryActivity;

import java.lang.reflect.Type;
import java.util.Date;

/**
 * Created by wb on 2017/12/14.
 */

public class LoginPresenter extends BasePresenter<ILoginView> {


    private final LoginModel loginModel;

    public LoginPresenter(ILoginView iLoginView) {
        super(iLoginView);
        loginModel = LoginModel.getInstance();
        regToWx();
    }

    /** -------------------------微信第三方登录---------------------- */
    /**
     * 微信平台应用授权登录接入代码示例
     */
    //---------------------------微信第三方相关
    public static IWXAPI api;

    private void regToWx() {
        // 通过WXAPIFactory工厂,获得IWXAPI的实例
        api = WXAPIFactory.createWXAPI((Context) mIView, "wx6a6349cd0ccf0b09", true);
        // 将应用的appid注册到微信
        api.registerApp("wx6a6349cd0ccf0b09");
    }

    //获取微信访问getCode
    public void getCode() {
        final SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "carjob_wx_login";
        api.sendReq(req);

    }

    /**
     * -------------------------微信第三方登录结束--------------------
     */
    public void initWX(String code, String APPID, String secret) {
        loginModel.getWX(code, APPID, secret, new ICallListener<String>() {
            @Override
            public void callSuccess(String url) {
             getWXUserInfo(url);
            }

            @Override
            public void callFailed() {
                mIView.showFailed(1);
            }

            @Override
            public void onFinish() {
                mIView.showFinish();
            }
        });
    }

    private void getWXUserInfo(String url) {
        loginModel.setUrl(url, new ICallListener<String>() {
            @Override
            public void callSuccess(String s) {
                Gson gson = new Gson();
                Type type = new TypeToken<WeiXinLoginGetUserinfoBean>() {
                }.getType();
                WeiXinLoginGetUserinfoBean bean = gson.fromJson(s, type);
                LogUtil.e("获取用户信息成功：\n" + "昵称:" + bean.getNickname() + "\n头像路径" + bean.getHeadimgurl());
                waWaLeLogin(bean);
            }

            @Override
            public void callFailed() {
                mIView.showFailed(2);
            }

            @Override
            public void onFinish() {
                mIView.showFinish();
            }
        });
    }

    private void waWaLeLogin(WeiXinLoginGetUserinfoBean bean) {
        loginModel.login(bean, new ICallListener<String>() {
            @Override
            public void callSuccess(String s) {
                Gson gson = new Gson();
                LoginInfoBean infoBean = gson.fromJson(s, LoginInfoBean.class);

                mIView.goToHomeActivity(infoBean);
            }

            @Override
            public void callFailed() {
                mIView.showFailed(3);
            }

            @Override
            public void onFinish() {
                mIView.showFinish();
            }
        });
    }


}

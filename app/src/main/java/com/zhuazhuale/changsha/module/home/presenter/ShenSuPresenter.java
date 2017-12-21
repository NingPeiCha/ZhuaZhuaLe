package com.zhuazhuale.changsha.module.home.presenter;

import com.google.gson.Gson;
import com.zhuazhuale.changsha.app.constant.ICallListener;
import com.zhuazhuale.changsha.module.home.Bean.EditAddressBean;
import com.zhuazhuale.changsha.module.home.model.ShenSuModel;
import com.zhuazhuale.changsha.module.home.ui.IShenSuView;
import com.zhuazhuale.changsha.presenter.base.BasePresenter;
import com.zhuazhuale.changsha.util.ToastUtil;
import com.zhuazhuale.changsha.util.log.LogUtil;

/**
 * 申诉
 * Created by 丁琪 on 2017/12/20.
 */

public class ShenSuPresenter extends BasePresenter<IShenSuView> {
    private String TAG = getClass().getName();

    private final ShenSuModel shenSuModel;

    public ShenSuPresenter(IShenSuView iShenSuView) {
        super(iShenSuView);
        shenSuModel = ShenSuModel.getInstance();
    }

    public void initAppeal(String vDeviceID, String vGrabID, String vRemark, String vVideoUrl) {
        shenSuModel.getAppeal(vDeviceID, vGrabID, vRemark, vVideoUrl, new ICallListener<String>() {
            @Override
            public void callSuccess(String s) {
                LogUtil.e(TAG, s);
                ToastUtil.show(s);
                Gson gson = new Gson();
                EditAddressBean newCPBean = gson.fromJson(s, EditAddressBean.class);
                mIView.showSuccess(newCPBean);
            }

            @Override
            public void callFailed() {
                mIView.showFailed();
            }

            @Override
            public void onFinish() {
                LogUtil.e(TAG, "接口结束");
                mIView.showFinish();
            }
        });
    }


}

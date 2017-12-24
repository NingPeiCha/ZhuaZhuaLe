package com.zhuazhuale.changsha.module.vital.presenter;

import com.google.gson.Gson;
import com.zhuazhuale.changsha.app.constant.ICallListener;
import com.zhuazhuale.changsha.module.home.Bean.NewCPBean;
import com.zhuazhuale.changsha.module.home.model.MineModel;
import com.zhuazhuale.changsha.module.home.ui.IMineView;
import com.zhuazhuale.changsha.module.vital.bean.StartGameBean;
import com.zhuazhuale.changsha.module.vital.model.PlayModel;
import com.zhuazhuale.changsha.module.vital.ui.IPlayView;
import com.zhuazhuale.changsha.presenter.base.BasePresenter;
import com.zhuazhuale.changsha.util.log.LogUtil;

/**
 * 个人中心
 * Created by 丁琪 on 2017/12/20.
 */

public class PlayPresenter extends BasePresenter<IPlayView> {
    private String TAG = getClass().getName();

    private final PlayModel playModel;

    public PlayPresenter(IPlayView view) {
        super(view);
        playModel = PlayModel.getInstance();
    }

    /**
     * 游戏上机
     *
     * @param vDeviceID
     */
    public void initUpperGame(String vDeviceID) {
        playModel.getUpperGame(vDeviceID, new ICallListener<String>() {
            @Override
            public void callSuccess(String s) {
                LogUtil.e(TAG, s);
                Gson gson = new Gson();
                StartGameBean gameBean = gson.fromJson(s, StartGameBean.class);
                mIView.showStartGame(gameBean);
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

    public void initControlGame(String vDeviceID, String vAction, String vToken, String timeStamp) {
        playModel.getControlGame(vDeviceID, vAction, vToken, timeStamp, new ICallListener<String>() {
            @Override
            public void callSuccess(String s) {
                LogUtil.e(TAG, s);
                Gson gson = new Gson();
//                NewCPBean newCPBean = gson.fromJson(s, NewCPBean.class);
            }

            @Override
            public void callFailed() {

            }

            @Override
            public void onFinish() {
                LogUtil.e(TAG, "接口结束");
            }
        });
    }


}

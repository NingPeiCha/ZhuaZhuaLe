package com.zhuazhuale.changsha.module.vital.ui;

import com.zhuazhuale.changsha.module.home.Bean.NewCPBean;
import com.zhuazhuale.changsha.module.home.Bean.QueryGameBean;
import com.zhuazhuale.changsha.module.vital.bean.StartGameBean;
import com.zhuazhuale.changsha.presenter.iview.IBaseView;

/**
 * 游戏页面
 * Created by Administrator on 2017/12/23 0023.
 */

public interface IPlayView extends IBaseView{
    void showStartGame(StartGameBean gameBean);
    void showFailed();
    void showFinish();

    void showQueryGame(QueryGameBean queryGameBean);

    void showNewCP(NewCPBean newCPBean);
}

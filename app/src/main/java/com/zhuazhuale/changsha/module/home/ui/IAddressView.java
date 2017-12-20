package com.zhuazhuale.changsha.module.home.ui;

import com.zhuazhuale.changsha.module.home.Bean.AddressBean;
import com.zhuazhuale.changsha.presenter.iview.IBaseView;

/**
 * 我的地址列表
 * Created by 丁琪 on 2017/12/20.
 */

public interface IAddressView extends IBaseView {
    void showQueryUserAddress(AddressBean addressBean);

    void showFailed();

    void showFinish();
}

package com.zhuazhuale.changsha.module.home.ui;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhuazhuale.changsha.R;
import com.zhuazhuale.changsha.module.home.Bean.SpoilsBean;
import com.zhuazhuale.changsha.module.home.adapter.AddressAdapter;
import com.zhuazhuale.changsha.module.home.adapter.SpoilsAdapter;
import com.zhuazhuale.changsha.module.home.presenter.SpoilsPresenter;
import com.zhuazhuale.changsha.util.ToastUtil;
import com.zhuazhuale.changsha.view.activity.base.AppBaseActivity;
import com.zhuazhuale.changsha.view.widget.loadlayout.OnLoadListener;
import com.zhuazhuale.changsha.view.widget.loadlayout.OnNoDataListener;
import com.zhuazhuale.changsha.view.widget.loadlayout.State;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 我的战利品
 * Created by 丁琪 on 2017/12/15.
 */

public class SpoilsActivity extends AppBaseActivity implements View.OnClickListener, ISpoilsView {

    @BindView(R.id.rv_spoils_list)
    RecyclerView rv_spoils_list;
    @BindView(R.id.tv_spoils_fahuo)
    TextView tv_spoils_fahuo;
    private Intent intent;
    private SpoilsPresenter presenter;
    private SpoilsAdapter addressAdapter;


    @Override
    protected void setContentLayout() {
        setContentView(R.layout.activity_spoils);
    }

    @Override
    protected void initView() {
        getTvToolbarRight().setText("申请发货");
    }

    @Override
    protected void obtainData() {
        showLoadingDialog();
        presenter = new SpoilsPresenter(this);
        presenter.initQueryUserGoods(9);
//        showAddressList();
        getLoadLayout().setOnLoadListener(new OnLoadListener() {
            @Override
            public void onLoad() {
                presenter.initQueryUserGoods(9);
            }
        });


    }

    @Override
    protected void initEvent() {
        tv_spoils_fahuo.setOnClickListener(this);
        //没有查到数据页面的点击监听事件
        getLoadLayout().setOnNoDataListener(new OnNoDataListener() {
            @Override
            public void onGoTo() {
//                getLoadLayout().setLayoutState(State.LOADING);
                //  回到首页
                getActivityStackManager().exitAllActivityExceptCurrent(HomeActivity.class);
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.tv_spoils_fahuo:
                intent = new Intent(SpoilsActivity.this, EditAddressActivity.class);
                startActivity(intent);
                break;
        }
    }

    /**
     * 抓取记录列表
     */
    private void showAddressList() {
        List<String> strings = new ArrayList<>();
        strings.add("  ");
        strings.add("  ");
        strings.add("  ");
        strings.add("  ");
        strings.add("  ");
        strings.add("  ");


    }


    @Override
    public void showQueryUserGoods(SpoilsBean spoilsBean) {
        if (0 == spoilsBean.getCode()) {
            getLoadLayout().setLayoutState(State.NO_DATA);
            ToastUtil.show(spoilsBean.getInfo());
        } else {
            getLoadLayout().setLayoutState(State.SUCCESS);
            addressAdapter = new SpoilsAdapter(this, spoilsBean.getRows());
            rv_spoils_list.setLayoutManager(new LinearLayoutManager(this));
            rv_spoils_list.setHasFixedSize(false);
            rv_spoils_list.setAdapter(addressAdapter);
        }
    }

    /**
     * 接口结束
     */
    @Override
    public void showFinish() {
        dismissLoadingDialog();
    }

    @Override
    public void showFailed() {
        getLoadLayout().setLayoutState(State.FAILED);
    }


    public void duiHuan(SpoilsBean.RowsBean rowsBean, int position) {
        addressAdapter.removeItem(position);

    }
}

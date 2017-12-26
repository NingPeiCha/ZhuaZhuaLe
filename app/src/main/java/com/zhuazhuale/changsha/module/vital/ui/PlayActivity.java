package com.zhuazhuale.changsha.module.vital.ui;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tencent.rtmp.ITXLivePlayListener;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePlayer;
import com.tencent.rtmp.ui.TXCloudVideoView;
import com.zhuazhuale.changsha.R;
import com.zhuazhuale.changsha.module.home.Bean.DeviceGoodsBean;
import com.zhuazhuale.changsha.module.home.Bean.NewCPBean;
import com.zhuazhuale.changsha.module.home.Bean.QueryGameBean;
import com.zhuazhuale.changsha.module.vital.bean.ControlGameBean;
import com.zhuazhuale.changsha.module.vital.bean.StartGameBean;
import com.zhuazhuale.changsha.module.vital.presenter.PlayPresenter;
import com.zhuazhuale.changsha.util.SoundUtils;
import com.zhuazhuale.changsha.util.ToastUtil;
import com.zhuazhuale.changsha.util.log.LogUtil;
import com.zhuazhuale.changsha.view.activity.base.AppBaseActivity;

import butterknife.BindView;

/**
 * 游戏页面
 * Created by dingqi on 2017/12/23 0023.
 */

public class PlayActivity extends AppBaseActivity implements View.OnClickListener, IPlayView {
    @BindView(R.id.iv_play_startgame)
    ImageView iv_play_startgame;
    @BindView(R.id.iv_play_up)
    ImageView iv_play_up;
    @BindView(R.id.iv_play_left)
    ImageView iv_play_left;
    @BindView(R.id.iv_play_right)
    ImageView iv_play_right;
    @BindView(R.id.iv_play_down)
    ImageView iv_play_down;
    @BindView(R.id.ll_play_caozuo)
    LinearLayout ll_play_caozuo;
    @BindView(R.id.iv_play_catch)
    ImageView iv_play_catch;
    @BindView(R.id.ll_play_open)
    LinearLayout ll_play_open;
    @BindView(R.id.iv_play_change)
    ImageView iv_play_change;
    @BindView(R.id.tv_play_cp)
    TextView tv_play_cp;
    @BindView(R.id.tv_play_bi)
    TextView tv_play_bi;

    private DeviceGoodsBean.RowsBean rowsBean;
    private TXLivePlayer mLivePlayer1;
    private PlayPresenter presenter;
    private StartGameBean.RowsBean gameBeanRows;
    private boolean isPlay = false;//是否可以开始操作
    private String url1;
    private String url2;
    private TXLivePlayer mLivePlayer2;
    private boolean isURL = false;  // 判断是否 是直播视频1
    private TXCloudVideoView mView2;
    private TXCloudVideoView mView1;
    private boolean isFirst = true; //判断是否第一次进入,主要为了创建直播视频二
    //  FORWARD BACKWARD LEFT  RIGHT
    private String up = "FORWARD";
    private String down = "BACKWARD";
    private String left = "LEFT";
    private String right = "RIGHT";
    private int newCP = 0;
    private boolean isOpen = false;// 判断游戏机器的状态,能否开始游戏
    private ColorMatrixColorFilter colorFilter;
    private SoundUtils soundUtils;
    private int bgvoice = 0;
    private int readygo = 1;
    private int move = 2;
    private int fail = 3;
    private int success = 4;
    private int start = 5;
    private int take = 6;
    private Dialog dialog;
    private TextView tv_dialog_info;

    @Override
    protected void setContentLayout() {
        setContentView(R.layout.activity_play);
        Intent intent = getIntent();
        rowsBean = (DeviceGoodsBean.RowsBean) intent.getSerializableExtra("DeviceGoods");
        url1 = rowsBean.getF_Camera1();
        url2 = rowsBean.getF_Camera2();
    }

    @Override
    protected void initView() {
        showLoadingDialog();
        //mPlayerView即step1中添加的界面view
        mView1 = (TXCloudVideoView) findViewById(R.id.video_view1);
        mView2 = (TXCloudVideoView) findViewById(R.id.video_view2);
        mView2.setVisibility(View.GONE);
        creatTXLivePlayer1();
        //让图片变灰色
        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0);//饱和度 0灰色 100过度彩色，50正常
        colorFilter = new ColorMatrixColorFilter(matrix);
        iv_play_startgame.setColorFilter(colorFilter);
        tv_play_bi.setText(rowsBean.getF_Price() + "币 / 次");
        //使用的时候先初始化一个声音播放工具
        soundUtils = new SoundUtils(this, SoundUtils.MEDIA_SOUND);
        //然后添加声音进去,参数是添加声音的编号和资源id
        soundUtils.putSound(bgvoice, R.raw.bgvoice);
        soundUtils.putSound(readygo, R.raw.readygo);
        soundUtils.putSound(move, R.raw.move);
        soundUtils.putSound(fail, R.raw.fail);
        soundUtils.putSound(success, R.raw.success);
        soundUtils.putSound(start, R.raw.start);
        soundUtils.putSound(take, R.raw.take);

        creatMyDialog();


    }

    private void creatMyDialog() {
        dialog = new Dialog(this, R.style.MyDialog);
        dialog.setContentView(R.layout.dialog_play);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        TextView tv_dialog_cancel = (TextView) dialog.findViewById(R.id.tv_dialog_cancel);
        TextView tv_dialog_ok = (TextView) dialog.findViewById(R.id.tv_dialog_ok);
        tv_dialog_info = (TextView) dialog.findViewById(R.id.tv_dialog_info);
        tv_dialog_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                presenter.initLowerGame(rowsBean.getF_ID());
                isPlay = false;
                ll_play_open.setVisibility(View.VISIBLE);
                ll_play_caozuo.setVisibility(View.INVISIBLE);
                dialog.dismiss();
            }
        });
        tv_dialog_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoadingDialog();
                soundUtils.playSound(start, 0);
                presenter.initUpperGame(rowsBean.getF_ID());
                dialog.dismiss();
            }
        });
    }

    private void creatTXLivePlayer2() {

        //创建player对象 setRenderMode
        mLivePlayer2 = new TXLivePlayer(getContext());
        //将图像等比例缩放，适配最长边，缩放后的宽和高都不会超过显示区域，居中显示，画面可能会留有黑边。
//        mLivePlayer.setRenderMode(TXLiveConstants.RENDER_MODE_ADJUST_RESOLUTION;);
        mLivePlayer2.setRenderMode(TXLiveConstants.RENDER_MODE_FULL_FILL_SCREEN);//填充
        mLivePlayer2.setRenderRotation(TXLiveConstants.RENDER_ROTATION_LANDSCAPE);
        //关键player对象与界面view
        mLivePlayer2.setPlayerView(mView2);
        //软解和硬解的切换需要在切换之前先stopPlay，切换之后再startPlay，否则会产生比较严重的花屏问题。
        mLivePlayer2.stopPlay(true);
        mLivePlayer2.enableHardwareDecode(true);
        mLivePlayer2.startPlay(url2, TXLivePlayer.PLAY_TYPE_LIVE_RTMP); //推荐FLV
        //监听第二个直播流拉流事件
        playerListen2();
    }

    /**
     * 创建直播视频
     */
    private void creatTXLivePlayer1() {
        //创建player对象 setRenderMode
        mLivePlayer1 = new TXLivePlayer(getContext());
        //将图像等比例缩放，适配最长边，缩放后的宽和高都不会超过显示区域，居中显示，画面可能会留有黑边。
//        mLivePlayer.setRenderMode(TXLiveConstants.RENDER_MODE_ADJUST_RESOLUTION;);
        mLivePlayer1.setRenderMode(TXLiveConstants.RENDER_MODE_FULL_FILL_SCREEN);//填充
        mLivePlayer1.setRenderRotation(TXLiveConstants.RENDER_ROTATION_LANDSCAPE);
        //关键player对象与界面view
        mLivePlayer1.setPlayerView(mView1);
        //软解和硬解的切换需要在切换之前先stopPlay，切换之后再startPlay，否则会产生比较严重的花屏问题。
        mLivePlayer1.stopPlay(true);
        mLivePlayer1.enableHardwareDecode(true);
        mLivePlayer1.startPlay(url1, TXLivePlayer.PLAY_TYPE_LIVE_RTMP); //推荐FLV
    }

    @Override
    protected void obtainData() {
        presenter = new PlayPresenter(this);
        //查询游戏币数量
        presenter.initNewCP();
        //查询游戏的状态
        presenter.initQueryGame(rowsBean.getF_ID());
    }

    /**
     * 查询游戏币数量
     *
     * @param newCPBean
     */
    @Override
    public void showNewCP(NewCPBean newCPBean) {
        if (newCPBean.getCode() == 1) {
            newCP = newCPBean.getRows().getCP();
            tv_play_cp.setText(newCP + "");
        } else {
            ToastUtil.show(newCPBean.getInfo());
            tv_play_cp.setText(newCP);
        }
    }

    /**
     * 操作返回的数据
     *
     * @param controlGameBean
     * @param vAction
     */
    @Override
    public void showControlGame(ControlGameBean controlGameBean, String vAction) {

        if (vAction.equals("DOWN")) {
            if (controlGameBean.getCode() == 1) {
                //需要播放的地方执行这句即可, 参数分别是声音的编号和循环次数
                soundUtils.playSound(success, 0);
                tv_dialog_info.setText("恭喜你,抓取成功!");
            } else {
                soundUtils.playSound(fail, 0);
                tv_dialog_info.setText("抓取失败,再接再厉!");
            }
            dialog.show();
        } else {
            //需要播放的地方执行这句即可, 参数分别是声音的编号和循环次数
            soundUtils.playSound(move, 0);
        }

    }

    /**
     * 查询机器的状态
     *
     * @param queryGameBean
     */
    @Override
    public void showQueryGame(QueryGameBean queryGameBean) {
        if (queryGameBean.getCode() == 1) {
            QueryGameBean.RowsBean rowsBean = queryGameBean.getRows();
            switch (rowsBean.getVStatus()) {
                case 1:
                    //空闲中,可以上机
                    isOpen = true;
                    ColorMatrix matrix1 = new ColorMatrix();
                    matrix1.setSaturation(1);//饱和度 0灰色 100过度彩色，50正常
                    colorFilter = new ColorMatrixColorFilter(matrix1);
                    iv_play_startgame.setColorFilter(colorFilter);
                    break;
                case 2:
                    //其他用户正在游戏中
                    isOpen = false;
                    ColorMatrix matrix = new ColorMatrix();
                    matrix.setSaturation(50);//饱和度 0灰色 100过度彩色，50正常
                    colorFilter = new ColorMatrixColorFilter(matrix);
                    iv_play_startgame.setColorFilter(colorFilter);
                    break;
                case 3:
                    break;
            }
        } else {
            ToastUtil.show(queryGameBean.getInfo());
        }
    }


    @Override
    protected void initEvent() {
        //监听第一个直播流拉流事件
        playerListen1();
        iv_play_startgame.setOnClickListener(this);
        iv_play_up.setOnClickListener(this);
        iv_play_left.setOnClickListener(this);
        iv_play_right.setOnClickListener(this);
        iv_play_down.setOnClickListener(this);
        iv_play_catch.setOnClickListener(this);
        iv_play_change.setOnClickListener(this);

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_play_startgame:

                if (isOpen) {
                    showLoadingDialog();
                    soundUtils.playSound(start, 0);
                    presenter.initUpperGame(rowsBean.getF_ID());
                } else {
                    ToastUtil.show("还有其他玩家在玩,请稍等!");
                }

                break;
            case R.id.iv_play_up:

                ControlGame(up);
                break;
            case R.id.iv_play_down:
                ControlGame(down);
                break;
            case R.id.iv_play_left:
                ControlGame(left);
                break;
            case R.id.iv_play_right:
                ControlGame(right);
                break;
            case R.id.iv_play_catch:
                soundUtils.playSound(take, 0);
                ControlGame("DOWN");
                break;
            case R.id.iv_play_change:

                if (isURL) {
                    ToastUtil.show("true");
                    isURL = false;
                    mView1.setVisibility(View.VISIBLE);
                    mView2.setVisibility(View.GONE);
                    //  FORWARD BACKWARD LEFT  RIGHT
                    //改变方向
                    up = "FORWARD";
                    down = "BACKWARD";
                    left = "LEFT";
                    right = "RIGHT";
                } else {
                    ToastUtil.show("false");
                    isURL = true;
                    mView1.setVisibility(View.GONE);
                    mView2.setVisibility(View.VISIBLE);
                    //改变方向
                    up = "LEFT";
                    down = "RIGHT";
                    left = "BACKWARD";
                    right = "FORWARD";
                }
                break;
        }

    }

    /**
     * 操作方向
     *
     * @param forward
     */
    private void ControlGame(String forward) {
        if (isPlay) {
            presenter.initControlGame(rowsBean.getF_ID(), forward, gameBeanRows.getToken(), gameBeanRows.getTimestamp() + "");
            if (forward.equals("DOWN")) {
                isPlay = false;//正在抓取,不能操作了
            }
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //判断是否有流,再关闭,不然会报错
        if (mLivePlayer1.isPlaying()) {
            mLivePlayer1.stopPlay(true); // true代表清除最后一帧画面

        }
        if (mLivePlayer2.isPlaying()) {
            mLivePlayer2.stopPlay(true); // true代表清除最后一帧画面

        }
        // 停止背景音乐
        soundUtils.stopSound();
//        soundUtils = null;
        mView1.onDestroy();
        mView2.onDestroy();
    }

    /**
     * 上机成功
     *
     * @param gameBean
     */
    @Override
    public void showStartGame(StartGameBean gameBean) {
        gameBeanRows = gameBean.getRows();
        isPlay = false;
        switch (gameBean.getCode()) {
            case 1://成功
                ToastUtil.show("开始游戏吧!");
                //需要播放的地方执行这句即可, 参数分别是声音的编号和循环次数
                soundUtils.playSound(readygo, 0);
                isPlay = true;
                ll_play_open.setVisibility(View.GONE);
                ll_play_caozuo.setVisibility(View.VISIBLE);
                break;
            case 0://失败
                ToastUtil.show(gameBean.getInfo());
                break;
            case -9999://当前有用户正在游戏
                ToastUtil.show(gameBean.getInfo());
                break;
        }

    }

    @Override
    public void showFailed() {
        isPlay = false;
        ToastUtil.show("游戏失败,请检查网络!");
    }

    @Override
    public void showFinish() {
        dismissLoadingDialog();
    }


    /**
     * 监听第一个直播流拉流事件
     */
    private void playerListen1() {
        mLivePlayer1.setPlayListener(new ITXLivePlayListener() {
            @Override
            public void onPlayEvent(int i, Bundle bundle) {
                LogUtil.e("       i   " + i + "        bundle  " + bundle.toString());
                switch (i) {
                    case 2004:
                        ToastUtil.show("欢迎进入游戏间");
                        dismissLoadingDialog();
                        break;
                    case 2002:
                        if (isFirst) {
                            creatTXLivePlayer2();
                            isFirst = false;
                        }
                        ToastUtil.show("欢迎进入游戏间");
                        dismissLoadingDialog();
                        break;
                    case 2007:
                        ToastUtil.show("有点延迟...");
                        showLoadingDialog();
                        break;

                }
            }

            @Override
            public void onNetStatus(Bundle bundle) {
//                LogUtil.e(bundle.toString());
            }
        });
    }

    /**
     * 监听第二个直播流拉流事件
     */
    private void playerListen2() {
        mLivePlayer2.setPlayListener(new ITXLivePlayListener() {
            @Override
            public void onPlayEvent(int i, Bundle bundle) {
                LogUtil.e("       i   " + i + "        bundle  " + bundle.toString());
                switch (i) {
                    case 2004:
                        iv_play_change.setClickable(true);
                        dismissLoadingDialog();
                        break;
                    case 2002:
                        //无限播放背景音乐
                        soundUtils.playSound(bgvoice, SoundUtils.INFINITE_PLAY);
                        if (isFirst) {
                            creatTXLivePlayer2();
                            isFirst = false;
                        }
                        iv_play_change.setClickable(true);
                        dismissLoadingDialog();
                        break;
                    case 2007:
                        ToastUtil.show("有点延迟...");
                        showLoadingDialog();
                        break;

                }
            }

            @Override
            public void onNetStatus(Bundle bundle) {
//                LogUtil.e(bundle.toString());
            }
        });
    }

}

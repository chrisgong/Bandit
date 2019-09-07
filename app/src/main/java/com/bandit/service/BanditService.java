package com.bandit.service;

import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.bandit.data.model.Commodity;
import com.bandit.support.AccessibilityHelper;

import java.util.ArrayList;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.N)
public class BanditService extends BaseAccessibilityService {
    private static final String TAG = "Gc";
    /**
     * 执行初始化脚本
     */
    private static final int ACTION_HANDLER_MONITORING = 0;

    /**
     * 刷新列表
     */
    private static final int ACTION_HANDLER_REFRESH_LIST = 1;

    /**
     * 目标商品集合
     */
    private List<Commodity> mTargetCommodities = new ArrayList<>();

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        initScreenInfo();
        goApp();
        initTargetCommodityInfo();
    }

    /**
     * 初始化抢购商品信息
     */
    private void initTargetCommodityInfo() {
        mTargetCommodities.add(new Commodity("P-6000", true));
        mTargetCommodities.add(new Commodity("AIR MAX 720", true));
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        super.onAccessibilityEvent(event);
        int eventType = event.getEventType();
        switch (eventType) {
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                String className = event.getClassName().toString();
                if (className.equals("com.nike.snkrs.main.activities.SnkrsActivity")) {
                    mHandler.sendEmptyMessageDelayed(ACTION_HANDLER_MONITORING, 3000);
                }
                break;
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case ACTION_HANDLER_MONITORING:
                    doInitScript();
                    break;
                case ACTION_HANDLER_REFRESH_LIST:
                    handlerRefreshList();
                    break;
            }
        }
    };

    /**
     * 刷新列表，并重新执行商品状态监测
     */
    private void handlerRefreshList() {
        performSlide(SLIDE_DOWN);
        mHandler.sendEmptyMessageDelayed(ACTION_HANDLER_MONITORING, 3000);
    }

    /**
     * 执行初始化脚本
     */
    private void doInitScript() {
        try {
            //如果在详情页，点击返回
            if (findViewByID("com.nike.snkrs:id/item_thread_detail_photo_card_cta") != null) {
                performBackClick();
                Thread.sleep(1500);
            }

            //如果 tab 不在新品预览 tab 则两次左滑
            AccessibilityNodeInfo targetTabNode = findViewByText("新品预览");
            if (!targetTabNode.isSelected()) {
                performSlide(SLIDE_LEFT);
                Thread.sleep(1500);
                performSlide(SLIDE_LEFT);
                Thread.sleep(1500);
            }

            handlerCommodityState();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 刷新目标商品状态
     *
     * @throws InterruptedException
     */
    private void handlerCommodityState() throws InterruptedException {
        for (int i = 0; i < mTargetCommodities.size(); i++) {
            Commodity commodity = mTargetCommodities.get(i);
            AccessibilityNodeInfo tagerNode = findViewByText(commodity.getName());
            if (tagerNode != null) {
                performViewClick(tagerNode);
                Thread.sleep(2000);
                AccessibilityNodeInfo targetButtonNode = findViewByID("com.nike.snkrs:id/view_cta_button");
                if (targetButtonNode.getText().equals("排队名额已满") || targetButtonNode.getText().equals("发售结束")) {
                    commodity.setBuy(false);
                    performBackClick();
                    Thread.sleep(2000);
                } else {
                    performViewClick(targetButtonNode);
                    return;
                }
            }
        }

        mHandler.sendEmptyMessageDelayed(ACTION_HANDLER_REFRESH_LIST, 3000);
    }

    /**
     * 打开目标 APP
     */
    public void goApp() {
        AccessibilityHelper.getInstance().goApp(getApplicationContext());
    }
}
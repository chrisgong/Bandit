package com.bandit.view;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;

import com.bandit.R;
import com.bandit.service.BaseAccessibilityService;
import com.bandit.support.AccessibilityHelper;

import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    private static final int ACTION_REFRESH_ACCESSIBILITY_SERVICE_ON = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        BaseAccessibilityService.getInstance().init(this);
        openAccessibilityServices();
    }

    /**
     * 打开AccessibilityServices设置
     */
    private void openAccessibilityServices() {
        boolean serviceON = AccessibilityHelper.getInstance().checkAccessibilityEnabled(this);
        if (!serviceON) {
            BaseAccessibilityService.getInstance().goAccess();
            mHandler.sendEmptyMessageDelayed(ACTION_REFRESH_ACCESSIBILITY_SERVICE_ON, 3000);
        } else {
            mHandler.removeMessages(ACTION_REFRESH_ACCESSIBILITY_SERVICE_ON);
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            openAccessibilityServices();
        }
    };
}

package com.autoxing.sdk.android.example;

import android.app.Application;
import android.util.Log;

import com.autoxing.robot.sdk.AXRobot;
import com.autoxing.robot.sdk.model.AppMode;

public class MyApplication extends Application {
    private final static String TAG = "MyApplication";
    private final static String appId = ""; // 请填写由运营人员分配的appId
    private final static String appSecret = ""; // 请填写由运营人员分配的secret

    private AXRobot mAXRobot;

    @Override
    public void onCreate() {
        super.onCreate();
        mAXRobot = new AXRobot(appId, appSecret, AppMode.WAN_APP);
    }

    public AXRobot getAXRobot() {
        return mAXRobot;
    }
}

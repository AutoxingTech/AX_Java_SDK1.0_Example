package com.autoxing.sdk.android.example;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.autoxing.robot.sdk.AXRobot;
import com.autoxing.robot.sdk.OnRobotListener;
import com.autoxing.robot.sdk.OnTaskListener;
import com.autoxing.robot.sdk.error.AXConnectException;
import com.autoxing.robot.sdk.error.AXInitException;
import com.autoxing.robot.sdk.model.ActionInfo;
import com.autoxing.robot.sdk.model.ConfigInfo;
import com.autoxing.robot.sdk.model.ConnectInfo;
import com.autoxing.robot.sdk.model.MapPose;
import com.autoxing.robot.sdk.model.PoiType;
import com.autoxing.robot.sdk.model.Pose;
import com.autoxing.robot.sdk.model.RequestParam;
import com.autoxing.robot.sdk.model.SerialType;
import com.autoxing.robot.sdk.model.StateInfo;


public class MainActivity extends AppCompatActivity implements OnRobotListener, OnTaskListener {
    private final static String TAG = "MainActivity";

    private AXRobot mAXRobot;
    private final static String mRobotId = ""; // 请填写机器人编号

    private TextView text_result;
    private TextView text_state;
    private TextView text_task_state;
    private Button btn_motion;
    private Button btn_hardware;
    private Button btn_task;
    private Button btn_update_config;
    private Button btn_robot_reset;
    private Button btn_charging_reset;

    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAXRobot = ((MyApplication)this.getApplication()).getAXRobot();

        text_result = (TextView)this.findViewById(R.id.text_result);
        text_state = (TextView)this.findViewById(R.id.text_state);
        text_task_state = (TextView)this.findViewById(R.id.text_task_state);
        Button btn_connect = (Button)this.findViewById(R.id.btn_connect);
        btn_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectRobot();
            }
        });

        btn_motion = (Button)this.findViewById(R.id.btn_motion);
        btn_motion.setEnabled(false);
        btn_motion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MotionActivity.class);
                startActivity(intent);
            }
        });

        btn_hardware = (Button)this.findViewById(R.id.btn_hardware);
        btn_hardware.setEnabled(false);
        btn_hardware.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, HardwareActivity.class);
                startActivity(intent);
            }
        });

        btn_task = (Button)this.findViewById(R.id.btn_task);
        btn_task.setEnabled(false);
        btn_task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TaskActivity.class);
                startActivity(intent);
            }
        });

        btn_update_config = (Button)this.findViewById(R.id.btn_update_config);
        btn_update_config.setEnabled(false);
        btn_update_config.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               new Thread(new Runnable() {
                   @Override
                   public void run() {
                       updateConfig();
                   }
               }).start();
            }
        });
        btn_robot_reset = (Button)this.findViewById(R.id.btn_robot_reset);
        btn_robot_reset.setEnabled(false);
        btn_robot_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String newAreaId = "";
                        boolean success = resetMapByRobotPose(newAreaId);
                        Log.e(TAG, "resetPose=" + success);
                    }
                }).start();
            }
        });

        btn_charging_reset = (Button)this.findViewById(R.id.btn_charging_reset);
        btn_charging_reset.setEnabled(false);
        btn_charging_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String chargingNmae = "";
                        JSONObject poiObj = queryChargingByName(chargingNmae);
                        if(poiObj != null){
                            JSONArray coordinate = poiObj.getJSONArray("coordinate");
                            String areaId = poiObj.getString("areaId");
                            float x = coordinate.getFloatValue(0);
                            float y = coordinate.getFloatValue(1);
                            float yaw = poiObj.getFloatValue("yaw");
                            //使用充电桩复位
                            boolean success = resetMapByCharging(areaId,x,y,yaw);
                            Log.e(TAG, "resetPose=" + success);
                        }
                        }
                }).start();
            }
        });
    }

    private JSONObject queryChargingByName(String chargingName) {
        //查询充电桩点位数据
        RequestParam requestParam = new RequestParam();
        requestParam.type = PoiType.Charging.getType();
        JSONObject resObj = mAXRobot.getPoiList(requestParam);
        if (resObj == null || !resObj.containsKey("data")) {
            return null;
        }
        JSONObject dataObj = resObj.getJSONObject("data");
        int count = dataObj.getIntValue("count");
        if (count == 0){
            return null;
        }
        for (int i = 0; i < count; i++) {
            JSONObject poiObj = dataObj.getJSONArray("list").getJSONObject(i);
            String name = poiObj.getString("name");
            if (chargingName.equals(name)) {
                return poiObj;
            }
        }
        return null;
    }

    private boolean resetMapByCharging(String areaId,float x,float y,float yaw){
        MapPose mapPose = new MapPose();
        mapPose.areaId = areaId;
        mapPose.pose = new Pose(x,y,yaw);
        return mAXRobot.resetPose(mapPose, true);
    }

    private boolean resetMapByRobotPose(String newAreaId){
        StateInfo state = mAXRobot.getState();
        if(state == null){
            return false;
        }
        MapPose mapPose = new MapPose();
        mapPose.areaId = newAreaId;
        mapPose.pose = new Pose(state.x,state.y,state.ori);
        return mAXRobot.resetPose(mapPose, false);
    }

    private void updateConfig() {
        ConfigInfo configInfo = new ConfigInfo();
        configInfo.language = 1;//播报语音，默认为中文
        configInfo.runMode = 1;//自动回桩任务行驶模式，默认为灵活避障
        configInfo.skipPtDelay = 60;//设定触发1008卡住自动回桩的时间,单位:秒，默认为60s
        configInfo.lowBattery = 15;//空闲状态下低电回桩最低电量值，默认为15%
        configInfo.forceBattery = 10;//任务中低电回桩最低电量值，默认为10%
        boolean success = mAXRobot.updateConfigInfo(configInfo);
        Log.e(TAG, "updateConfigInfo=" + success);
    }

    private void connectRobot() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //1.0.3版本新增 设置串口连接类型,不设置则SDK根据sn码动态设置
                    mAXRobot.setSerialType(SerialType.ANDROID);
                    mAXRobot.init();
                    ConnectInfo info = new ConnectInfo(mRobotId, 20, "");
                    boolean isOk = mAXRobot.connectRobot(info);
                    Log.e(TAG, "isOk="+isOk);
                    if (isOk) {
                        mAXRobot.subscribeRealState(MainActivity.this);
                        mAXRobot.subscribeTaskState(MainActivity.this);
                        setResultText("Connection succeeded. RobotId: " + mAXRobot.getRobotId());
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                btn_motion.setEnabled(true);
                                btn_hardware.setEnabled(true);
                                btn_task.setEnabled(true);
                                btn_update_config.setEnabled(true);
                                btn_robot_reset.setEnabled(true);
                                btn_charging_reset.setEnabled(true);
                            }
                        });
                    }
                } catch (AXInitException e) {
                    Log.e(TAG, "code="+e.getCode()+",message="+e.getMessage());
                    setResultText(e.getMessage());
                } catch (AXConnectException e) {
                    Log.e(TAG, "code="+e.getCode()+",message="+e.getMessage());
                    setResultText(e.getMessage());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void setResultText(final String text) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                text_result.setText(text);
            }
        });
    }

    @Override
    public void onStateChanged(StateInfo stateInfo) {
        Log.e(TAG, stateInfo.toString());
        final StringBuffer sb = new StringBuffer();
        sb.append("手动：");
        if (stateInfo.isManualMode)
            sb.append("是");
        else
            sb.append("否");
        sb.append("，远控：");
        if (stateInfo.isRemoteMode)
            sb.append("是");
        else
            sb.append("否");
        sb.append("，急停：");
        if (stateInfo.isEmergencyStop)
            sb.append("是");
        else
            sb.append("否");
        sb.append("，执行任务：");
        if (stateInfo.isTasking)
            sb.append("是");
        else
            sb.append("否");
        sb.append("，障碍物：");
        if (stateInfo.hasObstruction)
            sb.append("有");
        else
            sb.append("无");
        sb.append("，前往充电：");
        if (stateInfo.isGoHome)
            sb.append("是");
        else
            sb.append("否");
        sb.append("，正在充电：");
        if (stateInfo.isCharging)
            sb.append("是");
        else
            sb.append("否");
        sb.append("，是否异常：");
        if (stateInfo.errors != null && stateInfo.errors.length > 0)
            sb.append("是");
        else
            sb.append("否");
        sb.append("，电量：" + stateInfo.battery + "%");
        sb.append("，速度：" + stateInfo.speed + "m/s");
        sb.append("，顶升状态：" + stateInfo.jackProgress);
        sb.append("，定位评价："+stateInfo.locQuality);
        sb.append("，运动状态：" + stateInfo.moveState);
        sb.append("，卡住状态：" + stateInfo.stuckState);
        sb.append("，信号状态：" + stateInfo.robotSignal);
        sb.append("，当前位置：[x:"+stateInfo.x+",y:"+stateInfo.y+",yaw(弧度):"+stateInfo.yaw+",ori(角度):"+stateInfo.ori+"]");
        sb.append("，taskObj："+stateInfo.taskObj);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                text_state.setText(sb.toString());
            }
        });
    }

    @Override
    public void onTaskChanged(final ActionInfo info) {
        Log.e(TAG, info.toString());
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                text_task_state.setText(info.toString());
            }
        });
    }

    @Override
    public void onResume() {
        Log.e(TAG, "---onResume---");
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.e(TAG, "---onPause---");
        super.onPause();
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "---onDestroy---");
        super.onDestroy();
        mAXRobot.destroy();
    }
}

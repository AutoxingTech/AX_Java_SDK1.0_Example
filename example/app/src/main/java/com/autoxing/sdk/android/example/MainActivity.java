package com.autoxing.sdk.android.example;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.autoxing.robot.sdk.AXRobot;
import com.autoxing.robot.sdk.OnRobotListener;
import com.autoxing.robot.sdk.error.AXConnectException;
import com.autoxing.robot.sdk.error.AXInitException;
import com.autoxing.robot.sdk.model.ConnectInfo;
import com.autoxing.robot.sdk.model.StateInfo;

public class MainActivity extends AppCompatActivity implements OnRobotListener {
    private final static String TAG = "MainActivity";

    private AXRobot mAXRobot;
    private final static String mRobotId = ""; // 请填写机器人编号

    private TextView text_result;
    private TextView text_state;
    private Button btn_motion;
    private Button btn_hardware;
    private Button btn_task;

    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAXRobot = ((MyApplication)this.getApplication()).getAXRobot();

        text_result = (TextView)this.findViewById(R.id.text_result);
        text_state = (TextView)this.findViewById(R.id.text_state);
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
    }

    private void connectRobot() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mAXRobot.init();
                    ConnectInfo info = new ConnectInfo(mRobotId, 20, "");
                    boolean isOk = mAXRobot.connectRobot(info);
                    Log.e(TAG, "isOk="+isOk);
                    if (isOk) {
                        setResultText("Connection succeeded. RobotId: " + mAXRobot.getRobotId());
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                btn_motion.setEnabled(true);
                                btn_hardware.setEnabled(true);
                                btn_task.setEnabled(true);
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
        StringBuffer sb = new StringBuffer();
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
        sb.append("，定位评价："+stateInfo.locQuality);
        sb.append("，当前位置：[x:"+stateInfo.x+",y:"+stateInfo.y+",yaw:"+stateInfo.yaw+"]");
        text_state.setText(sb.toString());
    }

    @Override
    public void onResume() {
        Log.e(TAG, "---onResume---");
        super.onResume();
        mAXRobot.subscribeRealState(this);
    }

    @Override
    public void onPause() {
        Log.e(TAG, "---onPause---");
        super.onPause();
        mAXRobot.subscribeRealState(null);
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "---onDestroy---");
        super.onDestroy();
        mAXRobot.destroy();
    }
}

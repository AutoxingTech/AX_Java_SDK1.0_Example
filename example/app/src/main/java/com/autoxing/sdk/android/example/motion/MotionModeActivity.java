package com.autoxing.sdk.android.example.motion;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.autoxing.robot.sdk.AXRobot;
import com.autoxing.robot.sdk.OnRobotListener;
import com.autoxing.robot.sdk.model.EmergencyType;
import com.autoxing.robot.sdk.model.MotionType;
import com.autoxing.robot.sdk.model.StateInfo;
import com.autoxing.sdk.android.example.MyApplication;
import com.autoxing.sdk.android.example.R;

public class MotionModeActivity extends AppCompatActivity implements OnRobotListener {
    private final static String TAG = "MotionModeActivity";

    private AXRobot mAXRobot;

    private TextView text_result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_motionmode);

        mAXRobot = ((MyApplication)this.getApplication()).getAXRobot();

        text_result = (TextView)this.findViewById(R.id.text_result);
        Button btn_auto = (Button)this.findViewById(R.id.btn_auto);
        btn_auto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAXRobot.motionFor(MotionType.Auto);
            }
        });
        Button btn_manual = (Button)this.findViewById(R.id.btn_manual);
        btn_manual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAXRobot.motionFor(MotionType.Manual);
            }
        });
        Button btn_emergency = (Button)this.findViewById(R.id.btn_emergency);
        btn_emergency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAXRobot.setEmergency(EmergencyType.Start);
            }
        });
        Button btn_cancel_emergency = (Button)this.findViewById(R.id.btn_cancel_emergency);
        btn_cancel_emergency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAXRobot.setEmergency(EmergencyType.Stop);
            }
        });
    }

    @Override
    public void onResume() {
        Log.e(TAG, "---onResume---");
        super.onResume();
        mAXRobot.subscribeRealState(MotionModeActivity.this);
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
        text_result.setText(sb.toString());
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
    }
}

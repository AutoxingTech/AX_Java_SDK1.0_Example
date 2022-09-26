package com.autoxing.sdk.android.example;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.autoxing.robot.sdk.AXRobot;
import com.autoxing.robot.sdk.OnRobotListener;
import com.autoxing.robot.sdk.error.AXConnectException;
import com.autoxing.robot.sdk.error.AXInitException;
import com.autoxing.robot.sdk.model.ConnectInfo;
import com.autoxing.robot.sdk.model.MotionType;
import com.autoxing.robot.sdk.model.StateInfo;
import com.autoxing.sdk.android.example.hardware.LightbeltActivity;
import com.autoxing.sdk.android.example.motion.GoHomeActivity;
import com.autoxing.sdk.android.example.motion.MaptoActivity;
import com.autoxing.sdk.android.example.motion.MotionModeActivity;
import com.autoxing.sdk.android.example.motion.PoitoActivity;
import com.autoxing.sdk.android.example.motion.StepActivity;

public class MotionActivity extends AppCompatActivity implements OnRobotListener {
    private final static String TAG = "MotionActivity";

    private AXRobot mAXRobot;

    private TextView text_result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_motion);

        mAXRobot = ((MyApplication)this.getApplication()).getAXRobot();

        text_result = (TextView)this.findViewById(R.id.text_result);
        Button btn_motionmode = (Button)this.findViewById(R.id.btn_motionmode);
        btn_motionmode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MotionActivity.this, MotionModeActivity.class);
                startActivity(intent);
            }
        });
        Button btn_step = (Button)this.findViewById(R.id.btn_step);
        btn_step.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MotionActivity.this, StepActivity.class);
                startActivity(intent);
            }
        });
        Button btn_mapto = (Button)this.findViewById(R.id.btn_mapto);
        btn_mapto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MotionActivity.this, MaptoActivity.class);
                startActivity(intent);
            }
        });
        Button btn_poito = (Button)this.findViewById(R.id.btn_poito);
        btn_poito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MotionActivity.this, PoitoActivity.class);
                startActivity(intent);
            }
        });
        Button btn_charge = (Button)this.findViewById(R.id.btn_charge);
        btn_charge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MotionActivity.this, GoHomeActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onResume() {
        Log.e(TAG, "---onResume---");
        super.onResume();
        mAXRobot.subscribeRealState(MotionActivity.this);
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

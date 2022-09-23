package com.autoxing.sdk.android.example.motion;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.autoxing.robot.sdk.AXRobot;
import com.autoxing.robot.sdk.model.MotionType;
import com.autoxing.sdk.android.example.MyApplication;
import com.autoxing.sdk.android.example.R;

public class StepActivity extends AppCompatActivity {
    private final static String TAG = "StepActivity";

    private AXRobot mAXRobot;

    private TextView text_result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step);

        mAXRobot = ((MyApplication)this.getApplication()).getAXRobot();

        text_result = (TextView)this.findViewById(R.id.text_result);
        Button btn_Forward = (Button)this.findViewById(R.id.btn_Forward);
        btn_Forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAXRobot.motionFor(MotionType.Forward);
            }
        });
        Button btn_Back = (Button)this.findViewById(R.id.btn_Back);
        btn_Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAXRobot.motionFor(MotionType.Back);
            }
        });
        Button btn_TurnLeft = (Button)this.findViewById(R.id.btn_TurnLeft);
        btn_TurnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAXRobot.motionFor(MotionType.TurnLeft);
            }
        });
        Button btn_TurnRight = (Button)this.findViewById(R.id.btn_TurnRight);
        btn_TurnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAXRobot.motionFor(MotionType.TurnRight);
            }
        });
        Button btn_Cancel = (Button)this.findViewById(R.id.btn_Cancel);
        btn_Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAXRobot.motionFor(MotionType.Cancel);
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
    }
}

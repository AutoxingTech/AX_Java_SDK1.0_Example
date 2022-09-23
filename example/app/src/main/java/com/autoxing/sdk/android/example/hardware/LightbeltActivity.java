package com.autoxing.sdk.android.example.hardware;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.autoxing.robot.sdk.AXRobot;
import com.autoxing.robot.sdk.model.LightBelt;
import com.autoxing.robot.sdk.model.LightColor;
import com.autoxing.robot.sdk.model.LightIndex;
import com.autoxing.robot.sdk.model.MotionType;
import com.autoxing.sdk.android.example.MyApplication;
import com.autoxing.sdk.android.example.R;

import java.util.Vector;

public class LightbeltActivity extends AppCompatActivity {
    private final static String TAG = "LightbeltActivity";

    private AXRobot mAXRobot;

    private TextView text_result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lightbelt);

        mAXRobot = ((MyApplication)this.getApplication()).getAXRobot();

        text_result = (TextView)this.findViewById(R.id.text_result);
        Button btn_red1 = (Button)this.findViewById(R.id.btn_red1);
        btn_red1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSubLight(0);
            }
        });
        Button btn_green2 = (Button)this.findViewById(R.id.btn_green2);
        btn_green2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSubLight(1);
            }
        });
        Button btn_blue3 = (Button)this.findViewById(R.id.btn_blue3);
        btn_blue3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSubLight(2);
            }
        });
        Button btn_yellow4 = (Button)this.findViewById(R.id.btn_yellow4);
        btn_yellow4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSubLight(3);
            }
        });
        Button btn_red = (Button)this.findViewById(R.id.btn_red);
        btn_red.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLight(0);
            }
        });
        Button btn_green = (Button)this.findViewById(R.id.btn_green);
        btn_green.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLight(1);
            }
        });
        Button btn_blue = (Button)this.findViewById(R.id.btn_blue);
        btn_blue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLight(2);
            }
        });
        Button btn_yellow = (Button)this.findViewById(R.id.btn_yellow);
        btn_yellow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLight(3);
            }
        });
        Button btn_turnoff = (Button)this.findViewById(R.id.btn_turnoff);
        btn_turnoff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeLight();
            }
        });
    }

    private void openLight(int flag) {
        LightColor color = LightColor.Red;
        switch (flag) {
            case 1:
                color = LightColor.Green;
                break;
            case 2:
                color = LightColor.Blue;
                break;
            case 3:
                color = LightColor.Yellow;
                break;
        }
        LightBelt lightBelt = new LightBelt();
        lightBelt.color = color;
        mAXRobot.openLightBelt(lightBelt);
    }

    private void closeLight() {
        LightBelt lightBelt = new LightBelt();
        mAXRobot.closeLightBelt(lightBelt);
    }

    private void openSubLight(int flag) {
        LightBelt lightBelt = new LightBelt();
        Vector<LightIndex> indexs = new Vector<LightIndex>();
        LightColor color = LightColor.Red;
        LightIndex lightIndex = new LightIndex(0, 6);
        switch (flag) {
            case 1: {
                color = LightColor.Green;
                lightIndex = new LightIndex(6, 6);
                break;
            }
            case 2: {
                color = LightColor.Blue;
                lightIndex = new LightIndex(12, 6);
                break;
            }
            case 3: {
                color = LightColor.Yellow;
                lightIndex = new LightIndex(18, 6);
                break;
            }
        }
        indexs.add(lightIndex);
        lightBelt.color = color;
        lightBelt.indexs = indexs;
        mAXRobot.openLightBelt(lightBelt);
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

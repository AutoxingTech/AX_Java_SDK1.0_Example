package com.autoxing.sdk.android.example;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.autoxing.robot.sdk.AXRobot;
import com.autoxing.robot.sdk.model.MotionType;
import com.autoxing.sdk.android.example.hardware.BoxdoorActivity;
import com.autoxing.sdk.android.example.hardware.LightbeltActivity;
import com.autoxing.sdk.android.example.hardware.SprayerActivity;

public class HardwareActivity extends AppCompatActivity {
    private final static String TAG = "HardwareActivity";

    private AXRobot mAXRobot;

    private TextView text_result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hardware);

        mAXRobot = ((MyApplication)this.getApplication()).getAXRobot();

        text_result = (TextView)this.findViewById(R.id.text_result);
        Button btn_Lightbelt = (Button)this.findViewById(R.id.btn_Lightbelt);
        btn_Lightbelt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HardwareActivity.this, LightbeltActivity.class);
                startActivity(intent);
            }
        });
        Button btn_Boxdoor = (Button)this.findViewById(R.id.btn_Boxdoor);
        btn_Boxdoor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HardwareActivity.this, BoxdoorActivity.class);
                startActivity(intent);
            }
        });
        Button btn_Sprayer = (Button)this.findViewById(R.id.btn_Sprayer);
        btn_Sprayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HardwareActivity.this, SprayerActivity.class);
                startActivity(intent);
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

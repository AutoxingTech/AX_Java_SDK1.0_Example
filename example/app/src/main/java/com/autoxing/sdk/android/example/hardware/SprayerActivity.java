package com.autoxing.sdk.android.example.hardware;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.autoxing.robot.sdk.AXRobot;
import com.autoxing.sdk.android.example.MyApplication;
import com.autoxing.sdk.android.example.R;

public class SprayerActivity extends AppCompatActivity {
    private final static String TAG = "SprayerActivity";

    private AXRobot mAXRobot;

    private TextView text_result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sprayer);

        mAXRobot = ((MyApplication)this.getApplication()).getAXRobot();

        text_result = (TextView)this.findViewById(R.id.text_result);
        Button btn_opensprayer1 = (Button)this.findViewById(R.id.btn_opensprayer1);
        btn_opensprayer1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSprayer(1);
            }
        });
        Button btn_opensprayer2 = (Button)this.findViewById(R.id.btn_opensprayer2);
        btn_opensprayer2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSprayer(2);
            }
        });
        Button btn_opensprayer3 = (Button)this.findViewById(R.id.btn_opensprayer3);
        btn_opensprayer3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSprayer(3);
            }
        });
        Button btn_opensprayer4 = (Button)this.findViewById(R.id.btn_opensprayer4);
        btn_opensprayer4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSprayer(4);
            }
        });
        Button btn_opensprayer5 = (Button)this.findViewById(R.id.btn_opensprayer5);
        btn_opensprayer5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSprayer(5);
            }
        });
        Button btn_closesprayer = (Button)this.findViewById(R.id.btn_closesprayer);
        btn_closesprayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeSprayer();
            }
        });
    }

    private void openSprayer(int gear) {
        mAXRobot.openSprayer(gear);
    }

    private void closeSprayer() {
        mAXRobot.closeSprayer();
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

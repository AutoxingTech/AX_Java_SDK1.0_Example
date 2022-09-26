package com.autoxing.sdk.android.example;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.autoxing.robot.sdk.AXRobot;
import com.autoxing.sdk.android.example.hardware.BoxdoorActivity;
import com.autoxing.sdk.android.example.hardware.LightbeltActivity;
import com.autoxing.sdk.android.example.hardware.SprayerActivity;
import com.autoxing.sdk.android.example.task.GoHomeTaskActivity;
import com.autoxing.sdk.android.example.task.PoiActionActivity;

public class TaskActivity extends AppCompatActivity {
    private final static String TAG = "TaskActivity";

    private AXRobot mAXRobot;

    private TextView text_result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        mAXRobot = ((MyApplication)this.getApplication()).getAXRobot();

        text_result = (TextView)this.findViewById(R.id.text_result);
        Button btn_poiaction = (Button)this.findViewById(R.id.btn_poiaction);
        btn_poiaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TaskActivity.this, PoiActionActivity.class);
                startActivity(intent);
            }
        });
        Button btn_chargetask = (Button)this.findViewById(R.id.btn_chargetask);
        btn_chargetask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TaskActivity.this, GoHomeTaskActivity.class);
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

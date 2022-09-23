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
import com.autoxing.sdk.android.example.MyApplication;
import com.autoxing.sdk.android.example.R;

import java.util.Vector;

public class BoxdoorActivity extends AppCompatActivity {
    private final static String TAG = "BoxdoorActivity";

    private AXRobot mAXRobot;

    private TextView text_result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boxdoor);

        mAXRobot = ((MyApplication)this.getApplication()).getAXRobot();

        text_result = (TextView)this.findViewById(R.id.text_result);
        Button btn_openbox1 = (Button)this.findViewById(R.id.btn_openbox1);
        btn_openbox1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openBoxDoor(1);
            }
        });
        Button btn_openbox2 = (Button)this.findViewById(R.id.btn_openbox2);
        btn_openbox2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openBoxDoor(2);
            }
        });
        Button btn_openbox3 = (Button)this.findViewById(R.id.btn_openbox3);
        btn_openbox3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openBoxDoor(3);
            }
        });
        Button btn_openbox4 = (Button)this.findViewById(R.id.btn_openbox4);
        btn_openbox4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openBoxDoor(4);
            }
        });
        Button btn_closebox1 = (Button)this.findViewById(R.id.btn_closebox1);
        btn_closebox1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeBoxDoor(1);
            }
        });
        Button btn_closebox2 = (Button)this.findViewById(R.id.btn_closebox2);
        btn_closebox2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeBoxDoor(2);
            }
        });
        Button btn_closebox3 = (Button)this.findViewById(R.id.btn_closebox3);
        btn_closebox3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeBoxDoor(3);
            }
        });
        Button btn_closebox4 = (Button)this.findViewById(R.id.btn_closebox4);
        btn_closebox4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeBoxDoor(4);
            }
        });
    }

    private void openBoxDoor(int id) {
        int[] doorIds = new int[]{id};
        mAXRobot.openBoxDoor(doorIds);
    }

    private void closeBoxDoor(int id) {
        int[] doorIds = new int[]{id};
        mAXRobot.closeBoxDoor(doorIds);
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

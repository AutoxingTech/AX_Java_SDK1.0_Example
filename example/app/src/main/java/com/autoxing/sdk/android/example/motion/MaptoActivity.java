package com.autoxing.sdk.android.example.motion;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.autoxing.robot.sdk.AXRobot;
import com.autoxing.robot.sdk.model.MotionType;
import com.autoxing.robot.sdk.model.Pose;
import com.autoxing.sdk.android.example.MyApplication;
import com.autoxing.sdk.android.example.R;

public class MaptoActivity extends AppCompatActivity {
    private final static String TAG = "MaptoActivity";

    private AXRobot mAXRobot;

    private TextView text_result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapto);

        mAXRobot = ((MyApplication)this.getApplication()).getAXRobot();

        text_result = (TextView)this.findViewById(R.id.text_result);
        Button btn_goto = (Button)this.findViewById(R.id.btn_goto);
        btn_goto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et_x = (EditText)findViewById(R.id.et_x);
                float x = Float.parseFloat(et_x.getText().toString());
                EditText et_y = (EditText)findViewById(R.id.et_y);
                float y = Float.parseFloat(et_y.getText().toString());
                EditText et_yaw = (EditText)findViewById(R.id.et_yaw);
                float yaw = Float.parseFloat(et_yaw.getText().toString());
                Log.e(TAG, "x="+x+",y="+y+"yaw="+yaw);
                Pose pose = new Pose(x, y, yaw);
                mAXRobot.moveTo(pose);
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

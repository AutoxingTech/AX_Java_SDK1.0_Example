package com.autoxing.sdk.android.example.motion;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.autoxing.robot.sdk.AXRobot;
import com.autoxing.robot.sdk.model.JackType;
import com.autoxing.robot.sdk.model.Pose;
import com.autoxing.sdk.android.example.MyApplication;
import com.autoxing.sdk.android.example.R;

public class MaptoShelfActivity extends AppCompatActivity {
    private final static String TAG = "MaptoActivity";

    private AXRobot mAXRobot;

    private TextView text_result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maptoshelf);

        mAXRobot = ((MyApplication)this.getApplication()).getAXRobot();
        initData();
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
                Switch s_type = findViewById(R.id.s_type);
                Log.e(TAG, "x="+x+",y="+y+",yaw="+yaw+",取货:"+s_type.isChecked());
                Pose pose = new Pose(x, y, yaw);
                JackType jackType = s_type.isChecked() ? JackType.UP:JackType.DOWN;
                mAXRobot.moveToShelf(pose, jackType);
            }
        });

        Button btn_goto_p = (Button)this.findViewById(R.id.btn_goto_p);
        btn_goto_p.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et_x = (EditText)findViewById(R.id.et_x_p);
                float x = Float.parseFloat(et_x.getText().toString());
                EditText et_y = (EditText)findViewById(R.id.et_y_p);
                float y = Float.parseFloat(et_y.getText().toString());
                EditText et_yaw = (EditText)findViewById(R.id.et_yaw_p);
                float yaw = Float.parseFloat(et_yaw.getText().toString());
                Switch s_type = findViewById(R.id.s_type_p);
                Log.e(TAG, "x="+x+",y="+y+",yaw="+yaw+",取货:"+s_type.isChecked());
                Pose pose = new Pose(x, y, yaw);
                JackType jackType = s_type.isChecked() ? JackType.UP:JackType.DOWN;
                mAXRobot.moveToShelf(pose, jackType);
            }
        });

        Button btn_pickup = (Button)this.findViewById(R.id.btn_pickup);
        btn_pickup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAXRobot.setJackAction(JackType.UP);
            }
        });
        Button btn_pickdown = (Button)this.findViewById(R.id.btn_pickdown);
        btn_pickdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAXRobot.setJackAction(JackType.DOWN);
            }
        });

    }

    private void initData(){
        EditText et_x = (EditText)findViewById(R.id.et_x);
        EditText et_y = (EditText)findViewById(R.id.et_y);
        EditText et_yaw = (EditText)findViewById(R.id.et_yaw);
        et_x.setText("-5.796");
        et_y.setText("0.043");
        et_yaw.setText("31");

         et_x = (EditText)findViewById(R.id.et_x_p);
         et_y = (EditText)findViewById(R.id.et_y_p);
         et_yaw = (EditText)findViewById(R.id.et_yaw_p);
        et_x.setText("2.178");
        et_y.setText("-4.475");
        et_yaw.setText("119");
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

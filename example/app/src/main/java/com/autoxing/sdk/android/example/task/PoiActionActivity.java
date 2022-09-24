package com.autoxing.sdk.android.example.task;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.autoxing.robot.sdk.AXRobot;
import com.autoxing.robot.sdk.OnTaskListener;
import com.autoxing.robot.sdk.error.AXTaskException;
import com.autoxing.robot.sdk.model.ActionInfo;
import com.autoxing.robot.sdk.model.Pose;
import com.autoxing.robot.sdk.model.TaskInfo;
import com.autoxing.robot.sdk.model.TaskPoint;
import com.autoxing.sdk.android.example.MyApplication;
import com.autoxing.sdk.android.example.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class PoiActionActivity extends AppCompatActivity implements OnTaskListener {
    private final static String TAG = "PoiActionActivity";

    private AXRobot mAXRobot;

    private List<JSONObject> poiLists;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poito);

        mAXRobot = ((MyApplication)this.getApplication()).getAXRobot();

        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject resObj = mAXRobot.getPoiList(null);
                if (resObj != null && resObj.containsKey("data")) {
                    JSONObject dataObj = resObj.getJSONObject("data");
                    int count = dataObj.getIntValue("count");
                    if (count == 0)
                        return;
                    poiLists = new ArrayList<JSONObject>();
                    JSONArray list = dataObj.getJSONArray("list");
                    int size = list.size();
                    for (int i = 0; i < size; i++) {
                        poiLists.add(list.getJSONObject(i));
                    }
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            showPoiLiseView();
                        }
                    });
                }
            }
        }).start();
    }

    private void showPoiLiseView() {
        PoiAdapter adapter = new PoiAdapter(PoiActionActivity.this, R.layout.poi_list_item, poiLists);
        ListView list_poi = (ListView)findViewById(R.id.list_poi);
        list_poi.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                createTask(position);
            }
        });
        list_poi.setAdapter(adapter);
    }

    private void createTask(final int position) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject poiObj = poiLists.get(position);
                JSONArray coordinate = poiObj.getJSONArray("coordinate");
                Log.e(TAG, poiObj.toJSONString());
                String areaId = poiObj.getString("areaId");
                float x = coordinate.getFloatValue(0);
                float y = coordinate.getFloatValue(1);
                float yaw = poiObj.getFloatValue("yaw");
                Pose pose = new Pose(x, y, yaw);
                TaskInfo task = new TaskInfo();
                task.pts = new Vector<TaskPoint>();
                TaskPoint taskPoint = new TaskPoint();
                taskPoint.pose = pose;
                taskPoint.areaId = areaId; // 区域ID，任务站点间、或和机器人当前不同区域时，会进行乘梯动作
                task.pts.add(taskPoint);
                try {
                    String taskId = mAXRobot.createTask(task);
                    Log.e(TAG, "taskId=" + taskId);
                    mAXRobot.executeTask(taskId);
                } catch (AXTaskException e) {
                    Log.e(TAG, e.getCode() + "," + e.getMessage());
                }
            }
        }).start();
    }

    @Override
    public void onTaskChanged(ActionInfo actionInfo) {
        Log.e(TAG, actionInfo.actType + "," + actionInfo.data);
    }

    private class PoiAdapter extends ArrayAdapter<JSONObject> {
        private int resourceId;

        public PoiAdapter(Context context, int resourceId, List<JSONObject> objects) {
            super(context, resourceId, objects);
            this.resourceId = resourceId;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            JSONObject poiObj = getItem(position);
            View view = LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
            TextView text_poi = (TextView)view.findViewById(R.id.text_poi);
            String name = poiObj.getString("name");
            text_poi.setText(name);
            return view;
        }
    }

    @Override
    public void onResume() {
        Log.e(TAG, "---onResume---");
        super.onResume();
        mAXRobot.subscribeTaskState(this); // 订阅任务状态时，最好进行全局订阅，避免任务状态丢失
    }

    @Override
    public void onPause() {
        Log.e(TAG, "---onPause---");
        super.onPause();
        mAXRobot.subscribeTaskState(null);
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "---onDestroy---");
        super.onDestroy();
    }
}

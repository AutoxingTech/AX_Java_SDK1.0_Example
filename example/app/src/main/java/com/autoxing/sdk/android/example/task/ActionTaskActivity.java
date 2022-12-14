package com.autoxing.sdk.android.example.task;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.autoxing.robot.sdk.AXRobot;
import com.autoxing.robot.sdk.OnTaskListener;
import com.autoxing.robot.sdk.error.AXTaskException;
import com.autoxing.robot.sdk.model.ActionInfo;
import com.autoxing.robot.sdk.model.ActionType;
import com.autoxing.robot.sdk.model.Pose;
import com.autoxing.robot.sdk.model.RequestParam;
import com.autoxing.robot.sdk.model.StateInfo;
import com.autoxing.robot.sdk.model.TaskInfo;
import com.autoxing.robot.sdk.model.TaskPoint;
import com.autoxing.sdk.android.example.MainActivity;
import com.autoxing.sdk.android.example.MyApplication;
import com.autoxing.sdk.android.example.R;
import com.autoxing.sdk.android.example.TaskActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class ActionTaskActivity extends AppCompatActivity implements OnTaskListener {
    private final static String TAG = "ActionTaskActivity";

    private AXRobot mAXRobot;

    private int poiIndex = -1;
    private int chargeIndex = -1;
    private List<JSONObject> poiLists;
    private List<JSONObject> chargeLists;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actiontask);

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
                    chargeLists = new ArrayList<JSONObject>();
                    JSONArray list = dataObj.getJSONArray("list");
                    int size = list.size();
                    for (int i = 0; i < size; i++) {
                        JSONObject poiObj = list.getJSONObject(i);
                        int type = poiObj.getIntValue("type");
                        if (type == 9) { // ???????????????
                            chargeLists.add(poiObj);
                        } else {
                            poiLists.add(poiObj);
                        }
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

        Button btn_starttask = (Button)this.findViewById(R.id.btn_starttask);
        btn_starttask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createTask();
            }
        });
    }

    private void showPoiLiseView() {
        final PoiAdapter poiAdapter = new PoiAdapter(ActionTaskActivity.this, R.layout.poi_list_item,
                poiLists, 0);
        ListView list_poi = (ListView)findViewById(R.id.list_poi);
        list_poi.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                poiIndex = position;
                poiAdapter.notifyDataSetInvalidated();
            }
        });
        list_poi.setAdapter(poiAdapter);

        final PoiAdapter chargeAdapter = new PoiAdapter(ActionTaskActivity.this, R.layout.poi_list_item,
                chargeLists, 1);
        ListView list_chargepoi = (ListView)findViewById(R.id.list_chargepoi);
        list_chargepoi.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                chargeIndex = position;
                chargeAdapter.notifyDataSetInvalidated();
            }
        });
        list_chargepoi.setAdapter(chargeAdapter);
    }

    private TaskPoint objToTaskPoint(JSONObject poiObj) {
        TaskPoint taskPoint = new TaskPoint();
        JSONArray coordinate = poiObj.getJSONArray("coordinate");
        int type = poiObj.getIntValue("type");
        String name = poiObj.getString("name");
        String areaId = poiObj.getString("areaId");
        float x = coordinate.getFloatValue(0);
        float y = coordinate.getFloatValue(1);
        float yaw = poiObj.getFloatValue("yaw");
        Pose pose = new Pose(x, y, yaw);
        taskPoint.pose = pose;
        taskPoint.type = type; // ??????????????????????????????????????????????????????
        taskPoint.areaId = areaId; // ??????ID?????????????????????????????????????????????????????????????????????????????????
        taskPoint.ext = new JSONObject(); // ?????????????????????????????????????????????SDK??????
        taskPoint.ext.put("name", name);
        taskPoint.ext.put("anyKey", "anyValue");
        return taskPoint;
    }

    private void createTask() {
        if (this.poiIndex == -1) {
            Toast.makeText(ActionTaskActivity.this,"????????????????????????", Toast.LENGTH_SHORT).show();
            return;
        }
        if (this.chargeIndex == -1) {
            Toast.makeText(ActionTaskActivity.this,"?????????????????????", Toast.LENGTH_SHORT).show();
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject poiObj = poiLists.get(poiIndex);
                JSONObject chargeObj = chargeLists.get(chargeIndex);

                TaskInfo task = new TaskInfo();
                task.pts = new Vector<TaskPoint>();
                TaskPoint actTaskPoint = objToTaskPoint(poiObj);
                actTaskPoint.stepActs = new Vector<ActionInfo>();

                // ???????????????????????????????????????????????????
                JSONObject playAudioData = new JSONObject();
                playAudioData.put("url", "https://autoxingtest1.oss-cn-beijing.aliyuncs.com/mp3/autoxing/yijia_task_running.mp3"); // ??????????????????
                playAudioData.put("num", 2); // ????????????
                playAudioData.put("volume", 30); // ??????0-100
                playAudioData.put("duration", 999999); // ??????????????????????????????num???duration?????????num????????????????????????action
                playAudioData.put("interval", 10); // ??????10?????????
                ActionInfo playAudioAct = new ActionInfo(ActionType.PlayAudio, playAudioData); // ????????????????????????
                actTaskPoint.stepActs.add(playAudioAct); // ????????????????????????????????????

                JSONObject pauseData = new JSONObject();
                pauseData.put("pauseTime", 60); // ??????1??????
                ActionInfo pauseAct = new ActionInfo(ActionType.Pause, pauseData); // ??????????????????
                actTaskPoint.stepActs.add(pauseAct); // ??????????????????????????????

                // ?????????
                task.pts.add(actTaskPoint); // ???????????????
                task.pts.add(objToTaskPoint(chargeObj)); // ?????????????????????

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
        private int flag;

        public PoiAdapter(Context context, int resourceId, List<JSONObject> objects, int flag) {
            super(context, resourceId, objects);
            this.resourceId = resourceId;
            this.flag = flag;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            JSONObject poiObj = getItem(position);
            View view = LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
            TextView text_poi = (TextView)view.findViewById(R.id.text_poi);
            text_poi.setTextColor(Color.BLACK);
            if (this.flag == 0 && poiIndex == position) {
                text_poi.setTextColor(Color.RED);
            } else if (this.flag == 1 && chargeIndex == position) {
                text_poi.setTextColor(Color.RED);
            }
            int floor = poiObj.getIntValue("floor");
            String name = poiObj.getString("name") + "???" + floor + "??????";
            text_poi.setText(name);
            return view;
        }
    }

    @Override
    public void onResume() {
        Log.e(TAG, "---onResume---");
        super.onResume();
        mAXRobot.subscribeTaskState(this); // ???????????????????????????????????????????????????????????????????????????
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

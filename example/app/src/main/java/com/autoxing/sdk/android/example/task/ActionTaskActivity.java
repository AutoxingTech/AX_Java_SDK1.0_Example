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
                        if (type == 9) { // 充电桩类型
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
        taskPoint.type = type; // 若是充电桩类型，则自动前往并进行充电
        taskPoint.areaId = areaId; // 区域ID，任务站点间、或和机器人当前不同区域时，会进行乘梯动作
        taskPoint.ext = new JSONObject(); // 扩展信息，在任务状态订阅回调中SDK透传
        taskPoint.ext.put("name", name);
        taskPoint.ext.put("anyKey", "anyValue");
        return taskPoint;
    }

    private void createTask() {
        if (this.poiIndex == -1) {
            Toast.makeText(ActionTaskActivity.this,"请选择动作站点！", Toast.LENGTH_SHORT).show();
            return;
        }
        if (this.chargeIndex == -1) {
            Toast.makeText(ActionTaskActivity.this,"请选择充电桩！", Toast.LENGTH_SHORT).show();
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

                // 添加动作，动作按添加的顺序进行执行
                JSONObject playAudioData = new JSONObject();
                playAudioData.put("url", "https://autoxingtest1.oss-cn-beijing.aliyuncs.com/mp3/autoxing/yijia_task_running.mp3"); // 播放在线声音
                playAudioData.put("num", 2); // 播放两次
                playAudioData.put("duration", 999999); // 播放总时长，同时存在num和duration时，以num执行完毕及结束该action
                playAudioData.put("interval", 10); // 间隔10秒播放
                ActionInfo playAudioAct = new ActionInfo(ActionType.PlayAudio, playAudioData); // 创建播放声音动作
                actTaskPoint.stepActs.add(playAudioAct); // 在站点中添加播放声音动作

                JSONObject pauseData = new JSONObject();
                pauseData.put("pauseTime", 60); // 停留1分钟
                ActionInfo pauseAct = new ActionInfo(ActionType.Pause, pauseData); // 创建停留动作
                actTaskPoint.stepActs.add(pauseAct); // 在站点中添加停留动作

                // 任务点
                task.pts.add(actTaskPoint); // 添加动作点
                task.pts.add(objToTaskPoint(chargeObj)); // 最后返回充电桩

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
            String name = poiObj.getString("name") + "（" + floor + "层）";
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

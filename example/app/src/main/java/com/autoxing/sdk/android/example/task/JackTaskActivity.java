package com.autoxing.sdk.android.example.task;

import android.content.Context;
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
import com.autoxing.robot.sdk.model.PoiType;
import com.autoxing.robot.sdk.model.Pose;
import com.autoxing.robot.sdk.model.RequestParam;
import com.autoxing.robot.sdk.model.StateInfo;
import com.autoxing.robot.sdk.model.TaskInfo;
import com.autoxing.robot.sdk.model.TaskPoint;
import com.autoxing.sdk.android.example.MyApplication;
import com.autoxing.sdk.android.example.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class JackTaskActivity extends AppCompatActivity implements OnTaskListener {
    private final static String TAG = "JackTaskActivity";

    private AXRobot mAXRobot;

    private List<JSONObject> poiLists;
    private int pickUpPoiIndex = -1,pickDownPoiIndex = -1;
    private Handler mHandler = new Handler();
    private ListView list_poi1,list_poi2;
    private PoiAdapter adapter1,adapter2;
    private TextView tv_status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jack_task);
        list_poi1 = (ListView)findViewById(R.id.list_poi1);
        list_poi2 = (ListView)findViewById(R.id.list_poi2);
        tv_status = findViewById(R.id.tv_status);
        mAXRobot = ((MyApplication)this.getApplication()).getAXRobot();

        new Thread(new Runnable() {
            @Override
            public void run() {
                StateInfo stateInfo = mAXRobot.getState();
                RequestParam requestParam = new RequestParam();
                requestParam.areaId = stateInfo.areaId;
                requestParam.robotId = stateInfo.robotId;
                requestParam.type = PoiType.Goods.getType();
                JSONObject resObj = mAXRobot.getPoiList(requestParam);
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

        findViewById(R.id.btn_clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_status.setText("");
                pickUpPoiIndex = -1;
                pickDownPoiIndex = -1;
                if(adapter1 != null){
                    adapter1.notifyDataSetChanged();
                }
                if(adapter2 != null){
                    adapter2.notifyDataSetChanged();
                }
            }
        });
        findViewById(R.id.btn_jacktask).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pickUpPoiIndex == -1 && pickDownPoiIndex == -1){
                    Toast.makeText(JackTaskActivity.this,"请先选择取货点，放货点",Toast.LENGTH_LONG).show();
                    return;
                }
                createTask();
            }
        });
        findViewById(R.id.btn_canceltask).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelTask();
            }
        });
    }

    private void cancelTask(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                mAXRobot.cancelTask();
            }
        }).start();
    }

    private void showPoiLiseView() {
         adapter1 = new PoiAdapter(JackTaskActivity.this, R.layout.poi_list_item, poiLists,true);

        list_poi1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pickUpPoiIndex = position;
                adapter1.notifyDataSetChanged();
            }
        });
        list_poi1.setAdapter(adapter1);

        adapter2 = new PoiAdapter(JackTaskActivity.this, R.layout.poi_list_item, poiLists,false);
        list_poi2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pickDownPoiIndex = position;
                adapter2.notifyDataSetChanged();
            }
        });
        list_poi2.setAdapter(adapter2);
    }

    private TaskPoint generateTaskPoint(JSONObject poiObj){
        JSONArray coordinate = poiObj.getJSONArray("coordinate");
        int type = poiObj.getIntValue("type");
        String name = poiObj.getString("name");
        String areaId = poiObj.getString("areaId");
        float x = coordinate.getFloatValue(0);
        float y = coordinate.getFloatValue(1);
        float yaw = poiObj.getFloatValue("yaw");
        Pose pose = new Pose(x, y, yaw);


        TaskPoint taskPoint = new TaskPoint();
        taskPoint.pose = pose;
        taskPoint.type = type;
        taskPoint.areaId = areaId; // 区域ID，任务站点间、或和机器人当前不同区域时，会进行乘梯动作
        taskPoint.ext = new JSONObject(); // 扩展信息，在任务状态订阅回调中SDK透传
        taskPoint.ext.put("name", name);
        taskPoint.ext.put("anyKey", "anyValue");
        return taskPoint;
    }

    /**
     * 取货动作
     * @return
     */
    private ActionInfo generateJackUpAction(){
        return new ActionInfo(ActionType.JackingUp,new JSONObject());
    }

    /**
     * 放货动作
     * @return
     */
    private ActionInfo generateJackDownAction(){
        return new ActionInfo(ActionType.JackingDown,new JSONObject());
    }

    private TaskInfo buildTaskInfo() {
        TaskInfo task = new TaskInfo();
        task.pts = new Vector<>();
        if(pickUpPoiIndex > -1){
            TaskPoint taskPoint = generateTaskPoint(poiLists.get(pickUpPoiIndex));
            taskPoint.stepActs =  new Vector<>();
            taskPoint.stepActs.add(generateJackUpAction());
            task.pts.add(taskPoint);
        }
        if(pickDownPoiIndex > -1){
            TaskPoint taskPoint = generateTaskPoint(poiLists.get(pickDownPoiIndex));
            taskPoint.stepActs =  new Vector<>();
            taskPoint.stepActs.add(generateJackDownAction());
            task.pts.add(taskPoint);
        }
        return task;
    }

    private void createTask() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    TaskInfo task = buildTaskInfo();
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
    public void onTaskChanged(final ActionInfo actionInfo) {
        Log.e(TAG, actionInfo.actType + "," + actionInfo.data);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv_status.setText(actionInfo.actType + "," + actionInfo.data);
            }
        });
    }

    private class PoiAdapter extends ArrayAdapter<JSONObject> {
        private int resourceId;
        private boolean isPickUp;

        public PoiAdapter(Context context, int resourceId, List<JSONObject> objects,boolean isPickUp) {
            super(context, resourceId, objects);
            this.resourceId = resourceId;
            this.isPickUp = isPickUp;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            JSONObject poiObj = getItem(position);
            View view = LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
            TextView text_poi = (TextView)view.findViewById(R.id.text_poi);
            int floor = poiObj.getIntValue("floor");
            String name = poiObj.getString("name") + "（" + floor + "层）";
            text_poi.setText(name);
            boolean isSelect = isPickUp ? pickUpPoiIndex == position : pickDownPoiIndex == position;
            view.setBackgroundColor(isSelect ? Color.LTGRAY:Color.WHITE);  // 选中时的背景色
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

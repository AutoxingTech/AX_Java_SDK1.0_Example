package com.autoxing.sdk.android.example.motion;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.autoxing.robot.sdk.AXRobot;
import com.autoxing.robot.sdk.model.Pose;
import com.autoxing.sdk.android.example.MainActivity;
import com.autoxing.sdk.android.example.MyApplication;
import com.autoxing.sdk.android.example.R;

import java.util.ArrayList;
import java.util.List;

public class PoitoActivity extends AppCompatActivity {
    private final static String TAG = "PoitoActivity";

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
        PoiAdapter adapter = new PoiAdapter(PoitoActivity.this, R.layout.poi_list_item, poiLists);
        ListView list_poi = (ListView)findViewById(R.id.list_poi);
        list_poi.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                JSONObject poiObj = poiLists.get(position);
                JSONArray coordinate = poiObj.getJSONArray("coordinate");
                Log.e(TAG, poiObj.toJSONString());
                float x = coordinate.getFloatValue(0);
                float y = coordinate.getFloatValue(1);
                float yaw = poiObj.getFloatValue("yaw");
                Pose pose = new Pose(x, y, yaw);
                mAXRobot.moveTo(pose);
            }
        });
        list_poi.setAdapter(adapter);
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

package com.nova.yonggyun_client.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.nova.yonggyun_client.R;
import com.nova.yonggyun_client.adater.DailyMissionAdapter;
import com.nova.yonggyun_client.item.DailyMissionRecyclerItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DailyMissionActivity extends AppCompatActivity {

    private static final String TAG = DailyMissionActivity.class.getSimpleName();
    private Toolbar toolbar;
    private ActionBar actionBar;
    private RecyclerView mRcvDailyMission;
    private LinearLayoutManager mLayoutManager;
    ArrayList<DailyMissionRecyclerItem> mDailyMissionDataList;
    private DailyMissionAdapter mDailyMissionAdapter;
    DailyMissionRecyclerItem mDailyMissionRecyclerItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_mission);

        toolbar = findViewById(R.id.toolbar);
        mRcvDailyMission = findViewById(R.id.rcv_daily_mission);

        // 상단에 액션바
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);    //기본 제목을 없애줍니다.
        actionBar.setDisplayHomeAsUpEnabled(true);      // 뒤로 가기 버튼을 자동으로 만들어준다.

        // 리사이클러뷰
        mRcvDailyMission = findViewById(R.id.rcv_daily_mission);
        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRcvDailyMission.setLayoutManager(mLayoutManager);

        mDailyMissionDataList = new ArrayList<>();
        mDailyMissionAdapter =new DailyMissionAdapter();
        mDailyMissionAdapter.setData(mDailyMissionDataList);
        mRcvDailyMission.setAdapter(mDailyMissionAdapter);

        mDailyMissionAdapter.setOnItemClickListener(new DailyMissionAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                // Toast.makeText(DailyMissionActivity.this, ""+position, Toast.LENGTH_SHORT).show();

                DailyMissionRecyclerItem dailyMissionRecyclerItem = mDailyMissionDataList.get(position);
                Intent intent = new Intent(DailyMissionActivity.this , DailyMissionDetailActivity.class);
                intent.putExtra("DailyMissionRecyclerItem",dailyMissionRecyclerItem);
                startActivity(intent);
            }
        });
    }

    public void fetchPatdata(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "해당url", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("success");
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    // 데이터 초기화
                    mDailyMissionDataList.clear();

                    if (success.equals("success")) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            String Img = object.getString("missionimg");
                            String description = object.getString("description");
                            String title = object.getString("title");
//                            String completCount = object.getString("completcount");
//                            String participantCount = object.getString("participantcount");
                            String mainImg = "해당url"+Img;
                            //mDailyMissionRecyclerItems = new DailyMissionRecyclerItem(mainImg,description,title,completCount,participantCount);
                            mDailyMissionRecyclerItems = new DailyMissionRecyclerItem(mainImg,description,title);
                            mDailyMissionDataList.add(mDailyMissionRecyclerItems);
                        }
                        mDailyMissionAdapter.notifyDataSetChanged();
                        mRcvDailyMission.invalidate();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(DailyMissionActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    // 상단액션바에 back 버튼 클릭시 뒤로 가기
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");    
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
        fetchPatdata();

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }
}

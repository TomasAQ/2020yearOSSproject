package com.nova.yonggyun_client.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.nova.yonggyun_client.R;
import com.nova.yonggyun_client.adater.DailyMissionDeatilViewPagerAdapter;
import com.nova.yonggyun_client.adater.DailyMissionDetailRecyclerAdapter;
import com.nova.yonggyun_client.item.DailyMissionDetailRecyclerItem;
import com.nova.yonggyun_client.item.DailyMissionRecyclerItem;
import com.nova.yonggyun_client.item.DailyMissionViewPageritem;
import com.nova.yonggyun_client.util.SharedPreferenceData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DailyMissionDetailActivity extends AppCompatActivity {
    private static final String TAG = DailyMissionDetailActivity.class.getSimpleName();
    private Toolbar toolbar;
    private ActionBar actionBar;
    DailyMissionRecyclerItem mDailyMissionRecyclerItem;

    private ViewPager2 viewPager2;
    private Handler mSliderHandler = new Handler();
    private RecyclerView mRcvDailyCompltMember;
    private LinearLayoutManager mLayoutManager;
    private DailyMissionDetailRecyclerAdapter mDailyMissionDetailRecyclerAdapter;
    private DailyMissionDetailRecyclerItem mDailyMissionDetailRecyclerItem;
    ArrayList<DailyMissionDetailRecyclerItem> mDataList;
    TextView mMission_ck1,mMission_ck2,mMission_ck3,mMission_ck4,mMission_ck5,mMission_ck6,mMission_ck7;
    TextView mMissionCompletNum , mMissionRemainingNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_mission_detail);

        toolbar = findViewById(R.id.toolbar);
        viewPager2 = findViewById(R.id.mission_view_pager);
        mRcvDailyCompltMember = findViewById(R.id.rcv_daily_complt_member);
        mMission_ck1 = findViewById(R.id.mission_ck1);
        mMission_ck2 = findViewById(R.id.mission_ck2);
        mMission_ck3 = findViewById(R.id.mission_ck3);
        mMission_ck4 = findViewById(R.id.mission_ck4);
        mMission_ck5 = findViewById(R.id.mission_ck5);
        mMission_ck6 = findViewById(R.id.mission_ck6);
        mMission_ck7 = findViewById(R.id.mission_ck7);

        mMissionCompletNum = findViewById(R.id.txt_mission_complet_num);
        mMissionRemainingNum = findViewById(R.id.txt_mission_remaining_num);
        Intent intent = getIntent();
        mDailyMissionRecyclerItem =(DailyMissionRecyclerItem)intent.getSerializableExtra("DailyMissionRecyclerItem");


        List<DailyMissionViewPageritem> dataList = new ArrayList<>();
        dataList.add(new DailyMissionViewPageritem(R.drawable.mission1));
        dataList.add(new DailyMissionViewPageritem(R.drawable.mission2));
        dataList.add(new DailyMissionViewPageritem(R.drawable.mission3));
        dataList.add(new DailyMissionViewPageritem(R.drawable.mission4));
        dataList.add(new DailyMissionViewPageritem(R.drawable.mission5));
        dataList.add(new DailyMissionViewPageritem(R.drawable.mission6));
        dataList.add(new DailyMissionViewPageritem(R.drawable.mission7));


        viewPager2.setAdapter(new DailyMissionDeatilViewPagerAdapter(dataList,viewPager2));
        viewPager2.setClipToPadding(false);
        viewPager2.setClipChildren(false);
        viewPager2.setOffscreenPageLimit(3);
        viewPager2.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(40));
        compositePageTransformer.addTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                float r = 1- Math.abs(position);
                page.setScaleY(0.85f + r*0.15f);
            }
        });

        // 상단 뷰페이저
        viewPager2.setPageTransformer(compositePageTransformer);
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                mSliderHandler.removeCallbacks(sliderRunable);
                mSliderHandler.postDelayed(sliderRunable, 3000);
            }
        });

        // 상단에 메뉴
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);    //기본 제목을 없애줍니다.
        actionBar.setDisplayHomeAsUpEnabled(true);      // 뒤로 가기 버튼을 자동으로 만들어준다.
        actionBar.setTitle(mDailyMissionRecyclerItem.getTitle());

        // 하단 미션완료 회원 리스트
        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRcvDailyCompltMember.setLayoutManager(mLayoutManager);
        mDataList = new ArrayList<>();
        mDailyMissionDetailRecyclerAdapter = new DailyMissionDetailRecyclerAdapter(mDataList);
        mRcvDailyCompltMember.setAdapter(mDailyMissionDetailRecyclerAdapter);

        String ck1data = SharedPreferenceData.getAttribute(getBaseContext(),mDailyMissionRecyclerItem.getTitle()+"ck1");
        String ck2data = SharedPreferenceData.getAttribute(getBaseContext(),mDailyMissionRecyclerItem.getTitle()+"ck2");
        String ck3data = SharedPreferenceData.getAttribute(getBaseContext(),mDailyMissionRecyclerItem.getTitle()+"ck3");
        String ck4data = SharedPreferenceData.getAttribute(getBaseContext(),mDailyMissionRecyclerItem.getTitle()+"ck4");
        String ck5data = SharedPreferenceData.getAttribute(getBaseContext(),mDailyMissionRecyclerItem.getTitle()+"ck5");
        String ck6data = SharedPreferenceData.getAttribute(getBaseContext(),mDailyMissionRecyclerItem.getTitle()+"ck6");
        String ck7data = SharedPreferenceData.getAttribute(getBaseContext(),mDailyMissionRecyclerItem.getTitle()+"ck7");
        String completdata = SharedPreferenceData.getAttribute(getBaseContext(),mDailyMissionRecyclerItem.getTitle()+"completcount");
        if ( ck1data != null ){
            mMission_ck1.setText(ck1data);
            mMissionCompletNum.setText(completdata);
            int cpCount =Integer.parseInt(completdata);
            mMissionRemainingNum.setText(Integer.toString(7- cpCount));
        }
        if(ck2data != null)
            mMission_ck2.setText(ck2data);
        if(ck3data != null)
            mMission_ck3.setText(ck3data);
        if(ck4data != null)
            mMission_ck4.setText(ck4data);
        if(ck5data != null)
            mMission_ck5.setText(ck5data);
        if(ck6data != null)
            mMission_ck6.setText(ck6data);
        if(ck7data != null)
            mMission_ck7.setText(ck7data);


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

    // 미션 체크
    public void missionChk(View view) {

        String data = SharedPreferenceData.getAttribute(getBaseContext() , "userid");
        if (data == null || data.isEmpty() || data.equals("") ){
            Toast.makeText(this, "데일리 미션을 사용하려면 로그인이 필요합니다. ", Toast.LENGTH_SHORT).show();
        }else {

            // 1. 서버에 유저이메일과 미션id값을 보냄
            StringRequest stringRequest = new StringRequest(Request.Method.POST, "해당url", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(response);
                        String success = jsonObject.getString("success");
                        //boolean success = jsonObject.getBoolean("success");
                        String idx = jsonObject.getString("idx");
                        String completcount = jsonObject.getString("completcount");
                        String completdate = jsonObject.getString("completdate");
                        // 연도를 제외한 날짜만 표시
                        int year = completdate.indexOf("-");
                        String date = completdate.substring(year+1);

                        int cpCount =Integer.parseInt(completcount);
                        // 2. 미션 현황 변경하기

                        mMissionCompletNum.setText(completcount);
                        mMissionRemainingNum.setText(Integer.toString(7- cpCount));
                        SharedPreferenceData.setAttribute(getBaseContext(),mDailyMissionRecyclerItem.getTitle()+"completcount", completcount);

                        if (completcount.equals("1")){
                            mMission_ck1.setText(date);
                            SharedPreferenceData.setAttribute(getBaseContext(),mDailyMissionRecyclerItem.getTitle()+"ck1", date);
                        }else if(completcount.equals("2")){
                            mMission_ck2.setText(date);
                            SharedPreferenceData.setAttribute(getBaseContext(),mDailyMissionRecyclerItem.getTitle()+"ck2", date);
                        }else if(completcount.equals("3")){
                            mMission_ck3.setText(date);
                            SharedPreferenceData.setAttribute(getBaseContext(),mDailyMissionRecyclerItem.getTitle()+"ck3", date);
                        }else if(completcount.equals("4")){
                            mMission_ck4.setText(date);
                            SharedPreferenceData.setAttribute(getBaseContext(),mDailyMissionRecyclerItem.getTitle()+"ck4", date);
                        }else if(completcount.equals("5")){
                            mMission_ck5.setText(date);
                            SharedPreferenceData.setAttribute(getBaseContext(),mDailyMissionRecyclerItem.getTitle()+"ck5", date);
                        }else if(completcount.equals("6")){
                            mMission_ck6.setText(date);
                            SharedPreferenceData.setAttribute(getBaseContext(),mDailyMissionRecyclerItem.getTitle()+"ck6", date);
                        }else if(completcount.equals("7")){
                            mMission_ck7.setText(date);
                            SharedPreferenceData.setAttribute(getBaseContext(),mDailyMissionRecyclerItem.getTitle()+"ck7", date);
                        }

                        // 3. 세어드를 사용해서 미션 현황 유지


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(DailyMissionDetailActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    String useremail = SharedPreferenceData.getAttribute(getBaseContext() , "useremail");
                    String missiontitle = mDailyMissionRecyclerItem.getTitle();
                    Log.d(TAG, "getParams: useremail : "+useremail);
                    Log.d(TAG, "getParams: missiontitle : "+missiontitle);
                    Map<String,String> params = new HashMap<>();
                    params.put("useremail",useremail);
                    params.put("missiontitle",missiontitle);
                    return params;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(DailyMissionDetailActivity.this);
            requestQueue.add(stringRequest);
        }

        fetchPatdata();
    }

    // 뷰페이저 자동 이미지 변경
    private Runnable sliderRunable = new Runnable() {
        @Override
        public void run() {
            viewPager2.setCurrentItem(viewPager2.getCurrentItem()+1);
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        mSliderHandler.removeCallbacks(sliderRunable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchPatdata();
        mSliderHandler.postDelayed(sliderRunable,3000);
    }

    public void fetchPatdata(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "해당url", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                  // Log.d(TAG, "onResponse: "+response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    // 데이터 초기화
                    mDataList.clear();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        String nickname = object.getString("nickname");
                        String completcount = object.getString("completcount");
                        String completdate = object.getString("completdate");
                        mDataList.add(new DailyMissionDetailRecyclerItem(completdate ,nickname+" 회원 미션 완료"));
                    }
                    mDailyMissionDetailRecyclerAdapter.notifyDataSetChanged();
                    mRcvDailyCompltMember.invalidate();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(DailyMissionDetailActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                String useremail = SharedPreferenceData.getAttribute(getBaseContext() , "useremail");
                String missiontitle = mDailyMissionRecyclerItem.getTitle();
                Map<String,String> params = new HashMap<>();
                params.put("useremail",useremail);
                params.put("missiontitle",missiontitle);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


    // 알람 설정
    public void setAlarm(View view) {
        Intent intent = new Intent(DailyMissionDetailActivity.this , AlarmActivity.class);
        intent.putExtra("DailyMissionTitle",mDailyMissionRecyclerItem.getTitle());
        startActivity(intent);
    }
}

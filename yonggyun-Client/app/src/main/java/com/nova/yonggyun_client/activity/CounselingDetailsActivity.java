package com.nova.yonggyun_client.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.nova.yonggyun_client.R;
import com.nova.yonggyun_client.adater.CounselingchatAdapter;
import com.nova.yonggyun_client.item.CounselingRecyclerItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CounselingDetailsActivity extends AppCompatActivity {
    private static final String TAG = CounselingDetailsActivity.class.getSimpleName();

    RecyclerView mRecyclerCounselingchat;
    LinearLayoutManager mLayoutManager;
    CounselingchatAdapter mConCounselingchatAdapter;
    ArrayList<CounselingRecyclerItem> mDataList;
    CounselingRecyclerItem mCounselingRecyclerItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counseling_details);

        mRecyclerCounselingchat = findViewById(R.id.recycler_counselingchat);
        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerCounselingchat.setLayoutManager(mLayoutManager);

        mDataList = new ArrayList<>();
        mConCounselingchatAdapter = new CounselingchatAdapter();
        mConCounselingchatAdapter.setData(mDataList);
        mRecyclerCounselingchat.setAdapter(mConCounselingchatAdapter);

        mConCounselingchatAdapter.setOnItemClickListener(new CounselingchatAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                CounselingRecyclerItem counselingRecyclerItem = mDataList.get(position);
                Intent intent = new Intent(CounselingDetailsActivity.this,ChatingActivity.class);
                intent.putExtra("idx",counselingRecyclerItem.getIdx());
                startActivity(intent);
            }
        });
    }


    public void counselingfetchPatdata(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "해당url", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("success");
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    // 데이터 초기화
                    mDataList.clear();

                    if (success.equals("success")) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            String idx = object.getString("cs.idx");
                            String petName = object.getString("pt.name");
                            String petImg = object.getString("pt.imageurl");
                            String petIdx = object.getString("cs.petidx");
                            String counselingClassification = object.getString("cs.counselingclassification");
                            String counselingContent = object.getString("cs.counselingcontent");
                            String counselingImg = object.getString("cs.counselingimg");

                            String petImgurl = "해당url" + petImg;
                            String counselingImgurl = "해당url" + counselingImg;
                            mCounselingRecyclerItem = new CounselingRecyclerItem(idx,petName,petImgurl,petIdx,counselingClassification,counselingContent,counselingImgurl);
                            mDataList.add(mCounselingRecyclerItem);
                        }
                        mConCounselingchatAdapter.notifyDataSetChanged();
                        mRecyclerCounselingchat.invalidate();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(CounselingDetailsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void movepage(View view) {
        Intent intent = null;
        switch (view.getId()){
            case R.id.btn_counseling:
                intent = new Intent(this , CounselingActivity.class);
                break;
            case R.id.btn_counselchat:
                intent = new Intent(this , CounselingDetailsActivity.class);
                break;
            default:
                Log.d(TAG, "MovePage()함수 오류 ");
        }
        finish();
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        counselingfetchPatdata();
    }
}

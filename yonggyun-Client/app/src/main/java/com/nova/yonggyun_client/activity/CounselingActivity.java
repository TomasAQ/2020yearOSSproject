package com.nova.yonggyun_client.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.nova.yonggyun_client.R;
import com.nova.yonggyun_client.adater.PetRecyclerAdapter;
import com.nova.yonggyun_client.item.PetsRecyclerItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CounselingActivity extends AppCompatActivity {

    private static final String TAG = CounselingActivity.class.getSimpleName();
    RecyclerView mRecyclerPets;
    LinearLayoutManager mLayoutManager;
    PetRecyclerAdapter mPetRecyclerAdapter;
    ArrayList<PetsRecyclerItem> dataList;
    PetsRecyclerItem mPetsRecyclerItem;
    RadioGroup mCounselingClassification;
    String mCounseling;
    // 선택한 반려 동물
    List<String> selectPetList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counseling);

        mRecyclerPets = findViewById(R.id.recycler_pets);
        // LayoutManager
        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerPets.setLayoutManager(mLayoutManager);

        mPetRecyclerAdapter = new PetRecyclerAdapter();
        dataList = new ArrayList<>();
        mPetRecyclerAdapter.setData(dataList);

        mRecyclerPets.setAdapter(mPetRecyclerAdapter);

        mCounselingClassification = findViewById(R.id.rg_counseling_classification);


        mPetRecyclerAdapter.setOnItemClickListener(new PetRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                PetsRecyclerItem PetsRecyclerItem = dataList.get(position);
                TextView descriptionRecyclerPets= v.findViewById(R.id.description_recycler_pets);
                boolean choiceCheck = PetsRecyclerItem.isChoice();

                if (choiceCheck == true){
                    selectPetList.remove(PetsRecyclerItem.getIdx());
                    PetsRecyclerItem.setChoice(false);
                    descriptionRecyclerPets.setBackgroundColor(0x00000000);
                }else{
                    selectPetList.add(PetsRecyclerItem.getIdx());
                    PetsRecyclerItem.setChoice(true);
                    descriptionRecyclerPets.setBackgroundColor(Color.parseColor("#eac5d7"));
                }

            }
        });



        // 상담분류
        mCounselingClassification.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.rg_btn_behavior){
                    mCounseling = "행동";
                }else if(checkedId == R.id.rg_btn_nutrition){
                    mCounseling = "영양";
                }else if(checkedId == R.id.rg_btn_disease){
                    mCounseling = "질병";
                }else if(checkedId == R.id.rg_btn_surgery){
                    mCounseling = "수술";
                }else if(checkedId == R.id.rg_btn_vaccination){
                    mCounseling = "예방접종";
                }else if(checkedId == R.id.rg_btn_etc){
                    mCounseling = "기타";
                }
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
                    dataList.clear();
                    if (success.equals("success")){
                        for (int i =0 ; i<jsonArray.length() ; i++){
                            JSONObject object = jsonArray.getJSONObject(i);
                            String idx = object.getString("idx");
                            String imageurl = object.getString("imageurl");
                            String name = object.getString("name");

                            String url = "해당url/"+imageurl;
                            mPetsRecyclerItem = new PetsRecyclerItem(idx,url,name);
                            dataList.add(mPetsRecyclerItem);
                        }
                        mPetRecyclerAdapter.notifyDataSetChanged();
                        mRecyclerPets.invalidate();

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(CounselingActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


    public void petRegistration(View view) {
        Intent intent = new Intent(this,PatRegistrationActivity.class);
        startActivity(intent);
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
        // 데이터 불러오기
        fetchPatdata();
    }

    public void selectionComplete(View view) {

        if(TextUtils.isEmpty(mCounseling) || selectPetList.size() == 0){
            Toast.makeText(this, "반려 동물 및 상담분류를 선택해주세요.", Toast.LENGTH_SHORT).show();
        }else{
            Log.d(TAG, "selectionComplete: mCounseling: "+mCounseling);
            Log.d(TAG, "selectionComplete: size: "+selectPetList.size());

            Intent intent = new Intent(this,CounsellingInfoActivity.class);
            intent.putExtra("petIdx",selectPetList.get(0));
            intent.putExtra("counselng",mCounseling);
            startActivity(intent);
        }

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
}

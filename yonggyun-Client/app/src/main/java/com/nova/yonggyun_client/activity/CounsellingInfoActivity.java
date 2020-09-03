package com.nova.yonggyun_client.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.nova.yonggyun_client.R;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class CounsellingInfoActivity extends AppCompatActivity {
    private static final int REQUEST_TAKE_PHOTO=1;
    Switch consent1,consent2,consent3,consent4;
    private Bitmap bitmap;
    ImageButton mImgBtnCounseling;
    // 상담분류에서 넘겨 받는 데이터
    String mPetIdx , mCounselng;
    EditText mconsultationContent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counselling_info);

        // 상담 전 주의사항
        consent1 = findViewById(R.id.consent1);
        consent2 = findViewById(R.id.consent2);
        consent3 = findViewById(R.id.consent3);
        consent4 = findViewById(R.id.consent4);

        mImgBtnCounseling = findViewById(R.id.img_btn_counseling);
        mconsultationContent = findViewById(R.id.consultation_content);

        Intent intent = getIntent();
        mPetIdx = intent.getExtras().getString("petIdx");
        mCounselng = intent.getExtras().getString("counselng");

    }

    /**
     *  상담 진행
     */
    public void startconsultation(View view) {

        if (consent1.isChecked() && consent2.isChecked() && consent3.isChecked() && consent4.isChecked()){

        // 1. 상담방 만들기
            StringRequest stringRequest = new StringRequest(Request.Method.POST, "해당url", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    // 2. 채팅 페이지로 이동 -> roomName값을 주어야한다.
                    Intent intent = new Intent(CounsellingInfoActivity.this,ChatingActivity.class);
                    intent.putExtra("idx",response);
                    startActivity(intent);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(CounsellingInfoActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> params = new HashMap<>();
                    String image = imageStore(bitmap);
                    params.put("image",image);
                    params.put("petidx",mPetIdx);
                    params.put("counselingclassification",mCounselng);
                    params.put("counselingcontent",mconsultationContent.getText().toString());

                    return params;

                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(CounsellingInfoActivity.this);
            requestQueue.add(stringRequest);


        }else {
            Toast.makeText(this, "상담 전 주의사항을 모두 동의해주세요.", Toast.LENGTH_SHORT).show();
        }

    }

    // 모두 동의
    public void consentalltrue(View view) {
        consent1.setChecked(true);
        consent2.setChecked(true);
        consent3.setChecked(true);
        consent4.setChecked(true);
    }

    public void selectCounselingImg(View view) {
        // 1. 권한 관련 체크
        Dexter.withContext(this).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                // 2. 이미지 선택
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent,"Select Image"),REQUEST_TAKE_PHOTO);
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) { }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            // 사진 접근 권한
            if(requestCode == REQUEST_TAKE_PHOTO){
                Uri filePath = data.getData();
                try {
                    InputStream inputStream = getContentResolver().openInputStream(filePath);
                    bitmap = BitmapFactory.decodeStream(inputStream);
                    mImgBtnCounseling.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String imageStore(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        String temp = Base64.encodeToString(imageBytes,Base64.DEFAULT);
        return temp;
    }
}

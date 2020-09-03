package com.nova.yonggyun_client.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
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


public class PatRegistrationActivity extends AppCompatActivity {
    private static final String TAG = PatRegistrationActivity.class.getSimpleName();
    private static final int REQUEST_TAKE_PHOTO=1;

    ImageButton mImgBtnPetImage;
    EditText mEditPetName,mEditPetAge , mEditPetSpecies;
    RadioGroup mRadioGroupGender , mRadioGroupVaccination,mRadioGroupNeutralization;
    Bitmap bitmap;

    String mGender;
    //중성화 여부
    String mNeutralization;
    // 예방 접종
    String mVaccination;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pat_registration);

        mImgBtnPetImage = findViewById(R.id.img_btn_pet_image);
        mEditPetName = findViewById(R.id.edit_pet_name);
        mEditPetAge = findViewById(R.id.edit_pet_age);
        mEditPetSpecies = findViewById(R.id.edit_pet_species);

        mRadioGroupGender = findViewById(R.id.radio_group_gender);
        mRadioGroupVaccination = findViewById(R.id.radio_group_vaccination);
        mRadioGroupNeutralization = findViewById(R.id.radio_group_neutralization);

        mRadioGroupGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.rg_btn_male){
                    mGender = "남아";
                }else if(checkedId == R.id.rg_btn_female){
                    mGender = "여아";
                }
            }
        });
        // 중성화 여부
        mRadioGroupNeutralization.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.rg_btn_neutralization_after){
                    mNeutralization = "중성화 전";
                }else if(checkedId ==R.id.rg_btn_neutralization_before){
                    mNeutralization = "중성화 후";
                }
            }
        });
        // 기초 예방접종 여부
        mRadioGroupVaccination.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rg_vaccination_unsure){
                    mVaccination = "모름";
                }else if(checkedId == R.id.rg_vaccination_before){
                    mVaccination = "접종전";
                }else if(checkedId == R.id.rg_vaccination_inoculation){
                    mVaccination = "접종중";
                }else if(checkedId == R.id.rg_vaccination_complete){
                    mVaccination = "접종 완료";
                }
            }
        });

    }

    public void back(View view) {
        finish();
        startActivity(new Intent(this , CounselingActivity.class));

    }

    /**
     *  이미지 선택하기
     */
    public void selectImage(View view) {
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



    /**
     * 서버에 데이터를 저장
     */
    public void savePetdata(View view) {
        if (TextUtils.isEmpty(mEditPetName.getText().toString()) || TextUtils.isEmpty(mEditPetAge.getText().toString()) || TextUtils.isEmpty(mGender) ){
            Toast.makeText(this, "정보를 모두 입력해주세요.", Toast.LENGTH_SHORT).show();
        }else {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, "해당url", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    // Toast.makeText(PatRegistrationActivity.this, response, Toast.LENGTH_SHORT).show();
                    finish();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(PatRegistrationActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> params = new HashMap<>();
                    String image = imageStore(bitmap);
                    params.put("image",image);
                    params.put("name",mEditPetName.getText().toString());
                    params.put("age",mEditPetAge.getText().toString());
                    params.put("petspecies",mEditPetSpecies.getText().toString());
                    params.put("gender",mGender);
                    params.put("neutralization",mNeutralization);
                    params.put("vaccination",mVaccination);
                    return params;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(PatRegistrationActivity.this);
            requestQueue.add(stringRequest);
        }


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
                    mImgBtnPetImage.setImageBitmap(bitmap);
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

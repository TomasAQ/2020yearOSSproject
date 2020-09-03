package com.nova.yonggyun_client.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;

import com.nova.yonggyun_client.R;
import com.nova.yonggyun_client.util.SharedPreferenceData;

public class SettingActivity extends AppCompatActivity {

    public static final String[] MANDATORY_PERMISSIONS = {
            "android.permission.INTERNET",
            "android.permission.CAMERA",
            "android.permission.RECORD_AUDIO",
            "android.permission.MODIFY_AUDIO_SETTINGS",
            "android.permission.ACCESS_NETWORK_STATE",
            "android.permission.CHANGE_WIFI_STATE",
            "android.permission.ACCESS_WIFI_STATE",
            "android.permission.READ_PHONE_STATE",
            "android.permission.BLUETOOTH",
            "android.permission.BLUETOOTH_ADMIN",
            "android.permission.WRITE_EXTERNAL_STORAGE"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        if (Build.VERSION.SDK_INT >= 23) {
            checkPermission(MANDATORY_PERMISSIONS);
        }
    }

    public void movelogin(View view) {
        Intent intent = new Intent(this , LoginActivity.class);
        startActivity(intent);
    }

    public void logoutuser(View view) {
        SharedPreferenceData.removeAllAttribute(getBaseContext());
    }


    public void remote(View view) {
        // 방송을 위한 방을 생성하는 페이지
        Intent intent = new Intent(this , RemotemonActivity.class);
        intent.putExtra("isCreate", true);
        startActivity(intent);
    }

    public void seebrod(View view) {
        // 방송을 시정하기 위해서 방송 목록을 보여주는 페이지
        Intent intent = new Intent(this , RmViewerListActivity.class);
        intent.putExtra("isCreate", false);
        startActivity(intent);
    }

    @SuppressLint("NewApi")
    private void checkPermission(String[] permissions) {
        requestPermissions(permissions, 100);
    }

    public void searchcat(View view) {
        Intent intent = new Intent(this, SerachCatActivity.class);
        startActivity(intent);
    }

    public void Selfie(View view) {
        Intent intent = new Intent(this, SelfieActivity.class);
        startActivity(intent);
    }

//    public void showSelfie(View view) {
//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        String tempPath = Environment.getExternalStorageDirectory().getAbsolutePath();
//        Uri uri = Uri.parse(tempPath+"/Android/data/"+getPackageName()+"/");
//        intent.setDataAndType(uri, "image/*");
//        startActivity(Intent.createChooser(intent, "냥냥 셀피 사진첩"));
//    }
}

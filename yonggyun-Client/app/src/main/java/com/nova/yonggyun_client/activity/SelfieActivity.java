package com.nova.yonggyun_client.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;

import com.nova.yonggyun_client.R;
import com.nova.yonggyun_client.util.CameraAPI;

import java.io.File;

public class SelfieActivity extends AppCompatActivity {

    private static final String TAG = SelfieActivity.class.getSimpleName();
    String [] permission_list = {
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    //사진이 저장된 경로
    String dirpath;

    CameraAPI cameraAPI;

    TextureView textureView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 화면 출력 설정 1. 세로 , 2. 가로
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_selfie);
        textureView = findViewById(R.id.textureView);

        // 화면 유지
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // 액션바 제거
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN , WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        getSupportActionBar().hide();

        findViewById(R.id.game).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: 터치!!!!!!");
                String filePath = dirpath+"/temp_"+System.currentTimeMillis()+".jpg";
                cameraAPI.imageCapture(filePath);
            }
        });

        // 권한
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            requestPermissions(permission_list,0);
        }else {
            init();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int result : grantResults){
            if (result == PackageManager.PERMISSION_DENIED){
                return;
            }
        }

        init();
    }

    private void init() {
        // 사진이 저장된 경로를 설정하고 없으면 폴더를 만든다.
        try {
            String tempPath = Environment.getExternalStorageDirectory().getAbsolutePath();
            dirpath = tempPath+"/Android/data/"+getPackageName();

            File file = new File(dirpath);
            if(file.exists() == false){
                file.mkdir();
            }

            cameraAPI = new CameraAPI(this , textureView);
            cameraAPI.init();
            Log.d(TAG, "init: dirpath"+dirpath);

        }catch (Exception e){
             e.printStackTrace();
        }
    }



}

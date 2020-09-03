package com.nova.yonggyun_client.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.view.Display;
import android.view.Surface;
import android.view.TextureView;
import android.view.WindowManager;

import com.nova.yonggyun_client.activity.SelfieActivity;

import java.io.File;
import java.io.FileOutputStream;

public class CameraAPI {

    private SelfieActivity activity;
    private TextureView textureView;

    private Camera camera;

    public CameraAPI(SelfieActivity activity , TextureView textureView) {
        this.activity = activity;
        this.textureView = textureView;

    }

    // 카메라 열기 및 미리보기 화면 출력
    public void init(){
        try {
            // 카메라 동작
            camera = Camera.open(1);


            // 카메라 회전을 조절
            WindowManager windowManager = activity.getWindowManager();
            Display display = windowManager.getDefaultDisplay();
            int rotation = display.getRotation();

            int degree = 0;
            switch (rotation){
                // 가로 모드
                case Surface.ROTATION_0 :
                    degree = 90;
                    break;

                case Surface.ROTATION_90 :
                    degree = 0;
                    break;

                case Surface.ROTATION_180 :
                    degree = 270;
                    break;

                case Surface.ROTATION_270 :
                    degree = 180;
                    break;
            }

            camera.setDisplayOrientation(degree);


            boolean chk = textureView.isAvailable();

            if (chk == true){
                startPreview();
            }else {
                TextureListener linstener = new TextureListener();
                textureView.setSurfaceTextureListener(linstener);
            }

            // boolean chk = textureView.isAvailable();


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    // TexureView의 상태가 변화하면 반응하는 리스너
    private class TextureListener  implements TextureView.SurfaceTextureListener{
        // 사용 가능한 상태
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            // 미리보기 처리
            startPreview();
        }
        // 사이즈 변경
        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }
    }

    // 미리보기 처리
    private void startPreview(){
        try {
            camera.setPreviewTexture(textureView.getSurfaceTexture());
            camera.startPreview();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    // 사진 촬영
    public  void imageCapture(String filePath){
        CaptureCallback captureCallback = new CaptureCallback(filePath);
        camera.takePicture(null , null , captureCallback);

    }

    // 사진 촬영이 성공하면 반응하는 콜백
    private class CaptureCallback implements Camera.PictureCallback{
        String filePath;

        public CaptureCallback(String filePath){
            this.filePath = filePath;
        }

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            try {
                Bitmap bitmap = BitmapFactory.decodeByteArray(data,0,data.length);

                File file = new File(filePath);
                FileOutputStream fos = new FileOutputStream(file);

                bitmap.compress(Bitmap.CompressFormat.JPEG , 100 , fos);
                fos.flush();
                fos.close();

                startPreview();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

}

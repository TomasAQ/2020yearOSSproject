package com.nova.yonggyun_client.game;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.nova.yonggyun_client.R;

import java.text.DecimalFormat;
import java.util.Random;

public class Game extends View {

    private String TAG = Game.class.getSimpleName();
    Context context;

    // 스크린 넓이 와 높이
    int screenWidth , screenHeight;

    // T라는 이름의 게임 쓰레드를 Game 클래스 내부에서만 사용한다.
    private GameThread T;
    // 캐릭터 이미지 기준점의 x죄표와 y좌표 위치 값을 저장할 정수형 변수
    int chX , chY;

    // 생존 시간
    double suviceTime =0;

    Paint paint1 = new Paint();

    // 센서 좌표
    int xValue;
    int yValue;
    int zValue;

    // 적군 좌표
    int[] armyMoveX = new int[4];
    int[] armyMoveY = new int[4];
    // 적군 이동
    String[] armylocation = new String[4];

    boolean armyChk = false;

    // 랜덤 변수
    Random random = new Random();

    boolean gameCk = false;

    // 기울기 센서를 통해서 캐릭터 이동
    SensorEventListener listener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {

            if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
                //xValue = Math.abs(event.values[0] - lastX);
                //yValue = Math.abs(event.values[1] - lastY);
                //zValue = Math.abs(event.values[2] - lastZ);

                xValue =(int) event.values[0];
                //yValue =(int) event.values[1];
                zValue =(int) event.values[2];
/*
            Log.d(TAG, "onSensorChanged: X : "+xValue);
            Log.d(TAG, "onSensorChanged: Y : "+yValue);
            Log.d(TAG, "onSensorChanged: Z : "+zValue);*/
            }else if(event.sensor.getType() == Sensor.TYPE_ORIENTATION){

                yValue =(int) event.values[1];

            }


        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };



    // 생성자 -> Context는 앱에 대한 다양한 정보가 들어 있다. AttributeSet은 xml 정보를 가져온다.
    public Game(Context con , AttributeSet at ){
        super(con , at);
        this.context = con;
        //음악 연결
        /*mp = MediaPlayer.create(con , R.raw.timeloop);
        mp.start();
        // 무한 재성
        mp.setLooping(true);*/

        // 센서 추가
        SensorManager sm = (SensorManager) con.getSystemService(Context.SENSOR_SERVICE);
        Sensor accelerometer = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sm.registerListener(listener , accelerometer , SensorManager.SENSOR_DELAY_GAME);
        Sensor orientationSensor = sm.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        sm.registerListener(listener , orientationSensor , SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // 스크린 넓이와 높이
        // 뷰의 폭 정보 저장
        this.screenWidth = w;
        // 뷰의 높이 정보 저장
        this.screenHeight = h;

        // 쓰레드 시작
        if(T == null){
            Log.d(TAG, "onSizeChanged: ");
            T = new GameThread();
            T.start();

        }

    }

    // 뷰가 원도우에서 분리될 때마다 발생.
    @Override
    protected void onDetachedFromWindow() {
        // 쓰레드의 run 값으로 false 값을 줌.
        T.run = false;


        super.onDetachedFromWindow();
    }

    @Override
    protected void onDraw(Canvas canvas) {

            canvas.drawColor(Color.BLACK);


            int circleWidth = screenWidth/2 +chX;
            int circleHeight = screenHeight/2 + chY;

            // 높이 벗어나지 않게 조정
            if(circleHeight  >=  screenHeight){
                circleHeight = screenHeight-180;
                chY=screenHeight-(screenHeight/2);
            }
            if (circleHeight <=0){
                circleHeight = 10;
                chY= -screenHeight/2;
            }
            // 넓이 벗어나지 않게 처리
            if(circleWidth >= screenWidth){
                circleWidth = screenWidth -150;
            }
            if(circleWidth <=0){
                circleWidth = 10;
            }

            Resources res = getResources();
            BitmapDrawable  bitmapDrawable = null;
            bitmapDrawable = (BitmapDrawable) res.getDrawable(R.drawable.moussi,null);
            Bitmap bitmap = bitmapDrawable.getBitmap();
            canvas.drawBitmap(bitmap , circleWidth , circleHeight , null);

            for (int i=0 ; i<1 ; i++){
//                Paint armyPaint = new Paint();
//                armyPaint.setColor(Color.LTGRAY);
//                canvas.drawRect(screenWidth/2+armyMoveX[i],screenHeight/2 +armyMoveY[i],screenWidth/2+(screenWidth-screenWidth%64) / 8 + armyMoveX[i],screenHeight/2+(screenHeight-screenHeight%32) / 4 +armyMoveY[i],armyPaint);
            }


    }



    // 게임 쓰레드
    class GameThread extends Thread{
        // run은 0또는 1의 값을 가질 수 있으며, true 값을 넣어줌. (true = 1, false=0)
        public boolean run = true;

        @Override
        public void run() {
            while (run){
                //뷰에서 이미지를 분리시킨다.
                postInvalidate();

                    int r = random.nextInt(4-1+1)+1;

                    if(r ==1 ){
                        chY -= screenHeight / 8;
                    }
                    if(r ==2 ){
                        chY += screenHeight / 8;
                    }
                    if(r ==3 ){
                        chX-=screenWidth /20;
                    }
                    if(r ==4 ){
                        chX+=screenWidth /20;
                    }



                try {
                    sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }



}

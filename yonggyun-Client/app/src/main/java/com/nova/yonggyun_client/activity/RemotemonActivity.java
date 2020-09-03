package com.nova.yonggyun_client.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import com.nova.yonggyun_client.R;
import com.remotemonster.sdk.Config;
import com.remotemonster.sdk.RemonCall;
import com.remotemonster.sdk.RemonCast;
import com.remotemonster.sdk.data.Room;

import org.webrtc.SurfaceViewRenderer;

import java.nio.channels.Channel;
import java.util.List;

public class RemotemonActivity extends AppCompatActivity {
    private static final String TAG = RemotemonActivity.class.getSimpleName();
    private RemonCast caster = null;
    private RemonCast viewer = null;
    String roomName = "방이름";
    RemonCall remonCall;
    String serverid = "서버아이디";
    String key = "key값";
    String connectChId;
    private String myChannelId;
    SurfaceViewRenderer surfRendererlocal,surfRendererremote;
    boolean iswhile = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remotemon);
        Intent intent = getIntent();

        surfRendererlocal = (SurfaceViewRenderer)findViewById(R.id.local_video_view);
        surfRendererremote = (SurfaceViewRenderer)findViewById(R.id.remote_video_view);

// 방송
        if (intent.getBooleanExtra("isCreate", false)){
            caster = RemonCast.builder()
                    .context(RemotemonActivity.this)
                    .localView(surfRendererlocal)        // 자신 Video Renderer
                    .serviceId("id값")    // RemoteMonster
                    .key("key")    // RemoteMonster
                    .build();

            caster.onCreate((channelId)->{
                myChannelId = channelId;
            });
            caster.create(roomName); // 방송의 방 id와 함께 방송을 송출
            
            // Callback
            casterCall();
            
        }else {

            connectChId = intent.getStringExtra("chid");
            Log.d(TAG, "connectChId : "+connectChId);
            viewer = RemonCast.builder()
                    .serviceId("id값")
                    .key("key")
                    .context(RemotemonActivity.this)
                    .remoteView(surfRendererremote)        // remote video renderer
                    .build();

            viewer.onJoin(() -> {
                Log.d(TAG, "onCreate: onJoin : 안에 작동");
            });
            
            viewer.join(connectChId);

        }

    }

    private void casterCall() {
        
        // UI 초기화 되었을때 처리해야 하는 부분 
        caster.onInit(() -> {
            Log.d(TAG, "casterCall: onInit :  ");
        });
        
        // 방송 생성시 호출
        caster.onCreate(id -> {
            Log.d(TAG, "casterCall: onCreate : "+id);
        });
        
        // 방송 참여시에 호출 
        caster.onJoin(() -> {
            Log.d(TAG, "casterCall:  onJoin : 방송 참여 ");
        });

        // 반송 시작
        caster.onComplete(() -> {
            Log.d(TAG, "casterCall:  onComplete: ");
        });

        // 종료
        caster.onClose(() -> {
            Log.d(TAG, "casterCall:  onClose");
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        caster.close();
        viewer.close();
    }
}

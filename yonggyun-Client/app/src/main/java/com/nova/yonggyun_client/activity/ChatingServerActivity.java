package com.nova.yonggyun_client.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.nova.yonggyun_client.R;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ChatingServerActivity extends AppCompatActivity {
    private static final String TAG = ChatingClientActivity.class.getSimpleName();

    TextView mIpText;
    TextView mPortText;
    TextView mText_msg; //클라이언트로부터 받을 메세지를 표시하는 TextView
    EditText mEdit_msg; //클라이언트로 전송할 메세지를 작성하는 EditText

    ServerSocket mServersocket;
    Socket mSocket;
    DataInputStream mDataInputStream;
    DataOutputStream mDataOutputStream;

    String mMsg ="";
    boolean mIsConnected = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chating_server);

        mIpText = findViewById(R.id.ip);
        mPortText = findViewById(R.id.port);
        mText_msg = findViewById(R.id.chatting);
        mEdit_msg = findViewById(R.id.msg);
        // 아이피 확인
        try {
            mIpText.setText(getLocalIpAddress());
        }catch (UnknownHostException e){
            e.printStackTrace();
        }
    }

    // 내 ip 가져오기
    private String getLocalIpAddress() throws UnknownHostException {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        assert wifiManager != null;
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipInt = wifiInfo.getIpAddress();
        return InetAddress.getByAddress(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(ipInt).array()).getHostAddress();
    }

    public void ServerSocketOpen(View view) throws IOException {
        final String port = mPortText.getText().toString();
        if(port.isEmpty()){
            Toast.makeText(this, "포트번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this, "Socket Open", Toast.LENGTH_SHORT).show();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        //서버소켓 생성.
                        mServersocket = new ServerSocket(Integer.parseInt(port));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        // 1. 소켓 얻어오기
                        mSocket = mServersocket.accept(); //클라이언트 접속까지 대기
                        // 2. 통로 구축
                        mDataInputStream = new DataInputStream(mSocket.getInputStream()); // 메세지를 받기 위한 통로
                        mDataOutputStream = new DataOutputStream(mSocket.getOutputStream()); //메 세지를 보내기 위한 통로

                        Log.d(TAG, "run: 서버와 클라이언트가 연결되었습니다.");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //클라이언트가 접속을 끊을 때까지 무한반복하면서 클라이언트의 메세지 수신
                    while (mIsConnected) {
                        try {
                            mMsg = mDataInputStream.readUTF();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        //클라이언트로부터 읽어들인 메시지msg를 TextView에 출력한다.
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mText_msg.setText("[RECV]" + mMsg);
                            }
                        });
                    }
                }//run method...
            }).start();
        }
    }

    //메세지전송
    public void SendMessage(View view) {
        if(mDataOutputStream ==null) return; //클라이언트와 연결되어 있지 않다면 전송불가

        final String msg= mEdit_msg.getText().toString();
        new Thread(new Runnable() {
            @Override
            public void run() {
                //클라이언트로 보낼 메세지 EditText로 부터 얻어오기

                try {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mText_msg.setText("[서버]" +msg);
                        }
                    });

                    mDataOutputStream.writeUTF(msg); //클라이언트로 메세지 보내기.UTF 방식으로 한글 전송 가능하게함
                    mDataOutputStream.flush();   //다음 메세지 전송을 위해 연결통로의 버퍼를 지워주는 메소드
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}

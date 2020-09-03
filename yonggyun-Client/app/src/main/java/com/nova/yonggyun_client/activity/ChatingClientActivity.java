package com.nova.yonggyun_client.activity;

import androidx.appcompat.app.AppCompatActivity;

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
import java.net.Socket;

public class ChatingClientActivity extends AppCompatActivity {
    private static final String TAG = ChatingClientActivity.class.getSimpleName();
    Socket mSocket;

    DataInputStream mDataInputStream;
    DataOutputStream mDataOutputStream;

    String mIp;
    String mPort;

    TextView mText_msg;  //받은 메세지를 보여주는 TextView
    EditText mEdit_msg;  //전송할 메세지를 작성하는 EditText
    EditText mEdit_ip;   //IP를 작성할 수 있는 EditText
    EditText mEdit_port;
    String mMsg ="";
    boolean mIsConnected =true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chating_client);

        mEdit_ip = findViewById(R.id.ip);
        mEdit_port = findViewById(R.id.port);
        mEdit_msg = findViewById(R.id.msg);
        mText_msg = findViewById(R.id.chatting);
    }

    /**
     *  1. 소켓 만들고
     *  2. 통로 만들고 (Stream)
     *  3. 서버에서 오는 메시지 감지(while)
     * @param view
     */
    //클라이언트 소켓 열고 서버 소켓에 접속
    public void ClientSocketOpen(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                try {
                    mIp = mEdit_ip.getText().toString();//IP 주소가 작성되어 있는 EditText에서 서버 IP 얻어오기
                    mPort = mEdit_port.getText().toString();
                    if(mIp.isEmpty() || mPort.isEmpty()){
                        ChatingClientActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(ChatingClientActivity.this, "ip주소와 포트번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else {
                        // 1. 소켓 만들고
                        mSocket = new Socket(InetAddress.getByName(mIp), Integer.parseInt(mPort));

                        // 2. 통로 만들고 (Stream)
                        mDataInputStream = new DataInputStream(mSocket.getInputStream());
                        mDataOutputStream = new DataOutputStream(mSocket.getOutputStream());

                        Log.d(TAG, "run: 클라이언트 구축완료");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //서버와 접속이 끊길 때까지 무한반복하면서 서버의 메세지 수신
                while(mIsConnected){
                    try {
                        mMsg = mDataInputStream.readUTF(); //서버 부터 메세지가 전송되면 이를 UTF형식으로 읽어서 String 으로 리턴
                        //runOnUiThread()는 별도의 Thread가 main Thread에게 UI 작업을 요청하는 메소드이다
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mText_msg.setText("[서버]" + mMsg);
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }//run method...
        }).start();

    }

    public void SendMessage(View view){
        // 서버와 연결되지 않은 상태 일때
        if(mDataOutputStream == null) return;

        new Thread(new Runnable() {
            @Override
            public void run() {
                // 서버로 보낼 메시지 가져오기
                String msg = mEdit_msg.getText().toString();
                try {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // 내쪽에 보여주는 부분
                            String msg = mEdit_msg.getText().toString();
                            mText_msg.setText("[클라] : "+msg);
                        }
                    });
                    mDataOutputStream.writeUTF(msg); //서버로 메시지 전송
                    mDataOutputStream.flush();   // 버퍼를 비워주는 메소드
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            mSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

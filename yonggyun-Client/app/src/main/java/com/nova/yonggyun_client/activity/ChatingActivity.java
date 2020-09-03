package com.nova.yonggyun_client.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nova.yonggyun_client.R;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;

public class ChatingActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int CONNECTION_OK = 10;
    public static final int CONNECTION_FAIL = 20;
    public static final int MESSAGE_READ = 100;
    public static final int MESSAGE_WRITE = 200;
    ArrayList<ChatMessage> mChatList;
    ChatAdapter mChatAdapter;
    ListView mListView;
    Button mSendBtn;
    EditText mMsgEdit;
    // 소켓 통신의 위한 쓰레드 제어
    boolean mFlagConnection = true;
    // 소켓 연결여부 확인
    boolean mIsConnected = false;
    boolean mFlagRead = true;

    Handler mWriteHandler;

    Socket mSocket;
    DataInputStream bin;
    DataOutputStream bout;


    // 연결을 위한 쓰레드
    SocketThread mSocketThread;
    // 데이터를 읽는 쓰레드
    ReadThread mReadThread;
    // 서버에 데이터를 보내기
    WriteThread mWriteThread;

    // 서버 URL
    String serverIp="서버IP";

    int serverPort=1234;

    String mRoomName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chating);

        mListView = findViewById(R.id.lab1_list);
        mSendBtn = findViewById(R.id.lab1_send_btn);
        mMsgEdit = findViewById(R.id.lab1_send_text);

        mSendBtn.setOnClickListener(this);

        Intent intent = getIntent();
        mRoomName = intent.getExtras().getString("idx");

        mChatList = new ArrayList<ChatMessage>();
        mChatAdapter = new ChatAdapter(this, R.layout.chat_item, mChatList);
        mListView.setAdapter(mChatAdapter);


    }

    /**
     *  메시지 ListView에 입력
     * @param who
     * @param msg
     */
    private void addMessage(String who, String msg) {
        ChatMessage vo = new ChatMessage();
        vo.who = who;
        vo.msg = msg;
        mChatList.add(vo);
        mChatAdapter.notifyDataSetChanged();
        mListView.setSelection(mChatList.size() - 1);
    }


    @Override
    public void onClick(View v) {
        if (!mMsgEdit.getText().toString().trim().equals("")) {
            Message msg=new Message();
            msg.obj= mMsgEdit.getText().toString();
            mWriteHandler.sendMessage(msg);
            mMsgEdit.setText("");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mSocketThread =new SocketThread();
        mSocketThread.start();
    }

    @Override
    protected void onStop() {
        super.onStop();

        mFlagConnection = false;
        mIsConnected = false;

        if (mSocket != null) {
            mFlagRead = false;
            mWriteHandler.getLooper().quit();
            try {
                bout.close();
                bin.close();
                mSocket.close();
            } catch (IOException e) {
            }
        }
    }

    private void showToast(String message){
        Toast toast=Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    Handler mainHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what== CONNECTION_OK){
                showToast("연결성공");
            }else if(msg.what== CONNECTION_FAIL){
                showToast("연결실패");
            }else if(msg.what== MESSAGE_READ){
                addMessage("you", (String)msg.obj);
            }else if(msg.what== MESSAGE_WRITE){
                addMessage("me", (String)msg.obj);
            }
        }
    };


    /**
     *  1. 연결
     *  2. 통로 생성
     *  3. 쓰레드 실행(Read , write)
     */
    class SocketThread extends Thread {
        public void run() {
            while (mFlagConnection){
                try{
                    if(!mIsConnected){
                        mSocket =new Socket();
                        SocketAddress remoteAddr=new InetSocketAddress(serverIp, serverPort);
                        mSocket.connect(remoteAddr, 10000);

                        bout=new DataOutputStream(mSocket.getOutputStream());
                        bin=new DataInputStream(mSocket.getInputStream());
                        // 초기에 데이터를 보내준다.
                        bout.writeUTF(mRoomName);
                        bout.flush();

                        if(mReadThread != null){
                            mFlagRead =false;
                        }

                        if(mWriteThread != null){
                            mWriteHandler.getLooper().quit();
                        }
                        // 데이터 전송 쓰레드
                        mWriteThread =new WriteThread();
                        mWriteThread.start();
                        // 데이터 읽는 쓰레드
                        mReadThread =new ReadThread();
                        mReadThread.start();

                        mIsConnected =true;

                        Message msg=new Message();
                        msg.what=CONNECTION_OK;
                        mainHandler.sendMessage(msg);
                    }else {
                        SystemClock.sleep(10000);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    SystemClock.sleep(10000);
                }
            }

        }
    }

    class WriteThread extends Thread {
        @Override
        public void run() {
            Looper.prepare();
            mWriteHandler =new Handler(){
                @Override
                public void handleMessage(Message msg) {
                    try{
                        bout.writeUTF((String)msg.obj);
                        bout.flush();
                        Message message=new Message();
                        message.what=MESSAGE_WRITE;
                        message.obj=msg.obj;
                        mainHandler.sendMessage(message);
                    }catch (Exception e){
                        e.printStackTrace();
                        mIsConnected =false;
                        mWriteHandler.getLooper().quit();
                        try{
                            mFlagRead =false;
                        }catch (Exception e1){}
                    }
                }
            };
            Looper.loop();
        }
    }

    class ReadThread extends Thread {
        @Override
        public void run() {
            while(mFlagRead){
                try {
                    String message = bin.readUTF();

                    if(message != null && !message.equals("")){
                            Message msg=new Message();
                            msg.what=MESSAGE_READ;
                            msg.obj=message;
                            mainHandler.sendMessage(msg);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Message msg=new Message();
            msg.what=CONNECTION_FAIL;
            mainHandler.sendMessage(msg);
        }

    }


}

class ChatMessage {
    String who;
    String msg;
}

class ChatAdapter extends ArrayAdapter<ChatMessage> {
    ArrayList<ChatMessage> list;
    int resId;
    Context context;

    public ChatAdapter(Context context, int resId, ArrayList<ChatMessage> list) {
        super(context, resId, list);
        this.context = context;
        this.resId = resId;
        this.list = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(resId, null);

        TextView msgView = (TextView) convertView.findViewById(R.id.lab1_item_msg);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) msgView.getLayoutParams();

        ChatMessage msg = list.get(position);
        if (msg.who.equals("me")) {
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
            msgView.setTextColor(Color.WHITE);
            msgView.setBackgroundResource(R.drawable.chat_right);
        } else if (msg.who.equals("you")) {
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
            msgView.setBackgroundResource(R.drawable.chat_left);
        }
        msgView.setText(msg.msg);
        return convertView;
    }
}


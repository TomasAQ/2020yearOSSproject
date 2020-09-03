package com.nova.yonggyun_client.chat;

import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class PClient {
    Socket clientsocket;
    String host;
    int port;
    DataOutputStream outputStream;
    DataInputStream inputStream;
    int DELETEROOMCODE = 1001;


    public PClient(String host , int port){
        this.host = host;
        this.port = port;
        try {
            // 1. 소켓 만들기
            clientsocket = throwSocket(host,port);
            // 2. 통로 만들기
            inputStream = connectInputStream();
            outputStream = connectOurputStream();
            // 3. 데이터 보내기
            sendMessageToServer("Hello Iam Client");
            while (true){
                // 4. 데이터 받기
                String msg = receiveMessageFromServer();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void showClientview(){
        // 출력을 담당하는 부분
    }

    /**
     *  소켓을 연결해주는 클래스
     * @param host : 주소
     * @param port : 포트
     * @throws IOException
     */
    public Socket throwSocket(String host , int port) throws IOException {
        // 소켓을 서버로 전송
        clientsocket = new Socket(host,port);
        return clientsocket;

    }

    /**
     *  서버쪽에서 넘어오는 데이터를 받는 통로
     *  데이터를 받는 통로
     */
    public DataInputStream connectInputStream() throws IOException {
        inputStream = new DataInputStream(clientsocket.getInputStream());
        return inputStream;
    }

    /**
     *  데이터를 보내는 통로
     */
    public DataOutputStream connectOurputStream() throws IOException {
        outputStream = new DataOutputStream(clientsocket.getOutputStream());
        return outputStream;
    }

    // 데이터를 보내는 메소드 만들기
    public void sendMessageToServer(String msg) throws IOException {
        outputStream.writeUTF(msg);
        // 서버로 보내는 메시지
    }

    // 받는 메소드를 만들기
    public String receiveMessageFromServer() throws IOException {
        String msg = inputStream.readUTF();
        // 서버에서 온 메시지
        return msg;
    }

    public static void main(String[] args){
        PClient client = new PClient("localhost",3000);

    }

    // 이벤트 발생시 데이터가 서버로 전송된다.
    public void eventHandler(){
        // 입력한 값을 서버로 전송
        String msg = "입력한 값을 서버로 전송해준다.";
        try {
            sendMessageToServer(msg);
            // 이벤트 발생확인
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 자신에 화면에도 보여주게 msg를 출력해준다.
        Log.d("서버쪽 ", "eventHandler: "+msg);
        // 이벤트 발생후 초기화
        msg = "";
    }

}

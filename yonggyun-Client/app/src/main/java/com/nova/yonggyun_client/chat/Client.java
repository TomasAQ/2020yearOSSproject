package com.nova.yonggyun_client.chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {

    private Socket mSocket;

    private BufferedReader mIn;
    private PrintWriter mOut;

    public Client(String ip , int port) {
        try {
            // 서버에 요청 보내기
            mSocket = new Socket(ip,port);
            System.out.println("클라 연결됨");

            // 통로 만들기
            mIn = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
            mOut = new PrintWriter(mSocket.getOutputStream());

            // 메시지 전달
            mOut.print("응답!!!!");
            mOut.flush();

            // 응답 출력
            System.out.println(mIn.readLine());

        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            // 소켓 닫기 ( 연결 끊기)
            try {
                mSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    public static void main(String[] args){
        new Client("ip",1234);
    }
}

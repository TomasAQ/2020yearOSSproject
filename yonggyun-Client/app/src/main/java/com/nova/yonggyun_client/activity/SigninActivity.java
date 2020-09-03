package com.nova.yonggyun_client.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.nova.yonggyun_client.R;
import com.nova.yonggyun_client.request.SignUPRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class SigninActivity extends AppCompatActivity {
    private static final String TAG = SigninActivity.class.getSimpleName();
    EditText mEditEmail ,mEditAuthenticationNum ,mEditPw ,mEditConfirmationPw ,mEditNickname ,mEditPhoneNum;

    String mCheckedEmail;   // 회원 가입 가능한 이메일
    String mAuthenticationNum;  // 이메일 인증 번호
    boolean mCheckAuthenticationNum = false;    // 인증번호


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        mEditEmail = findViewById(R.id.edit_email);
        mEditAuthenticationNum = findViewById(R.id.edit_authentication_num);
        mEditPw = findViewById(R.id.edit_pw);
        mEditConfirmationPw = findViewById(R.id.edit_confirmation_pw);
        mEditNickname = findViewById(R.id.edit_nickname);
        mEditPhoneNum = findViewById(R.id.edit_phone_num);

    }

    /**
     *  이메일 유효성 체크
     * @param view 이메일 인증 버튼
     *  1. 빈값일때
     *  2. 이메일 유효성체크
     *  3. 이메일 중복 검사  CheckEmail()
     *  4. 인증이메일 발송  CheckCertifiedNum()
     */
    public void certifiedEmail(View view) {
        String email = mEditEmail.getText().toString();

        if(!email.isEmpty()){
            //todo: 2. 이메일 유효성체크
            //3. 이메일 중복 검사 CheckEmail(email);
            //4. 인증이메일 발송 CheckCertifiedNum();
            checkEmail(email);
        }else{
            //1. 빈값일때
            Toast.makeText(this, "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show();
        }

    }


    /**
     *  이메일 중복 검사
     * @param email : editText에 입력된 이메일
     *  이메일 인증번호 확인
     */
    public void checkEmail(String email) {
        //서버로 보낼 데이터 : email


        //TODO :  이 부분이 Volly 사용된 부분인 것 같은데, 미사용자의 입장에서 봤을 때, 알 수가 없어서 보기 어렵네요.
        //TODO : import된 부분 보고 유추하긴 했는데, 주석으로 달아주면 좋을 것 같아요.

        Response.Listener<String> responsStringListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    String ckEmail = jsonResponse.getString("email");
                    if (success){
                        Toast.makeText(SigninActivity.this, "해당 이메일로 인증번호가 전송되었습니다.", Toast.LENGTH_SHORT).show();

                        // 4. 인증이메일 발송 및 인증번호 확인
                        checkCertifiedNum(ckEmail);
                    }else {
                        Toast.makeText(SigninActivity.this, "이미 사용중인 이메일 입니다.", Toast.LENGTH_SHORT).show();
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }
                Log.d(TAG, "onResponse: "+response);

            }
        };

        SignUPRequest signUPRequest = new SignUPRequest("1",email,responsStringListener);
        RequestQueue requestQueue = Volley.newRequestQueue(SigninActivity.this);
        requestQueue.add(signUPRequest);
    }

    /**
     * 인증이메일 발송
     *  1. 인증 번호 만들기
     *  2. 서버로 인증번호와 이메일 보내주기
     * @param ckEmail : 회원가입 가능한 이메일
     */
    private void checkCertifiedNum(String ckEmail) {
        // 회원가입 가능한 이메일
        mCheckedEmail = ckEmail;

        // 1. 인증 번호 만들기
        mAuthenticationNum ="";
        int[] intArray = new int[4];
        for (int i =0 ; i<4; i++){
            int randomNum = (int)(Math.random()*10);
            intArray[i] = randomNum;
        }
        mAuthenticationNum = Arrays.toString(intArray).replaceAll("[^0-9]","");

        // 2. 서버로 인증번호와 이메일 보내주기
        Response.Listener<String> responsStringListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse: "+response);
            }
        };
        SignUPRequest signUPRequest = new SignUPRequest("2",mCheckedEmail , mAuthenticationNum ,responsStringListener);
        RequestQueue requestQueue = Volley.newRequestQueue(SigninActivity.this);
        requestQueue.add(signUPRequest);
    }


    /**
     *  인증번호 확인
     *  1. 인증번호 입력창에 내용과 인증번호 가 일치하는지 확인
     *  2. 일치 한다면 해당 이메일로 회원가입 가능하도록 처리
     */
    public void ckeckAuthenticationNum(View view) {
        String  authenticationNum = mEditAuthenticationNum.getText().toString();

        if(mAuthenticationNum.equals(authenticationNum) ){
            Toast.makeText(this, "인증번호가 일치 합니다. 회원가입을 진행하세요.", Toast.LENGTH_SHORT).show();
            //2. 해당 이메일로 회원가입 가능하도록 처리
            mCheckAuthenticationNum = true;
            // 이메일 수정 불가
            mEditEmail.setClickable(false);
            mEditEmail.setFocusable(false);

        }else{
            Toast.makeText(this, "인증번호를 다시 확인하세요.", Toast.LENGTH_SHORT).show();
            mCheckAuthenticationNum = false;

        }

    }

    /**
     *  회원가입하기
     *  1. 인증여부 확인
     *  2. 가입정보 유효성 체크
     *  3. 비밀번호와 비밀번호 확인 일치 여부 확인
     *  4. 비밀번호와 전화번호 암호화
     *  5. 입력데이터 서버로 전송
     */
    public void signUp(View view) {
        String email= mEditEmail.getText().toString();
        String pw = mEditPw.getText().toString();
        String checkPw = mEditConfirmationPw.getText().toString();
        String nickname = mEditNickname.getText().toString();
        String phonenum = mEditPhoneNum.getText().toString();
        // 1. 인증여부 확인
        if (mCheckAuthenticationNum){
            //2. 가입정보 유효성 체크
            if(email.equals("")||pw.equals("")||checkPw.equals("")||nickname.equals("")||phonenum.equals("")){
                Toast.makeText(this, "가입 양식에 누락된 부분이 있습니다. 모두 입력해주세요.", Toast.LENGTH_SHORT).show();
            }else{
                // 3. 비밀번호와 비밀번호 확인 일치 여부 확인
                if (checkPw.equals(pw)){
                    // todo : 4. 비밀번호와 전화번호 암호화

                    // 5. 입력데이터 서버로 전송
                    Response.Listener<String> responsStringListener = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                boolean success = jsonObject.getBoolean("success");
                                if (success){
                                    Toast.makeText(SigninActivity.this, "회원가입이 완료 되었습니다.", Toast.LENGTH_SHORT).show();
                                    finish();
                                }else{
                                    Toast.makeText(SigninActivity.this, "회원가입 실패", Toast.LENGTH_SHORT).show();
                                }
                            }catch (JSONException e){
                                e.printStackTrace();
                            }
                            Log.d(TAG, "onResponse: "+response);
                        }
                    };

                    SignUPRequest signUPRequest = new SignUPRequest("3",email,pw,nickname,phonenum,responsStringListener);
                    RequestQueue requestQueue = Volley.newRequestQueue(SigninActivity.this);
                    requestQueue.add(signUPRequest);

                }else{
                    Toast.makeText(this, "비밀번호가 일치하지 않습니다. ", Toast.LENGTH_SHORT).show();
                }
            }
        }else {
            Toast.makeText(this, "이메일 인증을 먼저 완료해 주세요.", Toast.LENGTH_SHORT).show();
            mCheckAuthenticationNum = false;
        }
    }
}

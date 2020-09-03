package com.nova.yonggyun_client.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;
import com.nova.yonggyun_client.R;
import com.nova.yonggyun_client.request.LoginRequest;
import com.nova.yonggyun_client.util.SharedPreferenceData;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();
    TextInputLayout mTextInputLayoutEmail;
    EditText mEditEmail;
    TextInputLayout mTextInputLayoutPw;
    EditText mEditPw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mTextInputLayoutEmail = findViewById(R.id.text_input_layout_email);
        mEditEmail = mTextInputLayoutEmail.getEditText();
        mTextInputLayoutPw = findViewById(R.id.text_input_layout_pw);
        mEditPw = mTextInputLayoutPw.getEditText();

        mEditEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
//todo : 이메일 유효성 체크 필요
                if (!s.toString().contains("@")){
                    mEditEmail.setError("이메일 형식이 아닙니다. ");
                }else {
                    mEditEmail.setError(null);
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

        });

    }

    /**
     *  페이지 이동을 위한 버튼
     *  아이디 찾기 , 비밀번호 찾기 , 회원가입 페이지로 이동
     */
    public void MovePage(View view) {
        Intent intent = null;
        switch (view.getId()){
            case R.id.btn_find_id :
                //intent = new Intent(this , .class);
                break;
            case R.id.btn_find_pw :
                //intent = new Intent(this , .class);
                break;
            case R.id.btn_sign_up :
                intent = new Intent(this , SigninActivity.class);
                break;
            default:
                Log.d(TAG, "MovePage()함수 오류 ");
        }
        startActivity(intent);
        //finish();
    }

    public void login(View view) {
        final String email = mEditEmail.getText().toString();
        String pw = mEditPw.getText().toString();


        Response.Listener<String> responsStringListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");
                    if (success){
                        Toast.makeText(LoginActivity.this, "로그인 성공", Toast.LENGTH_SHORT).show();
                        // shareddata 날리기
                        SharedPreferenceData.removeAllAttribute(getBaseContext());
                        // userid , useremail 저장하기
                        int idx = email.indexOf("@");
                        String userid = email.substring(0,idx);

                        SharedPreferenceData.setAttribute(getBaseContext(),"userid",userid);
                        SharedPreferenceData.setAttribute(getBaseContext(),"useremail",email);

                        Intent intent = new Intent(getBaseContext(), MainActivity.class);
                        startActivity(intent);
                    }else{
                        Toast.makeText(LoginActivity.this, "로그인 실패", Toast.LENGTH_SHORT).show();
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }
                Log.d(TAG, "onResponse: "+response);
            }
        };


        LoginRequest loginRequest = new LoginRequest(email,pw,responsStringListener);
        RequestQueue requestQueue = Volley.newRequestQueue(LoginActivity.this);
        requestQueue.add(loginRequest);

    }
}

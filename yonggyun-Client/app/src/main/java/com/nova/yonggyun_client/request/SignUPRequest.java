package com.nova.yonggyun_client.request;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class SignUPRequest extends StringRequest {
    final static String URL="해당url";
    Map<String,String> map;
    // requsetCode 를 통해서 서버측에서 로직 분기
    // ex) 1번 이메일 중복 검사 , 2번 인증번호 확인 , 3번 회원가입

    // 이메일 중복 검사
    public SignUPRequest(String requsetCode , String email , Response.Listener<String> listener) {
        super(Method.POST , URL , listener , null) ;
        map= new HashMap<>();
        map.put("requsetCode",requsetCode);
        map.put("email",email);
    }

    // 인증 번호 전송
    public SignUPRequest(String requsetCode , String email , String mAuthenticationNum , Response.Listener<String> listener){
        super(Method.POST , URL , listener , null);
        map= new HashMap<>();
        map.put("requsetCode",requsetCode);
        map.put("email",email);
        map.put("authenticationNum",mAuthenticationNum);

    }

    // 회원가입
    public SignUPRequest(String requsetCode , String email , String pw , String nickname , String phonenum , Response.Listener<String> listener) {
        super(Method.POST , URL , listener , null) ;
        map= new HashMap<>();
        map.put("requsetCode",requsetCode);
        map.put("email",email);
        map.put("pw",pw);
        map.put("nickname",nickname);
        map.put("phonenum",phonenum);
    }



    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}

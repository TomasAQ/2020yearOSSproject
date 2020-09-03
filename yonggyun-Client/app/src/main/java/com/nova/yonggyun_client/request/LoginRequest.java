package com.nova.yonggyun_client.request;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class LoginRequest extends StringRequest {
    final static String URL="해당url";
    Map<String,String> map;


    // 로그인시에 email + pw
    public LoginRequest(String email ,String pw , Response.Listener<String> listener) {
        super(Method.POST , URL , listener , null) ;
        map= new HashMap<>();
        map.put("email",email);
        map.put("pw",pw);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return  map;
    }
}

package com.cp.retrofitx;


import com.cp.retrofitx.core.HttpResponse;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Url;

/**
 * created by cp on 2018/10/26.
 */
public interface ApiService {

//    @POST
    Call<HttpResponse<Boolean>> loginAccount(@Url String url, @Body RequestBody requestBody);// 登录


}

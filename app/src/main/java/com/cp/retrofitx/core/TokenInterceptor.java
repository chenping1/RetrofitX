package com.cp.retrofitx.core;


import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * created by cp on 2019/8/17.
 */
public class TokenInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        Response response = chain.proceed(originalRequest);
        String content = response.body().string();
        MediaType type = response.body().contentType();
        try {
            //根据RefreshToken同步请求，获取最新的Token
            String url = originalRequest.url().toString();
            boolean isNeedLogin = false;//MZServiceCall.isNeedLogin(content);
            if (isNeedLogin && !url.contains("direct/sys/user/token/get")) {//
                //token过期
                /*String tokenKey = YCServiceHelper.generateTokenKey(url);
                if (url.contains(tokenKey)) {
                    String newToken = getNewToken();
                    if(!TextUtils.isEmpty(newToken)){
                        url = url.substring(0, url.indexOf(tokenKey)) + tokenKey + newToken;

                        //使用新的Token，创建新的请求
                        Request newRequest = originalRequest
                                .newBuilder()
                                .url(url)
                                .build();
                        //重新请求
                        return chain.proceed(newRequest);
                    }
                }*/
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response.newBuilder()
                .body(ResponseBody.create(type, content))
                .build();
    }


   /* private synchronized String getNewToken() {
        return YCService.getInstance().build(IUserService.class).refreshToken(CustomActivityManager.getInstance().getTopActivity());
    }

    public String refreshToken(Activity activity) {
        try {
            String userNameStr = DensityUtil.getShare(activity, YCBaseConstants.SP_USER_NAME);
            String passwordStr = DensityUtil.getShare(activity, YCBaseConstants.SP_USER_PASSWORD);
            if (!TextUtils.isEmpty(passwordStr)) {
                passwordStr = EncryptUtils.decryptDES(passwordStr, "chenname");
            }
            if (TextUtils.isEmpty(userNameStr) || TextUtils.isEmpty(passwordStr)) {
                login(activity);
            } else {
                UserInfo user = LoginUtil_New.constructorLoginParams(activity.getApplicationContext(), userNameStr, passwordStr);
                ApiService apiService = HttpHelper.getInstance().getService(ApiService.class);
                Call<HttpResponse<UserTokenVO>> call = apiService.loginAccount(com.miaozhang.mobile.http.UrlMap.getUrl(UrlMap.SYS_USER_CREATETOKEN),
                        RequestBody.create(MediaType.parse("application/json"), new Gson().toJson(user)));
                HttpResponse<UserTokenVO> responseBean = call.execute().body();
                if (responseBean != null && responseBean.data != null) {
                    String accessToken = responseBean.data.getAccess_token();
                    if (!TextUtils.isEmpty(accessToken)) {
                        LoginUtil_New.saveLoginInfo(activity, new Gson(), responseBean.data);
                        return accessToken;
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }*/

}
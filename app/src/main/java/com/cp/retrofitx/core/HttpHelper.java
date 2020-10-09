package com.cp.retrofitx.core;




import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;


/**
 * 请求  主类
 * created by cp on 2018/10/26.
 */
public class HttpHelper {
    private Gson mGson;
    private OkHttpClient mOkHttpClient;
    private Retrofit retrofit;
    private HashMap<String, Object> serviceMap;

    private static volatile HttpHelper httpHelper;

    public static HttpHelper getInstance() {
        if (httpHelper == null) {
            synchronized (HttpHelper.class) {
                httpHelper = new HttpHelper();
            }
        }

        return httpHelper;
    }

    private HttpHelper() {
        serviceMap = new HashMap<>();
        mGson = new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .setDateFormat("yyyy-MM-dd hh:mm:ss")
                .create();
        mOkHttpClient = OkHttpProvider.getDefaultOkHttpClient();
        retrofit = new Retrofit.Builder()
                .baseUrl("http://www.ydcfo.com/")
                .client(mOkHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(LenientGsonConverterFactory.create(mGson))
                .build();
    }

    public <S> S getService(Class<S> serviceClass) {
        if (serviceMap.containsKey(serviceClass.getName())) {
            return (S) serviceMap.get(serviceClass.getName());
        } else {
            Object object = retrofit.create(serviceClass);
            serviceMap.put(serviceClass.getName(), (S) object);
            return (S) object;
        }
    }

}

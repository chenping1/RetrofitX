package com.cp.retrofitx.core;

import android.text.TextUtils;

import com.cp.retrofitx.utils.LogUtil;
import com.cp.retrofitx.utils.NetUtil;
import com.google.gson.Gson;
import com.cp.helloworldx.android.architect.MyApplication;

import java.nio.charset.Charset;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import okhttp3.MediaType;
import okio.Buffer;
import okio.BufferedSource;
import retrofit2.HttpException;

/**
 * 请求结果返回类
 * created by cp on 2018/10/26.
 */
public abstract class HttpResultObserver<T> implements Observer<HttpResponse<T>> {

    private static final String TAG = HttpResponseObserver.class.getSimpleName();
    protected Disposable disposable;

    @Override
    public void onSubscribe(Disposable d) {
        disposable = d;
    }

    @Override
    public void onNext(HttpResponse<T> httpResponse) {
        if (httpResponse.errorCode == 0) {
            onSuccess(httpResponse.data);
        } else {
            //todo
            onError(new Throwable(""), httpResponse.errorCode);
        }
    }

    @Override
    public void onComplete() {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    @Override
    public void onError(Throwable e) {
        if (!NetUtil.isNetworkConnected(MyApplication.getInstance())) {
            onError(new Throwable("网络错误"), 404);
        } else {

            if(e instanceof HttpException){
                try {
                    okhttp3.ResponseBody body = ((HttpException) e).response().errorBody();
                    BufferedSource source = body.source();
                    source.request(Long.MAX_VALUE); // Buffer the entire body.
                    Buffer buffer = source.buffer();
                    Charset charset = Charset.defaultCharset();
                    MediaType contentType = body.contentType();
                    if (contentType != null) {
                        charset = contentType.charset(charset);
                    }
                    String content = buffer.clone().readString(charset);
                    ResponseBody responseBody = new Gson().fromJson(content, ResponseBody.class);
                    String errorInfo = responseBody.getErrorMsg();
                    if (!TextUtils.isEmpty(responseBody.getErrorCtx())) {
                        errorInfo += responseBody.getErrorCtx();
                    }
                    onError(new Throwable(errorInfo), -1);
                }catch (Exception error){
                    LogUtil.d(TAG, error.toString());
                }
            }else {
                onError(e, -1);
            }

        }

        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    public abstract void onSuccess(T t);

    public abstract void onError(Throwable e, int code);
}

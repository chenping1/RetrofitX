package com.cp.retrofitx.core;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * 请求结果返回类
 * created by cp on 2018/10/26.
 */
public abstract class HttpResponseObserver<T> implements Observer<HttpResponse<T>> {

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
        ApiException apiException = ApiException.handleException(e);
        onError(new Throwable(apiException.getMessage()), apiException.getCode());

        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    public abstract void onSuccess(T t);

    public abstract void onError(Throwable e, int code);
}

package com.cp.retrofitx.core;

import java.io.Serializable;

/**
 * created by cp on 2018/10/26.
 */
public class HttpResponse<T extends Object> extends Object implements Serializable {

    public int errorCode;
    public String errorMsg;
    public T data;
}

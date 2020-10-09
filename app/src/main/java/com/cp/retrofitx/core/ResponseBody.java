package com.cp.retrofitx.core;

import android.text.TextUtils;

public class ResponseBody<T>  {

    private String errorCode;
    private String errorMsg;
    private String errorCtx;
    private T data; // 兼容新版本

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }


    public String getErrorCtx() {
        return errorCtx;
    }

    public void setErrorCtx(String errorCtx) {
        this.errorCtx = errorCtx;
    }

    public String getCompleteErrorMsg() {
        String errorText = errorMsg;
        if(errorText == null) {
            errorText = "";
        }
        if(TextUtils.isEmpty(errorCtx)) {
            return errorText;
        }
        return errorCtx + errorText;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}

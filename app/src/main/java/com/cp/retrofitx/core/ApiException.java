package com.cp.retrofitx.core;

import android.net.ParseException;
import android.text.TextUtils;

import com.cp.retrofitx.utils.LogUtil;
import com.cp.retrofitx.utils.NetUtil;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializer;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;

import java.io.NotSerializableException;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;

import okhttp3.MediaType;
import okio.Buffer;
import okio.BufferedSource;
import retrofit2.HttpException;


public class ApiException extends Exception {
    private static final String TAG = ApiException.class.getSimpleName();
    private final int code;
    private String message;

    private ApiException(Throwable throwable, int code) {
        super(throwable);
        this.code = code;
        this.message = throwable.getMessage();
    }

    public int getCode() {
        return code;
    }


    public static ApiException handleException(Throwable e) {
        ApiException ex;
        if (!NetUtil.isNetworkConnected(MyApplication.getInstance())) {
            ex = new ApiException(new Throwable("网络错误"),404);
            return ex;
        } else if (e instanceof HttpException) {
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

                ex = new ApiException(new Throwable(errorInfo), -1);
                return ex;
            } catch (Exception error) {
                LogUtil.d(TAG, error.toString());
            }
        } else if (e instanceof JsonParseException
                || e instanceof JSONException
                || e instanceof JsonSerializer
                || e instanceof NotSerializableException
                || e instanceof ParseException) {
            ex = new ApiException(e, Error.PARSE_ERROR);
            ex.message = "网络错误";
            return ex;
        } else if (e instanceof ClassCastException) {
            ex = new ApiException(e, Error.CAST_ERROR);
            ex.message = "网络错误";
            return ex;
        } else if (e instanceof ConnectException) {
            ex = new ApiException(e, Error.NETWORD_ERROR);
            ex.message = "网络错误";
            return ex;
        } else if (e instanceof javax.net.ssl.SSLHandshakeException) {
            ex = new ApiException(e, Error.SSL_ERROR);
            ex.message = "网络错误";
            return ex;
        } else if (e instanceof ConnectTimeoutException) {
            ex = new ApiException(e, Error.TIMEOUT_ERROR);
            ex.message = "网络错误";
            return ex;
        } else if (e instanceof java.net.SocketTimeoutException) {
            ex = new ApiException(e, Error.TIMEOUT_ERROR);
            ex.message = "网络错误";
            return ex;
        } else if (e instanceof UnknownHostException) {
            ex = new ApiException(e, Error.UNKNOWNHOST_ERROR);
            ex.message = "网络错误";
            return ex;
        } else if (e instanceof NullPointerException) {
            ex = new ApiException(e, Error.NULLPOINTER_EXCEPTION);
            ex.message = "网络错误";
            return ex;
        }

        ex = new ApiException(e, Error.UNKNOWN);
        ex.message = "网络错误";
        return ex;


    }

    @Override
    public String getMessage() {
        return message;
    }

    /**
     * 约定异常
     */
    public static class Error {
        /**
         * 未知错误
         */
        public static final int UNKNOWN = 1000;
        /**
         * 解析错误
         */
        public static final int PARSE_ERROR = UNKNOWN + 1;
        /**
         * "网络错误"
         */
        public static final int NETWORD_ERROR = PARSE_ERROR + 1;
        /**
         * 协议出错
         */
        public static final int HTTP_ERROR = NETWORD_ERROR + 1;

        /**
         * 证书出错
         */
        public static final int SSL_ERROR = HTTP_ERROR + 1;

        /**
         * 连接超时
         */
        public static final int TIMEOUT_ERROR = SSL_ERROR + 1;

        /**
         * 调用错误
         */
        public static final int INVOKE_ERROR = TIMEOUT_ERROR + 1;
        /**
         * 类转换错误
         */
        public static final int CAST_ERROR = INVOKE_ERROR + 1;
        /**
         * 请求取消
         */
        public static final int REQUEST_CANCEL = CAST_ERROR + 1;
        /**
         * 未知主机错误
         */
        public static final int UNKNOWNHOST_ERROR = REQUEST_CANCEL + 1;

        /**
         * 空指针错误
         */
        public static final int NULLPOINTER_EXCEPTION = UNKNOWNHOST_ERROR + 1;
    }
}

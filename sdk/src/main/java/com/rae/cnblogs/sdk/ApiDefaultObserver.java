package com.rae.cnblogs.sdk;

import android.text.TextUtils;

import io.reactivex.observers.DefaultObserver;
import retrofit2.HttpException;

/**
 * 接口回调
 * Created by ChenRui on 2017/5/5 0005 16:48.
 */
public abstract class ApiDefaultObserver<T> extends DefaultObserver<T> {

    @Override
    public void onComplete() {

    }

    @Override
    public void onNext(T t) {
        if (t == null) {
            onEmpty(t);
            return;
        }
        accept(t);
    }

    @Override
    public void onError(Throwable e) {
        e.printStackTrace();

        if (e instanceof CnblogsApiException) {
            if (((CnblogsApiException) e).getCode() == ApiErrorCode.LOGIN_EXPIRED) {
                clearLoginToken();
                onLoginExpired();
                return;
            }
            onError((CnblogsApiException) e);
            return;
        }
        if (e instanceof HttpException) {
            HttpException ex = (HttpException) e;
            if (ex.code() == 401) {
                // 登录失效
                clearLoginToken();
                onLoginExpired();
                return;
            }
        }
        String message = e.getMessage();
        if (message != null && message.contains("登录过期")) {
            clearLoginToken();
            onLoginExpired();
            return;
        }
        if (TextUtils.isEmpty(message)) {
            message = "接口信息异常";
        }
        onError(message);
    }

    protected void clearLoginToken() {
        UserProvider.getInstance().logout(); // 退出登录
    }

    public void onError(CnblogsApiException e) {
        onError(e.getMessage());
    }

    /**
     * 登录过期
     */
    protected void onLoginExpired() {
        onError("登录失效，请重新登录");
    }

    protected abstract void onError(String message);

    protected abstract void accept(T t);

    protected void onEmpty(T t) {
        onError("没有数据");
    }
}

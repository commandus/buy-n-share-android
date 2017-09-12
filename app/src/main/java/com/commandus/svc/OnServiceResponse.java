package com.commandus.svc;

public interface OnServiceResponse {
    void onSuccess(int code, Object response);
    int onError(int code, int errorcode, String errorDescription);
}

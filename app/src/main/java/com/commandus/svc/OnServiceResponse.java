package com.commandus.svc;

public interface OnServiceResponse {
    void onSuccess(Object response);
    int onError(int errorcode, String errorDescription);
}

package com.example.mennomorsink.sslpinningwebview;

/**
 * Created by mennomorsink on 25/07/15.
 */
public interface LoadedListener {
    void Loaded(String url);
    void PinningPreventedLoading(String host);
}

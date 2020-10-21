package com.secugenfp.reactlibrary;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReadableMap;

public class SecugenScannerModule extends ReactContextBaseJavaModule {

    private final ReactApplicationContext reactContext;

    public SecugenScannerModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    public String getName() {
        return "SecugenScanner";
    }

    @ReactMethod
    public void captureFP(int timeoutGetImage, int qualityGetImage, Callback callback) {
        final Activity activity = getCurrentActivity();
        FPSecugen fpSecugen = new FPSecugen();
        fpSecugen.timeoutGetImage = timeoutGetImage;
        fpSecugen.qualityGetImage = qualityGetImage;
        fpSecugen.initialize(activity, reactContext, callback, FPSecugen.Actions.CAPTURE);
    }

    @ReactMethod
    public void matchImages(String image1, String image2, Callback callback) {
        final Activity activity = getCurrentActivity();
        FPSecugen fpSecugen = new FPSecugen();
        fpSecugen.imageMatch[0] = image1;
        fpSecugen.imageMatch[1] = image2;
        fpSecugen.initialize(activity, reactContext, callback, FPSecugen.Actions.MATCH_IMAGES);
    }

    @ReactMethod
    public void setLed(boolean ledActive, Callback callback) {
        final Activity activity = getCurrentActivity();
        FPSecugen fpSecugen = new FPSecugen();
        fpSecugen.LedActive = ledActive;
        fpSecugen.initialize(activity, reactContext, callback, FPSecugen.Actions.SET_LED);
    }

    @ReactMethod
    public void setMessages(ReadableMap messages) {
        Messages.updateMessages(messages);
    }

}

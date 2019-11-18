package com.github.alinz.reactnativewebviewbridge;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Promise;

public class WebViewModule extends ReactContextBaseJavaModule {
    public WebViewModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return "WebViewModule";
    }

    @ReactMethod
    public void clear(Promise promise) {
        WebViewBridgeManager.webViews.clear();
        WebViewBridgeManager.currentWebViewId = 0;
        promise.resolve(null);
    }

    @ReactMethod
    public void nextWebView() {
        WebViewBridgeManager.currentWebViewId =
        (WebViewBridgeManager.currentWebViewId + 1) % WebViewBridgeManager.webViews.size();
    }
}

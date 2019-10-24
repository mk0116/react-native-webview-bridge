package com.github.alinz.reactnativewebviewbridge;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

public class WebViewModule extends ReactContextBaseJavaModule {
    public WebViewModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return "WebViewModule";
    }

    @ReactMethod
    public void clear() {
        WebViewBridgeManager.webViews.clear();
        WebViewBridgeManager.currentWebViewId = 0;
    }

    @ReactMethod
    public void nextWebView() {
        WebViewBridgeManager.webViews.clear();
        WebViewBridgeManager.currentWebViewId =
        (WebViewBridgeManager.currentWebViewId + 1) % WebViewBridgeManager.webViews.size();
    }
}

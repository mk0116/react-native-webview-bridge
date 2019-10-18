package com.github.alinz.reactnativewebviewbridge;

import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Toast;

import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.views.webview.ReactWebViewManager;
import com.facebook.react.uimanager.annotations.ReactProp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public class LectureWebViewBridgeManager extends ReactWebViewManager {
    private static final String REACT_CLASS = "RCTLectureWebViewBridge";
    public static ArrayList<WebView> lectureWebViews = new ArrayList<>();
    public static Map<WebView, Boolean> lectureWebViewInUse = new HashMap<>();

    public static final int COMMAND_SEND_TO_BRIDGE = 101;

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @Override
    public @Nullable
    Map<String, Integer> getCommandsMap() {
        Map<String, Integer> commandsMap = super.getCommandsMap();
        commandsMap.put("sendToBridge", COMMAND_SEND_TO_BRIDGE);
        return commandsMap;
    }

    @Override
    protected WebView createViewInstance(ThemedReactContext reactContext) {
        WebView root = null;
        for (int i = 0; i< lectureWebViews.size(); i++) {
            if (!lectureWebViewInUse.get(lectureWebViews.get(i))) {
                root = lectureWebViews.get(i);
                lectureWebViewInUse.put(root, true);
                break;
            }
        }
        if (root != null) {
            return root;
        }
        root = super.createViewInstance(reactContext);
        root.addJavascriptInterface(new JavascriptBridge(root), "WebViewBridge");
        lectureWebViews.add(root);
        lectureWebViewInUse.put(root, true);
        return root;
    }

    @Override
    public void receiveCommand(WebView root, int commandId, @Nullable ReadableArray args) {
        super.receiveCommand(root, commandId, args);

        switch (commandId) {
            case COMMAND_SEND_TO_BRIDGE:
                sendToBridge(root, args.getString(0));
                break;
            default:
                //do nothing!!!!
        }
    }

    private void sendToBridge(WebView root, String message) {
        String script = "WebViewBridge.onMessage('" + message + "');";
        LectureWebViewBridgeManager.evaluateJavascript(root, script);
    }

    static private void evaluateJavascript(WebView root, String javascript) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            root.evaluateJavascript(javascript, null);
        } else {
            root.loadUrl("javascript:" + javascript);
        }
    }

    @ReactProp(name = "allowFileAccessFromFileURLs")
    public void setAllowFileAccessFromFileURLs(WebView root, boolean allows) {
        root.getSettings().setAllowFileAccessFromFileURLs(allows);
    }

    @ReactProp(name = "allowUniversalAccessFromFileURLs")
    public void setAllowUniversalAccessFromFileURLs(WebView root, boolean allows) {
        root.getSettings().setAllowUniversalAccessFromFileURLs(allows);
    }

    @Override
    public void onDropViewInstance(WebView webView) {
        lectureWebViewInUse.put(webView, false);
        if (webView.getParent() != null ){
            ((ViewGroup)webView.getParent()).removeView(webView);
        }
    }
}

package com.github.alinz.reactnativewebviewbridge;

import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Toast;

import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.uimanager.ThemedReactContext;
import com.reactnativecommunity.webview.RNCWebViewManager;
import com.facebook.react.uimanager.annotations.ReactProp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public class WebViewBridgeManager extends RNCWebViewManager {
    private static final String REACT_CLASS = "RCTWebViewBridge";
    public static ArrayList<WebView> webViews = new ArrayList<>();
    public static Map<WebView, Boolean> webViewInUse = new HashMap<>();

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
        for (int i = 0; i< webViews.size(); i++) {
            if (!webViewInUse.get(webViews.get(i))) {
                root = webViews.get(i);
                webViewInUse.put(root, true);
                break;
            }
        }
        if (root != null) {
            return root;
        }
        root = super.createViewInstance(reactContext);
        root.addJavascriptInterface(new JavascriptBridge(root), "WebViewBridge");
        webViews.add(root);
        webViewInUse.put(root, true);
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
        WebViewBridgeManager.evaluateJavascript(root, script);
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
        webViewInUse.put(webView, false);
        if (webView.getParent() != null ){
            ((ViewGroup)webView.getParent()).removeView(webView);
        }
    }
}

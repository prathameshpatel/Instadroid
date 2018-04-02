package io.github.prathameshpatel.instadroid.customviews;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import io.github.prathameshpatel.instadroid.Constants;
import io.github.prathameshpatel.instadroid.interfaces.AuthenticationListener;
import io.github.prathameshpatel.instadroid.R;

public class AuthenticationDialog extends Dialog {

    private AuthenticationListener listener;

    private final String url = Constants.BASE_URL
            +"oauth/authorize/?client_id="
            +Constants.CLIENT_ID
            +"&redirect_uri="
            +Constants.REDIRECT_URL
            +"&response_type=token"
            +"&display=touch&scope=basic+likes";

    public AuthenticationDialog(@NonNull Context context, AuthenticationListener listener) {
        super(context);
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.auth_dialog);

        initializeWebView();
    }

    private void initializeWebView() {
        WebView webView = findViewById(R.id.webView);
        webView.loadUrl(url); //load the auth url in the webview

        webView.setWebViewClient(new WebViewClient() {
            String access_token;
            boolean authComplete;

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                // need to check here
                if(url.contains("#access_token=") && !authComplete) {
                    Uri uri = Uri.parse(url);
                    access_token = uri.getEncodedFragment();

                    // get whole token after '=' sign
                    access_token = access_token.substring(access_token.lastIndexOf('=')+1);
                    Log.e("access_token",access_token);
                    authComplete = true;
                    listener.onCodeReceived(access_token); //send access_token to MainActivity
                    dismiss();
                } else if(url.contains("?error")) {
                    Log.e("access_token","getting error fetching access_token");
                    Log.e("response",url);
                    dismiss();
                }
            }
        });
    }
}

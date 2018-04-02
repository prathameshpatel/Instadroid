package io.github.prathameshpatel.instadroid.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.Button;
import android.widget.Toast;

import io.github.prathameshpatel.instadroid.customviews.AuthenticationDialog;
import io.github.prathameshpatel.instadroid.R;
import io.github.prathameshpatel.instadroid.interfaces.AuthenticationListener;

public class MainActivity extends AppCompatActivity implements AuthenticationListener{

    private AuthenticationDialog auth_dialog;
    SharedPreferences prefs = null;
    Button button_get_access_token = null;
    String token = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button_get_access_token = findViewById(R.id.button_get_access_token);

        button_get_access_token.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth_dialog = new AuthenticationDialog(MainActivity.this, MainActivity.this);
                auth_dialog.setCancelable(true); //can be cancelled with back key
                auth_dialog.show(); //display dialog on screen
            }
        });
    }

    @Override
    public void onCodeReceived(String auth_token) {
        if(auth_token == null)  {
            auth_dialog.dismiss();
            return;
        }

        //Switch to the Feed Activity
        token = auth_token;
        Intent intent = new Intent(MainActivity.this, FeedActivity.class);
        intent.putExtra("access_token", token);
        startActivityForResult(intent,0);
    }

    //Logout handler on finishing FeedActivity by logout button
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent i) {
        if(requestCode == 0 && resultCode == RESULT_OK) {
            boolean logout = i.getBooleanExtra("logout",false);
            if(logout) {
                //delete all cookies and webview saved data
                CookieSyncManager.createInstance(this);
                CookieManager cookieManager = CookieManager.getInstance();
                cookieManager.removeAllCookie();
                Toast.makeText(this,"User Logout Success!",Toast.LENGTH_LONG).show();
            }
        }
    }
}

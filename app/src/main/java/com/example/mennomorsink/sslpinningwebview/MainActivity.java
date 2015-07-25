package com.example.mennomorsink.sslpinningwebview;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity {

    private WebView webView;
    public static Switch pinningSwitch;
    private Button btnA;
    private Button btnB;
    public static TextView textView;

    private String url1 = "https://www.infosupport.com";
    private String url2 = "https://security.stackexchange.com";

    public MainActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = (WebView)findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);

        pinningSwitch = (Switch)findViewById(R.id.pinningSwitch);
        btnA = (Button)findViewById(R.id.btn1);
        btnB = (Button)findViewById(R.id.btn2);
        textView = (TextView)findViewById(R.id.textView);


        SslPinningWebViewClient webViewClient = new SslPinningWebViewClient(new LoadedListener() {
            @Override
            public void Loaded(final String url) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textView.setText("Loaded " + url);
                    }
                });
            }

            @Override
            public void PinningPreventedLoading(final String host) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textView.setText("SSL Pinning prevented loading from " + host);
                    }
                });
            }
        });
        webView.setWebViewClient(webViewClient);

        btnA.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 webView.clearView();
                 textView.setText("");
                 webView.loadUrl(url1);
             }
         });

        btnB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webView.clearView();
                textView.setText("");
                webView.loadUrl(url2);
            }
        });
    }
}

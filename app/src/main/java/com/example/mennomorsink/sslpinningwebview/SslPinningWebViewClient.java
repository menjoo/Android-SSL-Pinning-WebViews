package com.example.mennomorsink.sslpinningwebview;

import android.annotation.TargetApi;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.TrustManagerFactory;

/**
 * Created by mennomorsink on 06/05/15.
 */
public class SslPinningWebViewClient extends WebViewClient {

    private LoadedListener listener;
    private SSLContext sslContext;

    public SslPinningWebViewClient(LoadedListener listener) {
        this.listener = listener;
        prepareSslPinning();
    }

    @Override
    public WebResourceResponse shouldInterceptRequest (final WebView view, String url) {
        if(MainActivity.pinningSwitch.isChecked()) {
            return processRequest(Uri.parse(url));
        } else {
            return null;
        }
    }

    @Override
    @TargetApi(21)
    public WebResourceResponse shouldInterceptRequest (final WebView view, WebResourceRequest interceptedRequest) {
        if(MainActivity.pinningSwitch.isChecked()) {
            return processRequest(interceptedRequest.getUrl());
        } else {
            return null;
        }
    }

    private WebResourceResponse processRequest(Uri uri) {
        Log.d("SSL_PINNING_WEBVIEWS", "GET: " + uri.toString());

        try {
            // Setup connection
            URL url = new URL(uri.toString());
            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();

            // Set SSL Socket Factory for this request
            urlConnection.setSSLSocketFactory(sslContext.getSocketFactory());

            // Get content, contentType and encoding
            InputStream is = urlConnection.getInputStream();
            String contentType = urlConnection.getContentType();
            String encoding = urlConnection.getContentEncoding();

            // If got a contentType header
            if(contentType != null) {

                String mimeType = contentType;

                // Parse mime type from contenttype string
                if (contentType.contains(";")) {
                    mimeType = contentType.split(";")[0].trim();
                }

                Log.d("SSL_PINNING_WEBVIEWS", "Mime: " + mimeType);

                listener.Loaded(uri.toString());

                // Return the response
                return new WebResourceResponse(mimeType, encoding, is);
            }

        } catch (SSLHandshakeException e) {
            if(isCause(CertPathValidatorException.class, e)) {
                listener.PinningPreventedLoading(uri.getHost());
            }
            Log.d("SSL_PINNING_WEBVIEWS", e.getLocalizedMessage());
        } catch (Exception e) {
            Log.d("SSL_PINNING_WEBVIEWS", e.getLocalizedMessage());
        }

        // Return empty response for this request
        return new WebResourceResponse(null, null, null);
    }

    private void prepareSslPinning() {
        // Create keystore
        KeyStore keyStore = initKeyStore();

        // Setup trustmanager factory
        String algorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = null;
        try {
            tmf = TrustManagerFactory.getInstance(algorithm);
            tmf.init(keyStore);

            // Set SSL context
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, tmf.getTrustManagers(), null);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
    }

    private static KeyStore initKeyStore() {

        try {
            // Create keystore
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            keyStore.load(null, new char[]{});
            CertificateFactory cf = CertificateFactory.getInstance("X.509");

            addTrustedCertificates(keyStore, cf);

            return keyStore;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(BuildConfig.DEBUG ? "Not able to load the certificates : NoSuchAlgorithmException" : "");
        } catch (CertificateException e) {
            throw new RuntimeException(BuildConfig.DEBUG ? "Not able to load the certificates : CertificateException" : "");
        } catch (IOException e) {
            throw new RuntimeException(BuildConfig.DEBUG ? "Not able to load the certificates : IOException" : "");
        } catch (KeyStoreException e1) {
            throw new RuntimeException(BuildConfig.DEBUG ? "Not able to instantiate the keystore:" + e1.getMessage() : "");
        }
    }

    private static void addTrustedCertificates(KeyStore keyStore, CertificateFactory cf) {
        try {
            // Add cert
            byte[] derIng = Base64.decode(TrustedServerCertificates.INFO_SUPPORT_COM, Base64.NO_WRAP);
            ByteArrayInputStream bais = new ByteArrayInputStream(derIng);
            X509Certificate cert = (X509Certificate) cf.generateCertificate(bais);
            keyStore.setCertificateEntry("infosupport.com", cert);

        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        }
    }

    public static boolean isCause(
            Class<? extends Throwable> expected,
            Throwable exc
    ) {
        return expected.isInstance(exc) || (
                exc != null && isCause(expected, exc.getCause())
        );
    }
}

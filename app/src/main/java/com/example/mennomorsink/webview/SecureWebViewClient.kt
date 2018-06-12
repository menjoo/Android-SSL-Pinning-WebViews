package com.example.mennomorsink.webview

import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.URL

class SecureWebViewClient(private val httpClient: OkHttpClient) : WebViewClient() {

    private var listener: OnResultListener? = null

    companion object {
        const val HEADER_CONTENT_TYPE = "Content-Type"
    }

    fun setOnLoaderListener(loaderListener: OnResultListener) {
        this.listener = loaderListener
    }

    override fun shouldInterceptRequest(view: WebView, interceptedRequest: WebResourceRequest): WebResourceResponse? {
        Log.d(javaClass.simpleName, interceptedRequest.url.toString())

        try {
            // Try to execute call with OkHttp
            val url = URL(interceptedRequest.url.toString())
            val response = httpClient.newCall(Request.Builder()
                    .url(url)
                    .build()).execute()

            val contentType = response.header(HEADER_CONTENT_TYPE)

            // If got a contentType header
            if (contentType != null) {

                val inputStream = response.body()?.byteStream()
                val mimeType = ContentTypeParser.getMimeType(contentType)
                val charset = ContentTypeParser.getCharset(contentType)

                listener?.onLoaded(url.toString())

                // Return the response
                return WebResourceResponse(mimeType, charset, inputStream)
            }
        } catch (ex: Exception) {
            Log.w(javaClass.simpleName, ex.message)
            listener?.onPreventedLoading(ex.message ?: ex.javaClass.simpleName)
        }

        // Very important to return empty WebResourceResponse like this. If you return just null,
        // the request will still be executed!
        return WebResourceResponse(null, null, null)
    }

    interface OnResultListener {
        fun onLoaded(url: String)
        fun onPreventedLoading(reason: String)
    }
}

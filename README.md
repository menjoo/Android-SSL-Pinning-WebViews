# Android-SSL-Pinning-WebViews
## A simple demo app that demonstrates:
* Certificate pinning in Android WebViews
* Scheme and domain whitelisting in Android WebViews

The pinning and whitelisting is achieved on network level with OkHttp.
WebView is then secured by extending `WebViewClient` and using OkHttp for the requests.

The solution has a performance drawback: By extending `WebViewClient` and overriding
`shouldInterceptRequest`, all requests are performed sequentially which hurts the loading times.
Please let me know if there is a solution for this.




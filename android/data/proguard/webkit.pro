##---------------Start: proguard configuration for Webkit  ----------
-keep public class android.net.http.SslError
-keep public class android.webkit.WebViewClient
-keep public interface android.webkit.WebViewFactoryProvider {*;}

-dontwarn android.webkit.WebView
-dontwarn android.net.http.SslError
-dontwarn android.webkit.WebViewClient
##---------------End: proguard configuration for Webkit  ----------
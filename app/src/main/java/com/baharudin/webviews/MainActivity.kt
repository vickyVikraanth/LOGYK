package com.baharudin.webviews

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.ViewGroup
import android.webkit.*
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.baharudin.webviews.ui.theme.WebviewsTheme

class MainActivity : ComponentActivity(),CustomWebChromeClient.CustomListener {

    private var filePathCallback: ValueCallback<Array<Uri>>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WebviewsTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    WebviewScreen(this)
                }
            }
        }
    }

    override fun launchGallery(filePathCallback: ValueCallback<Array<Uri>>?) {
        this.filePathCallback = filePathCallback

        val galleryIntent = Intent().apply {
            // Show only images, no videos or anything else
            type = "image/*"
            action = Intent.ACTION_PICK
        }

        // Always show the chooser (if there are multiple options available)
        startActivityForResult(
            Intent.createChooser(galleryIntent, "Select Picture"),
            PIC_CHOOSER_REQUEST,
            null    // No additional data
        )
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode != PIC_CHOOSER_REQUEST) {
            return super.onActivityResult(requestCode, resultCode, data)
        }

        when (resultCode) {
            Activity.RESULT_OK -> data?.data?.let {
                filePathCallback?.onReceiveValue(arrayOf(it))
                filePathCallback = null
            }
            Activity.RESULT_CANCELED -> {
                filePathCallback?.onReceiveValue(null)
                filePathCallback = null
            }
        }
    }

    companion object {
        private const val PIC_CHOOSER_REQUEST = 100
        private const val LOG_TAG = "MainActivity"
    }
}

@Composable
fun WebviewScreen(customListioner: CustomWebChromeClient.CustomListener) {
    var backEnable by remember { mutableStateOf(false) }
    var webView : WebView? = null

    AndroidView(
        modifier = Modifier,
        factory = {context ->
            WebView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                )
                webViewClient = object : WebViewClient(){
                    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                        backEnable = view!!.canGoBack()
                    }

                    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                        if (url.startsWith("https://t.me/")) {
                            // Open internal links within the WebView
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            view.context.startActivity(intent)
                            return true
                        }
                        return false
                    }

                }
                settings.allowUniversalAccessFromFileURLs = true
                settings.allowFileAccessFromFileURLs = true
                settings.allowContentAccess = true;
                settings.allowFileAccess = true;
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                webChromeClient = CustomWebChromeClient( customListioner)
                settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK

                loadUrl("https://logykinfotech.com/login.php")
                webView = this

            }
        }, update = {
            webView = it
        })

    BackHandler(enabled = backEnable) {
        webView?.goBack()
    }
}

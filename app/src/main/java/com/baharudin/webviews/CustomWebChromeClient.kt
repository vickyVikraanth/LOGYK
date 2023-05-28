package com.baharudin.webviews

import android.net.Uri
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView


class CustomWebChromeClient(
    private val listener: CustomListener
) : WebChromeClient() {

    override fun onShowFileChooser(
        webView: WebView?,
        filePathCallback: ValueCallback<Array<Uri>>?,
        fileChooserParams: FileChooserParams?
    ): Boolean {
        listener.launchGallery(filePathCallback)
        return true
    }



    interface CustomListener {
        fun launchGallery(filePathCallback: ValueCallback<Array<Uri>>?)
    }
}
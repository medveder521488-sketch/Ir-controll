package com.irremote

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.hardware.ConsumerIrManager
import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebView
import org.json.JSONArray

class MainActivity : Activity() {
    private var ir: ConsumerIrManager? = null

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ir = getSystemService(Context.CONSUMER_IR_SERVICE) as? ConsumerIrManager
        val wv = WebView(this)
        wv.settings.javaScriptEnabled = true
        wv.addJavascriptInterface(Bridge(), "Android")
        wv.loadUrl("file:///android_asset/index.html")
        setContentView(wv)
    }

    inner class Bridge {
        @JavascriptInterface
        fun hasIr(): Boolean = ir?.hasIrEmitter() == true

        @JavascriptInterface
        fun transmit(freq: Int, pattern: String): Boolean {
            return try {
                val arr = JSONArray(pattern)
                val p = IntArray(arr.length()) { arr.getInt(it) }
                ir?.transmit(freq, p)
                ir != null
            } catch (e: Exception) {
                false
            }
        }
    }
}

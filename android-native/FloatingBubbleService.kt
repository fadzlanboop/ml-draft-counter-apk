package com.oo.mldraftcounter

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView

/**
 * Bubble mengambang legal (bukan cheat/hack ke game) - mirip chat-head Messenger.
 * Menampilkan ringkasan rekomendasi counter terakhir yang dikirim dari web app
 * lewat OverlayBridgePlugin. Bisa digeser bebas, dan diketuk untuk expand/collapse.
 *
 * WAJIB: pengguna sudah memberi izin "Tampil di atas aplikasi lain"
 * (Settings.canDrawOverlays) sebelum service ini dijalankan.
 */
class FloatingBubbleService : Service() {

    private lateinit var windowManager: WindowManager
    private lateinit var bubbleView: View
    private lateinit var panelText: TextView
    private var expanded = false

    private lateinit var prefs: SharedPreferences
    private val prefsListener = SharedPreferences.OnSharedPreferenceChangeListener { sp, key ->
        if (key == OverlayBridgePlugin.KEY_TEXT) {
            updateText(sp.getString(OverlayBridgePlugin.KEY_TEXT, "Belum ada rekomendasi") ?: "")
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        prefs = getSharedPreferences(OverlayBridgePlugin.PREFS_NAME, Context.MODE_PRIVATE)
        prefs.registerOnSharedPreferenceChangeListener(prefsListener)

        buildBubbleView()
        addBubbleToWindow()
    }

    private fun buildBubbleView() {
        val container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(0xE6121826.toInt()) // panel gelap semi transparan
            setPadding(24, 16, 24, 16)
        }

        val title = TextView(this).apply {
            text = "⚔ ML Counter"
            setTextColor(0xFFE8B64C.toInt())
            textSize = 12f
            setPadding(0, 0, 0, 6)
        }

        panelText = TextView(this).apply {
            text = "Ketuk untuk buka rekomendasi"
            setTextColor(0xFFE9EDF5.toInt())
            textSize = 12f
            maxWidth = 480
        }

        container.addView(title)
        container.addView(panelText)
        bubbleView = container
    }

    private fun addBubbleToWindow() {
        val overlayType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        else
            WindowManager.LayoutParams.TYPE_PHONE

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            overlayType,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )
        params.gravity = Gravity.TOP or Gravity.START
        params.x = 0
        params.y = 200

        // Bubble bisa digeser bebas di layar
        var initialX = 0
        var initialY = 0
        var touchX = 0f
        var touchY = 0f
        var moved = false

        bubbleView.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = params.x
                    initialY = params.y
                    touchX = event.rawX
                    touchY = event.rawY
                    moved = false
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    val dx = (event.rawX - touchX).toInt()
                    val dy = (event.rawY - touchY).toInt()
                    if (kotlin.math.abs(dx) > 12 || kotlin.math.abs(dy) > 12) moved = true
                    params.x = initialX + dx
                    params.y = initialY + dy
                    windowManager.updateViewLayout(bubbleView, params)
                    true
                }
                MotionEvent.ACTION_UP -> {
                    if (!moved) toggleExpand()
                    true
                }
                else -> false
            }
        }

        windowManager.addView(bubbleView, params)
        updateText(prefs.getString(OverlayBridgePlugin.KEY_TEXT, "Belum ada rekomendasi") ?: "")
    }

    private fun toggleExpand() {
        expanded = !expanded
        panelText.visibility = if (expanded) View.VISIBLE else View.GONE
    }

    private fun updateText(text: String) {
        panelText.text = if (text.isBlank()) "Belum ada rekomendasi" else text
    }

    override fun onDestroy() {
        super.onDestroy()
        prefs.unregisterOnSharedPreferenceChangeListener(prefsListener)
        if (::bubbleView.isInitialized) {
            windowManager.removeView(bubbleView)
        }
    }
    }

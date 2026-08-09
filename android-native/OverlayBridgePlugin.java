package com.oo.mldraftcounter;

import android.content.Context;
import android.content.SharedPreferences;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

/**
 * Jembatan antara JavaScript (index.html) dan bubble overlay.
 * Setiap kali rekomendasi counter berubah di web app, JS memanggil
 * Capacitor.Plugins.OverlayBridge.write({ text: "..." })
 * Teks itu disimpan di SharedPreferences, lalu dibaca live oleh FloatingBubbleService.
 */
@CapacitorPlugin(name = "OverlayBridge")
public class OverlayBridgePlugin extends Plugin {

    public static final String PREFS_NAME = "overlay_bridge_prefs";
    public static final String KEY_TEXT = "last_suggestion_text";

    @PluginMethod
    public void write(PluginCall call) {
        String text = call.getString("text", "");
        SharedPreferences prefs = getContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_TEXT, text).apply();

        JSObject ret = new JSObject();
        ret.put("saved", true);
        call.resolve(ret);
    }

    @PluginMethod
    public void startBubble(PluginCall call) {
        // Dipanggil dari JS untuk menyalakan bubble (setelah izin overlay diberikan)
        android.content.Intent intent = new android.content.Intent(getContext(), FloatingBubbleService.class);
        getContext().startService(intent);
        call.resolve();
    }

    @PluginMethod
    public void stopBubble(PluginCall call) {
        android.content.Intent intent = new android.content.Intent(getContext(), FloatingBubbleService.class);
        getContext().stopService(intent);
        call.resolve();
    }
}

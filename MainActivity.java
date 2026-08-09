package com.oo.mldraftcounter;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import com.getcapacitor.BridgeActivity;

/**
 * Tempel isi file ini ke MainActivity.java bawaan Capacitor
 * (lokasi: android/app/src/main/java/com/oo/mldraftcounter/MainActivity.java)
 */
public class MainActivity extends BridgeActivity {

    private static final int REQ_OVERLAY_PERMISSION = 9001;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Daftarkan plugin custom sebelum super.onCreate load web view (Capacitor 6: cukup panggil registerPlugin)
        registerPlugin(OverlayBridgePlugin.class);

        requestOverlayPermissionIfNeeded();
    }

    private void requestOverlayPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName())
                );
                startActivityForResult(intent, REQ_OVERLAY_PERMISSION);
            }
        }
    }
}

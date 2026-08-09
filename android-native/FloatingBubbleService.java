package com.oo.mldraftcounter;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FloatingBubbleService extends Service {

    private WindowManager windowManager;
    private View bubbleView;
    private TextView panelText;
    private boolean expanded = false;
    private boolean bubbleAdded = false;

    private SharedPreferences prefs;
    private final SharedPreferences.OnSharedPreferenceChangeListener prefsListener =
        (sp, key) -> {
            if (OverlayBridgePlugin.KEY_TEXT.equals(key)) {
                String text = sp.getString(OverlayBridgePlugin.KEY_TEXT, "Belum ada rekomendasi");
                updateText(text == null ? "" : text);
            }
        };

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        prefs = getSharedPreferences(OverlayBridgePlugin.PREFS_NAME, Context.MODE_PRIVATE);
        prefs.registerOnSharedPreferenceChangeListener(prefsListener);

        buildBubbleView();
        addBubbleToWindow();
    }

    private void buildBubbleView() {
        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setBackgroundColor(0xE6121826);
        container.setPadding(24, 16, 24, 16);

        TextView title = new TextView(this);
        title.setText("\u2694 ML Counter");
        title.setTextColor(0xFFE8B64C);
        title.setTextSize(12f);
        title.setPadding(0, 0, 0, 6);

        panelText = new TextView(this);
        panelText.setText("Ketuk untuk buka rekomendasi");
        panelText.setTextColor(0xFFE9EDF5);
        panelText.setTextSize(12f);
        panelText.setMaxWidth(480);

        container.addView(title);
        container.addView(panelText);
        bubbleView = container;
    }

    private void addBubbleToWindow() {
        int overlayType = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            : WindowManager.LayoutParams.TYPE_PHONE;

        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            overlayType,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        );
        params.gravity = Gravity.TOP | Gravity.START;
        params.x = 0;
        params.y = 200;

        final int[] initialX = {0};
        final int[] initialY = {0};
        final float[] touchX = {0f};
        final float[] touchY = {0f};
        final boolean[] moved = {false};

        bubbleView.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    initialX[0] = params.x;
                    initialY[0] = params.y;
                    touchX[0] = event.getRawX();
                    touchY[0] = event.getRawY();
                    moved[0] = false;
                    return true;
                case MotionEvent.ACTION_MOVE:
                    int dx = (int) (event.getRawX() - touchX[0]);
                    int dy = (int) (event.getRawY() - touchY[0]);
                    if (Math.abs(dx) > 12 || Math.abs(dy) > 12) moved[0] = true;
                    params.x = initialX[0] + dx;
                    params.y = initialY[0] + dy;
                    windowManager.updateViewLayout(bubbleView, params);
                    return true;
                case MotionEvent.ACTION_UP:
                    if (!moved[0]) toggleExpand();
                    return true;
                default:
                    return false;
            }
        });

        windowManager.addView(bubbleView, params);
        bubbleAdded = true;
        String text = prefs.getString(OverlayBridgePlugin.KEY_TEXT, "Belum ada rekomendasi");
        updateText(text == null ? "" : text);
    }

    private void toggleExpand() {
        expanded = !expanded;
        panelText.setVisibility(expanded ? View.VISIBLE : View.GONE);
    }

    private void updateText(String text) {
        panelText.setText((text == null || text.trim().isEmpty()) ? "Belum ada rekomendasi" : text);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        prefs.unregisterOnSharedPreferenceChangeListener(prefsListener);
        if (bubbleAdded && bubbleView != null) {
            windowManager.removeView(bubbleView);
        }
    }
}

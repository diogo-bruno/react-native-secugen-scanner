package com.secugenfp.reactlibrary;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbManager;

public class UsbBroadcastReceiver extends BroadcastReceiver {

    public static final String USB_PERMISSION = "com.secugenfp.reactlibrary.plugin.USB_PERMISSION";

    private final Activity activity;
    CallbackPermission callbackPermission;

    public UsbBroadcastReceiver(Activity activity, final CallbackPermission callbackPermission) {
        this.activity = activity;
        this.callbackPermission = callbackPermission;
    }

    public interface CallbackPermission {
        void afterAction(boolean accepted);
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        boolean accepted = false;

        String action = intent.getAction();

        if (USB_PERMISSION.equals(action)) {

            if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                accepted = true;
            }

            activity.unregisterReceiver(this);

        }

        callbackPermission.afterAction(accepted);

    }

}

package com.secugenfp.reactlibrary;

import android.graphics.Bitmap;
import android.os.Handler;
import java.nio.ByteBuffer;

public class Utils {

    public interface DelayCallback {
        void afterDelay();
    }

    public static void delay(int milliseconds, final DelayCallback delayCallback) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                delayCallback.afterDelay();
            }
        }, milliseconds);
    }

    public static Bitmap toGrayscale(byte[] mImageBuffer, int mImageWidth, int mImageHeight) {
        byte[] Bits = new byte[mImageBuffer.length * 4];
        for (int i = 0; i < mImageBuffer.length; i++) {
            Bits[i * 4] = Bits[i * 4 + 1] = Bits[i * 4 + 2] = mImageBuffer[i]; // Invert the source bits
            Bits[i * 4 + 3] = -1;
        }
        Bitmap bmpGrayscale = Bitmap.createBitmap(mImageWidth, mImageHeight, Bitmap.Config.ARGB_8888);
        bmpGrayscale.copyPixelsFromBuffer(ByteBuffer.wrap(Bits));
        return bmpGrayscale;
    }
}
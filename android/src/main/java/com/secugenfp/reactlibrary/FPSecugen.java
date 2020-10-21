package com.secugenfp.reactlibrary;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Environment;

import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.facebook.react.bridge.Callback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.reflect.Field;

import SecuGen.FDxSDKPro.JSGFPLib;
import SecuGen.FDxSDKPro.SGFDxDeviceName;
import SecuGen.FDxSDKPro.SGFDxErrorCode;
import SecuGen.FDxSDKPro.SGFDxSecurityLevel;
import SecuGen.FDxSDKPro.SGFDxTemplateFormat;
import SecuGen.FDxSDKPro.SGFingerInfo;
import SecuGen.FDxSDKPro.SGFingerPosition;
import SecuGen.FDxSDKPro.SGImpressionType;
import SecuGen.FDxSDKPro.SGWSQLib;

public class FPSecugen {

    private String serialNumber;

    private int[] mMaxTemplateSize;
    private int mImageWidth;
    private int mImageHeight;

    private JSGFPLib sgfplib;
    private Context context;

    private Callback callbackContext;
    private Activity activity;
    private Actions action;

    private Toast toast;
    private ProgressDialog spinnerDialog;

    public enum Actions {
        CAPTURE, SET_LED, MATCH_IMAGES
    }

    public boolean LedActive = false;

    public String[] imageMatch = new String[2];

    public int timeoutGetImage = 5000;
    public int qualityGetImage = 50;

    public void initialize(Activity activity, Context reactContext, Callback callbackContext, Actions action) {

        if (activity != null) {

            this.activity = activity;
            this.context = activity.getApplicationContext();
            if (this.context == null) this.context = reactContext;
            this.callbackContext = callbackContext;
            this.mMaxTemplateSize = new int[1];
            this.action = action;

            activity.getIntent().addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            File templatePathFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + "fprints/");

            boolean pathCreate = templatePathFile.mkdirs();

            if (pathCreate) {
                debugMessage("Path created");
            } else {
                debugMessage("Path not created");
            }

            showDialogProgress(Messages.wait, Messages.initializing);

            this.requestPermission();

        } else {

            callbackInvoke(true, CallbackInvokeTypeResponse.TEXT, "getCurrentActivity() is null");

        }

    }


    private void requestPermission() {

        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {

                UsbManager manager = (UsbManager) activity.getSystemService(Context.USB_SERVICE);

                sgfplib = new JSGFPLib((UsbManager) context.getSystemService(Context.USB_SERVICE));

                long error = sgfplib.Init(SGFDxDeviceName.SG_DEV_AUTO);

                if (error != SGFDxErrorCode.SGFDX_ERROR_NONE) {

                    String message = Messages.initialization_failed;

                    if (error == SGFDxErrorCode.SGFDX_ERROR_DEVICE_NOT_FOUND) {
                        message = Messages.error_device_not_found;
                    }

                    debugMessage(message + "-" + error);

                    toastMessage(message);

                    callbackInvoke(true, CallbackInvokeTypeResponse.TEXT, message + " [" + error + "]");

                } else {

                    UsbDevice usbDevice = sgfplib.GetUsbDevice();

                    if (usbDevice == null) {

                        String message = Messages.error_device_not_found;

                        debugMessage(message);

                        callbackInvoke(true, CallbackInvokeTypeResponse.TEXT, message);

                    } else {

                        // create the intent that will be used to get the permission
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(activity, 0, new Intent(UsbBroadcastReceiver.USB_PERMISSION), 0);

                        IntentFilter filter = new IntentFilter();
                        filter.addAction(UsbBroadcastReceiver.USB_PERMISSION);

                        // this broadcast receiver will handle the permission results
                        UsbBroadcastReceiver usbReceiver = new UsbBroadcastReceiver(activity, new UsbBroadcastReceiver.CallbackPermission() {
                            @Override
                            public void afterAction(boolean accepted) {
                                if (accepted) {
                                    debugMessage(Messages.permission_device_accepted);
                                    initDeviceSettings();
                                } else {
                                    String msgError = Messages.permission_device_denied;
                                    debugMessage(msgError);
                                    toastMessage(msgError);
                                    callbackInvoke(true, FPSecugen.CallbackInvokeTypeResponse.TEXT, msgError);
                                }
                            }
                        });

                        activity.registerReceiver(usbReceiver, filter);

                        manager.requestPermission(usbDevice, pendingIntent);

                    }

                }

            }
        });

    }

    private void initDeviceSettings() {

        Handler handler = new Handler();
        handler.post(new Runnable() {

            @Override
            public void run() {

                long error;

                error = sgfplib.OpenDevice(0);

                debugMessage("OpenDevice() ret: " + error + "\n");

                boolean isOpened = error == SGFDxErrorCode.SGFDX_ERROR_NONE;

                if (isOpened) {

                    showDialogProgress(Messages.wait, Messages.initialization_success);

                    SecuGen.FDxSDKPro.SGDeviceInfoParam deviceInfo = new SecuGen.FDxSDKPro.SGDeviceInfoParam();
                    error = sgfplib.GetDeviceInfo(deviceInfo);

                    debugMessage("GetDeviceInfo() ret: " + error + "\n");

                    mImageWidth = deviceInfo.imageWidth;
                    mImageHeight = deviceInfo.imageHeight;
                    serialNumber = new String(deviceInfo.deviceSN());

                    try {

                        String templateFormat = "";

                        Field fieldName = SGFDxTemplateFormat.class.getField(templateFormat);

                        short templateValue = fieldName.getShort(null);

                        debugMessage("templateValue: " + templateValue);

                        sgfplib.SetTemplateFormat(templateValue);

                    } catch (NoSuchFieldException e) {
                        e.printStackTrace();
                        sgfplib.SetTemplateFormat(SecuGen.FDxSDKPro.SGFDxTemplateFormat.TEMPLATE_FORMAT_SG400);

                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                        sgfplib.SetTemplateFormat(SecuGen.FDxSDKPro.SGFDxTemplateFormat.TEMPLATE_FORMAT_SG400);

                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                        sgfplib.SetTemplateFormat(SecuGen.FDxSDKPro.SGFDxTemplateFormat.TEMPLATE_FORMAT_SG400);

                    }

                    sgfplib.GetMaxTemplateSize(mMaxTemplateSize);

                    debugMessage("mMaxTemplateSize: " + mMaxTemplateSize[0] + "\n");

                    sgfplib.writeData((byte) 5, (byte) 1);

                    if (action.equals(Actions.CAPTURE)) {
                        captureFingerPrint();
                    } else if (action.equals(Actions.SET_LED)) {
                        sgfplib.SetLedOn(LedActive);
                        callbackInvoke(false, CallbackInvokeTypeResponse.TEXT, "OK");
                    } else if (action.equals(Actions.MATCH_IMAGES)) {
                        if (imageMatch == null || imageMatch.length < 2) {
                            callbackInvoke(true, CallbackInvokeTypeResponse.TEXT, Messages.alert_two_images_match);
                        } else {
                            if (!imageMatch[0].startsWith("data:image/png;base64,") || !imageMatch[1].startsWith("data:image/png;base64,")) {
                                callbackInvoke(true, CallbackInvokeTypeResponse.TEXT, Messages.alert_two_images_match_no_base64);
                            } else {
                                matchImages();
                            }
                        }
                    }

                } else {

                    debugMessage("Error init device [" + error + "]");

                    callbackInvoke(true, CallbackInvokeTypeResponse.TEXT, Messages.init_device_error + " [" + error + "]");

                }

            }

        });

    }

    private void captureFingerPrint() {

        showDialogProgress(Messages.attention, Messages.alert_capture_digital + " " + ((timeoutGetImage / 1000)) + " " + Messages.seconds);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                try {

                    Boolean error = null;
                    CallbackInvokeTypeResponse typeResponse = null;
                    String response = null;

                    long dwTimeStart, dwTimeEnd, dwTimeElapsed;

                    byte[] buffer = new byte[mImageWidth * mImageHeight];

                    dwTimeStart = System.currentTimeMillis();

                    sgfplib.SetLedOn(true);

                    long result = sgfplib.GetImageEx(buffer, timeoutGetImage, qualityGetImage);

                    sgfplib.SetLedOn(false);

                    if (result == SGFDxErrorCode.SGFDX_ERROR_NONE) {

                        dwTimeEnd = System.currentTimeMillis();
                        dwTimeElapsed = dwTimeEnd - dwTimeStart;

                        debugMessage("getImageEx(" + timeoutGetImage + "," + qualityGetImage + ") ret:" + result + " [" + dwTimeElapsed + "ms]");

                        int[] quality = new int[1];

                        int encodePixelDepth = 8;
                        int encodePPI = 500;

                        int[] wsqImageOutSize = new int[1];
                        byte[] wsqImage;

                        int QUALITY_VALUE = 0;

                        result = sgfplib.GetImageQuality(mImageWidth, mImageHeight, buffer, quality);

                        if (result == SGFDxErrorCode.SGFDX_ERROR_NONE) {

                            SGFingerInfo fingerInfo = new SGFingerInfo();

                            fingerInfo.FingerNumber = SGFingerPosition.SG_FINGPOS_LI;
                            fingerInfo.ImageQuality = quality[0];
                            fingerInfo.ImpressionType = SGImpressionType.SG_IMPTYPE_LP;
                            fingerInfo.ViewNumber = 1;

                            debugMessage(fingerInfo.ImageQuality + "");

                            if (fingerInfo.ImageQuality >= QUALITY_VALUE) {

                                result = sgfplib.WSQGetEncodedImageSize(wsqImageOutSize,
                                        SGWSQLib.BITRATE_5_TO_1, buffer, mImageWidth,
                                        mImageHeight, encodePixelDepth, encodePPI);

                                if (result == SGFDxErrorCode.SGFDX_ERROR_NONE) {

                                    wsqImage = new byte[wsqImageOutSize[0]];

                                    result = sgfplib.WSQEncode(wsqImage,
                                            SGWSQLib.BITRATE_5_TO_1, buffer,
                                            mImageWidth, mImageHeight, encodePixelDepth,
                                            encodePPI);

                                    if (result == SGFDxErrorCode.SGFDX_ERROR_NONE) {

                                        Bitmap bitmap = Utils.toGrayscale(buffer, mImageWidth, mImageHeight);

                                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);

                                        byte[] byteArray = byteArrayOutputStream.toByteArray();

                                        String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
                                        String wsqEncoded = Base64.encodeToString(wsqImage, Base64.DEFAULT);

                                        JSONObject json = new JSONObject();

                                        try {
                                            json.put("image", encoded);
                                            json.put("wsqImage", wsqEncoded);
                                            json.put("errorCode", result);
                                            json.put("quality", fingerInfo.ImageQuality);
                                            json.put("serialNumber", serialNumber);
                                        } catch (JSONException ex) {
                                            debugMessage("JSON Exception -> " + ex.getMessage());
                                        }

                                        error = false;
                                        typeResponse = CallbackInvokeTypeResponse.JSON;
                                        response = json.toString();

                                    } else {

                                        error = true;
                                        typeResponse = CallbackInvokeTypeResponse.TEXT;
                                        response = Messages.capture_error_code + ": " + result;

                                    }

                                }

                            } else {

                                error = true;
                                typeResponse = CallbackInvokeTypeResponse.TEXT;
                                response = Messages.quality_fingerprint + " " + QUALITY_VALUE;

                            }

                        } else {

                            error = true;
                            typeResponse = CallbackInvokeTypeResponse.TEXT;
                            response = Messages.quality_error_code + ": " + result;

                        }

                    } else {

                        error = true;
                        typeResponse = CallbackInvokeTypeResponse.TEXT;
                        response = Messages.error_during_capture + " [" + result + "]";

                    }

                    if (error == null) error = true;
                    if (typeResponse == null) typeResponse = CallbackInvokeTypeResponse.TEXT;
                    if (response == null) response = Messages.capture_error_code + ": " + result;

                    callbackInvoke(error, typeResponse, response);

                } catch (Exception e) {

                    debugMessage("captureFingerPrint -> " + e.getMessage());

                    callbackInvoke(true, CallbackInvokeTypeResponse.TEXT, Messages.error_exception_capture + " " + e.getMessage());

                }

            }

        }, 500);

    }

    private void matchImages() {

        showDialogProgress(Messages.wait, Messages.alert_comparing_digital);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                try {

                    boolean error;
                    CallbackInvokeTypeResponse typeResponse;
                    String response;

                    long dwTimeStart, dwTimeEnd, dwTimeElapsed;

                    dwTimeStart = System.currentTimeMillis();

                    byte[] image1 = Base64.decode(imageMatch[0], Base64.DEFAULT);
                    byte[] image2 = Base64.decode(imageMatch[1], Base64.DEFAULT);

                    boolean[] matched = new boolean[1];

                    long result = sgfplib.MatchTemplate(image1, image2, SGFDxSecurityLevel.SL_NORMAL, matched);

                    if (result == SGFDxErrorCode.SGFDX_ERROR_NONE) {

                        dwTimeEnd = System.currentTimeMillis();
                        dwTimeElapsed = dwTimeEnd - dwTimeStart;

                        debugMessage("getImageEx(" + timeoutGetImage + "," + qualityGetImage + ") ret:" + result + " [" + dwTimeElapsed + "ms]");

                        JSONObject json = new JSONObject();

                        try {
                            json.put("match", matched[0]);
                        } catch (JSONException ex) {
                            debugMessage("JSON Exception -> " + ex.getMessage());
                        }

                        error = false;
                        typeResponse = CallbackInvokeTypeResponse.JSON;
                        response = json.toString();

                    } else {

                        error = true;
                        typeResponse = CallbackInvokeTypeResponse.TEXT;
                        response = Messages.error_during_comparing + " [" + result + "]";

                    }

                    callbackInvoke(error, typeResponse, response);

                } catch (Exception e) {

                    debugMessage("captureFingerPrint -> " + e.getMessage());

                    callbackInvoke(true, CallbackInvokeTypeResponse.TEXT, Messages.error_exception_capture + " " + e.getMessage());

                }

            }

        }, 500);

    }

    private void debugMessage(String message) {
        Log.d("React - Secugen FP", message);
    }

    private void toastMessage(final String message) {
        if (toast != null)
            toast.cancel();
        if (toast == null)
            toast = Toast.makeText(context, "", Toast.LENGTH_LONG);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                toast.setText(message);
                toast.show();
            }
        });
    }

    private enum CallbackInvokeTypeResponse {
        TEXT, JSON
    }

    private void callbackInvoke(boolean error, CallbackInvokeTypeResponse typeResponse, String response) {
        try {
            hideDialogProgress();
            JSONObject json = new JSONObject();
            json.put("error", error);
            json.put("typeResponse", typeResponse.toString());
            json.put("response", response);
            callbackContext.invoke(json.toString());
        } catch (JSONException ex) {
            debugMessage("JSON CallbackInvoke Exception -> " + ex.getMessage());
        }
    }

    private void showDialogProgress(final String title, final String msg) {

        if (spinnerDialog == null)
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    int apiVersion = android.os.Build.VERSION.SDK_INT;
                    if (apiVersion >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                        spinnerDialog = new ProgressDialog(activity, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
                    } else {
                        spinnerDialog = new ProgressDialog(activity);
                    }
                    spinnerDialog.setCancelable(false);
                    spinnerDialog.setCanceledOnTouchOutside(false);
                    spinnerDialog.setIndeterminate(true);
                }
            });

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                spinnerDialog.setTitle(title);
                spinnerDialog.setMessage(msg);
                spinnerDialog.show();
            }
        });

    }

    private void hideDialogProgress() {
        if (spinnerDialog != null) {
            spinnerDialog.dismiss();
        }
    }

}
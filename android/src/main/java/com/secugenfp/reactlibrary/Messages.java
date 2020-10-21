package com.secugenfp.reactlibrary;

import com.facebook.react.bridge.ReadableMap;

public class Messages {

    public static String initializing = "Initializing Secugen Reader";

    public static String wait = "Please Wait";

    public static String attention = "Attention";

    public static String initialization_failed = "Fingerprint device initialization failed.";

    public static String initialization_success = "Device opened successfully";

    public static String error_device_not_found = "Either a fingerprint device is not attached or the attached fingerprint device is not supported.";

    public static String permission_device_accepted = "Permission to connect to the device was accepted!";

    public static String permission_device_denied = "Permission to connect to the device was denied!";

    public static String alert_two_images_match = "Two images are required for the match";

    public static String alert_two_images_match_no_base64 = "Two images in Base64 are required for the match";

    public static String init_device_error = "Error init device";

    public static String alert_capture_digital = "Capture in the coming";

    public static String alert_comparing_digital = "Comparing digital";

    public static String seconds = "Seconds";

    public static String quality_fingerprint = "Quality of the fingerprint is less than";

    public static String quality_error_code = "Quality Error Code";

    public static String error_during_capture = "Error when capturing digital ";

    public static String error_during_comparing = "Error when capturing digital ";

    public static String capture_error_code = "Capture Error Code ";

    public static String error_exception_capture = "Exception Capture - ";

    public static String error_exception_comparing = "Exception Comparing - ";

    public static void updateMessages(ReadableMap messages) {
        if (messages != null) {
            if (messages.hasKey("initializing")) {
                initializing = messages.getString("initializing");
            }
            if (messages.hasKey("wait")) {
                wait = messages.getString("wait");
            }
            if (messages.hasKey("attention")) {
                attention = messages.getString("attention");
            }
            if (messages.hasKey("initialization_failed")) {
                initialization_failed = messages.getString("initialization_failed");
            }
            if (messages.hasKey("initialization_success")) {
                initialization_success = messages.getString("initialization_success");
            }
            if (messages.hasKey("error_device_not_found")) {
                error_device_not_found = messages.getString("error_device_not_found");
            }
            if (messages.hasKey("permission_device_accepted")) {
                permission_device_accepted = messages.getString("permission_device_accepted");
            }
            if (messages.hasKey("permission_device_denied")) {
                permission_device_denied = messages.getString("permission_device_denied");
            }
            if (messages.hasKey("alert_two_images_match")) {
                alert_two_images_match = messages.getString("alert_two_images_match");
            }
            if (messages.hasKey("alert_two_images_match_no_base64")) {
                alert_two_images_match_no_base64 = messages.getString("alert_two_images_match_no_base64");
            }
            if (messages.hasKey("init_device_error")) {
                init_device_error = messages.getString("init_device_error");
            }
            if (messages.hasKey("alert_capture_digital")) {
                alert_capture_digital = messages.getString("alert_capture_digital");
            }
            if (messages.hasKey("alert_comparing_digital")) {
                alert_comparing_digital = messages.getString("alert_comparing_digital");
            }
            if (messages.hasKey("seconds")) {
                seconds = messages.getString("seconds");
            }
            if (messages.hasKey("quality_fingerprint")) {
                quality_fingerprint = messages.getString("quality_fingerprint");
            }
            if (messages.hasKey("quality_error_code")) {
                quality_error_code = messages.getString("quality_error_code");
            }
            if (messages.hasKey("error_during_capture")) {
                error_during_capture = messages.getString("error_during_capture");
            }
            if (messages.hasKey("error_during_comparing")) {
                error_during_comparing = messages.getString("error_during_comparing");
            }
            if (messages.hasKey("capture_error_code")) {
                capture_error_code = messages.getString("capture_error_code");
            }
            if (messages.hasKey("error_exception_capture")) {
                error_exception_capture = messages.getString("error_exception_capture");
            }
            if (messages.hasKey("error_exception_comparing")) {
                error_exception_comparing = messages.getString("error_exception_comparing");
            }
        }
    }

}

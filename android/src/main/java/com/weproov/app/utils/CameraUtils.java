package com.weproov.app.utils;

import android.content.Context;
import android.content.pm.PackageManager;

public final class CameraUtils {

    /** Check if this device has a camera */
    public static boolean checkCameraHardware(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

}

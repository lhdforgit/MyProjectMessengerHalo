package com.hahalolo.call.webrtc;

import android.hardware.Camera;
import android.os.SystemClock;
import android.text.TextUtils;

import org.webrtc.Camera1Capturer;
import org.webrtc.CameraEnumerationAndroid;
import org.webrtc.CameraEnumerator;
import org.webrtc.CameraVideoCapturer;
import org.webrtc.Logging;
import org.webrtc.Size;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class QBCameraEnumerator implements CameraEnumerator
{
    private static final String TAG;
    private static List<List<CameraEnumerationAndroid.CaptureFormat>> cachedSupportedFormats;
    
    public String[] getDeviceNames() {
        ArrayList<String> namesList = new ArrayList<String>();
        for (int i = 0; i < Camera.getNumberOfCameras(); ++i) {
            final String name = getDeviceName(i);
            if (name != null) {
                namesList.add(name);
                Logging.d(QBCameraEnumerator.TAG, "Index: " + i + ". " + name);
            }
            else {
                Logging.e(QBCameraEnumerator.TAG, "Index: " + i + ". Failed to query camera name.");
            }
        }
        namesList = this.collectCameras(namesList);
        final String[] namesArray = new String[namesList.size()];
        return namesList.toArray(namesArray);
    }
    
    private ArrayList<String> collectCameras(final List<String> namesList) {
        final ArrayList<String> cameraNamesList = new ArrayList<String>();
        String frontCameraName = "";
        String backCameraName = "";
        for (final String cameraName : namesList) {
            if (this.isBackFacing(cameraName) && this.nextPreferPrevious(backCameraName, cameraName)) {
                backCameraName = cameraName;
            }
            else {
                if (!this.isFrontFacing(cameraName) || !this.nextPreferPrevious(frontCameraName, cameraName)) {
                    continue;
                }
                frontCameraName = cameraName;
            }
        }
        if (!TextUtils.isEmpty((CharSequence)frontCameraName)) {
            cameraNamesList.add(frontCameraName);
        }
        if (!TextUtils.isEmpty((CharSequence)backCameraName)) {
            cameraNamesList.add(backCameraName);
        }
        return cameraNamesList;
    }
    
    private boolean nextPreferPrevious(final String previousCameraName, final String nextCameraName) {
        boolean prefer = false;
        if (TextUtils.isEmpty((CharSequence)previousCameraName)) {
            prefer = true;
        }
        else if (TextUtils.isEmpty((CharSequence)nextCameraName)) {
            prefer = false;
        }
        else if (this.getSupportedFormats(nextCameraName).size() > this.getSupportedFormats(previousCameraName).size()) {
            prefer = true;
        }
        return prefer;
    }
    
    public boolean isFrontFacing(final String deviceName) {
        final Camera.CameraInfo info = getCameraInfo(getCameraIndex(deviceName));
        return info != null && info.facing == 1;
    }
    
    public boolean isBackFacing(final String deviceName) {
        final Camera.CameraInfo info = getCameraInfo(getCameraIndex(deviceName));
        return info != null && info.facing == 0;
    }
    
    public List<CameraEnumerationAndroid.CaptureFormat> getSupportedFormats(final String deviceName) {
        return getSupportedFormats(getCameraIndex(deviceName));
    }
    
    public CameraVideoCapturer createCapturer(final String deviceName, final CameraVideoCapturer.CameraEventsHandler eventsHandler) {
        final Camera1Capturer camera1Capturer = new Camera1Capturer(deviceName, eventsHandler, false);
        try {
            final Field[] fields = camera1Capturer.getClass().getSuperclass().getDeclaredFields();
            final Field enumeratorField = fields[1];
            enumeratorField.setAccessible(true);
            enumeratorField.set(camera1Capturer, this);
        }
        catch (IllegalAccessException | NullPointerException ex2) {
            Logging.e(QBCameraEnumerator.TAG, "getField \"enumerator\" failed " + ex2);
            ex2.printStackTrace();
        }
        return (CameraVideoCapturer)camera1Capturer;
    }
    
    private static Camera.CameraInfo getCameraInfo(final int index) {
        final Camera.CameraInfo info = new Camera.CameraInfo();
        try {
            Camera.getCameraInfo(index, info);
            return info;
        }
        catch (Exception var3) {
            Logging.e(QBCameraEnumerator.TAG, "getCameraInfo failed on index " + index, (Throwable)var3);
            return null;
        }
    }
    
    private static synchronized List<CameraEnumerationAndroid.CaptureFormat> getSupportedFormats(final int cameraId) {
        if (QBCameraEnumerator.cachedSupportedFormats == null) {
            QBCameraEnumerator.cachedSupportedFormats = new ArrayList<List<CameraEnumerationAndroid.CaptureFormat>>();
            for (int i = 0; i < Camera.getNumberOfCameras(); ++i) {
                QBCameraEnumerator.cachedSupportedFormats.add(enumerateFormats(i));
            }
        }
        return QBCameraEnumerator.cachedSupportedFormats.get(cameraId);
    }
    
    private static List<CameraEnumerationAndroid.CaptureFormat> enumerateFormats(final int cameraId) {
        Logging.d(QBCameraEnumerator.TAG, "Get supported formats for camera index " + cameraId + ".");
        final long startTimeMs = SystemClock.elapsedRealtime();
        Camera camera = null;
        Camera.Parameters parameters = null;
        Label_0148: {
            List<CameraEnumerationAndroid.CaptureFormat> formatList;
            try {
                Logging.d(QBCameraEnumerator.TAG, "Opening camera with index " + cameraId);
                camera = Camera.open(cameraId);
                parameters = camera.getParameters();
                break Label_0148;
            }
            catch (RuntimeException e) {
                Logging.e(QBCameraEnumerator.TAG, "Open camera failed on camera index " + cameraId, (Throwable)e);
                formatList = new ArrayList<CameraEnumerationAndroid.CaptureFormat>();
            }
            finally {
                if (camera != null) {
                    camera.release();
                }
            }
            return formatList;
        }
        List<CameraEnumerationAndroid.CaptureFormat> formatList = new ArrayList<CameraEnumerationAndroid.CaptureFormat>();
        try {
            int minFps = 0;
            int maxFps = 0;
            final List<int[]> listFpsRange = (List<int[]>)parameters.getSupportedPreviewFpsRange();
            if (listFpsRange != null) {
                final int[] range = listFpsRange.get(listFpsRange.size() - 1);
                minFps = range[0];
                maxFps = range[1];
            }
            for (final Camera.Size size : parameters.getSupportedPreviewSizes()) {
                formatList.add(new CameraEnumerationAndroid.CaptureFormat(size.width, size.height, minFps, maxFps));
            }
        }
        catch (Exception var14) {
            Logging.e(QBCameraEnumerator.TAG, "getSupportedFormats() failed on camera index " + cameraId, (Throwable)var14);
        }
        final long endTimeMs = SystemClock.elapsedRealtime();
        Logging.d(QBCameraEnumerator.TAG, "Get supported formats for camera index " + cameraId + " done. Time spent: " + (endTimeMs - startTimeMs) + " ms.");
        return formatList;
    }
    
    static List<Size> convertSizes(final List<Camera.Size> cameraSizes) {
        final List<Size> sizes = new ArrayList<Size>();
        for (final Camera.Size size : cameraSizes) {
            sizes.add(new Size(size.width, size.height));
        }
        return sizes;
    }
    
    static List<CameraEnumerationAndroid.CaptureFormat.FramerateRange> convertFramerates(final List<int[]> arrayRanges) {
        final List<CameraEnumerationAndroid.CaptureFormat.FramerateRange> ranges = new ArrayList<CameraEnumerationAndroid.CaptureFormat.FramerateRange>();
        for (final int[] range : arrayRanges) {
            ranges.add(new CameraEnumerationAndroid.CaptureFormat.FramerateRange(range[0], range[1]));
        }
        return ranges;
    }
    
    private static int getCameraIndex(final String deviceName) {
        Logging.d(QBCameraEnumerator.TAG, "getCameraIndex: " + deviceName);
        for (int i = 0; i < Camera.getNumberOfCameras(); ++i) {
            if (deviceName.equals(getDeviceName(i))) {
                return i;
            }
        }
        throw new IllegalArgumentException("No such camera: " + deviceName);
    }
    
    private static String getDeviceName(final int index) {
        final Camera.CameraInfo info = getCameraInfo(index);
        if (info == null) {
            return null;
        }
        final String facing = (info.facing == 1) ? "front" : "back";
        return "Camera " + index + ", Facing " + facing + ", Orientation " + info.orientation;
    }
    
    static {
        TAG = QBCameraEnumerator.class.getSimpleName();
    }
}

package com.hahalolo.call.webrtc.view;

import android.graphics.Bitmap;

class GraphicsConverter
{
    public static final float PI = 3.1415927f;
    public static final float DEG_TO_RAD = 0.017453292f;
    public static final float RAD_TO_DEG = 57.295776f;
    
    public static byte[] RGBtoYUV(final byte r, final byte g, final byte b) {
        final byte y = (byte)(r * 0.299 + g * 0.587 + b * 0.114);
        final byte u = (byte)(r * -0.168736 + g * -0.331264 + b * 0.5 + 128.0);
        final byte v = (byte)(r * 0.5 + g * -0.418688 + b * -0.081312 + 128.0);
        return new byte[] { y, u, v };
    }
    
    public static float[] rotateVertices(final float[] vertices, final int rotation, final float rotationCenterX, final float rotationCenterY) {
        if (rotation == 0) {
            return vertices;
        }
        final float rotationRad = degToRad((float)rotation);
        final float sinRotationRad = (float)Math.sin(rotationRad);
        final float cosRotationInRad = (float)Math.cos(rotationRad);
        for (int i = vertices.length - 2; i >= 0; i -= 2) {
            final float pX = vertices[i];
            final float pY = vertices[i + 1];
            vertices[i] = rotationCenterX + (cosRotationInRad * (pX - rotationCenterX) - sinRotationRad * (pY - rotationCenterY));
            vertices[i + 1] = rotationCenterY + (sinRotationRad * (pX - rotationCenterX) + cosRotationInRad * (pY - rotationCenterY));
        }
        return vertices;
    }
    
    public static float degToRad(final float pDegree) {
        return 0.017453292f * pDegree;
    }
    
    public static byte[] BitmapToYUVPlane(final Bitmap bitmap, final int inputWidth, final int inputHeight) {
        final int[] argb = new int[inputWidth * inputHeight];
        bitmap.getPixels(argb, 0, inputWidth, 0, 0, inputWidth, inputHeight);
        final byte[] yuv = new byte[inputWidth * inputHeight * 3 / 2];
        encodeYUV420SP(yuv, argb, inputWidth, inputHeight);
        bitmap.recycle();
        return yuv;
    }
    
    private static void encodeYUV420SP(final byte[] yuv420sp, final int[] argb, final int width, final int height) {
        final int frameSize = width * height;
        int yIndex = 0;
        int uvIndex = frameSize;
        int index = 0;
        for (int j = 0; j < height; ++j) {
            for (int i = 0; i < width; ++i) {
                final int a = (argb[index] & 0xFF000000) >> 24;
                final int R = (argb[index] & 0xFF0000) >> 16;
                final int G = (argb[index] & 0xFF00) >> 8;
                final int B = (argb[index] & 0xFF) >> 0;
                final int Y = (66 * R + 129 * G + 25 * B + 128 >> 8) + 16;
                final int U = (-38 * R - 74 * G + 112 * B + 128 >> 8) + 128;
                final int V = (112 * R - 94 * G - 18 * B + 128 >> 8) + 128;
                yuv420sp[yIndex++] = (byte)((Y < 0) ? 0 : ((Y > 255) ? 255 : Y));
                if (j % 2 == 0 && index % 2 == 0) {
                    yuv420sp[uvIndex++] = (byte)((V < 0) ? 0 : ((V > 255) ? 255 : V));
                    yuv420sp[uvIndex++] = (byte)((U < 0) ? 0 : ((U > 255) ? 255 : U));
                }
                ++index;
            }
        }
    }
    
    private static byte[] encodeYUV420SP(final int[] argb, final int width, final int height) {
        final int frameSize = width * height;
        final byte[] yuv420sp = new byte[3];
        int yIndex = 0;
        int uvIndex = frameSize;
        int index = 0;
        for (int j = 0; j < height; ++j) {
            for (int i = 0; i < width; ++i) {
                final int a = (argb[index] & 0xFF000000) >> 24;
                final int R = (argb[index] & 0xFF0000) >> 16;
                final int G = (argb[index] & 0xFF00) >> 8;
                final int B = (argb[index] & 0xFF) >> 0;
                final int Y = (66 * R + 129 * G + 25 * B + 128 >> 8) + 16;
                final int U = (-38 * R - 74 * G + 112 * B + 128 >> 8) + 128;
                final int V = (112 * R - 94 * G - 18 * B + 128 >> 8) + 128;
                yuv420sp[yIndex++] = (byte)((Y < 0) ? 0 : ((Y > 255) ? 255 : Y));
                if (j % 2 == 0 && index % 2 == 0) {
                    yuv420sp[uvIndex++] = (byte)((V < 0) ? 0 : ((V > 255) ? 255 : V));
                    yuv420sp[uvIndex++] = (byte)((U < 0) ? 0 : ((U > 255) ? 255 : U));
                }
                ++index;
            }
        }
        return null;
    }
}

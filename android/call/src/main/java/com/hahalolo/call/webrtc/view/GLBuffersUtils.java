package com.hahalolo.call.webrtc.view;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Arrays;

class GLBuffersUtils
{
    public static FloatBuffer makeDirectNativeFloatBuffer(final float[] array) {
        final FloatBuffer buffer = ByteBuffer.allocateDirect(array.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        buffer.put(array);
        buffer.flip();
        return buffer;
    }
    
    public static ByteBuffer makeNativeByteBufferWithValue(final int width, final int height, final byte value) {
        final byte[] array = new byte[width * height];
        Arrays.fill(array, value);
        final ByteBuffer buffer = ByteBuffer.allocateDirect(width * height).order(ByteOrder.nativeOrder());
        buffer.put(array);
        buffer.flip();
        return buffer;
    }
    
    public static ByteBuffer makeNativeByteBufferFromArray(final byte[] array, final int width, final int height) {
        final ByteBuffer buffer = ByteBuffer.allocateDirect(width * height).order(ByteOrder.nativeOrder());
        buffer.put(array);
        buffer.flip();
        return buffer;
    }
    
    public static ByteBuffer[] makePlanesBufferWithValues(final int width, final int height, final byte[] yuv) {
        final ByteBuffer[] buffer = new ByteBuffer[3];
        for (int i = 0; i < 3; ++i) {
            final int w = (i == 0) ? width : (height / 2);
            buffer[i] = makeNativeByteBufferWithValue(w, height, yuv[i]);
        }
        return buffer;
    }
}

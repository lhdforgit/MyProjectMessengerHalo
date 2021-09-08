package com.halo.editor.scribbles;

import android.graphics.Bitmap;
import android.graphics.RectF;

import java.util.List;

interface FaceDetector {
  List<RectF> detect(Bitmap bitmap);
}

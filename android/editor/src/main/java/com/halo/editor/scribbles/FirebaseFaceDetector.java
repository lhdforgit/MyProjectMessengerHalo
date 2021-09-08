package com.halo.editor.scribbles;

import android.graphics.Bitmap;
import android.graphics.RectF;

import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetectorOptions;
import com.halo.stream.Stream;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

class FirebaseFaceDetector implements FaceDetector {

  @Override
  public List<RectF> detect(Bitmap source) {
    long startTime = System.currentTimeMillis();

    FaceDetectorOptions options =
            new FaceDetectorOptions.Builder()
                    .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                    .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_NONE)
                    .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_NONE)
                    .build();

    InputImage image = InputImage.fromBitmap(source, 0);
    List<RectF> output = new ArrayList<>();

    try (com.google.mlkit.vision.face.FaceDetector detector = FaceDetection.getClient(options)) {
      detector.process(image)
              .addOnSuccessListener(
                      faces -> output.addAll(Stream.of(faces)
                              .map(Face::getBoundingBox)
                              .map(r -> new RectF(r.left, r.top, r.right, r.bottom))
                              .toList()))
              .addOnFailureListener(Throwable::printStackTrace);
    } catch (Exception e) {
      Timber.w("Failed to close!");
    }
    Timber.d("Finished in " + (System.currentTimeMillis() - startTime) + " ms");
    return output;
  }
}

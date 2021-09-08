package com.halo.widget.shapeimageview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

import com.halo.widget.R;

/**
 * Create by ndn
 * Create on 6/2/21
 * com.halo.widget.shapeimageview
 */
public class ImageScaleView extends AppCompatImageView {

    private MatrixCropType mMatrixType = MatrixCropType.TOP_CENTER; // default

    private enum MatrixCropType {
        TOP_CENTER(0),
        BOTTOM_CENTER(1);

        private int mValue;

        private MatrixCropType(int value) {
            mValue = value;
        }

        public static MatrixCropType fromValue(int value) {
            for (MatrixCropType matrixCropType : values()) {
                if (matrixCropType.mValue == value) {
                    return matrixCropType;
                }
            }

            // default
            return MatrixCropType.TOP_CENTER;
        }
    }

    public ImageScaleView(Context context) {
        this(context, null);
    }

    public ImageScaleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImageScaleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        // get attributes
        if (attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ImageScaleView, 0, 0);
            try {
                mMatrixType = MatrixCropType.fromValue(a.getInteger(R.styleable.ImageScaleView_matrixType, 0));
            }
            finally {
                a.recycle();
            }
        }
    }

    @Override
    protected boolean setFrame(int frameLeft, int frameTop, int frameRight, int frameBottom) {

        Drawable drawable = getDrawable();
        if (drawable != null) {

            float frameWidth = frameRight - frameLeft;
            float frameHeight = frameBottom - frameTop;

            float originalImageWidth = (float) getDrawable().getIntrinsicWidth();
            float originalImageHeight = (float) getDrawable().getIntrinsicHeight();

            float usedScaleFactor = 1;

            if ((frameWidth > originalImageWidth) || (frameHeight > originalImageHeight)) {
                // If frame is bigger than image
                // => Crop it, keep aspect ratio and position it at the bottom
                // and
                // center horizontally
                float fitHorizontallyScaleFactor = frameWidth / originalImageWidth;
                float fitVerticallyScaleFactor = frameHeight / originalImageHeight;

                usedScaleFactor = Math.max(fitHorizontallyScaleFactor, fitVerticallyScaleFactor);
            }

            float newImageWidth = originalImageWidth * usedScaleFactor;
            float newImageHeight = originalImageHeight * usedScaleFactor;

            Matrix matrix = getImageMatrix();
            matrix.setScale(usedScaleFactor, usedScaleFactor, 0, 0);

            switch (mMatrixType) {
                case TOP_CENTER:
                    matrix.postTranslate((frameWidth - newImageWidth) / 2, 0);
                    break;
                case BOTTOM_CENTER:
                    matrix.postTranslate((frameWidth - newImageWidth) / 2, frameHeight - newImageHeight);
                    break;

                default:
                    break;
            }

            setImageMatrix(matrix);
        }
        return super.setFrame(frameLeft, frameTop, frameRight, frameBottom);
    }

}

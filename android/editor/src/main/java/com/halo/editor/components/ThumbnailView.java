/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.editor.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;

import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CenterInside;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.BaseRequestOptions;
import com.bumptech.glide.request.RequestOptions;
import com.halo.common.utils.ListenableFuture;
import com.halo.common.utils.SettableFuture;
import com.halo.common.utils.ThumbImageUtils;
import com.halo.editor.R;
import com.halo.editor.glide.DecryptableStreamUriLoader;
import com.halo.editor.slide.Slide;
import com.halo.editor.slide.SlideClickListener;
import com.halo.editor.slide.SlideControlClickListener;
import com.halo.editor.util.MediaUtil;
import com.halo.editor.util.Util;

import timber.log.Timber;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class ThumbnailView extends FrameLayout implements ThumbnailViewControlListener {

    private static final String TAG = ThumbnailView.class.getSimpleName();

    private ImageView image;
    private View playOverlay;
    private ThumbnailViewControl control;
    private OnClickListener parentClickListener;
    private SlideClickListener thumbnailClickListener = null;
    private SlideControlClickListener thumbnailControlClickListener = null;
    private Slide slide = null;
    private BitmapTransformation fit = new CenterCrop();

    private int radius;

    public ThumbnailView(Context context) {
        this(context, null);
    }

    public ThumbnailView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ThumbnailView(final Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        inflate(context, R.layout.editor_thumbnail_view, this);

        this.image = findViewById(R.id.thumbnail_image);
        this.playOverlay = findViewById(R.id.play_overlay);
        this.control = findViewById(R.id.thumbnail_control);
        this.control.setListener(this);
        super.setOnClickListener(new ThumbnailClickDispatcher());

        if (attrs != null) {
            TypedArray typedArray = context.getTheme().obtainStyledAttributes(
                    attrs, R.styleable.ThumbnailView, 0, 0);
            radius = typedArray.getDimensionPixelSize(
                    R.styleable.ThumbnailView_thumbnail_radius, getResources().getDimensionPixelSize(R.dimen.thumbnail_default_radius));
            boolean isFit = typedArray.getInt(
                    R.styleable.ThumbnailView_thumbnail_fit, 0) == 1;
            fit = isFit ? new CenterInside() : new CenterCrop();
            if (isFit) {
                setImageFitCenter();
            }
            boolean showControl = typedArray.getBoolean(
                    R.styleable.ThumbnailView_thumbnail_control, false);
            this.control.setVisibility(showControl ? VISIBLE : GONE);
            typedArray.recycle();
        } else {
            radius = getResources().getDimensionPixelSize(R.dimen.message_corner_collapse_radius);
        }
    }

    private void setImageFitCenter() {
        LayoutParams layoutParams = (LayoutParams) this.image.getLayoutParams();
        layoutParams.height = LayoutParams.WRAP_CONTENT;
        this.image.setLayoutParams(layoutParams);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        float playOverlayScale = 1;
        int playOverlayWidth = playOverlay.getLayoutParams().width;

        if (playOverlayWidth * 2 > getWidth()) {
            playOverlayScale /= 2;
        }

        playOverlay.setScaleX(playOverlayScale);
        playOverlay.setScaleY(playOverlayScale);
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        parentClickListener = l;
    }

    @UiThread
    public ListenableFuture<Boolean> setImageResource(@NonNull RequestManager glideRequests,
                                                      @NonNull Slide slide) {
        if (slide.getThumbnailUri() != null && slide.hasPlayOverlay()) {
            this.playOverlay.setVisibility(View.VISIBLE);
        } else {
            this.playOverlay.setVisibility(View.GONE);
        }

        if (Util.equals(slide, this.slide)) {
            Timber.i("Not re-loading slide %s", slide.asAttachment().getDataUri());
            return new SettableFuture<>(false);
        }

        if (this.slide != null && this.slide.getFastPreflightId() != null &&
                (!slide.hasVideo() || Util.equals(this.slide.getThumbnailUri(), slide.getThumbnailUri())) &&
                Util.equals(this.slide.getFastPreflightId(), slide.getFastPreflightId())) {
            Timber.i("Not re-loading slide for fast preflight: %s", slide.getFastPreflightId());
            this.slide = slide;
            return new SettableFuture<>(false);
        }

        Timber.i("loading part with id " + slide.asAttachment().getDataUri()
                + ", progress " + slide.getTransferState() + ", fast preflight id: " +
                slide.asAttachment().getFastPreflightId() + " has video: " + slide.hasVideo());

        this.slide = slide;
        this.control.setEditable(!slide.hasVideo() && !URLUtil.isNetworkUrl(slide.getThumbnailUri().toString()));
        invalidate();

        SettableFuture<Boolean> result = new SettableFuture<>();
        boolean resultHandled = false;

        if (slide.getThumbnailUri() != null) {
            if (!MediaUtil.isJpegType(slide.getContentType()) && !MediaUtil.isVideoType(slide.getContentType())) {
                SettableFuture<Boolean> thumbnailFuture = new SettableFuture<>();
                thumbnailFuture.deferTo(result);
            }

            buildThumbnailGlideRequest(glideRequests, slide).into(new GlideDrawableListeningTarget(image, result));
            resultHandled = true;
        } else {
            glideRequests.clear(image);
            image.setImageDrawable(null);
        }

        if (!resultHandled) {
            result.set(false);
        }

        return result;
    }

    public ListenableFuture<Boolean> setImageResource(@NonNull RequestManager glideRequests, @NonNull Uri uri) {
        return setImageResource(glideRequests, uri, 0, 0);
    }

    public ListenableFuture<Boolean> setImageResource(@NonNull RequestManager glideRequests, @NonNull Uri uri, int width, int height) {
        SettableFuture<Boolean> future = new SettableFuture<>();

        RequestBuilder request = buildRequestBuildWithUrl(glideRequests, uri);

        if (width > 0 && height > 0) {
            request = (RequestBuilder) request.override(width, height);
        }

        if (radius > 0) {
            request = (RequestBuilder) request.transforms(new CenterCrop(), new RoundedCorners(radius));
        } else {
            request = (RequestBuilder) request.transforms(new CenterCrop());
        }

        request.into(new GlideDrawableListeningTarget(image, future));

        return future;
    }

    public void setThumbnailClickListener(SlideClickListener listener) {
        this.thumbnailClickListener = listener;
    }

    public void setThumbnailControlClickListener(SlideControlClickListener thumbnailControlClickListener) {
        this.thumbnailControlClickListener = thumbnailControlClickListener;
    }

    public void clear(RequestManager glideRequests) {
        glideRequests.clear(image);
        slide = null;
    }

    protected void setRadius(int radius) {
        this.radius = radius;
    }

    private RequestBuilder buildThumbnailGlideRequest(@NonNull RequestManager glideRequests, @NonNull Slide slide) {
        BaseRequestOptions request = buildThumbnailRequestBuildWithUrl(glideRequests, slide.getThumbnailUri());

        return (RequestBuilder) request.apply(RequestOptions.errorOf(R.drawable.holder_rect));
    }

    private RequestBuilder buildThumbnailRequestBuildWithUrl(@NonNull RequestManager glideRequests, @NonNull Uri uri) {
        if (URLUtil.isNetworkUrl(uri.toString())) {
            return applySizing(glideRequests.load(transformUrlNetwork(uri))
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .transition(withCrossFade()), fit);
        } else {
            return applySizing(glideRequests.load(new DecryptableStreamUriLoader.DecryptableUri(uri))
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .transition(withCrossFade()), fit);
        }
    }

    private RequestBuilder buildRequestBuildWithUrl(@NonNull RequestManager glideRequests, @NonNull Uri uri) {
        if (URLUtil.isNetworkUrl(uri.toString())) {
            return glideRequests.load(transformUrlNetwork(uri))
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .transition(withCrossFade());
        } else {
            return glideRequests.load(new DecryptableStreamUriLoader.DecryptableUri(uri))
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .transition(withCrossFade());
        }
    }

    private String transformUrlNetwork(Uri uri) {
        if (URLUtil.isNetworkUrl(uri.toString())) {
            return ThumbImageUtils.thumb(uri.toString());
        }
        return uri.toString();
    }

    private RequestBuilder applySizing(@NonNull BaseRequestOptions request, @NonNull BitmapTransformation fitting) {
        if (radius > 0) {
            return (RequestBuilder) request.transforms(fitting, new RoundedCorners(radius));
        } else {
            return (RequestBuilder) request.transforms(fitting);
        }
    }

    @Override
    public void removeThumbnail() {
        if (thumbnailControlClickListener != null && slide != null) {
            thumbnailControlClickListener.onRemoveSlideClick(slide);
        }
    }

    @Override
    public void editThumbnail() {
        if (thumbnailControlClickListener != null && slide != null) {
            thumbnailControlClickListener.onEditSlideClick(slide);
        }
    }

    private class ThumbnailClickDispatcher implements OnClickListener {
        @Override
        public void onClick(View view) {
            if (thumbnailClickListener != null &&
                    slide != null &&
                    slide.asAttachment().getDataUri() != null) {
                thumbnailClickListener.onClick(view, slide);
            } else if (parentClickListener != null) {
                parentClickListener.onClick(view);
            }
        }
    }
}

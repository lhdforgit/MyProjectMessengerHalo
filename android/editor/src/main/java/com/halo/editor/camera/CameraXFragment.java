/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.editor.camera;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.util.Executors;
import com.google.common.base.Optional;
import com.halo.editor.R;
import com.halo.editor.components.TooltipPopup;
import com.halo.editor.glide.DecryptableStreamUriLoader;
import com.halo.editor.mediasend.Media;
import com.halo.editor.mediasend.MediaSendViewModel;
import com.halo.editor.util.MediaConstraints;
import com.halo.editor.util.MemoryFileDescriptor;
import com.halo.editor.util.SimpleTask;
import com.halo.editor.util.Stopwatch;
import com.halo.editor.util.TextSecurePreferences;
import com.halo.editor.util.VideoUtil;

import java.io.FileDescriptor;
import java.io.IOException;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;
import timber.log.Timber;

/**
 * Camera captured implemented using the CameraX SDK, which uses Camera2 under the hood. Should be
 * preferred whenever possible.
 */
@RequiresApi(21)
public class CameraXFragment extends DaggerFragment implements CameraFragment {

    private static final String TAG = CameraXFragment.class.getName();

    private CameraXView camera;
    private ViewGroup controlsContainer;
    private Controller controller;
    private MediaSendViewModel viewModel;
    private View selfieFlash;
    private MemoryFileDescriptor videoFileDescriptor;

    public static CameraXFragment newInstance() {
        CameraXFragment fragment = new CameraXFragment();

        fragment.setArguments(new Bundle());

        return fragment;
    }

    @Inject
    public CameraXFragment() {}

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (!(getActivity() instanceof Controller)) {
            throw new IllegalStateException("Parent activity must implement controller interface.");
        }

        this.controller = (Controller) getActivity();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel     = new ViewModelProvider(requireActivity()).get(MediaSendViewModel.class);
    }

    @Override
    public @Nullable
    View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.camerax_fragment, container, false);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        this.camera = view.findViewById(R.id.camerax_camera);
        this.controlsContainer = view.findViewById(R.id.camerax_controls_container);

        camera.bindToLifecycle(getViewLifecycleOwner());
        camera.setCameraLensFacing(CameraXUtil.toLensFacing(TextSecurePreferences.getDirectCaptureCameraId(requireContext())));

        onOrientationChanged(getResources().getConfiguration().orientation);

        viewModel.getMostRecentMediaItem().observe(this, this::presentRecentItemThumbnail);
        viewModel.getHudState().observe(this, this::presentHud);
    }

    @Override
    public void onResume() {
        super.onResume();

        viewModel.onCameraStarted();
        requireActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requireActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        closeVideoFileDescriptor();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        onOrientationChanged(newConfig.orientation);
    }

    private void onOrientationChanged(int orientation) {
        int layout = orientation == Configuration.ORIENTATION_PORTRAIT ? R.layout.camera_controls_portrait
                : R.layout.camera_controls_landscape;

        controlsContainer.removeAllViews();
        controlsContainer.addView(LayoutInflater.from(getContext()).inflate(layout, controlsContainer, false));
        initControls();
    }

    private void presentRecentItemThumbnail(Optional<Media> media) {
        if (media == null) {
            return;
        }

        ImageView thumbnail = controlsContainer.findViewById(R.id.camera_gallery_button);

        if (media.isPresent()) {
            thumbnail.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(new DecryptableStreamUriLoader.DecryptableUri(media.get().getUri()))
                    .centerCrop()
                    .into(thumbnail);
        } else {
            thumbnail.setVisibility(View.GONE);
            thumbnail.setImageResource(0);
        }
    }

    private void presentHud(@Nullable MediaSendViewModel.HudState state) {
        if (state == null) return;

        View countButton = controlsContainer.findViewById(R.id.camera_count_button);
        TextView countButtonText = controlsContainer.findViewById(R.id.mediasend_count_button_text);

        if (state.getButtonState() == MediaSendViewModel.ButtonState.COUNT) {
            countButton.setVisibility(View.VISIBLE);
            countButtonText.setText(String.valueOf(state.getSelectionCount()));
        } else {
            countButton.setVisibility(View.GONE);
        }
    }

    @SuppressLint({"ClickableViewAccessibility", "MissingPermission"})
    private void initControls() {
        View flipButton = requireView().findViewById(R.id.camera_flip_button);
        CameraButtonView captureButton = requireView().findViewById(R.id.camera_capture_button);
        View galleryButton = requireView().findViewById(R.id.camera_gallery_button);
        View countButton = requireView().findViewById(R.id.camera_count_button);
        CameraXFlashToggleView flashButton = requireView().findViewById(R.id.camera_flash_button);

        selfieFlash = requireView().findViewById(R.id.camera_selfie_flash);

        captureButton.setOnClickListener(v -> {
            captureButton.setEnabled(false);
            flipButton.setEnabled(false);
            flashButton.setEnabled(false);
            onCaptureClicked();
        });

        camera.setScaleType(CameraXView.ScaleType.CENTER_INSIDE);

        if (camera.hasCameraWithLensFacing(CameraSelector.LENS_FACING_FRONT) && camera.hasCameraWithLensFacing(CameraSelector.LENS_FACING_BACK)) {
            flipButton.setVisibility(View.VISIBLE);
            flipButton.setOnClickListener(v -> {
                camera.toggleCamera();
                TextSecurePreferences.setDirectCaptureCameraId(getContext(), CameraXUtil.toCameraDirectionInt(camera.getCameraLensFacing()));

                Animation animation = new RotateAnimation(0, -180, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
                animation.setDuration(200);
                animation.setInterpolator(new DecelerateInterpolator());
                flipButton.startAnimation(animation);
                flashButton.setAutoFlashEnabled(camera.hasFlash());
                flashButton.setFlash(camera.getFlash());
            });

            GestureDetector gestureDetector = new GestureDetector(requireContext(), new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    if (flipButton.isEnabled()) {
                        flipButton.performClick();
                    }
                    return true;
                }
            });

            camera.setOnTouchListener((v, event) -> gestureDetector.onTouchEvent(event));

        } else {
            flipButton.setVisibility(View.GONE);
        }

        flashButton.setAutoFlashEnabled(camera.hasFlash());
        flashButton.setFlash(camera.getFlash());
        flashButton.setOnFlashModeChangedListener(camera::setFlash);

        galleryButton.setOnClickListener(v -> controller.onGalleryClicked());
        countButton.setOnClickListener(v -> controller.onCameraCountButtonClicked());

        if (isVideoRecordingSupported(requireContext())) {
            try {
                closeVideoFileDescriptor();
                videoFileDescriptor = CameraXVideoCaptureHelper.createFileDescriptor(requireContext());

                Animation inAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in);
                Animation outAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_out);

                camera.setCaptureMode(CameraXView.CaptureMode.MIXED);

                int maxDuration = VideoUtil.getMaxVideoDurationInSeconds(requireContext(), viewModel.getMediaConstraints());
                Log.d(TAG, "Max duration: " + maxDuration + " sec");

                captureButton.setVideoCaptureListener(new CameraXVideoCaptureHelper(
                        this,
                        captureButton,
                        camera,
                        videoFileDescriptor,
                        maxDuration,
                        new CameraXVideoCaptureHelper.Callback() {
                            @Override
                            public void onVideoRecordStarted() {
                                hideAndDisableControlsForVideoRecording(captureButton, flashButton, flipButton, outAnimation);
                            }

                            @Override
                            public void onVideoSaved(@NonNull FileDescriptor fd) {
                                showAndEnableControlsAfterVideoRecording(captureButton, flashButton, flipButton, inAnimation);
                                controller.onVideoCaptured(fd);
                            }

                            @Override
                            public void onVideoError(@Nullable Throwable cause) {
                                showAndEnableControlsAfterVideoRecording(captureButton, flashButton, flipButton, inAnimation);
                                controller.onVideoCaptureError();
                            }
                        }
                ));
                displayVideoRecordingTooltipIfNecessary(captureButton);
            } catch (IOException e) {
                Timber.tag(TAG).w(e, "Video capture is not supported on this device.");
            }
        } else {
            Timber.i("Video capture not supported. " +
                    "API: " + Build.VERSION.SDK_INT + ", " +
                    "MFD: " + MemoryFileDescriptor.supported() + ", " +
                    "Camera: " + CameraXUtil.getLowestSupportedHardwareLevel(requireContext()) + ", " +
                    "MaxDuration: " + VideoUtil.getMaxVideoDurationInSeconds(requireContext(), viewModel.getMediaConstraints()) + " sec");
        }

        viewModel.onCameraControlsInitialized();
    }

    private boolean isVideoRecordingSupported(@NonNull Context context) {
        return Build.VERSION.SDK_INT >= 26 &&
                MediaConstraints.isVideoTranscodeAvailable() &&
                CameraXUtil.isMixedModeSupported(context) &&
                VideoUtil.getMaxVideoDurationInSeconds(context, viewModel.getMediaConstraints()) > 0;
    }

    private void displayVideoRecordingTooltipIfNecessary(CameraButtonView captureButton) {
        if (shouldDisplayVideoRecordingTooltip()) {
            int displayRotation = requireActivity().getWindowManager().getDefaultDisplay().getRotation();

            TooltipPopup.forTarget(captureButton)
                    .setOnDismissListener(this::neverDisplayVideoRecordingTooltipAgain)
                    .setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.core_ultramarine))
                    .setTextColor(ContextCompat.getColor(requireContext(), R.color.text_dark))
                    .setText(R.string.CameraXFragment_tap_for_photo_hold_for_video)
                    .show(displayRotation == Surface.ROTATION_0 || displayRotation == Surface.ROTATION_180 ? TooltipPopup.POSITION_ABOVE : TooltipPopup.POSITION_START);
        }
    }

    private boolean shouldDisplayVideoRecordingTooltip() {
        return !TextSecurePreferences.hasSeenVideoRecordingTooltip(requireContext()) && MediaConstraints.isVideoTranscodeAvailable();
    }

    private void neverDisplayVideoRecordingTooltipAgain() {
        Context context = getContext();
        if (context != null) {
            TextSecurePreferences.setHasSeenVideoRecordingTooltip(requireContext(), true);
        }
    }

    private void hideAndDisableControlsForVideoRecording(@NonNull View captureButton,
                                                         @NonNull View flashButton,
                                                         @NonNull View flipButton,
                                                         @NonNull Animation outAnimation) {
        captureButton.setEnabled(false);
        flashButton.startAnimation(outAnimation);
        flashButton.setVisibility(View.INVISIBLE);
        flipButton.startAnimation(outAnimation);
        flipButton.setVisibility(View.INVISIBLE);
    }

    private void showAndEnableControlsAfterVideoRecording(@NonNull View captureButton,
                                                          @NonNull View flashButton,
                                                          @NonNull View flipButton,
                                                          @NonNull Animation inAnimation) {
        requireActivity().runOnUiThread(() -> {
            captureButton.setEnabled(true);
            flashButton.startAnimation(inAnimation);
            flashButton.setVisibility(View.VISIBLE);
            flipButton.startAnimation(inAnimation);
            flipButton.setVisibility(View.VISIBLE);
        });
    }

    private void onCaptureClicked() {
        Stopwatch stopwatch = new Stopwatch("Capture");

        CameraXSelfieFlashHelper flashHelper = new CameraXSelfieFlashHelper(
                requireActivity().getWindow(),
                camera,
                selfieFlash
        );

        camera.takePicture(Executors.mainThreadExecutor(), new ImageCapture.OnImageCapturedCallback() {
            @Override
            public void onCaptureSuccess(@NonNull ImageProxy image) {
                flashHelper.endFlash();

                SimpleTask.run(CameraXFragment.this.getViewLifecycleOwner().getLifecycle(), () -> {
                    stopwatch.split("captured");
                    try {
                        return CameraXUtil.toJpeg(image, camera.getCameraLensFacing() == CameraSelector.LENS_FACING_FRONT);
                    } catch (IOException e) {
                        return null;
                    } finally {
                        image.close();
                    }
                }, result -> {
                    stopwatch.split("transformed");
                    stopwatch.stop(TAG);

                    if (result != null) {
                        controller.onImageCaptured(result.getData(), result.getWidth(), result.getHeight());
                    } else {
                        controller.onCameraError();
                    }
                });
            }

            @Override
            public void onError(ImageCaptureException exception) {
                flashHelper.endFlash();
                controller.onCameraError();
            }
        });

        flashHelper.startFlash();
    }

    private void closeVideoFileDescriptor() {
        if (videoFileDescriptor != null) {
            try {
                videoFileDescriptor.close();
                videoFileDescriptor = null;
            } catch (IOException e) {
                Log.w(TAG, "Failed to close video file descriptor", e);
            }
        }
    }
}

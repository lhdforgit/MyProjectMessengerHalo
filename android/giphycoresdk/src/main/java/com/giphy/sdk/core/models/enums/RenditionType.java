/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.giphy.sdk.core.models.enums;

public enum RenditionType {
    // Original file size and file dimensions. Good for desktop use.
    original,

    /// Preview image for original.
    originalStill,

    /// File size under 50kb. Duration may be truncated to meet file size requirements. Good for thumbnails and previews.
    preview,

    /// Duration set to loop for 15 seconds. Only recommended for this exact use case.
    looping,

    /// Height set to 200px. Good for mobile use.
    fixedHeight,

    /// Static preview image for fixed_height
    fixedHeightStill,

    /// Height set to 200px. Reduced to 6 frames to minimize file size to the lowest.
    /// Works well for unlimited scroll on mobile and as animated previews. See Giphy.com on mobile web as an example.
    fixedHeightDownsampled,

    /// Height set to 100px. Good for mobile keyboards.
    fixedHeightSmall,

    /// Static preview image for fixed_height_small
    fixedHeightSmallStill,

    /// Width set to 200px. Good for mobile use.
    fixedWidth,

    /// Static preview image for fixed_width
    fixedWidthStill,

    /// Width set to 200px. Reduced to 6 frames. Works well for unlimited scroll on mobile and as animated previews.
    fixedWidthDownsampled,

    /// Width set to 100px. Good for mobile keyboards.
    fixedWidthSmall,

    /// Static preview image for fixed_width_small
    fixedWidthSmallStill,

    /// File size under 2mb.
    downsized,

    /// File size under 200kb.
    downsizedSmall,

    /// File size under 5mb.
    downsizedMedium,

    /// File size under 8mb.
    downsizedLarge,

    /// Static preview image for downsized.
    downsizedStill,
}

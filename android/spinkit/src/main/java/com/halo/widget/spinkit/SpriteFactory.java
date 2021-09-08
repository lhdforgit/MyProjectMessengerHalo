/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.widget.spinkit;

import com.halo.widget.spinkit.sprite.Sprite;
import com.halo.widget.spinkit.style.ChasingDots;
import com.halo.widget.spinkit.style.Circle;
import com.halo.widget.spinkit.style.CubeGrid;
import com.halo.widget.spinkit.style.DoubleBounce;
import com.halo.widget.spinkit.style.FadingCircle;
import com.halo.widget.spinkit.style.FoldingCube;
import com.halo.widget.spinkit.style.MultiplePulse;
import com.halo.widget.spinkit.style.MultiplePulseRing;
import com.halo.widget.spinkit.style.Pulse;
import com.halo.widget.spinkit.style.PulseRing;
import com.halo.widget.spinkit.style.RotatingCircle;
import com.halo.widget.spinkit.style.RotatingPlane;
import com.halo.widget.spinkit.style.ThreeBounce;
import com.halo.widget.spinkit.style.WanderingCubes;
import com.halo.widget.spinkit.style.Wave;

/**
 * Create by ndn
 */
public class SpriteFactory {

    public static Sprite create(Style style) {
        Sprite sprite = null;
        switch (style) {
            case ROTATING_PLANE:
                sprite = new RotatingPlane();
                break;
            case DOUBLE_BOUNCE:
                sprite = new DoubleBounce();
                break;
            case WAVE:
                sprite = new Wave();
                break;
            case WANDERING_CUBES:
                sprite = new WanderingCubes();
                break;
            case PULSE:
                sprite = new Pulse();
                break;
            case CHASING_DOTS:
                sprite = new ChasingDots();
                break;
            case THREE_BOUNCE:
                sprite = new ThreeBounce();
                break;
            case CIRCLE:
                sprite = new Circle();
                break;
            case CUBE_GRID:
                sprite = new CubeGrid();
                break;
            case FADING_CIRCLE:
                sprite = new FadingCircle();
                break;
            case FOLDING_CUBE:
                sprite = new FoldingCube();
                break;
            case ROTATING_CIRCLE:
                sprite = new RotatingCircle();
                break;
            case MULTIPLE_PULSE:
                sprite = new MultiplePulse();
                break;
            case PULSE_RING:
                sprite = new PulseRing();
                break;
            case MULTIPLE_PULSE_RING:
                sprite = new MultiplePulseRing();
                break;
            default:
                break;
        }
        return sprite;
    }
}

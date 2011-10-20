package com.yukinkster.ar.guide;

/**
 * 端末の傾き(Azimuth, Pitch, Rollの変化を受け取るためのインターフェイス
 *
 */
public interface OrientationEventListener {

    /**
     * 端末の傾きが変化したときにコールバックされます
     *
     * @param azimuth
     * @param pitch
     * @param roll
     */
    void onOrientationChanged(float azimuth, float pitch, float roll);
}

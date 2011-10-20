package com.yukinkster.ar.guide;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * 端末の傾きを捕捉するためのSensorEventListenerの実装クラス
 *
 */
public class MySensorEventListener implements SensorEventListener {

    /** 計算用の回転行列 */
    float[] inR = new float[9];
    float[] outR = new float[9];
    float[] I = new float[9];

    /** 端末の傾き(順にAzimuth、 Pitch、 Roll。単位はradian) */
    private float[] orientation = new float[3];

    /** 地磁気センサーの値 */
    private float[] magneticFieldValues = new float[3];

    /** 加速度センサーの値 */
    private float[] accelerometerValues = new float[3];

    /** 傾きの変化を受け取るリスナーオブジェクト */
    private List<OrientationEventListener> orientationListeners = new CopyOnWriteArrayList<OrientationEventListener>();

    public MySensorEventListener(OrientationEventListener... listeners) {
        for (OrientationEventListener l : listeners)
            orientationListeners.add(l);
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void onSensorChanged(SensorEvent event) {
        synchronized (this) {
            switch (event.sensor.getType()) {
            case Sensor.TYPE_MAGNETIC_FIELD:
                // ローパスフィルターで細かい振動をゆるやかにします。
                for (int i = 0; i < 3; ++i)
                    magneticFieldValues[i] = event.values[i] * 0.1f + (magneticFieldValues[i] * (1.0f - 0.1f));
                break;
            case Sensor.TYPE_ACCELEROMETER:
                // ローパスフィルターで細かい振動をゆるやかにします。
                for (int i = 0; i < 3; ++i)
                    accelerometerValues[i] = event.values[i] * 0.1f + (accelerometerValues[i] * (1.0f - 0.1f));
                break;
            default:
                break;
            }

            // 加速度センサーと地磁気センサーの値から、回転行列を取得します。
            if (SensorManager.getRotationMatrix(inR, I, accelerometerValues, magneticFieldValues)) {
                // 端末の向きに合わせて行列を変換します。
                SensorManager.remapCoordinateSystem(inR, SensorManager.AXIS_Z, SensorManager.AXIS_MINUS_X, outR);
                // 端末の傾きを取得します。
                SensorManager.getOrientation(outR, orientation);
                for (int num = orientationListeners.size(), i = 0; i < num; ++i) {
                    float azimuth = orientation[0];
                    float pitch = orientation[1];
                    float roll = orientation[2];
                    // 傾きをリスナーに通知します.
                    orientationListeners.get(i).onOrientationChanged(azimuth, pitch, roll);
                }
            }
        }
    }
}

package com.yukinkster.ar.guide;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

public class MyActivity extends Activity {

    /** カメラ画像を表示するビューオブジェクト */
    SurfaceView cameraView;

    /** センサーの値を表示するためのビューオブジェクト */
    OrientationsView orientationsView;

    /** 端末の傾きを捕捉するリスナーオブジェクト */
    MySensorEventListener orientationListener;

    /** センサーの登録有無 */
    boolean sensorRegistered;

    /** SensorManager。端末の傾きを取得するために使います。 */
    SensorManager sensorManager;

    /** LocationManager。端末の現在位置を取得するために使います。 */
    LocationManager locationManager;

    /** 最新の現在位置 */
    Location lastLocation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //画面をONに保つ
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.main);

        // サービスを取得します
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // カメラ表示用のビューを初期化
        cameraView = (SurfaceView) findViewById(R.id.camera);
        SurfaceHolder surfaceHolder = cameraView.getHolder();
        surfaceHolder.addCallback(new MySurfaceHolderCallback());
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        // センサー表示用のビューを取得
        orientationsView = (OrientationsView) findViewById(R.id.orientations);
        // 傾きを検知するためのSensorEventListenerオブジェクトを作成します
        orientationListener = new MySensorEventListener(orientationsView);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // アプリケーションが前面になった場合にセンサーの捕捉を開始します。
        Sensor sensor;

        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        if (sensor != null)
            sensorRegistered |= sensorManager.registerListener(orientationListener, sensor, SensorManager.SENSOR_DELAY_UI);

        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (sensor != null)
            sensorRegistered |= sensorManager.registerListener(orientationListener, sensor, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // アプリケーションが前面でなくなった場合はセンサーの捕捉を中断して、電池の消費を抑えます。
        if (sensorRegistered)
            sensorManager.unregisterListener(orientationListener);
        sensorRegistered = false;
    }
}
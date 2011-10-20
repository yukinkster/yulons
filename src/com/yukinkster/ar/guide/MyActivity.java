package com.yukinkster.ar.guide;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

public class MyActivity extends Activity {


    /** 端末の座標の許容精度(m) */
    static final float ACCURACY_THRESHOLD = 100; // 100m
    /** 端末の座標を通知する間隔（秒） */
    static final int MIN_UPDATE_FLEQUENCY = 1 * 60 * 1000; // 1min
    /** 端末の座標を通知する最小距離（m） */
    static final int MIN_UPDATE_DISTANCE = 100; // 100m

    /** カメラ画像を表示するビューオブジェクト */
    SurfaceView cameraView;

    /** 位置情報をオーバーレイ表示するビューオブジェクト */
    LocationsView locationsView;

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

    /** 高精度な端末の座標を短期間で捕捉するリスナーオブジェクト */
    LocationListener bounceLocationListener = new LocationListener() {

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onProviderDisabled(String provider) {
        }

        public void onLocationChanged(Location location) {
            locationsView.onLocationChanged(location);
            if (location.getAccuracy() < ACCURACY_THRESHOLD) {
                // 許容される精度で座標が捕捉できたら、捕捉を終了します
                locationManager.removeUpdates(bounceLocationListener);
                locationManager.removeUpdates(coarseLocationListener);
            }
        }
    };

    /** 低精度な端末の座標を捕捉するリスナーオブジェクト */
    LocationListener coarseLocationListener = new LocationListener() {

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onProviderDisabled(String provider) {
        }

        public void onLocationChanged(Location location) {
            locationsView.onLocationChanged(location);
            if (location.getAccuracy() < ACCURACY_THRESHOLD) {
                // 許容される精度で座標が捕捉できたら、捕捉を終了します
                locationManager.removeUpdates(coarseLocationListener);
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //画面をONに保ちます
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.main);

        // サービスを取得します
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // カメラ表示用のビューを初期化します
        cameraView = (SurfaceView) findViewById(R.id.camera);
        SurfaceHolder surfaceHolder = cameraView.getHolder();
        surfaceHolder.addCallback(new MySurfaceHolderCallback());
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        // オーバーレイ表示用のビューを取得します
        locationsView = (LocationsView) findViewById(R.id.locations);
        // センサー表示用のビューを取得します
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

        // アプリケーションが前面になった場合に端末の座標の捕捉を開始します。

        // 高精度な位置情報を通常の間隔で捕捉するには時間がかかるので、
        // (1)高精度な位置情報を短期間で捕捉するリスナー
        // (2)低精度な位置情報を捕捉するリスナー
        // の二つを追加で登録し、短い時間で位置情報が取れるようにします。
        // 許容される精度の位置情報が取れた際には(1)、(2)の登録は解除します。
        Criteria criteria = new Criteria();
        criteria.setPowerRequirement(Criteria.POWER_LOW);

        // 高精度な位置情報を捕捉するためのリスナーを要求します。
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        String bestProvider = locationManager.getBestProvider(criteria, true);

        // 低精度な位置情報を捕捉するためのリスナーを要求します。
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        String coarseProvider = locationManager.getBestProvider(criteria, true);

        locationManager.requestLocationUpdates(bestProvider, MIN_UPDATE_FLEQUENCY, MIN_UPDATE_DISTANCE, locationsView);
        locationManager.requestLocationUpdates(coarseProvider, 0, 0, coarseLocationListener);
        locationManager.requestLocationUpdates(bestProvider, 0, 0, bounceLocationListener);

    }

    @Override
    protected void onPause() {
        super.onPause();

        // アプリケーションが前面でなくなった場合はセンサーの捕捉を中断して、電池の消費を抑えます。
        if (sensorRegistered)
            sensorManager.unregisterListener(orientationListener);
        sensorRegistered = false;

        // アプリケーションが前面でなくなった場合は端末の座標の捕捉を中断して、電池の消費を抑えます。
        locationManager.removeUpdates(locationsView);
        locationManager.removeUpdates(coarseLocationListener);
        locationManager.removeUpdates(bounceLocationListener);

    }
}
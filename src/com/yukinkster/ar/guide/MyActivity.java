package com.yukinkster.ar.guide;

import android.app.Activity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

public class MyActivity extends Activity {

    /** カメラ画像を表示するビューオブジェクト */
    SurfaceView cameraView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //画面をONに保つ
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.main);

        // カメラ表示用のビューを初期化
        cameraView = (SurfaceView) findViewById(R.id.camera);
        SurfaceHolder surfaceHolder = cameraView.getHolder();
        surfaceHolder.addCallback(new MySurfaceHolderCallback());
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }
}
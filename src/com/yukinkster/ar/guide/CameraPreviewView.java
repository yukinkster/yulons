package com.yukinkster.ar.guide;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

/**
 * カメラのプレビュー画像を表示
 *
 */
public class CameraPreviewView extends SurfaceView implements SurfaceHolder.Callback {

    private Camera camera;

    public CameraPreviewView(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);
        init();
    }

    public CameraPreviewView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    public CameraPreviewView(Context context) {
        super(context);
        init();
    }

    /**
     * 　SurfaceHolderを初期化
     *
     */
    protected void init() {
        SurfaceHolder surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    /**
     * surface変更時の処理
     */
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        if (camera != null) {
            // Surfaceが変更されたらプレビュー停止して、変更に合わせてプレビューサイズを変更
            camera.stopPreview();
            setPreviewSize(width, height);

            // 再プレビュー
            camera.startPreview();
        }
    }

    /**
     * surfaceの生成時の処理
     */
    public void surfaceCreated(SurfaceHolder holder) {

        if (camera == null) {
            try {
                // カメラを取得
                camera = Camera.open();
            } catch (RuntimeException e) {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }
        }

        try {

            // カメラのプレビュー画像の表示先を設定
            camera.setPreviewDisplay(holder);
        } catch (IOException e) {

            camera.release();
            camera = null;
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * surfaceの破棄
     */
    public void surfaceDestroyed(SurfaceHolder holder) {

        if (camera != null) {

            // プレビューを停止してカメラを解放
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    /**
     * プレビュー画像のサイズを設定.
     *
     * @param width 幅
     * @param height 高さ
     */
    void setPreviewSize(int width, int height) {

        // 端末のカメラでサポートされているプレビュー画像のサイズを取得します
        Camera.Parameters params = camera.getParameters();
        List<Camera.Size> supported = params.getSupportedPreviewSizes();

        if (supported != null) {
            for (Camera.Size size : supported) {

                // 画面のサイズ以下のサイズを選択して設定
                if (size.width <= width && size.height <= height) {
                    params.setPreviewSize(size.width, size.height);
                    camera.setParameters(params);
                    break;

                }
            }
        }
    }
}

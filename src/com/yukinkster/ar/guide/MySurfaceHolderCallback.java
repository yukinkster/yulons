package com.yukinkster.ar.guide;

import java.io.IOException;
import java.util.List;

import android.hardware.Camera;
import android.view.SurfaceHolder;

public class MySurfaceHolderCallback implements SurfaceHolder.Callback {

    private Camera camera;

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        if (camera != null) {
            // Surfaceが変更されたら一旦プレビューを停止します
            camera.stopPreview();

            // 変更されたSurfaceにあわせてプレビューのサイズを変更します
            setPreviewSize(width, height);
            // プレビューを開始します
            camera.startPreview();
        }
    }

    public void surfaceCreated(SurfaceHolder holder) {
        if (camera == null) {
            try {
                // Surfaceが生成されるときに、カメラを取得します。
                camera = Camera.open();
            } catch (RuntimeException e) {
                e.printStackTrace();
                return;
            }
        }

        try {
            // カメラのプレビュー画像の表示先を設定します。
            camera.setPreviewDisplay(holder);
        } catch (IOException e) {
            // 設定に失敗したらカメラを解放します。
            camera.release();
            camera = null;
            e.printStackTrace();
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        if (camera != null) {
            // Surfaceが破棄されるときに、プレビューを停止してカメラを解放します。
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    /**
     * プレビュー画像のサイズを設定します。
     *
     * @param width
     * @param height
     */
    void setPreviewSize(int width, int height) {
        Camera.Parameters params = camera.getParameters();
        // 端末のカメラでサポートされているプレビュー画像のサイズを取得します。
        List<Camera.Size> supported = params.getSupportedPreviewSizes();
        if (supported != null) {
            for (Camera.Size size : supported) {
                // サポートされているサイズの中から、画面のサイズと同じ（または小さい）ものを選択します。
                if (size.width <= width && size.height <= height) {
                    params.setPreviewSize(size.width, size.height);
                    camera.setParameters(params);
                    break;
                }
            }
        }
    }
}

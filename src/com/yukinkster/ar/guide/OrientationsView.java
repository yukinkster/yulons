package com.yukinkster.ar.guide;



import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class OrientationsView extends View implements OrientationEventListener {

    /** 端末のAzimuth, Pitch, Rollの値を保持します(degrees) */
    private float[] orientaitonValues = new float[3];

    /** センサー表示のためのPaintオブジェクト */
    private Paint paint = new Paint();
    /** センサー表示の前景の半円を描画するためのPathオブジェクト */
    private Path path = new Path();
    /** センサー表示の領域を定義するRectFオブジェクト */
    private RectF rect = new RectF();

    public OrientationsView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public OrientationsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public OrientationsView(Context context) {
        super(context);
        init();
    }

    private void init() {
        // センサーの前景の半円を描くためにpaint, rect, pathを初期化します。
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        rect.set(-0.5f, -0.5f, 0.5f, 0.5f);
        path.arcTo(rect, 0, 180);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        synchronized (this) {

            // 半透明の白色。センサー表示の背景に使います
            final int outer = 0x88FFFFFF;
            // 半透明の青色。センサー表示の前景に使います
            final int inner = 0x88005aff;
            // 画面の高さ
            final int height = getHeight();
            // 画面の幅
            final int width = getWidth();

            // センサー表示のy座標のオフセット
            float h0 = height * 0.333333f;
            float h = h0 - 32;
            float y = h0 * 0.5f;

            // メンバ変数 orientaionValues に
            // Azimuth, Pitch, Roll の順保存されている値を表示します。
            for (int i = 0; i < 3; i++) {
                canvas.save(Canvas.MATRIX_SAVE_FLAG);

                // 描画の起点を画面の右側のセンサー表示位置に移動します
                canvas.translate(width - (h * 0.5f + 4.0f), y);

                canvas.save(Canvas.MATRIX_SAVE_FLAG);

                // センサーの背景の色（白色）を設定します
                paint.setColor(outer);

                // 描画のサイズをセンサー表示一つ分にあわせます
                canvas.scale(h, h);
                // センサー表示の背景を描画します
                canvas.drawOval(rect, paint);
                canvas.restore();

                // 描画のサイズを背景より少し内側にします
                canvas.scale(h - 5, h - 5);
                paint.setColor(inner);
                // センサー表示の前景（半円）をセンサーの値の分だけ傾けます
                // センサーの値はradiansのため、degreesに変換します。
                canvas.rotate((float) -Math.toDegrees(orientaitonValues[i]));
                // センサー表示の前景を描画します
                canvas.drawPath(path, paint);
                canvas.restore();

                y += h0;
            }
        }
    }

    /**
     * OrientationEventListenerインターフェイスのメソッドを実装して、端末の傾きの変換を取得します。
     *
     * @see com.example.android.ar.simple.OrientationEventListener#onOrientationChanged(float,
     *      float, float)
     */
    public void onOrientationChanged(float azimuth, float pitch, float roll) {
        synchronized (this) {
            // 端末の傾き(azimuth, pitch, roll)の値をメンバ変数に保持します
            orientaitonValues[0] = azimuth;
            orientaitonValues[1] = pitch;
            orientaitonValues[2] = roll;
            // 傾きの変化に応じて、画面を再描画します
            invalidate();
        }
    }
}

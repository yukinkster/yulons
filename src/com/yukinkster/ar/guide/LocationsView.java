package com.yukinkster.ar.guide;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;

/**
 * カメラ画像に位置情報をオーバーレイ表示するためのクラスです。
 *
 */
public class LocationsView extends View implements LocationListener {

    /** 位置情報のラベルを表示するためのPaint */
    Paint paint = new Paint();

    /** 端末の座標 */
    Location deviceLocation;

    /** 端末の座標に対応する住所 */
    private String address;

    public LocationsView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public LocationsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LocationsView(Context context) {
        super(context);
        init();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        synchronized (this) {
            // 現在位置の住所を表示します。
            if (address != null) {
                canvas.drawText(address, 20, 20, paint);
            }
        }
    }

    /**
     * 文字列を表示するためのPaintを初期化します。
     *
     * @param context
     *
     */
    protected void init() {
        paint.setAntiAlias(true);
        paint.setTextSize(12);
        paint.setColor(Color.WHITE);
    }

    public void onLocationChanged(Location location) {
        synchronized (this) {
            if (location == null)
                return;

            GeocoderTask geocoderTask = new GeocoderTask(getContext()) {
                @Override
                protected void onPostExecute(String result) {
                    synchronized (this) {
                        address = result;
                        invalidate();
                    }
                }
            };
            geocoderTask.execute(location.getLatitude(), location.getLongitude());

            // 端末の位置が変わった場合、位置情報をクリアして取得しなおします。
            deviceLocation = location;
        }
    }

    public void onProviderDisabled(String provider) {
    }

    public void onProviderEnabled(String provider) {
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
    }
}

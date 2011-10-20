package com.yukinkster.ar.guide;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;

public abstract class GeocoderTask extends AsyncTask<Double, Void, String> {

    /** Geocoder。現在位置を記録する際に逆ジオコーディングで住所を取得するために使います。 */
    private Geocoder geocoder;

    /** 住所が取得できない場合の代替文字列 */
    private static final String UNKNOWN = "<unknown>";

    public GeocoderTask(Context context) {
        geocoder = new Geocoder(context);
    }

    @Override
    protected String doInBackground(Double... params) {
        try {
            double latitude = params[0];
            double longitude = params[1];
            // getFromLocationメソッドを呼び出して、座標から住所を取得します。
            List<Address> location = geocoder.getFromLocation(latitude, longitude, 1);
            String address = representOf(location);
            return address;
        } catch (IOException e) {
            return UNKNOWN;
        }
    }

    /**
     * アドレスを書式化します。
     *
     * @param address
     * @return
     */
    private String representOf(List<Address> addresses) {

        if (addresses == null || addresses.isEmpty())
            return UNKNOWN;
        Address address = addresses.get(0);
        StringBuilder b = new StringBuilder();
        int lines = address.getMaxAddressLineIndex();
        for (int i = 0; i <= lines; ++i) {
            if (i > 0)
                b.append(' ');
            b.append(address.getAddressLine(i));
        }
        return b.toString();
    }

}

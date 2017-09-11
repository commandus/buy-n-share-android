package com.commandus.buynshare;

import android.content.Context;
import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Helper class provides static methods to install app, get value line #1 number, impoved version of parseUri etc.
 */
public class Helper {
    private static final String TAG = Helper.class.getSimpleName();
    /**
     * Helper static method to read data from the application resource.
     * like Internet service or Android data provider.
     * @param context	Context
     * @param id	application resource id
     * @return	byte array. null if fail
     */
    public static byte[] loadResource(Context context, int id) {
        InputStream strm;
        try {
            strm = context.getResources().openRawResource(id);
        } catch (Exception e) {
            return null;
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        copy(baos, strm);
        byte[] r = baos.toByteArray();
        try {
            strm.close();
            baos.close();
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }
        return r;
    }

    /**
     * Copy stream from src to dest
     * @param dest	destination stream
     * @param src	source stream
     * @return	true if success
     */
    private static boolean copy(OutputStream dest, InputStream src) {
        int b;
        byte[] buffer = new byte[1024];
        do {
            try {
                b = src.read(buffer);
                if (b <= 0)
                    break;
                dest.write(buffer, 0, b);
            } catch (IOException e) {
                return false;
            }
        } while (true);
        return true;
    }

    /**
     * Get resource Uri string
     * @param context   Context
     * @param resourceId    resource id
     * @return String
	 */
    public static String getResourceUri(Context context, int resourceId) {
        return "android.resource://" + context.getPackageName() + "/" + resourceId;
    }

	/**
     * listview get item view by position
     * @param position   position
     * @param listView listview
     * @return list item
     *
     * @see "http://stackoverflow.com/questions/24811536/android-listview-get-item-view-by-position"
     */
    public static View getViewByPosition(ListView listView, int position) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (position < firstListItemPosition || position > lastListItemPosition ) {
            return listView.getAdapter().getView(position, null, listView);
        } else {
            final int childIndex = position - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }

    public static void installTTS(Context context) {
        // missing data, install it
        Intent installIntent = new Intent();
        installIntent.setAction(
                TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
        context.startActivity(installIntent);

    }

    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

}

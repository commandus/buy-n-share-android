package com.commandus.buynshare;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import bs.UserFridges;

public class UserFridgeProvider extends ContentProvider {
    public static final String CONTENT_AUTHORITY = "com.commandus.buynshare.userfridge";
    public static final Uri CONTENT_URI = Uri.parse(AppProviderHelper.PROTO_CONTENT + CONTENT_AUTHORITY);
    private static final String TAG = UserFridgeProvider.class.getSimpleName();

    private static UriMatcher URL_MATCHER;
    private static final int MATCH_LIST = 1;

    public static UserFridges userFridges = null;
    public static final java.lang.String[] FIELDS = {"_id", "fridge_cn", "meal_cn", "meal_qty"};

    public UserFridgeProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        return 0;
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        return "application/octet-stream";
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public boolean onCreate() {
        // TODO: Implement this to initialize your content provider on startup.
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        switch (URL_MATCHER.match(uri)) {
            case MATCH_LIST:
                break;
            default:
                throw new IllegalArgumentException("Invalid content provider URI: " + uri.toString());
                // return null;
        }
        MatrixCursor c = new MatrixCursor(FIELDS);
        ByteBuffer byteBuffer;
        try {
            byteBuffer = ByteBuffer.wrap(Helper.loadResource(getContext(), R.raw.ls_userfridge_2));
            userFridges = UserFridges.getRootAsUserFridges(byteBuffer);
            int count = 0;
            Log.i(TAG, Integer.toString(userFridges.mealcardsLength()));
            Log.i(TAG, Integer.toString(userFridges.usersLength()));
            Log.i(TAG, Long.toString(userFridges.user().id()));
            Log.i(TAG, userFridges.user().cn());
            for (int f = 0; f < userFridges.mealcardsLength(); f++)
            {
                for (int mc = 0; mc < userFridges.mealcards(f).mealcardsLength(); mc++)
                {
                    MatrixCursor.RowBuilder row = c.newRow();
                    row.add(count);
                    row.add(userFridges.mealcards(f).fridge().cn());
                    row.add(userFridges.mealcards(f).mealcards(mc).meal().cn());
                    row.add(userFridges.mealcards(f).mealcards(mc).qty());
                    count++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;

    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        return 0;
    }

    static {
        URL_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        URL_MATCHER.addURI(CONTENT_AUTHORITY , null, MATCH_LIST);
    }
}
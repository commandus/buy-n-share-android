package com.commandus.buynshare;

import android.content.*;
import android.database.*;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;

import android.net.Uri;
import android.content.ContentUris;
import android.support.annotation.NonNull;

import java.util.Date;

/**
 * Saved chat messages
 */
public class MealCardProvider extends ContentProvider {
    private SQLiteDatabase mDB;

    private static final String TABLE = "chatmsg";
    private static final String CURSOR_TYPE_DIR = AppProviderHelper.CURSOR_TYPE_DIR + AppProviderHelper.DOMAIN + "." + TABLE;
    private static final String CURSOR_TYPE_ITEM = AppProviderHelper.CURSOR_TYPE_ITEM + AppProviderHelper.DOMAIN + "." + TABLE;
    public static final Uri CONTENT_URI = Uri.parse(AppProviderHelper.PROTO_CONTENT + AppProviderHelper.DOMAIN + "." + TABLE);

    public static final String CONTENT_AUTHORITY = AppProviderHelper.DOMAIN + "." + TABLE;

    // fields
    private static final String F_IID = "_id";
    // message identifier
    public static final String F_ID  = "id";
    public static final String F_QTY = "q"; 			        // quantity
    public static final String F_MEAL_ID = "meal_id";	        // meal identifier
    public static final String F_MEAL_CN = "meal_cn";           // meal common name
    public static final String F_MEAL_LOCALE = "meal_locale";	// meal locale code

    public static final int ID = 1;
    public static final int QTY = 2;
    public static final int MEAL_ID = 3;
    public static final int MEAL_CN = 4;
    public static final int MEAL_LOCALE = 5;

    private static final UriMatcher URL_MATCHER;
    private static class DatabaseHelper extends SQLiteOpenHelper {
        public DatabaseHelper(Context context) {
            super(context, TABLE, null, AppProviderHelper.DATABASE_VERSION);
        }
        @Override public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + TABLE + " (" + F_IID + AppProviderHelper.TYPE_PK_COLON
                    + F_ID + AppProviderHelper.TYPE_INTEGER_COLON
                    + F_QTY + AppProviderHelper.TYPE_INTEGER_COLON
                    + F_MEAL_ID + AppProviderHelper.TYPE_INTEGER_COLON
                    + F_MEAL_CN + AppProviderHelper.TYPE_TEXT_COLON
                    + F_MEAL_LOCALE + AppProviderHelper.TYPE_TEXT
                    +")");
            db.execSQL(AppProviderHelper.getStmtCreateIndex1(TABLE, F_ID));
        }
        @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(AppProviderHelper.getStmtDropIndex1(TABLE, F_ID));
            db.execSQL(AppProviderHelper.STMT_DROP_TABLE + TABLE);
            onCreate(db);
        }
    }

    @Override
    public boolean onCreate() {
        DatabaseHelper dbHelper = new DatabaseHelper(getContext());
        mDB = dbHelper.getWritableDatabase();
        return (mDB != null);
    }

    @Override
    public Cursor query(@NonNull Uri url, String[] projection, String selection, String[] selectionArgs, String sort) {
        String[] sa;
        if ((sort == null) || (sort.isEmpty()))
            sort = F_IID + " ASC";

        switch (URL_MATCHER.match(url)) {
            case 1:
                sa = selectionArgs;
                break;
            case 2:
                selection = F_ID + "=?";
                sa = new String[]{url.getPathSegments().get(0)};
                break;
            default:
                throw new IllegalArgumentException(AppProviderHelper.ERR_WRONG_URL + url);
        }
        Cursor c = mDB.query(TABLE, null, selection, sa, null, null, sort);
        Context ctx = getContext();
        if (ctx != null)
            c.setNotificationUri(ctx.getContentResolver(), url);
        return c;
    }

    @Override
    public String getType(@NonNull Uri url) {
        switch (URL_MATCHER.match(url)) {
            case 1:
                return CURSOR_TYPE_DIR;
            case 2:
                return CURSOR_TYPE_ITEM;
            default:
                throw new IllegalArgumentException(AppProviderHelper.ERR_WRONG_URL + url);
        }
    }

    /**
     * Find certificate by cert id
     * @param msgId certificate identifier
     * @return row id
     */
    private long findRowId(String msgId)
    {
        if (msgId == null)
            return 0;
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(TABLE);
        Cursor c = qb.query(mDB, new String[]{F_ID}, F_ID + "=?", new String[]{msgId},
                null, null, null);
        long r = 0;
        if (c.moveToFirst())
            r = c.getLong(0);
        c.close();
        return r;
    }

    @Override
    public Uri insert(@NonNull Uri url, ContentValues initialValues) {
        if (URL_MATCHER.match(url) < 1)
            throw new IllegalArgumentException(AppProviderHelper.ERR_WRONG_URL + url);

        ContentValues values;
        if (initialValues != null)
            values = new ContentValues(initialValues);
        else
            values = new ContentValues();

        long row = findRowId(values.getAsString(F_ID));
        if (row == 0)
            row = mDB.insert(TABLE, F_MEAL_LOCALE, values);
        else
            mDB.update(TABLE, values, F_ID + "=?", new String[]{Long.toString(row)});
        Uri uri = ContentUris.appendId(CONTENT_URI.buildUpon(), row).build();
        Context ctx = getContext();
        if (ctx != null)
            ctx.getContentResolver().notifyChange(uri, null);
        return uri;
    }

    @Override public int delete(@NonNull Uri url, String where, String[] whereArgs) {
        int count;
        switch (URL_MATCHER.match(url)) {
            case 1:
                count = mDB.delete(TABLE, where, whereArgs);
                break;
            case 2:
                String segment = url.getPathSegments().get(0);
                count = mDB.delete(TABLE, F_ID + "=?", new String[] {segment});
                break;
            default:
                throw new IllegalArgumentException(AppProviderHelper.ERR_WRONG_URL + url);
        }
        Context ctx = getContext();
        if (ctx != null)
            ctx.getContentResolver().notifyChange(url, null);
        return count;
    }

    @Override
    public int update(@NonNull Uri url, ContentValues values, String where, String[] whereArgs) {
        int count;
        switch (URL_MATCHER.match(url)) {
            case 1:
                count = mDB.update(TABLE, values, where, whereArgs);
                break;
            case 2:
                String segment = url.getPathSegments().get(0);
                count = mDB.update(TABLE, values, F_ID + "=?", new String[] {segment});
                if (count == 0)
                    insert(url, values);
                break;
            default:
                throw new IllegalArgumentException(AppProviderHelper.ERR_WRONG_URL + url);
        }
        Context ctx = getContext();
        if (ctx != null)
            ctx.getContentResolver().notifyChange(url, null);
        return count;
    }

    static {
        URL_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        URL_MATCHER.addURI(CONTENT_AUTHORITY, null, 1);
        URL_MATCHER.addURI(CONTENT_AUTHORITY, "#", 2);
    }
}

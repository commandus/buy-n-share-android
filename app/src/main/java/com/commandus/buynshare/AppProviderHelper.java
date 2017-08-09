package com.commandus.buynshare;

public class AppProviderHelper {
    public static final String DOMAIN = "com.commandus.buynshare";
    public static final String DATABASE_NAME = "bns.db";
    public static final int DATABASE_VERSION = 1;

    public static final String CURSOR_TYPE_DIR = "vnd.android.cursor.dir/";
    public static final String CURSOR_TYPE_ITEM = "vnd.android.cursor.item/";
    public static final String PROTO_CONTENT = "content://";
    // SQLITE3 data type names

    public static final String TYPE_PK = " INTEGER PRIMARY KEY";
    public static final String TYPE_PK_COLON = TYPE_PK + ",";
    public static final String TYPE_TEXT = " TEXT";
    public static final String TYPE_TEXT_COLON = TYPE_TEXT + ",";
    public static final String TYPE_BLOB = " BLOB";
    public static final String TYPE_BLOB_COLON = TYPE_BLOB + ",";
    public static final String TYPE_DOUBLE = " DOUBLE";
    public static final String TYPE_DOUBLE_COLON = TYPE_DOUBLE + ",";
    public static final String TYPE_INTEGER = " INTEGER";
    public static final String TYPE_INTEGER_COLON = TYPE_INTEGER + ",";

    public static final String STMT_CREATE_INDEX = "CREATE INDEX i_";
    public static final String STMT_CREATE_UNIQUE_INDEX = "CREATE UNIQUE INDEX i_";
    public static final String STMT_DROP_INDEX = "DROP INDEX IF EXISTS i_";
    public static final String STMT_DROP_TABLE = "DROP TABLE IF EXISTS ";

    public static final String ERR_WRONG_URL = "Wrong URL ";
    public static final String ERR_NO_ID = "No id, URL ";

    public static String getStmtCreateIndex1(String table, String fieldname) {
        return STMT_CREATE_INDEX + table + "_" + fieldname + " ON " + table + "(" + fieldname + ")";
    }

    public static String getStmtCreateUniqueIndex1(String table, String fieldname) {
        return STMT_CREATE_UNIQUE_INDEX + table + "_" + fieldname + " ON " + table + "(" + fieldname + ")";
    }

    public static String getStmtDropIndex1(String table, String fieldname) {
        return AppProviderHelper.STMT_DROP_INDEX + table + "_" + fieldname;
    }
}

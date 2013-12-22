package org.gegorer.icecream.provider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.gegorer.icecream.ProviderHelper;
import org.gegorer.icecream.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.database.sqlite.SQLiteStatement;
import android.net.Uri;

public class IcecreamProvider extends ContentProvider {

    static final int URI_TYPE_FAMI_TABLE = 0;
    static final String TAG = "ICECREAM";
    static String VERSION;
    private static final int DB_VERSION = 1;


    private SQLiteDatabase mDb;

    static UriMatcher sUriMatcher;
    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(ProviderHelper.AUTHORITY, ProviderHelper.FAMI_TABLE, URI_TYPE_FAMI_TABLE);
    };

    @Override
    public boolean onCreate() {
        DatabaseHelper dbHelper = new DatabaseHelper(getContext());
        this.mDb = dbHelper.getReadableDatabase();
        return true;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        SQLiteQueryBuilder sqlBuilder;
        Cursor c;
        sqlBuilder = new SQLiteQueryBuilder();
        sqlBuilder.setTables(ProviderHelper.SHOPS_TABLE_NAME);
        c = sqlBuilder.query(mDb, new String[]{"rowid AS _id", ProviderHelper.SHOPS_FIELD_NAME, ProviderHelper.SHOPS_FIELD_ADDR}, selection, selectionArgs, null, null, sortOrder);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int delete(Uri arg0, String arg1, String[] arg2) {
        return 0;
    }

    private static class DatabaseHelper extends SQLiteOpenHelper{
        private static final String DATABASE_NAME = "IceCream.db";
        private static final int DATABASE_VERSION = IcecreamProvider.DB_VERSION;
        private static final String INFO_FIELDS = " ([" + ProviderHelper.INFO_FIELD_VERSION + "] TEXT)";
        private static final String SHOPS_FIELDS = " (" + 
                "[" + ProviderHelper.SHOPS_FIELD_SERID + "] INT PRIMARY KEY" +
                ",[" + ProviderHelper.SHOPS_FIELD_PX + "] REAL" +
                ",[" + ProviderHelper.SHOPS_FIELD_PY + "] REAL" +
                ",[" + ProviderHelper.SHOPS_FIELD_ADDR + "] TEXT" +
                ",[" + ProviderHelper.SHOPS_FIELD_NAME + "] TEXT" +
                ",[" + ProviderHelper.SHOPS_FIELD_ICECREAM + "] TEXT" +
                ")";
        private static final String INFO_TABLE_CREATE = "CREATE TABLE [" + ProviderHelper.INFO_TABLE_NAME + "]" + INFO_FIELDS;
        private static final String SHOPS_TABLE_CREATE = "CREATE TABLE [" + ProviderHelper.SHOPS_TABLE_NAME + "]" + SHOPS_FIELDS;
        private static final String SHOPS_TABLE_INSERT = "INSERT OR IGNORE INTO " + ProviderHelper.SHOPS_TABLE_NAME + " (" +
                ProviderHelper.SHOPS_FIELD_SERID + "," +
                ProviderHelper.SHOPS_FIELD_PX + "," +
                ProviderHelper.SHOPS_FIELD_PY + "," +
                ProviderHelper.SHOPS_FIELD_ADDR + "," +
                ProviderHelper.SHOPS_FIELD_NAME + "," +
                ProviderHelper.SHOPS_FIELD_ICECREAM + ")" +
                " VALUES (?, ?, ?, ?, ?, ?)";

        private Context mCtx;

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            mCtx = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(INFO_TABLE_CREATE);
            db.execSQL(SHOPS_TABLE_CREATE);
            InputStream is = mCtx.getResources().openRawResource(R.raw.all_shop);
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                StringBuilder responseStrBuilder = new StringBuilder();
                String inputStr;
                while((inputStr = reader.readLine()) != null){
                    responseStrBuilder.append(inputStr);
                }
                JSONObject obj = new JSONObject(responseStrBuilder.toString());
                String version = obj.getString(ProviderHelper.INFO_FIELD_VERSION);
                ContentValues cv = new ContentValues();
                cv.put(ProviderHelper.INFO_FIELD_VERSION, version);
                db.insert(ProviderHelper.INFO_TABLE_NAME, null, cv);

                JSONArray shopList = obj.getJSONArray("shopList");
                SQLiteStatement stmt = db.compileStatement(SHOPS_TABLE_INSERT);
                db.beginTransaction();
                int len = shopList.length();
                for(int i = 0; i < len; i++){
                    JSONObject shop = shopList.getJSONObject(i);
                    int idx = 1;
                    stmt.bindLong(idx++, shop.optLong(ProviderHelper.SHOPS_FIELD_SERID));
                    stmt.bindDouble(idx++, shop.optDouble(ProviderHelper.SHOPS_FIELD_PX));
                    stmt.bindDouble(idx++, shop.optDouble(ProviderHelper.SHOPS_FIELD_PY));
                    stmt.bindString(idx++, shop.optString(ProviderHelper.SHOPS_FIELD_ADDR));
                    stmt.bindString(idx++, shop.optString(ProviderHelper.SHOPS_FIELD_NAME));
                    stmt.bindString(idx++, shop.optString(ProviderHelper.SHOPS_FIELD_ICECREAM));
                    stmt.execute();
                }
                db.setTransactionSuccessful();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                db.endTransaction();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }
}

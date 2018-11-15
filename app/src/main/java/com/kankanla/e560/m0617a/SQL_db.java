package com.kankanla.e560.m0617a;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by E560 on 2017/06/29.
 */

public class SQL_db {
    protected final String db_name = "M0617A";
    protected final int db_ver = 2;
    protected Context context;
    protected DB db;

    public SQL_db(Context context) {
        this.context = context;
        db = new DB(context);
    }

    //
    protected boolean find_time(String time) {
        String table_name = "time_item";
        SQLiteDatabase sqLiteDatabase = db.getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("itme_time", time);
        Cursor cursor = sqLiteDatabase.query(table_name, new String[]{"itme_time"}, "itme_time = ?", new String[]{time}, null, null, null);
        if (cursor.getCount() > 0) {
            return true;
        } else {
            return false;
        }
    }

    protected String find_time_id(String time) {
        String table_name = "time_item";
        SQLiteDatabase sqLiteDatabase = db.getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("itme_time", time);
        Cursor cursor = sqLiteDatabase.query(table_name, new String[]{"itme_time"}, "itme_time = ?", new String[]{time}, null, null, null);
        String retid = cursor.getString(cursor.getColumnIndex("_id"));
        return retid;
    }

    protected void setAuto_Status(String id, Integer chk) {
        String table_name = "time_item";
        SQLiteDatabase sqLiteDatabase = db.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("auto_start", chk);
        int upint = sqLiteDatabase.update(table_name, contentValues, "_id = ?", new String[]{id});
        sqLiteDatabase.close();
    }

    protected int getAuto_Status(String id) {
        String table_name = "time_item";
        SQLiteDatabase sqLiteDatabase = db.getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("_id", id);
        Cursor cursor = sqLiteDatabase.query(table_name, new String[]{"auto_start"}, "_id = ?", new String[]{id}, null, null, null);
        if (cursor.moveToFirst()) {
            String x = cursor.getString(cursor.getColumnIndex("auto_start"));
            return Integer.parseInt(x);
        } else {
            return 0;
        }
    }

    protected void set_No_Sound(String id, Integer chk) {
        String table_name = "time_item";
        SQLiteDatabase sqLiteDatabase = db.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("no_sound", chk);
        int upint = sqLiteDatabase.update(table_name, contentValues, "_id = ?", new String[]{id});
        sqLiteDatabase.close();
    }

    protected int get_No_Sound(String id) {
        String table_name = "time_item";
        SQLiteDatabase sqLiteDatabase = db.getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("_id", id);
        Cursor cursor = sqLiteDatabase.query(table_name, new String[]{"no_sound"}, "_id = ?", new String[]{id}, null, null, null);
        if (cursor.moveToFirst()) {
            String x = cursor.getString(cursor.getColumnIndex("no_sound"));
            return Integer.parseInt(x);
        } else {
            return 0;
        }
    }


    protected String create_item(String time, String display_name) {
        String table_name = "time_item";
        SQLiteDatabase sqLiteDatabase = db.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("itme_time", time);
        contentValues.put("last_acctime", System.currentTimeMillis());
        contentValues.put("_display_name", display_name);
        String req = String.valueOf(sqLiteDatabase.insert(table_name, null, contentValues));
        sqLiteDatabase.close();
        return req;
    }

    protected void up_last_acctime(String time) {
        String table_name = "time_item";
        SQLiteDatabase sqLiteDatabase = db.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("last_acctime", System.currentTimeMillis());
        sqLiteDatabase.update(table_name, contentValues, "itme_time = ?", new String[]{time});
        sqLiteDatabase.close();
    }

    protected void up_count(String time) {
        String table_name = "time_item";
        SQLiteDatabase sqLiteDatabase = db.getWritableDatabase();
        String sql_cmd = String.format("update %s set count = count + 1 where itme_time = %s", table_name, time);
        sqLiteDatabase.execSQL(sql_cmd);
        sqLiteDatabase.close();
    }

    protected void delete_item(String itme_time) {
        String table_name = "time_item";
        SQLiteDatabase sqLiteDatabase = db.getWritableDatabase();
        sqLiteDatabase.delete(table_name, "itme_time = ?", new String[]{itme_time});
        sqLiteDatabase.close();
    }

    protected void delete_id_item(String id) {
        String table_name = "time_item";
        SQLiteDatabase sqLiteDatabase = db.getWritableDatabase();
        sqLiteDatabase.delete(table_name, "_id = ?", new String[]{id});
        sqLiteDatabase.close();
    }

    protected void up_comment(String id, String comment) {
        String table_name = "time_item";
        SQLiteDatabase sqLiteDatabase = db.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        if (comment.isEmpty()) {
            contentValues.put("comment", context.getString(R.string.up_comment));
        } else {
            contentValues.put("comment", comment);
        }

        int x = sqLiteDatabase.update(table_name, contentValues, "_id = ?", new String[]{id});
        sqLiteDatabase.close();
    }

    protected String show_comment(String id) {
        String table_name = "time_item";
        SQLiteDatabase sqLiteDatabase = db.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.query(table_name, new String[]{"comment"}, "_id = ?", new String[]{id}, null, null, null);
        cursor.moveToFirst();
        String x = cursor.getString(cursor.getColumnIndex("comment"));
        return x;
    }

    protected Cursor show_item() {
        String table_name = "time_item";
        SQLiteDatabase sqLiteDatabase = db.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query(table_name, new String[]{"*"}, "setActive != ?", new String[]{"0"}, null, null, "last_acctime DESC");
        return cursor;
    }

    protected void up_sound_url(String id, String uri) {
        String table_name = "time_item";
        SQLiteDatabase sqLiteDatabase = db.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("sound_url", uri);
        sqLiteDatabase.update(table_name, contentValues, "_id = ?", new String[]{id});
        sqLiteDatabase.close();
    }

    protected String get_sound_url(String id) {
        String table_name = "time_item";
        SQLiteDatabase sqLiteDatabase = db.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query(table_name, new String[]{"sound_url"}, "_id = ?", new String[]{id}, null, null, null);
        cursor.moveToFirst();
        String url = cursor.getString(cursor.getColumnIndex("sound_url"));
        sqLiteDatabase.close();
        return url;
    }


    private class DB extends SQLiteOpenHelper {
        public DB(Context context) {
            super(context, db_name, null, db_ver);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String sql_cmd = "create table time_item (" +
                    "_id integer primary key autoincrement," +
                    "itme_time text unique," +
                    "_display_name text," +
                    "last_acctime integer, " +
                    "setActive integer default 1," +
                    "auto_start integer default 0," +
                    "sound_url test," +
                    "comment text default ' '," +
                    "no_sound text default 0," +
                    "select_sound text default 1," +
                    "count integer default 1)";
            db.execSQL(sql_cmd);
            String sql_time1 = "insert into time_item (itme_time, _display_name,last_acctime) values(420,420, strftime('%s','now') * 1000 + 3)";
            String sql_time2 = "insert into time_item (itme_time, _display_name,last_acctime) values(300,300, strftime('%s','now') * 1000 + 2)";
            String sql_time3 = "insert into time_item (itme_time, _display_name,last_acctime) values(180,180, strftime('%s','now') * 1000 + 1)";
            db.execSQL(sql_time1);
            db.execSQL(sql_time2);
            db.execSQL(sql_time3);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}

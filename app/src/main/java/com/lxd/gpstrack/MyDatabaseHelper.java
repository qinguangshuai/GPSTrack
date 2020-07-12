package com.lxd.gpstrack;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * @author BJXT-LXD
 * @version 1.0
 * @date 2019/9/12 8:55
 * @description TODO
 */
public class MyDatabaseHelper extends SQLiteOpenHelper {

    public MyDatabaseHelper(Context context) {
        super(context, "TrackMap.db", null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table track(id integer primary key,name varchar(10),trackLength integer,startX float,startY float," +
               "endX float,endY float)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("create table trackLink(id integer primary key,onelinkone varchar(10),trackLinkLength float," +
                "startX float,startY float,endX float,endY float)");
    }
}

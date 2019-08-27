package com.hyphenate.easeui.sqlite;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.concurrent.atomic.AtomicInteger;

@SuppressLint("NewApi")
public class ConversationSqlite extends SQLiteOpenHelper {

    private static ConversationSqlite mHelper = null;
    private AtomicInteger mOpenCounter = new AtomicInteger();
    private SQLiteDatabase mDatabase = null;

    public static ConversationSqlite getInstance(Context context) {
        if (mHelper == null) {
            synchronized (ConversationSqlite.class) {
                if (mHelper == null) {
                    ConversationSqlite temp = new ConversationSqlite(context);
                    mHelper = temp;
                }
            }
        }

        return mHelper;
    }

    public synchronized SQLiteDatabase openDatabase() {
        if (mOpenCounter.incrementAndGet() == 1) {
            // Opening new database   
            mDatabase = getWritableDatabase();
        }
        return mDatabase;
    }

    public synchronized void closeDatabase() {
        if (mOpenCounter.decrementAndGet() == 0) {
            // Closing database   
            mDatabase.close();

        }
    }

    public ConversationSqlite(Context context) {
        super(context, "Conversation", null, 1);

    }

    public ConversationSqlite(Context context, String name,
                               CursorFactory factory, int version,
                               DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
        // TODO Auto-generated constructor stub
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table conversation( id Integer primary key autoincrement,uid varchar(100),name varchar(100),headsmall varchar(100),phone varchar(100) not null,remark varchar(100))";
        String mysql = "create table person( id Integer primary key autoincrement,uid varchar(100),name varchar(100),headsmall varchar(100),phone varchar(100) not null,remark varchar(100))";
        String strange = "create table stranger( id Integer primary key autoincrement,uid varchar(100),name varchar(100),headsmall varchar(100),phone varchar(100) not null,remark varchar(100))";
        String renmai = "create table renmai( id Integer primary key autoincrement,uid varchar(100),name varchar(100),headsmall varchar(100),phone varchar(100) not null,remark varchar(100))";
        String group = "create table groupList( id Integer primary key autoincrement,uid varchar(100),name varchar(100),headsmall varchar(100),is_show_name varchar(100),chatType varchar(100))";
        db.execSQL(sql);
        db.execSQL(mysql);
        db.execSQL(strange);
        db.execSQL(renmai);
        db.execSQL(group);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub

    }

}

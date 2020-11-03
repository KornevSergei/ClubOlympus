package com.example.clubolympus.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

//создаём базу данных
public class OlympusDBOpenHelper extends SQLiteOpenHelper {
    public OlympusDBOpenHelper(Context context) {
        super(context, ClubOlympusContract.DATABASE_NAME, null, ClubOlympusContract.DATABASE_VERSION);
    }

    //создаём таблицу
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_MEMBERS_TABLE = "CREATE TABLE " + ClubOlympusContract.DATABASE_NAME + "("
                + ClubOlympusContract.MemberEntry._ID + " INTEGER PRIMARY KEY,"
                + ClubOlympusContract.MemberEntry.COLUMN_FIRST_NAME + " TEXT,"
                + ClubOlympusContract.MemberEntry.COLUMN_LAST_NAME + " TEXT,"
                + ClubOlympusContract.MemberEntry.COLUMN_GENDER + " INTEGER NOT NULL,"
                + ClubOlympusContract.MemberEntry.COLUMN_SPORT + " TEXT" + ")";
        db.execSQL(CREATE_MEMBERS_TABLE);


    }

    //возможность работать с базой данных
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ClubOlympusContract.DATABASE_NAME);
        onCreate(db);

    }
}

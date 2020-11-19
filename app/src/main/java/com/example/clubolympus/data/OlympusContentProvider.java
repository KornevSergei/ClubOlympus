package com.example.clubolympus.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.clubolympus.MainActivity;

//класс для передачин информации в базу данных, КРУД запросы к ней
//Uri = доступ к какому то элементу или элементам
public class OlympusContentProvider extends ContentProvider {

    OlympusDBOpenHelper dbOpenHelper;

    private static final int MEMBERS = 111;
    private static final int MEMBER_ID = 222;

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);


    static {

        uriMatcher.addURI(ClubOlympusContract.AUTHORITY, ClubOlympusContract.PATH_MEMBERS, MEMBERS);
        uriMatcher.addURI(ClubOlympusContract.AUTHORITY, ClubOlympusContract.PATH_MEMBERS + "/#", MEMBER_ID);
    }


    @Override
    public boolean onCreate() {
        dbOpenHelper = new OlympusDBOpenHelper(getContext());
        return true;
    }


    //метод запроса с вводимыми параметрами
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        //считываем данные
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        Cursor cursor;

        //обращаемся к юрай и возвращаем его метод
        int match = uriMatcher.match(uri);

        //в зависимости что введено возвращаем резульат
        switch (match) {
            case MEMBERS:
                cursor = db.query(ClubOlympusContract.MemberEntry.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;

            case MEMBER_ID:
                selection = ClubOlympusContract.MemberEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(ClubOlympusContract.MemberEntry.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;

            default:
                throw new IllegalArgumentException("Ошибка " + uri);

        }
        return cursor;
    }

    //метод для добавления в базу данных
    @Override
    public Uri insert(Uri uri, ContentValues values) {

        //делаем проверку на валидность
        String firstName = values.getAsString(ClubOlympusContract.MemberEntry.COLUMN_FIRST_NAME);
        if (firstName == null) {
            throw new IllegalArgumentException("Введите имя!");
        }

        String lastName = values.getAsString(ClubOlympusContract.MemberEntry.COLUMN_LAST_NAME);
        if (lastName == null) {
            throw new IllegalArgumentException("Введите фамилию!");
        }

        Integer gender = values.getAsInteger(ClubOlympusContract.MemberEntry.COLUMN_GENDER);
        if (gender == null || !(gender == ClubOlympusContract.MemberEntry.GENDER_UNKNOWN || gender ==
                ClubOlympusContract.MemberEntry.GENDER_MALE || gender == ClubOlympusContract.MemberEntry.GENDER_FEMALE)) {
            throw new IllegalArgumentException("Введите пол!");
        }

        String sport = values.getAsString(ClubOlympusContract.MemberEntry.COLUMN_SPORT);
        if (sport == null) {
            throw new IllegalArgumentException("Введите спорт!");
        }


        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        int match = uriMatcher.match(uri);

        //в зависимости что введено возвращаем резульат
        switch (match) {
            case MEMBERS:
                //вставляем строку в базу данных
                long id = db.insert(ClubOlympusContract.MemberEntry.TABLE_NAME, null, values);
                //роверяем на заполненность, если не ок - выдаем -1 и не вставляем возвращая нул
                if (id == -1) {
                    //выводим в лог
                    Log.e("insertMethod", "Вставка данных в таблицу не получилось" + uri);
                    return null;
                }
                return ContentUris.withAppendedId(uri, id);


            default:
                throw new IllegalArgumentException("Вставка данных в таблицу не получилось" + uri);

        }

    }


    //метод для удаления элементов
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        //даем возможность вписывать новые данные
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();


        //обращаемся к юрай и возвращаем его метод
        int match = uriMatcher.match(uri);
        //в зависимости что введено возвращаем резульат
        switch (match) {
            case MEMBERS:

                return db.delete(ClubOlympusContract.MemberEntry.TABLE_NAME, selection, selectionArgs);

            case MEMBER_ID:
                selection = ClubOlympusContract.MemberEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return db.delete(ClubOlympusContract.MemberEntry.TABLE_NAME, selection, selectionArgs);

            default:
                throw new IllegalArgumentException("Удалено " + uri);

        }
    }


    //метод изменеия данных
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        //делаем проверку на валидность и доп проверку
        if (values.containsKey(ClubOlympusContract.MemberEntry.COLUMN_FIRST_NAME)) {

            String firstName = values.getAsString(ClubOlympusContract.MemberEntry.COLUMN_FIRST_NAME);
            if (firstName == null) {
                throw new IllegalArgumentException("Введите имя!");
            }
        }

        if (values.containsKey(ClubOlympusContract.MemberEntry.COLUMN_LAST_NAME)) {
            String lastName = values.getAsString(ClubOlympusContract.MemberEntry.COLUMN_LAST_NAME);
            if (lastName == null) {
                throw new IllegalArgumentException("Введите фамилию!");
            }
        }

        if (values.containsKey(ClubOlympusContract.MemberEntry.COLUMN_GENDER)) {
            Integer gender = values.getAsInteger(ClubOlympusContract.MemberEntry.COLUMN_GENDER);
            if (gender == null || !(gender == ClubOlympusContract.MemberEntry.GENDER_UNKNOWN || gender ==
                    ClubOlympusContract.MemberEntry.GENDER_MALE || gender == ClubOlympusContract.MemberEntry.GENDER_FEMALE)) {
                throw new IllegalArgumentException("Введите пол!");
            }
        }

        if (values.containsKey(ClubOlympusContract.MemberEntry.COLUMN_SPORT)) {
            String sport = values.getAsString(ClubOlympusContract.MemberEntry.COLUMN_SPORT);
            if (sport == null) {
                throw new IllegalArgumentException("Введите спорт!");
            }
        }


        //даем возможность вписывать новые данные
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();


        //обращаемся к юрай и возвращаем его метод
        int match = uriMatcher.match(uri);
        //в зависимости что введено возвращаем резульат
        switch (match) {
            case MEMBERS:

                return db.update(ClubOlympusContract.MemberEntry.TABLE_NAME, values, selection, selectionArgs);

            case MEMBER_ID:
                selection = ClubOlympusContract.MemberEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return db.update(ClubOlympusContract.MemberEntry.TABLE_NAME, values, selection, selectionArgs);

            default:
                throw new IllegalArgumentException("Изменено " + uri);

        }
    }


    //метод
    @Override
    public String getType(Uri uri) {

        //обращаемся к юрай и возвращаем его метод
        int match = uriMatcher.match(uri);
        //в зависимости что введено возвращаем резульат
        switch (match) {
            case MEMBERS:

                return ClubOlympusContract.MemberEntry.CONTENT_MULTIPLE_ITEMS;

            case MEMBER_ID:
                return ClubOlympusContract.MemberEntry.CONTENT_SINGLE_ITEM;

            default:
                throw new IllegalArgumentException("Неизвестный URI " + uri);

        }
    }


}

package com.example.clubolympus.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
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
                Toast.makeText(getContext(), "Неверный URI", Toast.LENGTH_LONG).show();
                throw new IllegalArgumentException("Ошибка " + uri);

        }
        return cursor;
    }


    @Override
    public Uri insert(Uri uri, ContentValues values) {

        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        int match = uriMatcher.match(uri);

        //в зависимости что введено возвращаем резульат
        switch (match) {
            case MEMBERS:
                //вставляем строку в базу данных
                long id = db.insert(ClubOlympusContract.MemberEntry.TABLE_NAME, null, values);
                //роверяем на заполненность, если не ок - выдаем -1 и не вставляем возвращая нул
                if (id == - 1){
                    //выводим в лог
                    Log.e("insertMethod", "Вставка данных в таблицу не получилось" + uri);
                    return null;
                }
                return ContentUris.withAppendedId(uri, id);


            default:
                throw new IllegalArgumentException("Вставка данных в таблицу не получилось" + uri);

        }

    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }


}

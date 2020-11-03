package com.example.clubolympus.data;

import android.net.Uri;
import android.provider.BaseColumns;

//класс для хранения информации ез возможности наследования
public final class ClubOlympusContract {

    private ClubOlympusContract() {

    }

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "olympus";

    public static final String SCHEME = "content://";
    public static final String AUTHORITY = "com.example.clubolympus.data";
    public static final String PATH_MEMBERS = "members";

    //оздаём константы для базы данных
    public static final Uri BASE_CONTENT_URI =
            Uri.parse(SCHEME + AUTHORITY);



    //информация о членах клуба, создаём константы что бы не ощибиться в будущем, имплементируемся для того что бы не перечеслять айди постоянно
    public static final class MemberEntry implements BaseColumns {

        public static final String TABLE_NAME = "members";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_FIRST_NAME = "firstName";
        public static final String COLUMN_LAST_NAME = "lastName";
        public static final String COLUMN_GENDER = "gender";
        public static final String COLUMN_SPORT = "sport";

        public static final int GENDER_UNKNOWN = 0;
        public static final int GENDER_MALE = 1;
        public static final int GENDER_FEMALE = 2;

        public static final Uri CONTENT_URI =
                Uri.withAppendedPath(BASE_CONTENT_URI, PATH_MEMBERS);
    }

}

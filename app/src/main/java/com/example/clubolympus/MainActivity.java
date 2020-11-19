package com.example.clubolympus;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.example.clubolympus.data.ClubOlympusContract;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    //онстанта для идентификации
    private static final int MEMBER_LOADER = 123;
    //адаптер для лист вью
    MemberCursorAdapter memberCursorAdapter;


    //переменная для отображания
    ListView dataListView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //связываем
        dataListView = findViewById(R.id.dataListView);

        //связываем кнопку
        FloatingActionButton floatingActionButton = findViewById(R.id.floatingActionButton);
        //создаём слушатель
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //запускаем новую активити по клику
                Intent intent = new Intent(MainActivity.this, AddMemberActivity.class);
                startActivity(intent);
            }
        });


        memberCursorAdapter = new MemberCursorAdapter(this,null,false);
        dataListView.setAdapter(memberCursorAdapter);

        getSupportLoaderManager().initLoader(MEMBER_LOADER,null,this);
    }


    //метод для вспомогательного потока
    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {


        String[] projection = {
                ClubOlympusContract.MemberEntry._ID,
                ClubOlympusContract.MemberEntry.COLUMN_FIRST_NAME,
                ClubOlympusContract.MemberEntry.COLUMN_LAST_NAME,
                ClubOlympusContract.MemberEntry.COLUMN_SPORT,
        };

        //делаем запрос к базе данных
        CursorLoader cursorLoader = new CursorLoader(this,
                ClubOlympusContract.MemberEntry.CONTENT_URI,
                projection, null, null, null
        );

        return cursorLoader;
    }

    //метод принимает параметры и отображает
    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {

        memberCursorAdapter.swapCursor(cursor);

    }

    //метод для удаления неправельные запросов
    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

        memberCursorAdapter.swapCursor(null);

    }
}
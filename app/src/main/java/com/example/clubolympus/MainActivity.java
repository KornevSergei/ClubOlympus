package com.example.clubolympus;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.clubolympus.data.ClubOlympusContract;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    //переменная для отображания
    TextView dataTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //связываем
        dataTextView = findViewById(R.id.dataTextView);

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
    }

    //запускаем метод прис тарте приложения
    @Override
    protected void onStart() {
        super.onStart();
        displayData();
    }

    //показываем данные
    private void displayData() {

        String[] projection = {
                ClubOlympusContract.MemberEntry._ID,
                ClubOlympusContract.MemberEntry.COLUMN_FIRST_NAME,
                ClubOlympusContract.MemberEntry.COLUMN_LAST_NAME,
                ClubOlympusContract.MemberEntry.COLUMN_GENDER,
                ClubOlympusContract.MemberEntry.COLUMN_SPORT,
        };
        //делаем запрос к базе данных
        Cursor cursor = getContentResolver().query(
                ClubOlympusContract.MemberEntry.CONTENT_URI,
                projection, null, null, null
        );

        //используем данные из обьекта
        dataTextView.setText("Все члены\n\n");
        dataTextView.append(ClubOlympusContract.MemberEntry._ID + " " +
                ClubOlympusContract.MemberEntry.COLUMN_FIRST_NAME + " " +
                ClubOlympusContract.MemberEntry.COLUMN_LAST_NAME + " " +
                ClubOlympusContract.MemberEntry.COLUMN_GENDER + " " +
                ClubOlympusContract.MemberEntry.COLUMN_SPORT);

        //олучаем индекс каждой колонки и сохраняем
        int idColumnIndex = cursor.getColumnIndex(ClubOlympusContract.MemberEntry._ID);
        int firstNameColumnIndex = cursor.getColumnIndex(ClubOlympusContract.MemberEntry.COLUMN_FIRST_NAME);
        int lastNameColumnIndex = cursor.getColumnIndex(ClubOlympusContract.MemberEntry.COLUMN_LAST_NAME);
        int genderColumnIndex = cursor.getColumnIndex(ClubOlympusContract.MemberEntry.COLUMN_GENDER);
        int sportColumnIndex = cursor.getColumnIndex(ClubOlympusContract.MemberEntry.COLUMN_SPORT);

        //ереераем все данные курсора
        while (cursor.moveToNext()){
            int currentId = cursor.getInt(idColumnIndex);
            String currentFirstName = cursor.getString(firstNameColumnIndex);
            String currentLastName = cursor.getString(lastNameColumnIndex);
            int currentGender = cursor.getInt(genderColumnIndex);
            String currentSport = cursor.getString(sportColumnIndex);

            //отображаем данные
            dataTextView.append("\n" +
                    currentId + " " +
                    currentFirstName + " " +
                    currentLastName + " " +
                    currentGender + " " +
                    currentSport + " "
                    );
        }
        //закрываем запрос
        cursor.close();

    }

}
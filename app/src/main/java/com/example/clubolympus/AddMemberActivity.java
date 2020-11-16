package com.example.clubolympus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.clubolympus.data.ClubOlympusContract;

import java.util.ArrayList;

public class AddMemberActivity extends AppCompatActivity {

    //переменные для связываения элементов разметки
    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText sportEditText;
    private Spinner genderSpinner;
    //переменная для полня пола, если 0 неизвестиен, если 1 мужской, 2 - женский
    private int gender = 0;
    //адаптер длоя определения выбора жлемента спинер
    private ArrayAdapter spinnerAdapter;
    //лист динамический в который будем помещать адаптер
//    private ArrayList spinnerArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_member);

        //связываем с разметкой
        firstNameEditText = findViewById(R.id.firstNameEditText);
        lastNameEditText = findViewById(R.id.lastNameEditText);
        sportEditText = findViewById(R.id.sportEditText);
        genderSpinner = findViewById(R.id.genderSpinner);

//        //для динамического добавления используем этот код
//        //добавляем строки в лист из спинера
//        spinnerArrayList = new ArrayList();
//        spinnerArrayList.add("Неизвестно");
//        spinnerArrayList.add("Мужской");
//        spinnerArrayList.add("Женский");
//        //присваиваем спинеру адаптер
//        spinnerAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, spinnerArrayList);


        //для статического метода используем этот код
        spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.array_gender, android.R.layout.simple_spinner_item);
        //доавляем вид каждого элемента спинера
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //устанавливаем спинеру адаптер
        genderSpinner.setAdapter(spinnerAdapter);

        //устанавливаем логику считывания хначения спинера
        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            //когда выбран какой либо элемент
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedGender = (String) parent.getItemAtPosition(position);
                //проверяем на пустоту,если не пустая то тогда выполняем присваивание из отдельного класса
                if (!TextUtils.isEmpty(selectedGender)) {
                    if (selectedGender.equals("Мужской")) {
                        gender = ClubOlympusContract.MemberEntry.GENDER_MALE;
                    } else if (selectedGender.equals("Женский")) {
                        gender = ClubOlympusContract.MemberEntry.GENDER_FEMALE;
                    } else {
                        gender = ClubOlympusContract.MemberEntry.GENDER_UNKNOWN;
                    }
                }
            }

            //когда никакой элемент не выбран
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //если пусто выбираем по умолчанию значение из отдельного класса
                gender = ClubOlympusContract.MemberEntry.GENDER_UNKNOWN;
            }
        });
    }


    //метод для действия меню
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_member_menu, menu);
        return true;
    }

    //метод для переключения
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_member:
                //ызываем метод при нажатии на кнопку сохранить
                insertMember();
                return true;
            case R.id.delete_member:
                return true;
            case R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    //вставляем в таблицу новового члена
    private void insertMember() {
        //получаем значение столбцов, обрезаем пробелы
        String firstName = firstNameEditText.getText().toString().trim();
        String lastName = lastNameEditText.getText().toString().trim();
        String sport = sportEditText.getText().toString().trim();

        //создаем обьект и помещаем в него контекнт
        ContentValues contentValues = new ContentValues();
        contentValues.put(ClubOlympusContract.MemberEntry.COLUMN_FIRST_NAME, firstName);
        contentValues.put(ClubOlympusContract.MemberEntry.COLUMN_LAST_NAME, lastName);
        contentValues.put(ClubOlympusContract.MemberEntry.COLUMN_SPORT, sport);
        contentValues.put(ClubOlympusContract.MemberEntry.COLUMN_GENDER, gender);

        //разрешаем добавление
        ContentResolver contentResolver = getContentResolver();
        Uri uri = contentResolver.insert(ClubOlympusContract.MemberEntry.CONTENT_URI, contentValues);

        //делаем проверку на наличие контента
        if (uri == null){
            Toast.makeText(this,"Вставка данных в таблицу не получилось",Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this,"Данные сохранены",Toast.LENGTH_LONG).show();
        }

    }
}
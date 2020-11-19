package com.example.clubolympus;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.clubolympus.data.ClubOlympusContract;

import java.util.ArrayList;

public class AddMemberActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EDIT_MEMBER_LOADER = 111;
    Uri currentMemberUri;

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


        Intent intent = getIntent();
        //устанавливаем заголовок по клику на список
        currentMemberUri = intent.getData();
        if (currentMemberUri == null) {
            setTitle("Добавить члена");
            //вызываем метод что бы спрятать меню в режиме редатирования
            invalidateOptionsMenu();
        } else {
            setTitle("Редактировать члена");
            getSupportLoaderManager().initLoader(EDIT_MEMBER_LOADER, null, this);
        }

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
                saveMember();
                return true;
            case R.id.delete_member:
                showDeleteMemberDialog();
                return true;
            case R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;

        }

        return super.onOptionsItemSelected(item);
    }



    //вставляем в таблицу новового члена
    private void saveMember() {
        //получаем значение столбцов, обрезаем пробелы
        String firstName = firstNameEditText.getText().toString().trim();
        String lastName = lastNameEditText.getText().toString().trim();
        String sport = sportEditText.getText().toString().trim();

        //проверяем поля на пустоту
        if (TextUtils.isEmpty(firstName)){
            Toast.makeText(this, "Введите имя", Toast.LENGTH_LONG).show();
            return;
        } else if (TextUtils.isEmpty(lastName)){
            Toast.makeText(this, "Введите фамилию", Toast.LENGTH_LONG).show();
            return;
        } else if (TextUtils.isEmpty(sport)){
            Toast.makeText(this, "Введите спорт", Toast.LENGTH_LONG).show();
        } else if (gender == ClubOlympusContract.MemberEntry.GENDER_UNKNOWN) {
            Toast.makeText(this, "Введите пол", Toast.LENGTH_LONG).show();
        }

        //создаем обьект и помещаем в него контекнт
        ContentValues contentValues = new ContentValues();
        contentValues.put(ClubOlympusContract.MemberEntry.COLUMN_FIRST_NAME, firstName);
        contentValues.put(ClubOlympusContract.MemberEntry.COLUMN_LAST_NAME, lastName);
        contentValues.put(ClubOlympusContract.MemberEntry.COLUMN_SPORT, sport);
        contentValues.put(ClubOlympusContract.MemberEntry.COLUMN_GENDER, gender);


        //проверяем на наличие уже сохранненого члена при сохранении о выполяем код
        if (currentMemberUri == null){
            //разрешаем добавление
            ContentResolver contentResolver = getContentResolver();
            Uri uri = contentResolver.insert(ClubOlympusContract.MemberEntry.CONTENT_URI, contentValues);

            //делаем проверку на наличие контента
            if (uri == null) {
                Toast.makeText(this, "Вставка данных в таблицу не получилось", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Данные сохранены", Toast.LENGTH_LONG).show();
            }

        } else {
            int rowsChanged = getContentResolver().update(currentMemberUri, contentValues,null,null);

            if (rowsChanged == 0){
                Toast.makeText(this, "охранение данных в таблицу не получилось", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Данные обновленны", Toast.LENGTH_LONG).show();
            }

        }

    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {

        String[] projection = {
                ClubOlympusContract.MemberEntry._ID,
                ClubOlympusContract.MemberEntry.COLUMN_FIRST_NAME,
                ClubOlympusContract.MemberEntry.COLUMN_LAST_NAME,
                ClubOlympusContract.MemberEntry.COLUMN_GENDER,
                ClubOlympusContract.MemberEntry.COLUMN_SPORT
        };
        return new CursorLoader(this, currentMemberUri,
                projection, null, null, null
        );
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        if (cursor.moveToFirst()) {
            //получаем индексы столбцов
            int firstNameColumnIndex = cursor.getColumnIndex(
                    ClubOlympusContract.MemberEntry.COLUMN_FIRST_NAME
            );
            int lastNameColumnIndex = cursor.getColumnIndex(
                    ClubOlympusContract.MemberEntry.COLUMN_LAST_NAME
            );
            int genderColumnIndex = cursor.getColumnIndex(
                    ClubOlympusContract.MemberEntry.COLUMN_GENDER
            );
            int sportColumnIndex = cursor.getColumnIndex(
                    ClubOlympusContract.MemberEntry.COLUMN_SPORT
            );

            String firstName = cursor.getString(firstNameColumnIndex);
            String lastName = cursor.getString(lastNameColumnIndex);
            int gender = cursor.getInt(genderColumnIndex);
            String sport = cursor.getString(sportColumnIndex);


            //устанавливаем текст в эдиттекст
            firstNameEditText.setText(firstName);
            lastNameEditText.setText(lastName);
            sportEditText.setText(sport);

            switch (gender) {
                case ClubOlympusContract.MemberEntry.GENDER_MALE:
                    genderSpinner.setSelection(1);
                    break;
                case ClubOlympusContract.MemberEntry.GENDER_FEMALE:
                    genderSpinner.setSelection(2);
                    break;
                case ClubOlympusContract.MemberEntry.GENDER_UNKNOWN:
                    genderSpinner.setSelection(0);
                    break;
            }
        }

    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }



    //даём возможность удалять в окне редактирования
    private void showDeleteMemberDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Хотите ли вы удалить пользователся?");
        builder.setPositiveButton("Удалить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteMember();
            }
        });
        builder.setNegativeButton("Отменить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              if (dialog != null){
                  dialog.dismiss();
              }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    private void deleteMember(){
        if (currentMemberUri !=null){
            int rowsDeleted = getContentResolver().delete(currentMemberUri,null,null);
            if (rowsDeleted == 0){
                Toast.makeText(this, "Удаление не произошло", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "лен удален", Toast.LENGTH_LONG).show();
            }
            finish();
        }
    }


    //метод для скрытия меню
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        if (currentMemberUri == null){
            MenuItem menuItem = menu.findItem(R.id.delete_member);
            menuItem.setVisible(false);
        }

        return true;
    }
}
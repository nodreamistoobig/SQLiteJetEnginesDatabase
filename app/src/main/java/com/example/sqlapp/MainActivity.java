package com.example.sqlapp;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    SQLiteDatabase DB;
    Button buttonId;
    Button buttonEnter;
    Button buttonName;
    Button buttonProducer;
    Button buttonThrust;
    //Направления сортировки по колонкам
    Boolean nameASC;  //TODO: можно заменить эти 4 переменные на HashMap<String, Boolean>
    Boolean producerASC;
    Boolean IDASC;
    Boolean thrustASC;

    Boolean editMode;

    EditText newEntryNameField; //TODO:длинное название, нечитабельно, можно сократить до newName (то же касается newEntryProducerField и newEntryThrustField)
    EditText newEntryProducerField;
    EditText newEntryThrustField;
    SimpleCursorAdapter adapter;
    TextView numberOfItems;
    ListView LV;
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //TODO: я бы функциональность БД вынесла в отдельный класс, так не будет длинных запросов в логике текущей активности
        DB = openOrCreateDatabase("AircraftEngines", MODE_PRIVATE, null);
        DB.execSQL("CREATE TABLE IF NOT EXISTS Engines(_id int, Name varchar, Producer varchar, Thrust int);");

        cursor = DB.rawQuery("SELECT * FROM Engines", null);

        buttonEnter = (Button) findViewById(R.id.button); //TODO: преобразование в button лишнее - это и так кнопка, то же касается остальных преобразований (подсвечены неактивными)
        newEntryNameField = (EditText) findViewById(R.id.newEntryName);
        newEntryProducerField = (EditText) findViewById(R.id.newEntryProducer);
        newEntryThrustField = (EditText) findViewById(R.id.newEntryThrust);
        numberOfItems = (TextView) findViewById(R.id.numberOfItems);

        int[] views = {R.id.idView, R.id.nameView, R.id.producerView, R.id.thrustView};
        String columnNames[] = {"_id", "Name", "Producer", "Thrust"}; //TODO: сделать глобальной переменной, пригодится для hashmap
        adapter = new SimpleCursorAdapter(this, R.layout.dbitem, cursor, columnNames, views, 0);

        LV = (ListView) findViewById(R.id.Viewer);
        LV.setAdapter(adapter);
        registerForContextMenu(LV);
        editMode = false;
        //getCount(); //TODO: убрать комментарий или оставить эту функциональность и выводить общее количество при запуске
        buttonEnter.setOnClickListener(new View.OnClickListener() {//TODO: на кнопку и так можно повешать функцию onClick и без слушателя, это немного сократит код. То же касается и остальных кнопок
            @Override
            public void onClick(View v) {
                if (!editMode) {
                    cursor = DB.rawQuery("SELECT COUNT(*) FROM Engines", null);
                    cursor.moveToFirst();
                    String newEntryName = newEntryNameField.getText().toString();  //TODO: слишком длинное имя
                    String newEntryProducer = newEntryProducerField.getText().toString(); //TODO: объявление строковых переменных перенести до условия. Его можно испоользовать в двух вариантах
                    String newEntryThrust = newEntryThrustField.getText().toString();
                    DB.execSQL("INSERT INTO Engines  VALUES(" + (cursor.getInt(0) + 1) +
                            " , '" + newEntryName + "' , '" + newEntryProducer + "', " + newEntryThrust + " );");
                    getCount(); //TODO: функция getCount включает строки 69-70, их нужно убрать
                    cursor = DB.rawQuery("SELECT * FROM Engines", null);
                    adapter.notifyDataSetChanged();  //TODO: переместить повторяющийся код (строки 77-79 и 86-88) за пределы условия
                    adapter.changeCursor(cursor);
                    //TODO: после добавления в БД необходимо очищать поля, иначе можно подумать, что эта строка еще редактируется. При добавлении нового приходится все удалять
                } else {
                    Log.d("D", cursor.getInt(0) + "");
                    DB.execSQL("UPDATE Engines SET Name = '" + newEntryNameField.getText().toString() + "' , Producer = '"
                            + newEntryProducerField.getText().toString() + "', Thrust =" + newEntryThrustField.getText().toString()
                            + " WHERE _id = " + cursor.getInt(0));
                    editMode = false;
                    cursor = DB.rawQuery("SELECT * FROM Engines", null);
                    adapter.notifyDataSetChanged();
                    adapter.changeCursor(cursor);
                }
            }
        });
        buttonName = (Button) findViewById(R.id.buttonName);
        nameASC = true;
        buttonName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //TODO: много повторяющегося кода с добавлением слушателя, который при клике выполняет то же самое. Лучше создать общую функцию
                if (nameASC) {
                    sortByColumn("Name", "ASC");
                    nameASC = false;
                } else {
                    sortByColumn("Name", "DESC");
                    nameASC = true;
                }
            }
        });
        buttonProducer = (Button) findViewById(R.id.buttonProducer);
        producerASC = true;
        buttonProducer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (producerASC) {
                    sortByColumn("Producer", "ASC");
                    producerASC = false;
                } else {
                    sortByColumn("Producer", "DESC");
                    producerASC = true;
                }
            }
        });
        buttonId = (Button) findViewById(R.id.buttonId);
        IDASC = true;
        buttonId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (IDASC) {
                    sortByColumn("_id", "ASC");
                    IDASC = false;
                } else {
                    sortByColumn("_id", "DESC");
                    IDASC = true;
                }
            }
        });
        buttonThrust = (Button) findViewById(R.id.buttonThrust);
        thrustASC = true;
        buttonThrust.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (thrustASC) {
                    sortByColumn("Thrust", "ASC");
                    thrustASC = false;
                } else {
                    sortByColumn("Thrust", "DESC");
                    thrustASC = true;
                }
            }
        });
        Log.d("A", cursor.getColumnNames()[0]);
        LV.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                editMode = true;
                cursor.moveToPosition(position);
                newEntryNameField.setText(cursor.getString(1));
                newEntryProducerField.setText(cursor.getString(2));
            }
        });
    }

    /**
     * Сортировка таблицы по заданной колонке в заданном порядке.
     *
     * @param column    Колонка, по которой сортируется таблица.
     * @param direction "ASC" - по возростанию, "DESC" - по убыванию.
     */
    //TODO: вынести эти 2 функции тоже в класс БД
    public void sortByColumn(String column, String direction) {
        cursor = DB.rawQuery("SELECT * FROM Engines ORDER BY " + column + " " + direction, null);
        adapter.changeCursor(cursor);
        adapter.notifyDataSetChanged();
    }

    public void getCount() {
        cursor = DB.rawQuery("SELECT COUNT(*) FROM Engines", null);
        cursor.moveToFirst();
        numberOfItems.setText("Number of items: " + (cursor.getInt(0))); //TODO: поместить повторяющуюся строку в ресурсы
    }
}
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
    Boolean nameASC;
    Boolean producerASC;
    Boolean IDASC;
    Boolean thrustASC;

    Boolean editMode;

    EditText newEntryNameField;
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
        DB = openOrCreateDatabase("AircraftEngines", MODE_PRIVATE, null);
        DB.execSQL("CREATE TABLE IF NOT EXISTS Engines(_id int, Name varchar, Producer varchar, Thrust int);");

        cursor = DB.rawQuery("SELECT * FROM Engines", null);

        buttonEnter = (Button) findViewById(R.id.button);
        newEntryNameField = (EditText) findViewById(R.id.newEntryName);
        newEntryProducerField = (EditText) findViewById(R.id.newEntryProducer);
        newEntryThrustField = (EditText) findViewById(R.id.newEntryThrust);
        numberOfItems = (TextView) findViewById(R.id.numberOfItems);

        int[] views = {R.id.idView, R.id.nameView, R.id.producerView, R.id.thrustView};
        String columnNames[] = {"_id", "Name", "Producer", "Thrust"};
        adapter = new SimpleCursorAdapter(this, R.layout.dbitem, cursor, columnNames, views, 0);

        LV = (ListView) findViewById(R.id.Viewer);
        LV.setAdapter(adapter);
        registerForContextMenu(LV);
        editMode = false;
        //getCount();
        buttonEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editMode) {
                    cursor = DB.rawQuery("SELECT COUNT(*) FROM Engines", null);
                    cursor.moveToFirst();
                    String newEntryName = newEntryNameField.getText().toString();
                    String newEntryProducer = newEntryProducerField.getText().toString();
                    String newEntryThrust = newEntryThrustField.getText().toString();
                    DB.execSQL("INSERT INTO Engines  VALUES(" + (cursor.getInt(0) + 1) +
                            " , '" + newEntryName + "' , '" + newEntryProducer + "', " + newEntryThrust + " );");
                    getCount();
                    cursor = DB.rawQuery("SELECT * FROM Engines", null);
                    adapter.notifyDataSetChanged();
                    adapter.changeCursor(cursor);
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
            public void onClick(View v) {
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
    public void sortByColumn(String column, String direction) {
        cursor = DB.rawQuery("SELECT * FROM Engines ORDER BY " + column + " " + direction, null);
        adapter.changeCursor(cursor);
        adapter.notifyDataSetChanged();
    }

    public void getCount() {
        cursor = DB.rawQuery("SELECT COUNT(*) FROM Engines", null);
        cursor.moveToFirst();
        numberOfItems.setText("Number of items: " + (cursor.getInt(0)));
    }
}
package com.example.sqlapp;

import androidx.appcompat.app.AppCompatActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    DB db;
    Button buttonId, buttonEnter, buttonName, buttonProducer, buttonThrust;

    HashMap<String, String> ascSort; //Направления сортировки по колонкам
    String[] fields;
    Boolean editMode;

    EditText newName, newProducer, newThrust;
    SimpleCursorAdapter adapter;
    TextView numberOfItems;
    ListView LV;
    Cursor cursor;
    int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = new DB();
        db.sqlDB = openOrCreateDatabase("AircraftEngines", MODE_PRIVATE, null);
        db.create();

        ascSort = new HashMap<>();
        fields = new String[]{"_id", "Name", "Producer", "Thrust"};
        for (String s: fields)
            ascSort.put(s, "ASC");

        buttonEnter = findViewById(R.id.button);
        newName = findViewById(R.id.newEntryName);
        newProducer = findViewById(R.id.newEntryProducer);
        newThrust = findViewById(R.id.newEntryThrust);
        numberOfItems = findViewById(R.id.numberOfItems);

        cursor = db.selectAll();
        int[] views = {R.id.idView, R.id.nameView, R.id.producerView, R.id.thrustView};
        adapter = new SimpleCursorAdapter(this, R.layout.dbitem, cursor, fields, views, 0);

        editMode = false;
        LV = findViewById(R.id.Viewer);
        LV.setAdapter(adapter);
        registerForContextMenu(LV);

        LV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                editMode = true;
                cursor.moveToPosition(position);
                newName.setText(cursor.getString(1));
                newProducer.setText(cursor.getString(2));
            }
        });

        buttonName = findViewById(R.id.buttonName);
        buttonName.setTag("Name");
        buttonProducer = findViewById(R.id.buttonProducer);
        buttonProducer.setTag("Producer");
        buttonId = findViewById(R.id.buttonId);
        buttonId.setTag("_id");
        buttonThrust = findViewById(R.id.buttonThrust);
        buttonThrust.setTag("Thrust");

        count = db.getCount();
        numberOfItems.setText(getString(R.string.number_of_items) + count);
    }

    public void onEnter(View v) {
        String name = newName.getText().toString();
        String producer = newProducer.getText().toString();
        String thrust = newThrust.getText().toString();

        if (!editMode) {
            db.insert(count+1, name, producer, thrust);
            numberOfItems.setText(getString(R.string.number_of_items) + (count+1));
        } else {
            db.update(cursor.getInt(0), name, producer, thrust);
            editMode = false;
        }
        newName.setText(""); newProducer.setText(""); newThrust.setText("");
        cursor = db.selectAll();
        adapter.notifyDataSetChanged();
        adapter.changeCursor(cursor);
    }

    public void onClick(View v){
        String column = v.getTag().toString();
        String direction = ascSort.get(column);
        cursor = db.sortByColumn(column, direction);
        adapter.changeCursor(cursor);
        adapter.notifyDataSetChanged();
    }
}
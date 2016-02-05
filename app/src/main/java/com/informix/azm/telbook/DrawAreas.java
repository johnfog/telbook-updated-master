package com.informix.azm.telbook;

import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;

/**
 * Created by adm on 02.02.2016.
 */
public class DrawAreas {
    DBHelper dbHelper;
    SQLiteDatabase database;

    public DrawAreas(SQLiteDatabase database) {
        this.database = database;
    }

    ArrayList<String> areaName = new ArrayList<String>();
    ArrayList<Integer> areaIdS= new ArrayList<Integer>();


    public void DrawArea(Spinner spinner) {

        String[][] areas;
        try
        {
            dbHelper = new DBHelper(spinner.getContext());
            areas = dbHelper.areaGetter(database);
            for (int i = 0; i < (areas[0].length); i++) {
                areaName.add(areas[0][i]);
                areaIdS.add(Integer.parseInt(areas[1][i]));
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(spinner.getContext(),
                    android.R.layout.simple_list_item_1, areaName);
            spinner.setAdapter(adapter);

        } catch (Exception e){}
    }

}
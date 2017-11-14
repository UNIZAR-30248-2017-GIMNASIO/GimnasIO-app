package com.patan.gimnasio;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

// En esta actividad se mostrara una lista con todos los ejercicios, ademas de una barra de busqueda para poder filtrar
public class ExerciseListActivity extends AppCompatActivity {

    private ListView l;
    private GymnasioDBAdapter db;
    private static final int ADD_ID = 1;
    private String mode_in;

    public GymnasioDBAdapter getGymnasioDbAdapter() {
        return db;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercises_list);
        db = new GymnasioDBAdapter(this);
        db.open();


        l = (ListView)findViewById(R.id.dbExercisesList);

        l.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Intent intent = new Intent(v.getContext(), ExerciseViewActivity.class);
                intent.putExtra("ID",id);
                startActivity(intent);
            }
        });

        fillData();
        registerForContextMenu(l);
        Intent intent = getIntent();
        mode_in = intent.getStringExtra("MODE");
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (mode_in.equals("routine")) {
            super.onCreateContextMenu(menu,v,menuInfo);
            menu.add(Menu.NONE, ADD_ID, Menu.NONE, R.string.menu_add);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == ADD_ID) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            Intent i = new Intent();
            i.putExtra("ID", info.id);
            setResult(RESULT_OK,i);
            finish();   // Forzamos volver a la actividad anterior
            return true;
        } else {
            return false;
        }
    }

    private void fillData() {
        // Get all of the exercises from the database and create the item list
        Cursor exercises = db.fetchExercises();
        startManagingCursor(exercises);
        // Create an array to specify the fields we want to display in the list (only NAME)
        String[] from = new String[] {GymnasioDBAdapter.KEY_EX_NAME,GymnasioDBAdapter.KEY_EX_TAG};
        // and an array of the fields we want to bind those fields to (in this case just text1)
        int[] to = new int[] { R.id.ex_row,R.id.ex_row2};
        // Now create an array adapter and set it to display using our row
        SimpleCursorAdapter notes =
                new SimpleCursorAdapter(this, R.layout.exercises_row, exercises, from, to,0);
        l.setAdapter(notes);
    }

}

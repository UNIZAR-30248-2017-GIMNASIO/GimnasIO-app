package com.patan.gimnasio.activities;

import android.content.Intent;
import android.database.Cursor;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import com.patan.gimnasio.database.GymnasioDBAdapter;
import com.patan.gimnasio.R;
import com.patan.gimnasio.domain.Exercise;

import java.util.ArrayList;

// En esta actividad se mostrara una lista con todos los ejercicios, ademas de una barra de busqueda para poder filtrar
public class ExerciseListActivity extends AppCompatActivity {

    private ListView l;
    private GymnasioDBAdapter db;
    private Spinner spinner;
    private FloatingActionButton boton;
    private EditText busqueda;
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
        busqueda = (EditText) (findViewById(R.id.busqueda));
        spinner = (Spinner) findViewById(R.id.spinner);
        boton = (FloatingActionButton) findViewById(R.id.añadir);
        boton.setVisibility(View.INVISIBLE);
        //We declare the search options
        ArrayList<String> categories = new ArrayList<String>();
        categories.add("Nombre");
        categories.add("Musculo");
        categories.add("Tag");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
        spinner.setAdapter(dataAdapter);

        l = (ListView)findViewById(R.id.dbExercisesList);

        l.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Adapter adapter = l.getAdapter();
                Cursor item = (Cursor) adapter.getItem(position);
                int pos = item.getColumnIndex(GymnasioDBAdapter.KEY_RO_ID);
                long id_ex = item.getLong(pos);
                Intent intent = new Intent(v.getContext(), ExerciseViewActivity.class);
                intent.putExtra("ID",id_ex);
                startActivity(intent);
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                busqueda.setText("");
                fillData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        busqueda.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = spinner.getSelectedItem().toString(); //Para saber sobre que categoria se etsa buscando
                if (s.length() == 0) {
                    fillData();
                } else if (text.equals("Musculo")) {
                    fillDataByMuscle(s.toString());
                } else if (text.equals("Tag")) {
                    fillDataByTag(s.toString());
                } else if (text.equals("Nombre")) {
                    fillDataByName(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

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
            //Log.d("SAVED","Dentro de añadir ejercicios");
            //We set the button visible
            //boton.setVisibility(View.VISIBLE);
            //Quizas cambiar el layout de las columnas


            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            Adapter adapter = l.getAdapter();
            Cursor c = (Cursor) adapter.getItem(info.position);
            int pos = c.getColumnIndex(GymnasioDBAdapter.KEY_EX_ID);
            long id = c.getLong(pos);
            int name = c.getColumnIndex(GymnasioDBAdapter.KEY_EX_NAME);
            String nombre = c.getString(name);

            Intent intent = new Intent(this, AddExerciseToRoutineActivity.class);
            intent.putExtra("MODE","ADD");
            intent.putExtra("ID",id);
            intent.putExtra("NAME", nombre);
            startActivityForResult(intent,1);

            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                long id = data.getLongExtra("ID",0);  // Cogemos el ID del ejercicio añadido;
                int series = data.getIntExtra("SERIES",0); // Cogemos las series del ejercicio añadido
                int rep = data.getIntExtra("REP",0); // Cogemos las repeticiones del ejercicio añadido
                double relax = data.getDoubleExtra("RELAX",0); // Cogemos el tiempo de relax del ejercicio añadido
                String mode = data.getStringExtra("MODE");

                Intent i = new Intent();
                i.putExtra("MODE", mode);
                i.putExtra("ID",id);
                i.putExtra("SERIES",series);
                i.putExtra("REP",rep);
                i.putExtra("RELAX",relax);
                setResult(RESULT_OK,i);
                finish();       // Forzamos volver a la actividad anterior
            }
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


    private void fillDataAddExercisesToRoutine(){
        Cursor exercises = db.fetchExercises();
        startManagingCursor(exercises);
        // Create an array to specify the fields we want to display in the list (only NAME)
        String[] from = new String[] {GymnasioDBAdapter.KEY_EX_NAME,GymnasioDBAdapter.KEY_EX_TAG};
        // and an array of the fields we want to bind those fields to (in this case just text1)
        int[] to = new int[] { R.id.ex_row,R.id.ex_row2};
        // Now create an array adapter and set it to display using our row
        SimpleCursorAdapter notes =
                new SimpleCursorAdapter(this, R.layout.exercises_row_checkbox, exercises, from, to,0);
        l.setAdapter(notes);


    }


    private void fillDataByMuscle(String muscle) {
        // Get all of the exercises from the database and create the item list
        Cursor exercises = db.getExercisesByMuscle(muscle);
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

    private void fillDataByTag(String tag) {
        // Get all of the exercises from the database and create the item list
        Cursor exercises = db.getExercisesByTag(tag);
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

    private void fillDataByName(String name) {
        // Get all of the exercises from the database and create the item list
        Cursor exercises = db.getExerciseByName(name);
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

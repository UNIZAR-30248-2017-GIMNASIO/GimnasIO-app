package com.patan.gimnasio.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.patan.gimnasio.domain.CustomAdapterExercise;
import com.patan.gimnasio.domain.ExFromRoutine;
import com.patan.gimnasio.domain.Exercise;
import com.patan.gimnasio.domain.Routine;

import java.lang.reflect.Array;
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
    private ArrayList<ExFromRoutine> ex = new ArrayList<>();

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

        l = (ListView) findViewById(R.id.dbExercisesList);

        l.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                if (!mode_in.equals("routine")) {
                    Adapter adapter = l.getAdapter();
                    Cursor item = (Cursor) adapter.getItem(position);
                    int pos = item.getColumnIndex(GymnasioDBAdapter.KEY_RO_ID);
                    long id_ex = item.getLong(pos);
                    Intent intent = new Intent(v.getContext(), ExerciseViewActivity.class);
                    intent.putExtra("ID", id_ex);
                    startActivity(intent);
                } else {
                    Adapter adapter = l.getAdapter();
                    Exercise item = (Exercise) adapter.getItem(position);
                    Cursor r = db.getExerciseByName(item.getName());
                    long id_ex = r.getLong(0);
                    //Almacenar id's seleccionadas
                    Intent intent = new Intent(v.getContext(), ExerciseViewActivity.class);
                    intent.putExtra("ID", id_ex);
                    startActivity(intent);
                }
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


        Intent intent = getIntent();
        mode_in = intent.getStringExtra("MODE");
        fillData();
        registerForContextMenu(l);
        if (mode_in.equals("routine")) {
            boton.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (mode_in.equals("routine")) {
            super.onCreateContextMenu(menu, v, menuInfo);
            menu.add(Menu.NONE, ADD_ID, Menu.NONE, R.string.menu_add);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == ADD_ID) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            Adapter adapter = l.getAdapter();
            //DE ALberto
            /*Cursor c = (Cursor) adapter.getItem(info.position);
            int pos = c.getColumnIndex(GymnasioDBAdapter.KEY_EX_ID);
            long id = c.getLong(pos);
            int name = c.getColumnIndex(GymnasioDBAdapter.KEY_EX_NAME);
            String nombre = c.getString(name);*/

            Exercise ejercicio = (Exercise) adapter.getItem(info.position);
            Cursor c = db.getExerciseByName(ejercicio.getName());
            c.moveToFirst();
            Intent intent = new Intent(this, AddExerciseToRoutineActivity.class);
            intent.putExtra("MODE", "ADD");
            long id = c.getLong(0);
            String nombre = c.getString(1);
            intent.putExtra("ID", id);
            intent.putExtra("NAME",nombre);
            intent.putExtra("LAST","ONE");
            intent.putExtra("LIST","");
            startActivityForResult(intent, 1);

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
                long id = data.getLongExtra("ID", 0);  // Cogemos el ID del ejercicio añadido;
                int series = data.getIntExtra("SERIES", 0); // Cogemos las series del ejercicio añadido
                int rep = data.getIntExtra("REP", 0); // Cogemos las repeticiones del ejercicio añadido
                double relax = data.getDoubleExtra("RELAX", 0); // Cogemos el tiempo de relax del ejercicio añadido
                String mode = data.getStringExtra("MODE");

                Intent i = new Intent();
                i.putExtra("MODE", mode);
                i.putExtra("ID", id);
                i.putExtra("SERIES", series);
                i.putExtra("REP", rep);
                i.putExtra("RELAX", relax);
                i.putExtra("LIST",false);
                i.putExtra("LIST VALUE","");
                setResult(RESULT_OK, i);
                finish();       // Forzamos volver a la actividad anterior
            }
        }
        else if(requestCode==2){//Venimos de una lista
            long id = data.getLongExtra("ID", 0);  // Cogemos el ID del ejercicio añadido;
            int series = data.getIntExtra("SERIES", 0); // Cogemos las series del ejercicio añadido
            int rep = data.getIntExtra("REP", 0); // Cogemos las repeticiones del ejercicio añadido
            double relax = data.getDoubleExtra("RELAX", 0); // Cogemos el tiempo de relax del ejercicio añadido
            String mode = data.getStringExtra("MODE");
            ExFromRoutine efr = new ExFromRoutine(id,series,rep,relax);
            ex.add(efr);
        }
        else { //Estamos al final de la lista de ejercicios
            long id = data.getLongExtra("ID", 0);  // Cogemos el ID del ejercicio añadido;
            int series = data.getIntExtra("SERIES", 0); // Cogemos las series del ejercicio añadido
            int rep = data.getIntExtra("REP", 0); // Cogemos las repeticiones del ejercicio añadido
            double relax = data.getDoubleExtra("RELAX", 0); // Cogemos el tiempo de relax del ejercicio añadido
            String mode = data.getStringExtra("MODE");
            ExFromRoutine efr = new ExFromRoutine(id,series,rep,relax);
            ex.add(efr);
            Intent i = new Intent();
            i.putExtra("LIST",true);
            i.putExtra("LIST VALUE",ex);
            setResult(RESULT_OK, i);
            finish();       // Forzamos volver a la actividad anterior
        }
    }

    private void fillData() {
        // Get all of the exercises from the database and create the item list
        Cursor exercises = db.fetchExercises();
        startManagingCursor(exercises);
        // Create an array to specify the fields we want to display in the list (only NAME)
        String[] from = new String[]{GymnasioDBAdapter.KEY_EX_NAME, GymnasioDBAdapter.KEY_EX_TAG};
        // and an array of the fields we want to bind those fields to (in this case just text1)
        int[] to = new int[]{R.id.ex_row, R.id.ex_row2};
        // Now create an array adapter and set it to display using our row
        SimpleCursorAdapter notes =
                new SimpleCursorAdapter(this, R.layout.exercises_row, exercises, from, to, 0);

        ArrayList<Exercise> listOfExercises = new ArrayList<>();
        //Como necesitamos una lista de rutinas, pasamos del cursor a esta lista

        exercises.moveToFirst();
        for (int i = 0; i < exercises.getCount(); i++) {
            ArrayList<String> tags = new ArrayList<>();
            String[] tagsplit = exercises.getString(5).split(",");

            for (String s : tagsplit) tags.add(s);
            listOfExercises.add(new Exercise(exercises.getString(1), exercises.getString(2), exercises.getString(3), exercises.getString(4), tags));
            exercises.moveToNext();
        }

        if (!mode_in.equals("routine")) {
            l.setAdapter(notes);
        } else {
            l.setAdapter(new CustomAdapterExercise(this, listOfExercises));
        }
        registerForContextMenu(l);
    }


    private void fillDataByMuscle(String muscle) {
        // Get all of the exercises from the database and create the item list
        Cursor exercises = db.getExercisesByMuscle(muscle);
        startManagingCursor(exercises);
        // Create an array to specify the fields we want to display in the list (only NAME)
        String[] from = new String[]{GymnasioDBAdapter.KEY_EX_NAME, GymnasioDBAdapter.KEY_EX_TAG};
        // and an array of the fields we want to bind those fields to (in this case just text1)
        int[] to = new int[]{R.id.ex_row, R.id.ex_row2};
        // Now create an array adapter and set it to display using our row
        SimpleCursorAdapter notes =
                new SimpleCursorAdapter(this, R.layout.exercises_row, exercises, from, to, 0);
        ArrayList<Exercise> listOfExercises = new ArrayList<>();
        //Como necesitamos una lista de rutinas, pasamos del cursor a esta lista

        exercises.moveToFirst();
        for (int i = 0; i < exercises.getCount(); i++) {
            ArrayList<String> tags = new ArrayList<>();
            String[] tagsplit = exercises.getString(5).split(",");

            for (String s : tagsplit) tags.add(s);
            listOfExercises.add(new Exercise(exercises.getString(1), exercises.getString(2), exercises.getString(3), exercises.getString(4), tags));
            exercises.moveToNext();
        }

        if (!mode_in.equals("routine")) {
            l.setAdapter(notes);
        } else {
            l.setAdapter(new CustomAdapterExercise(this, listOfExercises));
        }
        registerForContextMenu(l);
    }

    private void fillDataByTag(String tag) {
        // Get all of the exercises from the database and create the item list
        Cursor exercises = db.getExercisesByTag(tag);
        startManagingCursor(exercises);
        // Create an array to specify the fields we want to display in the list (only NAME)
        String[] from = new String[]{GymnasioDBAdapter.KEY_EX_NAME, GymnasioDBAdapter.KEY_EX_TAG};
        // and an array of the fields we want to bind those fields to (in this case just text1)
        int[] to = new int[]{R.id.ex_row, R.id.ex_row2};
        // Now create an array adapter and set it to display using our row
        SimpleCursorAdapter notes =
                new SimpleCursorAdapter(this, R.layout.exercises_row, exercises, from, to, 0);
        ArrayList<Exercise> listOfExercises = new ArrayList<>();
        //Como necesitamos una lista de rutinas, pasamos del cursor a esta lista

        exercises.moveToFirst();
        for (int i = 0; i < exercises.getCount(); i++) {
            ArrayList<String> tags = new ArrayList<>();
            String[] tagsplit = exercises.getString(5).split(",");

            for (String s : tagsplit) tags.add(s);
            listOfExercises.add(new Exercise(exercises.getString(1), exercises.getString(2), exercises.getString(3), exercises.getString(4), tags));
            exercises.moveToNext();
        }

        if (!mode_in.equals("routine")) {
            l.setAdapter(notes);
        } else {
            l.setAdapter(new CustomAdapterExercise(this, listOfExercises));
        }
        registerForContextMenu(l);
    }

    private void fillDataByName(String name) {
        // Get all of the exercises from the database and create the item list
        Cursor exercises = db.getExerciseByName(name);
        startManagingCursor(exercises);
        // Create an array to specify the fields we want to display in the list (only NAME)
        String[] from = new String[]{GymnasioDBAdapter.KEY_EX_NAME, GymnasioDBAdapter.KEY_EX_TAG};
        // and an array of the fields we want to bind those fields to (in this case just text1)
        int[] to = new int[]{R.id.ex_row, R.id.ex_row2};
        // Now create an array adapter and set it to display using our row
        SimpleCursorAdapter notes =
                new SimpleCursorAdapter(this, R.layout.exercises_row, exercises, from, to, 0);
        ArrayList<Exercise> listOfExercises = new ArrayList<>();
        //Como necesitamos una lista de rutinas, pasamos del cursor a esta lista

        exercises.moveToFirst();
        for (int i = 0; i < exercises.getCount(); i++) {
            ArrayList<String> tags = new ArrayList<>();
            String[] tagsplit = exercises.getString(5).split(",");

            for (String s : tagsplit) tags.add(s);
            listOfExercises.add(new Exercise(exercises.getString(1), exercises.getString(2), exercises.getString(3), exercises.getString(4), tags));
            exercises.moveToNext();
        }

        if (!mode_in.equals("routine")) {
            l.setAdapter(notes);
        } else {
            l.setAdapter(new CustomAdapterExercise(this, listOfExercises));
        }
        registerForContextMenu(l);
    }

    public ArrayList<Long> getChecked(){
        int mListLength = l.getCount();
        Adapter adapter = l.getAdapter();
        ArrayList<Long> checked = new ArrayList<>();
        for (int i = 0; i < mListLength; i++) {
            Exercise item = (Exercise) adapter.getItem(i);
            if (item.isChecked()) {
                Cursor c = db.getExerciseByName(item.getName());
                checked.add(c.getLong(0));
            }
        }
        return checked;
    }

    public void addExercises() {
        ArrayList<Long> checked = getChecked();
        for (int i = 0; i < checked.size(); i++) {
            if(i == 0) { //EL ultimo de la lista
                long id = checked.get(i);
                Cursor c = db.getExerciseByID(checked.get(i));
                Intent intent = new Intent(this, AddExerciseToRoutineActivity.class);
                intent.putExtra("MODE", "ADD");
                intent.putExtra("ID", id);
                intent.putExtra("NAME", c.getString(1));
                startActivityForResult(intent,3);
            }
            else{
                long id = checked.get(i);
                Cursor c = db.getExerciseByID(checked.get(i));
                Intent intent = new Intent(this, AddExerciseToRoutineActivity.class);
                intent.putExtra("MODE", "ADD");
                intent.putExtra("ID",id);
                intent.putExtra("NAME", c.getString(1));
                startActivityForResult(intent,2);
            }
        }
    }

    public void add(View v){
        AlertDialog.Builder builder = new AlertDialog.Builder(ExerciseListActivity.this);
        builder.setCancelable(true);
        builder.setTitle("Confirmacion");
        builder.setMessage("Desea añadir " + getChecked().size() + " ejercicios a la rutina?");
        builder.setPositiveButton("SI",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        addExercises();
                    }
                });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}

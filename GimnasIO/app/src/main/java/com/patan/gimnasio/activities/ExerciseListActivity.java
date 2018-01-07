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
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
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
    private static final int ADD_ID = 1;
    private String mode_in;
    private ArrayList<ExFromRoutine> ex = new ArrayList<>();

    private Spinner sp;
    private EditText sv;

    public GymnasioDBAdapter getGymnasioDbAdapter() {
        return db;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercises_list);
        db = new GymnasioDBAdapter(this);
        db.open();
        //We declare the search options
        ArrayList<String> categories = new ArrayList<String>();
        categories.add("Nombre");
        categories.add("Musculo");
        categories.add("Tag");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

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

        Intent intent = getIntent();
        mode_in = intent.getStringExtra("MODE");
        fillData();
        //registerForContextMenu(l);
        if (mode_in.equals("routine")) {
            l.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
        }
        if(l.getChoiceMode() == AbsListView.CHOICE_MODE_MULTIPLE_MODAL) {
            l.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
                @Override
                public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {

                }

                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    menu.add(0,1,1,"Añadir");
                    return true;
                }

                @Override
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                @Override
                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    if(item.getItemId() == 1){ // Añadir
                        add();
                    }
                    return false;
                }

                @Override
                public void onDestroyActionMode(ActionMode mode) {

                }
            });
        }

    }



    // Menu que se crea unicamente en modo premium o trainer
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.routine_list_menu2, menu);

//        SearchView sv = (SearchView) menu.findItem(R.id.customsearch).getActionView();
        LinearLayout rl = (LinearLayout) menu.findItem(R.id.customsearch).getActionView();
        sv = (EditText) rl.findViewById(R.id.etSearch);
        sp = (Spinner) rl.findViewById(R.id.spinnerAb);


        ArrayList<String> categories = new ArrayList<String>();
        categories.add("Nombre");
        categories.add("Músculo");
        categories.add("Tag");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, categories);
        sp.setAdapter(dataAdapter);

        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sv.setText("");
                fillData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        sv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = sp.getSelectedItem().toString(); //Para saber sobre que categoria se etsa buscando
                if (s.length() == 0) {
                    fillData();
                } else if (text.equals("Nombre")) {
                    fillDataByName(s.toString());
                } else if (text.equals("Músculo")) {
                    fillDataByMuscle(s.toString());
                } else if (text.equals("Tag")) {
                    fillDataByTag(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return true;
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
        else if(requestCode == 3){ //Estamos al final de la lista de ejercicios
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
            Log.d("ARRAYIO", ex.toString());
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


    public void addExercises() {
        SparseBooleanArray pos = l.getCheckedItemPositions();
        Adapter a = l.getAdapter();
        for (int i = 0; i < pos.size(); i++) {
            if(i == 0) { //EL ultimo de la lista
                if(pos.valueAt(i)) {
                    Exercise id = (Exercise) a.getItem(pos.keyAt(i));
                    Cursor c = db.getExerciseByName(id.getName());
                    Intent intent = new Intent(this, AddExerciseToRoutineActivity.class);
                    intent.putExtra("MODE", "ADD");
                    intent.putExtra("ID", c.getLong(c.getColumnIndex(GymnasioDBAdapter.KEY_EX_ID)));
                    intent.putExtra("NAME", c.getString(c.getColumnIndex(GymnasioDBAdapter.KEY_EX_NAME)));
                    startActivityForResult(intent,3);
                }

            }
            else{
                if(pos.valueAt(i)) {
                    Exercise id = (Exercise) a.getItem(pos.keyAt(i));
                    Cursor c = db.getExerciseByName(id.getName());
                    Intent intent = new Intent(this, AddExerciseToRoutineActivity.class);
                    intent.putExtra("MODE", "ADD");
                    intent.putExtra("ID", c.getLong(c.getColumnIndex(GymnasioDBAdapter.KEY_EX_ID)));
                    intent.putExtra("NAME", c.getString(c.getColumnIndex(GymnasioDBAdapter.KEY_EX_NAME)));
                    startActivityForResult(intent, 2);
                }
            }
        }
    }

    public void add(){
        AlertDialog.Builder builder = new AlertDialog.Builder(ExerciseListActivity.this);
        builder.setCancelable(true);
        builder.setTitle("Confirmacion");
        builder.setMessage("Desea añadir " + l.getCheckedItemCount() + " ejercicios a la rutina?");
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

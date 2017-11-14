package com.patan.gimnasio;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

// Aqui se mostrara toda la informacion de una rutina con ejercicios, etc. Mediante el boton flotante se podran añadir nuevos ejercicios
// Si se accedio mediante crear rutina esta activididad estara vacia
// Si se accedio mediante ver/editar rutina estara rellenada con los datos de la rutina
public class RoutineEditActivity extends AppCompatActivity {

    private String mode_in;
    private long id_in;
    private ListView l;
    private EditText textName;
    private EditText textGym;
    private EditText textSeries;
    private EditText textRep;
    private EditText textRelax;
    private EditText textObjetivo;
    private ArrayList<Exercise> ex;
    private Menu optionsMenu;

    private GymnasioDBAdapter db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routine_edit);

        l = (ListView)findViewById(R.id.routineEditList);
        textName = (EditText) findViewById(R.id.nombreRutina);
        textGym = (EditText) findViewById(R.id.gimnasioRutina);;
        textSeries = (EditText) findViewById(R.id.seriesRutina);
        textRep = (EditText) findViewById(R.id.repeticionesRutina);
        textRelax = (EditText) findViewById(R.id.tiempoRelaxRutina);
        textObjetivo = (EditText) findViewById(R.id.objetivoRutina);

        l.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Intent intent = new Intent(v.getContext(), ExerciseViewActivity.class);
                startActivity(intent);
            }
        });

        db = new GymnasioDBAdapter(this);
        db.open();

        Intent intent = getIntent();
        mode_in = intent.getStringExtra("MODE");

        if (mode_in.equals("new")) {
            // No hacemos nada, los campos se muestran vacios
        } else if (mode_in.equals("edit")) {
            // Abrimos en modo editar
            id_in = intent.getLongExtra("ID", 0);
            populateFields();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.routine_edit_menu_1, menu);
        optionsMenu = menu;
        return true;
    }

    // Para cambiar del menu de ver al de editar
    public boolean changeMenuToView() {
        optionsMenu.clear();
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.routine_edit_menu_1, optionsMenu);
        return true;
    }

    // Para cambiar del menu de editar al de ver
    public boolean changeMenuToEdit() {
        optionsMenu.clear();
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.routine_edit_menu_2, optionsMenu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.action_edit_1:
                goToEditMode();
                changeMenuToEdit();
                break;
            case R.id.action_edit_2:
                updateRoutine();
                changeMenuToView();
                break;
        }
        return true;
    }


    public void updateRoutine() {
        // TODO: Hacemos un update de la rutina y volvemos a modo edit
    }

    // TODO: poner icono de editar, cuando estes en modo edit poner icono de guardar y volver al modo view

    public void goToEditMode() {

        textName.setFocusable(true);
        textName.setEnabled(true);
        textName.setFocusableInTouchMode(true);
        textName.setClickable(true);

        textGym.setFocusable(true);
        textGym.setEnabled(true);
        textGym.setFocusableInTouchMode(true);
        textGym.setClickable(true);

        textSeries.setFocusable(true);
        textSeries.setEnabled(true);
        textSeries.setFocusableInTouchMode(true);
        textSeries.setClickable(true);

        textRep.setFocusable(true);
        textRep.setEnabled(true);
        textRep.setFocusableInTouchMode(true);
        textRep.setClickable(true);

        textRelax.setFocusable(true);
        textRelax.setEnabled(true);
        textRelax.setFocusableInTouchMode(true);
        textRelax.setClickable(true);

        textObjetivo.setFocusable(true);
        textObjetivo.setEnabled(true);
        textObjetivo.setFocusableInTouchMode(true);
        textObjetivo.setClickable(true);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveState();
        outState.putSerializable(db.KEY_RO_ID, id_in);
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveState();
    }

    public void goToListOfExercises(View v) {
        Intent intent = new Intent(v.getContext(), ExerciseListActivity.class);
        intent.putExtra("MODE","routine");
        startActivityForResult(intent,1);
    }

    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                long id = data.getLongExtra("ID",0);  // Cogemos el ID del ejercicio añadido
                Cursor cursor = db.fetchExercise(id);
                // TODO: Añadir el ejercicio obtenido al listView
            }
        }
    }

    public void saveState() {
        String nameGym = textGym.getText().toString();
        String name;
        if (textName.getText().toString().equals("")) {
            name = "Rutina sin nombre";
        } else name = textName.getText().toString();
        String objective = textObjetivo.getText().toString();
        int series = 0;
        if (!textSeries.getText().toString().equals("")) {
            series = Integer.parseInt(textSeries.getText().toString());
        }
        int rep = 0;
        if (!textRep.getText().toString().equals("")) {
            rep = Integer.parseInt(textRep.getText().toString());
        }
        double relxTime = 0.0;
        if (!textRelax.getText().toString().equals("")) {
            relxTime = Double.parseDouble(textRelax.getText().toString());
        }
        ArrayList<Long> exercises = new ArrayList<>();

        Routine r = new Routine(nameGym, name, objective, series, relxTime, rep, exercises);

        if (mode_in.equals("new")) {
            db.createFreemiumRoutine(r);
            mode_in = "edit";   // Cambiamos a modo edit para que no se cree la rutina multiples veces

        } else if (mode_in.equals("edit")) {
            db.updateFreemiumRoutine(id_in,r);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // No se si es necesario
        // populateFields();
    }

    public void populateFields() {
        Cursor routine = db.fetchRoutine(id_in);
        routine.moveToFirst();
        startManagingCursor(routine);
        if (routine.getString(routine.getColumnIndex(db.KEY_RO_NAME)) != null) {
            textName.setText(routine.getString(routine.getColumnIndex(db.KEY_RO_NAME)));
        } else textName.setText("");
        textName.setFocusable(false);
        textName.setEnabled(false);
        textName.setTextColor(getResources().getColor(R.color.labelColor));

        if (routine.getString(routine.getColumnIndex(db.KEY_RO_GYM)) != null) {
            textGym.setText(routine.getString(routine.getColumnIndex(db.KEY_RO_GYM)));
        } else textGym.setText("");
        textGym.setFocusable(false);
        textGym.setEnabled(false);
        textGym.setTextColor(getResources().getColor(R.color.labelColor));

        if (routine.getString(routine.getColumnIndex(db.KEY_RO_S)) != null) {
            textSeries.setText(routine.getString(routine.getColumnIndex(db.KEY_RO_S)));
        } else textSeries.setText("");
        textSeries.setFocusable(false);
        textSeries.setEnabled(false);
        textSeries.setTextColor(getResources().getColor(R.color.labelColor));

        if (routine.getString(routine.getColumnIndex(db.KEY_RO_R)) != null) {
            textRep.setText(routine.getString(routine.getColumnIndex(db.KEY_RO_R)));
        } else textRep.setText("");
        textRep.setFocusable(false);
        textRep.setEnabled(false);
        textRep.setTextColor(getResources().getColor(R.color.labelColor));

        if (routine.getString(routine.getColumnIndex(db.KEY_RO_RT)) != null) {
            textRelax.setText(routine.getString(routine.getColumnIndex(db.KEY_RO_RT)));
        } else textRelax.setText("");
        textRelax.setFocusable(false);
        textRelax.setEnabled(false);
        textRelax.setTextColor(getResources().getColor(R.color.labelColor));

        if (routine.getString(routine.getColumnIndex(db.KEY_RO_OBJ)) != null) {
            textObjetivo.setText(routine.getString(routine.getColumnIndex(db.KEY_RO_OBJ)));
        } else textObjetivo.setText("");
        textObjetivo.setFocusable(false);
        textObjetivo.setEnabled(false);
        textObjetivo.setTextColor(getResources().getColor(R.color.labelColor));



        // Faltara llenar el ArrayList de ejercicios, obtenemos la lista de ejrecicios y hacemos
        // consultas para ir buscando esos ejercicios y los vamos añadiendo a la listView
        // TODO: obtener la lista de ejercicios y mostrarlos
    }


}

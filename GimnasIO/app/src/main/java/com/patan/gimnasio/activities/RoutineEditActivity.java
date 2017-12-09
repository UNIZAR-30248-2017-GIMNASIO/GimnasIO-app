package com.patan.gimnasio.activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.patan.gimnasio.database.GymnasioDBAdapter;
import com.patan.gimnasio.R;
import com.patan.gimnasio.domain.ExFromRoutine;
import com.patan.gimnasio.domain.Routine;

import java.util.ArrayList;

// Aqui se mostrara toda la informacion de una rutina con ejercicios, etc. Mediante el boton flotante se podran añadir nuevos ejercicios
// Si se accedio mediante crear rutina esta activididad estara vacia
// Si se accedio mediante ver/editar rutina estara rellenada con los datos de la rutina
public class RoutineEditActivity extends AppCompatActivity {

    private String mode_in;
    private long id_in;
    private String user_type;
    private String gym_name;

    private EditText textName;
    private EditText textGym;
    private EditText textObjetivo;
    private FloatingActionButton fab;
    private Menu optionsMenu;

    private final int EDIT_EX = 1;
    private final int DELETE_EX = 2;
    private final int MOVE_EX_UP = 3;
    private final int MOVE_EX_DOWN = 4;


    private GymnasioDBAdapter db;

    private ListView l;

    public GymnasioDBAdapter getGymnasioDbAdapter() {
        return db;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routine_edit);

        l = (ListView)findViewById(R.id.routineEditList);
        textName = (EditText) findViewById(R.id.nombreRutina);
        textGym = (EditText) findViewById(R.id.gimnasioRutina);
        textObjetivo = (EditText) findViewById(R.id.objetivoRutina);
        fab = (FloatingActionButton) findViewById(R.id.fab);

        registerForContextMenu(l);
        l.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Adapter adapter = l.getAdapter();
                Cursor item = (Cursor) adapter.getItem(position);
                int pos = item.getColumnIndex(GymnasioDBAdapter.KEY_EXRO_IDE);
                long id_ex = item.getLong(pos);
                Intent intent = new Intent(v.getContext(), ExerciseViewActivity.class);
                intent.putExtra("ID",id_ex);
                startActivity(intent);
            }
        });

        db = new GymnasioDBAdapter(this);
        db.open();

        Intent intent = getIntent();
        mode_in = intent.getStringExtra("MODE");
        gym_name = intent.getStringExtra("GYMNAME");
        user_type = intent.getStringExtra("USERTYPE");

        // Descativamos el campo Gym para que nunca sea editable

        textGym.setKeyListener(null);
        textGym.setText(gym_name);
        textGym.setTextColor(getResources().getColor(R.color.labelColor));
        textGym.setEnabled(false);

        if (mode_in.equals("new")) {
            ArrayList<ExFromRoutine> efrArray = new ArrayList<>();
            Routine r = new Routine(gym_name,"","");
            if (user_type.equals("free")) {
                id_in = db.createFreemiumRoutine(r,efrArray);
            } else {
                id_in = db.createPremiumRoutine(r,efrArray);
            }

            populateFields();
            goToEditMode();
        } else if (mode_in.equals("view")) {
            // Abrimos en modo editar
            id_in = intent.getLongExtra("ID", 0);
            populateFields();
            goToViewMode();
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (getSupportActionBar().getTitle().equals("Rutina (Editar)")) {
            super.onCreateContextMenu(menu, v, menuInfo);
            menu.add(Menu.NONE, EDIT_EX, Menu.NONE, "Editar ejercicio");
            menu.add(Menu.NONE, DELETE_EX, Menu.NONE, R.string.menu_delete);
            menu.add(Menu.NONE, MOVE_EX_UP, Menu.NONE, "Subir ejercicio");
            menu.add(Menu.NONE, MOVE_EX_DOWN, Menu.NONE, "Bajar ejercicio");
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case EDIT_EX:
                AdapterView.AdapterContextMenuInfo info_edit = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                Adapter adapter_edit = l.getAdapter();
                Cursor c_edit = (Cursor) adapter_edit.getItem(info_edit.position);
                int pos_edit = c_edit.getColumnIndex(GymnasioDBAdapter.KEY_EX_ID);
                long id_edit = c_edit.getLong(pos_edit);
                pos_edit = c_edit.getColumnIndex(GymnasioDBAdapter.KEY_EX_NAME);
                String nombre_edit = c_edit.getString(pos_edit);
                pos_edit = c_edit.getColumnIndex(GymnasioDBAdapter.KEY_EXRO_EXSER);
                int series_edit = c_edit.getInt(pos_edit);
                pos_edit = c_edit.getColumnIndex(GymnasioDBAdapter.KEY_EXRO_EXREP);
                int rep_edit = c_edit.getInt(pos_edit);
                pos_edit = c_edit.getColumnIndex(GymnasioDBAdapter.KEY_EXRO_EXRT);
                double relax_edit = c_edit.getDouble(pos_edit);

                Intent intent = new Intent(this, AddExerciseToRoutineActivity.class);
                intent.putExtra("MODE","EDIT");
                intent.putExtra("ID",id_edit);
                intent.putExtra("NAME", nombre_edit);
                intent.putExtra("SERIES", series_edit);
                intent.putExtra("REP", rep_edit);
                intent.putExtra("RELAX", relax_edit);
                startActivityForResult(intent,1);
                return true;
            case DELETE_EX :
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                Adapter adapter = l.getAdapter();
                Cursor c = (Cursor) adapter.getItem(info.position);
                int pos = c.getColumnIndex(GymnasioDBAdapter.KEY_EXRO_IDE);
                long id = c.getLong(pos);

                // Obtenemos los ejercicios
                ArrayList<ExFromRoutine> efrArray = getExFromRoutineDB();

                // Borramos el seleccionado
                for (ExFromRoutine ex: efrArray) {
                    if (ex.getId() == id) {
                        int index = efrArray.indexOf(ex);
                        efrArray.remove(index);
                        break;
                    }
                }
                // Update de la rutina
                Routine r = getRoutineFields();
                // Actualizamos la rutina
                if (user_type.equals("free")) {
                    db.updateFreemiumRoutine(id_in,r,efrArray);
                } else if (user_type.equals("trainer")) {
                    db.updatePremiumRoutine(id_in,r,efrArray);
                }
                populateFields();
                return true;
            case MOVE_EX_UP :
                AdapterView.AdapterContextMenuInfo info_up = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                Adapter adapter_up = l.getAdapter();
                if (info_up.position > 0 && adapter_up.getCount() >= 2) {
                    Cursor c_up = (Cursor) adapter_up.getItem(info_up.position);
                    int pos_up = c_up.getColumnIndex(GymnasioDBAdapter.KEY_EXRO_IDE);
                    long id_up = c_up.getLong(pos_up);
                    Routine r_up = getRoutineFields();
                    ArrayList<ExFromRoutine> ex_up = getExFromRoutineDB();

                    // Buscamos el indice del ejercicio que queremos mover
                    int index_up = 0;
                    for (ExFromRoutine ex: ex_up) {
                        if (ex.getId() == id_up) {
                            index_up = ex_up.indexOf(ex);
                            break;
                        }
                    }

                    // Actualizamos las nuevas posiciones
                    ExFromRoutine exercise1_up = ex_up.get(index_up);
                    ExFromRoutine exercise2_up = ex_up.get(index_up -1);
                    ex_up.set(index_up, exercise2_up);
                    ex_up.set(index_up -1, exercise1_up);

                    // Actualizamos la rutina
                    if (user_type.equals("free")) {
                        db.updateFreemiumRoutine(id_in,r_up,ex_up);
                    } else if (user_type.equals("trainer")) {
                        db.updatePremiumRoutine(id_in,r_up,ex_up);
                    }
                    populateFields();
                }
                return true;
            case MOVE_EX_DOWN :
                AdapterView.AdapterContextMenuInfo info_down = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                Adapter adapter_down = l.getAdapter();
                if (info_down.position < adapter_down.getCount()-1 && adapter_down.getCount() >= 2) {
                    Cursor c_down = (Cursor) adapter_down.getItem(info_down.position);
                    int pos_down = c_down.getColumnIndex(GymnasioDBAdapter.KEY_EXRO_IDE);
                    long id_down = c_down.getLong(pos_down);
                    Routine r_down = getRoutineFields();
                    ArrayList<ExFromRoutine> ex_down = getExFromRoutineDB();

                    // Buscamos el indice del ejercicio que queremos mover
                    int index_down = 0;
                    for (ExFromRoutine ex: ex_down) {
                        if (ex.getId() == id_down) {
                            index_down = ex_down.indexOf(ex);
                            break;
                        }
                    }
                    Log.d("EX1:",""+index_down);
                    // Actualizamos las nuevas positiciones
                    ExFromRoutine exercise1_down = ex_down.get(index_down);
                    ExFromRoutine exercise2_down = ex_down.get(index_down + 1);
                    ex_down.set(index_down, exercise2_down);
                    ex_down.set(index_down + 1, exercise1_down);

                    // Actualizamos la rutina
                    if (user_type.equals("free")) {
                        db.updateFreemiumRoutine(id_in,r_down,ex_down);
                    } else if (user_type.equals("trainer")) {
                        db.updatePremiumRoutine(id_in,r_down,ex_down);
                    }
                    populateFields();
                }
                return true;
            default: return false;

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        if (user_type.equals("premium")) {
            inflater.inflate(R.menu.routine_edit_menu_viewonly, menu);
        } else if (mode_in.equals("new")) {
            inflater.inflate(R.menu.routine_edit_menu_2, menu);
        } else inflater.inflate(R.menu.routine_edit_menu_1, menu);
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
                saveState();
                changeMenuToView();
                goToViewMode();
                break;
        }
        return true;
    }

    // Metodo para cambiar las propiedades de los campos EditText al modo Editar
    public void goToEditMode() {

        textName.setFocusable(true);
        textName.setEnabled(true);
        textName.setFocusableInTouchMode(true);
        textName.setClickable(true);

        textObjetivo.setFocusable(true);
        textObjetivo.setEnabled(true);
        textObjetivo.setFocusableInTouchMode(true);
        textObjetivo.setClickable(true);

        fab.show();
        getSupportActionBar().setTitle("Rutina (Editar)");
    }

    // Metodo para cambiar las propiedades de los campos EditText al modo Ver
    public void goToViewMode() {
        textName.setFocusable(false);
        textName.setEnabled(false);
        textName.setTextColor(getResources().getColor(R.color.labelColor));

        textObjetivo.setFocusable(false);
        textObjetivo.setEnabled(false);
        textObjetivo.setTextColor(getResources().getColor(R.color.labelColor));

        fab.hide();
        getSupportActionBar().setTitle("Rutina");
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

    @Override
    protected void onStop() {
        super.onStop();
        saveState();
    }

    // Metodo que cambia a la actividad de AddExerciseToRoutineActivity en modo edit
    public void goToEditExercise(View v) {

    }


    // Metodo que cambia a la actividad de ExercisesListActivity en modo routine par añadir ejercicios
    public void goToListOfExercises(View v) {
        saveState();
        Intent intent = new Intent(v.getContext(), ExerciseListActivity.class);
        intent.putExtra("MODE","routine");
        startActivityForResult(intent,1);
    }

    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                String mode = data.getStringExtra("MODE");
                long id = data.getLongExtra("ID",0);  // Cogemos el ID del ejercicio añadido;
                int series = data.getIntExtra("SERIES",0);
                int rep = data.getIntExtra("REP",0);
                double relax = data.getDoubleExtra("RELAX",0);

                ArrayList<ExFromRoutine> efrArray = getExFromRoutineDB();

                if (!mode.equals("EDIT")) {
                    // Añadimos el nuevo ejercicio
                    ExFromRoutine efr_new = new ExFromRoutine(id,series,rep,relax);
                    efrArray.add(efr_new);


                } else {
                    // Buscamos el indice del ejercicio que queremos modificar
                    int index = 0;
                    for (ExFromRoutine ex: efrArray) {
                        if (ex.getId() == id) {
                            index = efrArray.indexOf(ex);
                            break;
                        }
                    }
                    // Actualizamos los valores del ejercicio
                    ExFromRoutine ex = efrArray.get(index);
                    ex.setSeries(series);
                    ex.setRep(rep);
                    ex.setRelxTime(relax);

                    // Actualizamos el ejercicio
                    efrArray.set(index,ex);
                }

                Routine r = getRoutineFields();

                // Actualizamos la rutina
                if (user_type.equals("free")) {
                    db.updateFreemiumRoutine(id_in,r,efrArray);
                } else if (user_type.equals("trainer")) {
                    db.updatePremiumRoutine(id_in,r,efrArray);
                }

                populateFields();

            }
        }
    }

    // Devuelve el array de ejercicios de rutina almacenado en la BD
    public ArrayList<ExFromRoutine> getExFromRoutineDB() {
        ArrayList<ExFromRoutine> efrArray = new ArrayList<>();
        // Cogemos los ejercicios de la rutina
        Cursor exercises = db.getExercisesFromRoutine(id_in);
        startManagingCursor(exercises);
        if (exercises != null) {
            int times = exercises.getCount();
            int index_id = exercises.getColumnIndex(GymnasioDBAdapter.KEY_EXRO_IDE);
            int index_series = exercises.getColumnIndex(GymnasioDBAdapter.KEY_EXRO_EXSER);
            int index_rep = exercises.getColumnIndex(GymnasioDBAdapter.KEY_EXRO_EXREP);
            int index_relax = exercises.getColumnIndex(GymnasioDBAdapter.KEY_EXRO_EXRT);

            for (int i = 0; i < times; i++) {
                long id_efr = exercises.getLong(index_id);
                int series_efr = exercises.getInt(index_series);
                int rep_efr = exercises.getInt(index_rep);
                double relax_efr = exercises.getDouble(index_relax);
                ExFromRoutine efr = new ExFromRoutine(id_efr, series_efr, rep_efr, relax_efr);
                efrArray.add(efr);
                exercises.moveToNext();
            }
        }
        stopManagingCursor(exercises);
        return efrArray;
    }

    // Metodo que genera un objeto rutina a partir del contenido de los campos de la actividad
    public Routine getRoutineFields() {
        String nameGym = textGym.getText().toString();
        String name;
        if (textName.getText().toString().equals("")) {
            name = "Rutina sin nombre";
        } else name = textName.getText().toString();
        String objective = textObjetivo.getText().toString();

        Routine r = new Routine(nameGym, name, objective);
        return r;
    }

    // Metodo que devuelve un arraylist con los ejercicios de la lista de ejercicios
    public ArrayList<ExFromRoutine> getExFromRoutine() {
        ArrayList<ExFromRoutine> returned = new ArrayList<>();
        if (l.getCount() != 0) {
            // Cogemos los ejercicios de la listView
            Adapter adapter = this.l.getAdapter();
            int elementos = adapter.getCount();

            if (elementos != 0) {
                for (int i = 0; i < elementos; i++) {
                    Cursor item = (Cursor) adapter.getItem(i);
                    int pos_id = item.getColumnIndex(GymnasioDBAdapter.KEY_EXRO_IDE);
                    long id = item.getLong(pos_id);
                    int pos_series = item.getColumnIndex(GymnasioDBAdapter.KEY_EXRO_EXSER);
                    int series = item.getInt(pos_series);
                    int pos_rep = item.getColumnIndex(GymnasioDBAdapter.KEY_EXRO_EXREP);
                    int rep = item.getInt(pos_rep);
                    int pos_relax = item.getColumnIndex(GymnasioDBAdapter.KEY_EXRO_EXRT);
                    double relax = item.getDouble(pos_relax);
                    ExFromRoutine ex = new ExFromRoutine(id, series, rep, relax);
                    returned.add(ex);
                }
            }
        }
        return returned;
    }

    public void saveState() {
        Routine r = getRoutineFields();
        ArrayList<ExFromRoutine> efrArray = getExFromRoutine();
        if (mode_in.equals("new")) {
            mode_in = "view";   // Cambiamos a modo view para que no se cree la rutina multiples veces
        }
        if (user_type.equals("free")) {
            db.updateFreemiumRoutine(id_in,r,efrArray);
        } else if (user_type.equals("trainer")) {
            db.updatePremiumRoutine(id_in,r,efrArray);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void populateFields() {
        Cursor routine = db.fetchRoutine(id_in);
        startManagingCursor(routine);
        if (routine.getString(routine.getColumnIndex(db.KEY_RO_NAME)) != null) {
            textName.setText(routine.getString(routine.getColumnIndex(db.KEY_RO_NAME)));
        } else textName.setText("");

        if (!mode_in.equals("new")) {
            populateExerciseList();
        }
        stopManagingCursor(routine);
    }

    // Metodo que rellena la lista de ejercicios
    public void populateExerciseList() {
        Cursor ejercicios = db.getExercisesFromRoutine(id_in);
        if (ejercicios != null) {
            startManagingCursor(ejercicios);
            // Create an array to specify the fields we want to display in the list (only NAME)
            String[] from = new String[] {GymnasioDBAdapter.KEY_EX_NAME, GymnasioDBAdapter.KEY_EXRO_EXSER, GymnasioDBAdapter.KEY_EXRO_EXREP, GymnasioDBAdapter.KEY_EXRO_EXRT};
            // and an array of the fields we want to bind those fields to (in this case just text1)
            int[] to = new int[] { R.id.ex_row, R.id.seriesRutina, R.id.repeticionesRutina, R.id.tiempoRelaxRutina};
            // Now create an array adapter and set it to display using our row
            SimpleCursorAdapter notes =
                    new SimpleCursorAdapter(this, R.layout.exfromroutine_row, ejercicios, from, to,0);
            l.setAdapter(notes);
            registerForContextMenu(l);
            stopManagingCursor(ejercicios);
        } else {
            String[] from = new String[] {};
            // and an array of the fields we want to bind those fields to (in this case just text1)
            int[] to = new int[] {};
            // Now create an array adapter and set it to display using our row
            SimpleCursorAdapter notes =
                    new SimpleCursorAdapter(this, R.layout.exfromroutine_row, ejercicios, from, to,0);
            l.setAdapter(notes);
            registerForContextMenu(l);
        }
    }
}

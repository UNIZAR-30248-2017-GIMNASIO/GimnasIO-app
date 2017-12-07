package com.patan.gimnasio.activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ListView;

import com.patan.gimnasio.database.GymnasioDBAdapter;
import com.patan.gimnasio.R;

// En esta actividad se mostrara una lista de rutinas creadas ademas de la opcion de crear una nueva rutina
//  - Crear rutina (boton flotante) llevara a una RoutineEditActivity vacia
//  - Con una rutina creada podremos:
//      - Verla/Editarla, se abrira una RoutineEditActivity rellenada con los datos de la rutina
//      - Ejecutarla, se abrira una RoutineExecuteActivity para ejecutarla
public class RoutineListActivity extends AppCompatActivity {

    private ListView l;
    private GymnasioDBAdapter db;

    private static final int DELETE_ID = 1;
    private String user_type;
    private String gym_name;
    private FloatingActionButton fab;


    public GymnasioDBAdapter getGymnasioDBAdapter(){
        return db;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routine_list);

        db = new GymnasioDBAdapter(this);
        db.open();

        l = (ListView)findViewById(R.id.dbRoutinesList);
        fab = (FloatingActionButton) findViewById(R.id.floatingActionButton);

        l.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Adapter adapter = l.getAdapter();
                Cursor item = (Cursor) adapter.getItem(position);
                int pos = item.getColumnIndex(GymnasioDBAdapter.KEY_RO_ID);
                long id_rut = item.getLong(pos);
                Intent intent = new Intent(v.getContext(), RoutineEditActivity.class);
                intent.putExtra("MODE","view");
                intent.putExtra("USERTYPE", user_type);
                intent.putExtra("ID",id_rut);
                startActivity(intent);
            }
        });
        Intent intent = getIntent();
        user_type = intent.getStringExtra("USERTYPE");

        // Si es modo premium o trainer cogemos ademas el nombre del gym
        if (user_type.equals("premium") || user_type.equals("trainer")) {
            gym_name = intent.getStringExtra("GYMNAME");
        }

        // Rellenamos la lista
        fillData();
    }

    // Menu que se crea unicamente en modo premium o trainer
    public boolean onCreateOptionsMenu(Menu menu) {
        if (user_type.equals("premium") || user_type.equals("trainer")) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.routine_list_menu, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if (item.getItemId() == R.id.logout) {
            db.logout();
            // Esto nos hara volver al menu principal directamente limpiando las actividades que tenga por encima (identificate)
            Intent intent = new Intent(this,MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        return true;
    }

    // Si pulsa el boton de volver atras y esta en modo premium o trainer volvera al menu principal
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }


    private void fillData() {
        Cursor routines = null ;
        if (user_type.equals("free")) {
            // Get all of the routines from the database and create the item list
            routines = db.fetchFreemiumRoutines();
            startManagingCursor(routines);
        } else if (user_type.equals("premium")) {
            fab.hide();
            getSupportActionBar().setTitle("Rutinas " + gym_name);
            routines = db.fetchPremiumRoutines(gym_name);
            startManagingCursor(routines);
        } else if (user_type.equals("trainer")) {
            routines = db.fetchPremiumRoutines(gym_name);
            startManagingCursor(routines);
        }
        // Create an array to specify the fields we want to display in the list (only NAME)
        String[] from = new String[] {GymnasioDBAdapter.KEY_RO_NAME};
        // and an array of the fields we want to bind those fields to (in this case just text1)
        int[] to = new int[] { R.id.ro_row };
        // Now create an array adapter and set it to display using our row
        SimpleCursorAdapter notes =
                new SimpleCursorAdapter(this, R.layout.routines_row, routines, from, to,0);
        l.setAdapter(notes);
        registerForContextMenu(l);
    }


    /**
     * Metodo que cambia a la actividad ExerciseViewActivity en modo crear nueva rutina
     */
    public void goToRoutineEditCreate(View v) {
        Intent intent = new Intent(this, RoutineEditActivity.class);
        intent.putExtra("MODE","new");
        intent.putExtra("ID",0);
        startActivity(intent);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (!user_type.equals("premium")) {
            super.onCreateContextMenu(menu,v,menuInfo);
            menu.add(Menu.NONE, DELETE_ID, Menu.NONE, R.string.menu_delete);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == DELETE_ID) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            Adapter adapter = l.getAdapter();
            Cursor c = (Cursor) adapter.getItem(info.position);
            int pos = c.getColumnIndex(GymnasioDBAdapter.KEY_RO_ID);
            long id = c.getLong(pos);
            db.deleteRoutine(id);
            fillData();
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        fillData();
    }
}

package com.patan.gimnasio;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;


@RunWith(AndroidJUnit4.class)
public class RoutineListTest extends ActivityInstrumentationTestCase2<RoutineListActivity> {

    private RoutineListActivity routineList;
    private GymnasioDBAdapter db;
    private long id1, id2, id3;
    ArrayList<Long> ex = new ArrayList<>();

    public RoutineListTest() {
        super(RoutineListActivity.class);
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        routineList = getActivity();
        db = routineList.getGymnasioDBAdapter();
        ex.add((long)1);  // Se añaden dos ID cualesquiera para simular IDs de ejercicios
        ex.add((long)2);
    }

    // Test que comprueba que la actividad lista todas las rutinas existentes
    @Test
    public void listarRutinasTest() throws Exception{
        Routine r1 = new Routine("Gym1","Rutina1","Objetivo1",1,1.0,1,ex);
        Routine r2 = new Routine("Gym2","Rutina2","Objetivo2",2,2.0,2,ex);
        Routine r3 = new Routine("Gym3","Rutina3","Objetivo3",3,3.0,3,ex);

        int nRoutinesPre = db.fetchRoutines().getCount();

        id1 = db.createFreemiumRoutine(r1);
        id2 = db.createFreemiumRoutine(r2);
        id3 = db.createFreemiumRoutine(r3);

        int nRoutinesPost = db.fetchRoutines().getCount();

        assertEquals( nRoutinesPre + 3, nRoutinesPost);
    }

    // Test que comprueba que la actividad elimina rutinas correctamente
    @Test
    public void eliminarRutinaTest_existe() throws Exception {
        Routine r4 = new Routine("Gym4","Rutina4","Objetivo4",4,4.0,4,ex);
        long id4 = db.createFreemiumRoutine(r4);
        assertEquals(true,db.deleteRoutine(id4));
    }

    // Test que comprueba que la actividad no elimina rutinas con ID = 0
    @Test
    public void eliminarRutinaTest_cero() throws Exception {
        assertEquals(false, db.deleteRoutine(0));
    }

    // Test que comprueba que la actividad no elimina al pasar id -1
    @Test
    public void eliminarRutinaTest_negativo() throws Exception {
        assertEquals(false, db.deleteRoutine(-1));
    }

    @After
    public void tearDown() throws Exception{
        //routineList = getActivity();
        //db = routineList.getGymnasioDBAdapter();
        db.deleteRoutine(id1);
        db.deleteRoutine(id2);
        db.deleteRoutine(id3);
        super.tearDown();
    }
}

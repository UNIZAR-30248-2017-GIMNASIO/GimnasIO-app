package com.patan.gimnasio;

import android.database.Cursor;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;

import com.patan.gimnasio.activities.ExerciseListActivity;
import com.patan.gimnasio.database.GymnasioDBAdapter;
import com.patan.gimnasio.domain.Exercise;
import com.patan.gimnasio.domain.Routine;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;

@RunWith(AndroidJUnit4.class)
public class DatabaseTest extends ActivityInstrumentationTestCase2<ExerciseListActivity> {

    private ExerciseListActivity exerciseList;
    private GymnasioDBAdapter db;
    private Exercise e,e2,e3,e4;
    private long id1,id2,id3,idr1,idr2,idr3;
    private ArrayList<Long> ExercisesList = new ArrayList<>();

    public DatabaseTest() {
        super(ExerciseListActivity.class);
    }

    @Before
    public void setUp() throws Exception{
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        exerciseList = getActivity();
        db = exerciseList.getGymnasioDbAdapter();
        ExercisesList.add((long)1);
    }

    /*Test que simula una actualización de la  BD*/
    @Ignore
    @Test
    public void upgradeTest() throws Exception{

        Cursor c = db.checkForUpdates();
        c.moveToFirst();

        int id = c.getCount();
        String date =  c.getString(1);

        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateandTime = sdf.format(currentTime);

        long after = db.updateLastUpdate(id,currentDateandTime);
        assertTrue(after == id && currentDateandTime != date);

    }

    /*Tests of exercises*/
    /*Test que comrpueba que la bd añade ejercicios correctamente*/
    @Test
    public void addExerciseTest() throws Exception{
        ArrayList<String> tags = new ArrayList<String>();
        tags.add("Tag1");
        tags.add("Tag2");
        Exercise e1 = new Exercise("Nombre1","Musculo1","El ejercicio 1","/ruta1",tags);
        Exercise e2 = new Exercise("Nombre2","Musculo2","El ejercicio 2","/ruta2",tags);
        Exercise e3 = new Exercise("Nombre3","Musculo3","El ejercicio 3","/ruta3",tags);

        int nExercisesPre = db.fetchExercises().getCount();

        id1 = db.createExercise(e1);
        id2 = db.createExercise(e2);
        id3 = db.createExercise(e3);

        int nExercisesPost = db.fetchExercises().getCount();

        assertEquals(nExercisesPre+3,nExercisesPost);
    }

    /*Test que comrpueba que la bd  no añade ejercicios cuando el nombre se repite*/
    @Test
    public void addExerciseTestRepeated() throws Exception{
        ArrayList<String> tags = new ArrayList<String>();
        tags.add("Tag1");
        tags.add("Tag2");
        Exercise e1 = new Exercise("same","Musculo1","El ejercicio 1","/ruta1",tags);
        Exercise e2 = new Exercise("same","Musculo2","El ejercicio 2","/ruta2",tags);

        int nExercisesPre = db.fetchExercises().getCount();

        id1 = db.createExercise(e1);
        id2 = db.createExercise(e2);


        int nExercisesPost = db.fetchExercises().getCount();

        assertEquals(nExercisesPre+1,nExercisesPost);
    }

    /*Test que comrpueba que la bd borra ejercicios correctamente*/
    @Test
    public void deleteExerciseTest() throws Exception{
        ArrayList<String> tags = new ArrayList<String>();
        tags.add("Tag1");
        tags.add("Tag2");
        Exercise e1 = new Exercise("Nombre1","Musculo1","El ejercicio 1","/ruta1",tags);

        id1 = db.createExercise(e1);

        int before = db.fetchExercises().getCount();

        db.deleteExercise(id1);

        int after = db.fetchExercises().getCount();

        assertEquals(before-1,after);
    }

    /*Test que comrpueba que la bd no borra ejercicios si no existen*/
    @Test
    public void dontDeleteExerciseTest() throws Exception{
        assertFalse( db.deleteExercise(0));
    }

    /*Test que comprueba que la bd devuelva bien el ejercicio al buscarlo por nombre*/
    @Test
    public void checkExerciseByName() throws Exception{

        ArrayList<String> tags = new ArrayList<String>();
        tags.add("Tag1");
        tags.add("Tag2");
        Exercise e1 = new Exercise("Nombre1","Musculo1","El ejercicio 1","/ruta1",tags);

        id1 = db.createExercise(e1);

        Cursor c = db.getExerciseByName(e1.getName());

        Exercise newE = new Exercise(c.getString(1),c.getString(3),c.getString(2),c.getString(4),tags);

        assertTrue(e1.equals(newE));
    }

    /*Test que comprueba que la bd devuelva bien el ejercicio al buscarlo por nombre, cuando no haya ningun ejercicio con dicho nombre*/
    @Test
    public void checkExerciseByNameFalse() throws Exception{
        Cursor c = db.getExerciseByName("No esta");
        assertTrue(c.getCount()==0);
    }

    /*Test que comprueba que la bd devuelva bien el ejercicio al buscarlo por musculo*/
    @Test
    public void checkExerciseByMuscle() throws Exception{

        ArrayList<String> tags = new ArrayList<String>();
        tags.add("Tag1");
        tags.add("Tag2");
        Exercise e1 = new Exercise("Nombre1","Musculo1","El ejercicio 1","/ruta1",tags);

        id1 = db.createExercise(e1);

        Cursor c = db.getExercisesByMuscle(e1.getMuscle());

        Exercise newE = new Exercise(c.getString(1),c.getString(3),c.getString(2),c.getString(4),tags);

        assertTrue(e1.equals(newE));
    }

    /*Test que comprueba que la bd devuelva bien el ejercicio al buscarlo por musculo, cuando no haya ningun ejercicio con dicho musculo*/
    @Test
    public void checkExerciseByMuscleFalse() throws Exception{
        Cursor c = db.getExercisesByMuscle("No esta");
        assertTrue(c.getCount()==0);
    }

    /*Test que comprueba que la bd devuelva bien los ejercicios al buscar por tag*/
    @Test
    public void checkExercisesByTag() throws Exception{

        ArrayList<String> tags = new ArrayList<String>();
        tags.add("Tag1");
        tags.add("Tag2");
        Exercise e1 = new Exercise("Nombre1","Musculo1","El ejercicio 1","/ruta1",tags);

        id1 = db.createExercise(e1);

        Cursor c = db.getExercisesByTag("Tag1");

        String[] tagsplit = c.getString(5).split(",");

        ArrayList<String> taglist = new ArrayList<String>(Arrays.asList(tagsplit));

        assertTrue(tagsplit[0].contains(tags.get(0)));
    }

    /*Test que comprueba que la bd devuelva bien el ejercicio al buscarlo por tag, cuando no haya ningun ejercicio con dicho tag*/
    @Test
    public void checkExerciseByTagFalse() throws Exception{
        Cursor c = db.getExercisesByTag("No esta");
        assertTrue(c.getCount()==0);
    }

    /*End tests of exercises*/

    /*Tests of routines*/
    /*Test que comprueba que la bd añada rutinas freemium correctamente*/
    @Test
    public void addFreemiumRoutineTest() throws Exception{

        Routine r1 = new Routine("Gym1","Rutina1","Objetivo1",1,1.0,1, ExercisesList);
        Routine r2 = new Routine("Gym2","Rutina2","Objetivo2",2,2.0,2,ExercisesList);
        Routine r3 = new Routine("Gym3","Rutina3","Objetivo3",3,3.0,3,ExercisesList);

        int nRoutinesPre = db.fetchRoutines().getCount();

        idr1 = db.createFreemiumRoutine(r1);
        idr2 = db.createFreemiumRoutine(r2);
        idr3 = db.createFreemiumRoutine(r3);

        int nRoutinesPost = db.fetchRoutines().getCount();

        assertEquals( nRoutinesPre + 3, nRoutinesPost);
    }

    /*Test que comprueba que la bd añada rutinas premium correctamente*/
    @Test
    public void addPremiumRoutineTest() throws Exception{

        Routine r1 = new Routine("Gym1","Rutina1","Objetivo1",1,1.0,1, ExercisesList);
        Routine r2 = new Routine("Gym2","Rutina2","Objetivo2",2,2.0,2,ExercisesList);
        Routine r3 = new Routine("Gym3","Rutina3","Objetivo3",3,3.0,3,ExercisesList);

        int nRoutinesPre = db.fetchRoutines().getCount();

        idr1 = db.createPremiumRoutine(r1);
        idr2 = db.createPremiumRoutine(r2);
        idr3 = db.createPremiumRoutine(r3);

        int nRoutinesPost = db.fetchRoutines().getCount();

        assertEquals( nRoutinesPre + 3, nRoutinesPost);
    }

    /*Test que comprueba que la bd saca rutinas correctamente*/

    @Test
    public void getRoutineTest() throws Exception{

        ArrayList<String> tags = new ArrayList<String>();
        tags.add("Tag1");
        tags.add("Tag2");
        Exercise e1 = new Exercise("Nombre1","Musculo1","El ejercicio 1","/ruta1",tags);
        Exercise e2 = new Exercise("Nombre2","Musculo2","El ejercicio 2","/ruta2",tags);

        Exercise e3 = new Exercise("Nombre3","Musculo2","El ejercicio 2","/ruta2",tags);

        id1 = db.createExercise(e1);
        id2 = db.createExercise(e2);
        id3 = db.createExercise(e3);

        ArrayList<Long> foo = new ArrayList<>();
        foo.add(id1);foo.add(id2);

        Routine r1 = new Routine("Gym1","Rutina1","Objetivo1",1,1.0,1, foo);

        idr1 = db.createPremiumRoutine(r1);

        Cursor c = db.fetchRoutine(idr1);

        Cursor ejs = db.getExercisesFromRoutine(idr1);
        ArrayList<Long> newE = new ArrayList<>();
        for(int number = ejs.getCount();number>0;number--) newE.add(ejs.getLong(0));

        Routine res = new Routine(c.getString(7),c.getString(5),c.getString(4),
                c.getInt(1),c.getDouble(2),c.getInt(3),newE);

        assertTrue( res.equals(r1));
    }

    /*Test que comprueba que la bd elimina rutinas correctamente*/
    @Test
    public void deleteRoutineTest_existe() throws Exception {
        Routine r4 = new Routine("Gym4","Rutina4","Objetivo4",4,4.0,4,ExercisesList);
        long id4 = db.createFreemiumRoutine(r4);
        assertEquals(true,db.deleteRoutine(id4));
    }

    /*Test que comprueba que la bd no elimina rutinas con ID = 0*/
    @Test
    public void deleteRoutineTest_cero() throws Exception {
        assertEquals(false, db.deleteRoutine(0));
    }

    /*Test que comprueba que la bd no elimina al pasar id -1*/
    @Test
    public void deleteRoutineTest_negativo() throws Exception {
        assertEquals(false, db.deleteRoutine(-1));
    }

    /*Test que comprueba que la bd modifica el campo gimnasio de una rutina freemium*/
    @Test
    public void updateNameGymRuotineFreemium() throws Exception{
        ArrayList<String> tags = new ArrayList<String>();
        tags.add("Tag1");
        tags.add("Tag2");
        Exercise e1 = new Exercise("Nombre1","Musculo1","El ejercicio 1","/ruta1",tags);
        Exercise e2 = new Exercise("Nombre2","Musculo2","El ejercicio 2","/ruta2",tags);

        id1 = db.createExercise(e1);
        id2 = db.createExercise(e2);

        ArrayList<Long> foo = new ArrayList<>();
        foo.add(id1);foo.add(id2);
        Routine r1 = new Routine("Gym1","Rutina1","Objetivo1",1,1.0,1, foo);

        idr1 = db.createFreemiumRoutine(r1);

        Routine newR = new Routine("Update!","Rutina1","Objetivo1",1,1.0,1, foo);

        boolean ok = db.updateFreemiumRoutine(idr1,newR);

        Cursor c = db.fetchRoutine(idr1);

        Cursor ejs = db.getExercisesFromRoutine(idr1);
        ArrayList<Long> newE = new ArrayList<>();
        for(int number = ejs.getCount();number>0;number--) newE.add(ejs.getLong(0));

        Routine afterUpdate = new Routine(c.getString(7),c.getString(5),c.getString(4),
                c.getInt(1),c.getDouble(2),c.getInt(3),newE);

        assertTrue(ok && afterUpdate.equals(newR));
    }

    /*Test que comprueba que la bd modifica el campo objetivo de una rutina freemium*/
    @Test
    public void updateObjRuotineFreemium() throws Exception{
        ArrayList<String> tags = new ArrayList<String>();
        tags.add("Tag1");
        tags.add("Tag2");
        Exercise e1 = new Exercise("Nombre1","Musculo1","El ejercicio 1","/ruta1",tags);
        Exercise e2 = new Exercise("Nombre2","Musculo2","El ejercicio 2","/ruta2",tags);

        id1 = db.createExercise(e1);
        id2 = db.createExercise(e2);

        ArrayList<Long> foo = new ArrayList<>();
        foo.add(id1);foo.add(id2);
        Routine r1 = new Routine("Gym1","Rutina1","Objetivo1",1,1.0,1, foo);

        idr1 = db.createFreemiumRoutine(r1);

        Routine newR = new Routine("Gym1!","Rutina1","Update!",1,1.0,1, foo);

        boolean ok = db.updateFreemiumRoutine(idr1,newR);

        Cursor c = db.fetchRoutine(idr1);

        Cursor ejs = db.getExercisesFromRoutine(idr1);
        ArrayList<Long> newE = new ArrayList<>();
        for(int number = ejs.getCount();number>0;number--) newE.add(ejs.getLong(0));

        Routine afterUpdate = new Routine(c.getString(7),c.getString(5),c.getString(4),
                c.getInt(1),c.getDouble(2),c.getInt(3),newE);

        assertTrue(ok && afterUpdate.equals(newR));
    }

    /*Test que comprueba que la bd modifica el campo nombre de una rutina freemium*/
    @Test
    public void updateNameRuotineFreemium() throws Exception{
        ArrayList<String> tags = new ArrayList<String>();
        tags.add("Tag1");
        tags.add("Tag2");
        Exercise e1 = new Exercise("Nombre1","Musculo1","El ejercicio 1","/ruta1",tags);
        Exercise e2 = new Exercise("Nombre2","Musculo2","El ejercicio 2","/ruta2",tags);

        id1 = db.createExercise(e1);
        id2 = db.createExercise(e2);

        ArrayList<Long> foo = new ArrayList<>();
        foo.add(id1);foo.add(id2);
        Routine r1 = new Routine("Gym1","Rutina1","Objetivo1",1,1.0,1, foo);

        idr1 = db.createFreemiumRoutine(r1);

        Routine newR = new Routine("Gym1","Update","Objetivo1",1,1.0,1, foo);

        boolean ok = db.updateFreemiumRoutine(idr1,newR);

        Cursor c = db.fetchRoutine(idr1);

        Cursor ejs = db.getExercisesFromRoutine(idr1);
        ArrayList<Long> newE = new ArrayList<>();
        for(int number = ejs.getCount();number>0;number--) newE.add(ejs.getLong(0));

        Routine afterUpdate = new Routine(c.getString(7),c.getString(5),c.getString(4),
                c.getInt(1),c.getDouble(2),c.getInt(3),newE);

        assertTrue(ok && afterUpdate.equals(newR));
    }

    /*Test que comprueba que la bd modifica el campo series de una rutina freemium*/
    @Test
    public void updateSeriesRuotineFreemium() throws Exception{
        ArrayList<String> tags = new ArrayList<String>();
        tags.add("Tag1");
        tags.add("Tag2");
        Exercise e1 = new Exercise("Nombre1","Musculo1","El ejercicio 1","/ruta1",tags);
        Exercise e2 = new Exercise("Nombre2","Musculo2","El ejercicio 2","/ruta2",tags);

        id1 = db.createExercise(e1);
        id2 = db.createExercise(e2);

        ArrayList<Long> foo = new ArrayList<>();
        foo.add(id1);foo.add(id2);
        Routine r1 = new Routine("Gym1","Rutina1","Objetivo1",1,1.0,1, foo);

        idr1 = db.createFreemiumRoutine(r1);

        Routine newR = new Routine("Gym1","Rutina1","Objetivo1",234,1.0,1, foo);

        boolean ok = db.updateFreemiumRoutine(idr1,newR);

        Cursor c = db.fetchRoutine(idr1);

        Cursor ejs = db.getExercisesFromRoutine(idr1);
        ArrayList<Long> newE = new ArrayList<>();
        for(int number = ejs.getCount();number>0;number--) newE.add(ejs.getLong(0));

        Routine afterUpdate = new Routine(c.getString(7),c.getString(5),c.getString(4),
                c.getInt(1),c.getDouble(2),c.getInt(3),newE);

        assertTrue(ok && afterUpdate.equals(newR));
    }

    /*Test que comprueba que la bd modifica el campo relaxTime de una rutina freemium*/
    @Test
    public void updateRelaxTimeRuotineFreemium() throws Exception{
        ArrayList<String> tags = new ArrayList<String>();
        tags.add("Tag1");
        tags.add("Tag2");
        Exercise e1 = new Exercise("Nombre1","Musculo1","El ejercicio 1","/ruta1",tags);
        Exercise e2 = new Exercise("Nombre2","Musculo2","El ejercicio 2","/ruta2",tags);

        id1 = db.createExercise(e1);
        id2 = db.createExercise(e2);

        ArrayList<Long> foo = new ArrayList<>();
        foo.add(id1);foo.add(id2);
        Routine r1 = new Routine("Gym1","Rutina1","Objetivo1",1,1.0,1, foo);

        idr1 = db.createFreemiumRoutine(r1);

        Routine newR = new Routine("Gym1","Rutina1","Objetivo1",1,234.0,1, foo);

        boolean ok = db.updateFreemiumRoutine(idr1,newR);

        Cursor c = db.fetchRoutine(idr1);

        Cursor ejs = db.getExercisesFromRoutine(idr1);
        ArrayList<Long> newE = new ArrayList<>();
        for(int number = ejs.getCount();number>0;number--) newE.add(ejs.getLong(0));

        Routine afterUpdate = new Routine(c.getString(7),c.getString(5),c.getString(4),
                c.getInt(1),c.getDouble(2),c.getInt(3),newE);

        assertTrue(ok && afterUpdate.equals(newR));
    }

    /*Test que comprueba que la bd modifica el campo repeticiones de una rutina freemium*/
    @Test
    public void updateRepRuotineFreemium() throws Exception{
        ArrayList<String> tags = new ArrayList<String>();
        tags.add("Tag1");
        tags.add("Tag2");
        Exercise e1 = new Exercise("Nombre1","Musculo1","El ejercicio 1","/ruta1",tags);
        Exercise e2 = new Exercise("Nombre2","Musculo2","El ejercicio 2","/ruta2",tags);

        id1 = db.createExercise(e1);
        id2 = db.createExercise(e2);

        ArrayList<Long> foo = new ArrayList<>();
        foo.add(id1);foo.add(id2);
        Routine r1 = new Routine("Gym1","Rutina1","Objetivo1",1,1.0,1, foo);

        idr1 = db.createFreemiumRoutine(r1);

        Routine newR = new Routine("Gym1","Rutina1","Objetivo1",1,1.0,22, foo);

        boolean ok = db.updateFreemiumRoutine(idr1,newR);

        Cursor c = db.fetchRoutine(idr1);

        Cursor ejs = db.getExercisesFromRoutine(idr1);
        ArrayList<Long> newE = new ArrayList<>();
        for(int number = ejs.getCount();number>0;number--) newE.add(ejs.getLong(0));

        Routine afterUpdate = new Routine(c.getString(7),c.getString(5),c.getString(4),
                c.getInt(1),c.getDouble(2),c.getInt(3),newE);

        assertTrue(ok && afterUpdate.equals(newR));
    }

    /*Test que comprueba que la bd modifica el campo repeticiones de una rutina freemium*/
    @Test
    public void updateExsRuotineFreemium() throws Exception{
        ArrayList<String> tags = new ArrayList<String>();
        tags.add("Tag1");
        tags.add("Tag2");
        Exercise e1 = new Exercise("Nombre1","Musculo1","El ejercicio 1","/ruta1",tags);
        Exercise e2 = new Exercise("Nombre2","Musculo2","El ejercicio 2","/ruta2",tags);
        Exercise e3 = new Exercise("Nombre3","Musculo2","El ejercicio 2","/ruta2",tags);


        id1 = db.createExercise(e1);
        id2 = db.createExercise(e2);
        id3 = db.createExercise(e3);

        ArrayList<Long> foo = new ArrayList<>();
        foo.add(id1);foo.add(id2);

        ArrayList<Long> bar = new ArrayList<>();
        bar.add(id3);

        Routine r1 = new Routine("Gym1","Rutina1","Objetivo1",1,1.0,1, foo);

        idr1 = db.createFreemiumRoutine(r1);

        Routine newR = new Routine("Gym1","Rutina1","Objetivo1",1,1.0,1, bar);

        boolean ok = db.updateFreemiumRoutine(idr1,newR);

        Cursor c = db.fetchRoutine(idr1);

        Cursor ejs = db.getExercisesFromRoutine(idr1);
        ArrayList<Long> newE = new ArrayList<>();
        for(int number = ejs.getCount();number>0;number--) newE.add(ejs.getLong(0));

        Routine afterUpdate = new Routine(c.getString(7),c.getString(5),c.getString(4),
                c.getInt(1),c.getDouble(2),c.getInt(3),newE);

        assertTrue(ok && afterUpdate.equals(newR));
    }

    /*Test que comprueba que la bd modifica TODOS los campos de una rutina freemium*/
    @Test
    public void updateRoutineFreemium() throws Exception{
        ArrayList<String> tags = new ArrayList<String>();
        tags.add("Tag1");
        tags.add("Tag2");
        Exercise e1 = new Exercise("Nombre1","Musculo1","El ejercicio 1","/ruta1",tags);
        Exercise e2 = new Exercise("Nombre2","Musculo2","El ejercicio 2","/ruta2",tags);

        Exercise e3 = new Exercise("Nombre3","Musculo2","El ejercicio 2","/ruta2",tags);

        id1 = db.createExercise(e1);
        id2 = db.createExercise(e2);
        id3 = db.createExercise(e3);

        ArrayList<Long> foo = new ArrayList<>();
        foo.add(id1);foo.add(id2);

        ArrayList<Long> bar = new ArrayList<>();
        bar.add(id3);

        Routine r1 = new Routine("Gym1","Rutina1","Objetivo1",1,1.0,1, foo);

        idr1 = db.createFreemiumRoutine(r1);

        Routine newR = new Routine("Update!","Update!","Update!",23,44.0,125, bar);

        boolean ok = db.updateFreemiumRoutine(idr1,newR);

        Cursor c = db.fetchRoutine(idr1);

        Cursor ejs = db.getExercisesFromRoutine(idr1);
        ArrayList<Long> newE = new ArrayList<>();
        for(int number = ejs.getCount();number>0;number--) newE.add(ejs.getLong(0));

        Routine afterUpdate = new Routine(c.getString(7),c.getString(5),c.getString(4),
                c.getInt(1),c.getDouble(2),c.getInt(3),newE);

        assertTrue(ok && afterUpdate.equals(newR));
    }

    /*Test que comprueba que la bd modifica TODOS los campos de una rutina premium*/
    @Test
    public void updateRoutinePremium() throws Exception{
        ArrayList<String> tags = new ArrayList<String>();
        tags.add("Tag1");
        tags.add("Tag2");
        Exercise e1 = new Exercise("Nombre1","Musculo1","El ejercicio 1","/ruta1",tags);
        Exercise e2 = new Exercise("Nombre2","Musculo2","El ejercicio 2","/ruta2",tags);

        Exercise e3 = new Exercise("Nombre3","Musculo2","El ejercicio 2","/ruta2",tags);

        id1 = db.createExercise(e1);
        id2 = db.createExercise(e2);
        id3 = db.createExercise(e3);

        ArrayList<Long> foo = new ArrayList<>();
        foo.add(id1);foo.add(id2);

        ArrayList<Long> bar = new ArrayList<>();
        bar.add(id3);

        Routine r1 = new Routine("Gym1","Rutina1","Objetivo1",1,1.0,1, foo);

        idr1 = db.createPremiumRoutine(r1);

        Routine newR = new Routine("Update!","Update!","Update!",23,44.0,125, bar);

        boolean ok = db.updatePremiumRoutine(idr1,newR);

        Cursor c = db.fetchRoutine(idr1);

        Cursor ejs = db.getExercisesFromRoutine(idr1);
        ArrayList<Long> newE = new ArrayList<>();
        for(int number = ejs.getCount();number>0;number--) newE.add(ejs.getLong(0));

        Routine afterUpdate = new Routine(c.getString(7),c.getString(5),c.getString(4),
                c.getInt(1),c.getDouble(2),c.getInt(3),newE);

        assertTrue(ok && afterUpdate.equals(newR));
    }
    /*Test que comprueba que el método getNumberOfRoutines devuelve el numero correcto*/
    @Test
    public void getNumberOfRotinesTest() throws Exception{

        Routine r1 = new Routine("Gym1","Rutina1","Objetivo1",1,1.0,1, ExercisesList);
        Routine r2 = new Routine("Update!","Update!","Update!",23,44.0,125, ExercisesList);
        Routine r3 = new Routine("Update!","Update!","Update!",23,44.0,125, ExercisesList);

        idr1 = db.createFreemiumRoutine(r1);
        idr2 = db.createFreemiumRoutine(r2);
        idr3 = db.createFreemiumRoutine(r3);

        int manual = db.fetchRoutines().getCount();

        int withMethod = db.getNumberOfRoutines();


        assertEquals(manual,withMethod);
    }

    /*Test que comprueba que la bd devuelve la rutina según el nombre cuando existe*/
    @Test
    public void getRoutineByNameTest() throws Exception{

        Routine r1 = new Routine("Gym1","Rutina1","Objetivo1",1,1.0,1, ExercisesList);
        Routine r2 = new Routine("Update!","Update!","Update!",23,44.0,125, ExercisesList);
        Routine r3 = new Routine("Update!","Update!","Update!",23,44.0,125, ExercisesList);

        idr1 = db.createFreemiumRoutine(r1);
        idr2 = db.createFreemiumRoutine(r2);
        idr3 = db.createFreemiumRoutine(r3);

        Cursor c = db.getRoutineByName(r1.getName());

        Routine res = new Routine(c.getString(7),c.getString(5),c.getString(4),
                c.getInt(1),c.getDouble(2),c.getInt(3),ExercisesList);

        assertEquals(c.getString(5),r1.getName());
    }

    /*Test que comprueba que la bd devuelve la rutina según el nombre cuando no existe*/
    @Test
    public void dontGetRoutineByNameTest() throws Exception{

        Cursor c = db.getRoutineByName("nothing");

        assertTrue(c.getCount() == 0);
    }

     /*End tests of routines*/

    @After
    public void tearDown() throws Exception {
        db.deleteExercise(id1);
        db.deleteExercise(id2);
        db.deleteExercise(id3);
        db.deleteRoutine(idr1);
        db.deleteRoutine(idr2);
        db.deleteRoutine(idr3);
    }

}

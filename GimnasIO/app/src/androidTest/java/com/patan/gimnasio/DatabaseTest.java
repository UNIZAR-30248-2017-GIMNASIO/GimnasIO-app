package com.patan.gimnasio;

import android.content.Context;
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
import com.patan.gimnasio.domain.ExFromRoutine;
import com.patan.gimnasio.domain.Exercise;
import com.patan.gimnasio.domain.Routine;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class DatabaseTest extends ActivityInstrumentationTestCase2<ExerciseListActivity> {

    private ExerciseListActivity exerciseList;
    private GymnasioDBAdapter db;
    private long id1,id2,id3,idr1,idr2,idr3;
    private ArrayList<Long> ExercisesList = new ArrayList<>();

    public DatabaseTest() {
        super(ExerciseListActivity.class);
    }

    @Test
    public void useAppContext() throws Exception {
         //Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.patan.gimnasio", appContext.getPackageName());
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

    /*Test que comrpueba que la bd no añade ejercicios cuando el nombre se repite*/
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

        Routine r1 = new Routine("Gym1","Rutina1","Objetivo1");
        Routine r2 = new Routine("Gym2","Rutina2","Objetivo2");
        Routine r3 = new Routine("Gym3","Rutina3","Objetivo3");

        int nRoutinesPre = db.getNumberOfRoutines();
        ArrayList<ExFromRoutine> efrArray = new ArrayList<>();

        idr1 = db.createFreemiumRoutine(r1,efrArray);
        idr2 = db.createFreemiumRoutine(r2,efrArray);
        idr3 = db.createFreemiumRoutine(r3,efrArray);

        int nRoutinesPost = db.getNumberOfRoutines();

        db.deleteRoutine(idr1);
        db.deleteRoutine(idr2);
        db.deleteRoutine(idr3);

        assertEquals( nRoutinesPre + 3, nRoutinesPost);
    }

    /*Test que comprueba que la bd añada rutinas premium correctamente*/
   @Test
    public void addPremiumRoutineTest() throws Exception{

        Routine r1 = new Routine("Gym1","Rutina1","Objetivo1");
        Routine r2 = new Routine("Gym2","Rutina2","Objetivo2");
        Routine r3 = new Routine("Gym3","Rutina3","Objetivo3");

        int nRoutinesPre = db.getNumberOfRoutines();
        ArrayList<ExFromRoutine> efrArray = new ArrayList<>();

        idr1 = db.createPremiumRoutine(r1,efrArray);
        idr2 = db.createPremiumRoutine(r2,efrArray);
        idr3 = db.createPremiumRoutine(r3,efrArray);

        int nRoutinesPost = db.getNumberOfRoutines();

       db.deleteRoutine(idr1);
       db.deleteRoutine(idr2);
       db.deleteRoutine(idr3);

        assertEquals( nRoutinesPre + 3, nRoutinesPost);
    }

    /*Test que comprueba que la bd saca rutinas correctamente*/

    @Test
    public void getRoutineTest() throws Exception{
        Routine r1 = new Routine("Gym1","Rutina1","Objetivo1");
        ArrayList<ExFromRoutine> efrArray = new ArrayList<>();

        idr1 = db.createFreemiumRoutine(r1,efrArray);

        Cursor c = db.fetchRoutine(idr1);
        int name_row = c.getColumnIndex(GymnasioDBAdapter.KEY_RO_NAME);
        String name_name = c.getString(name_row);

        db.deleteRoutine(idr1);

        assertTrue(name_name.equals("Rutina1"));
    }

    /*Test que comprueba que la bd elimina rutinas correctamente*/
    @Test
    public void deleteRoutineTest_existe() throws Exception {

        int nRoutinesPre = db.getNumberOfRoutines();

        Routine r1 = new Routine("Gym1","Rutina1","Objetivo1");

        ArrayList<ExFromRoutine> efrArray = new ArrayList<>();

        idr1 = db.createFreemiumRoutine(r1,efrArray);
        db.deleteRoutine(idr1);

        int nRoutinesPost = db.getNumberOfRoutines();

        assertEquals( nRoutinesPre, nRoutinesPost);
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
        Routine r1 = new Routine("Gym1","Rutina1","Objetivo1");
        ArrayList<ExFromRoutine> efrArray = new ArrayList<>();

        idr1 = db.createFreemiumRoutine(r1,efrArray);

        r1 = new Routine("new_Gym1","Rutina1","Objetivo1");
        db.updateFreemiumRoutine(idr1,r1,efrArray);

        Cursor c = db.fetchRoutine(idr1);
        int gym_row = c.getColumnIndex(GymnasioDBAdapter.KEY_RO_GYM);
        String gym_name = c.getString(gym_row);

        db.deleteRoutine(idr1);

        assertTrue(gym_name.equals("new_Gym1"));
    }

    /*Test que comprueba que la bd modifica el campo objetivo de una rutina freemium*/
    @Test
    public void updateObjRuotineFreemium() throws Exception{
        Routine r1 = new Routine("Gym1","Rutina1","Objetivo1");
        ArrayList<ExFromRoutine> efrArray = new ArrayList<>();

        idr1 = db.createFreemiumRoutine(r1,efrArray);

        r1 = new Routine("Gym1","Rutina1","new_Objetivo1");
        db.updateFreemiumRoutine(idr1,r1,efrArray);

        Cursor c = db.fetchRoutine(idr1);
        int obj_row = c.getColumnIndex(GymnasioDBAdapter.KEY_RO_OBJ);
        String obj_name = c.getString(obj_row);

        db.deleteRoutine(idr1);

        assertTrue(obj_name.equals("new_Objetivo1"));
    }

    /*Test que comprueba que la bd modifica el campo nombre de una rutina freemium*/
    @Test
    public void updateNameRoutineFreemium() throws Exception{
        Routine r1 = new Routine("Gym1","Rutina1","Objetivo1");
        ArrayList<ExFromRoutine> efrArray = new ArrayList<>();

        idr1 = db.createFreemiumRoutine(r1,efrArray);

        r1 = new Routine("Gym1","new_Rutina1","Objetivo1");
        db.updateFreemiumRoutine(idr1,r1,efrArray);

        Cursor c = db.fetchRoutine(idr1);
        int name_row = c.getColumnIndex(GymnasioDBAdapter.KEY_RO_NAME);
        String name_name = c.getString(name_row);

        db.deleteRoutine(idr1);

        assertTrue(name_name.equals("new_Rutina1"));
    }

    /*Test que comprueba que la bd modifica el campo ejercicios de una rutina freemium*/
    @Test
    public void updateExsRuotineFreemium() throws Exception{
        Routine r1 = new Routine("Gym1","Rutina1","Objetivo1");
        ArrayList<ExFromRoutine> efrArray = new ArrayList<>();

        idr1 = db.createFreemiumRoutine(r1,efrArray);

        efrArray.add(new ExFromRoutine(23,1,1,1.0));
        db.updateFreemiumRoutine(idr1,r1,efrArray);

        Cursor c = db.getExercisesFromRoutine(idr1);
        int n2 = c.getCount();

        db.deleteRoutine(idr1);

        assertTrue(n2 == 1);
    }

    /*Test que comprueba que el método getNumberOfRoutines devuelve el numero correcto*/
    @Test
    public void getNumberOfRoutinesTest() throws Exception{

        Cursor c1 = db.fetchFreemiumRoutines();
        int n1 = c1.getCount();

        Routine r1 = new Routine("Gym1","Rutina1","Objetivo1");
        ArrayList<ExFromRoutine> efrArray = new ArrayList<>();

        idr1 = db.createFreemiumRoutine(r1,efrArray);

        Cursor c2 = db.fetchFreemiumRoutines();
        int n2 = c2.getCount();

        assertTrue(n1 +1 == n2);
    }

    /*Test que comprueba que la bd devuelve la rutina según el nombre cuando existe*/
    @Test
    public void getRoutineByNameTest() throws Exception{

        Routine r1 = new Routine("Gym1","Rutina1","Objetivo1");
        ArrayList<ExFromRoutine> efrArray = new ArrayList<>();

        idr1 = db.createFreemiumRoutine(r1,efrArray);

        Cursor c = db.getRoutineByName("Rutina1");

        db.deleteRoutine(idr1);

        assertTrue(c != null);
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

package com.patan.gimnasio;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;


@RunWith(AndroidJUnit4.class)
public class ExerciseViewTest extends ActivityInstrumentationTestCase2<ExerciseViewActivity> {

    private ExerciseViewActivity exerciseView;
    private GymnasioDBAdapter db;
    private Exercise e;
    private long id,id2,id3;

    public ExerciseViewTest() {
        super(ExerciseViewActivity.class);
    }


    @Before
    public void setUp() throws Exception {
        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        exerciseView = getActivity();
        db = exerciseView.getBD();
        /*ArrayList<String> tags = new ArrayList<>();
        tags.add("em");tags.add("erg");
        e = new Exercise("test","musculito","bueno aqui una descripcion","ruta",tags);
        long id = db.createExercise(e);*/
    }

    /*Test que comprueba que la actividad muestra bien el ejercicio corresponeidnte*/
    @Test
    public void showExerciseTest() throws Exception{
        ArrayList<String> tags = new ArrayList<>();
        tags.add("#tag");tags.add("tageo");
        e = new Exercise("test","musculito","bueno aqui una descripcion","ruta",tags);

        id = db.createExercise(e);

        Cursor c = db.fetchExercise(id);

        String[] tagsplit = c.getString(5).split(",");

        ArrayList<String> taglist = new ArrayList<String>(Arrays.asList(tagsplit));


        Exercise newE = new Exercise(c.getString(1),c.getString(3),c.getString(2),c.getString(4),taglist);


        assertEquals(id,c.getInt(0));

    }

    /*Test que comprueba que la bd devuelva bien el ejercicio al buscarlo por nombre*/
    @Test
    public void checkExerciseByName() throws Exception{
        ArrayList<String> tags = new ArrayList<>();
        tags.add("test");tags.add("test");
        Exercise e2 = new Exercise("test","musculito","bueno aqui una descripcion","ruta",tags);
        id2 = db.createExercise(e2);

        ArrayList<String> error = new ArrayList<>();
        error.add("t");error.add("rr");
        e = new Exercise("errorciro","mal","no tiene que dar este","ruta",error);
         id = db.createExercise(e);

        Cursor c = db.getExerciseByName(e2.getName());

        String[] tagsplit = c.getString(5).split(",");

        ArrayList<String> taglist = new ArrayList<String>(Arrays.asList(tagsplit));

        Exercise newE = new Exercise(c.getString(1),c.getString(3),c.getString(2),c.getString(4),taglist);


        assertTrue(c.getString(1).equals(e2.getName()));
    }

    /*Test que comprueba que la bd devuelva bien el ejercicio al buscarlo por musculo*/
    @Test
    public void checkExerciseByMuscle() throws Exception{
        ArrayList<String> tags = new ArrayList<>();
        tags.add("uhh");tags.add("uu");
        Exercise e2 = new Exercise("test","musculito","bueno aqui una descripcion","ruta",tags);
        id = db.createExercise(e2);

        ArrayList<String> error = new ArrayList<>();
        error.add("tt");error.add("tes");
        e = new Exercise("errorciro","mal","no tiene que dar este","ruta",error);
        id2 = db.createExercise(e);

        Cursor c = db.getExercisesByMuscle(e2.getMuscle());

        String[] tagsplit = c.getString(5).split(",");

        ArrayList<String> taglist = new ArrayList<String>(Arrays.asList(tagsplit));

        Exercise newE = new Exercise(c.getString(1),c.getString(3),c.getString(2),c.getString(4),taglist);

        assertTrue(c.getString(3).equals(e2.getMuscle()));;
    }


    /*Test que comprueba que la bd devuelva bien los ejercicios al buscar por tag*/
    @Test
    public void checkExercisesByTag() throws Exception{
        ArrayList<String> tags = new ArrayList<>();
        tags.add("em");tags.add("erg");
        Exercise e2 = new Exercise("test","musculito","bueno aqui una descripcion","ruta",tags);
        id = db.createExercise(e2);

        ArrayList<String> error = new ArrayList<>();
        error.add("em");error.add("erg");
        e = new Exercise("errorciro","bien","bien","ruta",error);
        id2 = db.createExercise(e);

        ArrayList<String> tres = new ArrayList<>();
        error.add("no");error.add("cuenta");
        Exercise e3 = new Exercise("errorciro","mal","no tiene que dar este","ruta",tres);
        id3 = db.createExercise(e3);

        Cursor c = db.getExercisesByTag("erg");
        assertEquals(2,c.getCount());
    }

    @After
    public void tearDown() throws Exception {
        db.deleteExercise(id);
        db.deleteExercise(id2);
        db.deleteExercise(id3);
        super.tearDown();
    }

}

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
public class ExerciseListTest extends ActivityInstrumentationTestCase2<ExerciseListActivity> {

    private ExerciseListActivity exerciseList;
    private GymnasioDBAdapter db;
    private long id1,id2,id3;
    ArrayList<Integer> ex = new ArrayList<>();

    public ExerciseListTest() {
        super(ExerciseListActivity.class);
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        exerciseList = getActivity();
        db = exerciseList.getGymnasioDbAdapter();
        ex.add(1);
        ex.add(2);
    }

    //Test that checks if the activity shows every exercise on db.
    @Test
    public void listExerciseTest() throws Exception{
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

    @After
    public void tearDown() throws Exception {
        db.deleteExercise(id1);
        db.deleteExercise(id2);
        db.deleteExercise(id3);
        super.tearDown();
    }
}

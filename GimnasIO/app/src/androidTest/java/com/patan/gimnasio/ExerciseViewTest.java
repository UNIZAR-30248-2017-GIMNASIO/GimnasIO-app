package com.patan.gimnasio;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.test.RenamingDelegatingContext;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import android.content.Intent;
import android.database.Cursor;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.ImageView;
import android.widget.TextView;

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

    private Context context;
    private ExerciseViewActivity exerciseView;
    private GymnasioDBAdapter db;
    private Exercise e,e2,e3;
    private long id,id2,id3,id4;
    private TextView name;
    private TextView muscle;
    private TextView tags;
    private ImageView image;
    private TextView desc;

    public ExerciseViewTest() {
        super(ExerciseViewActivity.class);
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();
        Intent addEvent = new Intent();
        addEvent.setClassName("com.patan.gimnasio", "com.patan.gimnasio.ExerciseViewActivity");
        addEvent.putExtra("ID", 0);
        setActivityIntent(addEvent);
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        exerciseView = this.getActivity();
        db = exerciseView.getBD();
        //Insertamos ejs

        ArrayList<String> tags = new ArrayList<>();
        tags.add("em");tags.add("erg");
        e = new Exercise("test","musculito","bueno aqui una descripcion","ruta",tags);
        id = db.createExercise(e);
        ArrayList<String> tags2 = new ArrayList<>();
        tags2.add("test");tags2.add("test");
        e2 = new Exercise("test2","musculito","bueno aqui una descripcion","ruta",tags2);
        id2 = db.createExercise(e2);
        ArrayList<String> error = new ArrayList<>();
        error.add("t");error.add("rr");
        e3 = new Exercise("errorciro","mal","no tiene que dar este","ruta",error);
        id3 = db.createExercise(e3);

   }


    /*Test que comprueba que la bd devuelva bien el ejercicio al buscarlo por nombre*/
    @Test
    public void checkExerciseByName() throws Exception{


        Cursor c = db.getExerciseByName(e.getName());

        String[] tagsplit = c.getString(5).split(",");

        ArrayList<String> taglist = new ArrayList<String>(Arrays.asList(tagsplit));

        Exercise newE = new Exercise(c.getString(1),c.getString(3),c.getString(2),c.getString(4),taglist);


        assertTrue(c.getString(1).equals(e.getName()));
    }

    /*Test que comprueba que la bd devuelva bien el ejercicio al buscarlo por musculo*/
    @Test
    public void checkExerciseByMuscle() throws Exception{

        Cursor c = db.getExercisesByMuscle(e2.getMuscle());

        String[] tagsplit = c.getString(5).split(",");

        ArrayList<String> taglist = new ArrayList<String>(Arrays.asList(tagsplit));

        Exercise newE = new Exercise(c.getString(1),c.getString(3),c.getString(2),c.getString(4),taglist);

        assertTrue(c.getString(3).equals(e2.getMuscle()));;
    }


    /*Test que comprueba que la bd devuelva bien los ejercicios al buscar por tag*/
    @Test
    public void checkExercisesByTag() throws Exception{

        Cursor c = db.getExercisesByTag("rr");

        assertEquals(1,c.getCount());
    }

    @After
    public void tearDown() throws Exception {
        db.deleteExercise(id);
        db.deleteExercise(id2);
        db.deleteExercise(id3);
        db.deleteExercise(id4);
        super.tearDown();
    }

}

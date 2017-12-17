package com.patan.gimnasio;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.action.ViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.patan.gimnasio.activities.MainActivity;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.longClick;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;

@RunWith(AndroidJUnit4.class)
public class EspressoTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule =
            new ActivityTestRule<>(MainActivity.class);

    @Test
    public void testVerEjercicios() {
        onView(withId(R.id.imageView2)).perform(click());
    }

    @Test
    public void testVerEjercicio() {
        testVerEjercicios();
        onData(anything()).inAdapterView(withId(R.id.dbExercisesList)).atPosition(0).perform(click());
    }

    @Test
    public void testVerRutinas() {
        onView(withId(R.id.imageView)).perform(click());
    }

    @Test
    public void testCrearModificarEliminarRutina() {
        testVerRutinas();
        onView(withId(R.id.floatingActionButton)).perform(click());
        onView(withId(R.id.nombreRutina)).perform(replaceText("Rutina Espresso"));
        onView(withId(R.id.objetivoRutina)).perform(replaceText("Test Espresso"));
        onView(withId(R.id.action_edit_2)).perform(click());
        Espresso.pressBack();
        Espresso.pressBack();
        onData(anything()).inAdapterView(withId(R.id.dbRoutinesList)).atPosition(0).perform(click());
        onView(withId(R.id.action_edit_1)).perform(click());
        onView(withId(R.id.nombreRutina)).perform(replaceText("Rutina Espresso Editada"));
        onView(withId(R.id.fab)).perform(click());
        onData(anything()).inAdapterView(withId(R.id.dbExercisesList)).atPosition(0).perform(longClick());
        onView(withText("Añadir a rutina")).perform(click());
        onView(withId(R.id.seriesField)).perform(replaceText("2"));
        onView(withId(R.id.repeticionesField)).perform(replaceText("2"));
        onView(withId(R.id.relaxField)).perform(replaceText("2"));
        onView(withId(R.id.buttonAdd)).perform(click());
        onView(withId(R.id.action_edit_2)).perform(click());
        Espresso.pressBack();
        onData(anything()).inAdapterView(withId(R.id.dbRoutinesList)).atPosition(0).perform(longClick());
        onView(withText("Eliminar")).perform(click());
    }


    // TODO: Aqui toda la mierda de Login y todo lo de la zona premium

    /*Metodo que duerme al test durante 3 segundos para facilitar la visualizacion de las acciones
     realizadas */
    private void sleep() {
        try{
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }



}

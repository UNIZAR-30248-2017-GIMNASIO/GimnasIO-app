package com.patan.gimnasio;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.support.test.espresso.Espresso;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.patan.gimnasio.activities.MainActivity;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.longClick;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;

/*
 * Esta clase de test parte del supuesto de que se prueba con la aplicación vacía de rutinas pero con los ejercicios ya descargados.
 * Además la sesión del usuario premium no debe estar inciciada.
 */

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
    public void testCrearModificarEliminarRutinaFreemium() {
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
        onView(withText("Rutina Espresso Editada")).perform(longClick());
        //onData(anything()).inAdapterView(withId(R.id.dbRoutinesList)).atPosition(0).perform(longClick());
        onView(withText("Eliminar")).perform(click());
    }

    @Test
    public void testLoginPremiumAsNormalUserAndLogout() {
        onView(withId(R.id.imageView3)).perform(click());
        onView(withId(R.id.email)).perform(replaceText("EINAGym"));
        onView(withId(R.id.password)).perform(replaceText("kLX4a"));
        onView(withId(R.id.email_sign_in_button)).perform(click());
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText("Cerrar sesión")).perform(click());
    }

    @Test
    public void testLoginPremiumAsTrainerUserAndLogout() {
        onView(withId(R.id.imageView3)).perform(click());
        onView(withId(R.id.email)).perform(replaceText("EINAGym"));
        onView(withId(R.id.password)).perform(replaceText("ZpJ5KG"));
        onView(withId(R.id.email_sign_in_button)).perform(click());
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText("Cerrar sesión")).perform(click());
    }

    @Test
    public void testCrearModificiarEliminarRutinaPremium() {
        onView(withId(R.id.imageView3)).perform(click());
        onView(withId(R.id.email)).perform(replaceText("EINAGym"));
        onView(withId(R.id.password)).perform(replaceText("ZpJ5KG"));
        onView(withId(R.id.email_sign_in_button)).perform(click());

        onView(withId(R.id.floatingActionButton)).perform(click());
        onView(withId(R.id.nombreRutina)).perform(replaceText("Rutina Espresso Premium"));
        onView(withId(R.id.objetivoRutina)).perform(replaceText("Test Espresso Premium"));
        onView(withId(R.id.action_edit_2)).perform(click());
        Espresso.pressBack();
        Espresso.pressBack();
        onData(anything()).inAdapterView(withId(R.id.dbRoutinesList)).atPosition(0).perform(click());
        onView(withId(R.id.action_edit_1)).perform(click());
        onView(withId(R.id.nombreRutina)).perform(replaceText("Rutina Espresso Premium Editada"));
        onView(withId(R.id.fab)).perform(click());
        onData(anything()).inAdapterView(withId(R.id.dbExercisesList)).atPosition(0).perform(longClick());
        onView(withText("Añadir a rutina")).perform(click());
        onView(withId(R.id.seriesField)).perform(replaceText("2"));
        onView(withId(R.id.repeticionesField)).perform(replaceText("2"));
        onView(withId(R.id.relaxField)).perform(replaceText("2"));
        onView(withId(R.id.buttonAdd)).perform(click());
        onView(withId(R.id.action_edit_2)).perform(click());
        Espresso.pressBack();
        onView(withText("Rutina Espresso Premium Editada")).perform(longClick());
        //onData(anything()).inAdapterView(withId(R.id.dbRoutinesList)).atPosition(0).perform(longClick());
        onView(withText("Eliminar")).perform(click());
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText("Cerrar sesión")).perform(click());
    }

    @Test
    public void testModificarEjercicioDentroDeRutina() {
        onView(withId(R.id.imageView)).perform(click());
        onView(withId(R.id.floatingActionButton)).perform(click());
        onView(withId(R.id.nombreRutina)).perform(replaceText("Rutina Espresso Testing"));
        onView(withId(R.id.objetivoRutina)).perform(replaceText("Test Espresso Testing"));

        // Añadimos ejercicio
        onView(withId(R.id.fab)).perform(click());
        onData(anything()).inAdapterView(withId(R.id.dbExercisesList)).atPosition(0).perform(longClick());
        onView(withText("Añadir a rutina")).perform(click());
        onView(withId(R.id.seriesField)).perform(replaceText("2"));
        onView(withId(R.id.repeticionesField)).perform(replaceText("2"));
        onView(withId(R.id.relaxField)).perform(replaceText("2"));
        onView(withId(R.id.buttonAdd)).perform(click());

        // Modificamos ejercicio
        onData(anything()).inAdapterView(withId(R.id.routineEditList)).atPosition(0).perform(longClick());
        onView(withText("Editar ejercicio")).perform(click());
        onView(withId(R.id.seriesField)).perform(replaceText("3"));
        onView(withId(R.id.repeticionesField)).perform(replaceText("3"));
        onView(withId(R.id.relaxField)).perform(replaceText("3"));
        onView(withId(R.id.buttonAdd)).perform(click());

        // Guardamos y eliminamos la rutina
        onView(withId(R.id.action_edit_2)).perform(click());
        Espresso.pressBack();
        onView(withText("Rutina Espresso Testing")).perform(longClick());
        //onData(anything()).inAdapterView(withId(R.id.dbRoutinesList)).atPosition(0).perform(longClick());
        onView(withText("Eliminar")).perform(click());
    }

    @Test
    public void testMoverEjerciciosDentroDeRutina() {
        onView(withId(R.id.imageView)).perform(click());
        onView(withId(R.id.floatingActionButton)).perform(click());
        onView(withId(R.id.nombreRutina)).perform(replaceText("Rutina Espresso Testing"));
        onView(withId(R.id.objetivoRutina)).perform(replaceText("Test Espresso Testing"));

        // Añadimos ejercicio
        onView(withId(R.id.fab)).perform(click());
        onData(anything()).inAdapterView(withId(R.id.dbExercisesList)).atPosition(0).perform(longClick());
        onView(withText("Añadir a rutina")).perform(click());
        onView(withId(R.id.seriesField)).perform(replaceText("2"));
        onView(withId(R.id.repeticionesField)).perform(replaceText("2"));
        onView(withId(R.id.relaxField)).perform(replaceText("2"));
        onView(withId(R.id.buttonAdd)).perform(click());

        // Añadimos ejercicio
        onView(withId(R.id.fab)).perform(click());
        onData(anything()).inAdapterView(withId(R.id.dbExercisesList)).atPosition(1).perform(longClick());
        onView(withText("Añadir a rutina")).perform(click());
        onView(withId(R.id.seriesField)).perform(replaceText("2"));
        onView(withId(R.id.repeticionesField)).perform(replaceText("2"));
        onView(withId(R.id.relaxField)).perform(replaceText("2"));
        onView(withId(R.id.buttonAdd)).perform(click());

        // Añadimos ejercicios
        onView(withId(R.id.fab)).perform(click());
        onData(anything()).inAdapterView(withId(R.id.dbExercisesList)).atPosition(3).perform(longClick());
        onView(withText("Añadir a rutina")).perform(click());
        onView(withId(R.id.seriesField)).perform(replaceText("2"));
        onView(withId(R.id.repeticionesField)).perform(replaceText("2"));
        onView(withId(R.id.relaxField)).perform(replaceText("2"));
        onView(withId(R.id.buttonAdd)).perform(click());

        // Movemos los ejercicios y eliminamos uno
        onData(anything()).inAdapterView(withId(R.id.routineEditList)).atPosition(0).perform(longClick());
        onView(withText("Bajar ejercicio")).perform(click());
        onData(anything()).inAdapterView(withId(R.id.routineEditList)).atPosition(1).perform(longClick());
        onView(withText("Subir ejercicio")).perform(click());
        onData(anything()).inAdapterView(withId(R.id.routineEditList)).atPosition(1).perform(longClick());
        onView(withText("Eliminar")).perform(click());

        // Guardamos y eliminamos la rutina
        onView(withId(R.id.action_edit_2)).perform(click());
        Espresso.pressBack();
        onView(withText("Rutina Espresso Testing")).perform(longClick());
        //onData(anything()).inAdapterView(withId(R.id.dbRoutinesList)).atPosition(0).perform(longClick());
        onView(withText("Eliminar")).perform(click());
    }

    @Test
    public void testEjecutarRutina() {
        onView(withId(R.id.imageView)).perform(click());
        onView(withId(R.id.floatingActionButton)).perform(click());
        onView(withId(R.id.nombreRutina)).perform(replaceText("Rutina Espresso Testing Ejecutar"));
        onView(withId(R.id.objetivoRutina)).perform(replaceText("Test Espresso Testing Ejecutar"));

        // Añadimos ejercicio
        onView(withId(R.id.fab)).perform(click());
        onData(anything()).inAdapterView(withId(R.id.dbExercisesList)).atPosition(0).perform(longClick());
        onView(withText("Añadir a rutina")).perform(click());
        onView(withId(R.id.seriesField)).perform(replaceText("2"));
        onView(withId(R.id.repeticionesField)).perform(replaceText("2"));
        onView(withId(R.id.relaxField)).perform(replaceText("2"));
        onView(withId(R.id.buttonAdd)).perform(click());

        // Añadimos ejercicio
        onView(withId(R.id.fab)).perform(click());
        onData(anything()).inAdapterView(withId(R.id.dbExercisesList)).atPosition(1).perform(longClick());
        onView(withText("Añadir a rutina")).perform(click());
        onView(withId(R.id.seriesField)).perform(replaceText("2"));
        onView(withId(R.id.repeticionesField)).perform(replaceText("2"));
        onView(withId(R.id.relaxField)).perform(replaceText("2"));
        onView(withId(R.id.buttonAdd)).perform(click());

        // Guardamos la rutina
        onView(withId(R.id.action_edit_2)).perform(click());

        // Ejecutamos la rutina
        onView(withId(R.id.executeButton)).perform(click());

        onView(withId(R.id.startButton)).perform(click());
        onView(withId(R.id.stopButton)).perform(click());
        onView(withId(R.id.startButton)).perform(click());

        onView(withId(R.id.StartDisplay)).perform(swipeLeft());
        onView(withId(R.id.ex_series)).perform(swipeLeft());
        onView(withId(R.id.titleText)).perform(swipeLeft());
        onView(withId(R.id.ex_series)).perform(swipeLeft());
        onView(withId(R.id.titleText)).perform(swipeLeft());

        onView(withId(R.id.finButton)).perform(click());

        Espresso.pressBack();
    }

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

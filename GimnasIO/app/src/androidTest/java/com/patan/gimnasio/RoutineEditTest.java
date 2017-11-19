package com.patan.gimnasio;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;

import org.junit.Before;
import org.junit.runner.RunWith;

import java.util.ArrayList;

@RunWith(AndroidJUnit4.class)
public class RoutineEditTest extends ActivityInstrumentationTestCase2<RoutineEditActivity> {

    private RoutineEditActivity routineEdit;
    private GymnasioDBAdapter db;
    private long id1, id2, id3;
    private String url ="http://10.0.2.2:32001/exercises/";
    ArrayList<Long> ex = new ArrayList<>();

    public RoutineEditTest() {
        super(RoutineEditActivity.class);
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        routineEdit = getActivity();
        db = routineEdit.getGymnasioDbAdapter();
        long i = 1;
        long i2 = 2;
        ex.add(i);  // Se añaden dos ID cualesquiera para simular IDs de ejercicios
        ex.add(i2);
    }

}

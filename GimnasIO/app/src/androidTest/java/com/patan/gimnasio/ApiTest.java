package com.patan.gimnasio;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;

import com.patan.gimnasio.activities.MainActivity;
import com.patan.gimnasio.database.GymnasioDBAdapter;
import com.patan.gimnasio.domain.DBRData;
import com.patan.gimnasio.domain.ExFromRoutine;
import com.patan.gimnasio.domain.Routine;
import com.patan.gimnasio.services.ApiHandler;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;



@RunWith(AndroidJUnit4.class)
public class ApiTest extends ActivityInstrumentationTestCase2<MainActivity> {

    private GymnasioDBAdapter db;
    private MainActivity mList;
    private ApiHandler api;
    private Context mCtx;
    private String id1,id2,id3,idr1,idr2,idr3;

    public ApiTest () {
        super(MainActivity.class);
    }

    @Before
    public void setUp() throws Exception{
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        mList = getActivity();
        db = mList.getGymnasioDbAdapter();
        api = new ApiHandler(mList.getApplicationContext());
    }

   @Test
   public void loginPremium_Test() throws Exception {
        boolean ok = api.loginPremium("EINAGym","ZpJ5KG");
        assertEquals(true,ok);
   }
   @Test
   public void dbData_Test() throws  Exception {
       DBRData data = api.dbData();
       assertEquals(data!= null, true);
   }
    @Test
    public void updateDb_Test() throws  Exception {
        JSONObject json = api.updateDB();
        assertEquals(json!= null, true);
    }
    @Test
    public void updatePremiumDb_Test() throws Exception {
        JSONObject json = api.updatePremiumDB("EINAGym", "ZpJ5KG");
        assertEquals(json!=null,true);
    }
    @Test
    public void downloadImg_Test() throws Exception {
        Bitmap bitmap = api.downloadIMG("Aperturasconmancuernaenbancoinclinado");
        assertEquals(bitmap!=null,true);
    }

    @Test
    public void createPremiumRoutine_Test() throws Exception{
        Routine r1 = new Routine("EINAGym","Rutina1","Objetivo1");
        Routine r2 = new Routine("EINAGym","Rutina2","Objetivo2");
        Routine r3 = new Routine("EINAGym","Rutina3","Objetivo3");

        JSONObject json = api.updatePremiumDB("EINAGym", "ZpJ5KG");
        int nRoutinesPre = json.length();
        ArrayList<ExFromRoutine> efrArray = new ArrayList<>();

        JSONObject json1 = api.createPremiumRoutine(r1, efrArray);
        JSONObject json2 = api.createPremiumRoutine(r2, efrArray);
        JSONObject json3 = api.createPremiumRoutine(r3, efrArray);
        id1 = json1.getString("id");
        id2 = json2.getString("id");
        id3 = json3.getString("id");
        json = api.updatePremiumDB("EINAGym", "ZpJ5KG");
        int nRoutinesPost = json.length();
        assertEquals( nRoutinesPre + 3, nRoutinesPost);

        r1 = new Routine("EINAGym","Rutina11","Objetivo1");
        r2 = new Routine("EINAGym","Rutina12","Objetivo2");
        r3 = new Routine("EINAGym","Rutina13","Objetivo3");

        json = api.updatePremiumDB("EINAGym", "ZpJ5KG");
        nRoutinesPre = json.length();
        efrArray = new ArrayList<>();

        api.updatePremiumRoutine("Rutina1",id1,r1,efrArray);
        api.updatePremiumRoutine("Rutina2",id1,r1,efrArray);
        api.updatePremiumRoutine("Rutina3",id1,r1,efrArray);

        json = api.updatePremiumDB("EINAGym", "ZpJ5KG");

        nRoutinesPost = json.length();
        assertEquals( nRoutinesPre , nRoutinesPost);
        json = api.updatePremiumDB("EINAGym", "ZpJ5KG");
        nRoutinesPre = json.length();
        api.deletePremiumRoutine("Rutina1", -1,id1);
        api.deletePremiumRoutine("Rutina2", -1,id2);
        api.deletePremiumRoutine("Rutina3", -1,id3);
        json = api.updatePremiumDB("EINAGym", "ZpJ5KG");
        nRoutinesPost = json.length();
        assertEquals( nRoutinesPre - 3, nRoutinesPost);

    }
}

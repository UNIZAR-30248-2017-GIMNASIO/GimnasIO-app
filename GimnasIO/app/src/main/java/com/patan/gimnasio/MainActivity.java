package com.patan.gimnasio;

import android.database.sqlite.SQLiteDatabase;
import android.nfc.Tag;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;


import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;





public class MainActivity extends AppCompatActivity {
    private static final String DBname = "rutinasDB.db";
    //String url = "localhost:3000/ejercicios"; //ejemplo, a ver si despues del server pongo la correcta

    /*JsonObjectRequest obj = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
                Coiar el codigo de añadir a BD y parseo del JSON
                //db.actualizarejercicios(response);
        }
    }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.e("JSON","Error al pedir el JSON");
        }
    });*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView texto = (TextView) this.findViewById(R.id.texto);




        DBHandler db = new DBHandler(this);


        //if(!db.exists(this)) { //db doesn't exist, let's ask for JSON info

            //añadir aqui el JSON despues de preguntar
            List<String> musculos1 = new ArrayList<String>();
            List<String> musculos2 = new ArrayList<String>();

            List<String> tags1 = new ArrayList<String>();
            List<String> tags2 = new ArrayList<String>();


            musculos1.add("biceps");
            musculos1.add("triceps");
            musculos2.add("biceps");
            musculos2.add("gemelo");

            tags1.add("brazo");
            tags1.add("fuerza");
            tags2.add("brazo");
            tags2.add("pierna");

            Exercise uno = new Exercise("prueba1",musculos1,"la mano arriba","/path",tags1);
            Exercise dos = new Exercise("prueba2",musculos2,"cintura sola","/path",tags2);
            List<Exercise> e = new ArrayList<Exercise>();
            e.add(uno);e.add(dos);

            db.actualizarEjercicios(e);

        //}

        List<Exercise> ej = db.getExercises();
            for (Exercise ee : ej) {
                texto.append(ee.getName() + " " + ee.getDescription() + " " + ee.getImage());
                for (String m : ee.getMuscle()) texto.append(m + " ");
                for (String t : ee.getTags()) texto.append(t + " \n");
            }


    }
}

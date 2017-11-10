package com.patan.gimnasio;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private GymnasioDBAdapter db;
    private String url = "http://10.0.2.2:32001/update/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = new GymnasioDBAdapter(this);
        db.open();
        final Cursor c = db.checkForUpdates();
        int countej = c.getCount();
        RequestQueue mQueue = Volley.newRequestQueue(this);
        if (c.moveToFirst()){
            final String lUL = c.getString(c.getColumnIndex("lastUpdate"));
            final int fI = c.getInt(c.getColumnIndex("firstInstalation"));
            final int id = c.getInt(c.getColumnIndex("_id"));
            // Consulta al server
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            String lUR = null;
                            try {
                                lUR = response.getString("lastUpdate");
                                lUR = lUR.replace('T','_');
                                lUR = lUR.substring(0,19);
                                //Launch update
                                //IF FECHAANDROID < FECHASERVER OR FIRSTINSTALATION = 1
                                if (!lUR.equals(lUL) || fI==1) {
                                    Log.d("INFO", "Update needed because new " +
                                            "installation or new remote db");
                                    goToLoadingActivity();
                                    db.updateLastUpdate(id);
                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("TAG", error.getMessage(), error);
                }


            }){

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("u", "adminGPS");
                    params.put("p", "gimnasIOapp");
                    return params;
                }

            };
            mQueue.add(jsonObjectRequest);

        }
        c.close();

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }
    public void goToLoadingActivity() {
        //Intent intent = new Intent(this, LoadingActivity.class);
        //startActivity(intent);
        Log.d("Update", "Succesfully Updated");
    }


    public void goToExercise(View v) {
        Intent intent = new Intent(this, ExerciseListActivity.class);
        startActivity(intent);
    }

    public void goToRoutine(View v) {
        Intent intent = new Intent(this, RoutineListActivity.class);
        startActivity(intent);
    }

    public void goToPremium(View v) {
        Intent intent = new Intent(this, PremiumLoginActivity.class);
        startActivity(intent);
    }
}

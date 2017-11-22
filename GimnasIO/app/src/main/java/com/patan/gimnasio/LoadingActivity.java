package com.patan.gimnasio;

import android.Manifest;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LoadingActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {


    private int status = 0;
    private int total = 0;
    private TextView state;
    /**
     * Id to identify a contacts permission request.
     */
    private static final int REQUEST_RW = 0;

    /**
     * Permissions required to read and write contacts. Used by the {@link ContactsFragment}.
     */
    private static String[] EXTERNAL_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private GymnasioDBAdapter db;
    /**
     * Root of the layout of this Activity.
     */
    private View mLayout;
    private TextView texto;
    private ProgressBar barra;

    private String ruta = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "/GymnasIOapp";

    public GymnasioDBAdapter getGymnasioDbAdapter() {
        return db;
    }
    public Context getContext() {return this;}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        //barra = (ProgressBar) findViewById(R.id.progressBar);
        //texto = (TextView) findViewById(R.id.descargando);
        state = (TextView) findViewById(R.id.descargando);
        state.setText("Descargando");

        String url ="http://10.0.2.2:32001/exercises/";
        RequestQueue mQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //texto.setText("Descargando 0 ejercicios de " + response.length());
                            updateDatabase(response);
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
                        params.put("user", "adminGPS");
                        params.put("pwd", "gimnasIOapp");
                        return params;
                    }

        };
        mQueue.add(jsonObjectRequest);
    }

    private void updateDatabase(JSONObject list) throws JSONException {
        db = new GymnasioDBAdapter(this);
        db.open();
        total = list.length();
        for (int i = 0; i < list.length(); i++) {
            //texto.setText("Descargando 0 ejercicios de " + list.length());
            //barra.setProgress(list.length(),i);
            JSONObject ejercicio = list.getJSONObject(i + "");
            Log.d("" + i, ejercicio.toString());
            ArrayList<String> tags = new ArrayList<String>();
            String tag = ejercicio.getString("tag");
            String[] parts = tag.split(",");
            for (String s : parts) {
                tags.add(s);
            }
            String rutaEj = ruta + "/" + ejercicio.getString("name").replaceAll("\\s+", "") + ".jpg";
            final Exercise e = new Exercise(ejercicio.getString("name"),
                    ejercicio.getString("muscle"),
                    ejercicio.getString("description"),
                    rutaEj,
                    tags);
            new MyTask().execute(ejercicio.getString("name"));
            db.createExercise(e);
        }
        Log.d("Update", "Database updated");

        //this.finish();
    }
    private void SaveImage(Bitmap finalBitmap, String s) {
            File myDir = new File(ruta);
            Log.w("ImgSave",ruta);
            myDir.mkdirs();
            String fname = s+".jpg";
            File file = new File (myDir, fname);
            if (file.exists ()) file.delete ();
            try {
                FileOutputStream out = new FileOutputStream(file);
                finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                out.flush();
                out.close();
                Log.d("SAVED","Image with name "+s+" saved on filesystem");
                status++;
                if (status == total) {
                    state.setText("Descarga finalizada");
                    Log.d("DWL", "DOWNLOAD AND STORAGE FINISHED");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

    }

    private class MyTask extends AsyncTask<String, Integer, String> {
        private String result = "";
        // Runs in UI before background thread is called
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // Do something like display a progress bar
        }

        // This is run in a background thread
        @Override
        protected String doInBackground(String... params) {
            final String image = params[0].replaceAll("\\s+","");
            String url = "http://10.0.2.2:32001/exercises/download";
            Log.d("ImgDwn","Trying to download "+image);
            RequestQueue mQueue = Volley.newRequestQueue(LoadingActivity.this);
            //Retrieves an image specified by the URL, displays it in the UI.
            ImageRequest r = new ImageRequest(url, new Response.Listener<Bitmap>() {
                @Override
                public void onResponse(Bitmap bitmap) {
                    Log.d("ImgDwn","Image from "+image+" downloaded");
                    SaveImage(bitmap, image);
                }
            }, 1028,1028,Bitmap.Config.ARGB_8888,
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            Log.e("ERROR from " + image, "http Volley request failed!", volleyError);
                        }
                    }) {

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("image", image+".jpg");
                    return params;
                }
            };
            mQueue.add(r);
            return "DONE";
        }

        // This is called from background thread but runs in UI
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

            // Do things like update the progress bar
        }

        // This runs in UI when background thread finishes
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            // Do things like hide the progress bar or change a TextView
        }
    }

}

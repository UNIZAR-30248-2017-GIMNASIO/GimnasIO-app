package com.patan.gimnasio;

import android.Manifest;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

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

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LoadingActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {
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

    private String ruta = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "/GymnasIOapp";

    public GymnasioDBAdapter getGymnasioDbAdapter() {
        return db;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        String url ="http://10.0.2.2:32001/exercises/";
        RequestQueue mQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
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
        db= new GymnasioDBAdapter(this);
        db.open();
        for (int i = 0;i<list.length() ; i++) {
            JSONObject ejercicio = list.getJSONObject(i+"");
            Log.w("ELEMENTO"+i,ejercicio.toString());
            ArrayList<String> tags = new ArrayList<String>();
            String tag = ejercicio.getString("tag");
            String[] parts = tag.split(",");
            for (String s: parts) {
                tags.add(s);
            }
            //String ruta = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
            String rutaEj = ruta +"/"+ ejercicio.getString("name") + ".jpg";
            Exercise e = new Exercise(ejercicio.getString("name"),
                    ejercicio.getString("muscle"),
                    ejercicio.getString("description"),
                            rutaEj,
                    tags);
            boolean ok = getImage(e.getName());
            if (!ok) break;
            db.createExercise(e);
        }
        Log.d("Update","Database updated");

        this.finish();
    }
    private boolean getImage(String s) {
        final String image = s;
        final boolean error = false;
        String url = "http://10.0.2.2:32001/exercises/download";
        Log.w("ImgDwn","Trying to download "+image);
        RequestQueue mQueue = Volley.newRequestQueue(this);
        //Retrieves an image specified by the URL, displays it in the UI.
        ImageRequest r = new ImageRequest(url, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap bitmap) {
                Log.w("ImgDwn","Image from "+image+" downloaded");
                SaveImage(bitmap, image);
            }
        }, 1028,1028,Bitmap.Config.ARGB_8888,
                new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("ERROR from" + image, "http Volley request failed!", volleyError);
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
        return true;
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

            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.d("SAVED","Image with name "+s+" saved on filesystem");
    }
}

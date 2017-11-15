package com.patan.gimnasio;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


// En esta actividad se mostrara toda la informacion asociada a un ejercicio seleccionado desde ExerciseListActivity
public class ExerciseViewActivity extends AppCompatActivity {
    private GymnasioDBAdapter db;
    private TextView name;
    private TextView muscle;
    private TextView tags;
    private ImageView image;
    private TextView desc;

    public GymnasioDBAdapter getBD() {
        return db;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercises_view);
        db = new GymnasioDBAdapter(this);
        db.open();

        /*name = (TextView) findViewById(R.id.name);
        muscle = (TextView) findViewById(R.id.muscle);
        image = (ImageView) findViewById(R.id.image);
        tags = (TextView) findViewById(R.id.tags);
        desc  = (TextView) findViewById(R.id.ScrollText);


        long idEj = getIntent().getLongExtra("ID",0);

        Cursor c = db.fetchExercise(idEj);

        name.setText(c.getString(1));
        desc.setText(c.getString(2));
        muscle.append(" " + c.getString(3));

        File pathtest = new File(c.getString(4));
        Bitmap myBitmap = BitmapFactory.decodeFile(pathtest.getAbsolutePath());
        image.setImageBitmap(myBitmap);



        String[] tagsplit = c.getString(5).split(",");

        for(String s : tagsplit) tags.append(" " + s);*/




    }
}

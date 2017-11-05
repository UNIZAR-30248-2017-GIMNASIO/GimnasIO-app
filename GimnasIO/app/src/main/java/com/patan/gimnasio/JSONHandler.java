package com.patan.gimnasio;

import android.util.JsonReader;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class JSONHandler {

    public JSONHandler() {};

    /*La funcion devuelve una lista con todos los ejercicios en el parametro InputStream*/
    public List<Exercise> readJSONinput(InputStream in) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        try {
            return parseJSON(reader);
        } finally {
            reader.close();
        }
    }


    public List<Exercise> parseJSON(JsonReader reader) throws IOException{
        List<Exercise> ejercicios = new ArrayList<>();

        reader.beginArray();
        while (reader.hasNext()) {
            ejercicios.add(readExercise(reader));
        }
        reader.endArray();
        return ejercicios;

    }

    public Exercise readExercise(JsonReader reader) throws IOException{
        //name: args[0], muscle: args[1], description: args[2], images: destiny, tag: args[4]}],
        String name = "";
        List<String> muscle = new ArrayList<>();
        String description= "";
        String image= "";
        List<String> tags = new ArrayList<>();

        reader.beginObject();
        while (reader.hasNext()) {
            String leido = reader.nextName();
            if ( leido.equals("name")) name = reader.nextString();
            else if ( leido.equals("muscle")) muscle = readArray(reader);
            else if (leido.equals("description")) description = reader.nextString();
            else if (leido.equals("images")) image = reader.nextString();
            else if (leido.equals("tag")) tags = readArray(reader);
            else reader.skipValue();
        }
        reader.endObject();
        return new Exercise(name,muscle,description,image,tags);
    }

    public List<String> readArray(JsonReader reader) throws IOException{
        List<String> res = new ArrayList<>();
        reader.beginArray();
        while (reader.hasNext()) {
            res.add(reader.nextString());
        }
        reader.endArray();
        return res;
    }
}

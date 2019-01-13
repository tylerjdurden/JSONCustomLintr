package objects;

import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class JSONFile {

    private org.json.JSONObject object;

    public JSONFile(File file) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        JSONTokener jsonTokener = new JSONTokener(bufferedReader);
        object = new org.json.JSONObject(jsonTokener);
        bufferedReader.close();
    }

    public JSONObject getObject() {
        return new JSONObject(null, null, object);
    }
}
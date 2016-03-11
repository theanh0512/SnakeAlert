package fyp.theanh.snakealertapplication.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import fyp.theanh.snakealertapplication.activities.UploadActivity;
import fyp.theanh.snakealertapplication.fragments.FragmentMain;
import fyp.theanh.snakealertapplication.models.Snake;
import fyp.theanh.snakealertapplication.models.SnakeKind;

/**
 * Created by user on 12/24/2015.
 */
public class GetDataTask extends AsyncTask<String, Void, List<String>> {
    String result = "";
    String mUrlString = "http://www.snakealertapp.com/query_snake_table.php";
    private final Context mcontext;
    private ArrayAdapter<String> mSnakeAdapter;

    public GetDataTask(Context context, ArrayAdapter<String> snakeAdapter) {
        mcontext = context;
        mSnakeAdapter = snakeAdapter;
    }

    public GetDataTask(Context context, String urlString) {
        mcontext = context;
        mUrlString = urlString;
    }


    @Override
    protected List<String> doInBackground(String... strings) {
        List<String> snakeArray = new ArrayList<String>();
        URL url = null;
        try {
            url = new URL(mUrlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            InputStream is = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();

            String line = null;
            try {
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            result = sb.toString();

            //parse json data
            parseJsonData(snakeArray);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return snakeArray;
    }

    private void parseJsonData(List<String> snakeArray) {
        try {
            JSONArray jArray = new JSONArray(result);
            snakeArray.clear();
            FragmentMain.snakeList.clear();
            UploadActivity.snakeKinds.clear();
            for (int i = 0; i < jArray.length() - 1; i++) {
                String s = "";
                JSONObject json = jArray.getJSONObject(i);
                String name = json.getString("name");
                String specification = json.getString("specification");
                String firstAid = json.getString("first_aid");
                String imagePath = json.getString("image_path");
                if (json.has("latitude")) {
                    String location = json.getString("location");
                    double latitude = json.getDouble("latitude");
                    double longitude = json.getDouble("longitude");
                    String separatedName = separateName(name);
                    s = s + "Name : " + separatedName + "  Found at " + location;
                    snakeArray.add(s);
                    FragmentMain.snakeList.add(new Snake(name, location, specification, firstAid, imagePath, latitude, longitude));
                } else {
                    UploadActivity.snakeKinds.add(new SnakeKind(name, specification, firstAid, imagePath));
                }
            }

        } catch (Exception e) {
            // TODO: handle exception
            Log.e("log_tag", "Error Parsing Data " + e.toString());
        }
    }

    @Override
    protected void onPostExecute(List<String> strings) {
        if (strings != null && mSnakeAdapter !=
                null) {
            mSnakeAdapter.clear();
            for (String snakeStr : strings) {

                mSnakeAdapter.add(snakeStr);
            }
            // New data is back from the server.  Hooray!

        }
    }

    private String separateName(String name){
        String nameSeparated = name.replaceAll("(\\p{Ll})(\\p{Lu})","$1 $2");
        return nameSeparated;
    }
}

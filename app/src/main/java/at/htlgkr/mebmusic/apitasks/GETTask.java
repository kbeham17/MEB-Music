package at.htlgkr.mebmusic.apitasks;

import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class GETTask extends AsyncTask<String,Integer,String> {
    //AIzaSyC583ei0acTyI6_M1bKLeserE8nJjecrAg
    String url;
    String jsonResponse = "";
    ArrayList<String> arrayList;


    public GETTask(String url) {
        this.url = url;
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("GET");
            InputStream in = new BufferedInputStream(conn.getInputStream());
            jsonResponse = convertStreamToString(in);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonResponse;
    }


    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
    }


    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
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
        return sb.toString();

    }

    public String getJsonResponse(){
        return jsonResponse;
    }

}



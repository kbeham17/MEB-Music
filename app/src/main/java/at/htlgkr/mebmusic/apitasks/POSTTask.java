package at.htlgkr.mebmusic.apitasks;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class POSTTask extends AsyncTask<String, Integer, String> {
    String URL;
    String jsonResponse="";
    public POSTTask(String URL) {
        this.URL=URL;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }

    @Override
    protected String doInBackground(String... strings) {
        //String jsonString = strings[0];
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(URL).openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");
//            connection.setFixedLengthStreamingMode(jsonString.getBytes().length);
//            connection.getOutputStream().write(jsonString.getBytes());
            connection.getOutputStream().flush();

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK ||
                    responseCode == HttpURLConnection.HTTP_CREATED) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                jsonResponse = readResponseStream(reader);
            } else {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                jsonResponse = readResponseStream(reader);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonResponse;
    }

    private String readResponseStream(BufferedReader reader) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        String line = "";
        while ((line=reader.readLine()) != null) {
            stringBuilder.append(line);
        }
        return stringBuilder.toString();
    }

    public String getJsonResponse()
    {
        return jsonResponse;
    }
}
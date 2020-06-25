package at.htlgkr.mebmusic.apitasks;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class PUTTask extends AsyncTask<String, Integer, String> {
    String URL;
    String jsonResponse;

    public PUTTask(String URL) {
        this.URL = URL;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... strings) {
        String jsonString = strings[0];
        jsonResponse = "";
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(URL).openConnection();
            connection.setRequestMethod("PUT");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "applcation/json");
            connection.setFixedLengthStreamingMode(jsonString.getBytes().length);
            connection.getOutputStream().write(jsonString.getBytes());
            connection.getOutputStream().flush();

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK ||
                    responseCode == HttpURLConnection.HTTP_CREATED) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                jsonResponse = readResponseStream(reader);
                reader.close();
            } else {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                jsonResponse = readResponseStream(reader);
                reader.close();
            }
        } catch (IOException e) {
        }
        return jsonResponse;
    }

    private String readResponseStream(BufferedReader reader) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        String line = "";
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
        }
        return stringBuilder.toString();
    }

    public String getJsonResponse() {
        return jsonResponse;
    }
}
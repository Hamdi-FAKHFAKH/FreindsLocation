package issatso.hamdi.freindslocation;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;

public class JSONParser {

    String charset = "UTF-8";
    HttpURLConnection conn;
    DataOutputStream wr;
    StringBuilder result;
    URL urlObj;
    JSONObject jObj = null;
    StringBuilder sbParams;
    String paramsString;

    public JSONObject makeRequest(String url) {

        try {
            urlObj = new URL(url);
            /* ouvrir une connection HTTP pour un url */
            conn = (HttpURLConnection) urlObj.openConnection();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            /* Receive the response from the server with stream */
            InputStream in = new BufferedInputStream(conn.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            /* StringBuilder : Une suite de caractères mutable */
            result = new StringBuilder();
            /* read data from the stream */
            String line;
            while ((line = reader.readLine()) != null) {
                /* add data to StringBuilder */
                result.append(line);
            }

            /* affichage de log dans le console (déboguer) */
            Log.d("JSON Parser", "result: " + result.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }

        /* fermer la connection */
        conn.disconnect();

        /* try parse the string to a JSON object */
        try {
            jObj = new JSONObject(result.toString());
        } catch (JSONException e) {
            /* affichage de log dans le console (erreur) */
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }

        /* return JSON Object */
        return jObj;
    }

    /* 2 éme methode avec les methodes HTTP , les params  => les données envoyer par POST ou url param pour GET */
    public JSONObject makeHttpRequest(String url, String method,
                                      HashMap<String, String> params) {

        sbParams = new StringBuilder();
        /* récupération des valeur de param et save it dans le StringBuilder */
        if (params != null) {
            int i = 0;
            for (String key : params.keySet()) {
                try {
                    if (i != 0) {
                        sbParams.append("&");
                    }
                    sbParams.append(key).append("=")
                            .append(URLEncoder.encode(params.get(key), charset));

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                i++;
            }
        }
        /* methode POST */
        if (method.equals("POST")) {
            // request method is POST
            try {
                urlObj = new URL(url);

                conn = (HttpURLConnection) urlObj.openConnection();
                /* définit il y a output de données */
                conn.setDoOutput(true);

                conn.setRequestMethod("POST");

                conn.setRequestProperty("Accept-Charset", charset);
                // le délai d'expiration lors de la lecture à partir du flux d'entrée lorsqu'une connexion est établie
                conn.setReadTimeout(10000);
                // le délai d'expiration de connection
                conn.setConnectTimeout(15000);

                conn.connect();

                paramsString = sbParams.toString();
                /* create output stream pour envoyer les données */
                wr = new DataOutputStream(conn.getOutputStream());
                if (params != null) {
                    // convertir en binaire
                    wr.writeBytes(paramsString);
                    // envoyer les données
                    wr.flush();
                    wr.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (method.equals("GET")) {
            // request method is GET
            /* former les param URL*/
            if (sbParams.length() != 0) {
                url += "?" + sbParams.toString();
            }
            /* configuration de request HTTP */
            try {
                urlObj = new URL(url);

                conn = (HttpURLConnection) urlObj.openConnection();

                conn.setDoOutput(false);

                conn.setRequestMethod("GET");

                conn.setRequestProperty("Accept-Charset", charset);

                conn.setConnectTimeout(15000);

                conn.connect();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        try {
            /* Receive the response from the server */
            InputStream in = new BufferedInputStream(conn.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

            Log.d("JSON Parser", "result: " + result.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }

        conn.disconnect();

        // try parse the string to a JSON object
        try {
            jObj = new JSONObject(result.toString());
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }

        /* return JSON Object */
        return jObj;
    }
}

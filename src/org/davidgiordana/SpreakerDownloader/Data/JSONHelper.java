package org.davidgiordana.SpreakerDownloader.Data;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Created by davidgiordana on 6/19/17.
 */
public class JSONHelper {

    /**
     * Obtiene el texto de una URL en fortato String
     * @param url Ruta del sitio a solicitar su contenido
     * @return String con el contenido de la URL. null si ocurre algún problema
     */
    public static String getText(String url) {
        BufferedReader in = null;
        String inputLine;
        String content = "";
        try {
            URL textUrl = new URL(url);
            in = new BufferedReader(new InputStreamReader(textUrl.openStream()));
            while ((inputLine = in.readLine()) != null) {
                content += inputLine;
            }
        } catch (Exception e) {
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                return null;
            }
        }
        return content;
    }

    /**
     * Dada una url (String) retorna un JSONObject
     * @param url ruta a descargar
     * @return JSONObject asociado a la url, null si no existe
     */
    public static JSONObject getJSON(String url) {
        String content = getText(url);
        if(content == null) {
            return null;
        }
        try {
            return new JSONObject(content);
        } catch (JSONException e) {
            return null;
        }
    }

}

package org.davidgiordana.SpreakerDownloader.Data.SpreakerData;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableObjectValue;
import org.davidgiordana.SpreakerDownloader.Data.JSONHelper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Crea una instancia de SpreakerPodcast
 *
 * @author davidgiodana
 */
public class SpreakerPodcastFactory {

    /**
     * Obtiene la ruta del show de Spreaker
     * @param showID identificador de show de Spreaker
     * @return ruta del show de Spreaker
     */
    public static String getShowUrl(int showID) {
        return "https://api.spreaker.com/v2/shows/" + showID + "?limit=100";
    }

    /**
     * Obtiene la ruta de la lista de episodios de un show de Spreaker
     * @param showID identificador de show de Spreaker
     * @return ruta de la lista de episodios de un show de Spreaker
     */
    public static String getEpisodesUrl(int showID) {
        return "https://api.spreaker.com/v2/shows/" + showID + "/episodes?limit=100";
    }

    /**
     * Construye un podcast dado un id de show de Spreaker
     * @param showID id de Show de Spreaker
     * @return Podcast, null si la infomación no era válida
     */
    public static SpreakerPodcast getPodcast(int showID) {
        String url = getShowUrl(showID);
        JSONObject json = JSONHelper.getJSON(url);
        if (json == null)  {return null;}

        //Obitiene la información general del show
        String showName = "";
        String userName = "";
        try {
            JSONObject response = json.getJSONObject("response");
            JSONObject show = response.getJSONObject("show");
            JSONObject author = show.getJSONObject("author");
            userName = author.getString("fullname");
            showName = show.getString("title");
        } catch (Exception e) {e.printStackTrace();}

        // Obtiene los episodios
        ArrayList<SpreakerEpisode> episodes = new ArrayList<SpreakerEpisode>();
        try {
            episodes = getEpisodes(showID);
        } catch (JSONException e){e.printStackTrace();}
        return new SpreakerPodcast(showID, userName, showName, episodes);
    }

    /**
     * Obtiene un podcast
     * @param showID Identificador del show a obtener
     * @param podcastContainer Contenedor para almacenar el objecto del podcast
     */
    public static void setPodcast(int showID, SimpleObjectProperty<SpreakerPodcast> podcastContainer) {
        new Thread(() -> {
            final SpreakerPodcast podcast = getPodcast(showID);
            Platform.runLater(() -> {
                podcastContainer.setValue(podcast);
            });
        }).start();
    }

    /**
     * Dado un id de show de Spreaker retorna la lista de episodios del mismo
     * @param showID id de show de Spreaker
     * @return lista de episodios del show
     * @throws JSONException Si se solicita algún campo no existente
     */
    private static ArrayList<SpreakerEpisode> getEpisodes(int showID) throws JSONException{
        String url = getEpisodesUrl(showID);
        JSONObject json = JSONHelper.getJSON(url);
        if (json == null) {
            return new ArrayList<SpreakerEpisode>();
        }
        return getEpisodes(json);
    }

    /**
     * Dado un objeto JSONObject retorna la lista de episodios del mismo
     * @param json Objeto JSONObject con la información de los episodios
     * @return lista de episodios del show
     * @throws JSONException Si se solicita algún campo no existente
     */
    private static ArrayList<SpreakerEpisode> getEpisodes(JSONObject json) throws JSONException {
        ArrayList<SpreakerEpisode> episodes = new ArrayList<SpreakerEpisode>();
        JSONObject response = json.getJSONObject("response");
        JSONArray items = response.getJSONArray("items");
        for (int i = 0; i < items.length(); i++) {
            JSONObject object = (JSONObject) items.get(i);
            String title = object.getString("title");
            int id = object.getInt("episode_id");
            episodes.add(new SpreakerEpisode (title, id));
        }
        try {
            JSONObject newJSon = JSONHelper.getJSON(response.getString("next_url"));
            if (newJSon != null) {
                episodes.addAll(getEpisodes(newJSon));
            }
        } catch (Exception e){}
        return episodes;
    }

    // MARK: - get Spreaker show id

    /**
     * Retorna el identificador de un show de Spreaker si es posible
     * en base a un texto pasado como argumento.
     * @param text Texto a procesar
     * @return Show ID de Spreaker, null si no es posible obtener el dato
     */
    public static Integer getSpreakerShowID(String text) {
        // Elimina casos inútiles
        if (text == null) {
            return null;
        }
        text = text.trim();
        if (text.isEmpty()) {
            return null;
        }
        // Completa la url si es necesario
        if (!text.startsWith("https://")) {
            text = "https://" + text;
        }
        // Intenta obtener el id directamente del texto
        try {
            Integer id = Integer.parseInt(text);
            boolean isValid = JSONHelper.getJSON("https://api.spreaker.com/v2/shows/" + id) != null;
            return isValid ? id : null;
        } catch (Exception e) {}
        return getSpreakerShowIDFromUrl(text);
    }

    /**
     * Intenta obtener el id de un show de spreaker dada una url
     * @param url Enlace al sitio de Spreaker
     * @return Show ID de Spreaker, null en caso de no poder obtener la informacíon
     */
    private static Integer getSpreakerShowIDFromUrl(String url) {
        String id = null;
        // Intenta obtener el id desde la url
        Pattern p = Pattern.compile("(?<=spreaker.com/show/)[0-9]+");
        Matcher m = p.matcher(url);
        if (m.find()) {
            id = m.group();
        }
        // Intenta obtener el id desde el contenido de la página
        else {
            String text = JSONHelper.getText(url);
            if (text != null) {
                p = Pattern.compile("(?<=show_id=)[0-9]+");
                m = p.matcher(text);
                if (m.find()) {
                    id = m.group();
                }
            }
        }
        if (id == null) {
            return null;
        }
        try {
            return Integer.parseInt(id);
        } catch (Exception e) {return null;}
    }


}

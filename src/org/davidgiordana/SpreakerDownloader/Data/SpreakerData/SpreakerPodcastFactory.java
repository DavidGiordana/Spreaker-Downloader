package org.davidgiordana.SpreakerDownloader.Data.SpreakerData;

import org.davidgiordana.SpreakerDownloader.Data.JSONHelper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

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


}

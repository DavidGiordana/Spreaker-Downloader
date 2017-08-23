package org.davidgiordana.SpreakerDownloader.Data.SpreakerData;

/**
 * Objeto con la información de un episodio
 *
 * @author davidgiordana
 */
public class SpreakerEpisode  {

    /** Título */
    private String title;

    /** Identificador de episodio */
    private int episodeID;

    /**
     * Constructor
     * @param title título del episodio
     * @param id id del episodio
     */
    public SpreakerEpisode (String title, int id) {
        this.title = title;
        this.episodeID = id;
    }

    /**
     * GETTERS & SETTERS
     */

    public String getTitle() {
        return title;
    }

    public int getEpisodeID() {
        return episodeID;
    }

    public String getDownloadUrl() {
        return "https://api.spreaker.com/v2/episodes/" + episodeID + "/download";
    }

    /**
     * Overwrite
     */

    @Override
    public String toString(){
        return this.getTitle();
    }
}
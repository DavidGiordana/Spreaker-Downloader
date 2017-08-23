package org.davidgiordana.SpreakerDownloader.Data.SpreakerData;

import java.util.ArrayList;

/**
 * Objeto con la información de un podcast
 *
 * @author davidgiordana
 */
public class SpreakerPodcast {

    /** Datos */
    private int showID;                         // id de show de Spreaker
    private String userName;                    // Nombre de usuario
    private String showName;                    // Nombre del show
    private ArrayList<SpreakerEpisode> episodes;// Lista de episodios

    /**
     * Constructor
     * @param showID id de show de Spreaker
     * @param userName Nombre de usuario
     * @param showName Nombre de show de Spreaker (podcast)
     * @param episodes Lista de episodios del show
     */
    public SpreakerPodcast(int showID, String userName, String showName, ArrayList<SpreakerEpisode> episodes) {
        this.showID = showID;
        this.userName = userName;
        this.showName = showName;
        this.episodes = episodes == null ? new ArrayList<SpreakerEpisode>() : episodes;
    }

    /**
     * GETTERS & SETTERS
     */

    public int getShowID() {
        return showID;
    }

    public String getUserName() {
        return userName;
    }

    public String getShowName() {
        return showName;
    }

    public ArrayList<SpreakerEpisode> getEpisodes() {
        return episodes;
    }
}

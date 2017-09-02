package org.davidgiordana.SpreakerDownloader.Data.SpreakerData;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

import java.util.ArrayList;

/**
 * Objeto con la información de un podcast
 *
 * @author davidgiordana
 */
public class SpreakerPodcast {

    /** Datos */
    private IntegerProperty showID;                     // id de show de Spreaker
    private StringProperty userName;                    // Nombre de usuario
    private StringProperty showName;                    // Nombre del show
    private ObservableList<SpreakerEpisode> episodes;   // Lista de episodios

    /**
     * Constructor
     * @param showID id de show de Spreaker
     * @param userName Nombre de usuario
     * @param showName Nombre de show de Spreaker (podcast)
     * @param episodes Lista de episodios del show
     */
    public SpreakerPodcast(int showID, String userName, String showName, ArrayList<SpreakerEpisode> episodes) {
        this.showID = new SimpleIntegerProperty(showID);
        this.userName = new SimpleStringProperty(userName);
        this.showName = new SimpleStringProperty(showName);
        this.episodes = FXCollections.observableArrayList();
        setEpisodes(episodes);
    }


    /**
     * Configura los episodios actuales
     * @param episodes Lista de episodios a mostrar
     */
    public void setEpisodes(ArrayList<SpreakerEpisode> episodes) {
        ArrayList<SpreakerEpisode> eps = episodes == null ? new ArrayList() : episodes;
        this.episodes.setAll(eps);
    }

    /**
     * GETTERS & SETTERS
     */

    public int getShowID() {
        return showID.get();
    }

    public IntegerProperty showIDProperty() {
        return showID;
    }

    public String getUserName() {
        return userName.get();
    }

    public StringProperty userNameProperty() {
        return userName;
    }

    public String getShowName() {
        return showName.get();
    }

    public StringProperty showNameProperty() {
        return showName;
    }

    public ObservableList<SpreakerEpisode> getEpisodes() {
        return episodes;
    }

}

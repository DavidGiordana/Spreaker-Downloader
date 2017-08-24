package org.davidgiordana.SpreakerDownloader.Data.SpreakerData;

import javafx.beans.property.*;


/**
 * Objeto con la información de un episodio
 *
 * @author davidgiordana
 */
public class SpreakerEpisode  {

    /** Título */
    private StringProperty title;

    /** Identificador de episodio */
    private IntegerProperty episodeID;

    /** Indica que el episodio está seleccionado */
    private BooleanProperty selected;


    /**
     * Constructor
     * @param title título del episodio
     * @param id id del episodio
     * @param selected Indica que un episodio está seleccionado
     */
    public SpreakerEpisode (String title, int id, boolean selected) {
        this.title = new SimpleStringProperty(title);
        this.episodeID = new SimpleIntegerProperty(id);
        this.selected = new SimpleBooleanProperty(selected);
    }

    /**
     * Constructor
     * @param title título del episodio
     * @param id id del episodio
     */
    public SpreakerEpisode (String title, int id) {
        this(title,id,false);
    }

    /**
     * GETTERS & SETTERS
     */

    public String getTitle() {
        return title.get();
    }

    public StringProperty titleProperty() {
        return title;
    }

    public int getEpisodeID() {
        return episodeID.get();
    }

    public IntegerProperty episodeIDProperty() {
        return episodeID;
    }

    public boolean isSelected() {
        return selected.get();
    }

    public BooleanProperty selectedProperty() {
        return selected;
    }

    public String getDownloadUrl() {
        return "https://api.spreaker.com/v2/episodes/" + getEpisodeID() + "/download";
    }

    public void setSelected(boolean selected) {
        this.selected.set(selected);
    }
}
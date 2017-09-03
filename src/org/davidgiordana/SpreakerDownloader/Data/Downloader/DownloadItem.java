package org.davidgiordana.SpreakerDownloader.Data.Downloader;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.davidgiordana.SpreakerDownloader.Data.SpreakerData.SpreakerEpisode;

/**
 * Item de lista de descargas
 *
 * @author davidgiodana
 */
public class DownloadItem {

    /** Episodio asociado al item */
    private SpreakerEpisode episode;

    /** Indica si el item está seleccionado */
    private BooleanProperty selected;

    /**
     * Constructor
     * @param episode episodio a asociar
     */
    DownloadItem(SpreakerEpisode episode) {
        this.episode = episode;
        this.selected = new SimpleBooleanProperty();
    }

    /**
     * GETTERS & SETTERS
     */

    public SpreakerEpisode getEpisode() {
        return episode;
    }

    public boolean isSelected() {
        return selected.get();
    }

    public BooleanProperty selectedProperty() {
        return selected;
    }
}
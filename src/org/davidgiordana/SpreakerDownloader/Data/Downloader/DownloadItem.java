package org.davidgiordana.SpreakerDownloader.Data.Downloader;

import org.davidgiordana.SpreakerDownloader.Data.SpreakerData.SpreakerEpisode;

/**
 * Item de lista de descargas
 *
 * @author davidgiodana
 */
public class DownloadItem {

    /** Episodio asociado al item */
    private SpreakerEpisode episode;

    /**
     * Constructor
     * @param episode episodio a asociar
     */
    DownloadItem(SpreakerEpisode episode) {
        this.episode = episode;
    }

    /**
     * GETTERS & SETTERS
     */

    public SpreakerEpisode getEpisode() {
        return episode;
    }
}
package org.davidgiordana.SpreakerDownloader.GUI.SpreakerDownloader.DownloadView;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import org.davidgiordana.SpreakerDownloader.Data.Downloader.DownloadManager;
import org.davidgiordana.SpreakerDownloader.Data.SpreakerData.SpreakerEpisode;
import org.davidgiordana.SpreakerDownloader.Data.SpreakerData.SpreakerPodcast;

import java.net.URL;
import java.util.ResourceBundle;

public class DownloadViewController implements Initializable {


    private SpreakerPodcast podcastModel;

    // MARK: - Downloading Area

    @FXML
    /** Label con el nombre del item que se está descargando */
    public Label downloadingItem;

    @FXML
    /** Barra de progreso de descargas */
    public ProgressBar progress;

    @FXML
    /** Label con la cantidad de elementos restantes a descargar */
    public Label remaning;

    @FXML
    /** Selector de ancho de banda */
    public TextField bandwidthField;

    // MARK: - Initialize

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        prepareDownloadModule();
    }

    /**
     * Prepara el módulo de descargas
     */
    private void prepareDownloadModule() {
        // Enlaza la barra de progreso
        DownloadManager dm = DownloadManager.getInstance();
        this.progress.progressProperty().bind(dm.getCurrentProgressProperty());

        // Enlaza los elementos restantes
        dm.downloadCountProperty().addListener((obs,o,n)-> updateRemaningCount(n.intValue()));
        this.updateRemaningCount(dm.getDownloadCount());

        // Enlaza el elemento actual
        dm.currentItemProperty().addListener((obs,o,n)-> updateCurrentDownloadingItem(n));
        this.updateCurrentDownloadingItem(dm.getCurrentItem());

    }

    // MARK: - Downloads

    /**
     * Actualiza el episodio actual (GUI)
     * @param ep Episodio actual
     */
    private void updateCurrentDownloadingItem(SpreakerEpisode ep) {
        String t = "--";
        if (ep != null) {
            t = ep.getTitle();
        }
        this.downloadingItem.setText(t);
    }

    /**
     * Actualiza el contador de episodios restantes (GUI)
     * @param i Cantidad actual
     */
    private void updateRemaningCount(Integer i) {
        String t = "0";
        if (i != null) {
            t = i + "";
        }
        this.remaning.setText(t);
    }

    // MARK: - Bandwidth

    @FXML
    /** Cambia el ancho de banda */
    public void changeBandwidth() {
        String text = bandwidthField.getText();
        Integer speed = null;
        DownloadManager dm = DownloadManager.getInstance();
        // Configura el ancho de banda máximo
        if (text == null || text.isEmpty()) {
            dm.bandWidthProperty().set(Integer.MAX_VALUE);
            return;
        }
        // Intenta cambiar el ancho de banda
        try {
            speed = Integer.parseInt(text);
            if (speed < 0) {
                bandwidthField.setText(null);
                dm.bandWidthProperty().set(Integer.MAX_VALUE);
            } else {
                dm.bandWidthProperty().set(speed * 1024);
            }
        } catch (Exception e) {
            bandwidthField.setText(null);
            dm.bandWidthProperty().set(Integer.MAX_VALUE);
        }
    }

    /**
     * Getters & Setters
     */

    public void setPodcastModel(SpreakerPodcast podcastModel) {
        this.podcastModel = podcastModel;
    }
}

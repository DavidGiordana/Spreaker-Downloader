package org.davidgiordana.SpreakerDownloader.GUI.SpreakerDownloader;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.util.Callback;
import org.davidgiordana.SpreakerDownloader.Data.Downloader.DownloadManager;
import org.davidgiordana.SpreakerDownloader.Data.SpreakerData.SpreakerEpisode;
import org.davidgiordana.SpreakerDownloader.Data.SpreakerData.SpreakerPodcast;
import org.davidgiordana.SpreakerDownloader.Data.SpreakerData.SpreakerPodcastFactory;
import org.davidgiordana.SpreakerDownloader.GUI.CheckboxList.CheckboxListItem;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * Controlador de la ventana de descarga de episodios de Spreaker
 *
 * @author davidgiodana
 */
public class SpreakerDownloaderController implements Initializable{

    @FXML
    /** Label con el nombre del podcast */
    public Label podcastLabel;

    @FXML
    /** Label con el nombre del podcast */
    public Label podcastName;

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
    /** Vista de lista con los elementos descargables */
    public ListView downloadList;

    @FXML
    /** Selector de ancho de banda */
    public TextField bandwidthField;

    @FXML
    /** Check Box para seleccionar todos los elementos*/
    public CheckBox selectAllCB;

    @FXML
    /** Método llamado al cambiar el valor de seleccionar todo*/
    public void selectAllAction() {
        if (selectAllCB.isSelected()) {
            applySelectAll();
        } else {
            clearSelection();
        }
    }

    /**
     * Indica si todos los elementos están seleccionados
     * @return True si está todo seleccionado
     */
    private boolean isAllSelected() {
        int count = this.downloadDataList.size();
        int selected = 0;
        for (CheckboxListItem<SpreakerEpisode> i: this.downloadDataList) {
            if (i.selectedProperty().getValue()) {
                selected += 1;
            }
        }
        return count == selected;
    }

    @FXML
    /** Cambia el ancho de banda */
    public void changeBandwidth() {
        String text = bandwidthField.getText();
        Integer speed = null;
        DownloadManager dm = DownloadManager.getInstance();
        if (text == null || text.isEmpty()) {
            dm.bandWidthProperty().set(Integer.MAX_VALUE);
            return;
        }
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

    @FXML
    /** Método llamado al presionar el boton limpiar selecciones */
    public void clearSelection() {
        for (CheckboxListItem<SpreakerEpisode> i: this.downloadDataList) {
            i.selectedProperty().setValue(false);
        }
    }

    /** Método llamado al seleccionar todo */
    public void applySelectAll() {
        for (CheckboxListItem<SpreakerEpisode> i: this.downloadDataList) {
            i.selectedProperty().setValue(true);
        }
    }

    @FXML
    /** Método llamado al presionar el boton descargar */
    public void downloadSelection() {
        // Obtiene la lista de elementos a descargar
        ArrayList<SpreakerEpisode> list = new ArrayList<SpreakerEpisode>();
        for (CheckboxListItem<SpreakerEpisode> i: this.downloadDataList) {
            if (i.selectedProperty().get()) {
                list.add(i.getData());
                i.selectedProperty().set(false);
            }
        }

        // Descarga los elementos
        DownloadManager.getInstance().addDownloads(list);
    }

    /** Identificador de Show actual */
    public static int showID;

    /** Propiedad que almacena la información del podcast */
    private ObjectProperty<SpreakerPodcast> podcast = new SimpleObjectProperty<>(null);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        podcastLabel.setText("Cargando...");

        // Parapara la lista de descargas
        this.prepareDownloadList();

        // Solicita la información del podcast
        podcast.addListener((obs,o, n)-> loadData(n));
        this.getPodcastData();

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

    /** Lista observable para modelo de lista de descargas */
    private ObservableList<CheckboxListItem> downloadDataList;

    /**
     * Prepara la lista de descargas (inicialización)
     */
    private void prepareDownloadList() {
        this.downloadDataList = FXCollections.observableArrayList();
        downloadList.setItems(downloadDataList);
        Callback<ListView<CheckboxListItem>, ListCell<CheckboxListItem>> forListView =
                CheckBoxListCell.forListView(item -> {return item.selectedProperty();});
        downloadList.setCellFactory(forListView);
    }

    /**
     * Carga información a la lista de descargas
     * @param data Lista de episodios a cargar
     */
    private void loadDataInDownloadList(ArrayList<SpreakerEpisode> data){
        for (int i = 0; i < data.size(); i++) {
            final int index = i;
            SpreakerEpisode element = data.get(i);
            CheckboxListItem item = new CheckboxListItem(element);
            downloadDataList.add(item);
            item.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
                selectAllCB.setSelected(isAllSelected());
            });
        }


    }

    /**
     * Procesa el podcast para mostrar la información en pantalla
     **/
    private void loadData(SpreakerPodcast pod) {
        Platform.runLater(new Runnable(){
            @Override
            public void run() {
                podcastLabel.setText("Podcast");
                podcastName.setText(pod.getShowName());
                loadDataInDownloadList(pod.getEpisodes());
            }
        });
    }

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

    /**
     * Solicita la información del podcast
     * Advertencia: es asíncrono
     *
     */
    public void getPodcastData(){
        new Thread() {
            @Override
            public void run() {
                super.run();
                SpreakerPodcast temp = SpreakerPodcastFactory.getPodcast(showID);
                if (temp != null) {
                    podcast.setValue(temp);
                }
            }
        }.start();
    }
}



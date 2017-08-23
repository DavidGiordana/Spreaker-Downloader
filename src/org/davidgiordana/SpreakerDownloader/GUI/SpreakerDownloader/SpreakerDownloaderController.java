package org.davidgiordana.SpreakerDownloader.GUI.SpreakerDownloader;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
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

    // MARK: - Podcast label

    @FXML
    /** Label con el nombre del podcast */
    public Label podcastLabel;

    @FXML
    /** Label con el nombre del podcast */
    public Label podcastName;

    // MARK: -  Search

    /** Text Field de búsqueda */
    public TextField searchTF;



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
    /** Vista de tabla con los elementos descargables */
    public TableView<SpreakerEpisode> episodesTable;

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
        for (SpreakerEpisode i: this.podcastModel.get().getFilteredEpisodesList()) {
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
        for (SpreakerEpisode i: this.podcastModel.get().getFilteredEpisodesList()) {
            i.selectedProperty().setValue(false);
        }
    }

    /** Método llamado al seleccionar todo */
    public void applySelectAll() {
        for (SpreakerEpisode i: this.podcastModel.get().getFilteredEpisodesList()) {
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


    // MARK: - Data

    /** Identificador de Show actual */
    public static int showID;

    private SimpleObjectProperty<SpreakerPodcast> podcastModel;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        podcastLabel.setText("Cargando...");

        // Parapara la tabla de episodios
        TableColumn<SpreakerEpisode, Boolean> checkboxColumn = new TableColumn("");
        checkboxColumn.setCellValueFactory(c -> {return c.getValue().selectedProperty();});
        checkboxColumn.setCellFactory(CheckBoxTableCell.forTableColumn(checkboxColumn));

        TableColumn<SpreakerEpisode, String> titleColumn = new TableColumn("Título");
        titleColumn.setCellValueFactory(c -> {return c.getValue().titleProperty();});

        episodesTable.getColumns().setAll(checkboxColumn, titleColumn);

        // Solicita la información del podcast
        podcastModel = new SimpleObjectProperty<>();
        podcastModel.addListener((obs, o, n) -> {
            if (n != null) {
                podcastLabel.setText("Podcast");
                podcastName.setText(n.getShowName());
                episodesTable.setItems(n.getFilteredEpisodesList());
            } else {
                System.out.println("es null");
            }
        });
        SpreakerPodcastFactory.setPodcast(showID, podcastModel);

        titleColumn.prefWidthProperty().bind(episodesTable.widthProperty().multiply(0.9));

        // Enlaza la barra de progreso
        DownloadManager dm = DownloadManager.getInstance();
        this.progress.progressProperty().bind(dm.getCurrentProgressProperty());

        // Enlaza los elementos restantes
        dm.downloadCountProperty().addListener((obs,o,n)-> updateRemaningCount(n.intValue()));
        this.updateRemaningCount(dm.getDownloadCount());

        // Enlaza el elemento actual
        dm.currentItemProperty().addListener((obs,o,n)-> updateCurrentDownloadingItem(n));
        this.updateCurrentDownloadingItem(dm.getCurrentItem());

        searchTF.textProperty().addListener((obs, o, n) -> podcastModel.get().search(n));
    }

    /** Lista observable para modelo de lista de descargas */
    private ObservableList<CheckboxListItem> downloadDataList;


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


    public void searchAction(){

    }

    public void cancelSearchAction(){
        searchTF.setText(null);
    }

}



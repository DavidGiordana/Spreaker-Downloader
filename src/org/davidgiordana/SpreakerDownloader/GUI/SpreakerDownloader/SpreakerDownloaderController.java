package org.davidgiordana.SpreakerDownloader.GUI.SpreakerDownloader;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;

import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import org.davidgiordana.SpreakerDownloader.Data.Downloader.DownloadManager;
import org.davidgiordana.SpreakerDownloader.Data.SpreakerData.SpreakerEpisode;
import org.davidgiordana.SpreakerDownloader.Data.SpreakerData.SpreakerPodcast;
import org.davidgiordana.SpreakerDownloader.Data.SpreakerData.SpreakerPodcastFactory;
import org.davidgiordana.SpreakerDownloader.GUI.SettingsView.SettingsViewController;
import org.davidgiordana.SpreakerDownloader.Main;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controlador de la ventana de descarga de episodios de Spreaker
 *
 * @author davidgiodana
 */
public class SpreakerDownloaderController implements Initializable{

    /** Stack principal de vistas */
    public StackPane mainStack;

    /** */
    public VBox spreakerDownloaderView;


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

    /** Boton de cancelar de búsqueda */
    public Button searchCancelB;


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

    // MARK: - Table

    @FXML
    /** Vista de tabla con los elementos descargables */
    public TableView<SpreakerEpisode> episodesTable;

    @FXML
    /** Check Box para seleccionar todos los elementos*/
    public CheckBox selectAllCB;


    // MARK: - Data

    /** Identificador de Show actual */
    public IntegerProperty showID;

    /** Modelo de la tabla */
    private SimpleObjectProperty<SpreakerPodcast> podcastModel;

    // MARK: - Init

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        prepareTable();
        prepareData();
        prepareDownloadModule();
        prepareSearchTF();

        this.showID = new SimpleIntegerProperty(0);
        this.showID.addListener((obs, o, n) -> {
            if (n != null) {
                this.requestPodcast();

            }
            spreakerDownloaderView.setDisable(false);
            mainStack.getChildren().remove(pane);
        });
        this.showSettingsVC();
    }

    /**
     * Prepara la tabla de episodios
     */
    private void prepareTable() {
        // Columna de selecciones
        TableColumn<SpreakerEpisode, Boolean> checkboxColumn = new TableColumn("");
        checkboxColumn.setCellValueFactory(c -> {return c.getValue().selectedProperty();});
        checkboxColumn.setCellFactory(CheckBoxTableCell.forTableColumn(checkboxColumn));

        // Columna de títulos
        TableColumn<SpreakerEpisode, String> titleColumn = new TableColumn("Título");
        titleColumn.setCellValueFactory(c -> {return c.getValue().titleProperty();});

        episodesTable.getColumns().setAll(checkboxColumn, titleColumn);
        titleColumn.prefWidthProperty().bind(episodesTable.widthProperty().multiply(0.9));
    }

    /**
     * Prepara la interfaz y solicita la información del show actual
     */
    private void prepareData() {
        podcastModel = new SimpleObjectProperty<>();
        podcastModel.addListener((obs, o, n) -> {
            if (n != null) {
                podcastLabel.setText("Podcast");
                podcastName.setText(n.getShowName());
                episodesTable.setItems(n.getFilteredEpisodesList());
            }
        });
    }

    /**
     * Solicita la información del podcast actual
     */
    private void requestPodcast() {
        podcastLabel.setText("Cargando...");
        SpreakerPodcastFactory.setPodcast(showID.getValue(), podcastModel);
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

    /**
     * Prepara el text field de búsqueda
     */
    private void prepareSearchTF() {
        searchTF.textProperty().addListener((obs, o, n) -> {
            searchCancelB.setDisable(n == null || n.isEmpty());
            podcastModel.get().search(n);
        });
        searchCancelB.setDisable(true);
    }

    /**
     * Obtiene la lista de episodios filtrados
     * @return Lista de episodios filtrados
     */
    private ObservableList<SpreakerEpisode> getEpisodesModel() {
        return this.podcastModel.get().getFilteredEpisodesList();
    }


    // MARK: - Select All Table elements

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
     * @return True si están todos los elementos seleccionados
     */
    private boolean isAllSelected() {
        return getEpisodesModel().filtered(e -> !e.isSelected()).isEmpty();
    }

    @FXML
    /** Limpia los elementos seleccionados */
    public void clearSelection() {
        for (SpreakerEpisode i: getEpisodesModel()) {
            i.selectedProperty().setValue(false);
        }
    }

    /** Selecciona todos los elementos de la tabla */
    public void applySelectAll() {
        for (SpreakerEpisode i: getEpisodesModel()) {
            i.selectedProperty().setValue(true);
        }
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

    // MARK: - Downloads

    @FXML
    /** Descarga los elementos seleccionados */
    public void downloadSelection() {
        // Obtiene la lista de elementos a descargar
        ObservableList<SpreakerEpisode> downloadList = getEpisodesModel().filtered( episode -> episode.isSelected());
        clearSelection();
        // Descarga los elementos
        DownloadManager.getInstance().addDownloads(downloadList);
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

    // MARK: - Search

    public void cancelSearchAction(){
        searchTF.setText(null);
    }

    // MARK: - Settings

    private SettingsViewController settingsController;
    private VBox pane;

    private void showSettingsVC() {

        URL settingsFXML = Main.class.getResource("/org/davidgiordana/SpreakerDownloader/GUI/SettingsView/SettingsViewController.fxml");
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            Pane p = fxmlLoader.load(settingsFXML.openStream());
            settingsController = (SettingsViewController) fxmlLoader.getController();
            settingsController.setShowIDProperty(showID);

            pane = new VBox();
            pane.getChildren().add(p);


            pane.setBackground(new Background(new BackgroundFill(Color.gray(0,0.7), CornerRadii.EMPTY, Insets.EMPTY)));
            p.setBackground(new Background(new BackgroundFill(Color.gray(.9,1), CornerRadii.EMPTY, Insets.EMPTY)));
            pane.setAlignment(Pos.CENTER);
            mainStack.getChildren().add(pane);
            spreakerDownloaderView.setDisable(true);
        } catch (Exception e){e.printStackTrace();}

    }

}



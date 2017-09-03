package org.davidgiordana.SpreakerDownloader.GUI.SpreakerDownloader;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
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
import org.davidgiordana.SpreakerDownloader.Data.Downloader.DownloadItem;
import org.davidgiordana.SpreakerDownloader.Data.Downloader.DownloadManager;
import org.davidgiordana.SpreakerDownloader.Data.SpreakerData.SpreakerEpisode;
import org.davidgiordana.SpreakerDownloader.Data.SpreakerData.SpreakerPodcast;
import org.davidgiordana.SpreakerDownloader.Data.SpreakerData.SpreakerPodcastFactory;
import org.davidgiordana.SpreakerDownloader.GUI.SettingsView.SettingsViewController;
import org.davidgiordana.SpreakerDownloader.GUI.SpreakerDownloader.DownloadView.DownloadViewController;
import org.davidgiordana.SpreakerDownloader.Main;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controlador de la ventana de descarga de episodios de Spreaker
 *
 * @author davidgiodana
 */
public class SpreakerDownloaderController implements Initializable {

    /** Objeto tabla de episodios */
    private SearchableTable<SpreakerEpisode> episodeSearchableTable;

    /** Objeto tabla de descargas */
    private SearchableTable<DownloadItem> downloadSearchableTable;


    // MARK: - Panels

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


    // MARK: -  Episodes list

    /** Text Field de búsqueda */
    public TextField searchEpisodeTF;

    /** Boton de cancelar de búsqueda */
    public Button cancelSearchEpisodesB;

    /** Vista de tabla con los elementos descargables */
    public TableView<SpreakerEpisode> episodesTable;

    // MARK: -  Download list

    /** Text Field de búsqueda */
    public TextField searchDownloadTF;

    /** Boton de cancelar de búsqueda */
    public Button cancelSearchDownloadB;

    /** Vista de tabla con los elementos descargables */
    public TableView<DownloadItem> downloadTable;

    // MARK: - Data

    /** Identificador de Show actual */
    public IntegerProperty showID;

    /** Modelo de la tabla */
    private SimpleObjectProperty<SpreakerPodcast> podcastModel;

    // MARK: - Init

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        prepareEpisodesTable();
        prepareDownloadsTable();
        prepareData();

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
    private void prepareEpisodesTable() {
        // Prepara el objeto de tabla con búsqueda
        episodeSearchableTable = new SearchableTable<SpreakerEpisode>(episodesTable,searchEpisodeTF,cancelSearchEpisodesB);
        episodeSearchableTable.setSearchCallback(SpreakerEpisode::getTitle);
        episodeSearchableTable.setSelectionCallback(SpreakerEpisode::selectedProperty);

        // Columna de títulos
        TableColumn<SpreakerEpisode, String> titleColumn = new TableColumn("Título");
        titleColumn.setCellValueFactory(c -> {return c.getValue().titleProperty();});
        episodesTable.getColumns().add(titleColumn);

        // Configura los anchos
        titleColumn.prefWidthProperty().bind(episodesTable.widthProperty().multiply(0.9));
    }

    /**
     * Prepara la tabla de descargas
     */
    private void prepareDownloadsTable() {
        // Prepara el objeto de tabla con búsqueda
        downloadSearchableTable = new SearchableTable<DownloadItem>(downloadTable, searchDownloadTF, cancelSearchDownloadB);
        downloadSearchableTable.setSearchCallback(c -> c.getEpisode().getTitle());
        downloadSearchableTable.setSelectionCallback(DownloadItem::selectedProperty);
        DownloadManager dm = DownloadManager.getInstance();
        downloadSearchableTable.setBaseData(dm.getQueue());

        // Columna de títulos
        TableColumn<DownloadItem, String> titleColumn = new TableColumn("Título");
        titleColumn.setCellValueFactory(c -> {return c.getValue().getEpisode().titleProperty();});
        downloadTable.getColumns().add(titleColumn);


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
                episodeSearchableTable.setBaseData(n.getEpisodes());
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

    /** Descarga los elementos seleccionados */
    @FXML public void downloadSelection() {
        DownloadManager.getInstance().addDownloads(episodeSearchableTable.getSelected());
        episodeSearchableTable.selectAll(false);
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
        } catch (Exception e){System.err.println(e);}

    }

}



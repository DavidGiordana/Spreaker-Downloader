package org.davidgiordana.SpreakerDownloader;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;

/**
 * Clase principal
 *
 * @author davidgiordana
 */
public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    public static final String title = "Spreaker Downloader";

    @Override
    public void start(Stage stage) throws Exception {
        URL rootUrl = Main.class.getResource("/org/davidgiordana/SpreakerDownloader/GUI/SpreakerDownloader/SpreakerDownloaderMain.fxml");
        Parent root = FXMLLoader.load(rootUrl);
        stage.setTitle(Main.title);
        stage.setScene(new Scene(root, 570,175));
        stage.setMinHeight(500);
        stage.show();

    }


}

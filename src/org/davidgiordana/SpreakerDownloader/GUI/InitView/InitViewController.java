package org.davidgiordana.SpreakerDownloader.GUI.InitView;



import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.davidgiordana.SpreakerDownloader.Data.Downloader.DownloadManager;
import org.davidgiordana.SpreakerDownloader.Data.JSONHelper;
import org.davidgiordana.SpreakerDownloader.GUI.SpreakerDownloader.SpreakerDownloaderController;
import org.davidgiordana.SpreakerDownloader.Main;

import java.io.File;


/**
 * Controlador de la primer ventana que solicita la información para trabajar
 *
 * @author davidgiordana
 */
public class InitViewController {


    @FXML
    /** Layout principal */
    public VBox mainLayout;

    /** Retorna el Stage principal */
    private Stage getStage() {
        return (Stage) mainLayout.getScene().getWindow();
    }

    @FXML
    /** Campo de texto  para el id de show */
    public TextField showField;

    @FXML
    /** Campo de texto para el directorio de destino */
    public TextField destinationField;

    @FXML
    /** Indica el estado actual */
    public Label statusLabel;


    /**
     * Solicita el directorio de destino
     */
    public void browseButton() {
        File f = getDestinationFolder();
        if (f != null) {
            destinationField.setText(f.getAbsolutePath());
        }
    }

    /**
     * Acciona cuando se presiona el boton cancelar
     */
    public void cancelOption(){
        getStage().close();
    }

    /**
     * Acciona cuando se presiona el boton continuar
     */
    public void continueOption(){
        checkInformation();
    }

    /**
     * Comprueba el id del show
     * @return true si el ide es válido
     */
    private boolean checkSpreakerID() {
        Integer showID = getShowID();
        if (showID == null || showID < 1) { return false; }
        return JSONHelper.getJSON("https://api.spreaker.com/v2/shows/" + showID) != null;
    }

    /**
     * Comprueba el directorio de destino
     * @return true si el directorio de destino es válido
     */
    private boolean checkDestinationFolder() {
        if (destinationField.getText() == null) {return false;}
        return new File(destinationField.getText()).isDirectory();
    }

    /**
     * Comprueba la información ingresada
     */
    private void checkInformation() {
        statusLabel.setText("Comprobando datos...");
        statusLabel.setTextFill(Color.BLACK);
        new Thread() {
            @Override
            public void run() {
                super.run();
                if (!checkSpreakerID()) {
                    showErrorMessage("No se ha ingresado el id de show de Spreaker o no es válido");
                    return;
                }
                if (!checkDestinationFolder()) {
                    showErrorMessage("El directorio destino no es válido");
                    return;
                }
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        goToMainWindow();
                    }
                });
            }
        }.start();

    }

    /**
     * Muesta la ventana principal
     */
    private void goToMainWindow() {
        try {
            Stage stage = getStage();
            SpreakerDownloaderController.showID = getShowID();
            DownloadManager.getInstance().setDestination(destinationField.getText());
            Parent root = FXMLLoader.load(getClass().getResource("/org/davidgiordana/SpreakerDownloader/GUI/SpreakerDownloader/SpreakerDownloaderMain.fxml"));
            stage.setTitle(Main.title);
            stage.setScene(new Scene(root, 600, 500));
            stage.show();
        } catch (Exception e ){e.printStackTrace();}
    }

    /**
     * Obtiene el id del show
     * @return ID del show, null si no es válido
     */
    private Integer getShowID(){
        String text = showField.getText();
        if (text == null) { return null; }
        String f1 = "http://www.spreaker.com/show/";
        String f2 = "/episodes/feed";
        if (text.startsWith(f1) && text.endsWith(f2)) {
            text = text.replaceFirst(f1, "");
            text = text.replaceFirst(f2, "");
        }
        try {
            return Integer.parseInt(text);
        } catch (Exception e) {return null;}
    }

    /**
     * Solicita al usuario el directorio de destino al usuario
     * @return Directorio de destino, {@code null} si no se seleccionó nunguno
     */
    private File getDestinationFolder() {
        String current =  System.getProperty("user.dir");
        DirectoryChooser dc = new DirectoryChooser();
        dc.setTitle("Seleccione un directorio destino");
        dc.setInitialDirectory(new File(current));
        return dc.showDialog(null);
    }

    /**
     * Muestra un mensaje de error
     * @param message mensaje de error
     */
    private void showErrorMessage(String message) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                statusLabel.setText("Datos Inválidos");
                statusLabel.setTextFill(Color.RED);
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error: Información erronea");
                alert.setHeaderText("Un dato ingresado no es válido");
                alert.setContentText(message);
                alert.showAndWait();
            }
        });
    }

}



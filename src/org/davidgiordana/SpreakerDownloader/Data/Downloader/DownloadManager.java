package org.davidgiordana.SpreakerDownloader.Data.Downloader;

import javafx.beans.property.*;
import org.davidgiordana.SpreakerDownloader.Data.SpreakerData.SpreakerEpisode;

import java.io.File;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Administrador de descargas
 *
 * @author davidgiodana
 */
public class DownloadManager {

    // MARK: - Singleton

    /** Instancia única */
    private static DownloadManager instance;

    /**
     * Retorna la instancia única de la clase
     * @return Instancia de la clase
     */
    public static DownloadManager getInstance() {
        if (instance == null) {
            instance = new DownloadManager();
        }
        return instance;
    }

    // MARK: - Data

    /** hilo de descarga */
    private Thread thread;

    /** Tarea de descarga */
    private DownloadTask downloadTask;

    /** Destino de las descargas */
    private String destination;

    /** Cola con los elementos a descargar */
    private ConcurrentLinkedQueue<DownloadItem> queue;

    /** Propiedades */
    private DoubleProperty currentProgress;             // Progreso de la descarga actual
    private ObjectProperty<SpreakerEpisode> currentItem;// Item actual de descarga
    private IntegerProperty downloadCount;              // Cantidad de descargas restantes
    private IntegerProperty bandWidth;                  // Ancho de banda para la descarga

    // MARK: - init

    /**
     * Constructor
     */
    private DownloadManager(){
        this.queue = new ConcurrentLinkedQueue<DownloadItem>();
        this.currentProgress = new SimpleDoubleProperty();
        this.currentItem = new SimpleObjectProperty<SpreakerEpisode>();
        this.downloadCount = new SimpleIntegerProperty(0);
        this.bandWidth = new SimpleIntegerProperty(Integer.MAX_VALUE);
    }

    // MARK: - interface

    /**
     * Agrega una lista de episodios a la cola de descaga
     * @param episodes Lista de episodios a descargar
     */
    public void addDownloads(List<SpreakerEpisode> episodes) {
        for (SpreakerEpisode ep: episodes) {
            this.addDownload(ep);
        }
    }

    /**
     * Agrega un episodio a la cola de descaga
     * @param episode episodio a descargar
     */
    public synchronized void addDownload(SpreakerEpisode episode) {
        if (episode == null) { return; }
        boolean wasEmpty = queue.isEmpty();
        queue.offer(new DownloadItem(episode));
        if (wasEmpty) {
            startNewTask(false);
        }
        this.downloadCount.setValue(queue.size());
    }

    /**
     * Inicia una nueva descarga
     * @param removing indica si es necesario desencolar
     *                 un elemento antes de proceder a la descarga
     */
    public synchronized void startNewTask(boolean removing) {
        if (removing) {
            queue.poll();
        }
        // Actualiza las propiedades
        this.downloadCount.setValue(queue.size());
        currentProgress.setValue(0);
        DownloadItem di = queue.peek();
        if (di != null) {
            // Creación de nueva tarea
            downloadTask = new DownloadTask(di);

            // Actualiza propiedades
            downloadTask.progressProperty().addListener(((observableValue, oldValue, newValue) -> {currentProgress.set((Double) newValue);}));
            currentItem.set(di.getEpisode());


            // Creación de nuevo hilo
            thread = new Thread(downloadTask);
            thread.setDaemon(true);
            thread.start();

        } else {
            currentItem.set(null);
        }
    }

    /**
     * GETTERS & SETTERS
     */

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public double getCurrentProgress() {
        return currentProgress.get();
    }

    public DoubleProperty getCurrentProgressProperty() {
        return currentProgress;
    }

    public int getDownloadCount() {
        return downloadCount.get();
    }

    public IntegerProperty downloadCountProperty() {
        return downloadCount;
    }

    public SpreakerEpisode getCurrentItem() {
        return currentItem.get();
    }

    public ObjectProperty<SpreakerEpisode> currentItemProperty() {
        return currentItem;
    }

    public int getBandWidth() {
        return bandWidth.get();
    }

    public IntegerProperty bandWidthProperty() {
        return bandWidth;
    }

    /**
     * STATIC - verifier
     */

    /**
     * Comprueba el directorio de destino
     * @return null si el directorio de destino es válido
     *          Un String con el error si ha habido un problema
     */
    public static String checkDestinationFolder(String destination) {
        if (destination == null || destination.isEmpty()) {
            return "No se ha ingresado un directorio de destino";
        }
        File destinationFile = new File(destination);
        if (!destinationFile.isDirectory()) {
            return "La ruta de destino no es un directorio";
        }
        if (!destinationFile.canWrite()) {
            return "El directorio de destino no puede ser escrito";
        }
        return null;
    }
}
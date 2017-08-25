package org.davidgiordana.SpreakerDownloader.Data.Downloader;

import javafx.concurrent.Task;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;

/**
 * Tarea de descarga
 *
 * @author davidgiodana
 */
public class DownloadTask extends Task<Void> {

    /** Tamaño por defecto del buffer de descargas */
    public static final int BUFF_SIZE = 2048;

    /** Item a descargar */
    private DownloadItem downloadItem;

    /** Nombre temporal generado para la descarga */
    private String tempName;

    /**
     * Constructor
     * @param di DownloadItem con la información para la descarga
     */
    DownloadTask(DownloadItem di) {
        this.downloadItem = di;
        this.tempName = new Date().getTime() + "";
    }

    @Override
    protected Void call() throws Exception {
        // Prepara la descarga
        URLConnection connection = new URL(downloadItem.getEpisode().getDownloadUrl()).openConnection();
        long fileSize = connection.getContentLength();
        InputStream inputsream = null;
        LimitedBandwidthInputStream is = null;
        OutputStream os = null;
        DownloadManager dm = DownloadManager.getInstance();

        try {
            inputsream = connection.getInputStream();
            is = new LimitedBandwidthInputStream(inputsream, dm.getBandWidth());
            is.bandwidthProperty().bind(dm.bandWidthProperty());
            os = Files.newOutputStream(Paths.get(this.getTempName()));

            // Descarga el archivo
            long nread = 0l;
            byte[] buff = new byte[BUFF_SIZE];
            int n;
            while ((n = is.read(buff)) > 0){
                os.write(buff, 0, n);
                nread += n;
                updateProgress(nread, fileSize);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                is.close();
            }
            if (os != null) {
                os.close();
            }
        }
        return null;
    }

    @Override
    protected void succeeded() {
        super.succeeded();
        this.saveFile();
        DownloadManager.getInstance().startNewTask(true);
        downloadItem = null;
    }

    /**
     * Dado un nombre genera la ruta de destino
     * @param name Nombre del archivo a generar
     * @return Ruta de destino del archivo
     */
    private String getDestinationUrl(String name) {
        String dest = DownloadManager.getInstance().getDestination();
        return dest + File.separator + name + FilenameUtils.EXTENSION_SEPARATOR + "mp3";
    }

    /**
     * Retorna el nombre temporal de descarga del item
     * @return nombre temporal de descarga del item
     */
    private String getTempName() {
        return getDestinationUrl(tempName);
    }

    /**
     * Guarda el archivo con el nombre definitivo
     */
    private void saveFile() {
        String name = downloadItem.getEpisode().getTitle();
        String dest = getDestinationUrl(name);
        // Genera el nuevo nombre (se ejecuta en caso de ya existir el mismo)
        for (int i = 1; i < Integer.MAX_VALUE && (new File(dest).exists()); i++) {
            dest = getDestinationUrl(name + "-" + i);
        }
        File or = new File(getTempName());
        File de = new File(dest);
        or.renameTo(de);
    }

}

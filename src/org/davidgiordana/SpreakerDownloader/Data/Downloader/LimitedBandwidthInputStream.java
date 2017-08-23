package org.davidgiordana.SpreakerDownloader.Data.Downloader;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/*********************************************
 Copyright (c) 2001 by Daniel Matuschek
 *********************************************/


/**
 * A FilterInputStream with a limited bandwith
 *
 * This implements an filter for an existing input stream that allows
 * it to limit the read bandwidth. This can be useful for network
 * streams that should be limited to a specified bandwidth.
 *
 * @author <a href="mailto: daniel@matuschek.net">Daniel Matuschek</a>
 * @author davidgiodana
 */
public class LimitedBandwidthInputStream extends FilterInputStream {

    /** usable bandwidth in bytes/second **/
    private IntegerProperty bandwidth;

    /** bandwidth limit will be calculated form the start time **/
    private boolean isReading = false;

    /** number of bytes read **/
    private int count = 0;

    /** check bandwidth every n bytes **/
    private static int CHECK_INTERVAL = 100;

    /** start time **/
    long starttime = 0;

    /** used time **/
    long usedtime = 0;


    /**
     * initializes the LimitedBandWidth stream
     */
    public LimitedBandwidthInputStream (InputStream in, int bandwidth) throws IOException {
        super(in);
        this.bandwidth = new SimpleIntegerProperty(0);
        if (bandwidth > 0) {
            this.bandwidth.setValue(bandwidth);
        }
        count = 0;
    }

    /**
     * Reads the next byte.
     *
     * Reads the next byte of data from this input stream. The value byte
     * is returned as an int in the range 0 to 255. If no byte is available
     * because the end of the stream has been reached, the value -1 is
     * returned. This method blocks until input data is available, the end
     * of the stream is detected, or an exception is thrown.
     * If the bandwidth consumption exceeds the defined limit, read will block
     * until the bandwidth is in the limit again.
     *
     * @return the next byte from the stream or -1 if end-of-stream
     */
    public int read() throws IOException {
        long currentBandwidth;

        if (! isReading) {
            starttime = System.currentTimeMillis();
            isReading = true;
        }

        // do bandwidth check only if bandwidth
        if ((bandwidth.get() > 0) && ((count % CHECK_INTERVAL) == 0)) {
            do {
                usedtime = System.currentTimeMillis()-starttime;
                if (usedtime > 0) {
                    currentBandwidth = (count*1000) / usedtime;
                } else {
                    currentBandwidth = 0;
                }
                if (currentBandwidth > bandwidth.get()) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {}
                }
            } while (currentBandwidth > bandwidth.get());
        }

        count++;
        return super.read();
    }

    /**
     * Shortcut for read(b,0,b.length)
     *
     * @see #read(byte[], int, int)
     */
    public int read(byte[] b) throws IOException {
        return read(b, 0, b.length);
    }

    /**
     * Reads a block of bytes from the stream.
     *
     * If the bandwith is not limited, it simply used the
     * read(byte[], int, int) method of the input stream, otherwise it
     * uses multiple read() request to enforce bandwith limitation (this
     * is easier to implement using byte reads).
     *
     * @return the number of bytes read or -1 at end of stream
     */
    public int read(byte[] b, int off, int len) throws IOException {
        int mycount = 0;
        int current = 0;
        // limit bandwidth ?
        if (bandwidth.get() > 0) {
            for (int i=off; i < off+len; i++) {
                current = read();
                if (current == -1) {
                    return mycount;
                } else {
                    b[i]=(byte)current;
                    count++;
                    mycount++;
                }
            }
            return mycount;
        } else {
            return in.read(b, off, len);
        }
    }

    /**
     * GETTERS & SETTERS
     */

    public int getBandwidth() {
        return bandwidth.get();
    }

    public IntegerProperty bandwidthProperty() {
        return bandwidth;
    }

    public void setBandwidth(int bandwidth) {
        this.bandwidth.set(bandwidth);
    }
}

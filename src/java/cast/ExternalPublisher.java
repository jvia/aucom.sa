package cast;

import cast.architecture.ChangeFilterFactory;
import cast.architecture.ManagedComponent;
import cast.architecture.WorkingMemoryChangeReceiver;
import cast.cdl.CASTTime;
import cast.cdl.WorkingMemoryChange;
import cast.cdl.WorkingMemoryOperation;
import count.Count;
import experiments.Counter;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A simple component which exposes inter-subarchitecture communication
 * to other programs.
 *
 * @author Jeremiah Via <jxv911@cs.bham.ac.uk>
 */
public class ExternalPublisher extends ManagedComponent implements WorkingMemoryChangeReceiver {

    private int port;
    private ServerSocket server;
    private Socket client;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private boolean sendingMessage;

    private static List<String> filter;

    public ExternalPublisher(int port) {
        this.port = port;
        // Add to the filter list
        filter = new ArrayList<String>();
        //filter.add("counter");
        //filter.add("map.manager");
        //filter.add("place.manager");
        //filter.add("manual.gui");
    }

    public ExternalPublisher() {
        this(5555);
    }

    @Override
    protected void start() {
        // Subscribe to all change events
        StringBuilder b = new StringBuilder("Listening on: ");
        for (String name : getComponentManager().getComponentDescriptions().keySet()) {
            b.append(name).append(" ");
            addChangeFilter(ChangeFilterFactory.createSourceFilter(name, WorkingMemoryOperation.WILDCARD), this);
        }
        println(b);

        connect();
    }

    private void connectedStart() {
        // Open connection to aucom
        openStreams();
        createClientMonitor();

        // start sending data
        sendingMessage = true;
    }

    @Override
    protected void destroy() {
        // shutdown the client socket if it is still connected
        if (client != null && !client.isClosed()) {
            shutdown();
        }
    }

    /**
     * To make the logging less intrusive.
     *
     * @param lvl logging level
     * @param msg description
     * @param ex  error class
     */
    private void log(Level lvl, String msg, Object ex) {
        Logger.getLogger(ExternalPublisher.class.getName()).log(lvl, msg, ex);
    }

    /**
     * Call back method that is executed when any inter-component communication
     * occurs.
     * <p/>
     * The method simply collects the data necessary for the fault detector
     * and writes it to a socket the fault detector is subscribing to.
     *
     * @param wmc the change in working memory
     * @throws CASTException bad memory change
     */
    @Override
    public void workingMemoryChanged(WorkingMemoryChange wmc) throws CASTException {
        // Print timestamp, count, and source
        println(String.format("%-8s %-10s %-11s %-20s [%s]",
                              "<" + Counter.getCount() + ">",
                              "[" + cast2ms(wmc.timestamp) + "]",
                              "[" + wmc.operation.name() + "]",
                              "[" + wmc.src + "]",
                              wmc.address.subarchitecture));

        // Nothing to write until we have a cast
        if (client == null || (!client.isConnected() && !sendingMessage))
            return;
        if (Count.isErrorCondition() && wmc.src.equals("slam.process")) {
            println("Filtering slam.process");
            return;
        }

        // Filter certain messages
        if (filter.contains(wmc.src)) {
            println("Filtering: " + wmc.src);
            return;
        }


        try {
            if (sendingMessage) {
                output.writeObject(new String[]{String.valueOf(Cast2Ms(wmc.timestamp)),
                                                wmc.operation.name(),
                                                wmc.src,
                                                wmc.address.subarchitecture});
                output.flush();
            }
        } catch (IOException ex) {
            log(Level.SEVERE, "Could not send data to client", ex);
        }
    }

    /**
     * Convert cast time to milliseconds.
     *
     * @param ct cast time object containing seconds and microseconds.
     * @return milliseconds
     */
    private long Cast2Ms(CASTTime ct) {
        return 1000 * ct.s + (ct.us / 1000);
    }

    private void connect() {

        // Create server & accept cast from client
        try {
            println("Waiting...");
            server = new ServerSocket(port);
            server.setSoTimeout(100);
        } catch (IOException ex) {
            log(Level.SEVERE, "Could not create server", ex);
        }

        new Timer().scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                try {
                    client = server.accept();
                    connectedStart();
                } catch (IOException ex) {
                    // stay silent about timeout
                }

            }
        }, 0, 500);
    }

    private void openStreams() {
        // open output stream
        try {
            output = new ObjectOutputStream(client.getOutputStream());
            output.flush();
        } catch (IOException ex) {
            log(Level.SEVERE, "Could not create output stream", ex);
        }

        // open input stream
        try {
            input = new ObjectInputStream(client.getInputStream());
        } catch (IOException ex) {
            log(Level.SEVERE, "Could not create input stream", ex);
        }
    }

    private void createClientMonitor() {
        // make a thread to monitor input from client
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    String[] fromCast;
                    boolean done = false;
                    try {
                        while (!done) {
                            fromCast = (String[]) input.readObject();
                            log(Level.INFO, String.format("Received %s from client", fromCast[0]), null);

                            // initiate shutdown if cast says bye
                            if (fromCast[0].equals(".")) {
                                log(Level.INFO, "Received shutdown from client", null);
                                output.writeObject(new String[]{"."});
                                output.flush();
                                done = true;
                                sendingMessage = false;
                            }
                        }
                    } catch (ClassNotFoundException ex) {
                        log(Level.SEVERE, "Could not cast to String[]", ex);
                    } catch (IOException ex) {
                        log(Level.SEVERE, "Error reading input from client", ex);
                    }

                    client.close();
                } catch (IOException ex) {
                    log(Level.SEVERE, "IO exception closing client cast", ex);
                }


                restart();
            }
        }).start();
    }

    private void restart() {
        shutdown();
        connect();
    }

    private void shutdown() {
        sendingMessage = false;

        try {
            log(Level.INFO, "Sending shutdown signal to external client", null);
            output.writeObject(new String[]{"."});
            output.flush();
        } catch (IOException ex) {
            log(Level.SEVERE, "Could not send shutdown message", ex);
        }

        try {
            String[] stop = new String[1];
            try {
                stop = (String[]) input.readObject();
            } catch (ClassNotFoundException ex) {
                log(Level.SEVERE, "Could not cast to String[]", ex);
            }
            if (stop[0].equals(".")) {
                log(Level.INFO, "Received acknowledgement from client", null);
                try {
                    server.close();
                } catch (IOException ex) {
                    log(Level.INFO, "IO waiting...", ex);
                }
            }
        } catch (IOException ex) {
            log(Level.SEVERE, "Error reading client shutdown response", ex);
        }
    }

    public static long cast2ms(CASTTime ct) {
        return 1000 * ct.s + (ct.us / 1000);
    }
}

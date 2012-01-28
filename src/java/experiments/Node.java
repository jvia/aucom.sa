package experiments;

import cast.AlreadyExistsOnWMException;
import cast.CASTException;
import cast.architecture.ChangeFilterFactory;
import cast.architecture.ManagedComponent;
import cast.architecture.WorkingMemoryChangeReceiver;
import cast.cdl.WorkingMemoryChange;
import updater.UpdateMessage;

import java.util.*;

public class Node extends ManagedComponent implements WorkingMemoryChangeReceiver {

    private List<String> follows;
    private boolean isEndNode;
    private boolean isStartNode;
    private Delay delay;

    private final static String CONFIG_FOLLOWS = "--follows";
    private final static String CONFIG_IS_END = "--end";
    private final static String CONFIG_IS_START = "--start";
    private final static String CONFIG_DELAY = "--delay";

    @Override
    protected void configure(Map<String, String> config) {
        follows = (config.containsKey(CONFIG_FOLLOWS)) ? parseFollows(config.get(CONFIG_FOLLOWS)) : Collections.<String>emptyList();
        isEndNode = config.containsKey(CONFIG_IS_END);
        isStartNode = config.containsKey(CONFIG_IS_START);
        delay = (config.containsKey(CONFIG_DELAY)) ? Delay.parseDelay(config.get(CONFIG_DELAY)) : new Delay();
    }

    private List<String> parseFollows(String s) {
        String[] components = s.split(" ");
        return Arrays.asList(components);
    }

    @Override
    protected void start() {
        for (String component : follows)
            addChangeFilter(ChangeFilterFactory.createSourceFilter(component), this);
    }

    @Override
    public void run() {
        if (isStartNode) {
            Random random = new Random();

            while (isRunning()) {
                try {
                    UpdateMessage msg = new UpdateMessage(random.nextInt());
                    addToWorkingMemory(newDataID(), msg);
                    delay.execute();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (AlreadyExistsOnWMException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void workingMemoryChanged(WorkingMemoryChange wmc) throws CASTException {
        try {
            delay.execute();
            int update = getMemoryEntry(wmc.address, UpdateMessage.class).msg;

            if (isEndNode) {
                println(String.format("<%d> Deleting %d", System.currentTimeMillis(), update));
                deleteFromWorkingMemory(wmc.address);
            } else {
                println(String.format("<%d> Received %d", System.currentTimeMillis(), update));
                addToWorkingMemory(newDataID(), new UpdateMessage(update));
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}



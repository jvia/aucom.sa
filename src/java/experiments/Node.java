package experiments;

import cast.AlreadyExistsOnWMException;
import cast.CASTException;
import cast.architecture.ChangeFilterFactory;
import cast.architecture.ManagedComponent;
import cast.architecture.WorkingMemoryChangeReceiver;
import cast.cdl.CASTTime;
import cast.cdl.WorkingMemoryChange;
import updater.UpdateMessage;

import java.util.*;

public class Node extends ManagedComponent implements WorkingMemoryChangeReceiver {

    private List<String> follows;
    private boolean isEndNode;
    private boolean isStartNode;
    private Delay delay;
    private Fault fault;

    private final static String CONFIG_FOLLOWS = "--follows";
    private final static String CONFIG_IS_END = "--end";
    private final static String CONFIG_IS_START = "--start";
    private final static String CONFIG_DELAY = "--delay";
    private final static String CONFIG_FAULT = "--fault";

    @Override
    protected void configure(Map<String, String> config) {
        follows = (config.containsKey(CONFIG_FOLLOWS)) ? parseFollows(config.get(CONFIG_FOLLOWS)) : Collections.<String>emptyList();
        isEndNode = config.containsKey(CONFIG_IS_END);
        isStartNode = config.containsKey(CONFIG_IS_START);
        delay = (config.containsKey(CONFIG_DELAY)) ? Delay.parseDelay(config.get(CONFIG_DELAY)) : new Delay();
        fault = (config.containsKey(CONFIG_FAULT) ? Fault.parseFault(config.get(CONFIG_FAULT)) : new Fault());

    }

    private List<String> parseFollows(String s) {
        String[] components = s.split(" ");
        return Arrays.asList(components);
    }

    @Override
    protected void start() {
        println(String.format("Start node: %b; End node: %b; Delay: %s %d %d; Faulty: %d--%d",
                              isStartNode, isEndNode, delay.getDelayType(), delay.getMin(),
                              delay.getMax(), fault.getDie(), fault.getRecover()));
        StringBuilder following = new StringBuilder();
        for (String component : follows) {
            following.append(follows).append(" ");
            addChangeFilter(ChangeFilterFactory.createSourceFilter(component), this);
        }
        println("Listening to: " + following.toString());

    }

    @Override
    public void run() {
        if (isStartNode) {
            Random random = new Random();

            while (isRunning()) {
                try {
                    UpdateMessage msg = new UpdateMessage(random.nextInt());
//                    println(String.format("<%d> Sending %d", cast2ms(getCASTTime()), msg.msg));
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
        if (fault.isFaulty(Counter.getCount(), this)) {
            return;
        }

        try {
            delay.execute();
            int update = getMemoryEntry(wmc.address, UpdateMessage.class).msg;

            if (isEndNode) {
//                println(String.format("<%d> Deleting %d", cast2ms(getCASTTime()), update));
                deleteFromWorkingMemory(wmc.address);
            } else {
//                println(String.format("<%d> Received %d", cast2ms(getCASTTime()), update));
                addToWorkingMemory(newDataID(), new UpdateMessage(update));
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static long cast2ms(CASTTime ct) {
        return 1000 * ct.s + (ct.us / 1000);
    }
}



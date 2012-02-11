package experiments;

import cast.CASTException;
import cast.architecture.ChangeFilterFactory;
import cast.architecture.ManagedComponent;
import cast.architecture.WorkingMemoryChangeReceiver;
import cast.cdl.WorkingMemoryChange;
import cast.cdl.WorkingMemoryOperation;

/**
 * Just count memory events. Used to control the induction of errors.
 *
 * @author jxv911
 */
public class Counter extends ManagedComponent implements WorkingMemoryChangeReceiver {

    private static long count;

    public Counter() {
        count = 0;
    }

    @Override
    public void start() {
        StringBuilder b = new StringBuilder("Listening on: ");
        for (String name : getComponentManager().getComponentDescriptions().keySet()) {
            b.append(name).append(" ");
            addChangeFilter(ChangeFilterFactory.createSourceFilter(name, WorkingMemoryOperation.WILDCARD), this);
        }
        println(b);
    }

    @Override
    public void workingMemoryChanged(WorkingMemoryChange wmc) throws CASTException {
        if (count % 100 == 0)
            println(" ++ Counter: " + count + " ++");
        ++count;
    }

    public static long getCount() {
        return count;
    }
}

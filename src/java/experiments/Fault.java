package experiments;

import cast.core.CASTComponent;

/**
 * @author Jeremiah Via <jxv911@cs.bham.ac.uk>
 */
public class Fault {
    private long die;
    private long recover;

    public Fault(long die, long recover) {
        this.die = die;
        this.recover = recover;
    }

    public Fault(long die) {
        this(die, Long.MAX_VALUE);
    }

    /**
     * A constructor to ensure no fault ever occurs.
     */
    public Fault() {
        this(Long.MAX_VALUE, Long.MIN_VALUE);
    }

    public boolean isFaulty(long count, CASTComponent component) {
        if (count == die)
            component.println(String.format("<< Error at %d ms, count %d >>", System.currentTimeMillis(), count));
        if (count == recover)
            component.println(String.format("<< Recover at %d ms, count %d >>", System.currentTimeMillis(), count));
        return die <= count && count <= recover;
    }

    public static Fault parseFault(String s) {
        String[] config = s.split(" ");
        switch (config.length) {
            case 1:
                return new Fault(Long.parseLong(config[0]));
            case 2:
                return new Fault(Long.parseLong(config[0]), Long.parseLong(config[1]));
            default:
                throw new IllegalArgumentException("Trying to create fault with no count values.");
        }
    }

    public long getDie() {
        return die;
    }

    public long getRecover() {
        return recover;
    }
}

package experiments;

import org.apache.log4j.Logger;

import java.util.Random;

/**
 * @author Jeremiah Via <jxv911@cs.bham.ac.uk>
 */
public class Delay {

    enum DelayType {
        RANDOM,
        GAUSSIAN,
        NONE
    }

    private DelayType delayType;
    private int min;
    private int max;
    private Random random;
    private final Logger log = Logger.getLogger(getClass());

    public Delay(DelayType delayType, int min, int max) {
        this.delayType = delayType;
        this.min = min;
        this.max = max;
        random = new Random();
        log.debug(String.format("%s %d %d", delayType, min, max));
    }

    public Delay() {
        this(DelayType.NONE, 0, 0);
    }

    public void execute() throws InterruptedException {
        switch (delayType) {
            case RANDOM:
                Thread.sleep(min + random.nextInt(max - min));
                break;
            case GAUSSIAN:
                Thread.sleep((long) (min + random.nextGaussian() * max));
                break;
            case NONE:
                break;
        }
    }

    public static Delay parseDelay(String s) {
        String[] config = s.split(" ");
        if ("none".equals(config[0]))
            return new Delay();

        int min = Integer.parseInt(config[1]);
        int max = Integer.parseInt(config[2]);
        return new Delay(("random".equals(config[0]) ? DelayType.RANDOM : DelayType.GAUSSIAN), min, max);
    }

    public int getMax() {
        return max;
    }

    public int getMin() {
        return min;
    }

    public DelayType getDelayType() {
        return delayType;
    }
}

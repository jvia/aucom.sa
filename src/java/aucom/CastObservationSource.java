package aucom;

import aucom.data.Observation;
import nu.xom.Element;
import aucom.fts.source.ActionFailedException;
import aucom.fts.source.AucomSourceAdapter;
import aucom.fts.source.SourceStatus;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import nu.xom.Attribute;

/**
 *
 * @author Jeremiah Via <jxv911@cs.bham.ac.uk>
 */
public class CastObservationSource extends AucomSourceAdapter<Observation> {

    private ConnectionManager cast;
    private LinkedBlockingQueue<String[]> queue;

    public CastObservationSource()
    {
        super("CastObservationSource");
        queue = new LinkedBlockingQueue<String[]>();
    }

    @Override
    protected void iDisconnect() throws ActionFailedException
    {
        cast.shutdown();
        setState(SourceStatus.DISCONNECTED);
    }

    @Override
    protected void iConnect() throws ActionFailedException
    {
        if (getStatus().equals(SourceStatus.CONNECTED))
            return;
        cast = new ConnectionManager(queue);
        setState(SourceStatus.CONNECTED);
    }

    @Override
    protected Observation iNextItem() throws Exception
    {
        String[] msg = queue.take();
        Observation obs = null;
        
        if (msg[0].equals(".")) {
            setsendLastElement();
            obs = new Observation(null, 0L);
        } else {
            Element element = new Element("cast");
            // <cast eventType="ADD" generatorType="src" memoryType="0:null" />    
            element.addAttribute(new Attribute("eventType", msg[1]));
            element.addAttribute(new Attribute("generatorType", msg[2]));
            element.addAttribute(new Attribute("memoryType", msg[3]));
            obs = new Observation(element, Long.parseLong(msg[0]));
        }
        return obs;
    }
    
    private void log(Level level, String msg, Object ex)
    {
        Logger.getLogger(CastObservationSource.class.getName()).log(level, msg, ex);
    }
}
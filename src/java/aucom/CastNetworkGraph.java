package aucom;

import aucom.data.Observation;
import aucom.data.timeseries.ObservationTimeSeries;
import aucom.data.timeseries.TimeSeries;
import aucom.fts.graph.AbstractAucomGraph;
import aucom.fts.sink.TimeSeriesSink;

/**
 *
 * @author Jeremiah Via <jxv911@cs.bham.ac.uk>
 */
public class CastNetworkGraph extends AbstractAucomGraph {

    CastObservationSource source;
    TimeSeriesSink<Observation> sink;

    public CastNetworkGraph()
    {
        super("CastNetworkGraph");
        initGraph();
    }

    @Override
    protected void initGraph()
    {
        source = new CastObservationSource();
        sink = new TimeSeriesSink<Observation>(new ObservationTimeSeries());
        graph.connect(source, sink);
    }

    @Override
    protected String getReason()
    {
        return "no input";
    }

    @Override
    public boolean preconditionsSatisfied()
    {
        // TODO: check if cast is running
        return true;
    }

    @Override
    protected void cleanUp()
    {
        // ignored
    }

    TimeSeries<Observation> getObservationTimeSeries()
    {
        return sink.getOutput();
    }
}

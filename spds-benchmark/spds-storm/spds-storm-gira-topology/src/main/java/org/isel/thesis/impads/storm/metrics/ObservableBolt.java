package org.isel.thesis.impads.storm.metrics;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Tuple;
import org.isel.thesis.impads.metrics.api.Observable;
import org.isel.thesis.impads.metrics.collector.MetricsCollectorConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class ObservableBolt implements IRichBolt {

    private static final Logger LOG = LoggerFactory.getLogger(ObservableBolt.class);

    private final MetricsCollectorConfiguration config;
    private final IRichBolt richBolt;

    private StormObservableMetricsCollector collector;

    private ObservableBolt(final MetricsCollectorConfiguration config
            , final IRichBolt richBolt) {
        this.config = config;
        this.richBolt = richBolt;
    }

    public static ObservableBolt observe(MetricsCollectorConfiguration config) {
        return observe(config, null);
    }

    public static ObservableBolt observe(MetricsCollectorConfiguration config
            , IRichBolt richBolt) {
        return new ObservableBolt(config
                , richBolt);
    }

    @Override
    public void prepare(Map<String, Object> topoConf, TopologyContext context, OutputCollector collector) {

        this.collector = new StormObservableMetricsCollector(config);

        if (richBolt != null) {
            richBolt.prepare(topoConf, context, collector);
        }
    }

    @Override
    public void execute(Tuple input) {
        LOG.info("Sending latency metrics");

        this.collector.collect((Observable<?>) input.getValueByField("value"));

        if (richBolt != null) {
            richBolt.execute(input);
        }
    }

    @Override
    public void cleanup() {

    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {

    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        return null;
    }
}

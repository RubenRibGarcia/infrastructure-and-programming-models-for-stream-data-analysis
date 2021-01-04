package org.isel.thesis.impads.storm.topology;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.typesafe.config.ConfigFactory;
import org.apache.storm.Config;
import org.apache.storm.StormSubmitter;
import org.apache.storm.generated.AlreadyAliveException;
import org.apache.storm.generated.AuthorizationException;
import org.apache.storm.generated.InvalidTopologyException;
import org.apache.storm.topology.TopologyBuilder;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.isel.thesis.impads.metrics.Observable;
import org.isel.thesis.impads.storm.fasterxml.jackson.deserializers.InstanteDeserializer;
import org.isel.thesis.impads.storm.fasterxml.jackson.serializers.ObservableSerializer;
import org.locationtech.jts.geom.GeometryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.time.Instant;

public class MainStormGiraTopology {

    private static final Logger LOG = LoggerFactory.getLogger(MainStormGiraTopology.class);

    public static void main(String... args) throws InvalidTopologyException
            , AuthorizationException
            , AlreadyAliveException {

        if (args.length <= 0) {
            LOG.info("usage: storm jar </path/to/spds-storm-gira-topology.jar> " +
                    "org.isel.thesis.impads.storm.streams.topology.MainStormGiraTopology </path/to/config/file>");
        } else {
            File file = new File(args[0]);
            com.typesafe.config.Config config = ConfigFactory.parseFile(file);

            ConfigurationContainer configurationContainer =
                    ConfigurationContainer.setup(config);

            final ObjectMapper mapper = newMapper();
            final GeometryFactory geoFactory = initGeometryFactory();

            Config stormConfig = new Config();
            stormConfig.setMaxSpoutPending(5000);
            stormConfig.setFallBackOnJavaSerialization(true);
            stormConfig.setNumWorkers(configurationContainer.getTopologyConfiguration().getParallelism());

            TopologyBuilder topologyBuilder = new TopologyBuilder();

            StormSubmitter.submitTopologyWithProgressBar("gira-travel-patterns"
                    , stormConfig
                    , GiraTravelsTopologyBuilder.build(topologyBuilder
                            , configurationContainer
                            , mapper
                            , geoFactory));
        }
    }

    private static ObjectMapper newMapper() {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(Instant.class, new InstanteDeserializer());
        module.addSerializer(Observable.class, new ObservableSerializer());
        mapper.registerModule(module);

        return mapper;
    }

    private static GeometryFactory initGeometryFactory() {
        return JTSFactoryFinder.getGeometryFactory();
    }
}

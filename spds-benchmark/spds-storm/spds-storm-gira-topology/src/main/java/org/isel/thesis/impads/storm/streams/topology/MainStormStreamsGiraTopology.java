package org.isel.thesis.impads.storm.streams.topology;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.typesafe.config.ConfigFactory;
import org.apache.storm.Config;
import org.apache.storm.StormSubmitter;
import org.apache.storm.generated.AlreadyAliveException;
import org.apache.storm.generated.AuthorizationException;
import org.apache.storm.generated.InvalidTopologyException;
import org.apache.storm.streams.StreamBuilder;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.isel.thesis.impads.storm.fasterxml.jackson.deserializers.InstanteDeserializer;
import org.locationtech.jts.geom.GeometryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.time.Instant;

public class MainStormStreamsGiraTopology {

    private static final Logger LOG = LoggerFactory.getLogger(MainStormStreamsGiraTopology.class);

    public static void main(String... args) throws InvalidTopologyException
            , AuthorizationException
            , AlreadyAliveException {

        if (args.length <= 0) {
            LOG.info("usage: storm jar </path/to/spds-storm-gira-topology.jar> " +
                    "org.isel.thesis.impads.storm.streams.topology.MainStormStreamsGiraTopology </path/to/config/file>");
        }
        else {
            File file = new File(args[0]);
            com.typesafe.config.Config config = ConfigFactory.parseFile(file);

            ConfigurationContainer configurationContainer =
                    ConfigurationContainer.setup(config);

            StreamBuilder streamBuilder = new StreamBuilder();
            ObjectMapper mapper = newMapper();
            final GeometryFactory geoFactory = initGeometryFactory();

            TopologyStreamSources topologySources =
                    TopologyStreamSources.initializeTopologySources(configurationContainer
                        , mapper
                        , streamBuilder);

            Config stormConfig = new Config();
            stormConfig.setMaxSpoutPending(5000);
            stormConfig.setDebug(false);

            StormSubmitter.submitTopology("gira-travel-patterns"
                    , stormConfig
                    , GiraTravelsStreamTopologyBuilder.build(streamBuilder
                            , topologySources
                            , geoFactory
                            , configurationContainer
                            , mapper));
        }
    }

    private static ObjectMapper newMapper() {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(Instant.class, new InstanteDeserializer());
        mapper.registerModule(module);

        return mapper;
    }

    private static GeometryFactory initGeometryFactory() {
        return JTSFactoryFinder.getGeometryFactory();
    }
}

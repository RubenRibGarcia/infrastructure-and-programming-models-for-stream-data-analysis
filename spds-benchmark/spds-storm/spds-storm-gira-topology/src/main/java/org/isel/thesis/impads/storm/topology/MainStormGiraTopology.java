package org.isel.thesis.impads.storm.topology;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.typesafe.config.ConfigFactory;
import org.apache.storm.Config;
import org.apache.storm.StormSubmitter;
import org.apache.storm.generated.AlreadyAliveException;
import org.apache.storm.generated.AuthorizationException;
import org.apache.storm.generated.InvalidTopologyException;
import org.apache.storm.generated.StormTopology;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.isel.thesis.impads.giragen.datamodel.api.ipma.api.IpmaStationValue;
import org.isel.thesis.impads.metrics.api.Observable;
import org.isel.thesis.impads.storm.fasterxml.jackson.deserializers.InstanteDeserializer;
import org.isel.thesis.impads.storm.fasterxml.jackson.deserializers.IpmaStationValueDeserializer;
import org.isel.thesis.impads.storm.fasterxml.jackson.serializers.ObservableSerializer;
import org.isel.thesis.impads.storm.spouts.rabbitmq.conf.RabbitMQConfiguration;
import org.isel.thesis.impads.storm.topology.bolts.ExtendedTopologyBuilder;
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
            RabbitMQConfiguration rabbitMQConfiguration = RabbitMQConfiguration.initializeRabbitMQConfiguration(config);

            ExtendedTopologyBuilder topologyBuilder = new ExtendedTopologyBuilder();
            ObjectMapper mapper = newMapper();
            final GeometryFactory geoFactory = initGeometryFactory();

            TopologySpouts.initializeTopologySpouts(rabbitMQConfiguration
                    , mapper
                    , topologyBuilder);

            Config stormConfig = new Config();
            stormConfig.setNumWorkers(24);
            stormConfig.setMaxSpoutPending(5000);

            StormTopology topology = GiraTravelsTopologyBuilder
                    .build(topologyBuilder
                            , geoFactory
                            , rabbitMQConfiguration);

            StormSubmitter.submitTopologyWithProgressBar("gira_travels_topology"
                    , stormConfig
                    , topology);
        }
    }

    private static ObjectMapper newMapper() {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(Instant.class, new InstanteDeserializer());
        module.addDeserializer(IpmaStationValue.class, new IpmaStationValueDeserializer());
        module.addSerializer(Observable.class, new ObservableSerializer());
        mapper.registerModule(module);

        return mapper;
    }

    private static GeometryFactory initGeometryFactory() {
        return JTSFactoryFinder.getGeometryFactory();
    }

}

package org.isel.thesis.impads.kafka.stream.topology;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.isel.thesis.impads.kafka.stream.fasterxml.jackson.deserializers.InstanteDeserializer;
import org.isel.thesis.impads.kafka.stream.fasterxml.jackson.deserializers.ObservableJoinedGiraTravelsWithWazeDeserializer;
import org.isel.thesis.impads.kafka.stream.fasterxml.jackson.deserializers.ObservableJoinedGiraTravelsWithWazeJamsDeserializer;
import org.isel.thesis.impads.kafka.stream.fasterxml.jackson.deserializers.ObservableSimplifiedGiraTravelsDeserializer;
import org.isel.thesis.impads.kafka.stream.fasterxml.jackson.deserializers.ObservableSimplifiedWazeIrregularitiesDeserializer;
import org.isel.thesis.impads.kafka.stream.fasterxml.jackson.deserializers.ObservableSimplifiedWazeJamsDeserializer;
import org.isel.thesis.impads.kafka.stream.fasterxml.jackson.serializers.ObservableGiraTravelsWithWazeResultSerializer;
import org.isel.thesis.impads.kafka.stream.fasterxml.jackson.serializers.ObservableJoinedGiraTravelsWithWazeJamsSerializer;
import org.isel.thesis.impads.kafka.stream.fasterxml.jackson.serializers.ObservableJoinedGiraTravelsWithWazeSerializer;
import org.isel.thesis.impads.kafka.stream.fasterxml.jackson.serializers.ObservableSimplifiedGiraTravelsSerializer;
import org.isel.thesis.impads.kafka.stream.fasterxml.jackson.serializers.ObservableSimplifiedWazeIrregularitiesSerializer;
import org.isel.thesis.impads.kafka.stream.fasterxml.jackson.serializers.ObservableSimplifiedWazeJamsSerializer;
import org.isel.thesis.impads.kafka.stream.topology.model.ObservableGiraTravelsWithWazeResults;
import org.isel.thesis.impads.kafka.stream.topology.model.ObservableJoinedGiraTravelsWithWaze;
import org.isel.thesis.impads.kafka.stream.topology.model.ObservableJoinedGiraTravelsWithWazeJams;
import org.isel.thesis.impads.kafka.stream.topology.model.ObservableSimplifiedGiraTravelsModel;
import org.isel.thesis.impads.kafka.stream.topology.model.ObservableSimplifiedWazeIrregularitiesModel;
import org.isel.thesis.impads.kafka.stream.topology.model.ObservableSimplifiedWazeJamsModel;
import org.isel.thesis.impads.kafka.stream.topology.utils.ObservableMeasure;
import org.locationtech.jts.geom.GeometryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.time.Instant;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

public class MainKafkaStreamGiraTopology {

    private static final Logger logger = LoggerFactory.getLogger(MainKafkaStreamGiraTopology.class);

    public static void main(String... args) {
        logger.info("Args length: {}", args.length);
        for (String arg : args) {
            logger.info("Arg: {}", arg);
        }
        String configFilePath = args[0];

        logger.info("Config File Path: {}", configFilePath);
        final File file = new File(configFilePath);
        Config config = ConfigFactory.parseFile(file);

        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, config.getString("kafka.stream.application.id"));
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, config.getString("kafka.stream.bootstrap_servers"));
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(StreamsConfig.PROCESSING_GUARANTEE_CONFIG, "at_least_once");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");

        final StreamsBuilder streamsBuilder = new StreamsBuilder();
        ObjectMapper mapper = newMapper();
        final GeometryFactory geoFactory = initGeometryFactory();

        final TopologySources sources =
                TopologySources.initializeTopologySources(streamsBuilder, config, mapper);

        final ObservableMeasure observableMeasure = new ObservableMeasure(config);

        Topology topology = GiraTravelsTopologyBuilder.build(streamsBuilder
                , sources
                , geoFactory
                , mapper
                , observableMeasure);

        logger.info("Topology description: {}", topology.describe());
        final KafkaStreams app = new KafkaStreams(topology, props);

        final CountDownLatch latch = new CountDownLatch(1);

        // attach shutdown handler to catch control-c
        Runtime.getRuntime().addShutdownHook(new Thread("streams-shutdown-hook") {
            @Override
            public void run() {
                app.close();
                latch.countDown();
            }
        });

        try {
            app.cleanUp();
            app.start();
            latch.await();
        } catch (Throwable e) {
            System.exit(1);
        }
        System.exit(0);
    }

    private static ObjectMapper newMapper() {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(Instant.class, new InstanteDeserializer());
        module.addDeserializer(ObservableSimplifiedGiraTravelsModel.class, new ObservableSimplifiedGiraTravelsDeserializer());
        module.addDeserializer(ObservableSimplifiedWazeJamsModel.class, new ObservableSimplifiedWazeJamsDeserializer());
        module.addDeserializer(ObservableSimplifiedWazeIrregularitiesModel.class, new ObservableSimplifiedWazeIrregularitiesDeserializer());
        module.addDeserializer(ObservableJoinedGiraTravelsWithWazeJams.class, new ObservableJoinedGiraTravelsWithWazeJamsDeserializer());
        module.addDeserializer(ObservableJoinedGiraTravelsWithWaze.class, new ObservableJoinedGiraTravelsWithWazeDeserializer());
        module.addSerializer(ObservableSimplifiedGiraTravelsModel.class, new ObservableSimplifiedGiraTravelsSerializer());
        module.addSerializer(ObservableSimplifiedWazeJamsModel.class, new ObservableSimplifiedWazeJamsSerializer());
        module.addSerializer(ObservableSimplifiedWazeIrregularitiesModel.class, new ObservableSimplifiedWazeIrregularitiesSerializer());
        module.addSerializer(ObservableJoinedGiraTravelsWithWazeJams.class, new ObservableJoinedGiraTravelsWithWazeJamsSerializer());
        module.addSerializer(ObservableJoinedGiraTravelsWithWaze.class, new ObservableJoinedGiraTravelsWithWazeSerializer());
        module.addSerializer(ObservableGiraTravelsWithWazeResults.class, new ObservableGiraTravelsWithWazeResultSerializer());
        mapper.registerModule(module);

        return mapper;
    }

    private static GeometryFactory initGeometryFactory() {
        return JTSFactoryFinder.getGeometryFactory();
    }
}

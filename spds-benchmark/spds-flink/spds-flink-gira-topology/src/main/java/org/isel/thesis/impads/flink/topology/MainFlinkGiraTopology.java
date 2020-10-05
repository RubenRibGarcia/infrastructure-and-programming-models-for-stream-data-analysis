package org.isel.thesis.impads.flink.topology;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.module.SimpleModule;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.isel.thesis.impads.flink.fasterxml.jackson.deserializers.InstanteDeserializer;
import org.isel.thesis.impads.flink.fasterxml.jackson.serializers.ObservableSerializer;
import org.isel.thesis.impads.giragen.datamodel.api.ipma.api.IpmaStationValue;
import org.isel.thesis.impads.metrics.api.Observable;
import org.locationtech.jts.geom.GeometryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.time.Instant;

public class MainFlinkGiraTopology {

    private static final Logger logger = LoggerFactory.getLogger(MainFlinkGiraTopology.class);

    private static final String JOB_NAME = "gira-travels-pattern";

    public static void main(String... args) throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
//        env.enableCheckpointing(10000, CheckpointingMode.AT_LEAST_ONCE);
        env.setParallelism(5);

        ExecutionConfig executionConfig = env.getConfig();
        executionConfig.enableObjectReuse();
        executionConfig.setAutoWatermarkInterval(50);

        ParameterTool parameters = ParameterTool.fromArgs(args);

        logger.info("Args length: {}", args.length);
        for (String arg : args) {
            logger.info("Arg: {}", arg);
        }
        String configFilePath = parameters.get("config.file.path");

        logger.info("Config File Path: {}", configFilePath);

        final ObjectMapper mapper = initMapper();
        final GeometryFactory geoFactory = initGeometryFactory();

        final File file = new File(configFilePath);
        final Config conf = ConfigFactory.parseFile(file);

        final TopologySources topologySources =
                TopologySources.initializeTopologySources(env, conf, mapper);

        GiraTravelsTopologyExecutor.execute(conf
                , mapper
                , env
                , topologySources
                , geoFactory);

        env.execute(JOB_NAME);
    }

    private static ObjectMapper initMapper() {
        final ObjectMapper mapper = new ObjectMapper();
        final SimpleModule module = new SimpleModule();
        module.addDeserializer(Instant.class, new InstanteDeserializer());
        module.addSerializer(Observable.class, new ObservableSerializer());
        mapper.registerModule(module);

        return mapper;
    }

    private static GeometryFactory initGeometryFactory() {
        return JTSFactoryFinder.getGeometryFactory();
    }
}

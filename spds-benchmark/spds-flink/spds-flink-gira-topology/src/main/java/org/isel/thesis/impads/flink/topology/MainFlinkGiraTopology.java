package org.isel.thesis.impads.flink.topology;

import com.typesafe.config.Config;
import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.ExecutionMode;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.module.SimpleModule;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.isel.thesis.impads.flink.fasterxml.jackson.deserializers.InstanteDeserializer;
import org.isel.thesis.impads.flink.fasterxml.jackson.serializers.ObservableSerializer;
import org.isel.thesis.impads.flink.topology.utils.ConfigLoader;
import org.isel.thesis.impads.metrics.Observable;
import org.locationtech.jts.geom.GeometryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.Instant;

public class MainFlinkGiraTopology {

    private static final Logger logger = LoggerFactory.getLogger(MainFlinkGiraTopology.class);

    private static final String JOB_NAME = "gira-travels-pattern";

    public static void main(String... args) throws Exception {
        final Config config = ConfigLoader.loadFromParameterTool(ParameterTool.fromArgs(args));

        final ConfigurationContainer configurationContainer =
                ConfigurationContainer.setup(config);

        //---Stream Execution Environment configuration---
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        env.setParallelism(configurationContainer.getTopologyConfiguration().getParallelism());
        env.enableCheckpointing(10000, CheckpointingMode.AT_LEAST_ONCE);

        ExecutionConfig executionConfig = env.getConfig();
        executionConfig.enableObjectReuse();
        executionConfig.setAutoWatermarkInterval(50);

        final ObjectMapper mapper = initMapper();
        final GeometryFactory geoFactory = initGeometryFactory();

        GiraTravelsTopologyBuilder.build(env
                , configurationContainer
                , mapper
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

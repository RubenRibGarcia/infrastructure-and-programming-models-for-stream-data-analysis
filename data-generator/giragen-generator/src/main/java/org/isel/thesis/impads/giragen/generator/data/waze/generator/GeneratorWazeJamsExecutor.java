package org.isel.thesis.impads.giragen.generator.data.waze.generator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.util.concurrent.RateLimiter;
import io.vavr.control.Either;
import org.isel.thesis.impads.giragen.connector.rabbitmq.api.IRabbitMQQueue;
import org.isel.thesis.impads.giragen.connector.rabbitmq.api.IServicesRabbitMQ;
import org.isel.thesis.impads.giragen.data.api.CSVDataReaderError;
import org.isel.thesis.impads.giragen.data.api.EventTimestampGenerator;
import org.isel.thesis.impads.giragen.data.api.ICSVDataReader;
import org.isel.thesis.impads.giragen.data.func.FactoryEventTimestampGenerator;
import org.isel.thesis.impads.giragen.data.func.InMemoryCSVDataReader;
import org.isel.thesis.impads.giragen.generator.api.IGeneratorExecutor;
import org.isel.thesis.impads.giragen.generator.data.waze.conf.GeneratorWazeJamsConfiguration;
import org.isel.thesis.impads.giragen.generator.data.waze.model.WazeJamsDataModel;
import org.isel.thesis.impads.giragen.generator.func.GeneratorDataTask;
import org.isel.thesis.impads.giragen.metrics.func.ServicesMetrics.MetricsCounter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.util.UUID;

public class GeneratorWazeJamsExecutor implements IGeneratorExecutor {

    private final Logger logger = LoggerFactory.getLogger(GeneratorWazeJamsExecutor.class);

    private final EventTimestampGenerator eventTimestampSynthesizer
            = FactoryEventTimestampGenerator.newCurrentEventTimestampSynthesizer();

    private GeneratorWazeJamsConfiguration configuration;
    private final IServicesRabbitMQ servicesRabbitMQ;
    private final ObjectMapper mapper;
    private final MetricsCounter metricsCounter;
    private final RateLimiter rateLimiter;

    @Inject
    public GeneratorWazeJamsExecutor(GeneratorWazeJamsConfiguration configuration
            , IServicesRabbitMQ servicesRabbitMQ
            , ObjectMapper mapper
            , MetricsCounter metricsCounter
            , RateLimiter rateLimiter) {
        this.configuration = configuration;
        this.servicesRabbitMQ = servicesRabbitMQ;
        this.mapper = mapper;
        this.metricsCounter = metricsCounter;
        this.rateLimiter = rateLimiter;
    }

    @Override
    public void submitGenerator() {
        if (configuration.isGeneratorEnabled()) {
            logger.info("Submitting {}", GeneratorWazeJamsExecutor.class.getSimpleName());
            submitTask();
        }
        else {
            logger.info("{} disabled", GeneratorWazeJamsExecutor.class.getSimpleName());
        }
    }

    private void submitTask() {
        logger.info("Submiting {} by reading file from {}"
                , "Waze Jams Task"
                , configuration.getGeneratorDataFilePath().toString());

        new Thread(this::doSubmitTask).start();
    }

    private void doSubmitTask() {
        declareQueue();

        final Either<CSVDataReaderError, ICSVDataReader> csvDataReader =
                InMemoryCSVDataReader.initialize(configuration.getGeneratorDataFilePath());

        if (csvDataReader.isLeft()) {
            logger.error(csvDataReader.getLeft().getMessage());
        }
        else {
            GeneratorDataTask task = new GeneratorDataTask(configuration
                    , servicesRabbitMQ.createRabbitMQClient()
                    , mapper
                    , metricsCounter
                    , csvDataReader.get()
                    , record -> WazeJamsDataModel.fromCSV(record, eventTimestampSynthesizer).map(x -> x)
                    , rateLimiter);

            task.run();
        }
    }

    private void declareQueue() {
        final IRabbitMQQueue queue = IRabbitMQQueue.RabbitMQQueueNaming
                .withName(configuration.getGeneratorQueueName());

        servicesRabbitMQ.withRabbitMQClient(UUID.randomUUID().toString(), client -> {
            try {
                client.declareQueue(queue);
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
            return null;
        });
    }
}

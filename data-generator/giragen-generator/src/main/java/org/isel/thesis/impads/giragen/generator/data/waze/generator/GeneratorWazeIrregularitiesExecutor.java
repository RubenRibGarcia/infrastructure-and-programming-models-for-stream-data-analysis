package org.isel.thesis.impads.giragen.generator.data.waze.generator;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.control.Either;
import org.isel.thesis.impads.giragen.connector.rabbitmq.api.IRabbitMQQueue;
import org.isel.thesis.impads.giragen.connector.rabbitmq.api.IServicesRabbitMQ;
import org.isel.thesis.impads.giragen.data.api.CSVDataReaderError;
import org.isel.thesis.impads.giragen.data.api.EventTimestampGenerator;
import org.isel.thesis.impads.giragen.data.api.ICSVDataReader;
import org.isel.thesis.impads.giragen.data.func.FactoryEventTimestampGenerator;
import org.isel.thesis.impads.giragen.data.func.InMemoryCSVDataReader;
import org.isel.thesis.impads.giragen.generator.api.IGeneratorExecutor;
import org.isel.thesis.impads.giragen.generator.data.waze.conf.GeneratorWazeIrregularitiesConfiguration;
import org.isel.thesis.impads.giragen.generator.data.waze.model.WazeIrregularitiesDataModel;
import org.isel.thesis.impads.giragen.generator.func.GeneratorDataTask;
import org.isel.thesis.impads.giragen.metrics.api.IServicesMetrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

public class GeneratorWazeIrregularitiesExecutor implements IGeneratorExecutor {

    private final Logger logger = LoggerFactory.getLogger(GeneratorWazeIrregularitiesExecutor.class);

    private final EventTimestampGenerator eventTimestampSynthesizer
            = FactoryEventTimestampGenerator.newCurrentEventTimestampSynthesizer();

    private GeneratorWazeIrregularitiesConfiguration configuration;
    private final ExecutorService executorService;
    private final IServicesRabbitMQ servicesRabbitMQ;
    private final ObjectMapper mapper;
    private final IServicesMetrics servicesMetrics;

    @Inject
    public GeneratorWazeIrregularitiesExecutor(GeneratorWazeIrregularitiesConfiguration configuration
            , ExecutorService executorService
            , IServicesRabbitMQ servicesRabbitMQ
            , ObjectMapper mapper
            , IServicesMetrics servicesMetrics) {
        this.configuration = configuration;
        this.executorService = executorService;
        this.servicesRabbitMQ = servicesRabbitMQ;
        this.mapper = mapper;
        this.servicesMetrics = servicesMetrics;
    }

    @Override
    public void submitGenerator() {
        if (configuration.isGeneratorEnabled()) {
            logger.info("Submitting {}", GeneratorWazeIrregularitiesExecutor.class.getSimpleName());
            submitTasks();
        }
        else {
            logger.info("{} disabled", GeneratorWazeIrregularitiesExecutor.class.getSimpleName());
        }
    }

    private void submitTasks() {
        logger.info("Submiting {} with {} threads by reading file from {}"
                , "Waze Irregularities Task"
                , configuration.getGeneratorThreads()
                , configuration.getGeneratorDataFilePath().toString());

        declareQueue();

        final Either<CSVDataReaderError, ICSVDataReader> csvDataReader =
                InMemoryCSVDataReader.initialize(configuration.getGeneratorDataFilePath());

        if (csvDataReader.isLeft()) {
            logger.error(csvDataReader.getLeft().getMessage());
        }
        else {
            for (int i = 0; i < configuration.getGeneratorThreads(); i++) {
                GeneratorDataTask task = new GeneratorDataTask(configuration
                        , servicesRabbitMQ.createRabbitMQClient()
                        , mapper
                        , servicesMetrics
                        , csvDataReader.get()
                        , record -> Either.right(WazeIrregularitiesDataModel.fromCSV(record, eventTimestampSynthesizer)));

                executorService.submit(task);
            }
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

package org.isel.thesis.impads.giragen.generator.func;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.util.concurrent.RateLimiter;
import io.vavr.control.Either;
import org.apache.commons.csv.CSVRecord;
import org.isel.thesis.impads.giragen.connector.rabbitmq.api.IRabbitMQClient;
import org.isel.thesis.impads.giragen.connector.rabbitmq.api.IRabbitMQMessage;
import org.isel.thesis.impads.giragen.connector.rabbitmq.api.IRabbitMQQueue;
import org.isel.thesis.impads.giragen.data.api.ICSVDataReader;
import org.isel.thesis.impads.giragen.generator.api.GeneratorError;
import org.isel.thesis.impads.giragen.generator.base.AbstractGeneratorConfiguration;
import org.isel.thesis.impads.giragen.generator.data.JsonDataModel;
import org.isel.thesis.impads.giragen.generator.error.GeneratorIOError;
import org.isel.thesis.impads.giragen.metrics.api.IServicesMetrics;
import org.isel.thesis.impads.giragen.metrics.func.ServicesMetrics.MetricsCounter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.function.Function;

public class GeneratorDataTask implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(GeneratorDataTask.class);

    protected final IRabbitMQClient rabbitMQClient;
    private final AbstractGeneratorConfiguration config;
    private final ObjectMapper mapper;
    private final MetricsCounter metricsCounter;
    protected final ICSVDataReader csvDataReader;
    private final Function<CSVRecord, Either<GeneratorError, JsonDataModel>> generatorFunction;
    private final RateLimiter rateLimiter;

    protected int index;

    public GeneratorDataTask(final AbstractGeneratorConfiguration config
            , final IRabbitMQClient rabbitMQClient
            , final ObjectMapper mapper
            , final MetricsCounter metricsCounter
            , final ICSVDataReader csvDataReader
            , final Function<CSVRecord, Either<GeneratorError, JsonDataModel>> generatorFunction
            , final RateLimiter rateLimiter) {
        this.config = config;
        this.rabbitMQClient = rabbitMQClient;
        this.mapper = mapper;
        this.metricsCounter = metricsCounter;
        this.csvDataReader = csvDataReader;
        this.generatorFunction = generatorFunction;
        this.rateLimiter = rateLimiter;
        this.index = 0;
    }

    @Override
    public void run() {
        while(true) {
            rateLimiter.acquire();
            Either<GeneratorError, JsonDataModel> data = generatorFunction
                    .apply(csvDataReader.get(index).get());

            if (data.isLeft()) {
                System.err.println(data.getLeft().getMessage());
            } else {
                Either<GeneratorError, Void> rvalue = doSend(data.get(), rabbitMQClient);
                if (rvalue.isLeft()) {
                    logger.error(rvalue.getLeft().getMessage());
                }
                nextIndex();
            }

            metricsCounter.getCounter().increment();
        }
    }

    private Either<GeneratorError, Void> doSend(final JsonDataModel data
            , final IRabbitMQClient rabbitMQClient) {
        Either<GeneratorError, Void> rvalue;
        try {
            IRabbitMQQueue queue = IRabbitMQQueue.RabbitMQQueueNaming.withName(config.getGeneratorQueueName());
            IRabbitMQMessage message = IRabbitMQMessage.RabbitMQMessaging.withMessage(data.toJson(mapper));
            rabbitMQClient.publishMessage(queue, message);

            rvalue = Either.right(null);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            rvalue = Either.left(GeneratorIOError.error(e.getMessage()));
        }

        return rvalue;
    }

    private void nextIndex() {
        if (index < csvDataReader.getSize() - 1) {
            index++;
        }
        else {
            index = 0;
        }
    }
}

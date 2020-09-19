package org.isel.thesis.impads.giragen.data.func;

import com.google.common.collect.Iterators;
import io.vavr.control.Either;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.isel.thesis.impads.giragen.data.api.CSVDataReaderError;
import org.isel.thesis.impads.giragen.data.api.ICSVDataReader;
import org.isel.thesis.impads.giragen.data.error.CSVFileNotFoundError;
import org.isel.thesis.impads.giragen.data.error.CSVIOError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class InMemoryCSVDataReader implements ICSVDataReader {

    private static final Logger logger = LoggerFactory.getLogger(InMemoryCSVDataReader.class);

    private final Path csvPath;
    private List<CSVRecord> records;

    private InMemoryCSVDataReader(final Path csvPath) {
        this.csvPath = csvPath;
        this.records = null;
    }

    public static Either<CSVDataReaderError, ICSVDataReader> initialize(Path csvPath) {
        logger.info("Initializing InMemory CSV Data Reader for {}", csvPath.toString());

        final InMemoryCSVDataReader sequencialInMemoryCSVDataReader = new InMemoryCSVDataReader(csvPath);

        return sequencialInMemoryCSVDataReader.readCsvDataToMemory(csvPath)
                .map(x -> sequencialInMemoryCSVDataReader);
    }

    private Either<CSVDataReaderError, Void> readCsvDataToMemory(Path csvPath) {
        logger.info("Reading CSV data to memory for {}", csvPath.toString());

        Either<CSVDataReaderError, Void> rvalue;
        if (Files.notExists(csvPath)) {
            rvalue = Either.left(CSVFileNotFoundError.error("File path " + csvPath + "doesn't exists"));
        }
        else {
            try {
                CSVParser parser = CSVFormat.DEFAULT
                        .withFirstRecordAsHeader()
                        .parse(new InputStreamReader(Files.newInputStream(csvPath)));

                logger.info("Finished reading CSV data to memory");
                this.records = parser.getRecords();

                rvalue = Either.right(null);
            } catch (IOException e) {
                rvalue = Either.left(CSVIOError.error(e.getMessage()));
            }
        }

        return rvalue;
    }

    @Override
    public int getSize() {
        return records.size();
    }

    @Override
    public Either<CSVDataReaderError, CSVRecord> get(int index) {
        return Either.right(records.get(index));
    }
}

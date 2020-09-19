package org.isel.thesis.impads.giragen.data.api;

import io.vavr.control.Either;
import org.apache.commons.csv.CSVRecord;

import java.util.List;

public interface ICSVDataReader {

    int getSize();

    Either<CSVDataReaderError, CSVRecord> get(int index);
}

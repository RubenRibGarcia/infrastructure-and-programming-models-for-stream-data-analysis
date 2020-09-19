package org.isel.thesis.impads.giragen.data.error;

import org.isel.thesis.impads.giragen.data.api.CSVDataReaderError;

public class CSVFileNotFoundError extends CSVDataReaderError {

    private CSVFileNotFoundError(String message) {
        super(message, CSVDataReaderErrorTypes.CSV_FILE_NOT_FOUND);
    }

    public static CSVFileNotFoundError error(String message) {
        return new CSVFileNotFoundError(message);
    }
}

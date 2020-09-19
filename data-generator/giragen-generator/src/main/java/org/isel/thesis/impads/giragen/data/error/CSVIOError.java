package org.isel.thesis.impads.giragen.data.error;

import org.isel.thesis.impads.giragen.data.api.CSVDataReaderError;

public class CSVIOError extends CSVDataReaderError {

    protected CSVIOError(String message) {
        super(message, CSVDataReaderErrorTypes.IO_ERROR);
    }

    public static CSVIOError error(String message) {
        return new CSVIOError(message);
    }
}

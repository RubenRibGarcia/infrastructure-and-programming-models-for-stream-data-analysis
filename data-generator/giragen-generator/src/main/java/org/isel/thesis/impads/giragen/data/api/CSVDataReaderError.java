package org.isel.thesis.impads.giragen.data.api;

import org.isel.thesis.impads.giragen.base.api.BaseError;
import org.isel.thesis.impads.giragen.base.api.ErrorType;

public abstract class CSVDataReaderError implements BaseError {

    private final String message;
    private final ErrorType errorType;

    protected CSVDataReaderError(String message, ErrorType errorType) {
        this.message = message;
        this.errorType = errorType;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public ErrorType getErrorType() {
        return errorType;
    }

    protected static final class CSVDataReaderErrorTypes {
        private CSVDataReaderErrorTypes() { }

        public static ErrorType CSV_FILE_NOT_FOUND = () -> "csv-file-not-found";
        public static ErrorType IO_ERROR = () -> "io-error";
    }
}

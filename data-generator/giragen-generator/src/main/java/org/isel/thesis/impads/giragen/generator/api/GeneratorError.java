package org.isel.thesis.impads.giragen.generator.api;

import org.isel.thesis.impads.giragen.base.api.BaseError;
import org.isel.thesis.impads.giragen.base.api.ErrorType;

public abstract class GeneratorError implements BaseError {

    private final String message;
    private final ErrorType errorType;

    protected GeneratorError(String message, ErrorType errorType) {
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

    protected static final class GeneratorErrorTypes {
        private GeneratorErrorTypes() { }

        public static ErrorType IO_ERROR = () -> "generator-io-error";
    }
}

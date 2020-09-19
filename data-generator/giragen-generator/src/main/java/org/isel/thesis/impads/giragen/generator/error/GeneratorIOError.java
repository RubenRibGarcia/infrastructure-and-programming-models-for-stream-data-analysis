package org.isel.thesis.impads.giragen.generator.error;


import org.isel.thesis.impads.giragen.generator.api.GeneratorError;

public class GeneratorIOError extends GeneratorError {

    protected GeneratorIOError(String message) {
        super(message, GeneratorErrorTypes.IO_ERROR);
    }

    public static GeneratorIOError error(String message) {
        return new GeneratorIOError(message);
    }
}

package org.isel.thesis.impads.io;

import java.io.Serializable;

public interface OpenCloseable extends Serializable {

    void open() throws Exception;

    void close() throws Exception;
}

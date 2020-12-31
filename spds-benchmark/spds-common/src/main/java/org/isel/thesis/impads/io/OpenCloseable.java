package org.isel.thesis.impads.io;

public interface OpenCloseable {

    void open() throws Exception;

    void close() throws Exception;
}

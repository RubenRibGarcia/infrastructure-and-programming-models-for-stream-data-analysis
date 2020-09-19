package org.isel.thesis.impads.giragen.metrics.api;

import io.micrometer.core.instrument.Tag;

import java.util.List;

public interface IRegisterCounterMetric {

    String getName();

    List<Tag> getTags();
}

package org.isel.thesis.impads.giragen.metrics.func;

import io.micrometer.core.instrument.Tag;
import org.isel.thesis.impads.giragen.metrics.api.IRegisterCounterMetric;

import java.util.ArrayList;
import java.util.List;

public class RegisterCounterMetric implements IRegisterCounterMetric {

    private final String name;
    private List<Tag> tags;

    private RegisterCounterMetric(String name, List<Tag> tags) {
        this.name = name;
        this.tags = tags;
    }

    private RegisterCounterMetric(String name) {
        this(name, new ArrayList<>());
    }

    public static RegisterCounterMetric newCounterMetric(String name) {
        return new RegisterCounterMetric(name);
    }

    public static RegisterCounterMetric newCounterMetric(String name, List<Tag> tags) {
        return new RegisterCounterMetric(name, tags);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<Tag> getTags() {
        return tags;
    }
}

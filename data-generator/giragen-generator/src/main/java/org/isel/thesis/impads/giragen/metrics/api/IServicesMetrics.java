package org.isel.thesis.impads.giragen.metrics.api;

import org.isel.thesis.impads.giragen.metrics.func.ServicesMetrics.MetricsCounter;

public interface IServicesMetrics {

    MetricsCounter withMetricCounter(String name);

}

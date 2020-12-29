package org.isel.thesis.impads.connectors.redis.common;

import java.io.Serializable;

public abstract class RedisConfigurationBase implements Serializable {

    private static final long serialVersionUID = 1L;

    private final boolean mocked;

    protected RedisConfigurationBase(boolean mocked) {
        this.mocked = mocked;
    }

    public boolean isMocked() {
        return mocked;
    }
}

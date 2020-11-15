/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.isel.thesis.impads.storm.redis.common.container;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.isel.thesis.impads.storm.redis.common.config.JedisPoolConfig;
import redis.clients.jedis.JedisPool;

import java.util.Objects;

/**
 * The builder for {@link RedisCommandsContainer}.
 */
public class RedisCommandsContainerBuilder {

    public static final redis.clients.jedis.JedisPoolConfig DEFAULT_POOL_CONFIG = new redis.clients.jedis.JedisPoolConfig();

    /**
     * Builds container for single Redis environment.
     * @param config configuration for JedisPool
     * @return container for single Redis environment
     */
    public static RedisCommandsContainer build(JedisPoolConfig config) {
        GenericObjectPoolConfig genericObjectPoolConfig = new GenericObjectPoolConfig();
        genericObjectPoolConfig.setMaxIdle(config.getMaxIdle());
        genericObjectPoolConfig.setMaxTotal(config.getMaxTotal());
        genericObjectPoolConfig.setMinIdle(config.getMinIdle());

        JedisPool jedisPool =
                new JedisPool(genericObjectPoolConfig, config.getHost(), config.getPort(), config.getTimeout(), config.getPassword(),
                        config.getDatabase());
        return new RedisContainer(jedisPool);
    }
}

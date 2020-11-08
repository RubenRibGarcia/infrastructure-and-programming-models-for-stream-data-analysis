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
package org.isel.thesis.impads.kafka.connect.redis.container;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.isel.thesis.impads.kafka.connect.redis.conf.RedisConfiguration;
import redis.clients.jedis.JedisPool;

import java.util.Objects;

/**
 * The builder for {@link RedisCommandsContainer}.
 */
public class RedisCommandsContainerBuilder {

    public static RedisCommandsContainer build(RedisConfiguration redisConfiguration) {
        Objects.requireNonNull(redisConfiguration, "Redis pool config should not be Null");

        GenericObjectPoolConfig genericObjectPoolConfig = new GenericObjectPoolConfig();
        genericObjectPoolConfig.setMaxIdle(redisConfiguration.getRedisMaxIdle());
        genericObjectPoolConfig.setMaxTotal(redisConfiguration.getRedisMaxTotal());
        genericObjectPoolConfig.setMinIdle(redisConfiguration.getRedisMinIdle());

        JedisPool jedisPool = new JedisPool(genericObjectPoolConfig, redisConfiguration.getRedisHost(),
                redisConfiguration.getRedisPort());
        return new RedisContainer(jedisPool);
    }
}

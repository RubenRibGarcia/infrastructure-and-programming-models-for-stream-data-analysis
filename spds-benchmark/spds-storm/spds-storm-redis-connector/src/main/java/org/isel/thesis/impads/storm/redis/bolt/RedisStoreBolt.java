/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.  The ASF licenses this file to you under the Apache License, Version
 * 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */

package org.isel.thesis.impads.storm.redis.bolt;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Tuple;
import org.isel.thesis.impads.storm.redis.common.config.JedisPoolConfig;
import org.isel.thesis.impads.storm.redis.common.container.RedisCommandsContainer;
import org.isel.thesis.impads.storm.redis.common.container.RedisCommandsContainerBuilder;
import org.isel.thesis.impads.storm.redis.common.mapper.RedisCommand;
import org.isel.thesis.impads.storm.redis.common.mapper.RedisCommandDescription;
import org.isel.thesis.impads.storm.redis.common.mapper.RedisMapper;

import java.util.Map;

public class RedisStoreBolt implements IRichBolt {

    private final JedisPoolConfig config;
    private final RedisMapper mapper;

    private final RedisCommand redisCommand;
    private final Integer additionalTTL;
    private final String additionalKey;

    private TopologyContext context;
    private OutputCollector collector;

    private RedisCommandsContainer container;

    public RedisStoreBolt(JedisPoolConfig config, RedisMapper mapper) {
        this.config = config;
        this.mapper = mapper;

        RedisCommandDescription redisCommandDescription = mapper.getCommandDescription();

        this.redisCommand = redisCommandDescription.getCommand();
        this.additionalTTL = redisCommandDescription.getAdditionalTTL();
        this.additionalKey = redisCommandDescription.getAdditionalKey();
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        return null;
    }

    @Override
    public void prepare(Map<String, Object> topoConf, TopologyContext context, OutputCollector collector) {
        this.container = RedisCommandsContainerBuilder.build(config);
        this.context = context;
        this.collector = collector;
    }

    @Override
    public void execute(Tuple input) {
        String key = mapper.getKeyFromTuple(input);
        String value = mapper.getValueFromTuple(input);

        switch (redisCommand) {
            case RPUSH:
                container.rpush(key, value);
                break;
            default:
                throw new IllegalArgumentException("Cannot process such command: " + redisCommand);
        }
    }

    @Override
    public void cleanup() {

    }
}

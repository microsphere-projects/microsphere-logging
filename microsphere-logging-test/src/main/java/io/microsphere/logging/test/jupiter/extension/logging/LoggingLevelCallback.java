/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.microsphere.logging.test.jupiter.extension.logging;

import io.microsphere.logging.Logger;
import io.microsphere.logging.Logging;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.util.List;
import java.util.Map;

import static io.microsphere.collection.MapUtils.newHashMap;
import static io.microsphere.logging.LoggerFactory.getLogger;

/**
 * The Callback to set logging level before test method and recover it after test method.
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see BeforeEachCallback
 * @see AfterEachCallback
 * @since 1.0.0
 */
class LoggingLevelCallback implements BeforeEachCallback, AfterEachCallback {

    private static final Logger logger = getLogger(LoggingLevelCallback.class);

    private final List<Logging> loggings;

    private final String[] loggerNames;

    private final String level;

    private Map<Logging, String[]> loggingsWithoriginalLevels;

    LoggingLevelCallback(List<Logging> loggings, String[] loggerNames, String level) {
        this.loggings = loggings;
        this.loggerNames = loggerNames;
        this.level = level;
        this.loggingsWithoriginalLevels = newHashMap(loggings.size());
    }

    @Override
    public void beforeEach(ExtensionContext context) {
        int length = loggerNames.length;
        for (Logging logging : loggings) {
            String[] originalLevels = this.loggingsWithoriginalLevels.computeIfAbsent(logging, l -> new String[length]);
            for (int i = 0; i < length; i++) {
                String loggerName = loggerNames[i];
                originalLevels[i] = logging.getLoggerLevel(loggerName);
                logging.setLoggerLevel(loggerName, level);
                logger.info("Set logger[name :'{}']s' level ['{}' -> '{}] via {}", loggerName, originalLevels[i], level, logging);
            }
        }
    }

    @Override
    public void afterEach(ExtensionContext context) {
        int length = loggerNames.length;
        for (Logging logging : loggings) {
            String[] originalLevels = this.loggingsWithoriginalLevels.get(logging);
            for (int i = 0; i < length; i++) {
                String loggerName = loggerNames[i];
                String originalLevel = originalLevels[i];
                logging.setLoggerLevel(loggerName, originalLevel);
                logger.info("Recover logger[name :'{}']s' level ['{}' -> '{}] via {}", loggerName, level, originalLevel, logging);
            }
        }
    }
}
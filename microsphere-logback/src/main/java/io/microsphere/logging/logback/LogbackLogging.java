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

package io.microsphere.logging.logback;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import io.microsphere.logging.Logging;
import io.microsphere.logging.logback.util.LoggerUtils;

import java.util.List;
import java.util.Set;

import static io.microsphere.logging.DefaultLoggingLevelsResolver.INSTANCE;
import static io.microsphere.logging.logback.util.LoggerUtils.getLevelString;
import static io.microsphere.logging.logback.util.LoggerUtils.getLoggerContext;
import static java.util.stream.Collectors.toList;
import static org.slf4j.Logger.ROOT_LOGGER_NAME;

/**
 * {@link Logging} class based on the logback framework.
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see Logging
 * @since 1.0.0
 */
public class LogbackLogging implements Logging {

    /**
     * The priority of {@link LogbackLogging}
     */
    public static final int PRIORITY = NORMAL_PRIORITY;

    /**
     * All Logging Levels : "OFF", "ERROR", "WARN", "INFO", "DEBUG", "TRACE", "ALL"
     *
     * @see Level
     */
    public static final Set<String> ALL_LEVELS = INSTANCE.resolve(Level.class);

    @Override
    public String getRootLoggerName() {
        return ROOT_LOGGER_NAME;
    }

    @Override
    public List<String> getLoggerNames() {
        return getLoggerContext()
                .getLoggerList()
                .stream().map(Logger::getName)
                .collect(toList());
    }

    @Override
    public Set<String> getSupportedLoggingLevels() {
        return ALL_LEVELS;
    }

    @Override
    public String getLoggerLevel(String loggerName) {
        return getLevelString(loggerName);
    }

    @Override
    public void setLoggerLevel(String loggerName, String levelName) {
        LoggerUtils.setLoggerLevel(loggerName, levelName);
    }

    @Override
    public String getName() {
        return "Logback";
    }

    @Override
    public int getPriority() {
        return PRIORITY;
    }
}
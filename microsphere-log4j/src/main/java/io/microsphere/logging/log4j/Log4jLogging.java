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

package io.microsphere.logging.log4j;

import io.microsphere.logging.Logging;
import io.microsphere.logging.log4j.util.LoggerUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggerRepository;

import java.util.Enumeration;
import java.util.List;
import java.util.Set;

import static io.microsphere.collection.CollectionUtils.toIterable;
import static io.microsphere.constants.SymbolConstants.DOT;
import static io.microsphere.logging.DefaultLoggingLevelsResolver.INSTANCE;
import static io.microsphere.logging.log4j.util.LoggerUtils.ROOT_LOGGER_NAME;
import static io.microsphere.logging.log4j.util.LoggerUtils.getLevelString;
import static io.microsphere.logging.log4j.util.LoggerUtils.getLoggerRepository;
import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;

/**
 * {@link Logging} class based on the Log4j framework.
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see Logging
 * @since 1.0.0
 */
public class Log4jLogging implements Logging {

    /**
     * The priority of {@link Log4jLogging}
     */
    public static final int PRIORITY = NORMAL_PRIORITY - 3;

    /**
     * All Logging Levels : "OFF", "FATAL", "ERROR", "WARN", "INFO", "DEBUG", "TRACE", "ALL"
     *
     * @see Level
     */
    public static final Set<String> ALL_LEVELS = INSTANCE.resolve(Level.class);

    @Override
    public List<String> getLoggerNames() {
        LoggerRepository loggerRepository = getLoggerRepository();
        Enumeration loggers = loggerRepository.getCurrentLoggers();
        Iterable<Logger> iterable = toIterable(loggers);
        return stream(iterable.spliterator(), false)
                .map(Logger::getName)
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
    public String getParentLoggerName(String loggerName) {
        if (ROOT_LOGGER_NAME.equals(loggerName)) {
            return null;
        }
        int lastDotIndex = loggerName.lastIndexOf(DOT);
        if (lastDotIndex == -1) {
            return ROOT_LOGGER_NAME;
        }
        String parentLoggerName = loggerName.substring(0, lastDotIndex);
        return parentLoggerName;
    }

    @Override
    public String getName() {
        return "Log4j";
    }

    @Override
    public int getPriority() {
        return PRIORITY;
    }
}
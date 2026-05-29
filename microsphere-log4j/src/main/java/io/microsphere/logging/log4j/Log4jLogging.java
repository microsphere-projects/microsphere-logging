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
import io.microsphere.logging.log4j.util.Log4jUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggerRepository;

import java.util.Enumeration;
import java.util.List;
import java.util.Set;

import static io.microsphere.collection.CollectionUtils.toIterable;
import static io.microsphere.logging.DefaultLoggingLevelsResolver.INSTANCE;
import static io.microsphere.logging.log4j.util.Log4jUtils.ROOT_LOGGER_NAME;
import static io.microsphere.logging.log4j.util.Log4jUtils.getLevelString;
import static io.microsphere.logging.log4j.util.Log4jUtils.getLoggerRepository;
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

    /**
     * {@inheritDoc}
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     *   Log4jLogging logging = new Log4jLogging();
     *   String rootName = logging.getRootLoggerName(); // "root"
     * }</pre>
     */
    @Override
    public String getRootLoggerName() {
        return ROOT_LOGGER_NAME;
    }

    /**
     * {@inheritDoc}
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     *   Log4jLogging logging = new Log4jLogging();
     *   List<String> names = logging.getLoggerNames();
     * }</pre>
     */
    @Override
    public List<String> getLoggerNames() {
        LoggerRepository loggerRepository = getLoggerRepository();
        Enumeration loggers = loggerRepository.getCurrentLoggers();
        Iterable<Logger> iterable = toIterable(loggers);
        return stream(iterable.spliterator(), false)
                .map(Logger::getName)
                .collect(toList());
    }

    /**
     * {@inheritDoc}
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     *   Log4jLogging logging = new Log4jLogging();
     *   Set<String> levels = logging.getSupportedLoggingLevels();
     *   // e.g. ["OFF", "FATAL", "ERROR", "WARN", "INFO", "DEBUG", "TRACE", "ALL"]
     * }</pre>
     */
    @Override
    public Set<String> getSupportedLoggingLevels() {
        return ALL_LEVELS;
    }

    /**
     * {@inheritDoc}
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     *   Log4jLogging logging = new Log4jLogging();
     *   String level = logging.getLoggerLevel("io.microsphere"); // e.g. "INFO"
     * }</pre>
     */
    @Override
    public String getLoggerLevel(String loggerName) {
        return getLevelString(loggerName);
    }

    /**
     * {@inheritDoc}
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     *   Log4jLogging logging = new Log4jLogging();
     *   logging.setLoggerLevel("io.microsphere", "DEBUG");
     * }</pre>
     */
    @Override
    public void setLoggerLevel(String loggerName, String levelName) {
        Log4jUtils.setLoggerLevel(loggerName, levelName);
    }

    /**
     * {@inheritDoc}
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     *   Log4jLogging logging = new Log4jLogging();
     *   String name = logging.getName(); // "Log4j"
     * }</pre>
     */
    @Override
    public String getName() {
        return "Log4j";
    }

    /**
     * {@inheritDoc}
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     *   Log4jLogging logging = new Log4jLogging();
     *   int priority = logging.getPriority();
     *   // returns Log4jLogging.PRIORITY
     * }</pre>
     */
    @Override
    public int getPriority() {
        return PRIORITY;
    }
}

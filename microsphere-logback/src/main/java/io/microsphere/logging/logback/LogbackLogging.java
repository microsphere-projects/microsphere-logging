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
import io.microsphere.logging.logback.util.LogbackUtils;

import java.util.List;
import java.util.Set;

import static io.microsphere.logging.DefaultLoggingLevelsResolver.INSTANCE;
import static io.microsphere.logging.logback.util.LogbackUtils.getLevelString;
import static io.microsphere.logging.logback.util.LogbackUtils.getLoggerContext;
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

    /**
     * {@inheritDoc}
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     *   LogbackLogging logging = new LogbackLogging();
     *   String rootName = logging.getRootLoggerName();
     *   // returns org.slf4j.Logger.ROOT_LOGGER_NAME ("ROOT")
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
     *   LogbackLogging logging = new LogbackLogging();
     *   List<String> names = logging.getLoggerNames();
     * }</pre>
     */
    @Override
    public List<String> getLoggerNames() {
        return getLoggerContext()
                .getLoggerList()
                .stream().map(Logger::getName)
                .collect(toList());
    }

    /**
     * {@inheritDoc}
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     *   LogbackLogging logging = new LogbackLogging();
     *   Set<String> levels = logging.getSupportedLoggingLevels();
     *   // e.g. ["OFF", "ERROR", "WARN", "INFO", "DEBUG", "TRACE", "ALL"]
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
     *   LogbackLogging logging = new LogbackLogging();
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
     *   LogbackLogging logging = new LogbackLogging();
     *   logging.setLoggerLevel("io.microsphere", "DEBUG");
     * }</pre>
     */
    @Override
    public void setLoggerLevel(String loggerName, String levelName) {
        LogbackUtils.setLoggerLevel(loggerName, levelName);
    }

    /**
     * {@inheritDoc}
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     *   LogbackLogging logging = new LogbackLogging();
     *   String name = logging.getName(); // "Logback"
     * }</pre>
     */
    @Override
    public String getName() {
        return "Logback";
    }

    /**
     * {@inheritDoc}
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     *   LogbackLogging logging = new LogbackLogging();
     *   int priority = logging.getPriority();
     *   // returns LogbackLogging.PRIORITY
     * }</pre>
     */
    @Override
    public int getPriority() {
        return PRIORITY;
    }
}